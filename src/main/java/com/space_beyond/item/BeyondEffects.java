package com.space_beyond.item;

import com.space_beyond.BeyondMod;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

/** Effekte und Schadensarten von Beyond. */
public final class BeyondEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BeyondMod.MOD_ID, Registries.MOB_EFFECT);

    /** Sporos Runde 4: Sporeninfektion (Staerke = Stufe - 1), gesetzt von {@code SporeInfection.sync}. */
    public static final RegistrySupplier<MobEffect> SPORE_INFECTION = EFFECTS.register("spore_infection",
            com.space_beyond.dim.sporos.spores.SporeInfectionEffect::new);

    /** Sporos Runde 4: Schaden der Sporeninfektion Stufe 3 ("death.attack.spore_infection", durch Ruestung). */
    public static final ResourceKey<DamageType> SPORE_INFECTION_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, BeyondMod.id("spore_infection"));

    private BeyondEffects() {}

    public static void init() {
        EFFECTS.register();
    }

    public static DamageSource sporeInfection(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SPORE_INFECTION_DAMAGE));
    }
}
