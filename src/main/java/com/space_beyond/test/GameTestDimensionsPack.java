package com.space_beyond.test;

// Nur NeoForge 1.21.1. Der GameTestServer baut seine Welt aus dem Flachland-Preset mit leerer LEVEL_STEM-Liste
// (WorldDimensions.bake) und verwirft so die Datapack-Dimensionen. Mit -Dspace_beyond.gametestDimensions=true (nur
// runs.gameTestServer in build.neoforge.gradle.kts) meldet Beyond den eingebauten Pack "gametest_dimensions" an; dessen
// minecraft:flat-Preset enthaelt die Dimensionen von Space Modernized und Beyond (erzeugt in processResources).
// Normale Welten und Server sind nicht betroffen. Forge 1.20.1 laedt die Datapack-Dimensionen im GameTestServer bereits.
//? if neoforge {
/*import com.space_beyond.BeyondMod;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

@net.neoforged.fml.common.EventBusSubscriber(modid = BeyondMod.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
public final class GameTestDimensionsPack {

    public static final String PROPERTY = "space_beyond.gametestDimensions";

    private GameTestDimensionsPack() {}

    @net.neoforged.bus.api.SubscribeEvent
    public static void onAddPackFinders(net.neoforged.neoforge.event.AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA || !Boolean.getBoolean(PROPERTY)) return;
        event.addPackFinders(ResourceLocation.fromNamespaceAndPath(BeyondMod.MOD_ID, "gametest_dimensions"), PackType.SERVER_DATA,
                Component.literal("space_beyond GameTest dimensions"), PackSource.BUILT_IN, true, Pack.Position.TOP);
    }
}
*///?}
