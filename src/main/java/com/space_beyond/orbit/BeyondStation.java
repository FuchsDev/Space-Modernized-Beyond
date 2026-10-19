package com.space_beyond.orbit;

import java.util.Set;

import com.space_beyond.orbit.interstellar.IFoldDrive;

/**
 * Zusatzzustand einer {@code OrbitalStation} durch Beyond ({@code OrbitalStationMixin}): angemeldete Faltantriebe
 * und die synchronisierten Fehlerflags. Zugriff von aussen ueber {@link StationExt}.
 */
public interface BeyondStation {
    Set<IFoldDrive> beyond$foldDrives();

    /** Sync: mindestens ein Faltantrieb angemeldet. */
    boolean beyond$hasFoldDrive();

    /** Sync: letzter Reiseversuch in ein anderes System scheiterte am fehlenden/ungeladenen Faltantrieb. */
    boolean beyond$foldDriveError();

    /** Sync: Ziel noch gesperrt (Boss der vorherigen Stufe nicht besiegt). */
    boolean beyond$progressionLocked();
}
