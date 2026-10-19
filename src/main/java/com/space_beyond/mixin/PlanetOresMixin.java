package com.space_beyond.mixin;

import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.space_beyond.block.BeyondBlocks;
import com.space_beyond.celestial.SporosSystem;
import com.space_m.block.SpaceBlocks;
import com.space_m.dim.PlanetOres;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Erz-Varianten der Beyond-Koerper ({@code CelestialOreSet.create("sporos", ...)}) laufen ueber den Helfer von Space
 * Modernized, damit sie in dessen Variantenliste stehen (Weltgen, Materialien, Fundorte, Lagerstaetten). Nur fuer
 * Sporos und Wemburos landen die Bloecke in den Beyond-Registern ({@code space_beyond:ore_iron_sporos}); alle
 * Original-Koerper bleiben unveraendert {@code space_m}.
 */
@Mixin(value = PlanetOres.class, remap = false)
public abstract class PlanetOresMixin {

    @Redirect(method = "ore(Ljava/lang/String;Ljava/lang/String;Ljava/util/function/Function;FF)Ldev/architectury/registry/registries/RegistrySupplier;",
            at = @At(value = "INVOKE", target = "Lcom/space_m/block/SpaceBlocks;registerWithItem(Ljava/lang/String;Ljava/util/function/Supplier;Lcom/space_m/main/SpaceCreativeTabs$Tab;)Ldev/architectury/registry/registries/RegistrySupplier;"))
    private static <T extends Block> RegistrySupplier<T> beyond$ownRegister(String id, Supplier<T> block, SpaceCreativeTabs.Tab tab,
                                                                          String ore, String body, Function<BlockBehaviour.Properties, Block> factory,
                                                                          float hardness, float resistance1710) {
        boolean beyondBody = SporosSystem.PLANET_NAME.equals(body) || SporosSystem.WEMBUROS_NAME.equals(body);
        return beyondBody ? BeyondBlocks.registerWithItem(id, block, tab) : SpaceBlocks.registerWithItem(id, block, tab);
    }
}
