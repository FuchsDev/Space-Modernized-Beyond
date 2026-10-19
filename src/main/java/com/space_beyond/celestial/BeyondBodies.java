package com.space_beyond.celestial;

import com.space_m.celestial.SolarSystem;

/**
 * Die beiden Eintraege, die Beyond hinten an {@link SolarSystem.Body} anhaengt ({@code SolarSystemBodyMixin}).
 * Laufwerke (NBT "body") und {@code wand_d} (NBT "dim") speichern die Ordinalzahl: Sporos = 11, Wemburos = 12.
 */
public final class BeyondBodies {
    public static final SolarSystem.Body SPOROS = SolarSystem.Body.valueOf("SPOROS");
    public static final SolarSystem.Body WEMBUROS = SolarSystem.Body.valueOf("WEMBUROS");

    private BeyondBodies() {}
}
