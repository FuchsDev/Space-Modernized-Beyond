package com.space_beyond.entity.mob.boss;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.platform.PlatformHooks;
import com.space_m.particle.SpaceParticles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Orbital Strike Rod (Beute des Bosses {@link EntityUnstableSovereign}): ruft Orbitalschlaege auf das Ziel im Blick
 * (bis 160 Bloecke, Wesen werden erfasst). Schleichen + Rechtsklick wechselt den Modus; jeder Modus hat eine eigene
 * Aufladezeit, die beim Wechsel weiterlaeuft:
 * <ol>
 *   <li><b>Sovereign Lance</b> - ein gigantischer Strahl mit Megablitzen, Kernexplosion und Explosionsring (30 s).</li>
 *   <li><b>Tempest Barrage</b> - zwoelf Blitzstrahlen, verteilt ueber das Zielgebiet (20 s).</li>
 *   <li><b>Gilded Rain</b> - goldener Teppich: Einschlaege marschieren vom Schuetzen zum Ziel (15 s).</li>
 *   <li><b>Singularity</b> - Sog, der alles im Umkreis von 16 Bloecken einsaugt, dann Implosion (25 s, kein Blockschaden).</li>
 *   <li><b>Judgement</b> - schneller Praezisionsstrahl, hoher Einzelschaden (6 s, kein Blockschaden).</li>
 *   <li><b>Sovereign Cataclysm</b> - 5 s Vorwarnung, dann ein Krater mit 50 Bloecken Radius in jede Richtung und
 *       150 Schaden im ganzen Umkreis (5 min).</li>
 * </ol>
 */
public class ItemOrbitalStrikeRod extends Item implements ITooltipProvider {

    public enum Mode {
        LANCE("lance", 600, ChatFormatting.LIGHT_PURPLE),
        TEMPEST("tempest", 400, ChatFormatting.DARK_PURPLE),
        GILDED("gilded", 300, ChatFormatting.GOLD),
        SINGULARITY("singularity", 500, ChatFormatting.DARK_AQUA),
        JUDGEMENT("judgement", 120, ChatFormatting.WHITE),
        CATACLYSM("cataclysm", 6000, ChatFormatting.DARK_RED);

        public final String key;
        public final int cooldown;
        public final ChatFormatting color;

        Mode(String key, int cooldown, ChatFormatting color) {
            this.key = key;
            this.cooldown = cooldown;
            this.color = color;
        }

        public Component title() {
            return Component.translatable("item.space_m.orbital_strike_rod.mode." + key).withStyle(color, ChatFormatting.BOLD);
        }
    }

    public static final double RANGE = 160.0D;

    public ItemOrbitalStrikeRod(Properties properties) {
        super(properties.stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static Mode mode(ItemStack stack) {
        int i = PlatformHooks.getInt(stack, "Mode");
        Mode[] all = Mode.values();
        return all[Math.floorMod(i, all.length)];
    }

    /** Verbleibende Aufladezeit des Modus in Ticks. */
    public static long remaining(ItemStack stack, Mode mode, Level level) {
        return Math.max(0L, PlatformHooks.getLong(stack, "Ready_" + mode.key) - level.getGameTime());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                Mode next = Mode.values()[(mode(stack).ordinal() + 1) % Mode.values().length];
                PlatformHooks.putInt(stack, "Mode", next.ordinal());
                player.displayClientMessage(Component.translatable("item.space_m.orbital_strike_rod.selected", next.title()), true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.2F, 0.6F + next.ordinal() * 0.2F);
            }
            player.getCooldowns().addCooldown(this, 6);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        Mode mode = mode(stack);
        long left = remaining(stack, mode, level);
        if (left > 0) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("item.space_m.orbital_strike_rod.charging", mode.title(),
                        String.format("%.1f", left / 20.0F)).withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        Vec3 at = target(level, player);
        if (at == null) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server) {
            Vec3 from = player.position();
            switch (mode) {
                case LANCE -> OrbitalStrikes.lance(server, at, player, true);
                case TEMPEST -> OrbitalStrikes.storm(server, at, player, true);
                case GILDED -> OrbitalStrikes.line(server, from, at, player, true);
                case SINGULARITY -> OrbitalStrikes.singularity(server, at, player);
                case JUDGEMENT -> OrbitalStrikes.precision(server, at, player);
                case CATACLYSM -> OrbitalStrikes.cataclysm(server, at, player, true);
            }
            long ready = level.getGameTime() + (player.getAbilities().instabuild ? 20 : mode.cooldown);
            PlatformHooks.putLong(stack, "Ready_" + mode.key, ready);
            // Ausloesen: Funkenring um den Spieler, Zepterklang
            for (int i = 0; i < 24; i++) {
                double a = i / 24.0D * Math.PI * 2;
                server.sendParticles(mode == Mode.GILDED ? com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get() : com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(),
                        player.getX() + Math.cos(a) * 1.2, player.getY() + 0.2, player.getZ() + Math.sin(a) * 1.2, 1, 0, 0.25, 0, 0.02);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.5F, 1.4F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.7F);
            player.displayClientMessage(Component.translatable("item.space_m.orbital_strike_rod.fired", mode.title()), true);
        }
        player.getCooldowns().addCooldown(this, 10);
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** Ziel: erstes Wesen auf der Blicklinie (falls vor dem Block), sonst die Oberflaeche des angeblickten Blocks. */
    @Nullable
    private static Vec3 target(Level level, Player player) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(RANGE));
        BlockHitResult block = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 blockEnd = block.getType() == HitResult.Type.MISS ? end : block.getLocation();
        AABB box = player.getBoundingBox().expandTowards(blockEnd.subtract(eye)).inflate(1.0D);
        EntityHitResult entity = ProjectileUtil.getEntityHitResult(player, eye, blockEnd, box,
                e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator() && e != player, eye.distanceToSqr(blockEnd));
        if (entity != null) {
            Entity e = entity.getEntity();
            return new Vec3(e.getX(), e.getY(), e.getZ());
        }
        if (block.getType() != HitResult.Type.BLOCK) return null;
        return Vec3.atBottomCenterOf(block.getBlockPos().relative(block.getDirection()));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.space_m.orbital_strike_rod.desc").withStyle(ChatFormatting.LIGHT_PURPLE));
        Mode current = mode(stack);
        for (Mode m : Mode.values()) {
            boolean sel = m == current;
            Component line = Component.literal(sel ? " ▶ " : "   ")
                    .append(Component.translatable("item.space_m.orbital_strike_rod.mode." + m.key).withStyle(sel ? m.color : ChatFormatting.DARK_GRAY));
            if (level != null) {
                long left = remaining(stack, m, level);
                if (left > 0) line = line.copy().append(Component.literal(String.format(" (%.0fs)", Math.ceil(left / 20.0F))).withStyle(ChatFormatting.RED));
            }
            tooltip.add(line);
        }
        tooltip.add(Component.translatable("item.space_m.orbital_strike_rod.mode." + current.key + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.space_m.orbital_strike_rod.desc2").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.space_m.orbital_strike_rod.desc3").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }
}
