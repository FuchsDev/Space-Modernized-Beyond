package com.space_beyond.dim.dungeon.seal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Siegelschloss: unzerstoerbarer Block mit Schluesselloch in einer Siegelwand. Rechtsklick mit einem Siegelschluessel
 * fuellt das Schloss ({@link #LIT}); sind alle Schloesser der Wand gefuellt, loest sich das Siegelfeld auf.
 */
public class SealLockBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SealLockBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    //? if < 1.21.1 {
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return activate(state, level, pos, player, player.getItemInHand(hand));
    }
    //?} else {
    /*@Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                                                 InteractionHand hand, BlockHitResult hit) {
        InteractionResult r = activate(state, level, pos, player, stack);
        return r == InteractionResult.PASS ? net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                : net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return activate(state, level, pos, player, ItemStack.EMPTY);
    }
    *///?}

    private InteractionResult activate(BlockState state, Level level, BlockPos pos, Player player, ItemStack held) {
        if (state.getValue(LIT)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ServerLevel server = (ServerLevel) level;
        if (held.isEmpty() || held.getItem() != SealBlocks.SEAL_KEY.get()) {
            SealGates.status(server, pos, player, false);
            return InteractionResult.CONSUME;
        }
        if (!player.isCreative()) held.shrink(1);
        level.setBlock(pos, state.setValue(LIT, true), 3);
        SealGates.lockFilled(server, pos, player);
        return InteractionResult.CONSUME;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) || random.nextInt(3) != 0) return;
        Direction f = state.getValue(FACING);
        double x = pos.getX() + 0.5 + f.getStepX() * 0.55, z = pos.getZ() + 0.5 + f.getStepZ() * 0.55;
        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, pos.getY() + 0.5, z, 0, 0.01, 0);
    }
}
