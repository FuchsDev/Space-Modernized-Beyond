package com.space_beyond.dim.dungeon;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.space_m.SpaceMod;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialSeed;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_beyond.dim.sporos.city.CityCanvas;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.CityRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Boss-Dungeons der Planeten (eigene Erweiterung, Planeten-Fortschritt): je Koerper ein Bauplan ({@link DungeonDesign}),
 * in Bezirken von {@link #DISTRICT_CHUNKS} Chunks mit {@link #CHANCE} je ein Dungeon, gedreht, Grundhoehe per
 * Sonden-Generator, Gelaende im Grundriss eingeebnet. Malen im Chunk-Puffer (Aufruf aus
 * {@code CelestialGenerators.buffer}), Loot/Spawner/Bewohner/Boss in der Dekorationsphase
 * ({@code CelestialChunkGenerator.applyBiomeDecoration}). Auffinden: Dungeon-Peiler, {@code /space locate_dungeon}.
 */
public final class PlanetDungeons {

    private PlanetDungeons() {}

    public static final int DISTRICT_CHUNKS = 64;
    public static final float CHANCE = 0.5F;
    public static final int BLEND = 8;
    private static final long SALT = 0xB055_D0D6_E011L;

    private static final Map<String, DungeonDesign> DESIGNS = new HashMap<>();

    public static void register(String body, DungeonDesign design) {
        DESIGNS.put(body, design);
    }

    @Nullable
    public static DungeonDesign design(String body) {
        return DESIGNS.get(body);
    }

    public static Map<String, DungeonDesign> designs() {
        return DESIGNS;
    }

    /** Geplanter Dungeon: Koerper, Mittelpunkt, Drehung 0-3, Seed. */
    public record Dungeon(String body, int x, int z, int rotation, long seed) {
        public DungeonDesign design() {
            return DESIGNS.get(body);
        }

        public int reach() {
            return design().radius() + BLEND;
        }

        public boolean touchesChunk(int cx, int cz) {
            int r = reach();
            return cx * 16 + 15 >= x - r && cx * 16 <= x + r && cz * 16 + 15 >= z - r && cz * 16 <= z + r;
        }

        public int distance(int bx, int bz) {
            return Math.max(Math.abs(bx - x), Math.abs(bz - z));
        }

        /** Welt (x, z) -> lokal u (Umkehrung von {@link #wx}/{@link #wz}). */
        public int lu(int bx, int bz) {
            int dx = bx - x, dz = bz - z;
            return switch (rotation) {
                case 1 -> dz;
                case 2 -> -dx;
                case 3 -> -dz;
                default -> dx;
            };
        }

        /** Welt (x, z) -> lokal v. */
        public int lv(int bx, int bz) {
            int dx = bx - x, dz = bz - z;
            return switch (rotation) {
                case 1 -> -dx;
                case 2 -> -dz;
                case 3 -> dx;
                default -> dz;
            };
        }

        public int wx(int u, int v) {
            return x + switch (rotation) {
                case 1 -> -v;
                case 2 -> -u;
                case 3 -> v;
                default -> u;
            };
        }

        public int wz(int u, int v) {
            return z + switch (rotation) {
                case 1 -> u;
                case 2 -> -v;
                case 3 -> -u;
                default -> v;
            };
        }
    }

    @Nullable
    public static Dungeon ofDistrict(String body, CelestialWorld world, int dx, int dz) {
        DungeonDesign design = DESIGNS.get(body);
        if (design == null) return null;
        long seed = CityRandom.mix(world.getSeed() ^ SALT ^ body.hashCode(), dx, dz);
        Random rand = new Random(seed);
        if (rand.nextFloat() >= CHANCE) return null;
        int margin = (design.radius() + BLEND) / 16 + 2;
        int span = Math.max(1, DISTRICT_CHUNKS - 2 * margin);
        int x = (dx * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
        int z = (dz * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
        int rot = rand.nextInt(4);
        long sSeed = rand.nextLong();
        // Biom: am Mittelpunkt pruefen, notfalls vier Ausweichpunkte im Bezirk
        for (int tries = 0; tries < 5; tries++) {
            if (design.fitsAt(world, x, z)) return new Dungeon(body, x, z, rot, sSeed);
            x = (dx * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
            z = (dz * DISTRICT_CHUNKS + margin + rand.nextInt(span)) * 16 + rand.nextInt(16);
        }
        return null;
    }

    @Nullable
    public static Dungeon at(String body, CelestialWorld world, int cx, int cz) {
        Dungeon d = ofDistrict(body, world, Math.floorDiv(cx, DISTRICT_CHUNKS), Math.floorDiv(cz, DISTRICT_CHUNKS));
        return d != null && d.touchesChunk(cx, cz) ? d : null;
    }

    @Nullable
    public static Dungeon nearest(String body, CelestialWorld world, int x, int z, int range) {
        int dx0 = Math.floorDiv(x >> 4, DISTRICT_CHUNKS), dz0 = Math.floorDiv(z >> 4, DISTRICT_CHUNKS);
        Dungeon best = null;
        double bestD = Double.MAX_VALUE;
        for (int dx = dx0 - range; dx <= dx0 + range; dx++) for (int dz = dz0 - range; dz <= dz0 + range; dz++) {
            Dungeon s = ofDistrict(body, world, dx, dz);
            if (s == null) continue;
            double d = Math.hypot(s.x() - x, s.z() - z);
            if (d < bestD) {
                bestD = d;
                best = s;
            }
        }
        return best;
    }

    // ── Planung ─────────────────────────────────────────────────────────────

    private static final ThreadLocal<Map<String, ChunkProviderCelestial>> PROBES = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Long>> PROBE_SEEDS = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<Dungeon, DungeonPlan>> PLANS = ThreadLocal.withInitial(() -> new LinkedHashMap<>(8, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Dungeon, DungeonPlan> eldest) {
            return size() > 8;
        }
    });

    public static DungeonPlan plan(CelestialWorld world, Dungeon d) {
        Map<Dungeon, DungeonPlan> plans = PLANS.get();
        DungeonPlan p = plans.get(d);
        if (p == null) {
            p = new DungeonPlan(d, baseY(world, d));
            d.design().plan(p);
            plans.put(d, p);
        }
        return p;
    }

    /** Natuerliche Oberflaeche am Mittelpunkt (Median des Mittel-Chunks, Sonden-Generator ohne Dungeons). */
    static int baseY(CelestialWorld world, Dungeon d) {
        ChunkProviderCelestial probe = PROBES.get().get(d.body());
        Long probeSeed = PROBE_SEEDS.get().get(d.body());
        if (probe == null || probeSeed == null || probeSeed != world.getSeed()) {
            probe = CelestialGenerators.require(d.body()).chunkProvider().apply(world, world.getSeed());
            PROBES.get().put(d.body(), probe);
            PROBE_SEEDS.get().put(d.body(), world.getSeed());
        }
        BlockState[] blocks = probe.provideChunk(d.x() >> 4, d.z() >> 4).blocks;
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
        DungeonDesign design = d.design();
        if (n == 0) return Math.max(design.minBase(), 70);
        java.util.Arrays.sort(h, 0, n);
        return Math.max(design.minBase(), Math.min(design.maxBase(), h[n / 2]));
    }

    // ── Erzeugung ───────────────────────────────────────────────────────────

    /** Aus {@code CelestialGenerators.buffer} nach dem Generator des Koerpers. */
    public static void generateChunk(String body, CelestialWorld world, int cx, int cz, BlockState[] blocks) {
        if (!DESIGNS.containsKey(body)) return;
        Dungeon d = at(body, world, cx, cz);
        if (d == null) return;
        DungeonPlan plan = plan(world, d);
        CityCanvas c = new CityCanvas(blocks, cx, cz);
        terrain(c, plan);
        DungeonPainter w = new DungeonPainter(c, plan, d.design());
        d.design().paint(w);
        for (DungeonPlan.Loot l : plan.loot) {
            BlockState below = w.get(l.u(), l.y() - 1, l.v());
            if (below == null || below.isAir()) continue;
            w.set(l.u(), l.y(), l.v(), l.barrel() ? CityPalette.facingAll(Blocks.BARREL.defaultBlockState(), Direction.UP)
                    : CityPalette.facing(Blocks.CHEST.defaultBlockState(), l.facing()));
        }
        for (DungeonPlan.Mob sp : plan.spawners) w.set(sp.u(), sp.y(), sp.v(), Blocks.SPAWNER.defaultBlockState());
    }

    /** Breite des ebenen Vorplatzes um den Eingang (Bloecke), danach Uebergang {@link #BLEND} zur Natur. */
    static final int PAD = 3;

    /**
     * Gelaende ueber dem Dungeon ({@link DungeonSurface}): nur um den Eingang wird auf die Grundhoehe eingeebnet; sonst
     * bleibt die Natur, ausser Krater wuerden das Bauwerk freilegen - dann wird mit Boeschung aufgeschuettet.
     */
    static void terrain(CityCanvas c, DungeonPlan p) {
        Dungeon d = p.d;
        DungeonDesign design = d.design();
        DungeonSurface shape = p.surface();
        BlockState top = design.surface(), fill = design.filler();
        for (int x = c.minX; x <= c.maxX(); x++) {
            for (int z = c.minZ; z <= c.maxZ(); z++) {
                int u = d.lu(x, z), v = d.lv(x, z);
                float pad = shape.pad(u, v), cover = shape.cover(u, v);
                double jitter = (CityRandom.noise(d.seed() ^ 0x7E45L, x, z, 6) - 0.5) * 3.0;
                double wgt = CityRandom.smoothstep(PAD, PAD + BLEND, pad + jitter);
                boolean needsCover = cover != DungeonSurface.NONE && pad > 0.5F;
                if (wgt >= 0.995 && !needsCover) continue;
                int nat = 0;
                for (int y = CityCanvas.HEIGHT - 1; y > 0; y--) {
                    BlockState b = c.get(x, y, z);
                    if (b != null && !b.isAir() && b.getFluidState().isEmpty()) {
                        nat = y;
                        break;
                    }
                }
                int target = wgt >= 0.995 ? nat : (int) Math.round(p.base + (nat - p.base) * wgt);
                if (needsCover) target = Math.max(target, p.base + Math.round(cover));
                if (target == nat) continue;
                // Vorplatz im Planetenmaterial des Dungeons, Aufschuettungen mit dem natuerlichen Oberflaechenblock
                BlockState natTop = c.get(x, nat, z);
                BlockState topHere = wgt < 0.5 || natTop == null || natTop.isAir() ? top : natTop;
                for (int y = CityCanvas.HEIGHT - 1; y > target; y--) {
                    BlockState b = c.get(x, y, z);
                    BlockState want = design.outside(y);
                    if (b != null && !b.isAir() && b != want) c.set(x, y, z, want);
                    else if ((b == null || b.isAir()) && !want.isAir()) c.set(x, y, z, want);
                }
                for (int y = target; y > 1 && (y > nat || y > target - (wgt < 0.5 ? 6 : 2)); y--) {
                    BlockState b = c.get(x, y, z);
                    boolean open = b == null || b.isAir() || !b.getFluidState().isEmpty();
                    if (y == target) {
                        if (wgt < 0.7 || open) c.set(x, y, z, topHere);
                    } else if (open || y > nat) {
                        c.set(x, y, z, target - y <= 3 && wgt < 0.5 ? top : fill);
                    }
                }
            }
        }
    }

    // ── Dekorationsphase ────────────────────────────────────────────────────

    public static ResourceLocation tableId(String name) {
        return com.space_beyond.BeyondMod.id("chests/dungeon/" + name);
    }

    /** Aus {@code CelestialChunkGenerator.applyBiomeDecoration}. */
    public static void decorate(String body, WorldGenLevel level, ChunkAccess chunk) {
        if (!DESIGNS.containsKey(body)) return;
        int cx = chunk.getPos().x, cz = chunk.getPos().z;
        CelestialWorld world = CelestialGenerators.state(body).chunkManager;
        Dungeon d = at(body, world, cx, cz);
        if (d == null) return;
        DungeonPlan plan = plan(world, d);
        DungeonDesign design = d.design();
        long seed = CelestialSeed.get();
        int minX = cx << 4, minZ = cz << 4;
        int r = design.radius();
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        for (int x = Math.max(minX, d.x() - r); x <= Math.min(minX + 15, d.x() + r); x++) {
            for (int z = Math.max(minZ, d.z() - r); z <= Math.min(minZ + 15, d.z() + r); z++) {
                for (int y = Math.max(1, plan.base + design.minY()); y <= Math.min(254, plan.base + design.maxY()); y++) {
                    mp.set(x, y, z);
                    BlockState st = level.getBlockState(mp);
                    if (!st.hasBlockEntity() || st.getBlock() instanceof ChestBlock || st.getBlock() instanceof BarrelBlock || st.is(Blocks.SPAWNER)) continue;
                    if (level.getBlockEntity(mp) == null) level.setBlock(mp.immutable(), st, 2);
                }
            }
        }
        for (DungeonPlan.Loot l : plan.loot) {
            int x = d.wx(l.u(), l.v()), z = d.wz(l.u(), l.v()), y = plan.base + l.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            BlockState st = level.getBlockState(at);
            if (!(st.getBlock() instanceof ChestBlock) && !(st.getBlock() instanceof BarrelBlock)) continue;
            level.setBlock(at, st, 2);
            RandomSource rand = RandomSource.create(CityRandom.hash(seed ^ 0xD06EL, x, y, z));
            //? if < 1.21.1 {
            RandomizableContainerBlockEntity.setLootTable(level, rand, at, tableId(l.table()));
            //?} else {
            /*net.minecraft.world.RandomizableContainer.setBlockEntityLootTable(level, rand, at, ResourceKey.create(Registries.LOOT_TABLE, tableId(l.table())));
            *///?}
        }
        for (DungeonPlan.Mob sp : plan.spawners) {
            int x = d.wx(sp.u(), sp.v()), z = d.wz(sp.u(), sp.v()), y = plan.base + sp.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            if (!level.getBlockState(at).is(Blocks.SPAWNER)) continue;
            level.setBlock(at, Blocks.SPAWNER.defaultBlockState(), 2);
            EntityType<?> type = type(sp.type());
            if (type != null && level.getBlockEntity(at) instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(type, RandomSource.create(CityRandom.hash(seed ^ 0xD06FL, x, y, z)));
            }
        }
        for (DungeonPlan.Mob m : plan.mobs) {
            int x = d.wx(m.u(), m.v()), z = d.wz(m.u(), m.v()), y = plan.base + m.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            // Der Boss erscheint erst, wenn das Siegeltor geoeffnet wird (spawnBoss)
            if (isBoss(design, m)) continue;
            spawn(level, m.type(), x, y, z, CityRandom.hash(seed ^ 0xD070L, x, y, z));
        }
        for (DungeonPlan.Mob m : plan.dispensers) {
            int x = d.wx(m.u(), m.v()), z = d.wz(m.u(), m.v()), y = plan.base + m.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            if (level.getBlockEntity(new BlockPos(x, y, z)) instanceof net.minecraft.world.level.block.entity.DispenserBlockEntity disp) {
                for (int slot = 0; slot < 3; slot++) disp.setItem(slot, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW, 16));
            }
        }
        for (DungeonPlan.Mob m : plan.elites) {
            int x = d.wx(m.u(), m.v()), z = d.wz(m.u(), m.v()), y = plan.base + m.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            spawn(level, m.type(), x, y, z, CityRandom.hash(seed ^ 0xD071L, x, y, z), true);
        }
    }

    /** Ist {@code m} der Boss des Dungeons (Typ des Stufen-Bosses von {@code design})? */
    static boolean isBoss(DungeonDesign design, DungeonPlan.Mob m) {
        var def = com.space_beyond.entity.mob.stage.StageBosses.DEFS.get(design.stage());
        return def != null && type(m.type()) == def.type().get();
    }

    /**
     * Siegeltor bei {@code gate} geoeffnet: Boss des Dungeons an seinem Platz in der Arena erscheinen lassen. Nicht,
     * wenn der Dungeon schon besiegt ist oder sein Boss noch lebt (Welten, in denen er beim Generieren gesetzt wurde).
     */
    public static boolean spawnBoss(net.minecraft.server.level.ServerLevel level, BlockPos gate) {
        com.space_m.celestial.CelestialBody body = com.space_m.celestial.CelestialBody.getBodyOrNull(level.dimension());
        if (body == null || !DESIGNS.containsKey(body.name)) return false;
        CelestialWorld world = CelestialGenerators.state(body.name).chunkManager;
        Dungeon d = at(body.name, world, gate.getX() >> 4, gate.getZ() >> 4);
        if (d == null) return false;
        String key = body.name + ":" + d.x() + ":" + d.z();
        if (com.space_beyond.dim.dungeon.seal.DungeonProtection.Data.get(level.getServer()).defeated.contains(key)) return false;
        DungeonPlan plan = plan(world, d);
        DungeonDesign design = d.design();
        for (DungeonPlan.Mob m : plan.mobs) {
            if (!isBoss(design, m)) continue;
            EntityType<?> type = type(m.type());
            int r = design.radius();
            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(d.x() - r, plan.base + design.minY(), d.z() - r,
                    d.x() + r + 1, plan.base + design.maxY() + 1, d.z() + r + 1);
            if (!level.getEntities(type, area, Entity::isAlive).isEmpty()) return false;
            BlockPos at = worldPos(plan, m.u(), m.y(), m.v());
            return spawn(level, m.type(), at.getX(), at.getY(), at.getZ(), CityRandom.hash(CelestialSeed.get() ^ 0xD070L, at.getX(), at.getY(), at.getZ()));
        }
        return false;
    }

    @Nullable
    public static EntityType<?> type(String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return null;
        return BuiltInRegistries.ENTITY_TYPE.getOptional(rl).orElse(null);
    }

    /** Setzt einen Mob (Fuesse bei x, y, z), falls dort Platz ist; Wassermobs auch im Wasser. */
    public static boolean spawn(WorldGenLevel level, String id, int x, int y, int z, long seed) {
        return spawn(level, id, x, y, z, seed, false);
    }

    /** Wie oben; {@code elite}: als Siegelwaechter ({@link com.space_beyond.dim.dungeon.seal.SealBlocks#makeGuardian}). */
    public static boolean spawn(WorldGenLevel level, String id, int x, int y, int z, long seed, boolean elite) {
        EntityType<?> type = type(id);
        if (type == null) return false;
        BlockPos pos = null;
        for (int dy = 0; dy <= 3 && pos == null; dy++) {
            BlockPos p = new BlockPos(x, y + dy, z);
            BlockState feet = level.getBlockState(p), head = level.getBlockState(p.above());
            boolean wet = !feet.getFluidState().isEmpty();
            if (feet.getCollisionShape(level, p).isEmpty() && head.getCollisionShape(level, p.above()).isEmpty()
                    && (wet || !level.getBlockState(p.below()).getCollisionShape(level, p.below()).isEmpty())) pos = p;
        }
        if (pos == null) return false;
        Entity e = type.create(level.getLevel());
        if (!(e instanceof Mob mob)) return false;
        RandomSource rand = RandomSource.create(seed);
        mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rand.nextFloat() * 360.0F, 0.0F);
        mob.setPersistenceRequired();
        mob.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG);
        //? if < 1.21.1 {
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
        //?} else {
        /*mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
        *///?}
        if (elite) com.space_beyond.dim.dungeon.seal.SealBlocks.makeGuardian(mob);
        level.addFreshEntityWithPassengers(mob);
        return true;
    }

    /** Welt-Koordinaten eines lokalen Punktes (Tests, Befehle, Peiler). */
    public static BlockPos worldPos(DungeonPlan p, int u, int y, int v) {
        return new BlockPos(p.d.wx(u, v), p.base + y, p.d.wz(u, v));
    }
}
