package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.structure.SporosStructures.Structure;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gemeinsamer Maler der Wildnis-Strukturen: Gelaende im Grundriss auf die Grundhoehe einebnen (mit Uebergang zur
 * Natur), dann die Struktur je Art ({@link StructureRadioTower}, {@link StructureLodge}, {@link StructureHospital},
 * {@link StructurePowerPlant}, {@link StructureSettlement}), zuletzt Behaelter und Spawner der Planung. Alles ist
 * eine Funktion von Plan und Weltkoordinaten, jeder Chunk setzt nur seinen Ausschnitt.
 */
public final class StructurePainter {

    private StructurePainter() {}

    /** Einmalige Planung je Struktur (Zufall nur hier, s. {@link StructurePlan}). */
    static void plan(StructurePlan p) {
        switch (p.s.type()) {
            case RADIO_TOWER -> StructureRadioTower.plan(p);
            case LODGE -> StructureLodge.plan(p);
            case HOSPITAL -> StructureHospital.plan(p);
            case POWER_PLANT -> StructurePowerPlant.plan(p);
            case SETTLEMENT -> StructureSettlement.plan(p);
        }
    }

    static void paint(CityCanvas c, StructurePlan p) {
        CityPalette pal = CityPalette.get();
        terrain(c, p, pal);
        Frame f = new Frame(c, p);
        switch (p.s.type()) {
            case RADIO_TOWER -> StructureRadioTower.paint(f, p, pal);
            case LODGE -> StructureLodge.paint(f, p, pal);
            case HOSPITAL -> StructureHospital.paint(f, p, pal);
            case POWER_PLANT -> StructurePowerPlant.paint(f, p, pal);
            case SETTLEMENT -> StructureSettlement.paint(f, p, pal);
        }
        for (StructurePlan.Loot l : p.loot) container(f, l, pal);
        for (StructurePlan.Spawner sp : p.spawners) f.set(sp.u(), sp.y(), sp.v(), pal.spawner);
    }

