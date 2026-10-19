package com.space_beyond.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.client.sky.AsteroidBeltSkyRenderer;
import com.space_beyond.client.sky.BeyondSky;
import com.space_m.celestial.CelestialBody;
import com.space_m.client.sky.CelestialSkyProvider;
import com.space_m.client.sky.CelestialSkyRenderer;
import com.space_m.client.sky.OrbitSkyRenderer;
import com.space_m.client.sky.SkyGL;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/** Himmel im Orbit: Faltphase nur mit Sternen, Asteroidenguertel vom umkreisten Koerper aus gesehen. */
@Mixin(value = OrbitSkyRenderer.class, remap = false)
public abstract class OrbitSkyRendererMixin extends CelestialSkyRenderer {

    /** Faltphase zwischen zwei Sternsystemen - nur Sterne, keine Sonne und keine Koerper. */
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/space_m/client/sky/OrbitSkyRenderer;renderStars(FLnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Minecraft;FFF)V",
            shift = At.Shift.AFTER), cancellable = true)
    private void beyond$interstellarStarsOnly(float partialTicks, ClientLevel world, Minecraft mc, CelestialSkyProvider celestialProvider,
                                              Camera camera, Runnable setupFog, CallbackInfo ci) {
        if (!BeyondSky.interstellarTransfer()) return;
        SkyGL.color(1.0F, 1.0F, 1.0F, 1.0F);
        SkyGL.disableBlend();
        SkyGL.enableTexture();
        SkyGL.depthMask(true);
        ci.cancel();
    }

    /** Nach der Sonne: Asteroidenguertel, gesehen vom umkreisten Koerper. */
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/space_m/client/sky/OrbitSkyRenderer;renderSun(FLnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Minecraft;Lcom/space_m/client/sky/CelestialSkyProvider;Lcom/space_m/celestial/CelestialBody;DDFF)V"))
    private void beyond$sunThenBelts(OrbitSkyRenderer self, float partialTicks, ClientLevel world, Minecraft mc, CelestialSkyProvider provider,
                                     CelestialBody sun, double sunSize, double coronaSize, float visibility, float pressure) {
        renderSun(partialTicks, world, mc, provider, sun, sunSize, coronaSize, visibility, pressure);

        float starBrightness = provider.getStarBrightness(world, partialTicks);
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ONE, SkyGL.GL_ZERO);
        AsteroidBeltSkyRenderer.render(world, partialTicks, CelestialSkyProvider.Orbit.orbiting(), provider.metrics, starBrightness, true);
    }
}
