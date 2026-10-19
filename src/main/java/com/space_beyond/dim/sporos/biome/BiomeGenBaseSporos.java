package com.space_beyond.dim.sporos.biome;

import java.util.Random;

import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_m.dim.CelestialWorld;
import com.space_m.dim.legacy.Legacy;
import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Biome des Planeten Sporos (eigene Erweiterung). Wie bei den portierten Koerpern lebt hier nur, was die Generierung
 * im Code braucht (Rauschhoehe, Oberflaeche); Farben (gruenlich-gelber Dunst), Sporenpartikel, Spawns und der
 * Vanilla-Bewuchs stehen im Biom-JSON {@code data/space_m/worldgen/biome/sporos_*.json}.
 *
 * <p>Oberflaeche ({@link #genTerrainBlocks}, Aufbau wie 1.7.10 {@code genBiomeTerrain}, Pufferindex mit dem
 * Vanilla-x/z-Tausch): oberster Block je Biom und Rauschwert ({@link #surfaceTop}), darunter 3-4 Schichten
 * {@code sporos_silt}, dann {@code sporos_stone}. Unter Wasser {@link #underwaterTop} (Schlamm/Ton/Humus).
 */
public abstract class BiomeGenBaseSporos extends BiomeGenBaseCelestial {

    public static final BiomeGenBaseSporos overgrownPlains = new BiomeGenSporosOvergrownPlains(BiomeGenSporosOvergrownPlains.ID);
    public static final BiomeGenBaseSporos mossForest = new BiomeGenSporosMossForest(BiomeGenSporosMossForest.ID);
    public static final BiomeGenBaseSporos swamp = new BiomeGenSporosSwamp(BiomeGenSporosSwamp.ID);
    public static final BiomeGenBaseSporos river = new BiomeGenSporosRiver(BiomeGenSporosRiver.ID);
    // Erweiterung Flora/Biome (R2b): Pilzhain, Nebelhochland, Sporenfeld, Kueste, See
    public static final BiomeGenBaseSporos fungalGrove = new BiomeGenSporosFungalGrove(BiomeGenSporosFungalGrove.ID);
    public static final BiomeGenBaseSporos highland = new BiomeGenSporosHighland(BiomeGenSporosHighland.ID);
    public static final BiomeGenBaseSporos sporeField = new BiomeGenSporosSporeField(BiomeGenSporosSporeField.ID);
    public static final BiomeGenBaseSporos coast = new BiomeGenSporosCoast(BiomeGenSporosCoast.ID);
    public static final BiomeGenBaseSporos lake = new BiomeGenSporosLake(BiomeGenSporosLake.ID);

    /** Alle Sporos-Biome (Reihenfolge der IDs 140-148; Test- und Registrierungsliste). */
    public static final BiomeGenBaseSporos[] ALL = { overgrownPlains, mossForest, swamp, river, fungalGrove, highland, sporeField, coast, lake };

    /** Meeresspiegel des Sporos-Generators. */
    public static final int SEA_LEVEL = 63;

    /**
     * Sporenfaktor des Bioms (Runde 4, {@code SporeExposure}): Vielfaches der Sporendichte des Koerpers in der Luft
     * dieses Bioms. Sporenfeld 2,0, Pilzhain 1,4, Sumpf 1,0, Moosdickicht 0,8, Ebene 0,6, Hochland/Kueste 0,5,
     * See/Fluss 0,3 (ueber dem Wasser kaum Sporen).
     */
    protected float sporeFactor = 1.0F;

    public float sporeFactor() {
        return sporeFactor;
    }

    /** Sporenfaktor am Ort: Sporos-Biom -> sein Faktor, jedes andere Biom (z.B. per Befehl verseuchte Welt) 1. */
    public static float sporeFactorAt(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        var key = level.getBiome(pos).unwrapKey().orElse(null);
        if (key == null) return 1.0F;
        for (BiomeGenBaseSporos b : ALL) if (b.key.equals(key)) return b.sporeFactor;
        return 1.0F;
    }

    protected BiomeGenBaseSporos(int id, String path) {
        super(id, path);
        this.setFillerBlock(() -> SporosBlocks.SPOROS_SILT.get().defaultBlockState());
    }

    /** Klassen laden (Biom-IDs anmelden). */
    public static void init() {
    }

    /** Oberster Block an Land; {@code noise} = Steinrauschen der Spalte (etwa -4..4). */
    protected abstract BlockState surfaceTop(double noise);

    /** Oberster Block unter Wasser. */
    protected BlockState underwaterTop(double noise) {
        return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
    }

    @Override
    public void genTerrainBlocks(CelestialWorld world, Random rand, BlockState[] blocks, int x, int z, double noise) {
        BlockState stone = SporosBlocks.SPOROS_STONE.get().defaultBlockState();
        BlockState filler = this.fillerBlock();
        BlockState fill = filler;
        int remaining = -1;
        int depth = (int) (noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int i1 = x & 15;
        int j1 = z & 15;
        int k1 = blocks.length / 256;
        boolean wet = false;

        for (int y = 255; y >= 0; --y) {
            int index = (j1 * 16 + i1) * k1 + y;

            if (y <= rand.nextInt(5)) {
                blocks[index] = Blocks.BEDROCK.defaultBlockState();
                continue;
            }

            BlockState current = blocks[index];

            if (current == null || current.isAir()) {
                remaining = -1;
                wet = false;
                continue;
            }

            if (!current.getFluidState().isEmpty()) {
                remaining = -1;
                wet = true;
                continue;
            }

            if (!Legacy.is(current, stone)) continue;

            if (remaining == -1) {
                if (depth <= 0) {
                    fill = stone;
                    remaining = 0;
                    continue;
                }

                fill = filler;
                remaining = depth;
                blocks[index] = wet || y < SEA_LEVEL - 1 ? underwaterTop(noise) : surfaceTop(noise);
            } else if (remaining > 0) {
                --remaining;
                blocks[index] = fill;
            }
        }
    }
}
