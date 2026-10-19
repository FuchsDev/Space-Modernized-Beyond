package com.space_beyond.dim.sporos.flora.block;

import com.space_beyond.dim.sporos.flora.SporosFlora;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Leuchtpilz: Lichtquelle (Licht 12 ueber die Eigenschaften), waechst auf Boden, Gestein, Pilz- und Holzbloecken
 * ({@link SporosFlora#FUNGUS_SOIL}) und braucht kein Tageslicht - Nacht- und Hoehlenstimmung des Pilzhains.
 * Gelegentliche Leuchtpartikel.
 */
public class BlockGlowCap extends BushBlock {

    protected static final VoxelShape SHAPE = BushBlock.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);

    public BlockGlowCap(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return SporosFlora.FUNGUS_SOIL.test(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return mayPlaceOn(level.getBlockState(below), level, below);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.GLOW, pos.getX() + 0.3D + random.nextDouble() * 0.4D,
                    pos.getY() + 0.5D + random.nextDouble() * 0.4D, pos.getZ() + 0.3D + random.nextDouble() * 0.4D, 0.0D, 0.0D, 0.0D);
        }
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockGlowCap> CODEC = simpleCodec(BlockGlowCap::new);
    @Override protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() { return CODEC; }
    *///?}
}
