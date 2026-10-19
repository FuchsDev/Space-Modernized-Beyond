package com.space_beyond.dim.sporos.flora.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Moospolster: weicher, begehbarer Vollblock aus dichtem Moos; daempft Fallschaden auf 20 % (wie Heu). Boden fuer
 * Setzling und Pflanzen ({@code SporosFlora.SOIL}), Tag {@code dirt} (Setzlinge/Vanilla-Pflanzen).
 */
public class BlockMossCushion extends Block {

    public BlockMossCushion(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.2F, level.damageSources().fall());
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockMossCushion> CODEC = simpleCodec(BlockMossCushion::new);
    @Override protected com.mojang.serialization.MapCodec<? extends Block> codec() { return CODEC; }
    *///?}
}
