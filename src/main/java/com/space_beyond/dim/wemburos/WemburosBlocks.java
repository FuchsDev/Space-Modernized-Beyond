package com.space_beyond.dim.wemburos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.space_m.block.SpaceBlocks;
import com.space_m.dim.PlanetOres;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Bloecke des Planeten Wemburos (eigene Erweiterung, zweiter Planet im Sporos-System; Farbwelt Lila, Schwarz, Gold):
 * <ul>
 *   <li>{@code wemburos_stone} lila Planetengestein, {@code wemburos_regolith} dunkelvioletter Staubboden,
 *       {@code wemburos_onyx} schwarzer Onyx mit violetten Sprenkeln (Badlands, Riss, Tiefe).</li>
 *   <li>Baubloecke: {@code polished_wemburos_onyx}, {@code wemburos_bricks}, {@code gilded_wemburos_bricks}
 *       (Ziegel mit Goldeinlage).</li>
 *   <li>Biome: {@code duskwood_log}, {@code duskwood_planks}, {@code duskwood_leaves} (Baeume des Duskwood-Walds),
 *       {@code violet_moss} (Waldboden), {@code gilded_sand} (Goldduenen, faellt).</li>
 *   <li>Rohstoffe: {@code wemburos_gold_ore} (Rohgold wie Vanilla-Golderz, Eisenspitzhacke),
 *       {@code gilded_onyx} (Onyx mit Goldadern, droppt Goldnuggets), {@code unstable_crystal} (leuchtender
 *       Kristallblock) und {@code unstable_crystal_cluster} (Kristalldruse wie Amethyst) - beide liefern
 *       {@code unstable_shard}.</li>
 * </ul>
 * Modelle/Loot liegen als Hand-JSON vor (Datagen erzeugt fuer die {@code cube}-Bloecke dieselben Dateien).
 */
public final class WemburosBlocks {

    private WemburosBlocks() {}

    public static final String BODY = "wemburos";

    private static final List<Supplier<? extends Block>> SHOVEL = new ArrayList<>();
    private static final List<Supplier<? extends Block>> PICKAXE = new ArrayList<>();
    private static final List<Supplier<? extends Block>> IRON = new ArrayList<>();

    private static BlockBehaviour.Properties rock(float h, float r, MapColor color) {
        return PlanetOres.rock(h, r).mapColor(color);
    }

