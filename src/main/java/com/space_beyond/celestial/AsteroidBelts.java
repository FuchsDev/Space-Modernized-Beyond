package com.space_beyond.celestial;

import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.phys.Vec3;

/**
 * Asteroidenguertel (nur Darstellung: Sternkarte und Himmel): je Stern eine Liste von Guerteln, jeder Guertel eine feste
 * Menge Gesteinsbrocken mit eigener Kepler-Bahn (zufaellig, aber je Guertel mit festem Seed immer gleich). Die Brocken
 * kreisen wie Planeten (innen schneller als aussen); Positionen im selben heliozentrischen km-Rahmen wie
 * {@code SolarSystem.calculatePosition}.
 */
public final class AsteroidBelts {

    private AsteroidBelts() {}

    /** Guertel: Bahnradien (km), Anzahl Brocken, maximale Neigung, zwei Farben (Mischung je Brocken), Lang-Schluessel. */
    public record Belt(String langKey, float innerKm, float outerKm, int count, float maxInclinationDeg, float maxEccentricity,
                       float[] colorA, float[] colorB, long seed) {
        /** Mittlerer Abstand benachbarter Brocken (km), fuer die Sichtbarkeit am Himmel. */
        public double spacingKm() {
            double area = Math.PI * ((double) outerKm * outerKm - (double) innerKm * innerKm);
            return Math.sqrt(area / Math.max(1, count));
        }
    }

    /** Ein Brocken: Bahnelemente (Winkel in Bogenmass), Startanomalie, Umlaufzeit in Ticks, Helligkeit, Groesse, Farbe. */
    public record Rock(float semiMajorAxisKm, float eccentricity, float semiMinorAxisFactor, float argumentPeriapsis, float inclination,
                       float ascendingNode, double meanAnomaly0, double periodTicks, float brightness, float size, float r, float g, float b) {

        public double meanAnomalyAt(double worldTicks) {
            return meanAnomaly0 + (periodTicks > 0D ? 2D * Math.PI * (worldTicks / periodTicks) : 0D);
        }

        /** Heliozentrische Position (km) wie {@code SolarSystem.calculatePosition}. */
        public Vec3 positionAt(double worldTicks) {
            double m = meanAnomalyAt(worldTicks);
            double e = m;
            for (int i = 0; i < 4; i++) e = m + eccentricity * Math.sin(e);
            double x = semiMajorAxisKm * (Math.cos(e) - eccentricity);
            double y = semiMajorAxisKm * semiMinorAxisFactor * Math.sin(e);
            double px = x;
            x = Math.cos(argumentPeriapsis) * px - Math.sin(argumentPeriapsis) * y;
            y = Math.sin(argumentPeriapsis) * px + Math.cos(argumentPeriapsis) * y;
            double z = Math.sin(inclination) * y;
            y = Math.cos(inclination) * y;
            px = x;
            x = Math.cos(ascendingNode) * px - Math.sin(ascendingNode) * y;
            y = Math.sin(ascendingNode) * px + Math.cos(ascendingNode) * y;
            return new Vec3(x, y, z);
        }
    }

    /** Heimatsystem: Hauptguertel zwischen Mars und Jupiter (mit Ceres), Kuipergürtel hinter Neptun (mit Pluto, Haumea, Makemake). */
    private static final Map<String, List<Belt>> BELTS = Map.of(
        "kerbol", List.of(
            new Belt("gui.stardar.belt.main", 27_000_000F, 52_000_000F, 900, 9F, 0.08F,
                new float[] {0.62F, 0.55F, 0.47F}, new float[] {0.45F, 0.42F, 0.40F}, 0x5A57E4L),
            new Belt("gui.stardar.belt.kuiper", 430_000_000F, 700_000_000F, 1100, 14F, 0.10F,
                new float[] {0.62F, 0.74F, 0.86F}, new float[] {0.78F, 0.70F, 0.62F}, 0xC0BE1L)
        )
    );

    private static final Map<Belt, Rock[]> ROCKS = new HashMap<>();

    public static List<Belt> of(CelestialBody star) {
        if (star == null || star.name == null) return List.of();
        return BELTS.getOrDefault(star.name, List.of());
    }

    public static Rock[] rocks(Belt belt, CelestialBody star) {
        return ROCKS.computeIfAbsent(belt, b -> generate(b, star));
    }

    private static Rock[] generate(Belt belt, CelestialBody star) {
        Random r = new Random(belt.seed());
        Rock[] rocks = new Rock[belt.count()];
        for (int i = 0; i < rocks.length; i++) {
            // Dichte zur Mitte hin hoeher (Mittel zweier Zufallszahlen), Raender ausgefranst
            float t = (r.nextFloat() + r.nextFloat()) * 0.5F;
            float a = belt.innerKm() + (belt.outerKm() - belt.innerKm()) * t;
            float e = r.nextFloat() * belt.maxEccentricity();
            float incl = (float) Math.toRadians((r.nextFloat() * 2F - 1F) * belt.maxInclinationDeg() * r.nextFloat());
            double aM = a * 1000D;
            double periodDays = 2D * Math.PI * Math.sqrt(aM * aM * aM / (AstronomyUtil.GRAVITATIONAL_CONSTANT * (double) star.massKg))
                / (double) AstronomyUtil.SECONDS_IN_KSP_DAY;
            float mix = r.nextFloat();
            float[] ca = belt.colorA(), cb = belt.colorB();
            boolean big = r.nextFloat() < 0.05F;
            rocks[i] = new Rock(a, e, (float) Math.sqrt(1F - e * e), r.nextFloat() * 6.2832F, incl, r.nextFloat() * 6.2832F,
                r.nextDouble() * Math.PI * 2D, periodDays * AstronomyUtil.TICKS_IN_DAY,
                0.35F + r.nextFloat() * 0.65F, big ? 1.1F : 0.45F + r.nextFloat() * 0.35F,
                ca[0] + (cb[0] - ca[0]) * mix, ca[1] + (cb[1] - ca[1]) * mix, ca[2] + (cb[2] - ca[2]) * mix);
        }
        return rocks;
    }
}
