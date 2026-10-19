package com.space_beyond.item.survival;

import java.util.function.BiFunction;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Wurfgegenstand wie der Schneeball: Rechtsklick wirft das Geschoss ({@code factory}) mit {@code speed}. */
public class ItemThrowable extends Item {

    private final BiFunction<Level, LivingEntity, ThrowableItemProjectile> factory;
    private final float speed;

    public ItemThrowable(Properties props, BiFunction<Level, LivingEntity, ThrowableItemProjectile> factory, float speed) {
        super(props);
        this.factory = factory;
        this.speed = speed;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            ThrowableItemProjectile projectile = factory.apply(level, player);
            projectile.setItem(stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, 1.0F);
            level.addFreshEntity(projectile);
        }
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
