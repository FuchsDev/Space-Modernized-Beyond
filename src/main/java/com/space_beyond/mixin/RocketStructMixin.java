package com.space_beyond.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.celestial.StarSystem;
import com.space_beyond.progression.SpaceProgression;
import com.space_m.celestial.CelestialBody;
import com.space_m.rocket.RocketStruct;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Raketen: ohne Triebwerk der Zielstufe kein Flug (Planeten-Fortschritt), kein Flug in ein anderes Sternsystem (nur
 * Stationen mit Faltraum-Antrieb). In der Pruefliste ersetzt der passende Hinweis die Treibstoff-Zeile.
 */
@Mixin(value = RocketStruct.class, remap = false)
public abstract class RocketStructMixin {
    @Shadow public java.util.ArrayList<RocketStruct.RocketStage> stages;
    @Shadow @Nullable public com.space_m.rocket.RocketPart capsule;

    @Inject(method = "findIssues", at = @At("RETURN"))
    private void beyond$tierAndInterstellarIssues(int stageNum, @Nullable CelestialBody from, @Nullable CelestialBody to, boolean fromOrbit,
                                                  boolean toOrbit, CallbackInfoReturnable<List<String>> cir) {
        if (from == null || to == null || (capsule == null && stages.isEmpty())) return;

        Component tier = SpaceProgression.tierIssue((RocketStruct) (Object) this, to);
        String issue;
        if (tier != null) {
            issue = ChatFormatting.RED + tier.getString();
        } else if (!StarSystem.isReachable(from, to)) {
            issue = ChatFormatting.RED + "Interstellar travel locked: needs a station fold drive";
        } else {
            return;
        }

        List<String> issues = cir.getReturnValue();
        String thrust = ChatFormatting.YELLOW + "Insufficient thrust";
        String fuel = ChatFormatting.YELLOW + "Insufficient fuel: ";
        String possible = ChatFormatting.GREEN + "Trip possible! ";
        for (int i = issues.size() - 1; i >= 0; i--) {
            String s = issues.get(i);
            if (s.equals(thrust) || s.startsWith(fuel) || s.startsWith(possible)) {
                issues.set(i, issue);
                return;
            }
        }
        issues.add(issue);
    }

    /** Planeten-Fortschritt: ohne Triebwerk der Zielstufe kein Flug (Startplatz, Start und Kurswechsel). */
    @Inject(method = "hasSufficientFuel", at = @At("HEAD"), cancellable = true)
    private void beyond$tierLock(CelestialBody from, CelestialBody to, boolean fromOrbit, boolean toOrbit, CallbackInfoReturnable<Boolean> cir) {
        if (!SpaceProgression.rocketMayReach((RocketStruct) (Object) this, to)) cir.setReturnValue(false);
    }
}
