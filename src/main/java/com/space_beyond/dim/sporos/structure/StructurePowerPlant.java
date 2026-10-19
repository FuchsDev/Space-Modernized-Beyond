package com.space_beyond.dim.sporos.structure;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.city.CityPalette;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Kraftwerk: Turbinenhalle 29 x 15 (u -14..14, v -8..6, 10 hoch) mit drei Generatoren, Dampfrohren zum Dach und
 * Leitstand auf einer Empore; Schornstein (u 20), Umspannfeld mit Trafos und Masten (u 17..25, v 2..10), zwei Tanks
 * (u -24..-16), Aschehalde, Kuehlbecken (v 9..16) und Lagerschuppen (v -13..-9). Verfall: Scheiben fehlen, Moos,
 * Ranken, Pilze an der Halde. Loot {@code power_plant} (Leitstand, Schuppen) und {@code industrial} der Stadt (Halle).
 */
final class StructurePowerPlant {

    private StructurePowerPlant() {}

    static void plan(StructurePlan p) {
        p.values[0] = 30 + p.rand.nextInt(50);  // Verfall in Prozent
        p.values[1] = 22 + p.rand.nextInt(8);   // Schornsteinhoehe
        // Leitstand (Empore u 10..13, v -7..-3, Boden y5)
        p.loot(12, 6, -6, Direction.WEST, Container.MILITARY_CRATE);
        p.loot(10, 6, -4, Direction.EAST, Container.SUPPLY_CRATE);
        p.loot(13, 6, -4, Direction.WEST, Container.CHEST);
        // Halle: Werkzeug an den Generatoren
        for (int g = -1; g <= 1; g++) {
            if (p.rand.nextFloat() < 0.6F) p.loot(g * 9 + 3, 1, 4, Direction.NORTH, Container.BARREL, "industrial");
            if (p.rand.nextFloat() < 0.4F) p.loot(g * 9 - 3, 1, -5, Direction.SOUTH, Container.SUPPLY_CRATE, "industrial");
        }
        // Schuppen (u 12..22, v -13..-9)
        int n = 3 + p.rand.nextInt(3);
        for (int i = 0; i < n; i++) p.loot(13 + i * 2, 1, -12, Direction.SOUTH, p.rand.nextFloat() < 0.6F ? Container.SUPPLY_CRATE : Container.BARREL);
        p.loot(21, 1, -10, Direction.WEST, Container.MILITARY_CRATE);
        // Infizierte Arbeiter im Keller der Halle? Nein - in der Halle selbst, hinter dem dritten Generator
        if (p.rand.nextFloat() < 0.7F) p.spawner(-12, 1, 5, p.rand.nextFloat() < 0.5F ? 0 : 3);
        // Bewohner: infizierte Arbeiter in der Halle und auf dem Hof, selten ein Pilzkoloss bei den Tanks
        for (int i = 2 + p.rand.nextInt(3); i > 0; i--) p.mob(-11 + p.rand.nextInt(23), 1, 2 + p.rand.nextInt(3), p.infected(4, 3, 2, 0));
        for (int i = 1 + p.rand.nextInt(2); i > 0; i--) p.mob(-4 + p.rand.nextInt(10), 1, -12 + p.rand.nextInt(3), p.infected(4, 2, 0, 0));
        if (p.rand.nextFloat() < 0.3F) p.mob(-20, 1, 0, StructureMobs.MYCOBRUTE);
        // Wrack auf dem Hof
        p.part(1, -8 + p.rand.nextInt(6), -12, p.rand.nextInt(10), p.rand.nextFloat() < 0.4F ? 1 : 0, 0);
    }

