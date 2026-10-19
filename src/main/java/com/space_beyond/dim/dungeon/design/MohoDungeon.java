package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Moho - "Vulkanschmiede" (Volcano Forge). Ein eigener Vulkankegel (Krater auf y 40..51, Kronenklippe, Lavarinnen
 * in eingefassten Becken an den Flanken) mit einem in den Fels gehauenen Schmiedetor (Riesenambosse, Ketten,
 * Lavafaelle). Innen eine riesige Magmakammer (Ufer y -20, Kuppel bis ~y 28) mit eingefasstem Lavasee, Basaltsaeulen
 * und dem Hauptschlot in der Mitte; ein Wendelweg fuehrt an der Ostwand von der Galerie (y -2) zum Ufer.
 * <ul>
 *   <li>Schluessel 1: Inselkette ueber den Lavasee (Trittsteine), Truhe auf der mittleren Insel.</li>
 *   <li>Schluessel 2: Die Schmiede hinter der Ostwand - Esse, Giesserei, Waffenkammer (Spawner-Gauntlet).</li>
 *   <li>Schluessel 3: Kletterschacht im Hauptschlot zwischen Lavafaellen hinter Glas, oben im Kraternest der
 *       Siegelwaechter.</li>
 * </ul>
 * Siegeltor in der Westwand, dahinter ein Wendelschacht um eine Lavasaeule hinab zur Caldera-Arena (Boden y -50).
 */
public final class MohoDungeon extends CustomDungeon {

    static final int R = 88;
    /** Uferboden der Magmakammer (Block). */
    static final int SHORE = -20;
    /** Arena-Mitte und -Boden. */
    static final int AU = -57, AV = 36, AF = -50;
    /** Wendelschacht hinter dem Siegeltor. */
    static final int SU = -57, SV = 0;

    static final String IMP = "space_beyond:cinder_imp", HOUND = "space_beyond:magma_hound";

