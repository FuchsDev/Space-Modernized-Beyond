package com.space_beyond.main;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.space_beyond.block.BeyondBlocks;
import com.space_beyond.blockentity.BeyondBlockEntities;
import com.space_beyond.config.BeyondConfig;
import com.space_beyond.entity.BeyondEntities;
import com.space_beyond.item.BeyondEffects;
import com.space_beyond.item.BeyondItems;
import com.space_beyond.network.BeyondPackets;
import com.space_beyond.particle.BeyondParticles;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;

/**
 * Gemeinsame Initialisierung von Beyond (Gegenstueck zu {@code com.space_m.main.SpaceRegistry}).
 *
 * <p>Laeuft im Mod-Konstruktor, also nach Space Modernized (Ladereihenfolge AFTER). Die Aufrufe, die im Privat-Repo
 * direkt in Space-Modernized-Klassen standen (SpaceRegistry, SpaceItems, SpaceMobs, SpaceWorldgen ...), stehen hier.
 */
public final class BeyondRegistry {
    public static final Logger LOGGER = LogUtils.getLogger();

    private BeyondRegistry() {}

    public static void init() {
        LOGGER.info("[space_beyond] Initializing Space Modernized: Beyond");

        BeyondConfig.load();

        // Koerper-Eigenschaft Sporen (Sync-ID = Index: hinten an die 13 Original-Eigenschaften)
        com.space_beyond.mixin.CelestialBodyTraitAccessor.beyond$registerTrait("spores",
                com.space_beyond.celestial.trait.CBT_Spores.class, com.space_beyond.celestial.trait.CBT_Spores::new);

        // Sternsysteme: Faltraum-Antrieb als interstellarer Antrieb (nur fuer Stationen mit geladenem Antrieb)
        com.space_beyond.orbit.interstellar.FoldDriveNetwork.init();

        // Netzwerk (NeoForge verlangt die Anmeldung im Mod-Konstruktor)
        BeyondPackets.register();

        BeyondParticles.init();
        BeyondEffects.init();

        // Koerper (Bloecke + Generator-Fabriken), Weltgen-Features
        com.space_beyond.dim.BeyondWorldgen.init();

        // Mobs: Infizierte von Sporos, Bosse, Stufen-Bosse, Dungeon-Bewohner (Entities/Eier vor den Registern)
        com.space_beyond.entity.mob.infected.InfectedMobs.init();
        com.space_beyond.entity.mob.boss.BossMobs.init();
        com.space_beyond.entity.mob.stage.StageBosses.init();
        com.space_beyond.entity.mob.dungeon.DungeonMobs.init();

        com.space_beyond.dim.dungeon.seal.SealBlocks.init(); // Siegel der Boss-Dungeons (vor den Registern)
        com.space_beyond.dim.dungeon.seal.DungeonProtection.init(); // Dungeon-Schutz bis zum Sieg
        com.space_beyond.celestial.VisitedBodies.init(); // besuchte Koerper je Spieler (Sternkarte)

        // Ueberlebens-Set fuer Sporos, Planeten-Fortschritt (Kerne, Waffen, Essen, Triebwerke Mk.II-XI)
        com.space_beyond.item.survival.SurvivalItems.init();
        com.space_beyond.progression.ProgressionItems.init();

        BeyondBlocks.init();
        BeyondItems.init();
        BeyondBlockEntities.init();
        BeyondEntities.init();

        BeyondTabs.init();

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
                com.space_beyond.command.BeyondCommands.register(dispatcher));

        LifecycleEvent.SETUP.register(BeyondRegistry::commonSetup);

        EnvExecutor.runInEnv(Env.CLIENT, () -> com.space_beyond.client.BeyondClient::init);
    }

    private static void commonSetup() {
        LOGGER.info("[space_beyond] Common setup finished");
    }
}