    /**
     * Gelaende: innerhalb des Radius auf {@code base} (Bodenblock), im Uebergang ({@link SporosStructures#BLEND})
     * weich zur natuerlichen Hoehe; Wasser im Grundriss wird aufgefuellt, Hoehlen unter der Oberflaeche geschlossen.
     */
    static void terrain(CityCanvas c, StructurePlan p, CityPalette pal) {
        Structure s = p.s;
        int r = s.type().radius;
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = c.minX; x <= c.maxX(); x++) {
            for (int z = c.minZ; z <= c.maxZ(); z++) {
                int d = s.distance(x, z);
                if (d > r + SporosStructures.BLEND) continue;
                double jitter = (CityRandom.noise(s.seed() ^ 0x7E44L, x, z, 6) - 0.5) * 3.0;
                double w = CityRandom.smoothstep(r, r + SporosStructures.BLEND, d + jitter);
                if (w >= 0.995) continue;
                int nat = 0;
                for (int y = CityCanvas.HEIGHT - 1; y > 0; y--) {
                    BlockState b = c.get(x, y, z);
                    if (b != null && !b.isAir() && b.getFluidState().isEmpty()) {
                        nat = y;
                        break;
                    }
                }
                int target = (int) Math.round(p.base + (nat - p.base) * w);
                for (int y = CityCanvas.HEIGHT - 1; y > target; y--) {
                    BlockState b = c.get(x, y, z);
                    if (b != null && !b.isAir()) c.set(x, y, z, air);
                }
                int closeDepth = w < 0.5 ? 6 : 2;
                for (int y = target; y > 1 && (y > nat || y > target - closeDepth); y--) {
                    BlockState b = c.get(x, y, z);
                    boolean open = b == null || b.isAir() || !b.getFluidState().isEmpty();
                    if (y == target) {
                        if (w < 0.7 || open) c.set(x, y, z, pal.grassBlock);
                    } else if (open || y > nat) {
                        c.set(x, y, z, target - y <= 3 ? pal.silt : pal.stone);
                    }
                }
            }
        }
    }

    /** Behaelter der Planung setzen (nur auf festem Untergrund, Einrichtung darf weichen). */
    static void container(Frame f, StructurePlan.Loot l, CityPalette pal) {
        if (!f.contains(l.u(), l.v())) return;
        BlockState below = f.get(l.u(), l.y() - 1, l.v());
        if (below == null || below.isAir() || !below.getFluidState().isEmpty()) return;
        BlockState state = switch (l.container()) {
            case BARREL -> CityPalette.facingAll(pal.barrel, Direction.UP);
            case SUPPLY_CRATE -> pal.supplyCrate;
            case MILITARY_CRATE -> pal.militaryCrate;
            default -> CityPalette.facing(pal.chest, l.facing());
        };
        f.set(l.u(), l.y(), l.v(), state);
    }

    // ── gemeinsame Bauteile ─────────────────────────────────────────────────

    /** Autowrack 2 x {@code len} entlang u (ab u0) bzw. v, Farbe aus {@code carColors}, Dach aus Glas/Platte. */
    static void car(Frame f, int u0, int v0, boolean alongU, int len, int color, boolean burnt, CityPalette pal) {
        BlockState body = burnt ? pal.vConcreteGray : pal.carColors[color];
        BlockState dark = burnt ? pal.vConcreteBlack : pal.terracottaBrown;
        boolean van = len == 5;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 2; j++) {
                int u = alongU ? u0 + i : u0 + j, v = alongU ? v0 + j : v0 + i;
                boolean end = i == 0 || i == len - 1;
                f.setIfOpen(u, 1, v, end && f.unit(u, 1, v) < 0.5 ? dark : body);
                boolean cabin = van ? i >= 1 : i >= 1 && i <= 2;
                if (cabin) {
                    f.setIfOpen(u, 2, v, van && i >= 2 ? body : (burnt ? Blocks.AIR.defaultBlockState() : pal.glassBlack));
                    if (van) f.setIfOpen(u, 3, v, body);
                    else if (!burnt) f.setIfOpen(u, 3, v, CityPalette.slab(Blocks.SMOOTH_STONE_SLAB.defaultBlockState(),
                            net.minecraft.world.level.block.state.properties.SlabType.BOTTOM));
                }
            }
        }
    }

    /** Pritsche: zwei Platten entlang u ab (u, v) mit Laken am Kopfende. */
    static void cot(Frame f, int u, int y, int v, boolean alongU, CityPalette pal) {
        f.setIfOpen(u, y, v, pal.spruceSlab);
        f.setIfOpen(alongU ? u + 1 : u, y, alongU ? v : v + 1, pal.spruceSlab);
        f.setIfAir(alongU ? u + 1 : u, y + 1, alongU ? v : v + 1, pal.carpetWhite);
    }

    /** Leiter von y0 bis y1 an der Wand, die in Richtung {@code wall} (lokal) liegt. */
    static void ladder(Frame f, int u, int y0, int y1, int v, Direction wall, CityPalette pal) {
        for (int y = y0; y <= y1; y++) f.set(u, y, v, CityPalette.facing(pal.ladder, wall.getOpposite()));
    }

    /** Verfall: Moosteppich auf freier Oberflaeche, Ranken an Waenden - nach Hash, Anteil {@code amount}. */
    static void overgrow(Frame f, int u, int y, int v, double amount, CityPalette pal) {
        if (f.unit(0x0B3L, u, y, v) < amount) f.setIfAir(u, y + 1, v, pal.mossCarpet);
    }

    static void vineWall(Frame f, int u, int yTop, int v, Direction wall, int maxLen, double chance, CityPalette pal) {
        if (f.unit(0x71EL, u, 0, v) >= chance) return;
        int len = 1 + (int) (f.unit(0x71FL, u, 1, v) * maxLen);
        Direction out = wall.getOpposite();
        for (int y = yTop; y > yTop - len; y--) f.setIfAir(u + out.getStepX(), y, v + out.getStepZ(), pal.vine(wall));
    }
}
