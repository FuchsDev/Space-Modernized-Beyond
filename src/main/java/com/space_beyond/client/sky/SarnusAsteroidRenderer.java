package com.space_beyond.client.sky;

import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyTess;

import com.space_m.celestial.CelestialBody;
import com.space_beyond.celestial.SarnusAsteroidBelt;
import com.space_beyond.celestial.SarnusAsteroidBelt.Asteroid;
import com.space_m.celestial.SolarSystem.AstroMetric;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

/** Renders a fine cloud of orbital dust grains on Sarnus's ring plane. */
public final class SarnusAsteroidRenderer {

    private SarnusAsteroidRenderer() {}

    public static void render(ClientLevel level, float partialTicks, CelestialBody body, AstroMetric metric, double size, float visibility) {
        if (!"sarnus".equals(body.name) || size <= 0.01D) return;

        double ticks = level.getGameTime() + partialTicks;
        SkyGL.push();
        SkyGL.translate(0.0F, 100.0F, 0.0F);
        SkyGL.rotate(-metric.angle, 0, 0, 1);
        SkyGL.rotate(90.0F - body.ringTilt, 1, 0, 0);
        SkyGL.rotate(metric.angle, 0, 1, 0);
        SkyGL.disableTexture();
        SkyTess.startDrawingQuads();

        for (Asteroid asteroid : SarnusAsteroidBelt.asteroids()) {
            addAsteroid(asteroid, ticks, size, visibility);
        }

        SkyTess.draw();
        SkyGL.enableTexture();
        SkyGL.color(1, 1, 1, 1);
        SkyGL.pop();
    }

    private static void addAsteroid(Asteroid asteroid, double ticks, double size, float visibility) {
        var position = asteroid.positionAt(ticks);
        double grainSize = size * asteroid.radius;
        double angle = Math.atan2(position.z, position.x);
        double radialX = Math.cos(angle) * grainSize;
        double radialZ = Math.sin(angle) * grainSize;
        double tangentX = -Math.sin(angle) * grainSize;
        double tangentZ = Math.cos(angle) * grainSize;
        double x = position.x * size;
        double y = position.y * size;
        double z = position.z * size;
        float brightness = asteroid.color * (0.78F + 0.22F * (float) Math.sin(asteroid.phase + ticks * asteroid.spin));

        SkyGL.color(brightness, brightness * 0.82F, brightness * 0.62F, Mth.clamp(visibility * 0.8F, 0.0F, 1.0F));
        SkyTess.addVertex(x - radialX - tangentX, y, z - radialZ - tangentZ);
        SkyTess.addVertex(x + radialX - tangentX, y, z + radialZ - tangentZ);
        SkyTess.addVertex(x + radialX + tangentX, y, z + radialZ + tangentZ);
        SkyTess.addVertex(x - radialX + tangentX, y, z - radialZ + tangentZ);
    }
}
