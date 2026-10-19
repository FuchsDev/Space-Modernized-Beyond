package com.space_beyond.dim.dungeon.seal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Logik der Siegelwaende: eine Wand ist die zusammenhaengende Menge aus Siegelfeldern und -schloessern (6er-Nachbarschaft,
 * hoechstens {@link #REACH} Bloecke vom angeklickten Schloss). Sind alle Schloesser gefuellt, verschwinden die Felder.
 */
public final class SealGates {

    private SealGates() {}

    public static final int REACH = 24;

    record Gate(List<BlockPos> fields, List<BlockPos> locks, int lit) { }

    static Gate scan(ServerLevel level, BlockPos start) {
        List<BlockPos> fields = new ArrayList<>(), locks = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        seen.add(start);
        int lit = 0;
        while (!queue.isEmpty() && seen.size() < 6000) {
            BlockPos p = queue.poll();
            BlockState s = level.getBlockState(p);
            if (s.is(SealBlocks.SEAL_FIELD.get())) fields.add(p);
            else if (s.is(SealBlocks.SEAL_LOCK.get())) {
                locks.add(p);
                if (s.getValue(SealLockBlock.LIT)) lit++;
            } else continue;
            for (Direction d : Direction.values()) {
                BlockPos n = p.relative(d);
                if (n.distManhattan(start) > REACH || !seen.add(n)) continue;
                queue.add(n);
            }
        }
        return new Gate(fields, locks, lit);
    }

    /** Schloss ohne Schluessel angeklickt: Stand anzeigen. */
    static void status(ServerLevel level, BlockPos pos, Player player, boolean filled) {
        Gate g = scan(level, pos);
        player.displayClientMessage(Component.translatable("block.space_m.seal_lock.need", g.lit(), g.locks().size()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.6F);
    }

    /** Schloss bei {@code pos} fuellen (wie mit einem Schluessel); fuer Befehle und Tests. */
    public static void fill(ServerLevel level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        if (!s.is(SealBlocks.SEAL_LOCK.get()) || s.getValue(SealLockBlock.LIT)) return;
        level.setBlock(pos, s.setValue(SealLockBlock.LIT, true), 3);
        lockFilled(level, pos, null);
    }

    /** Schluessel eingesetzt: Klang, Partikel; alle gefuellt -> Siegel bricht. */
    static void lockFilled(ServerLevel level, BlockPos pos, Player player) {
        level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.5F, 0.8F);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 30, 0.4, 0.4, 0.4, 0.05);
        Gate g = scan(level, pos);
        if (g.lit() < g.locks().size()) {
            for (Player p : level.players()) {
                if (p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 48 * 48) {
                    p.displayClientMessage(Component.translatable("block.space_m.seal_lock.filled", g.lit(), g.locks().size()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
                }
            }
            return;
        }
        for (BlockPos f : g.fields()) {
            level.setBlock(f, Blocks.AIR.defaultBlockState(), 3);
            if (level.random.nextInt(3) == 0) {
                level.sendParticles(ParticleTypes.END_ROD, f.getX() + 0.5, f.getY() + 0.5, f.getZ() + 0.5, 4, 0.3, 0.3, 0.3, 0.08);
            }
        }
        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 2.0F, 1.0F);
        level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 2.0F, 0.6F);
        // Der Boss wartet nicht schon seit dem Generieren in der Arena, sondern erwacht erst jetzt
        com.space_beyond.dim.dungeon.PlanetDungeons.spawnBoss(level, pos);
        for (Player p : level.players()) {
            if (p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64) {
                p.displayClientMessage(Component.translatable("block.space_m.seal_lock.open").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
            }
        }
    }
}
