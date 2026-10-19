package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fluss ({@code space_beyond:sporos_river}): Flussbetten durch Ebene und Wald (nicht durch den Sumpf), Grund aus
 * Kies, Ton und Humus, Ufer mit Gras.
 */
public class BiomeGenSporosRiver extends BiomeGenBaseSporos {

    public static final int ID = 143;

    public BiomeGenSporosRiver(int id) {
        super(id, "sporos_river");
        this.setBiomeName("Sporos River");
        this.setHeight(new Height(-0.5F, 0.0F));
        this.setTemperatureRainfall(0.8F, 0.9F);
        this.setTopBlock(Blocks.GRASS_BLOCK::defaultBlockState);
        this.sporeFactor = 0.3F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    @Override
    protected BlockState underwaterTop(double noise) {
        if (noise > 1.2D) return Blocks.GRAVEL.defaultBlockState();
        if (noise < -1.2D) return Blocks.CLAY.defaultBlockState();
        return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
    }
}
