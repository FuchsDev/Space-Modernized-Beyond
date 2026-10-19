package com.space_beyond.dim.dungeon;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bauplan eines Planeten-Dungeons (je Koerper einer, s. {@link PlanetDungeons#register}).
 */
public interface DungeonDesign {

    Stage stage();

    /** Halbe Kantenlaenge des Grundrisses (Bloecke). */
    int radius();

    /** Gelaende im Grundriss: oberster Block und Fuellung. */
    BlockState surface();

    BlockState filler();

    /** Darf der Dungeon in diesem Biom stehen? */
    default boolean fits(BiomeGenBaseCelestial biome) {
        return true;
    }

    /** Standort-Pruefung (Standard: nur das Biom); z. B. Sporos meidet Staedte. */
    default boolean fitsAt(com.space_m.dim.CelestialWorld world, int x, int z) {
        return fits(world.getBiomeGenForCoords(x, z));
    }

    /** Was ausserhalb von Raeumen auf Hoehe {@code y} "leer" ist (Luft, unter dem Meeresspiegel ggf. Wasser). */
    default BlockState outside(int y) {
        return Blocks.AIR.defaultBlockState();
    }

    /** Untere/obere Grenze der Grundhoehe (unterirdische Raeume brauchen Platz nach unten). */
    default int minBase() {
        return 50;
    }

    default int maxBase() {
        return 190;
    }

    /** Beutestellen, Spawner, Bewohner und Boss eintragen. */
    void plan(DungeonPlan plan);

    /** Bauwerk in den Chunk-Puffer zeichnen. */
    void paint(DungeonPainter w);

    /** Alle Bloecke in eine beliebige Ausgabe zeichnen (ohne Plan-Eintraege), z. B. fuer die Gelaende-Huelle. */
    default void trace(DungeonOut out) {
    }

    /** Tiefster und hoechster benutzter y-Wert (relativ), fuer die Nachbearbeitung der Block-Entities. */
    default int minY() {
        return -36;
    }

    default int maxY() {
        return 24;
    }
}
