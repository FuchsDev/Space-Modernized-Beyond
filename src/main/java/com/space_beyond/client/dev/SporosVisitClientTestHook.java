// neo-pendant: keins - reines Entwickler-Werkzeug fuer den 1.20.1-Forge-Dev-Client
//? if forge {
package com.space_beyond.client.dev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import com.space_m.SpaceMod;
import com.space_m.dim.SpaceDimensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Entwickler-Werkzeug (nur mit {@code -Dspace.sporosTest=true}): Kreativwelt anlegen, per {@code /space tp sporos} auf
 * den Planeten Sporos, 300 Ticks dort bleiben (Gelaende/Himmel/Nebel rendern), Bildschirmfoto
 * {@code screenshots/sporos_test.png}, Ergebnis im Log ({@code [SPACE SporosTest]}), dann Spiel beenden.
 * Mit {@code -Dspace.sporosCity=true} statt des einen Bilds ein Rundgang durch die naechste Stadt (Runde 3):
 * Strasse, Vogelperspektive, Hochhaus-Etage, Draufsicht, Stadtrand ({@code screenshots/sporos_city_*.png}).
 */
@Mod.EventBusSubscriber(modid = com.space_beyond.BeyondMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SporosVisitClientTestHook {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("space_m SporosTest");

    private static final boolean ENABLED = Boolean.getBoolean("space.sporosTest");

    private static int phase; // 0 Titel, 1 Welt laedt, 2 Teleport, 3 auf Sporos, 4 fertig
    private static int ticks;

    private SporosVisitClientTestHook() { }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.END || phase == 4) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            switch (phase) {
                case 0 -> {
                    if (mc.getOverlay() == null && mc.screen instanceof TitleScreen && ++ticks > 20) {
                        startWorld(mc);
                        ticks = 0;
                        phase = 1;
                    }
                }
                case 1 -> {
                    if (mc.level != null && mc.player != null && mc.getOverlay() == null) {
                        if (mc.screen != null && !(mc.screen instanceof PauseScreen)) {
                            ticks = 0;
                        } else if (++ticks > 60) {
                            if (mc.screen != null) mc.setScreen(null);
                            MinecraftServer server = mc.getSingleplayerServer();
                            server.execute(() -> server.getCommands().performPrefixedCommand(
                                    server.createCommandSourceStack().withEntity(server.getPlayerList().getPlayers().get(0))
                                            .withPermission(4), "space tp sporos"));
                            LOGGER.info("[SPACE SporosTest] /space tp sporos ausgefuehrt");
                            ticks = 0;
                            phase = 2;
                        }
                    }
                }
                case 2 -> {
                    if (mc.level != null && mc.level.dimension() == com.space_beyond.dim.BeyondDimensions.SPOROS && mc.player != null) {
                        LOGGER.info("[SPACE SporosTest] in Dimension {} bei {}", mc.level.dimension().location(), mc.player.blockPosition());
                        ticks = 0;
                        phase = 3;
                    } else if (++ticks > 1200) {
                        LOGGER.error("[SPACE SporosTest] FEHLER: Sporos nicht erreicht (Dimension {})", mc.level == null ? null : mc.level.dimension().location());
                        finish(mc);
                    }
                }
                case 3 -> {
                    if (mc.screen != null && !(mc.screen instanceof PauseScreen)) mc.setScreen(null);
                    if (CITY) {
                        ticks = 0;
                        phase = 10;
                        break;
                    }
                    if (FLORA) { // R2b: Biom-Rundgang
                        ticks = 0;
                        phase = 20;
                        break;
                    }
                    ticks++;
                    if (ticks == 200) mc.player.setXRot(10F);
                    if (ticks == 300) {
                        var pos = mc.player.blockPosition();
                        var biome = mc.level.getBiome(pos).unwrapKey().map(k -> k.location().toString()).orElse("?");
                        LOGGER.info("[SPACE SporosTest] OK: lebendig={} Position={} Biom={} Boden={} Himmel={} Nebelfarbe-Dimension={}",
                                mc.player.isAlive(), pos, biome, mc.level.getBlockState(pos.below()).getBlock(),
                                mc.level.getSkyColor(mc.player.position(), 1F), mc.level.dimensionType().effectsLocation());
                        Screenshot.grab(mc.gameDirectory, "sporos_test.png", mc.getMainRenderTarget(), c -> LOGGER.info("[SPACE SporosTest] {}", c.getString()));
                    }
                    if (ticks > 340) finish(mc);
                }
                default -> {
                    if (phase >= 20) floraTour(mc);
                    else if (phase >= 10) cityTour(mc);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("[SPACE SporosTest] abgebrochen", t);
            finish(mc);
        }
    }

    // ── Staedte-Rundgang (-Dspace.sporosCity=true, Runde 3) ─────────────────────────────────

    private static final boolean CITY = Boolean.getBoolean("space.sporosCity");
    private static final String[] SHOTS = {"street", "street2", "above", "inside", "inside2", "top", "edge"};
    private static int shot;
    private static double[] view; // x, y, z, yaw, pitch

    private static void cmd(Minecraft mc, String command) {
        MinecraftServer server = mc.getSingleplayerServer();
        server.execute(() -> server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withEntity(server.getPlayerList().getPlayers().get(0)).withPermission(4), command));
    }

    private static void cityTour(Minecraft mc) {
        ticks++;
        if (phase == 10) {
            if (ticks == 5) {
                mc.options.renderDistance().set(12);
                mc.options.hideGui = true;
                cmd(mc, "time set 5000");
                cmd(mc, "gamerule doDaylightCycle false");
                cmd(mc, "weather clear 99999");
                cmd(mc, "gamerule doWeatherCycle false");
            }
            if (ticks == 20) {
                long seed = com.space_m.dim.CelestialSeed.get();
                var city = com.space_beyond.dim.sporos.city.CityLocator.nearest(seed, (int) mc.player.getX(), (int) mc.player.getZ(), 4);
                if (city == null) {
                    LOGGER.error("[SPACE SporosTest] FEHLER: keine Stadt gefunden");
                    finish(mc);
                    return;
                }
                cityPlan = city;
                LOGGER.info("[SPACE SporosTest] Stadt bei {} / {} Radius {} Basis {}", city.originX(), city.originZ(), city.radiusChunks(),
                        com.space_beyond.dim.sporos.city.CityLocator.baseY(city));
                phase = 11;
                ticks = 0;
                shot = 0;
            }
            return;
        }
        // phase 11: je Aufnahme teleportieren, warten (Chunks laden/rendern), Bild, naechste
        var city = cityPlan;
        int base = com.space_beyond.dim.sporos.city.CityLocator.baseY(city);
        if (ticks == 1) {
            view = viewFor(SHOTS[shot], city, base);
            mc.player.getAbilities().flying = true;
            mc.player.onUpdateAbilities();
            cmd(mc, String.format(java.util.Locale.ROOT, "tp @s %.1f %.1f %.1f %.1f %.1f", view[0], view[1], view[2], view[3], view[4]));
        }
        if (ticks > 1 && ticks < 300) {
            mc.player.getAbilities().flying = true;
            mc.player.setDeltaMovement(0, 0, 0);
            mc.player.setYRot((float) view[3]);
            mc.player.setXRot((float) view[4]);
        }
        if (ticks == 300) {
            String name = "sporos_city_" + SHOTS[shot] + ".png";
            LOGGER.info("[SPACE SporosTest] Aufnahme {} bei {} Blick {} Boden {}", name, mc.player.blockPosition(), (int) view[3],
                    mc.level.getBlockState(mc.player.blockPosition().below()).getBlock());
            Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(), c -> LOGGER.info("[SPACE SporosTest] {}", c.getString()));
        }
        if (ticks > 320) {
            ticks = 0;
            if (++shot >= SHOTS.length) {
                LOGGER.info("[SPACE SporosTest] OK: Stadt-Rundgang fertig");
                finish(mc);
            }
        }
    }

    private static com.space_beyond.dim.sporos.city.SporosCities.City cityPlan;

    /** Kamerapositionen: x, y (Fuesse), z, Blickrichtung (Grad), Neigung. */
    private static double[] viewFor(String name, com.space_beyond.dim.sporos.city.SporosCities.City city, int base) {
        int ox = city.originX(), oz = city.originZ();
        double r = city.radiusChunks() * 16.0D;
        switch (name) {
            case "street" -> {
                net.minecraft.core.BlockPos p = com.space_beyond.dim.sporos.city.CityLocator.street(city);
                return new double[] {p.getX() + 0.5, p.getY() + 0.6, p.getZ() + 0.5, 180, 2};
            }
            case "street2" -> {
                return new double[] {ox - 40.5, base + 2.2, oz + 0.5, -90, 4};
            }
            case "above" -> {
                return new double[] {ox + 0.5, base + 45, oz + 60.5, 180, 32};
            }
            case "inside", "inside2" -> {
                net.minecraft.core.BlockPos p = com.space_beyond.dim.sporos.city.CityLocator.inside(city);
                if (p == null) p = com.space_beyond.dim.sporos.city.CityLocator.street(city);
                return new double[] {p.getX() + 0.5, p.getY() + 0.1, p.getZ() + 0.5, name.equals("inside") ? 135 : -45, 5};
            }
            case "top" -> {
                return new double[] {ox + 0.5, base + 130, oz + 0.5, 0, 89};
            }
            default -> { // edge
                return new double[] {ox + r * 0.78, base + 18, oz + 0.5, 90, 12};
            }
        }
    }

    // ── Biom-/Flora-Rundgang (-Dspace.sporosFlora=true, R2b) ─────────────────────────────────

    private static final boolean FLORA = Boolean.getBoolean("space.sporosFlora");
    private static final String[] FLORA_BIOMES = {"sporos_moss_forest", "sporos_fungal_grove", "sporos_spore_field", "sporos_highland",
            "sporos_swamp", "sporos_coast", "sporos_overgrown_plains", "sporos_lake", "sporos_river"};
    private static int floraShot;
    private static volatile net.minecraft.core.BlockPos floraTarget;
    private static volatile boolean floraSearching;

    /** Je Biom: naechste Stelle suchen (Serverthread), hinteleportieren, Chunks laden, Blick aus 6 m Hoehe, Bild. */
    private static void floraTour(Minecraft mc) {
        ticks++;
        if (phase == 20) {
            if (ticks == 5) {
                mc.options.renderDistance().set(12);
                mc.options.hideGui = true;
                cmd(mc, "time set 6000");
                cmd(mc, "gamerule doDaylightCycle false");
                cmd(mc, "weather clear 99999");
                cmd(mc, "gamerule doWeatherCycle false");
            }
            if (ticks == 20) {
                phase = 21;
                ticks = 0;
                floraShot = 0;
            }
            return;
        }
        String biome = FLORA_BIOMES[floraShot];
        if (ticks == 1) {
            floraTarget = null;
            floraSearching = true;
            MinecraftServer server = mc.getSingleplayerServer();
            var key = net.minecraft.resources.ResourceKey.create(Registries.BIOME, com.space_beyond.BeyondMod.id(biome));
            net.minecraft.core.BlockPos from = mc.player.blockPosition();
            server.execute(() -> {
                var level = server.getLevel(com.space_beyond.dim.BeyondDimensions.SPOROS);
                var found = level.findClosestBiome3d(h -> h.is(key), from, 2400, 32, 64);
                floraTarget = found == null ? null : found.getFirst();
                floraSearching = false;
            });
        }
        if (ticks > 1 && ticks < 40 && floraSearching) { ticks = 1; return; } // warten
        if (ticks == 2) {
            if (floraTarget == null) {
                LOGGER.warn("[SPACE SporosTest] Biom {} nicht in Reichweite", biome);
                ticks = 400;
            } else {
                mc.player.getAbilities().flying = true;
                mc.player.onUpdateAbilities();
                cmd(mc, String.format(java.util.Locale.ROOT, "tp @s %d 110 %d 35 20", floraTarget.getX(), floraTarget.getZ()));
                LOGGER.info("[SPACE SporosTest] Biom {} bei {}", biome, floraTarget);
            }
        }
        if (ticks > 2 && ticks < 420 && floraTarget != null) {
            mc.player.getAbilities().flying = true;
            mc.player.setDeltaMovement(0, 0, 0);
            if (ticks == 160 || ticks == 240) {
                int top = mc.level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, floraTarget.getX(), floraTarget.getZ());
                cmd(mc, String.format(java.util.Locale.ROOT, "tp @s %d %d %d 35 14", floraTarget.getX(), top + 5, floraTarget.getZ()));
            }
            mc.player.setYRot(35F);
            mc.player.setXRot(14F);
        }
        if (ticks == 400 && floraTarget != null) {
            String name = "sporos_biome_" + biome.replace("sporos_", "") + ".png";
            var p = mc.player.blockPosition();
            LOGGER.info("[SPACE SporosTest] Aufnahme {} bei {} Biom {} Boden {}", name, p,
                    mc.level.getBiome(p).unwrapKey().map(k -> k.location().toString()).orElse("?"), mc.level.getBlockState(p.below(6)).getBlock());
            Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(), c -> LOGGER.info("[SPACE SporosTest] {}", c.getString()));
        }
        if (ticks > 420) {
            ticks = 0;
            if (++floraShot >= FLORA_BIOMES.length) {
                LOGGER.info("[SPACE SporosTest] OK: Biom-Rundgang fertig");
                finish(mc);
            }
        }
    }

    private static void finish(Minecraft mc) {
        phase = 4;
        mc.stop();
    }

    private static void startWorld(Minecraft mc) {
        mc.options.pauseOnLostFocus = false;
        Path saves = mc.gameDirectory.toPath().resolve("saves");
        if (Files.isDirectory(saves)) {
            try (Stream<Path> s = Files.list(saves)) {
                s.filter(p -> p.getFileName().toString().startsWith("space_sporostest")).forEach(SporosVisitClientTestHook::deleteTree);
            } catch (IOException ignored) { }
        }
        String level = "space_sporostest_" + System.currentTimeMillis();
        GameRules rules = new GameRules();
        rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        LevelSettings settings = new LevelSettings(level, GameType.CREATIVE, false, Difficulty.PEACEFUL, true, rules,
                WorldDataConfiguration.DEFAULT);
        LOGGER.info("[SPACE SporosTest] lege Testwelt {} an", level);
        mc.createWorldOpenFlows().createFreshLevel(level, settings, new WorldOptions(0L, false, false),
                ra -> ra.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions());
    }

    private static void deleteTree(Path root) {
        try (Stream<Path> s = Files.walk(root)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }
}
//?}
