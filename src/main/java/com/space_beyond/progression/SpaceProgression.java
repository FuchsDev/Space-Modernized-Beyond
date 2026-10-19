package com.space_beyond.progression;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.space_m.celestial.CelestialBody;
import com.space_m.platform.SavedDataHooks;
import com.space_m.platform.SpaceSavedData;
import com.space_m.rocket.RocketPart;
import com.space_m.rocket.RocketStruct;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Planeten-Fortschritt (eigene Erweiterung, Nutzerwunsch: Planeten nicht frei anfliegbar, sondern nacheinander).
 * <p><b>Reihenfolge</b> (nach Entfernung, {@link Stage}): Kerbin (Start) -> Mun -> Minmus -> Duna -> Ike -> Dres -> Moho
 * -> Eve -> Laythe -> Tekto -> Sporos -> Wemburos. Jeder Planet hat eine Stufe 1..11, Kerbin und der Orbit 0.
 * <ul>
 *   <li><b>Raketen</b>: Ein Ziel der Stufe N braucht mindestens ein Triebwerk der Stufe N in der Rakete. Alle bisherigen
 *       Triebwerke sind Stufe 1 (Mun). Triebwerk Mk.N ({@link ProgressionParts}) wird mit dem Kern des Bosses von Planet
 *       N-1 und Material dieses Planeten gebaut. Die Pruefung steckt in {@link RocketStruct#hasSufficientFuel} und gilt
 *       damit am Startplatz, beim Start der Rakete und beim Kurswechsel im Flug.</li>
 *   <li><b>Raumstationen</b> (einziger Weg ins Sporos-System): ein Ziel der Stufe N verlangt, dass auf diesem Server
 *       der Boss von Stufe N-1 besiegt wurde ({@link Data}, global gespeichert).</li>
 *   <li>Rueckflug nach Kerbin und in den Orbit ist immer erlaubt; Stufen-Bosse schalten beim Tod frei.</li>
 * </ul>
 * Abschaltbar ueber {@code SpaceConfig.progressionLocks}.
 */
public final class SpaceProgression {

    private SpaceProgression() {}

    /** Stufen in Reiseordnung (Ordinal + 1 = Stufe). */
    public enum Stage {
        MUN("mun"), MINMUS("minmus"), DUNA("duna"), IKE("ike"), DRES("dres"), MOHO("moho"), EVE("eve"),
        LAYTHE("laythe"), TEKTO("tekto"), SPOROS("sporos"), WEMBUROS("wemburos");

        public final String body;

        Stage(String body) {
            this.body = body;
        }

        public int tier() {
            return ordinal() + 1;
        }

        @Nullable
        public static Stage byBody(@Nullable String name) {
            if (name == null) return null;
            for (Stage s : values()) if (s.body.equals(name)) return s;
            return null;
        }

        @Nullable
        public static Stage byTier(int tier) {
            return tier >= 1 && tier <= values().length ? values()[tier - 1] : null;
        }
    }

    public static boolean enabled() {
        try {
            return com.space_beyond.config.BeyondConfig.get().progressionLocks;
        } catch (Throwable t) {
            return true;
        }
    }

    /** Benoetigte Stufe, um {@code body} zu erreichen (0 = frei). */
    public static int requiredTier(@Nullable CelestialBody body) {
        if (body == null) return 0;
        Stage s = Stage.byBody(body.name);
        return s == null ? 0 : s.tier();
    }

    // ── Triebwerke ─────────────────────────────────────────────────────────

    private static final Map<ResourceLocation, Integer> THRUSTER_TIERS = new HashMap<>();

    /** Stufe eines Triebwerks (Mk.N), aus {@link ProgressionParts}. */
    static void setThrusterTier(ResourceLocation id, int tier) {
        THRUSTER_TIERS.put(id, tier);
    }

    /** Stufe eines Triebwerks: Mk.N -> N, alle anderen 1. */
    public static int thrusterTier(@Nullable RocketPart part) {
        if (part == null) return 0;
        return THRUSTER_TIERS.getOrDefault(part.id, 1);
    }

    /** Stufe einer Rakete: hoechstes Triebwerk aller Stufen (0 ohne Triebwerk). */
    public static int rocketTier(RocketStruct rocket) {
        int tier = 0;
        for (RocketStruct.RocketStage stage : rocket.stages) tier = Math.max(tier, thrusterTier(stage.thruster));
        return tier;
    }

    /** Darf diese Rakete das Ziel anfliegen (nur Stufen, nicht Treibstoff)? */
    public static boolean rocketMayReach(RocketStruct rocket, @Nullable CelestialBody to) {
        if (!enabled() || to == null) return true;
        int need = requiredTier(to);
        if (need <= 0) return true;
        // Kapseln/Pods ohne Stufen fliegen nur zum umkreisten Koerper zurueck (dort ist man schon gewesen)
        if (rocket.stages.isEmpty()) return true;
        return rocketTier(rocket) >= need;
    }

    /** Hinweistext fuer GUIs, wenn die Stufe fehlt (sonst null). */
    @Nullable
    public static Component tierIssue(RocketStruct rocket, @Nullable CelestialBody to) {
        if (rocketMayReach(rocket, to)) return null;
        int need = requiredTier(to);
        return Component.translatable("progression.rocket.tierLocked", roman(need), roman(rocketTier(rocket)));
    }

    public static String roman(int n) {
        String[] r = {"0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        return n >= 0 && n < r.length ? r[n] : String.valueOf(n);
    }

    // ── Bosse und Stationen ────────────────────────────────────────────────

    /** Hoechste vom Server freigeschaltete Stufe (Bosse), fuer Stationen; auf dem Client ohne Bedeutung. */
    private static volatile int serverUnlocked = 1;

    /** Station darf {@code to} anfliegen? (Server; Stufe N braucht den besiegten Boss von Stufe N-1). */
    public static boolean stationMayTravel(@Nullable CelestialBody to) {
        if (!enabled()) return true;
        return requiredTier(to) <= serverUnlocked;
    }

    /** Nur fuer GameTests: Freischaltung fuer Stationen direkt setzen. */
    public static void setUnlockedForTests(int tier) {
        serverUnlocked = tier;
    }

    public static int unlockedTier() {
        return serverUnlocked;
    }

    /** Aufruf beim Tod eines Stufen-Bosses: schaltet Stufe {@code stage + 1} fuer Stationen frei, Meldung an alle. */
    public static void bossDefeated(ServerLevel level, Stage stage) {
        MinecraftServer server = level.getServer();
        Data data = Data.get(server);
        if (data.defeated < stage.tier()) {
            data.defeated = stage.tier();
            data.setDirty();
            serverUnlocked = data.defeated + 1;
            Stage next = Stage.byTier(stage.tier() + 1);
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                p.sendSystemMessage(next != null
                        ? Component.translatable("progression.boss.unlocked", Component.translatable("progression.stage." + stage.body),
                                Component.translatable("progression.stage." + next.body), roman(next.tier()))
                        : Component.translatable("progression.boss.final"));
            }
        }
    }

    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(server -> serverUnlocked = Data.get(server).defeated + 1);
        LifecycleEvent.SERVER_STOPPED.register(server -> serverUnlocked = 1);
    }

    /** Global (Overworld) gespeichert: hoechste besiegte Boss-Stufe. */
    public static final class Data extends SpaceSavedData {
        public static final String NAME = "space_m_progression";
        public int defeated;

        public static Data get(MinecraftServer server) {
            return SavedDataHooks.global(server, NAME, Data::new, Data::load);
        }

        static Data load(CompoundTag tag) {
            Data d = new Data();
            d.defeated = tag.getInt("Defeated");
            return d;
        }

        @Override
        protected CompoundTag write(CompoundTag tag) {
            tag.putInt("Defeated", defeated);
            return tag;
        }
    }
}
