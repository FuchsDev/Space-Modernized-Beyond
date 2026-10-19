package com.space_beyond.dim.sporos;

import com.space_m.dim.CelestialWorld;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.mapgen.MapGenVanillaCaves;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.SporosCities;

import net.minecraft.world.level.block.Blocks;

/**
 * Gelaende von Sporos (eigene Erweiterung): 3D-Rauschen des {@link ChunkProviderCelestial} mit
 * {@code sporos_stone}, Meer/Fluesse/Sumpfwasser bis y 62 ({@link BiomeGenBaseSporos#SEA_LEVEL} 63), Oberflaeche je
 * Biom, Vanilla-Hoehlen im Planetengestein (unter y 10 Lava wie ueberall), danach der Staedtegenerator
 * ({@link SporosCities#generateChunk} -> {@code SporosCityGenerator}, Runde 3: Gelaende einebnen, Strassenraster,
 * Ruinen, Bewuchs) und die Wildnis-Strukturen ({@code SporosStructures.generateChunk}).
 */
public class ChunkProviderSporos extends ChunkProviderCelestial {

    private final MapGenVanillaCaves caves;

    public ChunkProviderSporos(CelestialWorld world, long seed) {
        super(world, seed);
        this.stoneBlock = SporosBlocks.SPOROS_STONE.get().defaultBlockState();
        this.seaBlock = Blocks.WATER.defaultBlockState();
        this.seaLevel = BiomeGenBaseSporos.SEA_LEVEL;
        this.caves = new MapGenVanillaCaves(this.stoneBlock);
    }

    @Override
    public BlockMetaBuffer getChunkPrimer(int x, int z) {
        BlockMetaBuffer buffer = super.getChunkPrimer(x, z);

        caves.func_151539_a(worldObj, x, z, buffer.blocks);

        // Runde 3: Strassenraster und Ruinen (biomesForGeneration = Detailbiome dieses Chunks)
        SporosCities.generateChunk(worldObj, x, z, buffer.blocks, biomesForGeneration);
        // Wildnis-Strukturen ausserhalb der Staedte (Funkmast, Berghuette, Krankenhaus, Kraftwerk, Siedlung)
        com.space_beyond.dim.sporos.structure.SporosStructures.generateChunk(worldObj, x, z, buffer.blocks);

        return buffer;
    }
}
