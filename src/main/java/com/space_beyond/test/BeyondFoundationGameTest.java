package com.space_beyond.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hbm_m.platform.recipe.RecipeHooks;
import com.hbm_m.recipe.CentrifugeRecipe;
import com.hbm_m.recipe.CombinationOvenRecipe;
import com.hbm_m.recipe.CrystallizerRecipe;
import com.hbm_m.recipe.PyroOvenRecipe;
import com.space_beyond.BeyondMod;
import com.space_beyond.celestial.BeyondBodies;
import com.space_beyond.celestial.SarnusAsteroidBelt;
import com.space_beyond.celestial.SporosSystem;
import com.space_beyond.celestial.StarSystem;
import com.space_beyond.command.BeyondCommands;
import com.space_beyond.dim.BeyondDimensions;
import com.space_m.atmosphere.AtmosphereEffects;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_m.celestial.trait.CBT_Invasion;
import com.space_m.celestial.trait.CelestialBodyTrait;
import com.space_m.dim.SpaceDimensions;
import com.space_m.resources.ItemCelestialBedrockOre.BedrockOreGrade;
import com.space_m.resources.CelestialBedrockOre;
import com.space_m.resources.CelestialBedrockOreItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Andocken von Beyond an Space Modernized: die Erweiterungen, die im Privat-Repo direkt in den Basisklassen standen
 * und jetzt ueber Mixins kommen (Koerperliste, Body-Enum, Dimensionen, Traits, Atmosphaere, Bedrock-Erze, Befehle).
 */
