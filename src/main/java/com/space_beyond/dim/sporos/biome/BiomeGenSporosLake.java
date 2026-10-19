package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * See ({@code space_beyond:sporos_lake}): truebe Binnenseen unter dem Meeresspiegel (Grundhoehe -0.7), von der Kueste
 * umgeben; Grund aus Ton, Humus und Kies, Seegras und Seerosen; Wasser teal und klarer als im Sumpf.
 */
public class BiomeGenSporosLake extends BiomeGenBaseSporos {

    public static final int ID = 148;

    public BiomeGenSporosLake(int id) {
        super(id, "sporos_lake");
        this.setBiomeName("Spore Lake");
        this.setHeight(new Height(-0.7F, 0.08F));
        this.setTemperatureRainfall(0.75F, 0.9F);
        this.setTopBlock(Blocks.CLAY::defaultBlockState);
        this.sporeFactor = 0.3F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        return Blocks.SAND.defaultBlockState();
    }

    @Override
    protected BlockState underwaterTop(double noise) {
        if (noise > 1.4D) return Blocks.GRAVEL.defaultBlockState();
        if (noise < -0.6D) return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
        return Blocks.CLAY.defaultBlockState();
    }
}
