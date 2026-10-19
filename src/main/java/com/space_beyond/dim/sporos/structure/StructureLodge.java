package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Berghuette im Hochland: Blockhaus 15 x 11 (u -7..7, v -5..5) mit Steinsockel, Fichtenwaenden, Hyphe-Eckpfosten,
 * Satteldach aus Fichtenstufen (First entlang u), Veranda nach Sueden, Kamin mit brennendem Feuer, Obergeschoss
 * mit Pritschen und Leiter. Draussen Holzstapel, Zaun und Schnee auf Boden und Dachfirst.
 * Loot {@code lodge}: Truhe und Kiste unten, Truhe und Fass oben.
 */
final class StructureLodge {

    private StructureLodge() {}

    static void plan(StructurePlan p) {
        p.values[0] = p.rand.nextInt(2);             // 0 Fichte, 1 dunkle Eiche als Wand
        p.values[1] = 40 + p.rand.nextInt(40);       // Schneeanteil in Prozent
        p.loot(6, 1, -4, Direction.WEST, Container.CHEST);
        p.loot(-6, 1, -4, Direction.EAST, Container.SUPPLY_CRATE);
        p.loot(5, 6, -3, Direction.WEST, Container.CHEST);
        p.loot(-5, 6, 3, Direction.EAST, Container.BARREL);
        if (p.rand.nextFloat() < 0.5F) p.loot(10, 1, -1, Direction.WEST, Container.MILITARY_CRATE);
        // Bewohner: ein Blindhut im Obergeschoss (der letzte Bewohner), draussen ab und zu Lauerer
        p.mob(0, 6, -1, StructureMobs.BLINDCAP);
        if (p.rand.nextFloat() < 0.5F) p.mob(2, 1, 0, p.infected(3, 3, 0, 0));
        for (int i = p.rand.nextInt(2); i > 0; i--) p.mob(-9 + p.rand.nextInt(18), 1, 9 + p.rand.nextInt(2), StructureMobs.LURKER);
    }

