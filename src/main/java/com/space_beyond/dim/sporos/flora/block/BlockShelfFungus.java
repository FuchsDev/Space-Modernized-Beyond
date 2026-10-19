package com.space_beyond.dim.sporos.flora.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Pilzregal (Konsolenpilz): flache Scheibe, die an einer Wand oder einem Stamm haengt. {@code FACING} zeigt von der
 * Wand weg; faellt ab, wenn der Traeger verschwindet. Essbar (Block-Item mit Nahrung).
 */
public class BlockShelfFungus extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape NORTH = Block.box(2.0D, 6.0D, 7.0D, 14.0D, 9.0D, 16.0D);
    private static final VoxelShape SOUTH = Block.box(2.0D, 6.0D, 0.0D, 14.0D, 9.0D, 9.0D);
    private static final VoxelShape WEST = Block.box(7.0D, 6.0D, 2.0D, 16.0D, 9.0D, 14.0D);
    private static final VoxelShape EAST = Block.box(0.0D, 6.0D, 2.0D, 9.0D, 9.0D, 14.0D);

    public BlockShelfFungus(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();
        if (face.getAxis().isHorizontal()) {
            BlockState s = defaultBlockState().setValue(FACING, face);
            if (s.canSurvive(ctx.getLevel(), ctx.getClickedPos())) return s;
        }
        for (Direction d : ctx.getNearestLookingDirections()) {
            if (!d.getAxis().isHorizontal()) continue;
            BlockState s = defaultBlockState().setValue(FACING, d.getOpposite());
            if (s.canSurvive(ctx.getLevel(), ctx.getClickedPos())) return s;
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos wall = pos.relative(facing.getOpposite());
        BlockState s = level.getBlockState(wall);
        return s.isFaceSturdy(level, wall, facing) || s.is(net.minecraft.tags.BlockTags.LOGS);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockShelfFungus> CODEC = simpleCodec(BlockShelfFungus::new);
    @Override protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    *///?}
}
