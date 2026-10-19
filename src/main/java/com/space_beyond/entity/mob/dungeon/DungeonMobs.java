package com.space_beyond.entity.mob.dungeon;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.space_m.entity.SpaceEntities;
import com.space_beyond.entity.mob.dungeon.DungeonMobKind.Ranged;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceTabFill;
import com.space_beyond.progression.SpaceProgression.Stage;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

/**
 * Die Dungeon-Mobs der Planeten (je zwei, Sporos einer plus die Infizierten): Werte, Verhalten, Spawn-Eier.
 * Renderer: {@code DungeonMobClient} (Modelle aus {@code tools/bossgen}).
 */
public final class DungeonMobs {

    private DungeonMobs() {}

    public static final Map<String, DungeonMobKind> KINDS = new LinkedHashMap<>();
    public static final Map<String, RegistrySupplier<EntityType<EntityDungeonMob>>> TYPES = new LinkedHashMap<>();
    private static final List<RegistrySupplier<ArchitecturySpawnEggItem>> EGGS = new ArrayList<>();
    private static final Map<EntityType<?>, DungeonMobKind> BY_TYPE = new IdentityHashMap<>();

    /** Bauplan zum Entity-Typ. */
    public static synchronized DungeonMobKind kind(EntityType<?> type) {
        DungeonMobKind k = BY_TYPE.get(type);
        if (k == null) {
            k = KINDS.get(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath());
            if (k == null) throw new IllegalStateException("Kein Dungeon-Mob: " + type);
            BY_TYPE.put(type, k);
        }
        return k;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static RegistrySupplier<EntityType<EntityDungeonMob>> mob(DungeonMobKind k) {
        KINDS.put(k.id, k);
        RegistrySupplier<EntityType<EntityDungeonMob>> type = com.space_beyond.entity.BeyondEntities.register(k.id, () -> {
            EntityType.Builder<EntityDungeonMob> b = EntityType.Builder.<EntityDungeonMob>of(EntityDungeonMob::new, MobCategory.MONSTER)
                    .sized(k.width, k.height).clientTrackingRange(8);
            return k.fireImmune ? b.fireImmune() : b;
        });
        TYPES.put(k.id, type);
        EGGS.add(com.space_beyond.item.BeyondItems.register(k.id + "_spawn_egg", () -> new ArchitecturySpawnEggItem((RegistrySupplier) type, k.egg1, k.egg2, new Item.Properties()), null));
        return type;
    }

    private static DustParticleOptions dust(int rgb, float size) {
        return new DustParticleOptions(new Vector3f(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F), size);
    }

    // ── Mun: Forschungsstation ──────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> MOON_DRONE = mob(new DungeonMobKind("moon_drone", Stage.MUN)
            .size(0.9F, 0.8F).stats(24, 3, 4, 0.3).fly(0.55).xp(8)
            .ranged(new Ranged(0xFF3C32, 0.25F, 4.0F, 34, 18, 0.9, 0, 0, 0, 1, 0.02F), 9)
            .particle(() -> ParticleTypes.ELECTRIC_SPARK)
            .sounds(() -> SoundEvents.BEE_LOOP_AGGRESSIVE, () -> SoundEvents.IRON_GOLEM_HURT, () -> SoundEvents.IRON_GOLEM_DEATH, 1.8F)
            .egg(0xB2B6BE, 0xFF3C32));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> LOST_ASTRONAUT = mob(new DungeonMobKind("lost_astronaut", Stage.MUN)
            .size(0.7F, 2.0F).stats(36, 5, 6, 0.24).xp(8)
            .onHit(() -> new MobEffectInstance(MobEffects.WEAKNESS, 100, 0))
            .sounds(() -> SoundEvents.ZOMBIE_AMBIENT, () -> SoundEvents.ZOMBIE_HURT, () -> SoundEvents.ZOMBIE_DEATH, 0.7F)
            .step(() -> SoundEvents.NETHERITE_BLOCK_STEP)
            .egg(0xE8E8E8, 0xE0A020));

