package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.progression.SpaceProgression.Stage;

import java.util.ArrayDeque;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Minmus: "Kryo-Geode / Glacial Observatory".
 * <p>
 * Oben ein verlassenes Eis-Observatorium (Teleskopkuppel mit offenem Spalt und schraegem Rohr, Antennen, vereiste
 * Funkmasten, Schuesseln). Unter der Kuppel fuehrt eine Wendelrampe um einen riesigen Kristall in eine hohle
 * Kryo-Geode (ca. 76 x 68 x 48). Drei Pruefungen: Parkour ueber schwebende Eisschollen zum Kristallnest unter der
 * Decke (Schluessel 1), vereistes Forschungslabor-Labyrinth in der Ostwand (Schluessel 2), Kryo-Lager mit
 * Siegelwaechter in der Westwand (Schluessel 3). Siegeltor im Torhaus an der Nordwand, dahinter eine Treppe hinab
 * zur Arena auf einem zugefrorenen unterirdischen See.
 */
public final class MinmusDungeon extends CustomDungeon {

    static final int RAD = 80;
    /** Boden (Block) von Geode, Labor und Kryo-Lager; Fuesse auf FLOOR + 1. */
    static final int FLOOR = -47;
    /** Geode: Mittelpunkt und Radien des Hohlraums. */
    static final double GU = 0, GY = -31, GV = 8, GRU = 38, GRY = 24, GRV = 34;
    /** Wendelrampe: Startwinkel, Hoehe je Radiant, Radien. */
    static final double RAMP_A0 = 137, RAMP_PER_RAD = 4.0, RAMP_IN = 3.6, RAMP_OUT = 8.5;
    /** Parkour-Kristallsaeule (Schluessel 1). */
    static final int PU = -18, PV = 18;
    static final double FLOE_R = 6.5, FLOE_A0 = -29, FLOE_STEP = 36;
    static final int FLOES = 24, NEST_Y = FLOOR + 1 + FLOES;
    /** Labor-Labyrinth (Schluessel 2). */
    static final int LU0 = 40, LV0 = -24, LNU = 9, LNV = 15, L_ENT_J = 8;
    static final int[][] LAB_ROOMS = {{2, 2}, {5, 6}, {1, 11}, {6, 11}};
    /** Kryo-Lager (Schluessel 3). */
    static final int CU = -59, CV0 = -26, CV1 = 12, CHW = 15, C_SPRING = -41, C_RY = 11;
    static final int[] TANK_V = {-20, -11, -2, 7};
    /** Siegeltor und Arena. */
    static final int GATE_V = -30, AF = -57, AV = -61;
    static final double ARU = 22, ARY = 20, ARV = 16;

