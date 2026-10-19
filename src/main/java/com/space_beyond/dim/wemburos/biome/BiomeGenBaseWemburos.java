package com.space_beyond.dim.wemburos.biome;

import java.util.Random;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.legacy.Legacy;
import com.space_beyond.dim.wemburos.WemburosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Biome des Planeten Wemburos (eigene Erweiterung). Trockener Gesteinsplanet ohne Meere: der Generator fuellt unter
 * dem Meeresspiegel Luft, tiefe Becken bleiben also leer. Oberflaeche wie Sporos ({@link #surfaceTop} je Biom und
 * Rauschwert, darunter {@code wemburos_regolith}, dann {@code wemburos_stone}); Farben, Partikel und Spawns stehen
 * im Biom-JSON {@code data/space_m/worldgen/biome/wemburos_*.json}. IDs 150-159:
 * <ul>
 *   <li>150-154: Violette Ebene, Onyx-Badlands, Goldhochland, Kristallfelder, Riss.</li>
 *   <li>155 <b>Duskwood-Wald</b>: violettes Moos, Duskwood-Baeume (Schirmkronen und schlanke Turmbaeume) mit
 *       leuchtendem Laub und goldenen Lichtfruechten, Blueten.</li>
 *   <li>156 <b>Onyx-Stachelfeld</b>: bis zu 40 Bloecke hohe Felsnadeln aus Onyx und Gestein mit Goldbaendern.</li>
 *   <li>157 <b>Goldduenen</b>: wellige Duenen aus Goldsand, halb verschuettete Riesenrippen, Rohgold, Totholz.</li>
 *   <li>158 <b>Chorus-Oednis</b>: Endstein-Flaechen mit Chorus-Hainen und zerfallenen Purpursaeulen.</li>
 *   <li>159 <b>Caldera</b>: Schwarzstein und Magma, Basaltsaeulen, kleine Lavabecken, vergoldeter Schwarzstein, Asche.</li>
 * </ul>
 */
public abstract class BiomeGenBaseWemburos extends BiomeGenBaseCelestial {

    public static final BiomeGenBaseWemburos violetPlains = new Plains(150);
    public static final BiomeGenBaseWemburos onyxBadlands = new Badlands(151);
    public static final BiomeGenBaseWemburos gildedHighlands = new Highlands(152);
    public static final BiomeGenBaseWemburos crystalFields = new CrystalFields(153);
    public static final BiomeGenBaseWemburos rift = new Rift(154);
    public static final BiomeGenBaseWemburos duskwoodForest = new DuskwoodForest(155);
    public static final BiomeGenBaseWemburos onyxSpires = new OnyxSpires(156);
    public static final BiomeGenBaseWemburos goldenDunes = new GoldenDunes(157);
    public static final BiomeGenBaseWemburos chorusWastes = new ChorusWastes(158);
    public static final BiomeGenBaseWemburos caldera = new Caldera(159);

    public static final BiomeGenBaseWemburos[] ALL = { violetPlains, onyxBadlands, gildedHighlands, crystalFields, rift,
            duskwoodForest, onyxSpires, goldenDunes, chorusWastes, caldera };

    public static final int SEA_LEVEL = 63;

    protected BiomeGenBaseWemburos(int id, String path, String name, Height height) {
        super(id, path);
        this.setBiomeName(name);
        this.setHeight(height);
        this.setDisableRain();
        this.setTemperatureRainfall(1.2F, 0.0F);
        this.setFillerBlock(() -> WemburosBlocks.WEMBUROS_REGOLITH.get().defaultBlockState());
    }

    public static void init() {
    }

    protected static BlockState stone() {
        return WemburosBlocks.WEMBUROS_STONE.get().defaultBlockState();
    }

    protected static BlockState regolith() {
        return WemburosBlocks.WEMBUROS_REGOLITH.get().defaultBlockState();
    }

    protected static BlockState block(net.minecraft.world.level.block.Block b) {
        return b.defaultBlockState();
    }

    protected static BlockState onyx() {
        return WemburosBlocks.WEMBUROS_ONYX.get().defaultBlockState();
    }

    /** Oberster Block; {@code noise} = Steinrauschen der Spalte (etwa -4..4). */
    protected abstract BlockState surfaceTop(double noise);

    /** Unterboden (die naechsten Schichten). */
    protected BlockState subsurface(double noise) {
        return regolith();
    }

    @Override
    public void genTerrainBlocks(CelestialWorld world, Random rand, BlockState[] blocks, int x, int z, double noise) {
        BlockState stone = stone();
        BlockState fill = subsurface(noise);
        int remaining = -1;
        int depth = (int) (noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int i1 = x & 15, j1 = z & 15;
        int k1 = blocks.length / 256;
        for (int y = 255; y >= 0; --y) {
            int index = (j1 * 16 + i1) * k1 + y;
            if (y <= rand.nextInt(5)) {
                blocks[index] = Blocks.BEDROCK.defaultBlockState();
                continue;
            }
            BlockState current = blocks[index];
            if (current == null || current.isAir()) {
                remaining = -1;
                continue;
            }
            if (!Legacy.is(current, stone)) continue;
            if (remaining == -1) {
                if (depth <= 0) {
                    remaining = 0;
                    continue;
                }
                remaining = depth;
                blocks[index] = surfaceTop(noise);
            } else if (remaining > 0) {
                --remaining;
                blocks[index] = fill;
            }
        }
    }

    /** Violette Ebene: sanfte Huegel aus Regolith, einzelne Steinflaechen. */
    static final class Plains extends BiomeGenBaseWemburos {
        Plains(int id) {
            super(id, "wemburos_violet_plains", "Violet Plains", new Height(0.125F, 0.06F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 2.4D ? stone() : regolith();
        }
    }

    /** Onyx-Badlands: zerklueftete schwarze Tafelberge. */
    static final class Badlands extends BiomeGenBaseWemburos {
        Badlands(int id) {
            super(id, "wemburos_onyx_badlands", "Onyx Badlands", new Height(0.45F, 0.5F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > -1.5D ? onyx() : regolith();
        }

        @Override
        protected BlockState subsurface(double noise) {
            return onyx();
        }
    }

    /** Goldhochland: hohe violette Berge, blanker Fels, Goldspitzen (Generator). */
    static final class Highlands extends BiomeGenBaseWemburos {
        Highlands(int id) {
            super(id, "wemburos_gilded_highlands", "Gilded Highlands", new Height(1.1F, 0.55F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 0.5D ? stone() : noise < -2.0D ? onyx() : regolith();
        }

        @Override
        protected BlockState subsurface(double noise) {
            return stone();
        }
    }

    /** Kristallfelder: flache Senken voller leuchtender Kristalldrusen (Generator). */
    static final class CrystalFields extends BiomeGenBaseWemburos {
        CrystalFields(int id) {
            super(id, "wemburos_crystal_fields", "Crystal Fields", new Height(0.2F, 0.15F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 1.6D ? stone() : regolith();
        }
    }

    /** Der Riss: tiefes Becken mit Onyxboden, Kristallen und schwebenden Brocken (Generator). */
    static final class Rift extends BiomeGenBaseWemburos {
        Rift(int id) {
            super(id, "wemburos_rift", "The Rift", new Height(-0.6F, 0.15F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 2.0D ? regolith() : onyx();
        }

        @Override
        protected BlockState subsurface(double noise) {
            return onyx();
        }
    }

    /** Duskwood-Wald: Waldboden aus violettem Moos (Baeume, Blueten: Generator). */
    static final class DuskwoodForest extends BiomeGenBaseWemburos {
        DuskwoodForest(int id) {
            super(id, "wemburos_duskwood_forest", "Duskwood Forest", new Height(0.2F, 0.18F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 2.6D ? regolith() : WemburosBlocks.VIOLET_MOSS.get().defaultBlockState();
        }
    }

    /** Onyx-Stachelfeld: welliges Felsland, Nadeln aus dem Generator. */
    static final class OnyxSpires extends BiomeGenBaseWemburos {
        OnyxSpires(int id) {
            super(id, "wemburos_onyx_spires", "Onyx Spires", new Height(0.3F, 0.22F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 1.0D ? stone() : noise < -1.0D ? onyx() : regolith();
        }

        @Override
        protected BlockState subsurface(double noise) {
            return stone();
        }
    }

    /** Goldduenen: tiefer Goldsand in langen Wellen. */
    static final class GoldenDunes extends BiomeGenBaseWemburos {
        GoldenDunes(int id) {
            super(id, "wemburos_golden_dunes", "Golden Dunes", new Height(0.15F, 0.3F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return WemburosBlocks.GILDED_SAND.get().defaultBlockState();
        }

        @Override
        protected BlockState subsurface(double noise) {
            return WemburosBlocks.GILDED_SAND.get().defaultBlockState();
        }
    }

    /** Chorus-Oednis: flache Endstein-Ebene, Regolith in Mulden. */
    static final class ChorusWastes extends BiomeGenBaseWemburos {
        ChorusWastes(int id) {
            super(id, "wemburos_chorus_wastes", "Chorus Wastes", new Height(0.1F, 0.08F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            return noise > 2.2D ? regolith() : block(Blocks.END_STONE);
        }

        @Override
        protected BlockState subsurface(double noise) {
            return block(Blocks.END_STONE);
        }
    }

    /** Caldera: vulkanisches Hochbecken aus Schwarzstein, Basalt und Magma. */
    static final class Caldera extends BiomeGenBaseWemburos {
        Caldera(int id) {
            super(id, "wemburos_caldera", "Sovereign Caldera", new Height(0.35F, 0.4F));
        }

        @Override
        protected BlockState surfaceTop(double noise) {
            if (noise > 2.0D) return block(Blocks.MAGMA_BLOCK);
            if (noise > 0.4D) return block(Blocks.BLACKSTONE);
            if (noise < -2.0D) return block(Blocks.BASALT);
            return block(Blocks.SMOOTH_BASALT);
        }

        @Override
        protected BlockState subsurface(double noise) {
            return block(Blocks.BLACKSTONE);
        }
    }
}