    // ── Minmus: Kryo-Geode ──────────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> FROST_WISP = mob(new DungeonMobKind("frost_wisp", Stage.MINMUS)
            .size(0.8F, 1.2F).stats(24, 3, 0, 0.3).fly(0.6).xp(9)
            .ranged(new Ranged(0xA0F0E6, 0.3F, 3.5F, 40, 16, 0.7, 0, 0.06, 1.5F, 1, 0.02F), 8)
            .onHit(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1))
            .particle(() -> ParticleTypes.SNOWFLAKE)
            .sounds(() -> SoundEvents.VEX_AMBIENT, () -> SoundEvents.VEX_HURT, () -> SoundEvents.GLASS_BREAK, 0.6F)
            .egg(0xD8FFF6, 0x5AAAA0));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> GLACIER_BRUTE = mob(new DungeonMobKind("glacier_brute", Stage.MINMUS)
            .size(1.5F, 2.8F).stats(80, 10, 10, 0.21).knockResist(0.8).xp(18)
            .slam(90, () -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2))
            .onHit(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1))
            .particle(() -> ParticleTypes.SNOWFLAKE)
            .sounds(() -> SoundEvents.RAVAGER_AMBIENT, () -> SoundEvents.IRON_GOLEM_HURT, () -> SoundEvents.RAVAGER_DEATH, 0.8F)
            .step(() -> SoundEvents.GLASS_STEP)
            .egg(0xA0E8D6, 0x2F6E78));

    // ── Duna: Rost-Giesserei ────────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> RUST_SCUTTLER = mob(new DungeonMobKind("rust_scuttler", Stage.DUNA)
            .size(0.9F, 0.6F).stats(16, 4, 4, 0.34).leap(60).xp(6)
            .sounds(() -> SoundEvents.SPIDER_AMBIENT, () -> SoundEvents.SILVERFISH_HURT, () -> SoundEvents.CHAIN_BREAK, 1.3F)
            .step(() -> SoundEvents.CHAIN_STEP)
            .egg(0x964628, 0xFFAA28));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> SCRAP_GUNNER = mob(new DungeonMobKind("scrap_gunner", Stage.DUNA)
            .size(0.9F, 2.1F).stats(40, 5, 8, 0.22).xp(12)
            .ranged(new Ranged(0xFFB040, 0.18F, 3.0F, 46, 20, 1.3, 0, 0, 0, 3, 0.05F), 10)
            .particle(() -> ParticleTypes.SMOKE)
            .attackSound(() -> SoundEvents.CROSSBOW_SHOOT)
            .sounds(() -> SoundEvents.IRON_GOLEM_STEP, () -> SoundEvents.IRON_GOLEM_HURT, () -> SoundEvents.IRON_GOLEM_DEATH, 1.2F)
            .step(() -> SoundEvents.NETHERITE_BLOCK_STEP)
            .egg(0x6E5040, 0xFFB040));

    // ── Ike: Monolith-Nekropole ─────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> VOID_SHADE = mob(new DungeonMobKind("void_shade", Stage.IKE)
            .size(0.7F, 2.1F).stats(32, 7, 2, 0.3).teleport(90).xp(12)
            .onHit(() -> new MobEffectInstance(MobEffects.BLINDNESS, 40, 0))
            .particle(() -> ParticleTypes.REVERSE_PORTAL)
            .sounds(() -> SoundEvents.ENDERMAN_AMBIENT, () -> SoundEvents.ENDERMAN_HURT, () -> SoundEvents.ENDERMAN_DEATH, 0.6F)
            .egg(0x1A1424, 0xC86EFF));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> TOMB_WARDEN = mob(new DungeonMobKind("tomb_warden", Stage.IKE)
            .size(1.4F, 2.8F).stats(100, 12, 14, 0.19).knockResist(0.9).xp(20)
            .slam(80, () -> new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 1))
            .particle(() -> dust(0xC86EFF, 0.8F))
            .sounds(() -> SoundEvents.IRON_GOLEM_STEP, () -> SoundEvents.STONE_BREAK, () -> SoundEvents.IRON_GOLEM_DEATH, 0.5F)
            .step(() -> SoundEvents.STONE_STEP)
            .egg(0x45424C, 0x9A5AE0));

    // ── Dres: Kernschmelze ──────────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> IRRADIATED_GHOUL = mob(new DungeonMobKind("irradiated_ghoul", Stage.DRES)
            .size(0.7F, 2.0F).stats(42, 7, 4, 0.27).xp(12)
            .onHit(() -> new MobEffectInstance(MobEffects.POISON, 100, 1))
            .cloud(() -> new MobEffectInstance(MobEffects.POISON, 100, 0), 0x78FF46)
            .particle(() -> dust(0x78FF46, 0.7F))
            .sounds(() -> SoundEvents.ZOMBIE_VILLAGER_AMBIENT, () -> SoundEvents.ZOMBIE_VILLAGER_HURT, () -> SoundEvents.ZOMBIE_VILLAGER_DEATH, 0.8F)
            .egg(0x4C5A3A, 0x78FF46));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> TOXIC_SLUDGE = mob(new DungeonMobKind("toxic_sludge", Stage.DRES)
            .size(1.4F, 1.1F).stats(36, 5, 0, 0.22).split(3).xp(10)
            .onHit(() -> new MobEffectInstance(MobEffects.POISON, 80, 0))
            .particle(() -> ParticleTypes.ITEM_SLIME)
            .sounds(() -> SoundEvents.SLIME_SQUISH, () -> SoundEvents.SLIME_HURT, () -> SoundEvents.SLIME_DEATH, 0.7F)
            .step(() -> SoundEvents.SLIME_SQUISH_SMALL)
            .egg(0x3A6E1E, 0xB4FF5A));

    // ── Moho: Vulkanschmiede ────────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> CINDER_IMP = mob(new DungeonMobKind("cinder_imp", Stage.MOHO)
            .size(0.7F, 1.1F).stats(24, 4, 2, 0.3).fly(0.6).fireImmune().fire(4).xp(10)
            .ranged(new Ranged(0xFF7814, 0.3F, 4.0F, 36, 18, 0.8, 0, 0.03, 1.2F, 1, 0.03F), 8)
            .particle(() -> ParticleTypes.FLAME)
            .sounds(() -> SoundEvents.BLAZE_AMBIENT, () -> SoundEvents.BLAZE_HURT, () -> SoundEvents.BLAZE_DEATH, 1.5F)
            .egg(0x2E2828, 0xFF7814));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> MAGMA_HOUND = mob(new DungeonMobKind("magma_hound", Stage.MOHO)
            .size(1.0F, 1.1F).stats(38, 7, 4, 0.33).leap(70).fireImmune().fire(5).xp(12)
            .particle(() -> ParticleTypes.LAVA)
            .sounds(() -> SoundEvents.WOLF_GROWL, () -> SoundEvents.WOLF_HURT, () -> SoundEvents.WOLF_DEATH, 0.6F)
            .egg(0x3A2020, 0xFF5A14));

    // ── Eve: Schrabidium-Kathedrale ─────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> CRYSTAL_MANTIS = mob(new DungeonMobKind("crystal_mantis", Stage.EVE)
            .size(1.0F, 1.9F).stats(48, 9, 6, 0.33).leap(80).xp(14)
            .onHit(() -> new MobEffectInstance(MobEffects.WEAKNESS, 80, 0))
            .particle(() -> ParticleTypes.GLOW)
            .sounds(() -> SoundEvents.SILVERFISH_AMBIENT, () -> SoundEvents.AMETHYST_BLOCK_HIT, () -> SoundEvents.AMETHYST_BLOCK_BREAK, 0.7F)
            .step(() -> SoundEvents.AMETHYST_BLOCK_STEP)
            .egg(0x287878, 0x50F0F0));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> SCHRAB_SPITTER = mob(new DungeonMobKind("schrab_spitter", Stage.EVE)
            .size(1.0F, 1.4F).stats(36, 4, 4, 0.23).xp(12)
            .ranged(new Ranged(0x50F0F0, 0.35F, 4.0F, 50, 16, 0.75, 0.035, 0, 2.5F, 1, 0.02F), 9)
            .onHit(() -> new MobEffectInstance(MobEffects.POISON, 80, 1))
            .particle(() -> dust(0x50F0F0, 0.6F))
            .attackSound(() -> SoundEvents.LLAMA_SPIT)
            .sounds(() -> SoundEvents.FROG_AMBIENT, () -> SoundEvents.FROG_HURT, () -> SoundEvents.FROG_DEATH, 0.6F)
            .egg(0x3C2850, 0x50F0F0));

    // ── Laythe: Tiefsee-Station ─────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> DEEP_ANGLER = mob(new DungeonMobKind("deep_angler", Stage.LAYTHE)
            .size(1.2F, 0.9F).stats(40, 8, 4, 1.0).swim().xp(12)
            .onHit(() -> new MobEffectInstance(MobEffects.DARKNESS, 60, 0))
            .particle(() -> ParticleTypes.GLOW)
            .sounds(() -> SoundEvents.GUARDIAN_AMBIENT, () -> SoundEvents.GUARDIAN_HURT, () -> SoundEvents.GUARDIAN_DEATH, 0.7F)
            .egg(0x182840, 0x50F0F0));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> ABYSS_DIVER = mob(new DungeonMobKind("abyss_diver", Stage.LAYTHE)
            .size(0.8F, 2.0F).stats(46, 6, 8, 0.24).amphibious().xp(14)
            .ranged(new Ranged(0xC8DCE6, 0.15F, 5.0F, 44, 18, 1.6, 0.01, 0, 0, 1, 0.01F), 9)
            .attackSound(() -> SoundEvents.DROWNED_SHOOT)
            .sounds(() -> SoundEvents.DROWNED_AMBIENT, () -> SoundEvents.DROWNED_HURT, () -> SoundEvents.DROWNED_DEATH, 0.9F)
            .step(() -> SoundEvents.NETHERITE_BLOCK_STEP)
            .egg(0x2E4A5A, 0xE0A020));

    // ── Tekto: Polymer-Raffinerie ───────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> POLYMER_OOZE = mob(new DungeonMobKind("polymer_ooze", Stage.TEKTO)
            .size(1.4F, 1.1F).stats(44, 6, 2, 0.21).split(2).xp(12)
            .onHit(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2))
            .particle(() -> dust(0xBEFF3C, 0.7F))
            .sounds(() -> SoundEvents.SLIME_SQUISH, () -> SoundEvents.HONEY_BLOCK_BREAK, () -> SoundEvents.SLIME_DEATH, 0.5F)
            .step(() -> SoundEvents.HONEY_BLOCK_STEP)
            .egg(0xDCE0D6, 0xBEFF3C));

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> REFINERY_DRONE = mob(new DungeonMobKind("refinery_drone", Stage.TEKTO)
            .size(1.0F, 0.8F).stats(30, 4, 6, 0.3).fly(0.5).fire(4).xp(12)
            .ranged(new Ranged(0xFFC83C, 0.22F, 3.0F, 30, 10, 0.7, 0, 0, 1.0F, 3, 0.08F), 6)
            .particle(() -> ParticleTypes.SMOKE)
            .attackSound(() -> SoundEvents.FIRECHARGE_USE)
            .sounds(() -> SoundEvents.BEE_LOOP_AGGRESSIVE, () -> SoundEvents.IRON_GOLEM_HURT, () -> SoundEvents.IRON_GOLEM_DEATH, 1.3F)
            .egg(0xC8C832, 0x303030));

    // ── Sporos: Der Stock ───────────────────────────────────────────────────

    public static final RegistrySupplier<EntityType<EntityDungeonMob>> HIVE_WASP = mob(new DungeonMobKind("hive_wasp", Stage.SPOROS)
            .size(0.9F, 0.8F).stats(26, 6, 2, 0.3).fly(0.7).xp(12)
            .onHit(() -> new MobEffectInstance(MobEffects.POISON, 100, 1))
            .particle(() -> ParticleTypes.SPORE_BLOSSOM_AIR)
            .sounds(() -> SoundEvents.BEE_LOOP_AGGRESSIVE, () -> SoundEvents.BEE_HURT, () -> SoundEvents.BEE_DEATH, 0.6F)
            .egg(0x8C3C32, 0xFFC85A));

    private static boolean initialized;

    /** Aus {@code SpaceMobs.init()}. */
    public static void init() {
        if (initialized) return;
        initialized = true;
        for (var e : TYPES.entrySet()) {
            DungeonMobKind k = KINDS.get(e.getKey());
            SpaceEntities.attributes(e.getValue(), () -> EntityDungeonMob.attributes(k));
        }
        SpaceTabFill.modify(CreativeModeTabs.SPAWN_EGGS, "DungeonMobs",
                sink -> {
                    for (RegistrySupplier<ArchitecturySpawnEggItem> egg : EGGS) sink.accept(new ItemStack(egg.get()), String.valueOf(egg.getId()));
                });
    }
}
