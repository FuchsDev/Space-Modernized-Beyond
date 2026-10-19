package com.space_beyond.mixin.client;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.client.sky.BeyondSky;
import com.space_m.celestial.trait.CBT_Destroyed;
import com.space_m.client.sky.CelestialSkyProvider;

import net.minecraft.client.multiplayer.ClientLevel;

/** Orbit-Himmel: in der Faltphase keine Koerper/Sonne; Sonnenhelligkeit nach dem Stern des umkreisten Koerpers. */
@Mixin(value = CelestialSkyProvider.Orbit.class, remap = false)
public abstract class OrbitSkyProviderMixin extends CelestialSkyProvider {

    @Inject(method = "updateSky", at = @At("HEAD"), cancellable = true)
    private void beyond$interstellarSky(ClientLevel level, float partialTicks, CallbackInfo ci) {
        if (!BeyondSky.interstellarTransfer()) return;
        metrics = new ArrayList<>();
        eclipseAmount = 1;
        ci.cancel();
    }

    /** Original: {@code SolarSystem.kerbol} zerstoert -> dunkel; hier der Stern des eigenen Systems (Heimat identisch). */
    @Inject(method = "getSunBrightness", at = @At("HEAD"), cancellable = true)
    private void beyond$ownStarBrightness(ClientLevel level, float par1, CallbackInfoReturnable<Float> cir) {
        if (Orbit.orbiting().getStar().hasTrait(CBT_Destroyed.class)) {
            cir.setReturnValue(0F);
        } else {
            cir.setReturnValue(1.0F - (float) eclipseAmount);
        }
    }
}
