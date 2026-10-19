package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Kueste ({@code space_beyond:sporos_coast}): flacher Uferstreifen um die Seen ({@code GenLayerSporosShore}), Sand,
 * Kies und Humus, Sumpfgras und Schilf, einzelne Schirmbaeume; unter Wasser Sand und Ton.
 */
public class BiomeGenSporosCoast extends BiomeGenBaseSporos {

    public static final int ID = 147;

    public BiomeGenSporosCoast(int id) {
        super(id, "sporos_coast");
        this.setBiomeName("Spore Coast");
        this.setHeight(new Height(-0.05F, 0.03F));
        this.setTemperatureRainfall(0.8F, 0.85F);
        this.setTopBlock(Blocks.SAND::defaultBlockState);
        this.sporeFactor = 0.5F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 1.4D) return Blocks.GRASS_BLOCK.defaultBlockState();
        if (noise < -1.6D) return Blocks.GRAVEL.defaultBlockState();
        if (noise < -0.4D) return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
        return Blocks.SAND.defaultBlockState();
    }

    @Override
    protected BlockState underwaterTop(double noise) {
        if (noise < -1.2D) return Blocks.CLAY.defaultBlockState();
        return Blocks.SAND.defaultBlockState();
    }
}
