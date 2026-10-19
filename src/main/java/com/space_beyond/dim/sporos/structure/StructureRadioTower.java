package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;

/**
 * Funkmast mit Ranger-Huette (klein, haeufig). Huette 5x5 bei u -6..-2 (Tuer nach Osten zum Mast), Gittermast 2x2 bei
 * u 3..4 mit Leiter, Plattform, Gelaender und rotem Warnlicht; Hoehe aus der Planung ({@code values[0]}).
 * Loot {@code ranger}: Truhe und Fass in der Huette, Truhe auf der Plattform.
 */
final class StructureRadioTower {

    private StructureRadioTower() {}

    static void plan(StructurePlan p) {
        p.values[0] = 18 + p.rand.nextInt(7); // Masthoehe
        p.values[1] = p.rand.nextInt(3);      // Huettenvariante: 0 Holz, 1 Beton, 2 Ziegel
        p.loot(-5, 1, 1, Direction.EAST, Container.CHEST);
        p.loot(-5, 1, -1, Direction.EAST, Container.BARREL);
        if (p.rand.nextFloat() < 0.6F) p.loot(4, p.values[0] + 2, 1, Direction.WEST, Container.SUPPLY_CRATE);
        // Bewohner: ein bis zwei Sporlinge um die Huette, manchmal ein Lauerer im Gebuesch
        for (int i = p.rand.nextInt(3); i > 0; i--) p.mob(-4 + p.rand.nextInt(9), 1, 4 + p.rand.nextInt(4), p.infected(5, 2, 1, 0));
        if (p.rand.nextFloat() < 0.3F) p.spawner(-20 + p.rand.nextInt(3), 1, 2 + p.rand.nextInt(5), 1); // Sporeling-Nest am Rand
    }

    static void paint(Frame f, StructurePlan p, CityPalette pal) {
        int h = p.values[0];
        // ── Huette ──
        var wall = switch (p.values[1]) {
            case 1 -> pal.concrete;
            case 2 -> pal.bricks;
            default -> pal.hyphePlanks;
        };
        f.fill(-7, 0, -3, -1, 0, 3, pal.sprucePlanks);          // Boden
        f.walls(-6, 1, -2, -2, 3, 2, wall);                       // Waende
        for (int u : new int[] {-6, -2}) for (int v : new int[] {-2, 2}) f.fill(u, 1, v, u, 3, v, pal.hypheLog); // Eckpfosten
        f.fill(-5, 1, -1, -3, 3, 1, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()); // innen frei
        f.air(-2, 1, 0);
        f.air(-2, 2, 0);                                           // Tuer nach Osten
        f.set(-4, 2, -2, pal.glassPane);
        f.set(-4, 2, 2, pal.glassPane);
        f.set(-6, 2, 0, pal.glassPane);
        f.fill(-7, 4, -3, -1, 4, 3, pal.spruceSlab);               // flaches Dach mit Ueberstand
        f.setIfOpen(-3, 1, -1, pal.craftingTable);
        StructurePainter.cot(f, -4, 1, 1, true, pal);
        f.set(-3, 2, -1, pal.torch);                               // Fackel auf der Werkbank
        // Verfall
        for (int u = -6; u <= -2; u++) {
            StructurePainter.overgrow(f, u, 4, -3 + (u + 6) % 7, 0.4, pal);
            StructurePainter.vineWall(f, u, 3, 2, Direction.SOUTH, 3, 0.3, pal);
        }
        // ── Mast ──
        f.fill(1, 0, -3, 6, 0, 2, pal.gravel);                     // Kiesplatte
        for (int u = 3; u <= 4; u++) for (int v = -1; v <= 0; v++) f.fill(u, 1, v, u, h, v, pal.andesiteWall);
        for (int y = 4; y < h; y += 4) {                           // Verstrebungen
            for (int u = 2; u <= 5; u++) for (int v = -2; v <= 1; v++) {
                boolean ring = u == 2 || u == 5 || v == -2 || v == 1;
                if (ring) f.setIfAir(u, y, v, pal.oakFence);
            }
        }
        StructurePainter.ladder(f, 2, 1, h, -1, Direction.EAST, pal); // Leiter am Westpfosten
        f.fill(2, h + 1, -2, 5, h + 1, 1, pal.oakPlanks);           // Plattform
        f.air(2, h + 1, -1);                                        // Einstieg
        for (int u = 2; u <= 5; u++) for (int v = -2; v <= 1; v++) {
            boolean rim = u == 2 || u == 5 || v == -2 || v == 1;
            if (rim && !(u == 2 && v == -1)) f.set(u, h + 2, v, pal.oakFence);
        }
        f.fill(3, h + 2, 0, 3, h + 5, 0, pal.postBlack);            // Antenne
        f.set(3, h + 6, 0, pal.glassRed);
        f.set(4, h + 2, -1, pal.torch);
        // Stromkasten und Kabel zur Huette
        f.set(0, 1, 0, pal.vConcreteGray);
        for (int u = -1; u >= -2; u--) f.setIfAir(u, 1, 0, pal.sprucePressurePlate);
    }
}
