package com.space_beyond.dim.sporos.genlayer;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.legacy.GenLayer;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;

/**
 * Grundschicht der Sporos-Biome (Aufbau wie {@code GenlayerTektoBiomes}): je Zelle ein Zufallsbiom, gewichtet
 * Ebene 4 : Wald 4 : Sumpf 2 : Pilzhain 2 : Hochland 2 : Sporenfeld 1 : See 2 (R2b). Kuesten entstehen um die Seen
 * ({@link GenLayerSporosShore}), Fluesse spaeter ueber {@link GenLayerSporosRiverMix}.
 */
public class GenLayerSporosBiomes extends GenLayer {

    private static final BiomeGenBaseCelestial[] BIOMES = {
            BiomeGenBaseSporos.overgrownPlains, BiomeGenBaseSporos.overgrownPlains, BiomeGenBaseSporos.overgrownPlains, BiomeGenBaseSporos.overgrownPlains,
            BiomeGenBaseSporos.mossForest, BiomeGenBaseSporos.mossForest, BiomeGenBaseSporos.mossForest, BiomeGenBaseSporos.mossForest,
            BiomeGenBaseSporos.swamp, BiomeGenBaseSporos.swamp,
            BiomeGenBaseSporos.fungalGrove, BiomeGenBaseSporos.fungalGrove,
            BiomeGenBaseSporos.highland, BiomeGenBaseSporos.highland,
            BiomeGenBaseSporos.sporeField,
            BiomeGenBaseSporos.lake, BiomeGenBaseSporos.lake
    };

    public GenLayerSporosBiomes(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = new int[width * depth];

        for (int dz = 0; dz < depth; dz++) {
            for (int dx = 0; dx < width; dx++) {
                this.initChunkSeed(dx + x, dz + z);
                dest[dx + dz * width] = BIOMES[this.nextInt(BIOMES.length)].biomeID;
            }
        }
        return dest;
    }
}
