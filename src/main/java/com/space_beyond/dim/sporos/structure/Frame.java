package com.space_beyond.dim.sporos.structure;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.structure.SporosStructures.Structure;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lokales Koordinatensystem einer Struktur auf einem Chunk-Puffer: {@code (u, v)} relativ zum Mittelpunkt, {@code y}
 * relativ zur Grundhoehe (0 = Bodenblock, 1 = erste Ebene darauf), Rotation 0-3 um den Mittelpunkt
 * ({@link Rotation#CLOCKWISE_90} usw.; Blockzustaende mit Richtung/Achse werden mitgedreht). Schreibzugriffe
 * ausserhalb des Chunks verwirft die {@link CityCanvas}.
 */
public final class Frame {

    public final CityCanvas c;
    public final Structure s;
    public final int base;
    private final Rotation rot;
    private final long seed;

    public Frame(CityCanvas c, StructurePlan plan) {
        this.c = c;
        this.s = plan.s;
        this.base = plan.base;
        this.rot = rotation(s.rotation());
        this.seed = s.seed();
    }

    public static Rotation rotation(int r) {
        return switch (Math.floorMod(r, 4)) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    /** Lokale (u, v) -> Welt-x (CLOCKWISE_90: (u, v) -> (-v, u), passend zu {@code Direction.rotate}). */
    public static int worldX(Structure s, int u, int v) {
        return s.x() + switch (Math.floorMod(s.rotation(), 4)) {
            case 1 -> -v;
            case 2 -> -u;
            case 3 -> v;
            default -> u;
        };
    }

    public static int worldZ(Structure s, int u, int v) {
        return s.z() + switch (Math.floorMod(s.rotation(), 4)) {
            case 1 -> u;
            case 2 -> -v;
            case 3 -> -u;
            default -> v;
        };
    }

    public int wx(int u, int v) {
        return worldX(s, u, v);
    }

    public int wz(int u, int v) {
        return worldZ(s, u, v);
    }

    public Direction dir(Direction d) {
        return rot.rotate(d);
    }

    public BlockState rotate(BlockState state) {
        return rot == Rotation.NONE ? state : state.rotate(rot);
    }

    public boolean contains(int u, int v) {
        return c.contains(wx(u, v), wz(u, v));
    }

    public void set(int u, int y, int v, BlockState state) {
        c.set(wx(u, v), base + y, wz(u, v), rotate(state));
    }

    public void setIfOpen(int u, int y, int v, BlockState state) {
        c.setIfOpen(wx(u, v), base + y, wz(u, v), rotate(state));
    }

    public void setIfAir(int u, int y, int v, BlockState state) {
        c.setIfAir(wx(u, v), base + y, wz(u, v), rotate(state));
    }

    public void air(int u, int y, int v) {
        c.set(wx(u, v), base + y, wz(u, v), Blocks.AIR.defaultBlockState());
    }

    @Nullable
    public BlockState get(int u, int y, int v) {
        return c.get(wx(u, v), base + y, wz(u, v));
    }

    public boolean isAir(int u, int y, int v) {
        return c.isAir(wx(u, v), base + y, wz(u, v));
    }

    /** Quader (einschliesslich beider Ecken) fuellen. */
    public void fill(int u0, int y0, int v0, int u1, int y1, int v1, BlockState state) {
        for (int u = Math.min(u0, u1); u <= Math.max(u0, u1); u++)
            for (int v = Math.min(v0, v1); v <= Math.max(v0, v1); v++)
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) set(u, y, v, state);
    }

    /** Hohlen Quader (nur Waende, ohne Boden/Decke). */
    public void walls(int u0, int y0, int v0, int u1, int y1, int v1, BlockState state) {
        for (int u = Math.min(u0, u1); u <= Math.max(u0, u1); u++)
            for (int v = Math.min(v0, v1); v <= Math.max(v0, v1); v++) {
                boolean edge = u == Math.min(u0, u1) || u == Math.max(u0, u1) || v == Math.min(v0, v1) || v == Math.max(v0, v1);
                if (!edge) continue;
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) set(u, y, v, state);
            }
    }

    /** Deterministischer Zufall je Stelle (0..1). */
    public double unit(int u, int y, int v) {
        return CityRandom.unit(seed, u, y, v);
    }

    public double unit(long salt, int u, int y, int v) {
        return CityRandom.unit(seed ^ salt, u, y, v);
    }
}
