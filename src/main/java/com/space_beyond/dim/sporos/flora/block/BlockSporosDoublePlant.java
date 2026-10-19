package com.space_beyond.dim.sporos.flora.block;

import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Zwei Bloecke hohe Sporos-Pflanze (Riesenfarn, Sumpfgras) mit eigener Bodenregel; obere Haelfte wie Vanilla
 * ({@code DoublePlantBlock}: Beute nur aus der unteren Haelfte).
 */
public class BlockSporosDoublePlant extends DoublePlantBlock {

    private final Predicate<BlockState> soil;

    public BlockSporosDoublePlant(Properties properties, Predicate<BlockState> soil) {
        super(properties);
        this.soil = soil;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return soil.test(state);
    }

    /** Untere Haelfte prueft den Boden; obere Haelfte wie Vanilla (steht auf der unteren). */
    public boolean isLower(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockSporosDoublePlant> CODEC = simpleCodec(p -> new BlockSporosDoublePlant(p, s -> true));
    @Override public com.mojang.serialization.MapCodec<? extends DoublePlantBlock> codec() { return CODEC; }
    *///?}
}
