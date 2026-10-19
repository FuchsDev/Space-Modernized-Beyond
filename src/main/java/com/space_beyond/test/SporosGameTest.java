package com.space_beyond.test;

import java.util.HashMap;
import java.util.Map;

import com.hbm_m.platform.PlatformHooks;
import com.space_m.SpaceMod;
import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.CelestialEnvironment;
import com.space_m.celestial.CelestialFluids;
import com.space_m.celestial.SolarSystem;
import com.space_m.celestial.SolarSystemWorldSavedData;
import com.space_beyond.celestial.SporosSystem;
import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.trait.CBT_Atmosphere;
import com.space_beyond.celestial.trait.CBT_Spores;
import com.space_m.celestial.trait.CBT_Temperature;
import com.space_m.celestial.trait.CBT_Water;
import com.space_m.celestial.trait.CelestialBodyTrait;
import com.space_m.command.CommandSpace;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.SpaceDimensions;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_m.orbit.OrbitWorld;
import com.space_m.orbit.OrbitalStation;
import com.space_m.orbit.OrbitalStation.StationState;
import com.space_beyond.orbit.interstellar.FoldDriveNetwork;
import com.space_beyond.orbit.interstellar.TileEntityFoldDrive;
import com.space_m.orbit.propulsion.PropulsionBlocks;
import com.space_m.orbit.propulsion.TileEntityStationPropulsionCreative;
import com.space_m.rocket.ItemVOTVdrive;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Erweiterung Sternsysteme, Runde 2: Planet Sporos (Werte, Dimension, Sonnenleistung, Sporen-Trait), Gelaende und
 * Biome, Stadtplanung (Vorbereitung Runde 3), Laufwerk-NBT der alten Koerper, Faltraum-Antrieb (Station reist nur mit
 * geladenem Antrieb ins Sporos-System, Faltphase mit Sternenhimmel).
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosGameTest {

    private SporosGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static void near(double actual, double expected, double eps, String msg) {
        check(Math.abs(actual - expected) <= eps, msg + ": erwartet " + expected + ", ist " + actual);
    }

    private static CelestialBody body(String name) {
        CelestialBody b = CelestialBody.getBodyOrNull(name);
        check(b != null, "Koerper fehlt: " + name);
        return b;
    }

    /** Sporos: Platz im Sporos-System, Werte, Dimension, Laufwerk-/Teleportziel. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 20)
    public static void sporosPlanet(GameTestHelper helper) {
        CelestialBody sporos = body(SporosSystem.PLANET_NAME);
        StarSystem system = StarSystem.byId(SporosSystem.SYSTEM_ID);
        check(sporos == SporosSystem.planet && system != null && StarSystem.of(sporos) == system, "Sporos im Sporos-System");
        check(sporos.parent == SporosSystem.star && sporos.getStar() == SporosSystem.star && sporos.getPlanet() == sporos, "umkreist den Sporos-Stern");
        check(system.bodies().size() == 3 && SporosSystem.star.satellites.size() == 2, "Stern + Planeten Sporos und Wemburos");
        check(!StarSystem.sameSystem(sporos, body("kerbin")), "anderes System als Kerbin");

        // Werte
        check(sporos.massKg == 5.97e24F && sporos.radiusKm == 6_371, "Masse/Radius (Erde)");
        near(sporos.getSurfaceGravity(), 9.817, 0.01, "Schwerkraft m/s^2");
        near(sporos.getSurfaceGravity() / 9.81, 1.0, 0.01, "Schwerkraft in g");
        near(CelestialEnvironment.gravityOf(sporos), 1.601, 0.005, "NTM-Schwerkraft");
        near(OrbitWorld.getOrbitalAltitude(sporos) / 1000.0, 8060, 30, "Orbit-Radius km (ueber der Oberflaeche)");
        check(OrbitWorld.getOrbitalAltitude(sporos) / 1000.0 > sporos.radiusKm + 1000, "Station ueber der Atmosphaere");
        check(sporos.semiMajorAxisKm == 8_700_000 && sporos.getRotationalPeriodSeconds() == 26_400, "Bahn/Tag");
        near(sporos.getRotationalPeriod(), 26_400 * (1200.0 / 21600.0) * 20, 1e-6, "Tag in Ticks");
        near(sporos.getOrbitalPeriod(), 240.7, 1.5, "Jahr in KSP-Tagen");
        check(sporos.axialTilt == 18F && sporos.getMinProcessingLevel() == 3, "Achse/Verarbeitungsstufe");

        // Sonnenleistung ueber Leuchtkraft und Abstand zum eigenen Stern (habitable Zone)
        double au = 8_700_000.0 / AstronomyUtil.KM_IN_AU;
        near(sporos.getSunPower(), 0.45 / (au * au), 1e-4, "Sonnenleistung Sporos");
        check(sporos.getSunPower() > 1.0F && sporos.getSunPower() < 1.2F, "Sporos etwas waermer als Kerbin: " + sporos.getSunPower());
        near(Math.sqrt(0.45), 0.6708, 1e-4, "HZ-Abstand (Kerbin-Einstrahlung) in AU");
        check(body("kerbin").getSunPower() == 1.0F, "Kerbin exakt 1");
        near(body("duna").getSunPower(), 0.43055, 1e-4, "Duna unveraendert");

        // Atmosphaere, Wasser, Temperatur, Sporen
        CBT_Atmosphere atmo = sporos.getDefaultTrait(CBT_Atmosphere.class);
        check(atmo != null && atmo.getPressure() == 1.0D && atmo.hasFluid(CelestialFluids.EARTHAIR), "1 bar Erdluft");
        check(sporos.getDefaultTrait(CBT_Water.class) != null, "Wasser");
        CBT_Temperature temp = sporos.getDefaultTrait(CBT_Temperature.class);
        check(temp != null && temp.degrees == 24F, "24 Grad");
        CBT_Spores spores = sporos.getDefaultTrait(CBT_Spores.class);
        check(spores != null && spores.density == CBT_Spores.DEFAULT_DENSITY && spores.strain == 1, "Sporen-Trait");
        check(sporos.getTraits().get(CBT_Spores.class) != null, "Sporen-Trait in getTraits");

        // Dimension und Landung
        check(sporos.dimension == com.space_beyond.dim.BeyondDimensions.SPOROS && sporos.canLand && CelestialBody.getBody(com.space_beyond.dim.BeyondDimensions.SPOROS) == sporos, "Dimension");
        check(CelestialBody.getLandableBodies().contains(sporos), "landbar");
        check(SpaceDimensions.ALL.contains(com.space_beyond.dim.BeyondDimensions.SPOROS) && SpaceDimensions.BODIES.contains(com.space_beyond.dim.BeyondDimensions.SPOROS), "Dimensionslisten");
        check(SpaceDimensions.isSpaceDimension(com.space_beyond.dim.BeyondDimensions.SPOROS), "Space-Dimension");
        check(sporos.getEnum() == com.space_beyond.celestial.BeyondBodies.SPOROS && com.space_beyond.celestial.BeyondBodies.SPOROS.getBody() == sporos
                && com.space_beyond.celestial.BeyondBodies.SPOROS.getDimension() == com.space_beyond.dim.BeyondDimensions.SPOROS, "Body-Enum");
        check(com.space_beyond.command.BeyondCommands.targets().contains("sporos") && com.space_beyond.command.BeyondCommands.parse("sporos") == com.space_beyond.celestial.BeyondBodies.SPOROS, "/space tp sporos");
        check(CelestialGenerators.get(SporosBlocks.BODY) != null && CelestialGenerators.get(SporosBlocks.BODY).seaLevel() == 63, "Generator");
        helper.succeed();
    }

    /**
     * Laufwerke ({@code body}) und {@code wand_d} ({@code dim}) speichern die Ordinalzahl: alle alten Koerper behalten
     * ihre Zahl, Sporos haengt hinten an.
     */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 20)
    public static void driveOrdinalsUnchanged(GameTestHelper helper) {
        String[] old = {"ORBIT", "KERBIN", "MUN", "MINMUS", "DUNA", "MOHO", "DRES", "EVE", "IKE", "LAYTHE", "TEKTO"};
        SolarSystem.Body[] values = SolarSystem.Body.values();
        check(values.length == 13, "13 Eintraege, ist " + values.length);
        for (int i = 0; i < old.length; i++) {
            check(values[i].name().equals(old[i]), "Ordinal " + i + " = " + old[i] + ", ist " + values[i]);
            ItemStack drive = ItemVOTVdrive.of(values[i]);
            CompoundTag tag = PlatformHooks.getItemTag(drive);
            check(tag != null && tag.getInt("body") == i, "Laufwerk-NBT " + old[i]);
            check(ItemVOTVdrive.getBody(drive) == values[i], "Laufwerk liest " + old[i]);
        }
        check(com.space_beyond.celestial.BeyondBodies.SPOROS.ordinal() == 11, "Sporos hinten (11)");
        ItemStack drive = ItemVOTVdrive.of(com.space_beyond.celestial.BeyondBodies.SPOROS);
        check(PlatformHooks.getItemTag(drive).getInt("body") == 11 && ItemVOTVdrive.getBody(drive) == com.space_beyond.celestial.BeyondBodies.SPOROS, "Laufwerk Sporos");
        check(ItemVOTVdrive.creativeStacks().size() == 13, "ein Laufwerk je Koerper");
        // Sporen-Trait: Index nach allen Original-Traits (Sync-ID), NBT- und Byte-Rundreise
        check(CelestialBodyTrait.traitList.size() == 14 && CelestialBodyTrait.traitList.indexOf(CBT_Spores.class) == 13
                && "spores".equals(CelestialBodyTrait.traitMap.inverse().get(CBT_Spores.class)), "spores = Trait 13");
        CBT_Spores s = new CBT_Spores(0.8F, 0.6F, 7);
        CompoundTag nbt = new CompoundTag();
        s.writeToNBT(nbt);
        CBT_Spores r = (CBT_Spores) CelestialBodyTrait.create(CBT_Spores.class);
        r.readFromNBT(nbt);
        check(r.density == 0.8F && r.virulence == 0.6F && r.strain == 7, "NBT-Rundreise");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        s.writeToBytes(buf);
        CBT_Spores b = new CBT_Spores();
        b.readFromBytes(buf);
        check(b.density == 0.8F && b.virulence == 0.6F && b.strain == 7, "Sync-Rundreise");
        check(new CBT_Spores(3F, -1F, 1).density == 1F, "Dichte geklemmt");
        helper.succeed();
    }

    /** Datapack: Dimensionstyp, Biome, Erz-Features; Dimension geladen (Forge-GameTestServer). */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 100)
    public static void sporosRegistries(GameTestHelper helper) {
        var access = helper.getLevel().getServer().registryAccess();
        var type = access.registryOrThrow(Registries.DIMENSION_TYPE).get(com.space_beyond.dim.BeyondDimensions.type("sporos"));
        check(type != null && type.minY() == 0 && type.height() == 256 && com.space_beyond.BeyondMod.id("sporos").equals(type.effectsLocation()), "dimension_type sporos");
        for (BiomeGenBaseCelestial biome : new BiomeGenBaseCelestial[] {BiomeGenBaseSporos.overgrownPlains, BiomeGenBaseSporos.mossForest,
                BiomeGenBaseSporos.swamp, BiomeGenBaseSporos.river}) {
            check(access.registryOrThrow(Registries.BIOME).containsKey(biome.key), "Biom " + biome.key.location());
        }
        var placed = access.registryOrThrow(Registries.PLACED_FEATURE);
        for (String f : new String[] {"sporos/ore_uranium", "sporos/vanilla_ore_iron", "sporos/cluster_titanium", "sporos/depth_borax"}) {
            check(placed.containsKey(com.space_beyond.BeyondMod.id(f)), "Feature " + f);
        }
        ServerLevel level = helper.getLevel().getServer().getLevel(com.space_beyond.dim.BeyondDimensions.SPOROS);
        // NeoForge-1.21.1-GameTestServer laedt keine Datapack-Dimensionen (s. DimensionGameTest)
        if (level != null) {
            check(CelestialBody.getBody(level) == body("sporos"), "Welt -> Sporos");
            check(CBT_Spores.densityAt(level) == CBT_Spores.DEFAULT_DENSITY, "Sporendichte der Welt");
            check(CelestialBody.getGravity(new net.minecraft.world.entity.animal.Pig(net.minecraft.world.entity.EntityType.PIG, level)) > 1.4F, "Schwerkraft in der Welt");
        }
        helper.succeed();
    }

    private static Map<BiomeGenBaseCelestial, Integer> biomeCounts() {
        Map<BiomeGenBaseCelestial, Integer> counts = new HashMap<>();
        var wcm = CelestialGenerators.state(SporosBlocks.BODY).chunkManager;
        for (int x = -2048; x < 2048; x += 16) {
            for (int z = -2048; z < 2048; z += 16) {
                counts.merge(wcm.getBiomeGenForCoords(x, z), 1, Integer::sum);
            }
        }
        return counts;
    }

    /** GenLayer: alle neun Sporos-Biome (R2b) kommen vor, nichts anderes; Wald haeufiger als Sumpf. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 400)
    public static void sporosBiomes(GameTestHelper helper) {
        Map<BiomeGenBaseCelestial, Integer> c = biomeCounts();
        for (BiomeGenBaseCelestial b : BiomeGenBaseSporos.ALL) {
            check(c.getOrDefault(b, 0) > 0, "Biom " + b.biomeName + " fehlt: " + c);
        }
        check(c.size() == BiomeGenBaseSporos.ALL.length, "nur Sporos-Biome: " + c);
        check(c.get(BiomeGenBaseSporos.mossForest) > c.get(BiomeGenBaseSporos.swamp), "Wald > Sumpf: " + c);
        helper.succeed();
    }

    private static int count(BlockState[] blocks, Block b, int yMin, int yMax) {
        int n = 0;
        for (int c = 0; c < 256; c++) for (int y = yMin; y < yMax; y++) {
            BlockState s = blocks[c * 256 + y];
            if (s != null && s.is(b)) n++;
        }
        return n;
    }

    /** Puffer: Grundgestein, Sporos-Gestein, Humus, Bewuchsboden (Gras/Moos/Schlamm), Wasser nur bis y 62. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 400)
    public static void sporosTerrain(GameTestHelper helper) {
        int stone = 0, silt = 0, green = 0, water = 0, bed = 0;
        for (int cx = -3; cx <= 3; cx++) for (int cz = -3; cz <= 3; cz++) {
            BlockState[] b = CelestialGenerators.buffer(SporosBlocks.BODY, cx * 9, cz * 9).blocks;
            stone += count(b, SporosBlocks.SPOROS_STONE.get(), 0, 256);
            silt += count(b, SporosBlocks.SPOROS_SILT.get(), 0, 256);
            green += count(b, Blocks.GRASS_BLOCK, 0, 256) + count(b, Blocks.MOSS_BLOCK, 0, 256) + count(b, Blocks.MUD, 0, 256);
            water += count(b, Blocks.WATER, 0, 63);
            // Staedte (Runde 3) fluten Senken/Keller bis Strassenniveau - nur ausserhalb pruefen
            boolean inCity = SporosCities.cityAt(com.space_m.dim.CelestialSeed.get(), cx * 9, cz * 9) != null;
            check(inCity || count(b, Blocks.WATER, 63, 256) == 0, "kein Meerwasser ueber y 62");
            check(count(b, Blocks.STONE, 0, 256) == 0, "kein Vanilla-Stein");
            for (int c = 0; c < 256; c++) if (b[c * 256] != null && b[c * 256].is(Blocks.BEDROCK)) bed++;
        }
        int chunks = 49;
        check(bed == chunks * 256, "Grundgestein y 0: " + bed);
        check(stone > chunks * 256 * 30, "Sporos-Gestein: " + stone);
        check(silt > chunks * 256, "Humus: " + silt);
        check(green > chunks * 50, "Gras/Moos/Schlamm: " + green);
        check(water > 0, "Fluesse/Sumpfwasser: " + water);
        helper.succeed();
    }

    /** Deterministische Stadtplanung (Bezirke, Radius, Raster); Generator seit Runde 3 angemeldet. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 100)
    public static void sporosCityLayout(GameTestHelper helper) {
        check(SporosCities.generator() == com.space_beyond.dim.sporos.city.SporosCityGenerator.INSTANCE, "Staedtegenerator (Runde 3) angemeldet");
        long seed = 1234567L;
        int cities = 0;
        for (int dx = -5; dx < 5; dx++) for (int dz = -5; dz < 5; dz++) {
            SporosCities.City city = SporosCities.cityOfDistrict(seed, dx, dz);
            SporosCities.City again = SporosCities.cityOfDistrict(seed, dx, dz);
            check(city == null ? again == null : city.equals(again), "deterministisch");
            if (city == null) continue;
            cities++;
            check(city.radiusChunks() >= SporosCities.MIN_RADIUS_CHUNKS && city.radiusChunks() <= SporosCities.MAX_RADIUS_CHUNKS, "Radius");
            // ganz im Bezirk
            int minX = dx * SporosCities.DISTRICT_CHUNKS, minZ = dz * SporosCities.DISTRICT_CHUNKS;
            check(city.centerChunkX() - city.radiusChunks() >= minX && city.centerChunkX() + city.radiusChunks() < minX + SporosCities.DISTRICT_CHUNKS
                    && city.centerChunkZ() - city.radiusChunks() >= minZ && city.centerChunkZ() + city.radiusChunks() < minZ + SporosCities.DISTRICT_CHUNKS, "Stadt im Bezirk");
            check(city.equals(SporosCities.cityAt(seed, city.centerChunkX(), city.centerChunkZ())), "Mittelpunkt gehoert zur Stadt");
            check(SporosCities.cityAt(seed, city.centerChunkX() + city.radiusChunks() + 1, city.centerChunkZ()) == null, "ausserhalb keine Stadt");
            check(SporosCities.isStreet(city, city.originX(), city.originZ()), "Strassenkreuzung in der Mitte");
            check(!SporosCities.isStreet(city, city.originX() + SporosCities.STREET_PITCH / 2, city.originZ() + SporosCities.STREET_PITCH / 2), "Grundstueck zwischen Strassen");
        }
        check(cities > 25 && cities < 70, "Staedte in 100 Bezirken: " + cities);
        check(SporosCities.isCityBiome(BiomeGenBaseSporos.overgrownPlains) && !SporosCities.isCityBiome(BiomeGenBaseSporos.river), "Stadtbiome");
        helper.succeed();
    }

    /**
     * Faltraum-Antrieb: ohne Antrieb (oder ungeladen) bleibt Sporos gesperrt; geladen startet die Station von Kerbin
     * zur Station im Sporos-Orbit, die Faltphase dauert {@link FoldDriveNetwork#TRANSFER_TICKS} ohne Sonne.
     */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_station", timeoutTicks = 100)
    public static void foldDriveUnlocksTravel(GameTestHelper helper) {
        CelestialBody kerbin = body("kerbin");
        CelestialBody sporos = body("sporos");

        // Ausserhalb einer Station (Raketen, Transporter): immer gesperrt
        check(StarSystem.interstellarDrive() == FoldDriveNetwork.INSTANCE, "Faltantrieb angemeldet");
        check(StarSystem.reachability(kerbin, sporos) == StarSystem.Reachability.NEEDS_INTERSTELLAR_DRIVE, "ohne Station gesperrt");
        check(SolarSystem.getCostBetween(kerbin, sporos, 50_000, 2_000_000, 300, false, true) == Integer.MAX_VALUE, "Rakete: unbezahlbar");
        near(FoldDriveNetwork.escapeBurn(kerbin), 930, 15, "Fluchtimpuls Kerbin");
        near(FoldDriveNetwork.escapeBurn(sporos), 3250, 15, "Fluchtimpuls Sporos");

        ServerLevel orbit = helper.getLevel().getServer().getLevel(SpaceDimensions.ORBIT);
        // NeoForge-1.21.1-GameTestServer laedt keine Datapack-Dimensionen (s. DimensionGameTest)
        if (orbit == null) { helper.succeed(); return; }

        int dX = -53, dZ = 61;
        int x = dX * OrbitalStation.STATION_SIZE + OrbitalStation.STATION_SIZE / 2;
        int z = dZ * OrbitalStation.STATION_SIZE + OrbitalStation.STATION_SIZE / 2;
        BlockPos core = new BlockPos(x, 127, z);
        OrbitalStation.addStation(dX, dZ, kerbin);
        OrbitalStation station = OrbitalStation.getStation(dX, dZ);

        TileEntityStationPropulsionCreative engine = new TileEntityStationPropulsionCreative(core.offset(4, 0, 0),
                PropulsionBlocks.PROPULSION_CREATIVE.get().defaultBlockState());
        TileEntityFoldDrive drive = new TileEntityFoldDrive(core.offset(-4, 0, 0), com.space_beyond.block.BeyondBlocks.MACHINE_FOLD_DRIVE.get().defaultBlockState());
        try {
            engine.setLevel(orbit);
            TileEntityStationPropulsionCreative.tick(orbit, engine.getBlockPos(), engine.getBlockState(), engine);
            check(station.engineCount() == 1 && station.orbiting == kerbin, "Station um Kerbin mit Antrieb");

            // ohne Faltantrieb: gesperrt, eigener Fehler
            station.travelTo(orbit, sporos);
            check(station.state == StationState.ORBIT && com.space_beyond.orbit.StationExt.foldDriveError(station), "ohne Faltantrieb gesperrt");
            near(SolarSystem.getDeltaVBetween(kerbin, body("mun")), 1127.740111244644, 0.01, "Heimat-dv unveraendert");

            // ungeladen: weiter gesperrt
            drive.setLevel(orbit);
            TileEntityFoldDrive.tick(orbit, drive.getBlockPos(), drive.getBlockState(), drive);
            check(drive.isRegistered() && com.space_beyond.orbit.StationExt.foldDriveCount(station) == 1, "Faltantrieb angemeldet");
            station.travelTo(orbit, sporos);
            check(station.state == StationState.ORBIT && !drive.isCharged(), "ungeladen gesperrt");

            // geladen: Reise frei, Ladung verbraucht
            drive.setEnergyStored(TileEntityFoldDrive.MAX_POWER);
            check(drive.isCharged() && com.space_beyond.orbit.StationExt.hasChargedFoldDrive(station), "geladen");
            double dv = FoldDriveNetwork.withStation(station, () -> SolarSystem.getDeltaVBetween(kerbin, sporos));
            near(dv, FoldDriveNetwork.escapeBurn(kerbin) + FoldDriveNetwork.escapeBurn(sporos), 1e-6, "Delta-v der Faltreise");
            check(StarSystem.reachability(kerbin, sporos) == StarSystem.Reachability.NEEDS_INTERSTELLAR_DRIVE, "Raketen bleiben gesperrt");

            // Planeten-Fortschritt: Sporos (Stufe 10) erst nach dem Tekto-Boss
            int unlocked = com.space_beyond.progression.SpaceProgression.unlockedTier();
            com.space_beyond.progression.SpaceProgression.setUnlockedForTests(1);
            station.travelTo(orbit, sporos);
            check(station.state == StationState.ORBIT && com.space_beyond.orbit.StationExt.progressionLocked(station), "Sporos vor dem Tekto-Boss gesperrt");
            com.space_beyond.progression.SpaceProgression.setUnlockedForTests(99);
            station.travelTo(orbit, sporos);
            com.space_beyond.progression.SpaceProgression.setUnlockedForTests(unlocked);
            check(station.state == StationState.LEAVING && station.target == sporos && !com.space_beyond.orbit.StationExt.foldDriveError(station), "Start zur Sporos-Station");
            check(drive.getEnergyStored() == 0, "Ladung verbraucht");

            int ticks = 0;
            while (station.state == StationState.LEAVING && ticks < 1000) { station.update(orbit); ticks++; }
            check(station.state == StationState.TRANSFER && station.maxStateTimer == FoldDriveNetwork.TRANSFER_TICKS, "Faltphase " + station.maxStateTimer);
            check(com.space_beyond.orbit.StationExt.isInterstellarTransfer(station), "interstellarer Transfer");
            check(OrbitWorld.getSunPower(orbit, x, z) == 0F && station.getEclipseAmount(orbit) == 1, "keine Sonne in der Faltphase");

            ticks = 0;
            while (station.state == StationState.TRANSFER && ticks < 100_000) { station.update(orbit); ticks++; }
            check(ticks == FoldDriveNetwork.TRANSFER_TICKS + 1 && station.state == StationState.ARRIVING && station.orbiting == sporos, "Ankunft nach " + ticks);
            ticks = 0;
            while (station.state == StationState.ARRIVING && ticks < 1000) { station.update(orbit); ticks++; }
            check(station.state == StationState.ORBIT && station.orbiting == sporos, "im Sporos-Orbit");
            check(CelestialBody.getTarget(orbit, x, z).body == sporos, "getTarget -> Sporos (Landung mit vorhandener Mechanik)");
            near(OrbitWorld.getSunPower(orbit, x, z), sporos.getSunPower() * (1 - station.getEclipseAmount(orbit)), 1e-4, "Sonne im Sporos-Orbit");

            // Rueckweg wieder nur mit geladenem Antrieb
            station.travelTo(orbit, kerbin);
            check(station.state == StationState.ORBIT && com.space_beyond.orbit.StationExt.foldDriveError(station), "Rueckweg ohne Ladung gesperrt");
        } finally {
            engine.setRemoved();
            drive.setRemoved();
            SolarSystemWorldSavedData d = SolarSystemWorldSavedData.get(helper.getLevel().getServer());
            OrbitalStation s = d.getStationFromPosition(x, z);
            if (s != null) { s.hasStation = false; d.removeStation(s); }
        }
        check(com.space_beyond.orbit.StationExt.foldDriveCount(station) == 0, "abgemeldet");
        helper.succeed();
    }

    /** Overworld bleibt Kerbin; Testwelt-Stern Kerbol. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos", timeoutTicks = 20)
    public static void homeStillKerbin(GameTestHelper helper) {
        check(CelestialBody.getBody(Level.OVERWORLD) == body("kerbin") && CelestialBody.getStar(helper.getLevel()) == SolarSystem.kerbol, "Heimat");
        helper.succeed();
    }
}
