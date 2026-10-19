package com.space_beyond.main;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.space_beyond.BeyondMod;
import com.space_m.main.SpaceCreativeTabs.Tab;
import com.space_m.main.SpaceTabFill;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.level.ItemLike;

/**
 * Creative-Tabs von Beyond: die Eintraege kommen wie bei Space Modernized in die neun HBM-Tabs (Original-Einordnung,
 * {@link Tab}), zusaetzlich zeigt der eigene Tab {@code space_beyond:beyond_tab} alle {@code space_beyond}-Items.
 */
public final class BeyondTabs {
    private static final Tab[] ORDER = {
            Tab.BLOCKS, Tab.PARTS, Tab.CONTROL, Tab.CONSUMABLE, Tab.WEAPON, Tab.MACHINE, Tab.TEMPLATE, Tab.MISSILE, Tab.NUKE };

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BeyondMod.MOD_ID, Registries.CREATIVE_MODE_TAB);

    /** Sammel-Tab "Space: Beyond" (Lang {@code itemGroup.space_beyond.beyond_tab}). */
    public static final RegistrySupplier<CreativeModeTab> BEYOND_TAB = TABS.register("beyond_tab",
            () -> CreativeTabRegistry.create(b -> b
                    .title(Component.translatable("itemGroup.space_beyond.beyond_tab"))
                    .icon(() -> new ItemStack(com.space_beyond.block.BeyondBlocks.MACHINE_FOLD_DRIVE.get()))
                    .displayItems((params, output) -> fillBeyondTab(st -> output.accept(st, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY)))));

    private static final Map<Tab, List<Supplier<ItemStack>>> ENTRIES = new EnumMap<>(Tab.class);
    private static boolean initialized;

    private BeyondTabs() {}

    /** Haengt ein Item/einen Block an den HBM-Tab an. */
    public static void add(Tab tab, Supplier<? extends ItemLike> item) {
        ENTRIES.computeIfAbsent(tab, t -> new ArrayList<>()).add(() -> new ItemStack(item.get()));
    }

    /** Haengt einen Stack (z.B. mit NBT-Variante) an den HBM-Tab an. */
    public static void addStack(Tab tab, Supplier<ItemStack> stack) {
        ENTRIES.computeIfAbsent(tab, t -> new ArrayList<>()).add(stack);
    }

    public static List<Supplier<ItemStack>> entries(Tab tab) {
        return ENTRIES.getOrDefault(tab, List.of());
    }

    public static void init() {
        if (initialized) return;
        initialized = true;
        TABS.register();
        for (Tab tab : Tab.values()) {
            SpaceTabFill.modify(tab.id(), "BeyondTabs." + tab.name(), sink -> {
                for (Supplier<ItemStack> s : entries(tab)) sink.accept(s.get(), "space_beyond");
            });
        }
    }

    /** Alle Eintraege der HBM-Tabs (Reihenfolge {@link #ORDER}), danach die uebrigen {@code space_beyond}-Items. */
    public static void fillBeyondTab(Consumer<ItemStack> out) {
        Set<ItemStack> seen = new ObjectLinkedOpenCustomHashSet<>(ItemStackLinkedSet.TYPE_AND_TAG);
        Set<Item> seenItems = new HashSet<>();
        Consumer<ItemStack> add = st -> {
            if (st == null || st.isEmpty() || !seen.add(st)) return;
            seenItems.add(st.getItem());
            out.accept(st);
        };
        for (Tab tab : ORDER) {
            for (Supplier<ItemStack> s : entries(tab)) add.accept(s.get());
        }
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            if (!BeyondMod.MOD_ID.equals(key.getNamespace()) || seenItems.contains(item)) continue;
            add.accept(new ItemStack(item));
        }
    }
}
