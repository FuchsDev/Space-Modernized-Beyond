package com.space_beyond.mixin;

import static com.hbm_m.inventory.material.Mats.*;
import static com.space_m.material.SpaceMats.MAT_ZINC;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm_m.inventory.material.NTMMaterial;
import com.space_beyond.celestial.BeyondBodies;
import com.space_m.celestial.SolarSystem;
import com.space_m.resources.CelestialBedrockOre;
import com.space_m.resources.CelestialBedrockOre.Output;
import com.space_m.resources.CelestialBedrockOre.Type;

/**
 * Bedrock-Erz-Tabelle von Sporos (hinter den Original-Koerpern): feuchte Erdwelt - Bauxitboeden (Aluminium),
 * Schwermetalle, Seltene Erden, Thorium/Uran, Kohle aus versunkenen Pilzwaeldern, Smaragd/Sodalith. Space Modernized
 * legt daraus wie fuer jeden Koerper die 26 Verarbeitungsstufen je Sorte an ({@code space_m:bedrock_ore_<grade>_sporos_<sorte>}).
 */
@Mixin(value = CelestialBedrockOre.class, remap = false)
public abstract class CelestialBedrockOreMixin {

    @Shadow
    private static void register(SolarSystem.Body body, Type... types) {
        throw new AssertionError();
    }

    @Shadow
    private static Type T(String suffix, Output primary, Output byproductAcid, Output byproductSolvent, Output byproductRad) {
        throw new AssertionError();
    }

    @Shadow
    private static Output o(NTMMaterial mat, int amount) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void beyond$sporosTable(CallbackInfo ci) {
        register(
            BeyondBodies.SPOROS,
            T("light",		o(MAT_ALUMINIUM, 18),		o(MAT_TITANIUM, 9),			o(MAT_LITHIUM, 6),			o(MAT_SODIUM, 3)),
            T("heavy",		o(MAT_LEAD, 18),			o(MAT_TUNGSTEN, 9),			o(MAT_BISMUTH, 6),			o(MAT_ZINC, 3)),
            T("rare",		o(MAT_RAREEARTH, 18),		o(MAT_NEODYMIUM, 9),		o(MAT_LANTHANIUM, 6),		o(MAT_STRONTIUM, 3)),
            T("actinide",	o(MAT_THORIUM, 18),			o(MAT_URANIUM, 9),			o(MAT_RADIUM, 6),			o(MAT_U235, 3)),
            T("nonmetal",	o(MAT_COAL, 18),			o(MAT_SULFUR, 9),			o(MAT_PHOSPHORUS, 6),		o(MAT_KNO, 3)),
            T("crystal",	o(MAT_EMERALD, 18),			o(MAT_SODALITE, 9),			o(MAT_DIAMOND, 6),			o(MAT_CINNABAR, 3))
        );
    }
}
