package com.space_beyond.dim.sporos.flora;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.space_beyond.dim.sporos.flora.block.BlockShelfFungus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Baumgenerator der Hyphe (eigener Code, Aufbau wie {@code TTree}: direkter Blocksatz statt Trunk-/FoliagePlacer,
 * damit Setzling und Feature denselben Code nutzen). Drei Formen:
 * <ul>
 *   <li>{@link Shape#GIANT} Urwaldriese: 2x2-Stamm, 16-22 hoch, Brettwurzeln am Fuss, 5-7 aufsteigende Aeste mit
 *       Laubballen, breite Kronenkuppel, Pilzregale am Stamm, Ranken.</li>
 *   <li>{@link Shape#CANOPY} Schirmbaum: 8-13 hoch, 5-8 fast waagerechte Aeste, flache unregelmaessige Schirmkrone
 *       (Radius 5-7) mit vielen haengenden Ranken.</li>
 *   <li>{@link Shape#SWAMP} Sumpfbaum: krummer, wandernder Stamm 8-12 hoch, 3-5 Stelzwurzeln bis in den Boden
 *       (auch durch Wasser), kleine zerzauste Krone, Pilzregale, Ranken.</li>
 * </ul>
 * Dazu {@link #generateDeadwood} (umgestuerzte/abgestorbene Staemme mit Moos, Pilzregalen, Leuchtpilzen) und
 * {@link #generateGiantFungus} (Riesenpilz aus Stiel-/Hutbloecken fuer die Pilzhaine).
 *
 * <p>Laub bekommt {@code DISTANCE} = Manhattan-Abstand zum naechsten Stammblock (hoechstens 6, sonst kein Laub),
 * damit es nicht sofort zerfaellt. Ersetzt werden Luft, Laub, {@code replaceable_by_trees} und ersetzbare Bloecke.
 */
public final class HypheTree {

    private HypheTree() {}

    public enum Shape { GIANT, CANOPY, SWAMP }

    /** Zufallsform fuer den Setzling: Schirmbaum 50 %, Riese 25 %, Sumpfbaum 25 %. */
    public static Shape randomShape(Random rand) {
        int r = rand.nextInt(4);
        return r == 0 ? Shape.GIANT : r == 1 ? Shape.SWAMP : Shape.CANOPY;
    }

    /** Arbeitszustand eines Baums: gesetzte Staemme (fuer Laubabstand) und Laubkandidaten. */
    private static final class Build {
        final WorldGenLevel world;
        final Random rand;
        final Set<BlockPos> logs = new LinkedHashSet<>();
        final Set<BlockPos> leaves = new LinkedHashSet<>();
        final List<BlockPos> shelves = new ArrayList<>();

        Build(WorldGenLevel world, Random rand) {
            this.world = world;
            this.rand = rand;
        }

        boolean replaceable(BlockPos p) {
            BlockState s = world.getBlockState(p);
            return s.isAir() || s.getBlock() instanceof LeavesBlock || s.is(BlockTags.REPLACEABLE_BY_TREES) || s.canBeReplaced()
                    || s.is(SporosFlora.MYCEL_VINE.get()) || s.is(SporosFlora.SHELF_FUNGUS.get());
        }

        boolean solidGround(BlockPos p) {
            BlockState s = world.getBlockState(p);
            return SporosFlora.SOIL.test(s) || s.is(BlockTags.BASE_STONE_OVERWORLD) || s.is(BlockTags.SAND) || s.is(net.minecraft.world.level.block.Blocks.GRAVEL)
                    || s.is(com.space_beyond.dim.sporos.SporosBlocks.SPOROS_STONE.get());
        }

        void log(BlockPos p, Direction.Axis axis) {
            if (p.getY() < 1 || p.getY() > 254) return;
            if (!replaceable(p) && !logs.contains(p)) return;
            world.setBlock(p, SporosFlora.HYPHE_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), 2);
            logs.add(p.immutable());
        }

        /** Stammblock, der auch Boden/Wasser verdraengt (Wurzeln). */
        void root(BlockPos p, Direction.Axis axis) {
            if (p.getY() < 1) return;
            BlockState s = world.getBlockState(p);
            if (!replaceable(p) && s.getFluidState().isEmpty() && !solidGround(p) && !logs.contains(p)) return;
            world.setBlock(p, SporosFlora.HYPHE_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), 2);
            logs.add(p.immutable());
        }

        void leaf(BlockPos p) {
            if (p.getY() < 1 || p.getY() > 255) return;
            leaves.add(p.immutable());
        }

        /** Laubellipsoid um {@code c}; {@code cut} = Anteil der Randbloecke, der weggelassen wird (0 = glatt). */
        void blob(BlockPos c, double rxz, double ry, double cut) {
            int ixz = (int) Math.ceil(rxz), iy = (int) Math.ceil(ry);
            for (int dy = -iy; dy <= iy; dy++) for (int dx = -ixz; dx <= ixz; dx++) for (int dz = -ixz; dz <= ixz; dz++) {
                double d = (dx * dx + dz * dz) / (rxz * rxz) + (dy * dy) / (ry * ry);
                if (d > 1.0D) continue;
                if (d > 0.55D && rand.nextDouble() < cut) continue;
                leaf(c.offset(dx, dy, dz));
            }
        }

        /** Ast von {@code from} nach {@code to} (3D-Linie), Achse je dominanter Schrittrichtung. */
        void branch(BlockPos from, BlockPos to) {
            int dx = to.getX() - from.getX(), dy = to.getY() - from.getY(), dz = to.getZ() - from.getZ();
            int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            if (steps == 0) return;
            BlockPos last = from;
            for (int i = 1; i <= steps; i++) {
                BlockPos p = new BlockPos(from.getX() + Math.round(dx * (float) i / steps), from.getY() + Math.round(dy * (float) i / steps),
                        from.getZ() + Math.round(dz * (float) i / steps));
                int ax = Math.abs(p.getX() - last.getX()), ay = Math.abs(p.getY() - last.getY()), az = Math.abs(p.getZ() - last.getZ());
                Direction.Axis axis = ax >= ay && ax >= az ? Direction.Axis.X : az >= ay ? Direction.Axis.Z : Direction.Axis.Y;
                if (Math.abs(dy) >= Math.max(Math.abs(dx), Math.abs(dz))) axis = Direction.Axis.Y;
                log(p, axis);
                last = p;
            }
        }

        /** Laub setzen (nur wo ersetzbar und kein Stamm), Abstand zum naechsten Stamm, danach Ranken. */
        void finish(double vineChance) {
            BlockState leafState = SporosFlora.HYPHE_LEAVES.get().defaultBlockState();
            for (BlockPos p : leaves) {
                if (logs.contains(p) || !replaceable(p)) continue;
                int dist = 7;
                for (BlockPos l : logs) {
                    int d = Math.abs(l.getX() - p.getX()) + Math.abs(l.getY() - p.getY()) + Math.abs(l.getZ() - p.getZ());
                    if (d < dist) dist = d;
                    if (dist <= 1) break;
                }
                if (dist > 6) continue;
                world.setBlock(p, leafState.setValue(LeavesBlock.DISTANCE, Math.max(1, dist)), 2);
            }
            if (vineChance > 0) {
                BlockState vine = SporosFlora.MYCEL_VINE.get().defaultBlockState().setValue(VineBlock.UP, true);
                for (BlockPos p : leaves) {
                    if (!world.getBlockState(p).is(SporosFlora.HYPHE_LEAVES.get()) || rand.nextDouble() > vineChance) continue;
                    int len = 2 + rand.nextInt(5);
                    for (int i = 1; i <= len; i++) {
                        BlockPos v = p.below(i);
                        if (!world.getBlockState(v).isAir()) break;
                        world.setBlock(v, vine, 2);
                    }
                }
            }
            for (BlockPos p : shelves) {
                BlockState s = world.getBlockState(p);
                if (!s.isAir()) continue;
                for (Direction d : Direction.Plane.HORIZONTAL) {
                    if (logs.contains(p.relative(d))) {
                        world.setBlock(p, SporosFlora.SHELF_FUNGUS.get().defaultBlockState().setValue(BlockShelfFungus.FACING, d.getOpposite()), 2);
                        break;
                    }
                }
            }
        }

        /** Pilzregale an zufaelligen Stammseiten zwischen y1 und y2. */
        void shelves(BlockPos base, int y1, int y2, int count, int trunkSize) {
            for (int i = 0; i < count; i++) {
                int y = y1 + rand.nextInt(Math.max(1, y2 - y1));
                Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(rand.nextLong()));
                int ox = d == Direction.EAST ? trunkSize : d == Direction.WEST ? -1 : rand.nextInt(trunkSize);
                int oz = d == Direction.SOUTH ? trunkSize : d == Direction.NORTH ? -1 : rand.nextInt(trunkSize);
                shelves.add(new BlockPos(base.getX() + ox, y, base.getZ() + oz));
            }
        }
    }

    /** Baum der Form {@code shape} mit Fuss auf {@code origin} (Luftblock ueber dem Boden). */
    public static boolean generate(WorldGenLevel world, Random rand, BlockPos origin, Shape shape) {
        if (origin.getY() < 2 || origin.getY() > 230) return false;
        Build b = new Build(world, rand);
        if (!b.solidGround(origin.below())) return false;
        switch (shape) {
            case GIANT -> giant(b, origin);
            case CANOPY -> canopy(b, origin);
            case SWAMP -> swamp(b, origin);
        }
        return true;
    }

    private static void giant(Build b, BlockPos o) {
        int h = 16 + b.rand.nextInt(7);
        // Platz pruefen (2x2 Stamm bis zur Spitze)
        for (int y = 1; y < h; y++) for (int dx = 0; dx < 2; dx++) for (int dz = 0; dz < 2; dz++) {
            if (!b.replaceable(o.offset(dx, y, dz))) { h = Math.max(12, y - 1); break; }
        }
        for (int y = 0; y < h; y++) for (int dx = 0; dx < 2; dx++) for (int dz = 0; dz < 2; dz++) b.log(o.offset(dx, y, dz), Direction.Axis.Y);
        // Brettwurzeln: je Seite 1-2 Bloecke nach aussen, unten breiter, einen Block in den Boden
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int i = 0; i < 2; i++) {
                int ox = d == Direction.EAST ? 2 : d == Direction.WEST ? -1 : i;
                int oz = d == Direction.SOUTH ? 2 : d == Direction.NORTH ? -1 : i;
                b.root(o.offset(ox, -1, oz), Direction.Axis.Y);
                b.root(o.offset(ox, 0, oz), Direction.Axis.Y);
                if (b.rand.nextBoolean()) b.root(o.offset(ox, 1, oz), Direction.Axis.Y);
                if (b.rand.nextInt(3) == 0) {
                    int ox2 = ox + (d == Direction.EAST ? 1 : d == Direction.WEST ? -1 : 0);
                    int oz2 = oz + (d == Direction.SOUTH ? 1 : d == Direction.NORTH ? -1 : 0);
                    b.root(o.offset(ox2, -1, oz2), d.getAxis());
                    b.root(o.offset(ox2, 0, oz2), d.getAxis());
                }
            }
        }
        // Aeste im oberen Drittel
        int n = 5 + b.rand.nextInt(3);
        for (int i = 0; i < n; i++) {
            int y = (int) (h * (0.5D + 0.4D * b.rand.nextDouble()));
            double ang = b.rand.nextDouble() * Math.PI * 2;
            int len = 4 + b.rand.nextInt(4);
            int rise = 1 + b.rand.nextInt(3);
            BlockPos from = o.offset(Math.cos(ang) > 0 ? 1 : 0, y, Math.sin(ang) > 0 ? 1 : 0);
            BlockPos to = from.offset((int) Math.round(Math.cos(ang) * len), rise, (int) Math.round(Math.sin(ang) * len));
            b.branch(from, to);
            b.blob(to, 2.0D + b.rand.nextDouble() * 1.2D, 1.8D + b.rand.nextDouble(), 0.25D);
        }
        // Kronenkuppel
        BlockPos top = o.offset(0, h - 1, 0);
        b.blob(top.offset(1, 0, 1), 4.5D + b.rand.nextDouble() * 1.5D, 3.0D, 0.3D);
        b.blob(top.offset(0, 1, 0), 3.0D, 2.0D, 0.2D);
        b.shelves(o, 2, Math.max(3, h / 2), 2 + b.rand.nextInt(3), 2);
        b.finish(0.06D);
    }

    private static void canopy(Build b, BlockPos o) {
        int h = 8 + b.rand.nextInt(6);
        for (int y = 1; y < h; y++) if (!b.replaceable(o.above(y))) { h = Math.max(6, y - 1); break; }
        for (int y = 0; y < h; y++) b.log(o.above(y), Direction.Axis.Y);
        b.root(o.below(), Direction.Axis.Y);
        int crownY = h - 2;
        int radius = 5 + b.rand.nextInt(3);
        int n = 5 + b.rand.nextInt(4);
        for (int i = 0; i < n; i++) {
            double ang = (Math.PI * 2 * i) / n + b.rand.nextDouble() * 0.6D;
            int len = radius - 1 - b.rand.nextInt(2);
            BlockPos from = o.above(crownY - b.rand.nextInt(2));
            BlockPos to = from.offset((int) Math.round(Math.cos(ang) * len), b.rand.nextInt(3), (int) Math.round(Math.sin(ang) * len));
            b.branch(from, to);
        }
        // Schirm: flache Scheibe, Rand unregelmaessig, Mitte zwei bis drei Lagen
        BlockPos c = o.above(crownY + 1);
        for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz) / radius;
            if (d + b.rand.nextDouble() * 0.3D > 1.05D) continue;
            b.leaf(c.offset(dx, 0, dz));
            if (d < 0.75D && b.rand.nextDouble() > d * 0.6D) b.leaf(c.offset(dx, 1, dz));
            if (d < 0.45D && b.rand.nextInt(3) > 0) b.leaf(c.offset(dx, 2, dz));
            if (d > 0.3D && d < 0.9D && b.rand.nextInt(4) == 0) b.leaf(c.offset(dx, -1, dz));
        }
        b.shelves(o, 2, Math.max(3, h - 3), b.rand.nextInt(3), 1);
        b.finish(0.12D);
    }

    private static void swamp(Build b, BlockPos o) {
        int h = 8 + b.rand.nextInt(5);
        BlockPos p = o;
        List<BlockPos> trunk = new ArrayList<>();
        int nextBend = 2 + b.rand.nextInt(2);
        for (int y = 0; y < h; y++) {
            if (y == nextBend && y < h - 2) {
                Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(b.rand.nextLong()));
                b.log(p.relative(d), d.getAxis());
                p = p.relative(d);
                nextBend += 2 + b.rand.nextInt(3);
            }
            b.log(p, Direction.Axis.Y);
            trunk.add(p);
            p = p.above();
        }
        // Stelzwurzeln: ab Hoehe 2-3 schraeg nach aussen und unten bis in den Boden (durch Wasser)
        int roots = 3 + b.rand.nextInt(3);
        for (int i = 0; i < roots; i++) {
            double ang = (Math.PI * 2 * i) / roots + b.rand.nextDouble() * 0.8D;
            int sy = 2 + b.rand.nextInt(2);
            BlockPos from = trunk.get(Math.min(sy, trunk.size() - 1));
            BlockPos cur = from;
            for (int step = 0; step < 7; step++) {
                BlockPos next = new BlockPos(from.getX() + (int) Math.round(Math.cos(ang) * (step + 1)), from.getY() - step - 1,
                        from.getZ() + (int) Math.round(Math.sin(ang) * (step + 1)));
                if (next.getX() != cur.getX() || next.getZ() != cur.getZ()) {
                    // Zwischenblock waagerecht, dann senkrecht
                    BlockPos side = new BlockPos(next.getX(), cur.getY(), next.getZ());
                    b.root(side, Math.abs(next.getX() - cur.getX()) >= Math.abs(next.getZ() - cur.getZ()) ? Direction.Axis.X : Direction.Axis.Z);
                }
                b.root(next, Direction.Axis.Y);
                cur = next;
                if (b.solidGround(next.below()) && b.world.getBlockState(next.below()).getFluidState().isEmpty()) {
                    b.root(next.below(), Direction.Axis.Y);
                    break;
                }
            }
        }
        // zerzauste Krone: Hauptballen plus 2-3 Seitenballen an kurzen Aesten
        BlockPos top = trunk.get(trunk.size() - 1);
        b.blob(top, 2.6D + b.rand.nextDouble(), 2.0D, 0.45D);
        int side = 2 + b.rand.nextInt(2);
        for (int i = 0; i < side; i++) {
            double ang = b.rand.nextDouble() * Math.PI * 2;
            BlockPos from = trunk.get(trunk.size() - 2 - b.rand.nextInt(Math.min(3, trunk.size() - 2)));
            BlockPos to = from.offset((int) Math.round(Math.cos(ang) * 3), b.rand.nextInt(2), (int) Math.round(Math.sin(ang) * 3));
            b.branch(from, to);
            b.blob(to, 1.8D, 1.5D, 0.4D);
        }
        b.shelves(o, 3, Math.max(4, h - 2), 1 + b.rand.nextInt(3), 1);
        b.finish(0.15D);
    }

    // ── Totholz ──

    /** Umgestuerzter Stamm (3-7 lang) oder Stumpf aus Totholz, mit Moos, Pilzregalen und Leuchtpilzen. */
    public static boolean generateDeadwood(WorldGenLevel world, Random rand, BlockPos origin) {
        Build b = new Build(world, rand);
        if (!b.solidGround(origin.below()) || !b.replaceable(origin)) return false;
        BlockState dead = SporosFlora.DEAD_HYPHE_LOG.get().defaultBlockState();
        List<BlockPos> placed = new ArrayList<>();
        if (rand.nextInt(3) == 0) {
            int h = 1 + rand.nextInt(3);
            for (int y = 0; y < h; y++) {
                if (!b.replaceable(origin.above(y))) break;
                world.setBlock(origin.above(y), dead, 2);
                placed.add(origin.above(y));
            }
        } else {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(net.minecraft.util.RandomSource.create(rand.nextLong()));
            int len = 3 + rand.nextInt(5);
            int y = origin.getY();
            for (int i = 0; i < len; i++) {
                BlockPos col = origin.relative(d, i);
                int ground = world.getHeight(Heightmap.Types.OCEAN_FLOOR, col.getX(), col.getZ());
                if (Math.abs(ground - y) > 1 && i > 0) break;
                if (i > 0) y = ground;
                BlockPos p = new BlockPos(col.getX(), y, col.getZ());
                if (!b.replaceable(p)) break;
                world.setBlock(p, dead.setValue(RotatedPillarBlock.AXIS, d.getAxis()), 2);
                placed.add(p);
            }
        }
        if (placed.isEmpty()) return false;
        for (BlockPos p : placed) {
            BlockPos up = p.above();
            if (world.getBlockState(up).isAir()) {
                int r = rand.nextInt(10);
                if (r < 2) world.setBlock(up, SporosFlora.GLOW_CAP.get().defaultBlockState(), 2);
                else if (r < 5) world.setBlock(up, SporosFlora.MOSS_CUSHION.get().defaultBlockState(), 2);
            }
            for (Direction d : Direction.Plane.HORIZONTAL) {
                BlockPos s = p.relative(d);
                if (rand.nextInt(5) == 0 && world.getBlockState(s).isAir()) {
                    world.setBlock(s, SporosFlora.SHELF_FUNGUS.get().defaultBlockState().setValue(BlockShelfFungus.FACING, d), 2);
                }
            }
        }
        return true;
    }

    // ── Riesenpilz ──

    /** Riesenpilz: Stiel 4-8 hoch, Hut als Scheibe (Radius 2-3) mit Kuppe, Leuchtpilze obenauf. */
    public static boolean generateGiantFungus(WorldGenLevel world, Random rand, BlockPos origin) {
        Build b = new Build(world, rand);
        if (!b.solidGround(origin.below()) || !b.replaceable(origin)) return false;
        int h = 4 + rand.nextInt(5);
        for (int y = 0; y < h; y++) if (!b.replaceable(origin.above(y))) { h = Math.max(3, y); break; }
        BlockState stem = SporosFlora.FUNGAL_STEM.get().defaultBlockState();
        BlockState cap = SporosFlora.FUNGAL_CAP.get().defaultBlockState();
        for (int y = 0; y < h; y++) world.setBlock(origin.above(y), stem, 2);
        int r = 2 + rand.nextInt(2);
        BlockPos top = origin.above(h);
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > r + 0.3D) continue;
            BlockPos p = top.offset(dx, 0, dz);
            if (b.replaceable(p)) world.setBlock(p, cap, 2);
            if (d <= r - 1.2D) {
                BlockPos q = p.above();
                if (b.replaceable(q)) world.setBlock(q, cap, 2);
            }
            if (d > r - 0.8D && rand.nextInt(3) == 0) {
                BlockPos q = p.below();
                if (b.replaceable(q) && q.getY() > origin.getY() + 1) world.setBlock(q, cap, 2);
            }
        }
        for (int i = 0; i < 1 + rand.nextInt(3); i++) {
            BlockPos q = top.offset(rand.nextInt(2 * r + 1) - r, 1, rand.nextInt(2 * r + 1) - r);
            if (world.getBlockState(q).isAir() && world.getBlockState(q.below()).is(SporosFlora.FUNGAL_CAP.get()))
                world.setBlock(q, SporosFlora.GLOW_CAP.get().defaultBlockState(), 2);
            else if (world.getBlockState(q.above()).isAir() && world.getBlockState(q).is(SporosFlora.FUNGAL_CAP.get()))
                world.setBlock(q.above(), SporosFlora.GLOW_CAP.get().defaultBlockState(), 2);
        }
        return true;
    }
}
