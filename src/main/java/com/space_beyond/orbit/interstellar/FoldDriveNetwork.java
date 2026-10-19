package com.space_beyond.orbit.interstellar;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_beyond.celestial.StarSystem;
import com.space_m.orbit.OrbitalStation;

/**
 * Der interstellare Antrieb des Addons (eigene Erweiterung): meldet sich ueber
 * {@link StarSystem#setInterstellarDrive} an (Mod-Start, {@link #init()}) und schaltet Reisen zwischen
 * Sternsystemen frei - aber <b>nur</b> fuer eine Raumstation mit geladenem Faltantrieb ({@link IFoldDrive}).
 *
 * <p>{@link StarSystem.InterstellarDrive} fragt nur Koerperpaare; welche Station gerade rechnet, setzt
 * {@link OrbitalStation} fuer die Dauer ihrer Delta-v-Rechnung ueber {@link #withStation}. Ohne diesen Kontext
 * (Raketen, Transporter, Kapseln, Stardar ...) bleibt jedes andere System gesperrt
 * ({@link Double#POSITIVE_INFINITY} Delta-v -> {@code Integer.MAX_VALUE} Treibstoff).
 *
 * <p>Delta-v einer Faltreise: Der Faltantrieb ueberbrueckt die Lichtjahre; die normalen Triebwerke der Station
 * muessen nur aus dem Parkorbit des Startplaneten auf Fluchtgeschwindigkeit und am Ziel wieder einfangen:
 * je Planet {@code (sqrt(2) - 1) * sqrt(GM / (R + 100 km))} (Kerbin ~930 m/s, Sporos als Erdwelt ~3 250 m/s).
 * Reisedauer {@link #TRANSFER_TICKS} (feste lange Faltphase statt der schubabhaengigen Hohmann-Zeit).
 */
public final class FoldDriveNetwork implements StarSystem.InterstellarDrive {

    public static final FoldDriveNetwork INSTANCE = new FoldDriveNetwork();

    /** Dauer der Faltphase (TRANSFER) einer interstellaren Reise: 12 000 Ticks = 10 Minuten. */
    public static final int TRANSFER_TICKS = 12_000;

    private static final ThreadLocal<OrbitalStation> STATION = new ThreadLocal<>();

    private FoldDriveNetwork() {}

    /** Mod-Start (beide Seiten): Antrieb anmelden. */
    public static void init() {
        StarSystem.setInterstellarDrive(INSTANCE);
    }

    /** Fuehrt eine Rechnung im Kontext einer Station aus (nur diese darf ein anderes System anfliegen). */
    public static <T> T withStation(OrbitalStation station, Supplier<T> calculation) {
        OrbitalStation previous = STATION.get();
        STATION.set(station);
        try {
            return calculation.get();
        } finally {
            if (previous == null) STATION.remove(); else STATION.set(previous);
        }
    }

    @Nullable
    public static OrbitalStation currentStation() {
        return STATION.get();
    }

    @Override
    public boolean canTravel(CelestialBody from, CelestialBody to) {
        OrbitalStation station = STATION.get();
        if (station == null || station.orbiting != from) return false;
        if (from.parent == null || to.parent == null) return false; // Sterne kann keine Station umkreisen
        return com.space_beyond.orbit.StationExt.hasChargedFoldDrive(station);
    }

    @Override
    public double deltaV(CelestialBody from, CelestialBody to) {
        return escapeBurn(from) + escapeBurn(to);
    }

    /** Flucht aus dem Parkorbit des Planeten (Mond -> sein Planet), {@code (sqrt 2 - 1) * v_orbit}. */
    public static double escapeBurn(CelestialBody body) {
        CelestialBody planet = body.getPlanet();
        double mu = (double) AstronomyUtil.GRAVITATIONAL_CONSTANT * planet.massKg;
        double r = ((double) planet.radiusKm + AstronomyUtil.DEFAULT_ALTITUDE_KM) * 1_000D;
        return (Math.sqrt(2D) - 1D) * Math.sqrt(mu / r);
    }
}
