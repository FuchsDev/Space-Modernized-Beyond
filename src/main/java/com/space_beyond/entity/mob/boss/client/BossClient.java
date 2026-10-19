package com.space_beyond.entity.mob.boss.client;

import com.space_beyond.entity.mob.boss.BossMobs;
import com.space_beyond.entity.mob.client.RenderUnstableSovereign;
import com.space_m.particle.SpaceParticles;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;

/** Clientseite der Bosse: Renderer, eigene Partikel, Kamerawackeln. Aus {@code SpaceClient.init()}. */
public final class BossClient {

    private BossClient() {}

    public static void init() {
        EntityRendererRegistry.register(BossMobs.UNSTABLE_SOVEREIGN, RenderUnstableSovereign::new);
        EntityRendererRegistry.register(BossMobs.ORBITAL_BEAM, OrbitalBeamRenderer::new);
        EntityRendererRegistry.register(com.space_beyond.progression.fx.WeaponFx.SLASH, com.space_beyond.progression.fx.client.SlashRenderer::new);
        EntityRendererRegistry.register(BossMobs.CRYSTAL_SHARD, CrystalShardRenderer::new);
        ParticleProviderRegistry.register(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK, s -> new SovereignParticle.Provider(s, 0));
        ParticleProviderRegistry.register(com.space_beyond.particle.BeyondParticles.GOLD_EMBER, s -> new SovereignParticle.Provider(s, 1));
        ParticleProviderRegistry.register(com.space_beyond.particle.BeyondParticles.VOID_MOTE, s -> new SovereignParticle.Provider(s, 2));
        ScreenShake.init();
        com.space_beyond.entity.mob.stage.client.StageBossClient.init();
        com.space_beyond.entity.mob.dungeon.client.DungeonMobClient.init();
    }
}
