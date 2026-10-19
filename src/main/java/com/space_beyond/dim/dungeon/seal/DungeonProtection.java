package com.space_beyond.dim.dungeon.seal;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.space_m.atmosphere.ISuffocationImmune;
import com.space_m.celestial.CelestialBody;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.dungeon.DungeonDesign;
import com.space_beyond.dim.dungeon.DungeonPlan;
import com.space_beyond.dim.dungeon.PlanetDungeons;
import com.space_beyond.dim.wemburos.WemburosBlocks;
import com.space_beyond.dim.wemburos.structure.WemburosStructures;
import com.space_m.platform.SavedDataHooks;
import com.space_m.platform.SpaceSavedData;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;

/**
 * Schutz der Boss-Dungeons (Nutzerwunsch: kein Durchgraben, kein Ueberbruecken): solange der Boss eines Dungeons lebt,
 * koennen Spieler im Ueberlebensmodus innerhalb seines Grundrisses (und Hoehenbereichs) keine Bloecke abbauen (ausser
 * Spawnern und Spinnweben), setzen oder Eimer ausleeren; Explosionen zerstoeren dort nichts. Nach dem Sieg ({@link #markDefeated}) faellt der Schutz.
 * Gilt fuer die Planeten-Dungeons ({@link PlanetDungeons}) und die Zitadelle auf Wemburos.
 */
public final class DungeonProtection {

    private DungeonProtection() {}

    /** Ein geschuetzter Bereich: Schluessel fuer den Sieg, Mittelpunkt, Radius, Hoehenbereich. */
    public record Site(String key, int x, int z, int radius, int minY, int maxY) {
        public boolean contains(BlockPos p) {
            return Math.max(Math.abs(p.getX() - x), Math.abs(p.getZ() - z)) <= radius && p.getY() >= minY && p.getY() <= maxY;
        }
    }

    public static void init() {
        // Spawner und Spinnweben duerfen weg (klassisches Dungeon-Spiel), alles andere nicht
        BlockEvent.BREAK.register((level, pos, state, player, xp) -> !state.is(net.minecraft.world.level.block.Blocks.SPAWNER)
                && !state.is(net.minecraft.world.level.block.Blocks.COBWEB) && deny(level, pos, player) ? EventResult.interruptFalse() : EventResult.pass());
        // Dungeon-Bewohner (Spawner-Mobs, Waechter, Diener) ersticken nicht im Vakuum des Planeten
        EntityEvent.ADD.register((entity, level) -> {
            if (entity instanceof Mob && level instanceof ServerLevel sl && !entity.getTags().contains(com.space_beyond.lib.BeyondTags.DUNGEON_TAG)) {
                BlockPos pos = entity.blockPosition();
                Site s = siteAt(sl, pos);
                if (s != null && s.contains(pos)) entity.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG);
            }
            return EventResult.pass();
        });
        BlockEvent.PLACE.register((level, pos, state, placer) -> placer instanceof Player p && deny(level, pos, p)
                ? EventResult.interruptFalse() : EventResult.pass());
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> player.getItemInHand(hand).getItem() instanceof BucketItem
                && deny(player.level(), pos.relative(face), player) ? EventResult.interruptFalse() : EventResult.pass());
        ExplosionEvent.DETONATE.register((level, explosion, entities) -> {
            if (level instanceof ServerLevel server) explosion.getToBlow().removeIf(p -> isProtected(server, p));
        });
    }

    private static boolean deny(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel server) || player == null || player.isCreative() || player.isSpectator()) return false;
        if (!isProtected(server, pos)) return false;
        player.displayClientMessage(Component.translatable("dungeon.space_m.protected").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return true;
    }

    public static boolean isProtected(ServerLevel level, BlockPos pos) {
        Site s = siteAt(level, pos);
        return s != null && s.contains(pos) && !Data.get(level.getServer()).defeated.contains(s.key());
    }

    /** Dungeon/Zitadelle, deren Grundriss diese Spalte enthaelt (ohne Sieg-Pruefung). */
    @Nullable
    public static Site siteAt(ServerLevel level, BlockPos pos) {
        CelestialBody body = CelestialBody.getBodyOrNull(level.dimension());
        if (body == null) return null;
        String name = body.name;
        int cx = pos.getX() >> 4, cz = pos.getZ() >> 4;
        if (WemburosBlocks.BODY.equals(name)) {
            CelestialWorld world = CelestialGenerators.state(name).chunkManager;
            WemburosStructures.Structure s = WemburosStructures.structureAt(world, cx, cz);
            if (s == null || s.type() != WemburosStructures.Type.THRONE) return null;
            WemburosStructures.Plan p = WemburosStructures.plan(world, s);
            return new Site(name + ":" + s.x() + ":" + s.z(), s.x(), s.z(), s.type().radius, p.base - 52, p.base + 112);
        }
        DungeonDesign design = PlanetDungeons.design(name);
        if (design == null) return null;
        CelestialWorld world = CelestialGenerators.state(name).chunkManager;
        PlanetDungeons.Dungeon d = PlanetDungeons.at(name, world, cx, cz);
        if (d == null) return null;
        DungeonPlan p = PlanetDungeons.plan(world, d);
        return new Site(name + ":" + d.x() + ":" + d.z(), d.x(), d.z(), design.radius(), p.base + design.minY() - 2, p.base + design.maxY() + 2);
    }

    /** Aufruf beim Tod eines Dungeon-Bosses: Schutz seines Dungeons aufheben. */
    public static void markDefeated(ServerLevel level, BlockPos bossPos) {
        Site s = siteAt(level, bossPos);
        if (s == null) return;
        Data data = Data.get(level.getServer());
        if (data.defeated.add(s.key())) data.setDirty();
    }

    /** Global gespeichert: Schluessel der besiegten Dungeons. */
    public static final class Data extends SpaceSavedData {
        public static final String NAME = "space_m_dungeons";
        public final Set<String> defeated = new HashSet<>();

        public static Data get(MinecraftServer server) {
            return SavedDataHooks.global(server, NAME, Data::new, Data::load);
        }

        static Data load(CompoundTag tag) {
            Data d = new Data();
            ListTag list = tag.getList("Defeated", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) d.defeated.add(list.getString(i));
            return d;
        }

        @Override
        protected CompoundTag write(CompoundTag tag) {
            ListTag list = new ListTag();
            for (String k : defeated) list.add(StringTag.valueOf(k));
            tag.put("Defeated", list);
            return tag;
        }
    }
}
