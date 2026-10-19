package com.space_beyond.dim.sporos.flora;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.space_m.block.SpaceBlocks;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.flora.block.BlockGlowCap;
import com.space_beyond.dim.sporos.flora.block.BlockHypheSapling;
import com.space_beyond.dim.sporos.flora.block.BlockMossCushion;
import com.space_beyond.dim.sporos.flora.block.BlockMycelVine;
import com.space_beyond.dim.sporos.flora.block.BlockShelfFungus;
import com.space_beyond.dim.sporos.flora.block.BlockSporePod;
import com.space_beyond.dim.sporos.flora.block.BlockSporosDoublePlant;
import com.space_beyond.dim.sporos.flora.block.BlockSporosPlant;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Flora des Planeten Sporos (eigene Erweiterung, kein NTM-Original): Holzart <b>Hyphe</b> (Stamm, entrindet,
 * Totholz, Bretter, Stufe, Treppe, Zaun, Tor, Tuer, Falltuer, Laub, Setzling), zehn Pflanzen und die
 * Riesenpilz-Bloecke. Alle Texturen sind eigene Pixel-Art ({@code scratchpad/sporos/gen_flora_tex.py}); Modelle,
 * Loot und Rezepte liegen handgeschrieben unter {@code src/main/resources} ({@code gen_flora_json.py}).
 *
 * <p>Baeume: {@link HypheTree} (drei Formen) ueber die Features in {@link SporosFloraFeatures}; der Setzling
 * ({@link BlockHypheSapling}) waechst zu einer zufaelligen Form.
 *
 * <p>Pflanzen und Zweck:
 * <ul>
 *   <li>{@code spore_pod} Sporenkapsel - platzt bei Beruehrung (Partikel, kurze Uebelkeit), Beute Sporenstaub.</li>
 *   <li>{@code glow_cap} Leuchtpilz - Lichtquelle 12, essbar, waechst auch auf Gestein (Hoehlen).</li>
 *   <li>{@code mycel_vine} Geflecht-Ranke - haengt von Decken/Laub, kletterbar.</li>
 *   <li>{@code giant_fern} Riesenfarn - zwei Bloecke hoch, Kompost.</li>
 *   <li>{@code moss_cushion} Moospolster - begehbarer Vollblock, daempft Fallschaden (80 %).</li>
 *   <li>{@code shelf_fungus} Pilzregal - an Staemmen/Waenden, essbar.</li>
 *   <li>{@code marsh_reed} Sumpfgras - zwei Bloecke hoch am Wasser, ergibt Papier.</li>
 *   <li>{@code spore_bloom} Sporenbluete - Sporenstaub-Partikel, Faerbemittel (gelb).</li>
 *   <li>{@code fungal_stem}/{@code fungal_cap} Riesenpilz-Stiel/-Hut - Baustoff der Pilzhaine, Kompost.</li>
 *   <li>{@code spore_dust} (Item) Sporenstaub - Faerbemittel (gelb), Kompost.</li>
 *   <li>{@code antimycotic} (Item, Runde 4) Antimykotikum - heilt die Sporeninfektion.</li>
 * </ul>
 */
public final class SporosFlora {

    private SporosFlora() {}

    private static final Map<TagKey<Block>, List<Supplier<? extends Block>>> TAGS = new LinkedHashMap<>();

    @SafeVarargs
    private static <T extends Block> RegistrySupplier<T> tag(RegistrySupplier<T> b, TagKey<Block>... tags) {
        for (TagKey<Block> t : tags) TAGS.computeIfAbsent(t, k -> new ArrayList<>()).add(b);
        return b;
    }

