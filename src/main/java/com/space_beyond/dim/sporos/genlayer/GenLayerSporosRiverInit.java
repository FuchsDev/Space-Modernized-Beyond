package com.space_beyond.dim.sporos.genlayer;

import com.space_m.dim.legacy.GenLayer;

/**
 * Rauschfeld fuer Fluesse (1.7.10 {@code GenLayerRiverInit}: je Zelle {@code nextInt(299999) + 2}); nach dem
 * Vergroessern zeichnet {@code GenLayerRiver} die Flusslinien an den Grenzen gerader/ungerader Werte.
 */
public class GenLayerSporosRiverInit extends GenLayer {

    public GenLayerSporosRiverInit(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = new int[width * depth];

        for (int dz = 0; dz < depth; dz++) {
            for (int dx = 0; dx < width; dx++) {
                this.initChunkSeed(dx + x, dz + z);
                dest[dx + dz * width] = this.nextInt(299999) + 2;
            }
        }
        return dest;
    }
}
