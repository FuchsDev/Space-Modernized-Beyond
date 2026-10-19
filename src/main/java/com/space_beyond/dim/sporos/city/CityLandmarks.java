package com.space_beyond.dim.sporos.city;

import com.space_beyond.dim.sporos.city.CellPlan.Sandbags;
import com.space_beyond.dim.sporos.city.CellPlan.Tent;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Landmarken der Sporos-Staedte (eigene Erweiterung, Nutzerwunsch: Strukturen im Stil von Ueberlebens-Spielen mit
 * Infizierten, mit Loot-Truhen und -Kisten). Geplant in {@link CellPlan}, gemalt wie alles andere pro Chunk auf die
 * {@link CityCanvas}; Behaelter setzt {@link CityPainter#paintCell} danach ueber die Beutestellen, Loot-Tables und den
 * Spawner {@link CityLoot} in der Dekorationsphase.
 * <ul>
 *   <li><b>Kontrollpunkt</b> ({@code CHECKPOINT}): Betonsperre quer ueber die Hauptstrasse mit Luecke und
 *       Sandsack-Stellungen, Wachturm, ummauertes Lager mit Zelten, Armee-Lkw und Kistendepot. Loot {@code checkpoint}.</li>
 *   <li><b>Lager</b> ({@code CAMP}): Lagerfeuer, Zelte, Sitzstaemme, Barrikade, Vorratskisten. Loot {@code camp}.</li>
 *   <li><b>Feldlazarett</b> ({@code TRIAGE}): grosses weisses Zelt mit Pritschen, Medizinkisten, Krankenwagen,
 *       Kreuz-Schild. Loot {@code triage}.</li>
 *   <li><b>Nest</b> ({@code NEST}): hohler Pilzhuegel mit Eingang, Leuchtpilzen, Ranken, Spawner (Lurker) und den
 *       Hinterlassenschaften der Opfer. Loot {@code nest}.</li>
 *   <li><b>Konvoi</b> (Strassenobjekt): zwei liegengebliebene Armee-Lkw, verstreute Kisten, Sandsaecke. Loot {@code convoy}.</li>
 * </ul>
 */
final class CityLandmarks {

    private CityLandmarks() {}

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    static void paint(CityCanvas c, CellPlan plan, CityPalette p) {
        switch (plan.type) {
            case CHECKPOINT -> checkpoint(c, plan, p);
            case CAMP -> camp(c, plan, p);
            case TRIAGE -> triage(c, plan, p);
            case NEST -> nest(c, plan, p);
            default -> { }
        }
        if (plan.convoy != null) convoy(c, plan, p);
        for (Tent t : plan.tents) tent(c, plan, t, p);
        for (Sandbags s : plan.sandbags) sandbags(c, plan, s, p);
    }

    // ── Kontrollpunkt ───────────────────────────────────────────────────────

    /** Lokale Koordinate (Planung fuers Westsegment) -> Welt; beim Nordsegment transponiert. */
    private static int wx(CellPlan plan, boolean t, int lx, int lz) {
        return plan.minX + (t ? lz : lx);
    }

    private static int wz(CellPlan plan, boolean t, int lx, int lz) {
        return plan.minZ + (t ? lx : lz);
    }

    /** Richtung in lokaler Planung -> Welt (Transposition tauscht West/Nord und Ost/Sued). */
    private static Direction dir(boolean t, Direction d) {
        if (!t) return d;
        return switch (d) {
            case WEST -> Direction.NORTH;
            case NORTH -> Direction.WEST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            default -> d;
        };
    }

    private static void set(CityCanvas c, CellPlan plan, boolean t, int lx, int lz, int y, BlockState s) {
        c.set(wx(plan, t, lx, lz), y, wz(plan, t, lx, lz), s);
    }

    private static void setIfOpen(CityCanvas c, CellPlan plan, boolean t, int lx, int lz, int y, BlockState s) {
        c.setIfOpen(wx(plan, t, lx, lz), y, wz(plan, t, lx, lz), s);
    }

    private static void checkpoint(CityCanvas c, CellPlan plan, CityPalette p) {
        boolean t = plan.checkpoint == Direction.NORTH;
        int g = plan.groundY();
        long s = plan.layout.seed ^ 0xC4ECL;

        // Strassensperre quer ueber das Strassenstueck (lokal x 0-7) bei z 18, Luecke x 2-3 (dort steht der Lkw)
        for (int lx = 0; lx < CityLayout.STREET; lx++) {
            int x = wx(plan, t, lx, 18), z = wz(plan, t, lx, 18);
            if (!c.contains(x, z)) continue;
            // Haltelinie zwei Bloecke davor
            int hx = wx(plan, t, lx, 16), hz = wz(plan, t, lx, 16);
            if (CityRandom.unit(s, hx, 1, hz) < 0.7) c.set(hx, g, hz, p.lineWhite);
            if (lx == 2 || lx == 3) continue;
            double u = CityRandom.unit(s, x, 0, z);
            if (u < plan.decay * 0.3) {
                c.setIfOpen(x, g + 1, z, p.concreteCracked); // umgekippt/zerbrochen
                continue;
            }
            c.setIfOpen(x, g + 1, z, p.concreteSmooth);
            c.setIfOpen(x, g + 2, z, u < 0.5 ? p.concreteSlab : p.oakFence);
        }

        // Lagermauer um das Grundstueck (lokal 10-29): Beton 2 hoch + Zaun, Tor zur Strasse bei z 20-22, Verfall
        for (int i = 10; i <= 29; i++) {
            wall(c, plan, t, i, 10, g, p, s);
            wall(c, plan, t, i, 29, g, p, s);
            if (i < 20 || i > 22) wall(c, plan, t, 10, i, g, p, s);
            wall(c, plan, t, 29, i, g, p, s);
        }
        for (int lz : new int[] {19, 23}) for (int y = g + 1; y <= g + 4; y++) set(c, plan, t, 10, lz, y, p.postStone);
        // Hofboden am Tor: Kies/Beton bleibt (lotGround), kaltes Lagerfeuer in der Mitte
        setIfOpen(c, plan, t, 20, 20, g + 1, p.campfire);

        tower(c, plan, t, g, p, s);
    }

    /** Mauersaeule, ausser im Turm (lokal 10-12 / 16-18). */
    private static void wall(CityCanvas c, CellPlan plan, boolean t, int lx, int lz, int g, CityPalette p, long s) {
        if (lx >= 10 && lx <= 12 && lz >= 16 && lz <= 18) return;
        int x = wx(plan, t, lx, lz), z = wz(plan, t, lx, lz);
        if (!c.contains(x, z)) return;
        double u = CityRandom.unit(s ^ 0x3A11L, x, 0, z);
        if (u < plan.decay * 0.25) {
            // eingestuerzt: nur ein Rest
            c.set(x, g + 1, z, u < plan.decay * 0.1 ? p.cobble : p.brickConcreteBroken);
            return;
        }
        c.set(x, g + 1, z, p.concrete);
        c.set(x, g + 2, z, CityRandom.unit(s, x, 2, z) < plan.decay * 0.5 ? p.concreteCracked : p.concrete);
        if (CityRandom.unit(s, x, 3, z) > plan.decay * 0.5) c.set(x, g + 3, z, p.oakFence);
        if (CityRandom.unit(s ^ 0x77L, x, 0, z) < plan.veg * 0.4) c.setIfAir(x, g + 4, z, p.vine(Direction.UP));
    }

    /** Wachturm lokal (10-12, 16-18): Betonpfeiler, Plattform mit Gelaender bei g+5, Dach bei g+9, Leiter innen. */
    private static void tower(CityCanvas c, CellPlan plan, boolean t, int g, CityPalette p, long s) {
        for (int lx = 10; lx <= 12; lx++) {
            for (int lz = 16; lz <= 18; lz++) {
                boolean corner = (lx == 10 || lx == 12) && (lz == 16 || lz == 18);
                boolean north = lz == 16; // geschlossene Wand (Strassenseite) fuer die Leiter
                for (int y = g + 1; y <= g + 4; y++) {
                    if (corner || north) set(c, plan, t, lx, lz, y, CityRandom.unit(s, lx, y, lz) < plan.decay * 0.3 ? p.concreteCracked : p.concrete);
                    else set(c, plan, t, lx, lz, y, AIR);
                }
                // Plattform (Loch ueber der Leiter bei 11/17)
                set(c, plan, t, lx, lz, g + 5, lx == 11 && lz == 17 ? AIR : p.oakPlanks);
                boolean rim = lx == 10 || lx == 12 || lz == 16 || lz == 18;
                boolean crate = lx == 12 && lz == 18; // Kiste auf der Plattform (CellPlan.planCheckpoint)
                if (rim && !crate && CityRandom.unit(s ^ 0x9EL, lx, 6, lz) > plan.decay * 0.3) set(c, plan, t, lx, lz, g + 6, p.oakFence);
                else set(c, plan, t, lx, lz, g + 6, AIR);
                set(c, plan, t, lx, lz, g + 7, corner ? p.oakFence : AIR);
                set(c, plan, t, lx, lz, g + 8, corner ? p.oakFence : AIR);
                if (CityRandom.unit(s ^ 0xD0F0L, lx, 9, lz) > plan.decay * 0.35) set(c, plan, t, lx, lz, g + 9, p.spruceSlab);
            }
        }
        // Leiter an der Nordwand (lokal 11/17, Wand bei 11/16 -> Leiter zeigt nach Sueden)
        for (int y = g + 1; y <= g + 5; y++) set(c, plan, t, 11, 17, y, CityPalette.facing(p.ladder, dir(t, Direction.SOUTH)));
        // Sandsaecke auf der Plattform (Strassenseite)
        set(c, plan, t, 10, 17, g + 6, p.sandbag);
    }

    // ── Lager ───────────────────────────────────────────────────────────────

    private static void camp(CityCanvas c, CellPlan plan, CityPalette p) {
        int g = plan.groundY();
        int cx = plan.minX + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2, cz = plan.minZ + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2;
        long s = plan.layout.seed ^ 0xCA3FL;
        // Feuerstelle: Kiesring, brennendes Lagerfeuer
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            c.set(cx + dx, g, cz + dz, dx == 0 && dz == 0 ? p.cobble : p.gravel);
            c.set(cx + dx, g + 1, cz + dz, dx == 0 && dz == 0 ? p.campfireLit : AIR);
        }
        // Werkbank und ein Stuhl
        c.setIfOpen(cx + 3, g + 1, cz - 3, p.craftingTable);
        c.setIfOpen(cx - 3, g + 1, cz - 3, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        // Barrikade: Zaun und Bretter an der West- und Nordkante des Grundstuecks, mit Luecken
        int lx0 = plan.minX + CityLayout.LOT_MIN, lz0 = plan.minZ + CityLayout.LOT_MIN;
        for (int i = 0; i < 20; i++) {
            barricade(c, plan, lx0, lz0 + i, g, p, s);
            barricade(c, plan, lx0 + i, lz0, g, p, s);
        }
    }

    private static void barricade(CityCanvas c, CellPlan plan, int x, int z, int g, CityPalette p, long s) {
        if (!c.contains(x, z)) return;
        double u = CityRandom.unit(s, x, 0, z);
        if (u < 0.3) return; // Luecke
        if (u < 0.55) {
            c.setIfOpen(x, g + 1, z, p.oakFence);
        } else if (u < 0.8) {
            c.setIfOpen(x, g + 1, z, p.sprucePlanks);
            if (u > 0.7) c.setIfOpen(x, g + 2, z, p.oakFence);
        } else {
            c.setIfOpen(x, g + 1, z, p.sandbag);
        }
    }

    // ── Feldlazarett ────────────────────────────────────────────────────────

    private static void triage(CityCanvas c, CellPlan plan, CityPalette p) {
        if (plan.tents.isEmpty()) return;
        Tent t = plan.tents.get(0);
        int g = plan.groundY();
        int half = t.width() / 2;
        // Pritschen entlang beider Innenwaende (quer 1 und 7), alle 2 Bloecke
        for (int i = 1; i < t.len() - 1; i += 2) {
            for (int j : new int[] {1, t.width() - 2}) {
                int x = t.alongX() ? t.x() + i : t.x() + j;
                int z = t.alongX() ? t.z() + j : t.z() + i;
                if (!c.contains(x, z)) continue;
                c.setIfOpen(x, g + 1, z, p.spruceSlab);
                // Kopfende: weisses Laken daneben (innen)
                int kx = t.alongX() ? x : x + (j == 1 ? 1 : -1);
                int kz = t.alongX() ? z + (j == 1 ? 1 : -1) : z;
                if (CityRandom.unit(plan.layout.seed, kx, 0, kz) < 0.5) c.setIfAir(kx, g + 1, kz, p.carpetWhite);
            }
        }
        // Kreuz-Schild vor dem Eingang: 3x3 weisse Tafel mit rotem Kreuz, 2 Bloecke vor dem offenen Ende, seitlich versetzt
        int end = t.openHigh() ? t.len() + 1 : -2;
        int side = half + 3; // neben dem Eingang
        for (int k = -1; k <= 1; k++) {
            for (int y = 1; y <= 3; y++) {
                int x = t.alongX() ? t.x() + end : t.x() + side + k;
                int z = t.alongX() ? t.z() + side + k : t.z() + end;
                boolean cross = k == 0 || y == 2;
                c.setIfOpen(x, g + y, z, cross ? p.terracottaRed : p.concreteWhite);
            }
        }
    }

    // ── Nest ────────────────────────────────────────────────────────────────

    private static void nest(CityCanvas c, CellPlan plan, CityPalette p) {
        int g = plan.groundY();
        int r = plan.nestRadius;
        int cx = plan.spawner != null ? plan.spawner.x() : plan.minX + 19, cz = plan.spawner != null ? plan.spawner.z() : plan.minZ + 19;
        long s = plan.layout.seed ^ 0x4E57L;
        for (int x = Math.max(c.minX, cx - r - 1); x <= Math.min(c.maxX(), cx + r + 1); x++) {
            for (int z = Math.max(c.minZ, cz - r - 1); z <= Math.min(c.maxZ(), cz + r + 1); z++) {
                double d = Math.sqrt((x - cx) * (x - cx) + (z - cz) * (z - cz)) + (CityRandom.noise(s, x, z, 3) - 0.5) * 1.2;
                if (d > r + 0.5) {
                    // Vorplatz: Moos und Leuchtpilze
                    if (d < r + 2.5 && CityRandom.unit(s, x, 0, z) < 0.5) {
                        c.set(x, g, z, p.moss);
                        if (CityRandom.unit(s, x, 1, z) < 0.25) c.setIfOpen(x, g + 1, z, p.glowCap);
                    }
                    continue;
                }
                double h = Math.sqrt(Math.max(0, r * r - d * d)) * 0.85 + CityRandom.noise(s ^ 0x33L, x, z, 2) * 0.8;
                int top = (int) Math.round(h);
                boolean inner = d < r - 2.2;
                boolean tunnel = x < cx && x >= cx - r - 1 && Math.abs(z - cz) <= 1; // Eingang nach Westen
                c.set(x, g, z, inner ? p.moss : p.fungalStem);
                for (int dy = 1; dy <= Math.max(top, 2); dy++) {
                    int y = g + dy;
                    boolean hollow = (inner && dy < top - 1) || (tunnel && dy <= 2);
                    if (hollow) {
                        c.set(x, y, z, AIR);
                        continue;
                    }
                    if (dy > top) continue;
                    double u = CityRandom.unit(s, x, y, z);
                    BlockState b = dy >= top - 1 ? (u < 0.15 ? p.mossCushion : p.fungalCap) : (u < 0.2 ? p.fungalCap : p.fungalStem);
                    c.set(x, y, z, b);
                }
                // innen: Leuchtpilze am Boden, Ranken von der Decke; aussen: Leuchtpilze auf dem Huegel
                if (inner) {
                    if (CityRandom.unit(s ^ 0x44L, x, 0, z) < 0.18) c.set(x, g + 1, z, p.glowCap);
                    if (top - 2 > 2 && CityRandom.unit(s ^ 0x55L, x, 0, z) < 0.3) {
                        for (int y = g + top - 2; y > g + 2 && CityRandom.unit(s, x, y, z) < 0.8; y--) c.setIfAir(x, y, z, p.vine(Direction.UP));
                    }
                } else if (top >= 1 && CityRandom.unit(s ^ 0x66L, x, 0, z) < 0.12) {
                    c.setIfAir(x, g + top + 1, z, p.glowCap);
                }
            }
        }
        if (plan.spawner != null) c.set(plan.spawner.x(), plan.spawner.y(), plan.spawner.z(), p.spawner);
    }

    // ── Konvoi ──────────────────────────────────────────────────────────────

    private static void convoy(CityCanvas c, CellPlan plan, CityPalette p) {
        CellPlan.Convoy cv = plan.convoy;
        int g = plan.groundY();
        long s = plan.layout.seed ^ 0xC0E0L;
        // Betonbarrieren am Kopf des Konvois (quer 3-4, zwei Bloecke davor) und Truemmer auf der Fahrbahn
        for (int k = 0; k < 2; k++) {
            int across = 3 + k, along = cv.along() - 2;
            int x = cv.north() ? plan.minX + along : plan.minX + across;
            int z = cv.north() ? plan.minZ + across : plan.minZ + along;
            c.setIfOpen(x, g + 1, z, p.concreteSmooth);
        }
        for (int along = cv.along() - 2; along <= cv.along() + 14; along++) {
            for (int across = 0; across < CityLayout.STREET; across++) {
                int x = cv.north() ? plan.minX + along : plan.minX + across;
                int z = cv.north() ? plan.minZ + across : plan.minZ + along;
                double u = CityRandom.unit(s, x, 0, z);
                if (u < 0.06) c.set(x, g, z, p.asphaltPatch);
                else if (u < 0.1) c.setIfOpen(x, g + 1, z, p.cobbleSlab);
                else if (u < 0.12) c.setIfOpen(x, g + 1, z, p.gravel);
            }
        }
    }

    // ── Zelte und Sandsaecke ────────────────────────────────────────────────

    /**
     * Zelt als Giebeldach aus Wolle: Hoehe steigt von 1 am Rand bis {@code half + 1} am First, innen hohl, Rueckwand
     * geschlossen, Eingang offen. Boden innen Bretter (Lazarett) bzw. festgetretene Erde, Pritsche an der Rueckwand
     * (kleine Zelte), Loecher und Moos nach Verfall.
     */
    private static void tent(CityCanvas c, CellPlan plan, Tent t, CityPalette p) {
        int half = t.width() / 2;
        BlockState cloth = p.tentCloth[Math.min(t.kind(), p.tentCloth.length - 1)];
        long s = plan.layout.seed ^ 0x7E47L ^ ((long) t.x() << 20) ^ t.z();
        for (int i = 0; i < t.len(); i++) {
            for (int j = 0; j < t.width(); j++) {
                int x = t.alongX() ? t.x() + i : t.x() + j;
                int z = t.alongX() ? t.z() + j : t.z() + i;
                if (!c.contains(x, z)) continue;
                int d = Math.abs(j - half);
                int h = half - d + 1;
                boolean closed = t.openHigh() ? i == 0 : i == t.len() - 1;
                if (d < half) {
                    c.set(x, t.y(), z, t.kind() == 2 ? p.sprucePlanks : p.coarseDirt);
                    for (int dy = 1; dy < h; dy++) c.set(x, t.y() + dy, z, closed ? cloth : AIR);
                }
                boolean hole = CityRandom.unit(s, x, h, z) < plan.decay * 0.12;
                if (!hole) {
                    c.set(x, t.y() + h, z, cloth);
                    if (CityRandom.unit(s ^ 0x11L, x, 0, z) < plan.veg * 0.3) c.setIfAir(x, t.y() + h + 1, z, p.mossCarpet);
                }
                // Pritsche an der Rueckwand (kleine Zelte): zwei Platten neben der Mitte
                if (t.width() <= 5 && j == half + 1 && (t.openHigh() ? i == 1 || i == 2 : i == t.len() - 2 || i == t.len() - 3)) {
                    c.set(x, t.y() + 1, z, p.spruceSlab);
                }
            }
        }
        // Zeltstangen am Eingang: Zaunpfosten links und rechts des Durchgangs (der Mittelgang bleibt frei)
        int ei = t.openHigh() ? t.len() - 1 : 0;
        for (int j : new int[] {half - 1, half + 1}) {
            int px = t.alongX() ? t.x() + ei : t.x() + j;
            int pz = t.alongX() ? t.z() + j : t.z() + ei;
            c.setIfAir(px, t.y() + 1, pz, p.spruceFence);
        }
    }

    /** Sandsack-Reihe: eine Lage, in der Mitte zwei. */
    private static void sandbags(CityCanvas c, CellPlan plan, Sandbags b, CityPalette p) {
        for (int i = 0; i < b.len(); i++) {
            int x = b.alongX() ? b.x() + i : b.x();
            int z = b.alongX() ? b.z() : b.z() + i;
            if (!c.contains(x, z)) continue;
            c.setIfOpen(x, b.y() + 1, z, p.sandbag);
            if (b.len() >= 3 && i > 0 && i < b.len() - 1) c.setIfOpen(x, b.y() + 2, z, p.sandbag);
        }
    }
}
