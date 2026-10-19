package com.space_beyond.progression;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Kern eines Stufen-Bosses: Pflichtzutat des Triebwerks der naechsten Stufe. */
public class ItemBossCore extends Item implements ITooltipProvider {

    public final SpaceProgression.Stage stage;

    public ItemBossCore(SpaceProgression.Stage stage, Properties props) {
        super(props.rarity(Rarity.EPIC).fireResistant().stacksTo(16));
        this.stage = stage;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.space_m.boss_core.from", Component.translatable("progression.stage." + stage.body))
                .withStyle(ChatFormatting.GRAY));
        SpaceProgression.Stage next = SpaceProgression.Stage.byTier(stage.tier() + 1);
        if (next != null) {
            tooltip.add(Component.translatable("item.space_m.boss_core.unlocks", SpaceProgression.roman(next.tier()),
                    Component.translatable("progression.stage." + next.body)).withStyle(ChatFormatting.GOLD));
        }
    }
}
