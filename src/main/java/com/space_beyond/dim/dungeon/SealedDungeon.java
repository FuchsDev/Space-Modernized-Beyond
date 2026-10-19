package com.space_beyond.dim.dungeon;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.space_beyond.dim.dungeon.seal.SealBlocks;
import com.space_beyond.dim.dungeon.seal.SealLockBlock;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Versiegelter Boss-Dungeon (Nutzerwunsch: man darf nicht einfach zum Boss laufen). Alles liegt unter der Oberflaeche
 * und ist bis zum Sieg geschuetzt ({@link com.space_beyond.dim.dungeon.seal.DungeonProtection}):
 * <pre>
 *  Eingangsmonument (v 48..62, Oberflaeche) -> Treppe 20 Stufen hinab -> Vorhalle (v 21..30)
 *  Siegelhalle (u -16..16, v -12..20, Boden y -21): im Norden das Siegeltor mit drei Schloessern, in der Mitte die
 *      Wendeltreppe hinab zum Tiefen Fluegel, Tueren nach Westen und Osten
 *  Fluegel West / Ost (Boden y -21) und Tief (Boden y -43): je eine Pruefung ({@link Wing}), am Ende genau ein
 *      Siegelschluessel (Schatzkiste bzw. Siegelwaechter)
 *  Arena (Mitte v -40, Radius 21, Boden y -26) hinter dem Tor: Saeulenkranz, Graben mit Gefahr, Bruecken, Schatznische
 * </pre>
 * Material und Gegner je Planet ({@link Theme}), dazu planetenspezifische Ausstattung ({@link #entranceDeco},
 * {@link #hubDeco}, {@link #arenaDeco}). Gemalt und geplant wird mit demselben Code ({@link DungeonOut}).
 */
public abstract class SealedDungeon implements DungeonDesign {

    /** Halbe Kantenlaenge des Grundrisses. */
    public static final int R = 70;
    /** Boden der Siegelhalle, Vorhalle und der Fluegel West/Ost (Bloecke); begehbar eine Ebene hoeher. */
    public static final int FLOOR = -21;
    /** Boden des Tiefen Fluegels. */
    public static final int DEEP_FLOOR = -43;
    /** Siegelhalle. */
    public static final int HUB_U = 16, HUB_V0 = -12, HUB_V1 = 20, HUB_CV = 4, HUB_CEIL = -7;
    /** Arena. */
    public static final int ARENA_V = -40, ARENA_R = 21, ARENA_FLOOR = -26;
    /** Bossplatz. */
    public static final int BOSS_U = 0, BOSS_Y = ARENA_FLOOR + 1, BOSS_V = -42;

    /** Pruefungen der Fluegel; jede endet mit genau einem Siegelschluessel. */
    public enum Wing {
        /** Drei Hallen mit Spawnern, Schatzkiste in der Nische dahinter. */
        GAUNTLET,
        /** Hoehle ueber einer Gefahrengrube, Trittsteine und Kettenplattformen bis zum hohen Ende. */
        PARKOUR,
        /** Dunkles Labyrinth (9 x 9 Zellen, je Dungeon anders), Schatz in der entferntesten Zelle. */
        MAZE,
        /** Runde Kammer mit einem Siegelwaechter, der den Schluessel traegt. */
        GUARDIAN,
        /** Schacht mit Simsen an den Waenden, Schatz oben. */
        CLIMB,
        /** Gang mit Pfeilboden (Werfer unter Druckplatten), Magma-Trittsteinen und Spinnweben. */
        TRAPS
    }

    /** Material eines Planeten-Dungeons. */
    public record Theme(BlockState wall, BlockState wallAlt, BlockState floor, BlockState floorAlt, BlockState ceiling, BlockState accent,
                        BlockState trim, BlockState light, BlockState pillar, BlockState glass, Block stairs, Block slab,
                        BlockState hazard, BlockState hazardBed, BlockState rubble) { }

    protected abstract Theme theme();

    /** Pruefungen der Fluegel West, Ost, Tief. */
    protected abstract Wing[] wings();

    protected abstract String[] roomMobs();

    protected abstract String[] spawnerMobs();

    /** Mob-Art der Siegelwaechter. */
    protected abstract String eliteMob();

    /** Registry-ID des Bosses. */
    protected abstract String boss();

    /** Planetenspezifisches am Eingang, in der Siegelhalle, in der Arena. */
    protected void entranceDeco(Draw d) {
    }

    protected void hubDeco(Draw d) {
    }

    protected void arenaDeco(Draw d) {
    }

    @Override
    public int radius() {
        return R;
    }

    @Override
    public int minBase() {
        return 62;
    }

    @Override
    public int minY() {
        return -52;
    }

    @Override
    public int maxY() {
        return 26;
    }

    @Override
    public void plan(DungeonPlan p) {
        new Draw(new PlanOut(p), this).all();
    }

    @Override
    public void paint(DungeonPainter w) {
        new Draw(w, this).all();
    }

    @Override
    public void trace(DungeonOut out) {
        new Draw(out, this).all();
    }

    /** Plan-Modus: nichts malen, Eintraege sammeln. */
    static final class PlanOut implements DungeonOut {
        private final DungeonPlan p;

        PlanOut(DungeonPlan p) {
            this.p = p;
        }

        @Override public void set(int u, int y, int v, BlockState s) { }
        @Override public BlockState get(int u, int y, int v) { return null; }
        @Override public double unit(long salt, int u, int y, int v) { return com.space_beyond.dim.sporos.city.CityRandom.unit(p.d.seed() ^ salt, u, y, v); }
        @Override public int uMin() { return 1; }
        @Override public int uMax() { return 0; }
        @Override public int vMin() { return 1; }
        @Override public int vMax() { return 0; }
        @Override public void chest(int u, int y, int v, Direction facing, String table) { p.chest(u, y, v, facing, table); }
        @Override public void barrel(int u, int y, int v, String table) { p.barrel(u, y, v, table); }
        @Override public void spawner(int u, int y, int v, String type) { p.spawner(u, y, v, type); }
        @Override public void mob(int u, int y, int v, String type) { p.mob(u, y, v, type); }
        @Override public void elite(int u, int y, int v, String type) { p.elite(u, y, v, type); }
        @Override public void dispenser(int u, int y, int v) { p.dispenser(u, y, v); }
    }

    /**
     * Lokaler Rahmen eines Bauteils: (a, b) -> (u, v), +a zeigt in Richtung OST * k Vierteldrehungen im Uhrzeigersinn,
     * +b liegt 90 Grad rechts davon.
     */
    public record F(int ou, int ov, int k) {
        public int u(int a, int b) {
            return switch (k) {
                case 1 -> ou - b;
                case 2 -> ou - a;
                case 3 -> ou + b;
                default -> ou + a;
            };
        }

        public int v(int a, int b) {
            return switch (k) {
                case 1 -> ov + a;
                case 2 -> ov - b;
                case 3 -> ov - a;
                default -> ov + b;
            };
        }

        /** Welt-Richtung einer lokalen Richtung (lokal: +a = OST, +b = SUED). */
        public Direction d(Direction local) {
            if (local.getAxis() == Direction.Axis.Y) return local;
            Direction d = local;
            for (int i = 0; i < k; i++) d = d.getClockWise();
            return d;
        }

        int a(int u, int v) {
            return switch (k) {
                case 1 -> v - ov;
                case 2 -> ou - u;
                case 3 -> ov - v;
                default -> u - ou;
            };
        }

        int b(int u, int v) {
            return switch (k) {
                case 1 -> ou - u;
                case 2 -> ov - v;
                case 3 -> u - ou;
                default -> v - ov;
            };
        }
    }

    static final F HUB = new F(0, 0, 0);
    static final F WEST = new F(-HUB_U - 1, HUB_CV, 2);
    static final F EAST = new F(HUB_U + 1, HUB_CV, 0);
    static final F DEEP = new F(1, HUB_CV, 1);

    /** Zeichner: Werkzeuge, Raeume, Fluegel. Oeffentlich fuer die Planeten-Ausstattung. */
    public static final class Draw {
        public final DungeonOut o;
        public final SealedDungeon design;
        public final Theme t;
        private final String common, bossLoot;

        Draw(DungeonOut o, SealedDungeon design) {
            this.o = o;
            this.design = design;
            this.t = design.theme();
            this.common = design.stage().body + "_common";
            this.bossLoot = design.stage().body + "_boss";
        }

        // ── Grundwerkzeuge (Weltkoordinaten u, v) ──

        public boolean col(int u, int v) {
            return u >= o.uMin() && u <= o.uMax() && v >= o.vMin() && v <= o.vMax();
        }

        public boolean touches(int u0, int v0, int u1, int v1) {
            return Math.max(u0, u1) >= o.uMin() && Math.min(u0, u1) <= o.uMax() && Math.max(v0, v1) >= o.vMin() && Math.min(v0, v1) <= o.vMax();
        }

        public void set(int u, int y, int v, BlockState s) {
            if (col(u, v)) o.set(u, y, v, s);
        }

        public void air(int u, int y, int v) {
            set(u, y, v, AIR);
        }

        public BlockState get(int u, int y, int v) {
            return col(u, v) ? o.get(u, y, v) : null;
        }

        public boolean isAir(int u, int y, int v) {
            BlockState s = get(u, y, v);
            return s != null && s.isAir();
        }

        public void fill(int u0, int y0, int v0, int u1, int y1, int v1, BlockState s) {
            for (int u = Math.max(Math.min(u0, u1), o.uMin()); u <= Math.min(Math.max(u0, u1), o.uMax()); u++)
                for (int v = Math.max(Math.min(v0, v1), o.vMin()); v <= Math.min(Math.max(v0, v1), o.vMax()); v++)
                    for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) o.set(u, y, v, s);
        }

        public double rnd(long salt, int u, int y, int v) {
            return o.unit(salt, u, y, v);
        }

        // ── Bloecke ──

        public static final BlockState AIR = Blocks.AIR.defaultBlockState();

        public static BlockState b(Block x) {
            return x.defaultBlockState();
        }

        public BlockState stairs(F f, Direction localUp, boolean top) {
            return CityPalette.stairs(t.stairs(), f.d(localUp), top);
        }

        public BlockState slab(boolean top) {
            return CityPalette.slab(b(t.slab()), top ? SlabType.TOP : SlabType.BOTTOM);
        }

        public static BlockState facing(Block x, Direction d) {
            return CityPalette.facing(b(x), d);
        }

        public static BlockState lantern(boolean hanging) {
            return CityPalette.with(b(Blocks.SOUL_LANTERN), BlockStateProperties.HANGING, hanging);
        }

        /** Mauerwerk mit Flecken der Zweitfarbe und Rissen. */
        public BlockState wall(int u, int y, int v) {
            double h = rnd(0xA101L, u, y, v), patch = rnd(0xA102L, u >> 2, Math.floorDiv(y, 3), v >> 2);
            if (h < 0.05) return t.rubble();
            if (patch < 0.28) return t.wallAlt();
            return t.wall();
        }

        /** Bodenmuster: 2 x 2-Schachbrett. */
        public BlockState floor(int u, int v) {
            return ((u >> 1) + (v >> 1) & 1) == 0 ? t.floor() : t.floorAlt();
        }

        // ── Raeume (lokal im Rahmen f) ──

        /** Lokaler Ausschnitt des Chunks: {aMin, aMax, bMin, bMax}. */
        int[] local(F f) {
            int a0 = f.a(o.uMin(), o.vMin()), a1 = f.a(o.uMax(), o.vMax());
            int b0 = f.b(o.uMin(), o.vMin()), b1 = f.b(o.uMax(), o.vMax());
            return new int[] {Math.min(a0, a1), Math.max(a0, a1), Math.min(b0, b1), Math.max(b0, b1)};
        }

        public void lset(F f, int a, int y, int b, BlockState s) {
            set(f.u(a, b), y, f.v(a, b), s);
        }

        public BlockState lget(F f, int a, int y, int b) {
            return get(f.u(a, b), y, f.v(a, b));
        }

        public void lfill(F f, int a0, int y0, int b0, int a1, int y1, int b1, BlockState s) {
            fill(f.u(a0, b0), y0, f.v(a0, b0), f.u(a1, b1), y1, f.v(a1, b1), s);
        }

        public void lclear(F f, int a0, int y0, int b0, int a1, int y1, int b1) {
            lfill(f, a0, y0, b0, a1, y1, b1, AIR);
        }

        public boolean ltouches(F f, int a0, int b0, int a1, int b1) {
            return touches(f.u(a0, b0), f.v(a0, b0), f.u(a1, b1), f.v(a1, b1));
        }

        /**
         * Raum (Huelle inklusive): Boden y0 mit Muster und Randleiste, Decke y1 mit Balken, Waende mit Pilastern und
         * Lichtern; innen Luft, ein paar Truemmer.
         */
        public void room(F f, int a0, int b0, int a1, int b1, int y0, int y1) {
            if (!ltouches(f, a0, b0, a1, b1)) return;
            int[] L = local(f);
            for (int a = Math.max(a0, L[0]); a <= Math.min(a1, L[1]); a++) for (int b = Math.max(b0, L[2]); b <= Math.min(b1, L[3]); b++) {
                int u = f.u(a, b), v = f.v(a, b);
                boolean wallA = a == a0 || a == a1, wallB = b == b0 || b == b1;
                for (int y = y0; y <= y1; y++) {
                    BlockState s;
                    if (y == y0) s = (wallA || wallB) ? t.trim() : (a == a0 + 1 || a == a1 - 1 || b == b0 + 1 || b == b1 - 1) ? t.trim() : floor(u, v);
                    else if (y == y1) s = (wallA || wallB) ? t.trim() : (a - a0) % 4 == 0 ? t.accent() : t.ceiling();
                    else if (wallA || wallB) {
                        int along = wallA ? b - b0 : a - a0;
                        if (y == y0 + 1) s = t.trim();
                        else if (along % 4 == 0) s = t.pillar();
                        else if (along % 4 == 2 && y == y0 + 3) s = t.light();
                        else s = wall(u, y, v);
                    } else {
                        s = AIR;
                        if (y == y0 + 1 && rnd(0xA201L, u, y, v) < 0.015) s = t.rubble();
                    }
                    set(u, y, v, s);
                }
            }
        }

        /** Tuer: Luft im Bereich, Rahmen aus Akzent. */
        public void door(F f, int a0, int b0, int a1, int b1, int y, int h) {
            lclear(f, a0, y, b0, a1, y + h - 1, b1);
        }

        /** Runde Saeule (Radius r) von y0 bis y1 mit Sockel und Kapitell. */
        public void column(int cu, int cv, double r, int y0, int y1) {
            int ir = (int) Math.ceil(r + 1);
            if (!touches(cu - ir, cv - ir, cu + ir, cv + ir)) return;
            for (int u = cu - ir; u <= cu + ir; u++) for (int v = cv - ir; v <= cv + ir; v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d > r + 0.9) continue;
                for (int y = y0; y <= y1; y++) {
                    boolean foot = y <= y0 + 1 || y >= y1 - 1;
                    if (d > r && !foot) continue;
                    set(u, y, v, foot ? t.trim() : (y - y0) % 6 == 3 && d > r - 0.6 ? t.accent() : t.pillar());
                }
            }
        }

        // ── Gesamtaufbau ──

        void all() {
            entrance();
            descent();
            antechamber();
            hub();
            gate();
            arena();
            gatePassage();
            Wing[] w = design.wings();
            wing(WEST, FLOOR, w[0], 0);
            wing(EAST, FLOOR, w[1], 1);
            deepShaft();
            wing(DEEP, DEEP_FLOOR, w[2], 2);
            hubDoors();
            design.hubDeco(this);
            design.arenaDeco(this);
            design.entranceDeco(this);
        }

        // ── Eingang ──

        private void entrance() {
            o.mob(0, 1, 56, design.roomMobs()[0]);
            if (!touches(-14, 46, 14, 64)) return;
            int u0 = -8, u1 = 8, v0 = 48, v1 = 62, h = 9;
            for (int u = Math.max(u0, o.uMin()); u <= Math.min(u1, o.uMax()); u++) for (int v = Math.max(v0, o.vMin()); v <= Math.min(v1, o.vMax()); v++) {
                boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
                boolean corner = (u == u0 || u == u1) && (v == v0 || v == v1);
                set(u, 0, v, edge ? t.trim() : floor(u, v));
                for (int y = 1; y <= h; y++) {
                    BlockState s = AIR;
                    if (corner) s = y == h ? t.accent() : t.pillar();
                    else if (edge) s = y == 1 ? t.trim() : y == h ? t.accent() : (y == 4 && (u + v) % 3 == 0) ? t.glass() : wall(u, y, v);
                    set(u, y, v, s);
                }
                // Stufendach
                int m = Math.min(Math.min(u - u0, u1 - u), Math.min(v - v0, v1 - v));
                for (int y = h + 1; y <= h + 1 + Math.min(m, 4); y++) set(u, y, v, y == h + 1 + Math.min(m, 4) ? (m >= 4 ? t.accent() : t.ceiling()) : AIR);
                if (m < 4) set(u, h + 1 + m, v, t.ceiling());
            }
            for (int y = h + 6; y <= h + 9; y++) set(0, y, 55, y == h + 9 ? t.light() : t.pillar());
            // Portal mit Siegelzeichen
            for (int u = -2; u <= 2; u++) for (int y = 1; y <= 5; y++) air(u, y, v1);
            for (int u = -3; u <= 3; u++) {
                set(u, 6, v1, t.accent());
                set(u, 6, v1 + 1, stairs(HUB, Direction.NORTH, true));
            }
            for (int y = 1; y <= 5; y++) {
                set(-3, y, v1 + 1, t.pillar());
                set(3, y, v1 + 1, t.pillar());
            }
            set(0, 7, v1, t.light());
            set(-1, 7, v1, t.accent());
            set(1, 7, v1, t.accent());
            set(0, 8, v1, t.accent());
            // Pylonen vor dem Portal
            for (int s = -1; s <= 1; s += 2) {
                int u = s * 11, v = 63;
                for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) set(u + du, 1, v + dv, t.trim());
                for (int y = 2; y <= 10; y++) set(u, y, v, y % 4 == 0 ? t.accent() : t.pillar());
                set(u, 11, v, t.light());
                for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                    set(u + d.getStepX(), 2, v + d.getStepZ(), stairs(HUB, d.getOpposite(), false));
                }
            }
            // Weg zum Portal
            for (int v = v1 + 1; v <= 66; v++) for (int u = -2; u <= 2; u++) set(u, 0, v, u == 0 ? t.accent() : t.floor());
        }

        /** Treppe vom Eingang (v 50, y -1) nach Norden hinab bis zur Vorhalle (v 30, y -21). */
        private void descent() {
            if (!touches(-3, 28, 3, 52)) return;
            for (int k = 0; k <= 20; k++) {
                int v = 50 - k, y = -1 - k;
                for (int u = -2; u <= 2; u++) {
                    boolean side = Math.abs(u) == 2;
                    for (int yy = y - 1; yy <= y + 5; yy++) {
                        if (yy > 0) continue;
                        if (yy == 0 && v < 48 && !side) {
                            set(u, yy, v, t.ceiling());
                        } else if (side || yy == y - 1 || yy == y + 5) {
                            boolean lamp = side && yy == y + 3 && k % 4 == 1;
                            set(u, yy, v, lamp ? t.light() : side ? wall(u, yy, v) : t.ceiling());
                        } else if (yy == y) {
                            set(u, yy, v, k == 20 ? t.trim() : stairs(HUB, Direction.SOUTH, false));
                        } else {
                            set(u, yy, v, AIR);
                        }
                    }
                }
            }
        }

        private void antechamber() {
            o.chest(-6, FLOOR + 1, 28, Direction.EAST, common);
            o.mob(4, FLOOR + 1, 25, design.roomMobs()[1]);
            room(HUB, -8, 21, 8, 30, FLOOR, FLOOR + 8);
            if (!touches(-9, 20, 9, 31)) return;
            door(HUB, -1, 30, 1, 30, FLOOR + 1, 4);
            // Mittelstueck: Wasserbecken mit Lichtsaeule
            for (int u = -2; u <= 2; u++) for (int v = 24; v <= 27; v++) {
                boolean rim = Math.abs(u) == 2 || v == 24 || v == 27;
                set(u, FLOOR + 1, v, rim ? slab(false) : t.light());
            }
            for (int y = FLOOR + 2; y <= FLOOR + 6; y++) set(0, y, 25, y == FLOOR + 6 ? t.light() : t.pillar());
            set(0, FLOOR + 2, 26, t.pillar());
        }

        // ── Siegelhalle ──

        private void hub() {
            o.barrel(-14, FLOOR + 1, 18, common);
            o.barrel(14, FLOOR + 1, -10, common);
            o.mob(-8, FLOOR + 1, 14, design.roomMobs()[0]);
            o.mob(9, FLOOR + 1, -6, design.roomMobs()[2]);
            room(HUB, -HUB_U, HUB_V0, HUB_U, HUB_V1, FLOOR, HUB_CEIL);
            if (!touches(-HUB_U, HUB_V0, HUB_U, HUB_V1)) return;
            // Bodenemblem: Ringe um den Schacht
            for (int u = Math.max(-10, o.uMin()); u <= Math.min(10, o.uMax()); u++) for (int v = Math.max(HUB_CV - 10, o.vMin()); v <= Math.min(HUB_CV + 10, o.vMax()); v++) {
                double d = Math.sqrt(u * u + (v - HUB_CV) * (v - HUB_CV));
                if (d > 3.5 && d < 4.6) set(u, FLOOR, v, t.trim());
                else if (d >= 6.5 && d < 7.5) set(u, FLOOR, v, t.accent());
                else if (d >= 9.5 && d < 10.3) set(u, FLOOR, v, t.trim());
            }
            // Saeulenkranz
            for (int k = 0; k < 8; k++) {
                double a = Math.toRadians(22.5 + 45 * k);
                column((int) Math.round(Math.cos(a) * 11), HUB_CV + (int) Math.round(Math.sin(a) * 11), 1.2, FLOOR + 1, HUB_CEIL - 1);
            }
            // Kronleuchter ueber dem Schacht
            for (int y = HUB_CEIL - 3; y < HUB_CEIL; y++) set(0, y, HUB_CV, b(Blocks.CHAIN));
            set(0, HUB_CEIL - 4, HUB_CV, t.light());
            for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                set(d.getStepX(), HUB_CEIL - 4, HUB_CV + d.getStepZ(), t.accent());
                set(2 * d.getStepX(), HUB_CEIL - 4, HUB_CV + 2 * d.getStepZ(), t.accent());
                set(2 * d.getStepX(), HUB_CEIL - 5, HUB_CV + 2 * d.getStepZ(), lantern(true));
            }
        }

        /** Tueren der Siegelhalle (nach den Fluegeln, die eigene Waende setzen). */
        private void hubDoors() {
            if (!touches(-HUB_U - 2, HUB_V0, HUB_U + 2, HUB_V1 + 2)) return;
            door(HUB, -1, HUB_V1, 1, HUB_V1 + 1, FLOOR + 1, 4);
            for (int s = -1; s <= 1; s += 2) {
                int u = s * HUB_U;
                for (int v = HUB_CV - 1; v <= HUB_CV + 1; v++) for (int y = FLOOR + 1; y <= FLOOR + 4; y++) {
                    air(u, y, v);
                    air(u + s, y, v);
                }
                for (int v = HUB_CV - 2; v <= HUB_CV + 2; v += 4) for (int y = FLOOR + 1; y <= FLOOR + 5; y++) set(u - s, y, v, t.pillar());
                for (int v = HUB_CV - 2; v <= HUB_CV + 2; v++) set(u - s, FLOOR + 5, v, t.accent());
                set(u - s, FLOOR + 6, HUB_CV, t.light());
            }
        }

        // ── Siegeltor und Arena ──

        private void gate() {
            int gv = HUB_V0;
            if (!touches(-7, gv - 8, 7, gv + 2)) return;
            // Rahmen
            for (int u = -6; u <= 6; u++) for (int y = FLOOR; y <= FLOOR + 13; y++) for (int v = gv - 1; v <= gv + 1; v++) {
                boolean opening = Math.abs(u) <= 3 && y >= FLOOR + 1 && y <= FLOOR + 9;
                if (opening) continue;
                boolean front = v == gv + 1;
                if (front && Math.abs(u) <= 5 && y <= FLOOR + 11) {
                    if (Math.abs(u) == 4 || Math.abs(u) == 5) set(u, y, v, Math.abs(u) == 5 ? t.pillar() : t.accent());
                    else if (y >= FLOOR + 10) set(u, y, v, y == FLOOR + 10 ? t.accent() : t.trim());
                    continue;
                }
                if (!front) set(u, y, v, Math.abs(u) <= 4 && y <= FLOOR + 10 ? t.accent() : t.trim());
            }
            // Siegelfeld im Tor
            for (int u = -3; u <= 3; u++) for (int y = FLOOR + 1; y <= FLOOR + 9; y++) set(u, y, gv, b(SealBlocks.SEAL_FIELD.get()));
            for (int u = -3; u <= 3; u++) for (int y = FLOOR + 1; y <= FLOOR + 9; y++) {
                air(u, y, gv + 1);
                air(u, y, gv - 1);
            }
            // drei Schloesser: links, rechts (im Rahmen), Mitte (Sockel vor dem Tor)
            BlockState lock = CityPalette.facing(b(SealBlocks.SEAL_LOCK.get()), Direction.SOUTH).setValue(SealLockBlock.LIT, false);
            set(-4, FLOOR + 3, gv, lock);
            set(4, FLOOR + 3, gv, lock);
            air(-4, FLOOR + 3, gv + 1);
            air(4, FLOOR + 3, gv + 1);
            set(0, FLOOR + 1, gv + 1, lock);
            set(0, FLOOR, gv + 1, t.trim());
            // Siegelzeichen ueber dem Tor
            for (int u = -2; u <= 2; u++) for (int y = FLOOR + 11; y <= FLOOR + 15; y++) {
                int dd = Math.abs(u) + Math.abs(y - (FLOOR + 13));
                if (dd <= 2) set(u, y, gv + 1, dd == 0 ? t.light() : t.accent());
            }
        }

        /** Gang vom Tor hinab zur Arena (nach der Arena, schneidet ihre Wand). */
        private void gatePassage() {
            int gv = HUB_V0;
            if (!touches(-6, gv - 9, 6, gv)) return;
            for (int v = gv - 1; v >= gv - 8; v--) {
                int step = Math.min(gv - 2 - v, FLOOR - ARENA_FLOOR);
                int fy = v >= gv - 1 ? FLOOR : FLOOR - Math.max(0, step);
                for (int u = -5; u <= 5; u++) for (int y = ARENA_FLOOR - 1; y <= FLOOR + 11; y++) {
                    if (Math.abs(u) == 5 || y == FLOOR + 11) set(u, y, v, Math.abs(u) == 5 && y == fy + 4 && v % 2 == 0 ? t.light() : wall(u, y, v));
                    else if (y < fy) set(u, y, v, t.wall());
                    else if (y == fy) set(u, y, v, v == gv - 1 || step >= FLOOR - ARENA_FLOOR || Math.abs(u) == 4 ? t.trim() : stairs(HUB, Direction.SOUTH, false));
                    else if (v != gv) set(u, y, v, AIR);
                }
            }
        }

        private void arena() {
            o.mob(BOSS_U, BOSS_Y, BOSS_V, design.boss());
            o.chest(-2, ARENA_FLOOR + 2, ARENA_V - ARENA_R - 3, Direction.SOUTH, bossLoot);
            o.chest(2, ARENA_FLOOR + 2, ARENA_V - ARENA_R - 3, Direction.SOUTH, bossLoot);
            int r = ARENA_R + 3;
            if (!touches(-r, ARENA_V - r - 6, r, ARENA_V + r)) return;
            int top = -2;
            for (int u = Math.max(-r, o.uMin()); u <= Math.min(r, o.uMax()); u++) for (int v = Math.max(ARENA_V - r, o.vMin()); v <= Math.min(ARENA_V + r, o.vMax()); v++) {
                int dv = v - ARENA_V;
                double d = Math.sqrt(u * u + dv * dv);
                if (d > ARENA_R + 2.5) continue;
                double ang = Math.toDegrees(Math.atan2(dv, u));
                for (int y = ARENA_FLOOR - 3; y <= top; y++) {
                    // Kuppel: aussen ARENA_R+2.5, innen ARENA_R; ab y -10 gewoelbt
                    double hy = Math.max(0, y - (-12)) / (double) (top - (-12) + 1);
                    double rIn = ARENA_R * Math.sqrt(Math.max(0, 1 - hy * hy * 1.1)), rOut = (ARENA_R + 2.5) * Math.sqrt(Math.max(0, 1 - hy * hy * 0.95));
                    if (d > rOut) continue;
                    BlockState s;
                    if (y < ARENA_FLOOR) s = t.wall();
                    else if (y == top) s = t.ceiling();
                    else if (d >= rIn) {
                        boolean pil = Math.abs(((ang + 360) % 30) - 15) > 13.4;
                        s = y == ARENA_FLOOR + 1 ? t.trim() : pil ? (y == ARENA_FLOOR + 6 && d < rIn + 0.9 ? t.light() : t.pillar()) : y > -12 ? t.ceiling() : wall(u, y, v);
                        if (y > -12 && rnd(0xA301L, u, y, v) < 0.05) s = t.light();
                    } else if (y == ARENA_FLOOR) {
                        s = arenaFloor(u, dv, d, ang);
                    } else s = AIR;
                    set(u, y, v, s);
                }
                // Graben mit Gefahr (r 17..19), Bruecken in den vier Richtungen
                if (d >= 17 && d < 19.5) {
                    boolean bridge = Math.abs(u) <= 1 || Math.abs(dv) <= 1;
                    if (!bridge) {
                        set(u, ARENA_FLOOR, v, t.hazard());
                        set(u, ARENA_FLOOR - 1, v, t.hazard());
                        set(u, ARENA_FLOOR - 2, v, t.hazardBed());
                    } else {
                        set(u, ARENA_FLOOR, v, t.trim());
                    }
                }
            }
            // Saeulenkranz (r 13)
            for (int k = 0; k < 8; k++) {
                double a = Math.toRadians(22.5 + 45 * k);
                column((int) Math.round(Math.cos(a) * 13), ARENA_V + (int) Math.round(Math.sin(a) * 13), 1.6, ARENA_FLOOR + 1, -8);
            }
            // Schatznische im Norden
            int nv = ARENA_V - ARENA_R;
            for (int u = -4; u <= 4; u++) for (int v = nv - 5; v <= nv + 1; v++) for (int y = ARENA_FLOOR; y <= ARENA_FLOOR + 7; y++) {
                boolean shell = Math.abs(u) == 4 || v == nv - 5 || y == ARENA_FLOOR || y == ARENA_FLOOR + 7;
                set(u, y, v, shell ? (y == ARENA_FLOOR ? t.trim() : wall(u, y, v)) : AIR);
            }
            for (int u = -3; u <= 3; u++) for (int v = nv - 4; v <= nv - 2; v++) set(u, ARENA_FLOOR + 1, v, t.accent());
            set(0, ARENA_FLOOR + 2, nv - 3, t.light());
            set(-3, ARENA_FLOOR + 5, nv - 4, t.light());
            set(3, ARENA_FLOOR + 5, nv - 4, t.light());
        }

        private BlockState arenaFloor(int u, int dv, double d, double ang) {
            if (d < 3) return t.light();
            if (d < 4.2) return t.accent();
            if (d < 5) return t.trim();
            double ray = Math.abs(((ang + 360 + 22.5) % 45) - 22.5) * Math.PI / 180 * d;
            if (ray < 0.7 && d < 16) return t.accent();
            if (d > 15.5 && d < 16.5) return t.trim();
            return floor(u, dv);
        }

        // ── Wendeltreppe zum Tiefen Fluegel ──

        static final int[][] RING = {{-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}};

        private void deepShaft() {
            if (!touches(-3, HUB_CV - 3, 3, HUB_CV + 4)) return;
            int bottom = DEEP_FLOOR + 1, steps = FLOOR - bottom + 1;
            for (int u = -2; u <= 2; u++) for (int v = HUB_CV - 2; v <= HUB_CV + 2; v++) {
                boolean wallCell = Math.abs(u) == 2 || Math.abs(v - HUB_CV) == 2;
                for (int y = DEEP_FLOOR; y <= FLOOR; y++) {
                    if (wallCell) set(u, y, v, y == FLOOR ? t.trim() : (y - DEEP_FLOOR) % 6 == 3 ? t.light() : wall(u, y, v));
                    else if (u == 0 && v == HUB_CV) set(u, y, v, (y - DEEP_FLOOR) % 5 == 2 ? t.light() : t.pillar());
                    else set(u, y, v, y == DEEP_FLOOR ? t.trim() : AIR);
                }
            }
            int start = 4;
            for (int i = 0; i < steps; i++) {
                int[] c = RING[(i + start) % 8], n = RING[(i + start + 1) % 8];
                Direction up = Math.abs(n[0] - c[0]) > 0 ? (n[0] > c[0] ? Direction.EAST : Direction.WEST) : (n[1] > c[1] ? Direction.SOUTH : Direction.NORTH);
                set(c[0], bottom + i, HUB_CV + c[1], stairs(HUB, up, false));
                if (i > 0) set(c[0], bottom + i - 1, HUB_CV + c[1], t.wall());
            }
            // Gelaender oben (Ausstieg nach Norden), Eingang unten nach Sueden
            for (int u = -2; u <= 2; u++) for (int v = HUB_CV - 2; v <= HUB_CV + 2; v++) {
                boolean border = Math.abs(u) == 2 || Math.abs(v - HUB_CV) == 2;
                if (border && !(u == 0 && v == HUB_CV - 2)) set(u, FLOOR + 1, v, b(Blocks.POLISHED_BLACKSTONE_WALL));
            }
            set(0, FLOOR + 1, HUB_CV, lantern(false));
            for (int y = bottom; y <= bottom + 2; y++) air(1, y, HUB_CV + 2);
        }

        // ── Fluegel ──

        private void wing(F f, int y0, Wing type, int index) {
            // Zugang (a 0..6; im Tiefen Fluegel ab a 3 hinter dem Schacht)
            int start = f == DEEP ? 3 : 0;
            room(f, start, -2, 6, 2, y0, y0 + 5);
            switch (type) {
                case GAUNTLET -> gauntlet(f, y0, index);
                case PARKOUR -> parkour(f, y0, index);
                case MAZE -> maze(f, y0, index);
                case GUARDIAN -> guardian(f, y0, index);
                case CLIMB -> climb(f, y0, index);
                case TRAPS -> traps(f, y0, index);
            }
            if (ltouches(f, start - 1, -2, 8, 2)) {
                door(f, start == 0 ? 0 : 2, -1, start, 1, y0 + 1, 3);
                door(f, 5, -1, 7, 1, y0 + 1, 3);
            }
        }

        private String mob(int i) {
            String[] m = design.roomMobs();
            return m[Math.floorMod(i, m.length)];
        }

        private String spawnerMob(int i) {
            String[] m = design.spawnerMobs();
            return m[Math.floorMod(i, m.length)];
        }

        private void lchest(F f, int a, int y, int b, Direction localFacing, String table) {
            o.chest(f.u(a, b), y, f.v(a, b), f.d(localFacing), table);
        }

        private void lspawner(F f, int a, int y, int b, String type) {
            o.spawner(f.u(a, b), y, f.v(a, b), type);
        }

        private void lmob(F f, int a, int y, int b, String type) {
            o.mob(f.u(a, b), y, f.v(a, b), type);
        }

        /** Drei Hallen mit je zwei Spawnern; Schatznische dahinter. */
        private void gauntlet(F f, int y0, int index) {
            int w = 1;
            for (int i = 0; i < 3; i++) {
                int a0 = 6 + 13 * i;
                lspawner(f, a0 + 4, y0 + 2, -4, spawnerMob(i));
                lspawner(f, a0 + 8, y0 + 2, 4, spawnerMob(i + 1));
                lmob(f, a0 + 6, y0 + 1, 0, mob(i + index));
                if (i == 1) lchest(f, a0 + 6, y0 + 1, -6, Direction.SOUTH, common);
            }
            lchest(f, 48, y0 + 1, 0, Direction.WEST, "seal_key");
            for (int i = 0; i < 3; i++) {
                int a0 = 6 + 13 * i, a1 = a0 + 12;
                room(f, a0, -7, a1, 7, y0, y0 + 9);
                if (!ltouches(f, a0, -7, a1, 7)) continue;
                // Spawner-Sockel, Deckungsbloecke, Gitterfenster
                lset(f, a0 + 4, y0 + 1, -4, t.trim());
                lset(f, a0 + 8, y0 + 1, 4, t.trim());
                for (int[] c : new int[][] {{a0 + 3, 1}, {a0 + 9, -1}}) {
                    for (int da = 0; da <= 1; da++) for (int y = y0 + 1; y <= y0 + 2; y++) lset(f, c[0] + da, y, c[1], t.wallAlt());
                    lset(f, c[0], y0 + 3, c[1], slab(false));
                }
                for (int a = a0 + 2; a <= a1 - 2; a += 5) for (int s = -1; s <= 1; s += 2) {
                    lset(f, a, y0 + 5, s * 7, b(Blocks.IRON_BARS));
                    lset(f, a, y0 + 6, s * 7, b(Blocks.IRON_BARS));
                }
                if (rnd(0xB101L, i, index, 0) < 0.7) lset(f, a0 + 1, y0 + 8, -6, b(Blocks.COBWEB));
                lset(f, a1 - 1, y0 + 8, 6, b(Blocks.COBWEB));
            }
            for (int i = 0; i < 3; i++) {
                int a = 6 + 13 * i;
                if (ltouches(f, a - 1, -2, a + 1, 2)) {
                    door(f, a - 1, -w, a, w, y0 + 1, 4);
                    lfill(f, a - 1, y0 + 5, -w - 1, a, y0 + 5, w + 1, t.accent());
                }
            }
            // Nische mit Schatz
            room(f, 45, -3, 51, 3, y0, y0 + 6);
            if (ltouches(f, 43, -3, 51, 3)) {
                door(f, 44, -1, 45, 1, y0 + 1, 4);
                lset(f, 50, y0 + 1, 0, t.accent());
                lset(f, 50, y0 + 2, 0, t.light());
                lset(f, 48, y0, 0, t.accent());
            }
        }

        /** Trittsteine: (a, b, Hoehe ueber y0, Art 0 Saeule 1x1, 1 Saeule 2x2, 2 Kettenplattform 3x3). */
        static List<int[]> parkourPath(Random r) {
            List<int[]> out = new ArrayList<>();
            int a = 10, b = 0, h = 0; // Rand des Startsimses
            int n = 0;
            while (a < 36) {
                int kind = n % 5 == 2 ? 2 : r.nextInt(3) == 0 ? 1 : 0;
                int da = 2 + r.nextInt(2);
                int nb = Math.max(-5, Math.min(5, b + r.nextInt(5) - 2));
                int want = (int) Math.round((a - 10) / 26.0 * 3.0);
                int nh = h + Integer.signum(want - h + (r.nextInt(3) - 1));
                nh = Math.max(-1, Math.min(4, nh));
                double dist = Math.sqrt(da * da + (nb - b) * (nb - b));
                if (nh > h && dist > 3.0 || n == 0) nh = Math.min(nh, h);
                if (dist > 3.6) nb = b + Integer.signum(nb - b);
                a += da;
                b = nb;
                h = nh;
                out.add(new int[] {a, b, h, kind});
                n++;
            }
            // Finale: auf Hoehe 3 steigen (Endsims a 45, Block y0 + 3), letzter Stein ab a 42
            while (h < 3) {
                a += 2;
                b -= Integer.signum(b);
                h++;
                out.add(new int[] {a, b, h, 0});
            }
            while (a < 42) {
                a += Math.min(3, 42 - a + 1);
                b -= Integer.signum(b);
                out.add(new int[] {a, b, h, 1});
            }
            return out;
        }

        /** Hoehle ueber einer Gefahrengrube; vom niedrigen Start zum hohen Ende ueber Trittsteine. */
        private void parkour(F f, int y0, int index) {
            Random r = new Random((long) (rnd(0xB202L, index, 0, 0) * 1e12));
            List<int[]> path = parkourPath(r);
            lchest(f, 49, y0 + 4, 0, Direction.WEST, "seal_key");
            lmob(f, 26, y0 + 4, 9, mob(index + 1));
            lmob(f, 36, y0 + 4, -9, mob(index + 2));
            lspawner(f, 50, y0 + 4, 4, spawnerMob(index));
            int a0 = 7, a1 = 52, bw = 10, bot = y0 - 7, top = y0 + 13;
            if (!ltouches(f, a0, -bw - 2, a1, bw + 2)) return;
            int[] L = local(f);
            for (int a = Math.max(a0, L[0]); a <= Math.min(a1, L[1]); a++) for (int b = Math.max(-bw - 1, L[2]); b <= Math.min(bw + 1, L[3]); b++) {
                int u = f.u(a, b), v = f.v(a, b);
                double rough = rnd(0xB203L, a >> 1, 0, b >> 1) * 1.6;
                boolean wallCell = a == a0 || a == a1 || Math.abs(b) >= bw - (int) rough;
                for (int y = bot; y <= top; y++) {
                    boolean ceil = y >= top - (int) (rnd(0xB204L, a >> 1, 1, b >> 1) * 2);
                    BlockState s;
                    if (wallCell || ceil || y == bot) s = y == bot ? t.hazardBed() : wall(u, y, v);
                    else if (y <= bot + 2) s = t.hazard();
                    else s = AIR;
                    set(u, y, v, s);
                }
            }
            // Startsims und Ende
            for (int a = a0 + 1; a <= 10; a++) for (int b = -3; b <= 3; b++) for (int y = bot + 1; y <= y0; y++) lset(f, a, y, b, y == y0 ? (Math.abs(b) == 3 ? t.trim() : floor(f.u(a, b), f.v(a, b))) : t.wall());
            for (int a = 45; a <= a1 - 1; a++) for (int b = -4; b <= 4; b++) for (int y = bot + 1; y <= y0 + 3; y++) lset(f, a, y, b, y == y0 + 3 ? (Math.abs(b) == 4 ? t.trim() : t.floorAlt()) : t.wall());
            for (int y = y0 + 4; y <= y0 + 6; y++) lset(f, 51, y, 0, y == y0 + 6 ? t.light() : t.pillar());
            // Leiter aus der Grube zurueck auf den Startsims
            for (int y = bot + 3; y <= y0; y++) lset(f, 11, y, -3, CityPalette.facing(b(Blocks.LADDER), f.d(Direction.EAST)));
            lfill(f, 7, y0 - 1, -3, 10, y0 - 1, -3, t.wall());
            // Trittsteine
            for (int[] p : path) {
                int a = p[0], b = p[1], h = y0 + p[2], kind = p[3];
                int sz = kind == 0 ? 0 : 1;
                for (int da = -sz; da <= (kind == 1 ? 0 : sz); da++) for (int db = -sz; db <= (kind == 1 ? 0 : sz); db++) {
                    if (kind == 2) {
                        lset(f, a + da, h, b + db, Math.abs(da) + Math.abs(db) == 0 ? t.trim() : slab(true));
                    } else {
                        for (int y = bot + 1; y <= h; y++) lset(f, a + da, y, b + db, y == h ? t.trim() : t.pillar());
                    }
                }
                if (kind == 2) for (int y = h + 1; y < top; y++) {
                    lset(f, a - 1, y, b - 1, b(Blocks.CHAIN));
                    lset(f, a + 1, y, b + 1, b(Blocks.CHAIN));
                }
            }
            // Nischen an den Waenden fuer Schuetzen und Spawner
            for (int[] n : new int[][] {{26, 1}, {36, -1}, {30, -1}}) {
                int b = n[1] * (bw - 1);
                for (int da = -1; da <= 1; da++) for (int y = y0 + 4; y <= y0 + 7; y++) lset(f, n[0] + da, y, b, AIR);
                for (int da = -1; da <= 1; da++) lset(f, n[0] + da, y0 + 3, b, t.trim());
            }
            lset(f, 30, y0 + 5, -(bw - 1), t.trim());
            for (int a = a0 + 4; a < a1; a += 6) {
                lset(f, a, y0 + 8, bw - 1, t.light());
                lset(f, a + 3, y0 + 8, -(bw - 1), t.light());
            }
        }

        /** Labyrinth: Zellen 9 x 9 (je 3 frei + 1 Wand), Eingang in der Mitte der Westseite, Schatz in der fernsten Zelle. */
        static boolean[][][] mazeWalls(Random r, int n) {
            // [0] Wand oestlich der Zelle (a+), [1] Wand suedlich (b+)
            boolean[][][] w = new boolean[2][n][n];
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
                w[0][i][j] = true;
                w[1][i][j] = true;
            }
            boolean[][] seen = new boolean[n][n];
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[] {0, n / 2});
            seen[0][n / 2] = true;
            int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            while (!stack.isEmpty()) {
                int[] c = stack.peek();
                List<int[]> next = new ArrayList<>();
                for (int[] d : dirs) {
                    int x = c[0] + d[0], y = c[1] + d[1];
                    if (x >= 0 && y >= 0 && x < n && y < n && !seen[x][y]) next.add(d);
                }
                if (next.isEmpty()) {
                    stack.pop();
                    continue;
                }
                int[] d = next.get(r.nextInt(next.size()));
                int x = c[0] + d[0], y = c[1] + d[1];
                if (d[0] == 1) w[0][c[0]][c[1]] = false;
                else if (d[0] == -1) w[0][x][y] = false;
                else if (d[1] == 1) w[1][c[0]][c[1]] = false;
                else w[1][x][y] = false;
                seen[x][y] = true;
                stack.push(new int[] {x, y});
            }
            // ein paar Schleifen
            for (int k = 0; k < n; k++) {
                int i = r.nextInt(n - 1), j = r.nextInt(n - 1);
                w[r.nextInt(2)][i][j] = false;
            }
            return w;
        }

        static int[] mazeFarthest(boolean[][][] w, int n) {
            int[][] dist = new int[n][n];
            for (int[] row : dist) java.util.Arrays.fill(row, -1);
            ArrayDeque<int[]> q = new ArrayDeque<>();
            q.add(new int[] {0, n / 2});
            dist[0][n / 2] = 0;
            int[] best = {0, n / 2};
            while (!q.isEmpty()) {
                int[] c = q.poll();
                int i = c[0], j = c[1];
                if (dist[i][j] > dist[best[0]][best[1]]) best = c;
                int[][] nb = {{i + 1, j}, {i - 1, j}, {i, j + 1}, {i, j - 1}};
                for (int[] m : nb) {
                    if (m[0] < 0 || m[1] < 0 || m[0] >= n || m[1] >= n || dist[m[0]][m[1]] >= 0) continue;
                    boolean open = m[0] > i ? !w[0][i][j] : m[0] < i ? !w[0][m[0]][m[1]] : m[1] > j ? !w[1][i][j] : !w[1][m[0]][m[1]];
                    if (!open) continue;
                    dist[m[0]][m[1]] = dist[i][j] + 1;
                    q.add(m);
                }
            }
            return best;
        }

        private void maze(F f, int y0, int index) {
            int n = 9, cell = 4, a0 = 7, b0 = -18;
            Random r = new Random((long) (rnd(0xB301L, index, 0, 0) * 1e12));
            boolean[][][] w = mazeWalls(r, n);
            int[] far = mazeFarthest(w, n);
            int ka = a0 + far[0] * cell + 2, kb = b0 + far[1] * cell + 2;
            lchest(f, ka, y0 + 1, kb, Direction.WEST, "seal_key");
            lmob(f, a0 + 4 * cell + 2, y0 + 1, b0 + 2 * cell + 2, mob(index));
            lmob(f, a0 + 6 * cell + 2, y0 + 1, b0 + 6 * cell + 2, mob(index + 1));
            lmob(f, a0 + 2 * cell + 2, y0 + 1, b0 + 7 * cell + 2, mob(index + 2));
            lspawner(f, a0 + 4 * cell + 2, y0 + 1, b0 + 4 * cell + 2, spawnerMob(index));
            lchest(f, a0 + 8 * cell + 2, y0 + 1, b0 + 1, Direction.SOUTH, common);
            int a1 = a0 + n * cell, b1 = b0 + n * cell;
            if (!ltouches(f, a0, b0, a1, b1)) return;
            int[] L = local(f);
            for (int a = Math.max(a0, L[0]); a <= Math.min(a1, L[1]); a++) for (int b = Math.max(b0, L[2]); b <= Math.min(b1, L[3]); b++) {
                int u = f.u(a, b), v = f.v(a, b);
                int ca = (a - a0) / cell, cb = (b - b0) / cell, ra = (a - a0) % cell, rb = (b - b0) % cell;
                boolean solid;
                if (a == a0 || b == b0 || a == a1 || b == b1) solid = true;
                else if (ra == 0 && rb == 0) solid = true;
                else if (ra == 0) solid = ca > 0 && w[0][ca - 1][Math.min(cb, n - 1)];
                else if (rb == 0) solid = cb > 0 && w[1][Math.min(ca, n - 1)][cb - 1];
                else solid = false;
                set(u, y0, v, (ra == 0 || rb == 0) ? t.trim() : rnd(0xB302L, ca, 0, cb) < 0.08 ? b(Blocks.MAGMA_BLOCK) : floor(u, v));
                for (int y = y0 + 1; y <= y0 + 4; y++) {
                    BlockState s = solid ? ((ra == 0 && rb == 0) ? t.pillar() : wall(u, y, v)) : AIR;
                    if (!solid && y == y0 + 4 && rnd(0xB303L, a, y, b) < 0.06) s = b(Blocks.COBWEB);
                    set(u, y, v, s);
                }
                boolean lamp = ra == 2 && rb == 2 && (ca * 7 + cb * 3) % 5 == 0;
                set(u, y0 + 5, v, lamp ? t.light() : t.ceiling());
            }
            door(f, a0, b0 + (n / 2) * cell + 1, a0, b0 + (n / 2) * cell + 3, y0 + 1, 3);
        }

        /** Runde Kammer mit Grube und Saeulen; in der Mitte der Siegelwaechter. */
        private void guardian(F f, int y0, int index) {
            int ca = 25, rr = 13;
            o.elite(f.u(ca, 0), y0 - 1, f.v(ca, 0), design.eliteMob());
            lmob(f, ca - 4, y0 + 1, 8, mob(index));
            lmob(f, ca + 5, y0 + 1, -8, mob(index + 1));
            lspawner(f, ca + 10, y0 + 2, 0, spawnerMob(index));
            lchest(f, ca + 11, y0 + 1, 5, Direction.WEST, common);
            room(f, 6, -2, ca - rr, 2, y0, y0 + 5);
            if (ltouches(f, 6, -2, ca - rr, 2)) door(f, ca - rr, -1, ca - rr, 1, y0 + 1, 3);
            if (!ltouches(f, ca - rr - 1, -rr - 1, ca + rr + 1, rr + 1)) return;
            int[] L = local(f);
            int top = y0 + 14;
            for (int a = Math.max(ca - rr - 1, L[0]); a <= Math.min(ca + rr + 1, L[1]); a++) for (int b = Math.max(-rr - 1, L[2]); b <= Math.min(rr + 1, L[3]); b++) {
                double d = Math.sqrt((a - ca) * (a - ca) + b * b);
                if (d > rr + 1.2) continue;
                int u = f.u(a, b), v = f.v(a, b);
                for (int y = y0 - 3; y <= top; y++) {
                    double hy = Math.max(0, y - (y0 + 9)) / 6.0;
                    double rin = rr * Math.sqrt(Math.max(0, 1 - hy * hy));
                    BlockState s;
                    if (d >= rin || y == top) s = (y == y0 + 4 && Math.abs(Math.atan2(b, a - ca)) % 0.8 < 0.12) ? t.light() : wall(u, y, v);
                    else if (d < 7 && y < y0 - 1) s = y == y0 - 2 ? (d < 2 ? t.accent() : floor(u, v)) : t.wall();
                    else if (d >= 7 && y < y0) s = t.wall();
                    else if (d >= 7 && y == y0) s = d < 8 ? stairs(f, Direction.EAST, false) : floor(u, v);
                    else if (d < 7 && y == y0 - 1) s = AIR;
                    else s = AIR;
                    if (d >= 7 && d < 8 && y == y0) {
                        Direction in = Math.abs(a - ca) > Math.abs(b) ? (a > ca ? Direction.WEST : Direction.EAST) : (b > 0 ? Direction.NORTH : Direction.SOUTH);
                        s = stairs(f, in.getOpposite(), false);
                    }
                    set(u, y, v, s);
                }
            }
            for (int k = 0; k < 6; k++) {
                double ang = Math.toRadians(30 + 60 * k);
                int a = ca + (int) Math.round(Math.cos(ang) * 10), b = (int) Math.round(Math.sin(ang) * 10);
                column(f.u(a, b), f.v(a, b), 0.8, y0 + 1, y0 + 10);
            }
            lset(f, ca + 10, y0 + 1, 0, t.trim());
            door(f, ca - rr - 2, -1, ca - rr + 1, 1, y0 + 1, 3);
            lset(f, ca, top - 1, 0, b(Blocks.CHAIN));
            lset(f, ca, top - 2, 0, t.light());
        }

        /** Schacht mit Simsen an den Waenden; Schatz oben. */
        static List<int[]> climbLedges(int a0, int a1, int b0, int b1) {
            // Ring 1 Block innerhalb der Waende, im Uhrzeigersinn; Hoehe steigt alle 3 Zellen um 1, Luecken alle 8
            List<int[]> ring = new ArrayList<>();
            for (int a = a0 + 1; a < a1 - 1; a++) ring.add(new int[] {a, b0 + 1});
            for (int b = b0 + 1; b < b1 - 1; b++) ring.add(new int[] {a1 - 1, b});
            for (int a = a1 - 1; a > a0 + 1; a--) ring.add(new int[] {a, b1 - 1});
            for (int b = b1 - 1; b > b0 + 1; b--) ring.add(new int[] {a0 + 1, b});
            List<int[]> out = new ArrayList<>();
            for (int i = 0; i < ring.size() * 2 - 6; i++) {
                int[] c = ring.get(i % ring.size());
                int h = i / 3;
                boolean gap = i % 8 == 6;
                if (h > 15) break;
                if (!gap) out.add(new int[] {c[0], c[1], h, i});
            }
            return out;
        }

        private void climb(F f, int y0, int index) {
            int a0 = 9, a1 = 23, b0 = -7, b1 = 7, top = y0 + 19;
            List<int[]> ledges = climbLedges(a0, a1, b0, b1);
            int[] last = ledges.get(ledges.size() - 1);
            int lastH = y0 + last[2];
            lchest(f, a0 + 2, lastH + 1, b0 + 1, Direction.SOUTH, "seal_key");
            lspawner(f, (a0 + a1) / 2, y0 + 1, 2, spawnerMob(index));
            lmob(f, (a0 + a1) / 2, y0 + 1, 0, mob(index));
            lmob(f, (a0 + a1) / 2 + 2, y0 + 1, 2, mob(index + 1));
            room(f, 6, -2, a0, 2, y0, y0 + 5);
            if (!ltouches(f, a0, b0, a1, b1)) return;
            room(f, a0, b0, a1, b1, y0, top);
            door(f, a0, -1, a0, 1, y0 + 1, 3);
            for (int[] l : ledges) {
                int a = l[0], b = l[1], h = y0 + l[2];
                if (l[2] == 0) continue;
                lset(f, a, h, b, l[3] % 5 == 0 ? t.accent() : t.trim());
                // zweite Reihe nach innen
                int ia = a == a0 + 1 ? a + 1 : a == a1 - 1 ? a - 1 : a, ib = b == b0 + 1 ? b + 1 : b == b1 - 1 ? b - 1 : b;
                lset(f, ia, h, ib, slab(true));
            }
            // Plattform oben
            for (int a = a0 + 1; a <= a0 + 4; a++) for (int b = b0 + 1; b <= b0 + 3; b++) lset(f, a, lastH, b, a == a0 + 4 || b == b0 + 3 ? t.trim() : t.floorAlt());
            lset(f, a0 + 4, lastH + 1, b0 + 1, t.light());
            // Mittelsaeule mit Lichtern
            for (int y = y0 + 1; y < top; y++) lset(f, (a0 + a1) / 2, y, 0, (y - y0) % 5 == 4 ? t.light() : t.pillar());
        }

        /** Gang mit Pfeilboden, Magma-Trittsteinen und Spinnweben; Schatz am Ende. */
        private void traps(F f, int y0, int index) {
            lchest(f, 49, y0 + 1, 0, Direction.WEST, "seal_key");
            lmob(f, 36, y0 + 1, 0, mob(index));
            lmob(f, 39, y0 + 1, 1, mob(index + 1));
            for (int a = 8; a <= 20; a += 2) for (int b = -1; b <= 1; b++) {
                if ((a / 2 + b) % 2 == 0) o.dispenser(f.u(a, b), y0, f.v(a, b));
            }
            room(f, 6, -2, 52, 2, y0, y0 + 6);
            if (!ltouches(f, 6, -2, 52, 2)) return;
            BlockState plate = b(Blocks.STONE_PRESSURE_PLATE);
            BlockState dispUp = CityPalette.facingAll(b(Blocks.DISPENSER), Direction.UP);
            // Pfeilboden (a 8..20): Werfer nach oben unter Druckplatten, Schachbrett
            for (int a = 8; a <= 20; a += 2) for (int b = -1; b <= 1; b++) {
                if ((a / 2 + b) % 2 != 0) continue;
                lset(f, a, y0, b, dispUp);
                lset(f, a, y0 + 1, b, plate);
            }
            // Magma mit Trittsteinen (a 23..33)
            for (int a = 23; a <= 33; a++) for (int b = -1; b <= 1; b++) {
                boolean stone = (a % 3 == 0 && b == ((a / 3) % 3) - 1);
                lset(f, a, y0, b, stone ? t.trim() : b(Blocks.MAGMA_BLOCK));
                if (stone) lset(f, a, y0 + 1, b, slab(false));
            }
            // niedrige Decke mit Spinnweben (a 35..42)
            for (int a = 35; a <= 42; a++) for (int b = -1; b <= 1; b++) {
                lset(f, a, y0 + 5, b, t.wall());
                lset(f, a, y0 + 4, b, t.wall());
                if (rnd(0xB501L, a, index, b) < 0.25) lset(f, a, y0 + 3, b, b(Blocks.COBWEB));
            }
            // Schatzraum
            lset(f, 50, y0 + 1, 0, t.accent());
            lset(f, 50, y0 + 2, 0, t.light());
            lfill(f, 45, y0 + 6, -2, 52, y0 + 6, 2, t.accent());
        }
    }

    protected static BlockState b(Block x) {
        return x.defaultBlockState();
    }
}
