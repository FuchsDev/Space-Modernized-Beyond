package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Nebelhochland ({@code space_beyond:sporos_highland}): hoch liegende, zerklueftete Huegel mit Felsnasen aus
 * Sporos-Gestein, grauem Dunst (dichterer Nebel, blasser Himmel), trockenem Gras, Totholz und einzelnen
 * Schirmbaeumen; Boden Gras, grobe Erde und blankes Gestein.
 */
public class BiomeGenSporosHighland extends BiomeGenBaseSporos {

    public static final int ID = 145;

    public BiomeGenSporosHighland(int id) {
        super(id, "sporos_highland");
        this.setBiomeName("Mist Highland");
        this.setHeight(new Height(0.85F, 0.55F));
        this.setTemperatureRainfall(0.55F, 0.7F);
        this.setTopBlock(Blocks.GRASS_BLOCK::defaultBlockState);
        this.sporeFactor = 0.5F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 1.2D) return SporosBlocks.SPOROS_STONE.get().defaultBlockState();
        if (noise > 0.4D) return Blocks.COARSE_DIRT.defaultBlockState();
        if (noise < -2.4D) return Blocks.MOSS_BLOCK.defaultBlockState();
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    @Override
    protected BlockState underwaterTop(double noise) {
        return Blocks.GRAVEL.defaultBlockState();
    }
}
