package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_m.rocket.RocketPart;

/** Raketenteile: nach der Original-Tabelle die Triebwerke Mk.II-Mk.XI des Planeten-Fortschritts eintragen. */
@Mixin(value = RocketPart.class, remap = false)
public abstract class RocketPartMixin {

    @Inject(method = "ensureDefined", at = @At("TAIL"))
    private static void beyond$progressionParts(CallbackInfo ci) {
        com.space_beyond.progression.ProgressionParts.registerAll();
    }
}
