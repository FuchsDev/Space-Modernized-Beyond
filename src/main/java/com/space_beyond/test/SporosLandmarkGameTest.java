package com.space_beyond.test;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.space_m.SpaceMod;
import com.space_beyond.block.crate.SupplyCrateBlockEntity;
import com.space_m.blockentity.SpaceBlockEntities;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.city.CellPlan;
import com.space_beyond.dim.sporos.city.CellPlan.LotType;
import com.space_beyond.dim.sporos.city.CityLayout;
import com.space_beyond.dim.sporos.city.CityLoot;
import com.space_beyond.dim.sporos.city.CityPalette;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.flora.SporosFlora;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Landmarken der Sporos-Staedte (Kontrollpunkt, Lager, Feldlazarett, Nest, Konvoi) und die Beute-Kisten: Planung
 * (jede Art kommt vor, alles bleibt in der Zelle, Beutestellen tragen die richtige Tabelle), Erzeugung im Chunk-Puffer
 * (Zeltplanen, Kisten, Spawner, Lagerfeuer stehen da), Kisten-Block mit Loot-Table.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosLandmarkGameTest {

    private SporosLandmarkGameTest() {}

    private static final long SEED = 7654321L;

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static WorldChunkManagerCelestial world(long seed) {
        return CelestialGenerators.require(SporosBlocks.BODY).chunkManager().apply(seed);
    }

    private static ChunkProviderCelestial provider(long seed) {
        return CelestialGenerators.require(SporosBlocks.BODY).chunkProvider().apply(world(seed), seed);
    }

    private static int idx(int lx, int y, int lz) {
        return (lx * 16 + lz) * 256 + y;
    }

    /** Zelle einer Landmarke (erste gefundene) ueber mehrere Staedte/Seeds, oder null. */
    private static CellPlan find(LotType type, boolean convoy, long seed) {
        for (int d = 0; d < 400; d++) {
            SporosCities.City city = SporosCities.cityOfDistrict(seed, d % 20 - 10, d / 20 - 10);
            if (city == null) continue;
            CityLayout layout = CityLayout.of(city);
            WorldChunkManagerCelestial world = world(seed);
            int cells = city.radiusChunks() * 16 / CityLayout.CELL + 1;
            for (int gx = -cells; gx <= cells; gx++) {
                for (int gz = -cells; gz <= cells; gz++) {
                    CellPlan plan = layout.cell(world, gx, gz);
                    if (convoy ? plan.convoy != null : plan.type == type) return plan;
                }
            }
        }
        return null;
    }

    private static boolean inCell(CellPlan plan, int x, int z) {
        return x >= plan.minX && x < plan.minX + CityLayout.CELL && z >= plan.minZ && z < plan.minZ + CityLayout.CELL;
    }

    /** Jede Landmarke kommt vor; Objekte liegen in der Zelle; Beutestellen tragen ihre Tabelle; Nest hat Spawner. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 600)
    public static void landmarkPlanning(GameTestHelper helper) {
        Map<LotType, String> tables = new EnumMap<>(LotType.class);
        tables.put(LotType.CHECKPOINT, "checkpoint");
        tables.put(LotType.CAMP, "camp");
        tables.put(LotType.TRIAGE, "triage");
        tables.put(LotType.NEST, "nest");
        for (Map.Entry<LotType, String> e : tables.entrySet()) {
            CellPlan plan = find(e.getKey(), false, SEED);
            check(plan != null, "keine Zelle vom Typ " + e.getKey());
            check(plan.building == null, e.getKey() + " hat kein Gebaeude");
            boolean table = false;
            for (CellPlan.LootSpot s : plan.loot) {
                check(inCell(plan, s.x(), s.z()), e.getKey() + ": Beutestelle ausserhalb der Zelle");
                check(CityLoot.TABLES.contains(s.table()), "unbekannte Tabelle " + s.table());
                table |= s.table().equals(e.getValue());
            }
            check(table, e.getKey() + ": keine Beutestelle mit Tabelle " + e.getValue());
            for (CellPlan.Tent t : plan.tents) {
                int x1 = t.alongX() ? t.x() + t.len() - 1 : t.x() + t.width() - 1;
                int z1 = t.alongX() ? t.z() + t.width() - 1 : t.z() + t.len() - 1;
                check(inCell(plan, t.x(), t.z()) && inCell(plan, x1, z1), e.getKey() + ": Zelt ragt aus der Zelle");
                check(t.width() % 2 == 1, "Zeltbreite ungerade");
            }
            for (CellPlan.Sandbags b : plan.sandbags) {
                check(inCell(plan, b.x(), b.z()) && inCell(plan, b.alongX() ? b.x() + b.len() - 1 : b.x(), b.alongX() ? b.z() : b.z() + b.len() - 1),
                        e.getKey() + ": Sandsaecke ausserhalb der Zelle");
            }
            switch (e.getKey()) {
                case CHECKPOINT -> {
                    check(plan.checkpoint != null && (plan.gx == 0 || plan.gz == 0), "Kontrollpunkt auf der Hauptstrasse");
                    check(plan.tents.size() >= 2, "Kontrollpunkt hat Zelte");
                    boolean crate = false;
                    for (CellPlan.LootSpot s : plan.loot) crate |= s.container() == CellPlan.Container.MILITARY_CRATE;
                    check(crate, "Kontrollpunkt hat Armee-Kisten");
                }
                case CAMP -> check(plan.tents.size() >= 2 && !plan.logs.isEmpty(), "Lager hat Zelte und Sitzstaemme");
                case TRIAGE -> check(plan.mobs.size() >= 2 && plan.tents.size() == 1 && plan.tents.get(0).width() == 9 && plan.tents.get(0).kind() == 2, "Lazarett: ein grosses weisses Zelt");
                case NEST -> {
                    check(plan.mobs.size() >= 4, "Nest hat Bewohner");
                    check(plan.spawner != null && plan.nestRadius >= 5, "Nest hat Spawner und Huegel");
                    check(inCell(plan, plan.spawner.x(), plan.spawner.z()), "Spawner in der Zelle");
                }
                default -> { }
            }
        }
        CellPlan convoy = find(null, true, SEED);
        check(convoy != null, "kein Konvoi gefunden");
        int trucks = 0;
        boolean table = false;
        for (CellPlan.Car car : convoy.cars) if (car.color() == 10 && car.len() == 5) trucks++;
        for (CellPlan.LootSpot s : convoy.loot) table |= s.table().equals("convoy") && inCell(convoy, s.x(), s.z());
        check(trucks >= 2 && table, "Konvoi: zwei Lkw und Kisten mit Tabelle convoy");
        check(!convoy.mobs.isEmpty(), "Konvoi hat Bewohner");

        // Kontrollpunkte nur im aeusseren Ring, Lazarette nur innen
        check(find(LotType.CHECKPOINT, false, SEED).edge > 0.5, "Kontrollpunkt aussen");
        check(find(LotType.TRIAGE, false, SEED).edge < 0.5, "Lazarett innen");
        helper.succeed();
    }

    /** Chunk-Puffer: Zeltplanen, Kisten, Leiter (Kontrollpunkt), Spawner + Pilzhuegel (Nest), brennendes Lagerfeuer (Lager). */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 900)
    public static void landmarkChunks(GameTestHelper helper) {
        CityPalette p = CityPalette.get();
        check(p.allowedBlocks().contains(SporosBlocks.SUPPLY_CRATE.get()) && p.allowedBlocks().contains(SporosBlocks.MILITARY_CRATE.get())
                && p.allowedBlocks().contains(Blocks.SPAWNER) && p.allowedBlocks().contains(Blocks.CAMPFIRE), "Palette kennt Kisten, Spawner, Lagerfeuer");
        ChunkProviderCelestial prov = provider(SEED);

        Set<Block> cp = blocks(prov, find(LotType.CHECKPOINT, false, SEED));
        check(cp.contains(SporosBlocks.MILITARY_CRATE.get()), "Kontrollpunkt: Armee-Kiste gesetzt");
        check(cp.contains(Blocks.LADDER) && cp.contains(Blocks.PACKED_MUD), "Kontrollpunkt: Turmleiter und Sandsaecke");
        check(cp.contains(Blocks.GREEN_WOOL) || cp.contains(Blocks.LIGHT_GRAY_WOOL) || cp.contains(Blocks.BROWN_WOOL), "Kontrollpunkt: Zeltplanen");

        Set<Block> nest = blocks(prov, find(LotType.NEST, false, SEED));
        check(nest.contains(Blocks.SPAWNER) && nest.contains(SporosFlora.FUNGAL_CAP.get()) && nest.contains(Blocks.CHEST), "Nest: Spawner, Pilzhuegel, Truhe");

        CellPlan camp = find(LotType.CAMP, false, SEED);
        Set<Block> campBlocks = blocks(prov, camp);
        check(campBlocks.contains(Blocks.CAMPFIRE), "Lager: Lagerfeuer");
        int cx = camp.minX + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2, cz = camp.minZ + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2;
        BlockState fire = prov.provideChunk(cx >> 4, cz >> 4).blocks[idx(cx & 15, camp.groundY() + 1, cz & 15)];
        check(fire != null && fire.is(Blocks.CAMPFIRE) && fire.getValue(BlockStateProperties.LIT), "Lager: Feuer brennt");

        Set<Block> triage = blocks(prov, find(LotType.TRIAGE, false, SEED));
        check(triage.contains(Blocks.WHITE_WOOL) && triage.contains(Blocks.RED_TERRACOTTA), "Lazarett: weisses Zelt und Kreuz");
        helper.succeed();
    }

    /** Alle Bloecke der Chunks einer Zelle oberhalb des Strassenniveaus. */
    private static Set<Block> blocks(ChunkProviderCelestial prov, CellPlan plan) {
        check(plan != null, "Zelle fehlt");
        Set<Block> found = new HashSet<>();
        int g = plan.groundY();
        for (int cx = plan.minX >> 4; cx <= (plan.minX + CityLayout.CELL - 1) >> 4; cx++) {
            for (int cz = plan.minZ >> 4; cz <= (plan.minZ + CityLayout.CELL - 1) >> 4; cz++) {
                BlockState[] blocks = prov.provideChunk(cx, cz).blocks;
                for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
                    if (!inCell(plan, cx * 16 + lx, cz * 16 + lz)) continue;
                    for (int y = g; y < g + 14; y++) {
                        BlockState s = blocks[idx(lx, y, lz)];
                        if (s != null) found.add(s.getBlock());
                    }
                }
            }
        }
        return found;
    }

    /** Kisten-Block: beide Varianten teilen den Block-Entity-Typ, Loot-Table wird beim Oeffnen ausgepackt, Abbau droppt den Block. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_city", timeoutTicks = 100)
    public static void supplyCrate(GameTestHelper helper) {
        check(com.space_beyond.blockentity.BeyondBlockEntities.SUPPLY_CRATE.get().isValid(SporosBlocks.SUPPLY_CRATE.get().defaultBlockState()), "BE-Typ gilt fuer supply_crate");
        check(com.space_beyond.blockentity.BeyondBlockEntities.SUPPLY_CRATE.get().isValid(SporosBlocks.MILITARY_CRATE.get().defaultBlockState()), "BE-Typ gilt fuer military_crate");
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, SporosBlocks.MILITARY_CRATE.get());
        BlockEntity be = helper.getBlockEntity(pos);
        check(be instanceof SupplyCrateBlockEntity, "Kiste hat Block-Entity");
        SupplyCrateBlockEntity crate = (SupplyCrateBlockEntity) be;
        //? if < 1.21.1 {
        crate.setLootTable(CityLoot.tableId("checkpoint"), 42L);
        //?} else {
        /*crate.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, CityLoot.tableId("checkpoint")), 42L);
        *///?}
        crate.unpackLootTable(null);
        int filled = 0;
        for (int i = 0; i < crate.getContainerSize(); i++) if (!crate.getItem(i).isEmpty()) filled++;
        check(filled > 0, "Loot-Table checkpoint fuellt die Kiste");
        check(crate.getContainerSize() == 27, "27 Plaetze");
        helper.succeed();
    }
}
