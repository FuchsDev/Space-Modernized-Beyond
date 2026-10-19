package com.space_beyond.dim.wemburos;

import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_m.dim.WorldChunkManagerCelestial.BiomeGenLayers;
import com.space_m.dim.legacy.GenLayer;
import com.space_m.dim.legacy.GenLayerFuzzyZoom;
import com.space_m.dim.legacy.GenLayerSmooth;
import com.space_m.dim.legacy.GenLayerVoronoiZoom;
import com.space_m.dim.legacy.GenLayerZoom;
import com.space_beyond.dim.wemburos.biome.BiomeGenBaseWemburos;
import com.space_beyond.dim.wemburos.genlayer.GenLayerWemburosBiomes;
import com.space_beyond.dim.wemburos.structure.WemburosStructures;

/**
 * Planet Wemburos (eigene Erweiterung, zweiter Planet des Sporos-Systems): Generator-Anmeldung (Biomkette ohne Fluesse,
 * {@link ChunkProviderWemburos}, Meeresspiegel 63 ohne Meer) und Dekorations-Haken der Strukturen (Loot, Spawner,
 * Bewohner).
 */
public final class WorldProviderWemburos {

    private WorldProviderWemburos() {}

    public static void init() {
        WemburosBlocks.init();
        BiomeGenBaseWemburos.init();
        CelestialGenerators.register(new CelestialGenerators.BodyGen(WemburosBlocks.BODY,
                seed -> new WorldChunkManagerCelestial(createBiomeGenerators(seed), seed, BiomeGenBaseWemburos.violetPlains),
                ChunkProviderWemburos::new,
                () -> WemburosBlocks.WEMBUROS_STONE.get().defaultBlockState(),
                BiomeGenBaseWemburos.SEA_LEVEL));
        com.space_beyond.dim.BeyondDecorators.register(WemburosBlocks.BODY, WemburosStructures::decorate);
    }

    public static BiomeGenLayers createBiomeGenerators(long seed) {
        GenLayer biomes = new GenLayerWemburosBiomes(seed);
        biomes = new GenLayerFuzzyZoom(2000L, biomes);
        biomes = GenLayerZoom.magnify(1000L, biomes, 5);
        biomes = new GenLayerSmooth(700L, biomes);
        GenLayer voronoi = new GenLayerVoronoiZoom(10L, biomes);
        return new BiomeGenLayers(biomes, voronoi, seed);
    }
}
