package com.space_beyond.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.client.sky.AsteroidBeltSkyRenderer;
import com.space_beyond.client.sky.BeyondSky;
import com.space_beyond.client.sky.SarnusAsteroidRenderer;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem.AstroMetric;
import com.space_m.client.sky.CelestialSkyProvider;
import com.space_m.client.sky.CelestialSkyRenderer;
import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyHooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Himmel der Koerper (im Privat-Repo direkt in {@code CelestialSkyRenderer}): Asteroidenguertel hinter den Planeten,
 * ferne Sterne anderer Sternsysteme, eigene Sonnentextur je Sternsystem, Saturnring als Partikelguertel.
 */
@Mixin(value = CelestialSkyRenderer.class, remap = false)
public abstract class CelestialSkyRendererMixin {
    @Shadow @Final private static ResourceLocation flareTexture;

    @Shadow
    protected abstract void renderSun(float partialTicks, ClientLevel world, Minecraft mc, CelestialSkyProvider provider, CelestialBody sun,
                                      double sunSize, double coronaSize, float visibility, float pressure);

    /** Nach der Sonne: Asteroidenguertel (Staubband + Brocken), hinter den Planeten. */
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/space_m/client/sky/CelestialSkyRenderer;renderSun(FLnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Minecraft;Lcom/space_m/client/sky/CelestialSkyProvider;Lcom/space_m/celestial/CelestialBody;DDFF)V"))
    private void beyond$sunThenBelts(CelestialSkyRenderer self, float partialTicks, ClientLevel world, Minecraft mc, CelestialSkyProvider provider,
                                     CelestialBody sun, double sunSize, double coronaSize, float visibility, float pressure) {
        renderSun(partialTicks, world, mc, provider, sun, sunSize, coronaSize, visibility, pressure);

        float starBrightness = provider.getStarBrightness(world, partialTicks) * visibility;
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ONE, SkyGL.GL_ZERO);
        AsteroidBeltSkyRenderer.render(world, partialTicks, CelestialBody.getBody(world), provider.metrics, starBrightness, pressure < 0.01F);
    }

    /** Ende des Sternenhimmels (im mitdrehenden Bezugssystem): Sterne anderer Sternsysteme. */
    @Inject(method = "renderStars", at = @At(value = "INVOKE", target = "Lcom/space_m/client/sky/SkyGL;pop()V", ordinal = 5))
    private void beyond$distantStars(float partialTicks, ClientLevel world, Minecraft mc, float starBrightness, float siderealAngle,
                                     float axialTilt, CallbackInfo ci) {
        BeyondSky.renderDistantStarSystems(world, starBrightness, flareTexture);
    }

    /** Sonne als schwarzes Loch: Textur des eigenen Sterns statt immer Kerbol (Heimat identisch). */
    @Redirect(method = "renderSun", at = @At(value = "FIELD", target = "Lcom/space_m/celestial/SolarSystem;kerbol:Lcom/space_m/celestial/CelestialBody;"))
    private CelestialBody beyond$ownStar(float partialTicks, ClientLevel world, Minecraft mc, CelestialSkyProvider provider, CelestialBody sun,
                                         double sunSize, double coronaSize, float visibility, float pressure) {
        return sun;
    }

    /** Saturn zeigt statt des flachen Rings den Partikelguertel ({@link SarnusAsteroidRenderer}). */
    @Redirect(method = "renderCelestials", at = @At(value = "FIELD", target = "Lcom/space_m/celestial/CelestialBody;hasRings:Z"))
    private boolean beyond$ringsExceptSarnus(CelestialBody body) {
        return body.hasRings && !"sarnus".equals(body.name);
    }

    /** Nach dem vorderen Ringteil (vor den Blitz-Schockwellen): Asteroiden des Saturnrings. */
    @Redirect(method = "renderCelestials", at = @At(value = "INVOKE",
            target = "Lcom/space_m/client/sky/SkyHooks;clientShocks(Lcom/space_m/celestial/CelestialBody;)Ljava/util/List;", ordinal = 1))
    private List<SkyHooks.ShockStatus> beyond$sarnusAsteroids(SkyHooks hooks, CelestialBody body, float partialTicks, ClientLevel world,
                                                               Minecraft mc, List<AstroMetric> metrics, float solarAngle, CelestialBody tidalLockedBody,
                                                               Vec3 planetTint, float visibility, float blendAmount, CelestialBody orbiting, float maxSize) {
        for (AstroMetric metric : metrics) {
            if (metric.body != body) continue;
            double size = Mth.clamp(metric.apparentSize, 0, maxSize);
            SarnusAsteroidRenderer.render(world, partialTicks, body, metric, size, visibility);
            break;
        }
        return hooks.clientShocks(body);
    }
}
