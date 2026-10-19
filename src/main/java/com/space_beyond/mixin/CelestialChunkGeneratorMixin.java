package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_m.dim.CelestialChunkGenerator;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Bloecke, deren Form von den Nachbarn abhaengt (Scheiben/Zaeune/Mauern verbinden, Treppenecken, Ranken ohne Halt),
 * werden wie Fluide zur Nachbearbeitung vorgemerkt: der Puffer kennt keine Nachbarn ueber die Chunkgrenze, wie bei
 * Vanilla-Strukturen rechnet {@code LevelChunk.postProcessGeneration} die Form beim Laden nach. Kommt nur in den
 * Sporos-Staedten vor; das Gelaende der Original-Koerper enthaelt keine solchen Bloecke.
 */
@Mixin(value = CelestialChunkGenerator.class, remap = false)
public abstract class CelestialChunkGeneratorMixin {

    /** {@code if (!state.getFluidState().isEmpty()) markPosForPostprocessing(...)} - Formbloecke zaehlen wie Fluide. */
    @Redirect(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", remap = true))
    private FluidState beyond$markShapeBlocks(BlockState state) {
        FluidState fluid = state.getFluidState();
        if (fluid.isEmpty() && beyond$needsShapeUpdate(state)) return Fluids.WATER.defaultFluidState();
        return fluid;
    }

    @Unique
    private static boolean beyond$needsShapeUpdate(BlockState state) {
        Block b = state.getBlock();
        return b instanceof CrossCollisionBlock || b instanceof WallBlock || b instanceof StairBlock || b instanceof VineBlock;
    }
}
