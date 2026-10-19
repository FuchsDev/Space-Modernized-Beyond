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

/**
 * Dres - "Kernschmelze-Kraftwerk / Meltdown Plant": ein havariertes Atomkraftwerk auf dem Zwergplaneten.
 * <pre>
 *  Oberflaeche (y 0)   Werksgelaende mit Zaun, Strassen, Wache, Schaltanlage, Strommasten, Verwaltungsruine
 *  Kuehlturm (NW)      Hyperboloid bis y 62, Teileinsturz; Schluessel 1 auf dem Rand (Wendel aus Wartungsstegen)
 *  Turbinenhalle (SO)  Becken mit Atommuell-Bruehe (y -9..-7); Schluessel 2 nach Parkour ueber Turbinen/Rohre
 *  Atommuell-Lager (SW) Rampe hinab auf y -22, Fass-Labyrinth zwischen Regalen; Schluessel 3 traegt der Waechter
 *  Reaktorgebaeude (NO) Containment mit Kuppel, Siegeltor -> Treppenturm -> Wendel hinab
 *  Reaktorkern (y -44)  Zylinderhalle r 20 mit zerborstenem Kern, Steuerstab-Geruest, Boss + 2 Truhen
 * </pre>
 */
public final class DresDungeon extends CustomDungeon {

    private static final String GHOUL = "space_beyond:irradiated_ghoul", SLUDGE = "space_beyond:toxic_sludge";
    private static final double TAU = Math.PI * 2;

    // Kuehlturm
    private static final int TU = -42, TV = -42, TTOP = 62;
    // Reaktor / Arena / Treppenturm
    private static final int RU = 35, RV = -40, AF = -44, SU = 35, SV = -70;
    // Turbinenhalle
    private static final int HU0 = 10, HU1 = 84, HV0 = 4, HV1 = 48, HROOF = 18, POOL = -10;
    // Lager
    private static final int WU0 = -88, WU1 = -42, WV0 = 10, WV1 = 66, WF = -22, WC = -10, MU = -87, MV = 11, MN = 8;

