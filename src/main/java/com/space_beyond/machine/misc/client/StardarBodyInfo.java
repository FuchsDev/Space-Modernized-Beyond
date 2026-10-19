package com.space_beyond.machine.misc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_m.celestial.trait.CBT_Atmosphere;
import com.space_m.dim.PlanetOres;
import com.space_m.resources.CelestialBedrockOre;
import com.space_m.resources.OreLocations;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Daten der Infotafel der Sternkarte je Himmelskoerper: Sonnenlicht, Durchmesser, Schwerkraft, Tageslaenge, Umlaufzeit,
 * Atmosphaerendruck, Monde, normale Erze ({@link OreLocations}, Symbol = Planetenvariante des Erzblocks) und
 * Bedrock-Erze ({@link CelestialBedrockOre}, Symbol = Fragment der Ausbeute). Erze werden je Koerper zwischengespeichert.
 */
final class StardarBodyInfo {

    private StardarBodyInfo() {}

    record Ores(List<ItemStack> normal, List<ItemStack> bedrock) { }

    private static final Map<String, Ores> ORES = new HashMap<>();

    @Nullable
    static SolarSystem.Body bodyEnum(CelestialBody body) {
        if (body == null || body.name == null) return null;
        for (SolarSystem.Body b : SolarSystem.Body.values()) if (body.name.equals(b.name)) return b;
        return null;
    }

    /** Sonnenlicht in Prozent der Heimat (Erde = 100). */
    static float sunPercent(CelestialBody body) {
        try {
            return body.getSunPower() * 100F;
        } catch (RuntimeException e) {
            return 0F;
        }
    }

    static float gravityG(CelestialBody body) {
        return body.radiusKm > 0 ? body.getSurfaceGravity() / 9.81F : 0F;
    }

    /** Tageslaenge in Minuten (Spielzeit, Erde = 20). */
    static double dayMinutes(CelestialBody body) {
        return body.getRotationalPeriod() / 1200.0;
    }

    /** Umlaufzeit in Tagen; 0 fuer Sterne. */
    static double yearDays(CelestialBody body) {
        if (body.parent == null || body.semiMajorAxisKm <= 0) return 0;
        try {
            return body.getOrbitalPeriod();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    /** Atmosphaerendruck in atm (0 = Vakuum). */
    static double pressure(CelestialBody body) {
        CBT_Atmosphere atmo = body.getTrait(CBT_Atmosphere.class);
        if (atmo == null) atmo = body.getDefaultTrait(CBT_Atmosphere.class);
        return atmo == null ? 0 : atmo.getPressure();
    }

    static int moons(CelestialBody body) {
        return body.satellites == null ? 0 : body.satellites.size();
    }

    static Ores ores(CelestialBody body) {
        return ORES.computeIfAbsent(body.name, n -> compute(body));
    }

    private static Ores compute(CelestialBody body) {
        List<ItemStack> normal = new ArrayList<>(), bedrock = new ArrayList<>();
        SolarSystem.Body b = bodyEnum(body);
        if (b == null || b == SolarSystem.Body.ORBIT || !body.canLand) return new Ores(normal, bedrock);
        Set<Item> seen = new LinkedHashSet<>();
        if (b == SolarSystem.Body.KERBIN) add(normal, seen, Items.COAL_ORE);
        for (String ore : OreLocations.ores()) {
            if (!OreLocations.spawnsOn(ore).contains(b)) continue;
            Item icon = null;
            PlanetOres.Variant v = PlanetOres.get(ore, body.name);
            if (v != null) icon = v.block().get().asItem();
            if (icon == null || icon == Items.AIR) icon = com.space_beyond.resources.BeyondOreIcons.iconOf(ore);
            add(normal, seen, icon);
        }
        CelestialBedrockOre bo = CelestialBedrockOre.get(b);
        if (bo != null) {
            Set<Item> seenB = new LinkedHashSet<>();
            for (CelestialBedrockOre.Type t : bo.types) {
                for (CelestialBedrockOre.Output o : t.outputs()) {
                    if (o == null) continue;
                    add(bedrock, seenB, CelestialBedrockOre.fragment(o));
                }
            }
        }
        return new Ores(normal, bedrock);
    }

    private static void add(List<ItemStack> list, Set<Item> seen, @Nullable Item item) {
        if (item == null || item == Items.AIR || !seen.add(item)) return;
        list.add(new ItemStack(item));
    }
}
