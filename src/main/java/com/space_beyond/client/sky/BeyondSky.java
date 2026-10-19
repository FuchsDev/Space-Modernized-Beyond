package com.space_beyond.client.sky;

import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.CelestialBody;
import com.space_m.client.sky.CelestialSkyProvider;
import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyHooks;
import com.space_m.client.sky.SkyTess;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Himmels-Erweiterungen von Beyond (aufgerufen aus den Sky-Mixins). */
public final class BeyondSky {
    private BeyondSky() {}

    /** Faltphase zwischen zwei Sternsystemen - der Himmel zeigt nur Sterne. */
    public static boolean interstellarTransfer() {
        return SkyHooks.get().stationState() == SkyHooks.StationState.TRANSFER
                && !StarSystem.sameSystem(CelestialSkyProvider.Orbit.orbiting(), CelestialSkyProvider.Orbit.target());
    }

    /**
     * Jeder Stern eines anderen {@link StarSystem} erscheint als ferner, heller Stern in seiner Galaxie-Richtung (Azimut
     * um den Himmelspol, 70 Grad Polabstand), Farbe = Sternfarbe, Helligkeit faellt mit dem Abstand (5 Lichtjahre = voll).
     * Laeuft im Bezugssystem des Sternenhimmels (Ende von {@code renderStars}).
     */
    public static void renderDistantStarSystems(ClientLevel world, float starBrightness, ResourceLocation flareTexture) {
        StarSystem here = StarSystem.of(currentStar(world));
        if (here == null) return;

        for (StarSystem other : StarSystem.all()) {
            if (other == here) continue;

            double dx = other.galaxyX - here.galaxyX;
            double dy = other.galaxyY - here.galaxyY;
            double distanceLy = Math.sqrt(dx * dx + dy * dy);
            float falloff = (float) Mth.clamp(5.0D / Math.max(distanceLy, 0.001D), 0.3D, 1.0D);
            float brightness = Math.max(0.25F, Math.min(1F, starBrightness * 1.6F)) * falloff;
            float[] color = other.star.color;

            SkyGL.push();
            {
                SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ONE, SkyGL.GL_ZERO);

                SkyGL.rotate((float) Math.toDegrees(Math.atan2(dy, dx)), 0.0F, 1.0F, 0.0F);
                SkyGL.rotate(70.0F, 1.0F, 0.0F, 0.0F);

                double dist = 100D;

                // Strahlenkranz
                double flare = 2.6D;
                SkyGL.color(color[0], color[1], color[2], brightness * 0.55F);
                SkyGL.bind(flareTexture);
                SkyTess.startDrawingQuads();
                SkyTess.addVertexWithUV(-flare, dist, -flare, 0.0D, 0.0D);
                SkyTess.addVertexWithUV(flare, dist, -flare, 1.0D, 0.0D);
                SkyTess.addVertexWithUV(flare, dist, flare, 1.0D, 1.0D);
                SkyTess.addVertexWithUV(-flare, dist, flare, 0.0D, 1.0D);
                SkyTess.draw();

                // Kern (Sterntextur)
                double core = 0.9D;
                SkyGL.color(1.0F, 1.0F, 1.0F, brightness);
                SkyGL.bind(other.star.texture);
                SkyTess.startDrawingQuads();
                SkyTess.addVertexWithUV(-core, dist - 0.1D, -core, 0.0D, 0.0D);
                SkyTess.addVertexWithUV(core, dist - 0.1D, -core, 1.0D, 0.0D);
                SkyTess.addVertexWithUV(core, dist - 0.1D, core, 1.0D, 1.0D);
                SkyTess.addVertexWithUV(-core, dist - 0.1D, core, 0.0D, 1.0D);
                SkyTess.draw();

                SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ZERO);
            }
            SkyGL.pop();
        }
        SkyGL.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Stern des Betrachters (im Orbit der Stern des umkreisten Koerpers). */
    public static CelestialBody currentStar(ClientLevel world) {
        if (CelestialBody.inOrbit(world)) return CelestialSkyProvider.Orbit.orbiting().getStar();
        return CelestialBody.getBody(world).getStar();
    }
}
