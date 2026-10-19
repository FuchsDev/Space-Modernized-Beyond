package com.space_beyond.orbit.interstellar;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.blockentity.BaseMachineBlockEntity;
import com.space_m.celestial.CelestialBody;
import com.space_m.orbit.propulsion.PropulsionBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Faltraum-Antrieb (eigene Erweiterung): Stromspeicher fuer genau einen Faltsprung. Nimmt HBM-Strom von allen
 * Seiten (Energienetz-Anmeldung uebernimmt {@link BaseMachineBlockEntity}), meldet sich im Orbit bei der Station
 * unter sich an ({@link IFoldDrive}) und ist bei vollem Speicher sprungbereit. Der Start einer interstellaren Reise
 * leert den Speicher.
 *
 * <p>Werte: Speicher {@link #MAX_POWER} = 1 GHE je Sprung, Aufnahme bis {@link #MAX_RECEIVE} = 10 MHE/Tick
 * (voll in 100 Ticks an einem starken Netz, an einem 1-MHE/t-Reaktor ~17 Minuten).
 */
public class TileEntityFoldDrive extends BaseMachineBlockEntity implements IFoldDrive {

    public static final long MAX_POWER = 1_000_000_000L;
    public static final long MAX_RECEIVE = 10_000_000L;

    private boolean registered;

    public TileEntityFoldDrive(BlockPos pos, BlockState state) {
        super(com.space_beyond.blockentity.BeyondBlockEntities.FOLD_DRIVE.get(), pos, state, 0, MAX_POWER, MAX_RECEIVE);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityFoldDrive be) {
        if (level.isClientSide) return;
        if (!CelestialBody.inOrbit(level)) return;

        if (!be.registered) {
            be.registerFoldDrive();
            be.registered = true;
        }

        if (level.getGameTime() % 20 == 0) {
            be.setChanged();
            be.sendUpdateToClient();
        }
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public boolean isCharged() {
        return getEnergyStored() >= MAX_POWER;
    }

    @Override
    public void consumeCharge() {
        setEnergyStored(0);
        setChanged();
        sendUpdateToClient();
    }

    /** Ladestand 0..1 (Overlay). */
    public float chargeFraction() {
        return (float) ((double) getEnergyStored() / (double) MAX_POWER);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && !level.isClientSide && registered) {
            unregisterFoldDrive();
            registered = false;
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.foldDrive");
    }

    @Override
    protected boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }
}
