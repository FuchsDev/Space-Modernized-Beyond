package com.space_beyond.item.survival;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;
import com.space_beyond.entity.mob.infected.EntityBlindcap;
import com.space_beyond.entity.mob.infected.EntityInfected;

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
 * Shiv: improvisierte Klinge (Eisennugget + Faden), 5 Einsaetze, 3 Schaden. Schleicht der Traeger an einen Blindhut
 * heran, den er von hinten trifft, stirbt der Blindhut sofort ({@link #STEALTH_DAMAGE}); andere Infizierte bekommen
 * dann +6. Der Stich kostet immer eine Haltbarkeit.
 */
public class ItemShiv extends SwordItem implements ITooltipProvider {

    public static final int USES = 5;
    public static final float STEALTH_DAMAGE = 100F;
    public static final float STEALTH_BONUS = 6F;

    public ItemShiv(Properties props) {
        //? if < 1.21.1 {
        super(Tiers.WOOD, 2, -2.0F, props.durability(USES));
        //?} else {
        /*super(Tiers.WOOD, props.durability(USES).attributes(SwordItem.createAttributes(Tiers.WOOD, 2, -2.0F)));
        *///?}
    }

    /** Heimlicher Stich: Angreifer schleicht und das Ziel sieht ihn nicht. */
    public static boolean isStealthStab(LivingEntity attacker, LivingEntity target) {
        return attacker.isShiftKeyDown() && !EntityInfected.isFacing(target, attacker);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide && target instanceof EntityInfected && isStealthStab(attacker, target)) {
            float extra = target instanceof EntityBlindcap ? STEALTH_DAMAGE : STEALTH_BONUS;
            target.invulnerableTime = 0;
            target.hurt(attacker.damageSources().mobAttack(attacker), extra);
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.8F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
