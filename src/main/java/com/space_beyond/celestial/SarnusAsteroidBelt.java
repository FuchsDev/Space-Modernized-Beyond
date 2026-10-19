package com.space_beyond.celestial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.world.phys.Vec3;

/** Deterministic orbital data for the visible asteroid belt around Sarnus. */
public final class SarnusAsteroidBelt {

    private static final int ASTEROID_COUNT = 720;
    private static final List<Asteroid> ASTEROIDS = createAsteroids();

    private SarnusAsteroidBelt() {}

    public static List<Asteroid> asteroids() {
        return ASTEROIDS;
    }

    private static List<Asteroid> createAsteroids() {
        Random random = new Random(125_798_522L);
        List<Asteroid> asteroids = new ArrayList<>(ASTEROID_COUNT);
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            asteroids.add(new Asteroid(
                    1.12 + random.nextDouble() * 1.78,
                    random.nextDouble() * Math.PI * 2,
                    0.0008 + random.nextDouble() * 0.0022,
                    0.002 + random.nextDouble() * 0.005,
                    Math.toRadians(1.0 + random.nextDouble() * 7.0),
                    0.52F + random.nextFloat() * 0.3F,
                    0.004 + random.nextDouble() * 0.012));
        }
        return Collections.unmodifiableList(asteroids);
    }

    public static final class Asteroid {
        public final double orbitRadius;
        public final double phase;
        public final double angularVelocity;
        public final double radius;
        public final double inclination;
        public final float color;
        public final double spin;

        private Asteroid(double orbitRadius, double phase, double angularVelocity, double radius,
                         double inclination, float color, double spin) {
            this.orbitRadius = orbitRadius;
            this.phase = phase;
            this.angularVelocity = angularVelocity;
            this.radius = radius;
            this.inclination = inclination;
            this.color = color;
            this.spin = spin;
        }

        /** Position in planet-radius units, with the rocks following slowly changing orbits. */
        public Vec3 positionAt(double ticks) {
            double angle = phase + ticks * angularVelocity;
            return new Vec3(
                    orbitRadius * Math.cos(angle),
                    orbitRadius * Math.sin(inclination) * Math.sin(angle * 1.7 + phase),
                    orbitRadius * Math.sin(angle));
        }

    }
}
