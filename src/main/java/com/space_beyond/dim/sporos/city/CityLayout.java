package com.space_beyond.dim.sporos.city;

import java.util.LinkedHashMap;
import java.util.Map;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.SporosCities.City;

/**
 * Stadtweite Groessen einer geplanten Stadt: Strassenniveau, Rasterzellen, Rand-Faktor.
 *
 * <p><b>Rasterzelle:</b> Eine Zelle {@code (gx, gz)} ist ein 32 x 32-Block-Quadrat, beginnend 4 Bloecke vor einer
 * Strassenmitte. Sie besitzt ihre Kreuzung (lokal x,z 0-7), das Strassenstueck im Westen (x 0-7), das im Norden
 * (z 0-7) und das Grundstueck (x,z 8-31, davon 2 Bloecke Gehweg ringsum, Bauflaeche 10-29). Alles, was eine Zelle
 * baut, bleibt in ihrer Zelle; ein Chunk (16 x 16) schneidet hoechstens 2 x 2 Zellen.
 */
public final class CityLayout {

    /** Bis zu diesem Rand-Faktor ist die Stadt eben und bebaut; danach geht das Gelaende in die Natur ueber. */
    public static final double URBAN = 0.8;
    public static final int CELL = SporosCities.STREET_PITCH;
    public static final int STREET = SporosCities.STREET_WIDTH;
    /** Erste/letzte lokale Koordinate der Bauflaeche (innerhalb des Gehwegs). */
    public static final int LOT_MIN = STREET + 2, LOT_MAX = CELL - 3;

    public final City city;
    public final long seed;
    /** Oberkante Strasse = Fussboden Erdgeschoss. */
    public final int baseY;
    /** Stadtweiter Stil: 0 = Beton, 1 = Ziegel, 2 = gemischt. */
    public final int style;
    private final double radiusBlocks;

    private CityLayout(City city) {
        this.city = city;
        this.seed = city.seed();
        this.baseY = BiomeGenBaseSporos.SEA_LEVEL + 2 + (int) Math.floorMod(seed >>> 7, 3L);
        this.style = (int) Math.floorMod(seed >>> 17, 3L);
        // Spalten von Chunks ausserhalb der Stadt liegen mindestens 16R - 8*sqrt2 vom Mittelpunkt -> dort ist edge >= 1
        this.radiusBlocks = city.radiusChunks() * 16.0D - 12.0D;
    }

    private static final ThreadLocal<Map<City, CityLayout>> LAYOUTS = ThreadLocal.withInitial(() -> new LinkedHashMap<>(8, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<City, CityLayout> eldest) {
            return size() > 8;
        }
    });

    public static CityLayout of(City city) {
        return LAYOUTS.get().computeIfAbsent(city, CityLayout::new);
    }

    /** 0 = Mitte, 1 = Rand (und alles ausserhalb der Stadtchunks). */
    public double edge(int x, int z) {
        double dx = x - city.originX(), dz = z - city.originZ();
        return Math.min(1.0D, Math.sqrt(dx * dx + dz * dz) / radiusBlocks);
    }

    /** Rand-Faktor mit leicht gewelltem Verlauf, damit der Stadtrand nicht kreisrund aussieht. */
    public double edgeJittered(int x, int z) {
        return Math.min(1.0D, edge(x, z) + (CityRandom.noise(seed ^ 0x51L, x, z, 24) - 0.5D) * 0.08D);
    }

    public int cellX(int x) {
        return Math.floorDiv(x - city.originX() + STREET / 2, CELL);
    }

    public int cellZ(int z) {
        return Math.floorDiv(z - city.originZ() + STREET / 2, CELL);
    }

    public int cellMinX(int gx) {
        return city.originX() - STREET / 2 + gx * CELL;
    }

    public int cellMinZ(int gz) {
        return city.originZ() - STREET / 2 + gz * CELL;
    }

    // ── Zellplanung (pro Thread kurz zwischengespeichert) ───────────────────

    private final Map<Long, CellPlan> plans = new LinkedHashMap<>(32, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, CellPlan> eldest) {
            return size() > 48;
        }
    };

    public CellPlan cell(CelestialWorld world, int gx, int gz) {
        long key = ((long) gx & 0xFFFFFFFFL) | (((long) gz & 0xFFFFFFFFL) << 32);
        CellPlan p = plans.get(key);
        if (p == null) {
            p = new CellPlan(this, gx, gz, wetCell(world, gx, gz));
            plans.put(key, p);
        }
        return p;
    }

    /** Ueberflutete Zelle: Sumpf/Fluss unter der Grundstuecksmitte oder eine zufaellige Senke. */
    private boolean wetCell(CelestialWorld world, int gx, int gz) {
        int cx = cellMinX(gx) + CELL / 2 + 4, cz = cellMinZ(gz) + CELL / 2 + 4;
        if (edge(cx, cz) >= URBAN) return false;
        BiomeGenBaseCelestial b = world.getBiomeGenForCoords(cx, cz);
        if (b == BiomeGenBaseSporos.swamp || b == BiomeGenBaseSporos.river) return true;
        return CityRandom.unit(seed ^ 0x3E7L, gx, 0, gz) < 0.04D;
    }
}
