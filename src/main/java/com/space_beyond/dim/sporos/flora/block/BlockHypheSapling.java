package com.space_beyond.dim.sporos.flora.block;

import java.util.Random;

import com.space_beyond.dim.sporos.flora.HypheTree;
import com.space_beyond.dim.sporos.flora.SporosFlora;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Hyphe-Setzling: waechst auf erdigem Boden (Licht &gt;= 9, 1/7 je Zufallstick, Knochenmehl 45 %) zu einer
 * zufaelligen Form von {@link HypheTree} (Schirmbaum 50 %, Urwaldriese 25 %, Sumpfbaum 25 %). Aufbau wie
 * {@code BlockNTSapling} (Tekto), ohne Vanilla-{@code SaplingBlock} (Konstruktor unterscheidet sich 1.20/1.21).
 */
public class BlockHypheSapling extends BushBlock implements BonemealableBlock {

    protected static final VoxelShape SHAPE = BushBlock.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockHypheSapling(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return SporosFlora.SOIL.test(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            grow(level, pos, random);
        }
    }

    public void grow(ServerLevel world, BlockPos pos, RandomSource rand) {
        BlockState own = world.getBlockState(pos);
        world.removeBlock(pos, false);
        Random r = new Random(rand.nextLong());
        if (!HypheTree.generate(world, r, pos, HypheTree.randomShape(r))) {
            world.setBlock(pos, own, 2);
        }
    }

    @Override
    //? if < 1.21.1 {
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean client) {
    //?} else {
    /*public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    *///?}
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return level.random.nextFloat() < 0.45D;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(level, pos, random);
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockHypheSapling> CODEC = simpleCodec(BlockHypheSapling::new);
    @Override protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() { return CODEC; }
    *///?}
}
