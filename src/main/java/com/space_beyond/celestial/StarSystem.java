package com.space_beyond.celestial;

import com.space_m.celestial.CelestialBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * Sternsystem: ein Stern ({@link CelestialBody} ohne Eltern) mit seinem Koerperbaum und einer Position in der
 * Galaxie-Ebene. Eigene Erweiterung von Space-Modernized (kein NTM-Original).
 *
 * <p>Galaxie-Koordinaten in Lichtjahren, Heimatsystem (Kerbol) bei (0, 0). {@code +X} ist auf allen Karten "rechts",
 * {@code +Y} "oben". Die Koerper eines Systems sind genau die Nachfahren seines Sterns ({@link CelestialBody#getStar()}),
 * es gibt also keine zweite Buchfuehrung: ein spaeterer Planet (z.B. Sporos) wird einfach per
 * {@code withSatellites(...)} an den Stern gehaengt.
 *
 * <p>Reisen zwischen Systemen laufen ueber {@link InterstellarDrive}; Standard ist {@link InterstellarDrive#LOCKED}
 * (gesperrt), d.h. {@link SolarSystem#getDeltaVBetween} liefert fuer Ziele in anderen Systemen
 * {@link Double#POSITIVE_INFINITY} und damit {@code Integer.MAX_VALUE} Treibstoff.
 */
public final class StarSystem {

    /** 1 Lichtjahr in km (IAU). */
    public static final double KM_PER_LIGHT_YEAR = 9.4607304725808e12;

    private static final List<StarSystem> SYSTEMS = new ArrayList<>();

    private static volatile InterstellarDrive interstellarDrive = InterstellarDrive.LOCKED;

    /** Kennung (Platzhalter fuer Lang-Schluessel {@code system.<id>}). */
    public final String id;
    public final CelestialBody star;
    /** Position in der Galaxie-Ebene, Lichtjahre ({@code +X} = rechts). */
    public final double galaxyX;
    public final double galaxyY;
    /** Leuchtkraft relativ zu Kerbol (Heimat = 1). Noch ungenutzt, fuer die Sonnenleistung spaeterer Planeten. */
    public final double luminosity;

    private StarSystem(String id, CelestialBody star, double galaxyX, double galaxyY, double luminosity) {
        this.id = id;
        this.star = star;
        this.galaxyX = galaxyX;
        this.galaxyY = galaxyY;
        this.luminosity = luminosity;
    }

    /** Meldet ein System an (aus {@link SolarSystem#init()}); das erste ist das Heimatsystem. */
    static synchronized StarSystem register(String id, CelestialBody star, double galaxyX, double galaxyY, double luminosity) {
        if (star.parent != null) throw new IllegalArgumentException("Stern " + star.name + " hat einen Elternkoerper");
        for (StarSystem system : SYSTEMS) {
            if (system.star == star || system.id.equals(id)) throw new IllegalStateException("Sternsystem doppelt: " + id);
        }
        StarSystem system = new StarSystem(id, star, galaxyX, galaxyY, luminosity);
        SYSTEMS.add(system);
        return system;
    }

    /** Alle Systeme, Heimatsystem zuerst. */
    public static List<StarSystem> all() {
        return Collections.unmodifiableList(SYSTEMS);
    }

    /** Heimatsystem (Kerbol), null vor {@link SolarSystem#init()}. */
    @Nullable
    public static StarSystem home() {
        return SYSTEMS.isEmpty() ? null : SYSTEMS.get(0);
    }

    @Nullable
    public static StarSystem byId(String id) {
        for (StarSystem system : SYSTEMS) if (system.id.equals(id)) return system;
        return null;
    }

    /** System eines Koerpers (ueber seinen Stern), null fuer unbekannte Sterne. */
    @Nullable
    public static StarSystem of(@Nullable CelestialBody body) {
        if (body == null) return null;
        CelestialBody star = body.getStar();
        for (StarSystem system : SYSTEMS) if (system.star == star) return system;
        return null;
    }

    public boolean isHome() {
        return home() == this;
    }

    /** Stern und alle Nachfahren (Tiefensuche, Original-Reihenfolge). */
    public List<CelestialBody> bodies() {
        List<CelestialBody> list = new ArrayList<>();
        collect(star, list);
        return list;
    }

    private static void collect(CelestialBody body, List<CelestialBody> list) {
        list.add(body);
        for (CelestialBody child : body.satellites) collect(child, list);
    }

    /** Abstand in Lichtjahren. */
    public double distanceLightYears(StarSystem other) {
        double dx = other.galaxyX - galaxyX;
        double dy = other.galaxyY - galaxyY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceKm(StarSystem other) {
        return distanceLightYears(other) * KM_PER_LIGHT_YEAR;
    }

    /** Liegen beide Koerper im selben System (gleicher Stern)? */
    public static boolean sameSystem(CelestialBody a, CelestialBody b) {
        return a.getStar() == b.getStar();
    }

    // ── Interstellare Reise (Erweiterungspunkt) ─────────────────────────────────────────────────

    public enum Reachability {
        /** Ziel liegt im selben System, normale Hohmann-Rechnung. */
        SAME_SYSTEM,
        /** Anderes System, der angemeldete interstellare Antrieb erlaubt die Reise. */
        INTERSTELLAR,
        /** Anderes System, kein (freigeschalteter) interstellarer Antrieb. */
        NEEDS_INTERSTELLAR_DRIVE
    }

    public static Reachability reachability(CelestialBody from, CelestialBody to) {
        if (sameSystem(from, to)) return Reachability.SAME_SYSTEM;
        return interstellarDrive.canTravel(from, to) ? Reachability.INTERSTELLAR : Reachability.NEEDS_INTERSTELLAR_DRIVE;
    }

    public static boolean isReachable(CelestialBody from, CelestialBody to) {
        return reachability(from, to) != Reachability.NEEDS_INTERSTELLAR_DRIVE;
    }

    public static InterstellarDrive interstellarDrive() {
        return interstellarDrive;
    }

    /** Spaeter vom interstellaren Antrieb gesetzt; null setzt auf {@link InterstellarDrive#LOCKED} zurueck. */
    public static void setInterstellarDrive(@Nullable InterstellarDrive drive) {
        interstellarDrive = drive != null ? drive : InterstellarDrive.LOCKED;
    }

    /**
     * Interstellarer Antrieb (Erweiterungspunkt). Wird nur fuer Koerperpaare in verschiedenen Systemen gefragt.
     */
    public interface InterstellarDrive {

        /** Standard: alles gesperrt. */
        InterstellarDrive LOCKED = new InterstellarDrive() {
            @Override
            public boolean canTravel(CelestialBody from, CelestialBody to) {
                return false;
            }

            @Override
            public double deltaV(CelestialBody from, CelestialBody to) {
                return Double.POSITIVE_INFINITY;
            }
        };

        boolean canTravel(CelestialBody from, CelestialBody to);

        /** Delta-v (m/s) der ganzen Reise; {@link Double#POSITIVE_INFINITY} = unmoeglich. */
        double deltaV(CelestialBody from, CelestialBody to);
    }

    @Override
    public String toString() {
        return "StarSystem[" + id + ", " + star.name + " @ " + galaxyX + "/" + galaxyY + " ly]";
    }
}
