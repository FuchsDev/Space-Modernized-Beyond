package com.space_beyond.test;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.space_m.SpaceMod;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_beyond.celestial.SporosSystem;
import com.space_beyond.celestial.StarSystem;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.SpaceDimensions;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.wemburos.WemburosBlocks;
import com.space_beyond.dim.wemburos.biome.BiomeGenBaseWemburos;
import com.space_beyond.dim.wemburos.structure.WemburosStructures;
import com.space_beyond.dim.wemburos.structure.WemburosStructures.Plan;
import com.space_beyond.dim.wemburos.structure.WemburosStructures.Structure;
import com.space_beyond.dim.wemburos.structure.WemburosStructures.Type;

import net.minecraft.core.BlockPos;
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
 * Planet Wemburos (Sporos-System): Himmelskoerper (Venusgroesse, Bahn innerhalb Sporos, Dimension), Gelaende
 * (Biome, Wemburos-Gestein, kein Meer), Strukturen (Platzierung, Kennbloecke, Loot, Bewohner), Loot-Tables.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WemburosGameTest {

    private WemburosGameTest() {}

    private static final long SEED = 97531L;

    private static void check(boolean c, String msg) {
        if (!c) throw new GameTestAssertException(msg);
    }

    private static WorldChunkManagerCelestial world(long seed) {
        return CelestialGenerators.require(WemburosBlocks.BODY).chunkManager().apply(seed);
    }

    @GameTest(template = "empty3x3x3", batch = "space_wemburos", timeoutTicks = 40)
    public static void wemburosBody(GameTestHelper helper) {
        CelestialBody w = CelestialBody.getBody("wemburos");
        check(w == SporosSystem.wemburos && w.parent == SporosSystem.star, "Wemburos umkreist den Sporos-Stern");
        check(StarSystem.of(w) == StarSystem.byId(SporosSystem.SYSTEM_ID), "im Sporos-System");
        check(w.radiusKm == 6_052 && w.massKg == 4.87e24F, "Venusgroesse");
        check(com.space_beyond.celestial.BeyondBodies.WEMBUROS.ordinal() == 12 && com.space_beyond.celestial.BeyondBodies.WEMBUROS.getBody() == w, "Body-Enum hinten (12)");
        check(CelestialBody.getBody(com.space_beyond.dim.BeyondDimensions.WEMBUROS) == w && w.dimension == com.space_beyond.dim.BeyondDimensions.WEMBUROS, "Dimension");
        check(SporosSystem.WEMBUROS_SEMI_MAJOR_AXIS_KM < SporosSystem.PLANET_SEMI_MAJOR_AXIS_KM, "innerer Planet");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_wemburos", timeoutTicks = 600)
    public static void wemburosTerrain(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        Set<BiomeGenBaseCelestial> seen = new HashSet<>();
        for (int x = -4096; x <= 4096; x += 256) for (int z = -4096; z <= 4096; z += 256) seen.add(world.getBiomeGenForCoords(x, z));
        for (BiomeGenBaseWemburos b : BiomeGenBaseWemburos.ALL) check(seen.contains(b), "Biom kommt vor: " + b.key.location());
        ChunkProviderCelestial prov = CelestialGenerators.require(WemburosBlocks.BODY).chunkProvider().apply(world, SEED);
        Set<Block> blocks = new HashSet<>();
        for (int cx = 0; cx < 6; cx++) for (int cz = 0; cz < 6; cz++) {
            BlockState[] b = prov.provideChunk(cx * 7, cz * 7).blocks;
            for (BlockState s : b) if (s != null) {
                blocks.add(s.getBlock());
                check(s.getFluidState().isEmpty() || s.is(Blocks.LAVA), "kein Wasser auf Wemburos");
            }
        }
        check(blocks.contains(WemburosBlocks.WEMBUROS_STONE.get()) && blocks.contains(WemburosBlocks.WEMBUROS_REGOLITH.get()), "Gestein und Regolith");
        check(blocks.contains(WemburosBlocks.WEMBUROS_GOLD_ORE.get()), "Golderz");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_wemburos", timeoutTicks = 1200)
    public static void wemburosBiomeFeatures(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        ChunkProviderCelestial prov = CelestialGenerators.require(WemburosBlocks.BODY).chunkProvider().apply(world, SEED);
        java.util.Map<BiomeGenBaseWemburos, List<Block>> marks = new java.util.LinkedHashMap<>();
        marks.put(BiomeGenBaseWemburos.duskwoodForest, List.of(WemburosBlocks.VIOLET_MOSS.get(), WemburosBlocks.DUSKWOOD_LOG.get(), WemburosBlocks.DUSKWOOD_LEAVES.get()));
        marks.put(BiomeGenBaseWemburos.onyxSpires, List.of(WemburosBlocks.WEMBUROS_ONYX.get(), WemburosBlocks.GILDED_ONYX.get()));
        marks.put(BiomeGenBaseWemburos.goldenDunes, List.of(WemburosBlocks.GILDED_SAND.get()));
        marks.put(BiomeGenBaseWemburos.chorusWastes, List.of(Blocks.END_STONE, Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER));
        marks.put(BiomeGenBaseWemburos.caldera, List.of(Blocks.BLACKSTONE, Blocks.BASALT));
        for (var e : marks.entrySet()) {
            // Mitte eines Gebiets des Bioms suchen (Chunkmitte im Biom, damit die grossen Objekte erscheinen)
            List<int[]> chunks = new java.util.ArrayList<>();
            for (int cx = -200; cx <= 200 && chunks.size() < 12; cx += 3) for (int cz = -200; cz <= 200 && chunks.size() < 12; cz += 3) {
                if (world.getBiomeGenForCoords(cx * 16 + 8, cz * 16 + 8) == e.getKey()) chunks.add(new int[] {cx, cz});
            }
            check(!chunks.isEmpty(), "Biom nicht gefunden: " + e.getKey().key.location());
            Set<Block> seen = new HashSet<>();
            for (int[] c : chunks) for (BlockState st : prov.provideChunk(c[0], c[1]).blocks) if (st != null) seen.add(st.getBlock());
            for (Block m : e.getValue()) check(seen.contains(m), e.getKey().key.location() + ": Kennblock fehlt " + BuiltInRegistries.BLOCK.getKey(m));
        }
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_wemburos", timeoutTicks = 3600)
    public static void wemburosStructures(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        Map<Type, Structure> found = new EnumMap<>(Type.class);
        for (int dx = -10; dx <= 10; dx++) for (int dz = -10; dz <= 10; dz++) {
            Structure s = WemburosStructures.ofDistrict(world, dx, dz);
            if (s == null) continue;
            check(s.equals(WemburosStructures.ofDistrict(world, dx, dz)), "deterministisch");
            check(s.type().fits(world.getBiomeGenForCoords(s.x(), s.z())), "passendes Biom");
            int r = s.reach();
            check(Math.floorDiv((s.x() - r) >> 4, WemburosStructures.DISTRICT_CHUNKS) == dx && Math.floorDiv((s.x() + r) >> 4, WemburosStructures.DISTRICT_CHUNKS) == dx,
                    "im Bezirk");
            found.putIfAbsent(s.type(), s);
        }
        for (Type t : Type.values()) check(found.containsKey(t), "Struktur kommt vor: " + t);

        ChunkProviderCelestial prov = CelestialGenerators.require(WemburosBlocks.BODY).chunkProvider().apply(world, SEED);
        Map<Type, List<Block>> marks = new EnumMap<>(Type.class);
        marks.put(Type.THRONE, List.of(Blocks.GOLD_BLOCK, Blocks.PURPUR_PILLAR, Blocks.PURPUR_STAIRS, Blocks.PURPLE_CARPET, Blocks.CHEST,
                WemburosBlocks.POLISHED_WEMBUROS_ONYX.get(), WemburosBlocks.UNSTABLE_CRYSTAL.get(),
                // Zitadelle: Kaverne, Spawner, Turmleitern, Sternenkuppel, Fallgitter, Ketten, Rosette, Graben, Gaerten, HBM-Obsidianziegel
                Blocks.MAGMA_BLOCK, Blocks.LAVA, Blocks.SPAWNER, Blocks.LADDER, Blocks.PEARLESCENT_FROGLIGHT, Blocks.IRON_BARS, Blocks.BARREL, Blocks.CHAIN,
                Blocks.MAGENTA_STAINED_GLASS, Blocks.SOUL_FIRE, Blocks.DEEPSLATE_TILES, Blocks.AMETHYST_BLOCK, WemburosBlocks.DUSKWOOD_LEAVES.get(),
                com.hbm_m.block.ModBlocks.BRICK_OBSIDIAN.get()));
        marks.put(Type.VAULT, List.of(Blocks.GOLD_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.SPAWNER, Blocks.CHEST, Blocks.BARREL, Blocks.POLISHED_BLACKSTONE_STAIRS,
                WemburosBlocks.GILDED_WEMBUROS_BRICKS.get()));
        marks.put(Type.PORTAL, List.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.GOLD_BLOCK, Blocks.CHEST, WemburosBlocks.GILDED_ONYX.get(),
                WemburosBlocks.UNSTABLE_CRYSTAL_CLUSTER.get()));
        for (Type t : Type.values()) {
            Structure s = found.get(t);
            Plan plan = WemburosStructures.plan(world, s);
            check(!plan.loot.isEmpty() && !plan.mobs.isEmpty(), t + ": Loot und Bewohner");
            Set<Block> seen = new HashSet<>();
            int r = t.radius;
            for (int cx = (s.x() - r) >> 4; cx <= (s.x() + r) >> 4; cx++) for (int cz = (s.z() - r) >> 4; cz <= (s.z() + r) >> 4; cz++) {
                BlockState[] b = prov.provideChunk(cx, cz).blocks;
                for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
                    if (s.distance(cx * 16 + lx, cz * 16 + lz) > r) continue;
                    int lo = t == Type.THRONE ? -52 : -14, hi = t == Type.THRONE ? 112 : 20;
                    for (int y = Math.max(1, plan.base + lo); y < Math.min(255, plan.base + hi); y++) {
                        BlockState st = b[(lx * 16 + lz) * 256 + y];
                        if (st != null) seen.add(st.getBlock());
                    }
                }
            }
            for (Block m : marks.get(t)) check(seen.contains(m), t + ": Kennblock fehlt " + BuiltInRegistries.BLOCK.getKey(m));
            for (WemburosStructures.Loot l : plan.loot) {
                BlockPos p = WemburosStructures.worldPos(plan, l.u(), l.y(), l.v());
                check(s.distance(p.getX(), p.getZ()) <= r, t + ": Beutestelle im Grundriss");
            }
            if (t == Type.THRONE) {
                // Boss steht frei auf dem Saalboden (Teppich), darueber Luft
                for (WemburosStructures.MobSpot m : plan.mobs) {
                    if (m.kind() != com.space_beyond.dim.sporos.structure.StructureMobs.UNSTABLE_SOVEREIGN) continue;
                    BlockPos p = WemburosStructures.worldPos(plan, m.u(), m.y(), m.v());
                    BlockState[] b = prov.provideChunk(p.getX() >> 4, p.getZ() >> 4).blocks;
                    int i = ((p.getX() & 15) * 16 + (p.getZ() & 15)) * 256 + p.getY();
                    check(b[i - 1] != null && !b[i - 1].isAir(), "Thronsaal: Boden unter dem Boss");
                    for (int dy = 1; dy <= 12; dy++) check(b[i + dy] == null || b[i + dy].isAir(), "Thronsaal: Platz ueber dem Boss");
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_wemburos", timeoutTicks = 100)
    public static void wemburosLootTables(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        for (Type t : Type.values()) {
            ResourceLocation id = WemburosStructures.tableId(t.table);
            //? if < 1.21.1 {
            LootTable table = level.getServer().getLootData().getLootTable(id);
            //?} else {
            /*LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
            *///?}
            check(table != LootTable.EMPTY, "Loot-Table fehlt: " + id);
            int total = 0;
            for (int roll = 0; roll < 10; roll++) {
                LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).create(LootContextParamSets.CHEST);
                for (ItemStack s : table.getRandomItems(params, 3000L + roll)) {
                    check(!s.isEmpty() && s.getItem() != Items.AIR, "leerer Eintrag in " + id);
                    total += s.getCount();
                }
            }
            check(total > 10, "zu wenig Loot in " + id);
        }
        helper.succeed();
    }
}
