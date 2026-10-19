package com.space_beyond.dim.sporos.genlayer;

import com.space_m.dim.legacy.GenLayer;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;

/**
 * Kuestenschicht (Aufbau wie 1.7.10 {@code GenLayerShore}): jede Landzelle, die an einen See grenzt, wird zur
 * Kueste; Suempfe behalten ihr Ufer (Sumpf grenzt direkt ans Wasser). Wird nach vier Vergroesserungen
 * eingesetzt (Zellen 16 Bloecke), zwei weitere Zoomstufen verwischen den Rand.
 */
public class GenLayerSporosShore extends GenLayer {

    public GenLayerSporosShore(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int w, int h) {
        int pw = w + 2, ph = h + 2;
        int[] in = this.parent.getInts(x - 1, z - 1, pw, ph);
        int[] out = new int[w * h];
        int lake = BiomeGenBaseSporos.lake.biomeID;
        int swamp = BiomeGenBaseSporos.swamp.biomeID;
        int coast = BiomeGenBaseSporos.coast.biomeID;

        for (int dz = 0; dz < h; dz++) {
            for (int dx = 0; dx < w; dx++) {
                int c = in[dx + 1 + (dz + 1) * pw];
                if (c != lake && c != swamp) {
                    int n = in[dx + 1 + dz * pw], s = in[dx + 1 + (dz + 2) * pw];
                    int e = in[dx + 2 + (dz + 1) * pw], wv = in[dx + (dz + 1) * pw];
                    if (n == lake || s == lake || e == lake || wv == lake) c = coast;
                }
                out[dx + dz * w] = c;
            }
        }
        return out;
    }
}
