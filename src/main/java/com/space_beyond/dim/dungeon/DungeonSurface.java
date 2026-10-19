package com.space_beyond.dim.dungeon;

import java.util.Arrays;

import com.space_beyond.dim.sporos.city.CityRandom;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Gelaende ueber einem Dungeon (keine plattgewalzte Riesenflaeche): das Bauwerk wird einmal ueber den ganzen Grundriss
 * abgetastet ({@link DungeonDesign#trace}), daraus entstehen zwei Felder in lokalen Koordinaten (u, v):
 * <ul>
 *   <li>{@link #pad}: Abstand zur naechsten Spalte, in der ueber der Grundhoehe gebaut wird (Eingang, Pylonen) - nur
 *       dort und im Uebergang wird eingeebnet (Vorplatz).</li>
 *   <li>{@link #cover}: Mindesthoehe der Oberflaeche (relativ), damit alles Unterirdische {@link #COVER} Bloecke tief
 *       bleibt; mit Boeschung ({@link #SLOPE} je Block). Liegt das natuerliche Gelaende hoeher, bleibt es unberuehrt.</li>
 * </ul>
 */
public final class DungeonSurface {

    /** Mindestdeckung ueber dem hoechsten unterirdischen Block. */
    static final int COVER = 3;
    /** Gefaelle der Aufschuettung (Bloecke Hoehe je Block Abstand). */
    static final float SLOPE = 0.8F;
    /** Kein Deckungsbedarf. */
    static final float NONE = -1.0e9F;
    private static final float FAR = 1.0e9F;

    private final int span, size;
    private final float[] pad, cover;

    DungeonSurface(DungeonDesign design, DungeonPlan p) {
        span = design.radius() + PlanetDungeons.BLEND;
        size = 2 * span + 1;
        int[] top = new int[size * size];
        Arrays.fill(top, Integer.MIN_VALUE);
        long seed = p.d.seed();
        design.trace(new DungeonOut() {
            @Override
            public void set(int u, int y, int v, BlockState s) {
                int i = idx(u, v);
                if (i >= 0 && y > top[i]) top[i] = y;
            }

            @Override
            public BlockState get(int u, int y, int v) {
                return null;
            }

            @Override
            public double unit(long salt, int u, int y, int v) {
                return CityRandom.unit(seed ^ salt, u, y, v);
            }

            @Override public int uMin() { return -span; }
            @Override public int uMax() { return span; }
            @Override public int vMin() { return -span; }
            @Override public int vMax() { return span; }
        });
        pad = new float[top.length];
        cover = new float[top.length];
        for (int i = 0; i < top.length; i++) {
            pad[i] = top[i] >= 0 ? 0F : FAR;
            cover[i] = top[i] != Integer.MIN_VALUE && top[i] < 0 ? top[i] + COVER : NONE;
        }
        chamfer(pad, 1F, false);
        chamfer(cover, SLOPE, true);
    }

    private int idx(int u, int v) {
        if (u < -span || u > span || v < -span || v > span) return -1;
        return (u + span) * size + (v + span);
    }

    /** Abstand zum Vorplatz (Bloecke, ab {@code FAR} = keiner). */
    public float pad(int u, int v) {
        int i = idx(u, v);
        return i < 0 ? FAR : pad[i];
    }

    /** Mindesthoehe der Oberflaeche relativ zur Grundhoehe oder {@link #NONE}. */
    public float cover(int u, int v) {
        int i = idx(u, v);
        return i < 0 ? NONE : cover[i];
    }

    /** Zwei-Pass-Abstandstransformation: Minimum (+Kosten) bzw. Maximum (-Kosten) ueber die 8 Nachbarn. */
    private void chamfer(float[] f, float step, boolean max) {
        float diag = step * 1.4142F;
        for (int pass = 0; pass < 2; pass++) {
            int s = pass == 0 ? 1 : -1;
            int from = pass == 0 ? 0 : size - 1;
            for (int a = from; a >= 0 && a < size; a += s) {
                for (int b = from; b >= 0 && b < size; b += s) {
                    int i = a * size + b;
                    float best = f[i];
                    best = relax(f, best, a - s, b, step, max);
                    best = relax(f, best, a, b - s, step, max);
                    best = relax(f, best, a - s, b - s, diag, max);
                    best = relax(f, best, a - s, b + s, diag, max);
                    f[i] = best;
                }
            }
        }
    }

    private float relax(float[] f, float best, int a, int b, float cost, boolean max) {
        if (a < 0 || a >= size || b < 0 || b >= size) return best;
        float n = f[a * size + b];
        if (max) return n - cost < -64F ? best : Math.max(best, n - cost); // weit weg: kein Bedarf (bleibt NONE)
        return Math.min(best, n + cost);
    }
}