@GameTestHolder(BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BeyondFoundationGameTest {

    private BeyondFoundationGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static CelestialBody body(String name) {
        CelestialBody b = CelestialBody.getBodyOrNull(name);
        check(b != null, "Koerper fehlt: " + name);
        return b;
    }

    /** 32 Original-Koerper + 20 ergaenzte Monde/Zwergplaneten, danach das Sporos-System. */
    @GameTest(template = "empty3x3x3", batch = "beyond_foundation", timeoutTicks = 20)
    public static void solarSystemExtended(GameTestHelper helper) {
        List<String> original = List.of("kerbol", "moho", "eve", "gilly", "kerbin", "mun", "minmus", "duna", "ike", "dres",
                "jool", "laythe", "vall", "tylo", "bop", "pol", "sarnus", "hale", "ovok", "eeloo", "slate", "tekto",
                "urlum", "polta", "priax", "wal", "tal", "neidon", "thatmo", "nissee", "plock", "karen");
        List<String> added = List.of("deimos", "amalthea", "mimas", "enceladus", "tethys", "dione", "rhea", "iapetus",
                "miranda", "ariel", "umbriel", "titania", "oberon", "proteus", "nix", "hydra", "haumea", "makemake", "eris", "dysnomia");
        List<String> expected = new ArrayList<>(original);
        expected.addAll(added);
        expected.add(SporosSystem.STAR_NAME);
        expected.add(SporosSystem.PLANET_NAME);
        expected.add(SporosSystem.WEMBUROS_NAME);
        List<String> names = CelestialBody.getAllBodies().stream().map(b -> b.name).toList();
        check(names.equals(expected), "Koerperliste " + names);

        check(StarSystem.home().bodies().size() == 52, "Heimatsystem 52 Koerper, ist " + StarSystem.home().bodies().size());
        check(SolarSystem.kerbol.satellites.size() == 13, "Kerbol mit 10 Planeten + 3 Zwergplaneten");
        check(body("deimos").parent == body("duna") && body("dysnomia").parent == body("eris") && body("eris").parent == SolarSystem.kerbol, "Monde");
        check(body("deimos").texture.getNamespace().equals(BeyondMod.MOD_ID) && body("eeloo").texture.getNamespace().equals(BeyondMod.MOD_ID),
                "eigene Texturen der neuen/umgestalteten Monde");
        check(body("mun").texture.getNamespace().equals("space_m"), "Original-Texturen unveraendert");
        check(body("eeloo").radiusKm == 50 && body("slate").radiusKm == 40 && body("wal").radiusKm == 60, "Hyperion/Phoebe/Sycorax-Werte");

        check(CelestialBody.getLandableBodies().size() == 12, "10 landbare Heimatkoerper + Sporos + Wemburos");
        check(StarSystem.all().size() == 2 && StarSystem.of(body("sporos")) != StarSystem.home(), "zwei Sternsysteme");
        check(!StarSystem.sameSystem(body("kerbin"), body("sporos")) && StarSystem.sameSystem(body("sporos"), body("wemburos")), "sameSystem");
        check(SolarSystem.getDeltaVBetween(body("kerbin"), body("sporos")) == Double.POSITIVE_INFINITY, "ohne Station gesperrt");
        check(SarnusAsteroidBelt.asteroids().size() == 720, "Sarnus-Asteroidenguertel");
        helper.succeed();
    }

    /** Body-Enum: Original-Ordinalzahlen unveraendert, Sporos 11 / Wemburos 12. */
    @GameTest(template = "empty3x3x3", batch = "beyond_foundation", timeoutTicks = 20)
    public static void bodyEnumAndDimensions(GameTestHelper helper) {
        check(SolarSystem.Body.TEKTO.ordinal() == 10, "Tekto bleibt 10");
        check(BeyondBodies.SPOROS.ordinal() == 11 && BeyondBodies.WEMBUROS.ordinal() == 12, "Sporos 11, Wemburos 12");
        check(SolarSystem.Body.values().length == 13, "13 Body-Eintraege");
        check(BeyondBodies.SPOROS.getBody() == body("sporos") && BeyondBodies.SPOROS.getBody().dimension == BeyondDimensions.SPOROS, "Sporos-Body");

        check(SpaceDimensions.ALL.size() == 13 && SpaceDimensions.BODIES.size() == 12, "Dimensionen 11 + 2");
        check(SpaceDimensions.ALL.subList(0, 11).equals(List.of(SpaceDimensions.MUN, SpaceDimensions.DUNA, SpaceDimensions.IKE, SpaceDimensions.EVE,
                SpaceDimensions.DRES, SpaceDimensions.MOHO, SpaceDimensions.MINMUS, SpaceDimensions.LAYTHE, SpaceDimensions.ORBIT,
                SpaceDimensions.TEKTO, SpaceDimensions.THATMO)), "Original-Reihenfolge");
        check(SpaceDimensions.isSpaceDimension(BeyondDimensions.SPOROS) && SpaceDimensions.isSpaceDimension(BeyondDimensions.WEMBUROS), "isSpaceDimension");
        ServerLevel sporos = helper.getLevel().getServer().getLevel(BeyondDimensions.SPOROS);
        check(sporos != null, "Dimension space_beyond:sporos geladen");

        check(CelestialBodyTrait.traitList.size() == 14 && CelestialBodyTrait.traitList.indexOf(CBT_Invasion.class) == 12
                && "spores".equals(CelestialBodyTrait.traitMap.inverse().get(com.space_beyond.celestial.trait.CBT_Spores.class)), "13 Original-Traits + spores");

        check(BeyondCommands.targets().contains("thatmo") && BeyondCommands.targets().contains("sporos"), "/space tp Ziele");
        helper.succeed();
    }

    /** End-Kreaturen und Wemburos-Wachen sind luftunabhaengig (Tag), Dungeon-Bewohner per Entity-Tag. */
    @GameTest(template = "empty3x3x3", batch = "beyond_foundation", timeoutTicks = 20)
    public static void suffocationImmunity(GameTestHelper helper) {
        check(EntityType.ENDERMAN.is(com.space_m.lib.SpaceTags.SUFFOCATION_IMMUNE) && EntityType.ENDERMITE.is(com.space_m.lib.SpaceTags.SUFFOCATION_IMMUNE)
                && EntityType.WITHER_SKELETON.is(com.space_m.lib.SpaceTags.SUFFOCATION_IMMUNE), "Enderman/Endermite/Wither-Skelett im Tag");
        EnderMan e = helper.spawn(EntityType.ENDERMAN, 1, 2, 1);
        check(AtmosphereEffects.isSuffocationImmune(e), "Enderman immun");
        var pig = helper.spawn(EntityType.PIG, 1, 2, 1);
        check(!AtmosphereEffects.isSuffocationImmune(pig), "Schwein nicht immun");
        pig.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG);
        check(AtmosphereEffects.isSuffocationImmune(pig), "Dungeon-Tag immun");
        e.discard();
        pig.discard();
        helper.succeed();
    }

    /** Sporos-Bedrock-Erze: 6 Sorten x 26 Grade, Rezepte je Sorte wie bei den Original-Koerpern. */
    @GameTest(template = "empty3x3x3", batch = "beyond_foundation", timeoutTicks = 20)
    public static void sporosBedrockOres(GameTestHelper helper) {
        check(CelestialBedrockOre.getAllTypes().size() == 60, "54 Original-Sorten + 6 Sporos-Sorten");
        check(CelestialBedrockOre.get(BeyondBodies.SPOROS) != null && CelestialBedrockOre.get(BeyondBodies.SPOROS).types.length == 6, "Sporos-Tabelle");
        check(CelestialBedrockOreItems.all().size() == 60 * BedrockOreGrade.values().length, "1560 Items, ist " + CelestialBedrockOreItems.all().size());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("space_m", "bedrock_ore_base_sporos_light");
        check(BuiltInRegistries.ITEM.containsKey(id), "Item " + id);

        ServerLevel level = helper.getLevel();
        check(count(RecipeHooks.getAllRecipesById(level, CentrifugeRecipe.Type.INSTANCE)) == 6 * 15, "Zentrifuge Sporos");
        check(count(RecipeHooks.getAllRecipesById(level, CrystallizerRecipe.Type.INSTANCE)) == 6 * 29, "Kristallisator Sporos");
        check(count(RecipeHooks.getAllRecipesById(level, CombinationOvenRecipe.Type.INSTANCE)) == 6 * 5, "Kombinationsofen Sporos");
        check(count(RecipeHooks.getAllRecipesById(level, PyroOvenRecipe.Type.INSTANCE)) == 6 * 5, "Pyrolyseofen Sporos");
        helper.succeed();
    }

    private static long count(Map<ResourceLocation, ?> recipes) {
        return recipes.keySet().stream().filter(k -> k.getNamespace().equals(BeyondMod.MOD_ID) && k.getPath().contains("sporos")
                && k.getPath().startsWith("bedrock_ore/")).count();
    }
}
