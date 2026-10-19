package com.space_beyond.progression;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Boss-Waffe eines Planeten (Beute der Stufen-Bosse): Schwert mit Treffer-Effekt und einer Faehigkeit auf Rechtsklick
 * (eigene Abklingzeit). Beschreibung {@code item.space_m.<id>.desc}, Faehigkeit {@code item.space_m.<id>.ability}.
 */
public class ItemPlanetWeapon extends SwordItem implements ITooltipProvider {

    @FunctionalInterface
    public interface OnHit {
        void hit(ServerLevel level, ItemStack stack, LivingEntity target, LivingEntity attacker);
    }

    @FunctionalInterface
    public interface Ability {
        /** @return true, wenn die Faehigkeit ausgeloest wurde (dann Abklingzeit). */
        boolean use(ServerLevel level, Player player, ItemStack stack);
    }

    private final OnHit onHit;
    private final Ability ability;
    private final int cooldown;
    /** Farbe der Schwungboegen und Ringe (RGB). */
    private final int color;
    /** Partikel, die aus der Waffe steigen, solange man sie haelt. */
    private final java.util.function.Supplier<net.minecraft.core.particles.ParticleOptions> held;

    public ItemPlanetWeapon(Tier tier, int damage, float speed, int cooldown, OnHit onHit, Ability ability, int color,
                            java.util.function.Supplier<net.minecraft.core.particles.ParticleOptions> held, Properties props) {
        //? if < 1.21.1 {
        super(tier, damage, speed, props.rarity(Rarity.EPIC).fireResistant());
        //?} else {
        /*super(tier, props.rarity(Rarity.EPIC).fireResistant().attributes(SwordItem.createAttributes(tier, damage, speed)));
        *///?}
        this.onHit = onHit;
        this.ability = ability;
        this.cooldown = cooldown;
        this.color = color;
        this.held = held;
    }

    public int color() {
        return color;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.level() instanceof ServerLevel level) {
            onHit.hit(level, stack, target, attacker);
            com.space_beyond.progression.fx.SlashEffect.slash(level, attacker, color, 1.0F, (attacker.getRandom().nextFloat() - 0.5F) * 70.0F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.pass(stack);
        if (level instanceof ServerLevel server) {
            if (!ability.use(server, player, stack)) return InteractionResultHolder.pass(stack);
            com.space_beyond.progression.fx.SlashEffect.ring(server, player.position(), color, 6.0F);
            com.space_beyond.progression.fx.SlashEffect.slash(server, player, color, 1.7F, 0.0F);
        }
        player.getCooldowns().addCooldown(this, cooldown);
        player.swing(hand, true);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** Halte-Partikel: steigen aus der Waffe in der Hand (nur Client, nur ausgewaehlt). */
    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide || !selected || !(entity instanceof LivingEntity living) || level.random.nextFloat() > 0.35F) return;
        float yaw = living.yBodyRot * net.minecraft.util.Mth.DEG_TO_RAD;
        double rx = -net.minecraft.util.Mth.cos(yaw) * 0.38, rz = -net.minecraft.util.Mth.sin(yaw) * 0.38;
        double fx = -net.minecraft.util.Mth.sin(yaw) * 0.35, fz = net.minecraft.util.Mth.cos(yaw) * 0.35;
        double x = living.getX() + rx + fx + (level.random.nextDouble() - 0.5) * 0.3;
        double y = living.getY() + living.getBbHeight() * 0.55 + level.random.nextDouble() * 0.6;
        double z = living.getZ() + rz + fz + (level.random.nextDouble() - 0.5) * 0.3;
        level.addParticle(held.get(), x, y, z, 0, 0.02, 0);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
        tooltip.add(Component.translatable(getDescriptionId() + ".ability").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.space_m.planet_weapon.cooldown", cooldown / 20).withStyle(ChatFormatting.DARK_GRAY));
    }
}