    @Override public Stage stage() { return Stage.MOHO; }
    @Override public int radius() { return R; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.moho.MohoBlocks.MOHO_REGOLITH.get()); }
    @Override public BlockState filler() { return Kit.b(com.space_m.dim.moho.MohoBlocks.MOHO_STONE.get()); }
    @Override public boolean fits(BiomeGenBaseCelestial biome) {
        return biome != com.space_m.dim.moho.biome.BiomeGenBaseMoho.mohoLavaSea;
    }
    @Override public int minBase() { return 60; }
    @Override public int maxBase() { return 190; }
    @Override public int minY() { return -54; }
    @Override public int maxY() { return 54; }
    @Override public int[] entry() { return new int[] {0, 1, 84}; }
    @Override public String boss() { return "space_beyond:magma_wyrm"; }

    // ── Material ───────────────────────────────────────────────────────────

    private static final class P {
        final BlockState moho = Kit.b(com.space_m.dim.moho.MohoBlocks.MOHO_STONE.get());
        final BlockState regolith = Kit.b(com.space_m.dim.moho.MohoBlocks.MOHO_REGOLITH.get());
        final BlockState oreFire = Kit.b(com.space_m.dim.moho.MohoBlocks.ORE_FIRE.get());
        final BlockState oreGlow = Kit.b(com.space_m.dim.moho.MohoBlocks.ORE_GLOWSTONE.get());
        final BlockState basalt = Kit.axis(Blocks.BASALT, Direction.Axis.Y);
        final BlockState polBasalt = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y);
        final BlockState beamZ = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Z);
        final BlockState smoothBasalt = Kit.b(Blocks.SMOOTH_BASALT);
        final BlockState blackstone = Kit.b(Blocks.BLACKSTONE);
        final BlockState polBlack = Kit.b(Blocks.POLISHED_BLACKSTONE);
        final BlockState pbb = Kit.b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        final BlockState pbbCracked = Kit.b(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        final BlockState chiseled = Kit.b(Blocks.CHISELED_POLISHED_BLACKSTONE);
        final BlockState gilded = Kit.b(Blocks.GILDED_BLACKSTONE);
        final BlockState nether = Kit.b(Blocks.NETHER_BRICKS);
        final BlockState redNether = Kit.b(Blocks.RED_NETHER_BRICKS);
        final BlockState crackedNether = Kit.b(Blocks.CRACKED_NETHER_BRICKS);
        final BlockState chiseledNether = Kit.b(Blocks.CHISELED_NETHER_BRICKS);
        final BlockState fire = Kit.b(ModBlocks.BRICK_FIRE.get());
        final BlockState basaltBrick = Kit.b(ModBlocks.BASALT_BRICK.get());
        final BlockState basaltTiles = Kit.b(ModBlocks.BASALT_TILES.get());
        final BlockState hbmBasalt = Kit.b(ModBlocks.BASALT_POLISHED.get());
        final BlockState obsidian = Kit.b(Blocks.OBSIDIAN);
        final BlockState crying = Kit.b(Blocks.CRYING_OBSIDIAN);
        final BlockState magma = Kit.b(Blocks.MAGMA_BLOCK);
        final BlockState lava = Kit.b(Blocks.LAVA);
        final BlockState shroom = Kit.b(Blocks.SHROOMLIGHT);
        final BlockState glassO = Kit.b(Blocks.ORANGE_STAINED_GLASS);
        final BlockState glassR = Kit.b(Blocks.RED_STAINED_GLASS);
        final BlockState lantern = Kit.lantern(Blocks.LANTERN, false);
        final BlockState lanternH = Kit.lantern(Blocks.LANTERN, true);
        final BlockState chain = Kit.b(Blocks.CHAIN);
        final BlockState bars = Kit.b(Blocks.IRON_BARS);
        final BlockState pbWall = Kit.b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        final BlockState netherFence = Kit.b(Blocks.NETHER_BRICK_FENCE);
        final BlockState campfire = Kit.b(Blocks.CAMPFIRE);
        final BlockState netherrack = Kit.b(Blocks.NETHERRACK);
        final BlockState fireBlock = Kit.b(Blocks.FIRE);
        final BlockState anvil = Kit.b(Blocks.ANVIL);
        final BlockState anvilD = Kit.b(Blocks.DAMAGED_ANVIL);
        final BlockState grindstone = Kit.b(Blocks.GRINDSTONE);
        final BlockState smithing = Kit.b(Blocks.SMITHING_TABLE);
        final BlockState cauldronW = Kit.b(Blocks.WATER_CAULDRON);
        final BlockState cauldronL = Kit.b(Blocks.LAVA_CAULDRON);
        final BlockState cauldron = Kit.b(Blocks.CAULDRON);
        final BlockState coal = Kit.b(Blocks.COAL_BLOCK);
        final BlockState rod = Kit.b(Blocks.LIGHTNING_ROD);
        final BlockState steel = Kit.b(ModBlocks.DECO_STEEL.get());
        final BlockState rusty = Kit.b(ModBlocks.DECO_RUSTY_STEEL.get());
        final BlockState redCopper = Kit.b(ModBlocks.DECO_RED_COPPER.get());
        final BlockState slag = Kit.b(ModBlocks.BLOCK_SLAG.get());
        final BlockState grate = Kit.b(ModBlocks.STEEL_GRATE.get());
        final BlockState pbbSlabTop = Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, true);
        final BlockState pbbSlab = Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false);

        BlockState blast(Direction d) { return Kit.facing(Blocks.BLAST_FURNACE, d); }
        BlockState smoker(Direction d) { return Kit.facing(Blocks.SMOKER, d); }
        BlockState stairs(Direction up, boolean top) { return Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, up, top); }
        BlockState fireStairs(Direction up, boolean top) { return Kit.stairs(ModBlocks.BRICK_FIRE_STAIRS.get(), up, top); }
    }

    // ── Aufbau ─────────────────────────────────────────────────────────────

    @Override
    protected void build(Kit k) {
        P p = new P();
        columns(k, p);
        channels(k, p);
        crater(k, p);
        plaza(k, p);
        facade(k, p);
        gallery(k, p);
        lakeFeatures(k, p);
        chimney(k, p);
        shoreDeco(k, p);
        forge(k, p);
        sealWay(k, p);
        arena(k, p);
        plan(k);
    }

    // ── Form des Vulkans ───────────────────────────────────────────────────

    /** Hoehe des Kegels (oberster Block) oder -1 ausserhalb. Krater < 14, Kraterwand, Krone bis 22, dann Flanke. */
    static int coneH(Kit k, int u, int v) {
        double d = Math.sqrt(u * u + v * v);
        double h;
        if (d < 14) h = 40;
        else if (d < 17) h = 40 + (d - 14) * 11 / 3.0;
        else if (d < 22) h = 51 - (d - 17) * 0.6 + (k.rnd(91, u, 0, v) < 0.3 ? 1 : 0);
        else {
            double a = Math.atan2(v, u);
            double rb = 62 + 7 * (k.noise(92, (int) Math.round(Math.cos(a) * 40), (int) Math.round(Math.sin(a) * 40), 13) - 0.5);
            if (d >= rb) return -1;
            double t = (rb - d) / (rb - 22);
            double ridge = k.noise(94, (int) Math.round(Math.cos(a) * 90), (int) Math.round(Math.sin(a) * 90), 4) - 0.5;
            h = 37 * Math.pow(t, 1.35) + (k.noise(93, u, v, 5) - 0.5) * 3 * t + ridge * 14 * t * (1 - t) * 2;
        }
        return (int) Math.floor(h);
    }

    /** Fels-Bastion um das Schmiedetor (Sueden). */
    static int bastionH(Kit k, int u, int v) {
        if (v < 24 || v > 47 || Math.abs(u) > 27) return -1;
        double h = 25 - 0.03 * u * u + (k.noise(61, u, v, 5) - 0.5) * 4;
        if (v < 30) h -= (30 - v) * 2.5;
        if (Math.abs(u) > 20) h -= (Math.abs(u) - 20) * 3;
        return (int) Math.floor(h);
    }

    static int topH(Kit k, int u, int v) {
        return Math.max(coneH(k, u, v), bastionH(k, u, v));
    }

    /** Wandradius der Magmakammer (Mittelpunkt 0/0). */
    static double rw(Kit k, double ang) {
        return 40.8 + 0.8 * k.noise(301, (int) Math.round(Math.cos(ang) * 60), (int) Math.round(Math.sin(ang) * 60), 9);
    }

    /** Radius des Lavasees. */
    static double lakeR(Kit k, double ang) {
        return 23.8 + 1.4 * k.noise(302, (int) Math.round(Math.cos(ang) * 50), (int) Math.round(Math.sin(ang) * 50), 10);
    }

    static int ceilAt(Kit k, int u, int v, double d, double rw, int h) {
        double t = Math.min(1, d / rw);
        double dome = -20 + 48 * Math.sqrt(Math.max(0, 1 - Math.pow(t, 6))) + (k.noise(41, u, v, 7) - 0.5) * 3;
        return (int) Math.floor(Math.min(dome, h - 7));
    }

    static int envAt(double d, double rw, int h) {
        double t = Math.max(0, d - 3.4) / rw;
        double dome = -20 + 48 * Math.sqrt(Math.max(0, 1 - Math.pow(t, 6))) + 1.5;
        return (int) Math.min(dome, h - 7) + 3;
    }

    /** Wendelweg an der Ostwand: Boden-y je Winkel (Grad; -90 Nord = Ufer, +72 Galerie). */
    static int rampY(double th) {
        double t = Math.max(0, Math.min(1, (th + 90) / 162.0));
        return -20 + (int) Math.floor(t * 18);
    }

    /** Liegt der Winkel th (Grad) auf Bogenlaenge w an einem Vielfachen von step (versetzt um off)? */
    static boolean near(double th, double off, double step, double d, double w) {
        double x = th - off;
        double m = Math.round(x / step) * step;
        return Math.abs(x - m) * Math.PI / 180 * d < w;
    }

    // ── Materialien ────────────────────────────────────────────────────────

    private static BlockState rock(Kit k, P p, int u, int y, int v, double off) {
        double r = k.rnd(5, u, y, v);
        if (r < 0.018) return p.magma;
        if (r < 0.026) return p.oreFire;
        if (r < 0.031) return p.oreGlow;
        int band = Math.floorMod((int) Math.floor((y + off) / 4.0), 6);
        switch (band) {
            case 0: case 3: return p.moho;
            case 1: return p.blackstone;
            case 2: return p.basalt;
            case 4: return r < 0.5 ? p.smoothBasalt : p.basalt;
            default: return p.blackstone;
        }
    }

    private static BlockState wall(Kit k, P p, int u, int y, int v) {
        double n = k.noise(31, u + y * 2, v - y, 8);
        if (Math.abs(n - 0.5) < 0.022) return p.magma;
        double r = k.rnd(6, u, y, v);
        if (r < 0.012) return p.oreGlow;
        if (n < 0.33) return p.basalt;
        if (n > 0.7) return r < 0.4 ? p.smoothBasalt : p.blackstone;
        return r < 0.15 ? p.moho : p.blackstone;
    }

    private static BlockState top(Kit k, P p, int u, int y, int v, double d) {
        double n = k.noise(21, u, v, 7), r = k.rnd(22, u, y, v);
        if (d < 14) return r < 0.45 ? p.magma : r < 0.7 ? p.blackstone : p.basalt;
        if (d < 22) return n < 0.5 ? p.blackstone : p.basalt;
        if (r < 0.035) return p.magma;
        if (n > 0.66) return p.blackstone;
        if (n < 0.22) return p.basalt;
        return p.regolith;
    }

    private static BlockState shoreFloor(Kit k, P p, int u, int v) {
        double n = k.noise(23, u, v, 6), r = k.rnd(24, u, 0, v);
        if (r < 0.04) return p.magma;
        if (n < 0.35) return p.basaltTiles;
        if (n < 0.6) return p.blackstone;
        if (n < 0.8) return p.polBlack;
        return p.smoothBasalt;
    }

    private static BlockState brick(Kit k, P p, int u, int y, int v) {
        double r = k.rnd(8, u, y, v);
        return r < 0.12 ? p.pbbCracked : r < 0.2 ? p.polBlack : p.pbb;
    }

    // ── Kegel, Kammer, Wendelweg (spaltenweise) ────────────────────────────

    private void columns(Kit k, P p) {
        int ua = Math.max(-R, k.u0()), ub = Math.min(R, k.u1()), va = Math.max(-R, k.v0()), vb = Math.min(R, k.v1());
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            double d = Math.sqrt(u * u + v * v);
            int h = topH(k, u, v);
            double off = (k.noise(7, u, v, 11) - 0.5) * 10;
            if (h >= 0) {
                for (int y = 0; y < h; y++) k.set(u, y, v, rock(k, p, u, y, v, off));
                k.set(u, h, v, top(k, p, u, h, v, d));
            }
            if (d < 45.5) chamberColumn(k, p, u, v, d, h);
        }
    }

    private void chamberColumn(Kit k, P p, int u, int v, double d, int h) {
        double ang = Math.atan2(v, u);
        double rw = rw(k, ang);
        if (d >= rw + 3.4) return;
        double th = Math.toDegrees(ang);
        if (d >= rw) {
            int env = envAt(d, rw, h);
            for (int y = -26; y <= env; y++) k.set(u, y, v, wall(k, p, u, y, v));
            niche(k, p, u, v, d, th, rw);
            return;
        }
        int ceil = ceilAt(k, u, v, d, rw, h);
        for (int y = -26; y <= -21; y++) k.set(u, y, v, wall(k, p, u, y, v));
        double lr = lakeR(k, ang);
        boolean lake = d < lr;
        if (lake) {
            for (int y = -23; y <= -21; y++) k.set(u, y, v, p.lava);
            k.set(u, SHORE, v, Kit.AIR);
        } else {
            k.set(u, SHORE, v, d < lr + 1.3 ? (k.rnd(25, u, 0, v) < 0.3 ? p.basaltBrick : p.polBlack) : shoreFloor(k, p, u, v));
        }
        for (int y = SHORE + 1; y <= ceil; y++) k.set(u, y, v, Kit.AIR);
        for (int y = ceil + 1; y <= Math.min(ceil + 8, h - 1); y++) k.set(u, y, v, wall(k, p, u, y, v));
        // Tropfsteine aus Basalt
        if (d < rw - 5 && d > 9) {
            double r = k.rnd(43, u, 0, v);
            if (r < 0.06) {
                int len = 1 + (int) (r / 0.06 * 7);
                for (int y = ceil; y > ceil - len && y > -10; y--) k.set(u, y, v, r < 0.015 ? p.magma : p.basalt);
            }
        }
        if (d >= 33.5 && th >= -93 && th <= 92) ramp(k, p, u, v, d, th, rw);
    }

    private void ramp(Kit k, P p, int u, int v, double d, double th, double rw) {
        int yR = rampY(th);
        boolean edge = d < 34.5;
        k.set(u, yR - 1, v, p.basaltBrick);
        BlockState deck = edge ? p.polBlack : d > rw - 1.2 ? p.basaltTiles : (near(th, 0, 6, d, 0.5) ? p.chiseled : brick(k, p, u, yR, v));
        k.set(u, yR, v, deck);
        if (!edge && th > -84 && near(th, 0, 15, d, 1.3)) {
            for (int y = SHORE + 1; y <= yR - 2; y++) k.set(u, y, v, (y - SHORE) % 5 == 0 ? p.basaltBrick : p.polBasalt);
        } else if (edge && yR - 2 > SHORE) {
            k.set(u, yR - 2, v, p.pbbSlabTop);
        }
        for (int y = yR + 1; y <= yR + 5; y++) k.set(u, y, v, Kit.AIR);
        if (edge && yR > -19) {
            boolean post = near(th, 7.5, 15, d, 0.8);
            k.set(u, yR + 1, v, post ? p.pbb : p.pbWall);
            if (post) k.set(u, yR + 2, v, p.lantern);
        }
    }

    static final double[] NICHES = {115, 140, 220, 245};

    private void niche(Kit k, P p, int u, int v, double d, double th, double rw) {
        double t360 = th < 0 ? th + 360 : th;
        for (double c : NICHES) {
            double arc = Math.abs(t360 - c) * Math.PI / 180 * d;
            if (arc >= 2.6) continue;
            boolean inner = arc < 1.6 && d < rw + 2.3;
            for (int y = SHORE; y <= -13; y++) {
                BlockState s;
                if (inner) s = y == SHORE ? p.basaltTiles : y <= -16 ? Kit.AIR : y == -15 ? p.chiseled : p.fire;
                else s = (y == -13 || (y - SHORE) % 3 == 0) ? p.fire : p.nether;
                k.set(u, y, v, s);
            }
        }
    }

    // ── Lavarinnen an den Flanken ──────────────────────────────────────────

    /** {achse (0 = u, 1 = v), richtung, erste Querzelle}: zwei Zellen breit, Becken zu je 3 mit Daemmen. */
    static final int[][] CHANNELS = {{0, 1, -1}, {0, -1, 0}, {1, -1, -1}, {0, 1, 16}, {0, -1, 16}, {1, -1, 16}, {1, -1, -18}, {0, 1, -18}, {0, -1, -18}};

    private void channels(Kit k, P p) {
        for (int[] c : CHANNELS) channel(k, p, c[0], c[1], c[2]);
    }

    private static int cu(int axis, int dir, int s, int l) { return axis == 0 ? dir * s : l; }
    private static int cv(int axis, int dir, int s, int l) { return axis == 0 ? l : dir * s; }

    private void channel(Kit k, P p, int axis, int dir, int lat) {
        // Bereich der Rinne: unter der Kronenklippe bis kurz vor dem Fuss
        int s0 = -1, s1 = -1;
        for (int s = 0; s < R; s++) {
            boolean ok = true;
            for (int l = lat; l <= lat + 2; l++) {
                int u = cu(axis, dir, s, l), v = cv(axis, dir, s, l);
                double d = Math.sqrt(u * u + v * v);
                int h = coneH(k, u, v);
                if (d < 23.5 || h > 35 || h < 4 || bastionH(k, u, v) >= h) ok = false;
            }
            if (ok && s0 < 0) s0 = s;
            if (s0 >= 0) {
                if (!ok) break;
                s1 = s;
            }
        }
        if (s0 < 0 || s1 - s0 < 6) return;
        int lo = Math.min(cu(axis, dir, s0, lat - 1), cu(axis, dir, s1 + 1, lat + 3)), hi = Math.max(cu(axis, dir, s0, lat - 1), cu(axis, dir, s1 + 1, lat + 3));
        int lo2 = Math.min(cv(axis, dir, s0, lat - 1), cv(axis, dir, s1 + 1, lat + 3)), hi2 = Math.max(cv(axis, dir, s0, lat - 1), cv(axis, dir, s1 + 1, lat + 3));
        if (!k.touches(lo, lo2, hi, hi2)) return;
        int nseg = (s1 - s0) / 3 + 1;
        int[] L = new int[nseg];
        for (int g = 0; g < nseg; g++) {
            int se = Math.min(s0 + 3 * g + 2, s1);
            int m = Integer.MAX_VALUE;
            for (int l = lat; l <= lat + 2; l++) m = Math.min(m, coneH(k, cu(axis, dir, se, l), cv(axis, dir, se, l)));
            L[g] = m - 1;
            if (g > 0) L[g] = Math.min(L[g], L[g - 1]);
        }
        for (int s = s0; s <= s1 + 1; s++) {
            int g = Math.min((s - s0) / 3, nseg - 1);
            boolean dam = (s - s0) % 3 == 0 || s == s1 + 1;
            int lv = L[g];
            int up = s == s1 + 1 ? L[nseg - 1] : g > 0 ? L[g - 1] : lv + 1;
            int need = dam ? up + 1 : lv + 1;
            for (int l = lat - 1; l <= lat + 3; l++) {
                int u = cu(axis, dir, s, l), v = cv(axis, dir, s, l);
                if (!k.col(u, v)) continue;
                int h = coneH(k, u, v);
                double off = (k.noise(7, u, v, 11) - 0.5) * 10;
                if (l == lat - 1 || l == lat + 3) {
                    // Ufer der Rinne: mindestens bis need, oben eingefasst
                    int bt = Math.max(h, need);
                    for (int y = 0; y < bt; y++) k.set(u, y, v, rock(k, p, u, y, v, off));
                    k.set(u, bt, v, (s & 1) == 0 ? p.basaltBrick : p.pbb);
                } else if (dam) {
                    int dt = s == s0 ? Math.max(h, lv + 1) : need;
                    for (int y = 0; y < dt; y++) k.set(u, y, v, rock(k, p, u, y, v, off));
                    k.set(u, dt, v, s == s0 ? p.magma : p.polBlack);
                    for (int y = dt + 1; y <= h; y++) k.set(u, y, v, Kit.AIR);
                } else {
                    for (int y = 0; y <= lv - 2; y++) k.set(u, y, v, rock(k, p, u, y, v, off));
                    k.set(u, lv - 1, v, p.lava);
                    k.set(u, lv, v, p.lava);
                    for (int y = lv + 1; y <= h; y++) k.set(u, y, v, Kit.AIR);
                }
            }
        }
    }

    // ── Krater ─────────────────────────────────────────────────────────────

    static boolean craterLava(Kit k, int u, int v) {
        double d = Math.sqrt(u * u + v * v);
        if (d < 9.3 || d >= 12.8) return false;
        if (v > 8 && Math.abs(u) < 4) return false;   // Leiter
        if (v < -8 && Math.abs(u) < 3) return false;  // Truhe
        if (Math.abs(Math.abs(u) - 11) <= 1 && Math.abs(Math.abs(v) - 3) <= 1) return false;
        return k.noise(51, u, v, 4) > 0.58;
    }

    private void crater(Kit k, P p) {
        if (!k.touches(-18, -18, 18, 18)) return;
        for (int u = Math.max(-14, k.u0()); u <= Math.min(14, k.u1()); u++) for (int v = Math.max(-14, k.v0()); v <= Math.min(14, k.v1()); v++) {
            if (craterLava(k, u, v)) k.set(u, 40, v, p.lava);
        }
        // Gluehende Feuerstellen (Rauchsaeulen ueber dem Krater)
        k.set(11, 41, -3, p.campfire);
        k.set(-11, 41, 3, p.campfire);
        k.set(-11, 41, -3, p.campfire);
        k.set(11, 41, 3, p.campfire);
        // Leiterpfeiler an der Suedwand (Rueckweg ueber den Kraterrand)
        for (int u = -1; u <= 1; u++) for (int v = 14; v <= 16; v++) for (int y = 40; y <= 51; y++) k.set(u, y, v, (y % 4 == 0) ? p.fire : p.pbb);
        k.ladder(0, 41, 51, 13, Direction.SOUTH);
        k.set(-1, 41, 13, p.chiseled);
        k.set(1, 41, 13, p.chiseled);
    }

    // ── Vorplatz und Schmiedetor ───────────────────────────────────────────

    private void plaza(Kit k, P p) {
        if (!k.touches(-25, 45, 25, R)) return;
        for (int u = Math.max(-25, k.u0()); u <= Math.min(25, k.u1()); u++) for (int v = Math.max(48, k.v0()); v <= Math.min(R - 2, k.v1()); v++) {
            double edge = 23 - k.noise(71, u, v, 6) * 5 - (v > 80 ? (v - 80) * 1.5 : 0);
            if (Math.abs(u) > edge) continue;
            BlockState s;
            int au = Math.abs(u);
            if (au <= 3) s = v % 6 == 0 ? p.chiseled : au == 3 ? p.polBlack : p.basaltTiles;
            else {
                double n = k.noise(72, u, v, 5), r = k.rnd(73, u, 0, v);
                s = r < 0.05 ? p.magma : n < 0.3 ? p.pbbCracked : n < 0.75 ? p.pbb : p.basaltBrick;
            }
            k.set(u, 0, v, s);
            int h = topH(k, u, v);
            for (int y = 1; y <= Math.max(h, 2); y++) k.set(u, y, v, Kit.AIR);
        }
        // Lavarinnen entlang der Allee (aus den Lavafaellen der Fassade gespeist)
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            for (int v = 45; v <= 81; v++) for (int a = 4; a <= 7; a++) {
                int u = sgn * a;
                k.set(u, -2, v, p.basaltBrick);
                k.set(u, -1, v, p.basaltBrick);
            }
            for (int v = 46; v <= 80; v++) for (int a = 5; a <= 6; a++) {
                int u = sgn * a;
                k.set(u, -1, v, p.lava);
                k.set(u, 0, v, p.lava);
            }
            for (int v = 48; v <= 81; v++) {
                k.set(sgn * 4, 0, v, p.polBlack);
                k.set(sgn * 7, 0, v, p.polBlack);
                k.set(sgn * 4, 1, v, v % 8 == 0 ? p.pbb : p.pbWall);
                k.set(sgn * 7, 1, v, v % 8 == 0 ? p.pbb : p.pbWall);
                if (v % 8 == 0) {
                    k.set(sgn * 4, 2, v, p.lantern);
                    k.set(sgn * 7, 2, v, p.lantern);
                }
            }
            for (int a = 5; a <= 6; a++) {
                k.set(sgn * a, 0, 81, p.polBlack);
                k.set(sgn * a, 1, 81, p.pbWall);
            }
            // Obelisken
            for (int v = 62; v <= 78; v += 8) {
                int u = sgn * 14;
                k.fill(u - 1, 1, v - 1, u + 1, 1, v + 1, p.pbb);
                k.set(u, 2, v, p.chiseled);
                for (int y = 3; y <= 7; y++) k.set(u, y, v, y == 5 ? p.magma : p.polBasalt);
                k.set(u, 8, v, p.shroom);
                k.set(u, 9, v, p.pbbSlab);
                for (Direction dd : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                    k.set(u + dd.getStepX(), 1, v + dd.getStepZ(), p.stairs(dd.getOpposite(), false));
                }
            }
        }
    }

    private void facade(Kit k, P p) {
        if (!k.touches(-20, 38, 20, 60)) return;
        // Fassade (in den Fels gehauen) v 46..47
        for (int u = -17; u <= 17; u++) for (int v = 46; v <= 47; v++) {
            int au = Math.abs(u);
            int hb = Math.min(22, topH(k, u, v));
            for (int y = 0; y <= hb; y++) {
                BlockState s;
                if (au == 9 || au == 15) s = y == 20 ? p.chiseled : p.polBasalt;
                else if (y == 0) s = p.basaltBrick;
                else if (y % 5 == 0) s = p.basaltBrick;
                else s = brick(k, p, u, y, v);
                k.set(u, y, v, s);
            }
            for (int y = hb + 1; y <= 24; y++) k.set(u, y, v, Kit.AIR);
            if (hb >= 21) k.set(u, 22, v, (u & 1) == 0 ? p.pbb : Kit.AIR);
        }
        // Gesims
        for (int u = -16; u <= 16; u++) {
            k.set(u, 20, 48, p.stairs(Direction.NORTH, true));
            k.set(u, 21, 48, p.pbb);
            k.set(u, 22, 48, (u & 1) == 0 ? p.pbbSlab : Kit.AIR);
        }
        // Pilaster vor der Fassade
        for (int u : new int[] {-15, -9, 9, 15}) {
            for (int y = 1; y <= 19; y++) k.set(u, y, 48, y % 6 == 0 ? p.basaltBrick : p.polBasalt);
            k.set(u, 0, 48, p.chiseled);
        }
        // Portal (Spitzbogen) und Laibung
        for (int u = -5; u <= 5; u++) for (int y = 0; y <= 12; y++) {
            int au = Math.abs(u);
            boolean open = y >= 1 && (y <= 6 && au <= 3 || y == 7 && au <= 2 || y == 8 && au <= 1);
            boolean frame = !open && y >= 1 && (au == 4 && y <= 7 || au == 3 && y >= 7 && y <= 8 || au == 2 && y >= 8 && y <= 9 || au <= 1 && y >= 9 && y <= 10);
            if (open) {
                k.set(u, y, 46, Kit.AIR);
                k.set(u, y, 47, Kit.AIR);
            } else if (frame) {
                k.set(u, y, 47, y == 10 && u == 0 ? p.crying : (y + au) % 2 == 0 ? p.chiseled : p.gilded);
                k.set(u, y, 48, au == 4 && y <= 6 ? p.polBasalt : Kit.AIR);
            }
        }
        // Amboss-Wappen ueber dem Tor
        int[][] glyph = {{16, -4, 4}, {15, -5, 3}, {14, -1, 1}, {13, -1, 1}, {12, -3, 3}};
        for (int[] g : glyph) for (int u = g[1]; u <= g[2]; u++) k.set(u, g[0], 47, p.gilded);
        k.set(-5, 15, 47, p.gilded);
        for (int u : new int[] {-12, 12}) {
            k.set(u, 15, 47, p.shroom);
            k.set(u, 14, 47, p.crying);
        }
        // Lavafaelle hinter Glas zu beiden Seiten des Tores, die in die Allee-Rinnen muenden
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            for (int y = 0; y <= 17; y++) {
                k.set(sgn * 4, y, 47, p.fire);
                k.set(sgn * 7, y, 47, p.fire);
                k.set(sgn * 4, y, 46, y <= 9 ? p.basaltBrick : p.fire);
                k.set(sgn * 7, y, 46, p.fire);
            }
            for (int a = 5; a <= 6; a++) {
                int u = sgn * a;
                k.set(u, 17, 47, p.fire);
                k.set(u, 17, 46, p.fire);
                k.set(u, 17, 45, p.fire);
                k.set(u, -1, 45, p.basaltBrick);
                for (int y = -1; y <= 16; y++) {
                    k.set(u, y, 45, y <= 0 ? p.basaltBrick : p.pbb);
                    k.set(u, y, 46, p.lava);
                }
                k.set(u, -1, 47, p.lava);
                k.set(u, 0, 47, p.lava);
                for (int y = 1; y <= 16; y++) k.set(u, y, 47, p.glassO);
            }
        }
        // Stufen vor dem Tor
        for (int u = -15; u <= 15; u++) {
            int au = Math.abs(u);
            if (au <= 3 || au >= 4 && au <= 7) continue;
            k.set(u, 1, 48, au == 9 || au == 15 ? p.polBasalt : p.stairs(Direction.NORTH, false));
        }
        // Kohlebecken an den Ecken
        for (int u : new int[] {-17, 17}) {
            k.set(u, 1, 49, p.pbb);
            k.set(u, 2, 49, p.chiseled);
            k.set(u, 3, 49, p.netherrack);
            k.set(u, 4, 49, p.fireBlock);
        }
        // Riesenambosse mit Ketten an Auslegern
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            int c = sgn * 12;
            k.fill(c - 3, 1, 50, c + 3, 1, 56, p.pbb);
            k.fill(c - 2, 2, 51, c + 2, 3, 55, p.rusty);
            k.fill(c - 1, 4, 52, c + 1, 4, 54, p.steel);
            k.fill(c - 2, 5, 50, c + 2, 7, 56, p.steel);
            k.fill(c - 1, 6, 48, c + 1, 7, 49, p.steel);
            k.fill(c - 1, 5, 57, c + 1, 7, 57, p.steel);
            k.fill(c - 2, 7, 50, c + 2, 7, 56, p.rusty);
            k.set(c, 8, 49, p.anvil);
            for (int v = 48; v <= 54; v++) k.set(c, 18, v, p.beamZ);
            k.set(c, 18, 55, p.chiseled);
            for (int y = 8; y <= 17; y++) k.set(c, y, 54, p.chain);
            for (int y = 9; y <= 17; y++) k.set(c, y, 52, y > 12 ? p.chain : Kit.AIR);
        }
    }

    private void gallery(Kit k, P p) {
        if (!k.touches(-11, 29, 11, 49)) return;
        // Tunnel vom Tor
        for (int u = -4; u <= 4; u++) for (int v = 41; v <= 47; v++) {
            int au = Math.abs(u);
            int fl = v >= 43 ? 0 : v == 42 ? -1 : -2;
            for (int y = fl - 1; y <= 9; y++) {
                boolean open = y > fl && (y <= 6 && au <= 3 || y == 7 && au <= 2 || y == 8 && au <= 1);
                BlockState s;
                if (open) s = Kit.AIR;
                else if (y == fl) s = au <= 1 ? p.basaltTiles : p.polBlack;
                else if (au == 4) s = (v == 44 && y == 4) ? p.shroom : y % 3 == 0 ? p.fire : p.basaltBrick;
                else s = p.pbb;
                if (v >= 46 && y >= 0 && !open && au <= 3 && y <= 10) continue;   // Torlaibung (Fassade)
                k.set(u, y, v, s);
            }
        }
        // Balkon ueber dem See
        for (int u = -9; u <= 9; u++) for (int v = 31; v <= 41; v++) {
            k.set(u, -3, v, p.basaltBrick);
            k.set(u, -2, v, (Math.abs(u) + v) % 5 == 0 ? p.chiseled : Math.abs(u) <= 1 ? p.basaltTiles : p.pbb);
            for (int y = -1; y <= 6; y++) k.set(u, y, v, Kit.AIR);
        }
        for (int u = -9; u <= 9; u++) {
            boolean post = Math.abs(u) % 3 == 0;
            k.set(u, -1, 31, post ? p.pbb : p.pbWall);
            if (post) k.set(u, 0, 31, p.lantern);
            k.set(u, -4, 31, p.stairs(Direction.SOUTH, true));
        }
        for (int v = 32; v <= 38; v++) {
            boolean post = v % 3 == 0;
            k.set(-9, -1, v, post ? p.pbb : p.pbWall);
            if (post) k.set(-9, 0, v, p.lantern);
        }
        // Stuetzpfeiler bis zum Ufer
        for (int u : new int[] {-7, 7}) {
            for (int y = SHORE + 1; y <= -4; y++) k.set(u, y, 32, y == -4 ? p.chiseled : y % 4 == 0 ? p.basaltBrick : p.polBasalt);
            k.set(u, -4, 31, p.chiseled);
        }
        // Rueckwand mit Feuerziegel-Relief
        for (int u = -9; u <= 9; u++) for (int y = -1; y <= 7; y++) {
            int au = Math.abs(u);
            if (au <= 4 && y <= 8) continue;
            k.set(u, y, 42, au == 6 || au == 9 ? p.polBasalt : y == 3 ? p.fire : y == 7 ? p.chiseled : p.pbb);
        }
        for (int u = -4; u <= 4; u++) k.set(u, 9, 42, Math.abs(u) == 4 ? p.polBasalt : p.chiseled);
        k.set(-6, 4, 41, p.shroom);
        k.set(6, 4, 41, p.shroom);
    }

    // ── Lavasee: Bruecke, Inselkette, Saeulen ──────────────────────────────

    /** Trittsteine {u, v, oberster Block}. Radius 1.5 (3x3). */
    static final int[][] STONES = {{21, 3, -20}, {16, 6, -19}, {11, 9, -19}, {6, 12, -18}, {-6, 12, -18}, {-11, 9, -19}, {-16, 6, -19}, {-21, 3, -20}};

    private void lakeFeatures(Kit k, P p) {
        if (!k.touches(-30, -30, 30, 30)) return;
        // Bruecke zum Hauptschlot (Nord)
        for (int u = -2; u <= 2; u++) for (int v = -28; v <= -8; v++) {
            int au = Math.abs(u);
            k.set(u, SHORE, v, au == 2 ? p.basaltBrick : v % 4 == 0 ? p.chiseled : au == 0 ? p.basaltTiles : p.pbb);
            k.set(u, SHORE - 1, v, p.basaltBrick);
            if (au == 2) {
                boolean post = v % 5 == 0;
                k.set(u, SHORE + 1, v, post ? p.pbb : p.pbWall);
                if (post) k.set(u, SHORE + 2, v, p.lantern);
            }
        }
        for (int pv : new int[] {-24, -18, -12}) {
            k.fill(-3, -24, pv - 1, 3, SHORE - 1, pv + 1, p.basaltBrick);
            k.fill(-3, SHORE - 1, pv - 1, 3, SHORE - 1, pv + 1, p.chiseled);
        }
        // Inselkette
        for (int i = 0; i < STONES.length; i++) {
            int[] s = STONES[i];
            k.cylinder(s[0], s[1], -24, s[2] - 1, 1.5, p.polBasalt);
            k.cylinder(s[0], s[1], s[2], s[2], 1.5, i % 2 == 1 ? p.magma : p.basalt);
            k.set(s[0], s[2], s[1], i % 2 == 1 ? p.shroom : p.polBasalt);
        }
        // Mittlere Insel mit Schrein
        k.cylinder(0, 15, -24, -21, 3.6, p.basalt);
        k.cylinder(0, 15, -24, -20, 3.3, p.polBasalt);
        k.cylinder(0, 15, -19, -19, 3.3, p.basaltTiles);
        k.set(0, -19, 15, p.chiseled);
        for (int du = -2; du <= 2; du += 4) for (int dv = -2; dv <= 2; dv += 4) {
            for (int y = -18; y <= -16; y++) k.set(du, y, 15 + dv, p.pbWall);
        }
        k.fill(-2, -15, 13, 2, -15, 17, p.pbbSlab);
        k.set(0, -15, 15, p.pbb);
        k.set(0, -16, 15, p.lanternH);
        // Saeule fuer den Feuerkobold-Spawner
        k.cylinder(0, 21, -24, -17, 1.0, p.polBasalt);
        // Grosse Basaltsaeulen bis zur Kuppel
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            int cu = sgn * 14, cv = -12;
            k.cylinder(cu, cv, -24, -19, 3.7, p.basaltBrick);
            k.cylinder(cu, cv, -18, 31, 2.6, p.polBasalt);
            for (int y = -14; y <= 26; y += 8) k.tube(cu, cv, y, y, 2.6, 1, p.magma, null);
            k.cylinder(cu, cv, 20, 23, 3.3, p.basaltBrick);
            k.cylinder(cu, cv, 24, 31, 4.2, p.pbb);
            // Basaltgruppen
            int[][] grp = {{18, -4, -15}, {20, -6, -12}, {16, -7, -18}, {21, -2, -16}, {18, -9, -14}, {22, -8, -17}};
            for (int[] g : grp) {
                k.cylinder(sgn * g[0], g[1], -24, g[2], 0.9, p.basalt);
                k.set(sgn * g[0], g[2], g[1], p.magma);
            }
        }
    }

    // ── Hauptschlot (Kletterschacht) und Kraternest ────────────────────────

    static final int[][] DIAG = {{3, -3}, {3, 3}, {-3, 3}, {-3, -3}};
    static final Direction[] DIAG_WALL = {Direction.EAST, Direction.EAST, Direction.WEST, Direction.WEST};
    static final double[] DIAG_ANG = {315, 45, 135, 225};

    static int stageY(int i) { return -20 + 6 * i; }
    static int stageS(int i) { return i % 2 == 1 ? 1 : 0; }

    private void chimney(Kit k, P p) {
        if (!k.touches(-16, -16, 16, 16)) return;
        for (int u = Math.max(-16, k.u0()); u <= Math.min(16, k.u1()); u++) for (int v = Math.max(-16, k.v0()); v <= Math.min(16, k.v1()); v++) {
            int r2 = u * u + v * v;
            if (r2 <= 21) {
                for (int y = -24; y <= -21; y++) k.set(u, y, v, p.pbb);
                k.set(u, SHORE, v, r2 <= 2 ? p.chiseled : r2 <= 9 ? p.basaltTiles : p.polBlack);
                for (int y = SHORE + 1; y <= 43; y++) k.set(u, y, v, Kit.AIR);
            } else if (r2 <= 57) {
                boolean inner = r2 <= 31;
                for (int y = -24; y <= 43; y++) {
                    BlockState s;
                    if (Math.floorMod(y, 6) == 2) s = p.fire;
                    else if (inner) s = k.rnd(9, u, y, v) < 0.15 ? p.crackedNether : p.nether;
                    else s = Math.floorMod(y, 6) == 5 ? p.basaltBrick : brick(k, p, u, y, v);
                    k.set(u, y, v, s);
                }
            } else {
                // Rippen (Diagonalen) und Trichter in die Kuppel
                boolean rib = r2 <= 82 && Math.abs(Math.abs(u) - Math.abs(v)) <= 1;
                if (rib) for (int y = -24; y <= 16; y++) k.set(u, y, v, y % 6 == 2 ? p.chiseled : p.pbb);
                double r = Math.sqrt(r2);
                for (int y = 12; y <= 34; y++) {
                    double rf = 7.6 + (y - 12) * 0.5;
                    if (r <= rf) k.set(u, y, v, y % 6 == 2 ? p.fire : (r > rf - 1.2 && y < 30 ? p.basaltBrick : p.pbb));
                }
            }
        }
        // Lavafaelle hinter Glas (Ost, Sued, West)
        int[][] dirs = {{1, 0}, {0, 1}, {-1, 0}};
        for (int[] d : dirs) for (int t = -1; t <= 1; t++) {
            int lu = d[0] * 6 + (d[0] == 0 ? t : 0), lv = d[1] * 6 + (d[1] == 0 ? t : 0);
            int iu = d[0] * 5 + (d[0] == 0 ? t : 0), iv = d[1] * 5 + (d[1] == 0 ? t : 0);
            int ou = d[0] * 7 + (d[0] == 0 ? t : 0), ov = d[1] * 7 + (d[1] == 0 ? t : 0);
            for (int y = -18; y <= 38; y++) {
                k.set(lu, y, lv, p.lava);
                k.set(iu, y, iv, p.glassO);
                k.set(ou, y, ov, y % 6 == 2 ? p.fire : p.glassR);
            }
            k.set(lu, -19, lv, p.fire);
            k.set(lu, 39, lv, p.fire);
        }
        // Tuer (Nord) zur Bruecke
        for (int u = -1; u <= 1; u++) for (int v = -7; v <= -5; v++) {
            for (int y = SHORE + 1; y <= SHORE + 4; y++) k.set(u, y, v, Kit.AIR);
            k.set(u, SHORE, v, p.basaltTiles);
            k.set(u, SHORE + 5, v, p.chiseled);
        }
        for (int y = SHORE; y <= SHORE + 6; y++) {
            k.set(-2, y, -7, p.fire);
            k.set(2, y, -7, p.fire);
        }
        k.set(0, SHORE + 6, -7, p.gilded);
        // Kletterweg: Gesimse rund um die Innenwand, dazwischen Leitern
        for (int i = 1; i <= 11; i++) {
            int di = (i - 1) % 4;
            int yb = stageY(i - 1) + stageS(i - 1) + 1, yt = i == 11 ? 44 : stageY(i);
            k.ladder(DIAG[di][0], yb, yt, DIAG[di][1], DIAG_WALL[di]);
        }
        for (int u = -4; u <= 4; u++) for (int v = -4; v <= 4; v++) {
            int r2 = u * u + v * v;
            if (r2 < 6 || r2 > 21) continue;
            double a = Math.toDegrees(Math.atan2(v, u));
            if (a < 0) a += 360;
            for (int i = 1; i <= 10; i++) {
                double a0 = DIAG_ANG[(i - 1) % 4];
                double rel = a - a0;
                rel = ((rel % 360) + 360) % 360;
                int y;
                if (rel > 5 && rel <= 35) y = stageY(i);
                else if (rel >= 57 && rel <= 90) y = stageY(i) + stageS(i);
                else continue;
                if (isLadderCell(u, v, i)) continue;
                k.set(u, y, v, r2 >= 13 ? p.basaltBrick : p.pbb);
                if (r2 >= 16) k.set(u, y - 1, v, p.pbbSlabTop);
            }
        }
        // Kraternest: Plattform auf dem Schlot
        for (int u = -9; u <= 9; u++) for (int v = -9; v <= 9; v++) {
            int r2 = u * u + v * v;
            if (r2 > 73) continue;
            k.set(u, 44, v, r2 <= 4 ? p.gilded : r2 <= 21 ? ((u + v) % 2 == 0 ? p.chiseled : p.pbb) : r2 <= 57 ? p.basaltTiles : p.basaltBrick);
            for (int y = 45; y <= 48; y++) k.set(u, y, v, Kit.AIR);
            if (r2 > 57) {
                double a = Math.toDegrees(Math.atan2(v, u)) + 360;
                if (((int) (a / 20)) % 2 == 0) k.set(u, 45, v, p.pbb);
            }
        }
        k.ladder(DIAG[2][0], 44, 44, DIAG[2][1], DIAG_WALL[2]);
        for (int[] c : new int[][] {{6, 5}, {-6, -5}, {5, -6}, {-5, 6}}) {
            k.set(c[0], 45, c[1], p.netherrack);
            k.set(c[0], 46, c[1], p.fireBlock);
        }
    }

    private static boolean isLadderCell(int u, int v, int i) {
        // Leiter L_i (Ende des Gesimses i-1) und L_{i+1}
        int a = (i - 1) % 4;
        return u == DIAG[a][0] && v == DIAG[a][1];
    }

    // ── Ufer-Deko ──────────────────────────────────────────────────────────

    private void shoreDeco(Kit k, P p) {
        // Laternenpfosten am West- und Nordufer
        for (int a = 100; a <= 260; a += 20) {
            if (a == 180) continue;
            double r = Math.toRadians(a);
            int u = (int) Math.round(Math.cos(r) * 30), v = (int) Math.round(Math.sin(r) * 30);
            k.set(u, SHORE + 1, v, p.pbb);
            k.set(u, SHORE + 2, v, p.pbWall);
            k.set(u, SHORE + 3, v, p.lantern);
        }
        // Nischen-Feuer
        for (double c : NICHES) {
            double r = Math.toRadians(c);
            double rr = rw(k, r) + 1.0;
            int u = (int) Math.round(Math.cos(r) * rr), v = (int) Math.round(Math.sin(r) * rr);
            k.set(u, SHORE + 1, v, p.campfire);
        }
        // Schlackehalden und Werkzeug am Ufer
        int[][] heaps = {{-30, 18}, {-34, -8}, {-27, -22}, {28, 24}};
        for (int[] h : heaps) {
            for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) {
                int dd = Math.abs(du) + Math.abs(dv);
                if (dd > 3) continue;
                k.set(h[0] + du, SHORE + 1, h[1] + dv, k.rnd(81, h[0] + du, 0, h[1] + dv) < 0.25 ? p.magma : p.slag);
                if (dd <= 1) k.set(h[0] + du, SHORE + 2, h[1] + dv, p.slag);
            }
            k.set(h[0], SHORE + 3, h[1], p.coal);
        }
        // Lavafaelle hinter Glas in der Kammerwand (Nord, Nordwest, Suedwest)
        wallFall(k, p, 6, -40, 0, -1);
        wallFall(k, p, -40, 10, -1, 0);
        wallFall(k, p, -40, -12, -1, 0);
        // Haengende Ketten mit Laternen ueber dem Ufer
        for (int a = 0; a < 360; a += 30) {
            double r = Math.toRadians(a + 15);
            int u = (int) Math.round(Math.cos(r) * 29.5), v = (int) Math.round(Math.sin(r) * 29.5);
            if (!k.col(u, v)) continue;
            double d = Math.sqrt(u * u + v * v);
            int c = ceilAt(k, u, v, d, rw(k, Math.atan2(v, u)), topH(k, u, v));
            int bottom = Math.max(SHORE + 7, c - 9);
            for (int y = c; y > bottom; y--) k.set(u, y, v, p.chain);
            k.set(u, bottom, v, p.lanternH);
        }
        k.set(-25, SHORE + 1, 22, p.anvilD);
        k.set(-24, SHORE + 1, 23, p.cauldronL);
        k.set(-26, SHORE + 1, 19, p.grindstone);
    }

    /** Lavafall hinter Glas: Nische ab (bu, bv), nach aussen (du, dv) Glas, dann Lava, alles eingefasst. */
    private void wallFall(Kit k, P p, int bu, int bv, int du, int dv) {
        if (!k.touches(bu - 4, bv - 4, bu + 4, bv + 4)) return;
        int lu = dv != 0 ? 1 : 0, lv = du != 0 ? 1 : 0;   // seitliche Richtung
        int cu = bu - 5 * du, cv = bv - 5 * dv;
        double d = Math.sqrt(cu * cu + cv * cv);
        int top = Math.min(ceilAt(k, cu, cv, d, rw(k, Math.atan2(cv, cu)), topH(k, cu, cv)), 8);
        for (int t = -3; t <= 4; t++) for (int q = -1; q <= 4; q++) top = Math.min(top, topH(k, bu + lu * t + du * q, bv + lv * t + dv * q) - 3);
        for (int t = -2; t <= 3; t++) for (int y = SHORE; y <= top + 1; y++) {
            boolean lat = t >= 0 && t <= 1;
            int u0 = bu + lu * t, v0 = bv + lv * t;
            // Nische (zwei Lagen) und Einfassung
            for (int q = -1; q <= 0; q++) {
                int u = u0 + du * q, v = v0 + dv * q;
                if (y == SHORE) k.set(u, y, v, lat ? p.basaltTiles : p.polBlack);
                else if (t == -2 || t == 3) k.set(u, y, v, q == 0 && y % 4 == 0 ? p.fire : Kit.AIR);
                else k.set(u, y, v, y == top + 1 ? p.chiseled : Kit.AIR);
            }
            if (t == -2 || t == 3) continue;
            int gu = u0 + du, gv = v0 + dv, fu = u0 + 2 * du, fv = v0 + 2 * dv;
            if (lat) {
                k.set(gu, y, gv, y == SHORE || y == top + 1 ? p.fire : p.glassO);
                k.set(fu, y, fv, y == SHORE || y == top + 1 ? p.fire : p.lava);
            } else {
                k.set(gu, y, gv, p.fire);
                k.set(fu, y, fv, p.fire);
            }
            k.set(u0 + 3 * du, y, v0 + 3 * dv, p.basaltBrick);
        }
    }

    // ── Die Schmiede (Schluessel 2) ────────────────────────────────────────

    /** Halle mit Hinterfuellung, Bodenmuster, Wandbaendern (Netherziegel unten, Feuerziegelband) und Deckenlichtern. */
    private void hall(Kit k, P p, int ua, int y0, int va, int ub, int y1, int vb) {
        if (!k.touches(ua - 1, va - 1, ub + 1, vb + 1)) return;
        for (int u = Math.max(ua - 1, k.u0()); u <= Math.min(ub + 1, k.u1()); u++) for (int v = Math.max(va - 1, k.v0()); v <= Math.min(vb + 1, k.v1()); v++) {
            boolean outer = u < ua || u > ub || v < va || v > vb;
            boolean edge = u == ua || u == ub || v == va || v == vb;
            boolean corner = (u == ua || u == ub) && (v == va || v == vb);
            for (int y = y0 - 1; y <= y1 + 1; y++) {
                BlockState s;
                int rel = y - y0;
                if (outer || y == y0 - 1 || y == y1 + 1) s = p.basaltBrick;
                else if (y == y0) s = (u + v) % 2 == 0 ? p.basaltTiles : k.rnd(82, u, y, v) < 0.2 ? p.blackstone : p.polBlack;
                else if (y == y1) s = (Math.floorMod(u, 6) == 0 && Math.floorMod(v, 6) == 0) ? p.shroom : p.pbb;
                else if (corner) s = p.polBasalt;
                else if (edge) {
                    if (rel <= 2) s = k.rnd(83, u, y, v) < 0.15 ? p.crackedNether : p.nether;
                    else if (rel == 3) s = p.fire;
                    else if (rel == 6 && (u + v) % 6 == 0) s = p.shroom;
                    else if (y == y1 - 1) s = p.chiseledNether;
                    else s = brick(k, p, u, y, v);
                } else s = Kit.AIR;
                k.set(u, y, v, s);
            }
        }
    }

    private void forge(Kit k, P p) {
        if (!k.touches(38, -78, R, 16)) return;
        hall(k, p, 47, -14, -14, 75, -3, 14);
        hall(k, p, 50, -16, -50, 84, -3, -22);
        hall(k, p, 52, -16, -76, 78, -6, -56);
        // Gang C1 vom Wendelweg (Boden -11) hinab zur Esse (-14)
        k.fill(42, -15, -4, 47, -4, 4, p.basaltBrick);
        for (int u = 40; u <= 47; u++) for (int v = -2; v <= 2; v++) {
            int fl = u <= 44 ? -11 : u == 45 ? -12 : u == 46 ? -13 : -14;
            if (u >= 42) {
                k.set(u, fl, v, Math.abs(v) == 2 ? p.polBlack : p.basaltTiles);
                for (int y = fl - 1; y >= -15 && y >= fl - 1; y--) k.set(u, y, v, p.basaltBrick);
            }
            for (int y = fl + 1; y <= -6; y++) k.set(u, y, v, Kit.AIR);
            if (u >= 42 && Math.abs(v) <= 1) for (int y = -5; y <= -5; y++) k.set(u, y, v, Kit.AIR);
        }
        // Portal in der Kammerwand
        for (int v = -5; v <= 5; v++) for (int y = -11; y <= -2; y++) {
            int av = Math.abs(v);
            boolean open = av <= 2 && y >= -10 && (y <= -6 || y == -5 && av <= 1);
            if (open) continue;
            k.set(42, y, v, av >= 4 ? p.polBasalt : y >= -4 ? (y == -3 && v == 0 ? p.gilded : p.fire) : av == 3 ? p.fire : p.chiseled);
        }
        k.set(42, -8, -4, p.shroom);
        k.set(42, -8, 4, p.shroom);

        // ── Esse ──
        k.fill(47, -13, -2, 47, -9, 2, Kit.AIR);
        if (k.touches(46, -15, 76, 15)) {
            // Herd mit Glutbecken und Abzugshaube
            k.fill(56, -13, -5, 66, -13, 5, p.fire);
            k.fill(59, -13, -2, 63, -13, 2, p.lava);
            for (int u = 58; u <= 64; u++) for (int v = -3; v <= 3; v++) {
                if (u == 58 || u == 64 || v == -3 || v == 3) k.set(u, -12, v, (u + v) % 2 == 0 ? p.fire : p.nether);
            }
            for (int[] c : new int[][] {{58, -3}, {64, -3}, {58, 3}, {64, 3}}) for (int y = -11; y <= -8; y++) k.set(c[0], y, c[1], p.redNether);
            int[][] hood = {{-7, 4}, {-6, 3}, {-5, 2}};
            for (int[] h : hood) for (int u = 61 - h[1] - 0; u <= 61 + h[1]; u++) for (int v = -h[1]; v <= h[1]; v++) {
                if (u == 61 - h[1] || u == 61 + h[1] || v == -h[1] || v == h[1]) k.set(u, h[0], v, h[0] == -7 ? p.fire : p.nether);
            }
            k.fill(60, -4, -1, 62, -4, 1, p.fire);
            k.set(61, -4, 0, p.magma);
            // Ambosse, Abschreckbottiche, Werkbaenke
            k.set(52, -13, 0, p.anvil);
            k.set(70, -13, 0, p.anvil);
            k.set(61, -13, -9, p.anvilD);
            k.set(61, -13, 9, p.anvil);
            k.set(54, -13, -6, p.cauldronW);
            k.set(68, -13, 6, p.cauldronW);
            k.set(54, -13, 6, p.grindstone);
            k.set(68, -13, -6, p.smithing);
            for (int v = -9; v <= 9; v += 3) {
                if (v == 0) continue;
                k.set(74, -13, v, p.blast(Direction.WEST));
                k.set(74, -12, v, p.smoker(Direction.WEST));
            }
            for (int v = -10; v <= 10; v++) k.set(74, -11, v, p.fireStairs(Direction.EAST, false));
            // Kohlehaufen
            k.fill(48, -13, -13, 51, -13, -10, p.coal);
            k.fill(48, -12, -13, 49, -12, -11, p.coal);
            k.fill(48, -13, 10, 50, -13, 13, p.coal);
            k.set(48, -12, 13, p.coal);
            // Saeulen mit Ketten und Laternen
            for (int[] c : new int[][] {{51, -10}, {51, 10}, {71, -10}, {71, 10}}) {
                for (int y = -13; y <= -4; y++) k.set(c[0], y, c[1], y == -4 ? p.chiseled : y == -9 ? p.magma : p.polBasalt);
            }
            for (int[] c : new int[][] {{56, -9}, {66, -9}, {56, 9}, {66, 9}, {61, -12}, {61, 12}}) {
                k.set(c[0], -4, c[1], p.chain);
                k.set(c[0], -5, c[1], p.chain);
                k.set(c[0], -6, c[1], p.lanternH);
            }
        }
        // Gang C2 zur Giesserei
        k.fill(65, -18, -21, 71, -8, -15, p.basaltBrick);
        for (int u = 66; u <= 70; u++) for (int v = -22; v <= -14; v++) {
            int fl = v >= -17 ? -14 : v == -18 ? -15 : -16;
            k.set(u, fl, v, u == 68 ? p.basaltTiles : p.polBlack);
            for (int y = fl + 1; y <= fl + 5; y++) k.set(u, y, v, Kit.AIR);
        }
        k.set(65, -11, -18, p.shroom);
        k.set(71, -11, -18, p.shroom);

        // ── Giesserei ──
        for (int u = 66; u <= 70; u++) for (int v = -22; v <= -22; v++) for (int y = -15; y <= -11; y++) k.set(u, y, v, Kit.AIR);
        if (k.touches(49, -51, 85, -21)) {
            // Schmelztiegel
            for (int u = 66; u <= 78; u++) for (int v = -46; v <= -34; v++) {
                int r2 = (u - 72) * (u - 72) + (v + 40) * (v + 40);
                if (r2 > 20) continue;
                for (int y = -15; y <= -12; y++) k.set(u, y, v, y == -13 ? p.redNether : p.fire);
                if (r2 < 9) {
                    k.set(u, -11, v, p.lava);
                    k.set(u, -10, v, p.lava);
                } else {
                    k.set(u, -11, v, p.fire);
                    k.set(u, -10, v, p.fire);
                    k.set(u, -9, v, r2 > 13 ? p.nether : Kit.AIR);
                }
            }
            // Steigleiter zum Tiegelrand (Deko)
            k.set(67, -14, -40, p.pbb);
            // Gussrinne unter Gitterrosten
            k.fill(53, -18, -34, 68, -17, -32, p.basaltBrick);
            for (int u = 54; u <= 67; u++) {
                k.set(u, -17, -33, p.lava);
                k.set(u, -16, -33, p.grate);
            }
            // Gussformen an der Nordwand
            for (int m = 0; m < 4; m++) {
                int u0 = 53 + m * 4;
                k.fill(u0 - 1, -18, -49, u0 + 3, -17, -45, p.basaltBrick);
                for (int u = u0; u <= u0 + 2; u++) for (int v = -48; v <= -47; v++) k.set(u, -16, v, p.lava);
                for (int u = u0 - 1; u <= u0 + 3; u++) k.set(u, -15, -46, p.netherFence);
                k.set(u0 - 1, -15, -47, p.netherFence);
                k.set(u0 + 3, -15, -47, p.netherFence);
                k.set(u0 - 1, -15, -48, p.netherFence);
                k.set(u0 + 3, -15, -48, p.netherFence);
            }
            // Haengende Giesspfannen
            for (int[] c : new int[][] {{60, -40}, {64, -28}, {78, -27}}) {
                for (int y = -8; y <= -4; y++) k.set(c[0], y, c[1], p.chain);
                k.set(c[0], -9, c[1], p.cauldronL);
            }
            // Barren- und Schlackestapel
            k.fill(52, -15, -24, 55, -15, -23, p.steel);
            k.fill(52, -14, -24, 54, -14, -24, p.redCopper);
            k.fill(80, -15, -24, 83, -14, -23, p.slag);
            k.fill(82, -15, -46, 83, -15, -40, p.rusty);
            // Saeulen
            for (int[] c : new int[][] {{56, -28}, {56, -42}, {64, -38}, {80, -32}}) {
                for (int y = -15; y <= -4; y++) k.set(c[0], y, c[1], y == -4 ? p.chiseled : y == -10 ? p.magma : p.polBasalt);
            }
            for (int[] c : new int[][] {{70, -28}, {58, -36}}) {
                k.set(c[0], -4, c[1], p.chain);
                k.set(c[0], -5, c[1], p.lanternH);
            }
        }
        // Gang C3 zur Waffenkammer
        k.fill(61, -17, -55, 67, -10, -51, p.basaltBrick);
        for (int u = 62; u <= 66; u++) for (int v = -56; v <= -50; v++) {
            k.set(u, -16, v, u == 64 ? p.basaltTiles : p.polBlack);
            for (int y = -15; y <= -11; y++) k.set(u, y, v, Kit.AIR);
        }
        k.set(61, -12, -53, p.shroom);
        k.set(67, -12, -53, p.shroom);

        // ── Waffenkammer ──
        for (int u = 62; u <= 66; u++) for (int y = -15; y <= -11; y++) k.set(u, y, -56, Kit.AIR);
        if (k.touches(51, -77, 79, -55)) {
            // Podest mit Schluesseltruhe
            k.fill(62, -15, -75, 68, -15, -72, p.pbb);
            k.fill(63, -15, -75, 67, -15, -73, p.chiseled);
            for (int u = 62; u <= 68; u++) k.set(u, -15, -71, p.stairs(Direction.NORTH, false));
            for (int u : new int[] {61, 69}) for (int y = -15; y <= -8; y++) k.set(u, y, -74, y == -8 ? p.shroom : y % 3 == 0 ? p.gilded : p.polBasalt);
            for (int u = 61; u <= 69; u++) k.set(u, -7, -74, p.chiseled);
            // Waffenstaender (Speere) und Ruestungsstaender
            for (int u : new int[] {57, 61, 69, 73}) for (int v : new int[] {-68, -62}) {
                k.set(u, -15, v, p.chiseled);
                k.set(u, -14, v, p.rod);
            }
            // Wandgestelle
            for (int v = -73; v <= -59; v += 2) {
                k.set(53, -15, v, p.grindstone);
                k.set(53, -14, v, p.bars);
                k.set(53, -13, v, p.bars);
                k.set(77, -15, v, p.anvilD);
                k.set(77, -14, v, p.chain);
                k.set(77, -13, v, p.chain);
            }
            k.set(65, -15, -60, p.anvil);
            for (int[] c : new int[][] {{58, -66}, {72, -66}, {65, -66}}) {
                k.set(c[0], -7, c[1], p.chain);
                k.set(c[0], -8, c[1], p.lanternH);
            }
        }
    }

    // ── Siegeltor, Wendelschacht, Arena ────────────────────────────────────

    private void sealWay(Kit k, P p) {
        if (!k.touches(-68, -12, -34, 16)) return;
        // Vorhalle in der Westwand
        for (int u = -42; u <= -37; u++) for (int v = -7; v <= 7; v++) {
            if (u >= -40 && Math.abs(v) <= 5) continue;
            k.set(u, SHORE, v, Math.abs(v) <= 3 ? p.basaltTiles : p.polBlack);
            for (int y = SHORE + 1; y <= -8; y++) k.set(u, y, v, Kit.AIR);
        }
        for (int u = -42; u <= -37; u++) for (int v = -5; v <= 5; v++) {
            k.set(u, SHORE, v, Math.abs(v) <= 1 ? p.chiseled : p.basaltTiles);
            for (int y = SHORE + 1; y <= -8; y++) k.set(u, y, v, Kit.AIR);
        }
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            for (int y = SHORE + 1; y <= -8; y++) {
                k.set(-41, y, sgn * 7, y % 4 == 0 ? p.fire : p.polBasalt);
                k.set(-38, y, sgn * 7, y % 4 == 0 ? p.fire : p.polBasalt);
            }
            k.set(-39, SHORE + 1, sgn * 6, p.pbb);
            k.set(-39, SHORE + 2, sgn * 6, p.netherrack);
            k.set(-39, SHORE + 3, sgn * 6, p.fireBlock);
        }
        for (int v = -7; v <= 7; v++) {
            k.set(-37, -7, v, p.chiseled);
            k.set(-38, -7, v, p.pbb);
            k.set(-39, -7, v, p.pbb);
            k.set(-40, -7, v, p.pbb);
            k.set(-41, -7, v, p.pbb);
            k.set(-42, -7, v, p.pbb);
        }
        // Gang hinter dem Tor
        k.fill(-51, -21, -4, -44, -12, 4, p.basaltBrick);
        for (int u = -51; u <= -44; u++) for (int v = -3; v <= 3; v++) {
            k.set(u, SHORE, v, Math.abs(v) <= 1 ? p.basaltTiles : p.polBlack);
            for (int y = SHORE + 1; y <= -13; y++) k.set(u, y, v, Kit.AIR);
        }
        k.set(-47, -16, -4, p.shroom);
        k.set(-47, -16, 4, p.shroom);
        // Wendelschacht um eine Lavasaeule
        if (k.touches(SU - 9, SV - 9, SU + 9, SV + 9)) {
            for (int u = SU - 8; u <= SU + 8; u++) for (int v = SV - 8; v <= SV + 8; v++) {
                double r = Math.sqrt((u - SU) * (u - SU) + (v - SV) * (v - SV));
                if (r > 8.3) continue;
                for (int y = -51; y <= -12; y++) {
                    BlockState s;
                    if (y == -51 || y == -12) s = p.basaltBrick;
                    else if (y == AF) s = r <= 2.9 ? p.pbb : (int) r % 2 == 0 ? p.basaltTiles : p.polBlack;
                    else if (r >= 6.5) {
                        double a = Math.toDegrees(Math.atan2(v - SV, u - SU)) + 360;
                        s = Math.floorMod(y, 5) == 0 ? p.fire : (y % 7 == 0 && ((int) (a / 30)) % 2 == 0 && r < 7.5) ? p.shroom : p.basaltBrick;
                    } else s = Kit.AIR;
                    k.set(u, y, v, s);
                }
            }
            for (int u = SU - 2; u <= SU + 2; u++) for (int v = SV - 2; v <= SV + 2; v++) {
                boolean core = Math.abs(u - SU) <= 1 && Math.abs(v - SV) <= 1;
                for (int y = AF + 1; y <= -16; y++) k.set(u, y, v, core ? p.lava : p.glassO);
                k.set(u, -15, v, core ? p.magma : p.pbb);
                k.set(u, -14, v, p.pbbSlab);
            }
            double perRad = 6.4 / 2.2;
            double a0 = Math.toDegrees(-31 / perRad);
            k.spiral(SU, SV, AF, SHORE, 3.4, 6.4, 2.2, a0, 4, p.pbb);
            // Ankunft oben
            for (int u = SU + 3; u <= SU + 6; u++) for (int v = SV - 3; v <= SV + 3; v++) {
                if ((u - SU) * (u - SU) + (v - SV) * (v - SV) > 42) continue;
                k.set(u, SHORE, v, p.chiseled);
                for (int y = SHORE + 1; y <= SHORE + 5; y++) k.set(u, y, v, Kit.AIR);
            }
        }
        // Durchbruch Gang -> Schacht
        for (int u = -51; u <= -48; u++) for (int v = -3; v <= 3; v++) {
            k.set(u, SHORE, v, Math.abs(v) <= 1 ? p.basaltTiles : p.polBlack);
            for (int y = SHORE + 1; y <= -13; y++) k.set(u, y, v, Kit.AIR);
        }
        // Siegeltor (nach dem Gang, damit die Rahmen stehen bleiben)
        k.sealGate(-43, SHORE, 0, Direction.EAST, 3, 7, p.pbb, p.gilded);
    }

    private void arena(Kit k, P p) {
        if (!k.touches(AU - 25, AV - 25, AU + 25, AV + 25) && !k.touches(SU - 4, SV, SU + 4, AV)) return;
        if (k.touches(AU - 25, AV - 25, AU + 25, AV + 25)) {
            for (int u = Math.max(AU - 25, k.u0()); u <= Math.min(AU + 25, k.u1()); u++) for (int v = Math.max(AV - 25, k.v0()); v <= Math.min(AV + 25, k.v1()); v++) {
                double du = u - AU, dv = v - AV, r = Math.sqrt(du * du + dv * dv);
                if (r > 24.5) continue;
                double a = Math.toDegrees(Math.atan2(dv, du));
                // Kuppel
                for (int y = AF + 1; y <= AF + 26; y++) {
                    double ny = (y - AF) / 26.0, nr = r / 23.0;
                    double dd = Math.sqrt(nr * nr + ny * ny);
                    if (dd > 1) continue;
                    double depth = (1 - dd) * 23;
                    BlockState s;
                    if (depth >= 2.5) s = Kit.AIR;
                    else {
                        double n = k.noise(111, u + y, v - y, 6), rr = k.rnd(112, u, y, v);
                        s = Math.abs(n - 0.5) < 0.03 ? p.magma : rr < 0.03 ? p.shroom : (y - AF) % 7 == 3 && r > 19 ? p.fire : n < 0.4 ? p.basalt : n < 0.7 ? p.blackstone : p.pbb;
                    }
                    k.set(u, y, v, s);
                }
                if (r > 23.5) continue;
                // Boden: Ringe, Pfuhl, Adern
                for (int y = AF - 3; y < AF; y++) k.set(u, y, v, p.basaltBrick);
                BlockState fl;
                int ring = (int) (r / 3);
                fl = ring % 2 == 0 ? p.pbb : (k.rnd(113, u, 0, v) < 0.15 ? p.magma : p.basaltTiles);
                if (r < 6) {
                    k.set(u, AF - 2, v, p.lava);
                    k.set(u, AF - 1, v, p.lava);
                    fl = p.lava;
                } else if (r < 7.5) {
                    fl = p.chiseled;
                    k.set(u, AF + 1, v, p.polBlack);
                } else if (r >= 8.5 && r <= 20.5) {
                    double rad = Math.toRadians(a);
                    for (int g = 0; g < 8; g++) {
                        double ga = Math.toRadians(45 * g);
                        double perp = Math.abs(du * Math.sin(ga) - dv * Math.cos(ga)), along = du * Math.cos(ga) + dv * Math.sin(ga);
                        if (perp < 0.6 && along > 0) {
                            fl = p.glassO;
                            k.set(u, AF - 1, v, p.lava);
                        }
                    }
                    if (rad > 99) fl = p.pbb;   // nie
                }
                k.set(u, AF, v, fl);
            }
            // Saeulenkranz
            for (int g = 0; g < 8; g++) {
                double ga = Math.toRadians(22.5 + 45 * g);
                double cu = AU + Math.cos(ga) * 19, cv = AV + Math.sin(ga) * 19;
                k.cylinder(cu, cv, AF + 1, AF + 1, 2.2, p.chiseled);
                k.cylinder(cu, cv, AF + 2, AF + 13, 1.5, p.polBasalt);
                k.cylinder(cu, cv, AF + 7, AF + 7, 1.5, p.magma);
                k.cylinder(cu, cv, AF + 14, AF + 15, 2.2, p.pbb);
            }
        }
        // Gang vom Schacht zur Arena
        k.fill(SU - 3, AF - 1, SV + 7, SU + 3, AF + 6, SV + 14, p.basaltBrick);
        for (int u = SU - 2; u <= SU + 2; u++) for (int v = SV + 6; v <= AV - 21; v++) {
            if (v >= SV + 7) k.set(u, AF, v, Math.abs(u - SU) <= 0 ? p.basaltTiles : p.polBlack);
            for (int y = AF + 1; y <= AF + 5; y++) k.set(u, y, v, Kit.AIR);
        }
        k.set(SU - 3, AF + 3, SV + 10, p.shroom);
        k.set(SU + 3, AF + 3, SV + 10, p.shroom);
    }

    // ── Plan-Eintraege ─────────────────────────────────────────────────────

    private void plan(Kit k) {
        // Schluessel
        k.keyChest(0, -18, 15, Direction.NORTH);          // 1: Inselkette, mittlere Insel
        k.keyChest(65, -14, -74, Direction.SOUTH);       // 2: Waffenkammer am Ende der Schmiede
        k.elite(0, 45, -3, HOUND);                       // 3: Kraternest auf dem Schlot
        // Beute
        k.loot(-8, -1, 39, Direction.EAST);
        k.barrel(73, -13, 12);
        k.barrel(82, -15, -48);
        k.barrel(52, -15, -27);
        k.loot(76, -15, -58, Direction.WEST);
        k.loot(3, 45, -3, Direction.WEST);
        k.loot(0, 41, -11, Direction.SOUTH);
        k.loot(36, -19, 16, Direction.WEST);
        // Spawner
        k.spawner(0, -16, 21, IMP);
        k.spawner(-5, 24, -2, IMP);
        k.spawner(50, -13, 6, HOUND);
        k.spawner(72, -13, -9, IMP);
        k.spawner(58, -15, -44, IMP);
        k.spawner(80, -15, -27, HOUND);
        k.spawner(58, -15, -66, HOUND);
        // Bewohner
        k.mob(-10, 1, 66, HOUND);
        k.mob(10, 1, 74, HOUND);
        k.mob(-31, -19, -12, HOUND);
        k.mob(-30, -19, 10, HOUND);
        k.mob(5, -1, 36, IMP);
        k.mob(2, -19, 2, IMP);
        k.mob(55, -13, -8, HOUND);
        k.mob(66, -13, 8, HOUND);
        k.mob(61, -13, -11, IMP);
        k.mob(60, -15, -30, HOUND);
        k.mob(76, -15, -32, HOUND);
        k.mob(66, -15, -26, IMP);
        k.mob(72, -15, -64, HOUND);
        k.mob(65, -15, -62, IMP);
        k.mob(-4, 45, 0, IMP);
        k.mob(4, 45, 3, IMP);
        // Boss und Bossbeute
        k.boss(AU, AF + 1, AV + 10);
        k.bossChest(AU - 4, AF + 1, AV + 17, Direction.NORTH);
        k.bossChest(AU + 4, AF + 1, AV + 17, Direction.NORTH);
    }
}
