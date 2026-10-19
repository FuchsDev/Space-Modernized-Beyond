package com.space_beyond.item.survival;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Nahkampfwaffe des Ueberlebens-Sets: Schwert-Item mit optionalem Zusatzrueckstoss ({@code knockback}) und
 * Verwesung ({@code witherTicks}, Nagelschlaeger). Tooltip-Beschreibung {@code item.space_m.<id>.desc}.
 */
public class ItemSurvivalWeapon extends SwordItem implements ITooltipProvider {

    private final float knockback;
    private final int witherTicks;

    public ItemSurvivalWeapon(Tier tier, int damage, float speed, float knockback, int witherTicks, Properties props) {
        //? if < 1.21.1 {
        super(tier, damage, speed, props);
        //?} else {
        /*super(tier, props.attributes(SwordItem.createAttributes(tier, damage, speed)));
        *///?}
        this.knockback = knockback;
        this.witherTicks = witherTicks;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide) {
            if (knockback > 0F) target.knockback(knockback, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
            if (witherTicks > 0) target.addEffect(new MobEffectInstance(MobEffects.WITHER, witherTicks, 0));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
