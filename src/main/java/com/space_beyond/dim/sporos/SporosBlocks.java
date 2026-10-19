package com.space_beyond.dim.sporos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.space_m.block.SpaceBlocks;
import com.space_m.dim.PlanetOres;
import com.space_m.dim.laythe.CelestialOreSet;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Bloecke des Planeten Sporos (eigene Erweiterung, kein NTM-Original). Wie bei den anderen Koerpern haben nur
 * Planetengestein ({@code sporos_stone}) und Boden ({@code sporos_silt}, dunkler Humus) eigene Texturen; der
 * Bewuchs ist die Sporos-Flora ({@code flora.SporosFlora}, R2b) plus Vanilla-Gras, -Moos und -Schlamm. Erz-Varianten {@code <erz>_sporos} wie Tekto
 * ({@link CelestialOreSet}, Vanilla- und NTM-Erze im Sporos-Gestein).
 */
public final class SporosBlocks {

    private SporosBlocks() {}

    public static final String BODY = "sporos";

    private static final List<Supplier<? extends Block>> SHOVEL = new ArrayList<>();
    private static final List<Supplier<? extends Block>> AXE = new ArrayList<>();

    /** Planetengestein: Haerte/Widerstand wie Vanilla-Stein (1,5 / 6), Spitzhacke. */
    public static final RegistrySupplier<Block> SPOROS_STONE = PlanetOres.pickaxe(com.space_beyond.block.BeyondBlocks.cube("sporos_stone",
            () -> new Block(PlanetOres.rock(1.5F, 10.0F).mapColor(MapColor.TERRACOTTA_GREEN)), SpaceCreativeTabs.Tab.BLOCKS));

    /** Humusboden unter Gras/Moos (Fuellschicht der Oberflaeche), Haerte wie Erde 0,5, Schaufel. */
    public static final RegistrySupplier<Block> SPOROS_SILT = shovel(com.space_beyond.block.BeyondBlocks.cube("sporos_silt",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.ROOTED_DIRT)),
            SpaceCreativeTabs.Tab.BLOCKS));

    /** Erze im Sporos-Gestein ({@code generateVanillaOres} + {@code generateNTMOres} wie Tekto). */
    public static final CelestialOreSet ORES = CelestialOreSet.create(BODY, true, true);

    // Beute-Kisten der Staedte (eigene Erweiterung): Holz, Haerte wie Fass (2,5), Axt; Inhalt droppt beim Abbau
    public static final RegistrySupplier<Block> SUPPLY_CRATE = axe(com.space_beyond.block.BeyondBlocks.dropsSelf(com.space_beyond.block.BeyondBlocks.registerWithItem("supply_crate",
            () -> (Block) new com.space_beyond.block.crate.BlockSupplyCrate(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F)
                    .sound(SoundType.WOOD).ignitedByLava()), SpaceCreativeTabs.Tab.BLOCKS)));
    public static final RegistrySupplier<Block> MILITARY_CRATE = axe(com.space_beyond.block.BeyondBlocks.dropsSelf(com.space_beyond.block.BeyondBlocks.registerWithItem("military_crate",
            () -> (Block) new com.space_beyond.block.crate.BlockSupplyCrate(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(2.5F)
                    .sound(SoundType.WOOD).ignitedByLava()), SpaceCreativeTabs.Tab.BLOCKS)));

    private static <T extends Block> RegistrySupplier<T> shovel(RegistrySupplier<T> b) {
        SHOVEL.add(b);
        return b;
    }

    private static <T extends Block> RegistrySupplier<T> axe(RegistrySupplier<T> b) {
        AXE.add(b);
        return b;
    }

    /** Werkzeug-Tags (Datagen {@code SpaceTagProviders}); das Gestein steht schon in {@link PlanetOres#pickaxeBlocks()}. */
    public static void addTags(BiConsumer<TagKey<Block>, Block> tag) {
        for (Supplier<? extends Block> b : SHOVEL) tag.accept(BlockTags.MINEABLE_WITH_SHOVEL, b.get());
        for (Supplier<? extends Block> b : AXE) tag.accept(BlockTags.MINEABLE_WITH_AXE, b.get());
    }

    public static void init() {
    }
}
