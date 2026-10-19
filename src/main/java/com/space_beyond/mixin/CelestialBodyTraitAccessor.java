package com.space_beyond.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.space_m.celestial.trait.CelestialBodyTrait;

/** Eigene Koerper-Eigenschaften anmelden (Sync-ID = Index, nur hinten anhaengen). */
@Mixin(value = CelestialBodyTrait.class, remap = false)
public interface CelestialBodyTraitAccessor {
    @Invoker("registerTrait")
    static <T extends CelestialBodyTrait> void beyond$registerTrait(String name, Class<T> clazz, Supplier<T> factory) {
        throw new AssertionError();
    }
}
