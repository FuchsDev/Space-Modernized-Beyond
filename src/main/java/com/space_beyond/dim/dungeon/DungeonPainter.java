package com.space_beyond.dim.dungeon;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.structure.Frame;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Zeichenrahmen eines Dungeons im Chunk-Puffer: lokale (u, y, v) relativ zu Mittelpunkt und Grundhoehe, gedreht.
 * Bausteine: Raeume mit Huelle ({@link #room}), Gaenge, Treppen, Schaechte; "offen" heisst Luft oder (unter dem
 * Meeresspiegel des Planeten, z. B. Laythe) Wasser, je nach {@link DungeonDesign#open}.
 */
public final class DungeonPainter implements DungeonOut {

    public final CityCanvas c;
    public final PlanetDungeons.Dungeon d;
    public final DungeonDesign design;
    public final int base;
    private final Rotation rot;
    /** Ausschnitt des Chunks in lokalen Koordinaten. */
    private final int uMin, uMax, vMin, vMax;

    DungeonPainter(CityCanvas c, DungeonPlan p, DungeonDesign design) {
        this.c = c;
        this.d = p.d;
        this.design = design;
        this.base = p.base;
        this.rot = Frame.rotation(p.d.rotation());
        int[] a = local(c.minX, c.minZ), b = local(c.maxX(), c.maxZ());
        uMin = Math.min(a[0], b[0]);
        uMax = Math.max(a[0], b[0]);
        vMin = Math.min(a[1], b[1]);
        vMax = Math.max(a[1], b[1]);
    }

    /** Welt (x, z) -> lokal (u, v), Umkehrung von {@link PlanetDungeons.Dungeon#wx}/{@link PlanetDungeons.Dungeon#wz}. */
    private int[] local(int x, int z) {
        int dx = x - d.x(), dz = z - d.z();
        return switch (d.rotation()) {
            case 1 -> new int[] {dz, -dx};
            case 2 -> new int[] {-dx, -dz};
            case 3 -> new int[] {-dz, dx};
            default -> new int[] {dx, dz};
        };
    }

    @Override
    public int uMin() {
        return uMin;
    }

    @Override
    public int uMax() {
        return uMax;
    }

    @Override
    public int vMin() {
        return vMin;
    }

    @Override
    public int vMax() {
        return vMax;
    }

    public static BlockState b(Block block) {
        return block.defaultBlockState();
    }

    @Override
    public void set(int u, int y, int v, BlockState st) {
        c.set(d.wx(u, v), base + y, d.wz(u, v), rot == Rotation.NONE ? st : st.rotate(rot));
    }

    /** Luft (bzw. Wasser im Meer, s. {@link DungeonDesign#outside}) - fuer das Gelaende ausserhalb von Raeumen. */
    public void clear(int u, int y, int v) {
        c.set(d.wx(u, v), base + y, d.wz(u, v), design.outside(base + y));
    }

    /** Luft im Inneren eines Raums (immer trocken). */
    public void air(int u, int y, int v) {
        c.set(d.wx(u, v), base + y, d.wz(u, v), Blocks.AIR.defaultBlockState());
    }

    @Nullable
    @Override
    public BlockState get(int u, int y, int v) {
        return c.get(d.wx(u, v), base + y, d.wz(u, v));
    }

    public boolean isAir(int u, int y, int v) {
        BlockState s = get(u, y, v);
        return s == null || s.isAir();
    }

    public void setIfAir(int u, int y, int v, BlockState st) {
        if (isAir(u, y, v)) set(u, y, v, st);
    }

    public void fill(int u0, int y0, int v0, int u1, int y1, int v1, BlockState st) {
        for (int u = Math.min(u0, u1); u <= Math.max(u0, u1); u++)
            for (int v = Math.min(v0, v1); v <= Math.max(v0, v1); v++)
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) set(u, y, v, st);
    }

    public void hollow(int u0, int y0, int v0, int u1, int y1, int v1) {
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) for (int y = y0; y <= y1; y++) air(u, y, v);
    }

    /** Raum mit Huelle (Waende {@code wall}, Boden {@code floor}, Decke {@code ceil}), Inneres trocken. */
    public void room(int u0, int y0, int v0, int u1, int y1, int v1, BlockState wall, BlockState floor, BlockState ceil) {
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) for (int y = y0; y <= y1; y++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            if (y == y0) set(u, y, v, floor);
            else if (y == y1) set(u, y, v, ceil);
            else if (edge) set(u, y, v, wall);
            else air(u, y, v);
        }
    }

    /** Gerader Gang entlang v (Breite 2w+1, Hoehe h) von v0 nach v1 auf Bodenhoehe y. */
    public void corridorV(int u, int y, int v0, int v1, int w, int h, BlockState wall, BlockState floor) {
        room(u - w - 1, y - 1, Math.min(v0, v1), u + w + 1, y + h, Math.max(v0, v1), wall, floor, wall);
    }

    /** Gerader Gang entlang u. */
    public void corridorU(int v, int y, int u0, int u1, int w, int h, BlockState wall, BlockState floor) {
        room(Math.min(u0, u1), y - 1, v - w - 1, Math.max(u0, u1), y + h, v + w + 1, wall, floor, wall);
    }

    /** Durchbruch (Tuer) in eine Wand: Luft u0..u1, v0..v1, y..y+h-1. */
    public void door(int u0, int v0, int u1, int v1, int y, int h) {
        for (int u = Math.min(u0, u1); u <= Math.max(u0, u1); u++)
            for (int v = Math.min(v0, v1); v <= Math.max(v0, v1); v++)
                for (int yy = y; yy < y + h; yy++) air(u, yy, v);
    }

    /**
     * Treppenstollen nach Norden (-v): Stufe k bei v = v0 - k, Hoehe y0 - k, k = 0..depth. Breite 3, Kopfhoehe 4,
     * Huelle {@code wall}, Licht alle 4 Stufen.
     */
    public void stairsDown(int u, int y0, int v0, int depth, BlockState wall, Block stair, BlockState light) {
        for (int k = 0; k <= depth; k++) {
            int v = v0 - k, y = y0 - k;
            for (int du = -2; du <= 2; du++) {
                for (int dy = -1; dy <= 5; dy++) {
                    boolean shell = Math.abs(du) == 2 || dy == -1 || dy == 5;
                    if (shell) {
                        if (y + dy < 0 || Math.abs(du) == 2) set(u + du, y + dy, v, wall);
                    } else if (dy == 0) {
                        set(u + du, y, v, k == depth ? wall : CityPalette.stairs(stair, Direction.SOUTH, false));
                    } else air(u + du, y + dy, v);
                }
            }
            if (k % 4 == 2) {
                set(u - 2, y + 3, v, light);
                set(u + 2, y + 3, v, light);
            }
        }
    }

    /** Zufall 0..1, deterministisch aus Dungeon-Seed und Position. */
    @Override
    public double unit(long salt, int u, int y, int v) {
        return CityRandom.unit(d.seed() ^ salt, u, y, v);
    }

    public int range(long salt, int u, int y, int v, int bound) {
        return (int) Math.floor(unit(salt, u, y, v) * bound);
    }
}
