package com.space_beyond.test;

import com.space_beyond.BeyondMod;

/**
 * Meldet die GameTests von Beyond auf beiden Loadern an.
 * Lauf: {@code :1.20.1-forge:runGameTestServer} / {@code :1.21.1-neoforge:runGameTestServer}
 * (Namespace-Filter {@code space_beyond} in den Build-Skripten). Neue Testklassen hier eintragen.
 */
//? if forge {
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BeyondMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
//?} elif neoforge {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@EventBusSubscriber(modid = BeyondMod.MOD_ID)
*///?}
public final class BeyondGameTestRegistration {

    private BeyondGameTestRegistration() {}

    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        event.register(BeyondFoundationGameTest.class); // Andocken an Space Modernized (Mixins, Koerper, Dimensionen)
        event.register(StarSystemGameTest.class); // Sternsysteme
        event.register(com.space_beyond.entity.mob.dungeon.DungeonMobGameTest.class); // Dungeon-Bewohner
        event.register(SporosGameTest.class); // Sporos: Planet, Gelaende, Stadtplanung, Faltantrieb
        event.register(com.space_beyond.dim.sporos.flora.SporosFloraGameTest.class); // Sporos: Flora
        event.register(SporosCityGameTest.class); // Sporos: Staedtegenerator
        event.register(SporosInfectionGameTest.class); // Sporos: Sporeninfektion
        event.register(GolfClubGameTest.class); // Golfschlaeger
        event.register(InfectedGameTest.class); // Sporos: Infizierte
        event.register(SurvivalItemsGameTest.class); // Ueberlebens-Set
        event.register(SporosLandmarkGameTest.class); // Landmarken + Beute-Kisten
        event.register(SporosStructureGameTest.class); // Wildnis-Strukturen
        event.register(WemburosGameTest.class); // Planet Wemburos
        event.register(RecordGameTest.class); // Schallplatten in der Jukebox (Musikdisc-Tag)
        event.register(BossGameTest.class); // Unstable Sovereign + Orbital Strike Rod
        event.register(ProgressionGameTest.class); // Planetendungeons, Bosse und Fortschritt
    }
}
