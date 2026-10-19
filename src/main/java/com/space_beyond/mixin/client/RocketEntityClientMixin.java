package com.space_beyond.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.CelestialBody;
import com.space_m.rocket.ItemVOTVdrive;
import com.space_m.rocket.entity.EntityRideableRocket;
import com.space_m.rocket.entity.client.RocketEntityClient;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/** Raketen-Anzeige: anderes Sternsystem -> eigener Hinweis statt "Rocket can't reach destination!" (Raketen falten nicht). */
@Mixin(value = RocketEntityClient.class, remap = false)
public abstract class RocketEntityClientMixin {

    @Redirect(method = "printHook(Lnet/minecraft/client/gui/GuiGraphics;Lcom/space_m/rocket/entity/EntityRideableRocket;Lnet/minecraft/world/level/Level;)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/space_m/rocket/entity/client/RocketEntityClient;printGeneric(Lnet/minecraft/client/gui/GuiGraphics;Ljava/lang/String;IILjava/util/List;)V",
                    ordinal = 1))
    private static void beyond$interstellarHint(GuiGraphics g, String title, int titleCol, int bgCol, List<Component> text,
                                                GuiGraphics g2, EntityRideableRocket rocket, Level world) {
        CelestialBody.Target from = CelestialBody.getTarget(world, (int) rocket.getX(), (int) rocket.getZ());
        ItemVOTVdrive.Target to = rocket.getTarget();
        if (to.body() != null && !StarSystem.isReachable(from.body, to.body())) {
            for (int i = 0; i < text.size(); i++) {
                if (text.get(i).getString().equals("Rocket can't reach destination!")) {
                    text.set(i, Component.translatable("rocket.interstellarLocked").withStyle(ChatFormatting.RED));
                    break;
                }
            }
        }
        RocketEntityClient.printGeneric(g, title, titleCol, bgCol, text);
    }
}
