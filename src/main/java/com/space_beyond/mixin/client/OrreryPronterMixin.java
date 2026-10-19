package com.space_beyond.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_m.celestial.CelestialBody;
import com.space_m.client.orrery.OrreryPronter;
import com.space_beyond.client.orrery.SarnusOrreryRing;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

/** Planetarium: Saturn bekommt einen Ring aus 3 200 Partikeln (fuenf Baender), der langsam umlaeuft. */
@Mixin(value = OrreryPronter.class, remap = false)
public abstract class OrreryPronterMixin {
    /** Koerper des gerade gezeichneten Eintrags (Schleife ueber die Orrery-Metriken). */
    @Unique private static CelestialBody beyond$current;

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lcom/space_m/celestial/SolarSystem$OrreryMetric;body:Lcom/space_m/celestial/CelestialBody;"))
    private static CelestialBody beyond$trackBody(com.space_m.celestial.SolarSystem.OrreryMetric metric) {
        beyond$current = metric.body;
        return metric.body;
    }

    /** Zweiter {@code renderBlock}-Aufruf = Koerperwuerfel; danach (gleiche Matrix) der Saturnring. */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/space_m/client/orrery/OrreryPronter;renderBlock(DD)V", ordinal = 1))
    private static void beyond$bodyThenRing(double uvMin, double uvMax, Minecraft mc, Level world, float partialTicks) {
        renderBlock(uvMin, uvMax);
        if (beyond$current != null && "sarnus".equals(beyond$current.name)) SarnusOrreryRing.render(world.getGameTime() + partialTicks);
    }

    @Shadow
    static void renderBlock(double uvMin, double uvMax) {
        throw new AssertionError();
    }

}
