package com.space_beyond.progression;

import com.space_m.rocket.RocketPart;
import com.space_m.rocket.RocketPart.FuelType;
import com.space_m.rocket.RocketPart.PartSize;
import com.space_m.rocket.RocketPart.PartType;

import net.minecraft.resources.ResourceLocation;

/**
 * Triebwerke der Planeten-Stufen (Mk.II - Mk.XI): Teil-Werte fuer die Raketen-Tabelle ({@link RocketPart}). Mk.N wird
 * mit dem Kern des Bosses von Stufe N-1 gebaut und ist Voraussetzung fuer Ziele der Stufe N
 * ({@link SpaceProgression}). Mk.II-IV laufen mit Methalox, ab Mk.V mit Wasserstoff; jede Stufe hat mehr Schub und
 * einen hoeheren spezifischen Impuls. Modell wie das Methalox-Triebwerk, Textur je Stufe eingefaerbt.
 */
public final class ProgressionParts {

    private ProgressionParts() {}

    /** Werte je Stufe 2..11: Masse (kg), Schub (N), ISP (s). */
    static final int[][] STATS = {
            {2300, 950_000, 330}, {2300, 1_050_000, 340}, {2250, 1_150_000, 350},
            {2500, 600_000, 400}, {2450, 720_000, 410}, {2400, 840_000, 425}, {2350, 980_000, 440},
            {2300, 1_120_000, 460}, {2250, 1_300_000, 490}, {2200, 1_500_000, 520}};

    public static final int FIRST = 2, LAST = 11;

    public static String id(int tier) {
        return "mp_thruster_20_mk" + tier;
    }

    public static FuelType fuel(int tier) {
        return tier <= 4 ? FuelType.METHALOX : FuelType.HYDROGEN;
    }

    public static void registerAll() {
        for (int tier = FIRST; tier <= LAST; tier++) {
            int[] st = STATS[tier - FIRST];
            String id = "space_beyond:" + id(tier);
            com.space_beyond.mixin.RocketPartInvoker.beyond$define(id, PartType.THRUSTER, PartSize.SIZE_20, PartSize.NONE, fuel(tier), 0, st[0], st[1], st[2], 2.0, 2.0,
                    "space_m:models/missile_parts/mp_t_20_kerosene.obj",
                    "space_beyond:textures/models/missile_parts/thrusters/mp_t_20_mk" + tier + ".png", null, null, false, null);
            SpaceProgression.setThrusterTier(ResourceLocation.tryParse(id), tier);
        }
    }
}
