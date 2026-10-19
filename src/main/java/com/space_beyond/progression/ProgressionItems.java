package com.space_beyond.progression;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.hbm_m.platform.PlatformHooks;
import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;
import com.space_beyond.progression.SpaceProgression.Stage;
import com.space_m.rocket.ItemRocketPart;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

/**
 * Items des Planeten-Fortschritts (eigene Erweiterung): je Stufe 1-10 (Mun ... Sporos) ein Boss-Kern, eine Boss-Waffe
 * und ein Planeten-Essen, dazu die Triebwerke Mk.II - Mk.XI ({@link ProgressionParts}) und der Dungeon-Peiler.
 * <pre>
 * Stufe  Planet  Kern                 Waffe               Essen
 *  1     Mun     sentinel_core        shock_baton         mun_cheese
 *  2     Minmus  cryo_heart           frostbite_cleaver   mint_ice_cream
 *  3     Duna    titan_core           rust_glaive         dust_jerky
 *  4     Ike     monolith_shard       obelisk_hammer      tomb_ration
 *  5     Dres    graphite_heart       control_rod_mace    glowing_stew
 *  6     Moho    molten_heart         flare_saber         lava_pepper
 *  7     Eve     schrabidic_crystal   hydra_fang          eve_grapes
 *  8     Laythe  leviathan_pearl      abyssal_harpoon     laythe_sushi
 *  9     Tekto   polymer_matrix       pvc_warhammer       synth_bar
 * 10     Sporos  queen_heart          mycelium_scythe     cordyceps_tea
 * </pre>
 * Der Wemburos-Boss (Stufe 11) bleibt beim Unstable Sovereign (Orbital Strike Rod).
 */
public final class ProgressionItems {

    private ProgressionItems() {}

    public static final Map<Stage, RegistrySupplier<Item>> CORES = new EnumMap<>(Stage.class);
    public static final Map<Stage, RegistrySupplier<Item>> WEAPONS = new EnumMap<>(Stage.class);
    public static final Map<Stage, RegistrySupplier<Item>> FOODS = new EnumMap<>(Stage.class);
    public static final List<RegistrySupplier<Item>> THRUSTERS = new ArrayList<>();

    private static RegistrySupplier<Item> core(Stage s, String id) {
        RegistrySupplier<Item> r = com.space_beyond.item.BeyondItems.register(id, () -> new ItemBossCore(s, new Item.Properties()), SpaceCreativeTabs.Tab.PARTS);
        CORES.put(s, r);
        return r;
    }

    private static RegistrySupplier<Item> weapon(Stage s, String id, net.minecraft.world.item.Tier tier, int dmg, float speed, int cd,
                                                 ItemPlanetWeapon.OnHit hit, ItemPlanetWeapon.Ability ability, int color,
                                                 java.util.function.Supplier<net.minecraft.core.particles.ParticleOptions> held) {
        RegistrySupplier<Item> r = com.space_beyond.item.BeyondItems.register(id, () -> new ItemPlanetWeapon(tier, dmg, speed, cd, hit, ability, color, held, new Item.Properties()),
                SpaceCreativeTabs.Tab.WEAPON);
        WEAPONS.put(s, r);
        return r;
    }

    private static RegistrySupplier<Item> food(Stage s, String id, FoodProperties food, boolean drink, int stack, ItemPlanetFood.Extra extra) {
        RegistrySupplier<Item> r = com.space_beyond.item.BeyondItems.register(id, () -> new ItemPlanetFood(new Item.Properties().food(food).stacksTo(stack), drink, extra),
                SpaceCreativeTabs.Tab.CONSUMABLE);
        FOODS.put(s, r);
        return r;
    }

    //? if < 1.21.1 {
    private static MobEffectInstance fx(net.minecraft.world.effect.MobEffect e, int sec, int amp) {
    //?} else {
    /*private static MobEffectInstance fx(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> e, int sec, int amp) {
    *///?}
        return new MobEffectInstance(e, sec * 20, amp);
    }

    // ── Kerne ──
    public static final RegistrySupplier<Item> SENTINEL_CORE = core(Stage.MUN, "sentinel_core");
    public static final RegistrySupplier<Item> CRYO_HEART = core(Stage.MINMUS, "cryo_heart");
    public static final RegistrySupplier<Item> TITAN_CORE = core(Stage.DUNA, "titan_core");
    public static final RegistrySupplier<Item> MONOLITH_SHARD = core(Stage.IKE, "monolith_shard");
    public static final RegistrySupplier<Item> GRAPHITE_HEART = core(Stage.DRES, "graphite_heart");
    public static final RegistrySupplier<Item> MOLTEN_HEART = core(Stage.MOHO, "molten_heart");
    public static final RegistrySupplier<Item> SCHRABIDIC_CRYSTAL = core(Stage.EVE, "schrabidic_crystal");
    public static final RegistrySupplier<Item> LEVIATHAN_PEARL = core(Stage.LAYTHE, "leviathan_pearl");
    public static final RegistrySupplier<Item> POLYMER_MATRIX = core(Stage.TEKTO, "polymer_matrix");
    public static final RegistrySupplier<Item> QUEEN_HEART = core(Stage.SPOROS, "queen_heart");

