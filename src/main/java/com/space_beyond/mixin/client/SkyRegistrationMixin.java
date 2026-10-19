package com.space_beyond.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.client.sky.BeyondSkyProviders;
import com.space_m.client.sky.CelestialSkyProvider;
import com.space_m.client.sky.SkyRegistration;

/**
 * Himmel der Beyond-Dimensionen: {@code SkyRegistration} meldet alle Dimensionen aus {@code SpaceDimensions.ALL} an
 * (dort haengt {@code SpaceDimensionsMixin} Sporos und Wemburos an) und waehlt den Provider nach dem Namen.
 */
@Mixin(value = SkyRegistration.class, remap = false)
public abstract class SkyRegistrationMixin {

    @Inject(method = "providerFor", at = @At("HEAD"), cancellable = true)
    private static void beyond$providerFor(String name, CallbackInfoReturnable<CelestialSkyProvider> cir) {
        switch (name) {
            case "sporos" -> cir.setReturnValue(new BeyondSkyProviders.Sporos());
            case "wemburos" -> cir.setReturnValue(new BeyondSkyProviders.Wemburos());
            default -> { }
        }
    }
}
