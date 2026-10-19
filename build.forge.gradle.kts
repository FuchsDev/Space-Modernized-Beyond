import groovy.json.JsonSlurper
import java.io.InputStream
import java.net.URI

plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("forge") {
			forgeVersionRange = "[47.3.31,)"
		}
		required("architectury") {
			slug("architectury-api")
			forgeVersionRange = "[${prop("deps.architectury")},)"
		}
		required("hbm_m") {
			slug("hbms-nuclear-tech-modernized")
			// mods.toml compares against hbm_m's own version ("0.2.2-alpha"), not the artifact
			// version "0.2.2-alpha+1.20.1-forge" - that one sorts higher and would reject the match.
			forgeVersionRange = "[${prop("deps.hbm_m").substringBefore('+')},)"
			// Beyond builds on hbm_m and space_m registries/events -> always load after both.
			ordering = "AFTER"
		}
		required("space_m") {
			// Space Modernized (Basis-Addon, originalgetreuer NTM-Space-Port); Beyond haengt sich per Mixin ein.
			forgeVersionRange = "[0.1.0-alpha,)"
			ordering = "AFTER"
		}
	}
}

legacyForge {
	version = "${property("deps.minecraft")}-${property("deps.forge")}"
	if (hasProperty("deps.parchment")) parchment {
		val parchmentProp = property("deps.parchment") as String
		val parts = parchmentProp.split(":")
		mappingsVersion = parts[1]
		minecraftVersion = parts[0]
	}
	validateAccessTransformers = true
	accessTransformers.from(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${stonecutter.active?.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll("-Xmx4G", "-Xms2G", "-Dfile.encoding=UTF-8", "-Dconsole.encoding=UTF-8",
				"-Duser.language=en", "-Duser.country=US")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${stonecutter.active?.version})"
			programArgument("--nogui")
			jvmArguments.addAll("-Xmx4G", "-Xms2G", "-Dfile.encoding=UTF-8",
				"-Duser.language=en", "-Duser.country=US")
		}
		// Headless server start without eula.txt (GameTestServer bypasses Main): loads hbm_m + space_m + space_beyond,
		// runs the space_beyond game tests and exits. ./gradlew :1.20.1-forge:runGameTestServer
		register("gameTestServer") {
			type = "gameTestServer"
			gameDirectory = file("run/")
			ideName = "Forge GameTest (${stonecutter.active?.version})"
			systemProperty("forge.enabledGameTestNamespaces", prop("mod.id"))
			jvmArguments.addAll("-Xmx4G", "-Xms2G", "-Dfile.encoding=UTF-8")
		}
		register("data") {
			data()
			gameDirectory = file("run/")
			ideName = "Forge Data (${stonecutter.active?.version})"
			jvmArguments.addAll("-Xmx4G", "-Xms2G", "-Dfile.encoding=UTF-8", "-Dconsole.encoding=UTF-8")
			val datagenOutput = rootProject.file("src/generated/resources")
			val existingResources = rootProject.file("src/main/resources")
			programArgument("--mod")
			programArgument(prop("mod.id"))
			programArgument("--all")
			programArgument("--output")
			programArgument(datagenOutput.absolutePath)
			programArgument("--existing")
			programArgument(existingResources.absolutePath)
			// Modelle/Texturen von HBM-Modernized (Eltern-Modelle, gemeinsame Texturen) fuer den ExistingFileHelper.
			programArgument("--existing-mod")
			programArgument("hbm_m")
			programArgument("--existing-mod")
			programArgument("space_m")
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

mixin {
	add(sourceSets.main.get(), prop("mod.mixin_refmap"))
	config(prop("mod.mixin_config"))
}

/** Dev-Shader an/aus: dev.shaders in local.properties oder -P (Standard: an). */
val devShaders: Boolean = run {
	val extra = gradle.extensions.extraProperties
	val v = (findProperty("dev.shaders") as? String) ?: (if (extra.has("dev.shaders")) extra.get("dev.shaders") as? String else null)
	v?.trim()?.lowercase() != "false"
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
	maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://cursemaven.com", "curse.maven") { name = "CurseForge" }
	strictMaven("https://maven.architectury.dev/", "dev.architectury") { name = "Architectury" }
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	// Architectury — compile+runtime, NOT jarJar (HBM-Modernized already bundles it)
	"modImplementation"("dev.architectury:architectury-forge:${prop("deps.architectury")}")

	// HBM-Modernized — required dependency.
	// If hbm.local.path is set in local.properties, a local jar is used;
	// otherwise it is pulled from Modrinth (for CI and other developers).
	"modImplementation"(hbmDependency(stonecutter.current.project))

	// Space Modernized - required parent addon (local clone via space.local.path until it is published).
	"modImplementation"(spaceDependency(stonecutter.current.project))

	// JEI (nur Compile, R2-C Atmo-Maschinen: Space-Kategorien auf Basis der HBM-JEI-Kategorien). Version wie HBM deps.jei.
	"modCompileOnly"("curse.maven:jei-238222:7270446")
	// Dev-Laufzeit (nur runClient/runServer, nicht im Jar): JEI + WorldEdit zum Testen
	"modRuntimeOnly"("curse.maven:jei-238222:7270446")
	"modRuntimeOnly"("maven.modrinth:worldedit:Wrlqaul6") // WorldEdit 7.2.15 Forge 1.20.1
	// Shader-Test (nur runClient, nicht im Jar): Embeddium + Oculus (Forge-Port von Iris). Abschalten: dev.shaders=false
	if (devShaders) {
		"modRuntimeOnly"("maven.modrinth:embeddium:0.3.31+mc1.20.1")
		"modRuntimeOnly"("maven.modrinth:oculus:1.20.1-1.8.0")
	}
}

sourceSets {
	main {
		resources.srcDir(rootProject.file("src/generated/resources"))
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

tasks.named<Jar>("jar") {
	exclude("com/${prop("mod.id")}/datagen/**")
}

tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }

stonecutter {
}

// Shaderpack fuer den Dev-Client: laedt die neueste Version von Modrinth nach run/shaderpacks (nur wenn noch keins da
// ist) und schaltet es in Oculus ein, falls noch keine Oculus-Konfiguration existiert. Schlaegt der Download fehl
// (offline), startet der Client trotzdem. Anderes Pack: dev.shaderpack=<modrinth-slug>, abschalten: dev.shaders=false.
val downloadShaderpack = tasks.register("downloadShaderpack") {
	group = "shaders"
	description = "Downloads the dev shaderpack (default: Photon) into run/shaderpacks."
	val slug = (findProperty("dev.shaderpack") as? String)?.trim()?.ifBlank { null }
		?: gradle.extensions.extraProperties.let { if (it.has("dev.shaderpack")) it.get("dev.shaderpack") as? String else null }
		?: "photon-shader"
	val runDir = file("run")
	onlyIf { devShaders }
	doLast {
		val packs = File(runDir, "shaderpacks").apply { mkdirs() }
		val prefix = slug.substringBefore('-')
		var pack = packs.listFiles()?.firstOrNull { it.name.startsWith(prefix, ignoreCase = true) && it.name.endsWith(".zip") }
		if (pack == null) {
			try {
				fun get(url: String): InputStream = URI(url).toURL().openConnection().apply {
					setRequestProperty("User-Agent", "Space-Modernized-Beyond dev build")
					connectTimeout = 10000
					readTimeout = 60000
				}.getInputStream()
				val json = get("https://api.modrinth.com/v2/project/$slug/version").bufferedReader().use { it.readText() }
				@Suppress("UNCHECKED_CAST")
				val versions = JsonSlurper().parseText(json) as List<Map<String, Any?>>
				@Suppress("UNCHECKED_CAST")
				val files = versions.firstOrNull()?.get("files") as? List<Map<String, Any?>> ?: emptyList()
				val file = files.firstOrNull { it["primary"] == true } ?: files.firstOrNull()
					?: throw GradleException("no files for $slug")
				val target = File(packs, file["filename"] as String)
				get(file["url"] as String).use { input -> target.outputStream().use { input.copyTo(it) } }
				logger.lifecycle("Shaderpack heruntergeladen: ${target.name}")
				pack = target
			} catch (e: Exception) {
				logger.warn("Shaderpack '$slug' konnte nicht geladen werden (${e.message}) - Client startet ohne.")
			}
		}
		val config = File(runDir, "config/oculus.properties")
		if (pack != null && !config.exists()) {
			config.parentFile.mkdirs()
			config.writeText("enableShaders=true\nshaderPack=${pack.name}\n")
		}
	}
}
tasks.matching { it.name == "runClient" }.configureEach { dependsOn(downloadShaderpack) }
