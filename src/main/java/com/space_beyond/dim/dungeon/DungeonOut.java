package com.space_beyond.dim.dungeon;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ausgabe der Dungeon-Generatoren in lokalen Koordinaten (u = Ost, v = Sued, y relativ zur Grundhoehe). Beim Malen
 * ({@link DungeonPainter}) landen Bloecke im Chunk-Puffer und Plan-Eintraege werden ignoriert; beim Planen landen nur
 * die Plan-Eintraege (Truhen, Spawner, Bewohner) im {@link DungeonPlan}. Gemalt wird nur, was in
 * {@link #uMin()}..{@link #vMax()} liegt - Plan-Eintraege muessen deshalb ausserhalb solcher Abfragen stehen.
 */
public interface DungeonOut {

    void set(int u, int y, int v, BlockState s);

    /** Block oder null ausserhalb des Ausschnitts (bzw. beim Planen). */
    BlockState get(int u, int y, int v);

    double unit(long salt, int u, int y, int v);

    int uMin();

    int uMax();

    int vMin();

    int vMax();

    default void chest(int u, int y, int v, Direction facing, String table) {
    }

    default void barrel(int u, int y, int v, String table) {
    }

    default void spawner(int u, int y, int v, String type) {
    }

    default void mob(int u, int y, int v, String type) {
    }

    /** Siegelwaechter (traegt einen Siegelschluessel). */
    default void elite(int u, int y, int v, String type) {
    }

    /** Pfeilfalle: Werfer, der beim Erzeugen mit Pfeilen gefuellt wird. */
    default void dispenser(int u, int y, int v) {
    }
}
