package com.space_beyond.client.sky;

import com.space_m.client.sky.CelestialSkyProvider;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** Himmel/Nebel der Beyond-Koerper (angemeldet ueber {@code SkyRegistrationMixin}). */
public final class BeyondSkyProviders {
    private BeyondSkyProviders() {}

    /**
     * Sporos: feuchte, sporenhaltige Luft. Himmelsfarbe aus dem Biom-JSON ({@code sky_color}, gruenlich-gelb), hier ein
     * dauerhafter leichter Dunst ({@link #HAZE_DENSITY}, linearer Ersatznebel bis ~230 Bloecke, bei Regen dichter) und
     * eine gruengelb getoente Nebelfarbe. Die violette Sonne kommt ueber den eigenen Stern ({@code renderSun} mit
     * {@code sun.texture}); Korona/Strahlenkranz ({@link #getSunColor}), Tageslicht ({@link #updateLightmap}) und
     * Morgen-/Abendrot ({@link #calcSunriseSunsetColors}) sind leicht violett getoent.
     */
    public static class Sporos extends CelestialSkyProvider {
        /** Grunddunst (Dichte wie {@code fogDensity}; 0.013 -> Sichtweite 3/0.013 = 230 Bloecke). */
        public static final float HAZE_DENSITY = 0.013F;
        /** Toenung der Nebelfarbe (Multiplikator auf die Erdluft-Farbe). */
        private static final float TINT_R = 1.06F, TINT_G = 1.02F, TINT_B = 0.76F;

        /** Nebel je Biom - Sporenfeld dichter gelber Nebel, Pilzhain Dauerdaemmerung, Hochland grauer Dunst. */
        private static float biomeHaze(ClientLevel level) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null) return HAZE_DENSITY;
            var key = level.getBiome(player.blockPosition()).unwrapKey().orElse(null);
            if (key == null) return HAZE_DENSITY;
            if (key.equals(com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.sporeField.key)) return 0.06F;
            if (key.equals(com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.fungalGrove.key)) return 0.035F;
            if (key.equals(com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.highland.key)) return 0.025F;
            return HAZE_DENSITY;
        }

        /** Nebelfarbe zur Haelfte aus dem Biom-JSON ({@code fog_color}), damit Sporenfeld (gelb), Pilzhain (dunkel) und Hochland (grau) sichtbar sind. */
        private static Vec3 biomeFog(ClientLevel level, Vec3 base, float sun) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null) return base;
            int c = level.getBiome(player.blockPosition()).value().getFogColor();
            float s = Mth.clamp(sun, 0.15F, 1.0F);
            double r = ((c >> 16) & 255) / 255.0D * s, g = ((c >> 8) & 255) / 255.0D * s, b = (c & 255) / 255.0D * s;
            return new Vec3((base.x + r) * 0.5D, (base.y + g) * 0.5D, (base.z + b) * 0.5D);
        }

        @Override
        public float fogDensity(ClientLevel level, boolean inWater) {
            if (inWater) return 0.05F; // truebes Sumpfwasser
            float density = Math.max(super.fogDensity(level, false), biomeHaze(level));
            return density * (1.0F + level.getRainLevel(1.0F) * 0.8F);
        }

        /** Korona/Strahlenkranz der violetten Sonne (Basis: Atmosphaeren-Toenung wie ueberall). */
        @Override
        public float[] getSunColor(ClientLevel level) {
            float[] c = super.getSunColor(level);
            return new float[] {c[0] * 0.92F, c[1] * 0.72F, c[2]};
        }

        /** Tageslicht leicht violett (nur der Himmelslicht-Anteil, nachts unveraendert). */
        @Override
        public boolean updateLightmap(ClientLevel level, float sky, float[] rgb) {
            float day = Mth.clamp(sky * lightmapSunBrightness, 0F, 1F);
            rgb[0] = Math.min(rgb[0] * (1F + 0.03F * day), 1F);
            rgb[1] = rgb[1] * (1F - 0.08F * day);
            rgb[2] = Math.min(rgb[2] * (1F + 0.06F * day), 1F);
            return true;
        }

        /** Morgen-/Abendrot ins Magenta verschoben (violettes Sternlicht durch gelbgruenen Dunst). */
        @Override
        public float[] calcSunriseSunsetColors(ClientLevel level, float solarAngle, float partialTicks) {
            float[] colors = super.calcSunriseSunsetColors(level, solarAngle, partialTicks);
            if (colors == null) return null;
            colors[1] *= 0.6F;
            colors[2] = Math.max(colors[2], colors[0] * 0.75F);
            return colors;
        }

        @Override
        public Vec3 getFogColor(ClientLevel level, float sun) {
            Vec3 c = super.getFogColor(level, sun);
            return biomeFog(level, new Vec3(Math.min(c.x * TINT_R, 1.0D), Math.min(c.y * TINT_G, 1.0D), Math.min(c.z * TINT_B, 1.0D)), sun);
        }
    }

    /**
     * Wemburos (Sporos-System): duenne Stickstoff-Xenon-Luft. Leichter lila Dunst (Sichtweite ~300 Bloecke), Nebel- und
     * Tageslichtfarbe violett, Morgen-/Abendrot golden (Farbwelt des Planeten: Lila, Schwarz, Gold).
     * Himmelsfarbe kommt aus dem Biom-JSON ({@code sky_color}).
     */
    public static class Wemburos extends CelestialSkyProvider {
        public static final float HAZE_DENSITY = 0.01F;

        @Override
        public float fogDensity(ClientLevel level, boolean inWater) {
            return Math.max(super.fogDensity(level, inWater), HAZE_DENSITY);
        }

        @Override
        public boolean updateLightmap(ClientLevel level, float sky, float[] rgb) {
            float day = Mth.clamp(sky * lightmapSunBrightness, 0F, 1F);
            rgb[0] = Math.min(rgb[0] * (1F + 0.04F * day), 1F);
            rgb[1] = rgb[1] * (1F - 0.10F * day);
            rgb[2] = Math.min(rgb[2] * (1F + 0.08F * day), 1F);
            return true;
        }

        @Override
        public float[] calcSunriseSunsetColors(ClientLevel level, float solarAngle, float partialTicks) {
            float[] colors = super.calcSunriseSunsetColors(level, solarAngle, partialTicks);
            if (colors == null) return null;
            colors[0] = Math.max(colors[0], 0.95F);
            colors[1] = Math.max(colors[1], 0.72F);
            colors[2] *= 0.35F;
            return colors;
        }

        @Override
        public Vec3 getFogColor(ClientLevel level, float sun) {
            Vec3 c = super.getFogColor(level, sun);
            return new Vec3(Math.min(c.x * 1.05D, 1.0D), c.y * 0.72D, Math.min(c.z * 1.2D, 1.0D));
        }
    }
}
