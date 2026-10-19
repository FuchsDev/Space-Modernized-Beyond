package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.dim.BeyondDecorators;
import com.space_m.dim.CelestialChunkGenerator;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * Nach der Vanilla-Dekoration eines Space-Koerpers: Zusatz des Koerpers ({@link BeyondDecorators}, z.B. Loot der
 * Sporos-Staedte) und die Boss-Dungeons (Loot, Spawner, Boss). {@code CelestialChunkGenerator} ueberschreibt die
 * Methode nicht, daher sitzt der Haken in {@code ChunkGenerator} und prueft die Klasse.
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void beyond$decorate(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        if (!((Object) this instanceof CelestialChunkGenerator gen)) return;
        BeyondDecorators.Decorator deco = BeyondDecorators.decorator(gen.body());
        if (deco != null) deco.decorate(level, chunk);
        com.space_beyond.dim.dungeon.PlanetDungeons.decorate(gen.body(), level, chunk);
    }
}
