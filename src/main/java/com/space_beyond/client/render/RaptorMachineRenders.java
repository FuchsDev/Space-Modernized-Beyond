package com.space_beyond.client.render;

import com.space_m.main.SpaceRegistry;

/**
 * Umstellung der Space-Maschinen-BERs auf Raptors Renderpfad ({@code com.hbm_m.client.render.machine.MachineRenderers}:
 * VBO je Teil, Instancing/MDI, Iris-Begleitmesh, Occlusion-Culling, 8-Ecken-Licht).
 *
 * <p>Anmeldung: Die Fabrik schreibt in {@code MachineRenderRegistry} (eine einfache Liste ohne Sperre), in die HBM im
 * selben Client-Setup aus seinem eigenen Thread schreibt. Deshalb laufen die Anmeldungen hier nicht im parallelen
 * {@code CLIENT_SETUP}, sondern ueber {@code FMLClientSetupEvent.enqueueWork} auf dem Hauptthread - nach allen
 * parallelen Setup-Handlern, vor dem Erzeugen der BER-Instanzen.
 *
 * <p>A/B-Vergleich im Dev-Client: JVM-Argument {@code -Dspace_m.legacyMachineRender=true} registriert wieder die
 * alten immediate-BERs.
 */
public final class RaptorMachineRenders {

    private RaptorMachineRenders() {}

    private static boolean initialized;

    /** Alte immediate-BERs statt Raptors Pfad (nur zum Vergleich). */
    public static boolean enabled() {
        return !Boolean.getBoolean("space_m.legacyMachineRender");
    }

    /** Aus {@code SpaceClient.init()} (Mod-Konstruktor, nur Client). */
    public static void init() {
        if (initialized) return;
        initialized = true;
        //? if forge {
        @SuppressWarnings("removal")
        net.minecraftforge.eventbus.api.IEventBus modBus =
                net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e) ->
                e.enqueueWork(RaptorMachineRenders::registerAll));
        //?} elif neoforge {
        /*net.neoforged.bus.api.IEventBus modBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        modBus.addListener((net.neoforged.fml.event.lifecycle.FMLClientSetupEvent e) ->
                e.enqueueWork(RaptorMachineRenders::registerAll));
        *///?}
    }

    private static void registerAll() {
        if (!enabled()) {
            SpaceRegistry.LOGGER.info("[space_m] Maschinen-BERs: alter immediate-Pfad (space_m.legacyMachineRender)");
            return;
        }
        com.space_beyond.machine.chem.client.ChemRaptorRenders.register();
        com.space_beyond.machine.mining.client.MiningRaptorRenders.register();
        com.space_beyond.machine.misc.client.MiscRaptorRenders.register();
        com.space_beyond.machine.atmo.client.AtmoRaptorRenders.register();
        com.space_beyond.machine.dyson.client.DysonRaptorRenders.register();
        com.space_beyond.lifesupport.LifeSupportRaptorRenders.register();
        com.space_beyond.orbit.client.OrbitRaptorRenders.register();
        com.space_beyond.orbit.propulsion.client.PropulsionRaptorRenders.register();
        com.space_beyond.rocket.transport.client.TransportRaptorRenders.register();
        com.space_beyond.rocket.pad.client.PadRaptorRenders.register();
        SpaceRegistry.LOGGER.info("[space_m] Maschinen-BERs auf Raptors Renderpfad angemeldet");
    }

    /** Meta-Drehung {@code case 2: 0; case 4: 90; case 3: 180; case 5: 270} (z.B. {@code ChemModels.rotate0}, {@code DysonModels.rotate}). */
    public static float rot0(int meta) {
        return switch (meta) {
            case 4 -> 90F;
            case 3 -> 180F;
            case 5 -> 270F;
            default -> 0F;
        };
    }

    /** Meta-Drehung {@code case 2: 90; case 4: 180; case 3: 270; case 5: 0} (z.B. {@code ChemModels.rotate90}, {@code PropulsionModels.rotateThruster}). */
    public static float rot90(int meta) {
        return switch (meta) {
            case 2 -> 90F;
            case 4 -> 180F;
            case 3 -> 270F;
            default -> 0F;
        };
    }
}
