package com.space_beyond.dim;

import java.util.List;

import com.space_beyond.BeyondMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Dimensionen von Beyond (Datapack {@code data/space_beyond/dimension}). {@code SpaceDimensionsMixin} haengt sie
 * zusaetzlich an {@code SpaceDimensions.ALL}/{@code BODIES} an.
 */
public final class BeyondDimensions {
    /** Zweites Sternsystem: Planet Sporos (Legacy-Nummer 413026). */
    public static final ResourceKey<Level> SPOROS = level("sporos");
    /** Planet Wemburos im Sporos-System (Legacy-Nummer 413027). */
    public static final ResourceKey<Level> WEMBUROS = level("wemburos");

    public static final List<ResourceKey<Level>> ALL = List.of(SPOROS, WEMBUROS);

    private BeyondDimensions() {}

    public static ResourceKey<Level> level(String name) {
        return ResourceKey.create(Registries.DIMENSION, BeyondMod.id(name));
    }

    public static ResourceKey<DimensionType> type(String name) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, BeyondMod.id(name));
    }
}
