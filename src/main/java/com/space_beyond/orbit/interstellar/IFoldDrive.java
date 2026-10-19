package com.space_beyond.orbit.interstellar;

import com.space_m.orbit.OrbitalStation;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Interstellarer Antrieb einer Raumstation (eigene Erweiterung, kein NTM-Original). Gegenstueck zu
 * {@code IPropulsion}: meldet sich bei der Station unter dem Block an ({@link OrbitalStation#addFoldDrive}); die
 * Station braucht fuer Ziele in einem anderen Sternsystem mindestens einen geladenen Faltantrieb und zusaetzlich
 * normale Triebwerke fuer Flucht- und Einfangmanoever.
 */
public interface IFoldDrive {

    BlockEntity getBlockEntity();

    /** Voll geladen und sprungbereit. */
    boolean isCharged();

    /** Ladung fuer einen Sprung verbrauchen (beim Start der Reise). */
    void consumeCharge();

    default void registerFoldDrive() {
        com.space_beyond.orbit.StationExt.addFoldDrive(this);
    }

    default void unregisterFoldDrive() {
        com.space_beyond.orbit.StationExt.removeFoldDrive(this);
    }
}
