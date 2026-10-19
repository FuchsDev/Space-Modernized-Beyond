package com.space_beyond.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.celestial.StarSystem;
import com.space_beyond.progression.SpaceProgression;
import com.space_m.celestial.CelestialBody;
import com.space_m.rocket.ItemVOTVdrive;
import com.space_m.rocket.RocketStruct;
import com.space_m.rocket.pad.TileEntityLaunchPadRocket;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Startrampe: statt "Rocket can't reach destination" der eigentliche Grund - Triebwerk der Zielstufe fehlt
 * (Planeten-Fortschritt) oder das Ziel liegt in einem anderen Sternsystem.
 */
@Mixin(value = TileEntityLaunchPadRocket.class, remap = false)
public abstract class LaunchPadRocketMixin {

    @Inject(method = "findTravelIssues", at = @At("TAIL"))
    private static void beyond$travelIssues(List<Component> issues, RocketStruct rocket, CelestialBody.Target from, ItemVOTVdrive.Target to, CallbackInfo ci) {
        if (to.body() == null) return;
        Component tier = SpaceProgression.tierIssue(rocket, to.body());
        Component issue;
        if (tier != null) {
            issue = tier.copy().withStyle(ChatFormatting.RED);
        } else if (!StarSystem.isReachable(from.body, to.body())) {
            issue = Component.translatable("rocket.interstellarLocked").withStyle(ChatFormatting.RED);
        } else {
            return;
        }
        for (int i = issues.size() - 1; i >= 0; i--) {
            if (issues.get(i).getString().equals("Rocket can't reach destination")) {
                issues.set(i, issue);
                return;
            }
        }
        issues.add(issue);
    }
}
