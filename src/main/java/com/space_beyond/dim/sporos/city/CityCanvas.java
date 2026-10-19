package com.space_beyond.dim.sporos.city;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Schreibflaeche fuer genau einen Chunk-Puffer (Index {@code (x * 16 + z) * 256 + y}). Alle Maler arbeiten in
 * Weltkoordinaten; Schreibzugriffe ausserhalb des Chunks werden verworfen, Lesezugriffe liefern dort {@code null}.
 * So kann jeder Chunk dieselbe Gebaeudeplanung abarbeiten und nur seinen Ausschnitt setzen.
 */
public final class CityCanvas {

    public static final int HEIGHT = 256;

    private final BlockState[] blocks;
    public final int minX, minZ;

    public CityCanvas(BlockState[] blocks, int chunkX, int chunkZ) {
        this.blocks = blocks;
        this.minX = chunkX << 4;
        this.minZ = chunkZ << 4;
    }

    public int maxX() {
        return minX + 15;
    }

    public int maxZ() {
        return minZ + 15;
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= minX + 15 && z >= minZ && z <= minZ + 15;
    }

    private int index(int x, int y, int z) {
        return ((x - minX) * 16 + (z - minZ)) * HEIGHT + y;
    }

    public void set(int x, int y, int z, BlockState state) {
        if (y < 1 || y >= HEIGHT || !contains(x, z)) return;
        blocks[index(x, y, z)] = state;
    }

    /** Block im Chunk, sonst null; leere Pufferstellen gelten als Luft. */
    @Nullable
    public BlockState get(int x, int y, int z) {
        if (y < 0 || y >= HEIGHT || !contains(x, z)) return null;
        BlockState s = blocks[index(x, y, z)];
        return s == null ? Blocks.AIR.defaultBlockState() : s;
    }

    public boolean isAir(int x, int y, int z) {
        BlockState s = get(x, y, z);
        return s != null && s.isAir();
    }

    /** Luft, Wasser oder ersetzbare Pflanze. */
    public boolean isOpen(int x, int y, int z) {
        BlockState s = get(x, y, z);
        return s != null && (s.isAir() || !s.getFluidState().isEmpty() || s.canBeReplaced());
    }

    public void setIfOpen(int x, int y, int z, BlockState state) {
        if (isOpen(x, y, z)) set(x, y, z, state);
    }

    public void setIfAir(int x, int y, int z, BlockState state) {
        if (isAir(x, y, z)) set(x, y, z, state);
    }
}
