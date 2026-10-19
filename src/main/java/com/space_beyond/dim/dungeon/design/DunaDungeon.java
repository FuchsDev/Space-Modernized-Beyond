package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;

/**
 * Duna - "Rust Foundry": verrosteter oberirdischer Industriekomplex auf dem roten Staubplaneten.
 * <pre>
 *  Oberflaeche  Haupthalle (Sheddach, Kranbahnen, Walzstrasse, Presse) mit dem Siegeltor an der Nordwand,
 *               Kesselhaus mit Schornstein A (74 hoch, begehbar), Schornsteine B/C, Giesshalle (Satteldach,
 *               Schlackebecken), Silos, Foerderbruecken, Portalkran im Schrottplatz, Foerdergeruest ueber dem Schacht.
 *  Schluessel 1 Schornstein A innen: Leiter, Wandkonsolen-Spirale, Haengepodest + Querspruenge, zerbrochene
 *               Treppe, Leiter - Truhe auf dem Aussichtsbalkon (y 67).
 *  Schluessel 2 Giesshalle: Laufsteg-Parkour ueber Giesspfannen, Kranhaken und einen Traeger ueber dem
 *               Lavabecken zum Leitstand (y 14) - Truhe; Notausstieg ins Abschreckbecken.
 *  Schluessel 3 Keller: Lorenbahn-Stollen (y -13) mit Spawnern, verwinkelt, Sackgassen - Siegelwaechter
 *               (scrap_gunner) im Erzbunker.
 *  Boss         Siegeltor -> Lastenaufzugsschacht (Wendelrampe) -> Montagehalle y -33 mit Robotergerippe.
 * </pre>
 */
public final class DunaDungeon extends CustomDungeon {

    private static final String SCUTTLER = "space_beyond:rust_scuttler", GUNNER = "space_beyond:scrap_gunner";

    // Haupthalle
    private static final int HU0 = -32, HU1 = 32, HV0 = -18, HV1 = 40;
    // Kesselhaus, Schornstein A
    private static final int BU0 = -86, BU1 = -42, BV0 = -6, BV1 = 30;
    private static final int CAU = -64, CAV = 12;
    // Giesshalle
    private static final int GU0 = 40, GU1 = 86, GV0 = -40, GV1 = 26;
    // Schacht und Arena
    private static final int SV = -29, AV = -62, AF = -34, AC = -10;
    // Keller (Tunnelboden)
    private static final int TF = -14;

    @Override public Stage stage() { return Stage.DUNA; }
    @Override public int radius() { return 90; }
    @Override public BlockState surface() { return b(com.space_m.dim.duna.DunaBlocks.DUNA_SANDS.get()); }
    @Override public BlockState filler() { return b(com.space_m.dim.duna.DunaBlocks.DUNA_ROCK.get()); }
    @Override public int minY() { return -40; }
    @Override public int maxY() { return 76; }
    @Override public int minBase() { return 62; }
    @Override public int maxBase() { return 170; }
    @Override public int[] entry() { return new int[] {0, 1, 86}; }
    @Override public String boss() { return "space_beyond:rust_titan"; }

    private static BlockState b(Block block) {
        return block.defaultBlockState();
    }

    // ── Palette (lazy, Registry erst zur Laufzeit) ──

    private static final class Pal {
        final BlockState rust = b(ModBlocks.DECO_RUSTY_STEEL.get()), steel = b(ModBlocks.DECO_STEEL.get());
        final BlockState conc = b(ModBlocks.CONCRETE.get()), concCr = b(ModBlocks.CONCRETE_CRACKED.get());
        final BlockState cBrick = b(ModBlocks.BRICK_CONCRETE.get()), cBrickCr = b(ModBlocks.BRICK_CONCRETE_CRACKED.get()), cBrickBr = b(ModBlocks.BRICK_CONCRETE_BROKEN.get());
        final BlockState hazard = b(ModBlocks.CONCRETE_HAZARD.get()), tile = b(ModBlocks.CONCRETE_TILE.get()), cPillar = b(ModBlocks.CONCRETE_PILLAR.get());
        final BlockState grate = b(ModBlocks.STEEL_GRATE.get()), scaffold = b(ModBlocks.STEEL_SCAFFOLD.get());
        final BlockState slag = b(ModBlocks.BLOCK_SLAG.get()), scrap = b(ModBlocks.BLOCK_SCRAP.get()), asphalt = b(ModBlocks.ASPHALT.get());
        final BlockState fireBrick = b(ModBlocks.BRICK_FIRE.get()), redCopper = b(ModBlocks.DECO_RED_COPPER.get());
        final BlockState pipeY = Kit.axis(ModBlocks.DECO_PIPE_RUSTED.get(), Direction.Axis.Y), pipeX = Kit.axis(ModBlocks.DECO_PIPE_RUSTED.get(), Direction.Axis.X),
                pipeZ = Kit.axis(ModBlocks.DECO_PIPE_RUSTED.get(), Direction.Axis.Z);
        final BlockState pipeQ = Kit.axis(ModBlocks.DECO_PIPE_QUAD_RUSTED.get(), Direction.Axis.Y), pipeRim = Kit.axis(ModBlocks.DECO_PIPE_RIM_RUSTED.get(), Direction.Axis.Y);
        final BlockState cage = b(ModBlocks.CAGE_LAMP.get());
        final BlockState sands = b(com.space_m.dim.duna.DunaBlocks.DUNA_SANDS.get()), cobble = b(com.space_m.dim.duna.DunaBlocks.DUNA_COBBLE.get()),
                rock = b(com.space_m.dim.duna.DunaBlocks.DUNA_ROCK.get()), clay = b(com.space_m.dim.duna.DunaBlocks.FERRIC_CLAY.get()),
                oreIron = b(com.space_m.dim.duna.DunaBlocks.ORE_IRON.get()), oreCopper = b(com.space_m.dim.duna.DunaBlocks.ORE_COPPER.get());
        final BlockState bricks = b(Blocks.BRICKS), redTerra = b(Blocks.RED_TERRACOTTA), whiteTerra = b(Blocks.WHITE_TERRACOTTA), orangeTerra = b(Blocks.ORANGE_TERRACOTTA),
                brownTerra = b(Blocks.BROWN_TERRACOTTA), terra = b(Blocks.TERRACOTTA);
        final BlockState copperW = b(Blocks.WAXED_WEATHERED_COPPER), copperO = b(Blocks.WAXED_OXIDIZED_COPPER), cutCopperO = b(Blocks.WAXED_OXIDIZED_CUT_COPPER),
                cutCopperW = b(Blocks.WAXED_WEATHERED_CUT_COPPER), cutCopperE = b(Blocks.WAXED_EXPOSED_CUT_COPPER);
        final BlockState cutRed = b(Blocks.CUT_RED_SANDSTONE), smoothRed = b(Blocks.SMOOTH_RED_SANDSTONE), chiselRed = b(Blocks.CHISELED_RED_SANDSTONE);
        final BlockState glassO = b(Blocks.ORANGE_STAINED_GLASS), glassR = b(Blocks.RED_STAINED_GLASS), bars = b(Blocks.IRON_BARS), chain = b(Blocks.CHAIN);
        final BlockState lantern = Kit.lantern(Blocks.LANTERN, false), lanternH = Kit.lantern(Blocks.LANTERN, true), froglight = b(Blocks.OCHRE_FROGLIGHT);
        final BlockState lava = b(Blocks.LAVA), magma = b(Blocks.MAGMA_BLOCK), water = b(Blocks.WATER);
        final BlockState blackstone = b(Blocks.BLACKSTONE), pbBricks = b(Blocks.POLISHED_BLACKSTONE_BRICKS), basalt = b(Blocks.SMOOTH_BASALT);
        final BlockState ironBlock = b(Blocks.IRON_BLOCK), rawIron = b(Blocks.RAW_IRON_BLOCK), rawCopper = b(Blocks.RAW_COPPER_BLOCK), coal = b(Blocks.COAL_BLOCK);
        final BlockState grayConc = b(Blocks.GRAY_CONCRETE), blackConc = b(Blocks.BLACK_CONCRETE), yellowConc = b(Blocks.YELLOW_CONCRETE), orangeConc = b(Blocks.ORANGE_CONCRETE);
        final BlockState redSand = b(Blocks.RED_SAND), hopper = b(Blocks.HOPPER), anvil = b(Blocks.ANVIL), redstoneLamp = b(Blocks.REDSTONE_LAMP);
        final BlockState stoneSlab = Kit.slab(Blocks.SMOOTH_STONE_SLAB, false), redNether = b(Blocks.RED_NETHER_BRICKS), mudBricks = b(Blocks.MUD_BRICKS);
        final BlockState railNS = CityPalette.with(b(Blocks.RAIL), BlockStateProperties.RAIL_SHAPE, RailShape.NORTH_SOUTH),
                railEW = CityPalette.with(b(Blocks.RAIL), BlockStateProperties.RAIL_SHAPE, RailShape.EAST_WEST);
        final BlockState trapdoor = b(Blocks.IRON_TRAPDOOR), lightningRod = b(Blocks.LIGHTNING_ROD), scaffolding = b(Blocks.SCAFFOLDING);
    }

    private volatile Pal pal;

    private Pal pal() {
        Pal p = pal;
        if (p == null) pal = p = new Pal();
        return p;
    }

    // ── Bau ──

    @Override
    protected void build(Kit k) {
        Pal p = pal();
        yardGround(k, p);
        mainHall(k, p);
        boilerHouse(k, p);
        chimneyA(k, p);
        chimneyB(k, p);
        chimneyC(k, p);
        castingHall(k, p);
        basement(k, p);
        shaftAndArena(k, p);
        yardProps(k, p);
    }

    // ── Hilfen ──

    private static int lo(int a, int b) { return Math.max(a, b); }

    private static int hi(int a, int b) { return Math.min(a, b); }

    /** Rost-/Wellblech-Mischung fuer Waende. */
    private BlockState rustMix(Kit k, Pal p, long salt, int s, int y) {
        double n = k.noise(salt, s, y, 5);
        double r = k.rnd(salt + 1, s, y, 0);
        if (n > 0.72) return r < 0.5 ? p.orangeTerra : p.brownTerra;
        if (n < 0.2) return p.copperW;
        return (s & 1) == 0 ? p.rust : (r < 0.15 ? p.scrap : p.redTerra);
    }

    /** Dachhoehe je Gebaeude (0 Haupthalle Sheddach, 1 Giesshalle Satteldach, 2 Kesselhaus Satteldach). */
    private static int roofTop(int style, int u, int v) {
        switch (style) {
            case 0: return 25 + Math.floorMod(v - HV0, 8) * 6 / 7;
            case 1: return 23 + (23 - Math.abs(u - 63)) * 7 / 23;
            default: return 17 + (18 - Math.abs(v - 12)) * 5 / 18;
        }
    }

