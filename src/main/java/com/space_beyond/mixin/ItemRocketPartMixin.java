package com.space_beyond.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.space_beyond.progression.SpaceProgression;
import com.space_m.rocket.ItemRocketPart;
import com.space_m.rocket.RocketPart;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Triebwerks-Tooltip: Stufe des Triebwerks und hoechstes erreichbares Ziel (Planeten-Fortschritt). */
@Mixin(ItemRocketPart.class)
public abstract class ItemRocketPartMixin {

    //? if < 1.21.1 {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void beyond$tierLine(ItemStack stack, @Nullable net.minecraft.world.level.Level level, List<Component> list, TooltipFlag flag, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "appendHoverText", at = @At("TAIL"))
    private void beyond$tierLine(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> list, TooltipFlag flag, CallbackInfo ci) {
    *///?}
        RocketPart part = ((ItemRocketPart) (Object) this).part();
        if (part == null || part.type != RocketPart.PartType.THRUSTER) return;
        int tier = SpaceProgression.thrusterTier(part);
        SpaceProgression.Stage reach = SpaceProgression.Stage.byTier(tier);
        list.add(beyond$line("item.missile.part.tier", Component.literal("Mk." + SpaceProgression.roman(tier))
                .append(reach != null ? Component.literal(" (").append(Component.translatable("progression.stage." + reach.body)).append(")") : Component.empty())
                .withStyle(ChatFormatting.GOLD)));
    }

    /** Wie {@code ItemRocketPart.line}: fett "Schluessel: ", Wert grau falls ohne eigene Farbe. */
    private static MutableComponent beyond$line(String key, Component value) {
        MutableComponent v = value.getStyle().getColor() == null ? value.copy().withStyle(ChatFormatting.GRAY) : value.copy();
        return Component.literal("").append(Component.translatable(key).withStyle(ChatFormatting.BOLD))
                .append(Component.literal(": ").withStyle(ChatFormatting.BOLD)).append(v);
    }
}
