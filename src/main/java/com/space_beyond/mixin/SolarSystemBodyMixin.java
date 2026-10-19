package com.space_beyond.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_m.celestial.SolarSystem;

/**
 * Haengt Sporos und Wemburos hinten an {@code SolarSystem.Body} an (Laufwerke und {@code wand_d} speichern die
 * Ordinalzahl; alle Original-Eintraege behalten ihre Zahl). Zugriff ueber {@code BeyondBodies}.
 */
@Mixin(value = SolarSystem.Body.class, remap = false)
public abstract class SolarSystemBodyMixin {
    @Shadow @Final @Mutable private static SolarSystem.Body[] $VALUES;

    @Invoker("<init>")
    private static SolarSystem.Body beyond$create(String constant, int ordinal, String name) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void beyond$appendBodies(CallbackInfo ci) {
        List<SolarSystem.Body> values = new ArrayList<>(Arrays.asList($VALUES));
        values.add(beyond$create("SPOROS", values.size(), com.space_beyond.celestial.SporosSystem.PLANET_NAME));
        values.add(beyond$create("WEMBUROS", values.size(), com.space_beyond.celestial.SporosSystem.WEMBUROS_NAME));
        $VALUES = values.toArray(new SolarSystem.Body[0]);
    }
}
