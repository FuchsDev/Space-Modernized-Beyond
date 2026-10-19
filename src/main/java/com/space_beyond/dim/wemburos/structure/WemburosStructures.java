package com.space_beyond.dim.wemburos.structure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.space_m.SpaceMod;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialSeed;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.structure.Frame;
import com.space_beyond.dim.sporos.structure.StructureMobs;
import com.space_beyond.dim.wemburos.WemburosBlocks;
import com.space_beyond.dim.wemburos.biome.BiomeGenBaseWemburos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Strukturen des Planeten Wemburos (eigene Erweiterung, Nutzerwunsch: drei Bauten im Stil eines Lila-Schwarz-Gold-SMP,
 * passend zum Planeten). Platzierung wie die Sporos-Wildnis: Bezirke von {@link #DISTRICT_CHUNKS} Chunks, je Bezirk mit
 * {@link #CHANCE} eine Struktur nach Biom und Gewicht, gedreht um 0-270 Grad, Grundhoehe per Sonden-Chunk, Gelaende im
 * Grundriss eingeebnet. Malen im Chunk-Puffer ({@link #generateChunk}), Loot/Spawner/Bewohner in der Dekorationsphase
 * ({@link #decorate}).
 * <ul>
 *   <li><b>Zitadelle des Souveraens</b> ({@link Type#THRONE}, Boss-Dungeon, 177 x 177 Bloecke, {@link SovereignCitadel}):
 *       Prozessionsstrasse mit Waechterstatuen, Schlucht mit eingestuerzter Bruecke und Kettentrittsteinen, Torburg
 *       (Weg durch die Wachstube), Vorhof mit Kaserne, Bibliothek, Graeberfeld und Schmiede, Seelenfeuergraben,
 *       Mausoleum mit Treppe in die Katakomben, Glutkaverne mit Saeulen-Parkour, Wendeltreppe hinauf in den
 *       Kuppel-Thronsaal. Darueber schweben Kronkristall und angekettete Inseln. Boss {@code unstable_sovereign}.
 *       Loot {@code throne}, {@code vault}, {@code portal}.</li>
 *   <li><b>Goldtresor</b> ({@link Type#VAULT}): Onyx-Bunker mit Goldrahmen, Treppenstollen zu einer unterirdischen
 *       Schatzkammer (Goldboden, Goldpyramide, Truhenreihen, Kristalllicht), Skelett-Spawner und Waechter.
 *       Loot {@code vault}.</li>
 *   <li><b>Instabiles Portal</b> ({@link Type#PORTAL}): Krater mit zerbrochenem Portalrahmen aus Obsidian und
 *       weinendem Obsidian (Goldecken), flackernde Fuellung aus Glas und Kristall, schwebende Splitter und zwei
 *       schwebende Onyxinseln, Endermen und Endermiten. Loot {@code portal}.</li>
 * </ul>
 */
public final class WemburosStructures {

    private WemburosStructures() {}

    public static final int DISTRICT_CHUNKS = 30;
    public static final float CHANCE = 0.55F;
    public static final int BLEND = 8;
    private static final long SALT = 0x3E3B_0705_0C1L;

    public enum Type {
        THRONE("throne", SovereignCitadel.R, 3),
        VAULT("vault", 13, 3),
        PORTAL("portal", 15, 3);

        public final String table;
        public final int radius;
        public final int weight;

        Type(String table, int radius, int weight) {
            this.table = table;
            this.radius = radius;
            this.weight = weight;
        }

        public boolean fits(BiomeGenBaseCelestial b) {
            return switch (this) {
                case THRONE -> b == BiomeGenBaseWemburos.violetPlains || b == BiomeGenBaseWemburos.crystalFields || b == BiomeGenBaseWemburos.onyxBadlands
                        || b == BiomeGenBaseWemburos.goldenDunes || b == BiomeGenBaseWemburos.chorusWastes;
                case VAULT -> b != BiomeGenBaseWemburos.rift && b != BiomeGenBaseWemburos.onyxSpires;
                case PORTAL -> b == BiomeGenBaseWemburos.rift || b == BiomeGenBaseWemburos.crystalFields || b == BiomeGenBaseWemburos.violetPlains
                        || b == BiomeGenBaseWemburos.chorusWastes;
            };
        }

        @Nullable
        public static Type byName(String name) {
            for (Type t : values()) if (t.name().equalsIgnoreCase(name) || t.table.equalsIgnoreCase(name)) return t;
            return null;
        }
    }

    /** Geplante Struktur: Mittelpunkt, Drehung 0-3, Seed. Die Drehung wird ueber {@link Frame} gerechnet. */
    public record Structure(Type type, int x, int z, int rotation, long seed) {
        public int reach() {
            return type.radius + BLEND;
        }

        public boolean touchesChunk(int cx, int cz) {
            int r = reach();
            return cx * 16 + 15 >= x - r && cx * 16 <= x + r && cz * 16 + 15 >= z - r && cz * 16 <= z + r;
        }

        public int distance(int bx, int bz) {
            return Math.max(Math.abs(bx - x), Math.abs(bz - z));
        }

        int wx(int u, int v) {
            return x + switch (rotation) {
                case 1 -> -v;
                case 2 -> -u;
                case 3 -> v;
                default -> u;
            };
        }

        int wz(int u, int v) {
            return z + switch (rotation) {
                case 1 -> u;
                case 2 -> -v;
                case 3 -> -u;
                default -> v;
            };
        }
    }

    @Nullable
    public static Structure structureAt(CelestialWorld world, int cx, int cz) {
        Structure s = ofDistrict(world, Math.floorDiv(cx, DISTRICT_CHUNKS), Math.floorDiv(cz, DISTRICT_CHUNKS));
        return s != null && s.touchesChunk(cx, cz) ? s : null;
    }

    @Nullable
    public static Structure ofDistrict(CelestialWorld world, int dx, int dz) {
        long seed = CityRandom.mix(world.getSeed() ^ SALT, dx, dz);
        Random rand = new Random(seed);
        if (rand.nextFloat() >= CHANCE) return null;
        int margin = (Type.THRONE.radius + BLEND) / 16 + 2;
        int span = DISTRICT_CHUNKS - 2 * margin;
        int x = (dx * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
        int z = (dz * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
        int rot = rand.nextInt(4);
        long sSeed = rand.nextLong();
        BiomeGenBaseCelestial biome = world.getBiomeGenForCoords(x, z);
        int total = 0;
        for (Type t : Type.values()) if (t.fits(biome)) total += t.weight;
        if (total == 0) return null;
        int pick = rand.nextInt(total);
        for (Type t : Type.values()) {
            if (!t.fits(biome)) continue;
            pick -= t.weight;
            if (pick < 0) return new Structure(t, x, z, rot, sSeed);
        }
        return null;
    }

    @Nullable
    public static Structure nearest(CelestialWorld world, @Nullable Type type, int x, int z, int range) {
        int dx0 = Math.floorDiv(x >> 4, DISTRICT_CHUNKS), dz0 = Math.floorDiv(z >> 4, DISTRICT_CHUNKS);
        Structure best = null;
        double bestD = Double.MAX_VALUE;
        for (int dx = dx0 - range; dx <= dx0 + range; dx++) for (int dz = dz0 - range; dz <= dz0 + range; dz++) {
            Structure s = ofDistrict(world, dx, dz);
            if (s == null || (type != null && s.type() != type)) continue;
            double d = Math.hypot(s.x() - x, s.z() - z);
            if (d < bestD) {
                bestD = d;
                best = s;
            }
        }
        return best;
    }

    // ── Planung ─────────────────────────────────────────────────────────────

    public record Loot(int u, int y, int v, Direction facing, boolean barrel, String table) { }

    public record MobSpot(int u, int y, int v, int kind) { }

    /** Grundhoehe, Beutestellen, Spawner (Art nach {@link StructureMobs}) und Bewohner einer Struktur. */
    public static final class Plan {
        public final Structure s;
        public final int base;
        public final List<Loot> loot = new ArrayList<>();
        public final List<MobSpot> spawners = new ArrayList<>();
        public final List<MobSpot> mobs = new ArrayList<>();

        Plan(Structure s, int base) {
            this.s = s;
            this.base = base;
            String t = s.type().table;
            switch (s.type()) {
                case THRONE -> {
                    int kv = SovereignCitadel.KV;
                    // Thronsaal: Boss vor der Throntreppe (wacht auf, sobald ein Spieler aus der Wendeltreppe steigt),
                    // Schatz auf der Plattform neben dem Thron
                    mobs.add(new MobSpot(0, SovereignCitadel.FLOOR + 1, kv - 4, StructureMobs.UNSTABLE_SOVEREIGN));
                    int dais = SovereignCitadel.DAIS + 1;
                    loot.add(new Loot(-4, dais, kv - 17, Direction.SOUTH, false, t));
                    loot.add(new Loot(4, dais, kv - 17, Direction.SOUTH, false, t));
                    loot.add(new Loot(-9, dais, kv - 12, Direction.EAST, false, "vault"));
                    loot.add(new Loot(9, dais, kv - 12, Direction.WEST, false, "vault"));
                    // Prozessionsstrasse und Vorhof
                    mobs.add(new MobSpot(6, 1, 80, StructureMobs.ENDERMAN));
                    mobs.add(new MobSpot(-5, 1, 76, StructureMobs.ENDERMAN));
                    loot.add(new Loot(-9, 1, 16, Direction.UP, true, "vault"));
                    loot.add(new Loot(9, 1, 30, Direction.UP, true, "vault"));
                    for (int[] m : new int[][] {{0, 30}, {-20, 14}, {20, 32}, {30, -2}}) mobs.add(new MobSpot(m[0], 1, m[1], StructureMobs.WITHER_SKELETON));
                    mobs.add(new MobSpot(-50, 1, -50, StructureMobs.ENDERMAN));
                    mobs.add(new MobSpot(50, 1, -4, StructureMobs.ENDERMAN));
                    // Wachstube im westlichen Torturm, Truhe im zweiten Obergeschoss
                    spawners.add(new MobSpot(-16, 1, 46, StructureMobs.SKELETON));
                    loot.add(new Loot(-11, 23, 51, Direction.WEST, false, "vault"));
                    // Kaserne, Bibliothek, Gruft auf dem Graeberfeld, Schmiede
                    spawners.add(new MobSpot(-44, 1, 20, StructureMobs.WITHER_SKELETON));
                    loot.add(new Loot(-57, 1, 19, Direction.UP, true, "vault"));
                    loot.add(new Loot(-57, 1, 31, Direction.UP, true, "vault"));
                    mobs.add(new MobSpot(-45, 1, 16, StructureMobs.WITHER_SKELETON));
                    mobs.add(new MobSpot(-53, 1, 30, StructureMobs.WITHER_SKELETON));
                    loot.add(new Loot(53, 2, 35, Direction.NORTH, false, "vault"));
                    loot.add(new Loot(55, 2, 35, Direction.NORTH, false, t));
                    loot.add(new Loot(-53, 1, -34, Direction.UP, true, "vault"));
                    loot.add(new Loot(56, 1, -49, Direction.UP, true, "vault"));
                    // Katakomben
                    int fy = SovereignCitadel.WALK + 1;
                    spawners.add(new MobSpot(48, fy, -50, StructureMobs.SKELETON));
                    spawners.add(new MobSpot(54, fy, -56, StructureMobs.WITHER_SKELETON));
                    loot.add(new Loot(44, fy, -62, Direction.EAST, false, "vault"));
                    loot.add(new Loot(58, fy, -36, Direction.WEST, false, t));
                    mobs.add(new MobSpot(45, fy, -40, StructureMobs.SKELETON));
                    mobs.add(new MobSpot(57, fy, -52, StructureMobs.SKELETON));
                    mobs.add(new MobSpot(50, fy, -60, StructureMobs.ENDERMITE));
                    mobs.add(new MobSpot(44, fy, -45, StructureMobs.ENDERMITE));
                    // Glutkaverne: Blaze-Spawner auf der Mittelsaeule, Blazes ueber dem Pfad, Truhe am Einstieg
                    spawners.add(new MobSpot(0, -32, kv, StructureMobs.BLAZE));
                    int[][] path = SovereignCitadel.cavernPath();
                    for (int i = 4; i < path.length; i += 4) mobs.add(new MobSpot(path[i][0], path[i][2] + 3, path[i][1], StructureMobs.BLAZE));
                    loot.add(new Loot(8, fy, -51, Direction.SOUTH, false, "vault"));
                    // Schreine auf den schwebenden Inseln
                    for (int[] is : SovereignCitadel.ISLANDS) {
                        if (is[4] == 1) loot.add(new Loot(is[0], is[1] + 1, is[2], Direction.SOUTH, false, "portal"));
                    }
                }
                case VAULT -> {
                    for (int side : new int[] {-7, 7}) {
                        for (int v : new int[] {-9, -6, -3, 0}) loot.add(new Loot(side, -9, v, side < 0 ? Direction.EAST : Direction.WEST, false, t));
                        loot.add(new Loot(side, -9, -8, Direction.UP, true, t));
                    }
                    spawners.add(new MobSpot(0, -9, -10, StructureMobs.SKELETON));
                    mobs.add(new MobSpot(-4, -9, -1, StructureMobs.SKELETON));
                    mobs.add(new MobSpot(4, -9, -1, StructureMobs.SKELETON));
                    mobs.add(new MobSpot(3, -9, -9, StructureMobs.WITHER_SKELETON));
                }
                case PORTAL -> {
                    loot.add(new Loot(2, craterFloor(2, 2) + 1, 2, Direction.NORTH, false, t));
                    loot.add(new Loot(8, 11, 5, Direction.WEST, false, t));
                    for (int[] m : new int[][] {{5, 4}, {-5, -4}, {-6, 3}}) mobs.add(new MobSpot(m[0], craterFloor(m[0], m[1]) + 1, m[1], StructureMobs.ENDERMAN));
                    mobs.add(new MobSpot(0, craterFloor(0, 3) + 1, 3, StructureMobs.ENDERMITE));
                    mobs.add(new MobSpot(-2, craterFloor(-2, -3) + 1, -3, StructureMobs.ENDERMITE));
                }
            }
        }
    }

    /** Bodenhoehe des Portalkraters (relativ, 0 bis -4). */
    static int craterFloor(int u, int v) {
        double d = Math.sqrt(u * u + v * v);
        if (d > 12) return 0;
        return -(int) Math.round(4 * (1 - (d / 12) * (d / 12)));
    }

    private static final ThreadLocal<Boolean> PROBING = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<ChunkProviderCelestial> PROBE = new ThreadLocal<>();
    private static final ThreadLocal<Long> PROBE_SEED = new ThreadLocal<>();
    private static final ThreadLocal<Map<Structure, Plan>> PLANS = ThreadLocal.withInitial(() -> new LinkedHashMap<>(8, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Structure, Plan> eldest) {
            return size() > 6;
        }
    });

    public static Plan plan(CelestialWorld world, Structure s) {
        Map<Structure, Plan> plans = PLANS.get();
        Plan p = plans.get(s);
        if (p == null) {
            p = new Plan(s, baseY(world, s));
            plans.put(s, p);
        }
        return p;
    }

    /** Natuerliche Oberflaeche am Mittelpunkt (Median des Mittel-Chunks aus einem Sonden-Generator ohne Strukturen). */
    static int baseY(CelestialWorld world, Structure s) {
        ChunkProviderCelestial probe = PROBE.get();
        if (probe == null || PROBE_SEED.get() == null || PROBE_SEED.get() != world.getSeed()) {
            probe = CelestialGenerators.require(WemburosBlocks.BODY).chunkProvider().apply(world, world.getSeed());
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
        int[] h = new int[256];
        int n = 0;
        for (int i = 0; i < 256; i++) {
            for (int y = 254; y > 0; y--) {
                BlockState b = blocks[i * 256 + y];
                if (b != null && !b.isAir() && b.getFluidState().isEmpty() && !(b.getBlock() instanceof AmethystClusterBlock)) {
                    h[n++] = y;
                    break;
                }
            }
        }
        if (n == 0) return 70;
        java.util.Arrays.sort(h, 0, n);
        // Zitadelle: Platz fuer die Kaverne darunter und den Kronkristall darueber
        return s.type() == Type.THRONE ? Math.max(56, Math.min(128, h[n / 2])) : Math.max(20, Math.min(200, h[n / 2]));
    }

    // ── Erzeugung ───────────────────────────────────────────────────────────

    public static void generateChunk(CelestialWorld world, int cx, int cz, BlockState[] blocks) {
        if (PROBING.get()) return;
        Structure s = structureAt(world, cx, cz);
        if (s == null) return;
        Plan plan = plan(world, s);
        CityCanvas c = new CityCanvas(blocks, cx, cz);
        terrain(c, plan);
        W w = new W(c, plan);
        switch (s.type()) {
            case THRONE -> SovereignCitadel.build(w);
            case VAULT -> vault(w);
            case PORTAL -> portal(w);
        }
        for (Loot l : plan.loot) {
            BlockState below = w.get(l.u(), l.y() - 1, l.v());
            if (below == null || below.isAir()) continue;
            w.set(l.u(), l.y(), l.v(), l.barrel() ? CityPalette.facingAll(Blocks.BARREL.defaultBlockState(), Direction.UP)
                    : CityPalette.facing(Blocks.CHEST.defaultBlockState(), l.facing()));
        }
        for (MobSpot sp : plan.spawners) w.set(sp.u(), sp.y(), sp.v(), Blocks.SPAWNER.defaultBlockState());
    }

    /** Gelaende im Grundriss auf die Grundhoehe, Uebergang {@link #BLEND} zur Natur (Regolith oben, Stein darunter). */
    static void terrain(CityCanvas c, Plan p) {
        Structure s = p.s;
        int r = s.type().radius;
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState top = WemburosBlocks.WEMBUROS_REGOLITH.get().defaultBlockState();
        BlockState stone = WemburosBlocks.WEMBUROS_STONE.get().defaultBlockState();
        for (int x = c.minX; x <= c.maxX(); x++) {
            for (int z = c.minZ; z <= c.maxZ(); z++) {
                int d = s.distance(x, z);
                if (d > r + BLEND) continue;
                double jitter = (CityRandom.noise(s.seed() ^ 0x7E45L, x, z, 6) - 0.5) * 3.0;
                double w = CityRandom.smoothstep(r, r + BLEND, d + jitter);
                if (w >= 0.995) continue;
                int nat = 0;
                for (int y = CityCanvas.HEIGHT - 1; y > 0; y--) {
                    BlockState b = c.get(x, y, z);
                    if (b != null && !b.isAir() && !(b.getBlock() instanceof AmethystClusterBlock)) {
                        nat = y;
                        break;
                    }
                }
                int target = (int) Math.round(p.base + (nat - p.base) * w);
                for (int y = CityCanvas.HEIGHT - 1; y > target; y--) {
                    BlockState b = c.get(x, y, z);
                    if (b != null && !b.isAir()) c.set(x, y, z, air);
                }
                for (int y = target; y > 1 && (y > nat || y > target - (w < 0.5 ? 6 : 2)); y--) {
                    BlockState b = c.get(x, y, z);
                    boolean open = b == null || b.isAir();
                    if (y == target) {
                        if (w < 0.7 || open) c.set(x, y, z, top);
                    } else if (open || y > nat) {
                        c.set(x, y, z, target - y <= 3 ? top : stone);
                    }
                }
            }
        }
    }

    /** Zeichenrahmen: lokale (u, y, v) relativ zu Mittelpunkt und Grundhoehe, gedreht. */
    static final class W implements SovereignCitadel.Pen {
        final CityCanvas c;
        final Structure s;
        final int base;
        final Rotation rot;
        /** Ausschnitt des Chunks in lokalen Koordinaten. */
        private final int uMin, uMax, vMin, vMax;

        W(CityCanvas c, Plan p) {
            this.c = c;
            this.s = p.s;
            this.base = p.base;
            this.rot = Frame.rotation(p.s.rotation());
            int[] a = local(c.minX, c.minZ), b = local(c.maxX(), c.maxZ());
            uMin = Math.min(a[0], b[0]);
            uMax = Math.max(a[0], b[0]);
            vMin = Math.min(a[1], b[1]);
            vMax = Math.max(a[1], b[1]);
        }

        /** Welt (x, z) -> lokal (u, v), Umkehrung von {@link Structure#wx}/{@link Structure#wz}. */
        private int[] local(int x, int z) {
            int dx = x - s.x(), dz = z - s.z();
            return switch (s.rotation()) {
                case 1 -> new int[] {dz, -dx};
                case 2 -> new int[] {-dx, -dz};
                case 3 -> new int[] {-dz, dx};
                default -> new int[] {dx, dz};
            };
        }

        @Override
        public int uMin() {
            return uMin;
        }

        @Override
        public int uMax() {
            return uMax;
        }

        @Override
        public int vMin() {
            return vMin;
        }

        @Override
        public int vMax() {
            return vMax;
        }

        @Override
        public void set(int u, int y, int v, BlockState st) {
            c.set(s.wx(u, v), base + y, s.wz(u, v), rot == Rotation.NONE ? st : st.rotate(rot));
        }

        void setIfAir(int u, int y, int v, BlockState st) {
            if (c.isAir(s.wx(u, v), base + y, s.wz(u, v))) set(u, y, v, st);
        }

        void air(int u, int y, int v) {
            c.set(s.wx(u, v), base + y, s.wz(u, v), Blocks.AIR.defaultBlockState());
        }

        @Nullable
        @Override
        public BlockState get(int u, int y, int v) {
            return c.get(s.wx(u, v), base + y, s.wz(u, v));
        }

        void fill(int u0, int y0, int v0, int u1, int y1, int v1, BlockState st) {
            for (int u = Math.min(u0, u1); u <= Math.max(u0, u1); u++)
                for (int v = Math.min(v0, v1); v <= Math.max(v0, v1); v++)
                    for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) set(u, y, v, st);
        }

        @Override
        public double unit(long salt, int u, int y, int v) {
            return CityRandom.unit(s.seed() ^ salt, u, y, v);
        }
    }

    // ── Bloecke ─────────────────────────────────────────────────────────────

    private static BlockState b(Block block) {
        return block.defaultBlockState();
    }

    private static BlockState polOnyx() {
        return b(WemburosBlocks.POLISHED_WEMBUROS_ONYX.get());
    }

    private static BlockState onyx() {
        return b(WemburosBlocks.WEMBUROS_ONYX.get());
    }

    private static BlockState bricks() {
        return b(WemburosBlocks.WEMBUROS_BRICKS.get());
    }

    private static BlockState gilded() {
        return b(WemburosBlocks.GILDED_WEMBUROS_BRICKS.get());
    }

    private static BlockState crystal() {
        return b(WemburosBlocks.UNSTABLE_CRYSTAL.get());
    }

    private static BlockState cluster(Direction d) {
        return b(WemburosBlocks.UNSTABLE_CRYSTAL_CLUSTER.get()).setValue(AmethystClusterBlock.FACING, d);
    }

    private static BlockState gold() {
        return b(Blocks.GOLD_BLOCK);
    }

    // ── Zitadelle des Souveraens (Boss-Dungeon): siehe SovereignCitadel ─────

    // ── Goldtresor ──────────────────────────────────────────────────────────

    static void vault(W w) {
        BlockState air = Blocks.AIR.defaultBlockState();
        // Bunker an der Oberflaeche (u -3..3, v 6..12)
        w.fill(-3, 0, 6, 3, 0, 12, polOnyx());
        for (int u = -3; u <= 3; u++) for (int v = 6; v <= 12; v++) for (int y = 1; y <= 4; y++) {
            boolean edge = Math.abs(u) == 3 || v == 6 || v == 12;
            w.set(u, y, v, edge ? (y == 2 && (u == 0 || v == 9) && !(v == 12) ? bricks() : polOnyx()) : air);
        }
        w.fill(-4, 5, 5, 4, 5, 13, gilded());
        for (int u : new int[] {-4, 4}) for (int v : new int[] {5, 13}) w.set(u, 5, v, gold());
        w.set(0, 6, 9, crystal());
        w.air(0, 1, 12);
        w.air(0, 2, 12);
        for (int y = 1; y <= 3; y++) {
            w.set(-1, y, 12, gold());
            w.set(1, y, 12, gold());
        }
        w.set(0, 3, 12, gold());
        w.set(0, 4, 8, crystal());
        // Treppenstollen nach Norden hinab (Stufen v 10..2, y -1..-9)
        for (int i = 1; i <= 9; i++) {
            int vv = 11 - i, yy = -i;
            for (int u = -1; u <= 1; u++) {
                w.set(u, yy, vv, CityPalette.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.SOUTH, false));
                w.set(u, yy - 1, vv, polOnyx());
                for (int y = yy + 1; y <= yy + 4; y++) if (y <= 0 || vv < 7 || vv > 11) w.air(u, y, vv);
                if (yy + 5 <= 0) w.set(u, yy + 5, vv, polOnyx());
            }
            for (int y = yy - 1; y <= Math.min(0, yy + 4); y++) {
                w.set(-2, y, vv, (i % 3 == 0 && y == yy + 3) ? crystal() : polOnyx());
                w.set(2, y, vv, (i % 3 == 0 && y == yy + 3) ? crystal() : polOnyx());
            }
        }
        // Schatzkammer: Kasten, dann Innenraum (u -7..7, v -10..1, y -9..-5)
        w.fill(-8, -10, -11, 8, -4, 2, polOnyx());
        w.fill(-7, -9, -10, 7, -5, 1, air);
        for (int u = -8; u <= 8; u++) for (int v = -11; v <= 2; v++) {
            boolean wall = Math.abs(u) == 8 || v == -11 || v == 2;
            if (wall) w.set(u, -7, v, bricks());
            if (Math.abs(u) <= 7 && v >= -10 && v <= 1) {
                BlockState floor = Math.max(Math.abs(u), Math.abs(v + 5)) == 4 ? gold() : (u + v) % 2 == 0 ? gilded() : polOnyx();
                w.set(u, -10, v, floor);
                if (Math.floorMod(u, 4) == 0 && Math.floorMod(v + 5, 4) == 0) w.set(u, -4, v, crystal());
            }
        }
        // Durchgang vom Stollen mit Goldrahmen (letzte Stufe liegt in der Wand)
        for (int u = -1; u <= 1; u++) {
            w.set(u, -9, 2, CityPalette.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.SOUTH, false));
            for (int y = -8; y <= -6; y++) w.air(u, y, 2);
        }
        for (int y = -9; y <= -5; y++) {
            w.set(-2, y, 2, gold());
            w.set(2, y, 2, gold());
        }
        for (int u = -1; u <= 1; u++) w.set(u, -5, 2, gold());
        // Schatzpodest mit Goldpyramide
        w.fill(-2, -9, -7, 2, -9, -3, gilded());
        w.fill(-1, -8, -6, 1, -8, -4, gold());
        w.set(0, -7, -5, b(Blocks.RAW_GOLD_BLOCK));
        w.set(0, -6, -5, cluster(Direction.UP));
        // Goldlager in den Ecken
        for (int s = -1; s <= 1; s += 2) {
            w.set(s * 7, -9, -10, gold());
            w.set(s * 7, -8, -10, b(Blocks.RAW_GOLD_BLOCK));
            w.set(s * 6, -9, -10, b(Blocks.RAW_GOLD_BLOCK));
            w.set(s * 7, -9, 1, gold());
        }
    }

    // ── Instabiles Portal ───────────────────────────────────────────────────

    static void portal(W w) {
        BlockState obsidian = b(Blocks.OBSIDIAN), crying = b(Blocks.CRYING_OBSIDIAN), glass = b(Blocks.PURPLE_STAINED_GLASS);
        BlockState regolith = b(WemburosBlocks.WEMBUROS_REGOLITH.get());
        // Krater
        for (int u = -15; u <= 15; u++) for (int v = -15; v <= 15; v++) {
            double d = Math.sqrt(u * u + v * v);
            if (d <= 12) {
                int fy = craterFloor(u, v);
                double h = w.unit(0xC2A7L, u, 0, v);
                BlockState floor = h < 0.06 ? crying : h < 0.18 ? obsidian : d > 10 && h > 0.5 ? regolith : onyx();
                w.set(u, fy - 1, v, onyx());
                w.set(u, fy, v, floor);
                for (int y = fy + 1; y <= 16; y++) w.air(u, y, v);
                if (d > 4 && w.unit(0xC2A8L, u, 1, v) < 0.07) w.set(u, fy + 1, v, cluster(Direction.UP));
            } else if (d <= 14) {
                w.set(u, 0, v, regolith);
                if (w.unit(0xC2A9L, u, 1, v) < 0.4) w.set(u, 1, v, onyx());
            }
        }
        // Zerbrochener Portalrahmen (v = 0, u -3..3, y -4..5), Ecken aus Gold, oben rechts eingestuerzt
        for (int u = -3; u <= 3; u++) for (int y = -4; y <= 5; y++) {
            boolean frame = Math.abs(u) == 3 || y == -4 || y == 5;
            boolean broken = u >= 2 && y >= 2;
            if (frame) {
                if (broken) {
                    w.air(u, y, 0);
                    continue;
                }
                boolean corner = Math.abs(u) == 3 && (y == -4 || y == 5);
                w.set(u, y, 0, corner ? gold() : w.unit(0xF4A1L, u, y, 0) < 0.3 ? crying : obsidian);
            } else {
                double h = w.unit(0xF4A2L, u, y, 0);
                if (h < 0.35) w.set(u, y, 0, glass);
                else if (h < 0.45) w.set(u, y, 0, crystal());
                else w.air(u, y, 0);
            }
        }
        // Truemmer des eingestuerzten Stuecks
        w.set(4, craterFloor(4, 2) + 1, 2, obsidian);
        w.set(5, craterFloor(5, 1) + 1, 1, obsidian);
        w.set(4, craterFloor(4, 2) + 2, 2, crying);
        w.set(5, craterFloor(5, 3) + 1, 3, gold());
        // Schwebende Splitter um das Portal
        for (int k = 0; k < 14; k++) {
            double a = k * Math.PI * 2 / 14;
            int rr = 4 + (k % 4);
            int u = (int) Math.round(Math.cos(a) * rr), v = (int) Math.round(Math.sin(a) * rr);
            int y = 3 + (k * 5) % 7;
            BlockState s = switch (k % 4) {
                case 0 -> crystal();
                case 1 -> glass;
                case 2 -> obsidian;
                default -> gold();
            };
            w.set(u, y, v, s);
        }
        // Zwei schwebende Onyxinseln mit Goldadern und haengenden Kristallen
        island(w, 8, 9, 5);
        island(w, -7, 10, -6);
    }

    private static void island(W w, int cu, int cy, int cv) {
        BlockState gildedOnyx = b(WemburosBlocks.GILDED_ONYX.get());
        for (int du = -2; du <= 2; du++) for (int dv = -2; dv <= 2; dv++) for (int dy = -2; dy <= 1; dy++) {
            double d = du * du + dv * dv + dy * dy * 1.8;
            if (d > 5.5) continue;
            w.set(cu + du, cy + dy, cv + dv, dy == 1 && w.unit(0x15L, du, dy, dv) < 0.4 ? gildedOnyx : onyx());
        }
        for (int du = -1; du <= 1; du++) for (int dv = -1; dv <= 1; dv++) {
            if (w.unit(0x16L, du, 0, dv) < 0.6) w.setIfAir(cu + du, cy - 3, cv + dv, cluster(Direction.DOWN));
        }
    }

    // ── Dekorationsphase ────────────────────────────────────────────────────

    public static ResourceLocation tableId(String name) {
        return com.space_beyond.BeyondMod.id("chests/wemburos/" + name);
    }

    public static void decorate(WorldGenLevel level, ChunkAccess chunk) {
        int cx = chunk.getPos().x, cz = chunk.getPos().z;
        CelestialWorld world = CelestialGenerators.state(WemburosBlocks.BODY).chunkManager;
        Structure s = structureAt(world, cx, cz);
        if (s == null) return;
        Plan plan = plan(world, s);
        long seed = CelestialSeed.get();
        int minX = cx << 4, minZ = cz << 4;
        int r = s.type().radius;
        // Block-Entities der Puffer-Bloecke (Kisten, Spawner werden unten gesetzt)
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        for (int x = Math.max(minX, s.x() - r); x <= Math.min(minX + 15, s.x() + r); x++) {
            for (int z = Math.max(minZ, s.z() - r); z <= Math.min(minZ + 15, s.z() + r); z++) {
                for (int y = plan.base + (s.type() == Type.THRONE ? -52 : -12); y <= plan.base + (s.type() == Type.THRONE ? 112 : 20); y++) {
                    mp.set(x, y, z);
                    BlockState st = level.getBlockState(mp);
                    if (!st.hasBlockEntity() || st.getBlock() instanceof ChestBlock || st.getBlock() instanceof BarrelBlock || st.is(Blocks.SPAWNER)) continue;
                    if (level.getBlockEntity(mp) == null) level.setBlock(mp.immutable(), st, 2);
                }
            }
        }
        for (Loot l : plan.loot) {
            int x = s.wx(l.u(), l.v()), z = s.wz(l.u(), l.v()), y = plan.base + l.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            BlockState st = level.getBlockState(at);
            if (!(st.getBlock() instanceof ChestBlock) && !(st.getBlock() instanceof BarrelBlock)) continue;
            level.setBlock(at, st, 2);
            RandomSource rand = RandomSource.create(CityRandom.hash(seed ^ 0x3E3BL, x, y, z));
            //? if < 1.21.1 {
            RandomizableContainerBlockEntity.setLootTable(level, rand, at, tableId(l.table()));
            //?} else {
            /*net.minecraft.world.RandomizableContainer.setBlockEntityLootTable(level, rand, at, ResourceKey.create(Registries.LOOT_TABLE, tableId(l.table())));
            *///?}
        }
        for (MobSpot sp : plan.spawners) {
            int x = s.wx(sp.u(), sp.v()), z = s.wz(sp.u(), sp.v()), y = plan.base + sp.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            BlockState st = level.getBlockState(at);
            if (!st.is(Blocks.SPAWNER)) continue;
            level.setBlock(at, st, 2);
            if (level.getBlockEntity(at) instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(StructureMobs.type(sp.kind()), RandomSource.create(CityRandom.hash(seed ^ 0x3E3CL, x, y, z)));
            }
        }
        for (MobSpot m : plan.mobs) {
            int x = s.wx(m.u(), m.v()), z = s.wz(m.u(), m.v()), y = plan.base + m.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            StructureMobs.spawn(level, m.kind(), x, y, z, CityRandom.hash(seed ^ 0x3E3DL, x, y, z));
        }
    }

    /** Fuer Tests: lokale -> Welt-Koordinaten. */
    public static BlockPos worldPos(Plan p, int u, int y, int v) {
        return new BlockPos(p.s.wx(u, v), p.base + y, p.s.wz(u, v));
    }
}
