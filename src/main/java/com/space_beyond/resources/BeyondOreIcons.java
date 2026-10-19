package com.space_beyond.resources;

import java.util.Comparator;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.mixin.OreLocationsAccessor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** Symbol eines Original-Erzes fuer die Sternkarte (Fundort-Liste von {@code OreLocations}). */
public final class BeyondOreIcons {
    private BeyondOreIcons() {}

    /**
     * Ein Gegenstand des Original-Erzes (Vanilla- vor HBM-Variante, sonst alphabetisch) als Symbol, sonst null.
     * Space Modernized haelt die Zuordnung in einer HashMap; die feste Reihenfolge hier macht das Symbol stabil.
     */
    @Nullable
    public static Item iconOf(String ore) {
        Map<ResourceLocation, String> map = OreLocationsAccessor.beyond$itemToOre();
        return map.entrySet().stream()
                .filter(e -> e.getValue().equals(ore))
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing((ResourceLocation id) -> !"minecraft".equals(id.getNamespace()))
                        .thenComparing(ResourceLocation::toString))
                .map(BuiltInRegistries.ITEM::get)
                .filter(item -> item != null && item != Items.AIR)
                .findFirst().orElse(null);
    }
}
