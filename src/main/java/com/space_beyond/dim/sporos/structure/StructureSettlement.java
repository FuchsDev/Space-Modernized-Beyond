package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Befestigte Siedlung - sichere Zone (Nutzerwunsch: gepflegtes Staedtchen nach dem Zusammenbruch, rustikal mit etwas
 * heutiger Technik). Grundriss 65 x 65 (u, v -32..32), Tor nach Sueden (+v):
 * <ul>
 *   <li><b>Mauer</b> bei |u| oder |v| = 32: Bruchsteinsockel, Fichtenbohlen mit entrindeten Pfosten, Zinnen aus Zaun,
 *       innen ein umlaufender Wehrgang auf Stuetzen mit Leitern; Ecktuerme (5 x 5, zwei Ebenen, Walmdach,
 *       Scheinwerfer), Torhaus mit Bruecke ueber dem Tor, zwei Tortuerme, Sandsack-Schikane und Wachkisten.</li>
 *   <li><b>Strassenraster</b>: Hauptstrasse Tor -> Nordmauer (|u| &lt;= 2), Querstrasse (|v| &lt;= 1), Gassen bei
 *       |u| = 17 und |v| = 17 (Trampelpfad), Laternen mit Glowstone, Strommasten. 16 Grundstuecke
 *       ({@link #LOTS}) zu 11-13 Bloecken.</li>
 *   <li><b>Platz</b> (|u|, |v| &lt;= 6): Pflaster, Brunnen mit Dach, Marktstaende, Pflanzkuebel, Baenke.</li>
 *   <li><b>Gebaeude</b>: sechs Wohnhaeuser (Fachwerk, 1-2 Geschosse, Veranda, Fensterlaeden, Kamin mit Rauch, Betten,
 *       Kueche), Gemeinschaftshalle (lange Tafeln, Theke, Kamin, Antenne), Depot (Steinbau mit Solardach, Armeekisten),
 *       Wasserturm, Stall mit Koppel, Gewaechshaus, Scheune, drei Felder.</li>
 *   <li><b>Bewohner</b>: Siedler (Dorfbewohner, finden Betten und Werkstationen) und Pferde in der Koppel; keine
 *       Infizierten.</li>
 * </ul>
 * Loot {@code settlement} (Haeuser, Halle, Scheune), Depot zusaetzlich Armeekisten.
 */
final class StructureSettlement {

    private StructureSettlement() {}

    static final int W = 32;
    /** Grundstuecksbereiche in einer Achse: aussen West/Nord, innen, innen, aussen Ost/Sued. */
    static final int[][] RANGES = {{-29, -19}, {-15, -3}, {3, 15}, {19, 29}};

    // Teile: 0 Haus (u0, v0 = Ursprung, a = Drehung, b = Stil, c = Geschosse*100 + Breite*10 + Tiefe)
    static final int HOUSE = 0;

    // ── Planung ─────────────────────────────────────────────────────────────

    static void plan(StructurePlan p) {
        p.values[0] = p.rand.nextInt(3);                        // Dachstil der Halle
        // Wohnhaeuser: Grundstueck (iu, iv), Vorderseite zur naechsten Strasse
        int[][] houses = {{0, 0}, {1, 0}, {2, 0}, {0, 1}, {3, 1}, {3, 2}};
        for (int[] h : houses) {
            int[] ur = clamp(RANGES[h[0]]), vr = clamp(RANGES[h[1]]);
            // Front: inneres Grundstueck -> zur Hauptstrasse/Querstrasse; aeusseres -> zur Gasse
            Direction front = h[1] == 0 ? Direction.SOUTH : h[0] == 0 ? Direction.EAST : h[0] == 3 ? Direction.WEST : Direction.NORTH;
            int storeys = p.rand.nextFloat() < 0.7F ? 2 : 1;
            int w = 9, d = 7;
            int rot = rotFor(front);
            int[] o = place(rot, w, d + 3, ur[0], ur[1], vr[0], vr[1]);
            int style = p.rand.nextInt(3);
            p.part(HOUSE, o[0], o[1], rot, style, storeys * 100 + w * 10 + d);
            // Beute: Fass in der Kueche, Truhe oben (bzw. neben dem Bett)
            int ty = storeys == 2 ? 6 : 2;
            lootAt(p, o, rot, w - 3, 2, 1, Direction.SOUTH, Container.BARREL);
            lootAt(p, o, rot, w - 2, ty, d - 2, Direction.WEST, p.rand.nextFloat() < 0.3F ? Container.SUPPLY_CRATE : Container.CHEST);
            // Siedler vor dem Haus
            if (p.rand.nextFloat() < 0.6F) mobAt(p, o, rot, w / 2 + 2, 2, d, StructureMobs.VILLAGER);
        }
        // Halle (Grundstueck 1/1): Truhen hinter der Theke, Siedler drinnen
        p.loot(-13, 2, -13, Direction.SOUTH, Container.CHEST);
        p.loot(-6, 2, -13, Direction.SOUTH, Container.BARREL);
        p.mob(-9, 2, -8, StructureMobs.VILLAGER);
        p.mob(-8, 2, -9, StructureMobs.VILLAGER);
        // Depot (Grundstueck 2/1): Armee- und Vorratskisten
        for (int i = 0; i < 4; i++) p.loot(5 + i * 2, 1, -13, Direction.SOUTH, i % 2 == 0 ? Container.MILITARY_CRATE : Container.SUPPLY_CRATE);
        p.loot(12, 1, -6, Direction.WEST, Container.MILITARY_CRATE);
        // Scheune (Grundstueck 2/3)
        for (int i = 0; i < 3 + p.rand.nextInt(3); i++) p.loot(5 + i * 2, 1, 27, Direction.NORTH, p.rand.nextFloat() < 0.7F ? Container.SUPPLY_CRATE : Container.BARREL);
        // Wasserturm-Plattform, Ecktuerme, Torwache
        p.loot(26, 10, -23, Direction.WEST, Container.CHEST);
        p.loot(29, 10, 29, Direction.NORTH, Container.MILITARY_CRATE);
        p.loot(-29, 10, -29, Direction.SOUTH, Container.CHEST);
        p.loot(-4, 1, 30, Direction.EAST, Container.MILITARY_CRATE);
        // Gewaechshaus, Stall
        p.loot(13, 1, 13, Direction.WEST, Container.BARREL);
        p.loot(-28, 1, 14, Direction.EAST, Container.BARREL);
        // Siedler auf dem Platz und an den Feldern, Pferde in der Koppel
        p.mob(3, 1, 3, StructureMobs.VILLAGER);
        p.mob(-2, 1, -4, StructureMobs.VILLAGER);
        p.mob(0, 1, 9, StructureMobs.VILLAGER);
        p.mob(-20, 1, 22, StructureMobs.VILLAGER);
        for (int i = 0; i < 3; i++) p.mob(-13 + i * 4, 1, 6 + (i % 2) * 4, StructureMobs.HORSE);
    }

    /** Aeussere Grundstuecke enden vor den Ecktuermen (|u|, |v| &lt;= 27). */
    static int[] clamp(int[] r) {
        return new int[] {Math.max(r[0], -27), Math.min(r[1], 27)};
    }

    static int rotFor(Direction front) {
        return switch (front) {
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
    }

    // Unterkoordinaten eines Gebaeudes: (x, z) lokal, Drehung wie Frame (1 = 90 Grad im Uhrzeigersinn)
    static int su(int ou, int rot, int x, int z) {
        return ou + switch (rot) {
            case 1 -> -z;
            case 2 -> -x;
            case 3 -> z;
            default -> x;
        };
    }

    static int sv(int ov, int rot, int x, int z) {
        return ov + switch (rot) {
            case 1 -> x;
            case 2 -> -z;
            case 3 -> -x;
            default -> z;
        };
    }

    /** Ursprung, damit der gedrehte Kasten (0..w-1, 0..d-1) mittig im Grundstueck liegt. */
    static int[] place(int rot, int w, int d, int u0, int u1, int v0, int v1) {
        int minU = Integer.MAX_VALUE, minV = Integer.MAX_VALUE, maxU = Integer.MIN_VALUE, maxV = Integer.MIN_VALUE;
        for (int x : new int[] {0, w - 1}) for (int z : new int[] {0, d - 1}) {
            minU = Math.min(minU, su(0, rot, x, z));
            maxU = Math.max(maxU, su(0, rot, x, z));
            minV = Math.min(minV, sv(0, rot, x, z));
            maxV = Math.max(maxV, sv(0, rot, x, z));
        }
        int bw = maxU - minU + 1, bd = maxV - minV + 1;
        int tu = u0 + Math.max(0, (u1 - u0 + 1 - bw) / 2), tv = v0 + Math.max(0, (v1 - v0 + 1 - bd) / 2);
        return new int[] {tu - minU, tv - minV};
    }

    private static void lootAt(StructurePlan p, int[] o, int rot, int x, int y, int z, Direction facing, Container c) {
        p.loot(su(o[0], rot, x, z), y, sv(o[1], rot, x, z), Frame.rotation(rot).rotate(facing), c);
    }

    private static void mobAt(StructurePlan p, int[] o, int rot, int x, int y, int z, int kind) {
        p.mob(su(o[0], rot, x, z), y, sv(o[1], rot, x, z), kind);
    }

    /** Zeichenflaeche eines gedrehten Gebaeudes innerhalb des Struktur-Rahmens. */
    static final class Sub {
        final Frame f;
        final int ou, ov, rot;
        final Rotation r;

        Sub(Frame f, int ou, int ov, int rot) {
            this.f = f;
            this.ou = ou;
            this.ov = ov;
            this.rot = rot;
            this.r = Frame.rotation(rot);
        }

        BlockState rs(BlockState s) {
            return rot == 0 ? s : s.rotate(r);
        }

        void set(int x, int y, int z, BlockState s) {
            f.set(su(ou, rot, x, z), y, sv(ov, rot, x, z), rs(s));
        }

        void setIfAir(int x, int y, int z, BlockState s) {
            f.setIfAir(su(ou, rot, x, z), y, sv(ov, rot, x, z), rs(s));
        }

        void setIfOpen(int x, int y, int z, BlockState s) {
            f.setIfOpen(su(ou, rot, x, z), y, sv(ov, rot, x, z), rs(s));
        }

        void air(int x, int y, int z) {
            set(x, y, z, Blocks.AIR.defaultBlockState());
        }

        void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
            for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++)
                    for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) set(x, y, z, s);
        }

        double unit(int x, int y, int z) {
            return f.unit(0x5E77L, su(ou, rot, x, z), y, sv(ov, rot, x, z));
        }
    }

    // ── Malen ───────────────────────────────────────────────────────────────

    static void paint(Frame f, StructurePlan p, CityPalette pal) {
        ground(f, pal);
        streets(f, pal);
        wall(f, pal);
        for (int su : new int[] {-1, 1}) for (int sv : new int[] {-1, 1}) tower(f, su * 30, sv * 30, 11, pal);
        gate(f, pal);
        plaza(f, pal);
        for (StructurePlan.Part part : p.parts) if (part.kind() == HOUSE) house(new Sub(f, part.u(), part.v(), part.a()), part, pal);
        hall(f, p.values[0], pal);
        depot(f, pal);
        waterTower(f, pal);
        stable(f, pal);
        greenhouse(f, pal);
        barn(f, pal);
        field(f, -27, -19, 19, 27, pal);
        field(f, -15, -3, 19, 27, pal);
        field(f, 19, 27, 19, 27, pal);
        lamps(f, pal);
    }

    // ── Boden und Strassen ──────────────────────────────────────────────────

    private static void ground(Frame f, CityPalette pal) {
        int r = SporosStructures.Type.SETTLEMENT.radius;
        for (int u = -r; u <= r; u++) for (int v = -r; v <= r; v++) {
            if (!f.contains(u, v)) continue;
            int d = Math.max(Math.abs(u), Math.abs(v));
            if (d > W && d <= W + 3) {
                // geraeumter Streifen vor der Mauer
                f.set(u, 0, v, f.unit(0x6A1L, u, 0, v) < 0.5 ? pal.coarseDirt : pal.grassBlock);
                continue;
            }
            if (d >= W) continue;
            f.set(u, 0, v, pal.grassBlock);
            double h = f.unit(0x6A2L, u, 1, v);
            if (h < 0.06) f.setIfAir(u, 1, v, pal.shortGrass);
            else if (h < 0.075) f.setIfAir(u, 1, v, h < 0.068 ? pal.poppy : pal.dandelion);
        }
    }

    private static boolean street(int u, int v) {
        return (Math.abs(u) <= 2 && Math.abs(v) < W) || (Math.abs(v) <= 1 && Math.abs(u) < W)
                || (Math.abs(Math.abs(u) - 17) <= 1 && Math.abs(v) < W - 1) || (Math.abs(Math.abs(v) - 17) <= 1 && Math.abs(u) < W - 1)
                || (Math.abs(u) >= 30 && Math.abs(u) < W && Math.abs(v) < W) || (Math.abs(v) >= 30 && Math.abs(v) < W && Math.abs(u) < W);
    }

    private static void streets(Frame f, CityPalette pal) {
        for (int u = -W + 1; u < W; u++) for (int v = -W + 1; v < W; v++) {
            if (!f.contains(u, v) || !street(u, v)) continue;
            boolean main = Math.abs(u) <= 2 || Math.abs(v) <= 1;
            boolean edge = main && (Math.abs(u) == 2 && Math.abs(v) > 1 || Math.abs(v) == 1 && Math.abs(u) > 2);
            double h = f.unit(0x57EL, u, 0, v);
            BlockState top = edge ? (h < 0.3 ? pal.cobble : pal.gravel) : (h < 0.12 ? pal.coarseDirt : pal.dirtPath);
            f.set(u, 0, v, top);
            f.set(u, 1, v, Blocks.AIR.defaultBlockState());
        }
    }

    // ── Mauer, Tuerme, Tor ──────────────────────────────────────────────────

    private static void wall(Frame f, CityPalette pal) {
        BlockState post = pal.strippedSpruceLog;
        BlockState walk = CityPalette.slab(pal.spruceSlab, net.minecraft.world.level.block.state.properties.SlabType.TOP);
        for (int i = -W; i <= W; i++) {
            for (int side = 0; side < 4; side++) {
                int u = side == 0 ? i : side == 1 ? i : side == 2 ? -W : W;
                int v = side == 0 ? -W : side == 1 ? W : i;
                if (!f.contains(u, v)) continue;
                if (Math.abs(i) >= 28) continue;                                   // Ecktuerme
                if (side == 1 && Math.abs(u) <= 6) continue;                       // Torhaus
                boolean isPost = Math.floorMod(i, 4) == 0;
                f.set(u, 0, v, pal.cobble);
                f.set(u, 1, v, f.unit(0x3A1L, u, 1, v) < 0.25 ? pal.cobbleMossy : pal.stoneBricks);
                for (int y = 2; y <= 5; y++) f.set(u, y, v, isPost ? post : pal.sprucePlanks);
                f.set(u, 6, v, isPost ? post : (Math.floorMod(i, 2) == 0 ? pal.spruceFence : Blocks.AIR.defaultBlockState()));
                if (isPost) f.set(u, 7, v, pal.spruceSlab);
                // Wehrgang innen (eine Reihe nach innen), Stuetzen unter den Pfosten
                int wu = side == 2 ? u + 1 : side == 3 ? u - 1 : u;
                int wv = side == 0 ? v + 1 : side == 1 ? v - 1 : v;
                f.set(wu, 4, wv, walk);
                if (isPost) for (int y = 1; y <= 3; y++) f.set(wu, y, wv, pal.spruceFence);
                // Leitern zum Wehrgang alle 16 Bloecke (an der Mauer, Wehrgang dort offen)
                if (Math.floorMod(i, 16) == 6) {
                    Direction toWall = side == 0 ? Direction.NORTH : side == 1 ? Direction.SOUTH : side == 2 ? Direction.WEST : Direction.EAST;
                    for (int y = 1; y <= 5; y++) f.set(wu, y, wv, CityPalette.facing(pal.ladder, toWall.getOpposite()));
                }
                // Ranken aussen, wenig (die Mauer wird gepflegt)
                if (f.unit(0x3A2L, u, 0, v) < 0.06) {
                    int ou = side == 2 ? u - 1 : side == 3 ? u + 1 : u, ov = side == 0 ? v - 1 : side == 1 ? v + 1 : v;
                    Direction wallDir = side == 0 ? Direction.SOUTH : side == 1 ? Direction.NORTH : side == 2 ? Direction.EAST : Direction.WEST;
                    for (int y = 4; y > 4 - 1 - (int) (f.unit(0x3A3L, u, 0, v) * 3); y--) f.setIfAir(ou, y, ov, pal.vine(wallDir));
                }
            }
        }
    }

    /** Turm 5 x 5 um (cu, cv): Steinsockel, Fachwerk, Zwischenboden auf Wehrgang-Hoehe, Plattform, Walmdach, Scheinwerfer. */
    private static void tower(Frame f, int cu, int cv, int h, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int top = h - 2;                                                // Plattform
        for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) {
            int u = cu + du, v = cv + dv;
            boolean corner = Math.abs(du) == 2 && Math.abs(dv) == 2;
            boolean edge = Math.abs(du) == 2 || Math.abs(dv) == 2;
            f.set(u, 0, v, pal.cobble);
            for (int y = 1; y < top; y++) {
                BlockState b;
                if (!edge) b = air;
                else if (corner) b = pal.strippedSpruceLog;
                else if (y <= 2) b = pal.stoneBricks;
                else b = (y == 3 || y == 7) && (du == 0 || dv == 0) ? pal.glassPane : pal.sprucePlanks;
                f.set(u, y, v, b);
            }
            if (!edge) f.set(u, 4, v, pal.sprucePlanks);                    // Zwischenboden (Wehrgang-Hoehe)
            f.set(u, top, v, pal.sprucePlanks);
            if (edge) f.set(u, top + 1, v, corner ? pal.strippedSpruceLog : pal.spruceFence);
            else f.set(u, top + 1, v, air);
            f.set(u, top + 2, v, corner ? pal.strippedSpruceLog : air);
        }
        // Durchgaenge vom Wehrgang (der Wehrgang liegt eine Reihe innerhalb der Mauer, Hoehe 5-6)
        int iu = Integer.signum(cu), iv = Integer.signum(cv);
        for (int y = 5; y <= 6; y++) {
            f.set(cu - iu * 2, y, cv + iv, air);                         // Wehrgang entlang u (Mauer bei v = cv + 2 iv)
            f.set(cu + iu, y, cv - iv * 2, air);                         // Wehrgang entlang v (Mauer bei u = cu + 2 iu)
        }
        // Leiter innen bis zur Plattform (an der Aussenwand, Loch im Zwischenboden und in der Plattform)
        int lu = cu + iu, lv = cv + iv;
        Direction wall = iu > 0 ? Direction.EAST : Direction.WEST;
        for (int y = 1; y <= top; y++) f.set(lu, y, lv, CityPalette.facing(pal.ladder, wall.getOpposite()));
        // Walmdach
        for (int ring = 0; ring <= 2; ring++) {
            int y = top + 3 + ring;
            int r = 3 - ring;
            for (int du = -r; du <= r; du++) for (int dv = -r; dv <= r; dv++) {
                if (Math.max(Math.abs(du), Math.abs(dv)) != r) continue;
                Direction up = Math.abs(du) >= Math.abs(dv) ? (du < 0 ? Direction.EAST : Direction.WEST) : (dv < 0 ? Direction.SOUTH : Direction.NORTH);
                f.set(cu + du, y, cv + dv, pal.roofStairs(1, up, false));
            }
        }
        f.set(cu, top + 6, cv, pal.roofSlab(1));
        f.set(cu, top + 5, cv, pal.darkOakPlanks);
        f.set(cu, top + 4, cv, pal.darkOakPlanks);
        f.set(cu, top + 3, cv, pal.glowstone);                            // Licht unter dem Dach
        // Scheinwerfer nach aussen: Glowstone mit Klappe
        f.set(cu + iu * 2, top + 2, cv + iv * 2, pal.glowstone);
    }

    private static void gate(Frame f, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        // Tortuerme (3 x 3) bei u = +-5
        for (int s = -1; s <= 1; s += 2) {
            int cu = s * 5;
            for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
                int u = cu + du, v = W + dv;
                boolean corner = Math.abs(du) == 1 && Math.abs(dv) == 1;
                f.set(u, 0, v, pal.cobble);
                for (int y = 1; y <= 7; y++) f.set(u, y, v, corner ? pal.strippedSpruceLog : (y <= 2 ? pal.stoneBricks : pal.sprucePlanks));
                f.set(u, 8, v, pal.sprucePlanks);
                f.set(u, 9, v, corner ? pal.strippedSpruceLog : pal.spruceFence);
                f.set(u, 10, v, corner ? pal.strippedSpruceLog : air);
                f.set(u, 11, v, pal.roofSlab(1));
            }
            f.set(cu, 9, W, air);
            f.set(cu, 11, W, pal.glowstone);
            for (int y = 5; y <= 7; y++) f.set(cu, y, W, air);
            f.set(cu, 4, W, pal.sprucePlanks);
            // Leiter zur Plattform an der Aussenwand
            for (int y = 5; y <= 8; y++) f.set(cu, y, W, CityPalette.facing(pal.ladder, Direction.NORTH));
            for (int y = 5; y <= 6; y++) {
                f.set(cu - s, y, W, air);                                // zur Torbruecke
                f.set(cu, y, W - 1, air);                                // zum Wehrgang (eine Reihe innen)
                f.set(cu + s, y, W - 1, air);
            }
        }
        // Torbruecke ueber dem Tor (u -4..4, Hoehe 5), Bruestung aussen, Fallgitter aus Zaun
        for (int u = -4; u <= 4; u++) {
            f.set(u, 4, W, pal.sprucePlanks);
            f.set(u, 5, W + 1, pal.spruceFence);
            f.set(u, 4, W + 1, pal.sprucePlanks);
            if (Math.abs(u) <= 2) f.set(u, 3, W, pal.spruceFence);
            for (int y = 1; y <= 2; y++) f.set(u, y, W, air);
            f.set(u, 0, W, pal.gravel);
            f.set(u, 0, W + 1, pal.gravel);
        }
        f.set(-3, 3, W, pal.sprucePlanks);
        f.set(3, 3, W, pal.sprucePlanks);
        for (int u : new int[] {-3, 3}) for (int y = 1; y <= 2; y++) f.set(u, y, W, pal.strippedSpruceLog);
        // Schild ueber dem Tor: Bohlenbrett mit weissem Rand
        for (int u = -2; u <= 2; u++) f.set(u, 6, W + 1, Math.abs(u) == 2 ? pal.strippedDarkOakLog : pal.darkOakPlanks);
        // Zufahrt: Kies, Sandsack-Schikane, Lampen
        for (int v = W + 2; v <= W + 4; v++) for (int u = -2; u <= 2; u++) f.set(u, 0, v, pal.gravel);
        for (int u = -6; u <= -3; u++) f.setIfOpen(u, 1, W + 3, pal.sandbag);
        for (int u = 3; u <= 6; u++) f.setIfOpen(u, 1, W + 3, pal.sandbag);
        f.setIfOpen(-6, 2, W + 3, pal.sandbag);
        f.setIfOpen(6, 2, W + 3, pal.sandbag);
        f.set(-3, 2, W + 1, CityPalette.facing(pal.wallTorch, Direction.SOUTH));
        f.set(3, 2, W + 1, CityPalette.facing(pal.wallTorch, Direction.SOUTH));
        // Wachposten innen: Kiste, Stuhl, Feuerschale
        f.set(-4, 1, W - 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        f.set(4, 1, W - 3, pal.campfire);
    }

    // ── Platz ───────────────────────────────────────────────────────────────

    private static void plaza(Frame f, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int u = -6; u <= 6; u++) for (int v = -6; v <= 6; v++) {
            boolean ring = Math.max(Math.abs(u), Math.abs(v)) == 6;
            double h = f.unit(0x9A2L, u, 0, v);
            BlockState pave = ring ? pal.stoneBricks : (u + v) % 2 == 0 ? pal.polishedAndesite : pal.stoneBricks;
            if (!ring && h < 0.08) pave = pal.cobbleMossy;
            f.set(u, 0, v, pave);
            f.set(u, 1, v, air);
        }
        // Brunnen mit Dach (3 x 3)
        for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
            boolean rim = du != 0 || dv != 0;
            f.set(du, 1, dv, rim ? pal.stoneBricks : pal.water);
            if (!rim) for (int y = -4; y <= 0; y++) f.set(0, y, 0, pal.water);
        }
        f.set(0, -5, 0, pal.stoneBricks);
        for (int du = -1; du <= 1; du += 2) for (int dv = -1; dv <= 1; dv += 2) f.fill(du, 2, dv, du, 3, dv, pal.spruceFence);
        for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) {
            int d = Math.max(Math.abs(du), Math.abs(dv));
            if (d == 2) f.set(du, 4, dv, CityPalette.stairs(Blocks.SPRUCE_STAIRS,
                    Math.abs(du) >= Math.abs(dv) ? (du < 0 ? Direction.EAST : Direction.WEST) : (dv < 0 ? Direction.SOUTH : Direction.NORTH), false));
            else f.set(du, 4, dv, pal.sprucePlanks);
        }
        f.set(0, 5, 0, pal.spruceSlab);
        f.set(0, 3, 0, pal.spruceFence);
        // Pflanzkuebel in zwei Ecken (Hyphe-Busch); in den anderen stehen Marktstand und schwarzes Brett
        for (int[] c : new int[][] {{5, -5}, {-5, 5}}) {
            int u = c[0], v = c[1];
            f.set(u, 1, v, pal.grassBlock);
            f.set(u, 2, v, pal.hypheLeaves);
            for (Direction d : Direction.Plane.HORIZONTAL) f.setIfAir(u + d.getStepX(), 1, v + d.getStepZ(), pal.stoneBrickSlab);
        }
        // Baenke
        for (int s = -1; s <= 1; s += 2) {
            f.set(s * 3, 1, -4, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
            f.set(s * 3, 1, 4, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
            f.set(-4, 1, s * 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            f.set(4, 1, s * 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        }
        // Marktstand an der Suedseite des Platzes (Plane, Fass, Ernte) und schwarzes Brett an der Nordseite
        for (int du = -1; du <= 1; du += 2) for (int dv = 4; dv <= 5; dv++) if (dv == 4 || du == 1) f.fill(du + 4, 1, dv, du + 4, 2, dv, pal.spruceFence);
        for (int du = -1; du <= 1; du++) for (int dv = 3; dv <= 5; dv++) f.set(4 + du, 3, dv, pal.tentCloth[1]);
        f.set(4, 1, 5, pal.barrel);
        f.set(4, 1, 4, pal.pumpkin);
        f.fill(-5, 1, -4, -5, 2, -4, pal.darkOakFence);
        f.fill(-3, 1, -4, -3, 2, -4, pal.darkOakFence);
        f.fill(-5, 2, -4, -3, 3, -4, pal.darkOakPlanks);
        f.set(-4, 4, -4, pal.darkOakSlab);
    }

    // ── Wohnhaus ────────────────────────────────────────────────────────────

    /**
     * Fachwerkhaus (lokal x 0..w-1, z 0..d-1, Front bei z = d-1 nach Sueden, Veranda z d..d+1, Stufe z d+2):
     * Sockel, Boden y1, Erdgeschoss y2-4, Zwischendecke y5, Obergeschoss y6-8, Satteldach ab y9 (eingeschossig ab y5).
     * Stil 0 Fichte/Birke, 1 dunkle Eiche/Fichte, 2 Eiche/Terrakotta.
     */
    private static void house(Sub s, StructurePlan.Part part, CityPalette pal) {
        int style = part.b(), storeys = part.c() / 100, w = (part.c() / 10) % 10, d = part.c() % 10;
        BlockState lower = style == 1 ? pal.darkOakPlanks : style == 2 ? pal.oakPlanks : pal.sprucePlanks;
        BlockState upper = style == 1 ? pal.sprucePlanks : style == 2 ? pal.terracottaWhite : pal.birchPlanks;
        BlockState frame = style == 1 ? pal.strippedSpruceLog : pal.strippedDarkOakLog;
        int roofStyle = style == 0 ? 0 : style == 1 ? 1 : 0;
        int yE = storeys == 2 ? 9 : 5;
        int mid = w / 2;
        BlockState air = Blocks.AIR.defaultBlockState();
        // Sockel und Boden
        s.fill(0, 0, 0, w - 1, 0, d - 1, pal.stoneBricks);
        s.fill(0, 1, 0, w - 1, 1, d - 1, pal.cobble);
        s.fill(1, 1, 1, w - 2, 1, d - 2, pal.sprucePlanks);
        // Waende
        for (int x = 0; x < w; x++) for (int z = 0; z < d; z++) {
            boolean edge = x == 0 || x == w - 1 || z == 0 || z == d - 1;
            boolean corner = (x == 0 || x == w - 1) && (z == 0 || z == d - 1);
            boolean postCol = corner || (edge && (x == mid) && (z == 0)) ;
            for (int y = 2; y < yE; y++) {
                if (!edge) {
                    s.set(x, y, z, storeys == 2 && y == 5 ? pal.sprucePlanks : air);
                    continue;
                }
                BlockState b;
                if (corner || postCol) b = frame;
                else if (storeys == 2 && y == 5) b = CityPalette.log(frame, x == 0 || x == w - 1 ? Direction.Axis.Z : Direction.Axis.X);
                else b = y < 5 ? lower : upper;
                s.set(x, y, z, b);
            }
        }
        // Fenster (2 breit, 2 hoch im EG, 1 hoch im OG) mit Fensterlaeden
        int[] wx = {1, w - 3};
        for (int x = 3; x <= 4; x++) {                                  // Rueckseite: Fenster ueber der Kueche
            s.set(x, 3, 0, pal.glassPane);
            if (storeys == 2) s.set(x, 7, 0, pal.glassPane);
        }
        for (int x0 : wx) {
            for (int x = x0; x <= x0 + 1; x++) {
                for (int y = 3; y <= 4; y++) s.set(x, y, d - 1, pal.glassPane);
                if (storeys == 2) s.set(x, 7, d - 1, pal.glassPane);
            }
            for (int y = 3; y <= 4; y++) {
                s.setIfAir(x0 - 1, y, d, pal.shutter(Direction.SOUTH));
                s.setIfAir(x0 + 2, y, d, pal.shutter(Direction.SOUTH));
            }
        }
        for (int z = 2; z <= d - 3; z++) {
            s.set(0, 3, z, pal.glassPane);
            s.set(w - 1, 3, z, pal.glassPane);
            if (storeys == 2) {
                s.set(0, 7, z, pal.glassPane);
                s.set(w - 1, 7, z, pal.glassPane);
            }
        }
        // Tuer
        s.set(mid, 2, d - 1, pal.door(style == 1, Direction.NORTH, false, false));
        s.set(mid, 3, d - 1, pal.door(style == 1, Direction.NORTH, true, false));
        s.set(mid, 4, d - 1, frame);
        // Veranda: Boden, Pfosten, Gelaender, Dach, Stufe
        for (int x = 0; x < w; x++) {
            s.set(x, 0, d, pal.cobble);
            s.set(x, 0, d + 1, pal.cobble);
            s.set(x, 1, d, pal.sprucePlanks);
            s.set(x, 1, d + 1, pal.sprucePlanks);
            boolean postX = x == 0 || x == w - 1 || x == mid - 2 || x == mid + 2;
            if (postX) for (int y = 2; y <= 4; y++) s.set(x, y, d + 1, pal.spruceFence);
            else if (x != mid) s.set(x, 2, d + 1, pal.spruceFence);
            else s.set(x, 2, d + 1, air);
            s.set(x, 5, d, pal.roofSlab(roofStyle));
            s.set(x, 5, d + 1, pal.roofSlab(roofStyle));
        }
        s.set(mid, 1, d + 2, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        s.set(mid, 0, d + 2, pal.cobble);
        s.set(1, 2, d, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));     // Bank
        s.set(2, 2, d, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        s.set(w - 2, 2, d, pal.poppyPot);
        // Solarpaneele auf dem Verandadach (Tageslichtsensoren)
        if (part.b() != 2) for (int x = 1; x < w - 1; x += 2) s.set(x, 5, d + 1, pal.daylightDetector);
        // Dach
        gable(s, 0, w - 1, 0, d - 1, yE, roofStyle, upper, pal);
        if (storeys == 2) for (int y = yE + 1; y <= yE + 2; y++) {
            s.set(0, y, d / 2, pal.glassPane);
            s.set(w - 1, y, d / 2, pal.glassPane);
        }
        // Kamin hinten rechts mit Feuerstelle und Rauch
        int top = yE + (d + 1) / 2 + 2;
        s.fill(w - 2, 2, 0, w - 2, top, 0, pal.bricks);
        s.set(w - 2, 2, 1, pal.campfire);
        s.set(w - 2, 3, 1, pal.bricks);
        s.set(w - 2, top + 1, 0, pal.campfireLit);
        // Einrichtung Erdgeschoss: Kueche hinten, Tisch, Stuehle, Regal, Teppich, Licht
        s.set(w - 3, 2, 1, pal.barrel);
        s.set(w - 4, 2, 1, pal.smoker);
        s.set(w - 5, 2, 1, pal.craftingTable);
        s.set(mid, 2, 3, pal.spruceFence);
        s.set(mid, 3, 3, pal.sprucePressurePlate);
        s.set(mid - 1, 2, 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        s.set(mid + 1, 2, 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        s.set(1, 2, d - 2, pal.bookshelf);
        s.setIfAir(mid, 2, d - 3, pal.carpetRed);
        s.setIfAir(mid - 1, 2, d - 3, pal.carpetRed);
        s.set(mid, storeys == 2 ? 5 : yE, 2, pal.glowstone);
        if (storeys == 2) {
            // Leiter ins Obergeschoss (hinten links), Loch in der Decke
            for (int y = 2; y <= 5; y++) s.set(1, y, 1, CityPalette.facing(pal.ladder, Direction.SOUTH));
            s.set(2, 2, 1, pal.flowerPot);
            // Obergeschoss: zwei Betten, Teppich, Licht
            s.set(w - 2, 6, 2, pal.bed(style * 2, Direction.NORTH, false));
            s.set(w - 2, 6, 1, pal.bed(style * 2, Direction.NORTH, true));
            s.set(3, 6, 2, pal.bed(style * 2 + 1, Direction.NORTH, false));
            s.set(3, 6, 1, pal.bed(style * 2 + 1, Direction.NORTH, true));
            s.setIfAir(mid, 6, 3, pal.carpetBrown);
            s.setIfAir(mid, 6, 4, pal.carpetBrown);
            s.set(mid, 8, 1, CityPalette.facing(pal.wallTorch, Direction.SOUTH));
        } else {
            s.set(1, 2, 2, pal.bed(style * 2, Direction.NORTH, false));
            s.set(1, 2, 1, pal.bed(style * 2, Direction.NORTH, true));
        }
        // Vorgarten: Beet neben der Veranda
        for (int x = 0; x < w; x += w - 1) {
            int fx = x == 0 ? -1 : w;
            for (int z = 1; z < d; z++) {
                if (s.unit(fx, 0, z) < 0.5) s.setIfAir(fx, 1, z, z % 2 == 0 ? pal.fern : pal.shortGrass);
            }
        }
    }

    /** Satteldach (First entlang x) ueber (x0..x1, z0..z1) mit Ueberstand 1, Traufe bei {@code yE}, Giebel aus {@code gableFill}. */
    private static void gable(Sub s, int x0, int x1, int z0, int z1, int yE, int style, BlockState gableFill, CityPalette pal) {
        int zs = z0 - 1, ze = z1 + 1, n = ze - zs + 1;
        for (int i = 0; i < n; i++) {
            int z = zs + i;
            int dist = Math.min(i, n - 1 - i);
            int y = yE + dist;
            boolean ridge = n % 2 == 1 && i == n / 2;
            for (int x = x0 - 1; x <= x1 + 1; x++) {
                if (ridge) {
                    s.set(x, y, z, pal.roofSlab(style));
                    s.set(x, y - 1, z, gableFill);
                } else {
                    s.set(x, y, z, pal.roofStairs(style, i < n / 2 ? Direction.SOUTH : Direction.NORTH, false));
                }
                if ((x == x0 || x == x1) && z >= z0 && z <= z1) for (int g = yE; g < y; g++) s.set(x, g, z, gableFill);
            }
            // Dachraum frei
            if (z > z0 && z < z1) for (int x = x0 + 1; x < x1; x++) for (int g = yE; g < y - (ridge ? 1 : 0); g++) s.set(x, g, z, Blocks.AIR.defaultBlockState());
        }
    }

    // ── Gemeinschaftshalle (Grundstueck 1/1: u -15..-3, v -15..-3), Front nach Sueden ──

    private static void hall(Frame f, int roofStyle, CityPalette pal) {
        Sub s = new Sub(f, -14, -15, 0);                     // lokal x 0..11, z 0..9
        int w = 12, d = 10, yE = 7;
        BlockState air = Blocks.AIR.defaultBlockState();
        s.fill(0, 0, 0, w - 1, 0, d, pal.stoneBricks);
        s.fill(0, 1, 0, w - 1, 1, d - 1, pal.stoneBricks);
        s.fill(1, 1, 1, w - 2, 1, d - 2, pal.darkOakPlanks);
        for (int x = 0; x < w; x++) for (int z = 0; z < d; z++) {
            boolean edge = x == 0 || x == w - 1 || z == 0 || z == d - 1;
            boolean post = edge && (x % 3 == 0 || x == w - 1) && (z == 0 || z == d - 1) || edge && (z % 3 == 0 || z == d - 1) && (x == 0 || x == w - 1);
            for (int y = 2; y < yE; y++) {
                if (!edge) {
                    s.set(x, y, z, air);
                    continue;
                }
                BlockState b = post ? pal.strippedDarkOakLog : y == 2 ? pal.stoneBricks : pal.sprucePlanks;
                boolean window = !post && y >= 3 && y <= 5 && !(z == d - 1 && (x == 5 || x == 6));
                s.set(x, y, z, window ? pal.glassPane : b);
            }
        }
        // Doppeltuer vorn
        s.set(5, 2, d - 1, pal.door(true, Direction.NORTH, false, false));
        s.set(5, 3, d - 1, pal.door(true, Direction.NORTH, true, false));
        s.set(6, 2, d - 1, pal.door(true, Direction.NORTH, false, true));
        s.set(6, 3, d - 1, pal.door(true, Direction.NORTH, true, true));
        s.fill(5, 4, d - 1, 6, 6, d - 1, pal.sprucePlanks);
        // Vordach und Stufen
        for (int x = 3; x <= 8; x++) {
            s.set(x, 6, d, pal.roofSlab(roofStyle));
            s.set(x, 1, d, CityPalette.stairs(Blocks.STONE_BRICK_STAIRS, Direction.NORTH, false));
        }
        s.fill(3, 2, d, 3, 5, d, pal.spruceFence);
        s.fill(8, 2, d, 8, 5, d, pal.spruceFence);
        s.set(3, 2, d, pal.spruceFence);
        gable(s, 0, w - 1, 0, d - 1, yE, roofStyle, pal.sprucePlanks, pal);
        // Innen: zwei lange Tafeln, Baenke, Theke hinten, Kamin links, Kronleuchter
        for (int t : new int[] {3, 8}) {
            for (int z = 3; z <= 6; z++) {
                s.set(t, 2, z, pal.spruceFence);
                s.set(t, 3, z, pal.sprucePressurePlate);
                s.set(t - 1, 2, z, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
                s.set(t + 1, 2, z, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            }
        }
        for (int x = 2; x <= 9; x++) s.set(x, 2, 1, x % 3 == 0 ? pal.smoker : pal.barrel);   // Theke/Kueche
        s.set(1, 2, 1, pal.loom);
        s.set(w - 2, 2, 1, pal.fletchingTable);
        s.fill(0, 2, 4, 0, yE + 6, 5, pal.bricks);                                         // Kamin an der Westwand
        s.set(1, 2, 4, pal.bricks);
        s.set(1, 2, 5, pal.campfireLit);
        s.set(1, 3, 5, pal.bricks);
        s.set(0, yE + 7, 4, pal.campfireLit);
        s.set(w - 2, 2, d - 2, pal.bookshelf);
        s.set(w - 2, 3, d - 2, pal.bookshelf);
        for (int x : new int[] {3, 8}) {
            for (int y = yE + 2; y <= yE + 4; y++) s.set(x, y, 4, pal.spruceFence);   // Kronleuchter am Dachbalken
            s.set(x, yE + 1, 4, pal.glowstone);
        }
        // Funkantenne auf dem First, Satellitenschuessel
        s.fill(w - 2, yE + 6, 4, w - 2, yE + 10, 4, pal.andesiteWall);
        s.set(w - 2, yE + 11, 4, pal.glassRed);
    }

    // ── Depot (Grundstueck 2/1: u 3..15, v -15..-3), Steinbau mit Solardach ──

    private static void depot(Frame f, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int u0 = 4, u1 = 14, v0 = -14, v1 = -5;
        f.fill(u0, 0, v0, u1, 0, v1, pal.concrete);
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            for (int y = 1; y <= 4; y++) {
                if (!edge) { f.set(u, y, v, air); continue; }
                boolean window = y == 3 && (u - u0) % 3 == 2 && (v == v0 || v == v1);
                f.set(u, y, v, window ? pal.glassPane : (y == 1 ? pal.concrete : pal.stoneBricks));
            }
            f.set(u, 5, v, pal.concreteSmooth);
            if (edge) f.set(u, 6, v, pal.stoneBrickSlab);
        }
        // Rolltor zur Querstrasse (Sueden), Werkbank, Waffenregal
        for (int u = 8; u <= 10; u++) for (int y = 1; y <= 3; y++) f.set(u, y, v1, air);
        f.fill(8, 4, v1, 10, 4, v1, pal.vConcreteGray);
        f.set(13, 1, -10, pal.craftingTable);
        f.set(13, 1, -9, pal.fletchingTable);
        f.set(5, 1, -6, pal.sandbag);
        f.set(5, 1, -7, pal.sandbag);
        f.set(9, 4, -10, pal.glowstone);
        // Solarfeld auf dem Dach (Tageslichtsensoren auf Gestell), Batteriebank daneben
        for (int u = u0 + 1; u <= u1 - 1; u++) for (int v = v0 + 1; v <= v1 - 1; v++) {
            if ((v - v0) % 3 != 0) f.set(u, 6, v, pal.daylightDetector);
        }
        f.set(u1 + 1, 1, v0 + 1, pal.polishedBlackstone);
        f.set(u1 + 1, 2, v0 + 1, pal.glassLime);
        f.set(u1 + 1, 1, v0 + 2, pal.polishedBlackstone);
    }

    // ── Wasserturm (Grundstueck 3/0: u 19..29, v -29..-19) ──

    private static void waterTower(Frame f, CityPalette pal) {
        int cu = 23, cv = -23;
        for (int du : new int[] {-2, 2}) for (int dv : new int[] {-2, 2}) {
            f.set(cu + du, 0, cv + dv, pal.cobble);
            f.fill(cu + du, 1, cv + dv, cu + du, 8, cv + dv, pal.strippedSpruceLog);
            f.set(cu + du / 2, 4, cv + dv, pal.spruceFence);                    // Querstreben
            f.set(cu + du, 4, cv + dv / 2, pal.spruceFence);
        }
        for (int du = -4; du <= 4; du++) for (int dv = -4; dv <= 4; dv++) {
            boolean rim = Math.max(Math.abs(du), Math.abs(dv)) == 4;
            f.set(cu + du, 9, cv + dv, pal.sprucePlanks);
            if (rim && !(du == -2 && dv == 4)) f.set(cu + du, 10, cv + dv, pal.spruceFence);
        }
        // Tank: Ring aus Bohlen mit Reifen, Wasser innen, Kegeldach
        for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) {
            boolean corner = Math.abs(du) == 2 && Math.abs(dv) == 2;
            if (corner) continue;
            boolean rim = Math.abs(du) == 2 || Math.abs(dv) == 2;
            for (int y = 10; y <= 14; y++) {
                if (rim) f.set(cu + du, y, cv + dv, y == 11 || y == 14 ? pal.strippedDarkOakLog : pal.sprucePlanks);
                else f.set(cu + du, y, cv + dv, y <= 13 ? pal.water : Blocks.AIR.defaultBlockState());
            }
            f.set(cu + du, 15, cv + dv, rim ? pal.roofSlab(2) : pal.sprucePlanks);
        }
        f.set(cu, 16, cv, pal.roofSlab(2));
        // Plattformzugang: Leiter am Suedwest-Bein (haengt am Bein, endet im Loch der Plattform)
        for (int y = 1; y <= 9; y++) f.set(cu - 2, y, cv + 3, CityPalette.facing(pal.ladder, Direction.SOUTH));
        f.set(cu - 2, 0, cv + 3, pal.cobble);
        // Pumpenhaus
        f.fill(26, 0, -21, 28, 0, -19, pal.cobble);
        f.fill(26, 1, -21, 28, 2, -19, pal.sprucePlanks);
        f.set(27, 1, -19, Blocks.AIR.defaultBlockState());
        f.set(27, 2, -19, Blocks.AIR.defaultBlockState());
        f.set(27, 1, -20, pal.polishedBlackstone);
        f.fill(26, 3, -21, 28, 3, -19, pal.roofSlab(2));
    }

    // ── Stall mit Koppel (Grundstuecke 0/2 und 1/2) ──

    private static void stable(Frame f, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int u0 = -29, u1 = -24, v0 = 3, v1 = 15;
        f.fill(u0, 0, v0, u1, 0, v1, pal.coarseDirt);
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean back = u == u0, end = v == v0 || v == v1;
            for (int y = 1; y <= 3; y++) {
                if (back || end) f.set(u, y, v, (v - v0) % 4 == 0 || u == u1 ? pal.strippedSpruceLog : pal.sprucePlanks);
                else if (u == u1) f.set(u, y, v, (v - v0) % 4 == 0 ? pal.strippedSpruceLog : air);
                else f.set(u, y, v, air);
            }
            // Pultdach nach Osten abfallend
            f.set(u, 4, v, pal.roofStairs(2, Direction.WEST, false));
            if (u == u0) f.set(u, 4, v, pal.sprucePlanks);
        }
        for (int v = v0 + 4; v < v1; v += 4) for (int u = u0 + 1; u < u1; u++) f.set(u, 1, v, pal.spruceFence);   // Boxen
        for (int v = v0 + 1; v < v1; v++) {
            if ((v - v0) % 4 == 2) f.set(u0 + 1, 1, v, pal.hayBale);
            if ((v - v0) % 4 == 1) f.set(u0 + 1, 1, v, pal.water);
        }
        f.set(u0 + 1, 2, v0 + 2, pal.hayBale);
        f.set(u0 + 2, 3, v0 + 6, pal.glowstone);
        // Koppel u -22..-4, v 3..15
        for (int u = -22; u <= -4; u++) for (int v = 3; v <= 15; v++) {
            boolean edge = u == -22 || u == -4 || v == 3 || v == 15;
            f.set(u, 0, v, f.unit(0xB0L, u, 0, v) < 0.3 ? pal.coarseDirt : pal.grassBlock);
            f.set(u, 1, v, edge && !(u == -4 && (v == 9 || v == 10)) && !(u == -22 && v >= 5 && v <= 13) ? pal.spruceFence : air);
        }
        f.set(-4, 1, 9, CityPalette.facing(pal.spruceFenceGate, Direction.EAST));
        f.set(-12, 1, 14, pal.hayBale);
        f.set(-11, 1, 14, pal.hayBale);
        f.set(-12, 2, 14, pal.hayBale);
        for (int u = -18; u <= -16; u++) {
            f.set(u, 0, 4, pal.stoneBricks);
            f.set(u, 1, 4, pal.water);
            f.set(u, 1, 5, pal.stoneBrickSlab);
        }
    }

    // ── Gewaechshaus (Grundstueck 2/2: u 3..15, v 3..15) ──

    private static void greenhouse(Frame f, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int u0 = 4, u1 = 14, v0 = 4, v1 = 14;
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            boolean frame = (u == u0 || u == u1) && (v == v0 || v == v1) || edge && ((u - u0) % 5 == 0 && (v == v0 || v == v1) || (v - v0) % 5 == 0 && (u == u0 || u == u1));
            f.set(u, 0, v, edge ? pal.stoneBricks : (u == 9 ? pal.gravel : (v % 3 == 0 ? pal.water : pal.farmland)));
            for (int y = 1; y <= 4; y++) {
                if (edge) f.set(u, y, v, frame ? pal.strippedSpruceLog : (y == 1 ? pal.stoneBricks : pal.glass));
                else if (y == 1 && u != 9 && v % 3 != 0) {
                    double h = f.unit(0xC0L, u, 1, v);
                    f.set(u, 1, v, h < 0.33 ? pal.carrots : h < 0.66 ? pal.beetroots : pal.potatoes);
                } else f.set(u, y, v, air);
            }
            // Glasdach mit First entlang u
            int dz = Math.abs(v - 9);
            f.set(u, 5 + (5 - dz) / 2, v, pal.glass);
            for (int y = 5; y < 5 + (5 - dz) / 2; y++) f.set(u, y, v, edge && (u == u0 || u == u1) ? pal.glass : air);
        }
        f.set(9, 1, v0, air);
        f.set(9, 2, v0, air);
        f.set(9, 1, v1, air);
        f.set(9, 2, v1, air);
        f.set(5, 1, 13, pal.composter);
        f.set(9, 6, 9, pal.glowstone);
    }

    // ── Scheune (Grundstueck 2/3: u 3..15, v 19..29) ──

    private static void barn(Frame f, CityPalette pal) {
        Sub s = new Sub(f, 4, 19, 0);                           // lokal x 0..10, z 0..9, Tor nach Norden (z = 0)
        int w = 11, d = 10, yE = 6;
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState red = pal.terracottaRed;
        s.fill(0, 0, 0, w - 1, 0, d - 1, pal.sprucePlanks);
        for (int x = 0; x < w; x++) for (int z = 0; z < d; z++) {
            boolean edge = x == 0 || x == w - 1 || z == 0 || z == d - 1;
            boolean post = edge && ((x == 0 || x == w - 1) && (z == 0 || z == d - 1 || z % 3 == 0) || (z == 0 || z == d - 1) && x % 5 == 0);
            for (int y = 1; y < yE; y++) s.set(x, y, z, !edge ? air : post ? pal.strippedDarkOakLog : red);
        }
        for (int x = 4; x <= 6; x++) for (int y = 1; y <= 3; y++) s.set(x, y, 0, air);        // Tor
        for (int x = 3; x <= 7; x += 4) for (int y = 1; y <= 3; y++) s.set(x, y, 0, pal.strippedDarkOakLog);
        s.fill(3, 4, 0, 7, 4, 0, pal.strippedDarkOakLog);
        gable(s, 0, w - 1, 0, d - 1, yE, 1, red, pal);
        // Heuboden
        s.fill(1, 4, 5, w - 2, 4, d - 2, pal.sprucePlanks);
        s.fill(1, 5, 6, 4, 5, d - 2, pal.hayBale);
        for (int y = 1; y <= 4; y++) s.set(w - 2, y, 6, CityPalette.facing(pal.ladder, Direction.WEST));   // an der Ostwand
        s.set(1, 1, 1, pal.composter);
        s.set(2, 1, 1, pal.hayBale);
        s.set(5, 4, 3, pal.glowstone);
    }

    // ── Felder ──

    private static void field(Frame f, int u0, int u1, int v0, int v1, CityPalette pal) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            if (edge) {
                f.set(u, 0, v, pal.coarseDirt);
                boolean gap = v == v0 && Math.abs(u - (u0 + u1) / 2) <= 1;
                f.set(u, 1, v, gap ? air : pal.spruceFence);
                continue;
            }
            if ((u - u0) % 5 == 0) {                               // Bewaesserungsgraben
                f.set(u, 0, v, pal.water);
                f.set(u, -1, v, pal.dirt);
                f.set(u, 1, v, air);
                continue;
            }
            f.set(u, 0, v, pal.farmland);
            int row = Math.floorMod((u - u0) / 5 + v0, 4);
            BlockState crop = switch (row) {
                case 0 -> pal.wheat;
                case 1 -> pal.carrots;
                case 2 -> pal.potatoes;
                default -> pal.beetroots;
            };
            double g = f.unit(0xFA2L, u, 1, v);
            f.set(u, 1, v, g < 0.08 ? air : crop);
        }
        // Vogelscheuche und Kuerbisse am Rand
        int cu = (u0 + u1) / 2 + 1, cv = (v0 + v1) / 2;
        f.set(cu, 0, cv, pal.coarseDirt);
        f.fill(cu, 1, cv, cu, 2, cv, pal.spruceFence);
        f.set(cu, 3, cv, pal.hayBale);
        f.set(cu, 4, cv, pal.pumpkin);
        f.set(u0 + 1, 1, v1 - 1, pal.pumpkin);
        f.set(u1 - 1, 1, v1 - 1, pal.melon);
        f.set(u0 + 1, 1, v0 + 1, pal.composter);
    }

    // ── Laternen und Strommasten ──

    private static void lamps(Frame f, CityPalette pal) {
        for (int v = -26; v <= 26; v += 8) {
            if (Math.abs(v) <= 6) continue;
            lamp(f, 3, v, pal);
            lamp(f, -3, v + 4, pal);
        }
        for (int u = -26; u <= 26; u += 8) {
            if (Math.abs(u) <= 6) continue;
            lamp(f, u, 2, pal);
        }
    }

    private static void lamp(Frame f, int u, int v, CityPalette pal) {
        if (!f.isAir(u, 1, v) || !f.isAir(u, 2, v)) return;
        BlockState below = f.get(u, 0, v);
        if (below == null || below.isAir() || !below.getFluidState().isEmpty()) return;
        f.fill(u, 1, v, u, 3, v, pal.darkOakFence);
        f.set(u, 4, v, pal.glowstone);
        f.set(u, 5, v, pal.darkOakSlab);
    }
}