    @Override public Stage stage() { return Stage.DRES; }
    @Override public int radius() { return 92; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.dres.DresBlocks.DRES_ROCK.get()); }
    @Override public BlockState filler() { return surface(); }
    @Override public int minY() { return -50; }
    @Override public int maxY() { return 66; }
    @Override public int minBase() { return 56; }
    @Override public int maxBase() { return 180; }
    @Override public int[] entry() { return new int[] {0, 1, 91}; }
    @Override public String boss() { return "space_beyond:core_abomination"; }

    // ── Palette ──────────────────────────────────────────────────────────────

    private static BlockState b(Block x) {
        return Kit.b(x);
    }

    private static final class P {
        final BlockState rock = b(com.space_m.dim.dres.DresBlocks.DRES_ROCK.get());
        final BlockState con = b(ModBlocks.CONCRETE.get()), conCr = b(ModBlocks.CONCRETE_CRACKED.get()), conSm = b(ModBlocks.CONCRETE_SMOOTH.get());
        final BlockState conFlat = b(ModBlocks.CONCRETE_FLAT.get()), haz = b(ModBlocks.CONCRETE_HAZARD.get()), conTile = b(ModBlocks.CONCRETE_TILE.get());
        final BlockState brk = b(ModBlocks.BRICK_CONCRETE.get()), brkCr = b(ModBlocks.BRICK_CONCRETE_CRACKED.get()), brkBr = b(ModBlocks.BRICK_CONCRETE_BROKEN.get());
        final BlockState brkMarked = b(ModBlocks.BRICK_CONCRETE_MARKED.get()), pillar = b(ModBlocks.CONCRETE_PILLAR.get()), rebar = b(ModBlocks.CONCRETE_REBAR.get());
        final BlockState trefoil = b(ModBlocks.CONCRETE_TILE_TREFOIL.get()), vent = b(ModBlocks.CONCRETE_VENT.get()), superCon = b(ModBlocks.CONCRETE_SUPER.get());
        final BlockState ducrete = b(ModBlocks.DUCRETE.get()), brkDucrete = b(ModBlocks.BRICK_DUCRETE.get()), conGray = b(ModBlocks.CONCRETE_GRAY.get());
        final BlockState conBlack = b(ModBlocks.CONCRETE_BLACK.get()), conRed = b(ModBlocks.CONCRETE_RED.get()), conWhite = b(ModBlocks.CONCRETE_WHITE.get());
        final BlockState steel = b(ModBlocks.DECO_STEEL.get()), rust = b(ModBlocks.DECO_RUSTY_STEEL.get()), lead = b(ModBlocks.DECO_LEAD.get());
        final BlockState graphite = b(ModBlocks.BLOCK_GRAPHITE.get()), slag = b(ModBlocks.BLOCK_SLAG.get()), debris = b(ModBlocks.RBMK_DEBRIS.get());
        final BlockState rbmk = b(ModBlocks.DECO_RBMK.get()), rbmkSm = b(ModBlocks.DECO_RBMK_SMOOTH.get()), rbmkPanel = b(ModBlocks.DECO_RBMK_PANEL.get());
        final BlockState asph = b(ModBlocks.ASPHALT.get()), asphL = b(ModBlocks.ASPHALT_LIGHT.get());
        final BlockState glassLead = b(ModBlocks.GLASS_LEAD.get()), glassU = b(ModBlocks.GLASS_URANIUM.get()), glassR = b(ModBlocks.REINFORCED_GLASS.get());
        final BlockState lampG = b(ModBlocks.LAMP_TRITIUM_GREEN_ON.get()), lamp = b(ModBlocks.REINFORCED_LAMP_ON.get());
        final BlockState corium = b(ModBlocks.BLOCK_CORIUM.get()), toxic = b(ModBlocks.TOXIC_BLOCK.get());
        final BlockState plateY = b(ModBlocks.PLATEMETAL_YELLOW.get()), plateG = b(ModBlocks.PLATEMETAL_GREEN.get()), plateGray = b(ModBlocks.PLATEMETAL_LIGHT_GRAY.get());
        final BlockState plateB = b(ModBlocks.PLATEMETAL_BLUE.get());
        final BlockState bSteel = b(ModBlocks.BARREL_STEEL.get()), bIron = b(ModBlocks.BARREL_IRON.get()), bCor = b(ModBlocks.BARREL_CORRODED.get());
        final BlockState bVit = b(ModBlocks.BARREL_VITRIFIED.get()), bYel = b(ModBlocks.BARREL_YELLOW.get()), crate = b(ModBlocks.CRATE_LEAD.get());
        final BlockState sandbags = b(ModBlocks.SANDBAGS.get()), barbed = b(ModBlocks.BARBED_WIRE.get());
        final BlockState pipeX = Kit.axis(ModBlocks.DECO_PIPE_QUAD_GREEN_RUSTED.get(), Direction.Axis.X);
        final BlockState pipeZ = Kit.axis(ModBlocks.DECO_PIPE_QUAD_GREEN_RUSTED.get(), Direction.Axis.Z);
        final BlockState pipeY = Kit.axis(ModBlocks.DECO_PIPE_QUAD.get(), Direction.Axis.Y);
        final BlockState pipeRimY = Kit.axis(ModBlocks.DECO_PIPE_RIM_RUSTED.get(), Direction.Axis.Y);
        final BlockState pillarY = Kit.axis(ModBlocks.CONCRETE_PILLAR.get(), Direction.Axis.Y);
        final BlockState walk = Kit.slab(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, true), walkFull = b(Blocks.WAXED_EXPOSED_CUT_COPPER);
        final BlockState magma = b(Blocks.MAGMA_BLOCK), lava = b(Blocks.LAVA), froglight = b(Blocks.VERDANT_FROGLIGHT);
        final BlockState limeGlass = b(Blocks.LIME_STAINED_GLASS), gravel = b(Blocks.GRAVEL), bars = b(Blocks.IRON_BARS);
        final BlockState chainY = b(Blocks.CHAIN), chainX = Kit.axis(Blocks.CHAIN, Direction.Axis.X);
        final BlockState lanternH = Kit.lantern(Blocks.LANTERN, true), lanternS = Kit.lantern(Blocks.LANTERN, false);
        final BlockState rodDown = Kit.facingAll(Blocks.LIGHTNING_ROD, Direction.DOWN), rodUp = Kit.facingAll(Blocks.LIGHTNING_ROD, Direction.UP);
        final BlockState blackstone = b(Blocks.BLACKSTONE), basalt = b(Blocks.SMOOTH_BASALT), cobweb = b(Blocks.COBWEB);
        final BlockState[] fence = new BlockState[16];

        P() {
            for (int i = 0; i < 16; i++) {
                BlockState f = b(ModBlocks.FENCE_METAL.get());
                f = CityPalette.with(f, BlockStateProperties.NORTH, (i & 1) != 0);
                f = CityPalette.with(f, BlockStateProperties.EAST, (i & 2) != 0);
                f = CityPalette.with(f, BlockStateProperties.SOUTH, (i & 4) != 0);
                f = CityPalette.with(f, BlockStateProperties.WEST, (i & 8) != 0);
                fence[i] = f;
            }
        }

        /** Zaun entlang u (Ost-West verbunden). */
        BlockState fenceU() { return fence[2 | 8]; }
        /** Zaun entlang v (Nord-Sued verbunden). */
        BlockState fenceV() { return fence[1 | 4]; }

        BlockState wallCon(Kit k, int u, int y, int v) {
            double n = k.noise(17, u + y * 7, v - y * 5, 5);
            if (n < 0.22) return conCr;
            double r = k.rnd(18, u, y, v);
            return r < 0.05 ? brkCr : r < 0.08 ? conCr : con;
        }

        BlockState wallBrk(Kit k, int u, int y, int v) {
            double r = k.rnd(19, u, y, v);
            return r < 0.14 ? brkCr : r < 0.2 ? brkBr : brk;
        }

        BlockState barrel(Kit k, int u, int y, int v) {
            double r = k.rnd(23, u, y, v);
            return r < 0.36 ? bSteel : r < 0.6 ? bCor : r < 0.8 ? bIron : r < 0.93 ? bVit : r < 0.96 ? bYel : crate;
        }
    }

    // ── Hilfen ───────────────────────────────────────────────────────────────

    private interface Col {
        void at(int u, int v);
    }

    private static void cols(Kit k, int ua, int va, int ub, int vb, Col c) {
        int u0 = Math.max(Math.min(ua, ub), k.u0()), u1 = Math.min(Math.max(ua, ub), k.u1());
        int v0 = Math.max(Math.min(va, vb), k.v0()), v1 = Math.min(Math.max(va, vb), k.v1());
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) c.at(u, v);
    }

    private static double angDiff(double a, double b) {
        double d = (a - b) % TAU;
        if (d > Math.PI) d -= TAU;
        if (d < -Math.PI) d += TAU;
        return d;
    }

    private static double mod(double a, double m) {
        return ((a % m) + m) % m;
    }

    /** Waagrechter Zylinder entlang u (flache Enden). */
    private static void cylU(Kit k, int ua, int ub, double ya, double vc, double r, BlockState s) {
        int ir = (int) Math.ceil(r);
        cols(k, ua, (int) Math.floor(vc - ir), ub, (int) Math.ceil(vc + ir), (u, v) -> {
            double dv = v - vc;
            for (int y = (int) Math.floor(ya - ir); y <= (int) Math.ceil(ya + ir); y++) {
                double dy = y - ya;
                if (dv * dv + dy * dy <= r * r + 0.25) k.set(u, y, v, s);
            }
        });
    }

    // ── Bau ──────────────────────────────────────────────────────────────────

    @Override
    protected void build(Kit k) {
        P p = new P();
        ground(k, p);
        roads(k, p);
        debris(k, p);
        fenceRing(k, p);
        guardHouse(k, p);
        office(k, p);
        coolingTower(k, p);
        reactor(k, p);
        stack(k, p);
        turbineHall(k, p);
        wasteStore(k, p);
        pipes(k, p);
        switchyard(k, p);
        masts(k, p);
        lights(k, p);
        surfaceMobs(k);
    }

    // ── Gelaende, Strassen, Zaun ─────────────────────────────────────────────

    private void ground(Kit k, P p) {
        cols(k, -87, -87, 87, 87, (u, v) -> {
            double n = k.noise(31, u, v, 9);
            BlockState s;
            if (n < 0.3) s = k.rnd(32, u, 0, v) < 0.08 ? p.gravel : p.rock;
            else if (n < 0.34) s = p.slag;
            else {
                boolean joint = Math.floorMod(u, 7) == 0 || Math.floorMod(v, 7) == 0;
                double r = k.rnd(33, u, 0, v);
                s = r < 0.1 ? p.conCr : r < 0.13 ? p.rock : joint ? p.conFlat : p.con;
            }
            // Brandflecken rund um den Reaktor
            double dr = Math.hypot(u - RU, v - RV);
            if (dr > 18 && dr < 34 && k.noise(34, u, v, 4) > 0.62) s = k.rnd(35, u, 0, v) < 0.5 ? p.blackstone : p.slag;
            k.set(u, 0, v, s);
        });
    }

    private void road(Kit k, P p, int ua, int va, int ub, int vb, boolean alongV) {
        int cu = (ua + ub) / 2, cv = (va + vb) / 2;
        cols(k, ua, va, ub, vb, (u, v) -> {
            boolean line = alongV ? (u == cu && Math.floorMod(v, 6) < 3) : (v == cv && Math.floorMod(u, 6) < 3);
            double r = k.rnd(41, u, 0, v);
            k.set(u, 0, v, line ? p.asphL : r < 0.07 ? p.conCr : r < 0.1 ? p.gravel : p.asph);
        });
    }

    private void roads(Kit k, P p) {
        road(k, p, -2, -8, 2, 92, true);
        road(k, p, -46, -8, 37, -4, false);
        road(k, p, 33, -13, 37, -9, true);
        road(k, p, -44, -14, -40, -9, true);
        road(k, p, 3, 24, 9, 28, false);
        road(k, p, -15, 58, -3, 62, false);
        road(k, p, -27, 35, -3, 39, false);
        road(k, p, 3, 61, 23, 65, false);
    }

    private void debris(Kit k, P p) {
        cols(k, RU - 48, RV - 48, RU + 48, RV + 48, (u, v) -> {
            double d = Math.hypot(u - RU, v - RV);
            if (d < 19 || d > 47) return;
            if (Math.max(Math.abs(u), Math.abs(v)) > 86) return;
            double r = k.rnd(51, u, 0, v);
            double f = 0.03 * (1 - (d - 19) / 30.0);
            if (r < f) {
                k.set(u, 1, v, k.rnd(52, u, 1, v) < 0.6 ? p.graphite : p.debris);
                if (k.rnd(53, u, 2, v) < 0.25) k.set(u, 2, v, p.graphite);
            } else if (r < f * 1.6) {
                k.set(u, 1, v, k.rnd(54, u, 1, v) < 0.5 ? p.brkBr : p.conCr);
            }
        });
    }

    private void fenceRing(Kit k, P p) {
        final int F = 88;
        cols(k, -F, -F, F, F, (u, v) -> {
            if (Math.max(Math.abs(u), Math.abs(v)) != F) return;
            boolean southGate = v == F && Math.abs(u) <= 6;
            if (southGate) return;
            boolean corner = Math.abs(u) == F && Math.abs(v) == F;
            int along = Math.abs(v) == F ? u : v;
            if (corner || Math.floorMod(along, 6) == 0) {
                k.set(u, 1, v, p.pillarY);
                k.set(u, 2, v, Math.floorMod(along, 24) == 0 ? p.trefoil : p.pillarY);
                k.set(u, 3, v, p.haz);
                return;
            }
            if (k.noise(61, u, v, 5) > 0.82) return;   // eingerissene Felder
            BlockState f = Math.abs(v) == F ? p.fenceU() : p.fenceV();
            k.set(u, 1, v, f);
            k.set(u, 2, v, f);
            if (k.rnd(62, u, 3, v) < 0.8) k.set(u, 3, v, p.barbed);
        });
        // Tor
        for (int s = -1; s <= 1; s += 2) {
            int pu = 7 * s;
            k.fill(pu, 1, F, pu + s, 8, F, p.conSm);
            for (int y = 2; y <= 8; y += 3) k.fill(pu, y, F, pu + s, y, F, p.haz);
            k.set(pu, 9, F, p.lamp);
        }
        k.fill(-8, 9, F, 8, 9, F, p.haz);
        k.fill(-6, 10, F, 6, 10, F, p.steel);
        k.set(0, 11, F, p.trefoil);
        k.set(-1, 11, F, p.haz);
        k.set(1, 11, F, p.haz);
        // Sandsaecke an der Einfahrt
        k.fill(-6, 1, 85, -4, 1, 85, p.sandbags);
        k.fill(4, 1, 86, 5, 1, 86, p.sandbags);
    }

    private void guardHouse(Kit k, P p) {
        if (k.touches(5, 76, 12, 85)) {
            k.room(6, 0, 77, 11, 4, 84, p.brk, p.conTile, p.conSm);
            for (int v = 78; v <= 83; v += 2) k.set(6, 2, v, p.glassR);
            k.clear(6, 1, 80, 6, 2, 81);
            k.fill(5, 5, 76, 12, 5, 85, p.steel);
            k.set(7, 1, 78, p.rbmkPanel);
            k.set(8, 1, 78, p.rbmkPanel);
            k.set(7, 2, 78, p.lamp);
            k.set(9, 3, 83, p.lanternH);
        }
        k.loot(10, 1, 83, Direction.WEST);
    }

    private void office(Kit k, P p) {
        int u0 = -36, u1 = -16, v0 = 18, v1 = 34;
        if (k.touches(u0 - 2, v0 - 2, u1 + 2, v1 + 2)) {
            k.room(u0, 0, v0, u1, 6, v1, p.brk, p.conTile, p.conFlat);
            k.room(u0, 6, v0, u1, 12, v1, p.brk, p.conFlat, p.conSm);
            cols(k, u0, v0, u1, v1, (u, v) -> {
                boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
                for (int y = 1; y <= 12; y++) {
                    if (edge && (y < 12)) {
                        boolean win = (y == 2 || y == 3 || y == 8 || y == 9) && Math.floorMod(u + v, 4) == 1 && !(u == u0 && v == v0);
                        k.set(u, y, v, win ? (k.rnd(71, u, y, v) < 0.4 ? Kit.AIR : p.glassR) : p.wallBrk(k, u, y, v));
                    }
                }
                // Einsturz: NO-Ecke und Loecher im Dach
                double c = (u - u1) * (u - u1) + (v - v0) * (v - v0);
                if (c < 60 + 30 * k.rnd(72, u, 0, v)) {
                    for (int y = 7; y <= 12; y++) k.air(u, y, v);
                    if (!edge) k.set(u, 6, v, Kit.AIR);
                    if (!edge && k.rnd(73, u, 1, v) < 0.5) k.set(u, 1, v, k.rnd(74, u, 1, v) < 0.5 ? p.brkBr : p.conCr);
                } else if (!edge && k.noise(75, u, v, 4) > 0.62) k.air(u, 12, v);
                if (edge && c >= 60 && k.rnd(76, u, 11, v) < 0.3) k.air(u, 11, v);
            });
            // Eingang
            k.clear(-27, 1, v1, -25, 3, v1);
            k.fill(-28, 4, v1, -24, 4, v1, p.haz);
            // Treppe ins Obergeschoss
            for (int i = 0; i < 5; i++) {
                k.fill(-35, 1 + i, 31 - i, -34, 1 + i, 31 - i, Kit.stairs(Blocks.STONE_BRICK_STAIRS, Direction.NORTH, false));
                k.fill(-35, 1, 31 - i, -34, i, 31 - i, p.brk);
            }
            k.clear(-35, 6, 26, -34, 6, 31);
            // Mobiliar
            for (int v = 21; v <= 31; v += 5) {
                k.fill(-29, 1, v, -27, 1, v, p.steel);
                k.set(-28, 2, v, p.lamp);
                k.fill(-22, 1, v, -20, 1, v, p.steel);
            }
            k.fill(-31, 7, 30, -29, 7, 30, p.steel);
            k.set(-30, 8, 30, p.rbmkPanel);
            k.set(-25, 5, 26, p.lanternH);
        }
        k.loot(-30, 7, 32, Direction.NORTH);
        k.mob(-24, 1, 28, GHOUL);
    }

    // ── Kuehlturm ────────────────────────────────────────────────────────────

    private static double rin(double y) {
        double t = (y - 44) / 28.1;
        return 14.0 * Math.sqrt(1 + t * t);
    }

    private static double cwOut(int y) {
        double m = 1e9;
        for (int j = 0; j <= 3; j++) if (y + j <= TTOP) m = Math.min(m, rin(y + j));
        return m - 0.6;
    }

    private static int cwY(double s) {
        int n = (int) Math.floor(s / 24.0);
        double q = s - 24.0 * n;
        return Math.min(TTOP, 4 * n + Math.min(4, (int) Math.floor(q / 2.5)));
    }

    private static boolean cwGap(double s) {
        int n = (int) Math.floor(s / 24.0);
        double q = s - 24.0 * n;
        if (n < 1 || n > 14) return false;
        double g = (n % 2 == 0) ? 2.0 : 2.6;
        return q >= 15 && q < 15 + g;
    }

    private static boolean cwNearGap(double s) {
        return cwGap(s - 1.3) || cwGap(s + 1.3) || s < 3;
    }

    private static final double CW_PHI0 = Math.PI / 2, CW_DT = 0.002, CW_S62 = 365, CW_SEND = 373;
    private static final float[] CW_S;
    private static final int CW_N;
    private static final int KEY1_U, KEY1_V;

    static {
        float[] tmp = new float[40000];
        double s = 0;
        int i = 0;
        while (s <= CW_SEND + 2 && i < tmp.length) {
            tmp[i] = (float) s;
            s += (cwOut(cwY(s)) - 1.5) * CW_DT;
            i++;
        }
        CW_S = tmp;
        CW_N = i;
        int idx = 0;
        while (idx < CW_N - 1 && CW_S[idx] < 369.5) idx++;
        double phi = idx * CW_DT + CW_PHI0, r = cwOut(TTOP) - 2.2;
        KEY1_U = (int) Math.round(TU + r * Math.cos(phi));
        KEY1_V = (int) Math.round(TV + r * Math.sin(phi));
    }

    /** Liegt bei (u, v) ein Gelaenderfeld des Stegs mit Boden y? */
    private static boolean railAt(int u, int v, int y) {
        double du = u - TU, dv = v - TV, d = Math.sqrt(du * du + dv * dv);
        double th0 = mod(Math.atan2(dv, du) - CW_PHI0, TAU);
        for (double th = th0; ; th += TAU) {
            int idx = (int) (th / CW_DT);
            if (idx >= CW_N) return false;
            double s = CW_S[idx];
            if (s > CW_SEND) return false;
            if (cwY(s) != y) continue;
            double out = cwOut(y), w = s >= CW_S62 ? 5 : 3;
            if (d > out - w && d <= out - w + 1 && !cwGap(s) && !cwNearGap(s)) return true;
        }
    }

    private void coolingTower(Kit k, P p) {
        final double phiB = -0.35;
        if (k.touches(TU - 40, TV - 40, TU + 40, TV + 40)) {
            cols(k, TU - 29, TV - 29, TU + 29, TV + 29, (u, v) -> {
                double du = u - TU, dv = v - TV, d = Math.sqrt(du * du + dv * dv);
                if (d > 28.6) return;
                double phi = Math.atan2(dv, du);
                double r0 = rin(0);
                // Fundament / Becken
                if (d <= r0 + 3) {
                    BlockState f;
                    if (d > r0 - 0.5) f = p.conSm;
                    else {
                        double n = k.noise(81, u, v, 5);
                        f = n < 0.25 ? p.debris : n < 0.4 ? p.conCr : (Math.floorMod(u - TU, 6) == 0 || Math.floorMod(v - TV, 6) == 0) ? p.conFlat : p.con;
                    }
                    k.set(u, 0, v, f);
                }
                for (int y = 1; y <= TTOP; y++) {
                    double ri = rin(y);
                    if (d <= ri - 0.5 || d > ri + 1.6) continue;
                    double arc = phi * (ri + 0.5);
                    if (y <= 6) {
                        double a = mod(arc + y * 1.1, 7.0), c = mod(arc - y * 1.1, 7.0);
                        if (a < 1.7 || c < 1.7) k.set(u, y, v, p.conSm);
                        continue;
                    }
                    // Teileinsturz (Kerbe oben) und Riss darunter
                    double off = Math.abs(angDiff(phi, phiB)) * ri;
                    if (y >= 36) {
                        double hw = (y - 36) * 0.36 + 1.5 * k.rnd(82, (int) Math.round(phi * 12), y / 2, 0);
                        if (off < hw) continue;
                        if (off < hw + 1.3) {
                            k.set(u, y, v, k.rnd(83, u, y, v) < 0.5 ? p.rebar : p.brkBr);
                            continue;
                        }
                    } else if (y >= 14) {
                        double crack = Math.abs(angDiff(phi, phiB + 0.05 * Math.sin(y * 0.45)) * ri);
                        if (crack < 0.55) continue;
                        if (crack < 1.4) {
                            k.set(u, y, v, p.brkBr);
                            continue;
                        }
                    }
                    BlockState s;
                    if (y == 7 || y == TTOP) s = p.conSm;
                    else if (y >= 56 && k.rnd(84, u, y, v) < 0.45) s = k.rnd(85, u, y, v) < 0.5 ? p.conGray : p.conBlack;
                    else if (Math.floorMod(y, 11) == 7) s = p.conSm;
                    else {
                        s = p.wallCon(k, (int) Math.round(arc), y, 0);
                        for (double pc : new double[] {2.3, 3.9, 5.2}) {
                            double cr = Math.abs(angDiff(phi, pc + 0.04 * Math.sin(y * 0.3))) * ri;
                            if (cr < 0.7 && y > 12 && y < 50) s = k.rnd(86, u, y, v) < 0.3 ? Kit.AIR : p.brkCr;
                        }
                    }
                    k.set(u, y, v, s);
                }
                // Einbauten im Becken: Stuetzenraster mit Traegern
                if (d < 15.5) {
                    int mu = Math.floorMod(u - TU + 3, 7), mv = Math.floorMod(v - TV + 3, 7);
                    if (mu == 0 && mv == 0) for (int y = 1; y <= 8; y++) k.set(u, y, v, p.pillarY);
                    if (mu == 0 || mv == 0) k.set(u, 9, v, p.rust);
                    if (mu == 0 && mv == 3 && d < 14) k.set(u, 8, v, p.lanternH);
                }
            });
            catwalk(k, p);
            // Truemmer am Fuss des Einsturzes
            for (int i = 0; i < 12; i++) {
                double a = phiB + (k.rnd(87, i, 0, 0) - 0.5) * 0.9, r = 29 + 10 * k.rnd(88, i, 0, 0);
                double cu = TU + r * Math.cos(a), cv = TV + r * Math.sin(a), rr = 1.4 + 1.8 * k.rnd(89, i, 0, 0);
                BlockState s = i % 3 == 0 ? p.brkBr : i % 3 == 1 ? p.conCr : p.con;
                k.ellipsoid(cu, 0, cv, rr, rr * 0.8, rr * 1.2, s);
                if (i % 4 == 0) k.line(cu, 1, cv, cu + 2, 3, cv - 1, 0.3, p.rebar);
            }
        }
        k.keyChest(KEY1_U, TTOP + 1, KEY1_V, Direction.WEST);
        k.spawner(TU, 1, TV, GHOUL);
        k.mob(TU + 6, 1, TV + 7, GHOUL);
        k.mob(TU - 9, 1, TV + 1, GHOUL);
        k.barrel(TU - 2, 1, TV + 10);
    }

    private void catwalk(Kit k, P p) {
        cols(k, TU - 28, TV - 28, TU + 28, TV + 28, (u, v) -> {
            double du = u - TU, dv = v - TV, d = Math.sqrt(du * du + dv * dv);
            if (d > 27 || d < 8) return;
            double th0 = mod(Math.atan2(dv, du) - CW_PHI0, TAU);
            for (double th = th0; ; th += TAU) {
                int idx = (int) (th / CW_DT);
                if (idx >= CW_N) break;
                double s = CW_S[idx];
                if (s > CW_SEND) break;
                int y = cwY(s);
                double out = cwOut(y), w = s >= CW_S62 ? 5 : 3;
                if (d > out - w && d <= out) {
                    if (cwGap(s)) continue;
                    k.set(u, y, v, y == 0 ? p.walkFull : p.walk);
                    if (d <= out - w + 1 && !cwNearGap(s)) {
                        int m = (railAt(u, v - 1, y) ? 1 : 0) | (railAt(u + 1, v, y) ? 2 : 0) | (railAt(u, v + 1, y) ? 4 : 0) | (railAt(u - 1, v, y) ? 8 : 0);
                        k.set(u, y + 1, v, p.fence[m]);
                    } else if (d > out - 1 && y > 2 && ((int) s) % 6 == 0) {
                        k.set(u, y - 1, v, p.rust);
                    }
                } else if (d > out && d <= rin(y) - 0.5 && y > 0) {
                    if (!cwGap(s)) k.set(u, y, v, p.walk);
                }
                // Wandleuchten
                if (y + 2 <= TTOP && ((int) s) % 12 == 6) {
                    double rw = rin(y + 2);
                    if (d > rw - 0.5 && d <= rw + 0.5) k.set(u, y + 2, v, p.lamp);
                }
            }
        });
    }

    // ── Reaktor, Treppenturm, Arena ──────────────────────────────────────────

    private BlockState reactorFloor(Kit k, P p, int u, int v, double d) {
        if (d <= 4.2) return (Math.floorMod(u - RU, 3) == 0 || Math.floorMod(v - RV, 3) == 0) ? p.glassLead : p.glassU;
        if (d <= 5.0) return p.lampG;
        if (d <= 10.3) return ((u + v) & 1) == 0 ? p.rbmk : p.rbmkSm;
        if (d <= 11.2) return p.haz;
        double r = k.rnd(91, u, 0, v);
        return r < 0.12 ? p.conCr : r < 0.16 ? p.debris : p.conTile;
    }

    private void reactor(Kit k, P p) {
        final double phiDome = -1.0;
        if (k.touches(RU - 23, RV - 42, RU + 23, RV + 29)) {
            cols(k, RU - 21, RV - 21, RU + 21, RV + 21, (u, v) -> {
                double du = u - RU, dv = v - RV, d = Math.sqrt(du * du + dv * dv);
                if (d > 20.5) return;
                double phi = Math.atan2(dv, du);
                boolean butt = false;
                for (int q = 0; q < 4; q++) if (d > 17.5 && Math.abs(angDiff(phi, Math.PI / 4 + q * Math.PI / 2)) * d < 1.6) butt = true;
                if (butt) {
                    for (int y = 0; y <= 30; y++) k.set(u, y, v, y == 30 ? p.conSm : Math.floorMod(y, 8) == 4 ? p.haz : p.con);
                    return;
                }
                if (d > 18.0) return;
                k.set(u, 0, v, d > 15.5 ? p.conSm : reactorFloor(k, p, u, v, d));
                for (int y = 1; y <= 24; y++) {
                    BlockState s;
                    if (d > 15.5) {
                        if (d <= 16.5) s = Math.floorMod(y, 6) == 0 ? p.rust : p.steel;
                        else s = y == 24 ? p.conSm : Math.floorMod(y, 12) == 0 ? p.con : p.wallCon(k, u, y, v);
                        if (d <= 16.5 && Math.floorMod(y, 12) == 5 && Math.floorMod((int) Math.round(phi * 8), 2) == 0 && Math.abs(Math.round(phi * 8) - phi * 8) < 0.15) s = p.lamp;
                    } else s = Kit.AIR;
                    k.set(u, y, v, s);
                }
                // Kuppel
                for (int y = 25; y <= 41; y++) {
                    double ny = (y - 24) / 16.5, nd = d / 18.0, dd = Math.sqrt(nd * nd + ny * ny);
                    if (dd > 1.0) break;
                    double depth = (1 - dd) * 16.5;
                    if (depth < 2.3) {
                        double off = Math.abs(angDiff(phi, phiDome)) * d;
                        double hw = (y - 28) * 0.7 + 1.2 * k.rnd(92, (int) Math.round(phi * 10), y, 0);
                        if (y >= 29 && off < hw) continue;
                        if (y >= 29 && off < hw + 1.2) {
                            k.set(u, y, v, k.rnd(93, u, y, v) < 0.5 ? p.rebar : p.brkBr);
                            continue;
                        }
                        k.set(u, y, v, depth < 0.9 ? (k.rnd(94, u, y, v) < 0.15 ? p.conGray : p.conSm) : p.steel);
                    } else k.air(u, y, v);
                }
            });
            // Eingang + Schleuse
            k.room(31, 0, -21, 39, 7, -12, p.brk, p.conTile, p.conSm);
            k.clear(33, 1, -25, 37, 5, -12);
            k.fill(32, 6, -25, 38, 6, -21, p.haz);
            k.fill(30, 8, -22, 40, 8, -11, p.steel);
            k.set(35, 6, -16, p.lanternH);
            // RBMK-Deckel: herausgeschleuderte Kanalkappen
            cols(k, RU - 10, RV - 10, RU + 10, RV + 10, (u, v) -> {
                double d = Math.hypot(u - RU, v - RV);
                if (d > 5.5 && d < 9.6 && k.rnd(95, u, 1, v) < 0.11) k.set(u, 1, v, k.rnd(96, u, 1, v) < 0.5 ? p.rbmk : p.rbmkPanel);
            });
            // "Elena": gekippte obere Schutzplatte
            cols(k, 38, -42, 50, -30, (u, v) -> {
                double dd = (u - 44) * (u - 44) + (v + 36) * (v + 36);
                if (dd > 31) return;
                int yy = 1 + (int) Math.round((u - 38.5) * 0.8);
                k.set(u, yy, v, dd > 24 ? p.rust : p.steel);
                if (yy > 1) k.set(u, yy - 1, v, p.steel);
                if (Math.floorMod(u, 2) == 0 && Math.floorMod(v, 2) == 0 && dd < 20) k.set(u, yy + 1, v, p.pipeRimY);
            });
            // Polarkran
            cols(k, RU - 16, RV - 16, RU + 16, RV + 16, (u, v) -> {
                double d = Math.hypot(u - RU, v - RV);
                if (d > 13.5 && d <= 15.5) {
                    k.set(u, 19, v, p.plateY);
                    if (Math.floorMod(u + v, 5) == 0) k.set(u, 18, v, p.lanternH);
                }
            });
            k.fill(34, 20, -55, 36, 20, -25, p.plateY);
            k.fill(34, 21, -55, 34, 21, -25, p.haz);
            k.fill(36, 21, -55, 36, 21, -25, p.haz);
            k.fill(33, 21, -42, 37, 22, -38, p.steel);
            k.fill(35, 11, -40, 35, 20, -40, p.chainY);
            k.fill(34, 10, -40, 36, 10, -40, p.rust);
            k.set(34, 9, -40, p.rust);
            k.set(36, 9, -40, p.rust);
            // Siegeltor + Torhaus zum Treppenturm
            k.fill(30, 0, -61, 40, 8, -57, p.con);
            k.fill(29, 0, -58, 41, 11, -55, p.steel);
            k.sealGate(RU, 0, -55, Direction.SOUTH, 2, 5, p.lead, p.haz);
            k.clear(33, 1, -64, 37, 5, -57);
            k.fill(33, 0, -64, 37, 0, -57, p.conTile);
            // Treppenturm
            k.fill(25, 0, -81, 45, 17, -59, p.con);
            cols(k, 25, -81, 45, -59, (u, v) -> {
                boolean edge = u <= 26 || u >= 44 || v <= -80 || v >= -60;
                for (int y = 1; y <= 16; y++) {
                    if (!edge) k.air(u, y, v);
                    else {
                        boolean win = y >= 9 && y <= 12 && Math.floorMod(u + v, 4) < 2 && !(u <= 26 && v <= -80);
                        k.set(u, y, v, win ? p.glassR : Math.floorMod(y, 8) == 0 ? p.haz : p.wallCon(k, u, y, v));
                    }
                }
                if (!edge) k.set(u, 0, v, p.conTile);
            });
            k.clear(33, 1, -64, 37, 5, -59);
            // Schacht mit Wendel
            k.tube(SU, SV, AF - 1, 0, 7.5, 2.0, p.lead, Kit.AIR);
            k.cylinder(SU, SV, AF - 1, AF, 6.2, p.conSm);
            k.cylinder(SU, SV, AF, 16, 1.4, p.pillarY);
            k.spiral(SU, SV, AF, 0, 2.0, 5.5, 2.0, 253.3, 4, p.walkFull);
            cols(k, SU - 7, SV - 7, SU + 7, SV + 7, (u, v) -> {
                double d = Math.hypot(u - SU, v - SV);
                double a = Math.toDegrees(mod(Math.atan2(v - SV, u - SU), TAU));
                if (d <= 6.2 && a >= 88 && a <= 200) k.set(u, 0, v, p.walkFull);
                if (d > 6.2 && d <= 6.8) {
                    for (int y = AF + 4; y <= -2; y += 6) if (Math.floorMod(u * 3 + v, 7) == 0) k.set(u, y, v, p.lamp);
                }
                // Gelaender um das Schachtloch im Turm
                if (d > 6.2 && d <= 7.2 && !(a >= 70 && a <= 110)) k.set(u, 1, v, p.fence[0]);
            });
            arena(k, p);
            // Brunnen: Bleiglasfenster ueber dem Kern
            k.tube(RU, RV, -17, -1, 5.5, 1.5, p.lead, Kit.AIR);
        }
        k.mob(28, 1, -46, GHOUL);
        k.mob(44, 1, -46, GHOUL);
        k.mob(27, 1, -31, SLUDGE);
        k.mob(35, 1, -15, GHOUL);
        k.spawner(23, 1, -47, GHOUL);
        k.loot(22, 1, -40, Direction.EAST);
        k.barrel(41, 1, -77);
        // Arena
        k.boss(RU, AF + 1, -26);
        k.bossChest(30, AF + 1, -22, Direction.NORTH);
        k.bossChest(40, AF + 1, -22, Direction.NORTH);
    }

    private void arena(Kit k, P p) {
        cols(k, RU - 23, RV - 23, RU + 23, RV + 23, (u, v) -> {
            double du = u - RU, dv = v - RV, d = Math.sqrt(du * du + dv * dv);
            if (d > 22.8) return;
            double phi = Math.atan2(dv, du);
            k.set(u, AF - 1, v, p.con);
            BlockState fl;
            if (d > 20.5) fl = p.superCon;
            else if (d > 19.4) fl = p.haz;
            else {
                double n = k.noise(101, u, v, 4);
                boolean ray = Math.abs(Math.round(phi * 12 / Math.PI) - phi * 12 / Math.PI) < 0.08 * (d / 6);
                fl = d < 12 && n < 0.45 ? (k.rnd(102, u, 0, v) < 0.5 ? p.slag : p.basalt) : ray ? p.rbmk : n < 0.2 ? p.debris : p.rbmkSm;
            }
            k.set(u, AF, v, fl);
            double seg = mod(phi, Math.PI / 6) * d;   // Bogenmass innerhalb eines 30-Grad-Feldes
            boolean pil = Math.abs(angDiff(phi, Math.round(phi / (Math.PI / 6)) * (Math.PI / 6))) * d < 1.1;
            for (int y = AF + 1; y <= -26; y++) {
                BlockState s;
                if (d > 20.5) {
                    if (d <= 21.5) {
                        if (Math.floorMod(y, 7) == 0) s = p.steel;
                        else if ((y == AF + 5 || y == AF + 12) && !pil && seg > 2 && seg < 3.2) s = p.lampG;
                        else s = k.rnd(103, u, y, v) < 0.1 ? p.rust : p.lead;
                    } else s = p.wallCon(k, u, y, v);
                } else if (d > 19.5 && pil && y <= -28) s = p.pillarY;
                else if (d > 19.5 && y == -36 && !pil) s = p.pipeX;
                else s = Kit.AIR;
                k.set(u, y, v, s);
            }
            for (int y = -25; y <= -13; y++) {
                double ny = (y + 26) / 12.0, nd = d / 22.8, dd = Math.sqrt(nd * nd + ny * ny);
                if (dd > 1.0) break;
                double depth = (1 - dd) * 12;
                k.set(u, y, v, depth < 2.5 ? (depth < 1.0 ? (Math.floorMod(y, 4) == 0 ? p.steel : p.lead) : p.con) : Kit.AIR);
            }
        });
        // Tuer vom Schacht
        k.clear(33, AF + 1, -65, 37, AF + 5, -59);
        k.fill(32, AF + 1, -60, 32, AF + 6, -60, p.haz);
        k.fill(38, AF + 1, -60, 38, AF + 6, -60, p.haz);
        k.fill(32, AF + 6, -60, 38, AF + 6, -60, p.haz);
        // Kern: zerborstenes Druckgefaess, Schmelze, Elefantenfuss
        cols(k, RU - 8, RV - 8, RU + 8, RV + 8, (u, v) -> {
            double du = u - RU, dv = v - RV, d = Math.sqrt(du * du + dv * dv);
            if (d > 7.6) return;
            double phi = Math.atan2(dv, du);
            if (d <= 3.6) {
                k.set(u, AF, v, p.magma);
                k.set(u, AF + 1, v, d <= 2.6 ? p.lava : p.magma);
            } else if (d <= 5.8) {
                k.set(u, AF + 1, v, k.rnd(104, u, 1, v) < 0.5 ? p.magma : p.corium);
                int h = (int) Math.round(2.5 - (d - 3.6) + 2 * k.noise(105, u, v, 2));
                for (int y = AF + 2; y <= AF + 1 + h; y++) k.set(u, y, v, k.rnd(106, u, y, v) < 0.15 ? p.froglight : p.corium);
            } else {
                // Gefaesswand, oben zerrissen
                int top = AF + 3 + (int) Math.round(10 * k.noise(107, (int) Math.round(phi * 9), 0, 2));
                boolean gap = Math.abs(angDiff(phi, 2.4)) < 0.35 || Math.abs(angDiff(phi, -0.6)) < 0.3;
                if (gap) top = AF + 2;
                for (int y = AF + 1; y <= top; y++) k.set(u, y, v, Math.floorMod(y, 4) == 0 ? p.rust : p.steel);
                if (k.rnd(108, u, top, v) < 0.3) k.set(u, top + 1, v, p.limeGlass);
            }
        });
        // gruene Kristall-/Glutzacken aus der Schmelze
        for (int i = 0; i < 7; i++) {
            double a = i * TAU / 7 + 0.3, r = 4.2;
            int cu = (int) Math.round(RU + r * Math.cos(a)), cv = (int) Math.round(RV + r * Math.sin(a));
            int h = 5 + (i * 3) % 5;
            k.line(cu, AF + 3, cv, cu + Math.cos(a) * 1.5, AF + 3 + h, cv + Math.sin(a) * 1.5, 0.45, i % 2 == 0 ? p.glassU : p.limeGlass);
        }
        k.set(RU, AF + 6, RV, p.froglight);
        // gruen glimmende Saeule aus der Schmelze hinauf zum Bleiglasfenster
        k.cylinder(RU, RV, AF + 7, -20, 0.6, p.glassU);
        k.set(RU, -19, RV, p.froglight);
        // Steuerstab-Geruest
        int[][] legs = {{-9, -9}, {9, -9}, {9, 9}, {-9, 9}};
        for (int[] l : legs) {
            k.fill(RU + l[0], AF + 1, RV + l[1], RU + l[0] + Integer.signum(l[0]), -28, RV + l[1] + Integer.signum(l[1]), p.steel);
            for (int y = AF + 3; y <= -30; y += 5) k.set(RU + l[0], y, RV + l[1], p.haz);
        }
        int ft = -27;
        k.fill(RU - 10, ft, RV - 10, RU + 10, ft, RV - 9, p.plateY);
        k.fill(RU - 10, ft, RV + 9, RU + 10, ft, RV + 10, p.plateY);
        k.fill(RU - 10, ft, RV - 10, RU - 9, ft, RV + 10, p.plateY);
        k.fill(RU + 9, ft, RV - 10, RU + 10, ft, RV + 10, p.plateY);
        k.fill(RU - 10, ft, RV, RU + 10, ft, RV, p.steel);
        k.fill(RU, ft, RV - 10, RU, ft, RV + 10, p.steel);
        for (int i = 0; i < 12; i++) {
            int ru = RU - 7 + (i % 4) * 5 - (i / 4 == 1 ? 1 : 0), rv = RV - 6 + (i / 4) * 6;
            if (Math.abs(ru - RU) < 3 && Math.abs(rv - RV) < 3) continue;
            int len = 3 + (int) (k.rnd(111, i, 0, 0) * 8);
            k.fill(ru, ft - 1 - len, rv, ru, ft - 1, rv, p.chainY);
            k.fill(ru, ft - 5 - len, rv, ru, ft - 2 - len, rv, i % 3 == 0 ? p.graphite : p.lead);
            if (i % 3 == 0 && Math.hypot(ru - RU, rv - RV) < 7) k.set(ru, ft - 5 - len, rv, p.lead);
        }
        // abgestuerzte Staebe / Brennstab-Truemmer auf dem Boden
        for (int i = 0; i < 16; i++) {
            double a = k.rnd(112, i, 0, 0) * TAU, r = 9 + 9 * k.rnd(113, i, 0, 0);
            double cu = RU + r * Math.cos(a), cv = RV + r * Math.sin(a), ang = k.rnd(114, i, 0, 0) * TAU;
            double eu = cu + 3 * Math.cos(ang), ev = cv + 3 * Math.sin(ang);
            if (Math.hypot(cu - RU, cv + 26) < 5 || Math.hypot(eu - RU, ev + 26) < 5) continue;   // Bossplatz
            if (Math.hypot(cu - 35, cv + 22) < 7 || Math.abs(cu - RU) < 3 && cv < -56) continue;   // Truhen, Tuer
            if (Math.hypot(eu - RU, ev - RV) > 19 || Math.hypot(eu - 35, ev + 22) < 7) continue;
            BlockState s = i % 3 == 0 ? p.glassU : i % 3 == 1 ? p.graphite : p.rbmk;
            k.line(cu, AF + 1, cv, eu, AF + 1, ev, 0.3, s);
        }
        // Lichter an der Decke
        for (int i = 0; i < 8; i++) {
            double a = i * TAU / 8;
            int cu = (int) Math.round(RU + 15 * Math.cos(a)), cv = (int) Math.round(RV + 15 * Math.sin(a));
            k.set(cu, -21, cv, p.lamp);
        }
    }

    // ── Abluftkamin ──────────────────────────────────────────────────────────

    private void stack(Kit k, P p) {
        int cu = 64, cv = -66;
        k.line(52, 6, -50, cu - 3, 6, cv + 2, 1.0, p.rust);   // Verbindungsrohr vom Reaktor
        if (!k.touches(cu - 8, cv - 8, cu + 8, cv + 8)) return;
        k.fill(cu - 7, 0, cv - 7, cu + 7, 0, cv + 7, p.conSm);
        cols(k, cu - 3, cv - 3, cu + 3, cv + 3, (u, v) -> {
            double d = Math.hypot(u - cu, v - cv);
            if (d > 2.7) return;
            for (int y = 1; y <= 56; y++) {
                BlockState s = d > 1.6 ? (y >= 36 && Math.floorMod(y, 8) < 4 ? p.conRed : y >= 36 ? p.conWhite : p.wallCon(k, u, y, v)) : Kit.AIR;
                k.set(u, y, v, s);
            }
        });
        double[] sx = {-1, 1, 1, -1}, sz = {-1, -1, 1, 1};
        for (int i = 0; i < 4; i++) {
            k.line(cu + 6 * sx[i], 1, cv + 6 * sz[i], cu + 3 * sx[i], 50, cv + 3 * sz[i], 0.4, p.steel);
        }
        for (int y = 10; y <= 50; y += 10) {
            double o = 6 - 3.0 * y / 50;
            for (int i = 0; i < 4; i++) {
                int j = (i + 1) % 4;
                k.line(cu + o * sx[i], y, cv + o * sz[i], cu + o * sx[j], y, cv + o * sz[j], 0.4, p.steel);
                double o2 = 6 - 3.0 * (y - 10) / 50;
                k.line(cu + o2 * sx[i], y - 10, cv + o2 * sz[i], cu + o * sx[j], y, cv + o * sz[j], 0.3, p.rust);
            }
        }
        k.fill(cu - 3, 50, cv - 3, cu + 3, 50, cv + 3, p.plateY);
        k.fill(cu - 2, 50, cv - 2, cu + 2, 50, cv + 2, Kit.AIR);
        k.set(cu + 3, 51, cv + 3, p.lamp);
        k.set(cu - 3, 51, cv - 3, p.lamp);
    }

    // ── Turbinenhalle ────────────────────────────────────────────────────────

    private void casing(Kit k, P p, int ua, int ub, int vc, int top, BlockState body) {
        double r = 3.2, ya = top - 3;
        cols(k, ua, vc - 4, ub, vc + 4, (u, v) -> {
            int dv = v - vc;
            for (int y = POOL + 1; y <= top; y++) {
                double dy = y - ya;
                if (dv * dv + dy * dy <= r * r + 0.25) {
                    boolean rib = u == ua || u == ub || Math.floorMod(u - ua, 4) == 0;
                    k.set(u, y, v, rib ? p.steel : y == top && Math.abs(dv) == 1 ? p.haz : body);
                } else if (y < ya && Math.abs(dv) <= 2 && (u <= ua + 1 || u >= ub - 1 || Math.abs(dv) <= 1)) {
                    k.set(u, y, v, p.con);
                }
            }
        });
    }

    private void box(Kit k, P p, int ua, int va, int ub, int vb, int top, BlockState body) {
        k.fill(ua, POOL + 1, va, ub, -5, vb, p.con);
        k.fill(ua, -4, va, ub, top, vb, body);
        k.fill(ua, top, va, ub, top, va, p.haz);
        k.fill(ua, top, vb, ub, top, vb, p.haz);
        k.fill(ua, top - 1, va, ua, top - 1, vb, p.steel);
        k.fill(ub, top - 1, va, ub, top - 1, vb, p.steel);
    }

    private void pipeV(Kit k, P p, int u, int va, int vb, int top, int ga, int gb) {
        for (int v = va; v <= vb; v++) {
            if (v >= ga && v <= gb) continue;
            boolean flange = v == ga - 1 || v == gb + 1;
            k.set(u, top, v, flange ? p.rust : p.pipeZ);
            k.set(u - 1, top - 1, v, flange ? p.rust : p.pipeZ);
            k.set(u + 1, top - 1, v, flange ? p.rust : p.pipeZ);
            k.set(u, top - 1, v, p.pipeZ);
            k.set(u, top - 2, v, flange ? p.rust : p.pipeZ);
        }
        for (int v : new int[] {va, vb}) k.fill(u, POOL + 1, v, u, top - 3, v, p.con);
    }

    private void standpipe(Kit k, P p, int u, int v, int top) {
        k.fill(u, POOL + 1, v, u, top - 1, v, p.pipeY);
        k.set(u, top, v, p.rust);
    }

    private void disc(Kit k, P p, int u, int vc, double ya, double r) {
        cols(k, u, vc - 4, u, vc + 4, (uu, v) -> {
            int dv = v - vc;
            for (int y = (int) Math.floor(ya - r); y <= (int) Math.ceil(ya + r); y++) {
                double dy = y - ya, dd = dv * dv + dy * dy;
                if (dd <= r * r + 0.25) k.set(uu, y, v, dd > (r - 1) * (r - 1) ? p.rust : p.steel);
            }
        });
    }

    private void turbineHall(Kit k, P p) {
        if (k.touches(HU0, HV0, HU1, HV1)) {
            cols(k, HU0, HV0, HU1, HV1, (u, v) -> {
                boolean wall = u <= HU0 + 1 || u >= HU1 - 1 || v <= HV0 + 1 || v >= HV1 - 1;
                boolean outer = u == HU0 || u == HU1 || v == HV0 || v == HV1;
                int face = (v <= HV0 + 1 || v >= HV1 - 1) ? u : v;
                for (int y = POOL - 1; y <= HROOF; y++) {
                    BlockState s;
                    if (wall) {
                        if (y <= 0) s = p.con;
                        else if (y == HROOF) s = p.conSm;
                        else if (outer && Math.floorMod(face, 8) == 2) s = p.pillarY;
                        else if (y >= 10 && y <= 14) s = outer ? (k.rnd(121, u, y, v) < 0.25 ? Kit.AIR : p.glassR) : (y == 10 || y == 14 ? p.steel : Kit.AIR);
                        else if (y == 9 || y == 15) s = p.conSm;
                        else s = p.wallBrk(k, u, y, v);
                        if (!outer && y >= 10 && y <= 14) s = y == 10 || y == 14 ? p.steel : Kit.AIR;
                    } else if (y <= POOL) s = p.con;
                    else if (y == HROOF) s = k.noise(122, u, v, 6) > 0.66 ? Kit.AIR : Math.floorMod(u, 4) == 0 ? p.rust : p.plateGray;
                    else if (u <= 17 && y <= 0) s = y == 0 ? (u == 17 ? p.haz : p.conTile) : p.con;
                    else if (y <= POOL + 3) s = p.toxic;
                    else s = Kit.AIR;
                    k.set(u, y, v, s);
                }
                if (!wall) {
                    // Binder unter dem Dach
                    if (Math.floorMod(u - 12, 8) == 0) k.set(u, HROOF - 1, v, p.rust);
                    if (Math.floorMod(u - 12, 8) == 0 && Math.floorMod(v, 7) == 5 && v > 6 && v < 46) {
                        k.set(u, HROOF - 2, v, p.chainY);
                        k.set(u, HROOF - 3, v, p.lanternH);
                    }
                    // Kranbahn und Rohre an den Laengswaenden
                    if (v == HV0 + 2 || v == HV1 - 2) {
                        k.set(u, 13, v, p.steel);
                        k.set(u, 16, v, p.pipeX);
                    }
                }
            });
            // Eingang West
            k.clear(HU0, 1, 24, HU0 + 1, 4, 28);
            k.fill(HU0, 5, 23, HU0, 5, 29, p.haz);
            // Notausgang Ost (nur hinaus: 7 Bloecke ueber dem Boden)
            k.clear(HU1 - 1, 8, 39, HU1, 10, 41);
            // Galerie-Gelaender, Lueke beim Absprung (v 10..14) und an der Leiter
            for (int v = 6; v <= 46; v++) {
                if (v >= 10 && v <= 14 || v >= 29 && v <= 31) continue;
                int m = (v > 6 && !(v == 15 || v == 32) ? 1 : 0) | (v < 46 && !(v == 9 || v == 28) ? 4 : 0);
                k.set(17, 1, v, p.fence[m]);
            }
            k.ladder(18, -6, 0, 30, Direction.WEST);
            // Leitstand auf der Galerie
            k.fill(12, 1, 41, 16, 3, 46, p.glassR);
            k.clear(12, 1, 42, 15, 3, 46);
            k.clear(13, 1, 41, 14, 2, 41);
            k.fill(12, 4, 40, 16, 4, 46, p.steel);
            k.fill(12, 1, 46, 15, 1, 46, p.rbmkPanel);
            k.fill(12, 2, 46, 15, 2, 46, p.lamp);
            k.set(16, 1, 46, p.rbmkPanel);
            // Reihe A (v 12): nach Osten
            casing(k, p, 20, 28, 12, 0, p.plateG);
            cylU(k, 28, 31, -3, 12, 1.0, p.steel);
            casing(k, p, 31, 41, 12, 1, p.plateG);
            cylU(k, 41, 45, -2, 12, 1.0, p.steel);
            casing(k, p, 45, 55, 12, 1, p.plateG);
            box(k, p, 58, 9, 66, 15, 2, p.plateB);
            k.fill(68, POOL + 1, 11, 71, -1, 13, p.steel);
            // A -> B: Rohrbruecke mit Bruch
            pipeV(k, p, 64, 16, 22, 2, 19, 20);
            // Reihe B (v 26): nach Westen
            box(k, p, 62, 23, 66, 29, 2, p.plateY);
            cylU(k, 48, 61, -1, 26, 1.0, p.steel);
            for (int u = 59; u >= 50; u -= 3) disc(k, p, u, 26, -1, 3.4);
            casing(k, p, 40, 47, 26, 3, p.plateG);
            cylU(k, 30, 39, 2, 26, 1.2, p.steel);
            disc(k, p, 35, 26, 2, 2.2);
            k.fill(30, POOL + 1, 26, 30, 0, 26, p.con);
            casing(k, p, 20, 27, 26, 3, p.plateG);
            // B -> C
            pipeV(k, p, 22, 30, 36, 3, 32, 33);
            // Reihe C (v 40): nach Osten, steigend
            box(k, p, 19, 37, 24, 43, 3, p.plateGray);
            casing(k, p, 27, 36, 40, 4, p.plateG);
            standpipe(k, p, 39, 40, 4);
            standpipe(k, p, 42, 40, 5);
            standpipe(k, p, 45, 40, 5);
            standpipe(k, p, 48, 40, 6);
            casing(k, p, 51, 60, 40, 6, p.plateG);
            box(k, p, 63, 37, 70, 43, 7, p.plateB);
            box(k, p, 73, 35, 82, 45, 7, p.conSm);
            k.fill(74, 7, 36, 81, 7, 44, p.conTile);
            // Laufkran
            k.fill(47, 14, HV0 + 2, 48, 15, HV1 - 2, p.plateY);
            k.fill(47, 16, HV0 + 2, 48, 16, HV0 + 4, p.haz);
            k.fill(47, 7, 33, 47, 13, 33, p.chainY);
            k.set(47, 6, 33, p.rust);
            // Truemmer im Becken (knapp unter der Oberflaeche)
            for (int i = 0; i < 10; i++) {
                int du = 20 + (int) (k.rnd(123, i, 0, 0) * 60), dv = 7 + (int) (k.rnd(124, i, 0, 0) * 38);
                k.fill(du, POOL + 1, dv, du + 1, POOL + 2, dv, i % 2 == 0 ? p.rust : p.conCr);
            }
        }
        k.keyChest(80, 8, 40, Direction.WEST);
        k.loot(81, 8, 36, Direction.WEST);
        k.spawner(13, 1, 8, SLUDGE);
        k.spawner(66, 3, 9, SLUDGE);
        k.spawner(66, 3, 29, SLUDGE);
        k.mob(36, 2, 12, SLUDGE);
        k.mob(55, 7, 40, SLUDGE);
        k.mob(22, 4, 40, GHOUL);
    }

    // ── Atommuell-Lager ──────────────────────────────────────────────────────

    /** Labyrinth (Tiefensuche): wE[i][j] Wand zwischen (i,j)-(i+1,j), wS[i][j] zwischen (i,j)-(i,j+1). */
    private void maze(Kit k, boolean[][] wE, boolean[][] wS) {
        for (boolean[] a : wE) java.util.Arrays.fill(a, true);
        for (boolean[] a : wS) java.util.Arrays.fill(a, true);
        boolean[][] vis = new boolean[MN][MN];
        int[] st = new int[MN * MN];
        int sp = 0, step = 0;
        st[sp++] = 7 * MN + 7;
        vis[7][7] = true;
        while (sp > 0) {
            int c = st[sp - 1], i = c / MN, j = c % MN;
            int[] nb = new int[4];
            int n = 0;
            if (i > 0 && !vis[i - 1][j]) nb[n++] = 0;
            if (i < MN - 1 && !vis[i + 1][j]) nb[n++] = 1;
            if (j > 0 && !vis[i][j - 1]) nb[n++] = 2;
            if (j < MN - 1 && !vis[i][j + 1]) nb[n++] = 3;
            if (n == 0) {
                sp--;
                continue;
            }
            int d = nb[(int) (k.rnd(7001, i, step++, j) * n)];
            int ni = i, nj = j;
            switch (d) {
                case 0 -> { ni = i - 1; wE[ni][j] = false; }
                case 1 -> { ni = i + 1; wE[i][j] = false; }
                case 2 -> { nj = j - 1; wS[i][nj] = false; }
                default -> { nj = j + 1; wS[i][j] = false; }
            }
            vis[ni][nj] = true;
            st[sp++] = ni * MN + nj;
        }
        for (int t = 0; t < 4; t++) {
            wE[(int) (k.rnd(7002, t, 0, 0) * (MN - 1))][(int) (k.rnd(7003, t, 0, 0) * MN)] = false;
            wS[(int) (k.rnd(7004, t, 0, 0) * MN)][(int) (k.rnd(7005, t, 0, 0) * (MN - 1))] = false;
        }
        wE[0][0] = wE[0][1] = wS[0][0] = wS[1][0] = false;
    }

    private static int walls(boolean[][] wE, boolean[][] wS, int i, int j) {
        int n = 0;
        if (i == 0 || wE[i - 1][j]) n++;
        if (i == MN - 1 || wE[i][j]) n++;
        if (j == 0 || wS[i][j - 1]) n++;
        if (j == MN - 1 ? !(i == 7) : wS[i][j]) n++;
        return n;
    }

    private void wasteStore(Kit k, P p) {
        boolean[][] wE = new boolean[MN - 1][MN], wS = new boolean[MN][MN - 1];
        maze(k, wE, wS);
        if (k.touches(WU0 - 1, WV0 - 1, WU1 + 1, WV1 + 1)) {
            k.fill(WU0 - 1, WF - 1, WV0 - 1, WU1 + 1, WC + 1, WV1 + 1, p.con);
            k.room(WU0, WF, WV0, WU1, WC, WV1, p.con, p.conTile, p.conSm);
            cols(k, WU0 + 1, WV0 + 1, WU1 - 1, WV1 - 1, (u, v) -> {
                int a = u - MU, b = v - MV;
                boolean inMaze = a >= 0 && a <= 41 && b >= 0 && b <= 41;
                boolean strip = u > MU + 41 && b >= 0 && b <= 41;
                if (strip) {
                    for (int y = WF + 1; y <= WC - 3; y++) k.set(u, y, v, y == WF + 3 || y == WF + 6 || y == WF + 9 ? p.rust : p.barrel(k, u, y, v));
                    return;
                }
                if (!inMaze) {
                    // Vorhalle
                    if (v == MV + 42 || v == WV1 - 1) k.set(u, WF, v, p.haz);
                    if (Math.floorMod(u, 6) == 0 && Math.floorMod(v, 6) == 3) k.set(u, WC, v, p.lamp);
                    return;
                }
                int ia = a / 5, ra = a % 5, jb = b / 5, rb = b % 5;
                boolean solid, post = false;
                if (ra < 2 && rb < 2) {
                    solid = !(ia == 1 && jb == 1);
                    post = true;
                } else if (ra < 2) solid = ia == 0 || ia == 8 || wE[ia - 1][jb];
                else if (rb < 2) solid = jb == 0 || (jb == 8 ? ia != 7 : wS[ia][jb - 1]);
                else solid = false;
                if (solid) {
                    for (int y = WF + 1; y <= WC - 1; y++) {
                        BlockState s;
                        if (post) s = y == WF + 1 || y == WC - 1 ? p.haz : p.steel;
                        else if (y == WF + 3 || y == WF + 6 || y == WF + 9) s = p.rust;
                        else if (y >= WF + 10) s = Kit.AIR;
                        else s = p.barrel(k, u, y, v);
                        k.set(u, y, v, s);
                    }
                    if (!post) k.set(u, WC - 1, v, p.steel);
                } else {
                    if (ra == 3 && rb == 3) k.set(u, WC, v, p.lamp);
                    boolean vault = ia <= 1 && jb <= 1;
                    if (vault) k.set(u, WF, v, ((u + v) & 1) == 0 ? p.haz : p.lead);
                    else if (!(ra == 3 && rb == 3) && k.noise(131, u, v, 3) > 0.8 && k.rnd(132, u, 0, v) < 0.7) k.set(u, WF, v, p.toxic);
                    else if (k.rnd(133, u, 0, v) < 0.03 && (ra == 2 || ra == 4) && (rb == 2 || rb == 4)) k.set(u, WF + 1, v, p.barrel(k, u, 1, v));
                    else if (k.rnd(134, u, 9, v) < 0.04) k.set(u, WF + 9, v, p.cobweb);
                }
            });
            // Endlager-Kammer
            k.fill(-85, WF + 1, 13, -85, WF + 2, 20, p.lead);
            for (int v = 13; v <= 20; v += 2) k.set(-85, WF + 3, v, p.steel);
            k.fill(-84, WF + 1, 13, -80, WF + 1, 13, p.crate);
            k.fill(-82, WF, 16, -81, WF, 17, p.glassU);
            k.set(-83, WF, 16, p.lampG);
            k.set(-80, WF, 17, p.lampG);
            // Vorhalle: Gabelstapler, Kisten, Dekon-Dusche
            k.fill(-70, WF + 1, 57, -69, WF + 2, 59, p.plateY);
            k.fill(-71, WF + 1, 57, -71, WF + 4, 57, p.bars);
            k.fill(-71, WF + 1, 59, -71, WF + 4, 59, p.bars);
            k.fill(-72, WF + 1, 57, -72, WF + 1, 59, p.rust);
            k.fill(-86, WF + 1, 62, -83, WF + 2, 64, p.crate);
            k.fill(-86, WF + 3, 63, -84, WF + 3, 64, p.crate);
            for (int u = -66; u <= -56; u += 2) k.set(u, WF + 1, 64, p.bVit);
            k.fill(-66, WF + 1, 63, -56, WF + 1, 63, Kit.slab(Blocks.SMOOTH_STONE_SLAB, false));
            k.fill(-52, WF + 1, 61, -49, WF + 4, 64, p.glassR);
            k.fill(-51, WF + 1, 62, -50, WF + 3, 64, Kit.AIR);
            k.clear(-51, WF + 1, 61, -50, WF + 2, 61);
            k.set(-51, WF + 4, 63, p.lampG);
            k.fill(-60, WF + 1, 54, -56, WF + 1, 54, p.sandbags);
        }
        {
            // Portal
            k.fill(-23, 0, 56, -23, 4, 64, p.con);
            k.fill(-23, 4, 56, -23, 4, 64, p.haz);
            k.fill(-23, 0, 57, -23, -1, 63, p.con);
            k.set(-23, 2, 60, p.trefoil);
            k.set(-23, 3, 58, p.lamp);
            k.set(-23, 3, 62, p.lamp);
            k.fill(-23, 1, 56, -23, 3, 56, p.haz);
            k.fill(-23, 1, 64, -23, 3, 64, p.haz);
        }
        // Rampe
        cols(k, WU1, 57, -16, 63, (u, v) -> {
            int t = u >= -38 ? u + 16 : WF;
            boolean tunnel = t <= -7;
            int top = tunnel ? t + 6 : 0;
            if (v == 57 || v == 63) {
                for (int y = t - 1; y <= top; y++) k.set(u, y, v, Math.floorMod(y, 5) == 0 ? p.haz : p.wallCon(k, u, y, v));
                if (!tunnel) k.set(u, 1, v, p.fenceU());
            } else {
                k.set(u, t - 1, v, p.con);
                k.set(u, t, v, u >= -38 && u <= -17 ? Kit.stairs(ModBlocks.CONCRETE_STAIRS.get(), Direction.EAST, false) : p.conTile);
                for (int y = t + 1; y < top; y++) k.air(u, y, v);
                if (tunnel) k.set(u, top, v, Math.floorMod(u, 4) == 0 && v == 60 ? p.lamp : p.conSm);
                else for (int y = t + 1; y <= 0; y++) k.air(u, y, v);
            }
        });
        // Spawner in Sackgassen
        int sp = 0;
        for (int j = 0; j < MN && sp < 3; j++) for (int i = MN - 1; i >= 0 && sp < 3; i--) {
            if (i <= 1 && j <= 1 || i == 7 && j == 7) continue;
            if (walls(wE, wS, i, j) == 3 && (i + j) % 2 == sp % 2) {
                k.spawner(MU + 5 * i + 3, WF + 1, MV + 5 * j + 3, sp == 1 ? SLUDGE : GHOUL);
                sp++;
            }
        }
        for (int j = MN - 1; j >= 0 && sp < 3; j--) for (int i = 0; i < MN && sp < 3; i++) {
            if (i <= 1 && j <= 1 || i == 7 && j == 7) continue;
            if (walls(wE, wS, i, j) == 3 && (i + j) % 2 != 0) {
                k.spawner(MU + 5 * i + 3, WF + 1, MV + 5 * j + 3, GHOUL);
                sp++;
            }
        }
        k.mob(MU + 5 * 3 + 2, WF + 1, MV + 5 * 3 + 3, GHOUL);
        k.mob(MU + 5 * 5 + 2, WF + 1, MV + 5 * 1 + 3, SLUDGE);
        k.mob(MU + 5 * 2 + 2, WF + 1, MV + 5 * 6 + 3, GHOUL);
        k.mob(-62, WF + 1, 58, GHOUL);
        k.mob(-76, WF + 1, 56, SLUDGE);
        k.elite(-81, WF + 1, 17, GHOUL);
        k.loot(-84, WF + 1, 19, Direction.EAST);
        k.barrel(-79, WF + 1, 63);
    }

    // ── Rohre, Schaltanlage, Masten, Lichter ────────────────────────────────

    private void pipeRun(Kit k, P p, double ua, double va, double ub, double vb, double y) {
        k.line(ua, y, va, ub, y, vb, 1.4, p.steel);
        double len = Math.hypot(ub - ua, vb - va);
        for (double t = 3; t < len; t += 7) {
            double f = t / len, u = ua + (ub - ua) * f, v = va + (vb - va) * f;
            double fu = (ub - ua) / len * 0.3, fv = (vb - va) / len * 0.3;
            k.line(u - fu, y, v - fv, u + fu, y, v + fv, 1.9, p.rust);
            int iu = (int) Math.round(u), iv = (int) Math.round(v);
            boolean onRoad = Math.abs(iu) <= 3 || (iv >= -9 && iv <= -3);
            if (!onRoad) {
                k.fill(iu, 1, iv, iu, (int) y - 2, iv, p.pillarY);
                k.set(iu, (int) y - 1, iv, p.steel);
            }
        }
    }

    private void pipes(Kit k, P p) {
        if (k.touches(-30, -26, 10, 5)) {
            pipeRun(k, p, 9, 0, -22, 0, 8);
            pipeRun(k, p, -22, 0, -27, -22, 8);
            pipeRun(k, p, 9, 3, -19, 3, 12);
            pipeRun(k, p, -19, 3, -25, -18, 12);
        }
    }

    private void switchyard(Kit k, P p) {
        int u0 = 24, u1 = 60, v0 = 56, v1 = 68;
        if (k.touches(u0, v0, u1, v1)) {
            cols(k, u0, v0, u1, v1, (u, v) -> {
                k.set(u, 0, v, p.gravel);
                boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
                if (edge && !(u == u0 && v >= 61 && v <= 65)) {
                    boolean postC = (u == u0 || u == u1) && (v == v0 || v == v1);
                    k.set(u, 1, v, postC ? p.pillarY : (v == v0 || v == v1) ? p.fenceU() : p.fenceV());
                    if (!postC) k.set(u, 2, v, k.rnd(141, u, 2, v) < 0.7 ? p.barbed : Kit.AIR);
                }
            });
            for (int cu = 30; cu <= 54; cu += 12) {
                k.fill(cu - 1, 1, 60, cu + 1, 3, 63, p.steel);
                k.fill(cu - 2, 1, 61, cu - 2, 3, 62, p.bars);
                k.fill(cu + 2, 1, 61, cu + 2, 3, 62, p.bars);
                k.fill(cu - 1, 4, 61, cu + 1, 4, 61, p.rust);
                for (int du = -1; du <= 1; du++) k.set(cu + du, 4, 63, p.rodUp);
                k.set(cu, 1, 64, p.trefoil);
            }
            for (int cu = 28; cu <= 56; cu += 14) {
                k.fill(cu, 1, 58, cu, 9, 58, p.steel);
                k.fill(cu, 1, 66, cu, 9, 66, p.steel);
                k.fill(cu, 9, 58, cu, 9, 66, p.rust);
            }
            for (int v : new int[] {60, 62, 64}) k.fill(28, 8, v, 56, 8, v, p.chainX);
        }
        k.mob(44, 1, 66, GHOUL);
    }

    private void mast(Kit k, P p, int cu, int cv) {
        if (!k.touches(cu - 6, cv - 6, cu + 6, cv + 6)) return;
        double[] sx = {-1, 1, 1, -1}, sz = {-1, -1, 1, 1};
        k.fill(cu - 4, 0, cv - 4, cu + 4, 0, cv + 4, p.conSm);
        for (int i = 0; i < 4; i++) {
            k.line(cu + 3 * sx[i], 1, cv + 3 * sz[i], cu + sx[i], 21, cv + sz[i], 0.3, p.steel);
            int j = (i + 1) % 4;
            for (int y = 7; y <= 21; y += 7) {
                double o = 3 - 2.0 * (y - 1) / 20, o2 = 3 - 2.0 * (y - 8) / 20;
                k.line(cu + o * sx[i], y, cv + o * sz[i], cu + o * sx[j], y, cv + o * sz[j], 0.3, p.steel);
                k.line(cu + o2 * sx[i], y - 7, cv + o2 * sz[i], cu + o * sx[j], y, cv + o * sz[j], 0.2, p.bars);
            }
        }
        k.fill(cu, 21, cv, cu, 26, cv, p.steel);
        k.fill(cu, 21, cv - 6, cu, 21, cv + 6, p.steel);
        k.fill(cu, 17, cv - 4, cu, 17, cv + 4, p.steel);
        for (int s = -1; s <= 1; s += 2) {
            k.set(cu, 20, cv + 6 * s, p.chainY);
            k.set(cu, 16, cv + 4 * s, p.chainY);
        }
        k.set(cu, 27, cv, p.lamp);
    }

    private void wire(Kit k, P p, int ua, int ub, int v, int y, double sag) {
        if (!k.touches(ua, v, ub, v)) return;
        for (int u = ua; u <= ub; u++) {
            double t = (u - ua) / (double) (ub - ua);
            k.set(u, (int) Math.round(y - sag * 4 * t * (1 - t)), v, p.chainX);
        }
    }

    private void masts(Kit k, P p) {
        int[] mu = {18, 44, 70};
        for (int m : mu) mast(k, p, m, 76);
        for (int i = 0; i < 2; i++) {
            wire(k, p, mu[i], mu[i + 1], 70, 19, 5);
            wire(k, p, mu[i], mu[i + 1], 82, 19, 5);
            wire(k, p, mu[i], mu[i + 1], 72, 15, 4);
            wire(k, p, mu[i], mu[i + 1], 80, 15, 4);
        }
        // gerissene Leitungen hinter dem letzten Mast
        if (k.touches(70, 69, 87, 83)) {
            k.line(70, 19, 70, 84, 1, 69, 0.2, p.chainY);
            k.line(70, 19, 82, 82, 1, 84, 0.2, p.chainY);
            k.line(70, 15, 72, 76, 9, 72, 0.2, p.chainY);
        }
    }

    private void lights(Kit k, P p) {
        for (int v = 84; v >= -4; v -= 16) {
            int u = Math.floorMod(v, 32) < 16 ? 4 : -4, dir = u > 0 ? -1 : 1;
            if (!k.touches(u - 2, v, u + 2, v)) continue;
            k.fill(u, 1, v, u, 5, v, p.fence[0]);
            k.set(u, 6, v, p.steel);
            k.set(u + dir, 6, v, p.steel);
            k.set(u + dir, 5, v, p.lamp);
        }
        // Warnschilder an den Wegen
        int[][] signs = {{-6, 56}, {6, 22}, {-46, -3}, {31, -14}, {-14, 63}};
        for (int[] s : signs) {
            k.set(s[0], 1, s[1], p.fence[0]);
            k.set(s[0], 2, s[1], p.trefoil);
            k.set(s[0], 3, s[1], p.haz);
        }
    }

    private void surfaceMobs(Kit k) {
        k.mob(6, 1, 50, GHOUL);
        k.mob(-20, 1, -2, GHOUL);
        k.mob(22, 1, -1, SLUDGE);
    }
}
