package com.space_beyond.celestial;

import com.space_m.celestial.CelestialBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.space_m.network.SpacePacket;
import com.space_m.network.SpacePacketHandler;
import com.space_m.platform.SavedDataHooks;
import com.space_m.platform.SpaceSavedData;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Besuchte Himmelskoerper je Spieler (fuer die Infotafel der Sternkarte: Werte und Erze erst nach dem ersten Besuch).
 * Server: alle 2 s wird der Koerper der aktuellen Dimension eingetragen (nur landbare Koerper), gespeichert global
 * ({@link Data}), bei Aenderung und beim Einloggen an den Spieler geschickt. Client: {@link #client()}.
 */
public final class VisitedBodies {

    private VisitedBodies() {}

    private static volatile Set<String> clientVisited = Collections.emptySet();
    private static boolean initialized;

    /** Aus {@code SpaceRegistry.init()}. */
    public static void init() {
        if (initialized) return;
        initialized = true;
        TickEvent.PLAYER_POST.register(player -> {
            if (player instanceof ServerPlayer sp && sp.tickCount % 40 == 0) visit(sp);
        });
        PlayerEvent.PLAYER_JOIN.register(player -> {
            visit(player);
            sync(player);
        });
    }

    /** Traegt den Koerper unter dem Spieler ein (nur landbare Koerper mit eigener Dimension). */
    public static void visit(ServerPlayer player) {
        CelestialBody body = CelestialBody.getBodyOrNull(player.level().dimension());
        if (body == null || !body.canLand || body.name == null) return;
        Data data = Data.get(player.server);
        if (data.visited.computeIfAbsent(player.getUUID(), u -> new LinkedHashSet<>()).add(body.name)) {
            data.setDirty();
            sync(player);
        }
    }

    public static boolean hasVisited(MinecraftServer server, UUID player, String body) {
        Set<String> s = Data.get(server).visited.get(player);
        return s != null && s.contains(body);
    }

    private static void sync(ServerPlayer player) {
        Set<String> s = Data.get(player.server).visited.get(player.getUUID());
        SpacePacketHandler.sendToPlayer(player, com.space_beyond.network.BeyondPackets.VISITED_BODIES, new Packet(s == null ? List.of() : new ArrayList<>(s)));
    }

    /** Client: besuchte Koerper des eigenen Spielers (Namen wie {@link CelestialBody#name}). */
    public static Set<String> client() {
        return clientVisited;
    }

    /** S2C: Liste der besuchten Koerper. */
    public record Packet(List<String> bodies) implements SpacePacket {
        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(bodies.size());
            for (String b : bodies) buf.writeUtf(b);
        }

        public static Packet decode(FriendlyByteBuf buf) {
            int n = buf.readVarInt();
            List<String> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++) list.add(buf.readUtf());
            return new Packet(list);
        }

        public void apply() {
            clientVisited = Collections.unmodifiableSet(new LinkedHashSet<>(bodies));
        }
    }

    /** Global gespeichert: Spieler -> besuchte Koerper. */
    public static final class Data extends SpaceSavedData {
        public static final String NAME = "space_m_visited";
        final Map<UUID, Set<String>> visited = new HashMap<>();

        static Data get(MinecraftServer server) {
            return SavedDataHooks.global(server, NAME, Data::new, Data::load);
        }

        static Data load(CompoundTag tag) {
            Data d = new Data();
            CompoundTag players = tag.getCompound("Players");
            for (String key : players.getAllKeys()) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                ListTag list = players.getList(key, Tag.TAG_STRING);
                Set<String> s = new LinkedHashSet<>();
                for (int i = 0; i < list.size(); i++) s.add(list.getString(i));
                d.visited.put(id, s);
            }
            return d;
        }

        @Override
        protected CompoundTag write(CompoundTag tag) {
            CompoundTag players = new CompoundTag();
            for (Map.Entry<UUID, Set<String>> e : visited.entrySet()) {
                ListTag list = new ListTag();
                for (String b : e.getValue()) list.add(StringTag.valueOf(b));
                players.put(e.getKey().toString(), list);
            }
            tag.put("Players", players);
            return tag;
        }
    }
}
