package com.space_beyond.client.orrery;

import java.util.Random;

import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyTess;

/** Saturnring des Planetariums: 3 200 Partikel in fuenf Baendern, langsam umlaufend (Matrix des Saturnwuerfels). */
public final class SarnusOrreryRing {
    private static final RingParticle[] RING = create();

    private SarnusOrreryRing() {}

    private static RingParticle[] create() {
        Random random = new Random(125_798_522L);
        RingParticle[] particles = new RingParticle[3_200];
        for (int i = 0; i < particles.length; i++) {
            double radius;
            switch (random.nextInt(5)) {
                case 0 -> radius = 1.42 + random.nextDouble() * 0.26;
                case 1 -> radius = 1.78 + random.nextDouble() * 0.42;
                case 2 -> radius = 2.28 + random.nextDouble() * 0.18;
                case 3 -> radius = 2.54 + random.nextDouble() * 0.2;
                default -> radius = 2.82 + random.nextDouble() * 0.18;
            }
            particles[i] = new RingParticle(
                    random.nextDouble() * Math.PI * 2.0,
                    radius,
                    0.0015 + random.nextDouble() * 0.0035,
                    0.3F + random.nextFloat() * 0.4F);
        }
        return particles;
    }

    public static void render(double ticks) {
        SkyGL.disableTexture();
        SkyTess.startDrawingQuads();
        for (RingParticle particle : RING) {
            double angle = particle.angle + ticks * 0.0008;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double inner = particle.radius - particle.size;
            double outer = particle.radius + particle.size;
            double halfLength = particle.size;
            double tilt = Math.toRadians(5.0);
            double y = sin * particle.radius * Math.sin(tilt);
            double z = sin * particle.radius * Math.cos(tilt);
            double innerX = cos * inner;
            double innerZ = (inner / particle.radius) * z;
            double outerX = cos * outer;
            double outerZ = (outer / particle.radius) * z;
            double tangentX = -sin * halfLength;
            double tangentZ = cos * halfLength * Math.cos(tilt);

            SkyTess.setColorRGBA_F(particle.brightness, particle.brightness * 0.73F, particle.brightness * 0.48F, 0.32F);
            SkyTess.addVertex(innerX + tangentX, y, innerZ + tangentZ);
            SkyTess.addVertex(outerX + tangentX, y + sin * particle.size * Math.sin(tilt), outerZ + tangentZ);
            SkyTess.addVertex(outerX - tangentX, y + sin * particle.size * Math.sin(tilt), outerZ - tangentZ);
            SkyTess.addVertex(innerX - tangentX, y, innerZ - tangentZ);
        }
        SkyTess.draw();
        SkyGL.enableTexture();
        SkyGL.color(1, 1, 1);
    }

    private record RingParticle(double angle, double radius, double size, float brightness) {}
}
