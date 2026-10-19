package com.space_beyond;

import com.space_beyond.main.BeyondRegistry;

import net.minecraft.resources.ResourceLocation;
//? if forge {
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//?} elif neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*///?}

/**
 * Einstieg von Space Modernized: Beyond, dem Addon fuer Space Modernized (zweites Sternsystem mit Sporos und
 * Wemburos, neue Monde, Asteroidenguertel, Sternkarte, Planeten-Dungeons und -Fortschritt).
 *
 * <p>Space Modernized selbst bleibt unveraendert; wo Beyond in dessen Ablauf eingreift, geschieht das ueber
 * Mixins ({@code com.space_beyond.mixin}). Die Initialisierung laeuft loaderneutral ueber {@link BeyondRegistry#init()}.
 */
@Mod(BeyondMod.MOD_ID)
public final class BeyondMod {
    public static final String MOD_ID = /*$ mod_id */"space_beyond";

    //? if forge {
    @SuppressWarnings("removal")
    public BeyondMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(MOD_ID, modBus);
        BeyondRegistry.init();
    }
    //?} elif neoforge {
    /*public BeyondMod(IEventBus modBus) {
        BeyondRegistry.init();
    }
    *///?}

    /** {@code space_beyond:<path>}. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
