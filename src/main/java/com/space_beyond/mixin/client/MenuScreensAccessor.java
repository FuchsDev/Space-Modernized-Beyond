package com.space_beyond.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;

/** Bildschirm-Fabriken der Menues; Beyond tauscht die des StarDar gegen die neue Sternkarte (doppelt anmelden wirft). */
@Mixin(MenuScreens.class)
public interface MenuScreensAccessor {
    @Accessor("SCREENS")
    static Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> beyond$screens() {
        throw new AssertionError();
    }
}
