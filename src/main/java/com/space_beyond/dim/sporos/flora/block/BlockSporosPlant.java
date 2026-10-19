package com.space_beyond.dim.sporos.flora.block;

import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Einfache Sporos-Pflanze (Kreuzmodell) mit eigener Bodenregel; optional Sporenstaub-Partikel
 * ({@code spore_bloom}: gelegentlich ein {@code spore_blossom_air}-Partikel ueber der Bluete).
 */
public class BlockSporosPlant extends BushBlock {

    protected static final VoxelShape SHAPE = BushBlock.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    private final Predicate<BlockState> soil;
    private final boolean sporeParticles;

    public BlockSporosPlant(Properties properties, Predicate<BlockState> soil, boolean sporeParticles) {
        super(properties);
        this.soil = soil;
        this.sporeParticles = sporeParticles;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return soil.test(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE.move(state.getOffset(level, pos).x, 0, state.getOffset(level, pos).z);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (sporeParticles && random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, pos.getX() + 0.3D + random.nextDouble() * 0.4D,
                    pos.getY() + 0.6D + random.nextDouble() * 0.5D, pos.getZ() + 0.3D + random.nextDouble() * 0.4D, 0.0D, 0.01D, 0.0D);
        }
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockSporosPlant> CODEC = simpleCodec(p -> new BlockSporosPlant(p, s -> true, false));
    @Override protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() { return CODEC; }
    *///?}
}
