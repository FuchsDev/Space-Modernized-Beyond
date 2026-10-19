package com.space_beyond.progression.fx;

import com.space_m.entity.SpaceEntities;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Registrierung der Waffeneffekte (aus {@code ProgressionItems.init()}, vor dem Entity-Register). */
public final class WeaponFx {

    private WeaponFx() {}

    public static final RegistrySupplier<EntityType<SlashEffect>> SLASH = com.space_beyond.entity.BeyondEntities.register("weapon_slash",
            () -> EntityType.Builder.<SlashEffect>of(SlashEffect::new, MobCategory.MISC).sized(0.5F, 0.5F).noSave().fireImmune()
                    .clientTrackingRange(8).updateInterval(20));

    public static void init() {
        // Klasse laden
    }
}
