package com.space_beyond.dim.dungeon.design;

import com.hbm_m.block.ModBlocks;
import com.space_beyond.dim.dungeon.PlanetDungeons;
import com.space_beyond.dim.dungeon.SealedDungeon;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Registrierung der zehn Planeten-Dungeons. Mun behaelt den versiegelten Hallen-Grundriss ({@link SealedDungeon}),
 * die uebrigen haben je einen eigenen, frei gestalteten Bauplan ({@link com.space_beyond.dim.dungeon.CustomDungeon}):
 * <pre>
 *  Planet  Bauwerk                           Boss
 *  Mun     Mondstation (Hallen + 3 Fluegel)  Lunar Sentinel
 *  Minmus  Eis-Observatorium + Kristall-Geode Cryo Behemoth
 *  Duna    Rost-Giesserei mit Schornsteinen   Rust Titan
 *  Ike     Monolith-Nekropole mit Zikkurat    Monolith Keeper
 *  Dres    Kernschmelze-Kraftwerk, Kuehlturm  Core Abomination
 *  Moho    Vulkanschmiede mit Magmakammer     Magma Wyrm
 *  Eve     Schrabidium-Kathedrale             Schrabidium Hydra
 *  Laythe  Tiefsee-Forschungsstation          Abyssal Leviathan
 *  Tekto   Polymer-Raffinerie                 Polymer Colossus
 *  Sporos  Der Stock (Pilzhuegel)             Mycelial Queen
 * </pre>
 */
public final class PlanetDesigns {

    private PlanetDesigns() {}

    private static boolean initialized;

    public static void init() {
        if (initialized) return;
        initialized = true;
        PlanetDungeons.register("mun", new Mun());
        PlanetDungeons.register("minmus", new MinmusDungeon());
        PlanetDungeons.register("duna", new DunaDungeon());
        PlanetDungeons.register("ike", new IkeDungeon());
        PlanetDungeons.register("dres", new DresDungeon());
        PlanetDungeons.register("moho", new MohoDungeon());
        PlanetDungeons.register("eve", new EveDungeon());
        PlanetDungeons.register("laythe", new LaytheDungeon());
        PlanetDungeons.register("tekto", new TektoDungeon());
        PlanetDungeons.register("sporos", new SporosDungeon());
    }

    static BlockState b(Block block) {
        return block.defaultBlockState();
    }

    // ── 1 Mun ───────────────────────────────────────────────────────────────

    static final class Mun extends SealedDungeon {
        @Override public Stage stage() { return Stage.MUN; }
        @Override public BlockState surface() { return b(com.space_m.dim.moon.MunBlocks.MOON_ROCK.get()); }
        @Override public BlockState filler() { return surface(); }
        @Override protected Theme theme() {
            return new Theme(b(ModBlocks.CONCRETE.get()), b(ModBlocks.CONCRETE_CRACKED.get()), b(ModBlocks.TILE_LAB.get()), b(ModBlocks.DECO_STEEL.get()),
                    b(ModBlocks.BRICK_CONCRETE.get()), b(ModBlocks.CONCRETE_HAZARD.get()), b(Blocks.SMOOTH_STONE), b(Blocks.SEA_LANTERN),
                    b(ModBlocks.DECO_STEEL.get()), b(ModBlocks.REINFORCED_GLASS.get()), Blocks.STONE_BRICK_STAIRS, Blocks.SMOOTH_STONE_SLAB,
                    b(Blocks.MAGMA_BLOCK), b(Blocks.MAGMA_BLOCK), b(ModBlocks.TILE_LAB_BROKEN.get()));
        }
        @Override protected Wing[] wings() { return new Wing[] {Wing.GAUNTLET, Wing.MAZE, Wing.PARKOUR}; }
        @Override protected String[] roomMobs() { return new String[] {"space_beyond:lost_astronaut", "space_beyond:moon_drone", "space_beyond:lost_astronaut"}; }
        @Override protected String[] spawnerMobs() { return new String[] {"space_beyond:moon_drone", "space_beyond:lost_astronaut"}; }
        @Override protected String eliteMob() { return "space_beyond:lost_astronaut"; }
        @Override protected String boss() { return "space_beyond:lunar_sentinel"; }

        @Override
        protected void entranceDeco(Draw d) {
            // Funkmast mit Abspannung und Warnlicht, Solarfeld
            for (int y = 1; y <= 22; y++) d.set(16, y, 52, y % 5 == 0 ? b(ModBlocks.CONCRETE_HAZARD.get()) : b(Blocks.IRON_BARS));
            d.set(16, 23, 52, b(Blocks.REDSTONE_LAMP));
            d.set(16, 24, 52, b(Blocks.LIGHTNING_ROD));
            for (int u = -26; u <= -14; u++) for (int v = 50; v <= 60; v += 3) {
                d.set(u, 1, v, b(Blocks.IRON_BARS));
                d.set(u, 2, v, b(Blocks.DAYLIGHT_DETECTOR));
            }
        }

        @Override
        protected void arenaDeco(Draw d) {
            // Notlandekapsel (schraeg im Boden) und Kabeltrassen
            for (int y = 0; y < 6; y++) {
                double r = 2.6 - y * 0.35;
                int cu = -8, cv = ARENA_V + 7 - y / 2;
                for (int u = cu - 3; u <= cu + 3; u++) for (int v = cv - 3; v <= cv + 3; v++) {
                    if ((u - cu) * (u - cu) + (v - cv) * (v - cv) <= r * r) d.set(u, ARENA_FLOOR + 1 + y, v, y == 5 ? b(Blocks.SEA_LANTERN) : b(ModBlocks.DECO_STEEL.get()));
                }
            }
            d.set(-6, ARENA_FLOOR + 2, ARENA_V + 8, b(ModBlocks.REINFORCED_GLASS.get()));
            for (int u = -15; u <= 15; u++) d.set(u, ARENA_FLOOR, ARENA_V + 12, b(ModBlocks.CONCRETE_HAZARD.get()));
        }
    }
}
