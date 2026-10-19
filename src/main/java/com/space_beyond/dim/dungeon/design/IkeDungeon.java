package com.space_beyond.dim.dungeon.design;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

import com.hbm_m.block.ModBlocks;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Ike - "Monolith-Nekropole". Auf der grauen Oberflaeche ein Ring schraeger schwarzer Monolithen um einen Obelisken,
 * an dem eine Wendeltreppe in eine riesige Hoehle hinabfuehrt. Dort liegt eine Totenstadt: Gassen mit Grabhaeusern,
 * Pyramidengraebern und Graeberfeldern, eine tiefe Spalte mit Bruecken und in der Mitte eine Stufenpyramide (Zikkurat).
 * <pre>
 *  y   0..30   Monolithenring, Obelisk, Schacht (0, 62)
 *  y -44       Hoehlenboden (Fuesse -43), Hoehlendecke bis ca. -6
 *  y -24       Zikkurat-Plattform mit Tempel und Siegeltor (Front Sued)
 *  y -64..-25  Pyramidenhalle (Arena) im Inneren der Zikkurat, Wendeltreppe um eine Monolithsaeule
 *  Schluessel 1  Ost:  Krypta ueber dem Abgrund (Parkour auf Saeulenstuempfen/Sarkophagdeckeln, Grube bis -66)
 *  Schluessel 2  West: Katakomben unter der Stadt (zwei Labyrinth-Ebenen, Boden -50 und -57)
 *  Schluessel 3  Nord: grosses Mausoleum mit Siegelwaechter zwischen Statuen
 * </pre>
 */
public final class IkeDungeon extends CustomDungeon {

    static final int FLOOR = -44, F = -43, NONE = Integer.MIN_VALUE;
    static final double RU = 84, RV = 80, TAU = Math.PI * 2;
    static final int HMAX = 38;
    static final String SHADE = "space_beyond:void_shade", WARDEN = "space_beyond:tomb_warden";
    /** Eingangsschacht bei (0, EV). */
    static final int EV = 62;
    /** Katakomben: Gitter-Ursprung, Zellen je Seite, Bodenhoehe der beiden Ebenen. */
    static final int LU = -78, LV = -56, LN = 10, L1 = -50, L2 = -57;
    /** Bauzonen ohne Grabhaeuser: Zikkurat+Platz, Schacht, Krypta, Mausoleum, Beinhaus. */
    static final int[][] ZONES = {{-37, -37, 37, 38}, {-15, 47, 15, 80}, {36, -18, 80, 18}, {-26, -80, 26, -37}, {-41, -25, -28, -12}};
    /** Parkour in der Krypta: u0, v0, u1, v1, oberster Block. */
    static final int[][] PK = {{48, -1, 49, 1, -44}, {52, 1, 54, 2, -43}, {57, 4, 57, 4, -43}, {60, 5, 61, 7, -42}, {63, 2, 65, 3, -42},
            {66, -1, 67, 0, -41}, {63, -5, 64, -4, -41}, {66, -8, 67, -7, -40}, {69, -5, 69, -4, -40}};

