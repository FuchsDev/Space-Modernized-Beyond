package com.space_beyond.test;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.space_m.SpaceMod;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.structure.Frame;
import com.space_beyond.dim.sporos.structure.SporosStructures;
import com.space_beyond.dim.sporos.structure.SporosStructures.Structure;
import com.space_beyond.dim.sporos.structure.SporosStructures.Type;
import com.space_beyond.dim.sporos.structure.StructureLoot;
import com.space_beyond.dim.sporos.structure.StructurePlan;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Wildnis-Strukturen von Sporos (Funkmast, Berghuette, Krankenhaus, Kraftwerk, Siedlung): Platzierung (deterministisch,
 * passendes Biom, nie in einer Stadt, im Bezirk), Erzeugung im Chunk-Puffer (Kennbloecke, Behaelter, Spawner, nur
 * freigegebene Bloecke), Loot-Tables.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosStructureGameTest {

    private SporosStructureGameTest() {}

    private static final long SEED = 24681357L;

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static WorldChunkManagerCelestial world(long seed) {
        return CelestialGenerators.require(SporosBlocks.BODY).chunkManager().apply(seed);
    }

    private static ChunkProviderCelestial provider(WorldChunkManagerCelestial world, long seed) {
        return CelestialGenerators.require(SporosBlocks.BODY).chunkProvider().apply(world, seed);
    }

    private static int idx(int lx, int y, int lz) {
        return (lx * 16 + lz) * 256 + y;
    }

    /** Erste Struktur der Art im Bezirksraster -12..12 (oder null). */
    private static Structure find(WorldChunkManagerCelestial world, Type type) {
        for (int d = 0; d < 625; d++) {
            Structure s = SporosStructures.ofDistrict(world, d % 25 - 12, d / 25 - 12);
            if (s != null && s.type() == type) return s;
        }
        return null;
    }

    @GameTest(template = "empty3x3x3", batch = "space_sporos_structure", timeoutTicks = 600)
    public static void structurePlacement(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        Map<Type, Structure> found = new EnumMap<>(Type.class);
        int total = 0;
        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                Structure s = SporosStructures.ofDistrict(world, dx, dz);
                check(s == SporosStructures.ofDistrict(world, dx, dz) || s.equals(SporosStructures.ofDistrict(world, dx, dz)), "deterministisch");
                if (s == null) continue;
                total++;
                found.putIfAbsent(s.type(), s);
                check(s.rotation() >= 0 && s.rotation() < 4, "Rotation 0-3");
                check(s.type().fits(world.getBiomeGenForCoords(s.x(), s.z())), s.type() + " steht in passendem Biom");
                // ganz im Bezirk
                int r = s.reach();
                check(Math.floorDiv((s.x() - r) >> 4, SporosStructures.DISTRICT_CHUNKS) == dx && Math.floorDiv((s.x() + r) >> 4, SporosStructures.DISTRICT_CHUNKS) == dx
                        && Math.floorDiv((s.z() - r) >> 4, SporosStructures.DISTRICT_CHUNKS) == dz && Math.floorDiv((s.z() + r) >> 4, SporosStructures.DISTRICT_CHUNKS) == dz,
                        "Struktur bleibt in ihrem Bezirk");
                // nie in einer Stadt
                for (int cx = (s.x() - r) >> 4; cx <= (s.x() + r) >> 4; cx++)
                    for (int cz = (s.z() - r) >> 4; cz <= (s.z() + r) >> 4; cz++)
                        check(SporosCities.cityAt(SEED, cx, cz) == null, "Struktur ueberschneidet eine Stadt");
                // structureAt findet sie von jedem beruehrten Chunk aus
                check(s.equals(SporosStructures.structureAt(world, s.x() >> 4, s.z() >> 4)), "structureAt am Mittelpunkt");
                check(s.equals(SporosStructures.structureAt(world, (s.x() + r) >> 4, (s.z() - r) >> 4)), "structureAt am Rand");
                check(SporosStructures.structureAt(world, (s.x() + r + 16) >> 4, (s.z() + r + 16) >> 4) == null, "ausserhalb nichts");
            }
        }
        check(total >= 120, "genug Strukturen im Raster: " + total);
        for (Type t : Type.values()) check(found.containsKey(t), "Art kommt vor: " + t);
        // Rotation: Rahmen dreht Richtungen wie Koordinaten (NORTH -> EAST bei 90 Grad)
        Structure rot = new Structure(Type.LODGE, 100, 200, 1, 1L);
        check(Frame.worldX(rot, 0, -1) == 101 && Frame.worldZ(rot, 0, -1) == 200, "Drehung (0,-1) -> (+1, 0)");
        check(Frame.rotation(1).rotate(net.minecraft.core.Direction.NORTH) == net.minecraft.core.Direction.EAST, "Richtung NORTH -> EAST");
        helper.succeed();
    }

    /** Chunk-Puffer jeder Art: Kennbloecke vorhanden, Behaelter und Spawner gesetzt, nur freigegebene Bloecke. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_structure", timeoutTicks = 2400)
    public static void structureChunks(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        ChunkProviderCelestial prov = provider(world, SEED);
        CityPalette pal = CityPalette.get();
        Set<Block> allowed = new HashSet<>(pal.allowedBlocks());
        allowed.addAll(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.PODZOL, Blocks.CLAY, Blocks.GRAVEL, Blocks.MUD, Blocks.ROOTED_DIRT, Blocks.MOSS_BLOCK,
                Blocks.GRASS_BLOCK, Blocks.WATER, Blocks.LAVA, Blocks.SAND, Blocks.DIRT, Blocks.STONE, Blocks.MYCELIUM, Blocks.COARSE_DIRT, Blocks.BEDROCK,
                SporosBlocks.SPOROS_STONE.get(), SporosBlocks.SPOROS_SILT.get()));
        Map<Type, List<Block>> marks = new EnumMap<>(Type.class);
        marks.put(Type.RADIO_TOWER, List.of(Blocks.ANDESITE_WALL, Blocks.LADDER, Blocks.CHEST, Blocks.BARREL, Blocks.RED_STAINED_GLASS));
        marks.put(Type.LODGE, List.of(Blocks.SPRUCE_STAIRS, Blocks.CAMPFIRE, Blocks.SNOW, Blocks.COBBLESTONE, Blocks.CHEST, SporosBlocks.SUPPLY_CRATE.get()));
        marks.put(Type.HOSPITAL, List.of(Blocks.RED_TERRACOTTA, Blocks.SPAWNER, Blocks.GLASS_PANE, pal.concreteWhite.getBlock(), Blocks.STONE_BRICK_STAIRS,
                SporosBlocks.MILITARY_CRATE.get()));
        marks.put(Type.POWER_PLANT, List.of(pal.brickConcrete.getBlock(), Blocks.ANDESITE_WALL, Blocks.WATER, pal.vConcreteGray.getBlock(),
                SporosBlocks.MILITARY_CRATE.get(), Blocks.HAY_BLOCK));
        marks.put(Type.SETTLEMENT, List.of(Blocks.STRIPPED_SPRUCE_LOG, Blocks.FARMLAND, Blocks.WHEAT, Blocks.CAMPFIRE, Blocks.HAY_BLOCK, Blocks.GLOWSTONE,
                Blocks.DAYLIGHT_DETECTOR, Blocks.DARK_OAK_DOOR, Blocks.LADDER, Blocks.WATER, Blocks.GLASS, Blocks.DIRT_PATH));
        for (Type t : Type.values()) {
            Structure s = find(world, t);
            check(s != null, "keine Struktur " + t);
            StructurePlan plan = SporosStructures.plan(world, s);
            check(plan.base > 60 && plan.base < 200, t + ": Grundhoehe " + plan.base);
            check(!plan.loot.isEmpty(), t + " hat Beutestellen");
            Set<Block> found = new HashSet<>();
            int containers = 0, spawners = 0;
            int r = s.type().radius;
            for (int cx = (s.x() - r) >> 4; cx <= (s.x() + r) >> 4; cx++) {
                for (int cz = (s.z() - r) >> 4; cz <= (s.z() + r) >> 4; cz++) {
                    BlockState[] blocks = prov.provideChunk(cx, cz).blocks;
                    for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
                        int x = cx * 16 + lx, z = cz * 16 + lz;
                        if (s.distance(x, z) > r) continue;
                        for (int y = plan.base - 5; y < plan.base + 40 && y < 256; y++) {
                            BlockState b = blocks[idx(lx, y, lz)];
                            if (b == null) continue;
                            found.add(b.getBlock());
                            check(allowed.contains(b.getBlock()), t + ": nicht freigegebener Block " + BuiltInRegistries.BLOCK.getKey(b.getBlock()) + " bei " + x + " " + y + " " + z);
                            if (b.is(Blocks.CHEST) || b.is(Blocks.BARREL) || b.is(SporosBlocks.SUPPLY_CRATE.get()) || b.is(SporosBlocks.MILITARY_CRATE.get())) containers++;
                            if (b.is(Blocks.SPAWNER)) spawners++;
                            if (b.is(Blocks.CAMPFIRE) && t == Type.LODGE) check(b.getValue(BlockStateProperties.LIT), "Huette: Kaminfeuer brennt");
                        }
                    }
                }
            }
            for (Block m : marks.get(t)) check(found.contains(m), t + ": Kennblock fehlt: " + BuiltInRegistries.BLOCK.getKey(m));
            check(containers >= Math.min(plan.loot.size(), 2), t + ": Behaelter gesetzt (" + containers + " von " + plan.loot.size() + ")");
            check(spawners == plan.spawners.size(), t + ": Spawner gesetzt (" + spawners + " von " + plan.spawners.size() + ")");
            if (t == Type.SETTLEMENT) {
                check(plan.spawners.isEmpty(), "Siedlung ohne Spawner");
                int villagers = 0, horses = 0;
                for (StructurePlan.MobSpot m : plan.mobs) {
                    check(m.kind() == com.space_beyond.dim.sporos.structure.StructureMobs.VILLAGER || m.kind() == com.space_beyond.dim.sporos.structure.StructureMobs.HORSE,
                            "Siedlung ohne Infizierte");
                    if (m.kind() == com.space_beyond.dim.sporos.structure.StructureMobs.VILLAGER) villagers++;
                    else horses++;
                }
                check(villagers >= 5 && horses == 3, "Siedlung: Siedler und Pferde (" + villagers + "/" + horses + ")");
                boolean bed = false;
                for (Block b : found) bed |= b instanceof net.minecraft.world.level.block.BedBlock;
                check(bed, "Siedlung: Betten fuer die Siedler");
            } else if (t != Type.RADIO_TOWER) {
                check(!plan.mobs.isEmpty(), t + ": Infizierte als Bewohner");
                for (StructurePlan.MobSpot m : plan.mobs) check(m.kind() < 10, t + ": nur Infizierte");
            }
            for (StructurePlan.MobSpot m : plan.mobs) check(Math.abs(m.u()) <= r && Math.abs(m.v()) <= r, t + ": Bewohner im Grundriss");
            // Beutestellen im Grundriss
            for (StructurePlan.Loot l : plan.loot) {
                check(Math.abs(l.u()) <= r && Math.abs(l.v()) <= r, t + ": Beutestelle im Grundriss");
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_sporos_structure", timeoutTicks = 100)
    public static void structureLootTables(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<ResourceLocation> ids = new java.util.ArrayList<>();
        for (Type t : Type.values()) ids.add(StructureLoot.tableId(t.table));
        // gemeinsame Unter-Tabellen (Waffen aller Stufen, HBM-/Space-Material, Snacks, Medizin)
        for (String c : List.of("guns_civil", "guns_military", "guns_rare", "salvage_hbm", "salvage_space", "snacks", "meds")) {
            ids.add(com.space_beyond.BeyondMod.id("chests/sporos_common/" + c));
        }
        for (ResourceLocation id : ids) {
            //? if < 1.21.1 {
            LootTable table = level.getServer().getLootData().getLootTable(id);
            //?} else {
            /*LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
            *///?}
            check(table != LootTable.EMPTY, "Loot-Table fehlt oder ist kaputt: " + id);
            int total = 0;
            for (int roll = 0; roll < 20; roll++) {
                LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).create(LootContextParamSets.CHEST);
                for (ItemStack s : table.getRandomItems(params, 2000L + roll)) {
                    check(!s.isEmpty() && s.getItem() != Items.AIR, "leerer Eintrag in " + id);
                    total += s.getCount();
                }
            }
            check(total > 10, "zu wenig Loot in " + id + ": " + total);
        }
        helper.succeed();
    }
}