    /** Gebaeudehuelle: Boden, Waende mit Pfeilern/Fenstern, Dach. */
    private void building(Kit k, Pal p, int u0, int v0, int u1, int v1, int style) {
        if (!k.touches(u0 - 2, v0 - 2, u1 + 2, v1 + 2)) return;
        int winLo = style == 0 ? 10 : style == 1 ? 13 : 7, winHi = style == 0 ? 16 : style == 1 ? 19 : 12;
        int eave = style == 0 ? 24 : style == 1 ? 22 : 16;
        for (int u = lo(u0 - 1, k.u0()); u <= hi(u1 + 1, k.u1()); u++) for (int v = lo(v0 - 1, k.v0()); v <= hi(v1 + 1, k.v1()); v++) {
            boolean in = u >= u0 && u <= u1 && v >= v0 && v <= v1;
            boolean edgeU = u == u0 || u == u1, edgeV = v == v0 || v == v1;
            if (in) {
                // Fundament und Boden
                k.set(u, -1, v, p.conc);
                double fn = k.noise(31 + style, u, v, 4);
                BlockState floor = style == 1 ? (fn > 0.6 ? p.blackstone : fn > 0.3 ? p.pbBricks : p.basalt)
                        : style == 2 ? (fn > 0.55 ? p.cBrick : fn > 0.3 ? p.cBrickCr : p.conc)
                        : (fn > 0.66 ? p.concCr : (Math.floorMod(u, 4) == 0 || Math.floorMod(v, 4) == 0) ? p.conc : p.tile);
                k.set(u, 0, v, floor);
            }
            int top = roofTop(style, u, v);
            if (style == 0 && Math.floorMod(v - HV0, 8) == 0 && v > v0) top = 31;
            if (in && (edgeU || edgeV)) {
                int s = edgeU ? v : u;
                int rel = edgeU ? v - v0 : u - u0;
                boolean pil = rel % 8 == 0 || (edgeU ? v == v1 : u == u1);
                for (int y = 1; y <= top; y++) {
                    BlockState w;
                    if (pil) w = style == 2 ? (y % 6 == 0 ? p.cutRed : p.redNether) : style == 1 ? p.cPillar : p.rust;
                    else if (y <= 3) w = style == 2 ? p.mudBricks : k.rnd(41, u, y, v) < 0.3 ? p.cBrickCr : p.cBrick;
                    else if (y == 4) w = style == 1 ? p.fireBrick : p.cutRed;
                    else if (y >= winLo && y <= winHi && rel % 8 >= 2 && rel % 8 <= 6) w = rel % 8 == 4 ? p.rust : style == 1 ? p.glassR : p.glassO;
                    else if (y == winLo - 1 || y == winHi + 1 || y == eave) w = style == 2 ? p.cutRed : p.smoothRed;
                    else if (style == 2) w = k.noise(55, s, y, 4) > 0.7 ? p.mudBricks : p.bricks;
                    else if (style == 1) w = k.noise(56, s, y, 4) > 0.65 ? p.blackstone : rustMix(k, p, 57, s, y);
                    else w = rustMix(k, p, 58, s, y);
                    // Verfall: Loecher im oberen Blech
                    if (!pil && y > winHi + 1 && y < eave && k.noise(59 + style, s, y, 3) > 0.86) w = Kit.AIR;
                    k.set(u, y, v, w);
                }
            } else if (in) {
                // Dach
                BlockState r;
                double rn = k.noise(61 + style, u, v, 5);
                if (style == 0) r = rn > 0.62 ? p.cutCopperO : rn > 0.4 ? p.cutCopperW : (u & 3) == 0 ? p.rust : p.copperO;
                else if (style == 1) r = rn > 0.6 ? p.blackstone : (v & 1) == 0 ? p.rust : p.brownTerra;
                else r = rn > 0.5 ? p.cutCopperE : p.copperW;
                if (k.noise(65 + style, u, v, 3) > 0.84) r = Kit.AIR;   // eingebrochene Dachfelder
                k.set(u, top, v, r);
                if (style == 0 && Math.floorMod(v - HV0, 8) == 0) for (int y = top + 1; y <= 31; y++) k.set(u, y, v, (u & 3) == 0 ? p.rust : p.glassO);
                if (style == 0 && Math.floorMod(v - HV0, 8) == 7) k.set(u, top + 1, v, Kit.slab(Blocks.CUT_RED_SANDSTONE_SLAB, false));
            } else {
                // Pfeilervorlagen aussen
                boolean outU = (u == u0 - 1 || u == u1 + 1) && v >= v0 && v <= v1, outV = (v == v0 - 1 || v == v1 + 1) && u >= u0 && u <= u1;
                if (!outU && !outV) continue;
                int rel = outU ? v - v0 : u - u0;
                if (rel % 8 != 0) continue;
                int t = Math.min(top, eave);
                for (int y = 1; y <= t; y++) k.set(u, y, v, y <= 3 ? p.cBrick : style == 2 ? p.redNether : p.rust);
                k.set(u, t + 1, v, style == 2 ? p.cutRed : p.smoothRed);
            }
        }
    }

    /** Kranbahn-Pfette / Traeger von A nach B (achsparallel). */
    private void beamLine(Kit k, int ua, int ya, int va, int ub, int yb, int vb, BlockState s) {
        k.fill(ua, ya, va, ub, yb, vb, s);
    }

    /** Kette von y0 bis y1 (inkl.). */
    private void chain(Kit k, Pal p, int u, int y0, int y1, int v) {
        for (int y = y0; y <= y1; y++) k.set(u, y, v, p.chain);
    }

    /** Haengelaterne an Kette. */
    private void hangLamp(Kit k, Pal p, int u, int yTop, int len, int v) {
        chain(k, p, u, yTop - len + 1, yTop, v);
        k.set(u, yTop - len, v, p.lanternH);
    }

    // ── Hof ──

    private void yardGround(Kit k, Pal p) {
        // Strasse vom Eingang zur Haupthalle, Gehwege, Werkgleis
        for (int u = lo(-9, k.u0()); u <= hi(9, k.u1()); u++) for (int v = lo(HV1 + 1, k.v0()); v <= hi(90, k.v1()); v++) {
            int au = Math.abs(u);
            BlockState s = au <= 4 ? (k.noise(71, u, v, 3) > 0.75 ? p.concCr : p.asphalt) : au <= 6 ? (v % 3 == 0 ? p.cBrickCr : p.cBrick) : (k.noise(72, u, v, 4) > 0.5 ? p.sands : p.redSand);
            if (au == 0 && v % 4 < 2) s = p.yellowConc;
            k.set(u, 0, v, s);
            if (au > 6) k.set(u, -1, v, p.rock);
        }
        // Platz vor der Giesshalle / Kesselhaus (Beton mit Rissen)
        for (int u = lo(-90, k.u0()); u <= hi(90, k.u1()); u++) for (int v = lo(-90, k.v0()); v <= hi(90, k.v1()); v++) {
            double n = k.noise(73, u, v, 9);
            if (Math.abs(u) <= 9 && v > HV1) continue;
            if (n > 0.62) k.set(u, 0, v, k.rnd(74, u, 0, v) < 0.3 ? p.concCr : p.conc);
            else if (n < 0.25) k.set(u, 0, v, p.redSand);
        }
    }

    private void yardProps(Kit k, Pal p) {
        // Eingangstor: zwei Pfeiler mit Portalbalken und Schild
        for (int s = -1; s <= 1; s += 2) {
            int u = 8 * s;
            k.fill(u - 1, 1, 76, u + 1, 2, 78, p.cBrick);
            k.fill(u, 3, 77, u, 12, 77, p.rust);
            k.set(u, 13, 77, p.smoothRed);
            k.set(u + s, 9, 77, p.cage);
        }
        k.fill(-8, 12, 77, 8, 12, 77, p.rust);
        k.fill(-5, 13, 77, 5, 14, 77, p.redTerra);
        k.fill(-4, 13, 77, 4, 13, 77, p.orangeTerra);
        for (int u = -6; u <= 6; u += 4) chain(k, p, u, 10, 11, 77);
        // Zaunreste (Gitter) entlang v 77
        for (int u = -40; u <= 40; u++) {
            if (Math.abs(u) <= 9) continue;
            if (k.noise(81, u, 77, 4) < 0.35) continue;
            k.set(u, 1, 77, Math.abs(u) % 6 == 0 ? p.rust : p.bars);
            if (Math.abs(u) % 6 == 0) k.set(u, 2, 77, p.rust);
            else if (k.rnd(82, u, 2, 77) < 0.6) k.set(u, 2, 77, p.bars);
        }
        // Laternenmasten entlang der Strasse
        for (int v = 48; v <= 84; v += 12) for (int s = -1; s <= 1; s += 2) {
            int u = 7 * s;
            k.fill(u, 1, v, u, 6, v, p.bars);
            k.set(u, 7, v, p.rust);
            k.set(u - s, 7, v, p.rust);
            k.set(u - s, 6, v, p.lanternH);
        }
        // Silos im Suedosten mit Kegeldach und Laufring
        int[][] silos = {{54, 52}, {66, 52}, {78, 52}};
        for (int[] s : silos) silo(k, p, s[0], s[1]);
        // Foerderbruecke Silo -> Giesshalle
        if (k.touches(64, 27, 68, 52)) {
            k.fill(65, 19, 27, 67, 21, 46, p.rust);
            k.fill(65, 22, 27, 67, 22, 46, p.cutCopperO);
            for (int v = 29; v <= 45; v += 3) k.set(64, 20, v, p.glassO);
            for (int v = 29; v <= 45; v += 3) k.set(68, 20, v, p.glassO);
            for (int v : new int[] {33, 40}) {
                k.fill(64, 1, v, 64, 18, v, p.rust);
                k.fill(68, 1, v, 68, 18, v, p.rust);
                k.fill(64, 18, v, 68, 18, v, p.rust);
                for (int y = 4; y <= 16; y += 4) k.fill(65, y, v, 67, y, v, p.bars);
            }
        }
        // Kohlebunker-Turm westlich mit Foerderbruecke zum Kesselhaus
        if (k.touches(-86, 32, -72, 56)) {
            k.fill(-84, 1, 46, -76, 1, 54, p.cBrick);
            for (int[] c : new int[][] {{-84, 46}, {-76, 46}, {-84, 54}, {-76, 54}}) k.fill(c[0], 2, c[1], c[0], 10, c[1], p.rust);
            k.fill(-84, 11, 46, -76, 22, 54, p.rust);
            k.fill(-83, 12, 47, -77, 21, 53, p.coal);
            for (int y = 12; y <= 21; y += 3) k.fill(-84, y, 46, -76, y, 54, p.redTerra);
            k.fill(-82, 8, 48, -78, 10, 52, p.rust);
            k.fill(-81, 6, 49, -79, 7, 51, p.hopper);
            k.fill(-81, 23, 49, -79, 24, 51, p.rust);
            k.fill(-81, 17, 31, -79, 19, 45, p.rust);
            k.fill(-81, 20, 31, -79, 20, 45, p.cutCopperW);
            for (int v = 33; v <= 44; v += 3) k.set(-82, 18, v, p.glassO);
            k.fill(-80, 1, 38, -80, 16, 38, p.rust);
        }
        // Schrottplatz im Norden mit Portalkran
        craneYard(k, p);
        // Sandverwehungen an Hauswaenden und Ecken
        double[][] dunes = {{-37, 45, 5, 3, 4}, {37, 45, 5, 3, 4}, {-26, -25, 5, 3, 3}, {26, -25, 5, 2.5, 3}, {-82, 38, 5, 3, 4},
                {-37, -12, 4, 3, 3}, {88, 24, 4, 2, 6}, {40, 30, 8, 3, 5}, {-60, 34, 9, 3, 4}, {20, 64, 8, 2.5, 8}, {-30, 70, 10, 3, 9},
                {50, -70, 9, 3, 7}, {-48, -60, 10, 4, 9}, {82, 70, 8, 3, 8}, {-80, 76, 9, 3, 8}, {86, -84, 6, 3, 6}};
        for (double[] d : dunes) dune(k, p, (int) d[0], (int) d[1], d[2], d[3], d[4]);
        // Trummerbrocken und Rohrstuecke
        for (int i = 0; i < 26; i++) {
            int u = -86 + (int) (k.rnd(91, i, 0, 0) * 172), v = -86 + (int) (k.rnd(92, i, 0, 0) * 172);
            if (inBuilding(u, v, 3)) continue;
            if (!k.touches(u - 3, v - 3, u + 3, v + 3)) continue;
            int t = (int) (k.rnd(93, i, 0, 0) * 4);
            if (t == 0) {
                k.fill(u - 2, 1, v, u + 2, 1, v, p.pipeX);
                k.set(u - 3, 1, v, p.pipeRim);
            } else if (t == 1) {
                k.set(u, 1, v, p.scrap);
                k.set(u + 1, 1, v, p.rust);
                k.set(u, 2, v, Kit.slab(Blocks.CUT_RED_SANDSTONE_SLAB, false));
            } else if (t == 2) {
                k.set(u, 1, v, p.rust);
                k.set(u, 1, v + 1, p.trapdoor);
            } else {
                k.fill(u, 1, v - 1, u, 1, v + 1, p.pipeZ);
            }
        }
    }