    @Override public Stage stage() { return Stage.IKE; }
    @Override public int radius() { return 92; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.ike.IkeBlocks.IKE_REGOLITH.get()); }
    @Override public BlockState filler() { return Kit.b(com.space_m.dim.ike.IkeBlocks.IKE_STONE.get()); }
    @Override public int minY() { return -66; }
    @Override public int maxY() { return 32; }
    @Override public int minBase() { return 69; }
    @Override public int[] entry() { return new int[] {0, 1, 86}; }
    @Override public String boss() { return "space_beyond:monolith_keeper"; }

    /** Palette (erst beim ersten Bau initialisiert, dann sind alle Register befuellt). */
    private static final class P {
        static final BlockState IKE = Kit.b(com.space_m.dim.ike.IkeBlocks.IKE_STONE.get());
        static final BlockState REG = Kit.b(com.space_m.dim.ike.IkeBlocks.IKE_REGOLITH.get());
        static final BlockState BROM = Kit.b(com.space_m.dim.ike.IkeBlocks.BROMINE_BLOCK.get());
        static final BlockState DS = Kit.b(Blocks.DEEPSLATE), CDS = Kit.b(Blocks.COBBLED_DEEPSLATE), PDS = Kit.b(Blocks.POLISHED_DEEPSLATE),
                DSB = Kit.b(Blocks.DEEPSLATE_BRICKS), CDSB = Kit.b(Blocks.CRACKED_DEEPSLATE_BRICKS), DST = Kit.b(Blocks.DEEPSLATE_TILES),
                CDST = Kit.b(Blocks.CRACKED_DEEPSLATE_TILES), CHDS = Kit.b(Blocks.CHISELED_DEEPSLATE), TUFF = Kit.b(Blocks.TUFF);
        static final BlockState BS = Kit.b(Blocks.BLACKSTONE), PBS = Kit.b(Blocks.POLISHED_BLACKSTONE), PBB = Kit.b(Blocks.POLISHED_BLACKSTONE_BRICKS),
                CPBB = Kit.b(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS), CHPB = Kit.b(Blocks.CHISELED_POLISHED_BLACKSTONE), GILD = Kit.b(Blocks.GILDED_BLACKSTONE);
        static final BlockState OBS = Kit.b(Blocks.OBSIDIAN), COBS = Kit.b(Blocks.CRYING_OBSIDIAN), BOBS = Kit.b(ModBlocks.BRICK_OBSIDIAN.get()),
                BASB = Kit.b(ModBlocks.BASALT_BRICK.get()), BAST = Kit.b(ModBlocks.BASALT_TILES.get()), BASP = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y),
                SBAS = Kit.b(Blocks.SMOOTH_BASALT), BAS = Kit.axis(Blocks.BASALT, Direction.Axis.Y), DEPB = Kit.b(ModBlocks.DEPTH_BRICK.get()),
                DEPT = Kit.b(ModBlocks.DEPTH_TILES.get()), METB = Kit.b(ModBlocks.METEOR_BRICK.get()), METP = Kit.b(ModBlocks.METEOR_POLISHED.get());
        static final BlockState AME = Kit.b(Blocks.AMETHYST_BLOCK), BUD = Kit.b(Blocks.BUDDING_AMETHYST), CLU = Kit.b(Blocks.AMETHYST_CLUSTER),
                CLUD = Kit.facingAll(Blocks.AMETHYST_CLUSTER, Direction.DOWN), GLASS = Kit.b(Blocks.PURPLE_STAINED_GLASS), FROG = Kit.b(Blocks.PEARLESCENT_FROGLIGHT),
                BONE = Kit.b(Blocks.BONE_BLOCK), BONEX = Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.X), BONEZ = Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.Z),
                SOUL = Kit.b(Blocks.SOUL_SOIL), SKULL = Kit.b(Blocks.SKELETON_SKULL), WSKULL = Kit.b(Blocks.WITHER_SKELETON_SKULL), WEB = Kit.b(Blocks.COBWEB),
                CHAIN = Kit.b(Blocks.CHAIN), CARPET = Kit.b(Blocks.PURPLE_CARPET);
        static final BlockState SL = Kit.lantern(Blocks.SOUL_LANTERN, false), SLH = Kit.lantern(Blocks.SOUL_LANTERN, true);
        static final BlockState PBW = Kit.b(Blocks.POLISHED_BLACKSTONE_WALL), DSW = Kit.b(Blocks.DEEPSLATE_BRICK_WALL), DTW = Kit.b(Blocks.DEEPSLATE_TILE_WALL);
        static final BlockState CANDLE = candle(3), CANDLE1 = candle(1), PSLAB = Kit.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false);

        static BlockState candle(int n) {
            BlockState s = CityPalette.with(Kit.b(Blocks.PURPLE_CANDLE), BlockStateProperties.CANDLES, n);
            return CityPalette.with(s, BlockStateProperties.LIT, true);
        }
    }

    @Override
    protected void build(Kit k) {
        cave(k);
        stalactites(k);
        streets(k);
        lots(k);
        plaza(k);
        crevice(k);
        bridges(k);
        ziggurat(k);
        hall(k);
        temple(k);
        krypta(k);
        mausoleum(k);
        catacombs(k);
        ossuary(k);
        entrance(k);
        surfaceRing(k);
        cityMobs(k);
    }

    // ── Hilfen: Hoehle, Spalte, Zonen ───────────────────────────────────────

    /** Hoechste Luft der Hoehle ueber (u, v) oder NONE ausserhalb. */
    private int caveTop(Kit k, int u, int v) {
        double du = u / RU, dv = v / RV;
        double d = Math.sqrt(du * du + dv * dv) * (1 + (k.noise(11, u, v, 16) - 0.5) * 0.12);
        if (d >= 1) return NONE;
        double d2 = d * d;
        int top = FLOOR + (int) Math.round(HMAX * Math.sqrt(1 - d2 * d2) + (k.noise(12, u, v, 6) - 0.5) * 4);
        if (top > -6) top = -6;
        return top < FLOOR + 4 ? NONE : top;
    }

    private double crevC(Kit k, int u) {
        return 46 + 3 * Math.sin(u / 11.0) + (k.noise(13, u, 0, 9) - 0.5) * 3;
    }

    private double crevW(Kit k, int u) {
        double t = Math.min(1, Math.max(0, (62 - Math.abs(u)) / 10.0));
        return (2.6 + k.noise(14, u, 7, 7) * 1.6) * t;
    }

    private boolean nearCrev(Kit k, int u, int v, double margin) {
        double w = crevW(k, u);
        return w > 0.7 && Math.abs(v - crevC(k, u)) <= w + margin;
    }

    static boolean inZone(int ua, int va, int ub, int vb) {
        for (int[] z : ZONES) if (ub >= z[0] && ua <= z[2] && vb >= z[1] && va <= z[3]) return true;
        return false;
    }

    static boolean streetLine(int x) {
        int m = Math.floorMod(x, 14);
        return m <= 1 || m >= 13;
    }

    static double angle(double du, double dv, double a0deg) {
        double a = Math.atan2(dv, du) - Math.toRadians(a0deg);
        return ((a % TAU) + TAU) % TAU;
    }

    private BlockState rockMat(Kit k, int u, int y, int v) {
        if (k.noise(22, u + y * 3, v - y * 2, 5) > 0.79) return k.rnd(26, u, y, v) < 0.8 ? P.AME : P.BUD;
        int s = Math.floorMod((int) Math.floor(y + k.noise(21, u, v, 11) * 7), 10);
        if (s < 4) return P.IKE;
        if (s < 6) return k.rnd(27, u, y, v) < 0.7 ? P.DS : P.CDS;
        if (s < 7) return P.TUFF;
        if (s < 9) return P.SBAS;
        return P.BAS;
    }

    private BlockState floorMat(Kit k, int u, int v) {
        double n = k.noise(28, u, v, 6), r = k.rnd(29, u, 0, v);
        if (n < 0.35) return r < 0.7 ? P.REG : P.IKE;
        if (n < 0.65) return r < 0.6 ? P.IKE : P.CDS;
        return r < 0.5 ? P.DS : r < 0.8 ? P.CDS : P.TUFF;
    }

    /** Kette mit Seelenlaterne von yTop abwaerts. */
    private void hang(Kit k, int u, int v, int yTop, int len) {
        for (int y = yTop; y > yTop - len; y--) k.set(u, y, v, P.CHAIN);
        k.set(u, yTop - len, v, P.SLH);
    }

    /** Laternenpfahl auf Fusshoehe y. */
    private void post(Kit k, int u, int y, int v) {
        k.set(u, y, v, P.CHPB);
        k.set(u, y + 1, v, P.PBW);
        k.set(u, y + 2, v, P.PBW);
        k.set(u, y + 3, v, P.SL);
    }

    /**
     * Wendeltreppe (abwaerts im Uhrzeigersinn von oben gesehen): oberste Stufe bei Winkel a0 auf yTop, je Radiant
     * {@code perRad} Bloecke tiefer bis yBot. Lauffläche rMin..rRail, Gelaender (Mauer) aussen ueber rOut.
     */
    private void helix(Kit k, double cu, double cv, int yTop, int yBot, double rMin, double rOut, double rRail, double a0, double perRad, BlockState step, BlockState rail) {
        int ir = (int) Math.ceil(rRail) + 1;
        if (!k.touches((int) cu - ir, (int) cv - ir, (int) cu + ir, (int) cv + ir)) return;
        for (int u = Math.max((int) Math.floor(cu - ir), k.u0()); u <= Math.min((int) Math.ceil(cu + ir), k.u1()); u++) {
            for (int v = Math.max((int) Math.floor(cv - ir), k.v0()); v <= Math.min((int) Math.ceil(cv + ir), k.v1()); v++) {
                double d = Math.hypot(u - cu, v - cv);
                if (d < rMin || d > rRail + 0.01) continue;
                double ang = angle(u - cu, v - cv, a0);
                for (double a = ang; ; a += TAU) {
                    int y = yTop - (int) Math.floor(a * perRad);
                    if (y < yBot) break;
                    k.set(u, y, v, step);
                    for (int h = 1; h <= 3; h++) k.set(u, y + h, v, Kit.AIR);
                    if (d > rOut && y < yTop - 1 && y > yBot + 1) k.set(u, y + 1, v, rail);
                }
            }
        }
    }

    // ── Hoehle ──────────────────────────────────────────────────────────────

    private void cave(Kit k) {
        int ua = Math.max(k.u0(), -91), ub = Math.min(k.u1(), 91), va = Math.max(k.v0(), -91), vb = Math.min(k.v1(), 91);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            int t = caveTop(k, u, v);
            if (t != NONE) {
                for (int y = FLOOR + 1; y <= t; y++) k.set(u, y, v, Kit.AIR);
                k.set(u, FLOOR, v, floorMat(k, u, v));
                BlockState c1 = rockMat(k, u, t + 1, v);
                k.set(u, t + 1, v, c1);
                k.set(u, t + 2, v, rockMat(k, u, t + 2, v));
                if (c1 == P.AME && k.rnd(23, u, t, v) < 0.6) k.set(u, t, v, P.CLUD);
                double r = k.rnd(24, u, 0, v);
                boolean street = streetLine(u) || streetLine(v);
                if (t < FLOOR + 14 && r < 0.10) k.set(u, F, v, P.CLU);
                else if (!street && r < 0.006) k.set(u, F, v, P.SKULL);
                else if (!street && r < 0.012) k.set(u, F, v, k.rnd(25, u, 0, v) < 0.5 ? P.BONEX : P.BONEZ);
            } else {
                int tn = Math.max(Math.max(caveTop(k, u + 1, v), caveTop(k, u - 1, v)), Math.max(caveTop(k, u, v + 1), caveTop(k, u, v - 1)));
                if (tn != NONE) for (int y = FLOOR; y <= tn + 2; y++) k.set(u, y, v, rockMat(k, u, y, v));
            }
        }
    }

    private void stalactites(Kit k) {
        for (int gi = -10; gi <= 10; gi++) for (int gj = -10; gj <= 10; gj++) {
            int cu = gi * 9 + (int) (k.rnd(31, gi, 0, gj) * 5) - 2, cv = gj * 9 + (int) (k.rnd(32, gi, 0, gj) * 5) - 2;
            if (!k.touches(cu - 4, cv - 4, cu + 4, cv + 4)) continue;
            if (k.rnd(33, gi, 0, gj) < 0.3) continue;
            if (Math.abs(cu) <= 36 && Math.abs(cv) <= 36) continue;
            if (Math.hypot(cu, cv - EV) < 13) continue;
            int t = caveTop(k, cu, cv);
            if (t == NONE) continue;
            int lim = inZone(cu - 3, cv - 3, cu + 3, cv + 3) ? -21 : -29;
            int len = Math.min(3 + (int) (k.rnd(34, gi, 0, gj) * 10), t - lim);
            if (len < 3) continue;
            double r0 = 1.3 + k.rnd(35, gi, 0, gj) * 1.5;
            int ir = (int) Math.ceil(r0);
            for (int u = cu - ir; u <= cu + ir; u++) for (int v = cv - ir; v <= cv + ir; v++) {
                if (!k.col(u, v)) continue;
                double d = Math.hypot(u - cu, v - cv);
                if (d > r0 + 0.3) continue;
                int depth = (int) Math.round(len * (1 - d / (r0 + 0.6)));
                for (int y = t + 2; y >= t + 1 - depth; y--) k.set(u, y, v, rockMat(k, u, y, v));
            }
            if (k.rnd(36, gi, 0, gj) < 0.65) k.set(cu, t - len, cv, P.CLUD);
        }
    }

    // ── Totenstadt ──────────────────────────────────────────────────────────

    private void streets(Kit k) {
        int ua = Math.max(k.u0(), -90), ub = Math.min(k.u1(), 90), va = Math.max(k.v0(), -90), vb = Math.min(k.v1(), 90);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            boolean su = streetLine(u), sv = streetLine(v);
            if (!su && !sv) continue;
            int t = caveTop(k, u, v);
            if (t == NONE) continue;
            boolean center = (su && Math.floorMod(u, 14) == 0) || (sv && Math.floorMod(v, 14) == 0);
            BlockState s;
            if (su && sv) s = ((u + v) & 1) == 0 ? P.PBB : P.DST;
            else if (center) s = k.rnd(42, u, 0, v) < 0.1 ? P.CDST : P.PDS;
            else s = k.rnd(41, u, 0, v) < 0.18 ? P.CDST : P.DST;
            k.set(u, FLOOR, v, s);
            if (t > FLOOR + 1) k.set(u, F, v, Kit.AIR);
        }
        for (int a = -7; a <= 6; a++) for (int b = -7; b <= 6; b++) {
            int u = 14 * a + 2, v = 14 * b + 2;
            if (!k.col(u, v) || inZone(u, v, u, v) || nearCrev(k, u, v, 2)) continue;
            int t = caveTop(k, u, v);
            if (t == NONE || t < FLOOR + 8) continue;
            post(k, u, F, v);
        }
    }

    private boolean lotOk(Kit k, int u0, int v0, int u1, int v1) {
        if (inZone(u0 - 1, v0 - 1, u1 + 1, v1 + 1)) return false;
        int[][] pts = {{u0, v0}, {u1, v0}, {u0, v1}, {u1, v1}, {(u0 + u1) / 2, (v0 + v1) / 2}};
        for (int[] p : pts) {
            int t = caveTop(k, p[0], p[1]);
            if (t == NONE || t < FLOOR + 13) return false;
        }
        for (int u = u0 - 2; u <= u1 + 2; u++) {
            double w = crevW(k, u);
            if (w <= 0.7) continue;
            double c = crevC(k, u);
            if (c + w >= v0 - 3 && c - w <= v1 + 3) return false;
        }
        return true;
    }

    private void lots(Kit k) {
        int barrels = 0;
        for (int a = -7; a <= 6; a++) for (int b = -7; b <= 6; b++) {
            int u0 = 14 * a + 2, v0 = 14 * b + 2, u1 = u0 + 10, v1 = v0 + 10;
            if (Math.max(Math.abs(u0), Math.abs(u1)) > 88 || Math.max(Math.abs(v0), Math.abs(v1)) > 88) continue;
            if (!lotOk(k, u0, v0, u1, v1)) {
                if (!inZone(u0 - 1, v0 - 1, u1 + 1, v1 + 1) && k.touches(u0 - 1, v0 - 1, u1 + 1, v1 + 1)) rimField(k, a, b, u0, v0);
                continue;
            }
            double r = k.rnd(201, a, 0, b);
            int type = r < 0.5 ? 0 : r < 0.66 ? 1 : r < 0.84 ? 2 : 3;
            int w = k.rnd(202, a, 0, b) < 0.5 ? 7 : 9, d = k.rnd(203, a, 0, b) < 0.5 ? 7 : 9;
            if (type == 1) d = w;
            int ju = w == 7 ? (int) (k.rnd(204, a, 0, b) * 3) - 1 : 0, jv = d == 7 ? (int) (k.rnd(205, a, 0, b) * 3) - 1 : 0;
            int hu0 = u0 + (11 - w) / 2 + ju, hv0 = v0 + (11 - d) / 2 + jv;
            boolean south = k.rnd(206, a, 0, b) < 0.5;
            if (type == 0 && barrels < 3 && k.rnd(207, a, 0, b) < 0.45) {
                barrels++;
                k.barrel(hu0 + 1, F, south ? hv0 + 1 : hv0 + d - 2);
            }
            if (!k.touches(u0 - 1, v0 - 1, u1 + 1, v1 + 1)) continue;
            switch (type) {
                case 0 -> house(k, a, b, hu0, hv0, w, d, south, false);
                case 1 -> pyramidTomb(k, hu0, hv0, w, south);
                case 2 -> graveField(k, a, b, u0, v0);
                default -> house(k, a, b, hu0, hv0, w, d, south, true);
            }
        }
    }

    /** Randgrund (Lot am Hoehlenrand/an der Spalte): Grabsteine, liegende Sarkophage, kleiner Obelisk wo Platz ist. */
    private void rimField(Kit k, int a, int b, int u0, int v0) {
        int cx = u0 + 5, cz = v0 + 5;
        for (int gu = u0 + 1; gu <= u0 + 9; gu += 2) for (int gv = v0 + 1; gv <= v0 + 8; gv += 3) {
            if (Math.abs(gu - cx) <= 1 && Math.abs(gv - cz) <= 2) continue;
            int t0 = caveTop(k, gu, gv), t1 = caveTop(k, gu, gv + 1);
            if (t0 == NONE || t1 == NONE || t0 < FLOOR + 5 || t1 < FLOOR + 4 || nearCrev(k, gu, gv, 2.5) || nearCrev(k, gu, gv + 1, 2.5)) continue;
            double r = k.rnd(231, gu, 0, gv);
            if (r < 0.15) continue;
            if (r < 0.3) {
                k.set(gu, F, gv, P.CHPB);
                k.set(gu, F, gv + 1, P.PBS);
                k.set(gu, F + 1, gv, P.PSLAB);
                k.set(gu, F + 1, gv + 1, P.PSLAB);
                continue;
            }
            k.set(gu, F, gv, r < 0.55 ? P.CHDS : r < 0.8 ? P.PBS : P.CDSB);
            if (r > 0.85 && t0 >= FLOOR + 6) k.set(gu, F + 1, gv, P.PBW);
            else if (r < 0.4) k.set(gu, F + 1, gv, P.SKULL);
            k.set(gu, FLOOR, gv + 1, P.SOUL);
            double r2 = k.rnd(232, gu, 0, gv);
            if (r2 < 0.3) k.set(gu, F, gv + 1, P.CANDLE1);
            else if (r2 < 0.42) k.set(gu, F, gv + 1, P.CLU);
        }
        int tc = caveTop(k, cx, cz);
        if (tc != NONE && tc >= FLOOR + 10 && !nearCrev(k, cx, cz, 3)) {
            int h = Math.min(9, tc - F - 5);
            k.fill(cx - 1, F, cz - 1, cx + 1, F, cz + 1, P.PBB);
            for (int y = F + 1; y <= F + h; y++) k.set(cx, y, cz, y == F + h - 1 ? P.COBS : P.OBS);
            k.set(cx, F + h + 1, cz, P.CLU);
            k.set(cx + 1, F + 1, cz, P.SL);
            k.set(cx - 1, F + 1, cz, P.CANDLE);
        }
    }

    /** Grabhaus mit Satteldach, Sarkophag und Seelenlaterne (ruin: verfallen). */
    private void house(Kit k, int a, int b, int hu0, int hv0, int w, int d, boolean south, boolean ruin) {
        int hu1 = hu0 + w - 1, hv1 = hv0 + d - 1, cu = hu0 + w / 2, cvm = hv0 + d / 2;
        int h = 4 + (int) (k.rnd(210, a, 0, b) * 2);
        int mat = (int) (k.rnd(211, a, 0, b) * 4);
        BlockState wall = mat == 0 ? P.DSB : mat == 1 ? P.PBB : mat == 2 ? P.DEPB : P.BASB;
        BlockState crack = mat == 0 ? P.CDSB : mat == 1 ? P.CPBB : mat == 2 ? P.DEPT : P.BAST;
        boolean tiles = k.rnd(212, a, 0, b) < 0.5;
        Block roofSt = tiles ? Blocks.DEEPSLATE_TILE_STAIRS : Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS;
        BlockState ridge = tiles ? P.DST : P.PBB;
        k.fill(hu0 - 1, FLOOR, hv0 - 1, hu1 + 1, FLOOR, hv1 + 1, P.PBS);
        for (int u = Math.max(hu0, k.u0()); u <= Math.min(hu1, k.u1()); u++) for (int v = Math.max(hv0, k.v0()); v <= Math.min(hv1, k.v1()); v++) {
            boolean eu = u == hu0 || u == hu1, ev = v == hv0 || v == hv1;
            if (!eu && !ev) {
                k.set(u, FLOOR, v, ((u + v) & 1) == 0 ? P.PDS : P.DST);
                for (int y = F; y < F + h; y++) k.set(u, y, v, Kit.AIR);
                continue;
            }
            for (int y = F; y < F + h; y++) {
                BlockState s = eu && ev ? P.BASP : y == F ? P.PBS : y == F + h - 1 ? P.CHDS : k.rnd(213, u, y, v) < 0.2 ? crack : wall;
                if (ruin && !(eu && ev) && y > F + 1 && k.rnd(214, u, y, v) < 0.15 + 0.15 * (y - F)) s = Kit.AIR;
                k.set(u, y, v, s);
            }
        }
        // Tuer, Fenster
        int dv = south ? hv1 : hv0, out = south ? 1 : -1, dw = w == 9 ? 1 : 0;
        k.clear(cu - dw, F, dv, cu + dw, F + 1 + dw, dv);
        if (!ruin) {
            k.fill(cu - dw - 1, F + 2 + dw, dv, cu + dw + 1, F + 2 + dw, dv, P.CHPB);
            k.set(hu0, F + 2, cvm, P.GLASS);
            k.set(hu1, F + 2, cvm, P.GLASS);
            post(k, cu + dw + 2, F, dv + out);
        }
        // Dach
        int ridgeY = F + h - 1;
        for (int r = 0; ; r++) {
            int y = F + h - 1 + r, left = hu0 - 1 + r, right = hu1 + 1 - r;
            if (left > right) break;
            ridgeY = y;
            for (int v = hv0 - 1; v <= hv1 + 1; v++) {
                if (ruin && k.rnd(215, v, y, a * 31 + b) < 0.45 && v > hv0 && v < hv1) continue;
                if (left == right) k.set(left, y, v, ridge);
                else {
                    k.set(left, y, v, Kit.stairs(roofSt, Direction.EAST, false));
                    k.set(right, y, v, Kit.stairs(roofSt, Direction.WEST, false));
                }
            }
            if (r >= 1) for (int u = left + 1; u <= right - 1; u++) {
                k.set(u, y, hv0, wall);
                k.set(u, y, hv1, wall);
            }
        }
        // Innen: Sarkophag, Kerzen, Laterne
        int bv = south ? hv0 + 1 : hv1 - 1;
        k.set(cu - 1, F, bv, P.CHPB);
        k.set(cu, F, bv, P.PBS);
        k.set(cu + 1, F, bv, P.CHPB);
        k.fill(cu - 1, F + 1, bv, cu + 1, F + 1, bv, P.PSLAB);
        if (w == 9) k.set(cu + 2, F, bv, P.CANDLE);
        k.set(cu - 2, F, bv, k.rnd(216, a, 0, b) < 0.5 ? P.CANDLE : P.SKULL);
        if (!ruin) k.set(cu, ridgeY - 1, cvm, P.SLH);
        else {
            k.set(cu, F + 1, bv, P.WEB);
            k.set(hu1 - 1, F, cvm, P.CDS);
            k.set(hu1 - 1, F, cvm + 1, P.CANDLE1);
            k.set(hu0 + 1, F + h - 2, hv0 + 1, P.WEB);
        }
    }

    /** Kleines Stufenpyramiden-Grab mit Kammer. */
    private void pyramidTomb(Kit k, int hu0, int hv0, int w, boolean south) {
        int half = w / 2, cu = hu0 + half, cv = hv0 + half;
        k.fill(cu - half - 1, FLOOR, cv - half - 1, cu + half + 1, FLOOR, cv + half + 1, P.PBS);
        for (int i = 0; i <= half; i++) {
            int hs = half - i, y0 = F + 2 * i;
            BlockState m = (i & 1) == 0 ? P.PBB : P.BASB;
            if (hs == 0) k.set(cu, y0, cv, P.COBS);
            else {
                k.fill(cu - hs, y0, cv - hs, cu + hs, y0 + 1, cv + hs, m);
                for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) k.set(cu + sx * hs, y0 + 1, cv + sz * hs, P.CHPB);
            }
        }
        k.set(cu, F + 2 * half + 1, cv, P.CLU);
        k.clear(cu - 1, F, cv - 1, cu + 1, F + 2, cv + 1);
        k.fill(cu - 1, FLOOR, cv - 1, cu + 1, FLOOR, cv + 1, P.PDS);
        k.set(cu, F, cv, P.CHPB);
        k.set(cu, F + 1, cv, P.WSKULL);
        int s = south ? 1 : -1;
        for (int t = 2; t <= half; t++) k.clear(cu, F, cv + s * t, cu, F + 1, cv + s * t);
        k.set(cu - 1, F, cv - s, P.CANDLE);
        k.set(cu + 1, F, cv - s, P.CANDLE1);
        k.set(cu - 1, F, cv + s * (half + 1), P.SL);
        k.set(cu + 1, F, cv + s * (half + 1), P.SL);
    }

    /** Graeberfeld: niedrige Mauer, Grabsteine, Obelisk in der Mitte. */
    private void graveField(Kit k, int a, int b, int u0, int v0) {
        int x0 = u0 + 1, x1 = u0 + 9, z0 = v0 + 1, z1 = v0 + 9, cx = u0 + 5, cz = v0 + 5;
        for (int u = x0; u <= x1; u++) for (int v = z0; v <= z1; v++) {
            boolean edge = u == x0 || u == x1 || v == z0 || v == z1;
            k.set(u, FLOOR, v, edge ? P.PBS : k.rnd(220, u, 0, v) < 0.6 ? P.SOUL : P.REG);
            if (edge && Math.abs(u - cx) > 1 && Math.abs(v - cz) > 1) k.set(u, F, v, (u == x0 || u == x1) && (v == z0 || v == z1) ? P.CHPB : P.DSW);
        }
        for (int gu = x0 + 1; gu <= x1 - 1; gu += 2) for (int gv = z0 + 1; gv <= z1 - 2; gv += 3) {
            if (Math.abs(gu - cx) <= 1 && Math.abs(gv - cz) <= 2) continue;
            double r = k.rnd(221, gu, 0, gv);
            k.set(gu, F, gv, r < 0.4 ? P.CHDS : r < 0.7 ? P.PBS : P.DST);
            if (r > 0.75) k.set(gu, F + 1, gv, P.PBW);
            else if (r < 0.2) k.set(gu, F + 1, gv, P.SKULL);
            k.set(gu, FLOOR, gv + 1, P.SOUL);
            double r2 = k.rnd(222, gu, 0, gv);
            if (r2 < 0.35) k.set(gu, F, gv + 1, P.CANDLE1);
            else if (r2 < 0.5) k.set(gu, F, gv + 1, P.BONEX);
        }
        int h = 7 + (int) (k.rnd(223, a, 0, b) * 5);
        k.fill(cx - 1, F, cz - 1, cx + 1, F, cz + 1, P.PBB);
        k.set(cx, F + 1, cz, P.CHPB);
        for (int y = F + 2; y <= F + h; y++) k.set(cx, y, cz, (y - F) % 4 == 1 ? P.COBS : P.OBS);
        k.set(cx, F + h + 1, cz, P.AME);
        k.set(cx, F + h + 2, cz, P.CLU);
        for (int sx = -1; sx <= 1; sx += 2) k.set(cx + sx, F + 1, cz, P.SL);
    }

    private void obelisk(Kit k, int cu, int cv, int h, boolean big) {
        if (!k.touches(cu - 3, cv - 3, cu + 3, cv + 3)) return;
        if (big) {
            k.fill(cu - 2, F, cv - 2, cu + 2, F, cv + 2, P.PBB);
            k.fill(cu - 2, F + 1, cv - 2, cu + 2, F + 1, cv + 2, P.CHPB);
            for (int sx = -2; sx <= 2; sx += 4) for (int sz = -2; sz <= 2; sz += 4) k.set(cu + sx, F + 2, cv + sz, P.SL);
            for (int y = F + 2; y <= F + h - 4; y++) for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
                boolean face = (du == 0) != (dv == 0);
                k.set(cu + du, y, cv + dv, face && (y - F) % 3 == 0 ? P.COBS : face && (y - F) % 3 == 1 ? P.AME : P.OBS);
            }
            for (int y = F + h - 3; y <= F + h; y++) k.set(cu, y, cv, y == F + h ? P.COBS : P.BOBS);
            k.set(cu, F + h + 1, cv, P.FROG);
            k.set(cu, F + h + 2, cv, P.CLU);
        } else {
            k.fill(cu - 1, F, cv - 1, cu + 1, F, cv + 1, P.PBB);
            for (int y = F + 1; y <= F + h; y++) k.set(cu, y, cv, y == F + h - 1 ? P.COBS : P.OBS);
            k.set(cu, F + h + 1, cv, P.AME);
            k.set(cu, F + h + 2, cv, P.CLU);
        }
    }

    private void plaza(Kit k) {
        if (!k.touches(-24, 28, 24, 58)) return;
        for (int u = Math.max(k.u0(), -22); u <= Math.min(k.u1(), 22); u++) for (int v = Math.max(k.v0(), 29); v <= Math.min(k.v1(), 37); v++) {
            BlockState s = Math.abs(u) == 22 || v == 29 || v == 37 ? P.PBS : Math.floorMod(u, 6) == 0 && v == 33 ? P.COBS
                    : ((u + v) & 1) == 0 ? P.PBB : P.DST;
            k.set(u, FLOOR, v, s);
            k.set(u, F, v, Kit.AIR);
        }
        for (int u = Math.max(k.u0(), -3); u <= Math.min(k.u1(), 3); u++) for (int v = Math.max(k.v0(), 38); v <= Math.min(k.v1(), 56); v++) {
            k.set(u, FLOOR, v, Math.abs(u) == 3 ? P.PBS : u == 0 ? P.PDS : P.PBB);
            k.set(u, F, v, Kit.AIR);
        }
        obelisk(k, -10, 34, 15, true);
        obelisk(k, 10, 34, 15, true);
        for (int s = -1; s <= 1; s += 2) {
            obelisk(k, 19 * s, 31, 8, false);
            obelisk(k, 19 * s, 36, 10, false);
            obelisk(k, 6 * s, 54, 7, false);
        }
    }

    // ── Spalte und Bruecken ─────────────────────────────────────────────────

    private BlockState crevWall(Kit k, int u, int y, int v) {
        if (k.noise(16, u + y * 2, v + y, 4) > 0.76) return P.AME;
        double r = k.rnd(17, u, y, v);
        int band = Math.floorMod(y + (int) (k.noise(18, u, v, 9) * 5), 7);
        if (band < 2) return r < 0.7 ? P.DS : P.CDS;
        if (band < 4) return r < 0.8 ? P.IKE : P.TUFF;
        if (band < 6) return r < 0.6 ? P.BAS : P.SBAS;
        return r < 0.05 ? P.COBS : P.CDS;
    }

    private void crevice(Kit k) {
        if (!k.touches(-64, 34, 64, 60)) return;
        int bu = -30, bvv = (int) Math.round(crevC(k, -30));
        int[] ladders = {-24, 24, -52, 52};
        int ua = Math.max(k.u0(), -64), ub = Math.min(k.u1(), 64);
        for (int u = ua; u <= ub; u++) {
            double w = crevW(k, u);
            if (w <= 0.7) continue;
            double c = crevC(k, u);
            int va = Math.max(k.v0(), (int) Math.floor(c - w - 2)), vb = Math.min(k.v1(), (int) Math.ceil(c + w + 2));
            boolean nearLadder = false;
            for (int lu : ladders) if (Math.abs(u - lu) <= 1) nearLadder = true;
            for (int v = va; v <= vb; v++) {
                double dd = Math.abs(v - c);
                if (dd <= w) {
                    for (int y = -65; y <= F; y++) k.set(u, y, v, Kit.AIR);
                    boolean keep = nearLadder || (Math.abs(u - bu) <= 2 && Math.abs(v - bvv) <= 2);
                    double r = k.rnd(19, u, 0, v);
                    if (!keep && dd <= w - 1 && k.noise(15, u, v, 4) > 0.64) {
                        k.set(u, -66, v, P.BROM);
                        continue;
                    }
                    k.set(u, -66, v, r < 0.4 ? P.SOUL : r < 0.55 ? P.BONE : r < 0.75 ? P.CDS : r < 0.8 ? P.COBS : P.IKE);
                    if (keep) continue;
                    double r2 = k.rnd(20, u, 1, v);
                    if (r2 < 0.07) k.set(u, -65, v, P.CLU);
                    else if (r2 < 0.12) k.set(u, -65, v, P.SKULL);
                    else if (r2 < 0.15) k.set(u, -65, v, P.BONEX);
                } else if (dd <= w + 1.6) {
                    for (int y = -66; y < FLOOR; y++) k.set(u, y, v, crevWall(k, u, y, v));
                    k.set(u, FLOOR, v, dd <= w + 1 ? P.CDST : P.DST);
                }
            }
        }
        for (int lu : ladders) {
            double c = crevC(k, lu), w = crevW(k, lu);
            if (w <= 0.7) continue;
            int vn = (int) Math.ceil(c - w);
            k.ladder(lu, -65, FLOOR, vn, Direction.NORTH);
        }
        k.set(bu, -65, bvv, Kit.AIR);
    }

    private void bridges(Kit k) {
        bridge(k, -3, 3, false, true);
        bridge(k, -43, -41, false, false);
        bridge(k, 41, 43, false, false);
        bridge(k, -57, -55, true, false);
        bridge(k, 27, 29, true, false);
    }

    private void bridge(Kit k, int ua, int ub, boolean broken, boolean grand) {
        if (!k.touches(ua - 2, 30, ub + 2, 62)) return;
        double lo = 1e9, hi = -1e9;
        for (int u = ua - 1; u <= ub + 1; u++) {
            double w = crevW(k, u);
            if (w <= 0.7) continue;
            double c = crevC(k, u);
            lo = Math.min(lo, c - w);
            hi = Math.max(hi, c + w);
        }
        if (lo > hi) return;
        int va = (int) Math.floor(lo) - 2, vb = (int) Math.ceil(hi) + 2, mu = (ua + ub) / 2;
        double cm = crevC(k, mu);
        for (int v = va; v <= vb; v++) {
            int e = Math.min(v - va, vb - v);
            int under = Math.max(1, 6 - e);
            for (int u = ua - 1; u <= ub + 1; u++) {
                if (broken && v > cm - 1 + k.rnd(52, u, 0, 0) * 2) continue;
                boolean side = u == ua - 1 || u == ub + 1;
                for (int y = FLOOR - under; y < FLOOR; y++) k.set(u, y, v, y == FLOOR - under ? P.CHPB : P.DSB);
                k.set(u, FLOOR, v, side ? P.PBB : grand && u == mu ? P.PDS : k.rnd(51, u, 0, v) < 0.15 ? P.CPBB : P.PBB);
                if (side) {
                    if (e % 4 == 0) post(k, u, F, v);
                    else k.set(u, F, v, P.PBW);
                } else k.set(u, F, v, Kit.AIR);
            }
        }
        if (grand) for (int s = -1; s <= 1; s += 2) {
            hang(k, ub * s + s * 0, (int) Math.round(cm), FLOOR - 7 + 6, 1);
        }
    }

    // ── Zikkurat und Pyramidenhalle ─────────────────────────────────────────

    static int zigTop(int m) {
        return m > 28 ? NONE : -40 + 4 * Math.min(4, (28 - m) / 4);
    }

    static int hollow(int y) {
        return y <= -40 ? 24 : y <= -36 ? 20 : y <= -32 ? 16 : y <= -28 ? 12 : y <= -25 ? 8 : -1;
    }

    private BlockState hallFloor(Kit k, int u, int v) {
        double d = Math.hypot(u, v);
        if (Math.abs(d - 11) < 0.55) return P.AME;
        if (Math.abs(d - 18.5) < 0.5) return P.COBS;
        if (Math.abs(d - 19.5) < 0.5 || Math.abs(d - 10) < 0.5) return P.CHPB;
        if ((u == 0 || v == 0 || Math.abs(u) == Math.abs(v)) && d > 7) return P.DST;
        return k.rnd(65, u, 0, v) < 0.1 ? P.CDST : P.PDS;
    }

    private BlockState hallWall(Kit k, int u, int y, int v, int m, int t) {
        int g = Math.floorMod(t, 8);
        boolean win = g == 4 && y >= -56 && y <= -50 && Math.abs(t) < 22;
        if (m == 25) {
            if (g == 0 && Math.abs(t) < 24) return P.BASP;
            if (win) return P.GLASS;
            if (y == -63 || y == -45) return P.CHPB;
            if (y == -57 || y == -49) return P.PBB;
            return k.rnd(64, u, y, v) < 0.2 ? P.CDSB : P.DSB;
        }
        if (m == 26 && win) return P.FROG;
        return P.DS;
    }

    private BlockState zigBlock(Kit k, int u, int y, int v, int m, int t, int top) {
        int hol = hollow(y);
        if (y > FLOOR && (m == hol + 1 || hollow(y - 1) >= m)) {
            return k.rnd(61, u, y, v) < 0.22 ? P.CDSB : Math.floorMod(t, 6) == 0 && m == hol + 1 ? P.PBB : P.DSB;
        }
        int kTop = (top + 40) / 4, hh = 28 - 4 * kTop;
        if (y == top) {
            if (m == hh) return P.PBS;
            if (kTop == 4) return Math.abs(u) == Math.abs(v) ? P.CHPB : ((u + v) & 1) == 0 ? P.PDS : P.DST;
            return k.rnd(62, u, y, v) < 0.15 ? P.CDST : m == hh - 1 ? P.PDS : P.DST;
        }
        if (y >= F && (y + 43) / 4 == kTop && m == hh) {
            int row = (y + 43) % 4;
            if (Math.abs(u) == Math.abs(v)) return P.BOBS;
            if (row == 0) return P.BASB;
            if (row == 2) {
                int g = Math.floorMod(t, 4);
                return g == 0 ? P.COBS : g == 2 ? P.CHPB : P.PBB;
            }
            return k.rnd(63, u, y, v) < 0.18 ? P.CPBB : P.PBB;
        }
        return y == FLOOR ? P.PBS : P.DSB;
    }

    private void ziggurat(Kit k) {
        if (!k.touches(-28, -28, 28, 32)) return;
        int ua = Math.max(k.u0(), -28), ub = Math.min(k.u1(), 28), va = Math.max(k.v0(), -28), vb = Math.min(k.v1(), 28);
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            int m = Math.max(Math.abs(u), Math.abs(v)), top = zigTop(m);
            int t = Math.abs(u) == m ? v : u;
            for (int y = m <= 27 ? -66 : FLOOR; y <= top; y++) {
                BlockState s;
                if (y <= -65) s = P.DS;
                else if (y == -64) s = m <= 24 ? hallFloor(k, u, v) : P.DSB;
                else if (y < FLOOR) s = m <= 24 ? Kit.AIR : hallWall(k, u, y, v, m, t);
                else if (m <= hollow(y)) s = Kit.AIR;
                else s = zigBlock(k, u, y, v, m, t, top);
                k.set(u, y, v, s);
            }
        }
        // Freitreppe nach Sueden mit Wangen
        for (int i = 1; i <= 19; i++) {
            int v = 32 - i, ys = FLOOR + i;
            if (!k.touches(-5, v, 5, v)) continue;
            for (int u = -5; u <= 5; u++) {
                int from = (v > 28 ? FLOOR : zigTop(Math.max(Math.abs(u), v))) + 1;
                if (Math.abs(u) <= 3) {
                    for (int y = from; y < ys; y++) k.set(u, y, v, P.PBB);
                    k.set(u, ys, v, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH, false));
                } else {
                    for (int y = from; y <= ys + 1; y++) k.set(u, y, v, y == ys + 1 ? P.PBS : Math.abs(u) == 5 && (y & 1) == 0 ? P.BASB : P.PBB);
                    if (Math.abs(u) == 5 && i % 5 == 2) k.set(u, ys + 2, v, P.SL);
                }
            }
        }
        // Laternen auf den Stufenecken
        for (int kk = 0; kk < 4; kk++) {
            int c = 27 - 4 * kk, y = -39 + 4 * kk;
            for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) {
                k.set(sx * c, y, sz * c, P.PBW);
                k.set(sx * c, y + 1, sz * c, P.SL);
            }
        }
    }

    private void hall(Kit k) {
        if (k.touches(-24, -24, 24, 24)) {
            for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) {
                hallPillar(k, 16 * sx, 16 * sz);
                crystalMound(k, 21 * sx, 21 * sz);
            }
            int[][] rings = {{22, -40, 6}, {18, -36, 5}, {14, -32, 5}, {10, -28, 4}};
            for (int[] rg : rings) {
                int m = rg[0];
                for (int t = -m + 4; t <= m - 4; t += 6) {
                    int[][] pos = {{t, -m}, {t, m}, {-m, t}, {m, t}};
                    for (int[] p : pos) {
                        boolean bad = Math.hypot(p[0], p[1]) < 8;
                        for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) if (Math.hypot(p[0] - 16 * sx, p[1] - 16 * sz) < 4) bad = true;
                        if (Math.abs(p[0]) <= 3 && p[1] < -6) bad = true;
                        if (!bad) hang(k, p[0], p[1], rg[1], rg[2]);
                    }
                }
            }
            // Thron-Podest im Norden
            k.fill(-9, -63, -24, 9, -63, -20, P.PBB);
            for (int u = -9; u <= 9; u++) k.set(u, -63, -19, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH, false));
            for (int u = -8; u <= 8; u += 2) k.set(u, -63, -22, P.COBS);
            k.set(0, -62, -23, P.OBS);
            k.set(0, -61, -23, P.OBS);
            k.set(0, -60, -23, P.COBS);
            k.set(0, -59, -23, P.CLU);
            k.set(-1, -62, -23, Kit.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.EAST, false));
            k.set(1, -62, -23, Kit.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.WEST, false));
            for (int s = -1; s <= 1; s += 2) {
                for (int y = -62; y <= -56; y++) k.set(8 * s, y, -23, y == -58 ? P.COBS : P.OBS);
                k.set(8 * s, -55, -23, P.AME);
                k.set(8 * s, -54, -23, P.CLU);
                k.set(3 * s, -62, -20, P.CANDLE);
                k.set(7 * s, -62, -20, P.CANDLE);
                k.set(5 * s, -62, -24, P.SL);
            }
        }
        k.boss(0, -63, -12);
        k.bossChest(-5, -62, -22, Direction.SOUTH);
        k.bossChest(5, -62, -22, Direction.SOUTH);
    }

    private void hallPillar(Kit k, int pu, int pv) {
        if (!k.touches(pu - 4, pv - 4, pu + 4, pv + 4)) return;
        for (int u = pu - 3; u <= pu + 3; u++) for (int v = pv - 3; v <= pv + 3; v++) {
            if (!k.col(u, v)) continue;
            double d = Math.hypot(u - pu, v - pv);
            if (d <= 3.2) {
                k.set(u, -63, v, P.PBB);
                k.set(u, -62, v, d <= 2.9 ? P.CHPB : Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, dirTo(pu - u, pv - v), false));
                k.set(u, -37, v, d <= 2.9 ? P.CHPB : Kit.AIR);
            }
            if (d <= 2.3) for (int y = -61; y <= -36; y++) {
                boolean face = (u == pu || v == pv) && d > 1.5;
                BlockState s = face ? ((y & 3) == 0 ? P.AME : P.COBS) : k.rnd(66, u, y, v) < 0.3 ? P.BOBS : P.OBS;
                if (y == -38 || y == -48) s = P.CHPB;
                k.set(u, y, v, s);
            }
        }
    }

    static Direction dirTo(int du, int dv) {
        if (Math.abs(du) >= Math.abs(dv)) return du > 0 ? Direction.EAST : Direction.WEST;
        return dv > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private void crystalMound(Kit k, int cu, int cv) {
        if (!k.touches(cu - 4, cv - 4, cu + 4, cv + 4)) return;
        for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) {
            double d = Math.hypot(u - cu, v - cv);
            if (d > 3.3) continue;
            int hh = (int) Math.round(4 * (1 - d / 3.6));
            for (int y = -63; y < -63 + hh; y++) k.set(u, y, v, k.rnd(67, u, y, v) < 0.2 ? P.BUD : P.AME);
            if (k.rnd(68, u, 0, v) < 0.6) k.set(u, -63 + hh, v, P.CLU);
        }
        k.set(cu, -63, cv, P.FROG);
    }

    private void temple(Kit k) {
        double perRad = 40 / (3.5 * Math.PI);
        if (k.touches(-12, -12, 12, 12)) {
            for (int u = Math.max(k.u0(), -10); u <= Math.min(k.u1(), 10); u++) for (int v = Math.max(k.v0(), -10); v <= Math.min(k.v1(), 9); v++) {
                boolean edge = Math.abs(u) == 10 || v == -10 || v == 9;
                int t = Math.abs(u) == 10 ? v : u;
                if (!edge) {
                    double d = Math.hypot(u, v);
                    k.set(u, -24, v, Math.abs(d - 8) < 0.5 ? P.AME : ((u + v) & 1) == 0 ? P.PDS : P.DST);
                }
                for (int y = -23; y <= -16; y++) {
                    BlockState s = Kit.AIR;
                    if (edge) {
                        boolean corner = Math.abs(u) == 10 && (v == -10 || v == 9);
                        if (corner) s = P.BOBS;
                        else if (Math.floorMod(t, 4) == 0) s = P.BASP;
                        else if (y == -23) s = P.PBS;
                        else if (y == -16) s = P.CHPB;
                        else if (Math.abs(u) == 10 && (y == -20 || y == -19) && Math.floorMod(t, 4) == 2) s = P.GLASS;
                        else if (y == -18 && Math.floorMod(t, 4) == 2) s = P.COBS;
                        else s = k.rnd(71, u, y, v) < 0.18 ? P.CPBB : P.PBB;
                    }
                    k.set(u, y, v, s);
                }
            }
            // Stufendach
            k.fill(-11, -15, -11, 11, -15, 9, P.PBS);
            k.fill(-9, -14, -9, 9, -14, 8, P.DST);
            k.fill(-7, -13, -7, 7, -13, 6, P.PBB);
            k.fill(-5, -12, -5, 5, -12, 4, P.DST);
            k.fill(-3, -11, -3, 3, -11, 2, P.BOBS);
            k.fill(-1, -10, -1, 1, -10, 0, P.COBS);
            k.set(0, -9, 0, P.CLU);
            for (int sx = -1; sx <= 1; sx += 2) {
                k.set(7 * sx, -16, -7, P.SLH);
                k.set(7 * sx, -16, 6, P.SLH);
                k.set(8 * sx, -23, 7, P.CANDLE);
                k.set(8 * sx, -23, -8, P.CANDLE);
            }
            // Loch zur Halle (ohne Nahtsektor), Saeule mit Altar
            for (int u = Math.max(k.u0(), -7); u <= Math.min(k.u1(), 7); u++) for (int v = Math.max(k.v0(), -7); v <= Math.min(k.v1(), 7); v++) {
                double d = Math.hypot(u, v), a = angle(u, v, 90);
                if (d <= 1.5) {
                    for (int y = -64; y <= -24; y++) {
                        boolean vein = (u == 0) != (v == 0);
                        k.set(u, y, v, vein && (y & 1) == 0 ? P.COBS : vein ? P.AME : P.OBS);
                    }
                    k.set(u, -23, v, P.CHPB);
                } else if (d <= 6.5 && a < TAU - Math.toRadians(25)) k.set(u, -24, v, Kit.AIR);
            }
            k.set(0, -22, 0, P.COBS);
            k.set(0, -21, 0, P.CLU);
        }
        helix(k, 0, 0, -24, -64, 1.5, 5.5, 6.5, 90, perRad, P.PBB, P.PBW);
        if (k.touches(-8, -8, 8, 8)) {
            for (int u = -8; u <= 8; u++) for (int v = -8; v <= 8; v++) {
                double d = Math.hypot(u, v), a = Math.toDegrees(angle(u, v, 90));
                if (d > 6.5 && d <= 7.5 && a > 20 && a < 320) k.set(u, -23, v, P.PBW);
            }
        }
        k.sealGate(0, -24, 9, Direction.SOUTH, 2, 5, P.BOBS, P.AME);
    }

    // ── Schluessel 1: Krypta ueber dem Abgrund (Ost) ────────────────────────

    private void krypta(Kit k) {
        if (k.touches(36, -17, 77, 17)) {
            for (int u = Math.max(k.u0(), 40); u <= Math.min(k.u1(), 76); u++) for (int v = Math.max(k.v0(), -15); v <= Math.min(k.v1(), 15); v++) {
                boolean edge = u == 40 || u == 76 || Math.abs(v) == 15;
                int t = Math.abs(v) == 15 ? u : v;
                for (int y = FLOOR; y <= -27; y++) {
                    BlockState s;
                    if (y == FLOOR) s = edge ? P.PBB : ((u + v) & 1) == 0 ? P.PDS : P.DST;
                    else if (y == -27) s = Math.floorMod(u - 40, 6) == 0 ? P.PBB : P.DST;
                    else if (edge) {
                        if (Math.floorMod(t, 6) == 0) s = P.BOBS;
                        else if (y == -36 || y == -28) s = P.CHPB;
                        else if (u == 40 && Math.floorMod(t, 6) == 3 && y >= -35 && y <= -31) s = P.GLASS;
                        else s = k.rnd(81, u, y, v) < 0.2 ? P.CPBB : P.PBB;
                    } else s = Kit.AIR;
                    k.set(u, y, v, s);
                }
            }
            k.fill(39, -26, -16, 77, -26, 16, P.PBS);
            k.fill(42, -25, -13, 74, -25, 13, P.DST);
            k.fill(46, -24, -9, 70, -24, 9, P.PBB);
            // Tor nach Westen mit Saeulenvorhalle
            k.clear(40, F, -2, 40, -38, 2);
            k.fill(40, -37, -3, 40, -37, 3, P.CHPB);
            for (int s = -1; s <= 1; s += 2) {
                for (int y = F; y <= -33; y++) {
                    k.set(38, y, 4 * s, P.BASP);
                    k.set(38, y, 9 * s, P.BASP);
                }
                post(k, 37, F, 2 * s);
            }
            k.fill(37, -32, -10, 39, -32, 10, P.CHPB);
            k.fill(37, -31, -8, 39, -31, 8, P.PBB);
            k.set(39, -30, 0, P.COBS);
            // Grube
            for (int u = Math.max(k.u0(), 45); u <= Math.min(k.u1(), 76); u++) for (int v = Math.max(k.v0(), -15); v <= Math.min(k.v1(), 15); v++) {
                boolean pit = u >= 46 && u <= 70 && Math.abs(v) <= 14;
                if (pit) {
                    for (int y = -65; y <= FLOOR; y++) k.set(u, y, v, Kit.AIR);
                    boolean keep = Math.abs(u - 58) <= 2 && Math.abs(v + 10) <= 2 || Math.abs(u - 46) <= 2 && Math.abs(v - 10) <= 2;
                    double r = k.rnd(82, u, 0, v);
                    if (!keep && u >= 48 && u <= 68 && Math.abs(v) <= 12 && k.noise(83, u, v, 4) > 0.62) {
                        k.set(u, -66, v, P.BROM);
                        continue;
                    }
                    k.set(u, -66, v, r < 0.35 ? P.SOUL : r < 0.55 ? P.BONE : r < 0.7 ? P.CDS : r < 0.75 ? P.COBS : P.DS);
                    if (keep) continue;
                    double r2 = k.rnd(84, u, 1, v);
                    if (r2 < 0.06) k.set(u, -65, v, P.SKULL);
                    else if (r2 < 0.1) k.set(u, -65, v, P.BONEZ);
                    else if (r2 < 0.13) k.set(u, -65, v, P.CLU);
                } else if (u >= 71) {
                    for (int y = -66; y <= -40; y++) k.set(u, y, v, y == -40 ? (((u + v) & 1) == 0 ? P.CHPB : P.PBB) : y > -44 ? P.PBB : crevWall(k, u, y, v));
                } else {
                    for (int y = -66; y < FLOOR; y++) k.set(u, y, v, crevWall(k, u, y, v));
                }
            }
            // Kristalle an den Grubenwaenden
            for (int y = -64; y <= -48; y += 3) for (int v = -13; v <= 13; v += 2) {
                if (k.rnd(85, 0, y, v) < 0.4 && Math.abs(v - 10) > 1) k.set(46, y, v, Kit.facingAll(Blocks.AMETHYST_CLUSTER, Direction.EAST));
                if (k.rnd(86, 0, y, v) < 0.4) k.set(70, y, v, Kit.facingAll(Blocks.AMETHYST_CLUSTER, Direction.WEST));
            }
            // Saeulenstuempfe und Sarkophagdeckel
            for (int i = 0; i < PK.length; i++) {
                int[] p = PK[i];
                for (int u = p[0]; u <= p[2]; u++) for (int v = p[1]; v <= p[3]; v++) {
                    for (int y = -66; y < p[4]; y++) {
                        double r = k.rnd(87, u, y, v);
                        k.set(u, y, v, (y & 7) == 0 ? P.CHDS : r < 0.3 ? P.CDSB : r < 0.5 ? P.PBB : P.DSB);
                    }
                    boolean lid = (p[2] - p[0]) + (p[3] - p[1]) >= 2;
                    k.set(u, p[4], v, lid ? P.PBS : P.CDSB);
                }
                if ((p[2] - p[0]) + (p[3] - p[1]) >= 2) k.set(p[0], p[4] + 1, p[1], i % 2 == 0 ? P.SKULL : P.CANDLE);
            }
            int[][] decoy = {{51, -8, -55}, {55, 9, -51}, {62, 10, -58}, {59, -12, -53}, {53, -4, -60}, {67, 9, -50}};
            for (int[] dc : decoy) for (int y = -66; y <= dc[2]; y++) k.set(dc[0], y, dc[1], y == dc[2] ? P.CDSB : P.DSB);
            k.ladder(46, -65, FLOOR, 10, Direction.WEST);
            // Laternen ueber der Grube, Nischen mit Sarkophagen
            for (int u = 50; u <= 68; u += 6) for (int s = -1; s <= 1; s += 2) hang(k, u, 9 * s, -28, 6);
            hang(k, 59, 0, -28, 4);
            for (int u = 49; u <= 67; u += 6) for (int s = -1; s <= 1; s += 2) {
                int v = 15 * s;
                k.clear(u - 1, -40, v, u + 1, -38, v);
                k.fill(u - 1, -40, v, u + 1, -40, v, P.CHPB);
                k.set(u, -39, v, P.SKULL);
                k.set(u - 1, -39, v, P.CANDLE1);
                k.set(u + 1, -39, v, P.CANDLE1);
                k.fill(u - 1, -37, v, u + 1, -37, v, P.CHPB);
            }
            // Altar
            k.set(75, -39, 0, P.CHPB);
            k.set(75, -38, 0, P.COBS);
            k.set(75, -37, 0, P.CLU);
            for (int s = -1; s <= 1; s += 2) {
                for (int y = -39; y <= -35; y++) k.set(74, y, 5 * s, y == -36 ? P.COBS : P.OBS);
                k.set(74, -34, 5 * s, P.AME);
                k.set(74, -33, 5 * s, P.CLU);
                k.set(73, -39, 2 * s, P.CANDLE);
                k.set(75, -39, 12 * s, P.SL);
            }
            k.set(42, F, -13, P.SL);
            k.set(42, F, 13, P.SL);
        }
        k.keyChest(74, -39, 0, Direction.WEST);
        k.loot(58, -65, -10, Direction.NORTH);
        k.spawner(75, -39, 9, SHADE);
        k.mob(43, F, -8, SHADE);
        k.mob(43, F, 6, WARDEN);
    }

    // ── Schluessel 3: Mausoleum (Nord) ──────────────────────────────────────

    private void statue(Kit k, int u, int v, Direction face) {
        k.fill(u - 1, -42, v - 1, u + 1, -42, v + 1, P.CHPB);
        k.set(u, -41, v, P.PBS);
        k.set(u, -40, v, P.BOBS);
        k.set(u, -39, v, P.BOBS);
        Direction side = face.getClockWise();
        k.set(u + side.getStepX(), -40, v + side.getStepZ(), P.PBW);
        k.set(u - side.getStepX(), -40, v - side.getStepZ(), P.PBW);
        k.set(u, -38, v, P.CHDS);
        k.set(u, -37, v, P.WSKULL);
        k.set(u + face.getStepX(), -41, v + face.getStepZ(), P.CANDLE);
    }

    private void mausoleum(Kit k) {
        if (k.touches(-24, -72, 24, -37)) {
            k.fill(-22, FLOOR, -70, 22, -43, -40, P.PBB);
            for (int u = -22; u <= 22; u++) {
                k.set(u, -43, -40, P.CHPB);
                if (Math.abs(u) <= 10) k.set(u, -43, -39, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH, false));
            }
            k.room(-20, -43, -68, 20, -31, -46, P.PBB, P.PDS, P.DST);
            for (int u = Math.max(k.u0(), -20); u <= Math.min(k.u1(), 20); u++) for (int v = Math.max(k.v0(), -68); v <= Math.min(k.v1(), -46); v++) {
                boolean edge = Math.abs(u) == 20 || v == -68 || v == -46;
                int t = Math.abs(u) == 20 ? v : u;
                if (edge) for (int y = -42; y <= -32; y++) {
                    BlockState s;
                    if (Math.floorMod(t, 6) == 0) s = P.BOBS;
                    else if (y == -42) s = P.PBS;
                    else if (y == -33) s = Math.floorMod(t, 3) == 0 ? P.COBS : P.CHPB;
                    else s = k.rnd(91, u, y, v) < 0.2 ? P.CPBB : P.PBB;
                    k.set(u, y, v, s);
                } else if (Math.abs(u) <= 1 && v >= -63 && v <= -47) k.set(u, -42, v, P.CARPET);
                else if (((u + v) & 1) == 0) k.set(u, -43, v, P.DST);
            }
            // Vorhalle, Saeulen
            int[] cols = {-15, -9, -3, 3, 9, 15};
            for (int cu : cols) {
                k.set(cu, -42, -42, P.CHPB);
                for (int y = -41; y <= -33; y++) k.set(cu, y, -42, P.BASP);
                k.set(cu, -32, -42, P.CHPB);
            }
            k.fill(-18, -31, -46, 18, -31, -41, P.CHPB);
            // Dach (flach geneigt) mit Giebel
            for (int r = 0; r <= 6; r++) {
                int y = -30 + r, a = -21 + 3 * r, b = 21 - 3 * r;
                k.fill(a, y, -69, b, y, -41, P.DST);
                k.fill(a, y, -69, a, y, -41, Kit.stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.EAST, false));
                k.fill(b, y, -69, b, y, -41, Kit.stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.WEST, false));
                if (r >= 1) k.fill(a + 1, y, -41, b - 1, y, -41, P.PBB);
            }
            k.set(0, -27, -41, P.COBS);
            k.set(-2, -28, -41, P.AME);
            k.set(2, -28, -41, P.AME);
            k.set(0, -25, -41, P.CHPB);
            // Tor
            k.clear(-3, -42, -46, 3, -36, -46);
            k.fill(-4, -35, -46, 4, -35, -46, P.CHPB);
            k.set(0, -34, -46, P.COBS);
            // Fenster
            for (int s = -1; s <= 1; s += 2) for (int v = -62; v <= -50; v += 6) {
                k.fill(20 * s, -38, v, 20 * s, -35, v, P.GLASS);
                k.fill(21 * s, -38, v, 21 * s, -35, v, P.FROG);
            }
            // Saeulen innen, Statuen, Laternen
            for (int s = -1; s <= 1; s += 2) for (int v = -64; v <= -52; v += 6) {
                k.set(8 * s, -42, v, P.CHPB);
                for (int y = -41; y <= -33; y++) k.set(8 * s, y, v, P.BASP);
                k.set(8 * s, -32, v, P.CHPB);
                hang(k, 4 * s, v, -32, 2);
            }
            for (int s = -1; s <= 1; s += 2) for (int v = -63; v <= -51; v += 4) statue(k, 17 * s, v, s < 0 ? Direction.EAST : Direction.WEST);
            hang(k, 0, -50, -32, 3);
            // Koenigssarkophag
            k.fill(-6, -42, -67, 6, -42, -64, P.PBB);
            for (int u = -6; u <= 6; u++) k.set(u, -42, -63, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH, false));
            k.fill(-1, -41, -67, 1, -41, -65, P.OBS);
            k.fill(-1, -40, -67, 1, -40, -65, P.PSLAB);
            k.set(0, -41, -66, P.COBS);
            for (int s = -1; s <= 1; s += 2) {
                k.set(3 * s, -41, -65, P.CANDLE);
                k.set(3 * s, -41, -67, P.SL);
                for (int y = -41; y <= -36; y++) k.set(5 * s, y, -67, y == -37 ? P.COBS : P.OBS);
                k.set(5 * s, -35, -67, P.CLU);
            }
        }
        k.elite(0, -42, -57, WARDEN);
        k.loot(-18, -42, -67, Direction.EAST);
        k.loot(18, -42, -67, Direction.WEST);
        k.spawner(-18, -42, -48, SHADE);
        k.mob(-5, -42, -50, WARDEN);
        k.mob(5, -42, -61, WARDEN);
        k.mob(0, -42, -48, SHADE);
    }

    // ── Schluessel 2: Katakomben (West, zwei Ebenen) ────────────────────────

    private static final class Maze {
        final boolean[][] e = new boolean[LN][LN], s = new boolean[LN][LN];
        final int[][] dist = new int[LN][LN];

        boolean open(int i, int j, int d) {
            switch (d) {
                case 0: return i < LN - 1 && e[i][j];
                case 1: return j < LN - 1 && s[i][j];
                case 2: return i > 0 && e[i - 1][j];
                default: return j > 0 && s[i][j - 1];
            }
        }

        int links(int i, int j) {
            int n = 0;
            for (int d = 0; d < 4; d++) if (open(i, j, d)) n++;
            return n;
        }

        void link(int i, int j, int d) {
            switch (d) {
                case 0 -> e[i][j] = true;
                case 1 -> s[i][j] = true;
                case 2 -> e[i - 1][j] = true;
                default -> s[i][j - 1] = true;
            }
        }
    }

    static final int[] DI = {1, 0, -1, 0}, DJ = {0, 1, 0, -1};
    static final Direction[] DIRS = {Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};

    private Maze maze(Kit k, long salt, int si, int sj) {
        Random r = new Random(Double.doubleToLongBits(k.rnd(salt, 1, 2, 3)) ^ (salt * 0x9E3779B97F4A7C15L));
        Maze m = new Maze();
        boolean[][] seen = new boolean[LN][LN];
        ArrayDeque<int[]> st = new ArrayDeque<>();
        st.push(new int[] {si, sj});
        seen[si][sj] = true;
        while (!st.isEmpty()) {
            int[] c = st.peek();
            int[] opts = new int[4];
            int n = 0;
            for (int d = 0; d < 4; d++) {
                int ni = c[0] + DI[d], nj = c[1] + DJ[d];
                if (ni >= 0 && nj >= 0 && ni < LN && nj < LN && !seen[ni][nj]) opts[n++] = d;
            }
            if (n == 0) {
                st.pop();
                continue;
            }
            int d = opts[r.nextInt(n)];
            m.link(c[0], c[1], d);
            int ni = c[0] + DI[d], nj = c[1] + DJ[d];
            seen[ni][nj] = true;
            st.push(new int[] {ni, nj});
        }
        for (int x = 0; x < 5; x++) m.link(r.nextInt(LN - 1), r.nextInt(LN - 1), r.nextBoolean() ? 0 : 1);
        for (int[] row : m.dist) Arrays.fill(row, -1);
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[] {si, sj});
        m.dist[si][sj] = 0;
        while (!q.isEmpty()) {
            int[] c = q.poll();
            for (int d = 0; d < 4; d++) if (m.open(c[0], c[1], d)) {
                int ni = c[0] + DI[d], nj = c[1] + DJ[d];
                if (m.dist[ni][nj] < 0) {
                    m.dist[ni][nj] = m.dist[c[0]][c[1]] + 1;
                    q.add(new int[] {ni, nj});
                }
            }
        }
        return m;
    }

    /** Sackgasse mit groesster Entfernung, ausser den genannten Zellen (sonst entfernteste Zelle). */
    private static int[] deadEnd(Maze m, int[][] not) {
        int[] best = null;
        int bd = -1;
        for (int pass = 0; pass < 2 && best == null; pass++) {
            for (int i = 0; i < LN; i++) for (int j = 0; j < LN; j++) {
                boolean skip = false;
                for (int[] c : not) if (c != null && c[0] == i && c[1] == j) skip = true;
                if (skip || (pass == 0 && m.links(i, j) != 1)) continue;
                if (m.dist[i][j] > bd) {
                    bd = m.dist[i][j];
                    best = new int[] {i, j};
                }
            }
        }
        return best;
    }

    private static int openDir(Maze m, int[] c) {
        for (int d = 0; d < 4; d++) if (m.open(c[0], c[1], d)) return d;
        return 0;
    }

    /** Ecke der Zelle gegenueber ihrer Oeffnung (u, v). */
    private static int[] corner(Maze m, int[] c) {
        int d = openDir(m, c);
        int cu0 = LU + 4 * c[0] + 1, cv0 = LV + 4 * c[1] + 1;
        return new int[] {cu0 + (d == 2 ? 1 : 0), cv0 + (d == 3 ? 1 : 0), d};
    }

    private static int[] cellAt(Maze m, int target, int[][] not) {
        for (int i = 0; i < LN; i++) for (int j = 0; j < LN; j++) {
            boolean skip = false;
            for (int[] c : not) if (c != null && c[0] == i && c[1] == j) skip = true;
            if (!skip && m.dist[i][j] == target) return new int[] {i, j};
        }
        return new int[] {5, 5};
    }

    private void catacombs(Kit k) {
        Maze m1 = maze(k, 301, 9, 9);
        int lj = 0, best = -1;
        for (int j = 0; j < LN; j++) if (m1.dist[0][j] > best) {
            best = m1.dist[0][j];
            lj = j;
        }
        int[] lad = {0, lj}, start = {9, 9};
        Maze m2 = maze(k, 302, 0, lj);
        int[] key = deadEnd(m2, new int[][] {lad});
        int[] loot1 = deadEnd(m1, new int[][] {lad, start});
        int[] sp1 = deadEnd(m1, new int[][] {lad, start, loot1});
        int[] sp2 = deadEnd(m2, new int[][] {lad, key});
        int[] kc = corner(m2, key), lc = corner(m1, loot1), s1 = corner(m1, sp1), s2 = corner(m2, sp2);
        k.keyChest(kc[0], L2 + 1, kc[1], DIRS[kc[2]]);
        k.loot(lc[0], L1 + 1, lc[1], DIRS[lc[2]]);
        k.spawner(s1[0], L1 + 1, s1[1], SHADE);
        k.spawner(s2[0], L2 + 1, s2[1], SHADE);
        int max1 = 0, max2 = 0;
        for (int i = 0; i < LN; i++) for (int j = 0; j < LN; j++) {
            max1 = Math.max(max1, m1.dist[i][j]);
            max2 = Math.max(max2, m2.dist[i][j]);
        }
        int[][] not1 = {lad, start, loot1, sp1}, not2 = {lad, key, sp2};
        int[] mA = cellAt(m1, max1 / 3, not1), mB = cellAt(m1, 2 * max1 / 3, not1), mC = cellAt(m2, max2 / 2, not2);
        k.mob(LU + 4 * mA[0] + 2, L1 + 1, LV + 4 * mA[1] + 2, SHADE);
        k.mob(LU + 4 * mB[0] + 2, L1 + 1, LV + 4 * mB[1] + 2, SHADE);
        k.mob(LU + 4 * mC[0] + 2, L2 + 1, LV + 4 * mC[1] + 2, WARDEN);
        if (!k.touches(LU, LV, LU + 40, LV + 40)) return;
        level(k, m1, L1, false, lj);
        level(k, m2, L2, true, lj);
        int vl = LV + 4 * lj + 1;
        k.clear(LU + 1, -55, vl, LU + 1, -50, vl);
        k.ladder(LU + 1, -56, -49, vl, Direction.WEST);
    }

    private BlockState catWall(Kit k, int u, int y, int v, boolean deep) {
        double r = k.rnd(101, u, y, v);
        if (deep) return r < 0.5 ? P.PBB : r < 0.72 ? P.CPBB : r < 0.84 ? P.BONE : r < 0.94 ? P.BS : P.METB;
        return r < 0.5 ? P.DSB : r < 0.72 ? P.CDSB : r < 0.84 ? P.DST : r < 0.94 ? P.BONE : P.DEPB;
    }

    private void carve(Kit k, int ua, int va, int ub, int vb, int yF, boolean deep) {
        for (int u = Math.max(ua, k.u0()); u <= Math.min(ub, k.u1()); u++) for (int v = Math.max(va, k.v0()); v <= Math.min(vb, k.v1()); v++) {
            double r = k.rnd(102, u, yF, v);
            k.set(u, yF, v, deep ? (r < 0.5 ? P.SOUL : r < 0.75 ? P.CDS : P.BONEX) : (r < 0.55 ? P.DST : r < 0.85 ? P.CDST : P.DS));
            for (int y = yF + 1; y <= yF + 3; y++) k.set(u, y, v, Kit.AIR);
        }
    }

    private void level(Kit k, Maze m, int yF, boolean deep, int lj) {
        int ua = Math.max(k.u0(), LU), ub = Math.min(k.u1(), LU + 40), va = Math.max(k.v0(), LV), vb = Math.min(k.v1(), LV + 40);
        if (ua > ub || va > vb) return;
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            for (int y = yF; y <= yF + 3; y++) k.set(u, y, v, catWall(k, u, y, v, deep));
            k.set(u, yF + 4, v, deep ? P.PBB : P.DST);
        }
        for (int i = 0; i < LN; i++) for (int j = 0; j < LN; j++) {
            int cu0 = LU + 4 * i + 1, cv0 = LV + 4 * j + 1;
            if (!k.touches(cu0 - 2, cv0 - 2, cu0 + 3, cv0 + 3)) continue;
            carve(k, cu0, cv0, cu0 + 1, cv0 + 1, yF, deep);
            if (m.open(i, j, 0)) carve(k, cu0 + 2, cv0, cu0 + 3, cv0 + 1, yF, deep);
            if (m.open(i, j, 1)) carve(k, cu0, cv0 + 2, cu0 + 1, cv0 + 3, yF, deep);
            boolean ladderCell = i == 0 && j == lj;
            for (int d = 0; d < 4; d++) {
                if (m.open(i, j, d) || (d == 2 && i == 0) || (d == 3 && j == 0)) continue;
                if (k.rnd(103 + d, i, yF, j) > 0.6) continue;
                int row = k.rnd(108 + d, i, yF, j) < 0.5 ? 0 : 1;
                int wu = d == 0 ? cu0 + 2 : d == 2 ? cu0 - 1 : cu0 + row, wv = d == 1 ? cv0 + 2 : d == 3 ? cv0 - 1 : cv0 + row;
                double r = k.rnd(112 + d, i, yF, j);
                k.set(wu, yF + 1, wv, d % 2 == 0 ? P.BONEZ : P.BONEX);
                k.set(wu, yF + 2, wv, r < 0.3 ? P.SL : r < 0.55 ? P.CANDLE : r < 0.85 ? P.SKULL : P.WEB);
            }
            if (!ladderCell && k.rnd(120, i, yF, j) < 0.3) {
                int du = k.rnd(121, i, yF, j) < 0.5 ? 0 : 1, dv = k.rnd(122, i, yF, j) < 0.5 ? 0 : 1;
                k.set(cu0 + du, yF + 3, cv0 + dv, P.WEB);
            }
            if (!ladderCell && k.rnd(123, i, yF, j) < 0.14) k.set(cu0 + 1, yF + 3, cv0, P.SLH);
        }
    }

    /** Beinhaus ueber dem Katakomben-Eingang mit Treppe hinab nach Westen. */
    private void ossuary(Kit k) {
        if (!k.touches(-41, -25, -28, -12)) return;
        int u0 = -39, u1 = -30, v0 = -23, v1 = -14;
        k.fill(u0 - 1, FLOOR, v0 - 1, u1 + 1, FLOOR, v1 + 1, P.PBS);
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean eu = u == u0 || u == u1, ev = v == v0 || v == v1;
            for (int y = F; y <= -38; y++) {
                BlockState s;
                if (eu && ev) s = P.BASP;
                else if (eu || ev) s = y == F ? P.PBS : y == -38 ? P.CHPB : (u + v + y) % 3 == 0 ? P.BONE : k.rnd(131, u, y, v) < 0.2 ? P.CPBB : P.PBB;
                else s = Kit.AIR;
                k.set(u, y, v, s);
            }
            if (!eu && !ev) k.set(u, FLOOR, v, ((u + v) & 1) == 0 ? P.PDS : P.BONEX);
        }
        for (int r = 0; ; r++) {
            int y = -38 + r, a = v0 - 1 + r, b = v1 + 1 - r;
            if (a > b) break;
            for (int u = u0 - 1; u <= u1 + 1; u++) {
                if (a == b) k.set(u, y, a, P.DST);
                else {
                    k.set(u, y, a, Kit.stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.SOUTH, false));
                    k.set(u, y, b, Kit.stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.NORTH, false));
                }
            }
            if (r >= 1) for (int v = a + 1; v <= b - 1; v++) {
                k.set(u0, y, v, P.PBB);
                k.set(u1, y, v, P.PBB);
            }
        }
        k.set(u1, -34, -19, P.WSKULL);
        k.set(u1 + 1, -34, -18, P.COBS);
        // Tuer Ost
        k.clear(u1, F, -19, u1, F + 2, -18);
        k.fill(u1, F + 3, -20, u1, F + 3, -17, P.CHPB);
        post(k, u1 + 1, F, -21);
        post(k, u1 + 1, F, -16);
        // Schaedelregale innen
        for (int u = u0 + 1; u <= u1 - 1; u++) for (int s = -1; s <= 1; s += 2) {
            int v = s < 0 ? v0 + 1 : v1 - 1;
            if (u >= -36 && u <= -31) continue;
            k.set(u, F, v, P.BONEZ);
            k.set(u, F + 1, v, (u & 1) == 0 ? P.SKULL : P.CANDLE1);
        }
        k.set(u0 + 1, F, -19, P.CHPB);
        k.set(u0 + 1, F + 1, -19, P.WSKULL);
        k.set(u0 + 1, F, -18, P.SL);
        k.set(-34, -39, -18, P.SLH);
        // Treppenschacht
        for (int u = -37; u <= -31; u++) {
            for (int y = -51; y <= -45; y++) {
                k.set(u, y, -20, P.DSB);
                k.set(u, y, -17, P.DSB);
            }
            k.set(u, F, -20, P.PBW);
            k.set(u, F, -17, P.PBW);
        }
        for (int s = 0; s <= 5; s++) {
            int u = -31 - s, top = -45 - s;
            for (int v = -19; v <= -18; v++) {
                for (int y = -51; y < top; y++) k.set(u, y, v, P.DSB);
                k.set(u, top, v, Kit.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
                for (int y = top + 1; y <= -40; y++) k.set(u, y, v, Kit.AIR);
            }
        }
        for (int v = -19; v <= -18; v++) {
            k.set(-37, -50, v, P.DST);
            k.clear(-39, -49, v, -37, -47, v);
            k.set(-37, -46, v, P.DSB);
        }
        k.set(-37, -51, -19, P.DSB);
    }

    // ── Eingang: Schacht mit Wendeltreppe, Monolithenring ───────────────────

    private void entrance(Kit k) {
        if (!k.touches(-10, EV - 10, 10, EV + 10)) return;
        double perRad = 44 / (3 * Math.PI);
        for (int u = Math.max(k.u0(), -10); u <= Math.min(k.u1(), 10); u++) for (int v = Math.max(k.v0(), EV - 10); v <= Math.min(k.v1(), EV + 10); v++) {
            double d = Math.hypot(u, v - EV);
            if (d > 9.3) continue;
            int t = caveTop(k, u, v), y0 = t == NONE ? FLOOR + 1 : t + 1;
            if (d <= 2.4) {
                for (int y = FLOOR + 1; y <= 0; y++) {
                    boolean vein = (u == 0) != (v == EV) && d > 1.5;
                    k.set(u, y, v, vein ? ((y & 1) == 0 ? P.COBS : P.AME) : k.rnd(141, u, y, v) < 0.3 ? P.BOBS : P.OBS);
                }
            } else if (d <= 7.5) {
                for (int y = y0; y <= 0; y++) k.set(u, y, v, Kit.AIR);
            } else {
                for (int y = y0; y <= -1; y++) {
                    BlockState s = Math.floorMod(y, 6) == 0 ? P.CHDS : k.rnd(142, u, y, v) < 0.2 ? P.CDSB : P.DSB;
                    if (Math.floorMod(y, 6) == 3 && ((int) Math.round(Math.toDegrees(Math.atan2(v - EV, u))) + 360) % 45 < 8) s = P.COBS;
                    k.set(u, y, v, s);
                }
            }
            if (t != NONE) k.set(u, FLOOR, v, d > 7.5 ? P.PBS : ((u + v) & 1) == 0 ? P.PBB : P.DST);
        }
        helix(k, 0, EV, 0, FLOOR, 2.5, 6.5, 7.5, 90, perRad, P.PBB, P.PBW);
        for (int u = Math.max(k.u0(), -8); u <= Math.min(k.u1(), 8); u++) for (int v = Math.max(k.v0(), EV - 8); v <= Math.min(k.v1(), EV + 8); v++) {
            double d = Math.hypot(u, v - EV), a = angle(u, v - EV, 90);
            if (d >= 2.5 && d <= 7.5 && a >= TAU - Math.toRadians(25)) k.set(u, 0, v, P.PBB);
        }
    }

    private void monolith(Kit k, double bu, double bv, double ang, int h, double lean, int idx, double w) {
        double ru = Math.cos(ang), rv = Math.sin(ang), tu = -rv, tv = ru;
        int bb = (int) Math.ceil(lean) + 4;
        if (!k.touches((int) bu - bb, (int) bv - bb, (int) bu + bb, (int) bv + bb)) return;
        for (int u = (int) Math.floor(bu - 3); u <= (int) Math.ceil(bu + 3); u++) for (int v = (int) Math.floor(bv - 3); v <= (int) Math.ceil(bv + 3); v++) {
            if (!k.col(u, v)) continue;
            double d = Math.hypot(u - bu, v - bv);
            if (d <= 2.6) k.set(u, 0, v, d <= 1.6 ? P.CHPB : P.PBS);
            else if (d <= 3.4 && k.rnd(151, u, 0, v) < 0.3) k.set(u, 1, v, k.rnd(152, u, 0, v) < 0.5 ? P.BS : P.CDS);
        }
        for (int y = 1; y <= h; y++) {
            double f = Math.pow(y / (double) h, 1.4), cu = bu + ru * lean * f, cv = bv + rv * lean * f;
            double ta = y > h - 2 ? w * 0.5 : y > h - 4 ? w * 0.75 : w, rb = y > h - 2 ? 0.5 : 1.0;
            for (int u = (int) Math.floor(cu - 3); u <= (int) Math.ceil(cu + 3); u++) for (int v = (int) Math.floor(cv - 3); v <= (int) Math.ceil(cv + 3); v++) {
                if (!k.col(u, v)) continue;
                double du = u - cu, dv = v - cv, a = du * tu + dv * tv, b = du * ru + dv * rv;
                if (Math.abs(a) > ta || Math.abs(b) > rb) continue;
                BlockState s;
                boolean vein = b < -rb + 0.9 && Math.abs(a - 0.8 * Math.sin(y * 0.55 + idx)) < 0.55;
                if (vein) s = (y + idx) % 3 == 0 ? P.AME : P.COBS;
                else {
                    double r = k.rnd(153, u, y, v);
                    s = r < 0.55 ? P.OBS : r < 0.75 ? P.BOBS : r < 0.9 ? P.BS : P.BAS;
                }
                k.set(u, y, v, s);
            }
        }
    }

    private void surfaceRing(Kit k) {
        if (!k.touches(-48, EV - 48, 48, 92)) return;
        // Platz um den Schacht
        for (int u = Math.max(k.u0(), -12); u <= Math.min(k.u1(), 12); u++) for (int v = Math.max(k.v0(), EV - 12); v <= Math.min(k.v1(), EV + 12); v++) {
            double d = Math.hypot(u, v - EV), a = Math.toDegrees(Math.atan2(v - EV, u));
            if (d <= 7.5 || d > 12) continue;
            k.set(u, 0, v, d <= 8.6 ? P.PBS : Math.abs(d - 10.5) < 0.5 ? P.CHDS : ((u + v) & 1) == 0 ? P.PBB : P.DST);
            if (d <= 8.6 && !(a > 70 && a < 112)) k.set(u, 1, v, P.PBW);
            if (Math.abs(d - 11.5) < 0.5 && ((int) Math.round(a) + 360) % 30 < 3 && !(a > 70 && a < 112)) {
                k.set(u, 1, v, P.PBW);
                k.set(u, 2, v, P.SL);
            }
        }
        // Obelisk ueber dem Schacht
        if (k.touches(-3, EV - 3, 3, EV + 3)) for (int u = -2; u <= 2; u++) for (int v = EV - 2; v <= EV + 2; v++) {
            int du = Math.abs(u), dv = Math.abs(v - EV);
            for (int y = 1; y <= 28; y++) {
                boolean in = y <= 18 ? !(du == 2 && dv == 2) : y <= 24 ? du <= 1 && dv <= 1 : du == 0 && dv == 0;
                if (!in) continue;
                boolean face = (du == 0) != (dv == 0) && (du == 2 || dv == 2 || y > 18);
                BlockState s = y == 28 ? P.FROG : y == 18 || y == 24 ? P.CHPB : face ? ((y & 1) == 0 ? P.COBS : P.AME) : k.rnd(161, u, y, v) < 0.3 ? P.BOBS : P.OBS;
                k.set(u, y, v, s);
            }
            if (du == 0 && dv == 0) k.set(u, 29, v, P.CLU);
        }
        // Monolithenring
        for (int i = 0; i < 9; i++) {
            double ang = Math.toRadians(90 + 24 + i * 39 + (k.rnd(401, i, 0, 0) - 0.5) * 10);
            double rr = 15 + k.rnd(402, i, 0, 0) * 2;
            int h = 13 + (int) (k.rnd(403, i, 0, 0) * 14);
            double lean = 1.5 + k.rnd(404, i, 0, 0) * 3.5;
            monolith(k, Math.cos(ang) * rr, EV + Math.sin(ang) * rr, ang, h, lean, i, 1.6);
        }
        double[] sat = {20, 150, 205, 250, 300, 335};
        for (int i = 0; i < sat.length; i++) {
            double ang = Math.toRadians(sat[i] + (k.rnd(405, i, 0, 0) - 0.5) * 12), rr = 27 + k.rnd(406, i, 0, 0) * 6;
            monolith(k, Math.cos(ang) * rr, EV + Math.sin(ang) * rr, ang, 5 + (int) (k.rnd(407, i, 0, 0) * 5), 1 + k.rnd(408, i, 0, 0) * 2, i + 9, 1.1);
        }
        // umgestuerzte Monolithen
        k.line(-21, 1.5, EV + 8, -29, 1.5, EV + 14, 1.1, P.OBS);
        k.line(-29, 1.5, EV + 14, -31, 1.2, EV + 16, 0.8, P.BOBS);
        k.line(20, 1.5, EV - 11, 28, 1.5, EV - 16, 1.1, P.OBS);
        k.set(24, 2, EV - 13, P.COBS);
        k.set(-25, 2, EV + 11, P.COBS);
        // Weg zum Eingang
        for (int u = Math.max(k.u0(), -2); u <= Math.min(k.u1(), 2); u++) for (int v = Math.max(k.v0(), EV + 12); v <= Math.min(k.v1(), 91); v++) {
            k.set(u, 0, v, Math.abs(u) == 2 ? P.PBS : u == 0 ? P.PDS : k.rnd(162, u, 0, v) < 0.2 ? P.CDST : P.DST);
        }
        for (int v = EV + 14; v <= 90; v += 8) for (int s = -1; s <= 1; s += 2) {
            k.set(3 * s, 1, v, P.PBW);
            k.set(3 * s, 2, v, P.PBW);
            k.set(3 * s, 3, v, P.SL);
        }
        // Grabsteine
        for (int gu = -44; gu <= 44; gu += 5) for (int gv = EV - 44; gv <= 90; gv += 5) {
            int u = gu + (int) (k.rnd(163, gu, 0, gv) * 3), v = gv + (int) (k.rnd(164, gu, 0, gv) * 3);
            double d = Math.hypot(u, v - EV);
            if (d < 20 || d > 46 || Math.abs(u) <= 4 || k.rnd(165, gu, 0, gv) > 0.4) continue;
            double r = k.rnd(166, gu, 0, gv);
            k.set(u, 1, v, r < 0.4 ? P.CHDS : r < 0.7 ? P.PBS : P.CDSB);
            if (r > 0.7) k.set(u, 2, v, P.PBW);
            else if (r < 0.15) k.set(u, 2, v, P.SKULL);
            k.set(u, 0, v + 1, P.SOUL);
        }
    }

    // ── Bewohner der Stadt ──────────────────────────────────────────────────

    private void cityMobs(Kit k) {
        k.mob(16, F, 33, WARDEN);
        k.mob(-16, F, 33, SHADE);
        k.mob(-42, F, 14, SHADE);
        k.mob(42, F, -28, SHADE);
        k.mob(-56, F, 28, WARDEN);
        k.mob(28, F, 56, SHADE);
        k.mob(-18, -31, -18, SHADE);
        k.mob(22, -35, 0, WARDEN);
        k.spawner(14, F, 31, SHADE);
        k.spawner(-44, F, 16, SHADE);
        k.spawner(54, F, 30, WARDEN);
    }
}
