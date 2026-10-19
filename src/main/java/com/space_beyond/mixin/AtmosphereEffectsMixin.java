package com.space_beyond.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.lib.BeyondTags;
import com.space_m.atmosphere.AtmosphereEffects;
import com.space_m.celestial.trait.CBT_Atmosphere;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Dungeon-Bewohner (Entity-Tag {@link BeyondTags#DUNGEON_TAG}) ersticken und veraetzen nicht auf luftlosen Planeten. */
@Mixin(value = AtmosphereEffects.class, remap = false)
public abstract class AtmosphereEffectsMixin {

    @Inject(method = "isSuffocationImmune", at = @At("RETURN"), cancellable = true)
    private static void beyond$dungeonImmune(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && entity.getTags().contains(BeyondTags.DUNGEON_TAG)) cir.setReturnValue(true);
    }

    @Inject(method = "handleCorrosion", at = @At("HEAD"), cancellable = true)
    private static void beyond$dungeonNoCorrosion(LivingEntity entity, @Nullable CBT_Atmosphere atmosphere, CallbackInfo ci) {
        if (entity.getTags().contains(BeyondTags.DUNGEON_TAG)) ci.cancel();
    }
}
