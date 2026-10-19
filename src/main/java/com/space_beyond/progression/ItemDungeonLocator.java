package com.space_beyond.progression;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.item.ITooltipProvider;
import com.hbm_m.item.ItemCustomLore;
import com.space_m.celestial.CelestialBody;
import com.space_m.dim.CelestialGenerators;
import com.space_beyond.dim.dungeon.PlanetDungeons;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Dungeon-Peiler: Rechtsklick zeigt Entfernung und Richtung zum naechsten Boss-Dungeon des Planeten, auf dem man
 * steht ({@link PlanetDungeons}), und zieht eine Funkenspur in seine Richtung.
 */
public class ItemDungeonLocator extends Item implements ITooltipProvider {

    public ItemDungeonLocator(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            CelestialBody body = CelestialBody.getBody(server);
            String name = body != null ? body.name : null;
            if (name == null || PlanetDungeons.design(name) == null) {
                player.displayClientMessage(Component.translatable("item.space_m.dungeon_locator.none").withStyle(ChatFormatting.GRAY), true);
            } else {
                PlanetDungeons.Dungeon d = PlanetDungeons.nearest(name, CelestialGenerators.state(name).chunkManager,
                        (int) player.getX(), (int) player.getZ(), 4);
                if (d == null) {
                    player.displayClientMessage(Component.translatable("item.space_m.dungeon_locator.far").withStyle(ChatFormatting.GRAY), true);
                } else {
                    double dx = d.x() - player.getX(), dz = d.z() - player.getZ();
                    int dist = (int) Math.sqrt(dx * dx + dz * dz);
                    String[] dirs = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};
                    double deg = (Math.toDegrees(Math.atan2(-dx, dz)) + 360 + 22.5) % 360;
                    String dir = dirs[(int) (deg / 45) % 8];
                    player.displayClientMessage(Component.translatable("item.space_m.dungeon_locator.found", dist,
                            Component.translatable("item.space_m.dungeon_locator." + dir)).withStyle(ChatFormatting.GOLD), true);
                    Vec3 step = new Vec3(dx, 0, dz).normalize();
                    for (int i = 1; i <= 12; i++) {
                        Vec3 at = player.getEyePosition().add(step.scale(i)).add(0, -0.4, 0);
                        server.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 1, 0, 0, 0, 0);
                    }
                    server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8F, 1.4F);
                }
            }
        }
        player.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHbmTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemCustomLore.addLore(getDescriptionId(), tooltip);
    }
}
