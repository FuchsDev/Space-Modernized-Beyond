package com.space_beyond.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hbm_m.platform.BlockHooks;
import com.space_m.SpaceMod;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.SpaceDimensions;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.Building;
import com.space_beyond.dim.sporos.city.CellPlan;
import com.space_beyond.dim.sporos.city.CityLayout;
import com.space_beyond.dim.sporos.city.CityLoot;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.city.SporosCityGenerator;
import com.space_beyond.dim.sporos.flora.SporosFlora;
import com.space_beyond.dim.sporos.genlayer.GenLayerSporosCity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if >= 1.21.1 {
/*import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Sternsysteme Runde 3: Staedtegenerator auf Sporos. Determinismus (zwei unabhaengige Generatoren, verschiedene
 * Reihenfolgen), Strassen-/Grundstuecksraster im Puffer, Stadtbiome im Stadtkreis ({@link GenLayerSporosCity}),
 * Sporos-Flora in der Stadt, Blockwahl-Whitelist (keine Erze, Speicherbloecke, Stahl-Deko; kein Rueckweg zu Metall
 * ueber Schredder & Co.), Loot-Tables laden und wuerfeln, Truhen in der echten Dimension tragen ihre Loot-Table.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosCityGameTest {

    private SporosCityGameTest() {}

    private static final long SEED = 1234567L;

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static ChunkProviderCelestial provider(long seed) {
        CelestialGenerators.BodyGen gen = CelestialGenerators.require(SporosBlocks.BODY);
        return gen.chunkProvider().apply(world(seed), seed);
    }

    private static WorldChunkManagerCelestial world(long seed) {
        return CelestialGenerators.require(SporosBlocks.BODY).chunkManager().apply(seed);
    }

    /** Erste Stadt (nach Bezirk) mit Radius >= {@code minRadius}. */
    private static SporosCities.City city(long seed, int minRadius) {
        for (int d = 0; d < 400; d++) {
            SporosCities.City c = SporosCities.cityOfDistrict(seed, d % 20 - 10, d / 20 - 10);
            if (c != null && c.radiusChunks() >= minRadius) return c;
        }
        throw new GameTestAssertException("keine Stadt gefunden");
    }

    private static int idx(int lx, int y, int lz) {
        return (lx * 16 + lz) * 256 + y;
    }

    /** Gleicher Seed -> gleiche Bloecke, unabhaengig von Generator-Instanz und Chunk-Reihenfolge. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 600)
    public static void cityDeterminism(GameTestHelper helper) {
        check(SporosCities.generator() == SporosCityGenerator.INSTANCE, "Generator angemeldet");
        SporosCities.City city = city(SEED, 8);
        ChunkProviderCelestial a = provider(SEED), b = provider(SEED);
        int n = 4;
        BlockState[][] first = new BlockState[(2 * n + 1) * (2 * n + 1)][];
        int i = 0;
        for (int dx = -n; dx <= n; dx++) for (int dz = -n; dz <= n; dz++) {
            first[i++] = a.provideChunk(city.centerChunkX() + dx, city.centerChunkZ() + dz).blocks.clone();
        }
        // andere Instanz, umgekehrte Reihenfolge
        for (int dx = n; dx >= -n; dx--) for (int dz = n; dz >= -n; dz--) {
            int k = (dx + n) * (2 * n + 1) + (dz + n);
            BlockState[] again = b.provideChunk(city.centerChunkX() + dx, city.centerChunkZ() + dz).blocks;
            for (int j = 0; j < again.length; j++) {
                if (first[k][j] != again[j]) {
                    throw new GameTestAssertException("Chunk " + (city.centerChunkX() + dx) + "/" + (city.centerChunkZ() + dz)
                            + " weicht ab bei Index " + j + ": " + first[k][j] + " / " + again[j]);
                }
            }
        }
        // derselbe Generator ein zweites Mal
        BlockState[] c1 = a.provideChunk(city.centerChunkX(), city.centerChunkZ()).blocks;
        check(java.util.Arrays.equals(c1, first[(n) * (2 * n + 1) + n]), "zweiter Lauf derselben Instanz");
        helper.succeed();
    }

    /** Raster: Strassen auf Strassenniveau, Grundstuecke dazwischen, Gebaeude mehrstoeckig, Stufen im Treppenhaus. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 600)
    public static void cityGrid(GameTestHelper helper) {
        CityPalette p = CityPalette.get();
        SporosCities.City city = city(SEED, 8);
        CityLayout layout = CityLayout.of(city);
        ChunkProviderCelestial prov = provider(SEED);
        WorldChunkManagerCelestial world = world(SEED);
        Set<BlockState> streetish = Set.of(p.asphalt, p.asphaltPatch, p.lineYellow, p.lineWhite, p.manhole);
        int streetCols = 0, asphalt = 0, lotCols = 0, lotSolid = 0, tall = 0, stairs = 0, flora = 0, vanillaTrees = 0;
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {
            int cx = city.centerChunkX() + dx, cz = city.centerChunkZ() + dz;
            BlockState[] blocks = prov.provideChunk(cx, cz).blocks;
            for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
                int x = cx * 16 + lx, z = cz * 16 + lz;
                if (layout.edge(x, z) > 0.55) continue;
                CellPlan cell = layout.cell(world, layout.cellX(x), layout.cellZ(z));
                int g = cell.groundY();
                int clx = x - cell.minX, clz = z - cell.minZ;
                boolean street = clx < CityLayout.STREET || clz < CityLayout.STREET;
                check(street == SporosCities.isStreet(city, x, z), "isStreet stimmt mit Zellraster ueberein bei " + x + "/" + z);
                BlockState top = blocks[idx(lx, g, lz)];
                if (street) {
                    streetCols++;
                    if (top != null && streetish.contains(top)) asphalt++;
                } else {
                    lotCols++;
                    if (top != null && !top.isAir()) lotSolid++;
                }
                int h = 0;
                for (int y = g + 1; y < 256; y++) if (blocks[idx(lx, y, lz)] != null && !blocks[idx(lx, y, lz)].isAir()) h = y - g;
                if (h >= 12) tall++;
                for (int y = g - 4; y < g + 40; y++) {
                    BlockState s = blocks[idx(lx, y, lz)];
                    if (s == null) continue;
                    if (s.getBlock() instanceof net.minecraft.world.level.block.StairBlock) stairs++;
                    if (s.is(SporosFlora.HYPHE_LOG.get()) || s.is(SporosFlora.DEAD_HYPHE_LOG.get()) || s.is(SporosFlora.FUNGAL_STEM.get())
                            || s.is(SporosFlora.MYCEL_VINE.get()) || s.is(SporosFlora.GLOW_CAP.get())) flora++;
                    if (s.is(net.minecraft.tags.BlockTags.LOGS) && !s.is(SporosFlora.HYPHE_LOG.get()) && !s.is(SporosFlora.DEAD_HYPHE_LOG.get())) vanillaTrees++;
                    if (s.is(Blocks.VINE) || s.is(Blocks.AZALEA_LEAVES) || s.is(Blocks.OAK_LEAVES)) vanillaTrees++;
                }
            }
        }
        check(streetCols > 500, "Strassenspalten im Kern: " + streetCols);
        check(asphalt > streetCols * 0.35, "Asphalt auf Strassenniveau: " + asphalt + " / " + streetCols);
        check(lotCols > 1000 && lotSolid > lotCols * 0.9, "Grundstuecksboden: " + lotSolid + " / " + lotCols);
        check(tall > 50, "mehrstoeckige Gebaeude (Spalten >= 12 hoch): " + tall);
        check(stairs > 10, "Treppenhaus-Stufen: " + stairs);
        check(flora > 30, "Sporos-Flora in der Stadt (Hyphe-Staemme, Pilzstiele, Geflecht-Ranken, Leuchtpilze): " + flora);
        check(vanillaTrees == 0, "keine Vanilla-Baeume/-Ranken in der Stadt: " + vanillaTrees);

        // Planung: Grundstuecke und Gebaeude liegen in ihrer Zelle, Hochhaeuser 8-25 Etagen
        int towers = 0;
        for (int gx = -3; gx <= 3; gx++) for (int gz = -3; gz <= 3; gz++) {
            CellPlan c = layout.cell(world, gx, gz);
            Building b = c.building;
            if (b == null) continue;
            check(b.x0 >= c.minX + CityLayout.LOT_MIN && b.x1 <= c.minX + CityLayout.LOT_MAX
                    && b.z0 >= c.minZ + CityLayout.LOT_MIN && b.z1 <= c.minZ + CityLayout.LOT_MAX, "Gebaeude im Grundstueck");
            if (b.kind == Building.Kind.TOWER) {
                towers++;
                check(b.floors >= 8 && b.floors <= 25, "Hochhaus-Etagen " + b.floors);
            }
            for (CellPlan.Tree t : c.trees) {
                check(t.x() - t.radius() >= c.minX && t.x() + t.radius() < c.minX + CityLayout.CELL
                        && t.z() - t.radius() >= c.minZ && t.z() + t.radius() < c.minZ + CityLayout.CELL, "Baumkrone in der Zelle");
            }
        }
        check(towers > 0, "mindestens ein Hochhaus im Kern");
        helper.succeed();
    }

    /**
     * Stadtschicht: im Stadtkreis nur Stadtbiome (Ebene/Moosdickicht) sowie Sumpf/Fluss als geflutete Zellen, in der
     * 1/4- wie in der Blockaufloesung; ausserhalb der Staedte kommen weiterhin alle neun Biome vor.
     */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 600)
    public static void cityBiomes(GameTestHelper helper) {
        WorldChunkManagerCelestial world = world(SEED);
        int checked = 0, cityBiome = 0, cities = 0;
        for (int d = 0; d < 400 && cities < 6; d++) {
            SporosCities.City city = SporosCities.cityOfDistrict(SEED, d % 20 - 10, d / 20 - 10);
            if (city == null) continue;
            cities++;
            int r = city.radiusChunks() * 16;
            for (int dx = -r; dx <= r; dx += 6) for (int dz = -r; dz <= r; dz += 6) {
                int x = city.originX() + dx, z = city.originZ() + dz;
                // 8 Bloecke Rand: der Voronoi-Zoom verschiebt Biomgrenzen um bis zu ~6 Bloecke
                if (Math.hypot(dx, dz) >= r - 8) continue;
                BiomeGenBaseCelestial b = world.getBiomeGenForCoords(x, z);
                check(SporosCities.isCityBiome(b) || b == BiomeGenBaseSporos.swamp || b == BiomeGenBaseSporos.river,
                        "Biom im Stadtkreis bei " + x + "/" + z + ": " + b.biomeName);
                checked++;
                if (SporosCities.isCityBiome(b)) cityBiome++;
            }
            // 1/4-Aufloesung (Rauschhoehen des Generators) ebenso
            int cx = city.originX() >> 2, cz = city.originZ() >> 2;
            BiomeGenBaseCelestial[] coarse = world.getBiomesForGeneration(null, cx - 8, cz - 8, 16, 16);
            for (int i = 0; i < coarse.length; i++) {
                int bx = (cx - 8 + i % 16) * 4 + 2, bz = (cz - 8 + i / 16) * 4 + 2;
                if (!GenLayerSporosCity.inCity(city, bx, bz)) continue;
                BiomeGenBaseCelestial b = coarse[i];
                check(SporosCities.isCityBiome(b) || b == BiomeGenBaseSporos.swamp || b == BiomeGenBaseSporos.river,
                        "Rauschbiom im Stadtkreis bei " + bx + "/" + bz + ": " + b.biomeName);
            }
        }
        check(cities >= 3 && checked > 1000, "geprueft: " + cities + " Staedte, " + checked + " Stellen");
        check(cityBiome > checked * 0.6, "ueberwiegend Ebene/Moosdickicht in Staedten: " + cityBiome + " / " + checked);

        // ausserhalb der Staedte: alle Biome (2048 x 2048 Bloecke, Stichproben alle 16 Bloecke)
        Set<BiomeGenBaseCelestial> seen = new HashSet<>();
        for (int x = -1024; x < 1024; x += 16) for (int z = -1024; z < 1024; z += 16) {
            if (SporosCities.cityAt(SEED, x >> 4, z >> 4) != null) continue;
            seen.add(world.getBiomeGenForCoords(x, z));
        }
        for (BiomeGenBaseCelestial b : BiomeGenBaseSporos.ALL) check(seen.contains(b), "Biom fehlt ausserhalb der Staedte: " + b.biomeName);
        helper.succeed();
    }

    /** Blockwahl: alle gesetzten Bloecke stehen in der Whitelist; die Whitelist enthaelt nichts Verbotenes. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 900)
    public static void cityBlockWhitelist(GameTestHelper helper) {
        CityPalette p = CityPalette.get();
        Set<Block> allowed = new HashSet<>(p.allowedBlocks());
        // natuerliche Oberflaeche unter dem Strassenniveau
        allowed.addAll(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.PODZOL, Blocks.CLAY, Blocks.GRAVEL, Blocks.MUD, Blocks.ROOTED_DIRT,
                Blocks.MOSS_BLOCK, Blocks.GRASS_BLOCK, Blocks.WATER, SporosBlocks.SPOROS_STONE.get(), SporosBlocks.SPOROS_SILT.get()));

        // 1. Whitelist selbst
        Set<Block> forbidden = new HashSet<>(CityPalette.FORBIDDEN_VANILLA);
        for (String id : CityPalette.FORBIDDEN_HBM) {
            Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("hbm_m", id));
            if (b != Blocks.AIR) forbidden.add(b);
        }
        List<TagKey<Block>> badTags = List.of(tag("forge", "ores"), tag("c", "ores"), tag("forge", "storage_blocks"), tag("c", "storage_blocks"),
                tag("minecraft", "iron_ores"), tag("minecraft", "copper_ores"), tag("minecraft", "coal_ores"), tag("minecraft", "gold_ores"));
        for (Block b : p.allowedBlocks()) {
            check(!forbidden.contains(b), "verbotener Block in der Palette: " + BuiltInRegistries.BLOCK.getKey(b));
            for (TagKey<Block> t : badTags) check(!b.defaultBlockState().is(t), "Block " + BuiltInRegistries.BLOCK.getKey(b) + " ist " + t.location());
            String path = BuiltInRegistries.BLOCK.getKey(b).getPath();
            check(!path.contains("_ore") && !path.startsWith("ore_") && !path.startsWith("block_") && !path.contains("steel"),
                    "Erz/Speicher/Stahl in der Palette: " + path);
        }

        // 2. Rueckrezepte: kein Nicht-Handwerksrezept macht aus einem Palettenblock Metall
        int checked = recipeCheck(helper.getLevel(), p.allowedBlocks());

        // 3. generierte Chunks (Kern + Strahl bis zum Rand)
        SporosCities.City city = city(SEED, 10);
        CityLayout layout = CityLayout.of(city);
        ChunkProviderCelestial prov = provider(SEED);
        int chunks = 0;
        for (int dx = -city.radiusChunks(); dx <= city.radiusChunks(); dx++) {
            for (int dz = -city.radiusChunks(); dz <= city.radiusChunks(); dz++) {
                boolean core = Math.abs(dx) <= 3 && Math.abs(dz) <= 3;
                boolean ray = dz == 0 || dx == dz;
                if (!core && !ray) continue;
                int cx = city.centerChunkX() + dx, cz = city.centerChunkZ() + dz;
                if (SporosCities.cityAt(SEED, cx, cz) == null) continue;
                chunks++;
                BlockState[] blocks = prov.provideChunk(cx, cz).blocks;
                for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
                    if (layout.edge(cx * 16 + lx, cz * 16 + lz) >= CityLayout.URBAN) continue;
                    for (int y = layout.baseY - 6; y < 256; y++) {
                        BlockState s = blocks[idx(lx, y, lz)];
                        if (s == null) continue;
                        check(allowed.contains(s.getBlock()), "nicht freigegebener Block " + BuiltInRegistries.BLOCK.getKey(s.getBlock())
                                + " bei " + (cx * 16 + lx) + " " + y + " " + (cz * 16 + lz));
                    }
                }
            }
        }
        check(chunks > 40, "gepruefte Stadtchunks: " + chunks);
        check(checked > 0, "Rezepte geprueft: " + checked);
        helper.succeed();
    }

    private static TagKey<Block> tag(String ns, String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ns, path));
    }

    private static final List<String> METALS = List.of("iron", "steel", "copper", "alumin", "titanium", "tungsten", "lead", "gold",
            "nickel", "zinc", "cobalt", "uranium", "metal_scrap", "nugget", "ingot");

    /** Alle Rezepte, die kein Handwerks-/Steinmetz-Rezept sind und einen Palettenblock verbrauchen, duerfen kein Metall liefern. */
    private static int recipeCheck(ServerLevel level, Set<Block> palette) {
        Set<Item> items = new HashSet<>();
        for (Block b : palette) if (b.asItem() != Items.AIR) items.add(b.asItem());
        int checked = 0;
        //? if < 1.21.1 {
        for (net.minecraft.world.item.crafting.Recipe<?> r : level.getRecipeManager().getRecipes()) {
            ResourceLocation id = r.getId();
            ItemStack out;
            try {
                out = r.getResultItem(level.registryAccess());
            } catch (RuntimeException e) {
                continue;
            }
        //?} else {
        /*for (net.minecraft.world.item.crafting.RecipeHolder<?> h : level.getRecipeManager().getRecipes()) {
            net.minecraft.world.item.crafting.Recipe<?> r = h.value();
            ResourceLocation id = h.id();
            ItemStack out;
            try {
                out = r.getResultItem(level.registryAccess());
            } catch (RuntimeException e) {
                continue;
            }
        *///?}
            String type = String.valueOf(BuiltInRegistries.RECIPE_TYPE.getKey(r.getType()));
            if (type.equals("minecraft:crafting") || type.equals("minecraft:stonecutting")) continue;
            boolean uses = false;
            for (net.minecraft.world.item.crafting.Ingredient ing : r.getIngredients()) {
                for (Item it : items) {
                    if (ing.test(new ItemStack(it))) {
                        uses = true;
                        break;
                    }
                }
                if (uses) break;
            }
            if (!uses) continue;
            checked++;
            String result = String.valueOf(BuiltInRegistries.ITEM.getKey(out.getItem()));
            for (String m : METALS) {
                check(!result.contains(m), "Rezept " + id + " (" + type + ") macht aus einem Stadtblock " + result);
            }
        }
        return checked;
    }

    /** Alle Loot-Tables laden (nicht leer) und liefern beim Wuerfeln Gegenstaende. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 100)
    public static void cityLootTables(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        for (String name : CityLoot.TABLES) {
            ResourceLocation id = CityLoot.tableId(name);
            //? if < 1.21.1 {
            LootTable table = level.getServer().getLootData().getLootTable(id);
            //?} else {
            /*LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
            *///?}
            check(table != LootTable.EMPTY, "Loot-Table fehlt oder ist kaputt: " + id);
            int total = 0;
            for (int roll = 0; roll < 20; roll++) {
                LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).create(LootContextParamSets.CHEST);
                for (ItemStack s : table.getRandomItems(params, 1000L + roll)) {
                    check(!s.isEmpty() && s.getItem() != Items.AIR, "leerer Eintrag in " + id);
                    total += s.getCount();
                }
            }
            check(total > 10, "zu wenig Loot in " + id + ": " + total);
        }
        helper.succeed();
    }

    /** Echte Dimension: Truhen/Faesser einer Stadt tragen nach der Dekoration ihre Loot-Table. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 2400)
    public static void cityChestsHaveLoot(GameTestHelper helper) {
        ServerLevel sporos = helper.getLevel().getServer().getLevel(com.space_beyond.dim.BeyondDimensions.SPOROS);
        // NeoForge-1.21.1-GameTestServer laedt keine Datapack-Dimensionen (s. DimensionGameTest)
        if (sporos == null) {
            helper.succeed();
            return;
        }
        long seed = sporos.getSeed();
        SporosCities.City city = city(seed, 6);
        CityLayout layout = CityLayout.of(city);
        int found = 0, tried = 0;
        outer:
        for (int r = 0; r <= 2; r++) {
            for (int gx = -r; gx <= r; gx++) for (int gz = -r; gz <= r; gz++) {
                if (Math.max(Math.abs(gx), Math.abs(gz)) != r) continue;
                for (CellPlan.LootSpot s : layout.cell(com.space_beyond.dim.sporos.city.CityLocator.world(), gx, gz).loot) {
                    if (tried++ > 12) break outer;
                    BlockPos pos = new BlockPos(s.x(), s.y(), s.z());
                    sporos.getChunk(pos); // generiert bis FULL (inkl. Dekoration)
                    BlockEntity be = sporos.getBlockEntity(pos);
                    if (be == null) continue;
                    CompoundTag tag = BlockHooks.saveWithoutMetadata(be);
                    String table = tag.getString("LootTable");
                    if (table.startsWith(com.space_beyond.BeyondMod.MOD_ID + ":chests/sporos_city/")) found++;
                    if (found >= 2) break outer;
                }
            }
        }
        check(found >= 1, "keine Truhe mit Stadt-Loot gefunden (" + tried + " Stellen)");
        helper.succeed();
    }
}
