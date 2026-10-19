package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.celestial.BeyondSolarSystem;
import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;

import net.minecraft.world.level.Level;

/** Sonnensystem: Beyond-Koerper und Sternsysteme anmelden, Abstand/Delta-v zwischen zwei Sternsystemen. */
@Mixin(value = SolarSystem.class, remap = false)
public abstract class SolarSystemMixin {

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/space_m/celestial/SolarSystem;runTests()V"))
    private static void beyond$extend(CallbackInfo ci) {
        BeyondSolarSystem.extend();
    }

    /** Verschiedene Sternsysteme: Abstand der Systeme in der Galaxie (km). */
    @Inject(method = "calculateDistanceBetweenTwoBodies", at = @At("HEAD"), cancellable = true)
    private static void beyond$interstellarDistance(Level world, CelestialBody from, CelestialBody to, CallbackInfoReturnable<Double> cir) {
        if (!StarSystem.sameSystem(from, to)) {
            StarSystem a = StarSystem.of(from);
            StarSystem b = StarSystem.of(to);
            cir.setReturnValue(a != null && b != null ? a.distanceKm(b) : Double.POSITIVE_INFINITY);
        }
    }

    /**
     * Ziel in einem anderen Sternsystem -> interstellarer Antrieb (Standard gesperrt = unendlich, {@code getFuelCost} liefert
     * dann {@code Integer.MAX_VALUE}). Gleiches System: unveraendert die Original-Rechnung.
     */
    @Inject(method = "getDeltaVBetween", at = @At("HEAD"), cancellable = true)
    private static void beyond$interstellarDeltaV(CelestialBody start, CelestialBody end, CallbackInfoReturnable<Double> cir) {
        if (start != end && !StarSystem.sameSystem(start, end)) {
            StarSystem.InterstellarDrive drive = StarSystem.interstellarDrive();
            cir.setReturnValue(drive.canTravel(start, end) ? drive.deltaV(start, end) : Double.POSITIVE_INFINITY);
        }
    }
}
