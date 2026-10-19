package com.space_beyond.dim.sporos.flora.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Geflecht-Ranke: blasse Pilzfaeden, die von Decken, Aesten und Laub haengen; kletterbar (Tag {@code climbable}).
 * <ul>
 *   <li>Haengend ({@code up=true}): haelt unter Laub, Staemmen, jedem Block mit fester Unterseite und unter einer
 *       weiteren haengenden Geflecht-Ranke - so entstehen Straenge ({@code HypheTree}, Stadt-Rankenvorhaenge).
 *       Der Zustand rendert als haengender Strang (Kreuzmodell {@code mycel_vine_hanging}), nicht als flache
 *       Deckenflaeche wie die Vanilla-Ranke.</li>
 *   <li>An Waenden (Seitenflaechen) wie die Vanilla-Ranke.</li>
 *   <li>Waechst nur nach unten (Strang hoechstens {@link #MAX_STRAND} Bloecke), breitet sich nicht seitlich aus -
 *       die Vanilla-Ausbreitung hat die Welt ueberwuchert und staendig Chunks neu gebaut.</li>
 * </ul>
 * Ohne Halt (Nachbaraenderung) verschwindet die Ranke wie in Vanilla.
 */
public class BlockMycelVine extends VineBlock {

    public static final int MAX_STRAND = 8;

    public BlockMycelVine(Properties properties) {
        super(properties);
    }

    /** Traegt der Block ueber {@code pos} eine haengende Ranke? */
    public static boolean canHang(LevelReader level, BlockPos pos) {
        BlockPos up = pos.above();
        BlockState above = level.getBlockState(up);
        if (above.getBlock() instanceof BlockMycelVine) return above.getValue(UP);
        if (above.getBlock() instanceof LeavesBlock || above.is(BlockTags.LOGS)) return true;
        return above.isFaceSturdy(level, up, Direction.DOWN);
    }

    /** Zustand ohne die Flaechen, die keinen Halt mehr haben. */
    private static BlockState supported(BlockState state, LevelReader level, BlockPos pos) {
        BlockState s = state;
        if (s.getValue(UP) && !canHang(level, pos)) s = s.setValue(UP, false);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BooleanProperty p = PROPERTY_BY_DIRECTION.get(d);
            if (s.getValue(p) && !isAcceptableNeighbour(level, pos.relative(d), d)) s = s.setValue(p, false);
        }
        return s;
    }

    private static boolean anyFace(BlockState s) {
        if (s.getValue(UP)) return true;
        for (Direction d : Direction.Plane.HORIZONTAL) if (s.getValue(PROPERTY_BY_DIRECTION.get(d))) return true;
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return anyFace(supported(state, level, pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.DOWN) return state;
        BlockState s = supported(state, level, pos);
        return anyFace(s) ? s : Blocks.AIR.defaultBlockState();
    }

    /** Nur Wachstum nach unten: ein haengender Strang verlaengert sich mit 1/4 je Zufallstick bis {@link #MAX_STRAND}. */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(UP) || random.nextInt(4) != 0) return;
        BlockPos below = pos.below();
        if (!level.isEmptyBlock(below)) return;
        int len = 0;
        BlockPos p = pos;
        while (len < MAX_STRAND && level.getBlockState(p).getBlock() instanceof BlockMycelVine) {
            len++;
            p = p.above();
        }
        if (len >= MAX_STRAND) return;
        level.setBlock(below, defaultBlockState().setValue(UP, true), 2);
    }
}
