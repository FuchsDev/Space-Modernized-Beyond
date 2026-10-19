package com.space_beyond.dim.wemburos;

import java.util.Random;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.mapgen.MapGenCrater;
import com.space_m.dim.mapgen.MapGenVanillaCaves;
import com.space_m.dim.mapgen.MapgenRavineButBased;
import com.space_beyond.dim.wemburos.biome.BiomeGenBaseWemburos;
import com.space_beyond.dim.wemburos.structure.WemburosStructures;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gelaende von Wemburos (eigene Erweiterung): 3D-Rauschen mit {@code wemburos_stone}, kein Meer (unter y 63 Luft),
 * Hoehlen, Schluchten, kleine und grosse Krater (Regolith/Onyx), danach im Chunk-Puffer:
 * <ul>
 *   <li>Adern: Golderz (y 8-72), Goldadern im Onyx, instabile Kristalle in der Tiefe (y 5-32), Onyx-Taschen.</li>
 *   <li>Oberflaeche je Biom: Kristalldrusen (Kristallfelder, Riss), Kristallsaeulen, Goldspitzen aus Onyx mit
 *       Goldadern und gelegentlich einer Goldkrone (Goldhochland), Onyx-Findlinge (Ebene), schwebende Onyx-Brocken
 *       mit haengenden Kristallen (Riss).</li>
 *   <li>Neue Biome: Duskwood-Baeume, Blueten (Duskwood-Wald), Felsnadeln (Onyx-Stachelfeld), Riesenrippen, Totholz,
 *       Rohgold (Goldduenen), Chorus-Pflanzen und Purpurruinen (Chorus-Oednis), Basaltsaeulen und Lavabecken (Caldera).</li>
 *   <li>Wildnis-Strukturen ({@link WemburosStructures}).</li>
 * </ul>
 * Alle Puffer-Features bleiben im eigenen Chunk (deterministisch aus Weltseed und Chunk).
 */
public class ChunkProviderWemburos extends ChunkProviderCelestial {

    private final MapGenVanillaCaves caves;
    private final MapgenRavineButBased ravines = new MapgenRavineButBased();
    private final MapGenCrater smallCrater = new MapGenCrater(8);
    private final MapGenCrater largeCrater = new MapGenCrater(80);
    private final long seed;

    public ChunkProviderWemburos(CelestialWorld world, long seed) {
        super(world, seed);
        this.seed = seed;
        BlockState stone = WemburosBlocks.WEMBUROS_STONE.get().defaultBlockState();
        this.stoneBlock = stone;
        this.seaBlock = Blocks.AIR.defaultBlockState();
        this.seaLevel = BiomeGenBaseWemburos.SEA_LEVEL;
        this.caves = new MapGenVanillaCaves(stone);
        ravines.stoneBlock = stone;
        smallCrater.setSize(8, 24);
        largeCrater.setSize(48, 96);
        smallCrater.regolith = largeCrater.regolith = WemburosBlocks.WEMBUROS_REGOLITH.get().defaultBlockState();
        smallCrater.rock = largeCrater.rock = WemburosBlocks.WEMBUROS_ONYX.get().defaultBlockState();
    }

    @Override
    public BlockMetaBuffer getChunkPrimer(int x, int z) {
        BlockMetaBuffer buffer = super.getChunkPrimer(x, z);
        caves.func_151539_a(worldObj, x, z, buffer.blocks);
        ravines.func_151539_a(worldObj, x, z, buffer.blocks);
        smallCrater.func_151539_a(worldObj, x, z, buffer.blocks);
        largeCrater.func_151539_a(worldObj, x, z, buffer.blocks);
        decorate(x, z, buffer.blocks, biomesForGeneration);
        WemburosStructures.generateChunk(worldObj, x, z, buffer.blocks);
        return buffer;
    }

    // ── Puffer-Dekoration ───────────────────────────────────────────────────

    private static int idx(int lx, int y, int lz) {
        return (lx * 16 + lz) * 256 + y;
    }

    private static int top(BlockState[] b, int lx, int lz) {
        for (int y = 254; y > 1; y--) {
            BlockState s = b[idx(lx, y, lz)];
            if (s != null && !s.isAir()) return y;
        }
        return -1;
    }

