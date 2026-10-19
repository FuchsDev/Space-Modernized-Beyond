package com.space_beyond.entity.mob.dungeon.client;

import com.space_beyond.entity.mob.dungeon.DungeonMobs;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

/** Renderer der Dungeon-Mobs (aus {@code BossClient}; generiert von {@code tools/bossgen/build_mobs.py}). */
public final class DungeonMobClient {

    private DungeonMobClient() {}

    public static void init() {
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("moon_drone"), ctx -> new RenderDungeonMob(ctx, "moon_drone", DungeonMobModels.moonDrone(),
                DungeonMobModels.MOON_DRONE_ROLES, DungeonMobModels.MOON_DRONE_POSES, DungeonMobModels.MOON_DRONE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("lost_astronaut"), ctx -> new RenderDungeonMob(ctx, "lost_astronaut", DungeonMobModels.lostAstronaut(),
                DungeonMobModels.LOST_ASTRONAUT_ROLES, DungeonMobModels.LOST_ASTRONAUT_POSES, DungeonMobModels.LOST_ASTRONAUT_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("frost_wisp"), ctx -> new RenderDungeonMob(ctx, "frost_wisp", DungeonMobModels.frostWisp(),
                DungeonMobModels.FROST_WISP_ROLES, DungeonMobModels.FROST_WISP_POSES, DungeonMobModels.FROST_WISP_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("glacier_brute"), ctx -> new RenderDungeonMob(ctx, "glacier_brute", DungeonMobModels.glacierBrute(),
                DungeonMobModels.GLACIER_BRUTE_ROLES, DungeonMobModels.GLACIER_BRUTE_POSES, DungeonMobModels.GLACIER_BRUTE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("rust_scuttler"), ctx -> new RenderDungeonMob(ctx, "rust_scuttler", DungeonMobModels.rustScuttler(),
                DungeonMobModels.RUST_SCUTTLER_ROLES, DungeonMobModels.RUST_SCUTTLER_POSES, DungeonMobModels.RUST_SCUTTLER_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("scrap_gunner"), ctx -> new RenderDungeonMob(ctx, "scrap_gunner", DungeonMobModels.scrapGunner(),
                DungeonMobModels.SCRAP_GUNNER_ROLES, DungeonMobModels.SCRAP_GUNNER_POSES, DungeonMobModels.SCRAP_GUNNER_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("void_shade"), ctx -> new RenderDungeonMob(ctx, "void_shade", DungeonMobModels.voidShade(),
                DungeonMobModels.VOID_SHADE_ROLES, DungeonMobModels.VOID_SHADE_POSES, DungeonMobModels.VOID_SHADE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("tomb_warden"), ctx -> new RenderDungeonMob(ctx, "tomb_warden", DungeonMobModels.tombWarden(),
                DungeonMobModels.TOMB_WARDEN_ROLES, DungeonMobModels.TOMB_WARDEN_POSES, DungeonMobModels.TOMB_WARDEN_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("irradiated_ghoul"), ctx -> new RenderDungeonMob(ctx, "irradiated_ghoul", DungeonMobModels.irradiatedGhoul(),
                DungeonMobModels.IRRADIATED_GHOUL_ROLES, DungeonMobModels.IRRADIATED_GHOUL_POSES, DungeonMobModels.IRRADIATED_GHOUL_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("toxic_sludge"), ctx -> new RenderDungeonMob(ctx, "toxic_sludge", DungeonMobModels.toxicSludge(),
                DungeonMobModels.TOXIC_SLUDGE_ROLES, DungeonMobModels.TOXIC_SLUDGE_POSES, DungeonMobModels.TOXIC_SLUDGE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("cinder_imp"), ctx -> new RenderDungeonMob(ctx, "cinder_imp", DungeonMobModels.cinderImp(),
                DungeonMobModels.CINDER_IMP_ROLES, DungeonMobModels.CINDER_IMP_POSES, DungeonMobModels.CINDER_IMP_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("magma_hound"), ctx -> new RenderDungeonMob(ctx, "magma_hound", DungeonMobModels.magmaHound(),
                DungeonMobModels.MAGMA_HOUND_ROLES, DungeonMobModels.MAGMA_HOUND_POSES, DungeonMobModels.MAGMA_HOUND_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("crystal_mantis"), ctx -> new RenderDungeonMob(ctx, "crystal_mantis", DungeonMobModels.crystalMantis(),
                DungeonMobModels.CRYSTAL_MANTIS_ROLES, DungeonMobModels.CRYSTAL_MANTIS_POSES, DungeonMobModels.CRYSTAL_MANTIS_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("schrab_spitter"), ctx -> new RenderDungeonMob(ctx, "schrab_spitter", DungeonMobModels.schrabSpitter(),
                DungeonMobModels.SCHRAB_SPITTER_ROLES, DungeonMobModels.SCHRAB_SPITTER_POSES, DungeonMobModels.SCHRAB_SPITTER_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("deep_angler"), ctx -> new RenderDungeonMob(ctx, "deep_angler", DungeonMobModels.deepAngler(),
                DungeonMobModels.DEEP_ANGLER_ROLES, DungeonMobModels.DEEP_ANGLER_POSES, DungeonMobModels.DEEP_ANGLER_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("abyss_diver"), ctx -> new RenderDungeonMob(ctx, "abyss_diver", DungeonMobModels.abyssDiver(),
                DungeonMobModels.ABYSS_DIVER_ROLES, DungeonMobModels.ABYSS_DIVER_POSES, DungeonMobModels.ABYSS_DIVER_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("polymer_ooze"), ctx -> new RenderDungeonMob(ctx, "polymer_ooze", DungeonMobModels.polymerOoze(),
                DungeonMobModels.POLYMER_OOZE_ROLES, DungeonMobModels.POLYMER_OOZE_POSES, DungeonMobModels.POLYMER_OOZE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("refinery_drone"), ctx -> new RenderDungeonMob(ctx, "refinery_drone", DungeonMobModels.refineryDrone(),
                DungeonMobModels.REFINERY_DRONE_ROLES, DungeonMobModels.REFINERY_DRONE_POSES, DungeonMobModels.REFINERY_DRONE_SCALE));
        EntityRendererRegistry.register(DungeonMobs.TYPES.get("hive_wasp"), ctx -> new RenderDungeonMob(ctx, "hive_wasp", DungeonMobModels.hiveWasp(),
                DungeonMobModels.HIVE_WASP_ROLES, DungeonMobModels.HIVE_WASP_POSES, DungeonMobModels.HIVE_WASP_SCALE));
    }
}
