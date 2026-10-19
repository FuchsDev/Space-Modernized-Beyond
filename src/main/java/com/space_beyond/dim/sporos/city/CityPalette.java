package com.space_beyond.dim.sporos.city;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.hbm_m.platform.BlockHooks;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.flora.SporosFlora;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Blockwahl der Sporos-Staedte. Nur Bau-, Glas-, Holz- und Pflanzenbloecke aus Vanilla, HBM und der Sporos-Flora,
 * die sich selbst droppen und sich nicht zu Metall verarbeiten lassen:
 * <ul>
 *   <li>Sporos-Flora (R2b, {@link SporosFlora}): Hyphe-Stamm und Totholz, Hyphe-Laub, Riesenpilz-Stiel/-Hut,
 *       Geflecht-Ranke (statt Vanilla-Ranke), Leuchtpilz, Moospolster, Riesenfarn, Sporenbluete, Sporenkapsel.
 *       Der Bewuchs der Staedte ist damit derselbe wie der des Planeten; Vanilla bleiben nur Gras, Farn, Moos,
 *       Seerosen und die kleinen Blumen der Biom-JSONs.</li>
 *   <li>HBM: Asphalt, Beton (glatt/rissig/bemoost/farbig/Fliese), Betonziegel (auch gebrochen), Labor- und
 *       Vinylfliesen, helle Ziegel, Beton-Stufen/-Platten. HBM-Beton schreddert zu Kies, Asphalt hat keinen
 *       Rueckweg.</li>
 *   <li>Bewusst NICHT: Stahlgitter/-traeger/-geruest/-masten/-dach/-zaun, Deko-Rohre, Ketten (alle schreddern zu
 *       Stahlstaub), Verstaerktes Glas (Eisengitter), Leuchten (Wolfram/Aluminium/Quecksilber), Bewehrungsbeton,
 *       Speicherbloecke, Erze, Vanilla-Eisengitter/Ketten/Laternen/Schienen/Kupfer, Quarz.</li>
 * </ul>
 * Jeder Zustand, den der Generator setzen darf, wird hier angelegt; {@link #allowedBlocks()} ist die Whitelist, die
 * der GameTest gegen die generierten Chunks prueft.
 */
public final class CityPalette {

    private static volatile CityPalette instance;

    public static CityPalette get() {
        CityPalette p = instance;
        if (p == null) {
            synchronized (CityPalette.class) {
                p = instance;
                if (p == null) instance = p = new CityPalette();
            }
        }
        return p;
    }

    /** Verbotene Vanilla-Bloecke (Metallgehalt oder Rueckgewinnung), fuer den Whitelist-Test. */
    public static final List<Block> FORBIDDEN_VANILLA = List.of(Blocks.IRON_BARS, Blocks.CHAIN, Blocks.LANTERN, Blocks.SOUL_LANTERN,
            Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.ANVIL, Blocks.CAULDRON, Blocks.HOPPER, Blocks.IRON_BLOCK, Blocks.IRON_DOOR,
            Blocks.IRON_TRAPDOOR, Blocks.COPPER_BLOCK, Blocks.CUT_COPPER, Blocks.GOLD_BLOCK, Blocks.COAL_BLOCK, Blocks.QUARTZ_BLOCK,
            Blocks.REDSTONE_LAMP, Blocks.TINTED_GLASS, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);

    /** Verbotene HBM-Bloecke (Stahl-Deko schreddert zu Stahlstaub, Rest Metall/Erz), fuer den Whitelist-Test. */
    public static final List<String> FORBIDDEN_HBM = List.of("steel_grate", "steel_grate_wide", "steel_beam", "steel_scaffold",
            "steel_scaffold_red", "steel_scaffold_white", "steel_scaffold_yellow", "steel_pole", "steel_roof", "steel_wall",
            "steel_corner", "fence_metal", "reinforced_glass", "reinforced_glass_pane", "concrete_rebar", "concrete_rebar_alt",
            "deco_steel", "deco_rusty_steel", "deco_aluminum", "deco_lead", "deco_titanium", "deco_tungsten", "dungeon_chain",
            "cage_lamp", "fluorescent_lamp", "flood_lamp", "crate_steel", "crate_iron", "boxcar");

    private final Set<Block> allowed = new LinkedHashSet<>();

    // ── Strasse ──────────────────────────────────────────────────────────────
    public final BlockState asphalt, asphaltPatch, lineYellow, lineWhite, manhole;
    public final BlockState sidewalk, sidewalkTile, sidewalkSlab, curb, curbSlab;
    // ── Waende/Fassaden ──────────────────────────────────────────────────────
    public final BlockState concrete, concreteSmooth, concreteCracked, concreteMossy, concreteWhite, concreteGray, concreteSilver,
            brickConcrete, brickConcreteCracked, brickConcreteBroken, brickConcreteMossy, brickLight,
            bricks, stoneBricks, stoneBricksCracked, stoneBricksMossy, smoothStone, andesite, polishedAndesite,
            terracottaWhite, terracottaLightGray, terracottaBrown, terracottaOrange, terracottaRed, terracottaCyan, terracotta,
            vConcreteWhite, vConcreteLightGray, vConcreteGray, vConcreteBlack, vConcreteRed, vConcreteBlue, vConcreteYellow,
            vConcreteGreen, vConcreteCyan, mudBricks;
    // ── Boeden ───────────────────────────────────────────────────────────────
    public final BlockState tileLab, tileLabCracked, tileLabBroken, vinylTile, concreteTile, oakPlanks, sprucePlanks, darkOakPlanks;
    // ── Glas ─────────────────────────────────────────────────────────────────
    public final BlockState glass, glassPane, glassBlack, glassRed, glassYellow, glassLime;
    // ── Holz/Einrichtung ─────────────────────────────────────────────────────
    public final BlockState spruceSlabTop, spruceSlab, oakFence, darkOakFence, bookshelf, craftingTable, barrel, chest, flowerPot,
            carpetGray, carpetBrown, carpetRed, carpetGreen, sprucePressurePlate;
    // ── Masten ───────────────────────────────────────────────────────────────
    public final BlockState postBlack, postStone, armSlabTop, armSlab, stoneBrickSlab, polishedBlackstone, andesiteWall;
    // ── Schutt ───────────────────────────────────────────────────────────────
    public final BlockState cobble, cobbleMossy, cobbleSlab, gravel, tuff, coarseDirt;
    // ── Natur ────────────────────────────────────────────────────────────────
    public final BlockState grassBlock, dirt, rootedDirt, moss, mossCarpet, shortGrass, fern, largeFernLower, largeFernUpper,
            lilyPad, water, mud, silt, stone, poppy, dandelion;
    // ── Sporos-Flora (R2b) ───────────────────────────────────────────────────
    public final BlockState hypheLog, deadHypheLog, hypheLeaves, fungalStem, fungalCap, glowCap, mossCushion, giantFernLower,
            giantFernUpper, sporeBloom, sporePod;
    private final Block mycelVineBlock, shelfFungusBlock;

    // ── Landmarken (Checkpoint, Lager, Lazarett, Nest, Konvoi; s. CityLandmarks) ──
    public final BlockState supplyCrate, militaryCrate, campfire, campfireLit, spawner, sandbag, concreteSlab, spruceFence,
            terracottaGreen, carpetWhite, ladder;
    /** Zeltplanen je {@code CellPlan.Tent.kind}: 0 gruen, 1 braun, 2 weiss, 3 hellgrau. */
    public final BlockState[] tentCloth;
    // ── Wildnis-Strukturen (Funkmast, Berghuette, Krankenhaus, Kraftwerk, Siedlung; s. dim.sporos.structure) ──
    public final BlockState snow, farmland, wheat, carrots, potatoes, hayBale, torch, wallTorch, hyphePlanks, hypheSlab, hypheFence,
            cobbleWall, deepslateTiles, blackstone;
    private final Block hypheStairsBlock;
    // ── Siedlung (sichere Zone, ueberarbeitet): Fachwerk, Tueren, Laeden, Betten, Dachziegel, Lampen, Solarpaneele ──
    public final BlockState strippedSpruceLog, strippedDarkOakLog, birchPlanks, glowstone, furnace, smoker, composter, loom,
            fletchingTable, daylightDetector, dirtPath, pumpkin, melon, beetroots, poppyPot, deepslateTileSlab, darkOakSlab, spruceFenceGate;
    private final Block darkOakStairsBlock, deepslateTileStairsBlock, spruceDoorBlock, darkOakDoorBlock, spruceTrapdoorBlock;
    private final Block[] bedBlocks;
    // ── farbige HBM-Betonplatten (Markisen) und Treppen ──────────────────────
    public final BlockState[] awnings;
    public final BlockState[] carColors;
    private final Block concreteStairsBlock;

    private CityPalette() {
        asphalt = hbm("asphalt", Blocks.BLACK_CONCRETE);
        asphaltPatch = add(Blocks.POLISHED_BLACKSTONE);
        lineYellow = add(Blocks.YELLOW_CONCRETE);
        lineWhite = add(Blocks.WHITE_CONCRETE);
        manhole = add(Blocks.POLISHED_DEEPSLATE);

        sidewalk = add(Blocks.SMOOTH_STONE);
        sidewalkTile = hbm("concrete_tile", Blocks.STONE_BRICKS);
        sidewalkSlab = slab(add(Blocks.SMOOTH_STONE_SLAB), SlabType.BOTTOM);
        curb = add(Blocks.POLISHED_ANDESITE);
        curbSlab = slab(add(Blocks.POLISHED_ANDESITE_SLAB), SlabType.BOTTOM);

        concrete = hbm("concrete", Blocks.LIGHT_GRAY_CONCRETE);
        concreteSmooth = hbm("concrete_smooth", Blocks.LIGHT_GRAY_CONCRETE);
        concreteCracked = hbm("concrete_cracked", Blocks.CRACKED_STONE_BRICKS);
        concreteMossy = hbm("concrete_mossy", Blocks.MOSSY_STONE_BRICKS);
        concreteWhite = hbm("concrete_white", Blocks.WHITE_CONCRETE);
        concreteGray = hbm("concrete_gray", Blocks.GRAY_CONCRETE);
        concreteSilver = hbm("concrete_silver", Blocks.LIGHT_GRAY_CONCRETE);
        brickConcrete = hbm("brick_concrete", Blocks.STONE_BRICKS);
        brickConcreteCracked = hbm("brick_concrete_cracked", Blocks.CRACKED_STONE_BRICKS);
        brickConcreteBroken = hbm("brick_concrete_broken", Blocks.CRACKED_STONE_BRICKS);
        brickConcreteMossy = hbm("brick_concrete_mossy", Blocks.MOSSY_STONE_BRICKS);
        brickLight = hbm("brick_light", Blocks.BRICKS);
        bricks = add(Blocks.BRICKS);
        stoneBricks = add(Blocks.STONE_BRICKS);
        stoneBricksCracked = add(Blocks.CRACKED_STONE_BRICKS);
        stoneBricksMossy = add(Blocks.MOSSY_STONE_BRICKS);
        smoothStone = sidewalk;
        andesite = add(Blocks.ANDESITE);
        polishedAndesite = curb;
        terracottaWhite = add(Blocks.WHITE_TERRACOTTA);
        terracottaLightGray = add(Blocks.LIGHT_GRAY_TERRACOTTA);
        terracottaBrown = add(Blocks.BROWN_TERRACOTTA);
        terracottaOrange = add(Blocks.ORANGE_TERRACOTTA);
        terracottaRed = add(Blocks.RED_TERRACOTTA);
        terracottaCyan = add(Blocks.CYAN_TERRACOTTA);
        terracotta = add(Blocks.TERRACOTTA);
        vConcreteWhite = lineWhite;
        vConcreteLightGray = add(Blocks.LIGHT_GRAY_CONCRETE);
        vConcreteGray = add(Blocks.GRAY_CONCRETE);
        vConcreteBlack = add(Blocks.BLACK_CONCRETE);
        vConcreteRed = add(Blocks.RED_CONCRETE);
        vConcreteBlue = add(Blocks.BLUE_CONCRETE);
        vConcreteYellow = lineYellow;
        vConcreteGreen = add(Blocks.GREEN_CONCRETE);
        vConcreteCyan = add(Blocks.CYAN_CONCRETE);
        mudBricks = add(Blocks.MUD_BRICKS);

        tileLab = hbm("tile_lab", Blocks.SMOOTH_STONE);
        tileLabCracked = hbm("tile_lab_cracked", Blocks.SMOOTH_STONE);
        tileLabBroken = hbm("tile_lab_broken", Blocks.COBBLESTONE);
        vinylTile = hbm("vinyl_tile", Blocks.SMOOTH_STONE);
        concreteTile = sidewalkTile;
        oakPlanks = add(Blocks.OAK_PLANKS);
        sprucePlanks = add(Blocks.SPRUCE_PLANKS);
        darkOakPlanks = add(Blocks.DARK_OAK_PLANKS);

        glass = add(Blocks.GLASS);
        glassPane = add(Blocks.GLASS_PANE);
        glassBlack = add(Blocks.BLACK_STAINED_GLASS);
        glassRed = add(Blocks.RED_STAINED_GLASS);
        glassYellow = add(Blocks.YELLOW_STAINED_GLASS);
        glassLime = add(Blocks.LIME_STAINED_GLASS);

        spruceSlab = slab(add(Blocks.SPRUCE_SLAB), SlabType.BOTTOM);
        spruceSlabTop = slab(spruceSlab, SlabType.TOP);
        oakFence = add(Blocks.OAK_FENCE);
        darkOakFence = add(Blocks.DARK_OAK_FENCE);
        bookshelf = add(Blocks.BOOKSHELF);
        craftingTable = add(Blocks.CRAFTING_TABLE);
        barrel = add(Blocks.BARREL);
        chest = add(Blocks.CHEST);
        flowerPot = add(Blocks.FLOWER_POT);
        carpetGray = add(Blocks.GRAY_CARPET);
        carpetBrown = add(Blocks.BROWN_CARPET);
        carpetRed = add(Blocks.RED_CARPET);
        carpetGreen = add(Blocks.GREEN_CARPET);
        sprucePressurePlate = add(Blocks.SPRUCE_PRESSURE_PLATE);

        postBlack = add(Blocks.POLISHED_BLACKSTONE_WALL);
        postStone = add(Blocks.STONE_BRICK_WALL);
        armSlab = slab(add(Blocks.POLISHED_BLACKSTONE_SLAB), SlabType.BOTTOM);
        armSlabTop = slab(armSlab, SlabType.TOP);
        stoneBrickSlab = slab(add(Blocks.STONE_BRICK_SLAB), SlabType.BOTTOM);
        polishedBlackstone = asphaltPatch;
        andesiteWall = add(Blocks.ANDESITE_WALL);

        cobble = add(Blocks.COBBLESTONE);
        cobbleMossy = add(Blocks.MOSSY_COBBLESTONE);
        cobbleSlab = slab(add(Blocks.COBBLESTONE_SLAB), SlabType.BOTTOM);
        gravel = add(Blocks.GRAVEL);
        tuff = add(Blocks.TUFF);
        coarseDirt = add(Blocks.COARSE_DIRT);

        grassBlock = add(Blocks.GRASS_BLOCK);
        dirt = add(Blocks.DIRT);
        rootedDirt = add(Blocks.ROOTED_DIRT);
        moss = add(Blocks.MOSS_BLOCK);
        mossCarpet = add(Blocks.MOSS_CARPET);
        shortGrass = add(BlockHooks.shortGrass());
        fern = add(Blocks.FERN);
        largeFernLower = with(add(Blocks.LARGE_FERN), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        largeFernUpper = with(largeFernLower, DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
        lilyPad = add(Blocks.LILY_PAD);
        water = add(Blocks.WATER);
        mud = add(Blocks.MUD);
        silt = add(SporosBlocks.SPOROS_SILT.get());
        stone = add(SporosBlocks.SPOROS_STONE.get());
        poppy = add(Blocks.POPPY);
        dandelion = add(Blocks.DANDELION);

        // Sporos-Flora: Laub dauerhaft (zerfaellt nicht), Ranke/Pilzregal bekommen ihren Zustand beim Setzen
        hypheLog = add(SporosFlora.HYPHE_LOG.get());
        deadHypheLog = add(SporosFlora.DEAD_HYPHE_LOG.get());
        hypheLeaves = leaves(SporosFlora.HYPHE_LEAVES.get());
        fungalStem = add(SporosFlora.FUNGAL_STEM.get());
        fungalCap = add(SporosFlora.FUNGAL_CAP.get());
        glowCap = add(SporosFlora.GLOW_CAP.get());
        mossCushion = add(SporosFlora.MOSS_CUSHION.get());
        giantFernLower = with(add(SporosFlora.GIANT_FERN.get()), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        giantFernUpper = with(giantFernLower, DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
        sporeBloom = add(SporosFlora.SPORE_BLOOM.get());
        sporePod = add(SporosFlora.SPORE_POD.get());
        mycelVineBlock = SporosFlora.MYCEL_VINE.get();
        allowed.add(mycelVineBlock);
        shelfFungusBlock = SporosFlora.SHELF_FUNGUS.get();
        allowed.add(shelfFungusBlock);

        awnings = new BlockState[] {
                slab(hbm("concrete_red_slab", Blocks.RED_TERRACOTTA), SlabType.TOP),
                slab(hbm("concrete_yellow_slab", Blocks.YELLOW_TERRACOTTA), SlabType.TOP),
                slab(hbm("concrete_blue_slab", Blocks.BLUE_TERRACOTTA), SlabType.TOP),
                slab(hbm("concrete_green_slab", Blocks.GREEN_TERRACOTTA), SlabType.TOP),
                slab(hbm("concrete_white_slab", Blocks.WHITE_TERRACOTTA), SlabType.TOP)};
        // Landmarken: eigene Beute-Kisten (kein Metall), Lagerfeuer (eines brennt je Lager), Spawner im Nest (Block-Entity
        // setzt CityLoot), Sandsaecke = gepackter Schlamm, Zeltplanen aus Wolle
        supplyCrate = add(SporosBlocks.SUPPLY_CRATE.get());
        militaryCrate = add(SporosBlocks.MILITARY_CRATE.get());
        campfire = with(add(Blocks.CAMPFIRE), BlockStateProperties.LIT, false);
        campfireLit = with(campfire, BlockStateProperties.LIT, true);
        spawner = add(Blocks.SPAWNER);
        sandbag = add(Blocks.PACKED_MUD);
        concreteSlab = slab(hbm("concrete_slab", Blocks.SMOOTH_STONE_SLAB), SlabType.BOTTOM);
        spruceFence = add(Blocks.SPRUCE_FENCE);
        terracottaGreen = add(Blocks.GREEN_TERRACOTTA);
        carpetWhite = add(Blocks.WHITE_CARPET);
        ladder = add(Blocks.LADDER);
        tentCloth = new BlockState[] {add(Blocks.GREEN_WOOL), add(Blocks.BROWN_WOOL), add(Blocks.WHITE_WOOL), add(Blocks.LIGHT_GRAY_WOOL)};

        // Wildnis-Strukturen: Schnee (Huette), Felder (Siedlung), Fackeln, Hyphe-Holz, Keller-/Industriebloecke
        snow = add(Blocks.SNOW);
        farmland = with(add(Blocks.FARMLAND), BlockStateProperties.MOISTURE, 7);
        wheat = with(add(Blocks.WHEAT), BlockStateProperties.AGE_7, 7);
        carrots = with(add(Blocks.CARROTS), BlockStateProperties.AGE_7, 7);
        potatoes = with(add(Blocks.POTATOES), BlockStateProperties.AGE_7, 7);
        hayBale = add(Blocks.HAY_BLOCK);
        torch = add(Blocks.TORCH);
        wallTorch = add(Blocks.WALL_TORCH);
        hyphePlanks = add(SporosFlora.HYPHE_PLANKS.get());
        hypheSlab = slab(add(SporosFlora.HYPHE_SLAB.get()), SlabType.BOTTOM);
        hypheFence = add(SporosFlora.HYPHE_FENCE.get());
        hypheStairsBlock = SporosFlora.HYPHE_STAIRS.get();
        allowed.add(hypheStairsBlock);
        cobbleWall = add(Blocks.COBBLESTONE_WALL);
        strippedSpruceLog = add(Blocks.STRIPPED_SPRUCE_LOG);
        strippedDarkOakLog = add(Blocks.STRIPPED_DARK_OAK_LOG);
        birchPlanks = add(Blocks.BIRCH_PLANKS);
        glowstone = add(Blocks.GLOWSTONE);
        furnace = add(Blocks.FURNACE);
        smoker = add(Blocks.SMOKER);
        composter = add(Blocks.COMPOSTER);
        loom = add(Blocks.LOOM);
        fletchingTable = add(Blocks.FLETCHING_TABLE);
        daylightDetector = add(Blocks.DAYLIGHT_DETECTOR);
        dirtPath = add(Blocks.DIRT_PATH);
        spruceFenceGate = add(Blocks.SPRUCE_FENCE_GATE);
        pumpkin = add(Blocks.PUMPKIN);
        melon = add(Blocks.MELON);
        beetroots = with(add(Blocks.BEETROOTS), BlockStateProperties.AGE_3, 3);
        poppyPot = add(Blocks.POTTED_POPPY);
        deepslateTileSlab = slab(add(Blocks.DEEPSLATE_TILE_SLAB), SlabType.BOTTOM);
        darkOakSlab = slab(add(Blocks.DARK_OAK_SLAB), SlabType.BOTTOM);
        darkOakStairsBlock = add(Blocks.DARK_OAK_STAIRS).getBlock();
        deepslateTileStairsBlock = add(Blocks.DEEPSLATE_TILE_STAIRS).getBlock();
        spruceDoorBlock = add(Blocks.SPRUCE_DOOR).getBlock();
        darkOakDoorBlock = add(Blocks.DARK_OAK_DOOR).getBlock();
        spruceTrapdoorBlock = add(Blocks.SPRUCE_TRAPDOOR).getBlock();
        bedBlocks = new Block[] {add(Blocks.RED_BED).getBlock(), add(Blocks.WHITE_BED).getBlock(), add(Blocks.BLUE_BED).getBlock(),
                add(Blocks.GREEN_BED).getBlock(), add(Blocks.BROWN_BED).getBlock(), add(Blocks.CYAN_BED).getBlock()};
        add(Blocks.STONE_BRICK_STAIRS);
        add(Blocks.MOSSY_STONE_BRICKS);
        add(Blocks.BRICK_STAIRS);
        deepslateTiles = add(Blocks.DEEPSLATE_TILES);
        blackstone = add(Blocks.BLACKSTONE);

        // Index 10: olivgruener Armee-Lkw (Checkpoint/Konvoi), wird von den Zufallsfarben (0-9) nicht gewaehlt
        carColors = new BlockState[] {vConcreteRed, vConcreteBlue, vConcreteWhite, vConcreteGray, vConcreteBlack, vConcreteGreen,
                vConcreteCyan, vConcreteYellow, terracottaBrown, terracottaOrange, terracottaGreen};

        // Treppen (Stuehle, Stufen im Treppenhaus, Baenke) - Zustaende entstehen erst beim Setzen
        add(Blocks.SPRUCE_STAIRS);
        add(Blocks.STONE_BRICK_STAIRS);
        add(Blocks.STONE_BRICK_SLAB);
        add(Blocks.SMOOTH_STONE_SLAB);
        Block cs = hbm("concrete_stairs", Blocks.STONE_BRICK_STAIRS).getBlock();
        concreteStairsBlock = cs instanceof StairBlock ? cs : Blocks.STONE_BRICK_STAIRS;
        hbm("concrete_slab", Blocks.SMOOTH_STONE_SLAB);
    }

    // ── Hilfen ──────────────────────────────────────────────────────────────

    private BlockState add(Block b) {
        allowed.add(b);
        return b.defaultBlockState();
    }

    private BlockState add(BlockState s) {
        allowed.add(s.getBlock());
        return s;
    }

    /** HBM-Block per Registry-Name (die Addon-Klassen kennen {@code ModBlocks} nicht direkt), sonst Vanilla-Ersatz. */
    private BlockState hbm(String path, Block fallback) {
        Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("hbm_m", path));
        if (b == null || b == Blocks.AIR) b = fallback;
        return add(b);
    }

    private BlockState leaves(Block b) {
        return with(add(b), LeavesBlock.PERSISTENT, true);
    }

    public static BlockState slab(BlockState s, SlabType type) {
        return with(s, SlabBlock.TYPE, type);
    }

    public static <T extends Comparable<T>> BlockState with(BlockState s, Property<T> prop, T value) {
        return s.hasProperty(prop) ? s.setValue(prop, value) : s;
    }

    /** Stufe, die nach {@code up} ansteigt. */
    public static BlockState stairs(Block block, Direction up, boolean upsideDown) {
        // Bloecke stehen bereits im Konstruktor in der Whitelist (Set nach dem Bau unveraenderlich, threadsicher)
        BlockState s = with(block.defaultBlockState(), StairBlock.FACING, up);
        return with(s, StairBlock.HALF, upsideDown ? Half.TOP : Half.BOTTOM);
    }

    public BlockState concreteStairs(Direction up) {
        return stairs(concreteStairsBlock, up, false);
    }

    /** Dachstufe der Siedlung: 0 Tiefenschiefer-Ziegel, 1 dunkle Eiche, 2 Fichte. */
    public BlockState roofStairs(int style, Direction up, boolean upsideDown) {
        Block b = switch (style) {
            case 1 -> darkOakStairsBlock;
            case 2 -> Blocks.SPRUCE_STAIRS;
            default -> deepslateTileStairsBlock;
        };
        return stairs(b, up, upsideDown);
    }

    public BlockState roofSlab(int style) {
        return switch (style) {
            case 1 -> darkOakSlab;
            case 2 -> spruceSlab;
            default -> deepslateTileSlab;
        };
    }

    /** Tuerhaelfte (geschlossen), {@code facing} wie beim Setzen durch einen Spieler, der in diese Richtung schaut. */
    public BlockState door(boolean dark, Direction facing, boolean upper, boolean rightHinge) {
        BlockState s = (dark ? darkOakDoorBlock : spruceDoorBlock).defaultBlockState();
        s = with(s, DoorBlock.FACING, facing);
        s = with(s, DoorBlock.HALF, upper ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
        return with(s, DoorBlock.HINGE, rightHinge ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);
    }

    /** Offener Fensterladen (Falltuer), der flach an der Wand liegt, die auf der Seite gegenueber {@code facing} steht. */
    public BlockState shutter(Direction facing) {
        BlockState s = with(spruceTrapdoorBlock.defaultBlockState(), TrapDoorBlock.FACING, facing);
        return with(s, TrapDoorBlock.OPEN, true);
    }

    /** Bett (Farbe 0-5): Fussteil oder Kopfteil, {@code facing} zeigt vom Fuss- zum Kopfteil. */
    public BlockState bed(int color, Direction facing, boolean head) {
        BlockState s = with(bedBlocks[Math.floorMod(color, bedBlocks.length)].defaultBlockState(), BedBlock.FACING, facing);
        return with(s, BedBlock.PART, head ? BedPart.HEAD : BedPart.FOOT);
    }

    /** Hyphe-Holzstufe (Siedlungsdaecher), die nach {@code up} ansteigt. */
    public BlockState hypheStairs(Direction up, boolean upsideDown) {
        return stairs(hypheStairsBlock, up, upsideDown);
    }

    /** Geflecht-Ranke ({@code mycel_vine}), die an der Wand in Richtung {@code wall} haengt (UP = unter einem Block). */
    public BlockState vine(Direction wall) {
        BlockState s = mycelVineBlock.defaultBlockState();
        return switch (wall) {
            case NORTH -> s.setValue(VineBlock.NORTH, true);
            case SOUTH -> s.setValue(VineBlock.SOUTH, true);
            case EAST -> s.setValue(VineBlock.EAST, true);
            case WEST -> s.setValue(VineBlock.WEST, true);
            default -> s.setValue(VineBlock.UP, true);
        };
    }

    /** Pilzregal an der Wand, die in Richtung {@code wall} liegt ({@code FACING} zeigt von der Wand weg). */
    public BlockState shelfFungus(Direction wall) {
        return with(shelfFungusBlock.defaultBlockState(), BlockStateProperties.HORIZONTAL_FACING, wall.getOpposite());
    }

    public static BlockState log(BlockState log, Direction.Axis axis) {
        return with(log, RotatedPillarBlock.AXIS, axis);
    }

    public static BlockState facing(BlockState s, Direction dir) {
        return with(s, BlockStateProperties.HORIZONTAL_FACING, dir);
    }

    public static BlockState facingAll(BlockState s, Direction dir) {
        return with(s, BlockStateProperties.FACING, dir);
    }

    /** Alle Bloecke, die der Generator setzen darf (inkl. Gelaende-Fuellung). */
    public Set<Block> allowedBlocks() {
        return Collections.unmodifiableSet(allowed);
    }
}
