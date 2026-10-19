package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Laythe - "Tiefsee-Forschungsstation".
 * <pre>
 *  Oberflaeche (y 0..45): Kopfstation mit Wendeltreppe, Leuchtturm (SW), Hafenbecken mit Pier, Forschungsschiff und Kran (O)
 *  Schacht: Wendeltreppe in einer Glasroehre durch Fels und Wasser hinab in die Hauptkuppel
 *  Geflutete Riesenhoehle (y -58..-14, Wasser, dichter Felsrand), Meeresboden ~ -49 mit Kelp, Riffen, Felssaeulen
 *  Station (Deck y -40) auf Stelzen: Hauptkuppel (0|2), luftgefuellte Glasroehren zu
 *    W  Taucherschleuse  -> Schluessel 1: Tauchstrecke mit zwei Taucherglocken zum U-Boot-Wrack (Turm mit Luftblase)
 *    O  Aquarium-Labor   -> Schluessel 2: Labyrinth zwischen Glastanks auf zwei Ebenen (Leiter), Kelp-Kuppel auf dem Dach
 *    S  Drucklabor (Stahlkugel) -> Schluessel 3: Siegelwaechter (abyss_diver)
 *    N  Siegeltor im Torhaus -> Glasroehre zum Moonpool: Kuppelhalle (Boden -48, 22 hoch), Becken in der Mitte,
 *       Galerie mit zwei geschwungenen Rampen, zwei Bosstruhen.
 * </pre>
 */
public final class LaytheDungeon extends CustomDungeon {

    static final int FL = -40;                                  // Stationsdeck (Bodenblock)
    static final double CV_U = 0, CV_Y = -36, CV_V = -8, CV_RU = 78, CV_RY = 22, CV_RV = 76;
    static final int HUB_U = 0, HUB_V = 2;
    static final double HUB_R = 16, HUB_H = 15;
    static final int LOCK_U = -36, LOCK_V = 2;
    static final double LOCK_R = 9, LOCK_H = 9;
    static final int MP_U = 0, MP_V = -46, MP_FL = -48;
    static final double MP_R = 22, MP_H = 24;
    static final int PL_U = 0, PL_V = 38, PL_Y = -36;
    static final double PL_R = 12;
    static final int AQ_U0 = 25, AQ_U1 = 65, AQ_V0 = -15, AQ_V1 = 19;
    static final int MZ_U = 32, MZ_V = -14;
    static final int WRECK_U = -50, WRECK_Y = -44;
    static final int[][] BELLS = {{-42, -10}, {-45, -22}};
    static final int BELL_Y = -45;
    static final int LH_U = -42, LH_V = 42;
    static final String ANGLER = "space_beyond:deep_angler", DIVER = "space_beyond:abyss_diver";

