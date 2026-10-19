package com.space_beyond.mixin;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.trait.CBT_Dyson;
import com.space_m.celestial.trait.CelestialBodyTrait;

/** Himmelskoerper: Dyson-Schwaerme und Sonnenleistung je Stern des eigenen Sternsystems (Heimat identisch). */
@Mixin(value = CelestialBody.class, remap = false)
public abstract class CelestialBodyMixin {

    /** Original: nur Kerbol ("we currently only have the one solar body"); hier jeder Stern, Kerbol zuerst. */
    @Inject(method = "updateSwarms", at = @At("HEAD"), cancellable = true)
    private static void beyond$updateAllSwarms(CallbackInfo ci) {
        for (StarSystem system : StarSystem.all()) {
            HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits = system.star.getTraits();
            CBT_Dyson dyson = (CBT_Dyson) currentTraits.get(CBT_Dyson.class);
            if (dyson == null) continue;
            dyson.attenuate();
        }
        ci.cancel();
    }

    /** Leuchtkraft des eigenen Sterns (Heimat = 1 -> exakt 1 / d^2; Sporos 0,45 / d^2), Abstand zum eigenen Stern. */
    @Inject(method = "getSunPower", at = @At("HEAD"), cancellable = true)
    private void beyond$sunPower(CallbackInfoReturnable<Float> cir) {
        CelestialBody self = (CelestialBody) (Object) this;
        float distanceAU = self.getPlanet().semiMajorAxisKm / AstronomyUtil.KM_IN_AU;
        StarSystem system = StarSystem.of(self);
        float luminosity = system == null ? 1.0F : (float) system.luminosity;
        cir.setReturnValue(luminosity / (distanceAU * distanceAU));
    }
}
