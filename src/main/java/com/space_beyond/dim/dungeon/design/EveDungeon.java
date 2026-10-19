package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Eve: die Schrabidium-Kathedrale. Eine gotische Kathedrale (Langschiff mit Seitenschiffen, Querhaus, Chor mit
 * Umgang, Rosetten, Strebewerk, Wasserspeier, Vierungsturm) mit freistehendem Glockenturm (Spitze bis y 86).
 * <ul>
 * <li>Schluessel 1: Glockenturm - Treppen, Balken-Parkour, Leitern bis zur Glockenstube (Truhe unter der Glocke).</li>
 * <li>Schluessel 2: Krypta unter dem Chor, mit Quecksilber geflutet - schmale Stege und Sarkophag-Trittsteine.</li>
 * <li>Schluessel 3: Kreuzgang mit Kristallgarten - Siegelwaechter (crystal_mantis).</li>
 * </ul>
 * Hinter dem Altar (Chorumgang) das Siegeltor, dahinter eine Wendeltreppe 47 Bloecke hinab, der Prozessionsgang
 * und das runde Hydra-Heiligtum mit Kristallkuppel und Wassergraben unter dem Langschiff.
 */
public final class EveDungeon extends CustomDungeon {

    private static final String MANTIS = "space_beyond:crystal_mantis", SPITTER = "space_beyond:schrab_spitter";
    /** Apsis-Mitte, Querhausachse, Siegeltor, Brunnenschacht, Heiligtum (Mitte u, Boden y). */
    private static final int APSE = 46, TU = 14, GATE_U = 63, WELL_U = 77, SU = -20, SF = -47;
    /** Glockenturm-Mitte. */
    private static final int TCU = -68, TCV = -32;
    /** Kreuzgang (Aussenmauer). */
    private static final int CU0 = -52, CU1 = -12, CV0 = 28, CV1 = 68;
    private static final double[] APSE_POSTS = {-90, -54, -18, 18, 54, 90};
    /** Turm-Parkour: Polsterwinkel 72 Grad, 12 Balkenkoepfe. */
    private static final int PADS = 12;

