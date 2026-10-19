package com.space_beyond.entity;

import java.util.function.Supplier;

import com.space_beyond.BeyondMod;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

/** Entities von Beyond (Namensraum {@code space_beyond}); Attribute weiter ueber {@code SpaceEntities.attributes}. */
public final class BeyondEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BeyondMod.MOD_ID, Registries.ENTITY_TYPE);

    private BeyondEntities() {}

    /** {@code builder.build(id)} mit {@code space_beyond:<name>}. */
    public static <T extends net.minecraft.world.entity.Entity> RegistrySupplier<EntityType<T>> register(
            String name, Supplier<EntityType.Builder<T>> builder) {
        return ENTITIES.register(name, () -> builder.get().build(BeyondMod.MOD_ID + ":" + name));
    }

    public static void init() {
        ENTITIES.register();
    }
}
