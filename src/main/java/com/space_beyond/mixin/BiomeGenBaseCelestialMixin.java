package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_beyond.BeyondMod;
import com.space_m.SpaceMod;
import com.space_m.dim.BiomeGenBaseCelestial;

import net.minecraft.resources.ResourceLocation;

/**
 * Biome der Beyond-Koerper (Unterklassen in {@code com.space_beyond}) liegen im Datapack {@code space_beyond:<pfad>};
 * Space Modernized bildet den Schluessel fest als {@code space_m:<pfad>}.
 */
@Mixin(value = BiomeGenBaseCelestial.class, remap = false)
public abstract class BiomeGenBaseCelestialMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/space_m/SpaceMod;id(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation beyond$biomeNamespace(String path) {
        return getClass().getName().startsWith("com.space_beyond.") ? BeyondMod.id(path) : SpaceMod.id(path);
    }
}
