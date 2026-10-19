package com.space_beyond.particle;

import com.space_beyond.BeyondMod;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

/** Partikel von Beyond: Orbital Strike Rod / Unstable Sovereign (Funken, Goldglut, Leere-Staub). */
public final class BeyondParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BeyondMod.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SOVEREIGN_SPARK = simple("sovereign_spark", true);
    public static final RegistrySupplier<SimpleParticleType> GOLD_EMBER = simple("gold_ember", true);
    public static final RegistrySupplier<SimpleParticleType> VOID_MOTE = simple("void_mote", true);

    private BeyondParticles() {}

    public static RegistrySupplier<SimpleParticleType> simple(String name, boolean alwaysShow) {
        return PARTICLES.register(name, () -> new SimpleParticleType(alwaysShow) {});
    }

    public static void init() {
        PARTICLES.register();
    }
}