    static void paint(Frame f, StructurePlan p, CityPalette pal) {
        BlockState wall = p.values[0] == 0 ? pal.sprucePlanks : pal.darkOakPlanks;
        BlockState air = Blocks.AIR.defaultBlockState();
        // Sockel und Boden
        f.fill(-7, 0, -5, 7, 0, 5, pal.cobble);
        f.fill(-6, 0, -4, 6, 0, 4, pal.sprucePlanks);
        // Waende EG (y1-4) und Innenraum
        f.walls(-7, 1, -5, 7, 4, 5, wall);
        f.fill(-6, 1, -4, 6, 4, 4, air);
        for (int u : new int[] {-7, -3, 3, 7}) for (int v : new int[] {-5, 5}) f.fill(u, 1, v, u, 4, v, pal.hypheLog);
        for (int v : new int[] {-5, 0, 5}) for (int u : new int[] {-7, 7}) f.fill(u, 1, v, u, 4, v, pal.hypheLog);
        // Fenster
        for (int u : new int[] {-5, -1, 1, 5}) {
            f.fill(u, 2, -5, u, 3, -5, pal.glassPane);
            if (u != -1 && u != 1) f.fill(u, 2, 5, u, 3, 5, pal.glassPane);
        }
        for (int v : new int[] {-3, 3}) {
            f.fill(7, 2, v, 7, 3, v, pal.glassPane);
            if (v == 3) f.fill(-7, 2, v, -7, 3, v, pal.glassPane);
        }
        // Tuer nach Sueden, Veranda
        f.air(0, 1, 5);
        f.air(0, 2, 5);
        f.fill(-3, 0, 6, 3, 0, 7, pal.sprucePlanks);
        for (int u = -3; u <= 3; u++) f.setIfAir(u, 1, 7, u == 0 ? air : pal.spruceFence);
        f.set(-3, 1, 6, pal.spruceFence);
        f.set(3, 1, 6, pal.spruceFence);
        for (int u : new int[] {-3, 3}) f.fill(u, 2, 7, u, 4, 7, pal.spruceFence);
        f.fill(-3, 5, 6, 3, 5, 7, pal.spruceSlab);
        f.set(0, 0, 8, pal.cobbleSlab);
        // Obergeschoss: Boden y5, Leiter an der Ostwand
        f.fill(-6, 5, -4, 6, 5, 4, pal.sprucePlanks);
        f.air(6, 5, 3);
        StructurePainter.ladder(f, 6, 1, 5, 3, Direction.EAST, pal);
        // Dach: Reihen v = -6..6, Hoehe 5 + (6 - |v|), Stufen zum First, Giebel aus Wand
        for (int v = -6; v <= 6; v++) {
            int y = 5 + (6 - Math.abs(v));
            for (int u = -8; u <= 8; u++) {
                if (v == 0) {
                    f.set(u, y, v, pal.sprucePlanks);
                    if (f.unit(0x5A0L, u, 0, v) * 100 < p.values[1]) f.setIfAir(u, y + 1, v, pal.snow);
                } else {
                    f.set(u, y, v, CityPalette.stairs(Blocks.SPRUCE_STAIRS, v < 0 ? Direction.SOUTH : Direction.NORTH, false));
                    if (Math.abs(v) < 6) f.set(u, y - 1, v, CityPalette.stairs(Blocks.SPRUCE_STAIRS, v < 0 ? Direction.NORTH : Direction.SOUTH, true));
                }
            }
            // Giebelwaende unter dem Dach
            if (Math.abs(v) <= 5) {
                for (int u : new int[] {-7, 7}) for (int yy = 5; yy < y; yy++) f.set(u, yy, v, wall);
                if (Math.abs(v) < 5) for (int u = -6; u <= 6; u++) for (int yy = 6; yy < y - 1; yy++) f.set(u, yy, v, air);
            }
        }
        f.fill(7, 7, -1, 7, 8, 1, pal.glassPane); // Giebelfenster
        f.fill(-7, 7, -1, -7, 8, 1, pal.glassPane);
        // Kamin: Steinsaeule an der Westwand, Feuer davor, Schornstein durchs Dach
        f.fill(-6, 1, 0, -6, 12, 0, pal.cobble);
        f.fill(-6, 1, -1, -6, 3, -1, pal.cobble);
        f.fill(-6, 1, 1, -6, 3, 1, pal.cobble);
        f.set(-6, 1, 0, pal.cobble);
        f.set(-5, 1, 0, pal.campfireLit);
        f.set(-5, 0, 0, pal.cobble);
        f.set(-6, 13, 0, pal.cobbleWall);
        // Einrichtung EG: Tisch, Stuehle, Kueche
        f.set(0, 1, -2, pal.spruceFence);
        f.set(0, 2, -2, pal.sprucePressurePlate);
        f.set(-1, 1, -2, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        f.set(1, 1, -2, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        f.set(5, 1, 4, pal.craftingTable);
        f.set(4, 1, 4, pal.bookshelf);
        f.set(-3, 1, 4, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        f.set(-2, 1, 4, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        f.set(-4, 1, 4, pal.flowerPot);
        f.set(3, 3, -4, CityPalette.facing(pal.wallTorch, Direction.SOUTH)); // an der Nordwand
        f.set(-3, 1, -4, pal.carpetRed);
        f.set(-2, 1, -4, pal.carpetRed);
        // OG: Pritschen
        StructurePainter.cot(f, -4, 6, -3, true, pal);
        StructurePainter.cot(f, 2, 6, 3, true, pal);
        StructurePainter.cot(f, -4, 6, 3, true, pal);
        f.set(0, 6, 0, pal.carpetBrown);
        f.set(6, 6, -3, CityPalette.facing(pal.wallTorch, Direction.WEST)); // an der Giebelwand
        // Draussen: Holzstapel, Zaun, Hackklotz, Schnee
        for (int v = -3; v <= -1; v++) for (int y = 1; y <= 2; y++) f.setIfOpen(10, y, v, CityPalette.log(pal.hypheLog, Direction.Axis.X));
        f.setIfOpen(11, 1, -2, CityPalette.log(pal.hypheLog, Direction.Axis.X));
        f.setIfOpen(10, 1, 2, CityPalette.log(pal.hypheLog, Direction.Axis.Y));
        for (int u = -12; u <= 12; u++) {
            if (Math.abs(u) == 12 || u % 3 == 0) {
                f.setIfOpen(u, 1, -10, pal.spruceFence);
                if (u != 0) f.setIfOpen(u, 1, 11, pal.spruceFence);
            }
        }
        for (int v = -10; v <= 11; v++) if (Math.abs(v) == 10 || v == 11 || v % 3 == 0) {
            f.setIfOpen(-12, 1, v, pal.spruceFence);
            f.setIfOpen(12, 1, v, pal.spruceFence);
        }
        int r = p.s.type().radius;
        for (int u = -r; u <= r; u++) {
            for (int v = -r; v <= r; v++) {
                if (!f.contains(u, v)) continue;
                BlockState ground = f.get(u, 0, v);
                if (ground == null || ground != pal.grassBlock) continue;
                double n = f.unit(0x5A1L, u, 0, v) * 100;
                if (n < p.values[1]) f.setIfAir(u, 1, v, pal.snow);
                else if (n > 92) f.setIfAir(u, 1, v, pal.fern);
            }
        }
    }
}