    /** Liegt (u, v) in/an einem Gebaeude (fuer Streudeko)? */
    private static boolean inBuilding(int u, int v, int m) {
        if (u >= HU0 - m && u <= HU1 + m && v >= HV0 - m && v <= HV1 + m) return true;
        if (u >= BU0 - m && u <= BU1 + m && v >= BV0 - m && v <= BV1 + m) return true;
        if (u >= GU0 - m && u <= HU1 + 60 && v >= GV0 - m && v <= GV1 + m) return true;
        if (Math.abs(u) <= 12 + m && v >= -40 && v <= -16) return true;
        if (u >= 46 && u <= 86 && v >= 44 && v <= 60) return true;
        if (Math.abs(u) <= 10 && v > 40) return true;
        return false;
    }

    private void dune(Kit k, Pal p, int cu, int cv, double ru, double ry, double rv) {
        int ir = (int) Math.ceil(Math.max(ru, rv)) + 1;
        if (!k.touches(cu - ir, cv - ir, cu + ir, cv + ir)) return;
        for (int u = lo(cu - ir, k.u0()); u <= hi(cu + ir, k.u1()); u++) for (int v = lo(cv - ir, k.v0()); v <= hi(cv + ir, k.v1()); v++) {
            double du = (u - cu) / ru, dv = (v - cv) / rv;
            double h = ry * (1 - du * du - dv * dv) + (k.noise(95, u, v, 3) - 0.5) * 1.5;
            int top = (int) Math.floor(h);
            for (int y = 1; y <= top; y++) {
                if (!k.isAir(u, y, v)) break;
                k.set(u, y, v, y == top && k.rnd(96, u, y, v) < 0.5 ? p.redSand : p.sands);
            }
        }
    }

    private void silo(Kit k, Pal p, int cu, int cv) {
        if (!k.touches(cu - 7, cv - 7, cu + 7, cv + 7)) return;
        k.cylinder(cu, cv, 1, 3, 6, p.cBrick);
        for (int y = 4; y <= 26; y++) {
            BlockState w = y % 6 == 0 ? p.rust : (y / 6) % 2 == 0 ? p.cutCopperW : p.copperO;
            k.tube(cu, cv, y, y, 5, 1, w, null);
        }
        k.tube(cu, cv, 4, 26, 4, 1, p.rust, Kit.AIR);
        for (int i = 0; i < 6; i++) k.cylinder(cu, cv, 27 + i, 27 + i, 5.0 - i * 0.9, i % 2 == 0 ? p.rust : p.cutCopperO);
        k.set(cu, 33, cv, p.lightningRod);
        // Laufring auf halber Hoehe
        k.tube(cu, cv, 16, 16, 6.5, 1.5, p.grate, null);
        k.tube(cu, cv, 17, 17, 6.5, 0.6, p.bars, null);
        // Auslauf unten
        k.fill(cu - 1, 4, cv - 6, cu + 1, 5, cv - 5, p.rust);
        k.set(cu, 3, cv - 6, p.hopper);
    }

    private void craneYard(Kit k, Pal p) {
        // Kranschienen
        for (int s = -1; s <= 1; s += 2) {
            int u = 26 * s;
            if (k.touches(u - 2, -88, u + 2, -42)) for (int v = lo(-88, k.v0()); v <= hi(-42, k.v1()); v++) {
                k.set(u, 0, v, p.cBrick);
                k.set(u, 1, v, v % 2 == 0 ? p.rust : p.steel);
            }
        }
        // Portalkran: A-Stuetzen, Bruecke y 24..26, Laufkatze, Haken mit Schrottcontainer
        int cv = -64;
        if (k.touches(-32, cv - 8, 32, cv + 8)) {
            for (int s = -1; s <= 1; s += 2) {
                int u = 26 * s;
                k.line(u, 2, cv - 6, u, 24, cv - 1, 0.7, p.rust);
                k.line(u, 2, cv + 6, u, 24, cv + 1, 0.7, p.rust);
                k.fill(u - 1, 2, cv - 7, u + 1, 2, cv - 5, p.steel);
                k.fill(u - 1, 2, cv + 5, u + 1, 2, cv + 7, p.steel);
                k.fill(u, 12, cv - 4, u, 12, cv + 4, p.rust);
                k.fill(u - 1, 22, cv - 2, u + 1, 26, cv + 2, p.rust);
                k.set(u, 23, cv + 3, p.cage);
            }
            k.fill(-30, 24, cv - 1, 30, 26, cv - 1, p.rust);
            k.fill(-30, 24, cv + 1, 30, 26, cv + 1, p.rust);
            k.fill(-30, 26, cv, 30, 26, cv, p.grate);
            for (int u = -28; u <= 28; u += 4) k.set(u, 25, cv, p.bars);
            k.fill(-30, 27, cv - 1, 30, 27, cv - 1, p.bars);
            k.fill(-30, 27, cv + 1, 30, 27, cv + 1, p.bars);
            // Laufkatze + Fuehrerhaus
            k.fill(6, 27, cv - 2, 10, 29, cv + 2, p.rust);
            k.fill(7, 28, cv - 2, 9, 28, cv - 2, p.glassO);
            k.fill(-14, 20, cv + 2, -10, 23, cv + 4, p.redCopper);
            k.fill(-13, 21, cv + 4, -11, 22, cv + 4, p.glassO);
            // Seile + Containergreifer
            chain(k, p, 7, 11, 23, cv);
            chain(k, p, 9, 11, 23, cv);
            k.fill(5, 10, cv - 2, 11, 10, cv + 2, p.rust);
            k.fill(5, 5, cv - 2, 11, 9, cv + 2, p.scrap);
            k.fill(6, 5, cv - 1, 10, 9, cv + 1, p.rust);
        }
        // Schrott- und Schlackehalden
        double[][] heaps = {{-16, -78, 7, 6, 6}, {14, -80, 6, 5, 5}, {-18, -50, 5, 4, 4}, {18, -48, 5, 3, 4}, {-38, -84, 6, 5, 5}, {38, -80, 7, 6, 5}};
        for (int i = 0; i < heaps.length; i++) {
            double[] h = heaps[i];
            int cu = (int) h[0], cvv = (int) h[1];
            if (!k.touches(cu - 8, cvv - 8, cu + 8, cvv + 8)) continue;
            int ir = (int) h[2] + 1;
            for (int u = lo(cu - ir, k.u0()); u <= hi(cu + ir, k.u1()); u++) for (int v = lo(cvv - ir, k.v0()); v <= hi(cvv + ir, k.v1()); v++) {
                double du = (u - cu) / h[2], dv = (v - cvv) / h[4];
                double hh = h[3] * (1 - du * du - dv * dv) + (k.rnd(97, u, 0, v) - 0.5) * 1.6;
                for (int y = 1; y <= hh; y++) {
                    double r = k.rnd(98 + i, u, y, v);
                    k.set(u, y, v, i % 2 == 0 ? (r < 0.45 ? p.scrap : r < 0.7 ? p.rust : r < 0.85 ? p.pipeX : p.copperW) : (r < 0.6 ? p.slag : r < 0.85 ? p.blackstone : p.magma));
                }
            }
        }
        // Erzwagen auf einem Abstellgleis
        if (k.touches(-40, -46, 40, -42)) {
            for (int u = lo(-24, k.u0()); u <= hi(24, k.u1()); u++) if (k.col(u, -44)) {
                k.set(u, 0, -44, p.cobble);
                k.set(u, 1, -44, p.railEW);
            }
            for (int c = -16; c <= 16; c += 8) {
                k.fill(c - 1, 1, -44, c + 1, 1, -44, p.hopper);
                k.fill(c - 1, 2, -44, c + 1, 2, -44, k.rnd(99, c, 0, 0) < 0.5 ? p.rawIron : p.rawCopper);
            }
        }
    }

    // ── Haupthalle ──

