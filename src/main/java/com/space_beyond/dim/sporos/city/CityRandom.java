package com.space_beyond.dim.sporos.city;

/**
 * Zustandslose Zufallsfunktionen des Staedtegenerators. Jede Entscheidung, die einen einzelnen Block betrifft, haengt
 * nur von Seed und Weltkoordinaten ab - nie von der Reihenfolge, in der Chunks generiert werden. Deshalb kann jeder
 * Chunk seinen Ausschnitt eines Gebaeudes allein setzen und die Teile passen an den Chunk-Grenzen zusammen.
 */
public final class CityRandom {

    private CityRandom() {}

    public static long mix(long seed, long a, long b) {
        long h = seed;
        h ^= a * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27) * 0xBF58476D1CE4E5B9L;
        h ^= b * 0x94D049BB133111EBL;
        h = Long.rotateLeft(h, 31) * 0x9E3779B97F4A7C15L;
        return h ^ (h >>> 29);
    }

    public static long hash(long seed, int x, int y, int z) {
        long h = mix(seed, x, z);
        h ^= (long) y * 0xD6E8FEB86659FD93L;
        h *= 0xBF58476D1CE4E5B9L;
        return h ^ (h >>> 31);
    }

    /** Gleichverteilt in [0, 1). */
    public static double unit(long seed, int x, int y, int z) {
        return (hash(seed, x, y, z) >>> 11) * 0x1.0p-53;
    }

    public static int range(long seed, int x, int y, int z, int bound) {
        return (int) ((hash(seed, x, y, z) >>> 33) % bound);
    }

    /** Glattes Wertrauschen in [0, 1) mit Zellgroesse {@code scale} (bilinear, Smoothstep). */
    public static double noise(long seed, int x, int z, int scale) {
        int gx = Math.floorDiv(x, scale), gz = Math.floorDiv(z, scale);
        double fx = (x - gx * scale) / (double) scale, fz = (z - gz * scale) / (double) scale;
        fx = fx * fx * (3 - 2 * fx);
        fz = fz * fz * (3 - 2 * fz);
        double a = unit(seed, gx, 0, gz), b = unit(seed, gx + 1, 0, gz);
        double c = unit(seed, gx, 0, gz + 1), d = unit(seed, gx + 1, 0, gz + 1);
        return (a + (b - a) * fx) * (1 - fz) + (c + (d - c) * fx) * fz;
    }

    public static double smoothstep(double lo, double hi, double v) {
        if (v <= lo) return 0;
        if (v >= hi) return 1;
        double t = (v - lo) / (hi - lo);
        return t * t * (3 - 2 * t);
    }

    public static double clamp01(double v) {
        return v < 0 ? 0 : v > 1 ? 1 : v;
    }
}