    private void decorate(int cx, int cz, BlockState[] b, BiomeGenBaseCelestial[] biomes) {
        Random rand = new Random(seed ^ (cx * 341873128712L + cz * 132897987541L) ^ 0x5E3B0L);
        BlockState stone = WemburosBlocks.WEMBUROS_STONE.get().defaultBlockState();
        BlockState onyx = WemburosBlocks.WEMBUROS_ONYX.get().defaultBlockState();
        BlockState gildedOnyx = WemburosBlocks.GILDED_ONYX.get().defaultBlockState();
        BlockState goldOre = WemburosBlocks.WEMBUROS_GOLD_ORE.get().defaultBlockState();
        BlockState crystal = WemburosBlocks.UNSTABLE_CRYSTAL.get().defaultBlockState();

        // Adern im Gestein
        for (int i = 0; i < 7; i++) blob(b, rand, 8 + rand.nextInt(65), 3 + rand.nextInt(5), goldOre, stone);
        for (int i = 0; i < 6; i++) blob(b, rand, 5 + rand.nextInt(60), 12 + rand.nextInt(16), onyx, stone);
        for (int i = 0; i < 4; i++) blob(b, rand, 5 + rand.nextInt(70), 3 + rand.nextInt(4), gildedOnyx, onyx);
        for (int i = 0; i < 2; i++) blob(b, rand, 5 + rand.nextInt(28), 2 + rand.nextInt(3), crystal, stone);

        // Neue Biome (eigener Zufall, damit die alten Biome unveraendert bleiben): grosse Objekte zuerst
        Random fr = new Random(seed ^ (cx * 0x2545F4914F6CDD1DL + cz * 0x9E3779B97F4A7C15L) ^ 0xB10E5L);
        BiomeGenBaseCelestial center = biomes != null ? biomes[8 * 16 + 8] : null;
        if (center == BiomeGenBaseWemburos.duskwoodForest) forest(b, fr);
        else if (center == BiomeGenBaseWemburos.onyxSpires) spires(b, fr, onyx, stone, gildedOnyx, crystal);
        else if (center == BiomeGenBaseWemburos.goldenDunes) dunes(b, fr);
        else if (center == BiomeGenBaseWemburos.chorusWastes) chorusWastes(b, fr);
        else if (center == BiomeGenBaseWemburos.caldera) caldera(b, fr);

        // Oberflaeche je Spalte
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                BiomeGenBaseCelestial biome = biomes != null ? biomes[lz * 16 + lx] : null;
                int y = top(b, lx, lz);
                if (y < 2 || y > 250) continue;
                BlockState ground = b[idx(lx, y, lz)];
                if (ground == null || ground.getBlock() instanceof AmethystClusterBlock) continue;
                double r = rand.nextDouble();
                if (biome == BiomeGenBaseWemburos.crystalFields) {
                    if (r < 0.06) b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                    else if (r < 0.068 && lx > 0 && lx < 15 && lz > 0 && lz < 15) {
                        int h = 2 + rand.nextInt(3);
                        for (int dy = 1; dy <= h; dy++) b[idx(lx, y + dy, lz)] = crystal;
                        b[idx(lx, y + h + 1, lz)] = cluster(Direction.UP);
                    }
                } else if (biome == BiomeGenBaseWemburos.rift) {
                    if (r < 0.03) b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                    else if (r < 0.036) b[idx(lx, y, lz)] = gildedOnyx;
                } else if (biome == BiomeGenBaseWemburos.onyxBadlands) {
                    if (r < 0.012) b[idx(lx, y, lz)] = gildedOnyx;
                    else if (r < 0.016) b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                } else if (biome == BiomeGenBaseWemburos.gildedHighlands) {
                    if (r < 0.02) b[idx(lx, y, lz)] = gildedOnyx;
                } else if (biome == BiomeGenBaseWemburos.duskwoodForest) {
                    if (ground.is(WemburosBlocks.VIOLET_MOSS.get())) {
                        if (r < 0.07) b[idx(lx, y + 1, lz)] = Blocks.PINK_PETALS.defaultBlockState()
                                .setValue(PinkPetalsBlock.AMOUNT, 1 + rand.nextInt(4))
                                .setValue(PinkPetalsBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(rand.nextLong())));
                        else if (r < 0.075) b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                    }
                } else if (biome == BiomeGenBaseWemburos.goldenDunes) {
                    if (ground.is(WemburosBlocks.GILDED_SAND.get())) {
                        if (r < 0.006) b[idx(lx, y + 1, lz)] = Blocks.DEAD_BUSH.defaultBlockState();
                        else if (r < 0.009) b[idx(lx, y, lz)] = Blocks.RAW_GOLD_BLOCK.defaultBlockState();
                    }
                } else if (biome == BiomeGenBaseWemburos.chorusWastes) {
                    if (r < 0.01 && ground.is(Blocks.END_STONE)) b[idx(lx, y, lz)] = Blocks.END_STONE_BRICKS.defaultBlockState();
                } else if (biome == BiomeGenBaseWemburos.caldera) {
                    if (r < 0.025) b[idx(lx, y, lz)] = Blocks.GILDED_BLACKSTONE.defaultBlockState();
                    else if (r < 0.04) b[idx(lx, y, lz)] = Blocks.MAGMA_BLOCK.defaultBlockState();
                } else if (biome == BiomeGenBaseWemburos.onyxSpires) {
                    if (r < 0.01) b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                } else if (r < 0.004) {
                    b[idx(lx, y + 1, lz)] = cluster(Direction.UP);
                }
            }
        }

        // Grosse Einzelobjekte (Mitte des Chunks, damit nichts ueber den Rand ragt)
        BiomeGenBaseCelestial mid = biomes != null ? biomes[8 * 16 + 8] : null;
        int ox = 5 + rand.nextInt(6), oz = 5 + rand.nextInt(6);
        int oy = top(b, ox, oz);
        if (oy < 2 || oy > 220) return;
        if (mid == BiomeGenBaseWemburos.gildedHighlands && rand.nextFloat() < 0.3F) {
            spire(b, rand, ox, oy, oz, onyx, gildedOnyx);
        } else if (mid == BiomeGenBaseWemburos.rift && rand.nextFloat() < 0.35F) {
            floatingRock(b, rand, ox, oy + 12 + rand.nextInt(10), oz, onyx, gildedOnyx, crystal);
        } else if (mid == BiomeGenBaseWemburos.violetPlains && rand.nextFloat() < 0.2F) {
            boulder(b, rand, ox, oy, oz, onyx);
        }
    }

    private static BlockState cluster(Direction facing) {
        return WemburosBlocks.UNSTABLE_CRYSTAL_CLUSTER.get().defaultBlockState().setValue(AmethystClusterBlock.FACING, facing);
    }

    // ── Neue Biome ──────────────────────────────────────────────────────────

    private static boolean inside(int x, int y, int z) {
        return x >= 0 && x < 16 && z >= 0 && z < 16 && y > 1 && y < 255;
    }

    private static void put(BlockState[] b, int x, int y, int z, BlockState s) {
        if (inside(x, y, z)) b[idx(x, y, z)] = s;
    }

    private static void putIfAir(BlockState[] b, int x, int y, int z, BlockState s) {
        if (inside(x, y, z) && (b[idx(x, y, z)] == null || b[idx(x, y, z)].isAir())) b[idx(x, y, z)] = s;
    }

    private static boolean isAir(BlockState[] b, int x, int y, int z) {
        if (!inside(x, y, z)) return false;
        BlockState s = b[idx(x, y, z)];
        return s == null || s.isAir();
    }

    private static BlockState log(Direction.Axis axis) {
        return WemburosBlocks.DUSKWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    /** Laub mit Abstand 1 (sonst zerfaellt frisch generiertes Laub beim naechsten Zufallstick). */
    private static BlockState leaves() {
        return WemburosBlocks.DUSKWOOD_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
    }

    /** Duskwood-Wald: 3-6 Baeume je Chunk, Schirmkronen-Baeume und schlanke Turmbaeume. */
    private static void forest(BlockState[] b, Random r) {
        int n = 3 + r.nextInt(4);
        java.util.List<int[]> placed = new java.util.ArrayList<>();
        for (int i = 0; i < n * 3 && placed.size() < n; i++) {
            int x = 3 + r.nextInt(10), z = 3 + r.nextInt(10);
            boolean near = false;
            for (int[] p : placed) near |= Math.abs(p[0] - x) < 4 && Math.abs(p[1] - z) < 4;
            if (near) continue;
            int y = top(b, x, z);
            if (y < 2 || y > 220) continue;
            BlockState g = b[idx(x, y, z)];
            if (g == null || !(g.is(WemburosBlocks.VIOLET_MOSS.get()) || g.is(WemburosBlocks.WEMBUROS_REGOLITH.get()))) continue;
            if (r.nextFloat() < 0.65F) umbrellaTree(b, r, x, y, z);
            else spireTree(b, r, x, y, z);
            placed.add(new int[] {x, z});
        }
    }

    /** Schirmkrone: Stamm 6-9, zwei Seitenaeste, flache breite Krone, haengende Laubstraenge mit Goldlicht. */
    private static void umbrellaTree(BlockState[] b, Random r, int x, int y, int z) {
        int h = 6 + r.nextInt(4);
        for (int dy = 1; dy <= h; dy++) put(b, x, y + dy, z, log(Direction.Axis.Y));
        // Seitenaeste
        for (int k = 0; k < 2; k++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(r.nextLong()));
            int by = y + h - 2 - k;
            for (int s = 1; s <= 2; s++) put(b, x + d.getStepX() * s, by + (s == 2 ? 1 : 0), z + d.getStepZ() * s, log(d.getAxis()));
            int ex = x + d.getStepX() * 2, ez = z + d.getStepZ() * 2;
            for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) for (int dy = 1; dy <= 2; dy++) {
                if (Math.abs(dx) + Math.abs(dz) + dy <= 3) putIfAir(b, ex + dx, by + dy, ez + dz, leaves());
            }
        }
        // Krone: Ringe mit Radius 2,3,3,2
        int[] rad = {2, 3, 3, 2};
        for (int i = 0; i < rad.length; i++) {
            int cy = y + h - 1 + i, rr = rad[i];
            for (int dx = -rr; dx <= rr; dx++) for (int dz = -rr; dz <= rr; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 > rr * rr + 1 || (d2 > rr * rr - 1 && r.nextFloat() < 0.3F)) continue;
                putIfAir(b, x + dx, cy, z + dz, leaves());
            }
        }
        put(b, x, y + h + 3, z, leaves());
        // haengende Straenge unter dem Kronenrand, manche mit Goldlicht
        for (int k = 0; k < 4; k++) {
            int dx = r.nextInt(7) - 3, dz = r.nextInt(7) - 3;
            if (dx * dx + dz * dz < 5) continue;
            int len = 1 + r.nextInt(3);
            int sy = y + h - 2;
            if (isAir(b, x + dx, sy + 1, z + dz)) continue;
            for (int s = 0; s < len; s++) putIfAir(b, x + dx, sy - s, z + dz, leaves());
            if (r.nextFloat() < 0.4F) putIfAir(b, x + dx, sy - len, z + dz, Blocks.OCHRE_FROGLIGHT.defaultBlockState());
        }
    }

    /** Turmbaum: Stamm 10-14, drei kegelfoermige Laubetagen, Kristallspitze. */
    private static void spireTree(BlockState[] b, Random r, int x, int y, int z) {
        int h = 10 + r.nextInt(5);
        for (int dy = 1; dy <= h; dy++) put(b, x, y + dy, z, log(Direction.Axis.Y));
        for (int tier = 0; tier < 3; ++tier) {
            int base = y + h - 8 + tier * 3;
            for (int dy = 0; dy < 3; dy++) {
                int rr = 3 - tier / 2 - dy;
                if (rr < 1) rr = 1;
                for (int dx = -rr; dx <= rr; dx++) for (int dz = -rr; dz <= rr; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > rr + (dy == 0 ? 1 : 0)) continue;
                    putIfAir(b, x + dx, base + dy, z + dz, leaves());
                }
            }
        }
        put(b, x, y + h + 1, z, leaves());
        put(b, x, y + h + 2, z, r.nextFloat() < 0.5F ? cluster(Direction.UP) : leaves());
    }

    /** Onyx-Stachelfeld: 1-3 Felsnadeln (12-40 hoch) mit Goldbaendern und Kristallspitze. */
    private static void spires(BlockState[] b, Random r, BlockState onyx, BlockState stone, BlockState gilded, BlockState crystal) {
        int n = 1 + r.nextInt(3);
        for (int i = 0; i < n; i++) {
            int x = 4 + r.nextInt(8), z = 4 + r.nextInt(8);
            int y = top(b, x, z);
            if (y < 2 || y > 200) continue;
            int h = 12 + r.nextInt(29);
            // Basis <= 3, Neigung <= 1: bleibt im Chunk (Mitte 4..11)
            double base = 1.4 + r.nextDouble() * 1.2;
            double leanX = (r.nextDouble() - 0.5) * 2.0, leanZ = (r.nextDouble() - 0.5) * 2.0;
            int band = 5 + r.nextInt(4);
            for (int dy = -3; dy <= h; dy++) {
                double f = Math.max(0, (double) dy / h);
                double rad = base * Math.pow(1.0 - f, 1.3) + 0.35;
                int ox = (int) Math.round(leanX * f), oz = (int) Math.round(leanZ * f);
                int ir = (int) Math.ceil(rad);
                for (int dx = -ir; dx <= ir; dx++) for (int dz = -ir; dz <= ir; dz++) {
                    if (dx * dx + dz * dz > rad * rad) continue;
                    BlockState m = dy > 0 && dy % band == 0 ? gilded : (dx + dz + dy) % 3 == 0 ? stone : onyx;
                    put(b, x + ox + dx, y + dy, z + oz + dz, m);
                }
            }
            int tx = x + (int) Math.round(leanX), tz = z + (int) Math.round(leanZ);
            if (r.nextFloat() < 0.4F) {
                put(b, tx, y + h + 1, tz, crystal);
                putIfAir(b, tx, y + h + 2, tz, cluster(Direction.UP));
            }
        }
    }

    /** Goldduenen: gelegentlich ein halb verschuettetes Riesenskelett (Rippenboegen aus Knochenbloecken). */
    private static void dunes(BlockState[] b, Random r) {
        if (r.nextFloat() >= 0.12F) return;
        int z0 = 8, x0 = 3;
        int y = top(b, 8, 8);
        if (y < 2 || y > 220) return;
        BlockState bone = Blocks.BONE_BLOCK.defaultBlockState();
        int len = 8 + r.nextInt(3);
        for (int x = x0; x < x0 + len; x++) {
            put(b, x, y, z0, bone.setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
            if ((x - x0) % 2 == 1 && x < x0 + len - 1) {
                int rr = 4 - Math.abs(x - x0 - len / 2) / 3;
                for (int a = 0; a <= 180; a += 12) {
                    double ang = Math.toRadians(a);
                    int dz = (int) Math.round(Math.cos(ang) * rr), dy = (int) Math.round(Math.sin(ang) * rr) - 1;
                    put(b, x, y + dy, z0 + dz, bone);
                }
            }
        }
        // Schaedel am Ende
        for (int dx = 0; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) for (int dy = 0; dy <= 1; dy++) put(b, x0 + len + dx, y + dy, z0 + dz, bone);
        put(b, x0 + len + 1, y + 1, z0 - 1, Blocks.AIR.defaultBlockState());
        put(b, x0 + len + 1, y + 1, z0 + 1, Blocks.AIR.defaultBlockState());
    }

    /** Chorus-Oednis: 2-4 Chorus-Pflanzen auf Endstein, selten eine zerfallene Purpursaeule mit Endstab. */
    private static void chorusWastes(BlockState[] b, Random r) {
        int n = 2 + r.nextInt(3);
        for (int i = 0; i < n; i++) {
            int x = 3 + r.nextInt(10), z = 3 + r.nextInt(10);
            int y = top(b, x, z);
            if (y < 2 || y > 220) continue;
            put(b, x, y, z, Blocks.END_STONE.defaultBlockState());
            chorus(b, r, x, y, z);
        }
        if (r.nextFloat() < 0.18F) {
            int x = 5 + r.nextInt(6), z = 5 + r.nextInt(6);
            int y = top(b, x, z);
            if (y < 2 || y > 220) return;
            int h = 3 + r.nextInt(4);
            BlockState pillar = Blocks.PURPUR_PILLAR.defaultBlockState();
            for (int dy = 0; dy <= h; dy++) put(b, x, y + dy, z, dy == 0 ? Blocks.PURPUR_BLOCK.defaultBlockState() : pillar);
            if (r.nextBoolean()) put(b, x, y + h + 1, z, Blocks.END_ROD.defaultBlockState());
            for (int k = 0; k < 5; k++) {
                int dx = r.nextInt(5) - 2, dz = r.nextInt(5) - 2;
                int ty = top(b, x + dx, z + dz);
                if (ty > 1) putIfAir(b, x + dx, ty + 1, z + dz, k % 2 == 0 ? Blocks.PURPUR_BLOCK.defaultBlockState() : Blocks.PURPUR_SLAB.defaultBlockState());
            }
        }
    }

    /** Chorus-Pflanze wie in Ende: Stamm, Seitenzweige, verwelkte Blueten; Verbindungen werden aus den Nachbarn gesetzt. */
    private static void chorus(BlockState[] b, Random r, int x, int y, int z) {
        java.util.Set<Long> plant = new java.util.HashSet<>();
        java.util.Set<Long> flowers = new java.util.HashSet<>();
        grow(r, plant, flowers, x, y + 1, z, 0);
        for (long key : plant) {
            int px = (int) (key >> 40), py = (int) ((key >> 20) & 0xFFFFF), pz = (int) (key & 0xFFFFF);
            boolean up = plant.contains(pack(px, py + 1, pz)) || flowers.contains(pack(px, py + 1, pz));
            boolean down = plant.contains(pack(px, py - 1, pz)) || py == y + 1;
            BlockState st = Blocks.CHORUS_PLANT.defaultBlockState()
                    .setValue(BlockStateProperties.UP, up).setValue(BlockStateProperties.DOWN, down)
                    .setValue(BlockStateProperties.NORTH, linked(plant, flowers, px, py, pz - 1))
                    .setValue(BlockStateProperties.SOUTH, linked(plant, flowers, px, py, pz + 1))
                    .setValue(BlockStateProperties.WEST, linked(plant, flowers, px - 1, py, pz))
                    .setValue(BlockStateProperties.EAST, linked(plant, flowers, px + 1, py, pz));
            put(b, px, py, pz, st);
        }
        for (long key : flowers) {
            int px = (int) (key >> 40), py = (int) ((key >> 20) & 0xFFFFF), pz = (int) (key & 0xFFFFF);
            put(b, px, py, pz, Blocks.CHORUS_FLOWER.defaultBlockState().setValue(BlockStateProperties.AGE_5, 5));
        }
    }

    private static boolean linked(java.util.Set<Long> plant, java.util.Set<Long> flowers, int x, int y, int z) {
        return plant.contains(pack(x, y, z)) || flowers.contains(pack(x, y, z));
    }

    private static long pack(int x, int y, int z) {
        return ((long) x << 40) | ((long) y << 20) | (z & 0xFFFFF);
    }

    private static void grow(Random r, java.util.Set<Long> plant, java.util.Set<Long> flowers, int x, int y, int z, int depth) {
        int h = 1 + r.nextInt(depth == 0 ? 4 : 3);
        for (int i = 0; i < h; i++) {
            if (!inside(x, y + i, z) || x < 1 || x > 14 || z < 1 || z > 14) return;
            plant.add(pack(x, y + i, z));
        }
        int ty = y + h;
        if (depth >= 3 || r.nextFloat() < 0.25F) {
            flowers.add(pack(x, ty, z));
            return;
        }
        int branches = 1 + r.nextInt(depth == 0 ? 3 : 2);
        boolean any = false;
        for (int k = 0; k < branches; k++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(r.nextLong()));
            int nx = x + d.getStepX(), nz = z + d.getStepZ();
            if (nx < 1 || nx > 14 || nz < 1 || nz > 14 || plant.contains(pack(nx, ty - 1, nz))) continue;
            plant.add(pack(nx, ty - 1, nz));
            grow(r, plant, flowers, nx, ty, nz, depth + 1);
            any = true;
        }
        if (!any) flowers.add(pack(x, ty, z));
    }

    /** Caldera: Basaltsaeulen-Gruppen und kleine, ringsum dichte Lavabecken mit Magmarand. */
    private static void caldera(BlockState[] b, Random r) {
        BlockState basalt = Blocks.BASALT.defaultBlockState();
        int groups = 1 + r.nextInt(2);
        for (int g = 0; g < groups; g++) {
            int cx = 3 + r.nextInt(10), cz = 3 + r.nextInt(10);
            for (int k = 0; k < 6; k++) {
                int x = cx + r.nextInt(3) - 1, z = cz + r.nextInt(3) - 1;
                int y = top(b, x, z);
                if (y < 2 || y > 230) continue;
                int h = 3 + r.nextInt(8);
                for (int dy = 1; dy <= h; dy++) put(b, x, y + dy, z, basalt);
            }
        }
        for (int k = 0; k < 4; k++) {
            int x = 2 + r.nextInt(12), z = 2 + r.nextInt(12);
            int y = top(b, x, z);
            if (y < 3 || y > 230) continue;
            boolean ok = true;
            for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                BlockState n = b[idx(x + d[0], y, z + d[1])];
                ok &= n != null && !n.isAir() && n.getFluidState().isEmpty() && top(b, x + d[0], z + d[1]) == y;
            }
            BlockState below = b[idx(x, y - 1, z)];
            if (!ok || below == null || below.isAir()) continue;
            b[idx(x, y, z)] = Blocks.LAVA.defaultBlockState();
            for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) b[idx(x + d[0], y, z + d[1])] = Blocks.MAGMA_BLOCK.defaultBlockState();
        }
    }

    /** Kleiner Adernklumpen (ersetzt nur {@code host}). */
    private static void blob(BlockState[] b, Random rand, int y, int size, BlockState ore, BlockState host) {
        int x = rand.nextInt(16), z = rand.nextInt(16);
        for (int i = 0; i < size; i++) {
            if (x >= 0 && x < 16 && z >= 0 && z < 16 && y > 1 && y < 255) {
                int k = idx(x, y, z);
                if (b[k] == host) b[k] = ore;
            }
            switch (rand.nextInt(6)) {
                case 0 -> x++;
                case 1 -> x--;
                case 2 -> z++;
                case 3 -> z--;
                case 4 -> y++;
                default -> y--;
            }
        }
    }

    /** Goldspitze: Onyxkegel (Hoehe 7-14) mit Goldadern, selten eine Goldkrone. */
    private static void spire(BlockState[] b, Random rand, int x, int y, int z, BlockState onyx, BlockState gilded) {
        int h = 7 + rand.nextInt(8);
        for (int dy = -2; dy <= h; dy++) {
            int r = dy < h / 3 ? 2 : dy < 2 * h / 3 ? 1 : 0;
            for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz > r * r + 1) continue;
                int k = idx(x + dx, y + dy, z + dz);
                b[k] = rand.nextFloat() < 0.18F ? gilded : onyx;
            }
        }
        if (rand.nextFloat() < 0.25F) b[idx(x, y + h + 1, z)] = Blocks.GOLD_BLOCK.defaultBlockState();
        else if (rand.nextFloat() < 0.5F) b[idx(x, y + h + 1, z)] = Blocks.RAW_GOLD_BLOCK.defaultBlockState();
    }

    /** Schwebender Onyxbrocken (Riss) mit Goldadern oben und haengenden Kristallen unten. */
    private static void floatingRock(BlockState[] b, Random rand, int x, int y, int z, BlockState onyx, BlockState gilded, BlockState crystal) {
        if (y > 240) return;
        int r = 2 + rand.nextInt(2);
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) for (int dy = -r; dy <= 1; dy++) {
            double d = dx * dx + dz * dz + dy * dy * 2.2;
            if (d > r * r + 0.5) continue;
            BlockState s = dy == 1 && rand.nextFloat() < 0.3F ? gilded : rand.nextFloat() < 0.05F ? crystal : onyx;
            b[idx(x + dx, y + dy, z + dz)] = s;
        }
        for (int dx = -r + 1; dx <= r - 1; dx++) for (int dz = -r + 1; dz <= r - 1; dz++) {
            if (rand.nextFloat() < 0.35F) {
                int k = idx(x + dx, y - r, z + dz);
                if (b[k] == onyx || b[k] == gilded) {
                    int below = idx(x + dx, y - r - 1, z + dz);
                    if (b[below] == null || b[below].isAir()) b[below] = cluster(Direction.DOWN);
                }
            }
        }
    }

    /** Onyx-Findling (Ebene). */
    private static void boulder(BlockState[] b, Random rand, int x, int y, int z, BlockState onyx) {
        int r = 1 + rand.nextInt(2);
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) for (int dy = 0; dy <= r; dy++) {
            if (dx * dx + dz * dz + dy * dy > r * r + 1) continue;
            b[idx(x + dx, y + dy, z + dz)] = onyx;
        }
    }
}
