package com.space_beyond.dim.dungeon.design;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import com.hbm_m.block.ModBlocks;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Sporos - "Der Stock" (The Hive): ein lebender Pilzbau. Oben ein riesiger Myzelhuegel mit Sporenschloten, durch dessen
 * Kuppel der Stiel eines kolossalen Fliegenpilzes bricht; innen eine hohle Vorhalle, eine Wendelrampe um den Stiel hinab
 * in die Herzkammer. Von dort fuehren gewundene Roehren zu den drei Pruefungen:
 * <ul>
 *   <li>West: drei Brutkammern hintereinander voller Eiersaecke und Spawner (Gauntlet), Schluessel auf dem Brutnest.</li>
 *   <li>Ost: Wurzellabyrinth - 3D-Irrgarten aus engen Wurzelroehren auf drei Hoehen, Ranken zum Klettern.</li>
 *   <li>Suedwest: Kinderstube mit Brutnestern, der Siegelwaechter (Mycobrute) traegt den dritten Schluessel.</li>
 * </ul>
 * Nord: Vorkammer mit dem Siegeltor in der organischen Wand, dahinter die Thronkammer der Myzelkoenigin.
 */
public final class SporosDungeon extends CustomDungeon {

    static final int R = 88;

    @Override public Stage stage() { return Stage.SPOROS; }
    @Override public int radius() { return R; }
    @Override public BlockState surface() { return Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SPOROS_SILT.get()); }
    @Override public BlockState filler() { return Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SPOROS_STONE.get()); }
    @Override public int minY() { return -60; }
    @Override public int maxY() { return 56; }
    @Override public int minBase() { return 66; }
    @Override public int maxBase() { return 186; }
    @Override public int[] entry() { return new int[] {0, 1, 82}; }
    @Override public String boss() { return "space_beyond:mycelial_queen"; }

    @Override
    public boolean fitsAt(CelestialWorld world, int x, int z) {
        BiomeGenBaseCelestial biome = world.getBiomeGenForCoords(x, z);
        if (biome == com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.lake || biome == com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.river
                || biome == com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.coast || biome == com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.swamp) return false;
        for (int dx = -96; dx <= 96; dx += 48) for (int dz = -96; dz <= 96; dz += 48) {
            if (com.space_beyond.dim.sporos.city.SporosCities.cityAt(world.getSeed(), (x + dx) >> 4, (z + dz) >> 4) != null) return false;
        }
        return true;
    }

    // ───────────────────────── Geometrie ─────────────────────────

    /** Blockwahl je Position. */
    private interface Mat {
        BlockState at(int u, int y, int v);
    }

    /** Knubbeliges Ellipsoid (Hoehle/Huegel) mit optionalem Zuschnitt in y und v. */
    private static final class Ell {
        final double cu, cy, cv, ru, ry, rv, amp;
        final long salt;
        int yMin = -10000, yMax = 10000, vMin = -10000, vMax = 10000;

        Ell(double cu, double cy, double cv, double ru, double ry, double rv, double amp, long salt) {
            this.cu = cu; this.cy = cy; this.cv = cv; this.ru = ru; this.ry = ry; this.rv = rv; this.amp = amp; this.salt = salt;
        }

        Ell ys(int a, int b) { yMin = a; yMax = b; return this; }
        Ell vs(int a, int b) { vMin = a; vMax = b; return this; }

        boolean in(Kit k, int u, int y, int v, double t) {
            double nu = (u - cu) / (ru + t), ny = (y - cy) / (ry + t), nv = (v - cv) / (rv + t);
            double d2 = nu * nu + ny * ny + nv * nv, lo = 1 - 1.2 * amp, hi = 1 + 1.2 * amp;
            if (d2 <= lo * lo) return true;
            if (d2 > hi * hi) return false;
            return Math.sqrt(d2) <= 1 + amp * (lump(k, salt, u, y, v) - 0.5) * 2.4;
        }

        /** Hohlraum (mit Zuschnitt). */
        boolean cav(Kit k, int u, int y, int v) {
            return y >= yMin && y <= yMax && v >= vMin && v <= vMax && in(k, u, y, v, 0);
        }

        /** Begehbarer Radius auf Fusshoehe (ohne Knubbel), fuer Platzierungen. */
        double fu() { return ru * Math.sqrt(Math.max(0, 1 - sq((yMin - cy) / ry))); }
        double fv() { return rv * Math.sqrt(Math.max(0, 1 - sq((yMin - cy) / ry))); }
        /** Deckenhoehe (ohne Knubbel). */
        double ceil(double u, double v) { return cy + ry * Math.sqrt(Math.max(0, 1 - sq((u - cu) / ru) - sq((v - cv) / rv))); }
    }

    static double sq(double a) { return a * a; }

    static double lump(Kit k, long s, int u, int y, int v) {
        return (k.noise(s, u, v, 7) + k.noise(s + 1, u + 2 * v, y, 5) + k.noise(s + 2, v - 3 * u, y, 6)) / 3.0;
    }

    /** Fuellt das Ellipsoid (Zuschnitt beachtet) mit {@code m}. */
    static void fillEll(Kit k, Ell e, double t, Mat m) {
        double ext = 1 + 1.3 * e.amp;
        int uLo = (int) Math.floor(e.cu - (e.ru + t) * ext), uHi = (int) Math.ceil(e.cu + (e.ru + t) * ext);
        int vLo = (int) Math.floor(e.cv - (e.rv + t) * ext), vHi = (int) Math.ceil(e.cv + (e.rv + t) * ext);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        int yLo = Math.max(e.yMin, (int) Math.floor(e.cy - (e.ry + t) * ext)), yHi = Math.min(e.yMax, (int) Math.ceil(e.cy + (e.ry + t) * ext));
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(Math.max(vLo, e.vMin), k.v0()); v <= Math.min(Math.min(vHi, e.vMax), k.v1()); v++) {
            for (int y = yLo; y <= yHi; y++) if (e.in(k, u, y, v, t)) k.set(u, y, v, m.at(u, y, v));
        }
    }

    static void carve(Kit k, Ell e) {
        fillEll(k, e, 0, (u, y, v) -> Kit.AIR);
    }

    /** Huelle der Dicke t um den Hohlraum (alles im grossen Ellipsoid, was nicht Hohlraum ist; unten bis zwei unter den Boden). */
    static void hull(Kit k, Ell e, double t, Mat m) {
        double ext = 1 + 1.3 * e.amp;
        int uLo = (int) Math.floor(e.cu - (e.ru + t) * ext), uHi = (int) Math.ceil(e.cu + (e.ru + t) * ext);
        int vLo = (int) Math.floor(e.cv - (e.rv + t) * ext), vHi = (int) Math.ceil(e.cv + (e.rv + t) * ext);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        int yLo = Math.max(e.yMin - 2, (int) Math.floor(e.cy - (e.ry + t) * ext)), yHi = (int) Math.ceil(e.cy + (e.ry + t) * ext);
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(vLo, k.v0()); v <= Math.min(vHi, k.v1()); v++) {
            for (int y = yLo; y <= yHi; y++) if (e.in(k, u, y, v, t) && !e.cav(k, u, y, v)) k.set(u, y, v, m.at(u, y, v));
        }
    }

    /** Boden (y = yMin - 1) unter dem Hohlraum. */
    static void floor(Kit k, Ell e, Mat m) {
        int y = e.yMin;
        double ext = 1 + 1.3 * e.amp;
        int uLo = (int) Math.floor(e.cu - (e.ru + 1) * ext), uHi = (int) Math.ceil(e.cu + (e.ru + 1) * ext);
        int vLo = (int) Math.floor(e.cv - (e.rv + 1) * ext), vHi = (int) Math.ceil(e.cv + (e.rv + 1) * ext);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(Math.max(vLo, e.vMin), k.v0()); v <= Math.min(Math.min(vHi, e.vMax), k.v1()); v++) {
            if (e.in(k, u, y, v, 0.8)) k.set(u, y - 1, v, m.at(u, y - 1, v));
        }
    }

    /** Oberste Luftzelle der Spalte (oder MIN_VALUE). */
    static int top(Kit k, Ell e, int u, int v) {
        int yHi = (int) Math.ceil(e.cy + e.ry * (1 + 1.3 * e.amp));
        for (int y = Math.min(yHi, e.yMax); y >= e.yMin; y--) if (e.cav(k, u, y, v)) return y;
        return Integer.MIN_VALUE;
    }

    /** Dicke Linie mit Material. */
    static void rope(Kit k, double ua, double ya, double va, double ub, double yb, double vb, double r, Mat m) {
        int uLo = (int) Math.floor(Math.min(ua, ub) - r - 1), uHi = (int) Math.ceil(Math.max(ua, ub) + r + 1);
        int vLo = (int) Math.floor(Math.min(va, vb) - r - 1), vHi = (int) Math.ceil(Math.max(va, vb) + r + 1);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        int yLo = (int) Math.floor(Math.min(ya, yb) - r - 1), yHi = (int) Math.ceil(Math.max(ya, yb) + r + 1);
        double du = ub - ua, dy = yb - ya, dv = vb - va, len2 = du * du + dy * dy + dv * dv;
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(vLo, k.v0()); v <= Math.min(vHi, k.v1()); v++) {
            for (int y = yLo; y <= yHi; y++) {
                double t = len2 < 1e-9 ? 0 : ((u - ua) * du + (y - ya) * dy + (v - va) * dv) / len2;
                t = Math.max(0, Math.min(1, t));
                double pu = ua + du * t - u, py = ya + dy * t - y, pv = va + dv * t - v;
                if (pu * pu + py * py + pv * pv <= r * r + 0.25) k.set(u, y, v, m.at(u, y, v));
            }
        }
    }

    /** Polylinie (Punkte {u, y, v}). */
    static void path(Kit k, double[][] p, double dy, double r, Mat m) {
        for (int i = 0; i + 1 < p.length; i++) rope(k, p[i][0], p[i][1] + dy, p[i][2], p[i + 1][0], p[i + 1][1] + dy, p[i + 1][2], r, m);
    }

    /** Kuppelschale (oberhalb yMin) mit Material. */
    static void dome(Kit k, double cu, double cy, double cv, double ru, double ry, double rv, double thick, Mat m, int yMin) {
        int uLo = (int) Math.floor(cu - ru - 1), uHi = (int) Math.ceil(cu + ru + 1), vLo = (int) Math.floor(cv - rv - 1), vHi = (int) Math.ceil(cv + rv + 1);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        double mr = Math.min(ru, Math.min(ry, rv));
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(vLo, k.v0()); v <= Math.min(vHi, k.v1()); v++) {
            for (int y = Math.max(yMin, (int) Math.floor(cy - ry - 1)); y <= (int) Math.ceil(cy + ry + 1); y++) {
                double d = Math.sqrt(sq((u - cu) / ru) + sq((y - cy) / ry) + sq((v - cv) / rv));
                if (d <= 1.0 && (1 - d) * mr < thick) k.set(u, y, v, m.at(u, y, v));
            }
        }
    }

    /** Waagerechte Scheibe (Ellipse) in Hoehe y. */
    static void disk(Kit k, double cu, double cv, int y, double ru, double rv, Mat m) {
        int uLo = (int) Math.floor(cu - ru - 1), uHi = (int) Math.ceil(cu + ru + 1), vLo = (int) Math.floor(cv - rv - 1), vHi = (int) Math.ceil(cv + rv + 1);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(vLo, k.v0()); v <= Math.min(vHi, k.v1()); v++) {
            if (sq((u - cu) / ru) + sq((v - cv) / rv) <= 1.0 + 0.25 / (ru * rv)) k.set(u, y, v, m.at(u, y, v));
        }
    }

    // ───────────────────────── Palette ─────────────────────────

    private static final class P {
        final Kit k;
        final BlockState mud = Kit.b(Blocks.MUD), pmud = Kit.b(Blocks.PACKED_MUD), moss = Kit.b(Blocks.MOSS_BLOCK), myc = Kit.b(Blocks.MYCELIUM);
        final BlockState brownM = Kit.b(Blocks.BROWN_MUSHROOM_BLOCK), redM = Kit.b(Blocks.RED_MUSHROOM_BLOCK), stem = Kit.b(Blocks.MUSHROOM_STEM);
        final BlockState shroom = Kit.b(Blocks.SHROOMLIGHT), wart = Kit.b(Blocks.NETHER_WART_BLOCK), rooted = Kit.b(Blocks.ROOTED_DIRT);
        final BlockState slime = Kit.b(Blocks.SLIME_BLOCK), honey = Kit.b(Blocks.HONEY_BLOCK), comb = Kit.b(Blocks.HONEYCOMB_BLOCK);
        final BlockState ochre = Kit.b(Blocks.OCHRE_FROGLIGHT), pearl = Kit.b(Blocks.PEARLESCENT_FROGLIGHT), verdant = Kit.b(Blocks.VERDANT_FROGLIGHT);
        final BlockState bone = Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.Y), nylium = Kit.b(Blocks.CRIMSON_NYLIUM), mossCarpet = Kit.b(Blocks.MOSS_CARPET);
        final BlockState hRoots = Kit.b(Blocks.HANGING_ROOTS), blossom = Kit.b(Blocks.SPORE_BLOSSOM), cobweb = Kit.b(Blocks.COBWEB);
        final BlockState weep = Kit.b(Blocks.WEEPING_VINES), weepPlant = Kit.b(Blocks.WEEPING_VINES_PLANT);
        final BlockState twist = Kit.b(Blocks.TWISTING_VINES), twistPlant = Kit.b(Blocks.TWISTING_VINES_PLANT);
        final BlockState brownShroom = Kit.b(Blocks.BROWN_MUSHROOM), redShroom = Kit.b(Blocks.RED_MUSHROOM), crimsonRoots = Kit.b(Blocks.CRIMSON_ROOTS);
        final BlockState sStone = Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SPOROS_STONE.get()), silt = Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SPOROS_SILT.get());
        final BlockState fStem = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.FUNGAL_STEM.get()), fCap = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.FUNGAL_CAP.get());
        final BlockState hLog = Kit.axis(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_LOG.get(), Direction.Axis.Y);
        final BlockState hLogX = Kit.axis(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_LOG.get(), Direction.Axis.X);
        final BlockState dLog = Kit.axis(com.space_beyond.dim.sporos.flora.SporosFlora.DEAD_HYPHE_LOG.get(), Direction.Axis.Y);
        final BlockState dLogZ = Kit.axis(com.space_beyond.dim.sporos.flora.SporosFlora.DEAD_HYPHE_LOG.get(), Direction.Axis.Z);
        final BlockState cushion = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.MOSS_CUSHION.get());
        final BlockState bloom = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.SPORE_BLOOM.get()), glowCap = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.GLOW_CAP.get());
        final BlockState pod = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.SPORE_POD.get()), fence = Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_FENCE.get());
        final BlockState slab = Kit.slab(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_SLAB.get(), false);
        final BlockState mycVine = CityPalette.with(Kit.b(com.space_beyond.dim.sporos.flora.SporosFlora.MYCEL_VINE.get()), BlockStateProperties.UP, true);

        P(Kit k) {
            this.k = k;
        }

        /** Hoehlenwand: Schlamm, Myzel, Pilzfleisch mit roten Adern und Leuchtpusteln. */
        BlockState wall(int u, int y, int v) {
            double n = k.noise(101, u + 2 * y, v - y, 9), r = k.rnd(102, u, y, v);
            if (r < 0.012) return shroom;
            if (Math.abs(n - 0.5) < 0.022) return wart;
            if (n < 0.27) return r < 0.6 ? mud : pmud;
            if (n < 0.40) return r < 0.5 ? brownM : myc;
            if (n < 0.47) return r < 0.4 ? fCap : moss;
            if (n < 0.60) return r < 0.55 ? moss : rooted;
            if (n < 0.72) return r < 0.5 ? myc : brownM;
            return r < 0.5 ? sStone : pmud;
        }

        /** Huegel: Myzelkuppe, gelbe Pilzhaut, weisse Hyphenstraenge. */
        BlockState mound(int u, int y, int v) {
            double n = k.noise(111, u + y, v - 2 * y, 8), r = k.rnd(112, u, y, v);
            if (r < 0.008) return ochre;
            if (Math.abs(n - 0.5) < 0.025) return r < 0.5 ? stem : fStem;
            if (n < 0.3) return r < 0.7 ? myc : brownM;
            if (n < 0.45) return r < 0.6 ? fCap : myc;
            if (n < 0.62) return r < 0.5 ? brownM : moss;
            if (n < 0.75) return r < 0.6 ? myc : mud;
            return r < 0.5 ? moss : fCap;
        }

        BlockState floor(int u, int y, int v) {
            double n = k.noise(121, u, v, 6), r = k.rnd(122, u, y, v);
            if (n < 0.28) return r < 0.7 ? moss : cushion;
            if (n < 0.45) return r < 0.6 ? myc : moss;
            if (n < 0.6) return r < 0.6 ? mud : rooted;
            if (n < 0.75) return r < 0.5 ? cushion : myc;
            return r < 0.5 ? moss : pmud;
        }

        /** Wurzelwerk (Labyrinth). */
        BlockState root(int u, int y, int v) {
            double n = k.noise(131, u - y, v + 2 * y, 5), r = k.rnd(132, u, y, v);
            if (r < 0.01) return shroom;
            if (n < 0.3) return r < 0.6 ? hLog : rooted;
            if (n < 0.45) return r < 0.5 ? dLog : pmud;
            if (n < 0.6) return r < 0.5 ? rooted : mud;
            if (n < 0.75) return r < 0.7 ? hLogX : moss;
            return r < 0.5 ? dLogZ : rooted;
        }

        BlockState stemMat(int u, int y, int v) {
            double r = k.rnd(141, u, y, v);
            if (r < 0.02) return shroom;
            return k.noise(142, u + y, v, 3) < 0.4 ? fStem : stem;
        }

        BlockState capRed(int u, int y, int v) {
            double r = k.rnd(151, u, y, v);
            if (r < 0.025) return pearl;
            return r < 0.13 ? stem : redM;
        }

        BlockState capBrown(int u, int y, int v) {
            double r = k.rnd(152, u, y, v);
            return r < 0.12 ? fCap : brownM;
        }

        BlockState gills(int u, int y, int v) {
            double r = k.rnd(153, u, y, v);
            return r < 0.07 ? shroom : r < 0.5 ? stem : fStem;
        }

        BlockState plantOn(BlockState soil, double r) {
            if (soil == moss || soil == cushion || soil == mud || soil == rooted) {
                if (r < 0.25) return bloom;
                if (r < 0.45) return glowCap;
                if (r < 0.6) return pod;
                return r < 0.8 ? mossCarpet : brownShroom;
            }
            if (soil == myc) return r < 0.4 ? redShroom : r < 0.7 ? brownShroom : glowCap;
            if (soil == nylium) return r < 0.6 ? crimsonRoots : glowCap;
            return null;
        }
    }

    // ───────────────────────── Lageplan ─────────────────────────

    // Huegel und Vorhalle
    static final double MU = 0, MV = 6;
    // Kammern (Fusshoehe = yMin)
    static Ell mound() { return new Ell(0, -6, 6, 36, 32, 34, 0.07, 201).ys(-2, 60); }
    static Ell atrium() { return new Ell(0, -6, 6, 30, 26, 28, 0.05, 202).ys(1, 60); }
    static Ell hub() { return new Ell(0, -20, 6, 26, 12, 22, 0.07, 203).ys(-27, 0); }
    static Ell b1() { return new Ell(-44, -25, -5, 12, 8, 10, 0.08, 204).ys(-28, 0); }
    static Ell b2() { return new Ell(-63, -22, -25, 15, 9, 13, 0.08, 205).ys(-26, 0); }
    static Ell b3() { return new Ell(-64, -19, -55, 14, 11, 13, 0.07, 206).ys(-25, 0); }
    static Ell nursery() { return new Ell(-32, -31, 58, 19, 10, 15, 0.07, 207).ys(-37, 0); }
    static Ell ante() { return new Ell(0, -41, -28, 8, 6, 7, 0.06, 208).ys(-43, 0).vs(-35, 100); }
    static Ell throne() { return new Ell(0, -36, -60, 24, 18, 21, 0.05, 209).ys(-43, 0).vs(-100, -39); }

    static final int FT = -44;               // Thronboden
    static final int GATE_V = -36;            // Torebene

    // Roehren: Fusspunkte {u, fuss, v}
    static final double[][] T_W1 = {{-16, -27, 4}, {-24, -27, 1}, {-32, -28, -2}, {-40, -28, -4}};
    static final double[][] T_W2 = {{-49, -28, -9}, {-54, -27, -15}, {-59, -26, -20}};
    static final double[][] T_W3 = {{-64, -26, -31}, {-68, -26, -38}, {-65, -25, -48}};
    static final double[][] T_N = {{0, -27, -8}, {-8, -29, -14}, {-14, -33, -20}, {-13, -38, -27}, {-5, -43, -28}};
    static final double[][] T_S = {{-8, -27, 18}, {-12, -29, 28}, {-19, -32, 37}, {-25, -35, 44}, {-31, -37, 49}};
    static final double[][] T_E = {{16, -27, 6}, {24, -26, 8}, {31, -24, 9}, {36, -22, 9}};
    static final double[][][] TUNNELS = {T_W1, T_W2, T_W3, T_N, T_S, T_E};

    // Labyrinth
    static final int MI = 5, MJ = 7, ML = 3, START_J = 4;
    static int mu(int i) { return 36 + 9 * i; }
    static int mv(int j) { return -27 + 9 * j; }
    static int mf(int l) { return -22 - 9 * l; }

    /** Labyrinth: Kanten und ausgezeichnete Zellen (deterministisch je Dungeon). */
    private static final class Maze {
        final boolean[][][] eu = new boolean[MI][MJ][ML], ev = new boolean[MI][MJ][ML], ey = new boolean[MI][MJ][ML];
        final int[] dist = new int[MI * MJ * ML];
        int key;
        final List<Integer> ends = new ArrayList<>();

        static int id(int i, int j, int l) { return (l * MJ + j) * MI + i; }

        Maze(Kit k) {
            boolean[] seen = new boolean[MI * MJ * ML];
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[] {0, START_J, 0});
            seen[id(0, START_J, 0)] = true;
            int step = 0;
            while (!stack.isEmpty()) {
                int[] c = stack.peek();
                List<int[]> nb = new ArrayList<>();
                int[][] dirs = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
                for (int[] d : dirs) {
                    int i = c[0] + d[0], j = c[1] + d[1], l = c[2] + d[2];
                    if (i < 0 || j < 0 || l < 0 || i >= MI || j >= MJ || l >= ML || seen[id(i, j, l)]) continue;
                    int w = d[2] != 0 ? 1 : 3;
                    for (int q = 0; q < w; q++) nb.add(new int[] {i, j, l});
                }
                if (nb.isEmpty()) {
                    stack.pop();
                    continue;
                }
                int[] n = nb.get(Math.min(nb.size() - 1, (int) (k.rnd(5001, step++, 0, 0) * nb.size())));
                link(c[0], c[1], c[2], n[0], n[1], n[2]);
                seen[id(n[0], n[1], n[2])] = true;
                stack.push(n);
            }
            // ein paar Schleifen
            for (int l = 0; l < ML; l++) for (int j = 0; j < MJ; j++) for (int i = 0; i < MI; i++) {
                if (i + 1 < MI && !eu[i][j][l] && k.rnd(5002, i, l, j) < 0.07) eu[i][j][l] = true;
                if (j + 1 < MJ && !ev[i][j][l] && k.rnd(5003, i, l, j) < 0.07) ev[i][j][l] = true;
            }
            // Abstaende (BFS)
            java.util.Arrays.fill(dist, -1);
            ArrayDeque<int[]> q = new ArrayDeque<>();
            q.add(new int[] {0, START_J, 0});
            dist[id(0, START_J, 0)] = 0;
            int best = 0;
            key = id(0, START_J, 0);
            while (!q.isEmpty()) {
                int[] c = q.poll();
                int dc = dist[id(c[0], c[1], c[2])];
                if (dc > best) { best = dc; key = id(c[0], c[1], c[2]); }
                for (int[] n : neighbours(c[0], c[1], c[2])) {
                    int ni = id(n[0], n[1], n[2]);
                    if (dist[ni] < 0) { dist[ni] = dc + 1; q.add(n); }
                }
            }
            for (int l = 0; l < ML; l++) for (int j = 0; j < MJ; j++) for (int i = 0; i < MI; i++) {
                int ci = id(i, j, l);
                if (ci != key && ci != id(0, START_J, 0) && neighbours(i, j, l).size() == 1) ends.add(ci);
            }
        }

        void link(int a, int b, int c, int i, int j, int l) {
            if (l != c) ey[a][b][Math.min(c, l)] = true;
            else if (i != a) eu[Math.min(a, i)][b][c] = true;
            else ev[a][Math.min(b, j)][c] = true;
        }

        List<int[]> neighbours(int i, int j, int l) {
            List<int[]> r = new ArrayList<>();
            if (i + 1 < MI && eu[i][j][l]) r.add(new int[] {i + 1, j, l});
            if (i > 0 && eu[i - 1][j][l]) r.add(new int[] {i - 1, j, l});
            if (j + 1 < MJ && ev[i][j][l]) r.add(new int[] {i, j + 1, l});
            if (j > 0 && ev[i][j - 1][l]) r.add(new int[] {i, j - 1, l});
            if (l + 1 < ML && ey[i][j][l]) r.add(new int[] {i, j, l + 1});
            if (l > 0 && ey[i][j][l - 1]) r.add(new int[] {i, j, l - 1});
            return r;
        }

        boolean up(int i, int j, int l) { return l > 0 && ey[i][j][l - 1]; }      // Schacht nach oben (zur Ebene l-1)
        boolean down(int i, int j, int l) { return l + 1 < ML && ey[i][j][l]; }   // Schacht nach unten
    }

    // ───────────────────────── Bauen ─────────────────────────

    @Override
    protected void build(Kit k) {
        P p = new P(k);
        Maze mz = new Maze(k);
        Ell mound = mound(), atrium = atrium(), hub = hub(), b1 = b1(), b2 = b2(), b3 = b3(), nur = nursery(), ante = ante(), thr = throne();
        Ell[] rooms = {hub, b1, b2, b3, nur, ante, thr};

        // 1) Oberflaeche: Myzelteppich und Pfad
        groundCarpet(k, p);

        // 2) Huellen
        fillEll(k, mound, 0, p::mound);
        double[][] lobes = {{-26, -4, 26, 15, 13, 13}, {28, -4, -8, 16, 14, 15}, {-22, -4, -22, 14, 12, 14}, {24, -4, 26, 12, 11, 12}, {4, -4, -30, 13, 11, 12}};
        for (int i = 0; i < lobes.length; i++) {
            double[] l = lobes[i];
            fillEll(k, new Ell(l[0], l[1], l[2], l[3], l[4], l[5], 0.1, 220 + i).ys(-2, 60), 0, p::mound);
        }
        k.tube(MU, MV, -11, 0, 11.5, 3, p.sStone, null);
        tubeMat(k, MU, MV, -12, 0, 11.5, 3, p::wall);
        for (Ell e : rooms) hull(k, e, 3.2, p::wall);
        for (double[][] t : TUNNELS) path(k, t, 2, 4.6, p::wall);
        mazeSkin(k, p, mz);

        // 3) Hohlraeume
        carve(k, atrium);
        k.line(0, 4, 52, 0, 4, 26, 4.4, Kit.AIR);                     // Maul
        floorAtrium(k, p, atrium);
        k.cylinder(MU, MV, -10, 0, 9.0, Kit.AIR);                      // Schlund zur Herzkammer
        for (Ell e : rooms) carve(k, e);
        for (double[][] t : TUNNELS) path(k, t, 2, 2.5, (u, y, v) -> Kit.AIR);
        k.clear(-4, -43, -35, 4, -38, -33);                           // vor dem Tor
        k.clear(-2, -43, -41, 2, -39, -38);                           // hinter dem Tor
        mazeCarve(k, p, mz);

        // 4) Boeden
        for (Ell e : rooms) floor(k, e, p::floor);
        throneFloor(k, p, thr);

        // 5) Wahrzeichen und Bauwerke
        megaShroom(k, p);
        spiral(k, p);
        chimneys(k, p);
        pustules(k, p);
        maw(k, p);
        tendrils(k, p);
        surfaceShrooms(k, p);

        // 6) Raeume ausstatten
        atriumDeco(k, p, atrium);
        hubDeco(k, p, hub);
        broodDeco(k, p, b1, b2, b3);
        nurseryDeco(k, p, nur);
        mazeDeco(k, p, mz);
        anteDeco(k, p, ante);
        throneDeco(k, p, thr);

        // 7) Siegeltor in der Fleischwand
        k.sealGate(0, FT, GATE_V, Direction.SOUTH, 2, 5, p.stem, p.shroom);

        // 8) Plan
        plan(k, mz);
    }

    /** Senkrechte Roehrenwand mit Material. */
    static void tubeMat(Kit k, double cu, double cv, int y0, int y1, double r, double thick, Mat m) {
        int ir = (int) Math.ceil(r) + 1;
        if (!k.touches((int) cu - ir, (int) cv - ir, (int) cu + ir, (int) cv + ir)) return;
        for (int u = Math.max((int) cu - ir, k.u0()); u <= Math.min((int) cu + ir, k.u1()); u++) for (int v = Math.max((int) cv - ir, k.v0()); v <= Math.min((int) cv + ir, k.v1()); v++) {
            double d = Math.sqrt(sq(u - cu) + sq(v - cv));
            if (d > r + 0.5 || d <= r - thick + 0.5) continue;
            for (int y = y0; y <= y1; y++) k.set(u, y, v, m.at(u, y, v));
        }
    }

    // ── Oberflaeche ──

    static double pathU(double v) {
        return 3 * Math.sin(v / 9.0);
    }

    void groundCarpet(Kit k, P p) {
        for (int u = Math.max(-62, k.u0()); u <= Math.min(62, k.u1()); u++) for (int v = Math.max(-58, k.v0()); v <= Math.min(R, k.v1()); v++) {
            double d = Math.sqrt(sq(u - MU) + sq((v - MV) * 36.0 / 34.0));
            boolean onPath = v >= 36 && v <= 85 && Math.abs(u - pathU(v)) <= 3.2;
            double n = k.noise(301, u, v, 9);
            boolean carpet = d < 40 + 14 * n;
            if (!carpet && !onPath) continue;
            if (onPath) {
                double r = k.rnd(302, u, 0, v);
                boolean edge = Math.abs(u - pathU(v)) > 2.2;
                k.set(u, 0, v, edge ? (r < 0.5 ? p.cushion : p.moss) : r < 0.45 ? p.pmud : r < 0.75 ? p.myc : r < 0.9 ? p.rooted : p.mud);
                k.set(u, -1, v, p.mud);
                if (edge && k.rnd(303, u, 1, v) < 0.18) k.set(u, 1, v, k.rnd(304, u, 1, v) < 0.6 ? p.glowCap : p.bloom);
                continue;
            }
            BlockState s = p.floor(u, 0, v);
            if (d > 44 + 10 * n) s = n < 0.5 ? p.myc : p.silt;
            k.set(u, 0, v, s);
            double r = k.rnd(305, u, 1, v);
            if (r < 0.07) {
                BlockState pl = p.plantOn(s, k.rnd(306, u, 1, v));
                if (pl != null) k.set(u, 1, v, pl);
            }
        }
    }

    void floorAtrium(Kit k, P p, Ell a) {
        for (int u = Math.max(-32, k.u0()); u <= Math.min(32, k.u1()); u++) for (int v = Math.max(-26, k.v0()); v <= Math.min(52, k.v1()); v++) {
            boolean mouth = Math.abs(u) <= 5 && v >= 26;
            if (!mouth && !a.in(k, u, 1, v, 0.8)) continue;
            k.set(u, 0, v, p.floor(u, 0, v));
            k.set(u, -1, v, p.mud);
        }
    }

    /** Kolossaler Fliegenpilz: Stiel von der Herzkammer bis ueber die Kuppel, Hut 44 breit. */
    void megaShroom(Kit k, P p) {
        k.cylinder(MU, MV, -28, 41, 3.5, Kit.AIR);
        for (int u = Math.max(-5, k.u0()); u <= Math.min(5, k.u1()); u++) for (int v = Math.max(1, k.v0()); v <= Math.min(11, k.v1()); v++) {
            double d = Math.sqrt(sq(u - MU) + sq(v - MV));
            if (d > 3.55) continue;
            for (int y = -28; y <= 41; y++) {
                BlockState s = p.stemMat(u, y, v);
                if (d > 2.6 && (y + (int) (Math.atan2(v - MV, u - MU) * 4)) % 9 == 0) s = p.shroom;   // Leuchtring-Spirale
                k.set(u, y, v, s);
            }
        }
        // Wurzelanlauf unten und Kragen (Ring) unter dem Hut
        rope(k, MU, -28, MV, MU, -26, MV, 5.2, p::stemMat);
        k.cylinder(MU, MV, -25, -25, 4.6, p.stem);
        dome(k, MU, 26, MV, 7, 3, 7, 1.2, p::stemMat, 26);
        // Hut
        dome(k, MU, 41, MV, 22, 10, 22, 2.2, p::capRed, 41);
        disk(k, MU, MV, 41, 20.5, 20.5, (u, y, v) -> {
            double d = Math.sqrt(sq(u - MU) + sq(v - MV));
            if (d < 3.6) return p.stem;
            double a = Math.atan2(v - MV, u - MU);
            return ((int) Math.floor((a + Math.PI) * 18 / Math.PI)) % 2 == 0 ? p.gills(u, y, v) : p.capRed(u, y, v);
        });
        for (int i = 0; i < 12; i++) {
            double a = Math.PI * 2 * i / 12;
            int u = (int) Math.round(MU + Math.cos(a) * 13), v = (int) Math.round(MV + Math.sin(a) * 13);
            k.set(u, 40, v, p.shroom);
            if (i % 2 == 0) for (int y = 39; y >= 35; y--) k.set(u, y, v, y == 35 ? p.weep : p.weep);
        }
    }

    /** Wendelrampe aus Pilzkonsolen um den Stiel, von der Herzkammer (y -28) bis zum Hallenboden (y 0). */
    static final double SP_RISE = 3.0, SP_A0 = 1.5708 - (28 / 3.0 - 2 * Math.PI);

    void spiral(Kit k, P p) {
        if (!k.touches(-11, -5, 11, 17)) return;
        int y0 = -28, y1 = 0;
        double endA = SP_A0 + (y1 - y0) / SP_RISE;
        for (int u = Math.max(-11, k.u0()); u <= Math.min(11, k.u1()); u++) for (int v = Math.max(-5, k.v0()); v <= Math.min(17, k.v1()); v++) {
            double d = Math.sqrt(sq(u - MU) + sq(v - MV));
            if (d < 3.6 || d > 10.05) continue;
            double ang = Math.atan2(v - MV, u - MU) - SP_A0;
            ang = ((ang % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
            for (double a = ang; ; a += 2 * Math.PI) {
                double h = y0 + a * SP_RISE;
                int y = (int) Math.floor(h);
                if (y > y1) break;
                boolean half = h - y >= 0.5 && y < y1;
                if (d <= 9.1) {
                    k.set(u, y, v, d > 8.2 ? p.fCap : k.rnd(401, u, y, v) < 0.5 ? p.brownM : p.fCap);
                    if (half) k.set(u, y + 1, v, p.slab);
                    for (int hh = half ? 2 : 1; hh <= 4; hh++) k.set(u, y + hh, v, Kit.AIR);
                } else if (y + 1 <= -11 && y >= y0 + 2) {
                    k.set(u, y + 1, v, p.fence);
                    if (half) k.set(u, y + 2, v, p.fence);
                }
            }
            // Gelaender um den Schlund oben
            if (d > 9.1 && d <= 10.05) {
                double a = Math.atan2(v - MV, u - MU);
                double diff = Math.atan2(Math.sin(a - endA), Math.cos(a - endA));
                if (diff < -1.25 || diff > 0.5) k.set(u, 1, v, p.fence);
            }
        }
        // Lichter unter der Rampe
        for (int i = 0; i < 14; i++) {
            double a = SP_A0 + i * 0.7 + 0.35;
            int y = (int) Math.floor(y0 + (a - SP_A0) * SP_RISE);
            int u = (int) Math.round(MU + Math.cos(a) * 6.5), v = (int) Math.round(MV + Math.sin(a) * 6.5);
            if (y - 1 > y0) k.set(u, y - 1, v, p.shroom);
        }
    }

    /** Sporenschlote auf der Kuppel. */
    static final double[][] CHIMNEYS = {{10, 38, 1}, {50, 41, 0}, {135, 37, 1}, {180, 43, 0}, {230, 39, 1}, {290, 44, 0}, {335, 40, 1}};

    void chimneys(Kit k, P p) {
        for (double[] c : CHIMNEYS) {
            double a = Math.toRadians(c[0]), ca = Math.cos(a), sa = Math.sin(a);
            int top = (int) c[1];
            double bu = MU + ca * 23, bv = MV + sa * 22, tu = MU + ca * 29, tv = MV + sa * 28;
            if (!k.touches((int) bu - 8, (int) bv - 8, (int) tu + 8, (int) tv + 8) && !k.touches((int) tu - 8, (int) tv - 8, (int) bu + 8, (int) bv + 8)) continue;
            double mu = (bu + tu) / 2 + sa * 1.5, mv = (bv + tv) / 2 - ca * 1.5;
            rope(k, bu, 15, bv, mu, (15 + top) / 2.0, mv, 3.4, p::mound);
            rope(k, mu, (15 + top) / 2.0, mv, tu, top, tv, 2.6, p::capBrown);
            rope(k, tu, top - 1, tv, tu, top + 1, tv, 3.2, (u, y, v) -> y == top + 1 ? p.shroom : p.fCap);
            // Schlot innen offen, unten bis in die Vorhalle
            k.line(MU + ca * 17, 10, MV + sa * 16, mu, (15 + top) / 2.0, mv, 1.3, Kit.AIR);
            k.line(mu, (15 + top) / 2.0, mv, tu, top + 2, tv, 1.3, Kit.AIR);
            int iu = (int) Math.round(tu), iv = (int) Math.round(tv);
            if (c[2] > 0) {
                k.set(iu, top, iv, p.pmud);
                k.set(iu, top + 1, iv, CityPalette.with(Kit.b(Blocks.CAMPFIRE), BlockStateProperties.SIGNAL_FIRE, true));
                k.set(iu, top - 1, iv, Kit.b(Blocks.HAY_BLOCK));
            }
        }
    }

    /** Leuchtpusteln auf der Kuppel. */
    void pustules(Kit k, P p) {
        for (int i = 0; i < 26; i++) {
            double a = k.rnd(501, i, 0, 0) * Math.PI * 2, e = 0.15 + k.rnd(502, i, 0, 0) * 0.9;
            if (Math.abs(Math.atan2(Math.sin(a - Math.PI / 2), Math.cos(a - Math.PI / 2))) < 0.35 && e < 0.6) continue;
            double cu = MU + Math.cos(a) * Math.cos(e) * 35.5, cv = MV + Math.sin(a) * Math.cos(e) * 33.5, cy = -6 + Math.sin(e) * 31.5;
            if (cy < 3) continue;
            double r = 1.5 + k.rnd(503, i, 0, 0) * 1.4;
            k.ellipsoid(cu, cy, cv, r + 0.8, r + 0.5, r + 0.8, p.redM);
            k.ellipsoid(cu + Math.cos(a) * 0.8, cy + 0.6, cv + Math.sin(a) * 0.8, r, r, r, k.rnd(504, i, 0, 0) < 0.5 ? p.shroom : p.ochre);
        }
    }

    /** Maul: Rippenboegen aus Knochen und Pilzstiel vor dem Eingang. */
    void maw(Kit k, P p) {
        // Laternen-Pilze am Weg zum Maul
        for (int v = 56; v <= 80; v += 6) {
            int u = (int) Math.round(pathU(v));
            for (int s = -1; s <= 1; s += 2) {
                int uu = u + s * 4;
                k.set(uu, 1, v, p.stem);
                k.set(uu, 2, v, p.fStem);
                k.set(uu, 3, v, p.shroom);
                k.set(uu, 4, v, p.slab);
            }
        }
        if (!k.touches(-12, 34, 12, 56)) return;
        int[] vs = {39, 43, 47, 51};
        for (int n = 0; n < vs.length; n++) {
            double rr = 6.4 - n * 0.3;
            Mat m = n % 2 == 0 ? (u, y, v) -> p.bone : p::stemMat;
            for (int s = 0; s < 12; s++) {
                double a0 = Math.PI * s / 12, a1 = Math.PI * (s + 1) / 12;
                rope(k, Math.cos(a0) * rr, 0.5 + Math.sin(a0) * (rr + 2), vs[n] + Math.sin(a0) * 1.2, Math.cos(a1) * rr, 0.5 + Math.sin(a1) * (rr + 2), vs[n] + Math.sin(a1) * 1.2, 0.85, m);
            }
            // Zaehne
            for (int s = -1; s <= 1; s += 2) rope(k, s * (rr - 0.6), 6.5, vs[n] + 0.6, s * (rr - 1.8), 4.5, vs[n] + 0.8, 0.5, (u, y, v) -> p.bone);
        }
    }

    /** Wurzelranken: vom Huegel ueber den Boden und hinein, zwei Boegen ueber den Weg. */
    void tendrils(Kit k, P p) {
        for (int i = 0; i < 11; i++) {
            double a = Math.toRadians(i * 32.7 + 8 + k.rnd(511, i, 0, 0) * 14);
            if (Math.abs(Math.atan2(Math.sin(a - Math.PI / 2), Math.cos(a - Math.PI / 2))) < 0.42) continue;
            double ca = Math.cos(a), sa = Math.sin(a), sw = (k.rnd(512, i, 0, 0) - 0.5) * 0.5;
            double[][] pts = {{ca * 31, 6, MV + sa * 29}, {Math.cos(a + sw) * 43, 7 + k.rnd(513, i, 0, 0) * 5, MV + Math.sin(a + sw) * 41},
                    {Math.cos(a + sw * 2) * 54, 1, MV + Math.sin(a + sw * 2) * 52}, {Math.cos(a + sw * 2.5) * 60, -4, MV + Math.sin(a + sw * 2.5) * 57}};
            for (int s = 0; s < 3; s++)
                rope(k, pts[s][0], pts[s][1], pts[s][2], pts[s + 1][0], pts[s + 1][1], pts[s + 1][2], 1.9 - s * 0.35, p::root);
        }
        double[][][] arches = {{{-8, -2, 58}, {-4, 8, 59.5}, {3, 9, 61}, {8, -2, 63}}, {{8, -2, 69}, {3, 8, 71}, {-4, 9, 72}, {-9, -2, 74}}};
        for (double[][] ar : arches) for (int s = 0; s < 3; s++)
            rope(k, ar[s][0], ar[s][1], ar[s][2], ar[s + 1][0], ar[s + 1][1], ar[s + 1][2], 1.4, p::root);
    }

    /** Riesenpilz. */
    void shroom(Kit k, P p, double u, int y0, double v, int h, double capR, boolean red, double lu, double lv) {
        int ext = (int) Math.ceil(capR + Math.abs(lu) + Math.abs(lv) + 4);
        if (!k.touches((int) u - ext, (int) v - ext, (int) u + ext, (int) v + ext)) return;
        double sr = 0.6 + h / 13.0;
        double tu = u + lu, tv = v + lv;
        int top = y0 + h;
        k.ellipsoid(u, y0, v, sr + 1.4, 1.5, sr + 1.4, p.fStem);
        rope(k, u, y0, v, u + lu * 0.25, y0 + h * 0.5, v + lv * 0.25, sr, p::stemMat);
        rope(k, u + lu * 0.25, y0 + h * 0.5, v + lv * 0.25, tu, top, tv, sr * 0.9, p::stemMat);
        if (red) {
            dome(k, tu, top - 1, tv, capR, capR * 0.62, capR, 1.6, p::capRed, top - 1);
            disk(k, tu, tv, top - 1, capR - 1.2, capR - 1.2, p::gills);
        } else {
            fillEll(k, new Ell(tu, top + 0.5, tv, capR, 1.4, capR, 0.04, 600 + (long) u * 7 + (long) v), 0, p::capBrown);
            disk(k, tu, tv, top - 1, capR * 0.55, capR * 0.55, p::gills);
        }
        for (int i = 0; i < 4; i++) {
            double a = Math.PI / 2 * i + 0.4;
            k.set((int) Math.round(tu + Math.cos(a) * capR * 0.45), top - 2, (int) Math.round(tv + Math.sin(a) * capR * 0.45), p.shroom);
        }
    }

    static final double[][] SURF_SHROOMS = {
            {-20, 60, 18, 8, 1}, {22, 56, 22, 10, 0}, {-38, 44, 14, 6, 1}, {42, 32, 17, 7, 1}, {-47, 8, 20, 8, 0}, {48, -22, 13, 6, 1},
            {-42, -42, 17, 7, 1}, {30, -50, 12, 6, 0}, {-2, -52, 15, 6, 1}, {-15, 76, 9, 4, 0}, {17, 76, 11, 5, 1}, {-58, 62, 12, 6, 0},
            {60, 56, 10, 5, 1}, {62, -60, 14, 6, 0}, {-66, -10, 11, 5, 1}, {68, 10, 15, 6, 0}, {-60, -66, 9, 5, 1}, {38, 72, 8, 4, 0}};

    void surfaceShrooms(Kit k, P p) {
        for (int i = 0; i < SURF_SHROOMS.length; i++) {
            double[] s = SURF_SHROOMS[i];
            double lu = (k.rnd(521, i, 0, 0) - 0.5) * s[2] * 0.3, lv = (k.rnd(522, i, 0, 0) - 0.5) * s[2] * 0.3;
            shroom(k, p, s[0], 0, s[1], (int) s[2], s[3], s[4] > 0, lu, lv);
        }
    }

    // ── Raeume ──

    /** Pflanzen am Boden und Behang an der Decke. */
    void dress(Kit k, P p, Ell e, double plants, double hang, long salt, int minClear) {
        double ext = 1 + 1.3 * e.amp;
        int uLo = (int) Math.floor(e.cu - e.ru * ext), uHi = (int) Math.ceil(e.cu + e.ru * ext);
        int vLo = (int) Math.floor(e.cv - e.rv * ext), vHi = (int) Math.ceil(e.cv + e.rv * ext);
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) for (int v = Math.max(Math.max(vLo, e.vMin), k.v0()); v <= Math.min(Math.min(vHi, e.vMax), k.v1()); v++) {
            double r = k.rnd(salt, u, 0, v);
            if (r < plants && e.cav(k, u, e.yMin, v)) {
                BlockState pl = p.plantOn(p.floor(u, e.yMin - 1, v), k.rnd(salt + 1, u, 0, v));
                if (pl != null && k.isAir(u, e.yMin, v) && !k.isAir(u, e.yMin - 1, v)) k.set(u, e.yMin, v, pl);
            }
            double r2 = k.rnd(salt + 2, u, 0, v);
            if (r2 >= hang) continue;
            int t = top(k, e, u, v);
            if (t == Integer.MIN_VALUE || t - e.yMin < minClear) continue;
            double c = r2 / hang;
            if (c < 0.3) k.set(u, t, v, p.blossom);
            else if (c < 0.55) k.set(u, t, v, p.hRoots);
            else if (c < 0.62) k.set(u, t, v, p.shroom);
            else {
                int len = 2 + (int) (k.rnd(salt + 3, u, 0, v) * Math.min(7, t - e.yMin - minClear + 1));
                for (int i = 0; i < len; i++) {
                    if (!k.isAir(u, t - i, v)) break;
                    k.set(u, t - i, v, i == len - 1 ? p.weep : p.weep);
                }
            }
        }
    }

    /** Eiersack: Schleimhaut mit leuchtendem Dotter. */
    void egg(Kit k, P p, double u, double y, double v, double r, long salt) {
        double s = k.rnd(salt, (int) u, (int) y, (int) v);
        k.ellipsoid(u, y, v, r, r * 1.2, r, s < 0.7 ? p.slime : p.honey);
        k.ellipsoid(u, y, v, r * 0.5, r * 0.6, r * 0.5, s < 0.35 ? p.pearl : s < 0.7 ? p.ochre : p.verdant);
    }

    /** Eiersaecke am Kammerrand (ausser in der Naehe der Punkte {@code avoid}). */
    void eggRing(Kit k, P p, Ell e, int n, long salt, double[][] avoid) {
        if (!k.touches((int) (e.cu - e.ru - 4), (int) (e.cv - e.rv - 4), (int) (e.cu + e.ru + 4), (int) (e.cv + e.rv + 4))) return;
        double fu = e.fu(), fv = e.fv();
        for (int i = 0; i < n; i++) {
            double a = Math.PI * 2 * (i + k.rnd(salt, i, 0, 0) * 0.6) / n, f = 0.88 + k.rnd(salt + 1, i, 0, 0) * 0.1;
            double u = Math.round(e.cu + Math.cos(a) * fu * f), v = Math.round(e.cv + Math.sin(a) * fv * f);
            double r = 1.3 + k.rnd(salt + 2, i, 0, 0) * 1.1;
            boolean bad = false;
            for (double[] q : avoid) if (sq(u - q[0]) + sq(v - q[1]) < sq(r + q[2])) bad = true;
            if (bad) continue;
            egg(k, p, u, e.yMin + r * 0.7, v, r, salt + 3 + i);
            if (k.rnd(salt + 4, i, 0, 0) < 0.5) egg(k, p, u - Math.cos(a) * r * 1.3, e.yMin + r * 0.4, v - Math.sin(a) * r * 1.3 + 1, r * 0.6, salt + 40 + i);
        }
    }

    /** Haengende Eiersaecke unter der Decke mit Ranken darunter. */
    void hangingEggs(Kit k, P p, Ell e, int n, long salt) {
        if (!k.touches((int) (e.cu - e.ru - 4), (int) (e.cv - e.rv - 4), (int) (e.cu + e.ru + 4), (int) (e.cv + e.rv + 4))) return;
        for (int i = 0; i < n; i++) {
            double a = k.rnd(salt, i, 0, 0) * Math.PI * 2, f = 0.2 + k.rnd(salt + 1, i, 0, 0) * 0.45;
            int u = (int) Math.round(e.cu + Math.cos(a) * e.ru * f), v = (int) Math.round(e.cv + Math.sin(a) * e.rv * f);
            int c = (int) Math.floor(e.ceil(u, v)) - 1;
            int r = 1 + (int) (k.rnd(salt + 2, i, 0, 0) * 2);
            int bottom = c - (int) Math.floor(r * 1.2);
            if (bottom - e.yMin < 5) continue;
            k.cylinder(u, v, c, c + 4, 0.6, p.fStem);
            egg(k, p, u, c, v, r + 0.4, salt + 10 + i);
            int len = Math.min(bottom - e.yMin - 4, 2 + (int) (k.rnd(salt + 3, i, 0, 0) * 4));
            for (int j = 1; j <= len; j++) k.set(u, bottom - j, v, j == len ? p.weep : p.weep);
        }
    }

    void atriumDeco(Kit k, P p, Ell a) {
        if (!k.touches(-34, -28, 34, 50)) return;
        double[] angs = {25, 150, 200, 250, 290, 340};
        for (int i = 0; i < angs.length; i++) {
            double r = Math.toRadians(angs[i]);
            shroom(k, p, MU + Math.cos(r) * 19, 1, MV + Math.sin(r) * 17, 7 + i % 3 * 2, 3.5 + i % 2, i % 2 == 0, Math.cos(r) * 1.5, Math.sin(r) * 1.5);
        }
        dress(k, p, a, 0.05, 0.035, 610, 8);
        // Hyphenstraenge quer durch die Halle (hoch oben)
        for (int i = 0; i < 6; i++) {
            double a0 = k.rnd(620, i, 0, 0) * Math.PI * 2, a1 = a0 + Math.PI * (0.6 + k.rnd(621, i, 0, 0) * 0.6);
            rope(k, MU + Math.cos(a0) * 24, 10 + i % 3 * 2, MV + Math.sin(a0) * 22, MU + Math.cos(a1) * 24, 12 + i % 2 * 3, MV + Math.sin(a1) * 22, 0.45, (u, y, v) -> p.fStem);
        }
        // Reste einer Expedition am Maul
        k.set(5, 1, 28, Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SUPPLY_CRATE.get()));
        k.set(6, 1, 27, Kit.b(com.space_beyond.dim.sporos.SporosBlocks.SUPPLY_CRATE.get()));
        k.set(6, 2, 27, Kit.b(com.space_beyond.dim.sporos.SporosBlocks.MILITARY_CRATE.get()));
        k.set(-6, 1, 27, Kit.b(ModBlocks.BARREL_TAINT.get()));
        k.set(-7, 1, 28, Kit.b(ModBlocks.BARREL_CORRODED.get()));
        k.set(-5, 1, 29, p.cobweb);
        k.set(7, 1, 25, p.cobweb);
    }

    void hubDeco(Kit k, P p, Ell h) {
        if (!k.touches(-30, -18, 30, 30)) return;
        double[][] sh = {{-17, 0, 7, 3}, {-12, 18, 8, 4}, {17, -2, 9, 4}, {12, 20, 6, 3}, {-6, -10, 6, 3}, {20, 12, 7, 3}};
        for (int i = 0; i < sh.length; i++) shroom(k, p, sh[i][0], -27, sh[i][1], (int) sh[i][2], sh[i][3], i % 2 == 1, 0, 0);
        dress(k, p, h, 0.06, 0.04, 630, 7);
        // Hyphenstraenge
        for (int i = 0; i < 5; i++) {
            double a0 = k.rnd(640, i, 0, 0) * Math.PI * 2, a1 = a0 + Math.PI * (0.7 + k.rnd(641, i, 0, 0) * 0.5);
            rope(k, MU + Math.cos(a0) * 22, -16 - i % 2, MV + Math.sin(a0) * 18, MU + Math.cos(a1) * 22, -13 - i % 3, MV + Math.sin(a1) * 18, 0.45, (u, y, v) -> p.fStem);
        }
        // Pfuetzen aus Naehrschleim (Wasser, im Boden eingelassen)
        for (double[] w : new double[][] {{-14, 10}, {10, -6}, {6, 22}}) {
            for (int u = (int) w[0] - 2; u <= w[0] + 2; u++) for (int v = (int) w[1] - 2; v <= w[1] + 1; v++) {
                if (sq(u - w[0]) + sq(v - w[1] + 0.5) <= 4.5) k.set(u, -28, v, Kit.b(Blocks.WATER));
            }
        }
    }

    void broodDeco(Kit k, P p, Ell b1, Ell b2, Ell b3) {
        eggRing(k, p, b1, 9, 700, new double[][] {{-40, -4, 4}, {-49, -9, 4}, {-46, -8, 2}, {-40, 0, 2}, {-52, -2, 2}, {-44, -5, 2}});
        eggRing(k, p, b2, 12, 720, new double[][] {{-59, -20, 4}, {-64, -31, 4}, {-68, -20, 2}, {-58, -30, 2}, {-72, -27, 2}, {-60, -22, 2}});
        eggRing(k, p, b3, 12, 740, new double[][] {{-65, -48, 4}, {-56, -52, 2}, {-70, -51, 2}, {-61, -51, 2}});
        hangingEggs(k, p, b1, 3, 760);
        hangingEggs(k, p, b2, 5, 770);
        hangingEggs(k, p, b3, 7, 780);
        dress(k, p, b1, 0.04, 0.03, 790, 6);
        dress(k, p, b2, 0.04, 0.03, 800, 6);
        dress(k, p, b3, 0.04, 0.03, 810, 6);
        // Wabenstuempfe fuer die Wespen-Spawner
        k.fill(-58, -26, -30, -58, -25, -30, p.comb);
        k.fill(-56, -25, -52, -56, -24, -52, p.comb);
        k.ellipsoid(-58, -26, -30, 1.6, 0.8, 1.6, p.comb);
        k.ellipsoid(-56, -25, -52, 1.6, 0.8, 1.6, p.comb);
        // Brutnest mit dem Schluessel (Stufen aus Wabe, Moos, Schleim)
        int nu = -65, nv = -57;
        disk(k, nu, nv, -25, 5.5, 5.5, (u, y, v) -> k.rnd(820, u, y, v) < 0.3 ? p.comb : p.moss);
        disk(k, nu, nv, -24, 4.0, 4.0, (u, y, v) -> k.rnd(821, u, y, v) < 0.4 ? p.honey : p.cushion);
        disk(k, nu, nv, -23, 2.5, 2.5, (u, y, v) -> p.comb);
        for (int i = 0; i < 6; i++) {
            double a = Math.PI * 2 * i / 6 + 0.3;
            egg(k, p, nu + Math.cos(a) * 3.2, -21.5, nv + Math.sin(a) * 3.2, 1.0, 830 + i);
        }
        k.set(nu, -22, nv, Kit.AIR);
        for (int u = nu - 1; u <= nu + 1; u++) k.set(u, -22, nv + 2, Kit.AIR);
        // Wurzeltrichter ueber dem Nest
        for (int i = 0; i < 5; i++) {
            double a = Math.PI * 2 * i / 5;
            rope(k, nu + Math.cos(a) * 9, b3.ceil(nu + Math.cos(a) * 9, nv + Math.sin(a) * 9) + 1, nv + Math.sin(a) * 9, nu + Math.cos(a) * 2.5, -15, nv + Math.sin(a) * 2.5, 0.7, p::root);
        }
        k.set(nu, -14, nv, p.shroom);
    }

    void nurseryDeco(Kit k, P p, Ell n) {
        if (!k.touches(-55, 38, -9, 78)) return;
        double cu = n.cu, cv = n.cv;
        int f = n.yMin;
        // Mutterkokon unter der Decke
        k.ellipsoid(cu, -25, cv, 3.2, 4.2, 3.2, p.honey);
        k.ellipsoid(cu, -25, cv, 2, 3, 2, p.ochre);
        k.cylinder(cu, cv, -22, -20, 1.2, p.stem);
        for (int i = 0; i < 6; i++) {
            double a = Math.PI * 2 * i / 6;
            int u = (int) Math.round(cu + Math.cos(a) * 2), v = (int) Math.round(cv + Math.sin(a) * 2);
            for (int y = -29; y >= -32; y--) k.set(u, y, v, y == -32 ? p.weep : p.weep);
        }
        // Leuchtring im Boden um den Waechterplatz
        for (int u = (int) cu - 6; u <= cu + 6; u++) for (int v = (int) cv - 6; v <= cv + 6; v++) {
            double d = Math.sqrt(sq(u - cu) + sq(v - cv));
            if (d > 4.5 && d < 5.5) k.set(u, f - 1, v, p.ochre);
            else if (d <= 4.5) k.set(u, f - 1, v, k.rnd(850, u, 0, v) < 0.5 ? p.cushion : p.moss);
        }
        // Brutnester mit Naehrschlaeuchen
        for (int i = 0; i < NESTS.length; i++) {
            double a = Math.toRadians(NESTS[i]);
            int u = (int) Math.round(cu + Math.cos(a) * 13), v = (int) Math.round(cv + Math.sin(a) * 9.5);
            for (int uu = u - 3; uu <= u + 3; uu++) for (int vv = v - 3; vv <= v + 3; vv++) {
                double d = Math.sqrt(sq(uu - u) + sq(vv - v));
                if (d > 2.9) continue;
                if (d >= 2.0) k.set(uu, f, vv, k.rnd(851, uu, 0, vv) < 0.5 ? p.fStem : p.stem);
                else {
                    k.set(uu, f - 1, vv, p.cushion);
                    if (d >= 1.0 && k.rnd(852, uu, 0, vv) < 0.5) k.set(uu, f, vv, p.pod);
                }
            }
            double c = n.ceil(u, v);
            rope(k, u, c + 2, v, u, -33, v, 0.6, (uu, y, vv) -> p.honey);
            k.set(u, -34, v, p.pearl);
        }
        dress(k, p, n, 0.04, 0.035, 860, 6);
        // Wiegen-Kokons an der Wand
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2 * (i + 0.5) / 8;
            egg(k, p, Math.round(cu + Math.cos(a) * n.fu() * 0.97), f + 2, Math.round(cv + Math.sin(a) * n.fv() * 0.97), 1.6, 870 + i);
        }
    }

    static final double[] NESTS = {0, 55, 125, 180, 235, 305};

    void anteDeco(Kit k, P p, Ell a) {
        if (!k.touches(-12, -40, 12, -18)) return;
        dress(k, p, a, 0.05, 0.05, 880, 5);
        // Mandibeln: Knochenrippen beiderseits des Tors
        for (int s = -1; s <= 1; s += 2) {
            rope(k, s * 7, -44, -33, s * 6.5, -38, -34.5, 0.8, (u, y, v) -> p.bone);
            rope(k, s * 6.5, -38, -34.5, s * 4.5, -33, -34.5, 0.8, (u, y, v) -> p.bone);
            rope(k, s * 4.5, -33, -34.5, s * 3, -31, -34.5, 0.6, (u, y, v) -> p.bone);
            k.set(s * 6, -42, -34, p.shroom);
        }
        // Leuchtende Adern zum Tor
        for (int v = -34; v >= -35; v--) for (int u = -1; u <= 1; u++) k.set(u, FT, v, p.wart);
        for (int v = -33; v <= -24; v++) k.set(0, FT, v, k.rnd(890, 0, 0, v) < 0.5 ? p.wart : p.ochre);
    }

    void throneFloor(Kit k, P p, Ell t) {
        if (!k.touches(-28, -84, 28, -36)) return;
        for (int u = Math.max(-26, k.u0()); u <= Math.min(26, k.u1()); u++) for (int v = Math.max(-84, k.v0()); v <= Math.min(-38, k.v1()); v++) {
            if (!t.in(k, u, t.yMin, v, 0.8) && !(Math.abs(u) <= 2 && v >= -41)) continue;
            double d = Math.sqrt(sq(u) + sq(v + 60));
            double n = k.noise(901, u, v, 5), r = k.rnd(902, u, 0, v);
            BlockState s;
            if (d >= 9 && d < 9.8) s = p.pearl;
            else if (d < 3) s = p.ochre;
            else if (v < -66 && n < 0.5 || n < 0.2) s = r < 0.7 ? p.nylium : p.wart;
            else s = p.floor(u, FT, v);
            k.set(u, FT, v, s);
        }
    }

    static final double[] PILLARS = {15, 50, 130, 165, 210, 330};

    void throneDeco(Kit k, P p, Ell t) {
        if (!k.touches(-28, -84, 28, -36)) return;
        // Rippen an der Wand
        for (int i = 0; i < 12; i++) {
            double a = Math.PI * 2 * i / 12 + 0.26;
            if (Math.abs(Math.atan2(Math.sin(a - Math.PI / 2), Math.cos(a - Math.PI / 2))) < 0.5) continue;
            double ca = Math.cos(a), sa = Math.sin(a);
            double u0 = ca * 22.5, v0 = -60 + sa * 19.8;
            rope(k, u0, -43, v0, ca * 21, -30, -60 + sa * 18.5, 1.1, p::stemMat);
            rope(k, ca * 21, -30, -60 + sa * 18.5, ca * 12, -20, -60 + sa * 10, 0.9, p::stemMat);
        }
        // Pilzstielsaeulen mit Konsolen
        for (int i = 0; i < PILLARS.length; i++) {
            double a = Math.toRadians(PILLARS[i]);
            double u = Math.round(Math.cos(a) * 17), v = Math.round(-60 + Math.sin(a) * 14);
            int c = (int) Math.ceil(t.ceil(u, v)) + 3;
            k.ellipsoid(u, FT + 1, v, 3.0, 1.6, 3.0, p.fStem);
            k.cylinder(u, v, FT, c, 1.7, p.stem);
            for (int y = FT + 2; y <= c; y += 5) k.set((int) u + ((y / 5) % 2 == 0 ? 2 : -2), y, (int) v, p.shroom);
            k.ellipsoid(u, -35, v, 3.6, 0.7, 3.6, p.brownM);
            k.ellipsoid(u, -29, v, 2.8, 0.6, 2.8, p.fCap);
            for (int s = 0; s < 4; s++) {
                double b = Math.PI / 2 * s + 0.78;
                k.set((int) Math.round(u + Math.cos(b) * 2.6), -36, (int) Math.round(v + Math.sin(b) * 2.6), p.shroom);
            }
            k.ellipsoid(u, c - 3, v, 3.4, 2.2, 3.4, p.stem);
        }
        // Thronpodest
        disk(k, 0, -75, FT + 1, 7, 6, (u, y, v) -> k.rnd(910, u, y, v) < 0.5 ? p.fCap : p.brownM);
        disk(k, 0, -75.5, FT + 2, 5, 4.5, (u, y, v) -> k.rnd(911, u, y, v) < 0.3 ? p.wart : p.redM);
        disk(k, 0, -76, FT + 3, 3.4, 3, p::stemMat);
        // Sitz, Armlehnen, Rueckenschild (senkrechter Fliegenpilzhut), Baldachin
        k.set(0, FT + 4, -77, Kit.stairs(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_STAIRS.get(), Direction.NORTH, false));
        for (int s = -1; s <= 1; s += 2) {
            k.set(s, FT + 4, -77, p.stem);
            k.set(s, FT + 5, -77, p.shroom);
            k.set(s, FT + 4, -76, Kit.stairs(com.space_beyond.dim.sporos.flora.SporosFlora.HYPHE_STAIRS.get(), Direction.NORTH, false));
        }
        for (int u = -7; u <= 7; u++) for (int y = FT + 4; y <= FT + 18; y++) {
            double e = sq(u / 7.0) + sq((y - (FT + 11)) / 7.5);
            if (e > 1) continue;
            k.set(u, y, -79, e > 0.78 ? p.shroom : p.capRed(u, y, -79));
            if (e <= 0.78) k.set(u, y, -80, p.redM);
            if (Math.abs(u) <= 1 && y <= FT + 14) k.set(u, y, -78, p.stem);
        }
        dome(k, 0, FT + 15, -75, 7, 3.5, 6, 1.4, p::capRed, FT + 15);
        disk(k, 0, -75, FT + 15, 5.5, 4.5, p::gills);
        // Sporennebel: Bluetenbehang, Spinnweben-Schleier hoch oben
        dress(k, p, t, 0.05, 0.07, 920, 10);
        for (int i = 0; i < 16; i++) {
            double a = k.rnd(930, i, 0, 0) * Math.PI * 2, f = 0.15 + k.rnd(931, i, 0, 0) * 0.45;
            int u = (int) Math.round(Math.cos(a) * 24 * f), v = (int) Math.round(-60 + Math.sin(a) * 21 * f);
            int c = (int) Math.floor(t.ceil(u, v)) - 2;
            for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) if (k.rnd(932, u + du, c, v + dv) < 0.5) k.setIfAir(u + du, c, v + dv, p.cobweb);
        }
        // Hyphenstraenge hoch oben
        for (int i = 0; i < 7; i++) {
            double a0 = k.rnd(940, i, 0, 0) * Math.PI * 2, a1 = a0 + Math.PI * (0.7 + k.rnd(941, i, 0, 0) * 0.5);
            rope(k, Math.cos(a0) * 20, -30 + i % 3, -60 + Math.sin(a0) * 17, Math.cos(a1) * 20, -27 - i % 2, -60 + Math.sin(a1) * 17, 0.45, (u, y, v) -> p.fStem);
        }
        // Leuchtende Sporenkapseln an der Decke
        for (int i = 0; i < 9; i++) {
            double a = Math.PI * 2 * i / 9, f = 0.45;
            double u = Math.round(Math.cos(a) * 24 * f), v = Math.round(-60 + Math.sin(a) * 21 * f);
            k.ellipsoid(u, Math.floor(t.ceil(u, v)), v, 1.6, 1.6, 1.6, i % 2 == 0 ? p.shroom : p.verdant);
        }
        // Platz fuer die Koenigin freihalten
        k.clear(-3, FT + 1, -63, 3, FT + 9, -57);
    }

    // ── Labyrinth ──

    double[] mid(Kit k, int i, int j, int l, int i2, int j2) {
        double a = (mu(i) + mu(i2)) / 2.0, b = (mv(j) + mv(j2)) / 2.0;
        double off = (k.rnd(5010, i * 10 + i2, l, j * 10 + j2) - 0.5) * 3.2;
        if (i != i2) b += off; else a += off;
        return new double[] {a, b};
    }

    void mazeSkin(Kit k, P p, Maze mz) {
        if (!k.touches(26, -40, 82, 40)) return;
        for (int l = 0; l < ML; l++) for (int j = 0; j < MJ; j++) for (int i = 0; i < MI; i++) {
            int u = mu(i), v = mv(j), f = mf(l);
            boolean key = Maze.id(i, j, l) == mz.key;
            double r = key ? 4.6 : 3.1;
            k.ellipsoid(u, f + 0.5, v, r + 2.2, r + 1.8, r + 2.2, p.sStone);
            fillEll(k, new Ell(u, f + 0.5, v, r + 2.0, r + 1.6, r + 2.0, 0.0, 0), 0, p::root);
            if (i + 1 < MI && mz.eu[i][j][l]) { double[] m = mid(k, i, j, l, i + 1, j); rope(k, u, f + 1, v, m[0], f + 1, m[1], 3.1, p::root); rope(k, m[0], f + 1, m[1], mu(i + 1), f + 1, v, 3.1, p::root); }
            if (j + 1 < MJ && mz.ev[i][j][l]) { double[] m = mid(k, i, j, l, i, j + 1); rope(k, u, f + 1, v, m[0], f + 1, m[1], 3.1, p::root); rope(k, m[0], f + 1, m[1], u, f + 1, mv(j + 1), 3.1, p::root); }
            if (mz.down(i, j, l)) rope(k, u, f + 1, v, u, mf(l + 1) + 1, v, 3.2, p::root);
        }
    }

    void mazeCarve(Kit k, P p, Maze mz) {
        if (!k.touches(26, -40, 82, 40)) return;
        for (int l = 0; l < ML; l++) for (int j = 0; j < MJ; j++) for (int i = 0; i < MI; i++) {
            int u = mu(i), v = mv(j), f = mf(l);
            boolean key = Maze.id(i, j, l) == mz.key;
            double r = key ? 4.6 : 3.0;
            k.shellCut(u, f, v, r, key ? 4.6 : 3.5, r, 0, null, Kit.AIR, f, f + 6);
            if (i + 1 < MI && mz.eu[i][j][l]) { double[] m = mid(k, i, j, l, i + 1, j); k.line(u, f + 1, v, m[0], f + 1, m[1], 1.45, Kit.AIR); k.line(m[0], f + 1, m[1], mu(i + 1), f + 1, v, 1.45, Kit.AIR); }
            if (j + 1 < MJ && mz.ev[i][j][l]) { double[] m = mid(k, i, j, l, i, j + 1); k.line(u, f + 1, v, m[0], f + 1, m[1], 1.45, Kit.AIR); k.line(m[0], f + 1, m[1], u, f + 1, mv(j + 1), 1.45, Kit.AIR); }
            if (mz.down(i, j, l)) k.cylinder(u, v, mf(l + 1), f + 2, 1.5, Kit.AIR);
        }
    }

    void mazeDeco(Kit k, P p, Maze mz) {
        if (!k.touches(26, -40, 82, 40)) return;
        for (int l = 0; l < ML; l++) for (int j = 0; j < MJ; j++) for (int i = 0; i < MI; i++) {
            int u = mu(i), v = mv(j), f = mf(l);
            int id = Maze.id(i, j, l);
            boolean key = id == mz.key;
            // Licht: Pustel in der Kuppel (oder an der Wand, wenn ein Schacht nach oben fuehrt)
            if (!mz.up(i, j, l)) k.set(u, f + (key ? 5 : 4), v, p.shroom);
            else k.set(u + 2, f + 3, v + 2, p.shroom);
            // Ranken im Schacht nach unten (wachsen vom unteren Boden hoch)
            if (mz.down(i, j, l)) {
                int lf = mf(l + 1);
                for (int y = lf; y <= f - 1; y++) k.set(u - 1, y, v, y == f - 1 ? p.twist : p.twist);
                for (int y = lf; y <= f - 2; y++) k.set(u + 1, y, v, y == f - 2 ? p.twist : p.twist);
            }
            // Wurzelbehang
            if (!mz.up(i, j, l) && k.rnd(5020, i, l, j) < 0.6) {
                k.set(u + 1, f + 3, v, p.hRoots);
                k.set(u - 1, f + 3, v - 1, p.hRoots);
            }
            if (k.rnd(5021, i, l, j) < 0.5) {
                int du = k.rnd(5022, i, l, j) < 0.5 ? 2 : -2;
                if (!mz.down(i, j, l) || Math.abs(du) > 1) k.setIfAir(u + du, f, v + 1, k.rnd(5023, i, l, j) < 0.5 ? p.glowCap : p.brownShroom);
            }
        }
        // Wurzelherz um die Schluesselkiste
        int ki = mz.key % MI, kj = (mz.key / MI) % MJ, kl = mz.key / (MI * MJ);
        int u = mu(ki), v = mv(kj), f = mf(kl);
        for (int s = 0; s < 4; s++) {
            double a = Math.PI / 2 * s + 0.4;
            rope(k, u + Math.cos(a) * 4.5, f + 5, v + Math.sin(a) * 4.5, u + Math.cos(a + 1.2) * 4.5, f + 3.5, v + Math.sin(a + 1.2) * 4.5, 0.5, p::root);
        }
        k.set(u + 3, f, v - 3, p.shroom);
        k.set(u - 3, f, v + 3, p.shroom);
    }

    /** Freie Richtung fuer die Schluesselkiste in einer Labyrinthzelle. */
    static int[] keySpot(Maze mz) {
        int ki = mz.key % MI, kj = (mz.key / MI) % MJ, kl = mz.key / (MI * MJ);
        int u = mu(ki), v = mv(kj), f = mf(kl);
        boolean[] free = {
                !(kj + 1 < MJ && mz.ev[ki][kj][kl]),   // Sued
                !(ki > 0 && mz.eu[ki - 1][kj][kl]),    // West
                !(kj > 0 && mz.ev[ki][kj - 1][kl]),    // Nord
                !(ki + 1 < MI && mz.eu[ki][kj][kl])};  // Ost
        int[][] off = {{0, 3}, {-3, 0}, {0, -3}, {3, 0}};
        for (int d = 0; d < 4; d++) if (free[d]) return new int[] {u + off[d][0], f, v + off[d][1], d};
        return new int[] {u + 2, f, v + 2, 0};
    }

    // ── Plan (immer, unabhaengig vom Ausschnitt) ──

    void plan(Kit k, Maze mz) {
        // Schluessel 1: Brutnest in der dritten Brutkammer
        k.keyChest(-65, -22, -57, Direction.SOUTH);
        // Schluessel 2: Herz des Wurzellabyrinths
        int[] ks = keySpot(mz);
        k.keyChest(ks[0], ks[1], ks[2], Direction.from2DDataValue(ks[3]).getOpposite());
        // Schluessel 3: Siegelwaechter der Kinderstube
        k.elite(-32, -37, 58, "space_beyond:mycobrute");

        // Boss
        k.boss(0, FT + 1, -60);
        k.bossChest(-7, FT + 1, -71, Direction.SOUTH);
        k.bossChest(7, FT + 1, -71, Direction.SOUTH);

        // Beute
        k.barrel(-20, 1, 18);
        k.loot(12, -27, 12, Direction.WEST);
        k.barrel(-52, -28, -2);
        k.loot(-72, -26, -27, Direction.EAST);
        k.loot(-32, -37, 66, Direction.NORTH);
        k.barrel(5, -43, -26);
        int loots = 0;
        for (int c : mz.ends) {
            if (loots >= 2) break;
            int i = c % MI, j = (c / MI) % MJ, l = c / (MI * MJ);
            if (mz.down(i, j, l)) continue;
            k.loot(mu(i) + 1, mf(l), mv(j) - 1, Direction.SOUTH);
            loots++;
        }

        // Spawner
        k.spawner(-48, -28, -9, "space_beyond:sporeling");
        k.spawner(-40, -28, 0, "space_beyond:lurker");
        k.spawner(-68, -26, -20, "space_beyond:sporeling");
        k.spawner(-58, -24, -30, "space_beyond:hive_wasp");
        k.spawner(-56, -23, -52, "space_beyond:hive_wasp");
        k.spawner(-70, -25, -51, "space_beyond:lurker");
        double a0 = Math.toRadians(NESTS[0]);
        k.spawner((int) Math.round(-32 + Math.cos(a0) * 13), -37, (int) Math.round(58 + Math.sin(a0) * 9.5), "space_beyond:sporeling");
        int sp = 0;
        for (int idx = mz.ends.size() - 1; idx >= 0 && sp < 1; idx--) {
            int c = mz.ends.get(idx);
            int i = c % MI, j = (c / MI) % MJ, l = c / (MI * MJ);
            if (mz.down(i, j, l)) continue;
            k.spawner(mu(i) - 1, mf(l), mv(j) + 1, "space_beyond:lurker");
            sp++;
        }

        // Bewohner
        k.mob(8, 1, 20, "space_beyond:sporeling");
        k.mob(-12, 1, -6, "space_beyond:blindcap");
        k.mob(-10, -27, -4, "space_beyond:sporeling");
        k.mob(8, -27, 18, "space_beyond:lurker");
        k.mob(14, -27, -6, "space_beyond:blindcap");
        k.mob(-44, -28, -5, "space_beyond:sporeling");
        k.mob(-60, -26, -22, "space_beyond:lurker");
        k.mob(-61, -25, -51, "space_beyond:hive_wasp");
        for (int n = 1; n < NESTS.length; n++) {
            double a = Math.toRadians(NESTS[n]);
            k.mob((int) Math.round(-32 + Math.cos(a) * 13), -37, (int) Math.round(58 + Math.sin(a) * 9.5), n == 4 ? "space_beyond:blindcap" : "space_beyond:sporeling");
        }
        k.mob(-3, -43, -30, "space_beyond:mycobrute");
        int m = 0;
        for (int c : mz.ends) {
            int i = c % MI, j = (c / MI) % MJ, l = c / (MI * MJ);
            if (m++ >= 4) break;
            k.mob(mu(i), mf(l), mv(j) + (mz.down(i, j, l) ? 2 : 0), m == 2 ? "space_beyond:blindcap" : "space_beyond:lurker");
        }
    }
}