    // ── Eigenschaften ──
    private static BlockBehaviour.Properties wood() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(2.0F, 3.0F)
                .sound(SoundType.WOOD).ignitedByLava();
    }

    private static BlockBehaviour.Properties log(MapColor top) {
        return BlockBehaviour.Properties.of().mapColor(s -> s.getValue(RotatedPillarBlock.AXIS) == net.minecraft.core.Direction.Axis.Y ? top : MapColor.TERRACOTTA_LIGHT_GREEN)
                .strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava();
    }

    private static BlockBehaviour.Properties leaves() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                .noOcclusion().isValidSpawn((s, l, p, t) -> false).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)
                .ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s, l, p) -> false);
    }

    private static BlockBehaviour.Properties plant() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY);
    }

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition)
                //? if < 1.21.1 {
                .saturationMod(saturation)
                //?} else {
                /*.saturationModifier(saturation)
                *///?}
                .build();
    }

    // ── Boeden ──
    /** Boden fuer Setzling und Pflanzen: alles Erdige, Moos, Humus, Schlamm. */
    public static final Predicate<BlockState> SOIL = s -> s.is(BlockTags.DIRT) || s.is(Blocks.MOSS_BLOCK) || s.is(Blocks.MUD)
            || s.is(Blocks.CLAY) || s.is(SporosBlocks.SPOROS_SILT.get()) || s.is(SporosFlora.MOSS_CUSHION.get()) || s.is(Blocks.FARMLAND);
    /** Leuchtpilz zusaetzlich auf Gestein, Kies, Pilzstiel/-hut und Hyphe-Holz (Hoehlen, Totholz). */
    public static final Predicate<BlockState> FUNGUS_SOIL = s -> SOIL.test(s) || s.is(BlockTags.BASE_STONE_OVERWORLD)
            || s.is(SporosBlocks.SPOROS_STONE.get()) || s.is(Blocks.GRAVEL) || s.is(SporosFlora.FUNGAL_STEM.get()) || s.is(SporosFlora.FUNGAL_CAP.get())
            || s.is(BlockTags.LOGS) || s.is(Blocks.MYCELIUM) || s.is(Blocks.DEEPSLATE);
    /** Sumpfgras: Erdiges, Schlamm, Ton, Sand, Kies (Ufer). */
    public static final Predicate<BlockState> REED_SOIL = s -> SOIL.test(s) || s.is(BlockTags.SAND) || s.is(Blocks.GRAVEL);

    // ── Holz "Hyphe" ──
    public static final RegistrySupplier<Block> HYPHE_LOG = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_log",
            () -> new RotatedPillarBlock(log(MapColor.TERRACOTTA_WHITE)), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS, BlockTags.LOGS_THAT_BURN, BlockTags.OVERWORLD_NATURAL_LOGS);
    public static final RegistrySupplier<Block> STRIPPED_HYPHE_LOG = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("stripped_hyphe_log",
            () -> new RotatedPillarBlock(log(MapColor.TERRACOTTA_WHITE)), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS, BlockTags.LOGS_THAT_BURN);
    /** Totholz: abgestorbener/umgestuerzter Stamm, bruechig (Haerte 1), zaehlt als Stamm. */
    public static final RegistrySupplier<Block> DEAD_HYPHE_LOG = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("dead_hyphe_log",
            () -> new RotatedPillarBlock(log(MapColor.TERRACOTTA_GRAY).strength(1.0F, 2.0F)), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS, BlockTags.LOGS_THAT_BURN);
    public static final RegistrySupplier<Block> HYPHE_PLANKS = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_planks",
            () -> new Block(wood()), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.PLANKS);
    public static final RegistrySupplier<SlabBlock> HYPHE_SLAB = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_slab",
            () -> new SlabBlock(wood()), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_SLABS);
    public static final RegistrySupplier<StairBlock> HYPHE_STAIRS = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_stairs",
            () -> new StairBlock(HYPHE_PLANKS.get().defaultBlockState(), wood()), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_STAIRS);
    public static final RegistrySupplier<FenceBlock> HYPHE_FENCE = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_fence",
            () -> new FenceBlock(wood()), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_FENCES);
    public static final RegistrySupplier<FenceGateBlock> HYPHE_FENCE_GATE = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_fence_gate",
            //? if < 1.21.1 {
            () -> new FenceGateBlock(wood(), WoodType.OAK),
            //?} else {
            /*() -> new FenceGateBlock(WoodType.OAK, wood()),
            *///?}
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.FENCE_GATES);
    public static final RegistrySupplier<DoorBlock> HYPHE_DOOR = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_door",
            //? if < 1.21.1 {
            () -> new DoorBlock(wood().noOcclusion().pushReaction(PushReaction.DESTROY), BlockSetType.OAK),
            //?} else {
            /*() -> new DoorBlock(BlockSetType.OAK, wood().noOcclusion().pushReaction(PushReaction.DESTROY)),
            *///?}
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_DOORS);
    public static final RegistrySupplier<TrapDoorBlock> HYPHE_TRAPDOOR = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_trapdoor",
            //? if < 1.21.1 {
            () -> new TrapDoorBlock(wood().noOcclusion().isValidSpawn((s, l, p, t) -> false), BlockSetType.OAK),
            //?} else {
            /*() -> new TrapDoorBlock(BlockSetType.OAK, wood().noOcclusion().isValidSpawn((s, l, p, t) -> false)),
            *///?}
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_TRAPDOORS);
    public static final RegistrySupplier<Block> HYPHE_LEAVES = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_leaves",
            () -> new LeavesBlock(leaves()), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_HOE, BlockTags.LEAVES);
    public static final RegistrySupplier<Block> HYPHE_SAPLING = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("hyphe_sapling",
            () -> new BlockHypheSapling(plant().offsetType(BlockBehaviour.OffsetType.NONE)), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.SAPLINGS);

    // ── Pflanzen ──
    public static final RegistrySupplier<Block> SPORE_POD = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("spore_pod",
            () -> new BlockSporePod(plant().offsetType(BlockBehaviour.OffsetType.NONE).sound(SoundType.WET_GRASS)), SpaceCreativeTabs.Tab.BLOCKS),
            BlockTags.REPLACEABLE_BY_TREES);
    public static final RegistrySupplier<Block> GLOW_CAP = com.space_beyond.block.BeyondBlocks.registerWithItem("glow_cap",
            () -> new BlockGlowCap(plant().offsetType(BlockBehaviour.OffsetType.NONE).sound(SoundType.FUNGUS).lightLevel(s -> 12)),
            SpaceCreativeTabs.Tab.BLOCKS, b -> new BlockItem(b, new Item.Properties().food(food(2, 0.3F))));
    public static final RegistrySupplier<Block> MYCEL_VINE = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("mycel_vine",
            () -> new BlockMycelVine(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).replaceable().noCollission().randomTicks()
                    .strength(0.2F).sound(SoundType.VINE).ignitedByLava().pushReaction(PushReaction.DESTROY)),
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.CLIMBABLE, BlockTags.REPLACEABLE_BY_TREES);
    public static final RegistrySupplier<Block> GIANT_FERN = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("giant_fern",
            () -> new BlockSporosDoublePlant(plant(), SOIL), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.REPLACEABLE_BY_TREES);
    public static final RegistrySupplier<Block> MOSS_CUSHION = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("moss_cushion",
            () -> new BlockMossCushion(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.1F).sound(SoundType.MOSS)),
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_HOE, BlockTags.DIRT);
    public static final RegistrySupplier<Block> SHELF_FUNGUS = com.space_beyond.block.BeyondBlocks.registerWithItem("shelf_fungus",
            () -> new BlockShelfFungus(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noCollission().instabreak()
                    .sound(SoundType.FUNGUS).pushReaction(PushReaction.DESTROY).noOcclusion()),
            SpaceCreativeTabs.Tab.BLOCKS, b -> new BlockItem(b, new Item.Properties().food(food(3, 0.4F))));
    public static final RegistrySupplier<Block> MARSH_REED = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("marsh_reed",
            () -> new BlockSporosDoublePlant(plant(), REED_SOIL), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.REPLACEABLE_BY_TREES);
    public static final RegistrySupplier<Block> SPORE_BLOOM = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("spore_bloom",
            () -> new BlockSporosPlant(plant(), SOIL, true), SpaceCreativeTabs.Tab.BLOCKS), BlockTags.FLOWERS, BlockTags.REPLACEABLE_BY_TREES);
    public static final RegistrySupplier<Block> FUNGAL_STEM = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("fungal_stem",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(0.4F).sound(SoundType.WOOD)),
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE);
    public static final RegistrySupplier<Block> FUNGAL_CAP = tag(com.space_beyond.block.BeyondBlocks.registerWithItem("fungal_cap",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(0.3F).sound(SoundType.WOOD)),
            SpaceCreativeTabs.Tab.BLOCKS), BlockTags.MINEABLE_WITH_AXE);

    // ── Items ──
    /** Sporenstaub: Beute der Kapsel, Faerbemittel (gelb), Kompost. */
    public static final RegistrySupplier<Item> SPORE_DUST = com.space_beyond.item.BeyondItems.register("spore_dust", () -> new Item(new Item.Properties()), SpaceCreativeTabs.Tab.PARTS);
    /** Antimykotikum (Runde 4): Pille gegen die Sporeninfektion, s. {@link com.space_beyond.dim.sporos.spores.ItemAntimycotic}. */
    public static final RegistrySupplier<Item> ANTIMYCOTIC = com.space_beyond.item.BeyondItems.register("antimycotic",
            () -> new com.space_beyond.dim.sporos.spores.ItemAntimycotic(new Item.Properties()), SpaceCreativeTabs.Tab.CONSUMABLE);

    /** Tags (Datagen {@code SpaceTagProviders}). */
    public static void addTags(BiConsumer<TagKey<Block>, Block> tag) {
        TAGS.forEach((k, list) -> list.forEach(b -> tag.accept(k, b.get())));
    }

    /** Kompostierbarkeit (Vanilla-Werte: Setzling/Gras 0,3, Farn/Blume 0,65, Pilzbloecke 0,85) - nach der Registrierung. */
    public static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(HYPHE_LEAVES.get().asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(HYPHE_SAPLING.get().asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(MARSH_REED.get().asItem(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(GIANT_FERN.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(SPORE_BLOOM.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(GLOW_CAP.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(SHELF_FUNGUS.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(MYCEL_VINE.get().asItem(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(MOSS_CUSHION.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(FUNGAL_STEM.get().asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(FUNGAL_CAP.get().asItem(), 0.85F);
        ComposterBlock.COMPOSTABLES.put(SPORE_DUST.get(), 0.3F);
    }

    public static void init() {
        SporosFloraFeatures.init();
        dev.architectury.event.events.common.LifecycleEvent.SETUP.register(SporosFlora::registerCompostables);
    }
}
