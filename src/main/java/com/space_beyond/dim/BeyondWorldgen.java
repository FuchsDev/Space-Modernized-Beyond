package com.space_beyond.dim;

import com.space_beyond.BeyondMod;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Weltgen von Beyond: Koerper Sporos/Wemburos (Bloecke, Generator-Fabriken) und eigene Features. */
public final class BeyondWorldgen {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BeyondMod.MOD_ID, Registries.FEATURE);

    private BeyondWorldgen() {}

    public static void init() {
        com.space_beyond.dim.sporos.WorldProviderSporos.init();
        com.space_beyond.dim.wemburos.WorldProviderWemburos.init();
        FEATURES.register();
    }
}
