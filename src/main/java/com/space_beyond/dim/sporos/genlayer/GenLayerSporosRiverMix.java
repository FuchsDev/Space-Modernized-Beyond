package com.space_beyond.dim.sporos.genlayer;

import com.space_m.dim.legacy.GenLayer;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;

/**
 * Mischt Biome und Flusslinien (wie {@code GenLayerTektoRiverMix}): Flusskanten werden ausser im Sumpf zum
 * Sporos-Fluss; der Sumpf bleibt ein zusammenhaengendes Feuchtgebiet.
 */
public class GenLayerSporosRiverMix extends GenLayer {

    private final GenLayer biomeChain;
    private final GenLayer riverChain;

    public GenLayerSporosRiverMix(long seed, GenLayer biomeChain, GenLayer riverChain) {
        super(seed);
        this.biomeChain = biomeChain;
        this.riverChain = riverChain;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        this.biomeChain.initWorldGenSeed(seed);
        this.riverChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int x, int z, int width, int length) {
        int[] biomes = this.biomeChain.getInts(x, z, width, length);
        int[] rivers = this.riverChain.getInts(x, z, width, length);
        int[] out = new int[width * length];

        for (int i = 0; i < width * length; ++i) {
            if (rivers[i] == RIVER && biomes[i] != BiomeGenBaseSporos.swamp.biomeID && biomes[i] != BiomeGenBaseSporos.lake.biomeID) {
                out[i] = BiomeGenBaseSporos.river.biomeID;
            } else {
                out[i] = biomes[i];
            }
        }

        return out;
    }
}