    // ── Waffen (Schaden zusaetzlich zum Stufenbonus, Angriffstempo, Abklingzeit der Faehigkeit in Ticks) ──
    public static final RegistrySupplier<Item> SHOCK_BATON = weapon(Stage.MUN, "shock_baton", Tiers.IRON, 5, -2.2F, 160,
            WeaponAbilities::shock, WeaponAbilities::emp, 0x40A0FF, () -> net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK);
    public static final RegistrySupplier<Item> FROSTBITE_CLEAVER = weapon(Stage.MINMUS, "frostbite_cleaver", Tiers.DIAMOND, 5, -2.6F, 200,
            WeaponAbilities::freeze, WeaponAbilities::frostNova, 0x60E8C8, () -> net.minecraft.core.particles.ParticleTypes.SNOWFLAKE);
    public static final RegistrySupplier<Item> RUST_GLAIVE = weapon(Stage.DUNA, "rust_glaive", Tiers.DIAMOND, 6, -2.8F, 120,
            WeaponAbilities::rust, WeaponAbilities::sandDash, 0xE07830, () -> WeaponAbilities.dust(0xC2603A, 0.9F));
    public static final RegistrySupplier<Item> OBELISK_HAMMER = weapon(Stage.IKE, "obelisk_hammer", Tiers.DIAMOND, 8, -3.1F, 240,
            WeaponAbilities::gravity, WeaponAbilities::gravityWell, 0xC070FF, () -> net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL);
    public static final RegistrySupplier<Item> CONTROL_ROD_MACE = weapon(Stage.DRES, "control_rod_mace", Tiers.NETHERITE, 6, -2.9F, 300,
            WeaponAbilities::radiate, WeaponAbilities::meltdownPulse, 0x70FF40, () -> WeaponAbilities.dust(0x7CFF40, 0.8F));
    public static final RegistrySupplier<Item> FLARE_SABER = weapon(Stage.MOHO, "flare_saber", Tiers.NETHERITE, 6, -2.2F, 100,
            WeaponAbilities::burn, WeaponAbilities::flareFan, 0xFF8020, () -> net.minecraft.core.particles.ParticleTypes.SMALL_FLAME);
    public static final RegistrySupplier<Item> HYDRA_FANG = weapon(Stage.EVE, "hydra_fang", Tiers.NETHERITE, 6, -1.8F, 120,
            WeaponAbilities::venom, WeaponAbilities::schrabBeam, 0x30E8D8, () -> WeaponAbilities.dust(0x30E8D8, 0.8F));
    public static final RegistrySupplier<Item> ABYSSAL_HARPOON = weapon(Stage.LAYTHE, "abyssal_harpoon", Tiers.NETHERITE, 8, -2.7F, 160,
            WeaponAbilities::harpoon, WeaponAbilities::tideDash, 0x40C8FF, () -> net.minecraft.core.particles.ParticleTypes.GLOW);
    public static final RegistrySupplier<Item> PVC_WARHAMMER = weapon(Stage.TEKTO, "pvc_warhammer", Tiers.NETHERITE, 11, -3.2F, 240,
            WeaponAbilities::crush, WeaponAbilities::chlorineCloud, 0xB8F040, () -> WeaponAbilities.dust(0xB8F040, 0.8F));
    public static final RegistrySupplier<Item> MYCELIUM_SCYTHE = weapon(Stage.SPOROS, "mycelium_scythe", Tiers.NETHERITE, 10, -2.6F, 300,
            WeaponAbilities::leech, WeaponAbilities::sporeBurst, 0xFFB040, () -> net.minecraft.core.particles.ParticleTypes.SPORE_BLOSSOM_AIR);

