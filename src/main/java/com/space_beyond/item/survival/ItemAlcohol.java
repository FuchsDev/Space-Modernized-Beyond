package com.space_beyond.item.survival;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Reinigungsalkohol: Zutat fuer Feldverband und Molotow; trinkbar (immer): 10 s Uebelkeit, nimmt Vergiftung. Stapel 16.
 */
public class ItemAlcohol extends Item {

    public ItemAlcohol(Properties props) {
        super(props.food(com.hbm_m.platform.PlatformHooks.foodBuilder(0, 0F)
                //? if < 1.21.1 {
                .alwaysEat()
                //?} else {
                /*.alwaysEdible()
                *///?}
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack res = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            entity.removeEffect(MobEffects.POISON);
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }
        return res;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
