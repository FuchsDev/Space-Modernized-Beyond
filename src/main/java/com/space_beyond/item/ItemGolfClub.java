package com.space_beyond.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Golfschlaeger ({@code golf_club}, eigene Erweiterung, Nutzerwunsch): Nahkampfwaffe mit 5 Angriffsschaden im
 * Tooltip (Eisen-Stufe 2 + Bonus 2 = 4 auf den Grundschaden 1 des Spielers), Tempo -2.6 (etwas traeger als ein
 * Schwert), Haltbarkeit der Eisen-Stufe. Trifft er, gibt es einen dumpfen Holzklang ({@code wooden_door.close}, tief)
 * und etwas zusaetzlichen Rueckstoss. Vanilla-Kampf-Tab, Rezept {@code golf_club} (Leder + Stahl).
 * Beschreibung ({@code item.space_m.golf_club.desc}): "Joel Miller had some good experience with it."
 */
public class ItemGolfClub extends SwordItem implements ITooltipProvider {

    public ItemGolfClub(Properties props) {
        //? if < 1.21.1 {
        super(Tiers.IRON, 2, -2.6F, props);
        //?} else {
        /*super(Tiers.IRON, props.attributes(SwordItem.createAttributes(Tiers.IRON, 2, -2.6F)));
        *///?}
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = target.level();
        if (!level.isClientSide) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.PLAYERS, 0.6F, 0.5F);
            target.knockback(0.4D, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
