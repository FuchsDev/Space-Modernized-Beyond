package com.space_beyond.dim.sporos.spores;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;
import com.hbm_m.item.special.ModConsumables;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Antimykotikum (Sporos Runde 4, eigene Erweiterung): Pille gegen die Sporeninfektion. Rezept
 * {@code sporos/antimycotic} (Leuchtpilz + Sporenstaub + Zucker -> 2), Beute in Apotheken/Laeden der Sporos-Staedte
 * ({@code chests/sporos_city/medical}, {@code shop}). Einnahme (16 Ticks, immer moeglich wie {@code ItemPill}) setzt
 * den Infektionsfortschritt auf 0 ({@link SporeInfection#apply}) und nimmt damit den Effekt; danach 3 s
 * Trankuebelkeit ({@code ModConsumables.applyPotionSickness}) gegen Dauernuckeln. Stapel 16, Tooltip
 * {@code item.space_m.antimycotic.desc}.
 */
public class ItemAntimycotic extends Item implements ITooltipProvider {

    public ItemAntimycotic(Properties props) {
        super(props.stacksTo(16).food(com.hbm_m.platform.PlatformHooks.foodBuilder(0, 0F)
                //? if < 1.21.1 {
                .alwaysEat()
                //?} else {
                /*.alwaysEdible()
                *///?}
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        ItemStack res = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide) {
            SporeInfection.apply(entity, 0F);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8F, 1.2F);
            if (entity instanceof Player player) ModConsumables.applyPotionSickness(player, 3);
        }
        return res;
    }

    @Override
    //? if < 1.21.1 {
    public int getUseDuration(ItemStack stack) {
    //?} else {
    /*public int getUseDuration(ItemStack stack, LivingEntity user) {
    *///?}
        return 16;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ModConsumables.isPotionSick(player)) player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
