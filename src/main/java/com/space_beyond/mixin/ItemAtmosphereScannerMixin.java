package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm_m.network.InfoToastPacket;
import com.space_m.atmosphere.ChunkAtmosphereManager;
import com.space_m.celestial.trait.CBT_Atmosphere;
import com.space_m.lifesupport.ItemAtmosphereScanner;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Atmosphaeren-Scanner: zusaetzlich die Sporendichte der Luft am Ort (Koerper-Trait x Biomfaktor). */
@Mixin(ItemAtmosphereScanner.class)
public abstract class ItemAtmosphereScannerMixin {

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void beyond$spores(ItemStack stack, Level world, Entity entity, int slot, boolean inHand, CallbackInfo ci) {
        if (!(entity instanceof ServerPlayer player)) return;

        // gleiche Bedingung wie das Original fuer "hasAtmosphere" (mindestens ein Gas ueber 0,0001 atm)
        CBT_Atmosphere atmosphere = ChunkAtmosphereManager.proxy.getAtmosphere(entity);
        boolean hasAtmosphere = false;
        if (atmosphere != null) {
            for (int i = 0; i < atmosphere.fluids.size(); i++) {
                if (atmosphere.fluids.get(i).pressure > 0.0001) {
                    hasAtmosphere = true;
                    break;
                }
            }
        }
        if (!hasAtmosphere) return;

        float spores = com.space_beyond.celestial.trait.CBT_Spores.densityAt(world)
                * com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos.sporeFactorAt(world, entity.blockPosition());
        if (spores > 0F) {
            InfoToastPacket.sendTo(player, Component.literal("SPORES: " + Math.round(spores * 100F) + "%").withStyle(ChatFormatting.GOLD), 80, 990, 0xFFFFFF);
        }
    }
}
