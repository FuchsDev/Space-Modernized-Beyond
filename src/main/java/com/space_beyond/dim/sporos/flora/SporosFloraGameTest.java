package com.space_beyond.dim.sporos.flora;

import java.util.List;
import java.util.Random;

import com.space_m.SpaceMod;
import com.space_m.dim.CelestialSeed;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.sporos.WorldProviderSporos;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Sporos R2b (Flora): Hyphe-Holz, Pflanzen und Biome - Registrierung, Tags, Rezepte, Beute, Baumformen
 * ({@link HypheTree} erzeugt Staemme und Laub), Biomverteilung des Generators.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosFloraGameTest {

    private SporosFloraGameTest() {}

    private static final String[] BLOCKS = {"hyphe_log", "stripped_hyphe_log", "dead_hyphe_log", "hyphe_planks", "hyphe_slab", "hyphe_stairs",
            "hyphe_fence", "hyphe_fence_gate", "hyphe_door", "hyphe_trapdoor", "hyphe_leaves", "hyphe_sapling", "spore_pod", "glow_cap", "mycel_vine",
            "giant_fern", "moss_cushion", "shelf_fungus", "marsh_reed", "spore_bloom", "fungal_stem", "fungal_cap"};
    private static final String[] BIOMES = {"sporos_overgrown_plains", "sporos_moss_forest", "sporos_swamp", "sporos_river", "sporos_fungal_grove",
            "sporos_highland", "sporos_spore_field", "sporos_coast", "sporos_lake"};
    private static final String[] PLACED = {"trees_giant_dense", "trees_giant_sparse", "trees_canopy_dense", "trees_canopy_normal", "trees_canopy_sparse",
            "trees_swamp_dense", "trees_swamp_sparse", "giant_fungus_dense", "giant_fungus_sparse", "deadwood_dense", "deadwood_normal", "deadwood_sparse",
            "patch_giant_fern", "patch_marsh_reed", "patch_spore_bloom", "patch_spore_pod_dense", "patch_spore_pod_sparse", "patch_glow_cap_dense",
            "patch_glow_cap_sparse", "patch_moss_cushion", "glow_cap_caves"};
    private static final String[] RECIPES = {"hyphe_planks", "stripped_hyphe_log", "hyphe_slab", "hyphe_stairs", "hyphe_fence", "hyphe_fence_gate", "hyphe_door",
            "hyphe_trapdoor", "yellow_dye_from_spore_bloom", "yellow_dye_from_spore_dust", "paper_from_marsh_reed", "bone_meal_from_spore_dust"};

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 100)
    public static void sporosFloraRegistries(GameTestHelper helper) {
        for (String b : BLOCKS) {
            check(BuiltInRegistries.BLOCK.containsKey(com.space_beyond.BeyondMod.id(b)), "block space_beyond:" + b + " missing");
            check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id(b)), "block item space_beyond:" + b + " missing");
        }
        check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id("spore_dust")), "item spore_dust missing");
        for (String f : new String[] {"hyphe_tree_giant", "hyphe_tree_canopy", "hyphe_tree_swamp", "hyphe_deadwood", "giant_fungus"})
            check(BuiltInRegistries.FEATURE.containsKey(com.space_beyond.BeyondMod.id(f)), "feature space_beyond:" + f + " missing");
        var access = helper.getLevel().getServer().registryAccess();
        var biomes = access.registryOrThrow(Registries.BIOME);
        for (String b : BIOMES) check(biomes.containsKey(com.space_beyond.BeyondMod.id(b)), "biome space_beyond:" + b + " missing");
        check(BiomeGenBaseSporos.ALL.length == 9, "9 Sporos biomes in code");
        var placed = access.registryOrThrow(Registries.PLACED_FEATURE);
        for (String p : PLACED) check(placed.containsKey(com.space_beyond.BeyondMod.id("sporos/" + p)), "placed feature sporos/" + p + " missing");
        var recipes = helper.getLevel().getServer().getRecipeManager();
        for (String r : RECIPES) check(recipes.byKey(com.space_beyond.BeyondMod.id("sporos/" + r)).isPresent(), "recipe sporos/" + r + " missing");
        // Nahrung/Licht/Kompost
        check(isEdible(SporosFlora.GLOW_CAP.get().asItem().getDefaultInstance()) && isEdible(SporosFlora.SHELF_FUNGUS.get().asItem().getDefaultInstance()), "glow_cap/shelf_fungus edible");
        check(SporosFlora.GLOW_CAP.get().defaultBlockState().getLightEmission() == 12, "glow_cap light 12");
        check(net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.containsKey(SporosFlora.GIANT_FERN.get().asItem()), "giant_fern compostable");
        helper.succeed();
    }

    private static boolean isEdible(ItemStack st) {
        //? if <1.21.1 {
        return st.isEdible();
        //?} else {
        /*return st.has(net.minecraft.core.component.DataComponents.FOOD);
        *///?}
    }

    /** Tags aus dem Datagen ({@code SporosFlora.addTags}): Stamm/Bretter/Laub/Setzling/Kletterbar/Werkzeuge. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 100)
    public static void sporosFloraTags(GameTestHelper helper) {
        check(SporosFlora.HYPHE_LOG.get().defaultBlockState().is(BlockTags.LOGS), "hyphe_log #logs");
        check(SporosFlora.HYPHE_LOG.get().defaultBlockState().is(BlockTags.LOGS_THAT_BURN), "hyphe_log #logs_that_burn");
        check(SporosFlora.DEAD_HYPHE_LOG.get().defaultBlockState().is(BlockTags.LOGS), "dead_hyphe_log #logs");
        check(SporosFlora.HYPHE_PLANKS.get().defaultBlockState().is(BlockTags.PLANKS), "hyphe_planks #planks");
        check(SporosFlora.HYPHE_PLANKS.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "hyphe_planks axe");
        check(SporosFlora.HYPHE_SLAB.get().defaultBlockState().is(BlockTags.WOODEN_SLABS), "hyphe_slab #wooden_slabs");
        check(SporosFlora.HYPHE_STAIRS.get().defaultBlockState().is(BlockTags.WOODEN_STAIRS), "hyphe_stairs #wooden_stairs");
        check(SporosFlora.HYPHE_FENCE.get().defaultBlockState().is(BlockTags.WOODEN_FENCES), "hyphe_fence #wooden_fences");
        check(SporosFlora.HYPHE_FENCE_GATE.get().defaultBlockState().is(BlockTags.FENCE_GATES), "hyphe_fence_gate #fence_gates");
        check(SporosFlora.HYPHE_DOOR.get().defaultBlockState().is(BlockTags.WOODEN_DOORS), "hyphe_door #wooden_doors");
        check(SporosFlora.HYPHE_TRAPDOOR.get().defaultBlockState().is(BlockTags.WOODEN_TRAPDOORS), "hyphe_trapdoor #wooden_trapdoors");
        check(SporosFlora.HYPHE_LEAVES.get().defaultBlockState().is(BlockTags.LEAVES), "hyphe_leaves #leaves");
        check(SporosFlora.HYPHE_SAPLING.get().defaultBlockState().is(BlockTags.SAPLINGS), "hyphe_sapling #saplings");
        check(SporosFlora.MYCEL_VINE.get().defaultBlockState().is(BlockTags.CLIMBABLE), "mycel_vine #climbable");
        check(SporosFlora.SPORE_BLOOM.get().defaultBlockState().is(BlockTags.FLOWERS), "spore_bloom #flowers");
        check(SporosFlora.MOSS_CUSHION.get().defaultBlockState().is(BlockTags.DIRT), "moss_cushion #dirt");
        var cityTag = helper.getLevel().getServer().registryAccess().registryOrThrow(Registries.BIOME)
                .getTag(net.minecraft.tags.TagKey.create(Registries.BIOME, com.space_beyond.BeyondMod.id("sporos_city_biomes")));
        check(cityTag.isPresent() && cityTag.get().size() == 2, "sporos_city_biomes keeps 2 biomes (Runde 3)");
        helper.succeed();
    }

    /**
     * Geflecht-Ranke: haengende Straenge halten unter Laub und untereinander, in freier Luft verschwinden sie bei
     * der Nachbarpruefung; Wachstum nur nach unten.
     */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 20)
    public static void mycelVineHangs(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos leaf = helper.absolutePos(new BlockPos(1, 3, 1)).above(20);
        BlockState vine = SporosFlora.MYCEL_VINE.get().defaultBlockState().setValue(net.minecraft.world.level.block.VineBlock.UP, true);
        level.setBlock(leaf, SporosFlora.HYPHE_LEAVES.get().defaultBlockState(), 3);
        level.setBlock(leaf.below(), vine, 3);
        level.setBlock(leaf.below(2), vine, 3);
        check(level.getBlockState(leaf.below()).is(SporosFlora.MYCEL_VINE.get()) && level.getBlockState(leaf.below(2)).is(SporosFlora.MYCEL_VINE.get()), "Strang unter Laub bleibt");
        check(vine.canSurvive(level, leaf.below(2)), "zweites Glied haelt an der Ranke darueber");
        // freie Luft: kein Halt
        BlockPos free = leaf.offset(3, 0, 0);
        check(!vine.canSurvive(level, free), "ohne Halt kein Ueberleben");
        level.setBlock(free, vine, 3);
        level.getBlockState(free).updateNeighbourShapes(level, free, 3);
        BlockState after = level.getBlockState(free).updateShape(net.minecraft.core.Direction.UP, level.getBlockState(free.above()), level, free, free.above());
        check(after.isAir(), "Nachbarpruefung entfernt die haltlose Ranke");
        // Wachstum nach unten (1/4 je Tick): nach 40 Ticks mit festem Zufall sicher gewachsen, nie seitlich
        var block = (com.space_beyond.dim.sporos.flora.block.BlockMycelVine) SporosFlora.MYCEL_VINE.get();
        for (int i = 0; i < 40 && !level.getBlockState(leaf.below(3)).is(SporosFlora.MYCEL_VINE.get()); i++) {
            block.randomTick(level.getBlockState(leaf.below(2)), level, leaf.below(2), net.minecraft.util.RandomSource.create(i));
        }
        check(level.getBlockState(leaf.below(3)).is(SporosFlora.MYCEL_VINE.get()), "Strang waechst nach unten");
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            check(!level.getBlockState(leaf.below(2).relative(d)).is(SporosFlora.MYCEL_VINE.get()), "keine seitliche Ausbreitung");
        }
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 20)
    public static void sporosFloraDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        java.util.function.BiConsumer<BlockState, String> drops = (state, expected) -> {
            List<ItemStack> list = Block.getDrops(state, level, pos, null);
            check(!list.isEmpty(), BuiltInRegistries.BLOCK.getKey(state.getBlock()) + " drops nothing");
            String got = BuiltInRegistries.ITEM.getKey(list.get(0).getItem()).toString();
            check(got.equals(expected), BuiltInRegistries.BLOCK.getKey(state.getBlock()) + " drops " + got + ", expected " + expected);
        };
        drops.accept(SporosFlora.HYPHE_LOG.get().defaultBlockState(), "space_beyond:hyphe_log");
        drops.accept(SporosFlora.HYPHE_PLANKS.get().defaultBlockState(), "space_beyond:hyphe_planks");
        drops.accept(SporosFlora.HYPHE_SLAB.get().defaultBlockState(), "space_beyond:hyphe_slab");
        drops.accept(SporosFlora.HYPHE_DOOR.get().defaultBlockState(), "space_beyond:hyphe_door");
        drops.accept(SporosFlora.SPORE_POD.get().defaultBlockState(), "space_beyond:spore_dust");
        drops.accept(SporosFlora.GIANT_FERN.get().defaultBlockState(), "space_beyond:giant_fern");
        drops.accept(SporosFlora.GLOW_CAP.get().defaultBlockState(), "space_beyond:glow_cap");
        drops.accept(SporosFlora.FUNGAL_CAP.get().defaultBlockState(), "space_beyond:fungal_cap");
        List<ItemStack> dbl = Block.getDrops(SporosFlora.HYPHE_SLAB.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.DOUBLE), level, pos, null);
        check(dbl.size() == 1 && dbl.get(0).getCount() == 2, "double slab drops 2");
        helper.succeed();
    }

    /** Jede Baumform erzeugt Staemme und Laub (Plattform 60 Bloecke ueber der Vorlage, damit nichts hineinragt). */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 200)
    public static void sporosFloraTrees(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        int[] minLogs = {30, 8, 10};
        HypheTree.Shape[] shapes = HypheTree.Shape.values();
        for (int i = 0; i < shapes.length; i++) {
            BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1)).offset(i * 40, 60 + i * 30, 0);
            for (int dx = -8; dx <= 8; dx++) for (int dz = -8; dz <= 8; dz++) level.setBlock(base.offset(dx, -1, dz), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
            check(HypheTree.generate(level, new Random(1234L + i), base, shapes[i]), shapes[i] + " generate");
            int logs = 0, leaves = 0, vines = 0, maxY = 0;
            for (int dx = -14; dx <= 14; dx++) for (int dz = -14; dz <= 14; dz++) for (int dy = -2; dy <= 30; dy++) {
                BlockState s = level.getBlockState(base.offset(dx, dy, dz));
                if (s.is(SporosFlora.HYPHE_LOG.get())) { logs++; maxY = Math.max(maxY, dy); }
                else if (s.is(SporosFlora.HYPHE_LEAVES.get())) {
                    leaves++;
                    check(s.getValue(LeavesBlock.DISTANCE) <= 6, shapes[i] + " leaf distance");
                } else if (s.is(SporosFlora.MYCEL_VINE.get())) vines++;
            }
            com.mojang.logging.LogUtils.getLogger().info("[space_m] Hyphe {}: logs {} leaves {} vines {} height {}", shapes[i], logs, leaves, vines, maxY + 1);
            check(logs >= minLogs[i], shapes[i] + " logs " + logs);
            check(leaves >= 40, shapes[i] + " leaves " + leaves);
            check(maxY + 1 >= 8 && maxY + 1 <= 24, shapes[i] + " height " + (maxY + 1));
            if (shapes[i] == HypheTree.Shape.GIANT) check(maxY + 1 >= 16, "giant height " + (maxY + 1));
        }
        BlockPos dead = helper.absolutePos(new BlockPos(1, 1, 1)).offset(-40, 60, 0);
        for (int dx = -8; dx <= 8; dx++) for (int dz = -8; dz <= 8; dz++) level.setBlock(dead.offset(dx, -1, dz), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
        check(HypheTree.generateDeadwood(level, new Random(7L), dead), "deadwood generate");
        check(HypheTree.generateGiantFungus(level, new Random(8L), dead.offset(0, 0, 8)), "giant fungus generate");
        check(level.getBlockState(dead.offset(0, 2, 8)).is(SporosFlora.FUNGAL_STEM.get()), "fungus stem");
        helper.succeed();
    }

    /** GenLayer-Kette: alle neun Biome kommen vor, Kuesten liegen an Seen. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_flora", timeoutTicks = 200)
    public static void sporosBiomes(GameTestHelper helper) {
        long seed = CelestialSeed.get();
        WorldChunkManagerCelestial wcm = new WorldChunkManagerCelestial(WorldProviderSporos.createBiomeGenerators(seed), seed, BiomeGenBaseSporos.overgrownPlains);
        int[] counts = new int[BiomeGenBaseSporos.ALL.length];
        var arr = wcm.loadBlockGeneratorData(null, -1024, -1024, 2048, 2048);
        for (var b : arr) {
            for (int i = 0; i < BiomeGenBaseSporos.ALL.length; i++) if (b == BiomeGenBaseSporos.ALL[i]) counts[i]++;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < counts.length; i++) sb.append(BiomeGenBaseSporos.ALL[i].key.location().getPath()).append('=').append(counts[i]).append(' ');
        com.mojang.logging.LogUtils.getLogger().info("[space_m] Sporos biomes {}", sb);
        for (int i = 0; i < counts.length; i++) check(counts[i] > 0, "biome missing in layer: " + BiomeGenBaseSporos.ALL[i].key.location() + " " + sb);
        // Kueste grenzt an See: mindestens eine Kuestenzelle mit See-Nachbar
        boolean adjacent = false;
        for (int z = 1; z < 2047 && !adjacent; z++) for (int x = 1; x < 2047; x++) {
            if (arr[x + z * 2048] == BiomeGenBaseSporos.coast && (arr[x + 1 + z * 2048] == BiomeGenBaseSporos.lake || arr[x + (z + 1) * 2048] == BiomeGenBaseSporos.lake)) { adjacent = true; break; }
        }
        check(adjacent, "coast next to lake");
        helper.succeed();
    }

    @SuppressWarnings("unused")
    private static ResourceLocation id(String s) {
        return com.space_beyond.BeyondMod.id(s);
    }
}
