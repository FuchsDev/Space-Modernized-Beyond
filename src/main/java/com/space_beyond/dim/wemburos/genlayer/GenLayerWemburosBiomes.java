package com.space_beyond.dim.wemburos.genlayer;

import com.space_m.dim.legacy.GenLayer;
import com.space_m.dim.legacy.IntCache;
import com.space_beyond.dim.wemburos.biome.BiomeGenBaseWemburos;

/**
 * Grobe Biomwahl von Wemburos (Gewichte je Zelle): Ebene 3, Badlands 2, Goldhochland 2, Kristallfelder 2, Riss 1,
 * Duskwood-Wald 3, Onyx-Stachelfeld 2, Goldduenen 2, Chorus-Oednis 2, Caldera 1.
 */
public class GenLayerWemburosBiomes extends GenLayer {

    private static final BiomeGenBaseWemburos[] POOL = {
            BiomeGenBaseWemburos.violetPlains, BiomeGenBaseWemburos.violetPlains, BiomeGenBaseWemburos.violetPlains,
            BiomeGenBaseWemburos.onyxBadlands, BiomeGenBaseWemburos.onyxBadlands,
            BiomeGenBaseWemburos.gildedHighlands, BiomeGenBaseWemburos.gildedHighlands,
            BiomeGenBaseWemburos.crystalFields, BiomeGenBaseWemburos.crystalFields,
            BiomeGenBaseWemburos.rift,
            BiomeGenBaseWemburos.duskwoodForest, BiomeGenBaseWemburos.duskwoodForest, BiomeGenBaseWemburos.duskwoodForest,
            BiomeGenBaseWemburos.onyxSpires, BiomeGenBaseWemburos.onyxSpires,
            BiomeGenBaseWemburos.goldenDunes, BiomeGenBaseWemburos.goldenDunes,
            BiomeGenBaseWemburos.chorusWastes, BiomeGenBaseWemburos.chorusWastes,
            BiomeGenBaseWemburos.caldera };

    public GenLayerWemburosBiomes(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = IntCache.getIntCache(width * depth);
        for (int k = 0; k < depth; ++k) {
            for (int i = 0; i < width; ++i) {
                initChunkSeed(x + i, z + k);
                dest[i + k * width] = POOL[nextInt(POOL.length)].biomeID;
            }
        }
        return dest;
    }
}
