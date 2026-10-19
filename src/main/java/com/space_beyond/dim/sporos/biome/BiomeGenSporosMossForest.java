package com.space_beyond.dim.sporos.biome;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Moosdickicht ({@code space_beyond:sporos_moss_forest}): huegeliger, dichter Wald aus Dschungel- und Dunkeleichen mit
 * Ranken, Riesenpilzen und dichtem Unterholz; Boden fast ueberall Moos, dazwischen Podsol und Gras.
 */
public class BiomeGenSporosMossForest extends BiomeGenBaseSporos {

    public static final int ID = 141;

    public BiomeGenSporosMossForest(int id) {
        super(id, "sporos_moss_forest");
        this.setBiomeName("Moss Thicket");
        this.setHeight(new Height(0.25F, 0.3F));
        this.setTemperatureRainfall(0.75F, 0.95F);
        this.setTopBlock(Blocks.MOSS_BLOCK::defaultBlockState);
        this.sporeFactor = 0.8F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 1.5D) return Blocks.PODZOL.defaultBlockState();
        if (noise < -1.8D) return Blocks.GRASS_BLOCK.defaultBlockState();
        return Blocks.MOSS_BLOCK.defaultBlockState();
    }
}