    @Override public Stage stage() { return Stage.LAYTHE; }
    @Override public int radius() { return 88; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_SILT.get()); }
    @Override public BlockState filler() { return Kit.b(Blocks.STONE); }
    @Override public boolean fits(BiomeGenBaseCelestial biome) {
        return biome == com.space_m.dim.laythe.biome.BiomeGenBaseLaythe.laytheIsland || biome == com.space_m.dim.laythe.biome.BiomeGenBaseLaythe.laytheCoast;
    }
    @Override public int minBase() { return 66; }
    @Override public int minY() { return -62; }
    @Override public int maxY() { return 46; }
    @Override public int[] entry() { return new int[] {0, 1, 20}; }
    @Override public String boss() { return "space_beyond:abyssal_leviathan"; }

    static BlockState b(Block block) {
        return Kit.b(block);
    }

    /** Palette (erst beim Bauen aufgeloest, Registries muessen stehen). */
    static final class P {
        final BlockState air = Kit.AIR, water = b(Blocks.WATER);
        final BlockState glass = b(ModBlocks.REINFORCED_GLASS.get()), steel = b(ModBlocks.DECO_STEEL.get()), rust = b(ModBlocks.DECO_RUSTY_STEEL.get());
        final BlockState titan = b(ModBlocks.DECO_TITANIUM.get()), lead = b(ModBlocks.DECO_LEAD.get()), alu = b(ModBlocks.DECO_ALUMINUM.get());
        final BlockState light = b(ModBlocks.REINFORCED_LIGHT.get()), hazard = b(ModBlocks.CONCRETE_HAZARD.get()), concrete = b(ModBlocks.CONCRETE.get());
        final BlockState plate = b(ModBlocks.PLATEMETAL_CYAN.get()), brickC = b(ModBlocks.BRICK_CONCRETE.get());
        final BlockState pris = b(Blocks.PRISMARINE), pbrick = b(Blocks.PRISMARINE_BRICKS), dpris = b(Blocks.DARK_PRISMARINE), pwall = b(Blocks.PRISMARINE_WALL);
        final BlockState sea = b(Blocks.SEA_LANTERN), frogV = b(Blocks.VERDANT_FROGLIGHT), frogP = b(Blocks.PEARLESCENT_FROGLIGHT), frogO = b(Blocks.OCHRE_FROGLIGHT);
        final BlockState glow = b(Blocks.GLOWSTONE), shroom = b(Blocks.SHROOMLIGHT);
        final BlockState stone = b(Blocks.STONE), andes = b(Blocks.ANDESITE), tuff = b(Blocks.TUFF), deep = b(Blocks.DEEPSLATE), cdeep = b(Blocks.COBBLED_DEEPSLATE);
        final BlockState gravel = b(Blocks.GRAVEL), silt = b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_SILT.get());
        final BlockState kelp = b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_KELP.get()), sgrass = b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_SHORT.get());
        final BlockState glowgrass = b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_GLOW.get()), root = b(com.space_m.dim.laythe.LaytheBlocks.LAYTHE_ROOT.get());
        final BlockState vgrass = b(Blocks.SEAGRASS), pickle = b(Blocks.SEA_PICKLE);
        final BlockState[] coral = {b(Blocks.TUBE_CORAL_BLOCK), b(Blocks.BRAIN_CORAL_BLOCK), b(Blocks.BUBBLE_CORAL_BLOCK), b(Blocks.FIRE_CORAL_BLOCK), b(Blocks.HORN_CORAL_BLOCK)};
        final BlockState sbrick = b(Blocks.STONE_BRICKS), mbrick = b(Blocks.MOSSY_STONE_BRICKS), pand = b(Blocks.POLISHED_ANDESITE), smooth = b(Blocks.SMOOTH_STONE);
        final BlockState white = b(Blocks.WHITE_CONCRETE), red = b(Blocks.RED_CONCRETE), yellow = b(Blocks.YELLOW_CONCRETE), orange = b(Blocks.ORANGE_CONCRETE);
        final BlockState blue = b(Blocks.BLUE_CONCRETE), green = b(Blocks.GREEN_CONCRETE), black = b(Blocks.BLACK_CONCRETE), gray = b(Blocks.GRAY_CONCRETE), cyan = b(Blocks.CYAN_CONCRETE);
        final BlockState planks = b(Blocks.SPRUCE_PLANKS), log = b(Blocks.SPRUCE_LOG), fence = b(Blocks.SPRUCE_FENCE), bars = b(Blocks.IRON_BARS), chain = b(Blocks.CHAIN);
        final BlockState iron = b(Blocks.IRON_BLOCK), copper = b(Blocks.WEATHERED_COPPER), tinted = b(Blocks.TINTED_GLASS), barrel = b(Blocks.BARREL), rod = b(Blocks.LIGHTNING_ROD);
        final BlockState lantern = Kit.lantern(Blocks.LANTERN, false), hang = Kit.lantern(Blocks.LANTERN, true), path = b(Blocks.DIRT_PATH), pdeep = b(Blocks.POLISHED_DEEPSLATE);
        final BlockState cyanGlass = b(Blocks.LIGHT_BLUE_STAINED_GLASS), terra = b(Blocks.CYAN_TERRACOTTA);
    }

    // ── Hilfen ──

    interface Mat {
        BlockState at(int u, int y, int v);
    }

    /** Linie (Kugelkette) mit Material je Position. */
    static void lineM(Kit k, double ua, double ya, double va, double ub, double yb, double vb, double r, Mat m) {
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

    static int seabed(Kit k, int u, int v) {
        return -51 + (int) Math.floor(3.5 * k.noise(11, u, v, 13) + 2.0 * k.noise(12, u, v, 6));
    }

    static BlockState rock(Kit k, P p, int u, int y, int v) {
        double n = k.noise(15, u + y * 7, v - y * 5, 7);
        if (y < -55) return n < 0.5 ? p.deep : p.cdeep;
        return n < 0.22 ? p.andes : n > 0.8 ? p.tuff : p.stone;
    }

    /** Bereiche der Station (kein Kelp darunter). */
    static boolean stationZone(int u, int v) {
        if (sq(u - HUB_U, v - HUB_V) < 21 * 21) return true;
        if (sq(u - LOCK_U, v - LOCK_V) < 13 * 13) return true;
        if (sq(u - MP_U, v - MP_V) < 25 * 25) return true;
        if (sq(u - PL_U, v - PL_V) < 15 * 15) return true;
        if (u >= AQ_U0 - 3 && u <= AQ_U1 + 3 && v >= AQ_V0 - 3 && v <= AQ_V1 + 3) return true;
        if (Math.abs(v - 2) <= 5 && u >= -32 && u <= 30) return true;
        if (Math.abs(u) <= 5 && v >= -30 && v <= 30) return true;
        if (u >= WRECK_U - 7 && u <= WRECK_U + 7 && v >= -54 && v <= -22) return true;
        for (int[] bl : BELLS) if (sq(u - bl[0], v - bl[1]) < 36) return true;
        return false;
    }

    static int sq(int a, int b) {
        return a * a + b * b;
    }

    // ── Bau ──

    @Override
    protected void build(Kit k) {
        P p = new P();
        cavern(k, p);
        spires(k, p);
        surfaceWorks(k, p);
        lighthouse(k, p);
        harbor(k, p);
        stilts(k, p);
        // Roehren (Huelle), dann Kuppeln
        tube(k, p, true, 2, -31, -12, false);
        tube(k, p, true, 2, 12, 28, false);
        tube(k, p, false, 0, 14, 31, false);
        tube(k, p, false, 0, -32, -18, false);
        hub(k, p);
        airlock(k, p);
        moonpoolShell(k, p);
        aquarium(k, p);
        pressureLab(k, p);
        // Roehren innen freilegen
        tube(k, p, true, 2, -31, -12, true);
        tube(k, p, true, 2, 12, 28, true);
        tube(k, p, false, 0, 14, 31, true);
        tube(k, p, false, 0, -32, -18, true);
        shaft(k, p);
        gate(k, p);
        hubInterior(k, p);
        airlockInterior(k, p);
        moonpoolInterior(k, p);
        pressureInterior(k, p);
        bells(k, p);
        wreck(k, p);
        guideLine(k, p);
        plan(k, p);
    }

    // ── Hoehle ──

    private void cavern(Kit k, P p) {
        int uLo = Math.max(k.u0(), -82), uHi = Math.min(k.u1(), 82), vLo = Math.max(k.v0(), (int) CV_V - 80), vHi = Math.min(k.v1(), (int) CV_V + 80);
        for (int u = uLo; u <= uHi; u++) for (int v = vLo; v <= vHi; v++) {
            double ou = (u - CV_U) / (CV_RU + 3), ov = (v - CV_V) / (CV_RV + 3), so = ou * ou + ov * ov;
            if (so >= 1) continue;
            double ho = (CV_RY + 3) * Math.sqrt(1 - so);
            int yTop = (int) Math.floor(CV_Y + ho), yBot = Math.max(-61, (int) Math.ceil(CV_Y - ho));
            double iu = (u - CV_U) / CV_RU, iv = (v - CV_V) / CV_RV, si = iu * iu + iv * iv;
            int wTop = Integer.MIN_VALUE, wBot = Integer.MAX_VALUE, sb = Integer.MIN_VALUE;
            if (si < 1) {
                double hi = CV_RY * Math.sqrt(1 - si);
                double drip = k.rnd(14, u, 0, v) < 0.05 ? 1 + 3 * k.rnd(17, u, 0, v) : 0;
                wTop = (int) Math.floor(CV_Y + hi - 3 * k.noise(13, u, v, 9) - drip);
                wBot = (int) Math.ceil(CV_Y - hi);
                sb = Math.max(wBot, seabed(k, u, v));
            }
            boolean reef = k.noise(16, u, v, 10) > 0.74;
            for (int y = yBot; y <= yTop; y++) {
                BlockState s;
                if (y > wTop || y < wBot) s = rock(k, p, u, y, v);
                else if (y <= sb) {
                    if (y == sb) s = reef ? p.coral[(int) (k.rnd(18, u, 0, v) * 5)] : k.rnd(19, u, 0, v) < 0.15 ? p.gravel : p.silt;
                    else s = y >= sb - 2 ? p.silt : rock(k, p, u, y, v);
                } else s = p.water;
                k.set(u, y, v, s);
            }
            if (si >= 1 || sb + 4 > wTop) continue;
            // Bewuchs
            if (reef) {
                double r = k.rnd(20, u, 0, v);
                if (r < 0.3) k.set(u, sb + 1, v, p.coral[(int) (k.rnd(21, u, 1, v) * 5)]);
                else if (r < 0.45) k.set(u, sb + 1, v, p.pickle);
                else if (r < 0.6) k.set(u, sb + 1, v, p.vgrass);
                continue;
            }
            if (stationZone(u, v)) {
                if (k.rnd(23, u, 0, v) < 0.12) k.set(u, sb + 1, v, p.sgrass);
                continue;
            }
            if (k.noise(22, u, v, 11) > 0.58 && k.rnd(24, u, 0, v) < 0.45) {
                int h = Math.min(3 + (int) (k.rnd(25, u, 0, v) * 11), wTop - sb - 3);
                for (int y = sb + 1; y <= sb + h; y++) k.set(u, y, v, p.kelp);
            } else {
                double r = k.rnd(26, u, 0, v);
                if (r < 0.10) k.set(u, sb + 1, v, p.sgrass);
                else if (r < 0.16) k.set(u, sb + 1, v, p.vgrass);
                else if (r < 0.185) k.set(u, sb + 1, v, p.glowgrass);
                else if (r < 0.19) { k.set(u, sb + 1, v, rock(k, p, u, sb + 1, v)); k.set(u, sb + 2, v, p.pickle); }
            }
        }
    }

    /** Felssaeulen vom Grund bis zur Decke (Sanduhrform), unten mit Korallen bewachsen. */
    private void spires(Kit k, P p) {
        double[][] sp = {{-64, 28, 4}, {50, -42, 4.5}, {-26, -60, 3.5}, {36, 50, 4}, {-38, 46, 3.5}, {62, 32, 3}, {-62, -20, 3.5}, {24, -62, 3}};
        for (double[] s : sp) {
            int ir = (int) Math.ceil(s[2] * 1.8) + 1;
            int cu = (int) s[0], cv = (int) s[1];
            if (!k.touches(cu - ir, cv - ir, cu + ir, cv + ir)) continue;
            for (int u = Math.max(cu - ir, k.u0()); u <= Math.min(cu + ir, k.u1()); u++) for (int v = Math.max(cv - ir, k.v0()); v <= Math.min(cv + ir, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                for (int y = -58; y <= -12; y++) {
                    double t = (y + 36) / 20.0;
                    double r = s[2] * (0.75 + 0.75 * t * t) + 0.8 * (k.noise(30, u + y * 3, v, 4) - 0.5);
                    if (d > r) continue;
                    double eu = u / (CV_RU + 3), ey = (y - CV_Y) / (CV_RY + 3), ev = (v - CV_V) / (CV_RV + 3);
                    if (eu * eu + ey * ey + ev * ev > 1) continue;
                    BlockState m = rock(k, p, u, y, v);
                    if (y < -46 && d > r - 1.2 && k.rnd(31, u, y, v) < 0.35) m = p.coral[(int) (k.rnd(32, u, y, v) * 5)];
                    k.set(u, y, v, m);
                }
            }
        }
    }

    // ── Oberflaeche ──

    private void surfaceWorks(Kit k, P p) {
        int cu = HUB_U, cv = HUB_V;
        // Kopfstation: rundes Gebaeude r 10 mit Glaskuppel
        if (k.touches(cu - 12, cv - 12, cu + 12, cv + 14)) {
            for (int u = Math.max(cu - 12, k.u0()); u <= Math.min(cu + 12, k.u1()); u++) for (int v = Math.max(cv - 12, k.v0()); v <= Math.min(cv + 14, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                double ang = Math.toDegrees(Math.atan2(v - cv, u - cu));
                if (d <= 11.5) k.set(u, 0, v, d > 10.5 ? p.dpris : (((int) Math.floor(d)) % 3 == 0 ? p.pbrick : p.pris));
                if (d > 9 && d <= 10.2) {
                    boolean pil = Math.abs(((ang + 360) % 45) - 22.5) > 19;
                    for (int y = 1; y <= 7; y++) {
                        BlockState s = pil ? p.pbrick : y == 1 || y == 7 ? p.dpris : (y == 3 || y == 4 || y == 5) ? p.glass : p.white;
                        k.set(u, y, v, s);
                    }
                    k.set(u, 8, v, p.dpris);
                } else if (d <= 9) {
                    for (int y = 1; y <= 7; y++) k.air(u, y, v);
                }
                if (d > 10.2 && d <= 11.5) k.set(u, 1, v, Kit.slab(Blocks.PRISMARINE_BRICK_SLAB, false));
            }
            // Dach: Glaskuppel ueber Deckenring
            k.shellCut(cu, 8, cv, 9, 6, 9, 1.2, p.glass, Kit.AIR, 8, 14);
            for (int y = 8; y <= 14; y++) {
                for (int a = 0; a < 8; a++) {
                    double rr = 9 * Math.sqrt(Math.max(0, 1 - Math.pow((y - 8) / 6.0, 2))) - 0.5;
                    int u = cu + (int) Math.round(Math.cos(Math.PI * a / 4) * rr), v = cv + (int) Math.round(Math.sin(Math.PI * a / 4) * rr);
                    if (rr > 0.3) k.set(u, y, v, p.steel);
                }
            }
            for (int u = Math.max(cu - 9, k.u0()); u <= Math.min(cu + 9, k.u1()); u++) for (int v = Math.max(cv - 9, k.v0()); v <= Math.min(cv + 9, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                if (d > 7.3 && d <= 9.0) k.set(u, 8, v, p.dpris);
            }
            k.set(cu, 14, cv, p.light);
            k.set(cu, 15, cv, p.steel);
            k.set(cu, 16, cv, p.rod);
            // Tuer nach Sueden
            k.clear(cu - 1, 1, cv + 8, cu + 1, 3, cv + 11);
            for (int y = 1; y <= 4; y++) { k.set(cu - 2, y, cv + 10, p.dpris); k.set(cu + 2, y, cv + 10, p.dpris); }
            k.fill(cu - 2, 4, cv + 10, cu + 2, 4, cv + 10, p.dpris);
            k.set(cu, 4, cv + 11, p.light);
            k.fill(cu - 1, 0, cv + 9, cu + 1, 0, cv + 12, p.pbrick);
            // Innen: Gelaender um den Schacht (Luecke am oberen Treppenende ~ 30 Grad), Kisten, Leuchten
            for (int u = Math.max(cu - 7, k.u0()); u <= Math.min(cu + 7, k.u1()); u++) for (int v = Math.max(cv - 7, k.v0()); v <= Math.min(cv + 7, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                double ang = (Math.toDegrees(Math.atan2(v - cv, u - cu)) + 360) % 360;
                if (d > 5.4 && d <= 6.3 && !(ang > 350 || ang < 70)) k.set(u, 1, v, p.bars);
            }
            for (int a = 0; a < 8; a++) {
                int u = cu + (int) Math.round(Math.cos(Math.PI * a / 4 + 0.39) * 8.5), v = cv + (int) Math.round(Math.sin(Math.PI * a / 4 + 0.39) * 8.5);
                k.set(u, 7, v, p.hang);
            }
            k.set(cu - 7, 1, cv - 4, p.barrel); k.set(cu - 6, 1, cv - 6, p.barrel); k.set(cu - 7, 2, cv - 4, p.barrel);
            k.set(cu + 6, 1, cv + 5, b(ModBlocks.BARREL_STEEL.get()));
            k.set(cu - 6, 1, cv + 5, Kit.facing(Blocks.LECTERN, Direction.EAST));
        }
        // Wege
        path(k, p, 0, 13, 0, 24);
        path(k, p, 10, 2, 30, 2);
        path(k, p, -7, 9, LH_U + 6, LH_V - 6);
        // Solarfeld und Lagerschuppen
        if (k.touches(-34, -26, -12, -8)) {
            for (int u = -32; u <= -14; u += 3) for (int v = -24; v <= -12; v += 4) {
                k.set(u, 1, v, p.bars);
                for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) k.set(u + du, 2, v + dv, b(Blocks.DAYLIGHT_DETECTOR));
            }
        }
        if (k.touches(14, 14, 26, 24)) {
            k.room(15, 0, 15, 25, 6, 23, p.white, p.smooth, p.gray);
            k.fill(15, 6, 15, 25, 6, 23, p.gray);
            for (int u = 15; u <= 25; u += 5) for (int y = 1; y <= 5; y++) { k.set(u, y, 15, p.steel); k.set(u, y, 23, p.steel); }
            k.fill(16, 3, 15, 24, 4, 15, p.glass);
            k.clear(19, 1, 15, 21, 4, 15);
            k.set(17, 1, 21, p.barrel); k.set(18, 1, 21, p.barrel); k.set(17, 2, 21, p.barrel);
            k.set(23, 1, 21, b(ModBlocks.BARREL_STEEL.get())); k.set(23, 1, 20, b(ModBlocks.BARREL_IRON.get()));
            k.set(20, 5, 19, p.hang);
        }
        // Funkmast
        int mu = 14, mv = -10;
        if (k.touches(mu - 2, mv - 2, mu + 2, mv + 2)) {
            k.fill(mu - 1, 0, mv - 1, mu + 1, 1, mv + 1, p.concrete);
            for (int y = 2; y <= 28; y++) k.set(mu, y, mv, y % 6 == 0 ? p.steel : p.bars);
            for (int y = 6; y <= 24; y += 6) { k.set(mu + 1, y, mv, p.bars); k.set(mu - 1, y, mv, p.bars); k.set(mu, y, mv + 1, p.bars); k.set(mu, y, mv - 1, p.bars); }
            k.set(mu, 29, mv, p.frogO);
            k.set(mu, 30, mv, p.rod);
        }
    }

    private void path(Kit k, P p, int ua, int va, int ub, int vb) {
        int n = Math.max(Math.abs(ub - ua), Math.abs(vb - va));
        if (!k.touches(ua - 2, va - 2, ub + 2, vb + 2)) return;
        for (int i = 0; i <= n; i++) {
            double t = i / (double) n;
            int u = (int) Math.round(ua + (ub - ua) * t), v = (int) Math.round(va + (vb - va) * t);
            for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
                if (!k.col(u + du, v + dv)) continue;
                double r = k.rnd(40, u + du, 0, v + dv);
                k.set(u + du, 0, v + dv, r < 0.5 ? p.sbrick : r < 0.75 ? p.pand : r < 0.9 ? p.mbrick : p.gravel);
            }
        }
    }

    private void lighthouse(Kit k, P p) {
        int cu = LH_U, cv = LH_V;
        if (!k.touches(cu - 10, cv - 10, cu + 10, cv + 10)) return;
        for (int u = Math.max(cu - 10, k.u0()); u <= Math.min(cu + 10, k.u1()); u++) for (int v = Math.max(cv - 10, k.v0()); v <= Math.min(cv + 10, k.v1()); v++) {
            double d = Math.sqrt(sq(u - cu, v - cv));
            double ang = (Math.toDegrees(Math.atan2(v - cv, u - cu)) + 360) % 360;
            if (d <= 9.5) k.set(u, 0, v, k.rnd(41, u, 0, v) < 0.3 ? p.mbrick : p.sbrick);
            if (d > 7.5 && d <= 8.5) k.set(u, 1, v, Kit.slab(Blocks.STONE_BRICK_SLAB, false));
            // Sockel
            if (d > 5.5 && d <= 7.0) for (int y = 1; y <= 4; y++) k.set(u, y, v, y == 4 ? p.dpris : p.sbrick);
            // Turmschaft
            if (d > 4.4 && d <= 5.5) {
                for (int y = 1; y <= 33; y++) {
                    BlockState s = ((y - 1) / 4) % 2 == 0 ? p.white : p.red;
                    if (y % 8 == 4 && Math.abs((ang % 90) - 45) > 38) s = p.glass;
                    k.set(u, y, v, s);
                }
            }
            if (d <= 4.4) for (int y = 1; y <= 33; y++) k.air(u, y, v);
            if (d <= 1.2) for (int y = 1; y <= 33; y++) k.set(u, y, v, y % 8 == 0 ? p.light : p.sbrick);
            // Auskragung und Galerie
            if (d > 5.5 && d <= 6.5) k.set(u, 32, v, Kit.stairs(Blocks.DARK_PRISMARINE_STAIRS, dirTo(u - cu, v - cv), true));
            if (d > 5.5 && d <= 7.5) k.set(u, 33, v, p.dpris);
            if (d <= 7.5) k.set(u, 34, v, d > 6.5 ? p.dpris : p.steel);
            if (d > 6.5 && d <= 7.5) k.set(u, 35, v, p.bars);
            // Laternenraum
            if (d > 2.5 && d <= 3.5) for (int y = 35; y <= 39; y++) k.set(u, y, v, Math.abs((ang % 90) - 45) > 40 ? p.steel : p.glass);
            if (d <= 2.5) for (int y = 35; y <= 39; y++) k.air(u, y, v);
            // Dach (Kegel)
            for (int y = 40; y <= 44; y++) {
                double rr = 4.6 - (y - 40) * 1.05;
                if (d <= rr) k.set(u, y, v, d > rr - 1.1 ? p.dpris : p.steel);
            }
        }
        k.fill(cu, 35, cv, cu, 36, cv, p.light);
        k.set(cu, 37, cv, p.sea);
        k.set(cu, 38, cv, p.light);
        k.set(cu, 45, cv, p.rod);
        // Tuer nach Osten (zum Weg)
        k.clear(cu + 4, 1, cv - 1, cu + 7, 3, cv + 1);
        k.fill(cu + 4, 0, cv - 1, cu + 8, 0, cv + 1, p.pbrick);
        k.set(cu + 7, 4, cv, p.light);
        // Wendeltreppe, startet im Osten auf Bodenhoehe
        k.spiral(cu, cv, 0, 33, 2.0, 4.2, 1.3, 0, 3, p.dpris);
    }

    static Direction dirTo(int du, int dv) {
        if (Math.abs(du) >= Math.abs(dv)) return du >= 0 ? Direction.WEST : Direction.EAST;
        return dv >= 0 ? Direction.NORTH : Direction.SOUTH;
    }

    static boolean inBasin(int u, int v) {
        if (u < 38 || u > 80 || v < -30 || v > 28) return false;
        int cu = Math.max(48, Math.min(70, u)), cv = Math.max(-20, Math.min(18, v));
        return sq(u - cu, v - cv) <= 100;
    }

    private void harbor(Kit k, P p) {
        if (k.touches(26, -34, 84, 32)) {
            for (int u = Math.max(26, k.u0()); u <= Math.min(84, k.u1()); u++) for (int v = Math.max(-34, k.v0()); v <= Math.min(32, k.v1()); v++) {
                boolean in = inBasin(u, v);
                if (in) {
                    k.set(u, -5, v, k.rnd(42, u, 0, v) < 0.3 ? p.gravel : p.silt);
                    for (int y = -4; y <= 0; y++) k.set(u, y, v, p.water);
                    if (k.rnd(43, u, 0, v) < 0.08) k.set(u, -4, v, p.sgrass);
                    continue;
                }
                boolean quay = false;
                for (int du = -2; du <= 2 && !quay; du++) for (int dv = -2; dv <= 2; dv++) if (inBasin(u + du, v + dv)) { quay = true; break; }
                if (quay) {
                    for (int y = -6; y <= -1; y++) k.set(u, y, v, p.sbrick);
                    k.set(u, 0, v, u < 40 ? p.smooth : p.dpris);
                } else if (u <= 37 && u >= 26 && v >= -32 && v <= 30) {
                    k.set(u, 0, v, (u + v) % 4 == 0 ? p.pand : p.smooth);
                }
            }
            // Poller und Lampen an der Kaimauer
            for (int v = -26; v <= 24; v += 8) {
                k.set(36, 1, v, p.iron);
                if ((v / 8) % 2 == 0) { k.set(35, 1, v + 2, p.fence); k.set(35, 2, v + 2, p.fence); k.set(35, 3, v + 2, p.lantern); }
            }
        }
        // Pier mit T-Kopf
        if (k.touches(36, -10, 74, 10)) {
            for (int u = 36; u <= 72; u++) for (int v = -2; v <= 2; v++) k.set(u, 0, v, (u % 4 == 0) ? Kit.axis(Blocks.SPRUCE_LOG, Direction.Axis.Z) : p.planks);
            for (int u = 66; u <= 72; u++) for (int v = -9; v <= 9; v++) k.set(u, 0, v, u == 69 ? Kit.axis(Blocks.SPRUCE_LOG, Direction.Axis.Z) : p.planks);
            for (int u = 40; u <= 72; u += 4) for (int s = -3; s <= 3; s += 6) {
                for (int y = -4; y <= 1; y++) k.set(u, y, s, p.log);
                if (u % 8 == 0) k.set(u, 2, s, p.lantern);
            }
            for (int v = -9; v <= 9; v += 6) { for (int y = -4; y <= 1; y++) k.set(73, y, v, p.log); k.set(73, 2, v, p.lantern); }
        }
        // Forschungsschiff
        if (k.touches(44, 4, 68, 13)) {
            for (int u = 46; u <= 66; u++) {
                double hw = 3.2 * Math.min(1.0, Math.min((u - 45) / 3.0, (67 - u) / 7.0));
                for (int v = 4; v <= 12; v++) {
                    double dw = Math.abs(v - 8);
                    if (dw > hw) continue;
                    boolean edge = dw > hw - 1;
                    k.set(u, -2, v, p.black);
                    k.set(u, -1, v, p.red);
                    k.set(u, 0, v, p.white);
                    k.set(u, 1, v, edge ? p.white : p.planks);
                    if (edge) k.set(u, 2, v, Kit.slab(Blocks.SMOOTH_STONE_SLAB, false));
                }
            }
            // Brueckenhaus
            k.room(52, 1, 6, 58, 6, 10, p.white, p.planks, p.white);
            k.fill(52, 4, 6, 58, 4, 10, p.glass);
            k.clear(53, 2, 7, 57, 5, 9);
            k.clear(55, 2, 6, 55, 3, 6);
            // Landungsbruecke vom Pier
            k.fill(54, 0, 3, 56, 0, 4, p.planks);
            k.clear(54, 2, 5, 56, 2, 5);
            k.set(53, 1, 3, p.fence); k.set(57, 1, 3, p.fence);
            k.fill(52, 6, 6, 58, 6, 10, p.gray);
            for (int y = 7; y <= 13; y++) k.set(55, y, 8, p.bars);
            k.fill(54, 13, 8, 56, 13, 8, p.steel);
            k.set(55, 14, 8, p.frogO);
            // A-Rahmen am Heck mit Tauchboot
            for (int y = 2; y <= 7; y++) { k.set(47, y, 5, p.yellow); k.set(47, y, 11, p.yellow); }
            k.fill(47, 8, 5, 47, 8, 11, p.yellow);
            k.ellipsoid(50, 3.5, 8, 2.2, 1.6, 1.6, p.orange);
            k.set(51, 4, 8, p.glass);
            k.set(52, 3, 8, p.glass);
        }
        // Bojen
        for (int[] bo : new int[][] {{76, -24}, {76, 22}, {58, -26}}) {
            k.set(bo[0], 0, bo[1], p.red);
            k.set(bo[0], 1, bo[1], p.white);
            k.set(bo[0], 2, bo[1], p.lantern);
        }
        // Hafenkran
        int cu = 32, cv = -16;
        if (k.touches(18, cv - 4, 68, cv + 4)) {
            k.fill(cu - 2, 1, cv - 2, cu + 2, 1, cv + 2, p.concrete);
            for (int y = 2; y <= 21; y++) for (int du = -1; du <= 1; du += 2) for (int dv = -1; dv <= 1; dv += 2) k.set(cu + du, y, cv + dv, p.yellow);
            for (int y = 4; y <= 20; y += 4) {
                k.set(cu, y, cv - 1, p.bars); k.set(cu, y, cv + 1, p.bars); k.set(cu - 1, y, cv, p.bars); k.set(cu + 1, y, cv, p.bars);
            }
            k.room(cu - 2, 21, cv - 2, cu + 2, 25, cv + 2, p.yellow, p.steel, p.yellow);
            k.fill(cu - 2, 23, cv - 2, cu + 2, 23, cv + 2, p.glass);
            k.clear(cu - 1, 22, cv - 1, cu + 1, 24, cv + 1);
            Mat truss = (u, y, v) -> (u % 3 == 0) ? p.yellow : p.orange;
            lineM(k, cu + 2, 24, cv - 1, 66, 27, cv - 1, 0.45, truss);
            lineM(k, cu + 2, 24, cv + 1, 66, 27, cv + 1, 0.45, truss);
            for (int u = cu + 4; u <= 66; u += 3) { int y = 24 + (int) Math.round((u - cu - 2) * 3.0 / (64 - cu)); k.set(u, y, cv, p.bars); }
            lineM(k, cu - 2, 24, cv, 22, 24, cv, 0.5, (u, y, v) -> p.yellow);
            k.fill(19, 21, cv - 1, 23, 24, cv + 1, p.concrete);
            k.fill(cu - 1, 26, cv, cu - 1, 29, cv, p.yellow);
            lineM(k, cu - 1, 29, cv, 50, 26, cv, 0.3, (u, y, v) -> p.chain);
            for (int y = 9; y <= 26; y++) k.set(64, y, cv, p.chain);
            k.fill(62, 4, cv - 1, 66, 8, cv + 1, p.blue);
            k.fill(62, 4, cv - 1, 66, 4, cv + 1, p.steel);
            k.fill(62, 8, cv - 1, 66, 8, cv + 1, p.steel);
        }
        // Containerstapel am Kai
        if (k.touches(27, 8, 36, 26)) {
            k.fill(28, 1, 10, 30, 3, 17, p.orange); k.fill(28, 2, 10, 30, 2, 17, b(Blocks.BROWN_CONCRETE));
            k.fill(32, 1, 10, 34, 3, 17, p.blue);
            k.fill(28, 4, 11, 30, 6, 18, p.green);
            k.fill(28, 1, 20, 30, 3, 26, p.white); k.fill(28, 1, 19, 30, 3, 19, p.red);
            k.fill(32, 1, 20, 34, 3, 24, p.cyan);
        }
    }

    // ── Stelzen und Decks ──

    private void stilts(Kit k, P p) {
        // Hauptkuppel
        k.cylinder(HUB_U, HUB_V, FL - 2, FL, 17.4, p.dpris);
        for (int i = 0; i < 4; i++) k.cylinder(HUB_U, HUB_V, FL - 3 - i, FL - 3 - i, 13 - 3 * i, i % 2 == 0 ? p.steel : p.dpris);
        for (int a = 0; a < 8; a++) pillar(k, p, HUB_U + (int) Math.round(Math.cos(Math.PI * a / 4) * 14), HUB_V + (int) Math.round(Math.sin(Math.PI * a / 4) * 14), FL - 3);
        // Schleuse
        k.cylinder(LOCK_U, LOCK_V, FL - 2, FL, 10.4, p.dpris);
        for (int[] s : new int[][] {{5, 6}, {-6, 6}, {5, -6}, {-6, -6}}) pillar(k, p, LOCK_U + s[0], LOCK_V + s[1], FL - 3);
        // Roehren
        for (int u = -28; u <= -16; u += 6) pillar(k, p, u, 2, FL - 3);
        for (int u = 18; u <= 24; u += 6) pillar(k, p, u, 2, FL - 3);
        for (int v = 20; v <= 26; v += 6) pillar(k, p, 0, v, FL - 3);
        // Labor
        for (int u = AQ_U0 + 2; u <= AQ_U1; u += 8) for (int v = AQ_V0 + 2; v <= AQ_V1; v += 8) pillar(k, p, u, v, FL - 3);
        // Drucklabor-Beine
        for (int[] s : new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}) {
            lineM(k, PL_U + 5 * s[0], PL_Y - 9, PL_V + 5 * s[1], PL_U + 9 * s[0], -50, PL_V + 9 * s[1], 0.8, (u, y, v) -> p.steel);
            k.fill(PL_U + 9 * s[0] - 1, -51, PL_V + 9 * s[1] - 1, PL_U + 9 * s[0] + 1, -50, PL_V + 9 * s[1] + 1, p.dpris);
        }
    }

    private void pillar(Kit k, P p, int u, int v, int yTop) {
        if (!k.touches(u - 1, v - 1, u + 1, v + 1)) return;
        int sb = seabed(k, u, v);
        k.fill(u - 1, sb - 1, v - 1, u + 1, sb, v + 1, p.dpris);
        for (int y = sb + 1; y <= yTop; y++) k.set(u, y, v, (yTop - y) % 7 == 3 ? p.light : p.steel);
    }

    // ── Roehren ──

    /** Glasroehre auf Deckhoehe (Querschnitt: 5 breit, 4-5 hoch). carve = nur Boden/Luft. */
    private void tube(Kit k, P p, boolean alongU, int fixed, int a, int bb, boolean carve) {
        int lo = Math.min(a, bb), hi = Math.max(a, bb);
        int uA = alongU ? lo : fixed - 4, uB = alongU ? hi : fixed + 4, vA = alongU ? fixed - 4 : lo, vB = alongU ? fixed + 4 : hi;
        if (!k.touches(uA, vA, uB, vB)) return;
        for (int t = lo; t <= hi; t++) for (int w = -4; w <= 4; w++) {
            int u = alongU ? t : fixed + w, v = alongU ? fixed + w : t;
            if (!k.col(u, v)) continue;
            boolean rib = Math.floorMod(t, 6) == 0;
            for (int y = FL - 3; y <= FL + 6; y++) {
                double dy = y - (FL + 2.5), r2 = w * w + dy * dy;
                int c;
                if (y == FL && Math.abs(w) <= 2) c = 2;
                else if (y > FL && r2 <= 6.76) c = 3;
                else if (r2 <= 14.44) c = 1;
                else if ((y == FL - 1 || y == FL - 2) && Math.abs(w) <= 1) c = 4;
                else continue;
                if (carve && c != 2 && c != 3) continue;
                BlockState s;
                if (c == 3) s = Kit.AIR;
                else if (c == 2) s = Math.abs(w) == 2 ? p.pbrick : (w == 0 && Math.floorMod(t, 6) == 3) ? p.light : p.dpris;
                else if (c == 4) s = p.steel;
                else s = y <= FL ? p.steel : rib ? p.steel : p.glass;
                k.set(u, y, v, s);
            }
        }
    }

    // ── Kuppeln ──

    /**
     * Kuppel (Ellipsoid oberhalb yBase): Glas mit Stahlrippen, Sockel; innen Luft. ribs = Anzahl Meridianrippen.
     */
    private void dome(Kit k, P p, double cu, double cv, int yBase, double r, double h, double thick, int ribs, BlockState plinth) {
        int ir = (int) Math.ceil(r) + 1;
        if (!k.touches((int) cu - ir, (int) cv - ir, (int) cu + ir, (int) cv + ir)) return;
        for (int u = Math.max((int) cu - ir, k.u0()); u <= Math.min((int) cu + ir, k.u1()); u++) for (int v = Math.max((int) cv - ir, k.v0()); v <= Math.min((int) cv + ir, k.v1()); v++) {
            double du = u - cu, dv = v - cv, dh = Math.sqrt(du * du + dv * dv);
            double ang = Math.atan2(dv, du), step = 2 * Math.PI / ribs;
            double dA = ang - Math.round(ang / step) * step;
            boolean rib = Math.abs(dA) * dh < 0.6 && dh > 1.5;
            for (int y = yBase + 1; y <= yBase + (int) Math.ceil(h) + 1; y++) {
                double ny = (y - yBase) / h, d = Math.sqrt(du * du / (r * r) + ny * ny + dv * dv / (r * r));
                if (d > 1.0) continue;
                double depth = (1 - d) * Math.min(r, h);
                if (depth >= thick) { k.air(u, y, v); continue; }
                BlockState s;
                if (y <= yBase + 2) s = plinth;
                else if (dh < 2.6) s = p.steel;
                else if (rib) s = p.steel;
                else if (Math.abs(ny - 0.5) < 0.5 / h) s = p.steel;
                else s = p.glass;
                k.set(u, y, v, s);
            }
        }
    }

    private void hub(Kit k, P p) {
        dome(k, p, HUB_U, HUB_V, FL, HUB_R, HUB_H, 1.5, 12, p.pbrick);
    }

    private void airlock(Kit k, P p) {
        dome(k, p, LOCK_U, LOCK_V, FL, LOCK_R, LOCK_H, 1.5, 8, p.dpris);
    }

    private void moonpoolShell(Kit k, P p) {
        // Fundament und Bodenplatte
        k.cylinder(MP_U, MP_V, -56, MP_FL, 22.6, p.dpris);
        dome(k, p, MP_U, MP_V, MP_FL, MP_R, MP_H, 1.5, 16, p.dpris);
    }

    // ── Aquarium-Labor (Schluessel 2) ──

    /** Labyrinth (5x5 Zellen): Durchgaenge [0] Ost von (i,j), [1] Sued von (i,j). */
    private boolean[][][] maze(Kit k, long salt) {
        int n = 5;
        boolean[][] e = new boolean[n][n], s = new boolean[n][n], vis = new boolean[n][n];
        int[] stack = new int[n * n];
        int sp = 0, step = 0;
        int start = (int) (k.rnd(salt, 7, 7, 7) * n * n);
        stack[sp++] = start;
        vis[start % n][start / n] = true;
        int[] opts = new int[4];
        while (sp > 0) {
            int c = stack[sp - 1], i = c % n, j = c / n, m = 0;
            if (i + 1 < n && !vis[i + 1][j]) opts[m++] = 0;
            if (i > 0 && !vis[i - 1][j]) opts[m++] = 1;
            if (j + 1 < n && !vis[i][j + 1]) opts[m++] = 2;
            if (j > 0 && !vis[i][j - 1]) opts[m++] = 3;
            if (m == 0) { sp--; continue; }
            int d = opts[Math.min(m - 1, (int) (k.rnd(salt, i, step++, j) * m))];
            int ni = i, nj = j;
            if (d == 0) { e[i][j] = true; ni = i + 1; }
            else if (d == 1) { e[i - 1][j] = true; ni = i - 1; }
            else if (d == 2) { s[i][j] = true; nj = j + 1; }
            else { s[i][j - 1] = true; nj = j - 1; }
            vis[ni][nj] = true;
            stack[sp++] = ni + nj * n;
        }
        return new boolean[][][] {e, s};
    }

    static boolean mzWall(boolean[][] e, boolean[][] s, int lu, int lv, int level) {
        boolean entrance = level == 0 && lu <= 2 && lv >= 15 && lv <= 17;
        if (lu < 0 || lv < 0 || lu > 32 || lv > 32) return !(entrance && lu < 0);
        if (entrance) return false;
        boolean wu = lu % 6 < 3, wv = lv % 6 < 3;
        if (wu && wv) return true;
        if (!wu && !wv) return false;
        int i = lu / 6, j = lv / 6;
        if (wu) return i == 0 || i == 5 || !e[i - 1][j];
        return j == 0 || j == 5 || !s[i][j - 1];
    }

    private void aquarium(Kit k, P p) {
        if (!k.touches(AQ_U0, AQ_V0, AQ_U1, AQ_V1)) return;
        boolean[][][] m1 = maze(k, 501), m2 = maze(k, 502);
        for (int u = Math.max(AQ_U0, k.u0()); u <= Math.min(AQ_U1, k.u1()); u++) for (int v = Math.max(AQ_V0, k.v0()); v <= Math.min(AQ_V1, k.v1()); v++) {
            boolean edge = u == AQ_U0 || u == AQ_U1 || v == AQ_V0 || v == AQ_V1;
            boolean col = (u - AQ_U0) % 6 == 0 && (v == AQ_V0 || v == AQ_V1) || (v - AQ_V0) % 6 == 0 && (u == AQ_U0 || u == AQ_U1);
            k.set(u, FL - 2, v, p.steel);
            k.set(u, FL - 1, v, p.dpris);
            k.set(u, FL, v, (u + v) % 2 == 0 ? p.pbrick : p.dpris);
            for (int y = FL + 1; y <= FL + 9; y++) {
                BlockState s;
                if (edge) {
                    if (col) s = p.steel;
                    else if (y == FL + 5) s = p.dpris;
                    else if (y <= FL + 2) s = p.pbrick;
                    else s = p.glass;
                } else if (u == 31) {
                    s = (v - AQ_V0) % 6 == 0 || y == FL + 5 ? p.steel : y <= FL + 1 ? p.dpris : p.glass;
                } else if (u > 31 && y == FL + 5) {
                    s = p.steel;
                } else s = Kit.AIR;
                k.set(u, y, v, s);
            }
            // Dach mit Oberlichtern
            boolean grid = (u - AQ_U0) % 6 == 0 || (v - AQ_V0) % 6 == 0 || edge;
            k.set(u, FL + 10, v, grid ? p.steel : p.glass);
            // Labyrinth-Tanks
            int lu = u - MZ_U, lv = v - MZ_V;
            if (lu >= 0 && lu <= 32 && lv >= 0 && lv <= 32) {
                for (int level = 0; level < 2; level++) {
                    boolean[][][] mz = level == 0 ? m1 : m2;
                    int yF = level == 0 ? FL : FL + 5;
                    boolean room = lu % 6 >= 3 && lv % 6 >= 3;
                    if (room && lu % 6 == 4 && lv % 6 == 4) k.set(u, yF + 5, v, level == 0 ? p.light : p.light);
                    if (!mzWall(mz[0], mz[1], lu, lv, level)) continue;
                    boolean inner = mzWall(mz[0], mz[1], lu + 1, lv, level) && mzWall(mz[0], mz[1], lu - 1, lv, level)
                            && mzWall(mz[0], mz[1], lu, lv + 1, level) && mzWall(mz[0], mz[1], lu, lv - 1, level);
                    boolean kelpCol = k.rnd(510 + level, u, 0, v) < 0.22;
                    for (int y = yF + 1; y <= yF + 4; y++) {
                        BlockState s;
                        if (!inner) s = y == yF + 1 ? p.dpris : y == yF + 4 ? p.steel : p.glass;
                        else if (y == yF + 1) {
                            double r = k.rnd(512 + level, u, y, v);
                            s = kelpCol ? p.kelp : r < 0.3 ? p.sgrass : r < 0.42 ? p.coral[(int) (k.rnd(514, u, y, v) * 5)] : r < 0.5 ? p.pickle : r < 0.58 ? p.frogV : p.water;
                        } else if (kelpCol && y <= yF + 3) s = p.kelp;
                        else s = p.water;
                        k.set(u, y, v, s);
                    }
                }
            }
        }
        // Lobby: Eingang vom Hub, Tuer ins Labyrinth
        k.clear(31, FL + 1, 1, 31, FL + 4, 3);
        for (int y = FL + 1; y <= FL + 5; y++) { k.set(31, y, 0, p.hazard); k.set(31, y, 4, p.hazard); }
        k.fill(31, FL + 5, 1, 31, FL + 5, 3, p.hazard);
        // Leiter L1 -> L2 (Zelle 4/4, Ostwand)
        k.ladder(61, FL + 1, FL + 5, 14, Direction.EAST);
        // Lobby-Ausstattung: Probentanks (Glas um Wasser), Labortische
        for (int v : new int[] {-11, -5, 9, 15}) {
            k.fill(27, FL + 1, v - 1, 29, FL + 5, v + 1, p.glass);
            k.fill(28, FL + 1, v, 28, FL + 1, v, p.frogP);
            k.fill(28, FL + 2, v, 28, FL + 4, v, p.water);
            k.set(28, FL + 2, v, p.kelp);
            k.fill(27, FL + 5, v - 1, 29, FL + 5, v + 1, p.steel);
            k.fill(27, FL + 1, v - 1, 29, FL + 1, v - 1, p.steel);
        }
        for (int v = -14; v <= 18; v++) {
            if (Math.abs(v - 2) <= 3 || v == -11 || v == -10 || v == -12 || v == -5 || v == -4 || v == -6 || v == 9 || v == 8 || v == 10 || v == 15 || v == 14 || v == 16) continue;
            if (v % 3 == 0) k.set(26, FL + 1, v, Kit.stairs(Blocks.DARK_PRISMARINE_STAIRS, Direction.WEST, true));
            else if (v % 3 == 1) k.set(26, FL + 1, v, p.barrel);
            else k.set(26, FL + 1, v, b(Blocks.BREWING_STAND));
        }
        for (int v = -12; v <= 16; v += 4) k.set(28, FL + 9, v, p.light);
        // Kelp-Kuppel auf dem Dach (wassergefuellt)
        dome(k, p, 45, 2, FL + 10, 17, 6, 1.2, 10, p.dpris);
        // Innenraum der Kelp-Kuppel wieder fluten, Kelp
        for (int u = Math.max(28, k.u0()); u <= Math.min(62, k.u1()); u++) for (int v = Math.max(-15, k.v0()); v <= Math.min(19, k.v1()); v++) {
            double du = (u - 45) / 17.0, dv = (v - 2) / 17.0;
            for (int y = FL + 11; y <= FL + 17; y++) {
                double ny = (y - FL - 10) / 6.0, d = Math.sqrt(du * du + ny * ny + dv * dv);
                if (d > 1) continue;
                if ((1 - d) * 6 >= 1.2) {
                    boolean kc = k.rnd(520, u, 0, v) < 0.35 && y <= FL + 11 + (int) (k.rnd(521, u, 0, v) * 5);
                    k.set(u, y, v, kc ? p.kelp : p.water);
                }
            }
        }
    }

    // ── Drucklabor (Schluessel 3) ──

    private void pressureLab(Kit k, P p) {
        int ir = (int) PL_R + 1;
        if (!k.touches(PL_U - ir, PL_V - ir, PL_U + ir, PL_V + ir)) return;
        for (int u = Math.max(PL_U - ir, k.u0()); u <= Math.min(PL_U + ir, k.u1()); u++) for (int v = Math.max(PL_V - ir, k.v0()); v <= Math.min(PL_V + ir, k.v1()); v++) {
            double du = u - PL_U, dv = v - PL_V, dh = Math.sqrt(du * du + dv * dv);
            double ang = Math.atan2(dv, du), step = Math.PI / 6, dA = ang - Math.round(ang / step) * step;
            boolean port = Math.abs(dA) * dh < 1.6;
            boolean seam = Math.abs(dA) * dh < 0.5 && !port;
            for (int y = PL_Y - ir; y <= PL_Y + ir; y++) {
                double dy = y - PL_Y, d = Math.sqrt(du * du + dy * dy + dv * dv) / PL_R;
                if (d > 1) continue;
                double depth = (1 - d) * PL_R;
                BlockState s;
                if (depth < 1.6) {
                    if (port && Math.abs(dy) <= 1 && Math.abs(dA - Math.PI / 2) > 0.01) s = p.glass;
                    else if (y == PL_Y + 2 || y == PL_Y - 2 || y == PL_Y + 7) s = p.titan;
                    else if (dh < 3) s = p.titan;
                    else if (y < FL) s = p.rust;
                    else s = ((int) Math.floor((ang + Math.PI) * 18 / Math.PI)) % 3 == 0 ? p.lead : p.steel;
                } else if (y > FL) s = Kit.AIR;
                else if (y == FL) s = dh < 3.5 ? p.hazard : ((int) dh) % 3 == 0 ? p.titan : p.steel;
                else s = p.lead;
                k.set(u, y, v, s);
            }
        }
    }

    // ── Schacht ──

    private void shaft(Kit k, P p) {
        int cu = HUB_U, cv = HUB_V;
        if (k.touches(cu - 7, cv - 7, cu + 7, cv + 7)) {
            for (int u = Math.max(cu - 7, k.u0()); u <= Math.min(cu + 7, k.u1()); u++) for (int v = Math.max(cv - 7, k.v0()); v <= Math.min(cv + 7, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                if (d > 6.5) continue;
                for (int y = FL + 1; y <= 0; y++) {
                    if (d <= 5.3) {
                        if (d <= 1.2) k.set(u, y, v, y % 5 == 0 ? p.light : p.steel);
                        else k.air(u, y, v);
                    } else if (y >= FL + 4 && y <= -1) {
                        k.set(u, y, v, y >= -10 ? (y % 3 == 0 ? p.dpris : p.brickC) : (y % 4 == 0 ? p.steel : p.glass));
                    }
                }
                if (d <= 1.2) for (int y = 1; y <= 3; y++) k.set(u, y, v, y == 3 ? p.light : p.steel);
            }
        }
        k.spiral(cu, cv, FL, 0, 2.0, 4.5, 1.3, 90, 3, p.dpris);
    }

    // ── Siegeltor ──

    private void gate(Kit k, P p) {
        if (!k.touches(-8, -21, 8, -10)) return;
        k.fill(-6, FL - 2, -19, 6, FL + 12, -12, p.pbrick);
        for (int y = FL - 2; y <= FL + 12; y++) for (int s = -6; s <= 6; s += 12) { k.set(s, y, -19, p.steel); k.set(s, y, -12, p.steel); }
        k.fill(-6, FL + 12, -19, 6, FL + 12, -12, p.dpris);
        k.fill(-6, FL + 7, -12, 6, FL + 7, -12, p.dpris);
        k.clear(-2, FL + 1, -15, 2, FL + 5, -12);
        k.fill(-2, FL, -15, 2, FL, -12, p.dpris);
        k.sealGate(0, FL, -16, Direction.SOUTH, 2, 5, p.dpris, p.pris);
        k.set(-4, FL + 1, -12, p.light);
        k.set(4, FL + 1, -12, p.light);
        // Roehre hinter dem Tor durch das Torhaus
        tube(k, p, false, 0, -19, -18, true);
    }

    // ── Innenausbau ──

    private void hubInterior(Kit k, P p) {
        int cu = HUB_U, cv = HUB_V;
        if (k.touches(cu - 16, cv - 16, cu + 16, cv + 16)) {
            for (int u = Math.max(cu - 15, k.u0()); u <= Math.min(cu + 15, k.u1()); u++) for (int v = Math.max(cv - 15, k.v0()); v <= Math.min(cv + 15, k.v1()); v++) {
                double d = Math.sqrt(sq(u - cu, v - cv));
                if (d < 5.6 || d > 14.6) continue;
                if (v < -11 && Math.abs(u) <= 6) continue;
                boolean spoke = Math.abs(u - cu) <= 2 || Math.abs(v - cv) <= 2;
                BlockState s;
                if (d < 6.6) s = p.steel;
                else if (d > 13.6) s = p.dpris;
                else if (spoke) s = (Math.abs(u - cu) == 0 || Math.abs(v - cv) == 0) && ((int) d) % 3 == 0 ? p.light : p.dpris;
                else s = k.noise(60, u, v, 3) < 0.5 ? p.pbrick : p.pris;
                if (((int) Math.round(d)) == 10 && !spoke) s = p.plate;
                k.set(u, FL, v, s);
            }
            // Saeulenkranz
            for (int a = 0; a < 8; a++) {
                double ang = Math.PI / 8 + a * Math.PI / 4;
                int u = cu + (int) Math.round(Math.cos(ang) * 11.5), v = cv + (int) Math.round(Math.sin(ang) * 11.5);
                for (int y = FL + 1; y <= FL + 10; y++) k.set(u, y, v, y == FL + 1 ? p.dpris : y == FL + 6 ? p.light : p.steel);
                k.set(u, FL + 11, v, p.steel);
            }
            // Bodenaquarien
            for (int s = -1; s <= 1; s += 2) {
                int au = cu + 8 * s, av = cv + 6;
                k.fill(au - 2, FL, av - 2, au + 2, FL, av + 2, p.steel);
                k.fill(au - 1, FL, av - 1, au + 1, FL, av + 1, p.water);
                k.set(au, FL, av, p.sgrass);
                k.set(au - 1, FL - 1, av - 1, p.frogV);
                k.set(au + 1, FL - 1, av + 1, p.frogV);
                for (int du = -2; du <= 2; du += 4) for (int dv = -2; dv <= 2; dv += 4) k.set(au + du, FL + 1, av + dv, p.pwall);
            }
            // Pulte vor dem Torhaus und Kartentisch
            for (int s = -1; s <= 1; s += 2) {
                k.set(9 * s, FL + 1, -6, Kit.stairs(Blocks.DARK_PRISMARINE_STAIRS, Direction.NORTH, true));
                k.set(9 * s + s, FL + 1, -6, Kit.stairs(Blocks.DARK_PRISMARINE_STAIRS, Direction.NORTH, true));
                k.set(9 * s, FL + 1, -7, p.steel);
                k.set(9 * s + s, FL + 1, -7, p.steel);
                k.set(9 * s, FL + 2, -7, p.tinted);
                k.set(9 * s + s, FL + 2, -7, p.cyanGlass);
            }
            // Wegweiser-Rahmen an den Roehren (W blau, O tuerkis, S orange)
            frame(k, true, 2, -13, p.blue);
            frame(k, true, 2, 13, p.cyan);
            frame(k, false, 0, 15, p.orange);
            // Haengelampen unter der Kuppel
            for (int a = 0; a < 6; a++) {
                double ang = a * Math.PI / 3;
                int u = cu + (int) Math.round(Math.cos(ang) * 8), v = cv + (int) Math.round(Math.sin(ang) * 8);
                k.set(u, FL + 11, v, p.chain);
                k.set(u, FL + 10, v, p.hang);
            }
            k.set(cu - 12, FL + 1, cv - 5, p.barrel);
            k.set(cu - 12, FL + 1, cv - 6, b(ModBlocks.BARREL_STEEL.get()));
            k.set(cu + 12, FL + 1, cv + 6, b(ModBlocks.BARREL_PLASTIC.get()));
        }
    }

    /** Farbiger Torrahmen um eine Roehrenmuendung (Querschnitt bei t). */
    private void frame(Kit k, boolean alongU, int fixed, int t, BlockState s) {
        for (int w = -3; w <= 3; w++) for (int y = FL + 1; y <= FL + 6; y++) {
            if (Math.abs(w) < 3 && y < FL + 6) continue;
            int u = alongU ? t : fixed + w, v = alongU ? fixed + w : t;
            k.set(u, y, v, s);
        }
    }

    private void airlockInterior(Kit k, P p) {
        int cu = LOCK_U, cv = LOCK_V;
        if (!k.touches(cu - 9, cv - 9, cu + 9, cv + 9)) return;
        for (int u = cu - 8; u <= cu + 8; u++) for (int v = cv - 8; v <= cv + 8; v++) {
            double d = Math.sqrt(sq(u - cu, v - cv));
            if (d > 8.2) continue;
            k.set(u, FL, v, (u + v) % 2 == 0 ? p.dpris : p.pris);
        }
        // Tauchbecken (offen nach unten ins Meer)
        k.fill(cu - 5, FL, cv - 2, cu - 1, FL, cv + 2, p.hazard);
        k.fill(cu - 4, FL - 2, cv - 1, cu - 2, FL, cv + 1, p.water);
        k.set(cu - 3, FL - 2, cv, p.sgrass);
        // Ausruestung: Flaschen (Blitzableiter), Spinde, Dekompressionskammer
        for (int v = cv - 3; v <= cv + 3; v += 2) { k.set(cu + 4, FL + 1, v - 3 < cv - 5 ? v : v, p.steel); }
        k.fill(cu - 1, FL + 1, cv - 6, cu + 3, FL + 1, cv - 6, p.steel);
        for (int u = cu - 1; u <= cu + 3; u++) k.set(u, FL + 2, cv - 6, (u % 2 == 0) ? p.rod : p.barrel);
        k.fill(cu - 2, FL + 1, cv + 5, cu + 1, FL + 3, cv + 6, p.glass);
        k.fill(cu - 1, FL + 1, cv + 5, cu, FL + 2, cv + 5, Kit.AIR);
        k.set(cu - 1, FL + 3, cv + 6, p.light);
        k.set(cu, FL + 8, cv, p.chain);
        k.set(cu, FL + 7, cv, p.hang);
        k.set(cu - 6, FL + 1, cv - 3, p.light);
        k.set(cu - 6, FL + 1, cv + 3, p.light);
    }

    private void moonpoolInterior(Kit k, P p) {
        int cu = MP_U, cv = MP_V;
        if (!k.touches(cu - 23, cv - 23, cu + 23, cv + 23)) return;
        for (int u = Math.max(cu - 22, k.u0()); u <= Math.min(cu + 22, k.u1()); u++) for (int v = Math.max(cv - 22, k.v0()); v <= Math.min(cv + 22, k.v1()); v++) {
            double d = Math.sqrt(sq(u - cu, v - cv));
            if (d > 21) continue;
            double ang = Math.toDegrees(Math.atan2(v - cv, u - cu));
            // Becken
            if (d <= 9.6) {
                for (int y = -58; y <= MP_FL; y++) {
                    BlockState s;
                    if (d <= 8.2 && y >= -57) s = p.water;
                    else if (y == MP_FL) s = Math.abs((ang + 360) % 30 - 15) > 13 ? p.light : p.dpris;
                    else s = p.dpris;
                    k.set(u, y, v, s);
                }
                if (d <= 8.2) {
                    k.set(u, -58, v, ((int) Math.round(d)) % 3 == 0 ? p.frogP : p.pris);
                    double r = k.rnd(70, u, 0, v);
                    if (d > 5.5 && r < 0.18) for (int y = -57; y <= -53 - (int) (r * 10); y++) k.set(u, y, v, p.kelp);
                    else if (r < 0.3) k.set(u, -57, v, p.sgrass);
                }
                continue;
            }
            // Ringboden
            boolean radial = Math.abs((ang + 360) % 45 - 22.5) > 21.5;
            BlockState f = radial ? p.steel : ((int) Math.round(d)) % 4 == 0 ? p.dpris : k.noise(71, u, v, 3) < 0.5 ? p.pbrick : p.pris;
            if (((int) Math.round(d)) == 12 && Math.abs((ang + 360) % 30 - 15) > 13) f = p.light;
            k.set(u, MP_FL, v, f);
            // Galerie-Rampen an der Wand (beidseitig vom Eingang im Sueden)
            if (d >= 16.5 && d <= 20.3) {
                double phi = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, (v - cv) / d))));
                if (phi <= 104) {
                    int lvl = phi < 12 ? FL : FL - Math.min(8, 1 + (int) Math.floor(8 * (phi - 12) / 92.0));
                    if (lvl > MP_FL) {
                        k.set(u, lvl, v, d > 19.4 ? p.dpris : p.pbrick);
                        if (lvl - 1 > MP_FL) k.set(u, lvl - 1, v, p.steel);
                        if (((int) Math.round(phi)) % 18 == 0) for (int y = MP_FL + 1; y < lvl - 1; y++) k.set(u, y, v, p.steel);
                        if (d < 17.3 && lvl >= FL - 5 && phi >= 12) k.set(u, lvl + 1, v, p.bars);
                    }
                }
            }
        }
        // Balkon am Eingang
        k.fill(cu - 5, FL, cv + 13, cu + 5, FL, cv + 20, p.pbrick);
        k.fill(cu - 5, FL - 1, cv + 13, cu + 5, FL - 1, cv + 20, p.steel);
        k.fill(cu - 1, FL, cv + 13, cu + 1, FL, cv + 20, p.dpris);
        for (int u = cu - 5; u <= cu + 5; u++) k.set(u, FL + 1, cv + 13, p.bars);
        for (int s = -1; s <= 1; s += 2) for (int y = MP_FL + 1; y <= FL - 2; y++) { k.set(cu + 4 * s, y, cv + 14, p.steel); k.set(cu + 4 * s, y, cv + 18, p.steel); }
        k.set(cu, FL + 1, cv + 13, p.light);
        // Vier Saeulen
        for (int a = 0; a < 4; a++) {
            double ang = Math.PI / 4 + a * Math.PI / 2;
            double su = cu + Math.cos(ang) * 13.4, sv = cv + Math.sin(ang) * 13.4;
            k.cylinder(su, sv, MP_FL + 1, -31, 1.3, p.steel);
            k.cylinder(su, sv, MP_FL + 1, MP_FL + 1, 1.8, p.dpris);
            k.cylinder(su, sv, -44, -44, 1.4, p.light);
            k.cylinder(su, sv, -37, -37, 1.4, p.light);
        }
        // Geparktes Mini-U-Boot im Norden
        k.fill(cu - 2, MP_FL + 1, cv - 18, cu + 2, MP_FL + 1, cv - 13, p.steel);
        k.ellipsoid(cu, MP_FL + 3.5, cv - 15.5, 2.3, 1.8, 3.6, p.yellow);
        k.ellipsoid(cu, MP_FL + 4, cv - 12.5, 1.4, 1.2, 1.2, p.glass);
        k.set(cu, MP_FL + 6, cv - 16, p.steel);
        k.set(cu, MP_FL + 3, cv - 20, p.steel);
        k.set(cu - 1, MP_FL + 3, cv - 20, p.steel);
        k.set(cu + 1, MP_FL + 3, cv - 20, p.steel);
        // Fracht an der Wand
        for (int s = -1; s <= 1; s += 2) {
            k.set(cu + 15 * s, MP_FL + 1, cv - 9, p.barrel);
            k.set(cu + 15 * s, MP_FL + 1, cv - 8, b(ModBlocks.BARREL_STEEL.get()));
            k.set(cu + 14 * s, MP_FL + 1, cv - 10, p.barrel);
        }
    }

    private void pressureInterior(Kit k, P p) {
        int cu = PL_U, cv = PL_V;
        if (!k.touches(cu - 12, cv - 12, cu + 12, cv + 12)) return;
        // Drucktanks
        for (int[] s : new int[][] {{7, 7}, {-7, 7}, {7, -6}, {-7, -6}}) {
            int tu = cu + s[0], tv = cv + s[1];
            for (int y = FL + 1; y <= FL + 6; y++) k.cylinder(tu, tv, y, y, 1.6, y == FL + 1 || y == FL + 4 ? p.steel : y == FL + 6 ? p.dpris : p.titan);
            k.set(tu, FL + 7, tv, p.steel);
            for (int y = FL + 8; y <= FL + 12; y++) k.set(tu, y, tv, p.steel);
            k.set(tu, FL + 3, tv + (s[1] > 0 ? -2 : 2), p.light);
        }
        // Mittelpodest mit Ring
        k.cylinder(cu, cv, FL + 1, FL + 1, 2.6, p.hazard);
        k.cylinder(cu, cv, FL + 1, FL + 1, 1.6, p.steel);
        for (int a = 0; a < 8; a++) {
            double ang = a * Math.PI / 4;
            k.set(cu + (int) Math.round(Math.cos(ang) * 6), FL + 13, cv + (int) Math.round(Math.sin(ang) * 6), p.light);
        }
        k.cylinder(cu, cv, FL + 14, FL + 14, 3.2, p.steel);
        k.set(cu, FL + 13, cv, p.light);
        // Rohre an der Decke
        lineM(k, cu - 7, FL + 12, cv + 7, cu + 7, FL + 12, cv + 7, 0.5, (u, y, v) -> p.steel);
        lineM(k, cu - 7, FL + 12, cv - 6, cu + 7, FL + 12, cv - 6, 0.5, (u, y, v) -> p.steel);
        // Konsolen
        for (int s = -1; s <= 1; s += 2) {
            k.set(cu + 10 * s, FL + 1, cv + 2, p.steel);
            k.set(cu + 10 * s, FL + 1, cv + 3, p.steel);
            k.set(cu + 10 * s, FL + 2, cv + 2, p.tinted);
            k.set(cu + 10 * s, FL + 2, cv + 3, p.cyanGlass);
        }
    }

    // ── Tauchstrecke (Schluessel 1) ──

    private void bells(Kit k, P p) {
        for (int[] bl : BELLS) {
            int bu = bl[0], bv = bl[1], yb = BELL_Y;
            if (!k.touches(bu - 6, bv - 6, bu + 6, bv + 6)) continue;
            for (int[] s : new int[][] {{4, 4}, {-4, 4}, {4, -4}, {-4, -4}}) {
                int sb = seabed(k, bu + s[0], bv + s[1]);
                for (int y = sb; y <= yb; y++) k.set(bu + s[0], y, bv + s[1], y == yb ? p.steel : p.pwall);
            }
            k.shellCut(bu, yb, bv, 5, 6, 5, 1.2, p.glass, null, yb, yb + 6);
            k.shellCut(bu, yb, bv, 5, 6, 5, 1.2, null, Kit.AIR, yb + 2, yb + 6);
            // Rand und Rippen aus Stahl
            for (int u = bu - 5; u <= bu + 5; u++) for (int v = bv - 5; v <= bv + 5; v++) {
                double d = Math.sqrt(sq(u - bu, v - bv));
                if (d > 3.7 && d <= 5.3) k.set(u, yb, v, p.steel);
                if (d >= 2.0 && d <= 2.9) k.set(u, yb + 1, v, p.pbrick);
            }
            k.set(bu, yb + 5, bv, p.light);
            k.set(bu, yb + 6, bv, p.steel);
            k.set(bu, yb + 7, bv, p.frogO);
        }
    }

    private void wreck(Kit k, P p) {
        int wu = WRECK_U, wy = WRECK_Y;
        if (!k.touches(wu - 9, -56, wu + 9, -20)) return;
        // Schlick-Wall seitlich
        for (int u = wu - 8; u <= wu + 8; u++) for (int v = -50; v <= -27; v++) {
            if (!k.col(u, v)) continue;
            int du = Math.abs(u - wu);
            if (du < 4) continue;
            int top = -47 - (8 - du) / 3;
            for (int y = seabed(k, u, v); y <= top; y++) k.set(u, y, v, p.silt);
        }
        Mat hull = (u, y, v) -> {
            double r = k.rnd(80, u, y, v);
            return r < 0.45 ? p.rust : r < 0.7 ? p.steel : r < 0.85 ? p.copper : p.iron;
        };
        lineM(k, wu, wy, -47, wu, wy, -31, 4.6, hull);
        lineM(k, wu, wy + 0.5, -51, wu, wy + 0.5, -47, 3.2, hull);
        lineM(k, wu, wy, -31, wu, wy, -27, 3.0, hull);
        lineM(k, wu, wy, -47, wu, wy, -31, 3.6, (u, y, v) -> p.water);
        lineM(k, wu, wy + 0.5, -50, wu, wy + 0.5, -47, 2.2, (u, y, v) -> p.water);
        lineM(k, wu, wy, -31, wu, wy, -28, 2.0, (u, y, v) -> p.water);
        // Deck innen
        k.fill(wu - 2, wy - 3, -47, wu + 2, wy - 3, -30, p.rust);
        // Schotten mit Durchgang
        for (int sv : new int[] {-42, -35}) {
            for (int u = wu - 3; u <= wu + 3; u++) for (int y = wy - 3; y <= wy + 3; y++) {
                if (sq(u - wu, y - wy) > 13) continue;
                boolean door = Math.abs(u - wu) <= 0 && y >= wy - 2 && y <= wy;
                k.set(u, y, sv, door ? p.water : p.steel);
            }
        }
        // Turm mit Luftblase
        for (int u = wu - 2; u <= wu + 2; u++) for (int v = -42; v <= -36; v++) for (int y = wy + 2; y <= wy + 10; y++) {
            boolean wall = u == wu - 2 || u == wu + 2 || v == -42 || v == -36 || y == wy + 10;
            BlockState s;
            if (wall) s = (y == wy + 6 && (v == -39) && u != wu) || (y == wy + 6 && u == wu && (v == -42 || v == -36)) ? p.glass : hull.at(u, y, v);
            else if (y <= wy + 5) s = (y == wy + 5 && v == -41) ? p.rust : p.water;
            else s = Kit.AIR;
            k.set(u, y, v, s);
        }
        for (int u = wu - 1; u <= wu + 1; u++) for (int v = -41; v <= -37; v++) for (int y = wy + 2; y <= wy + 4; y++) k.set(u, y, v, p.water);
        k.set(wu - 1, wy + 9, -37, p.light);
        for (int y = wy + 11; y <= wy + 14; y++) k.set(wu, y, -40, p.bars);
        k.set(wu, wy + 14, -41, p.steel);
        // Leck an der Ostseite (Einstieg) und kleineres Loch oben am Bug
        k.ellipsoid(wu + 4, wy - 1, -32, 2.2, 2.0, 2.4, p.water);
        k.ellipsoid(wu - 3, wy + 3, -45, 1.6, 1.4, 1.8, p.water);
        // Schraube und Ruder am Heck
        for (int i = -2; i <= 2; i++) { k.set(wu + i, wy, -26, p.copper); k.set(wu, wy + i, -26, p.copper); }
        k.fill(wu, wy - 4, -27, wu, wy + 4, -27, p.rust);
        // Truemmer und Torpedo
        lineM(k, wu + 7, -47, -45, wu + 9, -47, -36, 0.7, (u, y, v) -> p.iron);
        for (int[] t : new int[][] {{wu - 7, -38}, {wu + 6, -26}, {wu - 6, -29}, {wu + 8, -50}}) k.set(t[0], seabed(k, t[0], t[1]) + 1, t[1], p.rust);
    }

    private void guideLine(Kit k, P p) {
        int[][] pts = {{LOCK_U - 2, LOCK_V - 4}, {BELLS[0][0], BELLS[0][1]}, {BELLS[1][0], BELLS[1][1]}, {WRECK_U + 5, -32}};
        int idx = 0;
        for (int i = 0; i + 1 < pts.length; i++) {
            double du = pts[i + 1][0] - pts[i][0], dv = pts[i + 1][1] - pts[i][1], len = Math.sqrt(du * du + dv * dv);
            for (double t = 0; t <= len; t += 4) {
                double side = (idx++ % 2 == 0 ? 3 : -3);
                int u = (int) Math.round(pts[i][0] + du * t / len - dv / len * side), v = (int) Math.round(pts[i][1] + dv * t / len + du / len * side);
                boolean nearBell = false;
                for (int[] bl : BELLS) if (sq(u - bl[0], v - bl[1]) < 49) nearBell = true;
                if (nearBell || sq(u - LOCK_U, v - LOCK_V) < 121 || !k.col(u, v)) continue;
                int sb = seabed(k, u, v);
                k.set(u, sb + 1, v, p.pwall);
                k.set(u, sb + 2, v, p.pwall);
                k.set(u, sb + 3, v, p.frogO);
            }
        }
        // Sockel fuer Angler-Spawner
        for (int[] s : new int[][] {{-48, -16}, {-56, -34}, {12, 22}}) {
            if (!k.col(s[0], s[1])) continue;
            for (int y = seabed(k, s[0], s[1]); y <= -46; y++) k.set(s[0], y, s[1], rock(k, p, s[0], y, s[1]));
        }
    }

    // ── Plan ──

    private void plan(Kit k, P p) {
        // Schluessel
        k.keyChest(WRECK_U, WRECK_Y + 6, -41, Direction.SOUTH);
        k.keyChest(36, FL + 6, -10, Direction.SOUTH);
        k.elite(PL_U, FL + 2, PL_V, DIVER);
        // Boss
        k.boss(MP_U, MP_FL + 1, MP_V);
        k.bossChest(MP_U + 14, MP_FL + 1, MP_V, Direction.WEST);
        k.bossChest(MP_U - 14, MP_FL + 1, MP_V, Direction.EAST);
        // Beute
        k.loot(LOCK_U + 3, FL + 1, LOCK_V - 3, Direction.WEST);
        k.barrel(WRECK_U, WRECK_Y - 2, -45);
        k.loot(LH_U + 6, 35, LH_V, Direction.WEST);
        k.loot(-8, FL + 1, -6, Direction.EAST);
        k.loot(36, FL + 6, 14, Direction.NORTH);
        k.barrel(PL_U, FL + 1, PL_V + 9);
        k.barrel(6, 1, -4);
        k.loot(55, 2, 8, Direction.SOUTH);
        // Spawner
        k.spawner(LOCK_U + 4, FL + 1, LOCK_V + 5, DIVER);
        k.spawner(-48, -45, -16, ANGLER);
        k.spawner(-56, -45, -34, ANGLER);
        k.spawner(12, -45, 22, ANGLER);
        k.spawner(47, FL + 1, 7, DIVER);
        k.spawner(49, FL + 6, -3, DIVER);
        k.spawner(PL_U + 9, FL + 1, PL_V, DIVER);
        k.spawner(PL_U - 9, FL + 1, PL_V, DIVER);
        // Bewohner
        int[][] anglers = {{-40, -44, -4}, {-46, -43, -16}, {-54, -42, -30}, {-42, -38, -46}, {22, -36, 30}, {-22, -40, -32}, {40, -36, -36}, {4, -52, MP_V}, {-4, -52, MP_V}};
        for (int[] a : anglers) k.mob(a[0], a[1], a[2], ANGLER);
        int[][] divers = {{3, FL + 1, 11}, {LOCK_U + 1, FL + 1, LOCK_V + 4}, {48, FL + 1, -4}, {54, FL + 1, 8}, {42, FL + 6, 2}, {PL_U + 5, FL + 1, PL_V + 4},
                {PL_U - 5, FL + 1, PL_V - 4}, {10, MP_FL + 1, MP_V + 6}, {-10, MP_FL + 1, MP_V - 6}, {70, 1, 0}};
        for (int[] d : divers) k.mob(d[0], d[1], d[2], DIVER);
    }
}
