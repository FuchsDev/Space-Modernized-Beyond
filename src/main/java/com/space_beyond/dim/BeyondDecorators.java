package com.space_beyond.dim;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Zusatz nach der Vanilla-Dekoration eines Chunks je Koerper (z.B. Loot-Tables der Sporos-Staedte). Aufgerufen von
 * {@code CelestialChunkGeneratorMixin} nach {@code applyBiomeDecoration}.
 */
public final class BeyondDecorators {
    @FunctionalInterface
    public interface Decorator {
        void decorate(WorldGenLevel level, ChunkAccess chunk);
    }

    private static final Map<String, Decorator> DECORATORS = new ConcurrentHashMap<>();

    private BeyondDecorators() {}

    public static void register(String body, Decorator decorator) {
        DECORATORS.put(body, decorator);
    }

    @Nullable
    public static Decorator decorator(String body) {
        return DECORATORS.get(body);
    }
}
