package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_m.dim.ChunkProviderCelestial.BlockMetaBuffer;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;

/** Planeten-Fortschritt: den Boss-Dungeon des Koerpers in den fertigen Chunk-Puffer zeichnen (vor dem Zwischenspeicher). */
@Mixin(value = CelestialGenerators.class, remap = false)
public abstract class CelestialGeneratorsMixin {

    @Redirect(method = "buffer", at = @At(value = "INVOKE", target = "Lcom/space_m/dim/ChunkProviderCelestial;provideChunk(II)Lcom/space_m/dim/ChunkProviderCelestial$BlockMetaBuffer;"))
    private static BlockMetaBuffer beyond$withDungeon(ChunkProviderCelestial provider, int x, int z, String body, int chunkX, int chunkZ) {
        BlockMetaBuffer b = provider.provideChunk(x, z);
        com.space_beyond.dim.dungeon.PlanetDungeons.generateChunk(body, CelestialGenerators.state(body).chunkManager, chunkX, chunkZ, b.blocks);
        return b;
    }
}
