package com.space_beyond.dim.sporos.city;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.SporosCities.City;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Staedtegenerator von Sporos (Runde 3, eigener Code). Laeuft im Chunk-Puffer nach Gelaende und Hoehlen:
 * <ol>
 *   <li><b>Gelaende:</b> innerhalb von {@link CityLayout#URBAN} eben auf Strassenniveau (geflutete Zellen 2 tiefer,
 *       Wasser bis Strassenniveau), danach weicher Uebergang zur natuerlichen Hoehe bis zum Stadtrand. Die obersten
 *       Schichten werden geschlossen (Hoehlen brechen nicht durch die Strasse).</li>
 *   <li><b>Zellen:</b> jede Rasterzelle, die den Chunk schneidet, wird geplant ({@link CellPlan}) und ihr Ausschnitt
 *       gemalt ({@link CityPainter}).</li>
 * </ol>
 * Behaelter-Inhalte (Loot-Tables) setzt {@link CityLoot} in der Dekorationsphase.
 */
public final class SporosCityGenerator implements SporosCities.CityGenerator {

    public static final SporosCityGenerator INSTANCE = new SporosCityGenerator();

    private SporosCityGenerator() {}

    @Override
    public void generate(CelestialWorld world, City city, int chunkX, int chunkZ, BlockState[] blocks, BiomeGenBaseCelestial[] biomes) {
        CityLayout layout = CityLayout.of(city);
        CityCanvas canvas = new CityCanvas(blocks, chunkX, chunkZ);

        int gx0 = layout.cellX(canvas.minX), gx1 = layout.cellX(canvas.maxX());
        int gz0 = layout.cellZ(canvas.minZ), gz1 = layout.cellZ(canvas.maxZ());
        CellPlan[][] cells = new CellPlan[gx1 - gx0 + 1][gz1 - gz0 + 1];
        for (int gx = gx0; gx <= gx1; gx++) for (int gz = gz0; gz <= gz1; gz++) cells[gx - gx0][gz - gz0] = layout.cell(world, gx, gz);

        terrain(canvas, layout, cells, gx0, gz0);
        for (CellPlan[] row : cells) for (CellPlan plan : row) CityPainter.paintCell(canvas, plan);
    }

    private static void terrain(CityCanvas c, CityLayout layout, CellPlan[][] cells, int gx0, int gz0) {
        CityPalette p = CityPalette.get();
        int sea = BiomeGenBaseSporos.SEA_LEVEL - 1;
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = c.minX; x <= c.maxX(); x++) {
            for (int z = c.minZ; z <= c.maxZ(); z++) {
                double e = layout.edgeJittered(x, z);
                double w = CityRandom.smoothstep(CityLayout.URBAN, 1.0D, e);
                if (w >= 0.995D) continue;
                CellPlan cell = cells[layout.cellX(x) - gx0][layout.cellZ(z) - gz0];
                boolean wet = cell.wet && w <= 0.0D;

                int nat = 0;
                for (int y = CityCanvas.HEIGHT - 1; y > 0; y--) {
                    BlockState s = c.get(x, y, z);
                    if (s != null && !s.isAir() && s.getFluidState().isEmpty()) {
                        nat = y;
                        break;
                    }
                }
                int g0 = wet ? layout.baseY - 2 : layout.baseY;
                int target = (int) Math.round(g0 + (nat - g0) * w);

                // ueber dem Ziel freiraeumen (natuerliches Fluss-/Meerwasser am Rand bleibt)
                for (int y = CityCanvas.HEIGHT - 1; y > target; y--) {
                    BlockState s = c.get(x, y, z);
                    if (wet && y <= layout.baseY) {
                        c.set(x, y, z, p.water);
                    } else if (s != null && !s.isAir()) {
                        if (w > 0 && y <= sea && !s.getFluidState().isEmpty()) continue;
                        c.set(x, y, z, air);
                    }
                }
                // auffuellen und oberste Schichten schliessen
                int closeDepth = w < 0.5 ? 6 : 2;
                for (int y = target; y > 1 && (y > nat || y > target - closeDepth); y--) {
                    BlockState s = c.get(x, y, z);
                    boolean open = s == null || s.isAir() || !s.getFluidState().isEmpty();
                    if (y == target) {
                        if (w < 0.7 || open) c.set(x, y, z, wet ? p.mud : p.grassBlock);
                    } else if (open || y > nat) {
                        c.set(x, y, z, target - y <= 3 ? p.silt : p.stone);
                    }
                }
            }
        }
    }
}