    public static final RegistrySupplier<Block> WEMBUROS_STONE = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("wemburos_stone",
            () -> new Block(rock(1.5F, 10.0F, MapColor.COLOR_PURPLE)), SpaceCreativeTabs.Tab.BLOCKS));
    public static final RegistrySupplier<Block> WEMBUROS_REGOLITH = shovel(com.space_beyond.block.BeyondBlocks.cube("wemburos_regolith",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(0.6F).sound(SoundType.SAND)),
            SpaceCreativeTabs.Tab.BLOCKS));
    public static final RegistrySupplier<Block> WEMBUROS_ONYX = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("wemburos_onyx",
            () -> new Block(rock(2.0F, 12.0F, MapColor.COLOR_BLACK).sound(SoundType.DEEPSLATE)), SpaceCreativeTabs.Tab.BLOCKS));
    public static final RegistrySupplier<Block> POLISHED_WEMBUROS_ONYX = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("polished_wemburos_onyx",
            () -> new Block(rock(2.0F, 12.0F, MapColor.COLOR_BLACK).sound(SoundType.POLISHED_DEEPSLATE)), SpaceCreativeTabs.Tab.BLOCKS));
    public static final RegistrySupplier<Block> WEMBUROS_BRICKS = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("wemburos_bricks",
            () -> new Block(rock(2.0F, 12.0F, MapColor.COLOR_PURPLE).sound(SoundType.DEEPSLATE_BRICKS)), SpaceCreativeTabs.Tab.BLOCKS));
    public static final RegistrySupplier<Block> GILDED_WEMBUROS_BRICKS = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("gilded_wemburos_bricks",
            () -> new Block(rock(2.0F, 12.0F, MapColor.GOLD).sound(SoundType.DEEPSLATE_BRICKS)), SpaceCreativeTabs.Tab.BLOCKS));

    /** Golderz im Wemburos-Gestein: Rohgold (Fortune), Eisenspitzhacke. */
    public static final RegistrySupplier<Block> WEMBUROS_GOLD_ORE = iron(PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.registerWithItem("wemburos_gold_ore",
            () -> new Block(rock(3.0F, 5.0F, MapColor.COLOR_PURPLE)), SpaceCreativeTabs.Tab.BLOCKS)));
    /** Onyx mit Goldadern (Spitzen im Goldhochland): 3-7 Goldnuggets (Fortune), Steinspitzhacke. */
    public static final RegistrySupplier<Block> GILDED_ONYX = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.registerWithItem("gilded_onyx",
            () -> new Block(rock(2.5F, 12.0F, MapColor.GOLD).sound(SoundType.NETHER_GOLD_ORE)),
            SpaceCreativeTabs.Tab.BLOCKS));
    /** Leuchtender instabiler Kristall (Licht 12): 2-4 Splitter, mit Behutsamkeit der Block. */
    public static final RegistrySupplier<Block> UNSTABLE_CRYSTAL = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.registerWithItem("unstable_crystal",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(1.5F).sound(SoundType.AMETHYST)
                    .lightLevel(s -> 12).requiresCorrectToolForDrops()), SpaceCreativeTabs.Tab.BLOCKS));
    /** Kristalldruse (wie Amethyst-Cluster, an jeder Flaeche), Licht 6, 1-2 Splitter. */
    public static final RegistrySupplier<Block> UNSTABLE_CRYSTAL_CLUSTER = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.registerWithItem("unstable_crystal_cluster",
            () -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).noOcclusion()
                    .sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel(s -> 6).pushReaction(PushReaction.DESTROY)),
            SpaceCreativeTabs.Tab.BLOCKS));

    // ── Biome-Erweiterung: Duskwood-Wald, Goldduenen ──

    private static final List<Supplier<? extends Block>> AXE = new ArrayList<>();
    private static final List<Supplier<? extends Block>> HOE = new ArrayList<>();
    private static final List<Supplier<? extends Block>> LOGS = new ArrayList<>();
    private static final List<Supplier<? extends Block>> LEAVES = new ArrayList<>();
    private static final List<Supplier<? extends Block>> PLANKS = new ArrayList<>();
    private static final List<Supplier<? extends Block>> DIRT = new ArrayList<>();
    private static final List<Supplier<? extends Block>> SAND = new ArrayList<>();

    private static BlockBehaviour.Properties wood(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava();
    }

    /** Duskwood-Stamm: schwarzviolette Rinde mit Goldadern (Baeume des Duskwood-Walds). */
    public static final RegistrySupplier<Block> DUSKWOOD_LOG = add(com.space_beyond.block.BeyondBlocks.registerWithItem("duskwood_log",
            () -> new RotatedPillarBlock(wood(MapColor.COLOR_PURPLE)), SpaceCreativeTabs.Tab.BLOCKS), AXE, LOGS);
    public static final RegistrySupplier<Block> DUSKWOOD_PLANKS = add(com.space_beyond.block.BeyondBlocks.registerWithItem("duskwood_planks",
            () -> new Block(wood(MapColor.COLOR_PURPLE)), SpaceCreativeTabs.Tab.BLOCKS), AXE, PLANKS);
    /** Duskwood-Laub: magenta-violett mit goldenen Funken, leuchtet schwach (Licht 3). */
    public static final RegistrySupplier<Block> DUSKWOOD_LEAVES = add(com.space_beyond.block.BeyondBlocks.registerWithItem("duskwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(0.2F).randomTicks()
                    .sound(SoundType.AZALEA_LEAVES).noOcclusion().lightLevel(st -> 3).isValidSpawn((st, l, ps, t) -> false)
                    .isSuffocating((st, l, ps) -> false).isViewBlocking((st, l, ps) -> false).ignitedByLava()
                    .pushReaction(PushReaction.DESTROY).isRedstoneConductor((st, l, ps) -> false)),
            SpaceCreativeTabs.Tab.BLOCKS), HOE, LEAVES);
    /** Violettes Moos: Waldboden des Duskwood-Walds (zaehlt als Erde, Pflanzen wachsen darauf). */
    public static final RegistrySupplier<Block> VIOLET_MOSS = add(com.space_beyond.block.BeyondBlocks.registerWithItem("violet_moss",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.3F).sound(SoundType.MOSS)),
            SpaceCreativeTabs.Tab.BLOCKS), HOE, DIRT);
    /** Goldsand der Goldduenen (faellt wie Sand). */
    public static final RegistrySupplier<Block> GILDED_SAND = add(shovel(com.space_beyond.block.BeyondBlocks.registerWithItem("gilded_sand",
            () -> fallingSand(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(0.5F).sound(SoundType.SAND)),
            SpaceCreativeTabs.Tab.BLOCKS)), SAND);

    private static Block fallingSand(BlockBehaviour.Properties p) {
        //? if < 1.21.1 {
        return new net.minecraft.world.level.block.SandBlock(0xD9A93A, p);
        //?} else {
        /*return new net.minecraft.world.level.block.ColoredFallingBlock(new net.minecraft.util.ColorRGBA(0xFFD9A93A), p);
        *///?}
    }

    @SafeVarargs
    private static <T extends Block> RegistrySupplier<T> add(RegistrySupplier<T> b, List<Supplier<? extends Block>>... lists) {
        for (List<Supplier<? extends Block>> l : lists) l.add(b);
        return b;
    }

    /** Splitter des instabilen Kristalls (Handwerk: 4 -> Kristallblock, Goldbarren + Splitter -> vergoldete Ziegel). */
    public static final RegistrySupplier<Item> UNSTABLE_SHARD = com.space_beyond.item.BeyondItems.register("unstable_shard", () -> new Item(new Item.Properties()),
            SpaceCreativeTabs.Tab.PARTS);

    private static <T extends Block> RegistrySupplier<T> shovel(RegistrySupplier<T> b) {
        SHOVEL.add(b);
        return b;
    }

    private static <T extends Block> RegistrySupplier<T> iron(RegistrySupplier<T> b) {
        IRON.add(b);
        return b;
    }

    /** Werkzeug-Tags (Datagen {@code SpaceTagProviders}); Spitzhacke ueber {@link PlanetOres#pickaxeBlocks()}. */
    public static void addTags(BiConsumer<TagKey<Block>, Block> tag) {
        for (Supplier<? extends Block> b : SHOVEL) tag.accept(BlockTags.MINEABLE_WITH_SHOVEL, b.get());
        for (Supplier<? extends Block> b : IRON) tag.accept(BlockTags.NEEDS_IRON_TOOL, b.get());
        for (Supplier<? extends Block> b : AXE) tag.accept(BlockTags.MINEABLE_WITH_AXE, b.get());
        for (Supplier<? extends Block> b : HOE) tag.accept(BlockTags.MINEABLE_WITH_HOE, b.get());
        for (Supplier<? extends Block> b : LOGS) {
            tag.accept(BlockTags.LOGS, b.get());
            tag.accept(BlockTags.LOGS_THAT_BURN, b.get());
        }
        for (Supplier<? extends Block> b : LEAVES) tag.accept(BlockTags.LEAVES, b.get());
        for (Supplier<? extends Block> b : PLANKS) tag.accept(BlockTags.PLANKS, b.get());
        for (Supplier<? extends Block> b : DIRT) tag.accept(BlockTags.DIRT, b.get());
        for (Supplier<? extends Block> b : SAND) tag.accept(BlockTags.SAND, b.get());
    }

    public static void init() {
    }
}