    // ── Essen ──
    public static final RegistrySupplier<Item> MUN_CHEESE = food(Stage.MUN, "mun_cheese",
            PlatformHooks.foodBuilder(6, 0.6F).effect(() -> fx(MobEffects.JUMP, 60, 1), 1.0F).build(), false, 64, null);
    public static final RegistrySupplier<Item> MINT_ICE_CREAM = food(Stage.MINMUS, "mint_ice_cream",
            PlatformHooks.foodBuilder(5, 0.5F).effect(() -> fx(MobEffects.MOVEMENT_SPEED, 60, 0), 1.0F)
                    .effect(() -> fx(MobEffects.FIRE_RESISTANCE, 60, 0), 1.0F).build(), false, 16, null);
    public static final RegistrySupplier<Item> DUST_JERKY = food(Stage.DUNA, "dust_jerky",
            PlatformHooks.foodBuilder(7, 0.8F).effect(() -> fx(MobEffects.DAMAGE_RESISTANCE, 45, 0), 1.0F).build(), false, 64, null);
    public static final RegistrySupplier<Item> TOMB_RATION = food(Stage.IKE, "tomb_ration",
            PlatformHooks.foodBuilder(8, 1.0F).effect(() -> fx(MobEffects.ABSORPTION, 120, 1), 1.0F).build(), false, 16, null);
    public static final RegistrySupplier<Item> GLOWING_STEW = food(Stage.DRES, "glowing_stew",
            PlatformHooks.foodBuilder(10, 1.0F).effect(() -> fx(MobEffects.NIGHT_VISION, 120, 0), 1.0F)
                    .effect(() -> fx(MobEffects.REGENERATION, 10, 1), 1.0F).effect(() -> fx(MobEffects.CONFUSION, 5, 0), 0.5F).build(), false, 8, null);
    public static final RegistrySupplier<Item> LAVA_PEPPER = food(Stage.MOHO, "lava_pepper",
            PlatformHooks.foodBuilder(3, 0.3F).fast().effect(() -> fx(MobEffects.FIRE_RESISTANCE, 180, 0), 1.0F)
                    .effect(() -> fx(MobEffects.DAMAGE_BOOST, 30, 0), 1.0F).build(), false, 64, null);
    public static final RegistrySupplier<Item> EVE_GRAPES = food(Stage.EVE, "eve_grapes",
            PlatformHooks.foodBuilder(4, 0.4F).fast().effect(() -> fx(MobEffects.DIG_SPEED, 60, 1), 1.0F)
                    .effect(() -> fx(MobEffects.REGENERATION, 20, 0), 1.0F).build(), false, 64, null);
    public static final RegistrySupplier<Item> LAYTHE_SUSHI = food(Stage.LAYTHE, "laythe_sushi",
            PlatformHooks.foodBuilder(8, 0.8F).effect(() -> fx(MobEffects.WATER_BREATHING, 300, 0), 1.0F)
                    .effect(() -> fx(MobEffects.DOLPHINS_GRACE, 60, 0), 1.0F).build(), false, 64, null);
    public static final RegistrySupplier<Item> SYNTH_BAR = food(Stage.TEKTO, "synth_bar",
            PlatformHooks.foodBuilder(12, 1.2F).effect(() -> fx(MobEffects.DAMAGE_BOOST, 60, 0), 1.0F).build(), false, 32, null);
    public static final RegistrySupplier<Item> CORDYCEPS_TEA = food(Stage.SPOROS, "cordyceps_tea",
            PlatformHooks.foodBuilder(6, 0.6F).effect(() -> fx(MobEffects.REGENERATION, 30, 1), 1.0F).build(), true, 16,
            (level, eater) -> SporeInfection.apply(eater, 0F));

    // ── Triebwerke Mk.II - Mk.XI ──
    static {
        for (int tier = ProgressionParts.FIRST; tier <= ProgressionParts.LAST; tier++) {
            THRUSTERS.add(com.space_beyond.item.BeyondItems.register(ProgressionParts.id(tier), () -> new ItemRocketPart(new Item.Properties(), 1F, 100_000F),
                    SpaceCreativeTabs.Tab.MISSILE));
        }
    }

    /** Peilt den Boss-Dungeon des aktuellen Planeten an. */
    public static final RegistrySupplier<Item> DUNGEON_LOCATOR = com.space_beyond.item.BeyondItems.register("dungeon_locator",
            () -> new ItemDungeonLocator(new Item.Properties().stacksTo(1)), SpaceCreativeTabs.Tab.CONTROL);

    private static boolean initialized;

    /** Aus {@code SpaceItems.init()} (vor {@code ITEMS.register()}). */
    public static void init() {
        if (initialized) return;
        initialized = true;
        SpaceProgression.init();
        com.space_beyond.progression.fx.WeaponFx.init(); // Schwungboegen/Ringe der Planetenwaffen
        com.space_beyond.dim.dungeon.design.PlanetDesigns.init(); // Boss-Dungeons je Planet
    }
}
