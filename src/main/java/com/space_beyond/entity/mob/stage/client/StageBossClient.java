package com.space_beyond.entity.mob.stage.client;

import com.space_beyond.entity.mob.stage.StageBosses;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

/** Renderer der Stufen-Bosse und ihres Geschosses (aus {@code BossClient}; generiert von {@code scratchpad/bossgen}). */
public final class StageBossClient {

    private StageBossClient() {}

    public static void init() {
        EntityRendererRegistry.register(StageBosses.LUNAR_SENTINEL, ctx -> new RenderStageBoss(ctx, "lunar_sentinel", StageBossModels.lunarSentinel(), StageBossModels.LUNAR_SENTINEL_ROLES,
                StageBossModels.LUNAR_SENTINEL_POSES, StageBossModels.LUNAR_SENTINEL_SCALE));
        EntityRendererRegistry.register(StageBosses.CRYO_BEHEMOTH, ctx -> new RenderStageBoss(ctx, "cryo_behemoth", StageBossModels.cryoBehemoth(), StageBossModels.CRYO_BEHEMOTH_ROLES,
                StageBossModels.CRYO_BEHEMOTH_POSES, StageBossModels.CRYO_BEHEMOTH_SCALE));
        EntityRendererRegistry.register(StageBosses.RUST_TITAN, ctx -> new RenderStageBoss(ctx, "rust_titan", StageBossModels.rustTitan(), StageBossModels.RUST_TITAN_ROLES,
                StageBossModels.RUST_TITAN_POSES, StageBossModels.RUST_TITAN_SCALE));
        EntityRendererRegistry.register(StageBosses.MONOLITH_KEEPER, ctx -> new RenderStageBoss(ctx, "monolith_keeper", StageBossModels.monolithKeeper(), StageBossModels.MONOLITH_KEEPER_ROLES,
                StageBossModels.MONOLITH_KEEPER_POSES, StageBossModels.MONOLITH_KEEPER_SCALE));
        EntityRendererRegistry.register(StageBosses.CORE_ABOMINATION, ctx -> new RenderStageBoss(ctx, "core_abomination", StageBossModels.coreAbomination(), StageBossModels.CORE_ABOMINATION_ROLES,
                StageBossModels.CORE_ABOMINATION_POSES, StageBossModels.CORE_ABOMINATION_SCALE));
        EntityRendererRegistry.register(StageBosses.MAGMA_WYRM, ctx -> new RenderStageBoss(ctx, "magma_wyrm", StageBossModels.magmaWyrm(), StageBossModels.MAGMA_WYRM_ROLES,
                StageBossModels.MAGMA_WYRM_POSES, StageBossModels.MAGMA_WYRM_SCALE));
        EntityRendererRegistry.register(StageBosses.SCHRAB_HYDRA, ctx -> new RenderStageBoss(ctx, "schrab_hydra", StageBossModels.schrabHydra(), StageBossModels.SCHRAB_HYDRA_ROLES,
                StageBossModels.SCHRAB_HYDRA_POSES, StageBossModels.SCHRAB_HYDRA_SCALE));
        EntityRendererRegistry.register(StageBosses.ABYSSAL_LEVIATHAN, ctx -> new RenderStageBoss(ctx, "abyssal_leviathan", StageBossModels.abyssalLeviathan(), StageBossModels.ABYSSAL_LEVIATHAN_ROLES,
                StageBossModels.ABYSSAL_LEVIATHAN_POSES, StageBossModels.ABYSSAL_LEVIATHAN_SCALE));
        EntityRendererRegistry.register(StageBosses.POLYMER_COLOSSUS, ctx -> new RenderStageBoss(ctx, "polymer_colossus", StageBossModels.polymerColossus(), StageBossModels.POLYMER_COLOSSUS_ROLES,
                StageBossModels.POLYMER_COLOSSUS_POSES, StageBossModels.POLYMER_COLOSSUS_SCALE));
        EntityRendererRegistry.register(StageBosses.MYCELIAL_QUEEN, ctx -> new RenderStageBoss(ctx, "mycelial_queen", StageBossModels.mycelialQueen(), StageBossModels.MYCELIAL_QUEEN_ROLES,
                StageBossModels.MYCELIAL_QUEEN_POSES, StageBossModels.MYCELIAL_QUEEN_SCALE));
        EntityRendererRegistry.register(StageBosses.BOSS_ORB, BossOrbRenderer::new);
    }
}
