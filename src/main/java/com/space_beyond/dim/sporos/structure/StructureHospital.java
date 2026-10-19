package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Verlassenes Krankenhaus: Hauptblock 37 x 15 (u -18..18, v -7..7), drei Geschosse zu 4 Bloecken, Flachdach mit
 * Bruestung, Eingang nach Sueden mit Vordach und rotem H, Mittelflur mit Zimmern beidseits (Pritschen, Medizin),
 * Treppenhaus im Westfluegel, Keller (Leichenhalle) voller Pilzbewuchs mit zwei Spawnern, Parkplatz mit Wracks
 * und Krankenwagen davor. Verfall: fehlende Scheiben, Loecher in Decken und Waenden, Moos, Ranken.
 * Loot {@code hospital}.
 */
final class StructureHospital {

    private StructureHospital() {}

    static final int U0 = -18, U1 = 18, V0 = -7, V1 = 7;

    static void plan(StructurePlan p) {
        p.values[0] = 30 + p.rand.nextInt(50); // Verfall in Prozent
        // Zimmer: je Seite 7 Zimmer (Breite 5) zwischen den Zwischenwaenden bei u = -15, -10, ..., 15; Westfluegel = Treppenhaus
        for (int floor = 0; floor < 3; floor++) {
            int y = floor * 4 + 1;
            for (int side = -1; side <= 1; side += 2) {
                for (int room = 0; room < 6; room++) {
                    int uc = -12 + room * 5; // Zimmermitte
                    if (uc == -12 && side < 0) continue; // Treppenhaus
                    float r = p.rand.nextFloat();
                    int v = side < 0 ? -6 : 6;
                    if (r < 0.35F) p.loot(uc + 1, y, v, side < 0 ? Direction.SOUTH : Direction.NORTH, p.rand.nextFloat() < 0.5F ? Container.SUPPLY_CRATE : Container.CHEST);
                    else if (r < 0.5F) p.loot(uc - 1, y, v, Direction.EAST, Container.BARREL);
                    p.part(0, uc, side, floor, p.rand.nextInt(3), 0); // Zimmerinhalt: 0/1 Betten, 2 Buero
                }
            }
        }
        // Keller: Leichenhalle, Kisten, Spawner
        p.loot(10, -3, -5, Direction.SOUTH, Container.MILITARY_CRATE);
        p.loot(-4, -3, 5, Direction.NORTH, Container.CHEST);
        p.loot(14, -3, 5, Direction.NORTH, Container.SUPPLY_CRATE);
        // Bewohner: Flure voller Blindhuete und Sporlinge, im Keller ein Pilzkoloss, Lauerer auf dem Parkplatz
        for (int floor = 0; floor < 3; floor++) {
            for (int i = 1 + p.rand.nextInt(2); i > 0; i--) p.mob(-10 + p.rand.nextInt(26), floor * 4 + 1, -1 + p.rand.nextInt(3), p.infected(3, 1, 4, 0));
        }
        p.mob(-6 + p.rand.nextInt(5), -3, 0, p.rand.nextFloat() < 0.4F ? StructureMobs.MYCOBRUTE : StructureMobs.BLINDCAP);
        p.mob(6, -3, 1, StructureMobs.BLINDCAP);
        for (int i = p.rand.nextInt(3); i > 0; i--) p.mob(-12 + p.rand.nextInt(24), 1, 13 + p.rand.nextInt(3), p.infected(2, 3, 0, 0));
        p.spawner(2, -3, 0, 2);
        p.spawner(12, -3, -2, 0);
        // Parkplatz: Wracks
        int cars = 2 + p.rand.nextInt(3);
        for (int i = 0; i < cars; i++) p.part(1, -14 + i * 7 + p.rand.nextInt(3), 11 + p.rand.nextInt(2), p.rand.nextInt(10), p.rand.nextFloat() < 0.3F ? 1 : 0, 0);
        p.part(2, 8 + p.rand.nextInt(4), 11, 0, 0, 0); // Krankenwagen
    }

