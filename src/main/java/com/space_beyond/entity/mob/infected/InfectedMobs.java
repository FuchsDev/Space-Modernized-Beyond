package com.space_beyond.entity.mob.infected;

import java.util.List;
import java.util.function.Supplier;

import com.space_m.entity.SpaceEntities;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceTabFill;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Die Infizierten von Sporos (eigene Erweiterung, Sporos Runde 5): Entities, Spawn-Eier (Vanilla-Reiter), Attribute,
 * Platzierung (Monster-Regeln: Dunkelheit, fester Boden). Spawnlisten in den Biom-JSONs {@code sporos_*.json}
 * ({@code monster}): Sporling 60/2-4, Lauerer 25/1-2, Blindhut 20/1-2, Pilzkoloss 3/1-1; Stadtbiome mehr Lauerer/
 * Blindhuete/Kolosse, Sporenfeld mehr Kolosse. Renderer: {@code MobClient} ({@code RenderInfected}, Zombiemodell mit
 * eigenen Texturen {@code textures/entity/infected/<name>.png}).
 */
public final class InfectedMobs {

    private InfectedMobs() {}

    public static final RegistrySupplier<EntityType<EntitySporeling>> SPORELING = mob("sporeling",
            () -> EntityType.Builder.<EntitySporeling>of(EntitySporeling::new, MobCategory.MONSTER).sized(0.6F, 1.95F));
    public static final RegistrySupplier<EntityType<EntityLurker>> LURKER = mob("lurker",
            () -> EntityType.Builder.<EntityLurker>of(EntityLurker::new, MobCategory.MONSTER).sized(0.6F, 1.95F));
    public static final RegistrySupplier<EntityType<EntityBlindcap>> BLINDCAP = mob("blindcap",
            () -> EntityType.Builder.<EntityBlindcap>of(EntityBlindcap::new, MobCategory.MONSTER).sized(0.7F, 2.0F));
    public static final RegistrySupplier<EntityType<EntityMycobrute>> MYCOBRUTE = mob("mycobrute",
            () -> EntityType.Builder.<EntityMycobrute>of(EntityMycobrute::new, MobCategory.MONSTER).sized(0.9F, 2.6F));

    public static final RegistrySupplier<ArchitecturySpawnEggItem> SPORELING_EGG = egg("sporeling", SPORELING, 0x9DA78C, 0x4E5A3F);
    public static final RegistrySupplier<ArchitecturySpawnEggItem> LURKER_EGG = egg("lurker", LURKER, 0x5E6B4E, 0x2B3324);
    public static final RegistrySupplier<ArchitecturySpawnEggItem> BLINDCAP_EGG = egg("blindcap", BLINDCAP, 0x8A7A62, 0xC9842F);
    public static final RegistrySupplier<ArchitecturySpawnEggItem> MYCOBRUTE_EGG = egg("mycobrute", MYCOBRUTE, 0x6B6F52, 0xD8C45A);

    public static final List<RegistrySupplier<ArchitecturySpawnEggItem>> EGGS = List.of(SPORELING_EGG, LURKER_EGG, BLINDCAP_EGG, MYCOBRUTE_EGG);
    public static final List<RegistrySupplier<? extends EntityType<? extends EntityInfected>>> ALL = List.of(SPORELING, LURKER, BLINDCAP, MYCOBRUTE);

    private static <T extends Mob> RegistrySupplier<EntityType<T>> mob(String name, Supplier<EntityType.Builder<T>> builder) {
        return com.space_beyond.entity.BeyondEntities.register(name, () -> builder.get().clientTrackingRange(8).updateInterval(3));
    }

    private static <T extends Mob> RegistrySupplier<ArchitecturySpawnEggItem> egg(String name, RegistrySupplier<EntityType<T>> type, int c1, int c2) {
        return com.space_beyond.item.BeyondItems.register(name + "_spawn_egg", () -> new ArchitecturySpawnEggItem(type, c1, c2, new Item.Properties()), null);
    }

    private static boolean initialized;

    /** Aus {@code SpaceMobs.init()} (vor den gemeinsamen Registern). */
    public static void init() {
        if (initialized) return;
        initialized = true;

        SpaceEntities.attributes(SPORELING, EntitySporeling::createAttributes);
        SpaceEntities.attributes(LURKER, EntityLurker::createAttributes);
        SpaceEntities.attributes(BLINDCAP, EntityBlindcap::createAttributes);
        SpaceEntities.attributes(MYCOBRUTE, EntityMycobrute::createAttributes);

        SpaceTabFill.modify(CreativeModeTabs.SPAWN_EGGS, "InfectedMobs",
                sink -> {
                    for (RegistrySupplier<ArchitecturySpawnEggItem> egg : EGGS) sink.accept(new ItemStack(egg.get()), String.valueOf(egg.getId()));
                });

        // Platzierung wie Vanilla-Monster (Dunkelheit, fester Boden)
        //? if < 1.21.1 {
        var ground = net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND;
        //?} else {
        /*var ground = net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND;
        *///?}
        SpawnPlacementsRegistry.register(SPORELING, ground, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacementsRegistry.register(LURKER, ground, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacementsRegistry.register(BLINDCAP, ground, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacementsRegistry.register(MYCOBRUTE, ground, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }
}
