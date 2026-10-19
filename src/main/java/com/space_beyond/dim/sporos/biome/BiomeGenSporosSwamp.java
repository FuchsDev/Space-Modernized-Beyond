package com.space_beyond.dim.sporos.biome;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Faulsumpf ({@code space_beyond:sporos_swamp}): Feuchtgebiet knapp um den Meeresspiegel mit Mangroven, Sumpfeichen,
 * Seerosen und Schilf; Boden Schlamm und Gras, unter Wasser Schlamm und Ton.
 */
public class BiomeGenSporosSwamp extends BiomeGenBaseSporos {

    public static final int ID = 142;

    public BiomeGenSporosSwamp(int id) {
        super(id, "sporos_swamp");
        this.setBiomeName("Rot Marsh");
        this.setHeight(new Height(-0.18F, 0.08F));
        this.setTemperatureRainfall(0.85F, 0.95F);
        this.setTopBlock(Blocks.GRASS_BLOCK::defaultBlockState);
        this.sporeFactor = 1.0F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 0.8D) return Blocks.MUD.defaultBlockState();
        if (noise < -2.2D) return Blocks.MOSS_BLOCK.defaultBlockState();
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    @Override
    protected BlockState underwaterTop(double noise) {
        if (noise < -1.5D) return Blocks.CLAY.defaultBlockState();
        return Blocks.MUD.defaultBlockState();
    }
}
