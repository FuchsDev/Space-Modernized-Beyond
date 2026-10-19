package com.space_beyond.block.crate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if < 1.21.1 {
import net.minecraft.world.InteractionHand;
//?}

/**
 * Beute-Kiste der Sporos-Staedte (eigene Erweiterung, Nutzerwunsch "Loot Chests und Crates"): einfacher
 * 27-Platz-Behaelter wie ein Fass, ohne Metall (die NTM-Kisten {@code crate_iron/steel} sind in der Stadt-Whitelist
 * verboten, weil sie sich zu Metall zerlegen lassen). Zwei Varianten: {@code supply_crate} (Holz, Vorraete) und
 * {@code military_crate} (olivgruen, Armee). Loot-Table setzt {@code CityLoot} in der Dekorationsphase.
 */
public class BlockSupplyCrate extends BaseEntityBlock {

    public BlockSupplyCrate(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SupplyCrateBlockEntity(pos, state);
    }

    //? if < 1.21.1 {
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return open(level, pos, player);
    }
    //?} else {
    /*@Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return open(level, pos, player);
    }
    *///?}

    private static InteractionResult open(Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate) {
            player.openMenu(crate);
            player.awardStat(Stats.OPEN_BARREL);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate) {
                Containers.dropContents(level, pos, crate);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockSupplyCrate> CODEC = simpleCodec(BlockSupplyCrate::new);
    @Override protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    *///?}
}
