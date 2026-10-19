package com.space_beyond.dim.sporos.city;

import org.jetbrains.annotations.Nullable;

import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.city.SporosCities.City;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Suche nach Staedten und Landmarken fuer {@code /space locate_city} / {@code locate_landmark} (Testhilfe). Reine Planungsrechnung, generiert nichts. */
public final class CityLocator {

    private CityLocator() {}

    /** Naechste Stadtmitte zu (x, z) im Umkreis von {@code rangeDistricts} Bezirken, oder null. */
    @Nullable
    public static City nearest(long seed, int x, int z, int rangeDistricts) {
        int dx0 = Math.floorDiv(x >> 4, SporosCities.DISTRICT_CHUNKS), dz0 = Math.floorDiv(z >> 4, SporosCities.DISTRICT_CHUNKS);
        City best = null;
        double bestD = Double.MAX_VALUE;
        for (int dx = dx0 - rangeDistricts; dx <= dx0 + rangeDistricts; dx++) {
            for (int dz = dz0 - rangeDistricts; dz <= dz0 + rangeDistricts; dz++) {
                City c = SporosCities.cityOfDistrict(seed, dx, dz);
                if (c == null) continue;
                double d = Math.hypot(c.originX() - x, c.originZ() - z);
                if (d < bestD) {
                    bestD = d;
                    best = c;
                }
            }
        }
        return best;
    }

    public static CelestialWorld world() {
        return CelestialGenerators.state(SporosBlocks.BODY).chunkManager;
    }

    public static int baseY(City city) {
        return CityLayout.of(city).baseY;
    }

    /** Freie Stelle auf der Strasse nahe der Stadtmitte (Fuesse auf Strassenniveau + 1). */
    public static BlockPos street(City city) {
        CityLayout layout = CityLayout.of(city);
        CellPlan plan = layout.cell(world(), 0, 0);
        int y = plan.groundY() + 1;
        int[][] candidates = {{4, 18}, {4, 24}, {18, 4}, {24, 4}, {4, 4}};
        for (int[] c : candidates) {
            int x = plan.minX + c[0], z = plan.minZ + c[1];
            if (free(plan, x, z)) return new BlockPos(x, y, z);
        }
        return new BlockPos(city.originX(), y, city.originZ());
    }

    private static boolean free(CellPlan plan, int x, int z) {
        for (CellPlan.Tree t : plan.trees) if (Math.abs(t.x() - x) <= t.radius() + 1 && Math.abs(t.z() - z) <= t.radius() + 1) return false;
        for (CellPlan.Car c : plan.cars) if (x >= c.x() - 1 && x <= c.x() + 6 && z >= c.z() - 1 && z <= c.z() + 6) return false;
        for (CellPlan.Log l : plan.logs) if (x >= l.x() - 1 && x <= l.x() + l.len() && z >= l.z() - 1 && z <= l.z() + l.len()) return false;
        return true;
    }

    /** Stelle in einem Stockwerk des naechsten Hochhauses (oder Wohnblocks) zur Stadtmitte, oder null. */
    @Nullable
    public static BlockPos inside(City city) {
        CityLayout layout = CityLayout.of(city);
        CelestialWorld world = world();
        BlockPos best = null;
        int bestScore = Integer.MAX_VALUE;
        for (int r = 0; r <= 4; r++) {
            for (int gx = -r; gx <= r; gx++) {
                for (int gz = -r; gz <= r; gz++) {
                    if (Math.max(Math.abs(gx), Math.abs(gz)) != r) continue;
                    Building b = layout.cell(world, gx, gz).building;
                    if (b == null || (b.kind != Building.Kind.TOWER && b.kind != Building.Kind.APARTMENT)) continue;
                    BlockPos p = interiorSpot(b);
                    if (p == null) continue;
                    int score = r * 10 + (b.kind == Building.Kind.TOWER ? 0 : 5);
                    if (score < bestScore) {
                        bestScore = score;
                        best = p;
                    }
                }
            }
            if (best != null) return best;
        }
        return best;
    }

    /**
     * Naechste Landmarke ({@code type}: CHECKPOINT/CAMP/TRIAGE/NEST, oder null = Konvoi) der Stadt zu (x, z):
     * Stelle davor auf dem Gehweg bzw. der Strasse (Fuesse auf Bodenniveau + 1), oder null, wenn die Stadt keine hat.
     */
    @Nullable
    public static BlockPos landmark(City city, @Nullable CellPlan.LotType type, int x, int z) {
        CityLayout layout = CityLayout.of(city);
        CelestialWorld world = world();
        int cells = city.radiusChunks() * 16 / CityLayout.CELL + 1;
        BlockPos best = null;
        double bestD = Double.MAX_VALUE;
        for (int gx = -cells; gx <= cells; gx++) {
            for (int gz = -cells; gz <= cells; gz++) {
                CellPlan plan = layout.cell(world, gx, gz);
                if (type == null ? plan.convoy == null : plan.type != type) continue;
                BlockPos p;
                if (type == null) {
                    p = plan.convoy.north() ? new BlockPos(plan.minX + plan.convoy.along() - 3, plan.groundY() + 1, plan.minZ + 3)
                            : new BlockPos(plan.minX + 3, plan.groundY() + 1, plan.minZ + plan.convoy.along() - 3);
                } else if (type == CellPlan.LotType.CHECKPOINT) {
                    p = plan.checkpoint == net.minecraft.core.Direction.NORTH ? new BlockPos(plan.minX + 12, plan.groundY() + 1, plan.minZ + 3)
                            : new BlockPos(plan.minX + 3, plan.groundY() + 1, plan.minZ + 12);
                } else {
                    p = new BlockPos(plan.minX + 19, plan.groundY() + 1, plan.minZ + CityLayout.LOT_MIN - 1);
                }
                double d = Math.hypot(p.getX() - x, p.getZ() - z);
                if (d < bestD) {
                    bestD = d;
                    best = p;
                }
            }
        }
        return best;
    }

    @Nullable
    private static BlockPos interiorSpot(Building b) {
        for (int f = Math.min(2, b.floors - 1); f >= 1; f--) {
            int y = b.floorY(f) + 1;
            for (int dx = 2; dx <= 6; dx++) {
                for (int dz = 2; dz <= 6; dz++) {
                    int x = b.midX + dx, z = b.midZ + dz;
                    if (x >= b.x1 || z >= b.z1) continue;
                    BlockState at = b.blockAt(x, y, z), head = b.blockAt(x, y + 1, z), floor = b.blockAt(x, y - 1, z);
                    if (at != null && at.isAir() && head != null && head.isAir() && floor != null && !floor.isAir() && floor.getFluidState().isEmpty()) {
                        return new BlockPos(x, y, z);
                    }
                }
            }
        }
        return null;
    }
}
