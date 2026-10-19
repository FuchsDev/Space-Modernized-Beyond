package com.space_beyond.dim.dungeon;

import com.space_beyond.dim.dungeon.seal.SealBlocks;
import com.space_beyond.dim.dungeon.seal.SealLockBlock;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.CityRandom;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Frei gestalteter Siegel-Dungeon: jeder Planet baut seinen eigenen Grundriss ({@link #build}), das Prinzip bleibt
 * gleich - drei Siegelschluessel (Schatzkisten {@code seal_key} oder Siegelwaechter), ein Siegeltor mit drei
 * Schloessern ({@link Kit#sealGate}), dahinter die Arena mit dem Boss ({@link Kit#boss}).
 * <p>
 * Gemalt, geplant und abgetastet wird mit demselben Code: {@link #build} laeuft je Chunk mit einem Ausschnitt
 * ({@link Kit#u0()}..{@link Kit#v1()}); alle Kit-Primitive schneiden selbst zu. Plan-Eintraege (Truhen, Spawner,
 * Mobs, Boss) muessen <b>unabhaengig vom Ausschnitt</b> aufgerufen werden (nicht hinter {@code if (!k.touches(..))
 * return;}), sonst fehlen sie im Plan.
 * <p>
 * Lokale Koordinaten: u = Ost, v = Sued, y relativ zur Grundhoehe (y 0 = Gelaendeoberflaeche, y 1 = Fuesse darauf).
 * Gedreht wird beim Malen automatisch (auch Treppen/Leitern/Truhen).
 */
public abstract class CustomDungeon implements DungeonDesign {

    /** Bauwerk (Bloecke und Plan-Eintraege) mit dem Baukasten. */
    protected abstract void build(Kit k);

    /** Startpunkt eines Spielers am Eingang (Fuesse, lokal {u, y, v}) - fuer die Erreichbarkeits-Tests. */
    public abstract int[] entry();

    /** Registry-ID des Bosses (z. B. {@code space_beyond:cryo_behemoth}). */
    public abstract String boss();

    @Override
    public void plan(DungeonPlan p) {
        build(new Kit(new PlanOut(p), this));
    }

    @Override
    public void paint(DungeonPainter w) {
        build(new Kit(w, this));
    }

    @Override
    public void trace(DungeonOut out) {
        build(new Kit(out, this));
    }

    /** Plan-Modus: keine Bloecke, nur Eintraege. */
    static final class PlanOut implements DungeonOut {
        private final DungeonPlan p;

        PlanOut(DungeonPlan p) {
            this.p = p;
        }

        @Override public void set(int u, int y, int v, BlockState s) { }
        @Override public BlockState get(int u, int y, int v) { return null; }
        @Override public double unit(long salt, int u, int y, int v) { return CityRandom.unit(p.d.seed() ^ salt, u, y, v); }
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

    /** Baukasten: Zuschnitt, Grundformen, Zustaende, Siegeltor und Plan-Eintraege. */
    public static final class Kit {
        public static final BlockState AIR = Blocks.AIR.defaultBlockState();

        public final DungeonOut o;
        public final CustomDungeon design;
        /** Beutetabellen des Planeten ({@code <body>_common}, {@code <body>_boss}). */
        public final String common, bossLoot;

        Kit(DungeonOut o, CustomDungeon design) {
            this.o = o;
            this.design = design;
            this.common = design.stage().body + "_common";
            this.bossLoot = design.stage().body + "_boss";
        }

        // ── Ausschnitt ──

        public int u0() { return o.uMin(); }
        public int u1() { return o.uMax(); }
        public int v0() { return o.vMin(); }
        public int v1() { return o.vMax(); }

        /** Liegt die Spalte im Ausschnitt? */
        public boolean col(int u, int v) {
            return u >= o.uMin() && u <= o.uMax() && v >= o.vMin() && v <= o.vMax();
        }

        /** Beruehrt das Rechteck (beliebige Ecken) den Ausschnitt? Zum Ueberspringen ganzer Bauteile. */
        public boolean touches(int ua, int va, int ub, int vb) {
            return Math.max(ua, ub) >= o.uMin() && Math.min(ua, ub) <= o.uMax() && Math.max(va, vb) >= o.vMin() && Math.min(va, vb) <= o.vMax();
        }

        // ── Bloecke ──

        public void set(int u, int y, int v, BlockState s) {
            if (col(u, v)) o.set(u, y, v, s);
        }

        public void air(int u, int y, int v) {
            set(u, y, v, AIR);
        }

        /** Block im Ausschnitt (null ausserhalb und beim Planen). Nur fuer Bloecke, die dieses Bauwerk selbst gesetzt hat. */
        public BlockState get(int u, int y, int v) {
            return col(u, v) ? o.get(u, y, v) : null;
        }

        public boolean isAir(int u, int y, int v) {
            BlockState s = get(u, y, v);
            return s != null && s.isAir();
        }

        /** Setzt nur, wo bereits Luft ist (z. B. Deko, die nichts ueberschreiben soll). */
        public void setIfAir(int u, int y, int v, BlockState s) {
            if (isAir(u, y, v)) set(u, y, v, s);
        }

        /** Quader (Ecken beliebig). */
        public void fill(int ua, int ya, int va, int ub, int yb, int vb, BlockState s) {
            int uMin = Math.max(Math.min(ua, ub), o.uMin()), uMax = Math.min(Math.max(ua, ub), o.uMax());
            int vMin = Math.max(Math.min(va, vb), o.vMin()), vMax = Math.min(Math.max(va, vb), o.vMax());
            int yMin = Math.min(ya, yb), yMax = Math.max(ya, yb);
            for (int u = uMin; u <= uMax; u++) for (int v = vMin; v <= vMax; v++) for (int y = yMin; y <= yMax; y++) o.set(u, y, v, s);
        }

        /** Luftquader. */
        public void clear(int ua, int ya, int va, int ub, int yb, int vb) {
            fill(ua, ya, va, ub, yb, vb, AIR);
        }

        /** Raum: Huelle {@code wall}, Boden {@code floor} (y0), Decke {@code ceil} (y1), innen Luft. */
        public void room(int ua, int y0, int va, int ub, int y1, int vb, BlockState wall, BlockState floor, BlockState ceil) {
            int uLo = Math.min(ua, ub), uHi = Math.max(ua, ub), vLo = Math.min(va, vb), vHi = Math.max(va, vb);
            if (!touches(uLo, vLo, uHi, vHi)) return;
            for (int u = Math.max(uLo, o.uMin()); u <= Math.min(uHi, o.uMax()); u++) for (int v = Math.max(vLo, o.vMin()); v <= Math.min(vHi, o.vMax()); v++) {
                boolean edge = u == uLo || u == uHi || v == vLo || v == vHi;
                for (int y = y0; y <= y1; y++) o.set(u, y, v, y == y0 ? floor : y == y1 ? ceil : edge ? wall : AIR);
            }
        }

        /** Senkrechter Zylinder (voll) mit Radius r um (cu, cv). */
        public void cylinder(double cu, double cv, int y0, int y1, double r, BlockState s) {
            int ir = (int) Math.ceil(r);
            int uLo = (int) Math.floor(cu - ir), uHi = (int) Math.ceil(cu + ir), vLo = (int) Math.floor(cv - ir), vHi = (int) Math.ceil(cv + ir);
            if (!touches(uLo, vLo, uHi, vHi)) return;
            for (int u = Math.max(uLo, o.uMin()); u <= Math.min(uHi, o.uMax()); u++) for (int v = Math.max(vLo, o.vMin()); v <= Math.min(vHi, o.vMax()); v++) {
                double du = u - cu, dv = v - cv;
                if (du * du + dv * dv > r * r + 0.25) continue;
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) o.set(u, y, v, s);
            }
        }

        /** Rohr/Turm: Ring der Dicke {@code thick} aussen bis Radius r; {@code inside} (z. B. AIR) innen oder null = unberuehrt. */
        public void tube(double cu, double cv, int y0, int y1, double r, double thick, BlockState wall, BlockState inside) {
            int ir = (int) Math.ceil(r);
            int uLo = (int) Math.floor(cu - ir), uHi = (int) Math.ceil(cu + ir), vLo = (int) Math.floor(cv - ir), vHi = (int) Math.ceil(cv + ir);
            if (!touches(uLo, vLo, uHi, vHi)) return;
            for (int u = Math.max(uLo, o.uMin()); u <= Math.min(uHi, o.uMax()); u++) for (int v = Math.max(vLo, o.vMin()); v <= Math.min(vHi, o.vMax()); v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d > r + 0.5) continue;
                BlockState s = d > r - thick + 0.5 ? wall : inside;
                if (s == null) continue;
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) o.set(u, y, v, s);
            }
        }

        /** Ellipsoid (voll), z. B. Hoehlen mit {@code AIR} ausheben. */
        public void ellipsoid(double cu, double cy, double cv, double ru, double ry, double rv, BlockState s) {
            shell(cu, cy, cv, ru, ry, rv, 0, null, s);
        }

        /**
         * Ellipsoid-Schale: Aussenhuelle der Dicke {@code thick} aus {@code wall} (null = unberuehrt), innen {@code inside}
         * (null = unberuehrt). Kuppeln: unten mit {@link #fill} abschneiden oder y-Grenzen ueber {@link #shellCut}.
         */
        public void shell(double cu, double cy, double cv, double ru, double ry, double rv, double thick, BlockState wall, BlockState inside) {
            shellCut(cu, cy, cv, ru, ry, rv, thick, wall, inside, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        /** Wie {@link #shell}, nur zwischen yMin und yMax (Halbkugel, Kuppel, Schuessel). */
        public void shellCut(double cu, double cy, double cv, double ru, double ry, double rv, double thick, BlockState wall, BlockState inside, int yMin, int yMax) {
            int uLo = (int) Math.floor(cu - ru - 1), uHi = (int) Math.ceil(cu + ru + 1), vLo = (int) Math.floor(cv - rv - 1), vHi = (int) Math.ceil(cv + rv + 1);
            if (!touches(uLo, vLo, uHi, vHi)) return;
            int ya = Math.max(yMin, (int) Math.floor(cy - ry - 1)), yb = Math.min(yMax, (int) Math.ceil(cy + ry + 1));
            for (int u = Math.max(uLo, o.uMin()); u <= Math.min(uHi, o.uMax()); u++) for (int v = Math.max(vLo, o.vMin()); v <= Math.min(vHi, o.vMax()); v++) {
                for (int y = ya; y <= yb; y++) {
                    double nu = (u - cu) / ru, ny = (y - cy) / ry, nv = (v - cv) / rv;
                    double d = Math.sqrt(nu * nu + ny * ny + nv * nv);
                    if (d > 1.0) continue;
                    // Abstand zur Huelle in Bloecken (Naeherung ueber den kleinsten Radius)
                    double depth = (1.0 - d) * Math.min(ru, Math.min(ry, rv));
                    BlockState s = depth < thick ? wall : inside;
                    if (s != null) o.set(u, y, v, s);
                }
            }
        }

        /** Dicke Linie (Kugelkette) von A nach B mit Radius r: Bruecken, Rohre, Ketten, Wurzeln, Stuetzen. */
        public void line(double ua, double ya, double va, double ub, double yb, double vb, double r, BlockState s) {
            int uLo = (int) Math.floor(Math.min(ua, ub) - r - 1), uHi = (int) Math.ceil(Math.max(ua, ub) + r + 1);
            int vLo = (int) Math.floor(Math.min(va, vb) - r - 1), vHi = (int) Math.ceil(Math.max(va, vb) + r + 1);
            if (!touches(uLo, vLo, uHi, vHi)) return;
            int yLo = (int) Math.floor(Math.min(ya, yb) - r - 1), yHi = (int) Math.ceil(Math.max(ya, yb) + r + 1);
            double du = ub - ua, dy = yb - ya, dv = vb - va, len2 = du * du + dy * dy + dv * dv;
            for (int u = Math.max(uLo, o.uMin()); u <= Math.min(uHi, o.uMax()); u++) for (int v = Math.max(vLo, o.vMin()); v <= Math.min(vHi, o.vMax()); v++) {
                for (int y = yLo; y <= yHi; y++) {
                    double t = len2 < 1e-9 ? 0 : ((u - ua) * du + (y - ya) * dy + (v - va) * dv) / len2;
                    t = Math.max(0, Math.min(1, t));
                    double pu = ua + du * t - u, py = ya + dy * t - y, pv = va + dv * t - v;
                    if (pu * pu + py * py + pv * pv <= r * r + 0.25) o.set(u, y, v, s);
                }
            }
        }

        /**
         * Wendelrampe um (cu, cv): Laufflaeche (Bloecke {@code s}) zwischen rIn und rOut, steigt von y0 an je
         * {@code blocksPerStep} Bogenlaenge (aussen) um einen Block bis y1; darueber {@code headroom} Bloecke Luft.
         * Startwinkel {@code a0} in Grad (0 = Ost, 90 = Sued), im Uhrzeigersinn von oben.
         */
        public void spiral(double cu, double cv, int y0, int y1, double rIn, double rOut, double blocksPerStep, double a0, int headroom, BlockState s) {
            int ir = (int) Math.ceil(rOut + 1);
            if (!touches((int) cu - ir, (int) cv - ir, (int) cu + ir, (int) cv + ir)) return;
            double perRad = rOut / blocksPerStep;   // Bloecke Hoehe je Radiant
            for (int u = Math.max((int) Math.floor(cu - ir), o.uMin()); u <= Math.min((int) Math.ceil(cu + ir), o.uMax()); u++) {
                for (int v = Math.max((int) Math.floor(cv - ir), o.vMin()); v <= Math.min((int) Math.ceil(cv + ir), o.vMax()); v++) {
                    double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                    if (d < rIn - 0.5 || d > rOut + 0.5) continue;
                    double ang = Math.atan2(v - cv, u - cu) - Math.toRadians(a0);
                    ang = ((ang % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
                    for (double a = ang; ; a += 2 * Math.PI) {
                        int y = y0 + (int) Math.floor(a * perRad);
                        if (y > y1) break;
                        o.set(u, y, v, s);
                        for (int h = 1; h <= headroom; h++) if (y + h <= y1 + headroom) o.set(u, y + h, v, AIR);
                    }
                }
            }
        }

        // ── Zufall ──

        /** Gleichverteilt in [0, 1), fest je Dungeon und Position. */
        public double rnd(long salt, int u, int y, int v) {
            return o.unit(salt, u, y, v);
        }

        /** Glattes Rauschen [0, 1) in der Ebene mit Zellgroesse {@code scale}. */
        public double noise(long salt, int u, int v, int scale) {
            int gu = Math.floorDiv(u, scale), gv = Math.floorDiv(v, scale);
            double fu = (u - gu * scale) / (double) scale, fv = (v - gv * scale) / (double) scale;
            fu = fu * fu * (3 - 2 * fu);
            fv = fv * fv * (3 - 2 * fv);
            double a = rnd(salt, gu, 0, gv), b = rnd(salt, gu + 1, 0, gv), c = rnd(salt, gu, 0, gv + 1), d = rnd(salt, gu + 1, 0, gv + 1);
            return (a + (b - a) * fu) * (1 - fv) + (c + (d - c) * fu) * fv;
        }

        /** Zufaellige Wahl (fest je Position). */
        public BlockState pick(long salt, int u, int y, int v, BlockState... options) {
            return options[Math.min(options.length - 1, (int) (rnd(salt, u, y, v) * options.length))];
        }

        // ── Zustaende ──

        public static BlockState b(Block block) {
            return block.defaultBlockState();
        }

        /** Treppe: {@code up} = Richtung, in die man hinaufsteigt (wie in {@link CityPalette#stairs}); {@code top} = kopfueber. */
        public static BlockState stairs(Block block, Direction up, boolean top) {
            return CityPalette.stairs(block, up, top);
        }

        public static BlockState slab(Block block, boolean top) {
            return CityPalette.slab(b(block), top ? SlabType.TOP : SlabType.BOTTOM);
        }

        /** Horizontal ausgerichtet (Truhe, Ofen, Leiter, Werfer ...). */
        public static BlockState facing(Block block, Direction d) {
            return CityPalette.facing(b(block), d);
        }

        /** Alle sechs Richtungen (Werfer, Endstab, Blitzableiter, Fass). */
        public static BlockState facingAll(Block block, Direction d) {
            return CityPalette.facingAll(b(block), d);
        }

        /** Stamm/Saeule entlang einer Achse. */
        public static BlockState axis(Block block, Direction.Axis a) {
            return CityPalette.log(b(block), a);
        }

        public static BlockState lantern(Block lantern, boolean hanging) {
            return CityPalette.with(b(lantern), BlockStateProperties.HANGING, hanging);
        }

        /** Leiter bei (u, y, v), haengt an dem Block in Richtung {@code wall} (der muss fest sein). */
        public void ladder(int u, int y, int v, Direction wall) {
            set(u, y, v, CityPalette.facing(b(Blocks.LADDER), wall.getOpposite()));
        }

        /** Leitersaeule von y0 bis y1. */
        public void ladder(int u, int y0, int y1, int v, Direction wall) {
            for (int y = y0; y <= y1; y++) ladder(u, y, v, wall);
        }

        // ── Siegeltor ──

        /**
         * Siegeltor mit drei Schloessern. Die Torebene liegt quer zu {@code front}; {@code front} zeigt zur Seite des
         * Spielers (vor dem Tor). Oeffnung {@code 2 * half + 1} breit, {@code h} hoch ueber dem Boden {@code y}
         * (Bodenblock bei y, Oeffnung y+1..y+h); Rahmen aus {@code frame}, Kanten/Zeichen aus {@code accent}.
         * Schloesser: links und rechts im Rahmen (Hoehe y+3), eines auf einem Sockel mittig vor dem Feld (y+1).
         * Vor dem Tor (Seite {@code front}) muss der Spieler stehen koennen; hinter dem Tor geht es zum Boss.
         */
        public void sealGate(int cu, int y, int cv, Direction front, int half, int h, BlockState frame, BlockState accent) {
            Direction side = front.getClockWise();
            int su = side.getStepX(), sv = side.getStepZ(), fu = front.getStepX(), fv = front.getStepZ();
            int ext = half + 3;
            if (!touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
            BlockState field = b(SealBlocks.SEAL_FIELD.get());
            BlockState lock = CityPalette.facing(b(SealBlocks.SEAL_LOCK.get()), front).setValue(SealLockBlock.LIT, false);
            // Rahmen (Torebene und eine Lage dahinter), Oeffnung frei
            for (int t = -(half + 2); t <= half + 2; t++) {
                for (int yy = y; yy <= y + h + 3; yy++) {
                    boolean opening = Math.abs(t) <= half && yy >= y + 1 && yy <= y + h;
                    int u = cu + su * t, v = cv + sv * t;
                    if (opening) {
                        set(u, yy, v, field);
                        air(u + fu, yy, v + fv);
                        air(u - fu, yy, v - fv);
                    } else {
                        boolean edge = Math.abs(t) == half + 1 || yy == y + h + 1;
                        set(u, yy, v, edge ? accent : frame);
                        set(u - fu, yy, v - fv, frame);
                    }
                }
            }
            // Schloesser links/rechts im Rahmen, vorne frei; mittig auf Sockel vor dem Feld
            for (int s = -1; s <= 1; s += 2) {
                int u = cu + su * s * (half + 1), v = cv + sv * s * (half + 1);
                set(u, y + 3, v, lock);
                air(u + fu, y + 3, v + fv);
            }
            set(cu + fu, y + 1, cv + fv, lock);
            set(cu + fu, y, cv + fv, frame);
            // Siegelzeichen ueber dem Tor (Raute)
            for (int t = -2; t <= 2; t++) for (int yy = y + h + 2; yy <= y + h + 6; yy++) {
                int dd = Math.abs(t) + Math.abs(yy - (y + h + 4));
                if (dd <= 2) set(cu + su * t + fu, yy, cv + sv * t + fv, dd == 0 ? b(Blocks.CRYING_OBSIDIAN) : accent);
            }
        }

        // ── Plan-Eintraege (immer aufrufen, nie hinter einem Zuschnitt-Return) ──

        /** Schatzkiste mit Siegelschluessel (Truhe steht bei u, y, v; darunter muss ein fester Block sein). */
        public void keyChest(int u, int y, int v, Direction facing) {
            o.chest(u, y, v, facing, "seal_key");
        }

        /** Beutekiste des Planeten. */
        public void loot(int u, int y, int v, Direction facing) {
            o.chest(u, y, v, facing, common);
        }

        /** Beutefass des Planeten. */
        public void barrel(int u, int y, int v) {
            o.barrel(u, y, v, common);
        }

        /** Schatzkiste des Bosses (Arena). */
        public void bossChest(int u, int y, int v, Direction facing) {
            o.chest(u, y, v, facing, bossLoot);
        }

        /** Monster-Spawner (Block wird automatisch gesetzt). */
        public void spawner(int u, int y, int v, String type) {
            o.spawner(u, y, v, type);
        }

        /** Bewohner (Fuesse bei u, y, v). */
        public void mob(int u, int y, int v, String type) {
            o.mob(u, y, v, type);
        }

        /** Siegelwaechter: verstaerkter Mob mit einem Siegelschluessel (zaehlt als einer der drei Schluessel). */
        public void elite(int u, int y, int v, String type) {
            o.elite(u, y, v, type);
        }

        /** Der Boss (Fuesse bei u, y, v), genau einmal. */
        public void boss(int u, int y, int v) {
            o.mob(u, y, v, design.boss());
        }

        /** Pfeilfalle: setzt einen Werfer mit Blickrichtung {@code d} und fuellt ihn beim Erzeugen mit Pfeilen. */
        public void arrowTrap(int u, int y, int v, Direction d) {
            set(u, y, v, facingAll(Blocks.DISPENSER, d));
            o.dispenser(u, y, v);
        }
    }
}
