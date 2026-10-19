package com.space_beyond.dim.sporos;

import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_m.dim.WorldChunkManagerCelestial.BiomeGenLayers;
import com.space_m.dim.legacy.GenLayer;
import com.space_m.dim.legacy.GenLayerRiver;
import com.space_m.dim.legacy.GenLayerSmooth;
import com.space_m.dim.legacy.GenLayerVoronoiZoom;
import com.space_m.dim.legacy.GenLayerZoom;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.CityLoot;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.city.SporosCityGenerator;
import com.space_beyond.dim.sporos.genlayer.GenLayerSporosBiomes;
import com.space_beyond.dim.sporos.genlayer.GenLayerSporosCity;
import com.space_beyond.dim.sporos.genlayer.GenLayerSporosRiverInit;
import com.space_beyond.dim.sporos.genlayer.GenLayerSporosRiverMix;

/**
 * Dimension "Sporos" ({@code space_m:sporos}, eigene Erweiterung im zweiten Sternsystem). Gegenstueck zu den
 * {@code WorldProvider<Koerper>}-Klassen: Biomverteilung (GenLayer-Kette unten), Generator {@link ChunkProviderSporos},
 * Erz-Ersatzblock {@code sporos_stone}, Meeresspiegel 63. Himmel/Nebel: {@code CelestialSkyProvider.Sporos}
 * ({@code effects space_m:sporos}).
 *
 * <p>Biomkette: Grundschicht (Ebene/Wald/Sumpf/Pilzhain/Hochland/Sporenfeld/See) sechsmal vergroessert (Biomflecken
 * ~256 Bloecke), Kueste um Seen, geglaettet, Stadtbiome in den geplanten Staedten ({@link GenLayerSporosCity}, Runde 3);
 * Flussrauschen gleich weit vergroessert, Flusskanten ({@link GenLayerRiver}), geglaettet; Mischung
 * ({@link GenLayerSporosRiverMix}), Voronoi auf Blockaufloesung.
 */
public final class WorldProviderSporos {

    private WorldProviderSporos() {}

    public static void init() {
        SporosBlocks.init();
        com.space_beyond.dim.sporos.flora.SporosFlora.init(); // R2b: Hyphe-Holz, Pflanzen, Baum-Features (vor FEATURES.register())
        BiomeGenBaseSporos.init();
        CelestialGenerators.register(new CelestialGenerators.BodyGen(SporosBlocks.BODY,
                seed -> new WorldChunkManagerCelestial(createBiomeGenerators(seed), seed, BiomeGenBaseSporos.overgrownPlains),
                ChunkProviderSporos::new,
                () -> SporosBlocks.SPOROS_STONE.get().defaultBlockState(),
                BiomeGenBaseSporos.SEA_LEVEL));
        // Runde 3: Staedte (Puffer) + Loot-Tables der Behaelter (Dekorationsphase)
        SporosCities.setGenerator(SporosCityGenerator.INSTANCE);
        com.space_beyond.dim.BeyondDecorators.register(SporosBlocks.BODY, (level, chunk) -> {
            CityLoot.decorate(level, chunk);
            com.space_beyond.dim.sporos.structure.StructureLoot.decorate(level, chunk); // Wildnis-Strukturen: Loot + Spawner
        });
        // Runde 4: Sporeninfektion (Expositions-Tick je Sekunde, Tod infizierter Wesen)
        com.space_beyond.dim.sporos.spores.SporeExposure.init();
    }

    public static BiomeGenLayers createBiomeGenerators(long seed) {
        GenLayer biomes = new GenLayerSporosBiomes(seed);
        biomes = GenLayerZoom.magnify(1000L, biomes, 4);
        biomes = new com.space_beyond.dim.sporos.genlayer.GenLayerSporosShore(2000L, biomes); // R2b: Kueste um Seen (Zellen 16 Bloecke)
        biomes = GenLayerZoom.magnify(1000L, biomes, 2);
        biomes = new GenLayerSmooth(1000L, biomes);
        biomes = new GenLayerSporosCity(seed, 3000L, biomes); // Runde 3: in Staedten nur Ebene/Moosdickicht/Sumpf/Fluss

        GenLayer rivers = new GenLayerSporosRiverInit(100L);
        rivers = GenLayerZoom.magnify(1000L, rivers, 6);
        rivers = new GenLayerRiver(1L, rivers);
        rivers = new GenLayerSmooth(1000L, rivers);

        GenLayer mixed = new GenLayerSporosRiverMix(100L, biomes, rivers);
        GenLayer voronoi = new GenLayerVoronoiZoom(10L, mixed);

        return new BiomeGenLayers(mixed, voronoi, seed);
    }
}
