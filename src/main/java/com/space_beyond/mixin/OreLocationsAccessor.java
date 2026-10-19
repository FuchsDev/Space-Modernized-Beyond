package com.space_beyond.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.space_m.resources.OreLocations;

import net.minecraft.resources.ResourceLocation;

/** Gegenstands-ID -> Original-Erzname (fuer die Erz-Symbole der Sternkarte). */
@Mixin(value = OreLocations.class, remap = false)
public interface OreLocationsAccessor {
    @Accessor("itemToOre")
    static Map<ResourceLocation, String> beyond$itemToOre() {
        throw new AssertionError();
    }
}
