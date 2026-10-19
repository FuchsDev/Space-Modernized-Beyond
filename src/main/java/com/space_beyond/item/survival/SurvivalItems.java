package com.space_beyond.item.survival;

import java.util.List;

import com.space_m.entity.SpaceEntities;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;
import com.space_m.main.SpaceTabFill;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/**
 * Ueberlebens-Ausruestung fuer Sporos (eigene Erweiterung, Nutzerwunsch im Stil von Survival-Spielen mit Infizierten,
 * eigene Namen):
 * <ul>
 *   <li>Essen: {@code canned_beans} Dosenbohnen (6/0,6, nur Beute), {@code jerky} Trockenfleisch (4/0,4, schnell; aus
 *       verrottetem Fleisch im Raeucherofen/Lagerfeuer), {@code ration_bar} Notration (5/0,5; Weizen + Zucker + Seetang).</li>
 *   <li>Verbrauch: {@code alcohol} Reinigungsalkohol (Zutat; trinkbar: Uebelkeit, heilt Vergiftung), {@code field_kit}
 *       Feldverband (Lappen + Alkohol; 3 s anlegen, heilt 10 Herzen), {@code molotov} (Alkohol + Lappen; wirft Feuer),
 *       {@code nail_bomb} Nagelbombe (Schwarzpulver + Eisennuggets + Faden; Splitterschaden ohne Blockschaden).</li>
 *   <li>Waffen (Addon-Tab "NTM Space: Weapons" + Vanilla-Kampf-Tab): {@code shiv} Shiv (Eisennugget + Faden; 5 Einsaetze, 3 Schaden, schleichend von
 *       hinten toetet er Blindhuete sofort), {@code machete} (Eisen, 6 Schaden, schnell), {@code bat} Holzschlaeger
 *       (4 Schaden, Rueckstoss), {@code nail_bat} Nagelschlaeger (Schlaeger + Nuggets; 7 Schaden, langsam, Verwesung).</li>
 * </ul>
 * Beute in den Stadt-Tabellen ({@code chests/sporos_city/*}). Wurfgeschosse: {@link EntityMolotov}, {@link EntityNailBomb}
 * (Renderer {@code ThrownItemRenderer} in {@code MobClient}).
 */
public final class SurvivalItems {

    private SurvivalItems() {}

    // ── Essen ──
    public static final RegistrySupplier<Item> CANNED_BEANS = com.space_beyond.item.BeyondItems.register("canned_beans",
            () -> new Item(new Item.Properties().food(com.hbm_m.platform.PlatformHooks.foodBuilder(6, 0.6F).build())), SpaceCreativeTabs.Tab.CONSUMABLE);
    public static final RegistrySupplier<Item> JERKY = com.space_beyond.item.BeyondItems.register("jerky",
            () -> new Item(new Item.Properties().food(com.hbm_m.platform.PlatformHooks.foodBuilder(4, 0.4F).fast().build())), SpaceCreativeTabs.Tab.CONSUMABLE);
    public static final RegistrySupplier<Item> RATION_BAR = com.space_beyond.item.BeyondItems.register("ration_bar",
            () -> new Item(new Item.Properties().food(com.hbm_m.platform.PlatformHooks.foodBuilder(5, 0.5F).build())), SpaceCreativeTabs.Tab.CONSUMABLE);

    // ── Verbrauch ──
    public static final RegistrySupplier<Item> ALCOHOL = com.space_beyond.item.BeyondItems.register("alcohol", () -> new ItemAlcohol(new Item.Properties().stacksTo(16)), SpaceCreativeTabs.Tab.CONSUMABLE);
    public static final RegistrySupplier<Item> FIELD_KIT = com.space_beyond.item.BeyondItems.register("field_kit", () -> new ItemFieldKit(new Item.Properties().stacksTo(8)), SpaceCreativeTabs.Tab.CONSUMABLE);
    // Wurfwaffen: Tab.WEAPON -> Addon-Tab "NTM Space: Weapons" (SpaceCreativeTabs.categorize ordnet nach Original-Tab)
    public static final RegistrySupplier<Item> MOLOTOV = com.space_beyond.item.BeyondItems.register("molotov",
            () -> new ItemThrowable(new Item.Properties().stacksTo(8), (level, shooter) -> new EntityMolotov(level, shooter), 1.2F), SpaceCreativeTabs.Tab.WEAPON);
    public static final RegistrySupplier<Item> NAIL_BOMB = com.space_beyond.item.BeyondItems.register("nail_bomb",
            () -> new ItemThrowable(new Item.Properties().stacksTo(8), (level, shooter) -> new EntityNailBomb(level, shooter), 1.1F), SpaceCreativeTabs.Tab.WEAPON);

    // ── Waffen (Tab.WEAPON -> "NTM Space: Weapons"; zusaetzlich Vanilla-Kampf-Tab, siehe init) ──
    public static final RegistrySupplier<Item> SHIV = com.space_beyond.item.BeyondItems.register("shiv", () -> new ItemShiv(new Item.Properties()), SpaceCreativeTabs.Tab.WEAPON);
    public static final RegistrySupplier<Item> MACHETE = com.space_beyond.item.BeyondItems.register("machete",
            () -> new ItemSurvivalWeapon(Tiers.IRON, 3, -2.2F, 0F, 0, new Item.Properties()), SpaceCreativeTabs.Tab.WEAPON);
    public static final RegistrySupplier<Item> BAT = com.space_beyond.item.BeyondItems.register("bat",
            () -> new ItemSurvivalWeapon(Tiers.WOOD, 3, -2.6F, 0.6F, 0, new Item.Properties()), SpaceCreativeTabs.Tab.WEAPON);
    public static final RegistrySupplier<Item> NAIL_BAT = com.space_beyond.item.BeyondItems.register("nail_bat",
            () -> new ItemSurvivalWeapon(Tiers.STONE, 5, -2.8F, 0.3F, 60, new Item.Properties()), SpaceCreativeTabs.Tab.WEAPON);

    public static final List<RegistrySupplier<Item>> WEAPONS = List.of(SHIV, MACHETE, BAT, NAIL_BAT);

    // ── Wurfgeschosse ──
    public static final RegistrySupplier<EntityType<EntityMolotov>> MOLOTOV_ENTITY = com.space_beyond.entity.BeyondEntities.register("molotov",
            () -> EntityType.Builder.<EntityMolotov>of(EntityMolotov::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    public static final RegistrySupplier<EntityType<EntityNailBomb>> NAIL_BOMB_ENTITY = com.space_beyond.entity.BeyondEntities.register("nail_bomb",
            () -> EntityType.Builder.<EntityNailBomb>of(EntityNailBomb::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

    private static boolean initialized;

    /** Aus {@code SpaceItems.init()} (vor {@code ITEMS.register()} und {@code ENTITIES.register()}). */
    public static void init() {
        if (initialized) return;
        initialized = true;
        SpaceTabFill.modify(CreativeModeTabs.COMBAT, "SurvivalItems",
                sink -> {
                    for (RegistrySupplier<Item> w : WEAPONS) sink.accept(new ItemStack(w.get()), String.valueOf(w.getId()));
                });
    }

    /** Tooltip-Schaden eines Schwert-Items (Modifikator + Stufenbonus + Grundschaden 1 des Spielers). */
    public static float tooltipDamage(Item item) {
        //? if < 1.21.1 {
        return item instanceof SwordItem sword ? sword.getDamage() + 1F : 0F;
        //?} else {
        /*return 0F;
        *///?}
    }
}
