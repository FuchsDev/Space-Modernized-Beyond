package com.space_beyond.progression;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

/** Essen/Getraenk eines Planeten (Dungeon-Beute) mit Effekten aus den {@code FoodProperties}; optional Extra-Wirkung. */
public class ItemPlanetFood extends Item implements ITooltipProvider {

    @FunctionalInterface
    public interface Extra {
        void apply(Level level, LivingEntity eater);
    }

    private final boolean drink;
    @Nullable private final Extra extra;

    public ItemPlanetFood(Properties props, boolean drink, @Nullable Extra extra) {
        super(props);
        this.drink = drink;
        this.extra = extra;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return drink ? UseAnim.DRINK : UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && extra != null) extra.apply(level, entity);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