    @Override public Stage stage() { return Stage.MINMUS; }
    @Override public int radius() { return RAD; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.minmus.MinmusBlocks.MINMUS_REGOLITH.get()); }
    @Override public BlockState filler() { return Kit.b(com.space_m.dim.minmus.MinmusBlocks.MINMUS_STONE.get()); }
    @Override public int minY() { return -62; }
    @Override public int maxY() { return 48; }
    @Override public int minBase() { return 66; }
    @Override public int maxBase() { return 190; }
    @Override public int[] entry() { return new int[] {0, 1, 74}; }
    @Override public String boss() { return "space_beyond:cryo_behemoth"; }

    static final String WISP = "space_beyond:frost_wisp", BRUTE = "space_beyond:glacier_brute";

    // ── Materialien ──────────────────────────────────────────────────────────

    private static final class M {
        final BlockState air = Kit.AIR;
        final BlockState packed = Kit.b(Blocks.PACKED_ICE), blue = Kit.b(Blocks.BLUE_ICE), snow = Kit.b(Blocks.SNOW_BLOCK), calcite = Kit.b(Blocks.CALCITE);
        final BlockState snowLayer = Kit.b(Blocks.SNOW);
        final BlockState prism = Kit.b(Blocks.PRISMARINE), prismBr = Kit.b(Blocks.PRISMARINE_BRICKS), darkPrism = Kit.b(Blocks.DARK_PRISMARINE);
        final BlockState prismWall = Kit.b(Blocks.PRISMARINE_WALL), amethyst = Kit.b(Blocks.AMETHYST_BLOCK);
        final BlockState cluster = Kit.facingAll(Blocks.AMETHYST_CLUSTER, Direction.UP);
        final BlockState lbGlass = Kit.b(Blocks.LIGHT_BLUE_STAINED_GLASS), cyGlass = Kit.b(Blocks.CYAN_STAINED_GLASS), wGlass = Kit.b(Blocks.WHITE_STAINED_GLASS);
        final BlockState sea = Kit.b(Blocks.SEA_LANTERN), glow = Kit.b(Blocks.PEARLESCENT_FROGLIGHT), verdant = Kit.b(Blocks.VERDANT_FROGLIGHT), pearl = Kit.b(Blocks.PEARLESCENT_FROGLIGHT);
        final BlockState soulHang = Kit.lantern(Blocks.SOUL_LANTERN, true), soulStand = Kit.lantern(Blocks.SOUL_LANTERN, false);
        final BlockState chain = Kit.b(Blocks.CHAIN), bars = Kit.b(Blocks.IRON_BARS), endRod = Kit.facingAll(Blocks.END_ROD, Direction.UP);
        final BlockState rod = Kit.facingAll(Blocks.LIGHTNING_ROD, Direction.UP);
        final BlockState white = Kit.b(Blocks.WHITE_CONCRETE), lgray = Kit.b(Blocks.LIGHT_GRAY_CONCRETE), cyan = Kit.b(Blocks.CYAN_CONCRETE), lblue = Kit.b(Blocks.LIGHT_BLUE_CONCRETE);
        final BlockState quartz = Kit.b(Blocks.SMOOTH_QUARTZ), quartzPillar = Kit.b(Blocks.QUARTZ_PILLAR), basalt = Kit.b(Blocks.SMOOTH_BASALT), diorite = Kit.b(Blocks.POLISHED_DIORITE);
        final BlockState water = Kit.b(Blocks.WATER), cauldron = Kit.b(Blocks.CAULDRON), brewing = Kit.b(Blocks.BREWING_STAND), wool = Kit.b(Blocks.WHITE_WOOL);
        final BlockState benchTop = Kit.slab(Blocks.SMOOTH_QUARTZ_SLAB, true), quartzSlab = Kit.slab(Blocks.SMOOTH_QUARTZ_SLAB, false);
        final BlockState prismSlab = Kit.slab(Blocks.PRISMARINE_BRICK_SLAB, false), darkSlab = Kit.slab(Blocks.DARK_PRISMARINE_SLAB, false);
        final BlockState stone = Kit.b(com.space_m.dim.minmus.MinmusBlocks.MINMUS_STONE.get());
        final BlockState smooth = Kit.b(com.space_m.dim.minmus.MinmusBlocks.MINMUS_SMOOTH.get());
        final BlockState rego = Kit.b(com.space_m.dim.minmus.MinmusBlocks.MINMUS_REGOLITH.get());
        final BlockState concrete = Kit.b(ModBlocks.CONCRETE_SMOOTH.get()), concreteG = Kit.b(ModBlocks.CONCRETE.get());
        final BlockState brick = Kit.b(ModBlocks.BRICK_CONCRETE.get()), brickCr = Kit.b(ModBlocks.BRICK_CONCRETE_CRACKED.get());
        final BlockState steel = Kit.b(ModBlocks.DECO_STEEL.get()), alu = Kit.b(ModBlocks.DECO_ALUMINUM.get()), titan = Kit.b(ModBlocks.DECO_TITANIUM.get());
        final BlockState pipe = Kit.b(ModBlocks.DECO_PIPE_QUAD.get()), hazard = Kit.b(ModBlocks.CONCRETE_HAZARD.get());
        final BlockState crt = Kit.b(ModBlocks.CRT_BROKEN.get()), crtOn = Kit.b(ModBlocks.CRT_CLEAN.get());

        BlockState stairs(net.minecraft.world.level.block.Block b, Direction up) {
            return Kit.stairs(b, up, false);
        }
    }

    private interface Shell { BlockState at(int u, int y, int v, double t); }
    private interface Ceil { double at(int u, int v); }

    private static double sq(double x) { return x * x; }

    // ── Bau ──────────────────────────────────────────────────────────────────

    @Override
    protected void build(Kit k) {
        M m = new M();
        Maze z = maze(k);

        // Unterirdisch zuerst: Geode, Arena, dann Durchbrueche
        geode(k, m);
        geodeSpikes(k, m);
        geodePillars(k, m);
        pool(k, m);
        islands(k, m);
        floorDeco(k, m);
        camp(k, m);
        arena(k, m);
        labPortal(k, m);
        lab(k, m, z);
        cryo(k, m);
        cryoCorridor(k, m);
        gatehouse(k, m);
        descent(k, m);
        parkour(k, m);

        // Oberflaeche
        plaza(k, m);
        dome(k, m);
        shaft(k, m);
        centralCrystal(k, m);
        ramp(k, m);
        domeInterior(k, m);
        telescope(k, m);
        vestibule(k, m);
        path(k, m);
        hut(k, m, 31, -4, 41, 6, 6, Direction.WEST);
        shed(k, m);
        mast(k, m, 46, -30, 40);
        mast(k, m, -44, -36, 44);
        mast(k, m, -50, 46, 34);
        dish(k, m, 52, 28, 7, 6);
        dish(k, m, -58, 8, 6, 5);
        surfaceSpires(k, m);

        plan(k, z);
    }

    // ── Plan-Eintraege (immer) ───────────────────────────────────────────────

    private void plan(Kit k, Maze z) {
        // Schluessel 1: Kristallnest
        double na = Math.toRadians(FLOE_A0 + FLOES * FLOE_STEP + 50);
        int nu = PU + (int) Math.round(5 * Math.cos(na)), nv = PV + (int) Math.round(5 * Math.sin(na));
        k.keyChest(nu, NEST_Y + 1, nv, Direction.EAST);
        // Schluessel 2: Labyrinth (fernste Sackgasse)
        int[] kc = z.keyChest;
        k.keyChest(kc[0], FLOOR + 1, kc[1], z.keyFacing);
        // Schluessel 3: Siegelwaechter im Kryo-Lager
        k.elite(CU, FLOOR + 1, -4, BRUTE);

        // Beute
        k.loot(-15, 1, 8, Direction.EAST);                 // Kuppel
        k.loot(39, 1, 5, Direction.WEST);                  // Laborhuette
        k.barrel(-37, 1, 21);                              // Generatorschuppen
        k.loot(13, FLOOR + 1, 22, Direction.WEST);         // Forschercamp in der Geode
        int[] r1 = roomOrigin(0), r3 = roomOrigin(2), r2 = roomOrigin(1);
        k.loot(r1[0] + 6, FLOOR + 1, r1[1] + 6, Direction.NORTH);
        k.loot(r3[0] + 6, FLOOR + 1, r3[1] + 6, Direction.NORTH);
        k.barrel(r2[0] + 6, FLOOR + 1, r2[1] + 6);
        k.loot(CU, FLOOR + 1, CV0 + 2, Direction.SOUTH);   // Kryo-Leitstand
        k.barrel(-46, FLOOR + 1, 10);

        // Spawner
        k.spawner(9, FLOOR + 1, -7, WISP);
        k.spawner(12, FLOOR + 1, 28, WISP);
        k.spawner(r2[0] + 1, FLOOR + 1, r2[1] + 6, WISP);
        int[] r4 = roomOrigin(3);
        k.spawner(r4[0] + 1, FLOOR + 1, r4[1] + 6, BRUTE);
        k.spawner(-72, FLOOR + 1, -15, BRUTE);
        k.spawner(-46, FLOOR + 1, -20, WISP);
        k.spawner(-4, FLOOR + 1, -22, WISP);

        // Bewohner
        k.mob(10, 1, 36, WISP);
        k.mob(-24, 1, -8, WISP);
        k.mob(12, 1, 0, WISP);
        k.mob(8, FLOOR + 1, -4, BRUTE);
        k.mob(-12, FLOOR + 1, 2, BRUTE);
        k.mob(18, FLOOR + 1, 12, BRUTE);
        k.mob(-6, FLOOR + 1, 26, WISP);
        k.mob(4, -35, 3, WISP);
        k.mob(20, FLOOR + 1, -2, WISP);
        for (int[] c : z.mobCells) k.mob(cellU(c[0]) + 1, FLOOR + 1, cellV(c[1]) + 1, BRUTE);
        k.mob(r3[0] + 3, FLOOR + 1, r3[1] + 1, WISP);
        k.mob(CU, FLOOR + 1, -16, BRUTE);
        k.mob(-56, FLOOR + 1, 9, BRUTE);
        k.mob(-62, FLOOR + 1, 2, BRUTE);
        k.mob(-46, FLOOR + 1, -12, WISP);

        // Boss und Bosskisten
        k.boss(0, AF + 1, AV);
        k.bossChest(-7, AF + 1, AV - 10, Direction.SOUTH);
        k.bossChest(7, AF + 1, AV - 10, Direction.SOUTH);
    }

    // ── Geode ────────────────────────────────────────────────────────────────

    private static double geodeCeil(int u, int v) {
        double h = 1 - sq((u - GU) / GRU) - sq((v - GV) / GRV);
        return h <= 0 ? Double.NaN : GY + GRY * Math.sqrt(h);
    }

    /** 0 in der Mitte, 1 am Rand des Geodenbodens (y = FLOOR). */
    private static double floorEll(double u, double v) {
        double f = 1 - sq((FLOOR - GY) / GRY);
        return (sq((u - GU) / GRU) + sq((v - GV) / GRV)) / f;
    }

    private BlockState crystalMix(Kit k, M m, int u, int y, int v) {
        double r = k.rnd(21, u, y, v);
        if (r < 0.018) return m.sea;
        if (r < 0.03) return m.verdant;
        double n = k.noise(22, u + y * 5, v - y * 3, 6);
        if (n < 0.2) return m.amethyst;
        if (n < 0.36) return m.prism;
        if (n < 0.5) return m.blue;
        if (n < 0.62) return m.packed;
        if (n < 0.76) return m.lbGlass;
        if (n < 0.86) return m.cyGlass;
        return m.darkPrism;
    }

    private BlockState geodeFloor(Kit k, M m, int u, int v) {
        double c = k.noise(12, u, v, 3), n = k.noise(11, u, v, 7);
        if (c > 0.76) return m.blue;
        if (n < 0.28) return m.snow;
        if (n < 0.52) return m.packed;
        if (n < 0.68) return m.calcite;
        return k.rnd(13, u, 0, v) < 0.5 ? m.smooth : m.rego;
    }

    private void geode(Kit k, M m) {
        blob(k, GU, GY, GV, GRU, GRY, GRV, -60, -1, 4.6, (u, y, v, t) -> {
            if (t < 0) {
                if (y > FLOOR) return m.air;
                if (y == FLOOR) return geodeFloor(k, m, u, v);
                return y == FLOOR - 1 ? m.packed : m.calcite;
            }
            if (t < 1.6) return crystalMix(k, m, u, y, v);
            if (t < 3.1) {
                double n = k.noise(23, u + y * 2, v + y, 5);
                return n < 0.45 ? m.packed : n < 0.7 ? m.blue : m.calcite;
            }
            return k.rnd(24, u, y, v) < 0.6 ? m.basalt : m.calcite;
        });
    }

    /** Kristallspitze von der Basis aus in Richtung d (normiert), drei Abschnitte mit abnehmender Dicke. */
    private static void spike(Kit k, double bu, double by, double bv, double du, double dy, double dv, double len, double r0,
                              BlockState a, BlockState b, BlockState c) {
        double p1u = bu + du * len * 0.45, p1y = by + dy * len * 0.45, p1v = bv + dv * len * 0.45;
        double p2u = bu + du * len * 0.8, p2y = by + dy * len * 0.8, p2v = bv + dv * len * 0.8;
        k.line(bu, by, bv, p1u, p1y, p1v, r0, a);
        k.line(p1u, p1y, p1v, p2u, p2y, p2v, r0 * 0.62, b);
        k.line(p2u, p2y, p2v, bu + du * len, by + dy * len, bv + dv * len, Math.max(0.3, r0 * 0.28), c);
    }

    private void geodeSpikes(Kit k, M m) {
        for (int i = 0; i < 90; i++) {
            double th = k.rnd(31, i, 1, 0) * Math.PI * 2, s = k.rnd(32, i, 2, 0);
            double sph = -0.3 + s * 1.25, cph = Math.sqrt(1 - sph * sph);
            double bu = GU + GRU * cph * Math.cos(th) * 1.02, by = GY + GRY * sph * 1.02, bv = GV + GRV * cph * Math.sin(th) * 1.02;
            if (by < FLOOR + 3) continue;
            double du = GU - bu, dy = GY - 3 - by, dv = GV - bv;
            double l = Math.sqrt(du * du + dy * dy + dv * dv);
            du /= l; dy /= l; dv /= l;
            if (sph > 0.55) {   // Decke: eher senkrecht haengend
                double w = (sph - 0.55) * 2.2;
                du *= 1 - w; dv *= 1 - w; dy = dy * (1 - w) - w;
                l = Math.sqrt(du * du + dy * dy + dv * dv);
                du /= l; dy /= l; dv /= l;
            }
            double len = 5 + k.rnd(33, i, 3, 0) * 8, r0 = 1.2 + k.rnd(34, i, 4, 0) * 1.3;
            double tu = bu + du * len, ty = by + dy * len, tv = bv + dv * len;
            // Wege frei halten: Rampe/Schacht, Parkoursaeule, Durchbrueche, Torhaus
            if (Math.hypot(tu - GU, tv - GV) < 14 || Math.hypot(bu - GU, bv - GV) < 13) continue;
            if (Math.hypot(tu - PU, tv - PV) < 12 && ty > FLOOR - 1) continue;
            if (Math.hypot(bu - 30, bv - 10) < 11 && by < -30) continue;
            if (Math.hypot(bu + 26, bv + 6) < 11 && by < -30) continue;
            if (Math.abs(bu) < 14 && bv < -8 && by < -28) continue;
            if (ty < FLOOR + 4) continue;
            switch (i % 4) {
                case 0 -> spike(k, bu, by, bv, du, dy, dv, len, r0, m.amethyst, m.amethyst, m.lbGlass);
                case 1 -> spike(k, bu, by, bv, du, dy, dv, len, r0, m.prism, m.prismBr, m.lbGlass);
                case 2 -> spike(k, bu, by, bv, du, dy, dv, len, r0, m.blue, m.packed, m.cyGlass);
                default -> spike(k, bu, by, bv, du, dy, dv, len, r0, m.packed, m.sea, m.lbGlass);
            }
        }
    }

    /** Sechseckige Kristallsaeule vom Boden bis zur Decke, unten und oben aufgeweitet. */
    private void pillar(Kit k, M m, int cu, int cv, double r, int floorY, Ceil ceil, int salt) {
        int ir = (int) Math.ceil(r + 2.6);
        if (!k.touches(cu - ir, cv - ir, cu + ir, cv + ir)) return;
        double topC = ceil.at(cu, cv);
        for (int u = Math.max(cu - ir, k.u0()); u <= Math.min(cu + ir, k.u1()); u++) {
            for (int v = Math.max(cv - ir, k.v0()); v <= Math.min(cv + ir, k.v1()); v++) {
                double du = Math.abs(u - cu), dv = Math.abs(v - cv);
                double hx = Math.max(dv, du * 0.866 + dv * 0.5);
                double top = ceil.at(u, v);
                if (Double.isNaN(top)) continue;
                for (int y = floorY + 1; y <= (int) top + 2; y++) {
                    double rr = r + 2.0 * Math.max(0, 1 - (y - floorY - 1) / 4.0) + 2.0 * Math.max(0, 1 - (topC - y) / 5.0);
                    if (hx > rr) continue;
                    BlockState s;
                    if (hx <= rr - 1.2) s = Math.floorMod(y + salt, 6) == 0 ? m.sea : m.blue;
                    else if (Math.floorMod(y + salt * 3, 11) == 0) s = m.verdant;
                    else {
                        double n = k.noise(41 + salt, u * 3 + y, v * 3 - y, 4);
                        s = switch (salt % 3) {
                            case 0 -> n < 0.4 ? m.prism : n < 0.75 ? m.lbGlass : m.prismBr;
                            case 1 -> n < 0.45 ? m.amethyst : n < 0.75 ? m.cyGlass : m.blue;
                            default -> n < 0.4 ? m.blue : n < 0.7 ? m.lbGlass : m.packed;
                        };
                    }
                    k.set(u, y, v, s);
                }
            }
        }
    }

    private void geodePillars(Kit k, M m) {
        Ceil c = MinmusDungeon::geodeCeil;
        pillar(k, m, 15, -6, 2.5, FLOOR, c, 0);
        pillar(k, m, 18, 26, 2.5, FLOOR, c, 1);
        pillar(k, m, -4, 30, 2.2, FLOOR, c, 2);
        pillar(k, m, -14, -10, 2.5, FLOOR, c, 4);
        pillar(k, m, 24, 6, 1.8, FLOOR, c, 5);
        pillar(k, m, PU, PV, 2.5, FLOOR, c, 3);
    }

    /** Schmelzwasserbecken unter dem Parkour (Stuerze landen im Wasser). */
    private void pool(Kit k, M m) {
        if (!k.touches(PU - 11, PV - 11, PU + 11, PV + 11)) return;
        for (int u = Math.max(PU - 11, k.u0()); u <= Math.min(PU + 11, k.u1()); u++) {
            for (int v = Math.max(PV - 11, k.v0()); v <= Math.min(PV + 11, k.v1()); v++) {
                double d = Math.hypot(u - PU, v - PV);
                if (d < 4.8 || d > 10.6 || floorEll(u, v) > 0.84) continue;
                if (d > 9.5) {
                    k.set(u, FLOOR, v, k.rnd(51, u, 0, v) < 0.25 ? m.verdant : m.prismBr);
                    continue;
                }
                k.set(u, FLOOR, v, m.water);
                k.set(u, FLOOR - 1, v, m.water);
                k.set(u, FLOOR - 2, v, k.rnd(52, u, 0, v) < 0.18 ? m.glow : m.blue);
            }
        }
    }

    /** Schwebende Eisinseln (Deko) mit Eiszapfen und Kristallen. */
    private void islands(Kit k, M m) {
        int[][] is = {{17, -27, 14, 5}, {9, -19, -9, 4}, {-6, -33, -13, 4}, {-27, -26, 2, 3}, {6, -24, 33, 3}};
        for (int[] s : is) {
            int cu = s[0], cy = s[1], cv = s[2], r = s[3];
            if (!k.touches(cu - r - 1, cv - r - 1, cu + r + 1, cv + r + 1)) continue;
            for (int u = Math.max(cu - r - 1, k.u0()); u <= Math.min(cu + r + 1, k.u1()); u++) {
                for (int v = Math.max(cv - r - 1, k.v0()); v <= Math.min(cv + r + 1, k.v1()); v++) {
                    double d = Math.hypot(u - cu, v - cv) + k.rnd(61, u, 0, v) * 0.6;
                    if (d > r + 0.3) continue;
                    int depth = (int) Math.round((r + 0.5 - d) * 1.6);
                    k.set(u, cy, v, k.noise(62, u, v, 3) < 0.5 ? m.snow : m.packed);
                    for (int y = cy - 1; y >= cy - depth; y--) k.set(u, y, v, y == cy - depth ? m.lbGlass : (y + u + v) % 3 == 0 ? m.blue : m.packed);
                    if (k.rnd(63, u, 0, v) < 0.12) k.set(u, cy + 1, v, m.cluster);
                }
            }
            spike(k, cu + 1, cy, cv, 0.1, 1, -0.15, r + 2, 1.1, m.prism, m.lbGlass, m.cyGlass);
            k.set(cu, cy, cv, m.verdant);
        }
    }

    /** Kristallstuempfe am Rand des Geodenbodens. */
    private void floorDeco(Kit k, M m) {
        int uLo = Math.max(-30, k.u0()), uHi = Math.min(30, k.u1()), vLo = Math.max(-20, k.v0()), vHi = Math.min(36, k.v1());
        for (int u = uLo; u <= uHi; u++) for (int v = vLo; v <= vHi; v++) {
            double e = floorEll(u, v);
            if (e > 0.95 || e < 0.5) continue;
            if (k.rnd(64, u, 0, v) > 0.03) continue;
            if (Math.hypot(u - PU, v - PV) < 12 || Math.hypot(u - 24, v - 10) < 7 || Math.hypot(u + 22, v + 6) < 7 || (Math.abs(u) < 10 && v < -8)) continue;
            if (Math.hypot(u - 12, v - 22) < 6) continue;
            int h = 1 + (int) (k.rnd(65, u, 0, v) * 3);
            BlockState s = k.pick(66, u, 0, v, m.blue, m.prism, m.amethyst, m.packed);
            for (int y = FLOOR + 1; y <= FLOOR + h; y++) k.set(u, y, v, s);
            k.set(u, FLOOR + h + 1, v, m.cluster);
        }
    }

    /** Verlassenes Forschercamp am Fuss der Rampe. */
    private void camp(Kit k, M m) {
        if (!k.touches(6, 16, 16, 26)) return;
        int y = FLOOR + 1;
        // Zelt (Plane ueber Stangen)
        for (int v = 17; v <= 21; v++) {
            k.set(7, y, v, m.wool);
            k.set(8, y + 1, v, m.wool);
            k.set(9, y + 2, v, m.wool);
            k.set(10, y + 1, v, m.wool);
            k.set(11, y, v, m.wool);
        }
        k.set(9, y, 17, m.chain);
        k.set(9, y + 1, 17, m.chain);
        // Kisten, Lampe, Geraete
        k.set(14, y, 24, m.steel);
        k.set(15, y, 24, m.alu);
        k.set(15, y + 1, 24, m.crt);
        k.set(14, y, 19, m.quartzPillar);
        k.set(14, y + 1, 19, m.quartzPillar);
        k.set(14, y + 2, 19, m.soulStand);
        k.set(11, y, 24, m.cauldron);
        k.set(8, y, 23, m.snowLayer);
        k.set(12, y, 17, m.snowLayer);
    }

    // ── Parkour zum Kristallnest (Schluessel 1) ──────────────────────────────

    private void parkour(Kit k, M m) {
        if (!k.touches(PU - 10, PV - 10, PU + 10, PV + 10)) return;
        for (int i = 0; i < FLOES; i++) {
            double a = Math.toRadians(FLOE_A0 + i * FLOE_STEP);
            int fu = PU + (int) Math.round(FLOE_R * Math.cos(a)), fv = PV + (int) Math.round(FLOE_R * Math.sin(a));
            int fy = FLOOR + 1 + i;
            int type = i % 4;   // 0 gross, 1 klein (Plus), 2 gross, 3 Spitze
            if (type == 3) {
                k.set(fu, fy, fv, m.amethyst);
                k.set(fu, fy - 1, fv, m.prism);
                if (i > 3) {
                    k.set(fu, fy - 2, fv, m.lbGlass);
                    k.set(fu, fy - 3, fv, m.cyGlass);
                }
                continue;
            }
            for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
                if (type == 1 && du != 0 && dv != 0) continue;
                boolean c = du == 0 && dv == 0;
                BlockState top = c && i % 3 == 0 ? m.verdant : k.pick(71, fu + du, fy, fv + dv, m.packed, m.snow, m.blue, m.packed);
                k.set(fu + du, fy, fv + dv, top);
                if (i > 2 && (du == 0 || dv == 0)) k.set(fu + du, fy - 1, fv + dv, m.packed);
            }
            if (i > 2) {
                k.set(fu, fy - 2, fv, m.blue);
                k.set(fu, fy - 3, fv, m.lbGlass);
            }
        }
        // Nest: Kreisringsektor um die Saeule unter der Decke
        double aN = FLOE_A0 + FLOES * FLOE_STEP;
        for (int u = Math.max(PU - 8, k.u0()); u <= Math.min(PU + 8, k.u1()); u++) {
            for (int v = Math.max(PV - 8, k.v0()); v <= Math.min(PV + 8, k.v1()); v++) {
                double d = Math.hypot(u - PU, v - PV);
                if (d < 2.4 || d > 7.6) continue;
                double rel = Math.toDegrees(Math.atan2(v - PV, u - PU)) - aN;
                rel = ((rel % 360) + 540) % 360 - 180;   // -180..180
                if (rel < -15 || rel > 110) continue;
                k.set(u, NEST_Y, v, d > 6.4 ? m.prismBr : k.pick(72, u, 0, v, m.amethyst, m.calcite, m.blue, m.amethyst));
                if (d < 5 && d > 3) {
                    k.set(u, NEST_Y - 1, v, m.amethyst);
                    if (k.rnd(73, u, 0, v) < 0.5) k.set(u, NEST_Y - 2, v, m.lbGlass);
                }
                if (d > 6.4 && rel > -2) {
                    k.set(u, NEST_Y + 1, v, Math.floorMod(u + v, 4) == 0 ? m.sea : m.amethyst);
                    if (k.rnd(74, u, 0, v) < 0.55) k.set(u, NEST_Y + 2, v, m.cluster);
                } else if (d > 2.4 && d < 3.6 && k.rnd(75, u, 0, v) < 0.4) {
                    k.set(u, NEST_Y + 1, v, m.cluster);
                }
            }
        }
    }

    // ── Wendelrampe, Schacht, Zentralkristall ────────────────────────────────

    private void shaft(Kit k, M m) {
        if (!k.touches(-11, -3, 11, 19)) return;
        for (int u = Math.max(-11, k.u0()); u <= Math.min(11, k.u1()); u++) {
            for (int v = Math.max(-3, k.v0()); v <= Math.min(19, k.v1()); v++) {
                double d = Math.hypot(u - GU, v - GV);
                if (d > 10.6) continue;
                for (int y = -9; y <= 0; y++) {
                    if (d <= 8.51) k.set(u, y, v, m.air);
                    else if (y < 0) k.set(u, y, v, y % 3 == 0 ? (Math.floorMod(u * 7 + v * 3, 5) == 0 ? m.sea : m.steel) : y == -9 ? m.darkPrism : m.concrete);
                }
            }
        }
    }

    private void centralCrystal(Kit k, M m) {
        if (!k.touches(-4, 4, 4, 12)) return;
        for (int u = Math.max(-4, k.u0()); u <= Math.min(4, k.u1()); u++) {
            for (int v = Math.max(4, k.v0()); v <= Math.min(12, k.v1()); v++) {
                double du = Math.abs(u - GU), dv = Math.abs(v - GV);
                double hx = Math.max(dv, du * 0.866 + dv * 0.5);
                for (int y = FLOOR - 1; y <= 21; y++) {
                    double r = y <= 12 ? 3.2 : 3.2 * (21 - y) / 9.0;
                    if (hx > r) continue;
                    BlockState s;
                    if (hx <= r - 1.1) s = Math.floorMod(y, 4) == 0 ? m.sea : m.blue;
                    else {
                        double n = k.noise(81, u * 5 + y, v * 5 - y, 3);
                        s = n < 0.4 ? m.lbGlass : n < 0.65 ? m.cyGlass : n < 0.85 ? m.prism : m.amethyst;
                    }
                    k.set(u, y, v, s);
                }
            }
        }
        // Kristallwurzeln am Fuss
        if (k.touches(-9, -1, 9, 17)) {
            for (int i = 0; i < 6; i++) {
                double a = Math.toRadians(20 + i * 60);
                spike(k, GU, FLOOR + 1, GV, Math.cos(a) * 0.55, 0.83, Math.sin(a) * 0.55, 4, 0.9, m.prism, m.lbGlass, m.cyGlass);
            }
        }
    }

    private void ramp(Kit k, M m) {
        int ir = 10;
        if (!k.touches((int) GU - ir, (int) GV - ir, (int) GU + ir, (int) GV + ir)) return;
        for (int u = Math.max((int) GU - ir, k.u0()); u <= Math.min((int) GU + ir, k.u1()); u++) {
            for (int v = Math.max((int) GV - ir, k.v0()); v <= Math.min((int) GV + ir, k.v1()); v++) {
                double d = Math.hypot(u - GU, v - GV);
                if (d < RAMP_IN || d > 9.6) continue;
                double ang = Math.atan2(v - GV, u - GU) - Math.toRadians(RAMP_A0);
                ang = ((ang % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
                for (double a = ang; ; a += 2 * Math.PI) {
                    int step = (int) Math.floor(a * RAMP_PER_RAD);
                    int y = FLOOR + step;
                    if (y > 0) break;
                    if (d <= RAMP_OUT) {
                        boolean edge = d > RAMP_OUT - 1;
                        BlockState top = edge ? (step % 6 == 0 ? m.verdant : m.darkPrism) : d < 4.6 ? m.prismBr : k.pick(91, u, y, v, m.packed, m.prismBr, m.calcite, m.packed);
                        k.set(u, y, v, top);
                        if (y - 1 > FLOOR) k.set(u, y - 1, v, edge ? m.darkPrism : m.packed);
                        if (y - 2 > FLOOR && d > 5.5 && d < 7.5 && step % 3 == 0) k.set(u, y - 2, v, m.packed);
                        for (int h = 1; h <= 4; h++) k.set(u, y + h, v, m.air);
                    } else if (y < -6 && y >= FLOOR + 3) {
                        k.set(u, y, v, m.darkPrism);
                        k.set(u, y + 1, v, step % 8 == 0 ? m.darkPrism : m.prismWall);
                        if (step % 8 == 0) k.set(u, y + 2, v, m.soulStand);
                    }
                }
            }
        }
    }

    // ── Labor-Labyrinth (Schluessel 2) ───────────────────────────────────────

    private static final class Maze {
        final boolean[][] e = new boolean[LNU][LNV], s = new boolean[LNU][LNV];
        final int[][] room = new int[LNU][LNV], dist = new int[LNU][LNV];
        int[] keyChest;
        int keyI, keyJ;
        Direction keyFacing;
        int[][] mobCells;
    }

    static int cellU(int i) { return LU0 + 1 + 4 * i; }
    static int cellV(int j) { return LV0 + 1 + 4 * j; }

    static int[] roomOrigin(int q) {
        return new int[] {cellU(LAB_ROOMS[q][0]), cellV(LAB_ROOMS[q][1])};
    }

    private Maze maze(Kit k) {
        Maze z = new Maze();
        Random r = new Random(Double.doubleToLongBits(k.rnd(901, 3, 5, 7)));
        for (int q = 0; q < LAB_ROOMS.length; q++) for (int a = 0; a < 2; a++) for (int b = 0; b < 2; b++) z.room[LAB_ROOMS[q][0] + a][LAB_ROOMS[q][1] + b] = q + 1;
        boolean[][] vis = new boolean[LNU][LNV];
        ArrayDeque<int[]> st = new ArrayDeque<>();
        st.push(new int[] {0, L_ENT_J});
        vis[0][L_ENT_J] = true;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!st.isEmpty()) {
            int[] c = st.peek();
            int[][] cand = new int[4][];
            int n = 0;
            for (int[] d : dirs) {
                int ni = c[0] + d[0], nj = c[1] + d[1];
                if (ni < 0 || nj < 0 || ni >= LNU || nj >= LNV || vis[ni][nj]) continue;
                cand[n++] = new int[] {ni, nj};
            }
            if (n == 0) { st.pop(); continue; }
            int[] nx = cand[r.nextInt(n)];
            open(z, c[0], c[1], nx[0], nx[1]);
            vis[nx[0]][nx[1]] = true;
            st.push(nx);
        }
        for (int i = 0; i < LNU; i++) for (int j = 0; j < LNV; j++) {
            if (z.room[i][j] == 0) continue;
            if (i + 1 < LNU && z.room[i + 1][j] == z.room[i][j]) z.e[i][j] = true;
            if (j + 1 < LNV && z.room[i][j + 1] == z.room[i][j]) z.s[i][j] = true;
        }
        for (int q = 0; q < 7; q++) z.e[r.nextInt(LNU - 1)][r.nextInt(LNV)] = true;
        for (int q = 0; q < 5; q++) z.s[r.nextInt(LNU)][r.nextInt(LNV - 1)] = true;
        // Abstaende vom Eingang
        for (int[] row : z.dist) java.util.Arrays.fill(row, -1);
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[] {0, L_ENT_J});
        z.dist[0][L_ENT_J] = 0;
        int best = -1;
        while (!q.isEmpty()) {
            int[] c = q.poll();
            int i = c[0], j = c[1], dd = z.dist[i][j];
            if (z.room[i][j] == 0 && openCount(z, i, j) == 1 && dd > best) { best = dd; z.keyI = i; z.keyJ = j; }
            for (int[] d : dirs) {
                int ni = i + d[0], nj = j + d[1];
                if (ni < 0 || nj < 0 || ni >= LNU || nj >= LNV || z.dist[ni][nj] >= 0 || !isOpen(z, i, j, ni, nj)) continue;
                z.dist[ni][nj] = dd + 1;
                q.add(new int[] {ni, nj});
            }
        }
        // Schluesselkiste an der geschlossenen Rueckwand der Sackgasse
        int i = z.keyI, j = z.keyJ, cu = cellU(i) + 1, cv = cellV(j) + 1;
        int[][] side = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        Direction[] face = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
        z.keyChest = new int[] {cu, cv};
        z.keyFacing = Direction.NORTH;
        for (int a = 0; a < 4; a++) {
            int ni = i + side[a][0], nj = j + side[a][1];
            boolean closed = ni < 0 || nj < 0 || ni >= LNU || nj >= LNV || !isOpen(z, i, j, ni, nj);
            if (closed && !(i == 0 && j == L_ENT_J)) {
                z.keyChest = new int[] {cu + side[a][0], cv + side[a][1]};
                z.keyFacing = face[a];
                break;
            }
        }
        // Wachen: Zellen in 1/4, 1/2, 3/4 der Strecke, nicht in Raeumen
        int[][] mc = new int[3][];
        int nmc = 0;
        for (int f = 1; f <= 3 && best > 0; f++) {
            int want = best * f / 4;
            outer:
            for (int a = 0; a < LNU; a++) for (int b = 0; b < LNV; b++) {
                if (z.dist[a][b] == want && z.room[a][b] == 0 && !(a == z.keyI && b == z.keyJ)) { mc[nmc++] = new int[] {a, b}; break outer; }
            }
        }
        z.mobCells = java.util.Arrays.copyOf(mc, nmc);
        return z;
    }

    private static void open(Maze z, int i, int j, int ni, int nj) {
        if (ni == i + 1) z.e[i][j] = true;
        else if (ni == i - 1) z.e[ni][nj] = true;
        else if (nj == j + 1) z.s[i][j] = true;
        else z.s[ni][nj] = true;
    }

    private static boolean isOpen(Maze z, int i, int j, int ni, int nj) {
        if (ni == i + 1) return z.e[i][j];
        if (ni == i - 1) return z.e[ni][nj];
        if (nj == j + 1) return z.s[i][j];
        return z.s[ni][nj];
    }

    private static int openCount(Maze z, int i, int j) {
        int n = 0;
        if (i + 1 < LNU && z.e[i][j]) n++;
        if (i > 0 && z.e[i - 1][j]) n++;
        if (j + 1 < LNV && z.s[i][j]) n++;
        if (j > 0 && z.s[i][j - 1]) n++;
        if (i == 0 && j == L_ENT_J) n++;
        return n;
    }

    private BlockState labWall(Kit k, M m, int u, int y, int v) {
        if (y <= FLOOR) return m.concrete;
        if (y >= FLOOR + 6) return m.packed;
        if (y == FLOOR + 5) return k.rnd(101, u, y, v) < 0.7 ? m.concrete : m.concreteG;
        if (y == FLOOR + 1) return k.rnd(102, u, y, v) < 0.75 ? m.brick : m.brickCr;
        double n = k.noise(103, u + v, y * 7 + (u - v), 4);
        if (n < 0.34) return m.packed;
        if (n < 0.56) return m.concrete;
        if (n < 0.7) return m.brickCr;
        if (n < 0.82) return m.blue;
        if (n < 0.9) return m.snow;
        return m.brick;
    }

    private BlockState labFloor(Kit k, M m, int u, int v) {
        double n = k.noise(104, u, v, 5);
        if (n > 0.72) return m.packed;
        if (n < 0.18) return m.snow;
        return Math.floorMod(u + v, 2) == 0 ? m.concrete : m.lgray;
    }

    private void labPortal(Kit k, M m) {
        if (!k.touches(22, 6, 40, 14)) return;
        int y0 = FLOOR + 1;
        for (int u = Math.max(24, k.u0()); u <= Math.min(40, k.u1()); u++) {
            for (int v = Math.max(6, k.v0()); v <= Math.min(14, k.v1()); v++) {
                boolean rib = (u - 24) % 4 == 0;
                for (int y = FLOOR - 1; y <= FLOOR + 7; y++) {
                    BlockState s = rib ? (y == FLOOR + 4 && (v == 6 || v == 14) ? m.sea : m.steel) : (y == FLOOR + 7 ? m.packed : m.concrete);
                    if (u == 24 && v >= 7 && v <= 13) s = (v + y) % 2 == 0 ? m.hazard : m.concreteG;
                    k.set(u, y, v, s);
                }
            }
        }
        k.clear(22, y0, 9, 40, y0 + 3, 11);
        for (int u = 22; u <= 40; u++) for (int v = 9; v <= 11; v++) k.set(u, FLOOR, v, u % 4 == 0 ? m.steel : m.concrete);
        for (int u = 26; u <= 38; u += 4) k.set(u, y0 + 3, 10, m.soulHang);
        // Rahmen-Kristalle
        spike(k, 24, FLOOR + 7, 6, -0.3, 0.9, -0.3, 6, 1.0, m.prism, m.lbGlass, m.cyGlass);
        spike(k, 24, FLOOR + 7, 14, -0.3, 0.9, 0.3, 5, 1.0, m.amethyst, m.amethyst, m.lbGlass);
    }

    private void lab(Kit k, M m, Maze z) {
        if (!k.touches(LU0, LV0, LU0 + 4 * LNU, LV0 + 4 * LNV)) return;
        int uA = Math.max(LU0, k.u0()), uB = Math.min(LU0 + 4 * LNU, k.u1()), vA = Math.max(LV0, k.v0()), vB = Math.min(LV0 + 4 * LNV, k.v1());
        for (int u = uA; u <= uB; u++) for (int v = vA; v <= vB; v++) for (int y = FLOOR - 1; y <= FLOOR + 6; y++) k.set(u, y, v, labWall(k, m, u, y, v));
        int y0 = FLOOR + 1;
        for (int i = 0; i < LNU; i++) for (int j = 0; j < LNV; j++) {
            int cu = cellU(i), cv = cellV(j);
            if (!k.touches(cu - 1, cv - 1, cu + 4, cv + 4)) continue;
            k.clear(cu, y0, cv, cu + 2, y0 + 3, cv + 2);
            if (z.e[i][j]) k.clear(cu + 3, y0, cv, cu + 3, y0 + 3, cv + 2);
            if (z.s[i][j]) k.clear(cu, y0, cv + 3, cu + 2, y0 + 3, cv + 3);
            if (z.room[i][j] > 0 && i + 1 < LNU && j + 1 < LNV && z.room[i + 1][j] == z.room[i][j] && z.room[i][j + 1] == z.room[i][j]) k.clear(cu + 3, y0, cv + 3, cu + 3, y0 + 3, cv + 3);
            if (i == 0 && j == L_ENT_J) k.clear(cu - 1, y0, cv, cu - 1, y0 + 3, cv + 2);
            for (int u = cu - 1; u <= cu + 3; u++) for (int v = cv - 1; v <= cv + 3; v++) {
                if (u >= cu && u <= cu + 2 && v >= cv && v <= cv + 2 || u == cu + 3 && z.e[i][j] && v <= cv + 2 && v >= cv || v == cv + 3 && z.s[i][j] && u <= cu + 2 && u >= cu)
                    k.set(u, FLOOR, v, labFloor(k, m, u, v));
            }
            if (z.room[i][j] > 0) continue;
            // Gangdeko: Licht, Rohre, Eiszapfen, Frost
            if (k.rnd(105, i, 0, j) < 0.45) k.set(cu + 1, y0 + 3, cv + 1, m.soulHang);
            if (z.e[i][j]) for (int u = cu; u <= cu + 3; u++) k.set(u, y0 + 3, cv, m.pipe);
            for (int u = cu; u <= cu + 2; u++) for (int v = cv; v <= cv + 2; v++) {
                if (u == cu + 1 && v == cv + 1) continue;
                double rr = k.rnd(106, u, 0, v);
                if (rr < 0.12) k.set(u, y0 + 3, v, m.packed);
                else if (rr < 0.2) k.set(u, y0, v, m.snowLayer);
            }
            // eingefrorene Geraete in Sackgassen (nicht im Schluesselraum / Eingang)
            if (openCount(z, i, j) == 1 && !(i == z.keyI && j == z.keyJ) && !(i == 0 && j == L_ENT_J)) {
                int[][] side = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
                for (int[] sd : side) {
                    int ni = i + sd[0], nj = j + sd[1];
                    boolean closed = ni < 0 || nj < 0 || ni >= LNU || nj >= LNV || !isOpen(z, i, j, ni, nj);
                    if (!closed) continue;
                    int mu = cu + 1 + sd[0], mv = cv + 1 + sd[1];
                    k.set(mu, y0, mv, m.steel);
                    k.set(mu, y0 + 1, mv, k.rnd(107, i, 0, j) < 0.5 ? m.crt : m.crtOn);
                    k.set(mu - sd[1], y0, mv + sd[0], m.packed);
                    k.set(mu + sd[1], y0, mv - sd[0], m.blue);
                    k.set(mu, y0 + 2, mv, m.packed);
                    break;
                }
            }
        }
        // Laborraeume
        for (int q = 0; q < LAB_ROOMS.length; q++) {
            int[] o = roomOrigin(q);
            int U = o[0], V = o[1];
            if (!k.touches(U - 1, V - 1, U + 7, V + 7)) continue;
            for (int u = U + 1; u <= U + 5; u++) {
                k.set(u, y0, V, m.benchTop);
                if (u == U + 2) k.set(u, y0 + 1, V, m.brewing);
                if (u == U + 4) k.set(u, y0 + 1, V, m.crtOn);
            }
            k.set(U, y0, V, m.cauldron);
            // Probentank (eingefroren)
            for (int u = U + 2; u <= U + 4; u++) for (int v = V + 3; v <= V + 5; v++) {
                boolean c = u == U + 3 && v == V + 4;
                k.set(u, y0, v, c ? m.blue : m.steel);
                k.set(u, y0 + 1, v, c ? m.blue : m.lbGlass);
                k.set(u, y0 + 2, v, c ? m.packed : m.lbGlass);
                k.set(u, y0 + 3, v, c ? m.sea : m.steel);
            }
            k.set(U + 6, y0 + 3, V, m.soulHang);
            k.set(U, y0 + 3, V + 6, m.soulHang);
            for (int u = U; u <= U + 6; u++) k.set(u, y0 + 3, V + 2, m.pipe);
            k.set(U, y0, V + 3, m.packed);
            k.set(U, y0 + 1, V + 3, m.packed);
            k.set(U + 6, y0, V + 3, m.alu);
            k.set(U + 6, y0 + 1, V + 3, m.crt);
        }
        // Schluesselzelle: Siegellicht
        int kcU = cellU(z.keyI) + 1, kcV = cellV(z.keyJ) + 1;
        k.set(kcU, y0 + 3, kcV, m.sea);
    }

    // ── Kryo-Lager (Schluessel 3) ────────────────────────────────────────────

    private static boolean inVault(int u, int y, int v, double hw, double ry, int v0, int v1, int yFloor) {
        if (v < v0 || v > v1 || y < yFloor) return false;
        double du = (u - CU) / hw;
        if (y <= C_SPRING) return Math.abs(du) <= 1;
        return du * du + sq((y - C_SPRING) / ry) <= 1;
    }

    private void cryoCorridor(Kit k, M m) {
        if (!k.touches(-46, -9, -20, -3)) return;
        int y0 = FLOOR + 1;
        for (int u = Math.max(-44, k.u0()); u <= Math.min(-22, k.u1()); u++) {
            for (int v = Math.max(-9, k.v0()); v <= Math.min(-3, k.v1()); v++) {
                boolean rib = Math.floorMod(u, 4) == 0;
                for (int y = FLOOR - 1; y <= FLOOR + 6; y++) {
                    BlockState s = rib ? (y == FLOOR + 3 && (v == -9 || v == -3) ? m.sea : m.titan) : (y == FLOOR + 6 ? m.packed : k.rnd(111, u, y, v) < 0.7 ? m.concrete : m.packed);
                    if (u == -22 && v >= -8 && v <= -4) s = (v + y) % 2 == 0 ? m.hazard : m.concreteG;
                    k.set(u, y, v, s);
                }
            }
        }
        k.clear(-44, y0, -7, -20, y0 + 3, -5);
        for (int u = -44; u <= -20; u++) for (int v = -7; v <= -5; v++) k.set(u, FLOOR, v, v == -6 ? m.lblue : m.concrete);
        for (int u = -40; u <= -24; u += 4) k.set(u, y0 + 3, -6, m.soulHang);
        spike(k, -22, FLOOR + 6, -9, 0.3, 0.9, -0.3, 5, 1.0, m.blue, m.packed, m.lbGlass);
        spike(k, -22, FLOOR + 6, -3, 0.3, 0.9, 0.3, 6, 1.0, m.prism, m.lbGlass, m.cyGlass);
    }

    private void tank(Kit k, M m, int cu, int cv, boolean broken) {
        if (!k.touches(cu - 4, cv - 4, cu + 4, cv + 4)) return;
        int y0 = FLOOR + 1;
        for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) {
            double d = Math.hypot(u - cu, v - cv);
            if (d > 3.2) continue;
            k.set(u, y0, v, d > 2.6 ? m.darkPrism : m.steel);
            for (int y = y0 + 1; y <= y0 + 8; y++) {
                if (d > 2.7) continue;
                BlockState s;
                if (d <= 1.5) s = (y == y0 + 4 ? m.sea : y % 2 == 0 ? m.blue : m.packed);
                else s = y == y0 + 1 || y == y0 + 8 ? m.steel : m.lbGlass;
                if (broken && d > 1.5 && y > y0 + 2 && y < y0 + 8 && k.rnd(121, u, y, v) < 0.45) s = m.air;
                k.set(u, y, v, s);
            }
            if (d <= 3.0) k.set(u, y0 + 9, v, m.steel);
            if (d <= 2.0) k.set(u, y0 + 10, v, m.titan);
        }
        for (int y = y0 + 11; y <= y0 + 12; y++) k.set(cu, y, cv, m.pipe);
        if (broken) {   // ausgelaufenes Eis
            for (int u = cu - 4; u <= cu + 4; u++) for (int v = cv - 4; v <= cv + 4; v++) {
                double d = Math.hypot(u - cu, v - cv);
                if (d > 2.7 && d < 4.3 && k.rnd(122, u, 0, v) < 0.5) k.set(u, y0, v, m.snowLayer);
            }
        }
    }

    private void cryo(Kit k, M m) {
        int uLo = CU - CHW - 3, uHi = CU + CHW + 3, vLo = CV0 - 3, vHi = CV1 + 3;
        if (!k.touches(uLo, vLo, uHi, vHi)) return;
        int y0 = FLOOR + 1;
        for (int u = Math.max(uLo, k.u0()); u <= Math.min(uHi, k.u1()); u++) {
            for (int v = Math.max(vLo, k.v0()); v <= Math.min(vHi, k.v1()); v++) {
                for (int y = FLOOR - 1; y <= C_SPRING + C_RY + 3; y++) {
                    boolean in = inVault(u, y, v, CHW, C_RY, CV0, CV1, y0);
                    if (in) {
                        boolean inner = inVault(u, y, v, CHW - 1, C_RY - 1, CV0, CV1, y0);
                        boolean rib = Math.floorMod(v - CV0, 5) == 0 && v > CV0 && v < CV1;
                        if (!inner && rib) k.set(u, y, v, (u == CU || y == C_SPRING) && Math.floorMod(v - CV0, 10) == 0 ? m.sea : m.steel);
                        else k.set(u, y, v, m.air);
                        continue;
                    }
                    boolean shell = inVault(u, y, v, CHW + 2, C_RY + 2, CV0 - 2, CV1 + 2, FLOOR - 1);
                    if (!shell) continue;
                    if (y == FLOOR && Math.abs(u - CU) <= CHW && v >= CV0 && v <= CV1) {
                        k.set(u, y, v, Math.abs(u - CU) <= 2 ? (Math.floorMod(v, 3) == 0 ? m.cyan : m.lblue) : Math.floorMod(u + v, 2) == 0 ? m.concrete : m.lgray);
                        continue;
                    }
                    double n = k.noise(123, u + v * 2, y * 5 + v, 4);
                    k.set(u, y, v, n < 0.4 ? m.packed : n < 0.65 ? m.concrete : n < 0.82 ? m.blue : m.snow);
                }
            }
        }
        // Tanks
        for (int a = 0; a < TANK_V.length; a++) {
            tank(k, m, CU - 9, TANK_V[a], a == 2);
            tank(k, m, CU + 9, TANK_V[a], a == 0);
        }
        // Rohrsammler unter der Decke
        for (int v = CV0; v <= CV1; v++) {
            k.set(CU - 9, FLOOR + 13, v, m.pipe);
            k.set(CU + 9, FLOOR + 13, v, m.pipe);
        }
        // Leitstand an der Nordwand
        for (int u = CU - 5; u <= CU + 5; u++) {
            k.set(u, y0, CV0, m.steel);
            k.set(u, y0 + 1, CV0, u % 2 == 0 ? m.crtOn : m.crt);
        }
        for (int u = CU - 3; u <= CU + 3; u++) for (int y = y0 + 3; y <= y0 + 6; y++) k.set(u, y, CV0 - 1, m.lbGlass);
        // Haengelampen im Mittelgang
        for (int v = CV0 + 4; v <= CV1 - 2; v += 7) {
            for (int y = y0 + 7; y <= y0 + 13; y++) k.set(CU, y, v, m.chain);
            k.set(CU, y0 + 6, v, m.soulHang);
        }
        // Frostbrocken
        for (int[] s : new int[][] {{CU - 3, -24}, {CU + 4, 10}, {CU - 13, 10}, {CU + 13, -24}}) {
            k.set(s[0], y0, s[1], m.packed);
            k.set(s[0] + 1, y0, s[1], m.snowLayer);
        }
    }

    // ── Torhaus und Siegeltor ────────────────────────────────────────────────

    private void gatehouse(Kit k, M m) {
        if (!k.touches(-9, GATE_V - 3, 9, -13)) return;
        int y0 = FLOOR + 1;
        for (int u = Math.max(-7, k.u0()); u <= Math.min(7, k.u1()); u++) {
            for (int v = Math.max(GATE_V - 1, k.v0()); v <= Math.min(-15, k.v1()); v++) {
                for (int y = FLOOR; y <= FLOOR + 10; y++) {
                    boolean col = Math.abs(u) == 7 && Math.floorMod(v, 3) == 0;
                    BlockState s = col ? (y == FLOOR + 8 ? m.sea : m.quartzPillar) : y == FLOOR + 10 ? m.darkPrism : y == FLOOR + 9 ? m.prismBr : k.pick(131, u, y, v, m.prismBr, m.prismBr, m.prism, m.darkPrism);
                    k.set(u, y, v, s);
                }
            }
        }
        // Halle
        k.clear(-5, y0, GATE_V + 1, 5, y0 + 6, -15);
        for (int u = -5; u <= 5; u++) for (int v = GATE_V + 1; v <= -15; v++) k.set(u, FLOOR, v, Math.abs(u) <= 1 ? m.darkPrism : (u + v) % 2 == 0 ? m.prismBr : m.calcite);
        // Portal vorn: Stufenbogen
        for (int u = -7; u <= 7; u++) for (int y = y0; y <= y0 + 8; y++) {
            if (Math.abs(u) <= 4 && y <= y0 + 5 + (Math.abs(u) <= 2 ? 1 : 0)) continue;
            if (Math.abs(u) == 5 || y == y0 + 7) k.set(u, y, -15, m.darkPrism);
        }
        for (int u = -4; u <= 4; u++) k.set(u, y0 + 7, -15, Math.abs(u) == 0 ? m.sea : m.darkPrism);
        // Saeulen und Lichter in der Halle
        for (int v = GATE_V + 3; v <= -17; v += 4) {
            for (int s = -1; s <= 1; s += 2) {
                for (int y = y0; y <= y0 + 5; y++) k.set(5 * s, y, v, y == y0 + 4 ? m.sea : m.quartzPillar);
            }
        }
        // Kristallkrone auf dem Dach
        spike(k, -6, FLOOR + 10, -16, -0.2, 1, 0.15, 7, 1.3, m.prism, m.lbGlass, m.cyGlass);
        spike(k, 6, FLOOR + 10, -16, 0.2, 1, 0.15, 7, 1.3, m.amethyst, m.amethyst, m.lbGlass);
        spike(k, 0, FLOOR + 10, -20, 0, 1, -0.1, 9, 1.6, m.blue, m.prism, m.lbGlass);
        k.sealGate(0, FLOOR, GATE_V, Direction.SOUTH, 2, 5, m.darkPrism, m.glow);
    }

    private void descent(Kit k, M m) {
        if (!k.touches(-4, -48, 4, GATE_V - 1)) return;
        for (int v = GATE_V - 2; v >= -47; v--) {
            int fy = Math.max(AF, FLOOR - 1 - (GATE_V - 2 - v));
            if (v >= -42) {
                for (int u = -3; u <= 3; u++) for (int y = fy - 1; y <= fy + 6; y++) {
                    boolean light = Math.abs(u) == 3 && y == fy + 3 && Math.floorMod(v, 3) == 0;
                    k.set(u, y, v, light ? m.sea : y == fy + 6 ? m.darkPrism : m.prismBr);
                }
            }
            k.clear(-2, fy + 1, v, 2, fy + 5, v);
            for (int u = -2; u <= 2; u++) k.set(u, fy, v, fy > AF ? m.stairs(Blocks.PRISMARINE_BRICK_STAIRS, Direction.SOUTH) : m.darkPrism);
        }
    }

    // ── Arena: zugefrorener See ──────────────────────────────────────────────

    private static double arenaCeil(int u, int v) {
        double h = 1 - sq(u / ARU) - sq((v - AV) / ARV);
        return h <= 0 ? Double.NaN : AF + ARY * Math.sqrt(h);
    }

    private boolean lakeWindow(Kit k, int u, int v) {
        return k.noise(52, u, v, 6) > 0.74 && Math.hypot(u, v - AV) > 5;
    }

    private void arena(Kit k, M m) {
        blob(k, 0, AF, AV, ARU, ARY, ARV, AF - 3, AF + (int) ARY + 5, 4.0, (u, y, v, t) -> {
            if (t < 0) {
                if (y > AF) return m.air;
                boolean win = lakeWindow(k, u, v);
                if (y == AF) {
                    if (win) return m.lbGlass;
                    double ring = Math.abs(Math.hypot(u, (v - AV) * ARU / ARV) - 12);
                    if (ring < 0.5) return Math.floorMod(u + v, 3) == 0 ? m.glow : m.prismBr;
                    if (Math.abs(k.noise(51, u, v, 5) - 0.5) < 0.035) return m.packed;
                    if (t > -2.5) return m.snow;
                    return m.blue;
                }
                if (y == AF - 1) return win ? m.water : m.packed;
                return win && y == AF - 2 ? m.glow : m.blue;
            }
            if (t < 2) {
                double n = k.noise(53, u + y * 3, v - y * 2, 5);
                return n < 0.4 ? m.packed : n < 0.65 ? m.blue : n < 0.8 ? m.snow : n < 0.9 ? m.lbGlass : m.calcite;
            }
            return k.rnd(54, u, y, v) < 0.5 ? m.stone : m.smooth;
        });
        // Kristallsaeulen am Rand (Sueden frei fuer den Zugang)
        Ceil c = MinmusDungeon::arenaCeil;
        int[] angs = {0, 40, 140, 180, 220, 270, 320};
        for (int a = 0; a < angs.length; a++) {
            double r = Math.toRadians(angs[a]);
            int pu = (int) Math.round(ARU * 0.8 * Math.cos(r)), pv = AV + (int) Math.round(ARV * 0.8 * Math.sin(r));
            pillar(k, m, pu, pv, 2.0, AF, c, a);
        }
        // Riesige Eiszapfen
        if (k.touches(-24, AV - 18, 24, AV + 18)) {
            for (int i = 0; i < 34; i++) {
                double fu = (k.rnd(141, i, 0, 0) * 2 - 1) * 0.85, fv = (k.rnd(142, i, 0, 0) * 2 - 1) * 0.85;
                if (fu * fu + fv * fv > 0.75) continue;
                int u = (int) Math.round(fu * ARU), v = AV + (int) Math.round(fv * ARV);
                double top = arenaCeil(u, v);
                if (Double.isNaN(top)) continue;
                double near = Math.hypot(u, v - AV);
                double maxLen = top - (near < 7 ? AF + 12 : AF + 6);
                double len = Math.min(maxLen, 4 + k.rnd(143, i, 0, 0) * 11);
                if (len < 3) continue;
                double r0 = 1.1 + k.rnd(144, i, 0, 0) * 1.3;
                spike(k, u, top + 1.5, v, 0, -1, 0, len + 1.5, r0, m.packed, i % 3 == 0 ? m.blue : m.packed, m.lbGlass);
            }
        }
        // Bossaltar hinter den Kisten
        if (k.touches(-10, AV - 16, 10, AV - 8)) {
            for (int u = -9; u <= 9; u++) for (int v = AV - 14; v <= AV - 11; v++) {
                double e = sq(u / ARU) + sq((v - AV) / ARV);
                if (e > 0.85) continue;
                k.set(u, AF, v, Math.abs(u) % 3 == 0 ? m.darkPrism : m.prismBr);
            }
            spike(k, 0, AF + 1, AV - 13, 0, 1, -0.1, 9, 1.8, m.blue, m.prism, m.lbGlass);
            spike(k, -3, AF + 1, AV - 13, -0.3, 1, 0, 6, 1.2, m.amethyst, m.amethyst, m.lbGlass);
            spike(k, 3, AF + 1, AV - 13, 0.3, 1, 0, 6, 1.2, m.prism, m.lbGlass, m.cyGlass);
            k.set(-7, AF + 1, AV - 12, m.soulStand);
            k.set(7, AF + 1, AV - 12, m.soulStand);
        }
        // umgestuerzte Kristalle auf dem Eis
        spike(k, -16, AF + 1, AV + 6, 0.8, 0.15, 0.2, 7, 1.2, m.prism, m.lbGlass, m.cyGlass);
        spike(k, 15, AF + 1, AV + 7, -0.8, 0.2, -0.1, 6, 1.1, m.amethyst, m.amethyst, m.lbGlass);
    }

    // ── Oberflaeche: Observatorium ───────────────────────────────────────────

    private void plaza(Kit k, M m) {
        if (!k.touches(-28, -20, 28, 36)) return;
        for (int u = Math.max(-28, k.u0()); u <= Math.min(28, k.u1()); u++) {
            for (int v = Math.max(-20, k.v0()); v <= Math.min(36, k.v1()); v++) {
                double d = Math.hypot(u - GU, v - GV);
                if (d > 27.5) continue;
                BlockState s;
                if (d <= 17) {
                    int ring = (int) d;
                    double ang = Math.toDegrees(Math.atan2(v - GV, u - GU)) + 360;
                    s = ring % 4 == 1 ? m.prismBr : ((int) (ang / 15)) % 2 == 0 ? m.white : m.quartz;
                    if (ring == 12 && ((int) (ang / 7.5)) % 4 == 0) s = m.glow;
                } else if (d <= 19.5) s = m.lgray;
                else {
                    double n = k.noise(151, u, v, 4);
                    s = d > 26.5 ? m.smooth : n < 0.3 ? m.snow : n < 0.65 ? m.smooth : m.concrete;
                }
                k.set(u, 0, v, s);
                if (d <= 19.5) k.set(u, -1, v, m.concrete);
            }
        }
    }

    private void dome(Kit k, M m) {
        if (!k.touches(-20, -12, 20, 28)) return;
        // Trommel
        for (int u = Math.max(-19, k.u0()); u <= Math.min(19, k.u1()); u++) {
            for (int v = Math.max(-11, k.v0()); v <= Math.min(27, k.v1()); v++) {
                double d = Math.hypot(u - GU, v - GV);
                if (d <= 16.7 || d > 18.3) continue;
                double ang = Math.toDegrees(Math.atan2(v - GV, u - GU)) + 360;
                boolean rib = ((int) Math.round(ang / 15)) * 15 - ang < 2.5 && ((int) Math.round(ang / 15)) * 15 - ang > -2.5;
                for (int y = 1; y <= 7; y++) {
                    BlockState s = y == 1 ? m.steel : rib ? m.steel : (y == 3 || y == 4) && ((int) (ang / 15)) % 2 == 0 ? m.lbGlass : y == 7 ? m.lgray : m.white;
                    k.set(u, y, v, s);
                }
            }
        }
        // Kuppel mit Spalt (Nordseite) und zurueckgeschobenem Verschluss (Suedseite)
        blob(k, GU, 7, GV, 18, 15, 18, 8, 23, 0.01, (u, y, v, t) -> {
            if (t < -1.3) return null;
            boolean slot = Math.abs(u) <= 3 && v < 12;
            if (slot) return m.air;
            if (Math.abs(u) == 4 && v < 12) return m.steel;
            double ang = Math.toDegrees(Math.atan2(v - GV, u - GU)) + 360;
            double rr = ang / 30 - Math.round(ang / 30);
            if (Math.abs(rr) < 0.035) return m.steel;
            if (y <= 9) return m.lgray;
            if (y >= 19 && k.noise(161, u, v, 3) < 0.45) return m.snow;
            return (y % 4 == 0) ? m.alu : m.white;
        });
        blob(k, GU, 7, GV, 19, 16, 19, 12, 24, 0.01, (u, y, v, t) -> {
            if (t < -1.1) return null;
            if (Math.abs(u) <= 4 && v >= 12 && v <= 21) return Math.abs(u) == 4 ? m.steel : m.alu;
            return null;
        });
        // Eiszapfen an der Traufe
        for (int i = 0; i < 40; i++) {
            double a = Math.toRadians(i * 9 + 4);
            int u = (int) Math.round(GU + 18.8 * Math.cos(a)), v = (int) Math.round(GV + 18.8 * Math.sin(a));
            if (v > 22 && Math.abs(u) < 6) continue;
            int len = 1 + (int) (k.rnd(162, i, 0, 0) * 3);
            k.set(u, 7, v, m.lgray);
            for (int y = 6; y > 6 - len; y--) k.set(u, y, v, y == 7 - len ? m.lbGlass : m.packed);
        }
    }

    private void domeInterior(Kit k, M m) {
        if (!k.touches(-18, -10, 18, 26)) return;
        // Gelaender um das Schachtloch (offen am Rampenausgang im Sueden)
        for (int u = Math.max(-10, k.u0()); u <= Math.min(10, k.u1()); u++) {
            for (int v = Math.max(-2, k.v0()); v <= Math.min(18, k.v1()); v++) {
                double d = Math.hypot(u - GU, v - GV);
                if (d <= 8.51 || d > 9.6) continue;
                double ang = Math.toDegrees(Math.atan2(v - GV, u - GU));
                if (ang > 48 && ang < 96) continue;
                k.set(u, 1, v, Math.floorMod(u + v, 5) == 0 ? m.quartzPillar : m.prismWall);
                if (Math.floorMod(u + v, 5) == 0) k.set(u, 2, v, m.soulStand);
            }
        }
        // Konsolen an der Wand
        for (int deg : new int[] {20, 160, 200, 340, 125, 55}) {
            double a = Math.toRadians(deg);
            int u = (int) Math.round(GU + 15.6 * Math.cos(a)), v = (int) Math.round(GV + 15.6 * Math.sin(a));
            k.set(u, 1, v, m.steel);
            k.set(u, 2, v, deg % 40 == 0 ? m.crtOn : m.crt);
            int u2 = (int) Math.round(GU + 14.4 * Math.cos(a)), v2 = (int) Math.round(GV + 14.4 * Math.sin(a));
            k.set(u2, 1, v2, m.benchTop);
        }
        // Haengelampen an der Kuppel
        for (int deg = 0; deg < 360; deg += 60) {
            double a = Math.toRadians(deg + 30);
            int u = (int) Math.round(GU + 12 * Math.cos(a)), v = (int) Math.round(GV + 12 * Math.sin(a));
            if (Math.abs(u) <= 4 && v < 12) continue;
            double top = 7 + 15 * Math.sqrt(Math.max(0, 1 - sq(12 / 18.0)));
            for (int y = 11; y < (int) top - 1; y++) k.set(u, y, v, m.chain);
            k.set(u, 10, v, m.soulHang);
        }
        k.set(-15, 0, 8, m.steel);
    }

    private void telescope(Kit k, M m) {
        if (!k.touches(-6, -24, 6, 6)) return;
        // Pfeiler
        k.cylinder(0, -5, 1, 6, 2.2, m.concrete);
        k.cylinder(0, -5, 7, 7, 2.2, m.steel);
        for (int s = -1; s <= 1; s += 2) {
            k.line(3.6 * s, 7, -5, 3.6 * s, 13.5, -3.2, 0.6, m.steel);
        }
        k.line(-3.6, 13.5, -3.2, 3.6, 13.5, -3.2, 0.5, m.titan);
        // Rohr
        double au = 0, ay = 8, av = 2, bu = 0, by = 32, bv = -20;
        double du = bu - au, dy = by - ay, dv = bv - av, len2 = du * du + dy * dy + dv * dv;
        int uLo = Math.max(-4, k.u0()), uHi = Math.min(4, k.u1()), vLo = Math.max(-24, k.v0()), vHi = Math.min(6, k.v1());
        for (int u = uLo; u <= uHi; u++) for (int v = vLo; v <= vHi; v++) for (int y = 4; y <= 36; y++) {
            double t = ((u - au) * du + (y - ay) * dy + (v - av) * dv) / len2;
            if (t < -0.03 || t > 1.0) continue;
            double pu = au + du * t - u, py = ay + dy * t - y, pv = av + dv * t - v;
            double dist = Math.sqrt(pu * pu + py * py + pv * pv);
            double r = t < 0.06 ? 2.0 : t > 0.9 ? 3.1 : 2.6;
            if (dist > r) continue;
            BlockState s;
            if (t > 0.94 && dist < 2.1) s = m.air;
            else if (t > 0.92 && dist < 2.1) s = dist < 0.8 ? m.sea : m.lbGlass;
            else if (t < 0.06) s = m.steel;
            else if (t > 0.9) s = m.steel;
            else s = ((int) (t * 24)) % 5 == 0 ? m.titan : (dist > 2.0 ? m.white : m.alu);
            k.set(u, y, v, s);
        }
    }

    private void vestibule(Kit k, M m) {
        if (!k.touches(-6, 24, 6, 34)) return;
        for (int u = -5; u <= 5; u++) for (int v = 25; v <= 33; v++) for (int y = 0; y <= 6; y++) {
            boolean edge = Math.abs(u) == 5 || v == 33 || y == 0 || y == 6;
            if (!edge && v >= 27) { k.set(u, y, v, m.air); continue; }
            if (v <= 26 && !edge) continue;
            BlockState s = y == 6 ? m.lgray : y == 0 ? m.concrete : (Math.abs(u) == 5 && (v == 27 || v == 33)) ? m.steel : (y == 3 && v % 2 == 0) ? m.lbGlass : m.white;
            k.set(u, y, v, s);
        }
        k.clear(-1, 1, 24, 1, 3, 33);
        for (int v = 24; v <= 33; v++) for (int u = -1; u <= 1; u++) k.set(u, 0, v, u == 0 ? m.prismBr : m.white);
        for (int u = -5; u <= 5; u++) for (int v = 25; v <= 33; v++) k.set(u, 7, v, m.snowLayer);
        for (int u = -5; u <= 5; u++) if (k.rnd(171, u, 0, 0) < 0.6) {
            int len = 1 + (int) (k.rnd(172, u, 0, 0) * 2);
            k.set(u, 6, 34, m.lgray);
            for (int y = 5; y > 5 - len; y--) k.set(u, y, 34, m.packed);
        }
        k.set(0, 4, 34, m.sea);
        k.set(0, 5, 26, m.soulHang);
    }

    private void path(Kit k, M m) {
        if (!k.touches(-7, 33, 7, 80)) return;
        for (int v = 34; v <= 78; v++) for (int u = -3; u <= 3; u++) {
            BlockState s = Math.abs(u) == 3 ? m.prismBr : u == 0 && v % 4 == 0 ? m.lblue : (u + v) % 2 == 0 ? m.smooth : m.concrete;
            k.set(u, 0, v, s);
        }
        for (int v = 38; v <= 70; v += 8) for (int s = -1; s <= 1; s += 2) {
            for (int y = 1; y <= 3; y++) k.set(5 * s, y, v, m.prismWall);
            k.set(5 * s, 0, v, m.darkPrism);
            k.set(5 * s, 4, v, m.sea);
        }
        // Eingangstor
        for (int s = -1; s <= 1; s += 2) for (int y = 1; y <= 8; y++) {
            k.set(5 * s, y, 72, y == 8 ? m.darkPrism : m.quartzPillar);
            k.set(6 * s, y, 72, y > 6 ? m.packed : m.prismBr);
        }
        for (int u = -6; u <= 6; u++) {
            k.set(u, 9, 72, Math.abs(u) <= 1 ? m.sea : m.darkPrism);
            if (Math.abs(u) <= 4 && k.rnd(173, u, 0, 0) < 0.6) k.set(u, 8, 72, m.packed);
        }
        spike(k, -6, 9, 72, -0.3, 1, 0.1, 6, 1.0, m.prism, m.lbGlass, m.cyGlass);
        spike(k, 6, 9, 72, 0.3, 1, 0.1, 6, 1.0, m.amethyst, m.amethyst, m.lbGlass);
    }

    /** Laborhuette mit Vordach, Fenstern, Eiszapfen. */
    private void hut(Kit k, M m, int u0, int v0, int u1, int v1, int h, Direction door) {
        if (!k.touches(u0 - 2, v0 - 2, u1 + 2, v1 + 2)) return;
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean wall = u == u0 || u == u1 || v == v0 || v == v1;
            boolean corner = (u == u0 || u == u1) && (v == v0 || v == v1);
            k.set(u, 0, v, m.concrete);
            for (int y = 1; y < h; y++) {
                if (!wall) { k.set(u, y, v, m.air); continue; }
                k.set(u, y, v, corner ? m.steel : (y == 2 || y == 3) && (u + v) % 3 != 0 ? m.lbGlass : y == 1 ? m.lgray : m.white);
            }
        }
        for (int u = u0 - 1; u <= u1 + 1; u++) for (int v = v0 - 1; v <= v1 + 1; v++) {
            boolean rim = u == u0 - 1 || u == u1 + 1 || v == v0 - 1 || v == v1 + 1;
            k.set(u, h, v, rim ? m.steel : m.lgray);
            k.set(u, h + 1, v, m.snowLayer);
            if (rim && k.rnd(181, u, 0, v) < 0.35) {
                int len = 1 + (int) (k.rnd(182, u, 0, v) * 3);
                for (int y = h - 1; y >= h - len; y--) k.set(u, y, v, y == h - len ? m.lbGlass : m.packed);
            }
        }
        int mu = (u0 + u1) / 2, mv = (v0 + v1) / 2;
        // Tuer
        if (door == Direction.WEST) {
            k.clear(u0, 1, mv - 1, u0, 3, mv + 1);
            for (int v = mv - 1; v <= mv + 1; v++) for (int y = 1; y < h; y++) k.set(u0 - 1, y, v, y == h - 1 ? m.steel : m.air);
        }
        // Inneres
        for (int v = v0 + 1; v <= v1 - 1; v += 2) {
            k.set(u1 - 1, 1, v, m.steel);
            k.set(u1 - 1, 2, v, v % 4 == 0 ? m.crtOn : m.crt);
        }
        k.set(u0 + 2, 1, v0 + 1, m.benchTop);
        k.set(u0 + 3, 1, v0 + 1, m.benchTop);
        k.set(u0 + 3, 2, v0 + 1, m.brewing);
        k.set(mu, h - 1, mv, m.soulHang);
        // Dachantenne
        for (int y = h + 1; y <= h + 6; y++) k.set(u1 - 1, y, v0 + 1, m.bars);
        k.set(u1 - 1, h + 7, v0 + 1, m.rod);
        k.set(u0 + 2, h + 1, v1 - 2, m.alu);
        k.set(u0 + 2, h + 2, v1 - 2, m.endRod);
    }

    /** Generatorschuppen mit liegenden Treibstofftanks. */
    private void shed(Kit k, M m) {
        if (!k.touches(-44, 8, -28, 26)) return;
        for (int u = -42; u <= -30; u++) for (int v = 10; v <= 24; v++) k.set(u, 0, v, (u + v) % 2 == 0 ? m.concrete : m.lgray);
        for (int s = 0; s < 2; s++) {
            int v = 13 + s * 6;
            k.line(-41, 2.5, v, -33, 2.5, v, 1.6, s == 0 ? m.white : m.lblue);
            k.line(-41.5, 2.5, v, -41.5, 2.5, v, 1.2, m.steel);
            k.line(-32.5, 2.5, v, -32.5, 2.5, v, 1.2, m.steel);
            for (int u = -40; u <= -34; u += 6) { k.set(u, 1, v - 1, m.steel); k.set(u, 1, v + 1, m.steel); }
            k.set(-37, 4, v, m.pipe);
            k.set(-37, 5, v, m.pipe);
        }
        for (int v = 13; v <= 19; v++) k.set(-37, 6, v, m.pipe);
        // Dachgestell
        for (int[] c : new int[][] {{-42, 10}, {-30, 10}, {-42, 24}, {-30, 24}}) for (int y = 1; y <= 6; y++) k.set(c[0], y, c[1], m.steel);
        for (int u = -42; u <= -30; u++) for (int v = 10; v <= 24; v++) {
            if (u == -42 || u == -30 || v == 10 || v == 24) k.set(u, 7, v, m.steel);
            else k.set(u, 7, v, m.lgray);
            k.set(u, 8, v, m.snowLayer);
        }
        for (int u = -42; u <= -30; u++) if (k.rnd(191, u, 0, 0) < 0.5) { k.set(u, 6, 25, m.lgray); k.set(u, 5, 25, m.packed); }
        k.set(-36, 6, 17, m.soulHang);
    }

    private void mast(Kit k, M m, int cu, int cv, int h) {
        if (!k.touches(cu - 11, cv - 11, cu + 11, cv + 11)) return;
        // Eishuegel am Fuss
        for (int u = cu - 4; u <= cu + 4; u++) for (int v = cv - 4; v <= cv + 4; v++) {
            double d = Math.hypot(u - cu, v - cv);
            if (d > 4.3) continue;
            int hh = (int) Math.round(2.5 - d * 0.55 + k.rnd(201, u, 0, v));
            for (int y = 0; y <= hh; y++) k.set(u, y, v, y == hh ? m.snow : m.packed);
        }
        for (int y = 1; y <= h; y++) {
            for (int du = -1; du <= 1; du += 2) for (int dv = -1; dv <= 1; dv += 2) {
                k.set(cu + du, y, cv + dv, k.rnd(202, cu + du, y, cv + dv) < 0.1 ? m.packed : m.bars);
            }
            if (y % 5 == 0) {
                for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) if (du == 0 || dv == 0) k.set(cu + du, y, cv + dv, du == 0 && dv == 0 ? m.steel : m.bars);
                for (int du = -1; du <= 1; du += 2) for (int dv = -1; dv <= 1; dv += 2) {
                    if (k.rnd(203, du, y, dv + cu) < 0.5) {
                        k.set(cu + du * 2, y, cv + dv, m.bars);
                        k.set(cu + du * 2, y - 1, cv + dv, m.packed);
                        if (k.rnd(204, du, y, dv + cv) < 0.5) k.set(cu + du * 2, y - 2, cv + dv, m.lbGlass);
                    }
                }
            }
        }
        // Querarm mit kleinen Schuesseln
        int ya = h - 6;
        for (int d = -4; d <= 4; d++) k.set(cu + d, ya, cv, m.steel);
        for (int s = -1; s <= 1; s += 2) {
            for (int dv = -1; dv <= 1; dv++) for (int dy = -1; dy <= 1; dy++) k.set(cu + 5 * s, ya + dy, cv + dv, (dv == 0 && dy == 0) ? m.steel : m.white);
            k.set(cu + 4 * s, ya - 1, cv, m.packed);
            k.set(cu + 4 * s, ya - 2, cv, m.lbGlass);
        }
        // Spitze
        for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) k.set(cu + du, h + 1, cv + dv, m.steel);
        k.set(cu, h + 2, cv, m.sea);
        k.set(cu, h + 3, cv, m.rod);
        // Abspannseile
        for (int du = -1; du <= 1; du += 2) for (int dv = -1; dv <= 1; dv += 2) {
            k.line(cu + du, h - 8, cv + dv, cu + du * 9, 1, cv + dv * 9, 0, m.chain);
            k.set(cu + du * 9, 0, cv + dv * 9, m.steel);
            k.set(cu + du * 9, 1, cv + dv * 9, m.steel);
        }
    }

    private void dish(Kit k, M m, int cu, int cv, int by, int r) {
        if (!k.touches(cu - r - 2, cv - r - 2, cu + r + 2, cv + r + 2)) return;
        k.cylinder(cu, cv, 1, by - 1, 1.2, m.steel);
        k.cylinder(cu, cv, 0, 0, 2.5, m.concrete);
        for (int u = cu - r; u <= cu + r; u++) for (int v = cv - r; v <= cv + r; v++) {
            double d2 = sq(u - cu) + sq(v - cv);
            if (d2 > r * r + 0.5) continue;
            int y = by + (int) Math.round(d2 / (2.2 * r) + 0.55 * (cv - v) * 0.6);
            k.set(u, y, v, d2 > sq(r - 0.8) ? m.steel : k.noise(211, u, v, 2) < 0.3 ? m.snow : m.white);
        }
        double fy = by + r * 0.9, fv = cv - r * 0.15;
        for (int s = -1; s <= 1; s += 2) k.line(cu + s * (r - 1), by + 2, cv, cu, fy, fv, 0, m.bars);
        k.set(cu, (int) Math.round(fy), (int) Math.round(fv), m.sea);
    }

    private void surfaceSpires(Kit k, M m) {
        int[][] sp = {{62, 62, 14}, {68, -8, 11}, {-66, -54, 16}, {-30, 68, 10}, {30, -64, 12}, {66, 40, 9}, {-70, 28, 12}, {-58, -66, 8}, {58, -58, 13}};
        for (int i = 0; i < sp.length; i++) {
            int[] s = sp[i];
            if (!k.touches(s[0] - 6, s[1] - 6, s[0] + 6, s[1] + 6)) continue;
            double lean = (k.rnd(221, i, 0, 0) - 0.5) * 0.5;
            spike(k, s[0], 0, s[1], lean, 1, -lean * 0.6, s[2], 2.2, m.packed, m.blue, m.lbGlass);
            spike(k, s[0] + 3, 0, s[1] + 1, 0.35, 1, 0.2, s[2] * 0.55, 1.3, m.packed, m.prism, m.lbGlass);
            spike(k, s[0] - 2, 0, s[1] - 2, -0.3, 1, -0.25, s[2] * 0.45, 1.1, m.blue, m.packed, m.cyGlass);
        }
    }

    // ── Hilfen ───────────────────────────────────────────────────────────────

    /**
     * Ellipsoid mit Abstandsfeld: ruft {@code f} fuer jede Zelle mit Abstand {@code t} zur Oberflaeche
     * (negativ = innen) bis {@code tMax}; null = unberuehrt. Auf den Ausschnitt und den Radius begrenzt.
     */
    private static void blob(Kit k, double cu, double cy, double cv, double ru, double ry, double rv, int yLo, int yHi, double tMax, Shell f) {
        int uLo = Math.max(Math.max((int) Math.floor(cu - ru - tMax - 1), k.u0()), -RAD), uHi = Math.min(Math.min((int) Math.ceil(cu + ru + tMax + 1), k.u1()), RAD);
        int vLo = Math.max(Math.max((int) Math.floor(cv - rv - tMax - 1), k.v0()), -RAD), vHi = Math.min(Math.min((int) Math.ceil(cv + rv + tMax + 1), k.v1()), RAD);
        if (uLo > uHi || vLo > vHi) return;
        int ya = Math.max(yLo, (int) Math.floor(cy - ry - tMax - 1)), yb = Math.min(yHi, (int) Math.ceil(cy + ry + tMax + 1));
        double minR = Math.min(ru, Math.min(ry, rv));
        double lim = sq(1 + (tMax + 1) / minR);
        for (int u = uLo; u <= uHi; u++) {
            double nu = (u - cu) / ru, gu = nu / ru;
            for (int v = vLo; v <= vHi; v++) {
                double nv = (v - cv) / rv, gv = nv / rv, h = nu * nu + nv * nv;
                if (h > lim) continue;
                for (int y = ya; y <= yb; y++) {
                    double ny = (y - cy) / ry;
                    double d2 = h + ny * ny;
                    if (d2 > lim) continue;
                    double d = Math.sqrt(d2);
                    double t;
                    if (d < 1e-6) t = -1e9;
                    else {
                        double g = Math.sqrt(gu * gu + sq(ny / ry) + gv * gv);
                        t = (d - 1) * d / g;
                    }
                    if (t > tMax) continue;
                    BlockState s = f.at(u, y, v, t);
                    if (s != null) k.set(u, y, v, s);
                }
            }
        }
    }
}
