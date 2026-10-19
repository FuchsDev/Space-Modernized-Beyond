package com.space_beyond.blockentity;

import java.util.function.Supplier;

import com.space_beyond.BeyondMod;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Block-Entities von Beyond (Namensraum {@code space_beyond}). */
public final class BeyondBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BeyondMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    /** Sporos-Staedte: Beute-Kisten, ein Typ fuer beide Varianten. */
    public static final RegistrySupplier<BlockEntityType<com.space_beyond.block.crate.SupplyCrateBlockEntity>> SUPPLY_CRATE =
            register("supply_crate", com.space_beyond.block.crate.SupplyCrateBlockEntity::new,
                    com.space_beyond.dim.sporos.SporosBlocks.SUPPLY_CRATE, com.space_beyond.dim.sporos.SporosBlocks.MILITARY_CRATE);

    /** Sternsysteme: Faltraum-Antrieb. */
    public static final RegistrySupplier<BlockEntityType<com.space_beyond.orbit.interstellar.TileEntityFoldDrive>> FOLD_DRIVE =
            register("machine_fold_drive", com.space_beyond.orbit.interstellar.TileEntityFoldDrive::new,
                    com.space_beyond.block.BeyondBlocks.MACHINE_FOLD_DRIVE);

    private BeyondBlockEntities() {}

    @SafeVarargs
    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(
            String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] valid = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) valid[i] = blocks[i].get();
            return BlockEntityType.Builder.of(factory, valid).build(null);
        });
    }

    public static void init() {
        BLOCK_ENTITIES.register();
    }
}
