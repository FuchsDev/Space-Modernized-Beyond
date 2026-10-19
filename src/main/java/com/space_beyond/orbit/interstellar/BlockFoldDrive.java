package com.space_beyond.orbit.interstellar;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.interfaces.ILookOverlay;
import com.space_m.celestial.CelestialBody;
import com.space_m.orbit.propulsion.PropulsionBlockEntities;
import com.space_m.orbit.propulsion.PropulsionUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Faltraum-Antrieb ({@code machine_fold_drive}, eigene Erweiterung): Block der Raumstation, der Reisen in andere
 * Sternsysteme freischaltet ({@link TileEntityFoldDrive}). Overlay: ausserhalb des Orbits "MUST BE IN ORBIT", sonst
 * Ladung und Bereitschaft.
 */
public class BlockFoldDrive extends BaseEntityBlock implements ILookOverlay {

    public BlockFoldDrive(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityFoldDrive(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, com.space_beyond.blockentity.BeyondBlockEntities.FOLD_DRIVE.get(), TileEntityFoldDrive::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void printHook(GuiGraphics g, Level world, BlockPos pos) {
        List<Component> text = new ArrayList<>();

        if (!CelestialBody.inOrbit(world)) {
            text.add(PropulsionUtil.blinkWarning("atmosphere.noOrbit"));
        } else if (world.getBlockEntity(pos) instanceof TileEntityFoldDrive drive) {
            text.add(PropulsionUtil.powerLine(drive.getEnergyStored()).copy()
                    .append(Component.literal(" / 1GHE (" + Math.round(drive.chargeFraction() * 100) + "%)").withStyle(ChatFormatting.RESET)));
            text.add(drive.isCharged()
                    ? Component.translatable("station.foldDrive.ready").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("station.foldDrive.charging").withStyle(ChatFormatting.YELLOW));
        }

        ILookOverlay.printGeneric(g, Component.translatable(getDescriptionId()), 0xffff00, 0x404000, text);
    }

    //? if >1.20.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockFoldDrive> CODEC = simpleCodec(BlockFoldDrive::new);
    @Override protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    *///?}
}
