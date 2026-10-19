package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Pilzhain ({@code space_beyond:sporos_fungal_grove}): sanfte Mulden unter dauerhafter Daemmerung (dunkler Himmel,
 * dichter olivgruener Nebel ueber {@code CelestialSkyProvider.Sporos}), Riesenpilze, Leuchtpilze, Moospolster und
 * Schirmbaeume; Boden Myzel, Humus und Moos.
 */
public class BiomeGenSporosFungalGrove extends BiomeGenBaseSporos {

    public static final int ID = 144;

    public BiomeGenSporosFungalGrove(int id) {
        super(id, "sporos_fungal_grove");
        this.setBiomeName("Fungal Grove");
        this.setHeight(new Height(0.08F, 0.12F));
        this.setTemperatureRainfall(0.7F, 1.0F);
        this.setTopBlock(Blocks.MYCELIUM::defaultBlockState);
        this.sporeFactor = 1.4F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 1.6D) return Blocks.MOSS_BLOCK.defaultBlockState();
        if (noise < -1.6D) return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
        return Blocks.MYCELIUM.defaultBlockState();
    }
}
