package com.space_beyond.entity.mob.boss;

import com.space_m.entity.SpaceEntities;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;
import com.space_m.main.SpaceTabFill;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Bosse (eigene Erweiterung): {@link EntityUnstableSovereign} im Thronsaal auf Wemburos und seine Beute
 * {@link ItemOrbitalStrikeRod} mit {@link OrbitalBeamEntity} und {@link EntityCrystalShard}. Client: {@code BossClient}.
 */
public final class BossMobs {

    private BossMobs() {}

    public static final RegistrySupplier<EntityType<EntityUnstableSovereign>> UNSTABLE_SOVEREIGN = com.space_beyond.entity.BeyondEntities.register("unstable_sovereign",
            () -> EntityType.Builder.<EntityUnstableSovereign>of(EntityUnstableSovereign::new, MobCategory.MONSTER)
                    .sized(1.4F, 4.2F).fireImmune().clientTrackingRange(10).updateInterval(2));

    public static final RegistrySupplier<ArchitecturySpawnEggItem> UNSTABLE_SOVEREIGN_EGG = com.space_beyond.item.BeyondItems.register("unstable_sovereign_spawn_egg",
            () -> new ArchitecturySpawnEggItem(UNSTABLE_SOVEREIGN, 0x2A1238, 0xE8B830, new Item.Properties()), null);

    public static final RegistrySupplier<Item> ORBITAL_STRIKE_ROD = com.space_beyond.item.BeyondItems.register("orbital_strike_rod",
            () -> new ItemOrbitalStrikeRod(new Item.Properties()), SpaceCreativeTabs.Tab.WEAPON);

    /** Orbitalschlag (Rod und Boss), nur Effekt + Wirkung, wird nicht gespeichert. */
    public static final RegistrySupplier<EntityType<OrbitalBeamEntity>> ORBITAL_BEAM = com.space_beyond.entity.BeyondEntities.register("orbital_beam",
            () -> EntityType.Builder.<OrbitalBeamEntity>of(OrbitalBeamEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).noSave().fireImmune().clientTrackingRange(16).updateInterval(1));

    /** Kristallsplitter-Salve des Bosses. */
    public static final RegistrySupplier<EntityType<EntityCrystalShard>> CRYSTAL_SHARD = com.space_beyond.entity.BeyondEntities.register("crystal_shard",
            () -> EntityType.Builder.<EntityCrystalShard>of(EntityCrystalShard::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).noSave().fireImmune().clientTrackingRange(8).updateInterval(1));

    private static boolean initialized;

    /** Aus {@code SpaceMobs.init()} (vor den gemeinsamen Registern). */
    public static void init() {
        if (initialized) return;
        initialized = true;
        SpaceEntities.attributes(UNSTABLE_SOVEREIGN, EntityUnstableSovereign::createAttributes);
        OrbitalStrikes.init();
        SpaceTabFill.modify(CreativeModeTabs.SPAWN_EGGS, "BossMobs",
                sink -> sink.accept(new ItemStack(UNSTABLE_SOVEREIGN_EGG.get()), String.valueOf(UNSTABLE_SOVEREIGN_EGG.getId())));
    }
}
