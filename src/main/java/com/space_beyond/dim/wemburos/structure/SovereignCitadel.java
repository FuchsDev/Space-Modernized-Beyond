package com.space_beyond.dim.wemburos.structure;

import com.hbm_m.block.ModBlocks;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.wemburos.WemburosBlocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Die Zitadelle des Souveraens: Boss-Dungeon von Wemburos (Nutzerwunsch: riesig, mit Wow-Effekt, viele verschiedene
 * Bloecke). Grundriss 177 x 177 Bloecke ({@link #R} = 88), Kronkristall rund 90 Bloecke ueber dem Boden, Gewoelbe
 * 50 Bloecke darunter. Vorbilder: gotische Kathedralen und Rotunden (Spitzboegen, Rosette, Strebepfeiler, Kuppel mit
 * Opaion), Festungsbau (Zwinger, Torburg mit Fallgitter, Wehrgang mit Maschikulis) und schwebende Inseln.
 * <p>
 * Lokale Koordinaten (u = Ost, v = Sued, y relativ zur Grundhoehe; Boden y 0). Der Weg zum Boss:
 * <ol>
 *   <li><b>Prozessionsstrasse</b> (v 74..88): Ruinenbogen, zwei Waechterstatuen mit Kristallschwertern, Seelenfeuer.</li>
 *   <li><b>Abgrund</b> (v 57..72): Schlucht 40 tief bis zum Lavasee; die Bruecke ist in der Mitte eingestuerzt, ueber
 *       die Luecke fuehren an Ketten haengende Trittsteine.</li>
 *   <li><b>Torburg</b> (v 39..55): Haupttor mit geschlossenem Fallgitter; der Weg fuehrt durch die Wachstube im
 *       westlichen Torturm (Spawner, Leiter zum Wehrgang).</li>
 *   <li><b>Vorhof</b>: Prachtstrasse mit Kristallaltaeren, Kaserne (Westen), verfallene Bibliothek (Osten), Graeberfeld,
 *       Schmiede. Der innere Bezirk liegt hinter einem Seelenfeuergraben, die Zugbruecke ist hochgezogen.</li>
 *   <li><b>Mausoleum</b> (Osten): Treppe hinab in die <b>Katakomben</b> (Gruftsaal mit Spawnern) und weiter unter dem
 *       Graben in die <b>Glutkaverne</b> unter dem Bergfried: Lavasee, Saeulen- und Kettenplattform-Parkour im
 *       Bogen um die Kaverne, Blazes, haengender Herzkristall.</li>
 *   <li><b>Wendeltreppe</b> durch die Kavernendecke hinauf mitten in den <b>Thronsaal</b>: Rotunde (Radius 19,
 *       Kuppel bis 58) mit Sternenhimmel-Gewoelbe, Rosette, Lanzettfenstern, Saeulenkranz mit Arkaden,
 *       Kronleuchtern, Sonnenscheibe hinter dem Thron. Ueber dem Opaion schwebt der Kronkristall mit zwei
 *       Goldringen, um die Zitadelle schweben angekettete Inseln (zwei mit Schatzschrein).</li>
 * </ol>
 * Der Maler zeichnet nur, was im aktuellen Chunk liegt ({@link Pen#uMin()} usw.), damit der riesige Bau billig bleibt.
 */
final class SovereignCitadel {

    /** Zeichenflaeche in lokalen Koordinaten (gedreht wird vom Aufrufer). */
    interface Pen {
        void set(int u, int y, int v, BlockState s);

        /** Block oder null ausserhalb des Chunks. */
        BlockState get(int u, int y, int v);

        double unit(long salt, int u, int y, int v);

        int uMin();

        int uMax();

        int vMin();

        int vMax();
    }

    /** Halbe Kantenlaenge des Grundrisses. */
    static final int R = 88;
    /** Mitte des Bergfrieds (v). */
    static final int KV = -34;
    /** Innenradius des Thronsaals. */
    static final int HALL = 19;
    /** Wendeltreppe: Mitte (v) und unterste Stufe (y). */
    static final int SPIRAL_V = -19, SPIRAL_BOTTOM = -20;
    /** Hoehe ueber Grund: Bodenplatte des Thronsaals (begehbar auf y 2). */
    static final int FLOOR = 1;
    /** Throntreppe: Plattform (Bloecke auf y 4). */
    static final int DAIS = 4;
    /** Kaverne: Lavaspiegel, Laufhoehe der Plattformen (Bloecke). */
    static final int LAVA = -42, WALK = -21;
    /** Graben, innere Mauer, Rotunde (Radien um den Bergfried). */
    static final int MOAT_IN = 34, MOAT_OUT = 40, INNER_IN = 29, INNER_OUT = 33, KEEP = 22;
    /** Aeussere Mauer: Aussenkante Ost/West, Sued, Nord; Hoehe des Wehrgangs. */
    static final int WALL_U = 66, WALL_S = 46, WALL_N = -82, WALL_H = 22;
    /** Schlucht: Mitte (v) und halbe Breite. */
    static final double CHASM_C = 64.5, CHASM_HW = 8.0;

    private final Pen p;

    private SovereignCitadel(Pen p) {
        this.p = p;
    }

    static void build(Pen p) {
        new SovereignCitadel(p).all();
    }

    private void all() {
        ground();
        outskirts();
        chasm();
        bridge();
        approach();
        curtain();
        towers();
        gatehouse();
        bailey();
        barracks();
        library();
        graveyard();
        forge();
        gardens();
        moat();
        innerWall();
        keep();
        hall();
        throne();
        mausoleum();
        cavern();
        catacombs();
        spiral();
        sky();
    }

    // ── Werkzeuge ───────────────────────────────────────────────────────────

    private int ua(int a) {
        return Math.max(a, p.uMin());
    }

    private int ub(int b) {
        return Math.min(b, p.uMax());
    }

    private int va(int a) {
        return Math.max(a, p.vMin());
    }

    private int vb(int b) {
        return Math.min(b, p.vMax());
    }

    private boolean col(int u, int v) {
        return u >= p.uMin() && u <= p.uMax() && v >= p.vMin() && v <= p.vMax();
    }

    /** Beruehrt das Rechteck den Chunk? */
    private boolean touches(int u0, int v0, int u1, int v1) {
        return Math.max(u0, u1) >= p.uMin() && Math.min(u0, u1) <= p.uMax() && Math.max(v0, v1) >= p.vMin() && Math.min(v0, v1) <= p.vMax();
    }

    private void set(int u, int y, int v, BlockState s) {
        if (col(u, v)) p.set(u, y, v, s);
    }

    private void air(int u, int y, int v) {
        if (col(u, v)) p.set(u, y, v, AIR);
    }

    private BlockState get(int u, int y, int v) {
        return col(u, v) ? p.get(u, y, v) : null;
    }

    private boolean isAir(int u, int y, int v) {
        BlockState s = get(u, y, v);
        return s != null && s.isAir();
    }

    private void setIfAir(int u, int y, int v, BlockState s) {
        if (isAir(u, y, v)) p.set(u, y, v, s);
    }

    private void fill(int u0, int y0, int v0, int u1, int y1, int v1, BlockState s) {
        for (int u = ua(Math.min(u0, u1)); u <= ub(Math.max(u0, u1)); u++)
            for (int v = va(Math.min(v0, v1)); v <= vb(Math.max(v0, v1)); v++)
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) p.set(u, y, v, s);
    }

    private void clear(int u0, int y0, int v0, int u1, int y1, int v1) {
        fill(u0, y0, v0, u1, y1, v1, AIR);
    }

    private double rnd(long salt, int u, int y, int v) {
        return p.unit(salt, u, y, v);
    }

    /** Weiches Rauschen 0..1 (bilinear, Gitter {@code scale}). */
    private double noise(long salt, int u, int v, int scale) {
        int iu = Math.floorDiv(u, scale), iv = Math.floorDiv(v, scale);
        double fu = (u - iu * scale) / (double) scale, fv = (v - iv * scale) / (double) scale;
        fu = fu * fu * (3 - 2 * fu);
        fv = fv * fv * (3 - 2 * fv);
        double a = rnd(salt, iu, 0, iv), b = rnd(salt, iu + 1, 0, iv), c = rnd(salt, iu, 0, iv + 1), d = rnd(salt, iu + 1, 0, iv + 1);
        double top = a + (b - a) * fu, bottom = c + (d - c) * fu;
        return top + (bottom - top) * fv;
    }

    private static double dist(double du, double dv) {
        return Math.sqrt(du * du + dv * dv);
    }

    /** Winkel in Grad 0..360 (0 = Ost, 90 = Sued). */
    private static double angle(double du, double dv) {
        double a = Math.toDegrees(Math.atan2(dv, du));
        return a < 0 ? a + 360 : a;
    }

    /** Abstand (Grad, mit Vorzeichen) zur naechsten von n Speichen ab {@code offset}. */
    private static double spoke(double deg, double offset, double step) {
        double x = (deg - offset) / step;
        return (x - Math.rint(x)) * step;
    }

    /** Spitzbogen (gleichseitig): liegt (x, h) in einer Oeffnung der halben Breite hw, Kaempfer bei h = 0? */
    private static boolean arch(double x, double h, double hw) {
        if (h <= 0) return Math.abs(x) <= hw;
        double s = 2 * hw;
        return (x + hw) * (x + hw) + h * h <= s * s && (x - hw) * (x - hw) + h * h <= s * s;
    }

    /** Vorherrschende Himmelsrichtung eines Vektors (u, v). */
    private static Direction toward(double du, double dv) {
        if (Math.abs(du) > Math.abs(dv)) return du > 0 ? Direction.EAST : Direction.WEST;
        return dv > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    // ── Bloecke ─────────────────────────────────────────────────────────────

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private static BlockState b(Block x) {
        return x.defaultBlockState();
    }

    private static BlockState stairs(Block x, Direction up) {
        return CityPalette.stairs(x, up, false);
    }

    private static BlockState stairsTop(Block x, Direction up) {
        return CityPalette.stairs(x, up, true);
    }

    private static BlockState slab(Block x, boolean top) {
        return CityPalette.slab(b(x), top ? SlabType.TOP : SlabType.BOTTOM);
    }

    private static BlockState facing(Block x, Direction d) {
        return CityPalette.facing(b(x), d);
    }

    private static BlockState lantern(boolean soul, boolean hanging) {
        return CityPalette.with(b(soul ? Blocks.SOUL_LANTERN : Blocks.LANTERN), BlockStateProperties.HANGING, hanging);
    }

    private static BlockState candles(Block candle, int n) {
        return CityPalette.with(CityPalette.with(b(candle), BlockStateProperties.CANDLES, n), BlockStateProperties.LIT, true);
    }

    private static BlockState cluster(Direction d) {
        return CityPalette.facingAll(b(WemburosBlocks.UNSTABLE_CRYSTAL_CLUSTER.get()), d);
    }

    private static BlockState amethyst(Direction d) {
        return CityPalette.facingAll(b(Blocks.AMETHYST_CLUSTER), d);
    }

    private static BlockState pillar(Block x, Direction.Axis axis) {
        return CityPalette.log(b(x), axis);
    }

    private static BlockState bed(Direction facing, boolean head) {
        BlockState s = CityPalette.facing(b(Blocks.PURPLE_BED), facing);
        return CityPalette.with(s, BlockStateProperties.BED_PART, head ? BedPart.HEAD : BedPart.FOOT);
    }

    private static BlockState crystal() {
        return b(WemburosBlocks.UNSTABLE_CRYSTAL.get());
    }

    private static BlockState polOnyx() {
        return b(WemburosBlocks.POLISHED_WEMBUROS_ONYX.get());
    }

    private static BlockState onyx() {
        return b(WemburosBlocks.WEMBUROS_ONYX.get());
    }

    private static BlockState wBricks() {
        return b(WemburosBlocks.WEMBUROS_BRICKS.get());
    }

    private static BlockState gilded() {
        return b(WemburosBlocks.GILDED_WEMBUROS_BRICKS.get());
    }

    private static BlockState gold() {
        return b(Blocks.GOLD_BLOCK);
    }

    /** Hauptmauerwerk: Tiefenschieferziegel mit Flecken aus Schwarzstein, Rissen und Fliesen. */
    private BlockState masonry(int u, int y, int v) {
        double h = rnd(0xA11L, u, y, v), patch = rnd(0xA12L, u >> 2, y / 3, v >> 2);
        if (patch < 0.22) return b(h < 0.3 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS);
        if (Math.floorMod(y, 8) == 5 && h > 0.2) return wBricks(); // lila Schicht im Mauerwerk
        if (h < 0.10) return b(Blocks.CRACKED_DEEPSLATE_BRICKS);
        if (h < 0.14) return b(Blocks.DEEPSLATE_TILES);
        return b(Blocks.DEEPSLATE_BRICKS);
    }

    /** Pflaster: Schwarzsteinziegel, rissig, mit Fliesen. */
    private BlockState paving(int u, int v) {
        double h = rnd(0xB01L, u, 0, v);
        if (h < 0.14) return b(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        if (h < 0.22) return b(Blocks.POLISHED_BLACKSTONE);
        if (h < 0.27) return b(Blocks.DEEPSLATE_TILES);
        return b(Blocks.POLISHED_BLACKSTONE_BRICKS);
    }

    /** Fels der Schlucht und Kaverne nach Tiefe: Schichten aus Schwarzstein, Basalt, Onyx; Gold- und Kristalladern. */
    private BlockState rock(int u, int y, int v) {
        double h = rnd(0xC01L, u, y, v);
        if (h < 0.025) return b(WemburosBlocks.WEMBUROS_GOLD_ORE.get());
        if (h < 0.04) return crystal();
        if (y > -4) return h < 0.5 ? b(WemburosBlocks.WEMBUROS_STONE.get()) : b(WemburosBlocks.WEMBUROS_REGOLITH.get());
        if (y < -30 && h < 0.3) return b(Blocks.MAGMA_BLOCK);
        if (y < -24 && h > 0.93) return b(Blocks.CRYING_OBSIDIAN);
        int band = Math.floorMod(y + (int) (noise(0xC02L, u, v, 9) * 4), 7);
        return switch (band) {
            case 0, 1 -> b(Blocks.BLACKSTONE);
            case 2 -> b(Blocks.BASALT);
            case 3 -> onyx();
            case 4 -> h < 0.5 ? b(Blocks.GILDED_BLACKSTONE) : b(Blocks.BLACKSTONE);
            default -> b(WemburosBlocks.WEMBUROS_STONE.get());
        };
    }

    // ── Gelaende ────────────────────────────────────────────────────────────

    /** Boden innerhalb der Mauern pflastern, ausserhalb Regolith mit Moos. */
    private void ground() {
        for (int u = ua(-R); u <= ub(R); u++) for (int v = va(-R); v <= vb(R); v++) {
            boolean inside = Math.abs(u) < WALL_U - 4 && v > WALL_N + 4 && v < WALL_S - 4;
            if (inside) {
                double moss = noise(0xB02L, u, v, 7);
                set(u, 0, v, moss > 0.72 ? b(WemburosBlocks.VIOLET_MOSS.get()) : moss > 0.66 ? b(WemburosBlocks.WEMBUROS_REGOLITH.get()) : paving(u, v));
                set(u, -1, v, b(WemburosBlocks.WEMBUROS_STONE.get()));
            } else {
                double moss = noise(0xB03L, u, v, 6);
                if (moss > 0.62) set(u, 0, v, b(WemburosBlocks.VIOLET_MOSS.get()));
            }
        }
    }

    /** Ausserhalb der Mauern: Felsdornen aus Onyx, Basalt und Schwarzstein. */
    private void outskirts() {
        for (int gu = -R; gu <= R; gu += 7) for (int gv = -R; gv <= R; gv += 7) {
            if (!touches(gu - 3, gv - 3, gu + 9, gv + 9)) continue;
            if (rnd(0xD01L, gu, 0, gv) < 0.35) continue;
            int u = gu + (int) (rnd(0xD02L, gu, 0, gv) * 6), v = gv + (int) (rnd(0xD03L, gu, 0, gv) * 6);
            if (Math.abs(u) > R - 3 || Math.abs(v) > R - 3) continue;
            boolean nearWall = Math.abs(u) < WALL_U + 6 && v > WALL_N - 7 && v < WALL_S + 10;
            boolean road = Math.abs(u) < 14 && v > 40;
            if (nearWall || road || inChasm(u, v, 0)) continue;
            spike(u, v, 4 + (int) (rnd(0xD04L, u, 0, v) * 10), rnd(0xD05L, u, 0, v));
        }
    }

    private void spike(int cu, int cv, int h, double kind) {
        BlockState main = kind < 0.4 ? onyx() : kind < 0.7 ? b(Blocks.BASALT) : b(Blocks.BLACKSTONE);
        for (int y = 0; y <= h; y++) {
            double r = 1.8 * (1 - y / (double) (h + 1)) + 0.3;
            for (int u = cu - 2; u <= cu + 2; u++) for (int v = cv - 2; v <= cv + 2; v++) {
                if (dist(u - cu, v - cv) > r) continue;
                set(u, y, v, y == h ? (kind > 0.85 ? crystal() : b(Blocks.GILDED_BLACKSTONE)) : main);
            }
        }
    }

    // ── Abgrund und Bruecke ─────────────────────────────────────────────────

    private double chasmHalf(int u) {
        double t = u / 86.0;
        return t * t >= 1 ? 0 : CHASM_HW * Math.sqrt(1 - t * t);
    }

    /** Liegt (u, v) auf Hoehe y in der Schlucht? Unten schmaler, Rand ausgefranst; vor der Torburg mindestens v 57. */
    private boolean inChasm(int u, int v, int y) {
        double hw = chasmHalf(u);
        if (hw <= 0.5) return false;
        double bottom = chasmBottom(u, v);
        if (y <= bottom) return false;
        double depthT = y >= 0 ? 1 : Math.max(0, (y - bottom) / -bottom);
        double half = hw * (0.55 + 0.45 * depthT) + (noise(0xE01L, u, v + y / 3, 4) - 0.5) * 2.4;
        if (Math.abs(u) < 24 && (v < 57 || v > 72)) return false;
        return Math.abs(v - CHASM_C) < half;
    }

    private double chasmBottom(int u, int v) {
        double t = u / 86.0;
        double depth = 6 + 34 * Math.sqrt(Math.max(0, 1 - t * t));
        return -depth + noise(0xE02L, u, v, 5) * 4;
    }

    private void chasm() {
        if (!touches(-R, 50, R, 80)) return;
        for (int u = ua(-86); u <= ub(86); u++) for (int v = va(50); v <= vb(80); v++) {
            int bottom = (int) Math.floor(chasmBottom(u, v));
            boolean any = false;
            for (int y = 24; y >= bottom - 2; y--) {
                if (inChasm(u, v, y)) {
                    any = true;
                    if (y <= LAVA + 6) set(u, y, v, b(Blocks.LAVA));
                    else air(u, y, v);
                } else if (y <= 0 && (inChasm(u, v - 1, y) || inChasm(u, v + 1, y) || inChasm(u - 1, v, y) || inChasm(u + 1, v, y)
                        || inChasm(u, v, y + 1))) {
                    set(u, y, v, rock(u, y, v));
                    // Kristalle an der Wand, zur Mitte gerichtet
                    if (y < -6 && y > LAVA + 8 && rnd(0xE03L, u, y, v) < 0.05) {
                        int dv = v < CHASM_C ? 1 : -1;
                        if (inChasm(u, v + dv, y)) set(u, y, v + dv, cluster(dv > 0 ? Direction.SOUTH : Direction.NORTH));
                    }
                }
            }
            if (any) set(u, bottom, v, rnd(0xE04L, u, 0, v) < 0.5 ? b(Blocks.MAGMA_BLOCK) : b(Blocks.BLACKSTONE));
        }
        // Basaltsaeulen aus dem Lavasee
        for (int i = 0; i < 11; i++) {
            int cu = -72 + i * 14 + (int) (rnd(0xE05L, i, 0, 0) * 8);
            int cv = (int) Math.round(CHASM_C + (rnd(0xE06L, i, 0, 0) - 0.5) * 5);
            if (Math.abs(cu) < 8 || !touches(cu - 2, cv - 2, cu + 2, cv + 2)) continue;
            int top = LAVA + 6 + (int) (rnd(0xE07L, i, 0, 0) * 22);
            double rr = 1.2 + rnd(0xE08L, i, 0, 0) * 1.3;
            for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) {
                double d = dist(u - cu, v - cv);
                if (d > rr || !inChasm(u, v, top - 1)) continue;
                int t = top - (int) (d * 2) - (int) (rnd(0xE09L, u, 0, v) * 2);
                for (int y = (int) chasmBottom(u, v); y <= t; y++) set(u, y, v, y == t && d < 0.8 ? b(Blocks.MAGMA_BLOCK) : pillar(Blocks.BASALT, Direction.Axis.Y));
            }
        }
    }

    /**
     * Bruecke (u -3..3, Deck y 0) von v 54 bis 75 auf zwei Pfeilern; die Mitte (v 62..68) ist eingestuerzt. Ueber der
     * Luecke ein Joch aus zwei Spitzbogentoren und einem Balken, daran haengen drei Trittsteine an Ketten.
     */
    private void bridge() {
        if (!touches(-8, 52, 8, 78)) return;
        BlockState pbb = b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int v = 54; v <= 75; v++) {
            if (v >= 62 && v <= 68) continue;
            for (int u = -3; u <= 3; u++) {
                boolean broken = (v == 61 || v == 69) && rnd(0xF01L, u, 0, v) < 0.35 && Math.abs(u) == 3;
                if (broken) continue;
                set(u, 0, v, u == 0 ? (v % 3 == 0 ? b(Blocks.GILDED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE)) : paving(u, v));
                set(u, -1, v, b(Blocks.POLISHED_BLACKSTONE));
                set(u, -2, v, Math.abs(u) == 3 ? stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, u < 0 ? Direction.EAST : Direction.WEST) : pbb);
            }
            for (int s = -1; s <= 1; s += 2) {
                if (v >= 59 && v <= 71 && (v == 61 || v == 69)) continue;
                boolean post = v % 5 == 0;
                set(s * 3, 1, v, b(post ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
                if (post) {
                    set(s * 3, 2, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
                    set(s * 3, 3, v, lantern(true, false));
                }
            }
        }
        // Pfeiler bis zum Grund, Spitzboegen zu den Ufern
        for (int[] pier : new int[][] {{59, 61}, {69, 71}}) {
            for (int u = -4; u <= 4; u++) for (int v = pier[0] - 1; v <= pier[1] + 1; v++) {
                boolean cutwater = v < pier[0] || v > pier[1];
                if (cutwater && Math.abs(u) > 2) continue;
                int bottom = (int) Math.floor(chasmBottom(u, v)) - 1;
                for (int y = bottom; y <= -1; y++) {
                    if (y == -1 && Math.abs(u) == 4) set(u, y, v, gilded());
                    else set(u, y, v, cutwater ? b(Blocks.POLISHED_BLACKSTONE) : masonry(u, y, v));
                }
            }
        }
        for (int v = 55; v <= 75; v++) {
            if (v >= 59 && v <= 71) continue;
            int x = v < 59 ? v - 55 : 75 - v;
            for (int u = -3; u <= 3; u++) for (int y = -9; y <= -3; y++) {
                if (y >= -3 - x) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            }
        }
        // Joch ueber der Luecke: Spitzbogentore auf beiden Pfeilern, Balken, haengende Trittsteine
        for (int gv : new int[] {60, 70}) {
            for (int u = -5; u <= 5; u++) for (int y = 1; y <= 15; y++) {
                boolean opening = arch(u, y - 6, 3.5);
                boolean ring = arch(u, y - 6, 5.5);
                if (!ring || opening) continue;
                BlockState s = Math.abs(u) <= 1 && y >= 13 ? gilded() : Math.abs(u) == 5 && y % 4 == 0 ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : masonry(u, y, gv);
                set(u, y, gv, s);
            }
            set(0, 15, gv, crystal());
            set(0, 16, gv, b(Blocks.END_ROD));
            for (int s = -1; s <= 1; s += 2) set(s * 4, 7, gv + (gv == 60 ? -1 : 1), lantern(true, true));
        }
        for (int v = 60; v <= 70; v++) for (int s = -1; s <= 1; s += 2) {
            set(s * 2, 13, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            set(s * 2, 14, v, slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
        }
        // Trittsteine (u -2..2) an zwei Ketten: Luecken 1, 2, 2
        for (int v : new int[] {63, 66}) {
            for (int u = -2; u <= 2; u++) {
                set(u, 0, v, Math.abs(u) == 2 ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, -1, v, stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, u < 0 ? Direction.EAST : Direction.WEST));
            }
            for (int s = -1; s <= 1; s += 2) for (int y = 1; y <= 12; y++) set(s * 2, y, v, b(Blocks.CHAIN));
        }
        // abgerissene Ketten und Truemmer
        for (int y = -1; y >= -6; y--) {
            set(-2, y, 62, b(Blocks.CHAIN));
            set(2, y, 68, b(Blocks.CHAIN));
        }
        for (int i = 0; i < 6; i++) {
            int u = (int) (rnd(0xF02L, i, 0, 0) * 9) - 4, v = 62 + (int) (rnd(0xF03L, i, 0, 0) * 7);
            int y = (int) Math.floor(chasmBottom(u, v)) + 1;
            if (y <= LAVA + 6) y = LAVA + 7;
            set(u, y, v, b(i % 2 == 0 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS));
        }
    }

    // ── Prozessionsstrasse ──────────────────────────────────────────────────

    private void approach() {
        if (!touches(-20, 72, 20, R)) return;
        for (int v = 73; v <= R; v++) for (int u = -6; u <= 6; u++) {
            if (inChasm(u, v, 0)) continue;
            BlockState s = Math.abs(u) == 6 ? b(Blocks.POLISHED_DEEPSLATE) : u == 0 ? (v % 3 == 0 ? b(Blocks.GILDED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE))
                    : paving(u, v);
            set(u, 0, v, s);
        }
        // Ruinenbogen am Anfang der Strasse (rechte Haelfte eingestuerzt)
        int av = 86;
        for (int d = 0; d <= 1; d++) {
            int v = av + d;
            for (int u = -9; u <= 9; u++) for (int y = 1; y <= 22; y++) {
                boolean pillarCell = Math.abs(u) >= 7 && y <= 12;
                boolean ring = arch(u, y - 12, 8.5) && !arch(u, y - 12, 6.5);
                if (!pillarCell && !ring) continue;
                if (u >= 2 && y >= 14) continue;
                if (u >= 7 && y > 12 - (int) (rnd(0xF11L, u, 0, d) * 4)) continue;
                BlockState s = Math.abs(u) <= 1 && y >= 19 ? gilded() : (Math.abs(u) == 9 && y % 5 == 0) ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : masonry(u, y, v);
                set(u, y, v, s);
            }
        }
        set(-8, 13, av, crystal());
        set(-8, 14, av, cluster(Direction.UP));
        set(-3, 15, av, lantern(true, true));
        // Truemmer der eingestuerzten Haelfte
        for (int i = 0; i < 10; i++) {
            int u = 4 + (int) (rnd(0xF12L, i, 0, 0) * 8), v = av - 3 + (int) (rnd(0xF13L, i, 0, 0) * 6);
            set(u, 1, v, rnd(0xF14L, i, 0, 0) < 0.5 ? b(Blocks.CRACKED_DEEPSLATE_BRICKS) : slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
        }
        statue(-13, 79, Direction.EAST);
        statue(13, 79, Direction.WEST);
        for (int v : new int[] {75, 83}) for (int s = -1; s <= 1; s += 2) brazier(s * 8, v);
    }

    /** Feuerschale: Sockel, Gold, Seelenlagerfeuer. */
    private void brazier(int u, int v) {
        set(u, 1, v, b(Blocks.CHISELED_POLISHED_BLACKSTONE));
        set(u, 2, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
        set(u, 3, v, gold());
        set(u, 4, v, b(Blocks.SOUL_CAMPFIRE));
    }

    /** Lokaler Rahmen fuer Figuren: a quer, f nach vorn (Blickrichtung {@code facing}). */
    private final class Local {
        final int cu, cv, k;
        final int fu, fv, au, av;

        Local(int cu, int cv, Direction facing) {
            this.cu = cu;
            this.cv = cv;
            this.k = switch (facing) {
                case WEST -> 1;
                case NORTH -> 2;
                case EAST -> 3;
                default -> 0;
            };
            int[] f = {0, 1}, a = {1, 0};
            for (int i = 0; i < k; i++) {
                f = new int[] {-f[1], f[0]};
                a = new int[] {-a[1], a[0]};
            }
            fu = f[0];
            fv = f[1];
            au = a[0];
            av = a[1];
        }

        void set(int a, int y, int f, BlockState s) {
            SovereignCitadel.this.set(cu + a * au + f * fu, y, cv + a * av + f * fv, s);
        }

        Direction d(Direction local) {
            Direction d = local;
            for (int i = 0; i < k; i++) d = d.getClockWise();
            return d;
        }
    }

    /** Waechter in Kapuze mit Kristallschwert, Umhang, leuchtenden Augen (19 hoch, Blick {@code facing}). */
    private void statue(int cu, int cv, Direction facing) {
        if (!touches(cu - 5, cv - 5, cu + 5, cv + 5)) return;
        Local l = new Local(cu, cv, facing);
        BlockState robe = b(Blocks.POLISHED_DEEPSLATE), fold = b(Blocks.DEEPSLATE_TILES), dark = polOnyx();
        // Sockel
        for (int a = -3; a <= 3; a++) for (int f = -3; f <= 3; f++) {
            boolean corner = Math.abs(a) == 3 && Math.abs(f) == 3;
            l.set(a, 0, f, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            l.set(a, 1, f, corner ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            if (Math.abs(a) == 3 || Math.abs(f) == 3) {
                Direction in = Math.abs(a) == 3 ? (a > 0 ? Direction.WEST : Direction.EAST) : (f > 0 ? Direction.NORTH : Direction.SOUTH);
                l.set(a, 2, f, corner ? gold() : stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, l.d(in)));
            } else {
                l.set(a, 2, f, gilded());
            }
        }
        // Gewand mit Falten, Guertel
        for (int y = 3; y <= 10; y++) for (int a = -2; a <= 2; a++) for (int f = -1; f <= 1; f++) {
            if (y <= 4 && Math.abs(a) == 2 && f == 1) continue;
            if (y >= 9 && Math.abs(a) == 2 && f == -1) continue;
            BlockState s = y == 7 ? b(Blocks.GILDED_BLACKSTONE) : Math.abs(a) == 1 && f == 1 ? fold : robe;
            l.set(a, y, f, s);
        }
        for (int a = -1; a <= 1; a++) l.set(a, 3, 2, fold);
        // Schultern, Arme zum Schwertgriff
        for (int a = -3; a <= 3; a++) {
            l.set(a, 11, 0, Math.abs(a) == 3 ? b(Blocks.POLISHED_BLACKSTONE) : robe);
            if (Math.abs(a) == 3) {
                l.set(a, 12, 0, slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
                l.set(a, 10, 0, fold);
            }
        }
        for (int s = -1; s <= 1; s += 2) {
            l.set(s * 2, 10, 1, fold);
            l.set(s, 9, 2, fold);
            l.set(s, 10, 2, fold);
        }
        // Kristallschwert, Spitze auf dem Sockel
        for (int y = 3; y <= 7; y++) l.set(0, y, 2, crystal());
        for (int a = -1; a <= 1; a++) l.set(a, 8, 2, gold());
        l.set(0, 9, 2, b(Blocks.POLISHED_BLACKSTONE));
        l.set(0, 10, 2, b(Blocks.POLISHED_BLACKSTONE));
        l.set(0, 11, 2, gold());
        // Kapuze mit dunklem Gesicht und Kristallaugen
        for (int y = 12; y <= 15; y++) for (int a = -2; a <= 2; a++) for (int f = -1; f <= 1; f++) {
            if (y == 15 && (Math.abs(a) == 2 || f == 1)) continue;
            boolean face = f == 1 && Math.abs(a) <= 1 && y <= 14;
            l.set(a, y, f, face ? (y == 14 && a != 0 ? crystal() : dark) : fold);
        }
        for (int a = -2; a <= 2; a++) l.set(a, 12, 1, b(Blocks.GILDED_BLACKSTONE));
        l.set(0, 16, -1, stairs(Blocks.DEEPSLATE_TILE_STAIRS, l.d(Direction.NORTH)));
        l.set(0, 16, 0, stairs(Blocks.DEEPSLATE_TILE_STAIRS, l.d(Direction.NORTH)));
        for (int a = -1; a <= 1; a++) l.set(a, 15, 1, a == 0 ? gold() : stairs(Blocks.DEEPSLATE_TILE_STAIRS, l.d(Direction.NORTH)));
        // Umhang
        for (int y = 3; y <= 11; y++) for (int a = -2; a <= 2; a++) {
            if (y <= 4 && Math.abs(a) == 2) continue;
            l.set(a, y, -2, b(Blocks.PURPLE_WOOL));
        }
        l.set(0, 2, -3, b(Blocks.PURPLE_WOOL));
    }

    // ── Aeussere Mauer ──────────────────────────────────────────────────────

    private boolean inCurtain(int u, int v) {
        int au = Math.abs(u);
        if (au > WALL_U || v > WALL_S || v < WALL_N) return false;
        return au >= WALL_U - 4 || v >= WALL_S - 4 || v <= WALL_N + 4;
    }

    /** 0 = innen, 1 = aussen (Kante der Mauer), sonst -1. */
    private int curtainEdge(int u, int v) {
        int au = Math.abs(u);
        if (au == WALL_U || v == WALL_S || v == WALL_N) return 1;
        if (au == WALL_U - 4 && v > WALL_N + 4 && v < WALL_S - 4) return 0;
        if ((v == WALL_S - 4 || v == WALL_N + 4) && au < WALL_U - 4) return 0;
        return -1;
    }

    /** Richtung nach aussen fuer eine Zelle der Aussenkante. */
    private static Direction curtainOut(int u, int v) {
        if (v == WALL_S) return Direction.SOUTH;
        if (v == WALL_N) return Direction.NORTH;
        return u > 0 ? Direction.EAST : Direction.WEST;
    }

    private void curtain() {
        for (int u = ua(-WALL_U - 2); u <= ub(WALL_U + 2); u++) for (int v = va(WALL_N - 2); v <= vb(WALL_S + 2); v++) {
            if (inCurtain(u, v)) {
                int edge = curtainEdge(u, v);
                for (int y = 0; y <= WALL_H; y++) {
                    BlockState s;
                    if (y <= 2) s = polOnyx();
                    else if (y == 11 && edge == 1) s = gilded();
                    else if (y == WALL_H - 2 && edge == 1) s = polOnyx();
                    else if (y == WALL_H) s = edge >= 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : (Math.floorMod(u + v, 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE) : b(Blocks.POLISHED_DEEPSLATE));
                    else s = masonry(u, y, v);
                    set(u, y, v, s);
                }
                if (edge == 0) set(u, WALL_H + 1, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
                // Schiessscharten (Eisengitter) zwischen den Strebepfeilern
                if (edge == 1) {
                    int along = v == WALL_S || v == WALL_N ? u : v;
                    if (Math.floorMod(along, 12) == 6) for (int y = 13; y <= 15; y++) set(u, y, v, b(Blocks.IRON_BARS));
                }
                continue;
            }
            // Zellen direkt vor der Aussenkante: Boeschung, Strebepfeiler, Maschikulis, Zinnen, Banner
            int au = Math.abs(u);
            Direction out;
            int along;
            if (au == WALL_U + 1 && v >= WALL_N && v <= WALL_S) {
                out = u > 0 ? Direction.EAST : Direction.WEST;
                along = v;
            } else if ((v == WALL_S + 1 || v == WALL_N - 1) && au <= WALL_U) {
                out = v > 0 ? Direction.SOUTH : Direction.NORTH;
                along = u;
            } else if (au == WALL_U + 2 && v >= WALL_N && v <= WALL_S) {
                if (Math.floorMod(v, 12) <= 1) buttressOuter(u, v, u > 0 ? Direction.EAST : Direction.WEST);
                continue;
            } else if ((v == WALL_S + 2 || v == WALL_N - 2) && au <= WALL_U) {
                if (Math.floorMod(u, 12) <= 1) buttressOuter(u, v, v > 0 ? Direction.SOUTH : Direction.NORTH);
                continue;
            } else {
                continue;
            }
            Direction in = out.getOpposite();
            boolean buttress = Math.floorMod(along, 12) <= 1;
            set(u, 1, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, in));
            if (buttress) {
                for (int y = 1; y <= 17; y++) set(u, y, v, y == 11 ? gilded() : masonry(u, y, v));
                set(u, 18, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, in));
            }
            set(u, WALL_H - 1, v, stairsTop(Blocks.DEEPSLATE_BRICK_STAIRS, in));
            set(u, WALL_H, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            boolean merlon = Math.floorMod(along, 3) != 0;
            if (merlon) {
                set(u, WALL_H + 1, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, WALL_H + 2, v, slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            }
            if (Math.floorMod(along, 12) == 6) {
                set(u, WALL_H - 2, v, lantern(true, true));
            }
            if (Math.floorMod(along, 24) == 18) set(u, WALL_H - 3, v, facing(Blocks.PURPLE_WALL_BANNER, out));
        }
        // Treppen vom Hof auf den Wehrgang (an der Suedmauer, beidseitig)
        for (int s = -1; s <= 1; s += 2) {
            for (int i = 0; i < WALL_H; i++) {
                int u = s * (30 + i);
                for (int v = WALL_S - 6; v <= WALL_S - 5; v++) {
                    set(u, i + 1, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, s > 0 ? Direction.EAST : Direction.WEST));
                    for (int y = 1; y <= i; y++) set(u, y, v, masonry(u, y, v));
                    for (int y = i + 2; y <= i + 4; y++) air(u, y, v);
                }
            }
            for (int u = 49; u <= 52; u++) air(s * u, WALL_H + 1, WALL_S - 4);
        }
    }

    private void buttressOuter(int u, int v, Direction out) {
        Direction in = out.getOpposite();
        set(u, 1, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, in));
        for (int y = 2; y <= 12; y++) set(u, y, v, masonry(u, y, v));
        set(u, 13, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, in));
    }

    // ── Tuerme ──────────────────────────────────────────────────────────────

    /**
     * Rundturm: Schale 2 dick, Geschosse alle 11 Bloecke mit Leiter, Fenster, Maschikulis, Zinnenkranz, spitzer Helm mit
     * Purpurband und Kristallspitze.
     */
    private void tower(int cu, int cv, int r, int h, int roofTop) {
        if (!touches(cu - r - 2, cv - r - 2, cu + r + 2, cv + r + 2)) return;
        for (int u = ua(cu - r - 2); u <= ub(cu + r + 2); u++) for (int v = va(cv - r - 2); v <= vb(cv + r + 2); v++) {
            double d = dist(u - cu, v - cv);
            double ang = angle(u - cu, v - cv);
            Direction out = toward(u - cu, v - cv);
            if (d <= r + 0.5) {
                boolean shell = d > r - 1.5;
                for (int y = 0; y <= h; y++) {
                    if (shell) {
                        BlockState s = y <= 2 ? polOnyx() : (y == h - 9 && d > r - 0.5) ? gilded() : masonry(u, y, v);
                        // Fenster in jedem Geschoss (vier Richtungen, versetzt)
                        int level = Math.floorMod(y, 11);
                        double off = Math.abs(spoke(ang, (y / 11) * 45.0, 90.0)) * Math.PI / 180 * r;
                        if (level >= 4 && level <= 7 && off < 0.8 && y < h - 2) {
                            s = d > r - 0.5 ? b(Blocks.PURPLE_STAINED_GLASS_PANE) : AIR;
                            if (level == 7) s = d > r - 0.5 ? stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, out.getOpposite()) : s;
                        }
                        set(u, y, v, s);
                    } else {
                        boolean floor = y % 11 == 0 || y == h;
                        set(u, y, v, floor ? (y == h ? (Math.floorMod(u + v, 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE) : b(Blocks.POLISHED_DEEPSLATE)) : b(Blocks.DARK_OAK_PLANKS)) : AIR);
                    }
                }
            } else if (d <= r + 1.5) {
                // Maschikulis und Zinnenkranz
                set(u, h - 1, v, stairsTop(Blocks.DEEPSLATE_BRICK_STAIRS, out.getOpposite()));
                set(u, h, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                boolean merlon = Math.floorMod((int) Math.round(ang / 9.0), 2) == 0;
                if (merlon) set(u, h + 1, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                else set(u, h + 1, v, slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
                set(u, 1, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, out.getOpposite()));
            }
            // Helm
            int base = h + 2;
            for (int y = base; y <= roofTop; y++) {
                double t = (y - base) / (double) (roofTop - base);
                double rr = (r - 0.5) * Math.pow(1 - t, 0.85);
                if (d > rr) continue;
                boolean surface = d > rr - 1.4;
                if (!surface) {
                    set(u, y, v, AIR);
                    continue;
                }
                int band = (y - base) % 7;
                BlockState s = band == 5 ? b(Blocks.PURPUR_BLOCK) : band == 6 ? gilded() : b(Blocks.DEEPSLATE_TILES);
                if (d > rr - 0.7 && y + 1 <= roofTop) {
                    double rrUp = (r - 0.5) * Math.pow(1 - (y + 1 - base) / (double) (roofTop - base), 0.85);
                    if (d > rrUp) s = band == 5 ? stairs(Blocks.PURPUR_STAIRS, out.getOpposite()) : stairs(Blocks.DEEPSLATE_TILE_STAIRS, out.getOpposite());
                }
                set(u, y, v, s);
            }
        }
        // Spitze
        set(cu, roofTop + 1, cv, gold());
        set(cu, roofTop + 2, cv, crystal());
        set(cu, roofTop + 3, cv, cluster(Direction.UP));
        // Leiter durch alle Geschosse an der Nordwand
        int lv = cv - r + 2;
        for (int y = 1; y < h; y++) set(cu, y, lv, facing(Blocks.LADDER, Direction.SOUTH));
        for (int y = 11; y < h; y += 11) set(cu, y, lv, facing(Blocks.LADDER, Direction.SOUTH));
        set(cu, h, lv, AIR);
        set(cu, h, lv, facing(Blocks.LADDER, Direction.SOUTH));
        // Geschoss-Licht
        for (int y = 10; y < h; y += 11) set(cu, y, cv, lantern(true, true));
    }

    /** Tuer in einer Turmschale: Zellen (u, v) entlang einer Richtung freimachen. */
    private void door(int u0, int v0, Direction dir, int len, int y0, int height) {
        for (int i = 0; i < len; i++) {
            int u = u0 + dir.getStepX() * i, v = v0 + dir.getStepZ() * i;
            for (int y = y0; y < y0 + height; y++) air(u, y, v);
        }
    }

    private void towers() {
        int[][] corners = {{-64, 44}, {64, 44}, {-64, -80}, {64, -80}};
        for (int[] c : corners) {
            tower(c[0], c[1], 7, 33, 54);
            // Wehrgang-Tueren
            door(c[0] - Integer.signum(c[0]) * 7, c[1], c[0] > 0 ? Direction.EAST : Direction.WEST, 3, WALL_H + 1, 2);
            door(c[0], c[1] - Integer.signum(c[1]) * 7, c[1] > 0 ? Direction.SOUTH : Direction.NORTH, 3, WALL_H + 1, 2);
        }
        for (int s = -1; s <= 1; s += 2) {
            tower(s * 64, -18, 6, 33, 50);
            door(s * 64, -18 + 6, Direction.NORTH, 3, WALL_H + 1, 2);
            door(s * 64, -18 - 6, Direction.SOUTH, 3, WALL_H + 1, 2);
            tower(s * 32, -80, 6, 33, 50);
            door(s * 32 + 6, -80, Direction.WEST, 3, WALL_H + 1, 2);
            door(s * 32 - 6, -80, Direction.EAST, 3, WALL_H + 1, 2);
        }
    }

    // ── Torburg ─────────────────────────────────────────────────────────────

    private void gatehouse() {
        if (!touches(-22, 38, 22, 56)) return;
        // Torblock zwischen den Tuermen
        for (int u = ua(-8); u <= ub(8); u++) for (int v = va(WALL_S - 6); v <= vb(53); v++) for (int y = 0; y <= 30; y++) {
            set(u, y, v, y <= 2 ? polOnyx() : y == 30 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : masonry(u, y, v));
        }
        for (int u = -8; u <= 8; u++) {
            set(u, 31, 53, Math.floorMod(u, 3) != 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : AIR);
            set(u, 31, WALL_S - 6, b(Blocks.POLISHED_BLACKSTONE_WALL));
        }
        tower(-13, 47, 8, 33, 56);
        tower(13, 47, 8, 33, 56);
        // Torgang mit Spitzbogen (9 breit, Scheitel ~ y 17)
        for (int u = -4; u <= 4; u++) for (int y = 1; y <= 18; y++) {
            if (!arch(u, y - 9, 4.5)) continue;
            for (int v = WALL_S - 6; v <= 53; v++) air(u, y, v);
        }
        for (int v = WALL_S - 6; v <= 53; v++) for (int u = -4; u <= 4; u++) set(u, 0, v, u == 0 ? b(Blocks.GILDED_BLACKSTONE) : paving(u, v));
        // Zierrahmen um das Tor, Wappen mit Kristall
        for (int u = -7; u <= 7; u++) for (int y = 1; y <= 22; y++) {
            if (arch(u, y - 9, 5.5) && !arch(u, y - 9, 4.5)) set(u, y, 54, gilded());
            else if (arch(u, y - 9, 6.5) && !arch(u, y - 9, 5.5) && y > 3) set(u, y, 54, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH));
        }
        for (int u = -3; u <= 3; u++) for (int y = 20; y <= 28; y++) {
            int dd = Math.abs(u) + Math.abs(y - 24);
            if (dd <= 4) set(u, y, 54, dd <= 1 ? crystal() : dd <= 2 ? gold() : dd == 3 ? b(Blocks.GILDED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        }
        for (int s = -1; s <= 1; s += 2) {
            for (int y = 19; y <= 28; y += 9) set(s * 6, y, 54, facing(Blocks.PURPLE_WALL_BANNER, Direction.SOUTH));
            set(s * 6, 12, 55, lantern(true, false));
            set(s * 6, 11, 55, b(Blocks.POLISHED_BLACKSTONE_WALL));
            set(s * 6, 10, 55, stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.NORTH));
        }
        // Fallgitter: aussen geschlossen, innen hochgezogen
        for (int u = -4; u <= 4; u++) for (int y = 1; y <= 18; y++) {
            if (!arch(u, y - 9, 4.5)) continue;
            set(u, y, 52, b(Blocks.IRON_BARS));
            if (y >= 9) set(u, y, WALL_S - 5, b(Blocks.IRON_BARS));
        }
        // Fackeln im Torgang
        for (int v = WALL_S - 3; v <= 50; v += 4) {
            set(-4, 6, v, facing(Blocks.SOUL_WALL_TORCH, Direction.EAST));
            set(4, 6, v, facing(Blocks.SOUL_WALL_TORCH, Direction.WEST));
        }
        // Wachstube im Westturm: Tuer von aussen (Sued), Tuer in den Torgang (Ost) hinter dem Fallgitter
        door(-13, 55, Direction.NORTH, 3, 1, 3);
        door(-13, 54, Direction.NORTH, 1, 4, 1);
        door(-7, 47, Direction.EAST, 3, 1, 3);
        set(-14, 1, 56, lantern(true, false));
        set(-12, 1, 56, lantern(true, false));
        for (int s = -1; s <= 1; s += 2) set(-13 + s, 4, 56, facing(Blocks.PURPLE_WALL_BANNER, Direction.SOUTH));
        // Einrichtung: Waffentisch, Faesser, Kerzen
        set(-17, 1, 47, b(Blocks.SMITHING_TABLE));
        set(-17, 1, 48, b(Blocks.FLETCHING_TABLE));
        set(-16, 1, 51, candles(Blocks.PURPLE_CANDLE, 3));
        set(-10, 1, 51, b(Blocks.COBWEB));
        set(-16, 1, 43, b(Blocks.COBWEB));
    }

    // ── Vorhof ──────────────────────────────────────────────────────────────

    private void bailey() {
        // Prachtstrasse vom Tor bis zum Graben
        for (int v = va(7); v <= vb(WALL_S - 7); v++) for (int u = ua(-5); u <= ub(5); u++) {
            BlockState s = Math.abs(u) == 5 ? b(Blocks.POLISHED_DEEPSLATE) : u == 0 ? (v % 4 == 0 ? gold() : b(Blocks.GILDED_BLACKSTONE))
                    : Math.abs(u) == 1 ? b(Blocks.POLISHED_BLACKSTONE) : paving(u, v);
            set(u, 0, v, s);
        }
        for (int v = 10; v <= 36; v += 6) for (int s = -1; s <= 1; s += 2) {
            int u = s * 7;
            set(u, 1, v, b(Blocks.CHISELED_POLISHED_BLACKSTONE));
            for (int y = 2; y <= 4; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
            set(u, 5, v, b(Blocks.GILDED_BLACKSTONE));
            set(u, 6, v, lantern(true, false));
        }
        altar(-26, 24);
        altar(26, 24);
    }

    /** Kristallaltar: achteckiges Becken mit Magma, Kristallobelisk, Amethyst. */
    private void altar(int cu, int cv) {
        if (!touches(cu - 8, cv - 8, cu + 8, cv + 8)) return;
        for (int u = cu - 7; u <= cu + 7; u++) for (int v = cv - 7; v <= cv + 7; v++) {
            int du = Math.abs(u - cu), dv = Math.abs(v - cv);
            double oct = Math.max(Math.max(du, dv), (du + dv) * 0.72);
            if (oct > 7.2) continue;
            if (oct > 6.2) {
                set(u, 0, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, 1, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, toward(cu - u, cv - v)));
            } else if (oct > 5.2) {
                set(u, 0, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, 1, v, Math.floorMod(u + v, 2) == 0 ? gilded() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            } else {
                set(u, -1, v, b(Blocks.MAGMA_BLOCK));
                set(u, 0, v, rnd(0x1A1L, u, 0, v) < 0.5 ? b(Blocks.MAGMA_BLOCK) : b(Blocks.AMETHYST_BLOCK));
                if (oct > 2.5 && rnd(0x1A2L, u, 1, v) < 0.18) set(u, 1, v, rnd(0x1A3L, u, 1, v) < 0.5 ? cluster(Direction.UP) : amethyst(Direction.UP));
            }
        }
        // Obelisk
        for (int u = cu - 1; u <= cu + 1; u++) for (int v = cv - 1; v <= cv + 1; v++) {
            set(u, 1, v, gilded());
            set(u, 2, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            for (int y = 3; y <= 6; y++) set(u, y, v, (u == cu || v == cv) ? crystal() : b(Blocks.AMETHYST_BLOCK));
        }
        for (int y = 7; y <= 12; y++) set(cu, y, cv, crystal());
        set(cu, 13, cv, cluster(Direction.UP));
        for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            set(cu + d.getStepX(), 7, cv + d.getStepZ(), cluster(d));
            set(cu + 2 * d.getStepX(), 1, cv + 2 * d.getStepZ(), gold());
            set(cu + 2 * d.getStepX(), 2, cv + 2 * d.getStepZ(), candles(Blocks.PURPLE_CANDLE, 4));
        }
    }

    /** Gotische Halle (Satteldach entlang v), Waende 1 dick mit Strebepfeilern und Lanzettfenstern. */
    private void hallHouse(int u0, int v0, int u1, int v1, int wallH, boolean ruined) {
        if (!touches(u0 - 2, v0 - 2, u1 + 2, v1 + 2)) return;
        int mid2 = u0 + u1; // doppelte Mitte
        int half = (u1 - u0) / 2;
        for (int u = ua(u0); u <= ub(u1); u++) for (int v = va(v0); v <= vb(v1); v++) {
            boolean wall = u == u0 || u == u1 || v == v0 || v == v1;
            set(u, 0, v, wall ? polOnyx() : b(Blocks.DARK_OAK_PLANKS));
            for (int y = 1; y <= wallH; y++) {
                if (!wall) {
                    air(u, y, v);
                    continue;
                }
                boolean side = u == u0 || u == u1;
                int along = side ? v - v0 : u - u0;
                boolean window = side && along % 4 == 2 && y >= 3 && y <= 6 && v != v0 && v != v1;
                BlockState s = y == wallH ? gilded() : masonry(u, y, v);
                if (window) s = y == 6 ? stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, u == u0 ? Direction.EAST : Direction.WEST) : b(Blocks.PURPLE_STAINED_GLASS_PANE);
                if (ruined && y > wallH - 4 + (int) (noise(0x2B1L, u, v, 3) * 6) - 2) s = AIR;
                set(u, y, v, s);
            }
        }
        // Strebepfeiler an den Langseiten
        for (int v = v0; v <= v1; v += 4) for (int s = 0; s <= 1; s++) {
            int u = s == 0 ? u0 - 1 : u1 + 1;
            Direction in = s == 0 ? Direction.EAST : Direction.WEST;
            for (int y = 1; y <= wallH - 2; y++) set(u, y, v, masonry(u, y, v));
            set(u, wallH - 1, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, in));
        }
        // Satteldach mit Giebeln, Firstkristallen
        for (int u = ua(u0 - 1); u <= ub(u1 + 1); u++) for (int v = va(v0 - 1); v <= vb(v1 + 1); v++) {
            int du2 = Math.abs(2 * u - mid2);
            int rise = half + 1 - (du2 + 1) / 2;
            if (rise < 0) continue;
            int y = wallH + rise;
            boolean ridge = du2 <= 1;
            boolean gable = v == v0 || v == v1;
            if (ruined && v < v0 + (v1 - v0) / 3) continue;
            Direction up = 2 * u < mid2 ? Direction.EAST : Direction.WEST;
            if (v >= v0 && v <= v1) {
                if (gable) {
                    for (int yy = wallH + 1; yy < y; yy++) {
                        boolean rose = dist(2 * u - mid2, 2 * (yy - wallH - half / 2)) < 4.5 && yy > wallH + 1;
                        set(u, yy, v, rose ? b(Blocks.MAGENTA_STAINED_GLASS) : masonry(u, yy, v));
                    }
                } else {
                    for (int yy = wallH + 1; yy < y; yy++) air(u, yy, v);
                }
            }
            if (ridge) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            else set(u, y, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, up));
            if (!ridge && v >= v0 && v <= v1 && y - 1 > wallH) set(u, y - 1, v, gable ? masonry(u, y - 1, v) : stairsTop(Blocks.DARK_OAK_STAIRS, up.getOpposite()));
            if (ridge && (v == v0 - 1 || v == v1 + 1) && du2 == 0) {
                set(u, y + 1, v, gold());
                set(u, y + 2, v, cluster(Direction.UP));
            }
        }
    }

    private void barracks() {
        int u0 = -58, u1 = -40, v0 = 12, v1 = 36;
        hallHouse(u0, v0, u1, v1, 10, false);
        if (!touches(u0, v0, u1, v1)) return;
        // Tor nach Osten
        for (int v = 22; v <= 26; v++) for (int y = 1; y <= 6; y++) if (arch(v - 24, y - 4, 2.5)) air(u1, y, v);
        set(u1 + 1, 5, 21, lantern(true, false));
        set(u1 + 1, 5, 27, lantern(true, false));
        // Stockbetten an den Waenden, Tische, Feuerstelle, Kronleuchter
        for (int v = 14; v <= 34; v += 4) {
            if (v >= 22 && v <= 26) continue;
            set(u0 + 1, 1, v, bed(Direction.WEST, true));
            set(u0 + 2, 1, v, bed(Direction.WEST, false));
            set(u0 + 1, 1, v + 1, CityPalette.facingAll(b(Blocks.BARREL), Direction.UP));
        }
        for (int v = 17; v <= 31; v += 7) {
            for (int u = -52; u <= -47; u++) {
                set(u, 1, v, b(Blocks.DARK_OAK_FENCE));
                set(u, 2, v, slab(Blocks.DARK_OAK_SLAB, false));
            }
            set(-50, 3, v, candles(Blocks.BLACK_CANDLE, 2));
        }
        for (int u = -51; u <= -47; u++) for (int y = 1; y <= 4; y++) {
            boolean hearth = u >= -50 && u <= -48 && y <= 2;
            set(u, y, v1 - 1, hearth ? (y == 1 && u == -49 ? b(Blocks.SOUL_CAMPFIRE) : AIR) : b(Blocks.BRICKS));
        }
        for (int y = 5; y <= 13; y++) set(-49, y, v1 - 1, b(Blocks.BRICKS));
        for (int v : new int[] {18, 30}) {
            for (int y = 7; y <= 13; y++) set(-49, y, v, b(Blocks.CHAIN));
            set(-49, 6, v, gold());
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                set(-49 + d.getStepX(), 6, v + d.getStepZ(), b(Blocks.GILDED_BLACKSTONE));
                set(-49 + d.getStepX(), 5, v + d.getStepZ(), lantern(true, true));
            }
        }
        set(-44, 1, 32, b(Blocks.COBWEB));
        set(-56, 3, 13, b(Blocks.COBWEB));
    }

    private void library() {
        int u0 = 40, u1 = 58, v0 = 12, v1 = 36;
        hallHouse(u0, v0, u1, v1, 10, true);
        if (!touches(u0, v0, u1, v1)) return;
        for (int v = 22; v <= 26; v++) for (int y = 1; y <= 6; y++) if (arch(v - 24, y - 4, 2.5)) air(u0, y, v);
        set(u0 - 1, 5, 21, lantern(true, false));
        set(u0 - 1, 5, 27, lantern(true, false));
        // Regalreihen, Lesepulte, Kerzen; hinten ein Kristallaltar
        for (int v = 15; v <= 31; v += 4) {
            if (v >= 22 && v <= 26) continue;
            for (int u = 46; u <= 54; u++) for (int y = 1; y <= 3; y++) {
                if (u == 50) continue;
                boolean gap = rnd(0x2C1L, u, y, v) < 0.15;
                set(u, y, v, gap ? AIR : b(Blocks.BOOKSHELF));
            }
            set(50, 1, v, facing(Blocks.LECTERN, Direction.SOUTH));
            set(50, 2, v + 1, candles(Blocks.PURPLE_CANDLE, 3));
        }
        for (int u = 52; u <= 56; u++) for (int v = 33; v <= 35; v++) set(u, 1, v, u == 54 && v == 34 ? crystal() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        set(54, 2, 34, cluster(Direction.UP));
        set(52, 2, 33, candles(Blocks.WHITE_CANDLE, 4));
        set(56, 2, 33, candles(Blocks.WHITE_CANDLE, 4));
        // Schutt im eingestuerzten Teil
        for (int i = 0; i < 18; i++) {
            int u = u0 + 1 + (int) (rnd(0x2C2L, i, 0, 0) * (u1 - u0 - 1)), v = v0 + 1 + (int) (rnd(0x2C3L, i, 0, 0) * 8);
            set(u, 1, v, rnd(0x2C4L, i, 0, 0) < 0.4 ? slab(Blocks.DEEPSLATE_TILE_SLAB, false) : b(Blocks.CRACKED_DEEPSLATE_BRICKS));
            if (rnd(0x2C5L, i, 0, 0) < 0.3) set(u, 2, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, Direction.NORTH));
        }
    }

    /** Graeberfeld im Westen: Grabsteine, Grabhuegel, kahle Daemmerholz-Baeume, Kerzen, kleine Gruft. */
    private void graveyard() {
        if (!touches(-66, -81, -37, 11)) return;
        for (int v = -72; v <= 2; v += 4) for (int u = -59; u <= -46; u += 3) {
            if (rnd(0x3A1L, u, 0, v) < 0.2) continue;
            set(u, 1, v, b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
            set(u, 2, v, rnd(0x3A2L, u, 0, v) < 0.3 ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
            set(u, 0, v + 1, b(Blocks.SOUL_SOIL));
            set(u, 0, v + 2, b(Blocks.SOUL_SOIL));
            set(u, 1, v + 1, rnd(0x3A3L, u, 0, v) < 0.4 ? candles(Blocks.BLACK_CANDLE, 1 + (int) (rnd(0x3A4L, u, 0, v) * 3)) : AIR);
        }
        for (int[] t : new int[][] {{-50, -63}, {-56, -41}, {-48, -20}, {-57, -5}, {-44, -70}, {-55, -57}, {-45, -45}}) deadTree(t[0], t[1]);
        // Einfriedung mit Tor und Laternen
        for (int u = ua(-61); u <= ub(-43); u++) for (int v = va(-76); v <= vb(6); v++) {
            boolean ring = u == -43 || v == 6 || v == -76;
            if (!ring || u == -43 && v >= -2 && v <= 1) continue;
            if (!isAir(u, 1, v)) continue;
            boolean post = Math.floorMod(u + v, 6) == 0;
            set(u, 1, v, post ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
            if (post) {
                set(u, 2, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
                if (Math.floorMod(u + v, 12) == 0) set(u, 3, v, lantern(true, false));
            }
        }
        for (int v = -3; v <= 2; v += 5) {
            for (int y = 1; y <= 4; y++) set(-43, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            set(-43, 5, v, gold());
            set(-43, 6, v, crystal());
        }
        for (int v = -2; v <= 1; v++) set(-43, 5, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        // Gruft
        int cu = -52, cv = -32;
        for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) for (int y = 0; y <= 6; y++) {
            boolean edge = Math.abs(u - cu) == 3 || Math.abs(v - cv) == 3;
            BlockState s = y == 0 ? polOnyx() : y >= 5 ? (y == 6 && (Math.abs(u - cu) == 3 || Math.abs(v - cv) == 3) ? AIR : b(Blocks.POLISHED_BLACKSTONE_BRICKS)) : edge ? masonry(u, y, v) : AIR;
            set(u, y, v, s);
        }
        for (int y = 1; y <= 3; y++) air(cu + 3, y, cv);
        set(cu, 7, cv, b(Blocks.GILDED_BLACKSTONE));
        set(cu, 8, cv, crystal());
        set(cu - 2, 1, cv, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        set(cu - 2, 2, cv, candles(Blocks.PURPLE_CANDLE, 4));
    }

    private void deadTree(int cu, int cv) {
        if (!touches(cu - 5, cv - 5, cu + 5, cv + 5)) return;
        BlockState log = b(WemburosBlocks.DUSKWOOD_LOG.get());
        int h = 7 + (int) (rnd(0x3B1L, cu, 0, cv) * 4);
        for (int y = 1; y <= h; y++) set(cu, y, cv, log);
        for (int i = 0; i < 4; i++) {
            Direction d = Direction.from2DDataValue(i);
            int len = 2 + (int) (rnd(0x3B2L, cu, i, cv) * 3);
            int y = h - 2 - i % 2 * 2;
            for (int j = 1; j <= len; j++) set(cu + d.getStepX() * j, y + j / 2, cv + d.getStepZ() * j, CityPalette.log(log, d.getAxis()));
        }
        set(cu, 0, cv, b(WemburosBlocks.VIOLET_MOSS.get()));
    }

    /** Ruinierte Schmiede im Osten: Esse, Ambosse, Hochofen, HBM-Stahlplatten. */
    private void forge() {
        int u0 = 44, u1 = 58, v0 = -66, v1 = -46;
        if (!touches(u0 - 1, v0 - 1, u1 + 1, v1 + 1)) return;
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean wall = u == u0 || u == u1 || v == v0 || v == v1;
            set(u, 0, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            int top = wall ? 6 - (int) (noise(0x3C1L, u, v, 3) * 4) : 0;
            for (int y = 1; y <= 6; y++) set(u, y, v, wall && y <= top ? (y % 3 == 0 ? b(ModBlocks.BRICK_FIRE.get()) : masonry(u, y, v)) : AIR);
        }
        for (int v = -58; v <= -54; v++) for (int y = 1; y <= 3; y++) air(u0, y, v);
        // Esse mit Kamin
        for (int u = 54; u <= 57; u++) for (int v = -65; v <= -62; v++) for (int y = 1; y <= 4; y++) {
            boolean inner = u >= 55 && u <= 56 && v >= -64 && v <= -63 && y <= 2;
            set(u, y, v, inner ? (y == 1 ? b(Blocks.MAGMA_BLOCK) : AIR) : b(ModBlocks.BRICK_FIRE.get()));
        }
        for (int y = 5; y <= 14; y++) for (int u = 55; u <= 56; u++) set(u, y, -64, b(Blocks.BRICKS));
        set(51, 1, -60, b(Blocks.ANVIL));
        set(48, 1, -60, b(Blocks.BLAST_FURNACE));
        set(48, 2, -60, b(Blocks.BLAST_FURNACE));
        set(47, 1, -50, b(ModBlocks.DECO_STEEL.get()));
        set(48, 1, -50, b(ModBlocks.DECO_LEAD.get()));
        set(47, 2, -50, b(ModBlocks.DECO_STEEL.get()));
        set(52, 1, -49, b(Blocks.CHAIN));
        set(56, 1, -49, CityPalette.facingAll(b(Blocks.BARREL), Direction.UP));
        set(46, 1, -64, b(Blocks.SMITHING_TABLE));
    }

    /** Hofgaerten: leuchtende Daemmerholz-Baeume in Hochbeeten, Laternen am Graben, Schutthaufen. */
    private void gardens() {
        for (int[] t : new int[][] {{-13, 11}, {13, 11}, {-13, 37}, {13, 37}, {-36, 4}, {36, 4}, {-24, 38}, {24, 38}, {-30, -64}, {30, -64}}) planterTree(t[0], t[1]);
        // Laternenpfosten am Grabenrand
        for (int k = 0; k < 24; k++) {
            double a = Math.toRadians(k * 15 + 7.5);
            int u = (int) Math.round(Math.cos(a) * (MOAT_OUT + 3)), v = KV + (int) Math.round(Math.sin(a) * (MOAT_OUT + 3));
            if (!col(u, v) || !isAir(u, 1, v) || Math.abs(u) <= 6 && v > 0) continue;
            BlockState g = get(u, 0, v);
            if (g == null || g.isAir()) continue;
            set(u, 1, v, b(Blocks.CHISELED_POLISHED_BLACKSTONE));
            set(u, 2, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
            set(u, 3, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
            set(u, 4, v, b(Blocks.GILDED_BLACKSTONE));
            set(u, 5, v, lantern(true, false));
        }
        // Schutthaufen
        for (int i = 0; i < 40; i++) {
            int u = -60 + (int) (rnd(0x3D1L, i, 0, 0) * 121), v = -76 + (int) (rnd(0x3D2L, i, 0, 0) * 117);
            if (!touches(u - 2, v - 2, u + 2, v + 2) || Math.abs(u) <= 6 && v > 0) continue;
            for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) {
                int uu = u + du, vv = v + dv;
                double d = dist(du, dv) + rnd(0x3D3L, uu, i, vv) * 1.2;
                if (d > 2.4 || !col(uu, vv) || !isAir(uu, 1, vv)) continue;
                BlockState g = get(uu, 0, vv);
                if (g == null || !isGround(g)) continue;
                double h = rnd(0x3D4L, uu, 1, vv);
                set(uu, 1, vv, d < 1.2 ? (h < 0.5 ? b(Blocks.CRACKED_DEEPSLATE_BRICKS) : b(Blocks.COBBLED_DEEPSLATE))
                        : h < 0.4 ? slab(Blocks.COBBLED_DEEPSLATE_SLAB, false) : h < 0.6 ? slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false) : AIR);
                if (d < 0.8 && h < 0.5) set(uu, 2, vv, stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.from2DDataValue(i)));
            }
        }
    }

    /** Offener Boden (Pflaster, Moos, Regolith), auf dem Schutt liegen darf. */
    private static boolean isGround(BlockState g) {
        return g.is(Blocks.POLISHED_BLACKSTONE_BRICKS) || g.is(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS) || g.is(Blocks.POLISHED_BLACKSTONE)
                || g.is(Blocks.DEEPSLATE_TILES) || g.is(WemburosBlocks.VIOLET_MOSS.get()) || g.is(WemburosBlocks.WEMBUROS_REGOLITH.get());
    }

    /** Hochbeet (5 x 5) mit leuchtendem Daemmerholz-Baum. */
    private void planterTree(int cu, int cv) {
        if (!touches(cu - 4, cv - 4, cu + 4, cv + 4)) return;
        BlockState leaves = CityPalette.with(b(WemburosBlocks.DUSKWOOD_LEAVES.get()), BlockStateProperties.PERSISTENT, true);
        BlockState log = b(WemburosBlocks.DUSKWOOD_LOG.get());
        for (int u = cu - 2; u <= cu + 2; u++) for (int v = cv - 2; v <= cv + 2; v++) {
            boolean edge = Math.abs(u - cu) == 2 || Math.abs(v - cv) == 2;
            set(u, 1, v, edge ? (Math.abs(u - cu) == 2 && Math.abs(v - cv) == 2 ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : b(Blocks.POLISHED_BLACKSTONE_BRICKS))
                    : b(WemburosBlocks.VIOLET_MOSS.get()));
            if (edge) set(u, 2, v, slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
        }
        int h = 6 + (int) (rnd(0x3E1L, cu, 0, cv) * 3);
        for (int y = 2; y <= h; y++) set(cu, y, cv, log);
        for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) for (int y = h - 2; y <= h + 3; y++) {
            double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv) + (y - h) * (y - h) * 1.6);
            if (d > 3.2 - rnd(0x3E2L, u, y, v) * 0.6) continue;
            if (u == cu && v == cv && y <= h) continue;
            set(u, y, v, u == cu && v == cv && y == h + 1 ? b(Blocks.PEARLESCENT_FROGLIGHT) : leaves);
        }
        set(cu, h + 1, cv, b(Blocks.PEARLESCENT_FROGLIGHT));
    }

    // ── Graben und innere Mauer ─────────────────────────────────────────────

    private void moat() {
        int r = MOAT_OUT + 2;
        for (int u = ua(-r); u <= ub(r); u++) for (int v = va(KV - r); v <= vb(KV + r); v++) {
            double d = dist(u, v - KV);
            if (d > MOAT_OUT + 1.5 || d < MOAT_IN - 1.5) continue;
            if (d > MOAT_OUT + 0.5) {
                for (int y = -12; y <= 0; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                boolean gap = Math.floorMod((int) Math.round(angle(u, v - KV) / 4.0), 3) == 0;
                set(u, 1, v, gap ? AIR : b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
                continue;
            }
            if (d < MOAT_IN - 0.5) {
                for (int y = -12; y <= 0; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                continue;
            }
            set(u, -12, v, rnd(0x4A1L, u, 0, v) < 0.2 ? b(Blocks.MAGMA_BLOCK) : b(Blocks.SOUL_SOIL));
            for (int y = -11; y <= 0; y++) air(u, y, v);
            double h = rnd(0x4A2L, u, 0, v);
            if (h < 0.3) set(u, -11, v, b(Blocks.SOUL_FIRE));
            else if (h < 0.36) set(u, -11, v, b(Blocks.BONE_BLOCK));
            else if (h < 0.4) set(u, -11, v, cluster(Direction.UP));
        }
    }

    private void innerWall() {
        int r = INNER_OUT + 6;
        if (!touches(-r, KV - r, r, KV + r)) return;
        for (int u = Math.max(-r, p.uMin() - 1); u <= Math.min(r, p.uMax() + 1); u++) for (int v = Math.max(KV - r, p.vMin() - 1); v <= Math.min(KV + r, p.vMax() + 1); v++) {
            double d = dist(u, v - KV);
            double ang = angle(u, v - KV);
            Direction out = toward(u, v - KV);
            if (d >= INNER_IN - 0.5 && d <= INNER_OUT + 0.5) {
                boolean outer = d > INNER_OUT - 0.5, inner = d < INNER_IN + 0.5;
                for (int y = -11; y <= 24; y++) {
                    BlockState s = y <= 2 ? polOnyx() : y == 12 && outer ? gilded() : y == 24 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : masonry(u, y, v);
                    set(u, y, v, s);
                }
                if (outer) {
                    boolean merlon = Math.floorMod((int) Math.round(ang / 3.0), 3) != 0;
                    set(u, 25, v, merlon ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : AIR);
                    if (merlon) set(u, 26, v, slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
                }
                if (inner) set(u, 25, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
                // Banner an der Aussenseite
                if (outer && Math.abs(spoke(ang, 0, 30)) < 1.2 && Math.abs(spoke(ang, 22.5, 45)) > 8) {
                    int bu = u + out.getStepX(), bv = v + out.getStepZ();
                    if (dist(bu, bv - KV) > INNER_OUT + 0.5) {
                        set(bu, 20, bv, facing(Blocks.BLACK_WALL_BANNER, out));
                        set(bu, 21, bv, stairsTop(Blocks.DEEPSLATE_BRICK_STAIRS, out.getOpposite()));
                    }
                }
            }
        }
        // Tuermchen an acht Stellen
        for (int k = 0; k < 8; k++) {
            double a = Math.toRadians(22.5 + 45 * k);
            int tu = (int) Math.round(Math.cos(a) * 31.5), tv = KV + (int) Math.round(Math.sin(a) * 31.5);
            turret(tu, tv);
        }
        innerGate();
    }

    /** Tuermchen der inneren Mauer: rund, Kegeldach mit Purpurstufen, Kristall. */
    private void turret(int cu, int cv) {
        int r = 4, h = 32, top = 46;
        if (!touches(cu - r - 1, cv - r - 1, cu + r + 1, cv + r + 1)) return;
        for (int u = cu - r - 1; u <= cu + r + 1; u++) for (int v = cv - r - 1; v <= cv + r + 1; v++) {
            double d = dist(u - cu, v - cv);
            Direction out = toward(u - cu, v - cv);
            if (d <= r + 0.5) {
                for (int y = -11; y <= h; y++) set(u, y, v, y <= 2 ? polOnyx() : y == h ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : y % 8 == 0 ? gilded() : masonry(u, y, v));
                if (d > r - 0.5 && y28window(u - cu, v - cv)) for (int y = 26; y <= 28; y++) set(u, y, v, b(Blocks.PURPLE_STAINED_GLASS));
            } else if (d <= r + 1.5) {
                set(u, h - 1, v, stairsTop(Blocks.DEEPSLATE_BRICK_STAIRS, out.getOpposite()));
                set(u, h, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, h + 1, v, Math.floorMod(u + v, 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : AIR);
            }
            for (int y = h + 1; y <= top; y++) {
                double t = (y - h - 1) / (double) (top - h - 1);
                double rr = (r + 0.2) * Math.pow(1 - t, 0.9);
                if (d > rr) continue;
                double rrUp = (r + 0.2) * Math.pow(1 - (y - h) / (double) (top - h - 1), 0.9);
                BlockState s = (y - h) % 5 == 0 ? b(Blocks.PURPUR_BLOCK) : b(Blocks.DEEPSLATE_TILES);
                if (d > rrUp) s = (y - h) % 5 == 0 ? stairs(Blocks.PURPUR_STAIRS, out.getOpposite()) : stairs(Blocks.DEEPSLATE_TILE_STAIRS, out.getOpposite());
                set(u, y, v, s);
            }
        }
        set(cu, top + 1, cv, gold());
        set(cu, top + 2, cv, crystal());
        set(cu, top + 3, cv, b(Blocks.END_ROD));
    }

    private static boolean y28window(int du, int dv) {
        return du == 0 || dv == 0;
    }

    /** Inneres Torhaus im Sueden mit hochgezogener Zugbruecke und abgerissenen Ketten; Brueckenstumpf ueber dem Graben. */
    private void innerGate() {
        int gv = KV + 31;
        if (!touches(-8, gv - 5, 8, KV + MOAT_OUT + 3)) return;
        for (int u = -7; u <= 7; u++) for (int v = gv - 4; v <= gv + 3; v++) for (int y = -11; y <= 30; y++) {
            set(u, y, v, y <= 2 ? polOnyx() : y == 30 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : masonry(u, y, v));
        }
        for (int u = -7; u <= 7; u++) set(u, 31, gv + 3, Math.floorMod(u, 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : AIR);
        for (int u = -3; u <= 3; u++) for (int y = 1; y <= 13; y++) {
            if (!arch(u, y - 7, 3.5)) continue;
            for (int v = gv - 4; v <= gv + 3; v++) air(u, y, v);
        }
        // Zugbruecke hochgeklappt vor dem Tor
        for (int u = -4; u <= 4; u++) for (int y = 1; y <= 12; y++) {
            set(u, y, gv + 4, Math.abs(u) == 4 || y % 4 == 0 ? b(Blocks.DARK_OAK_LOG) : b(Blocks.DARK_OAK_PLANKS));
        }
        for (int s = -1; s <= 1; s += 2) for (int y = 13; y <= 16; y++) set(s * 4, y, gv + 4, b(Blocks.CHAIN));
        for (int u = -5; u <= 5; u++) for (int y = 17; y <= 24; y++) {
            int dd = Math.abs(u) + Math.abs(y - 20);
            if (dd <= 3) set(u, y, gv + 4, dd == 0 ? crystal() : dd == 1 ? gold() : b(Blocks.GILDED_BLACKSTONE));
        }
        // Brueckenstumpf von aussen, ein Stueck Graben dazwischen
        int outerV = KV + MOAT_OUT + 1;
        for (int v = outerV - 4; v <= outerV; v++) for (int u = -4; u <= 4; u++) {
            if (v == outerV - 4 && rnd(0x4B1L, u, 0, v) < 0.5) continue;
            set(u, 0, v, Math.abs(u) == 4 ? b(Blocks.POLISHED_DEEPSLATE) : paving(u, v));
            set(u, -1, v, stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.SOUTH));
            if (Math.abs(u) == 4) set(u, 1, v, b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
        }
        for (int y = -1; y >= -6; y--) set(-3, y, outerV - 4, b(Blocks.CHAIN));
        set(-4, 2, outerV, lantern(true, false));
        set(4, 2, outerV, lantern(true, false));
    }

    // ── Bergfried (Rotunde mit Kuppel) ──────────────────────────────────────

    private static final double DOME_BASE = 42, DOME_H = 27, DOME_IN_H = 25;
    /** Hoehe der Strebepfeiler am Bergfried. */
    private static final int PIER = 36;

    private void keep() {
        int r = KEEP + 6;
        if (!touches(-r, KV - r, r, KV + r)) return;
        // eine Spalte Rand: Gesims und Sockel schreiben in die Nachbarspalte
        for (int u = Math.max(-r, p.uMin() - 1); u <= Math.min(r, p.uMax() + 1); u++) for (int v = Math.max(KV - r, p.vMin() - 1); v <= Math.min(KV + r, p.vMax() + 1); v++) {
            int dv = v - KV;
            double d = dist(u, dv);
            double ang = angle(u, dv);
            Direction out = toward(u, dv);
            // Innenhof zwischen Rotunde und Mauer: Weg, Moos, Amethyst
            if (d > KEEP + 0.5 && d < INNER_IN - 0.5) {
                double ring = Math.abs(d - 26.5);
                set(u, 0, v, ring < 0.8 ? b(Blocks.POLISHED_DEEPSLATE) : rnd(0x5A1L, u, 0, v) < 0.6 ? b(WemburosBlocks.VIOLET_MOSS.get()) : b(WemburosBlocks.WEMBUROS_REGOLITH.get()));
                if (ring > 1.2 && rnd(0x5A2L, u, 1, v) < 0.05) set(u, 1, v, rnd(0x5A3L, u, 1, v) < 0.5 ? amethyst(Direction.UP) : cluster(Direction.UP));
            }
            // Strebepfeiler an acht Stellen (zwischen den Fenstern)
            double lat = Math.abs(spoke(ang, 22.5, 45)) * Math.PI / 180 * d;
            if (d > KEEP + 0.5 && d <= KEEP + 4.5 && lat <= 1.6) {
                double t = d - KEEP;
                int top = t <= 1.5 ? PIER : t <= 2.5 ? PIER - 8 : t <= 3.5 ? PIER - 18 : 10;
                for (int y = 0; y <= top; y++) set(u, y, v, y <= 2 ? polOnyx() : y % 8 == 0 ? gilded() : masonry(u, y, v));
                set(u, top + 1, v, stairs(Blocks.DEEPSLATE_TILE_STAIRS, out.getOpposite()));
            }
            if (d > KEEP + 0.5) continue;
            // Sockel und Saalboden
            set(u, 0, v, polOnyx());
            if (d <= KEEP - 2.5) {
                set(u, FLOOR, v, hallFloor(u, dv, d, ang));
            }
            // Rotundenwand
            if (d > KEEP - 2.5) {
                int layer = d > KEEP - 0.5 ? 2 : d > KEEP - 1.5 ? 1 : 0; // 2 aussen, 0 innen
                for (int y = 1; y <= (int) DOME_BASE; y++) set(u, y, v, keepWall(u, y, v, dv, d, ang, layer, out));
                if (layer == 2) {
                    // Gesims mit Konsolen und Brustwehr
                    int cu = u + out.getStepX(), cv = v + out.getStepZ();
                    if (dist(cu, cv - KV) > KEEP + 0.5) {
                        set(cu, (int) DOME_BASE - 1, cv, stairsTop(Blocks.DEEPSLATE_BRICK_STAIRS, out.getOpposite()));
                        set(cu, (int) DOME_BASE, cv, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                        set(cu, (int) DOME_BASE + 1, cv, Math.floorMod((int) Math.round(ang / 4.0), 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL) : AIR);
                        set(cu, 2, cv, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, out.getOpposite()));
                    }
                }
            } else {
                for (int y = FLOOR + 1; y <= (int) DOME_BASE; y++) air(u, y, v);
            }
            // Kuppel
            for (int y = (int) DOME_BASE + 1; y <= (int) (DOME_BASE + DOME_H) + 1; y++) {
                double ty = (y - DOME_BASE) / DOME_H, tyi = (y - DOME_BASE) / DOME_IN_H;
                double ro = ty >= 1 ? -1 : (KEEP + 0.5) * Math.sqrt(1 - ty * ty);
                double ri = tyi >= 1 ? -1 : (KEEP - 1.5) * Math.sqrt(1 - tyi * tyi);
                if (d > ro) continue;
                if (d <= 4.5) {
                    air(u, y, v);
                    continue;
                }
                if (d <= ri) {
                    air(u, y, v);
                    continue;
                }
                set(u, y, v, domeBlock(u, y, v, d, ang, ri));
            }
        }
        // Laterne um das Opaion: acht Saeulchen, Goldkrone mit Kristallzacken
        int top = (int) (DOME_BASE + DOME_H);
        for (int u = ua(-8); u <= ub(8); u++) for (int v = va(KV - 8); v <= vb(KV + 8); v++) {
            double d = dist(u, v - KV);
            double ang = angle(u, v - KV);
            if (d > 4.5 && d <= 5.5) {
                set(u, top - 3, v, gold());
                boolean post = Math.abs(spoke(ang, 0, 45)) < 7;
                for (int y = top - 2; y <= top + 3; y++) set(u, y, v, post ? b(Blocks.PURPUR_PILLAR) : y == top - 2 ? b(Blocks.POLISHED_BLACKSTONE_BRICK_WALL) : AIR);
                set(u, top + 4, v, gold());
                if (post) {
                    set(u, top + 5, v, b(Blocks.GILDED_BLACKSTONE));
                    set(u, top + 6, v, crystal());
                    set(u, top + 7, v, cluster(Direction.UP));
                }
            } else if (d > 5.5 && d <= 6.5) {
                set(u, top - 3, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(u, top - 2, v, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, toward(-u, KV - v)));
            }
        }
        // Fialen auf den Strebepfeilern
        for (int k = 0; k < 8; k++) {
            double a = Math.toRadians(22.5 + 45 * k);
            int pu = (int) Math.round(Math.cos(a) * (KEEP + 1.5)), pv = KV + (int) Math.round(Math.sin(a) * (KEEP + 1.5));
            if (!touches(pu - 1, pv - 1, pu + 1, pv + 1)) continue;
            for (int y = PIER + 1; y <= PIER + 10; y++) set(pu, y, pv, y == PIER + 1 ? gilded() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                set(pu + d.getStepX(), PIER + 1, pv + d.getStepZ(), stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, d.getOpposite()));
                set(pu + d.getStepX(), PIER + 6, pv + d.getStepZ(), stairs(Blocks.DEEPSLATE_TILE_STAIRS, d.getOpposite()));
                set(pu + d.getStepX(), PIER + 7, pv + d.getStepZ(), b(Blocks.POLISHED_BLACKSTONE_WALL));
            }
            set(pu, PIER + 11, pv, gold());
            set(pu, PIER + 12, pv, crystal());
            set(pu, PIER + 13, pv, cluster(Direction.UP));
            // Strebebogen vom Tuermchen der inneren Mauer zum Gesims
            for (double dd = KEEP + 2.0; dd <= INNER_IN + 0.5; dd += 0.3) {
                int bu = (int) Math.round(Math.cos(a) * dd), bv = KV + (int) Math.round(Math.sin(a) * dd);
                double t = (dd - (KEEP + 2.0)) / (INNER_IN + 0.5 - (KEEP + 2.0));
                int y = (int) Math.round(DOME_BASE - 1 - t * 8);
                Direction toKeep = toward(-Math.cos(a), -Math.sin(a));
                set(bu, y, bv, stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, toKeep));
                set(bu, y - 1, bv, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                set(bu, y - 2, bv, stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, toKeep.getOpposite()));
            }
        }
    }

    /** Wand der Rotunde: Baender, Lanzettfenster (16, ohne Tor- und Thronseite), Rosette ueber dem versiegelten Tor. */
    private BlockState keepWall(int u, int y, int v, int dv, double d, double ang, int layer, Direction out) {
        // Rosette im Sueden (Mitte y 23, Radius 8)
        double ry = y - 27;
        double rho = dist(u, ry) * 8.0 / 9.5;
        if (dv > 0 && rho <= 8.5 && Math.abs(u) <= 11) {
            if (layer != 1) return rho > 7.5 ? (layer == 2 ? gold() : b(Blocks.POLISHED_BLACKSTONE_BRICKS)) : AIR;
            return roseWindow(u, ry, rho);
        }
        // versiegeltes Haupttor unter der Rosette
        if (dv > 0 && y >= 2 && arch(u, y - 9, 3.5)) {
            return switch (layer) {
                case 2 -> AIR;
                case 1 -> (Math.abs(u) + y) % 4 == 0 ? gold() : b(Blocks.CRYING_OBSIDIAN);
                default -> Math.abs(u) == 0 && y < 10 ? b(Blocks.DARK_OAK_LOG) : b(Blocks.DARK_OAK_PLANKS);
            };
        }
        if (dv > 0 && y >= 2 && arch(u, y - 9, 4.5)) return layer == 2 ? gilded() : b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        // Lanzettfenster
        double off = (Math.abs(spoke(ang, 0, 45)) - 10) * Math.PI / 180 * d; // Fensterpaare bei 45k +- 10 Grad
        boolean southZone = Math.abs(ang - 90) < 30, northZone = Math.abs(ang - 270) < 24;
        if (!southZone && !northZone && Math.abs(off) <= 1.6 && y >= 6 && arch(off, y - 30, 1.6)) {
            if (layer == 1) {
                if (y == 18) return b(Blocks.POLISHED_BLACKSTONE);
                if (y >= 31) return b(Blocks.YELLOW_STAINED_GLASS);
                return Math.abs(off) < 0.6 ? b(Blocks.MAGENTA_STAINED_GLASS) : b(Blocks.PURPLE_STAINED_GLASS);
            }
            return layer == 2 && Math.abs(off) > 1.2 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : AIR;
        }
        if (layer == 2) {
            if (y <= 3) return polOnyx();
            if (y == 14 || y == 34) return gilded();
            if (y == (int) DOME_BASE) return b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        if (layer == 0) {
            if (y == FLOOR + 1) return b(Blocks.POLISHED_BLACKSTONE_BRICKS);
            if (y == 14) return b(Blocks.GILDED_BLACKSTONE);
            if (y == 34) return gilded();
        }
        return masonry(u, y, v);
    }

    /** Rosette: Mitte Kristall, Bluetenblaetter magenta, Goldring, Speichen, Aussenkranz in Lila/Schwarz/Gelb. */
    private BlockState roseWindow(int u, double ry, double rho) {
        double a = angle(u, ry);
        boolean spokeLine = Math.abs(spoke(a, 0, 30)) * Math.PI / 180 * rho < 0.55;
        if (rho > 7.5) return b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        if (rho < 1.5) return b(Blocks.OCHRE_FROGLIGHT);
        if (rho < 2.5) return gold();
        if (spokeLine && rho > 2.5) return b(Blocks.POLISHED_BLACKSTONE);
        if (rho < 4.5) return b(Blocks.MAGENTA_STAINED_GLASS);
        if (rho < 5.3) return gold();
        int sector = Math.floorMod((int) Math.floor(a / 30), 3);
        return switch (sector) {
            case 0 -> b(Blocks.PURPLE_STAINED_GLASS);
            case 1 -> b(Blocks.BLACK_STAINED_GLASS);
            default -> b(Blocks.YELLOW_STAINED_GLASS);
        };
    }

    /** Kuppelschale: aussen Schiefer mit lila Rautengitter und Goldrippen, innen Sternenhimmel. */
    private BlockState domeBlock(int u, int y, int v, double d, double ang, double ri) {
        double lat = Math.abs(spoke(ang, 22.5, 45)) * Math.PI / 180 * d;
        boolean inner = d <= ri + 1.0;
        if (lat < 0.8) return inner ? gold() : gilded();
        if (d <= 6.5) return gold();
        if (inner) {
            double h = rnd(0x5B1L, u, y, v);
            if (h < 0.035) return b(Blocks.PEARLESCENT_FROGLIGHT);
            if (h < 0.05) return crystal();
            return polOnyx();
        }
        double a = ang * Math.PI / 180 * 10, t = (y - DOME_BASE) * 1.0;
        boolean lattice = Math.floorMod((int) Math.round(a + t), 6) == 0 || Math.floorMod((int) Math.round(a - t), 6) == 0;
        return lattice ? wBricks() : b(Blocks.DEEPSLATE_TILES);
    }

    // ── Thronsaal ───────────────────────────────────────────────────────────

    /** Saalboden: Kristallmedaillon, achtstrahliger Goldstern, Onyxring unter den Saeulen, Schachbrett, Purpur-Umgang. */
    private BlockState hallFloor(int u, int dv, double d, double ang) {
        if (d < 1.5) return crystal();
        if (d < 2.5) return gold();
        if (d < 3.5) return b(Blocks.AMETHYST_BLOCK);
        double ray = Math.abs(spoke(ang, 0, 45)) * Math.PI / 180 * d;
        double width = (1 - d / 12.5) * 2.6;
        if (d < 12.5 && ray < width) return ray < width - 0.9 ? b(Blocks.GILDED_BLACKSTONE) : gold();
        if (d >= 12.5 && d < 13.2) return polOnyx();
        if (d >= 15.8) return Math.floorMod(u + dv, 2) == 0 ? b(Blocks.PURPUR_BLOCK) : b(Blocks.POLISHED_BLACKSTONE_BRICKS);
        return Math.floorMod(u + dv, 2) == 0 ? b(Blocks.POLISHED_BLACKSTONE) : b(Blocks.POLISHED_DEEPSLATE);
    }

    private void hall() {
        int r = HALL + 1;
        if (!touches(-r, KV - r, r, KV + r)) return;
        // Saeulenkranz (Radius 14) mit Arkadenboegen, Rippen zur Wand
        for (int k = 0; k < 8; k++) {
            double a = Math.toRadians(22.5 + 45 * k);
            double cu = Math.cos(a) * 14, cv = KV + Math.sin(a) * 14;
            for (int u = (int) Math.floor(cu) - 3; u <= (int) Math.ceil(cu) + 3; u++) for (int v = (int) Math.floor(cv) - 3; v <= (int) Math.ceil(cv) + 3; v++) {
                double d = dist(u - cu, v - cv);
                if (d > 2.3) continue;
                for (int y = FLOOR + 1; y <= 31; y++) {
                    BlockState s = null;
                    if (y <= FLOOR + 2) s = d <= 2.3 ? (y == FLOOR + 1 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : stairsBase(u - cu, v - cv, d)) : null;
                    else if (y >= 28) s = d <= 2.3 ? (y == 28 ? gilded() : y == 29 ? gold() : b(Blocks.POLISHED_BLACKSTONE_BRICKS)) : null;
                    else if (d <= 1.3) s = (y == 10 || y == 20) ? gold() : pillar(Blocks.PURPUR_PILLAR, Direction.Axis.Y);
                    if (s != null) set(u, y, v, s);
                }
            }
            // Rippe von der Saeule zur Wand (y 32 -> 36)
            for (double dd = 14; dd <= HALL + 1; dd += 0.3) {
                int bu = (int) Math.round(Math.cos(a) * dd), bv = KV + (int) Math.round(Math.sin(a) * dd);
                int y = 31 + (int) Math.round((dd - 14) / (HALL + 1 - 14) * (DOME_BASE - 32));
                set(bu, y, bv, gilded());
            }
            // Banner an der Wand hinter der Saeule
            int wu = (int) Math.round(Math.cos(a) * (HALL + 0.3)), wv = KV + (int) Math.round(Math.sin(a) * (HALL + 0.3));
            Direction in = toward(-Math.cos(a), -Math.sin(a));
            set(wu, 11, wv, facing(k % 2 == 0 ? Blocks.PURPLE_WALL_BANNER : Blocks.BLACK_WALL_BANNER, in));
            set(wu, 6, wv, facing(Blocks.SOUL_WALL_TORCH, in));
        }
        // Arkaden zwischen den Saeulen (Ring r 14, Boegen y 21..30)
        for (int u = ua(-15); u <= ub(15); u++) for (int v = va(KV - 15); v <= vb(KV + 15); v++) {
            double d = dist(u, v - KV);
            if (d < 13.5 || d > 14.5) continue;
            double s = spoke(angle(u, v - KV), 0, 45) * Math.PI / 180 * 14; // Abstand zur Bogenmitte
            for (int y = 20; y <= 30; y++) {
                if (arch(s, y - 22, 3.6) || !arch(s, y - 22, 5.6)) continue;
                set(u, y, v, y >= 29 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : gilded());
            }
        }
        // Kronleuchter an Ketten (vier zwischen den Saeulen)
        for (int k = 0; k < 4; k++) {
            double a = Math.toRadians(45 + 90 * k);
            int cu = (int) Math.round(Math.cos(a) * 9), cv = KV + (int) Math.round(Math.sin(a) * 9);
            if (!touches(cu - 3, cv - 3, cu + 3, cv + 3)) continue;
            for (int y = 24; y <= 80; y++) {
                if (!isAir(cu, y, cv)) break;
                set(cu, y, cv, b(Blocks.CHAIN));
            }
            set(cu, 23, cv, gold());
            set(cu, 22, cv, lantern(true, true));
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                for (int i = 1; i <= 2; i++) set(cu + d.getStepX() * i, 23, cv + d.getStepZ() * i, i == 1 ? b(Blocks.GILDED_BLACKSTONE) : gold());
                set(cu + d.getStepX() * 2, 22, cv + d.getStepZ() * 2, lantern(false, true));
                set(cu + d.getStepX() * 2, 24, cv + d.getStepZ() * 2, candles(Blocks.PURPLE_CANDLE, 4));
                set(cu + d.getStepX() + d.getClockWise().getStepX(), 23, cv + d.getStepZ() + d.getClockWise().getStepZ(), b(Blocks.CHAIN));
            }
        }
        // Teppich vom Treppenausstieg zur Throntreppe
        for (int v = va(KV - 8); v <= vb(SPIRAL_V - 3); v++) for (int u = ua(-1); u <= ub(1); u++) set(u, FLOOR + 1, v, b(Blocks.PURPLE_CARPET));
        for (int v = KV - 8; v <= SPIRAL_V - 3; v += 3) for (int s = -1; s <= 1; s += 2) set(s * 2, FLOOR + 1, v, b(Blocks.BLACK_CARPET));
    }

    /** Saeulenbasis: Stufen, die zur Saeule ansteigen. */
    private static BlockState stairsBase(double du, double dv, double d) {
        if (d <= 1.3) return b(Blocks.CHISELED_POLISHED_BLACKSTONE);
        return stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, toward(-du, -dv));
    }

    /** Throntreppe, Thron, Sonnenscheibe an der Nordwand, Schatz und Feuerschalen. */
    private void throne() {
        int tv = KV - 16;
        if (!touches(-HALL, KV - HALL - 2, HALL, KV - 6)) return;
        // Treppe: drei Stufen nach Norden, Plattform auf y DAIS
        for (int u = ua(-11); u <= ub(11); u++) for (int v = va(KV - HALL); v <= vb(KV - 8); v++) {
            double d = dist(u, v - KV);
            if (d > HALL - 0.5) continue;
            int step = KV - 8 - v; // 0, 1, 2, 3+
            int width = 11 - Math.max(0, 3 - step);
            if (Math.abs(u) > width) continue;
            int topY = Math.min(DAIS, FLOOR + 1 + step);
            for (int y = FLOOR + 1; y <= topY; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            if (step < 3) set(u, topY, v, Math.abs(u) == width ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : stairs(Blocks.PURPUR_STAIRS, Direction.NORTH));
            else set(u, DAIS, v, Math.abs(u) == width || d > HALL - 1.5 ? gilded() : Math.floorMod(u + v, 2) == 0 ? b(Blocks.PURPUR_BLOCK) : polOnyx());
            for (int y = DAIS + 1; y <= DAIS + 1; y++) if (step >= 3) air(u, y, v);
        }
        for (int v = va(KV - 15); v <= vb(KV - 11); v++) for (int u = ua(-1); u <= ub(1); u++) set(u, DAIS + 1, v, b(Blocks.PURPLE_CARPET));
        // Thron
        int y0 = DAIS + 1;
        set(0, y0, tv, stairs(Blocks.PURPUR_STAIRS, Direction.NORTH));
        for (int s = -1; s <= 1; s += 2) {
            set(s, y0, tv, gold());
            set(s, y0 + 1, tv, stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, s < 0 ? Direction.WEST : Direction.EAST));
            for (int y = y0; y <= y0 + 10; y++) set(s * 2, y, tv - 1, y == y0 + 10 ? gold() : pillar(Blocks.PURPUR_PILLAR, Direction.Axis.Y));
            set(s * 2, y0 + 11, tv - 1, crystal());
            set(s * 2, y0 + 12, tv - 1, cluster(Direction.UP));
        }
        for (int u = -1; u <= 1; u++) for (int y = y0; y <= y0 + 9; y++) {
            boolean point = y >= y0 + 7 && Math.abs(u) > (y0 + 9 - y);
            if (point) continue;
            set(u, y, tv - 1, u == 0 && y >= y0 + 2 && y <= y0 + 6 ? b(Blocks.PURPLE_WOOL) : y == y0 + 9 || Math.abs(u) == 1 && y >= y0 + 7 ? gold() : gilded());
        }
        set(0, y0 + 10, tv - 1, crystal());
        set(0, y0 + 11, tv - 1, crystal());
        set(0, y0 + 12, tv - 1, cluster(Direction.UP));
        // Feuerschalen und Goldhaufen auf der Plattform
        for (int s = -1; s <= 1; s += 2) {
            set(s * 8, y0, KV - 12, gold());
            set(s * 8, y0 + 1, KV - 12, b(Blocks.SOUL_CAMPFIRE));
            set(s * 6, y0, tv + 1, b(Blocks.RAW_GOLD_BLOCK));
            set(s * 7, y0, tv + 1, gold());
            set(s * 6, y0 + 1, tv + 1, candles(Blocks.YELLOW_CANDLE, 3));
            set(s * 7, y0, tv, b(Blocks.RAW_GOLD_BLOCK));
        }
        // Sonnenscheibe an der Nordwand (Mitte y 22)
        for (int u = ua(-12); u <= ub(12); u++) for (int v = va(KV - KEEP); v <= vb(KV - HALL + 1); v++) {
            double d = dist(u, v - KV);
            if (d < HALL - 0.5 || d > HALL + 0.5) continue;
            for (int y = 9; y <= 35; y++) {
                double rho = dist(u, y - 22);
                double a = angle(u, y - 22);
                BlockState s = null;
                if (rho < 2) s = crystal();
                else if (rho < 4.5) s = gold();
                else if (rho < 5.5) s = b(Blocks.GILDED_BLACKSTONE);
                else if (rho < 6.5) s = gold();
                else if (rho < 12.5) {
                    double ray = Math.abs(spoke(a, 0, 22.5)) * Math.PI / 180 * rho;
                    boolean longRay = Math.floorMod((int) Math.round(a / 22.5), 2) == 0;
                    if (ray < 0.7 && (longRay || rho < 9.5)) s = rho > 11 || (!longRay && rho > 8.5) ? b(Blocks.GILDED_BLACKSTONE) : gold();
                }
                if (s != null) set(u, y, v, s);
            }
        }
    }

    // ── Mausoleum und Katakomben ────────────────────────────────────────────

    /** Mausoleum (u 44..58, v -32..-12): Saeulenvorhalle nach Westen, Stufenpyramide, Treppe nach Norden hinab. */
    private void mausoleum() {
        int u0 = 44, u1 = 58, v0 = -32, v1 = -12;
        if (!touches(u0 - 4, v0 - 8, u1 + 1, v1 + 1)) return;
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean wall = u == u0 || u == u1 || v == v0 || v == v1;
            set(u, 0, v, polOnyx());
            for (int y = 1; y <= 9; y++) set(u, y, v, wall ? (y == 9 ? gilded() : y % 4 == 0 ? polOnyx() : b(Blocks.POLISHED_BLACKSTONE_BRICKS)) : AIR);
            // Stufendach
            int du = Math.min(u - u0, u1 - u), dv = Math.min(v - v0, v1 - v);
            int m = Math.min(du, dv);
            for (int y = 10; y <= 10 + m && y <= 16; y++) set(u, y, v, y == 10 + m ? (m % 2 == 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : b(Blocks.DEEPSLATE_TILES)) : AIR);
        }
        set(51, 17, -22, gold());
        set(51, 18, -22, crystal());
        set(51, 19, -22, cluster(Direction.UP));
        // Vorhalle mit Purpursaeulen
        for (int v = -26; v <= -18; v += 4) {
            for (int y = 1; y <= 8; y++) set(u0 - 3, y, v, y == 1 ? b(Blocks.CHISELED_POLISHED_BLACKSTONE) : y == 8 ? gilded() : pillar(Blocks.PURPUR_PILLAR, Direction.Axis.Y));
        }
        for (int u = u0 - 4; u <= u0 - 1; u++) for (int v = -27; v <= -17; v++) {
            set(u, 9, v, u == u0 - 4 ? stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.EAST) : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            set(u, 0, v, polOnyx());
        }
        for (int v = -24; v <= -20; v++) for (int y = 1; y <= 5; y++) if (arch(v + 22, y - 3, 2.5)) air(u0, y, v);
        set(u0 - 1, 4, -25, lantern(true, false));
        set(u0 - 1, 4, -19, lantern(true, false));
        // Sarkophag
        for (int u = 46; u <= 48; u++) {
            set(u, 1, -15, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            set(u, 2, -15, slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
        }
        set(47, 3, -15, candles(Blocks.PURPLE_CANDLE, 4));
        // Treppe hinab (u 50..52), Stufe i auf y -i bei v -14-i, bis y -20 bei v -34
        for (int i = 0; i <= 20; i++) {
            int v = -13 - i, y = -i;
            if (!touches(48, v, 54, v)) continue;
            for (int u = 49; u <= 53; u++) {
                boolean side = u == 49 || u == 53;
                for (int yy = y - 1; yy <= y + 5; yy++) {
                    if (yy > 0 && v >= v0) continue;
                    if (side || yy == y - 1 || yy == y + 5) set(u, yy, v, side && yy == y + 3 && i % 4 == 2 ? b(Blocks.SOUL_LANTERN) : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                    else if (yy == y) set(u, yy, v, i == 20 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.SOUTH));
                    else air(u, yy, v);
                }
            }
        }
    }

    /**
     * Gruftsaal (u 42..60, v -64..-36, Boden y -21): Saeulen, Sarkophage, Grabnischen mit Schaedeln, Spawner; Gang nach
     * Westen (v -50) unter Graben und Mauer bis in die Kaverne.
     */
    private void catacombs() {
        int u0 = 42, u1 = 60, v0 = -64, v1 = -34, fy = WALK, cy = WALK + 8;
        if (touches(u0, v0, u1, v1)) {
            for (int u = ua(u0); u <= ub(u1); u++) for (int v = va(v0); v <= vb(v1); v++) {
                boolean wall = u == u0 || u == u1 || v == v0 || v == v1;
                for (int y = fy; y <= cy; y++) {
                    BlockState s;
                    if (y == fy) s = wall ? b(ModBlocks.BRICK_OBSIDIAN.get()) : Math.floorMod(u + v, 2) == 0 ? b(Blocks.POLISHED_DEEPSLATE) : b(Blocks.DEEPSLATE_TILES);
                    else if (y == cy) s = b(Blocks.DEEPSLATE_BRICKS);
                    else if (wall) {
                        boolean niche = (y == fy + 2 || y == fy + 4) && Math.floorMod(u + v, 3) == 0;
                        s = niche ? AIR : y % 3 == 0 ? b(ModBlocks.BRICK_OBSIDIAN.get()) : b(Blocks.DEEPSLATE_BRICKS);
                    } else s = AIR;
                    set(u, y, v, s);
                }
            }
            // Grabnischen fuellen: Schaedel, Knochen, Kerzen
            for (int u = ua(u0); u <= ub(u1); u++) for (int v = va(v0); v <= vb(v1); v++) {
                if (!(u == u0 || u == u1 || v == v0 || v == v1)) continue;
                for (int y = fy + 2; y <= fy + 4; y += 2) {
                    if (!isAir(u, y, v)) continue;
                    double h = rnd(0x6A1L, u, y, v);
                    set(u, y, v, h < 0.3 ? b(Blocks.SKELETON_SKULL) : h < 0.45 ? b(Blocks.WITHER_SKELETON_SKULL) : h < 0.7 ? b(Blocks.BONE_BLOCK) : candles(Blocks.WHITE_CANDLE, 1 + (int) (h * 3)));
                }
            }
            // Saeulen und Bogen
            for (int u = u0 + 4; u <= u1 - 3; u += 5) for (int v = v0 + 5; v <= v1 - 4; v += 6) {
                if (u == 51 && v > -40) continue;
                for (int y = fy + 1; y < cy; y++) set(u, y, v, y == cy - 1 ? b(Blocks.CHISELED_DEEPSLATE) : pillar(Blocks.POLISHED_BASALT, Direction.Axis.Y));
            }
            // Sarkophage mit Kerzen
            for (int[] sp : new int[][] {{47, -58}, {55, -58}, {47, -44}, {55, -46}}) {
                for (int dv = 0; dv <= 2; dv++) {
                    set(sp[0], fy + 1, sp[1] + dv, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                    set(sp[0], fy + 2, sp[1] + dv, dv == 1 ? gilded() : slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
                }
                set(sp[0] + 1, fy + 1, sp[1] + 1, candles(Blocks.PURPLE_CANDLE, 3));
            }
            // Spinnweben und Kronleuchter
            for (int i = 0; i < 12; i++) {
                int u = u0 + 1 + (int) (rnd(0x6A2L, i, 0, 0) * (u1 - u0 - 1)), v = v0 + 1 + (int) (rnd(0x6A3L, i, 0, 0) * (v1 - v0 - 1));
                int y = rnd(0x6A4L, i, 0, 0) < 0.5 ? cy - 1 : fy + 1;
                setIfAir(u, y, v, b(Blocks.COBWEB));
            }
            for (int v = v0 + 8; v <= v1 - 6; v += 10) {
                set(51, cy - 1, v, b(Blocks.CHAIN));
                set(51, cy - 2, v, lantern(true, true));
            }
            // Treppe aus dem Mausoleum muendet bei v -34
            for (int u = 50; u <= 52; u++) for (int y = fy + 1; y <= fy + 5; y++) air(u, y, v1);
        }
        // Gang nach Westen in die Kaverne (v -51..-49, y fy..fy+5)
        if (touches(14, -53, u0, -47)) {
            for (int u = ua(14); u <= ub(u0); u++) for (int v = -52; v <= -48; v++) {
                if (!col(u, v)) continue;
                boolean side = v == -52 || v == -48;
                for (int y = fy; y <= fy + 5; y++) {
                    BlockState s;
                    if (y == fy || y == fy + 5) s = y == fy ? paving(u, v) : b(Blocks.DEEPSLATE_BRICKS);
                    else if (side) s = u % 6 == 0 && y == fy + 3 ? lantern(true, false) : u % 6 == 0 ? pillar(Blocks.POLISHED_BASALT, Direction.Axis.Y) : b(Blocks.DEEPSLATE_BRICKS);
                    else s = AIR;
                    set(u, y, v, s);
                }
            }
            // Gitter im Gang, zur Haelfte herausgerissen
            for (int v = -51; v <= -49; v++) for (int y = fy + 1; y <= fy + 4; y++) {
                if (v == -49 && y <= fy + 3) continue;
                set(30, y, v, b(Blocks.IRON_BARS));
            }
            set(31, fy + 1, -49, b(Blocks.IRON_BARS));
            setIfAir(36, fy + 4, -51, b(Blocks.COBWEB));
            setIfAir(25, fy + 1, -50, b(Blocks.COBWEB));
        }
    }

    // ── Glutkaverne ─────────────────────────────────────────────────────────

    /** Kavernenform: Ellipsoid um den Bergfried (Radius 24, y -48..-12). */
    private static boolean inCavern(int u, int y, int v) {
        double du = u / 24.0, dv = (v - KV) / 24.0, dy = (y + 30) / 18.0;
        return du * du + dv * dv + dy * dy <= 1 && y > -48;
    }

    /** Pfad um die Kaverne: Einstieg Nordost (Gang), ueber Westen bis zur Wendeltreppe im Sueden. */
    static int[][] cavernPath() {
        int n = 19;
        int[][] pts = new int[n][3];
        double a0 = Math.toRadians(-60), a1 = Math.toRadians(-250);
        for (int i = 0; i < n; i++) {
            double t = i / (double) (n - 1);
            double a = a0 + (a1 - a0) * t;
            double r = 15.5 + Math.sin(t * Math.PI * 3) * 1.5;
            pts[i][0] = (int) Math.round(Math.cos(a) * r);
            pts[i][1] = KV + (int) Math.round(Math.sin(a) * r);
            pts[i][2] = WALK + (int) Math.round(Math.sin(t * Math.PI * 3) * 1.0);
        }
        return pts;
    }

    private void cavern() {
        int r = 27;
        if (!touches(-r, KV - r, r, KV + r)) return;
        for (int u = ua(-r); u <= ub(r); u++) for (int v = va(KV - r); v <= vb(KV + r); v++) {
            for (int y = -50; y <= -9; y++) {
                if (inCavern(u, y, v)) {
                    set(u, y, v, y <= LAVA ? b(Blocks.LAVA) : AIR);
                } else if (inCavern(u + 2, y, v) || inCavern(u - 2, y, v) || inCavern(u, y, v + 2) || inCavern(u, y, v - 2) || inCavern(u, y + 2, v) || inCavern(u, y - 2, v)) {
                    double h = rnd(0x7A1L, u, y, v);
                    set(u, y, v, h < 0.08 ? b(ModBlocks.BRICK_OBSIDIAN.get()) : rock(u, y - 6, v));
                }
            }
            // Tropfsteine aus Basalt an der Decke, Kristalle
            double d = dist(u, v - KV);
            if (d < 21 && rnd(0x7A2L, u, 0, v) < 0.06) {
                int top = -12 - (int) Math.round((d / 24) * (d / 24) * 8);
                while (top > -40 && !inCavern(u, top, v)) top--;
                int len = 2 + (int) (rnd(0x7A3L, u, 0, v) * 7);
                for (int y = top; y > top - len; y--) set(u, y, v, y == top - len + 1 ? cluster(Direction.DOWN) : pillar(Blocks.BASALT, Direction.Axis.Y));
            }
        }
        // Herzkristall: haengt von der Decke, Spitze ueber der Mittelsaeule
        for (int u = ua(-4); u <= ub(4); u++) for (int v = va(KV - 4); v <= vb(KV + 4); v++) for (int y = -27; y <= -12; y++) {
            double t = (y + 27) / 15.0;
            double rr = 0.6 + t * 3.4;
            double dd = Math.abs(u) + Math.abs(v - KV) * 1.0;
            if (dd > rr) continue;
            set(u, y, v, dd > rr - 1 ? (rnd(0x7A4L, u, y, v) < 0.5 ? b(Blocks.AMETHYST_BLOCK) : crystal()) : crystal());
        }
        set(0, -28, KV, cluster(Direction.DOWN));
        // Mittelsaeule mit Blaze-Spawner-Plattform
        for (int u = ua(-2); u <= ub(2); u++) for (int v = va(KV - 2); v <= vb(KV + 2); v++) {
            double d = dist(u, v - KV);
            if (d > 2.5) continue;
            for (int y = -47; y <= -34; y++) set(u, y, v, d > 1.6 ? b(Blocks.BASALT) : b(Blocks.MAGMA_BLOCK));
            set(u, -33, v, d > 1.6 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : gold());
        }
        // Saeulen und Kettenplattformen entlang des Pfads
        int[][] path = cavernPath();
        for (int i = 0; i < path.length; i++) {
            int pu = path[i][0], pv = path[i][1], py = path[i][2];
            if (!touches(pu - 2, pv - 2, pu + 2, pv + 2)) continue;
            boolean chained = i % 4 == 2;
            if (chained) {
                for (int u = pu - 1; u <= pu + 1; u++) for (int v = pv - 1; v <= pv + 1; v++) set(u, py, v, (u == pu && v == pv) ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, true));
                for (int[] c : new int[][] {{-1, -1}, {1, 1}}) {
                    for (int y = py + 1; y <= -10; y++) {
                        if (!isAir(pu + c[0], y, pv + c[1]) && y > py + 1) break;
                        set(pu + c[0], y, pv + c[1], b(Blocks.CHAIN));
                    }
                }
                set(pu, py - 1, pv, lantern(true, true));
            } else {
                int size = i % 3 == 0 ? 1 : 0;
                for (int u = pu - size; u <= pu + size; u++) for (int v = pv - size; v <= pv + size; v++) {
                    for (int y = LAVA - 4; y <= py; y++) set(u, y, v, y == py ? (i % 2 == 0 ? b(Blocks.POLISHED_BLACKSTONE_BRICKS) : b(Blocks.SMOOTH_BASALT)) : y > py - 3 ? masonry(u, y, v) : pillar(Blocks.BASALT, Direction.Axis.Y));
                }
                set(pu + size, py, pv - size, i % 5 == 1 ? gold() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            }
            for (int y = py + 1; y <= py + 3; y++) for (int u = pu - 1; u <= pu + 1; u++) for (int v = pv - 1; v <= pv + 1; v++) {
                BlockState s = get(u, y, v);
                if (s != null && !s.isAir() && !s.is(Blocks.CHAIN)) air(u, y, v);
            }
        }
        // Einstieg (Gangende Nordost) und Plattform vor der Wendeltreppe
        for (int u = ua(6); u <= ub(16); u++) for (int v = va(-52); v <= vb(-48); v++) {
            set(u, WALK, v, paving(u, v));
            for (int y = WALK - 3; y < WALK; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            for (int y = WALK + 1; y <= WALK + 4; y++) air(u, y, v);
        }
        for (int u = ua(-4); u <= ub(6); u++) for (int v = va(SPIRAL_V - 4); v <= vb(SPIRAL_V + 4); v++) {
            set(u, WALK, v, paving(u, v));
            for (int y = WALK - 5; y < WALK; y++) set(u, y, v, b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        }
        set(5, WALK + 1, SPIRAL_V - 3, lantern(true, false));
        set(5, WALK + 1, SPIRAL_V + 3, lantern(true, false));
    }

    // ── Wendeltreppe ────────────────────────────────────────────────────────

    /** Ring der Wendeltreppe um die Mittelsaeule (0, SPIRAL_V). */
    static final int[][] RING = {{-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}};

    /** Schacht (u -2..2) von der Kavernenplattform bis in den Thronsaal; Ausstieg nach Norden auf (0, SPIRAL_V - 1). */
    private void spiral() {
        if (!touches(-3, SPIRAL_V - 3, 3, SPIRAL_V + 3)) return;
        int bottom = SPIRAL_BOTTOM, steps = FLOOR - bottom + 1; // letzte Stufe liegt in der Saalbodenebene
        for (int u = -2; u <= 2; u++) for (int v = SPIRAL_V - 2; v <= SPIRAL_V + 2; v++) {
            boolean wall = Math.abs(u) == 2 || Math.abs(v - SPIRAL_V) == 2;
            for (int y = bottom - 1; y <= FLOOR; y++) {
                if (wall) {
                    boolean window = y < -12 && y % 4 == 0 && (u == 0 || v == SPIRAL_V);
                    set(u, y, v, window ? b(Blocks.IRON_BARS) : y == bottom - 1 ? polOnyx() : y % 6 == 0 ? gilded() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
                } else if (u == 0 && v == SPIRAL_V) {
                    set(u, y, v, y % 6 == 3 ? crystal() : pillar(Blocks.POLISHED_BASALT, Direction.Axis.Y));
                } else {
                    set(u, y, v, y == bottom - 1 ? polOnyx() : AIR);
                }
            }
        }
        int start = 4; // Stufe 0 bei (1, 1), letzte bei (0, -1)
        for (int i = 0; i < steps; i++) {
            int[] c = RING[(i + start) % 8], n = RING[(i + start + 1) % 8];
            Direction up = toward(n[0] - c[0], n[1] - c[1]);
            set(c[0], bottom + i, SPIRAL_V + c[1], stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, up));
            if (i > 0) set(c[0], bottom + i - 1, SPIRAL_V + c[1], b(Blocks.POLISHED_BLACKSTONE_BRICKS));
        }
        // Eingang von der Plattform (Ost), Ausstieg im Saal
        for (int y = bottom; y <= bottom + 2; y++) air(2, y, SPIRAL_V + 1);
        set(0, FLOOR + 1, SPIRAL_V, lantern(true, false));
        for (int u = -2; u <= 2; u++) for (int v = SPIRAL_V - 2; v <= SPIRAL_V + 2; v++) {
            boolean border = Math.abs(u) == 2 || Math.abs(v - SPIRAL_V) == 2;
            if (border && !(u == 0 && v == SPIRAL_V - 2)) set(u, FLOOR + 1, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
        }
        for (int u = -1; u <= 1; u++) for (int v = SPIRAL_V - 2; v <= SPIRAL_V + 2; v++) {
            BlockState s = get(u, FLOOR + 1, v);
            if (s != null && s.is(Blocks.PURPLE_CARPET)) air(u, FLOOR + 1, v);
        }
    }

    // ── Himmel: Kronkristall, Ringe, schwebende Inseln ─────────────────────

    /** Schwebende Inseln (u, Oberseite y, v, Radius, Schrein 1/0). */
    static final int[][] ISLANDS = {{-46, 46, -48, 7, 1}, {46, 52, 12, 6, 1}, {-40, 60, 24, 5, 0}, {40, 42, -66, 5, 0}, {-16, 68, 34, 4, 0},
            {22, 74, -72, 4, 0}};

    private void sky() {
        int cy = 96;
        int r = 22;
        if (touches(-r, KV - r, r, KV + r)) {
            // Kronkristall
            for (int u = ua(-8); u <= ub(8); u++) for (int v = va(KV - 8); v <= vb(KV + 8); v++) for (int y = cy - 14; y <= cy + 14; y++) {
                double dd = Math.abs(u) + Math.abs(v - KV) + Math.abs(y - cy) * 0.55;
                if (dd > 7.5) continue;
                BlockState s;
                if (dd > 6.5) {
                    double h = rnd(0x8A1L, u, y, v);
                    s = h < 0.45 ? b(Blocks.PURPLE_STAINED_GLASS) : h < 0.75 ? b(Blocks.AMETHYST_BLOCK) : crystal();
                } else s = dd < 2 ? b(Blocks.PEARLESCENT_FROGLIGHT) : crystal();
                set(u, y, v, s);
            }
            // Heiligenschein und zwei geneigte Ringe
            for (int i = 0; i < 360; i++) {
                double a = Math.toRadians(i);
                ringPoint(Math.cos(a) * 12, 0, Math.sin(a) * 12, cy, i % 40 < 31 ? gold() : null);
                double x = Math.cos(a) * 15, z = Math.sin(a) * 15;
                ringPoint(x, z * Math.sin(Math.toRadians(32)), z * Math.cos(Math.toRadians(32)), cy, i % 30 < 24 ? b(Blocks.GILDED_BLACKSTONE) : null);
                ringPoint(x * Math.cos(Math.toRadians(30)), x * Math.sin(Math.toRadians(30)), z, cy, i % 50 < 40 ? gilded() : null);
            }
            // Splitter im Orbit
            for (int k = 0; k < 14; k++) {
                double a = Math.toRadians(k * 360.0 / 14 + rnd(0x8A2L, k, 0, 0) * 12);
                double rr = 18 + rnd(0x8A3L, k, 0, 0) * 3;
                int u = (int) Math.round(Math.cos(a) * rr), v = KV + (int) Math.round(Math.sin(a) * rr), y = cy - 10 + (int) (rnd(0x8A4L, k, 0, 0) * 20);
                set(u, y, v, k % 3 == 0 ? b(Blocks.AMETHYST_BLOCK) : crystal());
                if (k % 2 == 0) set(u, y - 1, v, cluster(Direction.DOWN));
                if (k % 3 == 1) set(u, y + 1, v, cluster(Direction.UP));
            }
        }
        for (int[] is : ISLANDS) island(is[0], is[1], is[2], is[3], is[4] == 1);
    }

    private void ringPoint(double x, double y, double z, int cy, BlockState s) {
        if (s == null) return;
        set((int) Math.round(x), cy + (int) Math.round(y), KV + (int) Math.round(z), s);
    }

    /** Schwebende Insel: umgedrehter Kegel aus Onyx und Gestein, Moos, haengende Kristalle, Ankerketten zum Boden. */
    private void island(int cu, int top, int cv, int r, boolean shrine) {
        int depth = (int) (r * 1.8);
        if (!touches(cu - r - 2, cv - r - 2, cu + r + 2, cv + r + 2)) return;
        for (int u = cu - r - 1; u <= cu + r + 1; u++) for (int v = cv - r - 1; v <= cv + r + 1; v++) {
            double d = dist(u - cu, v - cv) + (noise(0x9A1L, u, v, 3) - 0.5) * 1.6;
            for (int dy = 0; dy <= depth; dy++) {
                double rr = r * Math.pow(1 - dy / (double) (depth + 1), 0.75);
                if (d > rr) continue;
                int y = top - dy;
                BlockState s;
                if (dy == 0) s = rnd(0x9A2L, u, 0, v) < 0.7 ? b(WemburosBlocks.VIOLET_MOSS.get()) : b(WemburosBlocks.WEMBUROS_REGOLITH.get());
                else if (dy <= 2) s = b(WemburosBlocks.WEMBUROS_STONE.get());
                else {
                    double h = rnd(0x9A3L, u, y, v);
                    s = h < 0.1 ? b(WemburosBlocks.GILDED_ONYX.get()) : h < 0.15 ? crystal() : onyx();
                }
                set(u, y, v, s);
                if (dy == depth || d > rr - 1 && dy > 2 && rnd(0x9A4L, u, y, v) < 0.25) {
                    if (isAir(u, y - 1, v)) set(u, y - 1, v, cluster(Direction.DOWN));
                }
            }
        }
        // Ankerketten zum Boden
        for (int[] c : new int[][] {{-1, 0}, {1, 1}}) {
            int u = cu + c[0] * (r / 2), v = cv + c[1] * (r / 2);
            int y = top - 3;
            while (y > 1 && !isAir(u, y, v)) y--;
            for (; y >= 1; y--) {
                BlockState s = get(u, y, v);
                if (s == null || !s.isAir() && !s.canBeReplaced()) break;
                set(u, y, v, b(Blocks.CHAIN));
            }
            if (y >= 0) {
                set(u, y, v, gold());
                set(u, y + 1, v, b(Blocks.CHAIN));
            }
        }
        if (shrine) {
            for (int[] c : new int[][] {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}}) {
                for (int y = top + 1; y <= top + 4; y++) set(cu + c[0], y, cv + c[1], pillar(Blocks.PURPUR_PILLAR, Direction.Axis.Y));
            }
            for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) {
                boolean edge = Math.abs(u - cu) == 3 || Math.abs(v - cv) == 3;
                set(u, top + 5, v, edge ? slab(Blocks.PURPUR_SLAB, false) : b(Blocks.PURPUR_BLOCK));
                if (Math.abs(u - cu) <= 2 && Math.abs(v - cv) <= 2) set(u, top, v, Math.abs(u - cu) + Math.abs(v - cv) <= 1 ? gilded() : b(Blocks.POLISHED_BLACKSTONE_BRICKS));
            }
            set(cu, top + 6, cv, gold());
            set(cu, top + 7, cv, crystal());
            set(cu, top + 4, cv, lantern(true, true));
        }
    }
}