    @Override public Stage stage() { return Stage.EVE; }
    @Override public int radius() { return 92; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.eve.EveBlocks.EVE_SILT.get()); }
    @Override public BlockState filler() { return Kit.b(com.space_m.dim.eve.EveBlocks.EVE_ROCK.get()); }
    @Override public boolean fits(BiomeGenBaseCelestial biome) {
        return biome != com.space_m.dim.eve.biome.BiomeGenBaseEve.eveOcean && biome != com.space_m.dim.eve.biome.BiomeGenBaseEve.eveRiver;
    }
    @Override public int minBase() { return 58; }
    @Override public int maxBase() { return 160; }
    @Override public int minY() { return -54; }
    @Override public int maxY() { return 86; }
    @Override public int[] entry() { return new int[] {-88, 1, 0}; }
    @Override public String boss() { return "space_beyond:schrab_hydra"; }

    private static BlockState b(Block x) {
        return x.defaultBlockState();
    }

    /** Palette (je Bau neu, damit keine Registry-Objekte beim Klassenladen gebraucht werden). */
    private static final class P {
        final BlockState brick = b(Blocks.DEEPSLATE_BRICKS), cracked = b(Blocks.CRACKED_DEEPSLATE_BRICKS), tiles = b(Blocks.DEEPSLATE_TILES),
                ctiles = b(Blocks.CRACKED_DEEPSLATE_TILES), pol = b(Blocks.POLISHED_DEEPSLATE), chis = b(Blocks.CHISELED_DEEPSLATE),
                cobble = b(Blocks.COBBLED_DEEPSLATE), rock = b(com.space_m.dim.eve.EveBlocks.EVE_ROCK.get()),
                schrabOre = b(com.space_m.dim.eve.EveBlocks.ORE_SCHRABIDIUM.get()), mercury = b(com.space_m.dim.eve.EveBlocks.MERCURY_BLOCK.get()),
                purpur = b(Blocks.PURPUR_BLOCK), pillar = Kit.axis(Blocks.PURPUR_PILLAR, Direction.Axis.Y), amethyst = b(Blocks.AMETHYST_BLOCK),
                // bewusst wertlos (Prismarin statt Schrabidium-Cluster), sonst bauen Spieler die Kathedrale ab
                crystal = b(Blocks.PRISMARINE), schrabidic = b(ModBlocks.SCHRABIDIC_BLOCK.get()),
                calcite = b(Blocks.CALCITE), basalt = b(Blocks.SMOOTH_BASALT), tuff = b(Blocks.TUFF),
                cyanG = b(Blocks.CYAN_STAINED_GLASS), lbG = b(Blocks.LIGHT_BLUE_STAINED_GLASS), magG = b(Blocks.MAGENTA_STAINED_GLASS),
                purG = b(Blocks.PURPLE_STAINED_GLASS), tinted = b(Blocks.TINTED_GLASS), frog = b(Blocks.PEARLESCENT_FROGLIGHT), sea = b(Blocks.SEA_LANTERN),
                lanternH = Kit.lantern(Blocks.SOUL_LANTERN, true), lantern = Kit.lantern(Blocks.SOUL_LANTERN, false), chain = b(Blocks.CHAIN),
                water = b(Blocks.WATER), copper = b(Blocks.WAXED_OXIDIZED_COPPER), copperCut = b(Blocks.WAXED_OXIDIZED_CUT_COPPER),
                planks = b(Blocks.DARK_OAK_PLANKS), logX = Kit.axis(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X),
                logZ = Kit.axis(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Z), logY = Kit.axis(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Y),
                bwall = b(Blocks.DEEPSLATE_BRICK_WALL), polWall = b(Blocks.POLISHED_DEEPSLATE_WALL), carpet = b(Blocks.PURPLE_CARPET),
                bone = b(Blocks.BONE_BLOCK), purpurSlab = Kit.slab(Blocks.PURPUR_SLAB, false), purpurSlabTop = Kit.slab(Blocks.PURPUR_SLAB, true),
                clusterUp = Kit.facingAll(Blocks.AMETHYST_CLUSTER, Direction.UP), budUp = Kit.facingAll(Blocks.LARGE_AMETHYST_BUD, Direction.UP),
                rodUp = Kit.facingAll(Blocks.END_ROD, Direction.UP), lightningRod = Kit.facingAll(Blocks.LIGHTNING_ROD, Direction.UP),
                candles = CityPalette.with(CityPalette.with(b(Blocks.PURPLE_CANDLE), BlockStateProperties.CANDLES, 3), BlockStateProperties.LIT, true),
                candlesM = CityPalette.with(CityPalette.with(b(Blocks.MAGENTA_CANDLE), BlockStateProperties.CANDLES, 4), BlockStateProperties.LIT, true),
                bell = CityPalette.with(Kit.facing(Blocks.BELL, Direction.EAST), BlockStateProperties.BELL_ATTACHMENT, BellAttachType.CEILING);

        private final BlockState[] tileSt = new BlockState[6], polSt = new BlockState[6], polStTop = new BlockState[6], clusterOut = new BlockState[6];

        P() {
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                int i = d.ordinal();
                tileSt[i] = Kit.stairs(Blocks.DEEPSLATE_TILE_STAIRS, d, false);
                polSt[i] = Kit.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, d, false);
                polStTop[i] = Kit.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, d, true);
                clusterOut[i] = Kit.facingAll(Blocks.AMETHYST_CLUSTER, d);
            }
        }

        BlockState tileStairs(Direction up) {
            return tileSt[up.ordinal()];
        }

        BlockState polStairs(Direction up, boolean top) {
            return top ? polStTop[up.ordinal()] : polSt[up.ordinal()];
        }

        BlockState cluster(Direction d) {
            return clusterOut[d.ordinal()];
        }
    }

    // ── Hilfen ──

    private static BlockState wallStone(Kit k, P p, int u, int y, int v) {
        double r = k.rnd(101, u, y, v);
        double n = k.noise(102, u * 2 + y, v * 2 - y, 11);
        if (n > 0.76 && r < 0.55) return r < 0.2 ? p.rock : r < 0.4 ? p.cobble : p.amethyst;
        if (r < 0.10) return p.cracked;
        if (r < 0.17) return p.tiles;
        if (r < 0.20) return p.rock;
        if (r < 0.21) return p.schrabOre;
        return p.brick;
    }

    private static void column(Kit k, int u, int v, int y0, int y1, BlockState s) {
        if (!k.col(u, v)) return;
        for (int y = y0; y <= y1; y++) k.set(u, y, v, s);
    }

    private static double vaultMain(double a) {
        return 22 + Math.sqrt(Math.max(0, 324 - (a + 9) * (a + 9)));
    }

    private static double aisleVault(double a) {
        double x = Math.abs(a - 13) + 3.5;
        return 10 + Math.sqrt(Math.max(0, 49 - x * x));
    }

    private static int postDist(int u) {
        int d = Math.floorMod(u + 2, 8);
        return Math.min(d, 8 - d);
    }

    private static int postDistT(int av) {
        int d = Math.floorMod(av - 18, 8);
        return Math.min(d, 8 - d);
    }

    private static BlockState glass(P p, int y, double x, int s) {
        if (x > 0.7) return (y & 1) == 0 ? p.magG : p.purG;
        int m = Math.floorMod(y + s, 4);
        return m == 0 ? p.cyanG : m == 2 ? p.lbG : p.purG;
    }

    /** Rosette: Radius d, Winkel in Grad, Aussenradius R. */
    private static BlockState rose(P p, double d, double ang, double rr) {
        if (d < 1.3) return p.crystal;
        int sect = Math.floorMod((int) Math.floor(ang / 30.0), 12);
        double off = Math.abs(Math.floorMod((int) Math.round(ang * 10), 300) / 10.0 - 15);   // 0 Sektormitte, 15 Speiche
        if ((15 - off) * Math.PI / 180 * d < 0.55) return p.purpur;
        if (Math.abs(d - rr * 0.5) < 0.5) return p.purpur;
        if (d < rr * 0.5) return sect % 2 == 0 ? p.magG : p.purG;
        if (off < 6 && d > rr * 0.74) return p.magG;
        return sect % 2 == 0 ? p.cyanG : p.lbG;
    }

    /** Riesenkristall: Glashaut, Prismarin-Kern, Leuchtkern, Spitze. */
    private static void crystal(Kit k, P p, double u, double v, double tu, double tv, double h, double r) {
        double eu = u + tu, ev = v + tv;
        k.line(u, -2, v, eu, h, ev, r, p.cyanG);
        k.line(u, -2, v, eu, h, ev, Math.max(0.6, r - 0.9), p.crystal);
        k.line(u, 0, v, eu, h * 0.8, ev, Math.max(0.3, r * 0.3), p.sea);
        k.line(eu, h, ev, u + tu * 1.3, h * 1.25, v + tv * 1.3, Math.max(0.5, r * 0.55), p.crystal);
        k.line(u + tu * 1.3, h * 1.25, v + tv * 1.3, u + tu * 1.4, h * 1.33, v + tv * 1.4, 0.4, p.lbG);
    }

    @Override
    protected void build(Kit k) {
        P p = new P();
        plaza(k, p);
        outdoor(k, p);
        ruins(k, p);
        cathedral(k, p);
        facade(k, p);
        fleche(k, p);
        choir(k, p);
        cloister(k, p);
        tower(k, p);
        crypt(k, p);
        well(k, p);
        sanctum(k, p);
        processional(k, p);
        k.sealGate(GATE_U, 0, 0, Direction.WEST, 2, 6, p.chis, p.amethyst);
        plan(k);
    }

    // ── Vorplatz ──

    private void plaza(Kit k, P p) {
        if (!k.touches(-92, -34, -61, 34)) return;
        int ua = Math.max(k.u0(), -92), ub = Math.min(k.u1(), -61), va = Math.max(k.v0(), -34), vb = Math.min(k.v1(), 34);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            double fr = Math.hypot(u + 76, v);
            double ang = Math.toDegrees(Math.atan2(v, u + 76));
            BlockState f;
            if (Math.abs(v) <= 34 && (Math.abs(v) == 34 || u == -92)) f = p.pol;
            else if (fr >= 7 && fr < 8) f = p.purpur;
            else if (fr < 13 && Math.abs(Math.floorMod((int) Math.round(ang), 45) - 22.5) > 22.5 - 0.6 * 57.3 / Math.max(fr, 1)) f = p.amethyst;
            else if (Math.abs(v) <= 1 && u > -68) f = p.purpur;
            else if (k.noise(110, u, v, 7) > 0.72) f = k.pick(111, u, 0, v, p.rock, p.cobble, p.ctiles, p.basalt);
            else f = ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles;
            k.set(u, 0, v, f);
            k.set(u, -1, v, p.cobble);
            // Balustrade am Rand mit Luecken
            if ((Math.abs(v) == 34 || u == -92) && Math.floorMod(u + v, 9) != 0 && Math.abs(v) > 3 && k.rnd(112, u, 0, v) > 0.15) k.set(u, 1, v, p.bwall);
            // Brunnen
            if (fr <= 4.5) {
                k.set(u, -1, v, p.pol);
                if (fr <= 0.7) column(k, u, v, 0, 5, p.crystal);
                else if (fr <= 3.0) k.set(u, 0, v, p.mercury);
                else {
                    k.set(u, 0, v, p.pol);
                    k.set(u, 1, v, fr > 3.8 ? p.purpur : p.purpurSlab);
                }
            }
        }
        k.set(-76, 6, 0, p.crystal);
        k.set(-76, 7, 0, p.clusterUp);
        // Laternenpfosten
        for (int u = -88; u <= -64; u += 6) for (int s = -1; s <= 1; s += 2) {
            int v = s * 10;
            k.set(u, 1, v, p.pol);
            column(k, u, v, 2, 3, p.polWall);
            k.set(u, 4, v, p.frog);
            k.set(u, 5, v, p.lantern);
        }
    }

    // ── Kristalle und Trummer im Freien ──

    private void outdoor(Kit k, P p) {
        double[][] cr = {{-84, -26, -2, -2, 10, 1.5}, {-86, 24, -3, 2, 12, 1.7}, {-66, 30, 1, 3, 8, 1.3}, {-8, -32, -2, -3, 15, 2.0},
                {36, 30, 2, 3, 16, 2.1}, {58, -28, 3, -2, 13, 1.9}, {30, -36, 1, -3, 11, 1.6}, {-2, 48, -2, 3, 14, 1.9}, {68, 24, 3, 2, 11, 1.6},
                {-58, 76, -2, 3, 12, 1.7}, {44, 52, 2, 2, 9, 1.4}, {-88, -6, -2, -1, 7, 1.2}};
        for (double[] c : cr) crystal(k, p, c[0], c[1], c[2], c[3], c[4], c[5]);
        // gestuerzte Saeulen auf dem Vorplatz
        int[][] cols = {{-86, -18, 7}, {-87, 17, 4}, {-70, 26, 9}, {-82, 28, 5}, {-90, 8, 3}};
        for (int[] c : cols) {
            column(k, c[0], c[1], 1, c[2], p.pillar);
            k.set(c[0], 0, c[1], p.chis);
            k.set(c[0], c[2] + 1, c[1], p.budUp);
        }
        k.line(-84, 1, 20, -79, 1, 23, 0.6, p.pillar);
        k.line(-89, 1, -12, -86, 1, -9, 0.6, p.pillar);
    }

    // ── Kapellenruine im Norden ──

    private void ruins(Kit k, P p) {
        if (!k.touches(-48, -66, -12, -44)) return;
        int ua = Math.max(k.u0(), -46), ub = Math.min(k.u1(), -14), va = Math.max(k.v0(), -64), vb = Math.min(k.v1(), -46);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            if (k.noise(301, u, v, 5) > 0.3) k.set(u, 0, v, k.pick(302, u, 0, v, p.tiles, p.ctiles, p.cobble, p.pol, p.rock));
            boolean edge = u == -46 || u == -14 || v == -64 || v == -46;
            if (edge) {
                boolean door = (v == -46 && u >= -31 && u <= -29) || (u == -14 && v >= -56 && v <= -54) || (u == -46 && v >= -56 && v <= -54);
                int h = 2 + (int) (15 * k.noise(303, u, v, 6));
                if (door) h = 0;
                for (int y = 1; y <= h; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                // Fensterreste
                if ((v == -64 || v == -46) && Math.floorMod(u + 2, 6) <= 1 && h > 9) for (int y = 4; y <= 8; y++) k.set(u, y, v, Kit.AIR);
                if (h > 0 && h < 14 && k.rnd(304, u, h, v) < 0.5) k.set(u, h + 1, v, p.budUp);
            } else if (Math.abs(u + 30) + Math.abs(v + 50) <= 2 || Math.abs(u + 40) + Math.abs(v + 60) <= 2) {
                continue;
            } else if (k.rnd(305, u, 1, v) < 0.05) {
                k.set(u, 1, v, k.rnd(306, u, 1, v) < 0.5 ? p.cobble : p.polStairs(Direction.from2DDataValue((int) (k.rnd(307, u, 1, v) * 4)), false));
            } else if (k.rnd(308, u, 1, v) < 0.04) k.set(u, 1, v, p.clusterUp);
        }
        for (int cu = -38; cu <= -22; cu += 8) for (int cv = -58; cv <= -52; cv += 6) {
            int h = 3 + (int) (k.rnd(309, cu, 0, cv) * 10);
            column(k, cu, cv, 1, h, p.pillar);
            k.set(cu, h + 1, cv, p.chis);
        }
        // Bogenreste
        k.line(-38, 9, -58, -30, 12, -58, 0.6, p.brick);
        k.line(-30, 12, -52, -22, 8, -52, 0.6, p.brick);
        crystal(k, p, -27, -57, 2, -2, 17, 2.3);
        crystal(k, p, -18, -61, 3, -1, 12, 1.7);
        crystal(k, p, -42, -49, -2, 2, 9, 1.4);
        // Altarstein
        k.fill(-44, 1, -56, -43, 1, -54, p.purpur);
        k.set(-44, 2, -55, p.candles);
    }

    // ── Kathedrale (Spaltenweise) ──

    private void cathedral(Kit k, P p) {
        int ua = Math.max(k.u0(), -57), ub = Math.min(k.u1(), APSE + 25);
        int va = Math.max(k.v0(), -42), vb = Math.min(k.v1(), 42);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            if (u <= APSE) {
                if (Math.abs(v) <= 24) nave(k, p, u, v, Math.abs(v), postDist(u), 4, v > 0 ? Direction.NORTH : Direction.SOUTH, false);
            } else {
                double du = u - APSE, a = Math.sqrt(du * du + v * v);
                if (a <= 24.5) {
                    double th = Math.toDegrees(Math.atan2(v, du)), dth = 180;
                    for (double q : APSE_POSTS) dth = Math.min(dth, Math.abs(th - q));
                    Direction in = Math.abs(du) >= Math.abs(v) ? Direction.WEST : (v > 0 ? Direction.NORTH : Direction.SOUTH);
                    nave(k, p, u, v, a, Math.toRadians(dth) * a, Math.toRadians(18) * a, in, true);
                }
            }
            if (Math.abs(u - TU) <= 16 && Math.abs(v) <= 41) transept(k, p, u, v);
        }
        // Vierungspfeiler
        for (int cu : new int[] {5, 23}) for (int cv : new int[] {-9, 9}) {
            for (int u = cu - 1; u <= cu + 1; u++) for (int v = cv - 1; v <= cv + 1; v++) {
                boolean corner = u != cu && v != cv;
                column(k, u, v, 1, 30, corner ? p.pillar : p.pol);
                k.set(u, 10, v, p.chis);
                k.set(u, 23, v, p.chis);
                if (!corner && (u != cu || v != cv)) k.set(u, 6, v, p.frog);
            }
        }
        // Kronleuchter in Jochmitte
        for (int u = -54; u <= 42; u += 8) {
            if (u > 4 && u < 24) continue;
            int top = (int) Math.floor(vaultMain(0));
            column(k, u, 0, 13, top, p.chain);
            k.set(u, 12, 0, p.purpurSlabTop);
            k.set(u, 11, 0, p.frog);
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                k.set(u + d.getStepX(), 12, d.getStepZ(), p.purpurSlabTop);
                k.set(u + d.getStepX(), 11, d.getStepZ(), p.lanternH);
            }
        }
        // Vierung: haengender Kristall
        column(k, TU, 0, 26, 37, p.chain);
        for (int y = 19; y <= 25; y++) {
            double r = y >= 23 ? (26 - y) * 0.7 : (y - 18) * 0.45;
            k.cylinder(TU, 0, y, y, r, y == 22 ? p.sea : p.crystal);
        }
    }

    private void nave(Kit k, P p, int u, int v, double a, double dpost, double half, Direction in, boolean apse) {
        int ai = (int) Math.round(a);
        if (ai > 24) return;
        Direction out = in.getOpposite();
        boolean post = dpost < 0.5, pier = dpost < 1.5;
        double x = Math.abs(half - dpost);
        if (ai <= 17) {
            k.set(u, 0, v, floorNave(k, p, u, v, ai, x, apse));
            for (int y = -3; y < 0; y++) k.set(u, y, v, p.cobble);
        }
        if (ai <= 8) {
            int vt = (int) Math.floor(vaultMain(a));
            for (int y = 1; y <= vt; y++) k.set(u, y, v, Kit.AIR);
            boolean rib = post || Math.abs(a - 8 * (1 - dpost / half)) < 0.6 || ai == 0;
            k.set(u, vt + 1, v, rib ? p.purpur : p.tiles);
            k.set(u, vt + 2, v, p.tiles);
            int ry = 30 + (int) Math.round(10 - a);
            for (int y = vt + 3; y < ry - 1; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, ry - 1, v, p.tiles);
            if (ai == 0) {
                k.set(u, ry, v, p.pol);
                k.set(u, ry + 1, v, post ? p.clusterUp : p.purpurSlab);
            } else k.set(u, ry, v, p.tileStairs(in));
            if (ai == 8 && post) {
                for (int y = 1; y <= 21; y++) k.set(u, y, v, p.pillar);
                k.set(u, 6, v, p.frog);
                k.set(u, 22, v, p.chis);
            }
            // Gestuehl und Chorgestuehl
            if (!apse && !pier && u <= 3 && u >= -54 && ai >= 3 && ai <= 7 && Math.floorMod(u, 3) == 0)
                k.set(u, 1, v, Kit.stairs(Blocks.DARK_OAK_STAIRS, Direction.WEST, false));
            if (!apse && !pier && u >= 25 && u <= 44 && ai == 7) k.set(u, 1, v, Kit.stairs(Blocks.DARK_OAK_STAIRS, out, false));
            if (!apse && ai <= 1 && (u < 5 || u > 23) && u > -56 && u < 46) k.set(u, 1, v, p.carpet);
        } else if (ai == 9) {
            for (int y = 0; y <= 30; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
            if (pier) {
                for (int y = 1; y <= 21; y++) k.set(u, y, v, post ? p.pillar : p.pol);
                k.set(u, 9, v, p.chis);
                k.set(u, 22, v, p.chis);
            } else {
                double w = half - 1.5;
                double top = 9 + Math.sqrt(Math.max(0, 4 * w * w - (x + w) * (x + w)));
                for (int y = 1; y <= top; y++) k.set(u, y, v, Kit.AIR);
                k.set(u, (int) Math.floor(top) + 1, v, p.purpur);
                k.set(u, 14, v, p.pol);
                k.set(u, 17, v, p.pol);
                boolean c = Math.round(x) % 2 == 0;
                k.set(u, 15, v, c ? p.pillar : p.tinted);
                k.set(u, 16, v, c ? p.pillar : p.tinted);
                if (x <= 1.2) for (int y = 23; y <= (x < 0.5 ? 28 : 27); y++) k.set(u, y, v, glass(p, y, x, u + v));
            }
            k.set(u, 31, v, p.tileStairs(in));
        } else if (ai <= 16) {
            int at = (int) Math.floor(aisleVault(a));
            for (int y = 1; y <= at; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, at + 1, v, post ? p.purpur : p.tiles);
            int ra = 15 + (int) Math.round(17 - a);
            for (int y = at + 2; y < ra - 1; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, ra - 1, v, p.tiles);
            k.set(u, ra, v, p.tileStairs(in));
            if (ai == 10) k.set(u, 30, v, p.tileStairs(in));
            if (ai == 10 && post) {
                for (int y = 1; y <= 8; y++) k.set(u, y, v, p.pillar);
                k.set(u, 5, v, p.frog);
                k.set(u, 9, v, p.chis);
            }
            if (ai == 16 && post) {
                for (int y = 1; y <= 8; y++) k.set(u, y, v, p.pol);
                k.set(u, 4, v, p.frog);
                k.set(u, 9, v, p.chis);
            }
        } else if (ai == 17) {
            for (int y = 0; y <= 14; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
            if (!pier && x <= 1.2) {
                k.set(u, 2, v, p.pol);
                for (int y = 3; y <= (x < 0.5 ? 12 : 11); y++) k.set(u, y, v, glass(p, y, x, u + v));
                k.set(u, x < 0.5 ? 13 : 12, v, p.purpur);
            }
            k.set(u, 15, v, p.tileStairs(in));
        } else if (ai == 18 && !pier) {
            k.set(u, 14, v, p.tileStairs(in));
        }
        if (pier && ai >= 18) {
            int top = ai <= 19 ? 19 : ai <= 21 ? 13 : ai == 22 ? 7 : -1;
            if (top > 0) {
                for (int y = -1; y <= top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                if (ai == 19 && post) {
                    for (int y = 20; y <= 23; y++) k.set(u, y, v, p.pillar);
                    k.set(u, 24, v, p.clusterUp);
                } else k.set(u, top + 1, v, p.polStairs(in, false));
            }
            if (post && ai == 23) k.set(u, 7, v, p.polStairs(in, true));
            if (post && ai == 24) k.set(u, 7, v, p.cluster(out));
        }
        if (post && ai >= 10 && ai <= 18) {
            double t = (a - 10) / 9.0;
            int yb = (int) Math.floor(20 + 7 * Math.sqrt(Math.max(0, 1 - t * t)));
            k.set(u, yb, v, p.pol);
            k.set(u, yb + 1, v, ai == 18 ? p.pol : p.polStairs(in, false));
        }
    }

    private static BlockState floorNave(Kit k, P p, int u, int v, int ai, double x, boolean apse) {
        if (ai <= 8) {
            if (!apse && Math.abs(v) <= 1) return x < 0.5 ? p.amethyst : p.purpur;
            if (ai == 4 && x < 0.5) return p.sea;
            return ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles;
        }
        return k.rnd(120, u, 0, v) < 0.15 ? p.ctiles : p.tiles;
    }

    private void transept(Kit k, P p, int u, int v) {
        int at = Math.abs(u - TU), av = Math.abs(v);
        Direction in = u < TU ? Direction.EAST : Direction.WEST, out = in.getOpposite();
        if (av <= 17) {
            if (at > 8) return;
            k.set(u, 0, v, crossFloor(p, u, v));
            int vt = (int) Math.floor(vaultMain(at));
            int nt = av <= 8 ? (int) Math.floor(vaultMain(av)) : (av >= 10 && av <= 16) ? (int) Math.floor(aisleVault(av)) : 0;
            for (int y = 1; y <= vt; y++) k.set(u, y, v, Kit.AIR);
            for (int y = vt + 1; y <= vt + 2; y++) if (y > nt) k.set(u, y, v, at == 0 || at == av ? p.purpur : p.tiles);
            int rt = 30 + (10 - at), rn = av <= 10 ? 30 + (10 - av) : 15 + (17 - av);
            if (rt > rn) {
                for (int y = Math.max(vt + 3, nt + 3); y < rt - 1; y++) k.set(u, y, v, Kit.AIR);
                k.set(u, rt - 1, v, p.tiles);
                k.set(u, rt, v, at == 0 ? p.pol : p.tileStairs(in));
            }
            return;
        }
        double dp = postDistT(av);
        boolean post = dp < 0.5, pier = dp < 1.5;
        double x = Math.abs(4 - dp);
        if (av >= 37) {
            if (av <= 38 && at <= 10) {
                int top = 30 + (10 - at);
                for (int y = -1; y < top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                k.set(u, top, v, at == 0 ? p.pol : p.tileStairs(in));
                for (int y = 14; y <= 31; y++) {
                    double d = Math.hypot(at, y - 22);
                    double ang = Math.toDegrees(Math.atan2(y - 22, u - TU));
                    if (av == 37 && d <= 7) k.set(u, y, v, rose(p, d, ang, 7));
                    else if (av == 38 && d <= 7.5) k.set(u, y, v, Kit.AIR);
                    else if (av == 38 && d <= 8.5) k.set(u, y, v, p.purpur);
                }
                if (at <= 1) for (int y = 1; y <= (at == 0 ? 5 : 4); y++) k.set(u, y, v, Kit.AIR);
                if (at == 2 && av == 38) for (int y = 1; y <= 5; y++) k.set(u, y, v, p.pillar);
                if (at == 0 && av == 38) {
                    for (int y = top + 1; y <= top + 4; y++) k.set(u, y, v, p.pillar);
                    k.set(u, top + 5, v, p.crystal);
                    k.set(u, top + 6, v, p.clusterUp);
                }
            } else if (at >= 8 && at <= 10 && av <= 41) {
                int top = av <= 39 ? 28 : av == 40 ? 20 : 10;
                for (int y = -1; y <= top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                Direction up = v > 0 ? Direction.NORTH : Direction.SOUTH;
                k.set(u, top + 1, v, av <= 39 ? p.pillar : p.polStairs(up, false));
                if (av <= 39 && at == 9) k.set(u, top + 2, v, p.clusterUp);
            }
            return;
        }
        // Querhausarm
        if (at <= 8) {
            k.set(u, 0, v, at <= 1 ? p.purpur : ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles);
            for (int y = -3; y < 0; y++) k.set(u, y, v, p.cobble);
            int vt = (int) Math.floor(vaultMain(at));
            for (int y = 1; y <= vt; y++) k.set(u, y, v, Kit.AIR);
            boolean rib = post || Math.abs(at - 8 * (1 - dp / 4.0)) < 0.6 || at == 0;
            k.set(u, vt + 1, v, rib ? p.purpur : p.tiles);
            k.set(u, vt + 2, v, p.tiles);
            int ry = 30 + (10 - at);
            for (int y = vt + 3; y < ry - 1; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, ry - 1, v, p.tiles);
            if (at == 0) {
                k.set(u, ry, v, p.pol);
                k.set(u, ry + 1, v, post ? p.clusterUp : p.purpurSlab);
            } else k.set(u, ry, v, p.tileStairs(in));
            if (at == 8 && post) {
                for (int y = 1; y <= 21; y++) k.set(u, y, v, p.pillar);
                k.set(u, 6, v, p.frog);
                k.set(u, 22, v, p.chis);
            }
            if (at == 4 && x < 0.5) k.set(u, 0, v, p.sea);
        } else if (at == 9) {
            for (int y = -1; y <= 30; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
            if (!pier && x <= 1.2) {
                k.set(u, 2, v, p.pol);
                for (int y = 3; y <= (x < 0.5 ? 25 : 24); y++) k.set(u, y, v, y == 13 ? p.purpur : glass(p, y, x, u + v));
                k.set(u, x < 0.5 ? 26 : 25, v, p.purpur);
            }
            k.set(u, 31, v, p.tileStairs(in));
        } else if (at == 10 && !pier) {
            k.set(u, 30, v, p.tileStairs(in));
        }
        if (pier && at >= 10) {
            int top = at <= 11 ? 26 : at <= 13 ? 18 : at == 14 ? 10 : -1;
            if (top > 0) {
                for (int y = -1; y <= top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                if (at == 11 && post) {
                    for (int y = 27; y <= 30; y++) k.set(u, y, v, p.pillar);
                    k.set(u, 31, v, p.clusterUp);
                } else k.set(u, top + 1, v, p.polStairs(in, false));
            }
            if (post && at == 15) k.set(u, 10, v, p.polStairs(in, true));
            if (post && at == 16) k.set(u, 10, v, p.cluster(out));
        }
    }

    private static BlockState crossFloor(P p, int u, int v) {
        double du = u - TU, r = Math.hypot(du, v);
        if (r < 1.5) return p.crystal;
        if (r < 2.5) return p.amethyst;
        if (r >= 6 && r < 7) return p.purpur;
        double ang = Math.toDegrees(Math.atan2(v, du));
        double off = Math.abs(Math.floorMod((int) Math.round(ang * 10), 450) / 10.0 - 22.5);
        if (r < 8 && (22.5 - off) * Math.PI / 180 * r < 0.6) return p.amethyst;
        return ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles;
    }

    // ── Westfassade ──

    private void facade(Kit k, P p) {
        if (!k.touches(-63, -24, -55, 24)) return;
        for (int u = Math.max(k.u0(), -60); u <= Math.min(k.u1(), -58); u++) for (int v = Math.max(k.v0(), -18); v <= Math.min(k.v1(), 18); v++) {
            int av = Math.abs(v), depth = u + 60;
            int top = av <= 10 ? 31 + (10 - av) : 24;
            for (int y = -1; y <= top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
            Direction in = v > 0 ? Direction.NORTH : Direction.SOUTH;
            if (av <= 10) k.set(u, top + 1, v, av == 0 ? p.pol : p.polStairs(in, false));
            else k.set(u, top + 1, v, (av & 1) == 0 ? p.pol : p.polStairs(in, false));
            // Strebepfeiler-Lisenen an der Fassade
            if (depth == 0 && (av == 10 || av == 11)) for (int y = 0; y <= top; y++) k.set(u, y, v, y % 6 == 0 ? p.chis : p.pol);
            // Koenigsgalerie
            if (depth == 0 && av <= 9) {
                k.set(u, 14, v, p.pol);
                if (av % 3 == 1) for (int y = 15; y <= 17; y++) k.set(u, y, v, p.pillar);
                else {
                    for (int y = 15; y <= 17; y++) k.set(u, y, v, Kit.AIR);
                    k.set(u, 15, v, av % 3 == 0 ? p.amethyst : p.crystal);
                    k.set(u, 16, v, p.clusterUp);
                }
                k.set(u, 18, v, p.pol);
            }
            // Rosette
            for (int y = 16; y <= 36; y++) {
                double d = Math.hypot(v, y - 26), ang = Math.toDegrees(Math.atan2(y - 26, v));
                if (depth == 2 && d <= 7) k.set(u, y, v, rose(p, d, ang, 7));
                else if (depth == 1 && d <= 7) k.set(u, y, v, Kit.AIR);
                else if (depth == 1 && d <= 8) k.set(u, y, v, p.purpur);
                else if (depth == 0 && d <= 8) k.set(u, y, v, Kit.AIR);
                else if (depth == 0 && d <= 9) k.set(u, y, v, p.amethyst);
            }
            // Hauptportal (gestuftes Gewaende)
            double w = 4.5 - depth;
            for (int y = 1; y <= 14; y++) {
                if (open(av, y, w, 6)) k.set(u, y, v, Kit.AIR);
                else if (open(av - 1, y, w, 6) || open(av, y - 1, w, 6)) k.set(u, y, v, depth == 2 ? p.amethyst : p.purpur);
            }
            // Seitenportale
            int sv = Math.abs(av - 13);
            for (int y = 1; y <= 9; y++) {
                if (open(sv, y, 1.5, 4)) k.set(u, y, v, Kit.AIR);
                else if (open(sv - 1, y, 1.5, 4) || open(sv, y - 1, 1.5, 4)) k.set(u, y, v, p.purpur);
            }
        }
        // Fassadentuerme
        for (int s = -1; s <= 1; s += 2) {
            double cv = s * 20.5, cu = -59;
            if (!k.touches(-63, (int) cv - 4, -55, (int) cv + 4)) continue;
            for (int u = Math.max(k.u0(), -62); u <= Math.min(k.u1(), -56); u++) for (int v = Math.max(k.v0(), (int) cv - 4); v <= Math.min(k.v1(), (int) cv + 4); v++) {
                double d = Math.hypot(u - cu, v - cv);
                if (d > 3.0) continue;
                for (int y = -1; y <= 46; y++) k.set(u, y, v, y % 9 == 0 ? p.pol : wallStone(k, p, u, y, v));
                if (d > 2.2 && (u == -62 || Math.abs(v - cv) > 2.4)) for (int y = 10; y <= 40; y += 10) {
                    k.set(u, y, v, p.purG);
                    k.set(u, y + 1, v, p.purG);
                }
                for (int y = 47; y <= 58; y++) {
                    double r = 3.0 * (58 - y) / 11.0;
                    if (d <= r) k.set(u, y, v, y % 4 == 0 ? p.purpur : p.tiles);
                }
                if (d < 0.8) {
                    k.set(u, 59, v, p.crystal);
                    k.set(u, 60, v, p.rodUp);
                }
                if (d > 2.5 && d <= 3.0) k.set(u, 47, v, p.clusterUp);
            }
        }
    }

    private static boolean open(double ax, int y, double w, int spring) {
        if (ax < 0 || ax > w || y < 1) return false;
        if (y <= spring) return true;
        return y <= spring + Math.sqrt(Math.max(0, 4 * w * w - (ax + w) * (ax + w)));
    }

    // ── Vierungsturm (Dachreiter) ──

    private void fleche(Kit k, P p) {
        if (!k.touches(TU - 6, -6, TU + 6, 6)) return;
        for (int u = Math.max(k.u0(), TU - 6); u <= Math.min(k.u1(), TU + 6); u++) for (int v = Math.max(k.v0(), -6); v <= Math.min(k.v1(), 6); v++) {
            int du = Math.abs(u - TU), dv = Math.abs(v);
            double o = Math.max(Math.max(du, dv), (du + dv) * 0.7071);
            if (o <= 4.6) {
                for (int y = 40; y <= 48; y++) {
                    if (o > 3.5) {
                        boolean face = du <= 1 || dv <= 1;
                        k.set(u, y, v, face && y >= 42 && y <= 46 ? (y == 46 && (du == 1 || dv == 1) ? p.purpur : p.magG) : wallStone(k, p, u, y, v));
                    } else k.set(u, y, v, y == 48 ? p.tiles : Kit.AIR);
                }
                k.set(u, 49, v, p.pol);
            }
            if (o > 3.6 && o <= 5.2 && du == dv) for (int y = 40; y <= 52; y++) k.set(u, y, v, y >= 50 ? p.pillar : p.pol);
            if (o > 3.6 && o <= 5.2 && du == dv) k.set(u, 53, v, p.clusterUp);
            for (int y = 50; y <= 73; y++) {
                double rr = 4.6 * (73 - y) / 23.0;
                if (o <= rr) k.set(u, y, v, y % 5 == 0 ? p.purpur : p.tiles);
            }
        }
        k.set(TU, 74, 0, p.crystal);
        k.set(TU, 75, 0, p.lightningRod);
    }

    // ── Chor: Altar, Leuchter, Krypta-Abgang ──

    private void choir(Kit k, P p) {
        // Seitenkapelle im noerdlichen Seitenschiff
        k.fill(-48, 1, -16, -44, 1, -16, p.purpur);
        k.set(-48, 2, -16, p.candles);
        k.set(-44, 2, -16, p.candles);
        if (!k.touches(24, -18, 62, 18)) return;
        // Altarpodest
        for (int u = 47; u <= 54; u++) for (int v = -4; v <= 4; v++) {
            if (Math.hypot(u - APSE, v) > 8.4) continue;
            boolean edge = u == 47 || Math.abs(v) == 4;
            k.set(u, 1, v, edge ? (u == 47 ? p.polStairs(Direction.EAST, false) : p.polStairs(v > 0 ? Direction.NORTH : Direction.SOUTH, false)) : p.pol);
        }
        k.fill(50, 2, -2, 51, 2, 2, p.purpur);
        k.fill(50, 3, -2, 51, 3, 2, p.purpurSlab);
        k.set(50, 3, -2, p.candles);
        k.set(50, 3, 2, p.candles);
        k.set(51, 3, 0, p.crystal);
        k.set(51, 4, 0, p.clusterUp);
        // Monstranz: haengender Kristall ueber dem Altar
        column(k, 50, 0, 18, 36, p.chain);
        for (int y = 13; y <= 17; y++) k.cylinder(50, 0, y, y, y == 15 ? 1.2 : 0.6, y == 15 ? p.sea : p.crystal);
        // Kerzenstaender im Chor
        for (int u = 27; u <= 43; u += 8) for (int s = -1; s <= 1; s += 2) {
            k.set(u, 1, s * 5, p.pol);
            k.set(u, 2, s * 5, p.polWall);
            k.set(u, 3, s * 5, p.candlesM);
        }
        // Krypta-Abgang im noerdlichen Chorseitenschiff (u 31..44, v -16..-15)
        if (k.touches(25, -18, 46, -12)) {
            k.fill(26, -17, -17, 45, -1, -17, p.brick);
            k.fill(26, -17, -14, 45, -1, -14, p.brick);
            k.fill(26, -17, -17, 26, -1, -14, p.brick);
            for (int i = 1; i <= 14; i++) {
                int u = 45 - i;
                for (int v = -16; v <= -15; v++) {
                    for (int y = -16; y < -i; y++) k.set(u, y, v, p.cobble);
                    k.set(u, -i, v, Kit.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
                    for (int y = -i + 1; y <= Math.min(-1, -i + 5); y++) k.set(u, y, v, Kit.AIR);
                    if (u >= 41) k.set(u, 0, v, Kit.AIR);
                }
            }
            for (int u = 27; u <= 30; u++) for (int v = -16; v <= -15; v++) {
                for (int y = -16; y <= -14; y++) k.set(u, y, v, p.pol);
                for (int y = -13; y <= -9; y++) k.set(u, y, v, Kit.AIR);
            }
            k.clear(27, -13, -14, 29, -10, -13);
            for (int u = 41; u <= 45; u++) k.set(u, 1, -14, p.bwall);
            k.set(40, 1, -16, p.bwall);
            k.set(40, 1, -15, p.bwall);
            k.set(40, 2, -14, p.lantern);
            k.set(40, 1, -14, p.bwall);
            k.set(36, -6, -15, p.lanternH);
            k.set(36, -5, -15, p.pol);
        }
    }

    // ── Kreuzgang mit Kristallgarten ──

    private void cloister(Kit k, P p) {
        if (k.touches(CU0, CV0, CU1, CV1)) {
            int ua = Math.max(k.u0(), CU0), ub = Math.min(k.u1(), CU1), va = Math.max(k.v0(), CV0), vb = Math.min(k.v1(), CV1);
            for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
                int du = Math.min(u - CU0, CU1 - u), dv = Math.min(v - CV0, CV1 - v), d = Math.min(du, dv);
                Direction out = du < dv ? (u - CU0 < CU1 - u ? Direction.WEST : Direction.EAST) : (v - CV0 < CV1 - v ? Direction.NORTH : Direction.SOUTH);
                int s = du < dv ? v : u;
                for (int y = -2; y < 0; y++) k.set(u, y, v, p.cobble);
                if (d == 0) {
                    for (int y = 0; y <= 12; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                    k.set(u, 13, v, Math.floorMod(s, 2) == 0 ? p.pol : p.purpurSlab);
                    if (Math.floorMod(s, 4) == 0) for (int y = 0; y <= 12; y++) k.set(u, y, v, p.pol);
                    else if (Math.floorMod(s, 4) == 2) for (int y = 3; y <= 7; y++) k.set(u, y, v, glass(p, y, 0, s));
                } else if (d <= 5) {
                    k.set(u, 0, v, d == 3 ? (Math.floorMod(s, 4) == 0 ? p.amethyst : p.purpur) : (Math.floorMod(s + d, 2) == 0 ? p.pol : p.tiles));
                    double vx = Math.abs(d - 3) + 2.5;
                    int vt = (int) Math.floor(5 + Math.sqrt(Math.max(0, 25 - vx * vx)));
                    for (int y = 1; y <= vt; y++) k.set(u, y, v, Kit.AIR);
                    k.set(u, vt + 1, v, Math.floorMod(s, 4) == 0 ? p.purpur : p.tiles);
                    int ry = 12 - (int) Math.round(d * 4 / 6.0);
                    for (int y = vt + 2; y < ry; y++) k.set(u, y, v, p.tiles);
                    k.set(u, ry, v, p.tileStairs(out));
                    if (d == 3 && Math.floorMod(s, 8) == 4 && du != dv) k.set(u, vt, v, p.lanternH);
                } else if (d == 6) {
                    int base = du < dv ? CV0 + 6 : CU0 + 6;
                    int off = Math.floorMod(s - base, 4);
                    boolean corner = du == dv;
                    k.set(u, 0, v, p.pol);
                    for (int y = 1; y <= 7; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                    if (corner || off == 0) {
                        for (int y = 1; y <= 5; y++) k.set(u, y, v, p.pillar);
                        k.set(u, 6, v, p.chis);
                    } else for (int y = 1; y <= (off == 2 ? 6 : 5); y++) k.set(u, y, v, Kit.AIR);
                    k.set(u, 8, v, p.tileStairs(out));
                    if (corner) {
                        for (int y = 8; y <= 13; y++) k.set(u, y, v, p.pillar);
                        k.set(u, 14, v, p.crystal);
                        k.set(u, 15, v, p.clusterUp);
                    }
                } else {
                    BlockState f;
                    boolean path = Math.abs(u + 32) <= 1 || Math.abs(v - 48) <= 1;
                    if (path) f = (Math.abs(u + 32) == 0 || Math.abs(v - 48) == 0) ? p.purpur : p.pol;
                    else {
                        double n = k.noise(401, u, v, 4);
                        f = n < 0.3 ? p.rock : n < 0.55 ? p.calcite : n < 0.75 ? p.tuff : p.amethyst;
                        if (k.rnd(402, u, 0, v) < 0.04) f = p.schrabOre;
                        double r = k.rnd(403, u, 1, v);
                        if (r < 0.05) k.set(u, 1, v, p.clusterUp);
                        else if (r < 0.09) k.set(u, 1, v, p.budUp);
                        else if (r < 0.10) k.set(u, 0, v, p.frog);
                    }
                    k.set(u, 0, v, f);
                }
            }
        }
        // Quecksilberbrunnen in der Gartenmitte
        int fu = -32, fv = 48;
        if (k.touches(fu - 5, fv - 5, fu + 5, fv + 5)) for (int u = fu - 4; u <= fu + 4; u++) for (int v = fv - 4; v <= fv + 4; v++) {
            double r = Math.hypot(u - fu, v - fv);
            if (r > 4.0) continue;
            k.set(u, -1, v, p.pol);
            if (r <= 0.6) {
                column(k, u, v, 0, 7, p.crystal);
                k.set(u, 8, v, p.sea);
                k.set(u, 9, v, p.crystal);
                k.set(u, 10, v, p.clusterUp);
            } else if (r <= 2.5) k.set(u, 0, v, p.mercury);
            else {
                k.set(u, 0, v, p.pol);
                k.set(u, 1, v, r > 3.4 ? p.purpur : p.purpurSlab);
            }
        }
        // Riesenkristalle
        crystal(k, p, -40, 40, -4, -3, 20, 2.4);
        crystal(k, p, -23, 39, 4, -3, 24, 2.6);
        crystal(k, p, -41, 57, -3, 4, 17, 2.2);
        crystal(k, p, -24, 57, 3, 4, 21, 2.5);
        crystal(k, p, -39, 52, -1, 1, 9, 1.4);
        crystal(k, p, -25, 44, 1, 0, 8, 1.2);
        // Verbindungsgang vom suedlichen Seitenschiff
        if (k.touches(-33, 17, -27, 28)) {
            for (int v = 18; v <= 27; v++) {
                for (int u = -32; u <= -28; u++) {
                    int du = Math.abs(u + 30);
                    k.set(u, 0, v, du == 0 ? p.purpur : p.pol);
                    for (int y = -2; y < 0; y++) k.set(u, y, v, p.cobble);
                    if (du == 2) for (int y = 1; y <= 5; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                    else for (int y = 1; y <= (du == 0 ? 5 : 4); y++) k.set(u, y, v, Kit.AIR);
                    if (du == 1) k.set(u, 5, v, p.tiles);
                    if (du == 0) k.set(u, 6, v, p.tiles);
                    k.set(u, 7 - du / 2 - (du == 2 ? 1 : 0), v, du == 0 ? p.pol : p.tileStairs(u < -30 ? Direction.EAST : Direction.WEST));
                }
                if (v % 4 == 0) k.set(-30, 5, v, p.lanternH);
            }
            k.clear(-31, 1, 17, -29, 4, 17);
            k.clear(-31, 1, 28, -29, 4, 28);
        }
    }

    // ── Glockenturm ──

    private static int padX(int i) {
        return (int) Math.round(4 + 2.4 * Math.cos(Math.toRadians(180 + 72 * i)));
    }

    private static int padZ(int i) {
        return (int) Math.round(4 + 2.4 * Math.sin(Math.toRadians(180 + 72 * i)));
    }

    private void tower(Kit k, P p) {
        int cu = TCU, cv = TCV;
        if (!k.touches(cu - 9, cv - 9, cu + 9, cv + 9)) return;
        for (int u = Math.max(k.u0(), cu - 9); u <= Math.min(k.u1(), cu + 9); u++) for (int v = Math.max(k.v0(), cv - 9); v <= Math.min(k.v1(), cv + 9); v++) {
            int dx = u - cu, dz = v - cv, ax = Math.abs(dx), az = Math.abs(dz), m = Math.max(ax, az);
            if (m <= 6) {
                for (int y = -2; y <= 0; y++) k.set(u, y, v, m <= 4 && y == 0 ? ((dx + dz) % 2 == 0 ? p.pol : p.tiles) : p.pol);
                if (m >= 5) {
                    for (int y = 1; y <= 60; y++) k.set(u, y, v, (y == 15 || y == 28 || y == 44) && m == 6 ? p.pol : wallStone(k, p, u, y, v));
                    if (ax == az) for (int y = 1; y <= 60; y++) if (m == 6 && y % 4 == 0) k.set(u, y, v, p.purpur);
                    int t = ax == m && az != m ? dz : dx;
                    if (ax != az) {
                        if (t == 0) for (int y0 : new int[] {8, 20, 32}) for (int y = y0; y <= y0 + 4; y++) k.set(u, y, v, m == 5 ? p.purG : Kit.AIR);
                        if (Math.abs(t) >= 1 && Math.abs(t) <= 3) {
                            int top = Math.abs(t) == 2 ? 57 : 56;
                            for (int y = 45; y <= top; y++) k.set(u, y, v, Kit.AIR);
                            if (m == 5) k.set(u, 45, v, p.bwall);
                            if (m == 6) k.set(u, top + 1, v, p.purpur);
                        }
                        if (dz >= 5 && ax <= 1) for (int y = 1; y <= (ax == 0 ? 4 : 3); y++) k.set(u, y, v, Kit.AIR);
                        if (m == 5 && t == -3 && Math.floorMod(-ax - az, 1) == 0) for (int y = 6; y <= 40; y += 8) k.set(u, y, v, p.frog);
                    }
                    if (m == 6 && ((dx + dz) & 1) == 0) k.set(u, 61, v, p.pol);
                    else if (m == 6) k.set(u, 61, v, p.polWall);
                    if (ax >= 5 && az >= 5) {
                        for (int y = 61; y <= 67; y++) k.set(u, y, v, p.pillar);
                        if (ax == 6 && az == 6) {
                            k.set(u, 68, v, p.crystal);
                            k.set(u, 69, v, p.clusterUp);
                        }
                    }
                } else {
                    for (int y = 1; y <= 59; y++) k.set(u, y, v, Kit.AIR);
                    k.set(u, 60, v, p.tiles);
                }
                // Spitze
                double o = Math.max(m, (ax + az) * 0.7071);
                for (int y = 61; y <= 84; y++) {
                    double r = 5.2 * (85 - y) / 24.0;
                    if (o <= r && o > r - 1.4) k.set(u, y, v, y % 6 == 0 ? p.purpur : p.tiles);
                    else if (o <= r) k.set(u, y, v, Kit.AIR);
                    if (o > r && o <= r + 1 && (ax == az || ax == 0 || az == 0) && y % 5 == 0 && y < 80) k.set(u, y, v, p.budUp);
                }
            } else {
                boolean bt = (ax >= 7 && ax <= 8 && az >= 5 && az <= 6) || (az >= 7 && az <= 8 && ax >= 5 && ax <= 6);
                if (bt) {
                    int far = Math.max(ax, az), top = far == 7 ? 42 : 22;
                    Direction in = ax > az ? (dx > 0 ? Direction.WEST : Direction.EAST) : (dz > 0 ? Direction.NORTH : Direction.SOUTH);
                    for (int y = -1; y <= top; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                    k.set(u, top + 1, v, p.polStairs(in, false));
                    if (far == 8 && Math.min(ax, az) == 5) k.set(u, 9, v, p.cluster(in.getOpposite()));
                }
            }
        }
        k.set(cu, 85, cv, p.crystal);
        k.set(cu, 86, cv, p.rodUp);
        // Innenausbau (lokale Koordinaten x, z = 0..8)
        int ox = cu - 4, oz = cv - 4;
        // Treppenlauf A1 an der Ostwand nach Norden
        for (int z = 1; z <= 7; z++) for (int x = 7; x <= 8; x++) {
            int top = 8 - z;
            column(k, ox + x, oz + z, 1, top - 1, p.pol);
            k.set(ox + x, top, oz + z, Kit.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.NORTH, false));
        }
        for (int x = 7; x <= 8; x++) column(k, ox + x, oz, 1, 8, p.pol);
        // Treppenlauf A2 an der Nordwand nach Westen
        for (int x = 1; x <= 6; x++) for (int z = 0; z <= 1; z++) {
            int top = 9 + (6 - x);
            column(k, ox + x, oz + z, 1, top - 1, p.pol);
            k.set(ox + x, top, oz + z, Kit.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.WEST, false));
        }
        // Galerie G1 (Oberkante 14)
        for (int x = 0; x <= 8; x++) for (int z = 0; z <= 8; z++) {
            boolean g = x <= 1 || (z >= 7) || (x >= 7 && z >= 2);
            if (!g) continue;
            if (x >= 2 && x <= 6 && z <= 1) continue;
            k.set(ox + x, 14, oz + z, x == 0 || x == 8 || z == 8 || (x == 1 && z == 0) ? p.planks : p.logZ);
            if ((x == 0 || x == 8) && (z == 0 || z == 8)) column(k, ox + x, oz + z, 1, 13, p.logY);
        }
        k.set(ox, 15, oz + 8, p.lantern);
        // Balkenkoepfe (Parkour) Oberkante 15..26
        for (int i = 0; i < PADS; i++) {
            double th = Math.toRadians(180 + 72 * i);
            int px = padX(i), pz = padZ(i), top = 15 + i;
            boolean alongX = Math.abs(Math.cos(th)) > Math.abs(Math.sin(th));
            BlockState log = alongX ? p.logX : p.logZ;
            for (double s = 3.4; s <= 4.5; s += 1.0) {
                int bx = (int) Math.round(4 + s * Math.cos(th)), bz = (int) Math.round(4 + s * Math.sin(th));
                if (bx >= 0 && bx <= 8 && bz >= 0 && bz <= 8) k.set(ox + bx, top, oz + bz, log);
            }
            k.set(ox + px, top, oz + pz, i % 3 == 2 ? p.purpur : log);
        }
        // Zwischenboden F2 (Oberkante 27) auf der Osthaelfte
        for (int x = 5; x <= 8; x++) for (int z = 0; z <= 3; z++) k.set(ox + x, 27, oz + z, (z & 1) == 0 ? p.planks : p.logZ);
        k.set(ox + 5, 28, oz, p.lantern);
        // Leiter 1 (Ostwand) 28..33, Balken B1 (Oberkante 33), Leiter 2 (Westwand) 34..44
        k.ladder(ox + 8, 28, 33, oz + 2, Direction.EAST);
        for (int x = 1; x <= 7; x++) k.set(ox + x, 33, oz + 2, p.logX);
        k.set(ox + 4, 32, oz + 2, p.lanternH);
        k.ladder(ox, 34, 44, oz + 2, Direction.WEST);
        // Glockenstube: Boden 44
        for (int x = 0; x <= 8; x++) for (int z = 0; z <= 8; z++) {
            if (x == 0 && z == 2) continue;
            k.set(ox + x, 44, oz + z, (x == 4 || z == 4) ? p.purpur : ((x + z) & 1) == 0 ? p.pol : p.tiles);
        }
        // Glockenseil
        column(k, cu, cv, 29, 43, p.chain);
        // Glockenbalken und Riesenglocke
        for (int x = 0; x <= 8; x++) k.set(ox + x, 59, cv, p.logX);
        double[] rb = {3.6, 3.2, 2.9, 2.7, 2.6, 2.5, 2.2, 1.6};
        for (int y = 51; y <= 58; y++) {
            double r = rb[y - 51];
            for (int x = 0; x <= 8; x++) for (int z = 0; z <= 8; z++) {
                double d = Math.hypot(x - 4, z - 4);
                if (d > r + 0.3) continue;
                boolean shell = y == 58 || d > r - 1.0;
                k.set(ox + x, y, oz + z, shell ? (y == 51 || y == 55 ? p.copperCut : p.copper) : Kit.AIR);
            }
        }
        column(k, cu, cv, 53, 57, p.chain);
        k.set(cu, 52, cv, p.copperCut);
        k.set(cu - 3, 58, cv, p.bell);
        k.set(cu + 3, 58, cv, p.bell);
        for (int[] c : new int[][] {{1, 1}, {7, 1}, {1, 7}, {7, 7}}) {
            k.set(ox + c[0], 45, oz + c[1], p.pol);
            k.set(ox + c[0], 46, oz + c[1], p.candles);
        }
    }

    // ── Krypta ──

    private static final int[][] CRYPT_PLATS = {
            // u0, v0, u1, v1, top, art (0 fest, 1 Steg, 2 Sarkophag)
            {26, -12, 30, -8, -14, 0}, {31, -10, 35, -10, -14, 1}, {34, -12, 36, -12, -14, 0}, {38, -11, 40, -10, -13, 2},
            {43, -11, 43, -7, -14, 1}, {43, -4, 43, -2, -14, 1}, {45, -1, 47, 0, -13, 2}, {50, 0, 53, 0, -14, 1},
            {51, 3, 52, 4, -13, 2}, {56, -1, 57, 1, -13, 2}, {60, -4, 64, 4, -14, 0}, {62, -2, 64, 2, -13, 0}};

    private void crypt(Kit k, P p) {
        if (!k.touches(23, -16, 68, 16)) return;
        int ua = Math.max(k.u0(), 24), ub = Math.min(k.u1(), 68), va = Math.max(k.v0(), -15), vb = Math.min(k.v1(), 15);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            double r = u > 54 ? Math.hypot(u - 54, v) : Math.abs(v);
            boolean hall = u >= 26 && (u <= 54 ? Math.abs(v) <= 12 : r <= 11.5);
            boolean shell = u >= 24 && (u <= 54 ? Math.abs(v) <= 14 : r <= 13.5);
            if (!shell) continue;
            if (!hall) {
                for (int y = -23; y <= -2; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                continue;
            }
            k.set(u, -23, v, p.pol);
            k.set(u, -22, v, p.pol);
            k.set(u, -21, v, p.tiles);
            for (int y = -20; y <= -15; y++) k.set(u, y, v, p.mercury);
            int ceil;
            boolean rib;
            if (u <= 54) {
                double xu = Math.abs(Math.floorMod(u - 26, 7) - 3.5) / 3.5;
                int av = Math.abs(v);
                double xv = av <= 6 ? av / 6.0 : Math.abs(av - 9) / 3.0;
                xu = Math.min(1, xu);
                xv = Math.min(1, xv);
                ceil = -9 + (int) Math.floor(5 * Math.min(Math.sqrt(1 - xu * xu), Math.sqrt(1 - xv * xv)));
                rib = Math.abs(xu - xv) < 0.12 || xu > 0.93 || xv > 0.93;
            } else {
                ceil = -9 + (int) Math.floor(5 * Math.sqrt(Math.max(0, 1 - (r / 11.5) * (r / 11.5))));
                double th = Math.toDegrees(Math.atan2(v, u - 54));
                rib = Math.abs(Math.floorMod((int) Math.round(th), 30) - 15) > 12;
            }
            for (int y = -14; y <= ceil; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, ceil + 1, v, rib ? p.purpur : p.tiles);
            for (int y = ceil + 2; y <= -2; y++) k.set(u, y, v, p.brick);
            // Ossuarium-Nischen
            if (Math.abs(v) == 12 && u <= 54 && Math.floorMod(u, 3) == 0 && u > 31) {
                k.set(u, -11, v, p.bone);
                k.set(u, -10, v, Kit.facingAll(Blocks.SKELETON_SKULL, Direction.UP));
            }
        }
        // Saeulen
        for (int cu = 33; cu <= 54; cu += 7) for (int s = -1; s <= 1; s += 2) {
            int cv = s * 6;
            for (int u = cu - 1; u <= cu + 1; u++) for (int v = cv - 1; v <= cv + 1; v++) {
                boolean corner = u != cu && v != cv;
                for (int y = -21; y <= -4; y++) k.set(u, y, v, corner ? p.pillar : p.pol);
                k.set(u, -8, v, p.chis);
                if (!corner && (u != cu || v != cv)) k.set(u, -12, v, p.sea);
            }
        }
        // Stege und Sarkophage
        for (int[] pl : CRYPT_PLATS) {
            for (int u = pl[0]; u <= pl[2]; u++) for (int v = pl[1]; v <= pl[3]; v++) {
                int top = pl[4];
                if (pl[5] == 0) {
                    for (int y = -21; y < top; y++) k.set(u, y, v, p.pol);
                    k.set(u, top, v, ((u + v) & 1) == 0 ? p.tiles : p.pol);
                } else if (pl[5] == 1) {
                    if (Math.floorMod(u + v, 3) == 0) for (int y = -21; y < top; y++) k.set(u, y, v, p.pillar);
                    k.set(u, top, v, p.pol);
                } else {
                    for (int y = -21; y < top; y++) k.set(u, y, v, p.pol);
                    boolean end = (pl[2] - pl[0] >= pl[3] - pl[1]) ? (u == pl[0] || u == pl[2]) : (v == pl[1] || v == pl[3]);
                    k.set(u, top, v, end ? p.chis : p.purpur);
                    k.set(u, top - 1, v, p.purpur);
                }
            }
        }
        // Reliquienschrein am Apsisende
        for (int v = -1; v <= 1; v++) {
            for (int y = -13; y <= -8; y++) k.set(65, y, v, v == 0 && y == -10 ? p.sea : p.crystal);
            k.set(65, -7, v, p.amethyst);
        }
        k.set(65, -6, 0, p.clusterUp);
        k.set(62, -12, -2, p.candles);
        k.set(62, -12, 2, p.candles);
        k.set(60, -13, -4, p.candlesM);
        k.set(60, -13, 4, p.candlesM);
        // Laternen in den Gewoelbemitten
        for (int u = 29; u <= 50; u += 7) for (int v = -9; v <= 9; v += 9) k.set(u, -5, v, p.lanternH);
        k.set(54, -4, 0, p.lanternH);
        k.set(28, -13, -12, p.lantern);
        // Durchgang vom Treppenschacht
        k.clear(27, -13, -14, 29, -10, -13);
        k.fill(27, -9, -14, 29, -9, -13, p.purpur);
    }

    // ── Achskapelle mit Brunnenschacht (hinter dem Siegeltor) ──

    private void well(Kit k, P p) {
        if (k.touches(63, -5, 70, 5)) {
            for (int u = 64; u <= 68; u++) for (int v = -3; v <= 3; v++) {
                for (int y = -1; y <= 8; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                k.set(u, 9, v, Math.abs(v) == 3 ? p.polStairs(v > 0 ? Direction.NORTH : Direction.SOUTH, false) : p.tiles);
            }
            for (int u = 64; u <= 70; u++) for (int v = -2; v <= 2; v++) {
                k.set(u, 0, v, v == 0 ? p.purpur : p.pol);
                for (int y = 1; y <= 6; y++) k.set(u, y, v, Kit.AIR);
            }
            k.set(66, 6, 0, p.lanternH);
        }
        int cu = WELL_U;
        if (k.touches(cu - 10, -10, cu + 10, 10)) {
            for (int u = Math.max(k.u0(), cu - 9); u <= Math.min(k.u1(), cu + 9); u++) for (int v = Math.max(k.v0(), -9); v <= Math.min(k.v1(), 9); v++) {
                int du = Math.abs(u - cu), dv = Math.abs(v);
                double o = Math.max(Math.max(du, dv), (du + dv) * 0.7071), d = Math.hypot(du, dv);
                if (o > 9.2) continue;
                if (d > 7.5) {
                    for (int y = -49; y <= 14; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                    boolean face = (du <= 2 || dv <= 2 || Math.abs(du - dv) <= 1) && o > 8.4;
                    if (face && u > cu - 6) for (int y = 4; y <= 11; y++) k.set(u, y, v, y == 11 ? p.purpur : glass(p, y, 0, u + v));
                    if (o > 8.4) k.set(u, 14, v, p.pol);
                } else {
                    k.set(u, -49, v, p.pol);
                    k.set(u, -48, v, p.pol);
                    k.set(u, -47, v, d < 4 ? p.purpur : p.tiles);
                    for (int y = -46; y <= 14; y++) k.set(u, y, v, Kit.AIR);
                    if (o < 8 && d > 6.6) for (int y = -40; y <= 10; y += 10) k.set(u, y, v, p.frog);
                }
                for (int y = 15; y <= 29; y++) {
                    double rr = 9.2 - (y - 15) * 0.65;
                    if (o <= rr) k.set(u, y, v, o > rr - 1 && y % 3 == 0 ? p.purpur : p.tiles);
                }
            }
            double perRad = 7.0 / 2.2;
            double a0 = 180 - Math.toDegrees(47 / perRad);
            k.spiral(cu, 0, -47, 0, 2.5, 7.0, 2.2, a0, 4, p.pol);
            k.cylinder(cu, 0, -47, 14, 2.0, p.crystal);
            for (int y = -44; y <= 12; y += 6) k.cylinder(cu, 0, y, y, 1.0, p.sea);
            k.set(cu, 30, 0, p.crystal);
            k.set(cu, 31, 0, p.rodUp);
            k.clear(67, 1, -2, 70, 6, 2);
        }
    }

    // ── Hydra-Heiligtum ──

    private void sanctum(Kit k, P p) {
        int F = SF;
        if (!k.touches(SU - 28, -28, SU + 28, 28)) return;
        for (int u = Math.max(k.u0(), SU - 28); u <= Math.min(k.u1(), SU + 28); u++) for (int v = Math.max(k.v0(), -28); v <= Math.min(k.v1(), 28); v++) {
            double du = u - SU, r = Math.hypot(du, v);
            if (r > 27.5) continue;
            double ang = Math.toDegrees(Math.atan2(v, du));
            double aoff = Math.abs(Math.floorMod((int) Math.round(ang - 15), 30) - 15);   // 15 = Saeulen-/Rippenwinkel
            boolean onRib = (15 - aoff) * Math.PI / 180 * r < 0.8;
            for (int y = F - 4; y < F; y++) k.set(u, y, v, p.pol);
            boolean bridge = (Math.abs(v) <= 2) || (Math.abs(du) <= 2);
            if (r <= 18.5) k.set(u, F, v, sanctFloor(k, p, u, v, r, onRib, aoff));
            else if (r <= 21.5 && !bridge) {
                for (int y = F - 2; y <= F; y++) k.set(u, y, v, p.water);
            } else if (r <= 21.5) k.set(u, F, v, Math.abs(v) <= 0 || Math.abs(du) <= 0 ? p.purpur : p.pol);
            else k.set(u, F, v, onRib && r < 24.5 ? p.frog : (Math.floorMod(u + v, 2) == 0 ? p.tiles : p.pol));
            for (int y = F + 1; y <= -22; y++) {
                double h = y + 41;
                boolean inside = y <= -41 ? r <= 24.5 : (r / 24.5) * (r / 24.5) + (h / 16) * (h / 16) <= 1;
                if (inside) {
                    k.set(u, y, v, Kit.AIR);
                    continue;
                }
                boolean shell = y <= -41 ? r <= 27.5 : (r / 27.5) * (r / 27.5) + (h / 19) * (h / 19) <= 1;
                if (!shell) continue;
                BlockState s;
                if (y <= -41) s = onRib && r < 25.6 ? p.pillar : (y == -41 || y == F + 1 ? p.chis : wallStone(k, p, u, y, v));
                else if (onRib) s = p.purpur;
                else if (Math.floorMod((int) h, 5) == 0) s = p.pol;
                else if (k.rnd(501, u, y, v) < 0.07) s = p.frog;
                else s = ((Math.floorMod((int) Math.floor((ang + 180) / 30), 2) + (int) h / 5) & 1) == 0 ? p.amethyst : p.crystal;
                k.set(u, y, v, s);
            }
        }
        // Saeulenkranz (12 Saeulen, r 14)
        for (int i = 0; i < 12; i++) {
            double a = Math.toRadians(15 + 30 * i);
            double cu = SU + 14 * Math.cos(a), cv = 14 * Math.sin(a);
            k.cylinder(cu, cv, F + 1, F + 2, 1.8, p.pol);
            k.cylinder(cu, cv, F + 3, -29, 1.3, p.pillar);
            k.cylinder(cu, cv, F + 8, F + 8, 1.3, p.frog);
            k.cylinder(cu, cv, -30, -28, 1.7, p.chis);
            k.cylinder(cu, cv, F + 3, F + 3, 1.6, p.purpurSlab);
        }
        // Kristall-Leuchter in der Kuppel
        column(k, SU, 0, -30, -25, p.chain);
        for (int y = -36; y <= -31; y++) k.cylinder(SU, 0, y, y, (y + 37) * 0.45, y == -33 ? p.sea : p.crystal);
        // Obelisken am Eingang
        for (int s = -1; s <= 1; s += 2) {
            column(k, SU + 22, s * 4, F + 1, F + 6, p.crystal);
            k.set(SU + 22, F + 7, s * 4, p.clusterUp);
        }
    }

    private static BlockState sanctFloor(Kit k, P p, int u, int v, double r, boolean onRib, double aoff) {
        if (r < 2.5) return p.crystal;
        if (r < 3.5) return p.amethyst;
        if ((r >= 7 && r < 8) || (r >= 12.5 && r < 13.5)) return p.purpur;
        if (r >= 17.5) return aoff < 1.5 ? p.frog : p.chis;
        if (onRib && r < 17.5) return Math.abs(r - 10) < 0.8 ? p.frog : p.amethyst;
        return ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles;
    }

    // ── Prozessionsgang ──

    private void processional(Kit k, P p) {
        int F = SF;
        if (!k.touches(2, -8, 72, 8)) return;
        for (int u = Math.max(k.u0(), 2); u <= Math.min(k.u1(), 71); u++) for (int v = Math.max(k.v0(), -7); v <= Math.min(k.v1(), 7); v++) {
            int av = Math.abs(v);
            double vt = -42 + Math.sqrt(Math.max(0, 100 - (av + 5) * (av + 5)));
            boolean shell = u >= 7 && u <= 68;
            if (av <= 4) {
                if (shell) for (int y = F - 3; y < F; y++) k.set(u, y, v, p.pol);
                BlockState f = av <= 1 ? (Math.floorMod(u, 6) == 3 && av == 0 ? p.amethyst : p.purpur) : ((Math.floorMod(u, 4) < 2) ^ (Math.floorMod(v, 4) < 2)) ? p.pol : p.tiles;
                if (av == 4 && u >= 8 && u <= 66) f = p.mercury;
                k.set(u, F, v, f);
                for (int y = F + 1; y <= vt; y++) k.set(u, y, v, Kit.AIR);
                if (shell) {
                    int t = (int) Math.floor(vt);
                    k.set(u, t + 1, v, Math.floorMod(u, 6) == 0 || av == 0 ? p.purpur : p.tiles);
                    for (int y = t + 2; y <= -31; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                }
            } else if (shell && av <= 6) {
                for (int y = F - 3; y <= -31; y++) k.set(u, y, v, wallStone(k, p, u, y, v));
                if (av == 5) {
                    int m = Math.floorMod(u, 6);
                    if (m == 0) {
                        for (int y = F + 1; y <= F + 5; y++) k.set(u, y, v, p.pillar);
                        k.set(u, F + 3, v, p.frog);
                        k.set(u, F + 6, v, p.chis);
                    } else if (m == 3) {
                        for (int y = F + 1; y <= F + 3; y++) k.set(u, y, v, Kit.AIR);
                        k.set(u, F + 1, v, p.crystal);
                        k.set(u, F + 2, v, p.clusterUp);
                        k.set(u, F + 4, v, p.purpur);
                    }
                }
            }
        }
        for (int u = 9; u <= 63; u += 6) {
            column(k, u, 0, -38, -34, p.chain);
            k.set(u, -39, 0, p.lanternH);
        }
        // Portalbogen zum Heiligtum
        for (int v = -5; v <= 5; v++) for (int y = F + 1; y <= -32; y++) {
            int av = Math.abs(v);
            double vt = -42 + Math.sqrt(Math.max(0, 100 - (av + 5) * (av + 5)));
            if (av == 5 || y > vt) k.set(7, y, v, av == 5 ? p.pillar : p.purpur);
        }
        k.clear(66, F + 1, -2, 71, F + 6, 2);
    }

    // ── Plan ──

    private void plan(Kit k) {
        int ox = TCU - 4, oz = TCV - 4;
        // Schluessel
        k.keyChest(TCU, 45, TCV, Direction.SOUTH);
        k.keyChest(64, -12, 0, Direction.WEST);
        k.elite(-32, 1, 41, MANTIS);
        // Boss
        k.boss(SU, SF + 1, 0);
        k.bossChest(SU - 16, SF + 1, -3, Direction.EAST);
        k.bossChest(SU - 16, SF + 1, 3, Direction.EAST);
        // Beute
        k.loot(-46, 1, -15, Direction.SOUTH);
        k.barrel(ox + 8, 28, oz + 1);
        k.loot(52, -12, 4, Direction.NORTH);
        k.loot(-50, 1, 66, Direction.EAST);
        k.barrel(-14, 1, 30);
        k.loot(-30, 1, -50, Direction.SOUTH);
        k.barrel(-40, 1, -60);
        k.loot(20, 1, 34, Direction.WEST);
        // Spawner
        k.spawner(ox + 8, 15, oz + 8, SPITTER);
        k.spawner(35, -13, -12, MANTIS);
        k.spawner(-50, 1, 30, MANTIS);
        k.spawner(-14, 1, 66, SPITTER);
        k.spawner(14, 1, -30, SPITTER);
        k.spawner(-30, 1, -13, MANTIS);
        k.spawner(30, SF + 1, 3, MANTIS);
        k.spawner(50, SF + 1, -3, SPITTER);
        // Bewohner
        k.mob(-80, 1, 12, MANTIS);
        k.mob(-80, 1, -12, MANTIS);
        k.mob(-40, 1, 0, MANTIS);
        k.mob(-20, 1, 12, SPITTER);
        k.mob(-5, 1, -12, MANTIS);
        k.mob(14, 1, 25, SPITTER);
        k.mob(35, 1, 0, MANTIS);
        k.mob(ox + 6, 28, oz + 1, MANTIS);
        k.mob(-40, 1, 46, MANTIS);
        k.mob(-24, 1, 52, MANTIS);
        k.mob(-49, 1, 48, SPITTER);
        k.mob(-25, 1, -55, MANTIS);
        k.mob(-35, 1, -48, SPITTER);
        k.mob(28, -13, -10, MANTIS);
        k.mob(61, -13, 3, SPITTER);
        k.mob(20, SF + 1, 0, MANTIS);
        k.mob(45, SF + 1, 0, SPITTER);
    }
}