    static void paint(Frame f, StructurePlan p, CityPalette pal) {
        double decay = p.values[0] / 100.0;
        BlockState air = Blocks.AIR.defaultBlockState();
        // ── Hof: Beton/Kies, zugewachsen ──
        int r = p.s.type().radius;
        for (int u = -r; u <= r; u++) for (int v = -r; v <= r; v++) {
            if (!f.contains(u, v)) continue;
            double h = f.unit(0x0FF1L, u, 0, v);
            BlockState top = h < 0.3 + decay * 0.3 ? (h < 0.1 ? pal.gravel : pal.grassBlock) : h < 0.55 ? pal.concrete : pal.concreteTile;
            f.set(u, 0, v, top);
            if (top == pal.grassBlock && f.unit(0x0FF2L, u, 1, v) < 0.4) f.setIfAir(u, 1, v, pal.fern);
        }
        // ── Turbinenhalle ──
        f.fill(-14, 0, -8, 14, 0, 6, pal.concreteTile);
        f.walls(-14, 1, -8, 14, 10, 6, pal.concrete);
        f.fill(-13, 1, -7, 13, 10, 5, air);
        f.walls(-14, 1, -8, 14, 2, 6, pal.brickConcrete);
        for (int u = -14; u <= 14; u++) {
            for (int v : new int[] {-8, 6}) {
                boolean pier = u % 5 == 0 || Math.abs(u) == 14;
                for (int y = 4; y <= 8; y++) {
                    if (pier) f.set(u, y, v, pal.concreteGray);
                    else f.set(u, y, v, f.unit(0x61A5L, u, y, v) < decay * 0.5 ? air : pal.glassPane);
                }
                if (!pier && f.unit(0xC2ACL, u, 3, v) < decay * 0.3) f.set(u, 3, v, pal.concreteCracked);
            }
        }
        for (int v = -7; v <= 5; v++) {
            if (v % 3 == 0) continue;
            f.fill(-14, 5, v, -14, 7, v, pal.glassPane);
        }
        f.fill(-1, 1, 6, 1, 3, 6, air);                            // grosses Tor Sued
        f.fill(14, 1, 0, 14, 2, 1, air);                            // Tuer Ost zum Umspannfeld
        for (int u = -14; u <= 14; u++) for (int v = -8; v <= 6; v++) {
            boolean sky = Math.abs(v + 1) <= 1 && u % 4 != 0;
            BlockState roof = sky ? (f.unit(0x5C1L, u, 11, v) < decay * 0.4 ? air : pal.glass)
                    : f.unit(0xD00FL, u, 11, v) < decay * 0.4 ? pal.concreteMossy : pal.concreteGray;
            f.set(u, 11, v, roof);
            boolean edge = u == -14 || u == 14 || v == -8 || v == 6;
            if (edge) f.set(u, 12, v, pal.concreteSmooth);
            else StructurePainter.overgrow(f, u, 11, v, decay * 0.3, pal);
        }
        // Generatoren (3 Stueck, 5 x 3 x 3) mit Dampfrohr zum Dach
        for (int g = -1; g <= 1; g++) {
            int uc = g * 9;
            f.fill(uc - 2, 1, -2, uc + 2, 1, 0, pal.polishedBlackstone);
            f.fill(uc - 2, 2, -2, uc + 2, 3, 0, pal.vConcreteGray);
            f.fill(uc - 2, 4, -2, uc + 2, 4, 0, pal.armSlab);
            f.set(uc - 2, 2, -3, pal.glassRed);
            f.set(uc + 2, 2, -3, pal.glassLime);
            f.fill(uc, 4, -1, uc, 10, -1, pal.andesiteWall);
            f.fill(uc - 3, 2, -1, uc - 3, 2, -1, pal.andesiteWall);
            f.fill(uc + 3, 2, -1, uc + 3, 2, -1, pal.andesiteWall);
            f.set(uc, 1, 2, pal.vConcreteBlack);                     // Schaltkasten
            f.set(uc, 2, 2, pal.glassYellow);
        }
        // Rohrbruecke entlang der Nordwand
        f.fill(-13, 8, -7, 13, 8, -7, pal.andesiteWall);
        // Empore/Leitstand (u 10..13, v -7..-3): Boden y5, Gelaender, Konsolen, Leiter
        f.fill(9, 5, -7, 13, 5, -3, pal.concreteSmooth);
        for (int u = 9; u <= 13; u++) f.set(u, 6, -3, pal.oakFence);
        for (int v = -7; v <= -3; v++) f.set(9, 6, v, pal.oakFence);
        f.air(9, 6, -5);
        f.fill(10, 6, -7, 13, 6, -7, pal.vConcreteBlack);
        f.fill(10, 7, -7, 13, 7, -7, pal.glassLime);
        f.set(11, 6, -5, pal.craftingTable);
        f.set(13, 6, -6, pal.torch);
        for (int u = 10; u <= 13; u++) f.set(u, 4, -7, pal.concreteSmooth);
        f.fill(9, 1, -7, 9, 4, -7, pal.concreteGray);
        f.fill(9, 1, -5, 9, 4, -5, pal.concreteGray);               // Stuetze hinter der Leiter
        StructurePainter.ladder(f, 8, 1, 5, -5, Direction.EAST, pal);
        // Innen: Oelflecken, Moos, Pilze
        for (int u = -13; u <= 13; u++) for (int v = -7; v <= 5; v++) {
            double h = f.unit(0x01L, u, 0, v);
            if (h < 0.05) f.set(u, 0, v, pal.vConcreteBlack);
            else if (h < 0.05 + decay * 0.2) f.set(u, 0, v, pal.concreteCracked);
            if (f.unit(0x02L, u, 1, v) < decay * 0.08) f.setIfAir(u, 1, v, pal.mossCarpet);
            else if (f.unit(0x03L, u, 1, v) < decay * 0.02) f.setIfAir(u, 1, v, pal.glowCap);
        }
        // ── Schornstein (u 20, v -2): Sockel 3x3, Schaft 2x2 ──
        int ch = p.values[1];
        f.fill(19, 1, -3, 21, 6, -1, pal.brickConcrete);
        f.fill(19, 7, -3, 20, ch, -2, pal.brickConcrete);
        for (int y = 7; y <= ch; y++) {
            if (f.unit(0x5EL, 19, y, -3) < decay * 0.2) f.set(19, y, -3, pal.brickConcreteCracked);
            if (f.unit(0x5EL, 20, y, -2) < decay * 0.2) f.set(20, y, -2, pal.brickConcreteMossy);
        }
        f.fill(19, ch + 1, -3, 20, ch + 1, -2, pal.vConcreteBlack);
        f.fill(19, ch + 1, -3, 20, ch + 1, -2, pal.vConcreteBlack);
        for (int y = 2; y <= 6; y++) f.set(15, y, -2, pal.andesiteWall); // Abgasrohr Halle -> Schornstein
        f.fill(15, 7, -2, 18, 7, -2, pal.andesiteWall);
        // ── Umspannfeld (u 17..25, v 2..10): Kies, Zaun, Trafos, Masten ──
        f.fill(17, 0, 2, 25, 0, 10, pal.gravel);
        for (int u = 17; u <= 25; u++) for (int v = 2; v <= 10; v++) {
            boolean edge = u == 17 || u == 25 || v == 2 || v == 10;
            if (edge && !(u == 17 && (v == 5 || v == 6)) && f.unit(0xFE4CL, u, 1, v) > decay * 0.3) f.setIfOpen(u, 1, v, pal.oakFence);
        }
        for (int i = 0; i < 3; i++) {
            int u = 19 + i * 2, v = 7;
            f.fill(u, 1, v, u, 2, v + 1, pal.vConcreteGray);
            f.set(u, 3, v, pal.armSlabTop);
            f.set(u, 3, v + 1, pal.postBlack);
            f.set(u, 4, v + 1, pal.glassBlack);
        }
        for (int[] m : new int[][] {{18, 3}, {24, 3}, {24, 9}}) {
            f.fill(m[0], 1, m[1], m[0], 7, m[1], pal.postBlack);
            f.set(m[0] - 1, 7, m[1], pal.armSlabTop);
            f.set(m[0] + 1, 7, m[1], pal.armSlabTop);
        }
        // ── Tanks (u -24..-16): zwei Zylinder Radius 3, Hoehe 6 ──
        for (int t = 0; t < 2; t++) {
            int uc = -20, vc = t == 0 ? -4 : 4;
            for (int du = -3; du <= 3; du++) for (int dv = -3; dv <= 3; dv++) {
                if (du * du + dv * dv > 10) continue;
                boolean rim = du * du + dv * dv > 5;
                for (int y = 1; y <= 6; y++) {
                    BlockState b = rim ? (f.unit(0x7A4EL, uc + du, y, vc + dv) < decay * 0.25 ? pal.terracottaWhite : pal.concreteWhite) : air;
                    f.set(uc + du, y, vc + dv, b);
                }
                f.set(uc + du, 7, vc + dv, pal.concreteSmooth);
            }
            f.set(uc, 8, vc, pal.andesiteWall);
            for (int u = uc + 4; u <= -15; u++) f.set(u, 2, vc, pal.andesiteWall); // Rohr zur Halle
            f.set(-15, 1, vc, pal.andesiteWall);
        }
        // ── Aschehalde (u -20, v 11) ──
        for (int du = -4; du <= 4; du++) for (int dv = -3; dv <= 3; dv++) {
            double d = Math.sqrt(du * du + dv * dv * 1.5);
            int h = (int) Math.round(3.2 - d + f.unit(0xA5EL, -20 + du, 0, 11 + dv) * 0.8);
            for (int y = 1; y <= h; y++) f.setIfOpen(-20 + du, y, 11 + dv, f.unit(0xA5L, du, y, dv) < 0.6 ? pal.blackstone : pal.tuff);
            if (h >= 1 && f.unit(0xA6L, du, 0, dv) < 0.25) f.setIfAir(-20 + du, h + 1, 11 + dv, pal.glowCap);
        }
        // ── Kuehlbecken (u -8..8, v 9..16) ──
        for (int u = -8; u <= 8; u++) for (int v = 9; v <= 16; v++) {
            boolean rim = u == -8 || u == 8 || v == 9 || v == 16;
            if (rim) {
                f.set(u, 0, v, pal.stoneBricks);
                f.set(u, 1, v, f.unit(0xB00L, u, 1, v) < decay * 0.5 ? pal.stoneBricksMossy : pal.stoneBrickSlab);
            } else {
                f.set(u, -2, v, pal.stoneBricks);
                f.set(u, -1, v, pal.water);
                f.set(u, 0, v, pal.water);
                if (f.unit(0xB01L, u, 1, v) < 0.15) f.set(u, 1, v, pal.lilyPad);
                else f.set(u, 1, v, air);
            }
        }
        // ── Lagerschuppen (u 12..22, v -13..-9) ──
        f.fill(12, 0, -13, 22, 0, -9, pal.concrete);
        f.walls(12, 1, -13, 22, 3, -9, pal.concrete);
        f.fill(13, 1, -12, 21, 3, -10, air);
        f.fill(16, 1, -9, 18, 2, -9, air);                          // Tor Sued
        f.fill(11, 4, -14, 23, 4, -8, pal.concreteGray);
        for (int u = 11; u <= 23; u++) StructurePainter.overgrow(f, u, 4, -14 + (u % 7), decay * 0.5, pal);
        f.set(14, 1, -10, pal.craftingTable);
        f.fill(19, 1, -11, 20, 2, -11, pal.hayBale);
        // ── Wrack, Ranken ──
        for (StructurePlan.Part part : p.parts) {
            if (part.kind() == 1) StructurePainter.car(f, part.u(), part.v(), true, 4, part.a(), part.b() == 1, pal);
        }
        for (int u = -14; u <= 14; u++) {
            StructurePainter.vineWall(f, u, 10, -8, Direction.NORTH, 7, decay * 0.5, pal);
            StructurePainter.vineWall(f, u, 10, 6, Direction.SOUTH, 7, decay * 0.5, pal);
        }
        for (int v = -8; v <= 6; v++) {
            StructurePainter.vineWall(f, -14, 10, v, Direction.WEST, 7, decay * 0.5, pal);
            StructurePainter.vineWall(f, 14, 10, v, Direction.EAST, 7, decay * 0.5, pal);
        }
    }
}
