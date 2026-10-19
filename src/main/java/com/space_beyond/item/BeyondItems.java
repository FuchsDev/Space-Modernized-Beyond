package com.space_beyond.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.space_beyond.BeyondMod;
import com.space_beyond.main.BeyondTabs;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

/** Items von Beyond (Namensraum {@code space_beyond}); Helfer wie {@code com.space_m.item.SpaceItems}. */
public final class BeyondItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BeyondMod.MOD_ID, Registries.ITEM);

    private static final List<RegistrySupplier<Item>> GENERATED_MODELS = new ArrayList<>();

    /** Golfschlaeger (Nutzerwunsch): Waffen-Tab und Vanilla-Kampf-Tab, Hand-JSON (handheld). */
    public static final RegistrySupplier<Item> GOLF_CLUB = register("golf_club",
            () -> new com.space_beyond.item.ItemGolfClub(new Item.Properties().stacksTo(1)), SpaceCreativeTabs.Tab.WEAPON);

    private BeyondItems() {}

    public static <T extends Item> RegistrySupplier<T> register(String name, Supplier<T> item, SpaceCreativeTabs.Tab tab) {
        RegistrySupplier<T> reg = ITEMS.register(name, item);
        if (tab != null) BeyondTabs.add(tab, reg);
        return reg;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> RegistrySupplier<T> basic(String name, Supplier<T> item, SpaceCreativeTabs.Tab tab) {
        RegistrySupplier<T> reg = register(name, item, tab);
        GENERATED_MODELS.add((RegistrySupplier<Item>) (RegistrySupplier<?>) reg);
        return reg;
    }

    public static List<RegistrySupplier<Item>> generatedModels() { return Collections.unmodifiableList(GENERATED_MODELS); }

    public static void init() {
        // Vanilla-Kampf-Tab ueber das Loader-Ereignis (Architectury-modify stuerzt auf NeoForge ab, siehe SpaceTabFill)
        com.space_m.main.SpaceTabFill.modify(net.minecraft.world.item.CreativeModeTabs.COMBAT, "BeyondItems.GOLF_CLUB",
                sink -> sink.accept(new net.minecraft.world.item.ItemStack(GOLF_CLUB.get()), String.valueOf(GOLF_CLUB.getId())));
        // Butterschwert von Space Modernized zusaetzlich in den HBM-Waffen-Tab (dort nur im Vanilla-Kampf-Tab)
        BeyondTabs.add(SpaceCreativeTabs.Tab.WEAPON, com.space_m.item.SpaceMaterialItems.BUTTER_SWORD);
        ITEMS.register();
    }
}
