package com.space_beyond.dim.sporos.city;

import org.jetbrains.annotations.Nullable;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Planung der Sporos-Staedte (Runde 3, eigener Code ohne Lost-Cities-Abhaengigkeit).
 *
 * <p>Geruest, auf dem {@link SporosCityGenerator} aufsetzt:
 * <ul>
 *   <li>{@link #cityAt}: deterministische Stadtplanung aus Weltseed und Chunk-Koordinaten. Die Welt ist in
 *       Bezirke von {@link #DISTRICT_CHUNKS} x {@link #DISTRICT_CHUNKS} Chunks geteilt; jeder Bezirk traegt mit
 *       Wahrscheinlichkeit {@link #CITY_CHANCE} eine Stadt mit zufaelligem, vom Bezirksrand ferngehaltenem
 *       Mittelpunkt und Radius {@link #MIN_RADIUS_CHUNKS}-{@link #MAX_RADIUS_CHUNKS} Chunks. Keine Ueberlappung
 *       zwischen Bezirken, keine Abhaengigkeit von Nachbarchunks -> threadsicher, in jedem Generator-Thread gleich.</li>
 *   <li>{@link CityGenerator}: Haken im Chunk-Generator ({@code ChunkProviderSporos.getChunkPrimer}) nach Gelaende
 *       und Hoehlen, vor der Vanilla-Dekoration. Standard {@link CityGenerator#NONE} (kein Eingriff);
 *       {@code WorldProviderSporos.init} setzt {@link SporosCityGenerator#INSTANCE}.</li>
 *   <li>{@link #STREET_PITCH}/{@link #STREET_WIDTH}: Strassenraster je Stadt (an der Stadtmitte ausgerichtet),
 *       {@link #isStreet}/{@link #lotOf} als gemeinsame Rasterrechnung fuer Strassen und Grundstuecke.</li>
 * </ul>
 * Biome: Im Stadtkreis gibt es nur die Stadtbiome ({@link #isCityBiome}) sowie Sumpf und Fluss als geflutete
 * Zellen - {@code GenLayerSporosCity} ersetzt dort See/Kueste durch Sumpf und Hochland/Pilzhain/Sporenfeld durch die
 * Ebene, damit Gelaende und Bewuchs zur Stadt passen (das Biom-Tag {@code space_m:sporos_city_biomes} nennt dieselben
 * zwei Biome fuer Datapacks).
 */
public final class SporosCities {

    private SporosCities() {}

    /** Bezirksgroesse in Chunks (48 Chunks = 768 Bloecke). */
    public static final int DISTRICT_CHUNKS = 48;
    /** Anteil der Bezirke mit Stadt. */
    public static final float CITY_CHANCE = 0.45F;
    public static final int MIN_RADIUS_CHUNKS = 6;
    public static final int MAX_RADIUS_CHUNKS = 14;
    /** Abstand der Strassenmitten in Bloecken (ein Strassenblock = 32 x 32, davon 8 Strasse). */
    public static final int STREET_PITCH = 32;
    public static final int STREET_WIDTH = 8;

    private static final long SALT = 0x5F0F_05C1_7E5L;

    /** Eine geplante Stadt (Chunk-Koordinaten der Mitte, Radius in Chunks, eigener Seed fuer Ruinen). */
    public record City(int centerChunkX, int centerChunkZ, int radiusChunks, long seed) {

        public boolean containsChunk(int chunkX, int chunkZ) {
            int dx = chunkX - centerChunkX;
            int dz = chunkZ - centerChunkZ;
            return dx * dx + dz * dz <= radiusChunks * radiusChunks;
        }

        /** Mittelpunkt in Blockkoordinaten (Strassenraster-Ursprung). */
        public int originX() {
            return centerChunkX * 16 + 8;
        }

        public int originZ() {
            return centerChunkZ * 16 + 8;
        }

        /** 0 = Stadtmitte, 1 = Stadtrand (fuer Verfall/Bewuchs nach aussen). */
        public double edgeFactor(int blockX, int blockZ) {
            double dx = blockX - originX();
            double dz = blockZ - originZ();
            return Math.min(1.0D, Math.sqrt(dx * dx + dz * dz) / (radiusChunks * 16.0D));
        }
    }

    /**
     * Haken fuer den Staedtegenerator. Arbeitet auf dem Chunk-Puffer des Generators: Index
     * {@code (x * 16 + z) * 256 + y} (x/z 0..15 im Chunk, y 0..255), {@code biomes[z * 16 + x]}.
     */
    @FunctionalInterface
    public interface CityGenerator {

        CityGenerator NONE = (world, city, chunkX, chunkZ, blocks, biomes) -> { };

        /**
         * @param city  Stadt dieses Chunks ({@link #cityAt}), nie null
         * @param biomes Spaltenbiome ({@code biomesForGeneration} nach {@code loadBlockGeneratorData})
         */
        void generate(CelestialWorld world, City city, int chunkX, int chunkZ, BlockState[] blocks, BiomeGenBaseCelestial[] biomes);
    }

    private static volatile CityGenerator generator = CityGenerator.NONE;

    public static CityGenerator generator() {
        return generator;
    }

    /** Runde 3: echten Generator setzen; null = wieder {@link CityGenerator#NONE}. */
    public static void setGenerator(@Nullable CityGenerator gen) {
        generator = gen != null ? gen : CityGenerator.NONE;
    }

    /** Aufruf aus dem Chunk-Generator: Stadt des Chunks suchen und den Generator laufen lassen. */
    public static void generateChunk(CelestialWorld world, int chunkX, int chunkZ, BlockState[] blocks, BiomeGenBaseCelestial[] biomes) {
        CityGenerator gen = generator;
        if (gen == CityGenerator.NONE) return;
        City city = cityAt(world.getSeed(), chunkX, chunkZ);
        if (city != null) gen.generate(world, city, chunkX, chunkZ, blocks, biomes);
    }

    /** Stadt, zu der dieser Chunk gehoert, oder null. */
    @Nullable
    public static City cityAt(long worldSeed, int chunkX, int chunkZ) {
        City city = cityOfDistrict(worldSeed, Math.floorDiv(chunkX, DISTRICT_CHUNKS), Math.floorDiv(chunkZ, DISTRICT_CHUNKS));
        return city != null && city.containsChunk(chunkX, chunkZ) ? city : null;
    }

    /** Geplante Stadt eines Bezirks (unabhaengig davon, ob ein Chunk darin liegt), oder null. */
    @Nullable
    public static City cityOfDistrict(long worldSeed, int districtX, int districtZ) {
        long seed = mix(worldSeed ^ SALT, districtX, districtZ);
        java.util.Random rand = new java.util.Random(seed);
        if (rand.nextFloat() >= CITY_CHANCE) return null;

        int radius = MIN_RADIUS_CHUNKS + rand.nextInt(MAX_RADIUS_CHUNKS - MIN_RADIUS_CHUNKS + 1);
        int margin = radius + 1;
        int span = DISTRICT_CHUNKS - 2 * margin;
        int cx = districtX * DISTRICT_CHUNKS + margin + (span > 0 ? rand.nextInt(span) : 0);
        int cz = districtZ * DISTRICT_CHUNKS + margin + (span > 0 ? rand.nextInt(span) : 0);
        return new City(cx, cz, radius, rand.nextLong());
    }

    /** Liegt der Block auf einer Strasse des Rasters der Stadt? */
    public static boolean isStreet(City city, int blockX, int blockZ) {
        int lx = Math.floorMod(blockX - city.originX() + STREET_WIDTH / 2, STREET_PITCH);
        int lz = Math.floorMod(blockZ - city.originZ() + STREET_WIDTH / 2, STREET_PITCH);
        return lx < STREET_WIDTH || lz < STREET_WIDTH;
    }

    /** Grundstueck (Rasterzelle) des Blocks relativ zur Stadtmitte, z.B. fuer Gebaeudeauswahl je Zelle. */
    public static long lotOf(City city, int blockX, int blockZ) {
        int gx = Math.floorDiv(blockX - city.originX() + STREET_WIDTH / 2, STREET_PITCH);
        int gz = Math.floorDiv(blockZ - city.originZ() + STREET_WIDTH / 2, STREET_PITCH);
        return mix(city.seed(), gx, gz);
    }

    /** Stadtbiome (Sumpf/Fluss kommen in Staedten nur als geflutete Zellen vor, s. {@code GenLayerSporosCity}). */
    public static boolean isCityBiome(BiomeGenBaseCelestial biome) {
        return biome == BiomeGenBaseSporos.overgrownPlains || biome == BiomeGenBaseSporos.mossForest;
    }

    private static long mix(long seed, long a, long b) {
        long h = seed;
        h ^= a * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27) * 0xBF58476D1CE4E5B9L;
        h ^= b * 0x94D049BB133111EBL;
        h = Long.rotateLeft(h, 31) * 0x9E3779B97F4A7C15L;
        return h ^ (h >>> 29);
    }
}