    static void paint(Frame f, StructurePlan p, CityPalette pal) {
        double decay = p.values[0] / 100.0;
        BlockState air = Blocks.AIR.defaultBlockState();
        // ── Keller (y -4..-1) ──
        f.fill(U0, -4, V0, U1, -4, V1, pal.concreteGray);
        f.walls(U0, -3, V0, U1, -1, V1, pal.vConcreteGray);
        f.fill(U0 + 1, -3, V0 + 1, U1 - 1, -1, V1 - 1, air);
        f.fill(U0, 0, V0, U1, 0, V1, pal.tileLab);                 // Erdgeschossboden
        // Leichenhalle: Tische, Pilzbewuchs, Leuchtpilze, Ranken
        for (int u = -14; u <= 14; u += 4) {
            for (int v : new int[] {-4, 4}) {
                f.set(u, -3, v, pal.concreteSmooth);
                f.set(u + 1, -3, v, pal.concreteSmooth);
            }
        }
        for (int u = U0 + 1; u <= U1 - 1; u++) {
            for (int v = V0 + 1; v <= V1 - 1; v++) {
                double h = f.unit(0xCE11L, u, -3, v);
                if (h < 0.08) f.setIfAir(u, -3, v, pal.glowCap);
                else if (h < 0.2) f.setIfAir(u, -3, v, pal.mossCushion);
                else if (h < 0.26) f.setIfAir(u, -1, v, pal.vine(Direction.UP));
                if (h > 0.97) f.fill(u, -3, v, u, -1, v, pal.fungalStem);
            }
        }
        f.fill(16, -3, -5, 17, -2, -3, pal.fungalCap);
        // Leiter Keller -> EG im Treppenhaus
        f.air(-17, 0, -2);
        StructurePainter.ladder(f, -17, -3, 0, -2, Direction.WEST, pal);

        // ── Geschosse ──
        for (int floor = 0; floor < 3; floor++) {
            int y0 = floor * 4 + 1;
            if (floor > 0) {
                // Geschossdecke mit Loechern
                for (int u = U0; u <= U1; u++) for (int v = V0; v <= V1; v++) {
                    boolean hole = f.unit(0xF100L + floor, u, 0, v) < decay * 0.12 && Math.abs(v) <= 5 && Math.abs(u) <= 16;
                    f.set(u, y0 - 1, v, hole ? air : pal.concreteTile);
                }
            }
            // Aussenwaende: Betonweiss mit Rissen, Fensterband y0+1..y0+2
            for (int u = U0; u <= U1; u++) {
                for (int v = V0; v <= V1; v++) {
                    boolean edge = u == U0 || u == U1 || v == V0 || v == V1;
                    if (!edge) {
                        f.fill(u, y0, v, u, y0 + 2, v, air);
                        continue;
                    }
                    boolean pier = u % 3 == 0 || u == U0 || u == U1 || v == V0 + 7 && (u == -1 || u == 1 || u == 0);
                    for (int y = y0; y <= y0 + 2; y++) {
                        boolean window = !pier && y >= y0 + 1 && !(v == V1 && Math.abs(u) <= 1 && floor == 0);
                        BlockState b;
                        if (window) {
                            b = f.unit(0x61A5L, u, y, v) < decay * 0.6 ? air : pal.glassPane;
                        } else {
                            double c = f.unit(0xC2ACL, u, y, v);
                            b = c < decay * 0.25 ? pal.concreteCracked : c < decay * 0.32 ? pal.concreteMossy : pal.concreteWhite;
                            if (floor == 2 && f.unit(0x4011L, u, y, v) < decay * 0.15) b = air; // Loecher oben
                        }
                        f.set(u, y, v, b);
                    }
                }
            }
            // Zwischenwaende (u = -15, -10, ..., 15) beidseits des Flurs (v -1..1), Tuerluecke am Flur
            for (int u = -15; u <= 15; u += 5) {
                for (int v = V0 + 1; v <= V1 - 1; v++) {
                    if (Math.abs(v) <= 1) continue;
                    f.fill(u, y0, v, u, y0 + 2, v, pal.terracottaWhite);
                }
            }
            for (int v : new int[] {-2, 2}) {
                for (int u = U0 + 1; u <= U1 - 1; u++) {
                    boolean door = (u + 12) % 5 == 0 || Math.abs(u) > 16;
                    if (!door) f.fill(u, y0, v, u, y0 + 2, v, f.unit(0x7A11L, u, y0, v) < decay * 0.2 ? air : pal.vConcreteWhite);
                }
            }
            // Flur: Fliesen, Moos
            for (int u = U0 + 1; u <= U1 - 1; u++) {
                for (int v = -1; v <= 1; v++) {
                    if (f.unit(0xF10EL, u, y0, v) < decay * 0.3) f.setIfAir(u, y0, v, pal.mossCarpet);
                }
            }
        }
        // Dach (y12, direkt ueber dem 2. OG) und Bruestung
        for (int u = U0; u <= U1; u++) for (int v = V0; v <= V1; v++) {
            boolean edge = u == U0 || u == U1 || v == V0 || v == V1;
            f.set(u, 12, v, f.unit(0xD00FL, u, 12, v) < decay * 0.5 ? pal.concreteMossy : pal.concreteGray);
            if (edge && f.unit(0xB2E5L, u, 13, v) > decay * 0.3) f.set(u, 13, v, pal.concreteSmooth);
            StructurePainter.overgrow(f, u, 12, v, decay * 0.4, pal);
        }
        f.fill(-3, 13, -3, -1, 15, -1, pal.concreteGray);         // Aufzugs-/Lueftungsaufbau
        f.set(-2, 16, -2, pal.vConcreteBlack);
        // Treppenhaus Westfluegel (u -17..-14, v -6..-2): Lauf A v=-5 nach Westen (EG -> 1. OG, Podest bei u -17),
        // Lauf B v=-3 nach Osten (1. OG -> 2. OG, Podest bei u -14); die Podeste grenzen an den Flur (v -1)
        f.fill(-17, 1, -6, -14, 11, -2, air);
        for (int i = 0; i < 4; i++) {
            f.set(-14 - i, 1 + i, -5, CityPalette.stairs(Blocks.STONE_BRICK_STAIRS, Direction.WEST, false));
            f.fill(-14 - i, 0, -5, -14 - i, i, -5, pal.concrete);
            f.set(-17 + i, 5 + i, -3, CityPalette.stairs(Blocks.STONE_BRICK_STAIRS, Direction.EAST, false));
            f.fill(-17 + i, 4, -3, -17 + i, 4 + i, -3, pal.concrete);
        }
        f.fill(-17, 4, -6, -17, 4, -2, pal.concreteTile);
        f.fill(-14, 8, -6, -14, 8, -2, pal.concreteTile);
        for (int y = 1; y <= 11; y++) f.set(-16, y, -4, pal.postStone); // Pfeiler zwischen den Laeufen
        // Eingang: Tuer, Vordach, H-Schild, Vorplatz
        f.fill(-1, 1, V1, 1, 2, V1, air);
        f.fill(-3, 4, V1 + 1, 3, 4, V1 + 3, pal.concreteGray);
        for (int u : new int[] {-3, 3}) f.fill(u, 1, V1 + 3, u, 3, V1 + 3, pal.postStone);
        for (int u = -2; u <= 2; u++) for (int y = 5; y <= 7; y++) {
            boolean red = Math.abs(u) == 2 || y == 6;
            f.set(u, y, V1 + 1, red ? pal.terracottaRed : pal.concreteWhite);
        }
        f.fill(-6, 0, V1 + 1, 6, 0, V1 + 3, pal.sidewalk);
        // Parkplatz v 9..16: Asphalt, Markierungen, Bordstein, Wracks (Teile 1), Krankenwagen (Teil 2)
        for (int u = -16; u <= 16; u++) {
            for (int v = V1 + 2; v <= V1 + 9; v++) {
                double h = f.unit(0xA5FL, u, 0, v);
                BlockState top = h < decay * 0.3 ? (h < decay * 0.1 ? pal.grassBlock : pal.gravel) : (u % 4 == 0 && v > V1 + 3 && v < V1 + 8 ? pal.lineWhite : pal.asphalt);
                f.set(u, 0, v, top);
                if (top == pal.grassBlock && f.unit(0xA60L, u, 1, v) < 0.5) f.setIfAir(u, 1, v, pal.fern);
            }
            f.set(u, 0, V1 + 10, pal.curb);
        }
        for (StructurePlan.Part part : p.parts) {
            switch (part.kind()) {
                case 0 -> room(f, part, pal);
                case 1 -> StructurePainter.car(f, part.u(), part.v(), true, 4, part.a(), part.b() == 1, pal);
                case 2 -> {
                    StructurePainter.car(f, part.u(), part.v(), true, 5, 2, false, pal);
                    for (int i = 1; i <= 3; i++) f.set(part.u() + i, 2, part.v(), pal.terracottaRed); // roter Streifen
                    f.set(part.u() + 2, 4, part.v(), pal.glassRed);
                }
                default -> { }
            }
        }
        // Aussen: Ranken und Moos
        for (int u = U0; u <= U1; u++) {
            StructurePainter.vineWall(f, u, 11, V0, Direction.NORTH, 8, decay * 0.5, pal);
            StructurePainter.vineWall(f, u, 11, V1, Direction.SOUTH, 8, decay * 0.5, pal);
        }
        for (int v = V0; v <= V1; v++) {
            StructurePainter.vineWall(f, U0, 11, v, Direction.WEST, 8, decay * 0.5, pal);
            StructurePainter.vineWall(f, U1, 11, v, Direction.EAST, 8, decay * 0.5, pal);
        }
    }

    /** Zimmer: {@code u} Mitte, {@code v} Seite (-1 Nord/1 Sued), {@code a} Geschoss, {@code b} Inhalt (0/1 Betten, 2 Buero). */
    private static void room(Frame f, StructurePlan.Part part, CityPalette pal) {
        int y = part.a() * 4 + 1;
        int vOuter = part.v() < 0 ? -6 : 6;
        int vIn = part.v() < 0 ? -4 : 4;
        if (part.b() == 2) {
            f.setIfOpen(part.u() - 1, y, vOuter, pal.bookshelf);
            f.setIfOpen(part.u(), y, vOuter, pal.craftingTable);
            f.setIfOpen(part.u(), y, vIn, CityPalette.stairs(Blocks.SPRUCE_STAIRS, part.v() < 0 ? Direction.NORTH : Direction.SOUTH, false));
        } else {
            int vCot = part.v() < 0 ? -6 : 5; // zwei Platten vom Aussenrand nach innen
            StructurePainter.cot(f, part.u() - 2, y, vCot, false, pal);
            if (part.b() == 1) StructurePainter.cot(f, part.u() + 1, y, vCot, false, pal);
            f.setIfOpen(part.u(), y, vOuter, pal.flowerPot);
        }
    }
}
