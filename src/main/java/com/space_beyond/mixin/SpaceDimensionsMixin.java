package com.space_beyond.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.dim.BeyondDimensions;
import com.space_m.dim.SpaceDimensions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Sporos und Wemburos zaehlen fuer Space Modernized als eigene Dimensionen (Himmel, Atmosphaere, Reise, Zeit):
 * {@code ALL} und {@code BODIES} bekommen sie hinten angehaengt, die 11 Original-Dimensionen behalten ihre Reihenfolge.
 */
@Mixin(value = SpaceDimensions.class, remap = false)
public abstract class SpaceDimensionsMixin {
    @Shadow @Final @Mutable public static List<ResourceKey<Level>> ALL;
    @Shadow @Final @Mutable public static List<ResourceKey<Level>> BODIES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void beyond$appendDimensions(CallbackInfo ci) {
        List<ResourceKey<Level>> all = new ArrayList<>(ALL);
        all.addAll(BeyondDimensions.ALL);
        ALL = List.copyOf(all);
        List<ResourceKey<Level>> bodies = new ArrayList<>(BODIES);
        bodies.addAll(BeyondDimensions.ALL);
        BODIES = List.copyOf(bodies);
    }
}
