package com.space_beyond.orbit;

import com.space_beyond.celestial.StarSystem;
import com.space_beyond.orbit.interstellar.IFoldDrive;
import com.space_m.orbit.OrbitalStation;
import com.space_m.orbit.OrbitalStation.StationState;

import net.minecraft.world.level.block.entity.BlockEntity;

/** Zugriff auf den Beyond-Zustand einer Station ({@link BeyondStation}, angelegt von {@code OrbitalStationMixin}). */
public final class StationExt {
    private StationExt() {}

    public static BeyondStation of(OrbitalStation station) {
        return (BeyondStation) station;
    }

    public static boolean hasFoldDrive(OrbitalStation station) {
        return of(station).beyond$hasFoldDrive();
    }

    public static boolean foldDriveError(OrbitalStation station) {
        return of(station).beyond$foldDriveError();
    }

    public static boolean progressionLocked(OrbitalStation station) {
        return of(station).beyond$progressionLocked();
    }

    public static int foldDriveCount(OrbitalStation station) {
        return of(station).beyond$foldDrives().size();
    }

    /** Faltphase zwischen zwei Sternsystemen (Himmel nur Sterne, keine Sonnenleistung). */
    public static boolean isInterstellarTransfer(OrbitalStation station) {
        return station.state == StationState.TRANSFER && !StarSystem.sameSystem(station.orbiting, station.target);
    }

    /** Mindestens ein angemeldeter, voll geladener Faltantrieb. */
    public static boolean hasChargedFoldDrive(OrbitalStation station) {
        for (IFoldDrive drive : of(station).beyond$foldDrives()) {
            if (drive.isCharged()) return true;
        }
        return false;
    }

    /** Ladung des ersten geladenen Faltantriebs verbrauchen (Start einer interstellaren Reise). */
    public static void consumeFoldCharge(OrbitalStation station) {
        for (IFoldDrive drive : of(station).beyond$foldDrives()) {
            if (drive.isCharged()) {
                drive.consumeCharge();
                return;
            }
        }
    }

    public static void addFoldDrive(IFoldDrive drive) {
        BlockEntity te = drive.getBlockEntity();
        OrbitalStation station = OrbitalStation.getStationFromPosition(te.getBlockPos().getX(), te.getBlockPos().getZ());
        of(station).beyond$foldDrives().add(drive);
    }

    public static void removeFoldDrive(IFoldDrive drive) {
        BlockEntity te = drive.getBlockEntity();
        OrbitalStation station = OrbitalStation.getStationFromPosition(te.getBlockPos().getX(), te.getBlockPos().getZ());
        of(station).beyond$foldDrives().remove(drive);
    }
}