    private void mainHall(Kit k, Pal p) {
        building(k, p, HU0, HV0, HU1, HV1, 0);
        if (k.touches(HU0, HV0, HU1, HV1)) {
            // Tore: Sued (Haupttor), West (Kesselhaus), Ost (Giesshalle)
            k.clear(-6, 1, HV1, 6, 12, HV1);
            k.fill(-7, 13, HV1, 7, 13, HV1, p.hazard);
            for (int y = 1; y <= 12; y++) {
                k.set(-7, y, HV1, y % 2 == 0 ? p.yellowConc : p.blackConc);
                k.set(7, y, HV1, y % 2 == 0 ? p.yellowConc : p.blackConc);
            }
            for (int u = -6; u <= 6; u++) k.set(u, 12, HV1, Kit.facing(Blocks.IRON_TRAPDOOR, Direction.SOUTH));
            k.clear(HU0, 1, 9, HU0, 5, 13);
            k.clear(HU1, 1, 1, HU1, 6, 7);
            // Stuetzenreihen u = +-16, Kranbahnen y 19
            for (int s = -1; s <= 1; s += 2) {
                int u = 16 * s;
                for (int v = -10; v <= 30; v += 8) {
                    k.fill(u - 1, 1, v - 1, u + 1, 1, v + 1, p.cBrick);
                    k.fill(u, 2, v, u, 18, v, p.rust);
                    k.set(u, 2, v, p.cPillar);
                    k.set(u - 1, 18, v, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, s < 0 ? Direction.EAST : Direction.WEST, true));
                    k.set(u + 1, 18, v, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, s < 0 ? Direction.WEST : Direction.EAST, true));
                    k.set(u, 10, v + (s < 0 ? 1 : -1), p.cage);
                }
                beamLine(k, u, 19, HV0 + 1, u, 19, HV1 - 1, p.rust);
                beamLine(k, 31 * s, 19, HV0 + 1, 31 * s, 19, HV1 - 1, p.rust);
                for (int v = HV0 + 2; v <= HV1 - 2; v += 8) {
                    k.set(31 * s, 18, v, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, s < 0 ? Direction.WEST : Direction.EAST, true));
                    hangLamp(k, p, 23 * s, 23, 4, v + 4);
                }
            }
            // Kranbruecken
            beamLine(k, -31, 20, 2, -16, 20, 2, p.rust);
            beamLine(k, -31, 20, 4, -16, 20, 4, p.rust);
            k.fill(-26, 21, 2, -22, 22, 4, p.redCopper);
            chain(k, p, -24, 9, 19, 3);
            k.fill(-25, 8, 3, -23, 8, 3, p.rust);
            k.set(-24, 7, 3, p.steel);
            beamLine(k, -16, 20, 17, 16, 20, 17, p.rust);
            beamLine(k, -16, 20, 19, 16, 20, 19, p.rust);
            k.fill(2, 21, 17, 6, 22, 19, p.redCopper);
            chain(k, p, 2, 12, 19, 18);
            chain(k, p, 6, 12, 19, 18);
            k.fill(0, 9, 17, 8, 11, 19, p.steel);
            k.fill(1, 11, 18, 7, 11, 18, p.ironBlock);
            beamLine(k, 16, 20, -8, 31, 20, -8, p.rust);
            beamLine(k, 16, 20, -6, 31, 20, -6, p.rust);
            k.fill(21, 21, -8, 25, 22, -6, p.redCopper);
            chain(k, p, 23, 8, 19, -7);
            k.fill(22, 6, -8, 24, 7, -6, p.rust);
            k.set(23, 5, -7, p.hopper);
            // Walzstrasse in der Mitte
            for (int v = 2; v <= 32; v++) {
                if (!k.touches(-4, v, 4, v)) continue;
                boolean stand = v % 6 == 2 || v % 6 == 3;
                if (stand) {
                    k.fill(-3, 1, v, -3, 6, v, p.steel);
                    k.fill(3, 1, v, 3, 6, v, p.steel);
                    k.fill(-3, 7, v, 3, 7, v, p.rust);
                    k.fill(-2, 3, v, 2, 3, v, p.pipeX);
                    k.fill(-2, 5, v, 2, 5, v, p.pipeX);
                    k.set(-3, 8, v, p.redstoneLamp);
                } else {
                    k.set(-3, 1, v, p.rust);
                    k.set(3, 1, v, p.rust);
                    k.fill(-2, 1, v, 2, 1, v, v % 2 == 0 ? p.pipeX : p.steel);
                }
            }
            k.fill(-2, 2, 9, 2, 2, 15, p.ironBlock);
            k.fill(-2, 2, 21, 2, 2, 26, p.redCopper);
            // Presse im Ostschiff
            press(k, p, 24, 28);
            // Spulen- und Blechstapel
            for (int i = 0; i < 6; i++) {
                int u = 20 + (i % 3) * 3, v = 4 + (i / 3) * 4;
                k.fill(u, 1, v, u + 1, 1 + (i % 2), v + 1, i % 2 == 0 ? p.cutCopperE : p.steel);
            }
            for (int i = 0; i < 5; i++) k.fill(-12 + i, 1, -12, -12 + i, 1 + i % 3, -10, p.rust);
            // Schienen vom Haupttor
            for (int v = lo(-6, k.v0()); v <= hi(HV1 - 1, k.v1()); v++) {
                if (k.col(-8, v)) k.set(-8, 1, v, p.railNS);
                if (k.col(8, v)) k.set(8, 1, v, p.railNS);
            }
            // Gefahrenstreifen vor dem Siegeltor
            k.fill(-7, 0, HV0 + 1, 7, 0, HV0 + 5, p.hazard);
            for (int u = -6; u <= 6; u++) k.set(u, 0, HV0 + 6, (u & 1) == 0 ? p.yellowConc : p.blackConc);
            // Schaltschraenke an der Westwand
            for (int v = -14; v <= 38; v += 13) {
                k.fill(HU0 + 1, 1, v, HU0 + 1, 3, v + 1, p.steel);
                k.set(HU0 + 1, 2, v, p.redstoneLamp);
            }
            // Bodenlaternen in Reihe
            for (int v = -10; v <= 30; v += 8) for (int s = -1; s <= 1; s += 2) k.set(9 * s, 1, v + 4, p.lantern);
            // Truemmer unter Dachloechern
            for (int u = lo(HU0 + 1, k.u0()); u <= hi(HU1 - 1, k.u1()); u++) for (int v = lo(HV0 + 1, k.v0()); v <= hi(HV1 - 1, k.v1()); v++) {
                if (k.noise(65, u, v, 3) > 0.88 && k.isAir(u, 1, v) && k.rnd(66, u, 1, v) < 0.5 && Math.abs(u) > 5) k.set(u, 1, v, k.rnd(67, u, 1, v) < 0.5 ? p.cutCopperO : p.scrap);
            }
        }
        // Gang zum Kesselhaus und zur Giesshalle
        passage(k, p, BU1 + 1, 8, HU0 - 1, 14, true);
        passage(k, p, HU1 + 1, 0, GU0 - 1, 8, false);
        // Rohrbruecke zwischen Haupthalle und Giesshalle
        if (k.touches(HU1, 10, GU0, 16)) {
            k.fill(HU1 + 1, 12, 12, GU0 - 1, 12, 12, p.pipeX);
            k.fill(HU1 + 1, 13, 13, GU0 - 1, 13, 13, p.pipeX);
            k.fill(HU1 + 1, 12, 14, GU0 - 1, 12, 14, p.pipeX);
            k.fill(36, 1, 13, 36, 11, 13, p.rust);
            k.fill(36, 11, 11, 36, 11, 15, p.rust);
        }
        // Sand in den Hallenecken
        dune(k, p, HU0 + 2, HV1 - 2, 4, 2.5, 4);
        dune(k, p, HU1 - 2, HV0 + 2, 4, 2.5, 3);
        // Bewohner
        k.mob(-20, 1, 0, GUNNER);
        k.mob(20, 1, 12, GUNNER);
        k.mob(-10, 1, 20, SCUTTLER);
        k.mob(12, 1, -4, SCUTTLER);
        k.mob(-6, 1, 34, SCUTTLER);
        k.barrel(29, 1, 37);
        k.loot(-30, 1, -16, Direction.EAST);
    }

    private void passage(Kit k, Pal p, int u0, int v0, int u1, int v1, boolean alongU) {
        if (!k.touches(u0, v0, u1, v1)) return;
        k.room(u0, 0, v0, u1, 8, v1, p.cBrick, p.conc, p.rust);
        k.clear(u0, 1, v0 + 1, u1, 6, v1 - 1);
        for (int u = u0; u <= u1; u += 3) {
            k.set(u, 7, v0, p.rust);
            k.set(u, 7, v1, p.rust);
            k.set(u, 4, v0, p.glassO);
            k.set(u, 4, v1, p.glassO);
        }
        k.fill(u0, 9, v0, u1, 9, v1, Kit.slab(Blocks.CUT_RED_SANDSTONE_SLAB, false));
        int mid = (v0 + v1) / 2;
        for (int u = u0 + 2; u <= u1 - 1; u += 4) hangLamp(k, p, u, 7, 1, mid);
    }

    private void press(Kit k, Pal p, int cu, int cv) {
        if (!k.touches(cu - 5, cv - 5, cu + 5, cv + 5)) return;
        k.fill(cu - 4, 1, cv - 4, cu + 4, 2, cv + 4, p.rust);
        k.fill(cu - 2, 3, cv - 2, cu + 2, 3, cv + 2, p.steel);
        for (int a = -1; a <= 1; a += 2) for (int c = -1; c <= 1; c += 2) k.fill(cu + 4 * a, 3, cv + 4 * c, cu + 4 * a, 13, cv + 4 * c, p.pipeQ);
        k.fill(cu - 4, 14, cv - 4, cu + 4, 16, cv + 4, p.rust);
        k.fill(cu - 3, 17, cv - 3, cu + 3, 17, cv + 3, p.redCopper);
        k.fill(cu - 2, 8, cv - 2, cu + 2, 11, cv + 2, p.steel);
        k.fill(cu - 1, 12, cv - 1, cu + 1, 13, cv + 1, p.pipeY);
        k.set(cu - 4, 15, cv - 5, p.cage);
        k.set(cu + 4, 15, cv - 5, p.cage);
    }

    // ── Kesselhaus + Schornstein A (Schluessel 1) ──

    private void boilerHouse(Kit k, Pal p) {
        building(k, p, BU0, BV0, BU1, BV1, 2);
        if (k.touches(BU0, BV0, BU1, BV1)) {
            k.clear(BU1, 1, 9, BU1, 5, 13);
            k.clear(-62, 1, BV1, -58, 6, BV1);
            k.fill(-63, 7, BV1, -57, 7, BV1, p.hazard);
            // Zwei Kesseltrommeln (liegend, Achse v)
            for (int[] d : new int[][] {{-3, 7}, {16, 27}}) {
                for (int v = lo(d[0], k.v0()); v <= hi(d[1], k.v1()); v++) for (int u = lo(-53, k.u0()); u <= hi(-45, k.u1()); u++) {
                    for (int y = 1; y <= 9; y++) {
                        double r = Math.sqrt((u + 49) * (u + 49) + (y - 5) * (y - 5));
                        if (r <= 3.6) k.set(u, y, v, (v - d[0]) % 4 == 0 || v == d[1] ? p.steel : r > 2.8 ? p.rust : p.cutCopperE);
                    }
                }
                for (int v = d[0] + 1; v <= d[1]; v += 5) k.fill(-52, 1, v, -46, 1, v, p.cBrick);
                k.line(-49, 9, (d[0] + d[1]) / 2.0, -55, 12, (d[0] + d[1]) / 2.0 > 12 ? 17 : 7, 0.8, p.pipeX);
                k.set(-49, 10, d[0] + 2, p.pipeRim);
                k.set(-49, 10, d[1] - 2, p.pipeRim);
            }
            // Kohle-/Aschebunker im Westteil
            for (int u = lo(BU0 + 1, k.u0()); u <= hi(-74, k.u1()); u++) for (int v = lo(BV0 + 1, k.v0()); v <= hi(BV1 - 1, k.v1()); v++) {
                double h = 3 * k.noise(101, u, v, 5) - 0.6;
                if (u <= -82 && v >= 24) continue;
                for (int y = 1; y <= h; y++) k.set(u, y, v, k.rnd(102, u, y, v) < 0.6 ? p.coal : p.slag);
            }
            for (int v = 0; v <= 24; v += 8) hangLamp(k, p, -50, 15, 3, v + 4);
            for (int v = 2; v <= 26; v += 12) hangLamp(k, p, -80, 15, 3, v);
            // Rohrleitungen unter dem Dach
            k.fill(BU0 + 1, 14, 0, BU1 - 1, 14, 0, p.pipeX);
            k.fill(BU0 + 1, 14, 26, BU1 - 1, 14, 26, p.pipeX);
        }
        k.mob(-50, 1, 12, SCUTTLER);
        k.mob(-56, 1, 27, SCUTTLER);
        k.loot(-84, 1, 27, Direction.EAST);
    }

    /** Relativ zu Schornstein A. */
    private void ca(Kit k, int a, int y, int b, BlockState s) {
        k.set(CAU + a, y, CAV + b, s);
    }

    private void chimneyA(Kit k, Pal p) {
        int cu = CAU, cv = CAV;
        // Aussenhuelle mit Baendern
        if (k.touches(cu - 12, cv - 12, cu + 12, cv + 12)) {
            k.cylinder(cu, cv, -1, 0, 8, p.conc);
            for (int y = 1; y <= 74; y++) {
                BlockState w;
                if (y % 12 == 0) w = p.rust;
                else if (y >= 50) w = ((y - 50) / 4) % 2 == 0 ? p.redTerra : p.whiteTerra;
                else if (y <= 4) w = p.cBrick;
                else w = k.rnd(111, 0, y, 0) < 0.15 ? p.mudBricks : p.bricks;
                k.tube(cu, cv, y, y, 7.5, 2, w, Kit.AIR);
            }
            k.tube(cu, cv, 1, 74, 6.5, 1, p.fireBrick, null);
            k.tube(cu, cv, 75, 76, 8.5, 1.5, p.rust, null);
            k.cylinder(cu, cv, 0, 0, 6, p.slag);
            // Tuer ostseitig
            k.clear(cu + 6, 1, cv - 1, cu + 8, 3, cv + 1);
            k.set(cu + 8, 4, cv - 1, p.rust);
            k.set(cu + 8, 4, cv, p.rust);
            k.set(cu + 8, 4, cv + 1, p.rust);
            k.set(cu + 9, 3, cv - 2, p.cage);
            // S1: Wartungsleiter Nordwand bis Podest F1 (y 12)
            k.cylinder(cu, cv, 12, 12, 6, p.grate);
            k.ladder(cu, 1, 12, cv - 6, Direction.NORTH);
            for (int y = 3; y <= 11; y += 4) { ca(k, -1, y, -6, p.lantern); }
            // S2: Wandkonsolen-Spirale (12 Stufen) bis Leiter durch F2 (y 28)
            int[][] br = {{3, -5}, {5, -3}, {5, 0}, {5, 3}, {3, 5}, {0, 5}, {-3, 5}, {-5, 3}, {-5, 0}, {-5, -3}, {-3, -5}, {0, -5}};
            for (int i = 0; i < br.length; i++) {
                int a = br[i][0], b = br[i][1], y = 13 + i;
                ca(k, a, y, b, i % 3 == 2 ? p.redCopper : p.rust);
                int oa = Integer.signum(a) * (Math.abs(a) >= Math.abs(b) ? 1 : 0), ob = Integer.signum(b) * (Math.abs(b) >= Math.abs(a) ? 1 : 0);
                if (i < br.length - 1 && (a + oa) * (a + oa) + (b + ob) * (b + ob) <= 36) ca(k, a + oa, y, b + ob, p.rust);
                ca(k, a, y - 1, b, p.bars);
            }
            k.cylinder(cu, cv, 28, 28, 6, p.grate);
            k.ladder(cu, 25, 28, cv - 6, Direction.NORTH);
            ca(k, 3, 20, -5, p.lantern);
            ca(k, -3, 26, 5, p.lantern);
            // S3: Leiter West, Konsole, Haengepodest, Konsole, Leiter Ost bis F3 (y 44)
            k.ladder(cu - 6, 29, 34, cv, Direction.WEST);
            ca(k, -5, 34, 0, p.rust);
            ca(k, -4, 34, 0, p.rust);
            ca(k, -4, 33, 0, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, Direction.WEST, true));
            k.fill(cu - 1, 35, cv - 1, cu + 1, 35, cv + 1, p.grate);
            k.fill(cu - 1, 34, cv - 1, cu + 1, 34, cv + 1, p.bars);
            k.set(cu, 34, cv, p.rust);
            chain(k, p, cu, 36, 43, cv);
            ca(k, 4, 36, 0, p.rust);
            ca(k, 5, 36, 0, p.rust);
            ca(k, 4, 35, 0, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, Direction.EAST, true));
            k.ladder(cu + 6, 37, 44, cv, Direction.EAST);
            k.cylinder(cu, cv, 44, 44, 6, p.grate);
            ca(k, 6, 44, 0, Kit.facing(Blocks.LADDER, Direction.WEST));
            ca(k, 0, 40, -6, p.lantern);
            ca(k, 0, 40, 6, p.lantern);
            // S4: zerbrochene Treppe am Quadratring (Abstand 4) bis F4 (y 58)
            int[] heights = {45, 46, 47, -1, -1, 47, 48, 49, -1, 50, 51, -1, -1, 51, 52, 53, -1, 54, 55, -1, -1, 55, 56, 57};
            int[][] ring = ring4();
            for (int i = 0; i < heights.length; i++) {
                if (heights[i] < 0) continue;
                int a = ring[i][0], b = ring[i][1];
                ca(k, a, heights[i], b, i % 4 == 0 ? p.redCopper : p.rust);
                if (heights[i] > 45) ca(k, a, heights[i] - 1, b, p.bars);
            }
            k.cylinder(cu, cv, 58, 58, 6, p.grate);
            for (int i = 14; i < heights.length; i++) {
                int a = ring[i][0], b = ring[i][1];
                ca(k, a, 58, b, Kit.AIR);
                ca(k, a - Integer.signum(a) * (Math.abs(a) == 4 ? 1 : 0), 58, b - Integer.signum(b) * (Math.abs(b) == 4 ? 1 : 0), Kit.AIR);
            }
            ca(k, 3, 50, 5, p.lantern);
            ca(k, -5, 54, 3, p.lantern);
            // S5: Leiter Nordwand bis Kopfpodest (y 66)
            k.ladder(cu + 2, 59, 66, cv - 5, Direction.NORTH);
            k.cylinder(cu, cv, 66, 66, 6, p.grate);
            ca(k, 2, 66, -5, Kit.facing(Blocks.LADDER, Direction.SOUTH));
            ca(k, 4, 62, 3, p.lantern);
            // Tuer nach Sueden auf den Aussichtsbalkon
            k.clear(cu - 1, 67, cv + 6, cu + 1, 69, cv + 8);
            k.set(cu, 70, cv + 8, p.rust);
            // Balkon (Ring) y 66 mit Gelaender und Streben
            for (int u = lo(cu - 12, k.u0()); u <= hi(cu + 12, k.u1()); u++) for (int v = lo(cv - 12, k.v0()); v <= hi(cv + 12, k.v1()); v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d <= 8.0 || d > 11.5) continue;
                k.set(u, 66, v, p.grate);
                if (d > 10.4) k.set(u, 67, v, p.bars);
            }
            for (int i = 0; i < 8; i++) {
                double ang = Math.PI * 2 * i / 8;
                k.line(cu + Math.cos(ang) * 7.8, 60, cv + Math.sin(ang) * 7.8, cu + Math.cos(ang) * 10.5, 65, cv + Math.sin(ang) * 10.5, 0.5, p.rust);
                k.set((int) Math.round(cu + Math.cos(ang) * 10.6), 68, (int) Math.round(cv + Math.sin(ang) * 10.6), p.lantern);
            }
            // Blitzableiter
            k.set(cu + 7, 77 - 1, cv, p.lightningRod);
        }
        k.mob(cu + 2, 13, cv + 2, SCUTTLER);
        k.mob(cu - 2, 45, cv - 1, SCUTTLER);
        k.spawner(cu - 3, 29, cv + 3, SCUTTLER);
        k.barrel(cu + 4, 29, cv + 3);
        k.keyChest(cu, 67, cv + 10, Direction.NORTH);
    }

    /** Quadratring im Abstand 4 um die Schornsteinachse (32 Felder, im Uhrzeigersinn ab (4, 1)). */
    private static int[][] ring4() {
        int[][] r = new int[32][];
        int n = 0;
        for (int b = 1; b <= 4; b++) r[n++] = new int[] {4, b};
        for (int a = 3; a >= -4; a--) r[n++] = new int[] {a, 4};
        for (int b = 3; b >= -4; b--) r[n++] = new int[] {-4, b};
        for (int a = -3; a <= 4; a++) r[n++] = new int[] {a, -4};
        for (int b = -3; b <= 0; b++) r[n++] = new int[] {4, b};
        return r;
    }

    private void chimneyB(Kit k, Pal p) {
        int cu = -70, cv = -32;
        if (k.touches(cu - 8, cv - 8, cu + 8, BV0)) {
            k.cylinder(cu, cv, 1, 3, 7.5, p.cBrick);
            k.cylinder(cu, cv, 4, 4, 7, p.smoothRed);
            for (int y = 4; y <= 62; y++) {
                BlockState w = y % 10 == 0 ? p.rust : y >= 46 ? (((y - 46) / 3) % 2 == 0 ? p.whiteTerra : p.redTerra) : p.bricks;
                k.tube(cu, cv, y, y, 5.5, 1.5, w, Kit.AIR);
            }
            k.tube(cu, cv, 63, 64, 6.5, 1.5, p.rust, null);
            // Rauchkanal zum Kesselhaus
            k.fill(cu - 1, 6, cv + 6, cu + 1, 9, BV0 - 1, p.rust);
            for (int v = cv + 8; v < BV0; v += 4) {
                k.fill(cu - 2, 6, v, cu + 2, 9, v, p.steel);
                k.fill(cu, 1, v, cu, 5, v, p.rust);
            }
        }
    }

    private void chimneyC(Kit k, Pal p) {
        int cu = 70, cv = -62;
        if (k.touches(cu - 8, cv - 8, cu + 8, GV0)) {
            k.cylinder(cu, cv, 1, 3, 7.5, p.cBrick);
            for (int y = 4; y <= 58; y++) {
                BlockState w = y % 10 == 0 ? p.rust : y >= 42 ? (((y - 42) / 3) % 2 == 0 ? p.redTerra : p.whiteTerra) : (k.rnd(121, 0, y, 0) < 0.2 ? p.redNether : p.bricks);
                k.tube(cu, cv, y, y, 5.5, 1.5, w, Kit.AIR);
            }
            k.tube(cu, cv, 59, 60, 6.5, 1.5, p.rust, null);
            k.fill(cu - 1, 8, cv + 6, cu + 1, 11, GV0 - 1, p.rust);
            for (int v = cv + 8; v < GV0; v += 4) {
                k.fill(cu - 2, 8, v, cu + 2, 11, v, p.steel);
                k.fill(cu, 1, v, cu, 7, v, p.rust);
            }
        }
    }

    // ── Giesshalle (Schluessel 2) ──

    private void ladle(Kit k, Pal p, int cu, int cv, int y, boolean chainsAlongV) {
        if (!k.touches(cu - 4, cv - 4, cu + 4, cv + 4)) return;
        k.fill(cu - 2, y - 1, cv - 2, cu + 2, y, cv + 2, p.rust);
        k.fill(cu - 1, y - 1, cv - 1, cu + 1, y, cv + 1, p.lava);
        k.fill(cu - 2, y - 2, cv - 1, cu + 2, y - 2, cv + 1, p.rust);
        k.fill(cu - 1, y - 2, cv - 2, cu + 1, y - 2, cv + 2, p.rust);
        k.fill(cu - 1, y - 3, cv - 1, cu + 1, y - 3, cv + 1, p.steel);
        for (int s = -1; s <= 1; s += 2) {
            int u = chainsAlongV ? cu : cu + 3 * s, v = chainsAlongV ? cv + 3 * s : cv;
            k.set(u, y - 1, v, p.steel);
            chain(k, p, u, y, roofTop(1, u, v) - 1, v);
        }
        k.set(cu + 2, y - 1, cv + 2, p.redCopper);
    }

    private void hook(Kit k, Pal p, int cu, int cv, int y) {
        if (!k.touches(cu - 2, cv - 2, cu + 2, cv + 2)) return;
        k.fill(cu - 1, y, cv - 1, cu + 1, y, cv + 1, p.rust);
        k.set(cu - 1, y, cv - 1, p.steel);
        k.set(cu + 1, y, cv + 1, p.steel);
        k.set(cu, y - 1, cv, p.steel);
        k.set(cu, y - 2, cv, p.anvil);
        k.set(cu, y - 3, cv, p.trapdoor);
        chain(k, p, cu, y + 1, roofTop(1, cu, cv) - 1, cv);
    }

    private void castingHall(Kit k, Pal p) {
        building(k, p, GU0, GV0, GU1, GV1, 1);
        if (k.touches(GU0, GV0, GU1 + 4, GV1)) {
            k.clear(GU0, 1, 1, GU0, 6, 7);
            // Schlackebecken
            k.fill(51, -4, -37, 85, 0, 23, p.fireBrick);
            for (int u = lo(52, k.u0()); u <= hi(84, k.u1()); u++) for (int v = lo(-36, k.v0()); v <= hi(22, k.v1()); v++) {
                boolean crust = k.noise(131, u, v, 4) > 0.78 && (u > 54 || v > 4);
                k.set(u, -3, v, p.slag);
                k.set(u, -2, v, p.lava);
                k.set(u, -1, v, crust ? p.slag : p.lava);
                k.set(u, 0, v, Kit.AIR);
            }
            for (int u = lo(51, k.u0()); u <= hi(85, k.u1()); u++) for (int v = lo(-37, k.v0()); v <= hi(23, k.v1()); v++) {
                if (u == 51 || u == 85 || v == -37 || v == 23) k.set(u, 0, v, (u + v) % 5 == 0 ? p.magma : p.pbBricks);
            }
            // Dachbinder und Kranbahnen
            for (int v = GV0 + 6; v < GV1; v += 6) {
                for (int u = lo(GU0 + 1, k.u0()); u <= hi(GU1 - 1, k.u1()); u++) if (k.col(u, v)) k.set(u, roofTop(1, u, v) - 1, v, p.rust);
                k.fill(GU0 + 1, 22, v, GU1 - 1, 22, v, p.rust);
            }
            k.fill(GU0 + 1, 20, GV0 + 1, GU0 + 1, 20, GV1 - 1, p.rust);
            k.fill(GU1 - 1, 20, GV0 + 1, GU1 - 1, 20, GV1 - 1, p.rust);
            // Hochofen-Stich im Nordwesten (Deko) mit Rinne
            k.cylinder(46, -32, 1, 16, 4.5, p.fireBrick);
            k.cylinder(46, -32, 17, 21, 3.5, p.rust);
            for (int y = 3; y <= 15; y += 4) k.tube(46, -32, y, y, 4.8, 1, p.rust, null);
            k.fill(46, 1, -27, 46, 2, -27, p.magma);
            k.set(45, 3, -27, p.cage);
            k.set(47, 3, -27, p.cage);
            // Kokillen-Reihe im Eingangsbereich
            for (int v = -20; v <= 20; v += 4) {
                k.fill(42, 1, v, 44, 1, v + 1, p.steel);
                k.set(43, 1, v, p.slag);
            }
            // Start: Treppe auf die Abstichbuehne (y 8)
            for (int s = 1; s <= 8; s++) {
                int y = 9 - s;
                k.fill(45, y, s, 46, y, s, Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, Direction.NORTH, false));
                k.fill(45, 1, s, 46, y - 1, s, p.rust);
                k.set(47, y + 1, s, p.bars);
            }
            k.fill(45, 8, -6, 50, 8, 0, p.grate);
            k.fill(45, 1, -6, 45, 7, -6, p.rust);
            k.fill(50, 1, -6, 50, 7, -6, p.rust);
            k.fill(45, 1, 0, 45, 7, 0, p.rust);
            k.fill(44, 9, -6, 44, 9, 0, p.bars);
            k.set(47, 9, -6, p.bars);
            k.set(48, 9, -6, p.bars);
            k.set(50, 12, -3, p.cage);
            // Parkour ueber dem Becken
            ladle(k, p, 55, -3, 8, true);
            hook(k, p, 61, -3, 9);
            ladle(k, p, 67, -3, 9, true);
            hook(k, p, 73, -3, 10);
            // Traeger (abgestuerzter Kranbalken) u 73, v -7..-12 auf y 11 an Ketten
            k.fill(73, 11, -12, 73, 11, -7, p.rust);
            k.fill(73, 10, -12, 73, 10, -7, Kit.slab(Blocks.SMOOTH_STONE_SLAB, true));
            for (int v : new int[] {-8, -11}) {
                k.set(74, 11, v, p.steel);
                chain(k, p, 74, 12, roofTop(1, 74, v) - 1, v);
            }
            hook(k, p, 73, -16, 12);
            ladle(k, p, 67, -17, 12, true);
            hook(k, p, 65, -23, 13);
            hook(k, p, 70, -23, 13);
            // Leitstand (Boden y 13) mit Balkon
            k.room(76, 13, -34, 86, 19, -25, p.rust, p.grate, p.rust);
            for (int u = 77; u <= 85; u += 2) k.set(u, 16, -25, p.glassO);
            for (int v = -33; v <= -26; v++) k.fill(76, 15, v, 76, 17, v, p.glassO);
            k.clear(80, 14, -25, 81, 16, -25);
            k.fill(74, 13, -24, 85, 13, -18, p.grate);
            k.fill(74, 14, -18, 85, 14, -18, p.bars);
            k.fill(74, 14, -20, 74, 14, -19, p.bars);
            for (int[] c : new int[][] {{76, -34}, {74, -18}, {84, -18}, {84, -34}}) k.fill(c[0], -3, c[1], c[0], 12, c[1], p.rust);
            k.fill(77, 14, -33, 79, 14, -33, p.steel);
            k.fill(77, 15, -33, 79, 15, -33, Kit.slab(Blocks.SMOOTH_STONE_SLAB, false));
            k.set(78, 16, -33, p.redstoneLamp);
            k.fill(82, 14, -33, 85, 14, -33, p.steel);
            k.set(83, 15, -33, p.redstoneLamp);
            k.set(81, 18, -29, p.lanternH);
            k.set(81, 12, -21, p.cage);
            // Notausstieg nach Osten: Sims, Abschreckbecken
            k.clear(GU1, 14, -30, GU1, 16, -29);
            k.fill(87, 13, -31, 87, 13, -28, p.grate);
            k.fill(87, -3, -32, 90, 0, -27, p.pbBricks);
            k.fill(88, -2, -31, 89, 0, -28, p.water);
            // Lichter: Kranlampen
            for (int v = -34; v <= 20; v += 9) {
                k.set(GU0 + 2, 19, v, p.cage);
                k.set(GU1 - 2, 19, v, p.cage);
            }
        }
        k.spawner(62, 1, -38, GUNNER);
        k.spawner(48, 1, 18, SCUTTLER);
        k.mob(58, 1, 24, SCUTTLER);
        k.mob(80, 1, -38, SCUTTLER);
        k.mob(83, 14, -28, GUNNER);
        k.barrel(70, 1, 24);
        k.loot(77, 14, -30, Direction.EAST);
        k.keyChest(85, 14, -31, Direction.WEST);
    }

    // ── Keller: Lorenbahn-Stollen (Schluessel 3) ──

    /** Knoten des Stollennetzes (u, v). */
    private static final int[][] NODES = {
            {-24, 46}, {-44, 46}, {-44, 62}, {-66, 62}, {-66, 78}, {-44, 78}, {-20, 78}, {-20, 62}, {4, 78}, {4, 62}, {4, 46}, {16, 62}};
    /** Stollen zwischen Knoten. */
    private static final int[][] EDGES = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {2, 5}, {5, 6}, {6, 7}, {6, 8}, {8, 9}, {9, 10}, {9, 11}};

    private BlockState rockMix(Kit k, Pal p, int u, int y, int v) {
        double n = k.noise(141, u + y * 3, v - y * 2, 4);
        if (n > 0.7) return p.clay;
        if (n > 0.45) return p.cobble;
        if (n < 0.12) return k.rnd(142, u, y, v) < 0.3 ? p.oreIron : p.cBrickCr;
        return p.rock;
    }

    private void basement(Kit k, Pal p) {
        // 1) Huellen
        for (int[] e : EDGES) {
            int[] a = NODES[e[0]], c = NODES[e[1]];
            if (!k.touches(a[0] - 4, a[1] - 4, c[0] + 4, c[1] + 4)) continue;
            for (int u = lo(Math.min(a[0], c[0]) - 3, k.u0()); u <= hi(Math.max(a[0], c[0]) + 3, k.u1()); u++)
                for (int v = lo(Math.min(a[1], c[1]) - 3, k.v0()); v <= hi(Math.max(a[1], c[1]) + 3, k.v1()); v++)
                    for (int y = TF - 1; y <= TF + 6; y++) k.set(u, y, v, rockMix(k, p, u, y, v));
        }
        for (int[] n : NODES) {
            if (!k.touches(n[0] - 5, n[1] - 5, n[0] + 5, n[1] + 5)) continue;
            for (int u = lo(n[0] - 5, k.u0()); u <= hi(n[0] + 5, k.u1()); u++) for (int v = lo(n[1] - 5, k.v0()); v <= hi(n[1] + 5, k.v1()); v++)
                for (int y = TF - 1; y <= TF + 7; y++) k.set(u, y, v, rockMix(k, p, u, y, v));
        }
        // Erzbunker-Huelle
        int ku0 = 16, ku1 = 38, kv0 = 52, kv1 = 72;
        if (k.touches(ku0 - 2, kv0 - 2, ku1 + 2, kv1 + 2)) {
            for (int u = lo(ku0 - 2, k.u0()); u <= hi(ku1 + 2, k.u1()); u++) for (int v = lo(kv0 - 2, k.v0()); v <= hi(kv1 + 2, k.v1()); v++)
                for (int y = TF - 1; y <= -4; y++) k.set(u, y, v, rockMix(k, p, u, y, v));
        }
        // Treppenzugang: Huelle
        // 2) Aushub
        for (int[] e : EDGES) {
            int[] a = NODES[e[0]], c = NODES[e[1]];
            k.clear(Math.min(a[0], c[0]) - 2, TF + 1, Math.min(a[1], c[1]) - 2, Math.max(a[0], c[0]) + 2, TF + 5, Math.max(a[1], c[1]) + 2);
            k.fill(Math.min(a[0], c[0]) - 2, TF, Math.min(a[1], c[1]) - 2, Math.max(a[0], c[0]) + 2, TF, Math.max(a[1], c[1]) + 2, p.cobble);
        }
        for (int[] n : NODES) {
            k.clear(n[0] - 3, TF + 1, n[1] - 3, n[0] + 3, TF + 6, n[1] + 3);
            k.fill(n[0] - 3, TF, n[1] - 3, n[0] + 3, TF, n[1] + 3, p.conc);
        }
        k.clear(ku0, TF + 1, kv0, ku1, -6, kv1);
        k.fill(ku0, TF, kv0, ku1, TF, kv1, p.conc);
        // 3) Ausbau: Stempel, Schienen, Licht
        for (int[] e : EDGES) {
            int[] a = NODES[e[0]], c = NODES[e[1]];
            boolean alongU = a[1] == c[1];
            int s0 = alongU ? Math.min(a[0], c[0]) : Math.min(a[1], c[1]), s1 = alongU ? Math.max(a[0], c[0]) : Math.max(a[1], c[1]);
            int cross = alongU ? a[1] : a[0];
            if (!k.touches(alongU ? s0 : cross - 3, alongU ? cross - 3 : s0, alongU ? s1 : cross + 3, alongU ? cross + 3 : s1)) continue;
            for (int s = s0 + 4; s <= s1 - 4; s++) {
                int u = alongU ? s : cross, v = alongU ? cross : s;
                if (!k.touches(u - 3, v - 3, u + 3, v + 3)) continue;
                k.set(u, TF + 1, v, alongU ? p.railEW : p.railNS);
                if (s % 6 == 0) {
                    for (int t = -2; t <= 2; t += 4) {
                        int uu = alongU ? u : u + t, vv = alongU ? v + t : v;
                        k.fill(uu, TF + 1, vv, uu, TF + 4, vv, p.rust);
                    }
                    k.fill(alongU ? u : u - 2, TF + 5, alongU ? v - 2 : v, alongU ? u : u + 2, TF + 5, alongU ? v + 2 : v, p.rust);
                    if (s % 12 == 0) k.set(u, TF + 4, v, p.lanternH);
                }
                // Wandrohr
                int wu = alongU ? u : u + 2, wv = alongU ? v + 2 : v;
                k.set(wu, TF + 4, wv, alongU ? p.pipeX : p.pipeZ);
            }
        }
        for (int[] n : NODES) {
            if (!k.touches(n[0] - 4, n[1] - 4, n[0] + 4, n[1] + 4)) continue;
            for (int a = -3; a <= 3; a += 6) for (int c = -3; c <= 3; c += 6) k.fill(n[0] + a, TF + 1, n[1] + c, n[0] + a, TF + 5, n[1] + c, p.rust);
            k.fill(n[0] - 3, TF + 6, n[1], n[0] + 3, TF + 6, n[1], p.rust);
            k.fill(n[0], TF + 6, n[1] - 3, n[0], TF + 6, n[1] + 3, p.rust);
            k.set(n[0], TF + 6, n[1], p.froglight);
            k.set(n[0] + 2, TF + 1, n[1] + 2, p.rust);
            k.set(n[0] + 2, TF + 2, n[1] + 2, p.lantern);
        }
        // Lore (Trichterwagen) und Erz auf den Schienen
        int[][] carts = {{-34, 46}, {-44, 70}, {-55, 78}, {4, 70}, {-66, 70}};
        for (int[] c : carts) {
            boolean alongU = c[0] != -44 && c[0] != 4 && c[0] != -66;
            if (!k.touches(c[0] - 2, c[1] - 2, c[0] + 2, c[1] + 2)) continue;
            for (int t = -1; t <= 1; t++) {
                int u = alongU ? c[0] + t : c[0], v = alongU ? c[1] : c[1] + t;
                k.set(u, TF + 1, v, p.hopper);
                k.set(u, TF + 2, v, t == 0 ? p.rawIron : p.rawCopper);
            }
        }
        // Sackgasse West: Einsturz am Ende
        if (k.touches(-70, 74, -62, 84)) {
            for (int u = -69; u <= -63; u++) for (int v = 79; v <= 81; v++) for (int y = TF + 1; y <= TF + 6; y++) {
                if (y > TF + 2 + (v - 79) * 2 + (int) (k.rnd(151, u, y, v) * 2)) continue;
                k.set(u, y, v, k.rnd(152, u, y, v) < 0.5 ? p.cobble : p.scrap);
            }
        }
        // Sackgasse G: verschuetteter Stollen nach Norden
        if (k.touches(-24, 52, -16, 60)) {
            k.clear(-22, TF + 1, 54, -18, TF + 5, 58);
            for (int u = -22; u <= -18; u++) for (int v = 54; v <= 57; v++) for (int y = TF + 1; y <= TF + 5; y++) {
                if (v == 57 || y <= TF + 1 + (57 - v) * 0 + (int) (k.rnd(153, u, y, v) * 4)) k.set(u, y, v, k.rnd(154, u, y, v) < 0.6 ? p.rock : p.rust);
            }
            k.fill(-22, TF + 1, 54, -18, TF + 5, 54, p.rock);
        }
        // Erzbunker: Haufen, Trichter, Foerderband
        if (k.touches(ku0, kv0, ku1, kv1)) {
            for (int[] h : new int[][] {{20, 56, 0}, {35, 69, 1}, {34, 55, 0}}) {
                for (int u = lo(h[0] - 4, k.u0()); u <= hi(h[0] + 4, k.u1()); u++) for (int v = lo(h[1] - 4, k.v0()); v <= hi(h[1] + 4, k.v1()); v++) {
                    double d = Math.sqrt((u - h[0]) * (u - h[0]) + (v - h[1]) * (v - h[1]));
                    int top = (int) (3.5 - d * 0.9);
                    for (int y = 1; y <= top; y++) k.set(u, TF + y, v, h[2] == 0 ? (k.rnd(155, u, y, v) < 0.5 ? p.rawIron : p.oreIron) : (k.rnd(156, u, y, v) < 0.5 ? p.rawCopper : p.oreCopper));
                }
            }
            for (int[] t : new int[][] {{20, 56}, {35, 69}, {34, 55}}) {
                k.fill(t[0] - 2, -6, t[1] - 2, t[0] + 2, -6, t[1] + 2, p.rust);
                k.fill(t[0] - 1, -7, t[1] - 1, t[0] + 1, -7, t[1] + 1, p.rust);
                k.set(t[0], -8, t[1], p.hopper);
            }
            k.fill(ku0 + 1, TF + 3, 62, ku1 - 1, TF + 3, 62, p.rust);
            k.fill(ku0 + 1, TF + 4, 62, ku1 - 1, TF + 4, 62, p.slag);
            for (int u = ku0 + 3; u <= ku1 - 1; u += 5) k.fill(u, TF + 1, 62, u, TF + 2, 62, p.steel);
            for (int u = ku0 + 2; u <= ku1 - 2; u += 6) {
                k.set(u, -6, kv0 + 1, p.cage);
                k.set(u, -6, kv1 - 1, p.cage);
            }
            for (int v = kv0 + 3; v <= kv1 - 3; v += 6) k.set(ku1, TF + 3, v, p.froglight);
            k.fill(ku0, TF + 1, 60, ku0, TF + 4, 64, Kit.AIR);
            for (int v = 60; v <= 64; v++) k.set(ku0, TF, v, p.conc);
        }
        // Treppenhaus aus der Haupthalle (u -26..-22, v 25..38)
        if (k.touches(-28, 23, -20, 40)) {
            k.fill(-28, TF - 1, 24, -20, -1, 38, p.cBrick);
            for (int i = 0; i <= 13; i++) {
                int v = 25 + i, y = -1 - i;
                k.clear(-26, y + 1, v, -22, 0, v);
                k.fill(-26, y, v, -22, y, v, i == 13 ? p.cobble : Kit.stairs(Blocks.RED_SANDSTONE_STAIRS, Direction.NORTH, false));
            }
            k.clear(-26, TF + 1, 39, -22, TF + 5, 43);
            k.fill(-26, TF, 39, -22, TF, 43, p.cobble);
            k.fill(-27, TF + 1, 39, -27, TF + 5, 42, p.cBrick);
            k.fill(-21, TF + 1, 39, -21, TF + 5, 42, p.cBrick);
            k.fill(-26, TF + 6, 39, -22, TF + 6, 42, p.cBrick);
            for (int v = 24; v <= 39; v++) {
                k.set(-27, 1, v, p.bars);
                k.set(-21, 1, v, p.bars);
            }
            k.fill(-27, 1, 39, -21, 1, 39, p.bars);
            for (int v = 27; v <= 37; v += 5) {
                k.set(-27, -2 - (v - 25), v, p.lantern);
                k.set(-21, -2 - (v - 25), v, p.cage);
            }
            k.set(-27, 2, 24, p.cage);
            k.set(-21, 2, 24, p.cage);
        }
        // Gauntlet: Spawner in Wandnischen entlang des Hauptweges
        k.spawner(-34, TF + 2, 43, SCUTTLER);
        k.spawner(-41, TF + 2, 54, GUNNER);
        k.spawner(-66, TF + 1, 76, SCUTTLER);
        k.spawner(-32, TF + 2, 81, SCUTTLER);
        k.spawner(-20, TF + 1, 64, GUNNER);
        k.spawner(7, TF + 2, 70, SCUTTLER);
        k.spawner(24, TF + 1, 70, SCUTTLER);
        k.mob(-44, TF + 1, 47, SCUTTLER);
        k.mob(-44, TF + 1, 77, SCUTTLER);
        k.mob(4, TF + 1, 77, SCUTTLER);
        k.mob(-20, TF + 1, 77, GUNNER);
        k.mob(26, TF + 1, 58, SCUTTLER);
        k.loot(-66, TF + 1, 75, Direction.NORTH);
        k.barrel(4, TF + 1, 44);
        k.loot(37, TF + 1, 53, Direction.WEST);
        k.elite(30, TF + 1, 63, GUNNER);
    }

    // ── Schacht und Montagehalle (Boss) ──

    private void shaftAndArena(Kit k, Pal p) {
        int su = 0, sv = SV;
        // Arena-Huelle (Achteck)
        if (k.touches(-30, AV - 25, 30, AV + 25)) {
            for (int u = lo(-29, k.u0()); u <= hi(29, k.u1()); u++) for (int v = lo(AV - 24, k.v0()); v <= hi(AV + 24, k.v1()); v++) {
                int au = Math.abs(u), adv = Math.abs(v - AV);
                if (au > 29 || adv > 24 || au + adv > 45) continue;
                boolean air = au <= 27 && adv <= 22 && au + adv <= 42;
                for (int y = AF - 2; y <= AC + 2; y++) {
                    BlockState s;
                    if (air && y > AF && y < AC) s = Kit.AIR;
                    else if (air && y == AF) {
                        double n = k.noise(161, u, v, 4);
                        s = (u % 8 == 0 || (v - AV) % 8 == 0) ? p.steel : n > 0.7 ? p.concCr : p.tile;
                    } else if (air && y == AC) s = (v - AV) % 6 == 0 ? p.rust : (u % 6 == 3 && (v - AV) % 6 == 3) ? p.froglight : p.conc;
                    else s = y <= AF + 3 ? p.cBrick : k.noise(162, u + v, y, 4) > 0.7 ? p.cBrickCr : p.conc;
                    k.set(u, y, v, s);
                }
            }
            // Wandpfeiler
            for (int u = lo(-28, k.u0()); u <= hi(28, k.u1()); u++) for (int v = lo(AV - 23, k.v0()); v <= hi(AV + 23, k.v1()); v++) {
                int au = Math.abs(u), adv = Math.abs(v - AV);
                boolean air = au <= 27 && adv <= 22 && au + adv <= 42;
                if (!air) continue;
                boolean nearWall = au == 27 || adv == 22 || au + adv == 42;
                if (!nearWall) continue;
                if ((u + v) % 6 == 0) for (int y = AF + 1; y < AC; y++) k.set(u, y, v, y % 5 == 0 ? p.steel : p.rust);
                else if ((u + v) % 6 == 3) k.set(u, AF + 6, v, p.cage);
                else k.set(u, AF + 1, v, (u + v) % 2 == 0 ? p.yellowConc : p.blackConc);
            }
            // Freistehende Stuetzen mit Kranbahn
            for (int s = -1; s <= 1; s += 2) {
                for (int dv : new int[] {-12, 12}) {
                    int u = 18 * s, v = AV + dv;
                    k.fill(u - 1, AF + 1, v - 1, u + 1, AF + 2, v + 1, p.cBrick);
                    k.fill(u, AF + 1, v, u + 1, AC - 1, v + 1, p.rust);
                    k.fill(u, AF + 3, v, u + 1, AF + 3, v + 1, p.steel);
                    k.set(u - 1, AF + 8, v, p.cage);
                }
                k.fill(18 * s, AC - 5, AV - 21, 18 * s + s, AC - 5, AV + 21, p.rust);
                for (int v = AV - 18; v <= AV + 18; v += 6) hangLamp(k, p, 18 * s, AC - 6, 2, v + 3);
            }
            // Kranbruecke mit Laufkatze und Riesenarm am Haken
            k.fill(-18, AC - 4, AV + 4, 19, AC - 4, AV + 5, p.rust);
            k.fill(5, AC - 3, AV + 3, 9, AC - 2, AV + 6, p.redCopper);
            chain(k, p, 7, AC - 12, AC - 5, AV + 4);
            k.fill(5, AC - 15, AV + 3, 9, AC - 13, AV + 6, p.steel);
            k.fill(6, AC - 17, AV + 4, 8, AC - 16, AV + 5, p.pipeQ);
            // Montageplattform und halbfertiges Robotergerippe im Norden
            robot(k, p, 0, AV - 16);
            // Liegender Roboterkopf
            k.fill(-22, AF + 1, AV + 4, -17, AF + 4, AV + 9, p.steel);
            k.fill(-22, AF + 2, AV + 5, -22, AF + 3, AV + 8, p.glassR);
            k.set(-22, AF + 3, AV + 6, p.froglight);
            k.set(-22, AF + 3, AV + 7, p.froglight);
            k.fill(-21, AF + 5, AV + 6, -18, AF + 5, AV + 7, p.rust);
            // Liegender Arm
            k.fill(14, AF + 1, AV + 14, 24, AF + 2, AV + 15, p.rust);
            k.fill(22, AF + 1, AV + 13, 25, AF + 3, AV + 17, p.steel);
            // Montagegleis quer durch die Halle
            for (int u = lo(-26, k.u0()); u <= hi(26, k.u1()); u++) if (k.col(u, AV + 10)) k.set(u, AF + 1, AV + 10, p.railEW);
        }
        // Schacht: Turm ueber Tage, Wendelrampe, Fahrkorb
        if (k.touches(su - 10, sv - 10, su + 10, sv + 10)) {
            for (int y = AF - 1; y <= 14; y++) {
                BlockState w = y <= 0 ? (y % 8 == 0 ? p.rust : p.conc) : y % 5 == 0 ? p.rust : (y > 10 ? p.redTerra : p.bricks);
                k.tube(su, sv, y, y, 9, 2, w, Kit.AIR);
            }
            k.cylinder(su, sv, 15, 16, 9.5, p.conc);
            k.cylinder(su, sv, 17, 17, 8, p.rust);
            k.cylinder(su, sv, AF - 1, AF, 7.6, p.conc);
            k.spiral(su, sv, AF, 0, 3, 7, 1.5, 32.6, 4, p.grate);
            k.spiral(su, sv, AF + 1, 1, 2.6, 3.4, 3.4 / (7 / 1.5), 32.6, 0, p.bars);
            // Fahrkorb haengt fest
            for (int a = -1; a <= 1; a += 2) for (int c = -1; c <= 1; c += 2) chain(k, p, su + a, -19, 14, sv + c);
            k.fill(su - 1, -20, sv - 1, su + 1, -20, sv + 1, p.grate);
            k.fill(su - 1, -19, sv - 1, su + 1, -17, sv - 1, p.bars);
            k.fill(su - 1, -16, sv - 1, su + 1, -16, sv + 1, p.rust);
            for (int y = -30; y <= 12; y += 7) {
                k.set(su + 7, y, sv, p.cage);
                k.set(su - 7, y, sv, p.cage);
            }
            // Glasschlitze im Turm
            for (int y = 4; y <= 12; y += 4) for (int[] d : new int[][] {{9, 0}, {-9, 0}, {0, -9}}) k.set(su + d[0], y, sv + d[1], p.glassO);
        }
        // Foerdergeruest auf dem Turm
        if (k.touches(su - 12, sv - 22, su + 12, sv + 10)) {
            for (int a = -1; a <= 1; a += 2) for (int c = -1; c <= 1; c += 2) k.line(su + 6 * a, 18, sv + 6 * c, su + 3 * a, 44, sv + 3 * c, 0.6, p.rust);
            for (int y = 24; y <= 40; y += 8) {
                double w = 6 - (y - 18) * 3.0 / 26;
                int iw = (int) Math.round(w);
                k.fill(su - iw, y, sv - iw, su + iw, y, sv - iw, p.rust);
                k.fill(su - iw, y, sv + iw, su + iw, y, sv + iw, p.rust);
                k.fill(su - iw, y, sv - iw, su - iw, y, sv + iw, p.rust);
                k.fill(su + iw, y, sv - iw, su + iw, y, sv + iw, p.rust);
            }
            k.fill(su - 4, 45, sv - 4, su + 4, 45, sv + 4, p.grate);
            k.fill(su - 4, 46, sv - 4, su + 4, 46, sv - 4, p.bars);
            k.fill(su - 4, 46, sv + 4, su + 4, 46, sv + 4, p.bars);
            // Seilscheiben (Ebene u-y)
            for (int w : new int[] {sv - 2, sv + 2}) {
                for (int u = lo(su - 7, k.u0()); u <= hi(su + 7, k.u1()); u++) {
                    if (!k.col(u, w)) continue;
                    for (int y = 44; y <= 60; y++) {
                        double d = Math.sqrt((u - su) * (u - su) + (y - 52) * (y - 52));
                        if (Math.abs(d - 6) < 0.6) k.set(u, y, w, p.rust);
                        else if (d < 6 && (u == su || y == 52)) k.set(u, y, w, p.bars);
                    }
                    k.set(su, 52, w, p.steel);
                }
            }
            k.line(su, 44, sv + 3, su, 2, sv + 22 - 44, 0.8, p.rust);
            k.line(su, 44, sv - 3, su, 1, sv - 20, 0.9, p.rust);
            k.fill(su - 1, 1, sv - 21, su + 1, 2, sv - 19, p.cBrick);
            k.set(su, 61, sv - 2, p.lightningRod);
        }
        // Verbindung Haupthalle -> Siegeltor -> Schacht
        if (k.touches(-8, HV0 - 6, 8, HV0 + 2)) {
            k.fill(-6, 0, HV0 - 4, 6, 9, HV0 - 1, p.cBrick);
            k.clear(-3, 1, HV0 - 4, 3, 7, HV0 - 1);
            k.fill(-3, 0, HV0 - 4, 3, 0, HV0 - 1, p.grate);
        }
        k.sealGate(0, 0, HV0, Direction.SOUTH, 3, 7, p.rust, p.cutCopperO);
        if (k.touches(-8, HV0, 8, HV0 + 3)) {
            for (int y = 1; y <= 12; y += 2) {
                k.set(-6, y, HV0 + 1, y % 4 == 1 ? p.yellowConc : p.blackConc);
                k.set(6, y, HV0 + 1, y % 4 == 1 ? p.yellowConc : p.blackConc);
            }
            k.set(-6, 13, HV0 + 1, p.cage);
            k.set(6, 13, HV0 + 1, p.cage);
        }
        // Schacht -> Arena
        k.clear(-3, AF + 1, AV + 22, 3, AF + 6, sv - 7);
        k.fill(-3, AF, AV + 22, 3, AF, sv - 7, p.hazard);
        k.boss(0, AF + 1, AV);
        k.bossChest(-12, AF + 1, AV - 19, Direction.SOUTH);
        k.bossChest(12, AF + 1, AV - 19, Direction.SOUTH);
    }

    /** Montageplattform mit Robotergerippe, Mitte (cu, cv) = Fusspunkt. */
    private void robot(Kit k, Pal p, int cu, int cv) {
        if (!k.touches(cu - 12, cv - 6, cu + 12, cv + 6)) return;
        int f = AF + 1;
        // Plattform
        k.fill(cu - 9, AF, cv - 4, cu + 9, AF, cv + 4, p.hazard);
        k.fill(cu - 8, AF, cv - 3, cu + 8, AF, cv + 3, p.steel);
        // Beine
        for (int s = -1; s <= 1; s += 2) {
            int u = cu + 4 * s;
            k.fill(u - 1, f, cv - 2, u + 1, f + 1, cv + 2, p.rust);
            k.fill(u, f + 2, cv - 1, u + s, f + 5, cv, p.steel);
            k.fill(u - 1, f + 6, cv - 1, u + 1, f + 7, cv + 1, p.redCopper);
            k.fill(u, f + 8, cv - 1, u + s, f + 11, cv, p.pipeQ);
            k.set(u, f + 9, cv + 1, p.bars);
        }
        // Becken / Rumpf
        k.fill(cu - 5, f + 12, cv - 2, cu + 5, f + 13, cv + 1, p.steel);
        for (int y = f + 14; y <= f + 19; y++) {
            for (int u = cu - 5; u <= cu + 5; u++) for (int v = cv - 2; v <= cv + 1; v++) {
                boolean edge = u == cu - 5 || u == cu + 5 || v == cv - 2 || v == cv + 1;
                if (!edge) continue;
                boolean corner = (u == cu - 5 || u == cu + 5) && (v == cv - 2 || v == cv + 1);
                k.set(u, y, v, corner ? p.rust : (y % 2 == 0 ? p.rust : p.bars));
            }
        }
        k.fill(cu - 1, f + 15, cv - 1, cu + 1, f + 17, cv, p.froglight);
        k.fill(cu - 7, f + 19, cv - 2, cu + 7, f + 20, cv + 1, p.rust);
        // Linker Arm haengt herab
        k.fill(cu - 8, f + 9, cv - 1, cu - 7, f + 18, cv, p.steel);
        k.fill(cu - 9, f + 6, cv - 2, cu - 6, f + 8, cv + 1, p.redCopper);
        // Halsstumpf mit Kabeln
        k.fill(cu - 1, f + 21, cv - 1, cu + 1, f + 21, cv, p.pipeQ);
        // Geruest seitlich
        for (int s = -1; s <= 1; s += 2) {
            int u = cu + 11 * s;
            for (int y = f; y <= f + 18; y++) {
                k.set(u, y, cv - 3, p.scaffold);
                k.set(u, y, cv + 3, p.scaffold);
            }
            for (int y = f + 6; y <= f + 18; y += 6) k.fill(u - 1, y, cv - 3, u + 1, y, cv + 3, p.grate);
        }
    }
}
