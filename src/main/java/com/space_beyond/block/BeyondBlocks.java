package com.space_beyond.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.space_beyond.BeyondMod;
import com.space_beyond.item.BeyondItems;
import com.space_beyond.main.BeyondTabs;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Bloecke von Beyond (Namensraum {@code space_beyond}). Helfer wie {@code com.space_m.block.SpaceBlocks}:
 * <ul>
 *   <li>{@link #register(String, Supplier)} - nur Block.</li>
 *   <li>{@link #registerWithItem(String, Supplier, SpaceCreativeTabs.Tab)} - Block + BlockItem im HBM-Tab.</li>
 *   <li>{@link #cube(String, Supplier, SpaceCreativeTabs.Tab)} - wie oben, Standardmodell cube_all + dropSelf-Loot.</li>
 * </ul>
 */
public final class BeyondBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BeyondMod.MOD_ID, Registries.BLOCK);

    private static final List<RegistrySupplier<Block>> SIMPLE_CUBES = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> DROP_SELF = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> DROP_PERSISTENT = new ArrayList<>();

    /**
     * Sternsysteme: Faltraum-Antrieb (interstellare Stationsreise). Einfacher Wuerfel (Modell {@code cube_all},
     * Textur {@code block/machine_fold_drive}), Werte wie die Stationstriebwerke.
     */
    public static final RegistrySupplier<com.space_beyond.orbit.interstellar.BlockFoldDrive> MACHINE_FOLD_DRIVE = cube("machine_fold_drive",
            () -> new com.space_beyond.orbit.interstellar.BlockFoldDrive(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(5.0F, 60.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(s -> 7)),
            SpaceCreativeTabs.Tab.MACHINE);

    private BeyondBlocks() {}

    public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block, SpaceCreativeTabs.Tab tab) {
        return registerWithItem(name, block, tab, b -> new BlockItem(b, new Item.Properties()));
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block, SpaceCreativeTabs.Tab tab,
                                                                       Function<T, ? extends Item> item) {
        RegistrySupplier<T> reg = BLOCKS.register(name, block);
        RegistrySupplier<Item> it = BeyondItems.ITEMS.register(name, () -> item.apply(reg.get()));
        if (tab != null) BeyondTabs.add(tab, it);
        return reg;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> cube(String name, Supplier<T> block, SpaceCreativeTabs.Tab tab) {
        RegistrySupplier<T> reg = registerWithItem(name, block, tab);
        SIMPLE_CUBES.add((RegistrySupplier<Block>) (RegistrySupplier<?>) reg);
        DROP_SELF.add((RegistrySupplier<Block>) (RegistrySupplier<?>) reg);
        return reg;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> dropsSelf(RegistrySupplier<T> reg) {
        DROP_SELF.add((RegistrySupplier<Block>) (RegistrySupplier<?>) reg);
        return reg;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> dropsPersistent(RegistrySupplier<T> reg) {
        DROP_PERSISTENT.add((RegistrySupplier<Block>) (RegistrySupplier<?>) reg);
        return reg;
    }

    public static List<RegistrySupplier<Block>> simpleCubes() { return Collections.unmodifiableList(SIMPLE_CUBES); }

    public static List<RegistrySupplier<Block>> dropSelf() { return Collections.unmodifiableList(DROP_SELF); }

    public static List<RegistrySupplier<Block>> dropPersistent() { return Collections.unmodifiableList(DROP_PERSISTENT); }

    public static void init() {
        BLOCKS.register();
    }
}
