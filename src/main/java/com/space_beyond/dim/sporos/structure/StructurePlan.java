package com.space_beyond.dim.sporos.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.space_beyond.dim.sporos.city.CellPlan.Container;
import com.space_beyond.dim.sporos.structure.SporosStructures.Structure;

import net.minecraft.core.Direction;

/**
 * Deterministische Planung einer Wildnis-Struktur: Grundhoehe, Beutestellen und Spawner in lokalen Koordinaten
 * (s. {@link Frame}), dazu der Zufallsgenerator-Stand fuer die Maler. Die Maler ({@link StructurePainter}) tragen
 * die Beutestellen ein, waehrend sie planen - deshalb wird jede Struktur einmal "trocken" geplant
 * ({@link StructurePainter#plan}) und das Ergebnis je Thread zwischengespeichert.
 */
public final class StructurePlan {

    /** Beutestelle lokal: Behaelter bei (u, y, v), Blickrichtung lokal, Loot-Table-Name. */
    public record Loot(int u, int y, int v, Direction facing, Container container, String table) { }

    /** Spawner lokal mit Mob-Wahl: 0 Lurker, 1 Sporeling, 2 Blindcap, 3 Mycobrute. */
    public record Spawner(int u, int y, int v, int mob) { }

    /** Bewohner lokal: Art nach {@link StructureMobs} (Infizierte, Siedler, Pferde). */
    public record MobSpot(int u, int y, int v, int kind) { }

    /** Bauteil der Planung (Bedeutung von {@code kind} und {@code a..c} je Struktur, s. die Maler). */
    public record Part(int kind, int u, int v, int a, int b, int c) { }

    public final Structure s;
    public final int base;
    public final Random rand;
    public final List<Loot> loot = new ArrayList<>();
    public final List<Spawner> spawners = new ArrayList<>();
    public final List<Part> parts = new ArrayList<>();
    public final List<MobSpot> mobs = new ArrayList<>();
    /** Freie Zahlenwerte der Planung (z.B. Masthoehe), je Struktur belegt. */
    public final int[] values = new int[8];

    StructurePlan(Structure s, int base) {
        this.s = s;
        this.base = base;
        this.rand = new Random(s.seed());
        StructurePainter.plan(this);
    }

    public void loot(int u, int y, int v, Direction facing, Container container) {
        loot.add(new Loot(u, y, v, facing, container, s.type().table));
    }

    public void loot(int u, int y, int v, Direction facing, Container container, String table) {
        loot.add(new Loot(u, y, v, facing, container, table));
    }

    public void spawner(int u, int y, int v, int mob) {
        spawners.add(new Spawner(u, y, v, mob));
    }

    public void mob(int u, int y, int v, int kind) {
        mobs.add(new MobSpot(u, y, v, kind));
    }

    /** Infizierter nach Zufall: Sporling/Lauerer/Blindhut (Gewichte), selten Pilzkoloss. */
    public int infected(int sporeling, int lurker, int blindcap, int brute) {
        int r = rand.nextInt(sporeling + lurker + blindcap + brute);
        if (r < sporeling) return StructureMobs.SPORELING;
        if ((r -= sporeling) < lurker) return StructureMobs.LURKER;
        if ((r -= lurker) < blindcap) return StructureMobs.BLINDCAP;
        return StructureMobs.MYCOBRUTE;
    }

    public void part(int kind, int u, int v, int a, int b, int c) {
        parts.add(new Part(kind, u, v, a, b, c));
    }
}
