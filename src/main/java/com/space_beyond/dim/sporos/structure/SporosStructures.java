package com.space_beyond.dim.sporos.structure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.city.SporosCities;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Freistehende Strukturen in der Wildnis von Sporos (eigene Erweiterung, Nutzerwunsch: Kraftwerk, Berghuette,
 * Siedlung als sichere Zone, verlassenes Krankenhaus; dazu ein kleiner Funkmast mit Ranger-Huette als haeufige
 * Struktur). Unabhaengig von den Staedten ({@link SporosCities}), gleiches Muster:
 * <ul>
 *   <li><b>Platzierung:</b> die Welt ist in Bezirke von {@link #DISTRICT_CHUNKS} Chunks geteilt; jeder Bezirk traegt
 *       mit {@link #CHANCE} eine Struktur an zufaelliger Stelle (Rand frei, damit nichts in den Nachbarbezirk ragt).
 *       Art nach Gewicht unter den Arten, deren Biome an der Stelle passen ({@link Type#fits}); faellt aus, wenn ein
 *       Chunk des Grundrisses in einer Stadt liegt. Rotation 0-3 (Vielfache von 90 Grad).</li>
 *   <li><b>Hoehe:</b> das Gelaende wird nicht auf Meereshoehe gezogen wie bei den Staedten, sondern die natuerliche
 *       Oberflaeche am Mittelpunkt wird per Sonden-Chunk bestimmt ({@link #baseY}, eigener Generator je Thread,
 *       ohne Strukturen) und das Gelaende im Grundriss darauf eingeebnet ({@link StructurePainter#terrain}).</li>
 *   <li><b>Erzeugung:</b> {@link #generateChunk} im Chunk-Puffer nach Gelaende, Hoehlen und Staedten
 *       ({@code ChunkProviderSporos.getChunkPrimer}); jeder Chunk malt seinen Ausschnitt ({@link StructurePainter}).
 *       Loot-Tables und Spawner setzt {@link StructureLoot} in der Dekorationsphase.</li>
 * </ul>
 */
public final class SporosStructures {

    private SporosStructures() {}

    /** Bezirksgroesse in Chunks (24 Chunks = 384 Bloecke). */
    public static final int DISTRICT_CHUNKS = 24;
    public static final float CHANCE = 0.7F;
    /** Breite des Uebergangs vom eingeebneten Grundriss zur Natur (Bloecke). */
    public static final int BLEND = 10;

    private static final long SALT = 0x57A7_0C7E_5C1L;

    /** Strukturarten: Loot-Table {@code chests/sporos_structure/<table>}, Halbbreite des Grundrisses in Bloecken. */
    public enum Type {
        /** Funkmast mit Ranger-Huette (klein, haeufig). */
        RADIO_TOWER("ranger", 9, 5),
        /** Berghuette im Hochland (Schnee auf Dach und Boden). */
        LODGE("lodge", 13, 4),
        /** Verlassenes Krankenhaus mit Keller voller Infizierter. */
        HOSPITAL("hospital", 23, 3),
        /** Kraftwerk: Turbinenhalle, Schornstein, Umspannfeld, Tanks, Lager. */
        POWER_PLANT("power_plant", 27, 3),
        /** Befestigte Siedlung (sichere Zone): Palisade, Wachtuerme, Haeuser, Brunnen, Felder. */
        SETTLEMENT("settlement", 36, 3);

        public final String table;
        public final int radius;
        public final int weight;

        Type(String table, int radius, int weight) {
            this.table = table;
            this.radius = radius;
            this.weight = weight;
        }

        /** Biome, in denen die Art stehen darf (nie Wasser/Sumpf/Kueste). */
        public boolean fits(BiomeGenBaseCelestial biome) {
            if (biome == BiomeGenBaseSporos.lake || biome == BiomeGenBaseSporos.river || biome == BiomeGenBaseSporos.swamp
                    || biome == BiomeGenBaseSporos.coast) return false;
            return switch (this) {
                case LODGE -> biome == BiomeGenBaseSporos.highland;
                case RADIO_TOWER -> true;
                case HOSPITAL, POWER_PLANT -> biome == BiomeGenBaseSporos.overgrownPlains || biome == BiomeGenBaseSporos.mossForest
                        || biome == BiomeGenBaseSporos.fungalGrove || biome == BiomeGenBaseSporos.sporeField;
                case SETTLEMENT -> biome == BiomeGenBaseSporos.overgrownPlains || biome == BiomeGenBaseSporos.sporeField
                        || biome == BiomeGenBaseSporos.mossForest;
            };
        }

        @Nullable
        public static Type byName(String name) {
            for (Type t : values()) if (t.name().equalsIgnoreCase(name) || t.table.equalsIgnoreCase(name)) return t;
            return null;
        }
    }

    /** Eine geplante Struktur: Mittelpunkt (Bloecke), Rotation 0-3, eigener Seed. */
    public record Structure(Type type, int x, int z, int rotation, long seed) {

        /** Aeusserster Block des Grundrisses inkl. Uebergang. */
        public int reach() {
            return type.radius + BLEND;
        }

        public boolean touchesChunk(int chunkX, int chunkZ) {
            int r = reach();
            return chunkX * 16 + 15 >= x - r && chunkX * 16 <= x + r && chunkZ * 16 + 15 >= z - r && chunkZ * 16 <= z + r;
        }

        /** Chebyshev-Abstand zum Mittelpunkt (quadratischer Grundriss). */
        public int distance(int bx, int bz) {
            return Math.max(Math.abs(bx - x), Math.abs(bz - z));
        }
    }

    /** Struktur, die diesen Chunk schneidet (mit Uebergang), oder null. */
    @Nullable
    public static Structure structureAt(CelestialWorld world, int chunkX, int chunkZ) {
        Structure s = ofDistrict(world, Math.floorDiv(chunkX, DISTRICT_CHUNKS), Math.floorDiv(chunkZ, DISTRICT_CHUNKS));
        return s != null && s.touchesChunk(chunkX, chunkZ) ? s : null;
    }

    /** Geplante Struktur eines Bezirks (oder null). Rein deterministisch aus Seed, Bezirk und Biomen. */
    @Nullable
    public static Structure ofDistrict(CelestialWorld world, int districtX, int districtZ) {
        long seed = CityRandom.mix(world.getSeed() ^ SALT, districtX, districtZ);
        Random rand = new Random(seed);
        if (rand.nextFloat() >= CHANCE) return null;
        int marginChunks = (Type.SETTLEMENT.radius + BLEND) / 16 + 2;
        int span = DISTRICT_CHUNKS - 2 * marginChunks;
        int cx = districtX * DISTRICT_CHUNKS + marginChunks + rand.nextInt(span);
        int cz = districtZ * DISTRICT_CHUNKS + marginChunks + rand.nextInt(span);
        int x = cx * 16 + rand.nextInt(16), z = cz * 16 + rand.nextInt(16);
        int rotation = rand.nextInt(4);
        long structSeed = rand.nextLong();

        BiomeGenBaseCelestial biome = world.getBiomeGenForCoords(x, z);
        int total = 0;
        for (Type t : Type.values()) if (t.fits(biome)) total += t.weight;
        if (total == 0) return null;
        int pick = rand.nextInt(total);
        Type type = null;
        for (Type t : Type.values()) {
            if (!t.fits(biome)) continue;
            pick -= t.weight;
            if (pick < 0) {
                type = t;
                break;
            }
        }
        if (type == null) return null;
        Structure s = new Structure(type, x, z, rotation, structSeed);
        // keine Ueberschneidung mit Staedten: jeder Chunk des Grundrisses (inkl. Uebergang) muss stadtfrei sein
        int r = s.reach();
        for (int chx = (x - r) >> 4; chx <= (x + r) >> 4; chx++) {
            for (int chz = (z - r) >> 4; chz <= (z + r) >> 4; chz++) {
                if (SporosCities.cityAt(world.getSeed(), chx, chz) != null) return null;
            }
        }
        return s;
    }

    /** Naechste Struktur der Art {@code type} (null = jede) zu (x, z) im Umkreis von {@code rangeDistricts} Bezirken, oder null. */
    @Nullable
    public static Structure nearest(CelestialWorld world, @Nullable Type type, int x, int z, int rangeDistricts) {
        int dx0 = Math.floorDiv(x >> 4, DISTRICT_CHUNKS), dz0 = Math.floorDiv(z >> 4, DISTRICT_CHUNKS);
        Structure best = null;
        double bestD = Double.MAX_VALUE;
        for (int dx = dx0 - rangeDistricts; dx <= dx0 + rangeDistricts; dx++) {
            for (int dz = dz0 - rangeDistricts; dz <= dz0 + rangeDistricts; dz++) {
                Structure s = ofDistrict(world, dx, dz);
                if (s == null || (type != null && s.type() != type)) continue;
                double d = Math.hypot(s.x() - x, s.z() - z);
                if (d < bestD) {
                    bestD = d;
                    best = s;
                }
            }
        }
        return best;
    }

    // ── Erzeugung ───────────────────────────────────────────────────────────

    private static final ThreadLocal<Boolean> PROBING = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<ChunkProviderCelestial> PROBE = new ThreadLocal<>();
    private static final ThreadLocal<Long> PROBE_SEED = new ThreadLocal<>();

    private static final ThreadLocal<Map<Structure, StructurePlan>> PLANS = ThreadLocal.withInitial(() -> new LinkedHashMap<>(8, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Structure, StructurePlan> eldest) {
            return size() > 6;
        }
    });

    /** Aufruf aus {@code ChunkProviderSporos.getChunkPrimer} (nach Gelaende, Hoehlen, Staedten). */
    public static void generateChunk(CelestialWorld world, int chunkX, int chunkZ, BlockState[] blocks) {
        if (PROBING.get()) return;
        Structure s = structureAt(world, chunkX, chunkZ);
        if (s == null) return;
        StructurePlan plan = plan(world, s);
        StructurePainter.paint(new CityCanvas(blocks, chunkX, chunkZ), plan);
    }

    /** Planung einer Struktur (je Thread zwischengespeichert; enthaelt die Grundhoehe). */
    public static StructurePlan plan(CelestialWorld world, Structure s) {
        Map<Structure, StructurePlan> plans = PLANS.get();
        StructurePlan p = plans.get(s);
        if (p == null) {
            p = new StructurePlan(s, baseY(world, s));
            plans.put(s, p);
        }
        return p;
    }

    /**
     * Natuerliche Oberflaeche am Mittelpunkt: Median der Spaltenhoehen des Mittel-Chunks aus einem Sonden-Generator
     * (eigene Instanz je Thread, Strukturen abgeschaltet, Staedte/Hoehlen wie im echten Lauf). Mindestens ein Block ueber
     * dem Meer, damit nichts im Wasser versinkt.
     */
    public static int baseY(CelestialWorld world, Structure s) {
        ChunkProviderCelestial probe = PROBE.get();
        if (probe == null || PROBE_SEED.get() == null || PROBE_SEED.get() != world.getSeed()) {
            probe = CelestialGenerators.require(SporosBlocks.BODY).chunkProvider().apply(world, world.getSeed());
            PROBE.set(probe);
            PROBE_SEED.set(world.getSeed());
        }
        PROBING.set(true);
        BlockState[] blocks;
        try {
            blocks = probe.provideChunk(s.x() >> 4, s.z() >> 4).blocks;
        } finally {
            PROBING.set(false);
        }
        int[] heights = new int[256];
        int n = 0;
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int base = (lx * 16 + lz) * CityCanvas.HEIGHT;
                for (int y = CityCanvas.HEIGHT - 1; y > 0; y--) {
                    BlockState b = blocks[base + y];
                    if (b != null && !b.isAir() && b.getFluidState().isEmpty()) {
                        heights[n++] = y;
                        break;
                    }
                }
            }
        }
        if (n == 0) return BiomeGenBaseSporos.SEA_LEVEL + 1;
        java.util.Arrays.sort(heights, 0, n);
        return Math.max(BiomeGenBaseSporos.SEA_LEVEL + 1, heights[n / 2]);
    }
}
