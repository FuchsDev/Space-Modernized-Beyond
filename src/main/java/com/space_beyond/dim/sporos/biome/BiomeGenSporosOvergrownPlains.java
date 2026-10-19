package com.space_beyond.dim.sporos.biome;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Verwilderte Ebene ({@code space_beyond:sporos_overgrown_plains}): flache, hoch zugewachsene Wiesen mit Farnen und
 * vereinzelten rankenbehangenen Baeumen; hier liegen in Runde 3 bevorzugt die Staedte. Oberflaeche Gras, an feuchten
 * Stellen Moos, selten grober Humus (Wurzelerde).
 */
public class BiomeGenSporosOvergrownPlains extends BiomeGenBaseSporos {

    public static final int ID = 140;

    public BiomeGenSporosOvergrownPlains(int id) {
        super(id, "sporos_overgrown_plains");
        this.setBiomeName("Overgrown Plains");
        this.setHeight(new Height(0.15F, 0.07F));
        this.setTemperatureRainfall(0.8F, 0.8F);
        this.setTopBlock(Blocks.GRASS_BLOCK::defaultBlockState);
        this.sporeFactor = 0.6F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 2.6D) return Blocks.MOSS_BLOCK.defaultBlockState();
        if (noise < -2.8D) return Blocks.ROOTED_DIRT.defaultBlockState();
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }
}
