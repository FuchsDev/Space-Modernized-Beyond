package com.space_beyond.dim.sporos.biome;

import com.space_beyond.dim.sporos.SporosBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Sporenfeld ({@code space_beyond:sporos_spore_field}): flache, gefaehrliche Zone unter dichtem gelbem Nebel
 * ({@code CelestialSkyProvider.Sporos}, dichteste Stufe), uebersaet mit Sporenkapseln und Sporenblueten, abgestorbene
 * Staemme, kaum lebende Baeume; Boden gelbliches Gras, Humus und Myzelflecken.
 */
public class BiomeGenSporosSporeField extends BiomeGenBaseSporos {

    public static final int ID = 146;

    public BiomeGenSporosSporeField(int id) {
        super(id, "sporos_spore_field");
        this.setBiomeName("Spore Field");
        this.setHeight(new Height(0.12F, 0.04F));
        this.setTemperatureRainfall(0.9F, 0.6F);
        this.setTopBlock(Blocks.GRASS_BLOCK::defaultBlockState);
        this.sporeFactor = 2.0F;
    }

    @Override
    protected BlockState surfaceTop(double noise) {
        if (noise > 2.0D) return Blocks.MYCELIUM.defaultBlockState();
        if (noise < -1.4D) return SporosBlocks.SPOROS_SILT.get().defaultBlockState();
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }
}
