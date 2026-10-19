package com.space_beyond.dim.sporos.city;

import java.util.List;

import com.space_m.SpaceMod;
import com.space_beyond.block.crate.BlockSupplyCrate;
import com.space_beyond.entity.mob.infected.InfectedMobs;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialSeed;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.city.CellPlan.LootSpot;
import com.space_beyond.dim.sporos.city.SporosCities.City;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/**
 * Loot der Sporos-Staedte. Die Behaelter (Truhe/Fass) stehen schon im Chunk-Puffer; in der Dekorationsphase
 * (eigener Chunk, {@code WorldGenRegion}) werden sie hier noch einmal mit Block-Entity gesetzt und bekommen ihre
 * Loot-Table. Die Stellen stammen aus derselben deterministischen Zellplanung wie beim Bauen.
 *
 * <p>Tabellen ({@code data/space_m/loot_tables/chests/sporos_city/*.json}, HBM-, Vanilla- und Addon-Gegenstaende):
 * {@link #TABLES}. Seltenheit je Gebaeudetyp in {@link CellPlan} ({@code table(...)}); Landmarken-Tabellen
 * {@code checkpoint/camp/triage/nest/convoy} (s. {@link CityLandmarks}). Behaelter: Truhe, Fass, Beute-Kisten
 * ({@link BlockSupplyCrate}). Ausserdem bekommt der Spawner eines Nests hier seinen Mob.
 */
public final class CityLoot {

    private CityLoot() {}

    public static final List<String> TABLES = List.of("residential", "office", "shop", "medical", "industrial", "parking", "military",
            "checkpoint", "camp", "triage", "nest", "convoy");

    public static ResourceLocation tableId(String name) {
        return com.space_beyond.BeyondMod.id("chests/sporos_city/" + name);
    }

    /** Dekorations-Haken ({@link com.space_beyond.dim.BeyondDecorators#register}). */
    public static void decorate(WorldGenLevel level, ChunkAccess chunk) {
        int cx = chunk.getPos().x, cz = chunk.getPos().z;
        long seed = CelestialSeed.get();
        City city = SporosCities.cityAt(seed, cx, cz);
        if (city == null) return;
        CityLayout layout = CityLayout.of(city);
        CelestialWorld world = CelestialGenerators.state(SporosBlocks.BODY).chunkManager;
        int minX = cx << 4, minZ = cz << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int gx = layout.cellX(minX); gx <= layout.cellX(minX + 15); gx++) {
            for (int gz = layout.cellZ(minZ); gz <= layout.cellZ(minZ + 15); gz++) {
                CellPlan cell = layout.cell(world, gx, gz);
                spawner(level, cell, minX, minZ, pos);
                for (CellPlan.MobSpot m : cell.mobs) {
                    if (m.x() < minX || m.x() > minX + 15 || m.z() < minZ || m.z() > minZ + 15) continue;
                    com.space_beyond.dim.sporos.structure.StructureMobs.spawn(level, m.kind(), m.x(), m.y(), m.z(),
                            CityRandom.hash(seed ^ 0x40B6L, m.x(), m.y(), m.z()));
                }
                for (LootSpot s : cell.loot) {
                    if (s.x() < minX || s.x() > minX + 15 || s.z() < minZ || s.z() > minZ + 15) continue;
                    pos.set(s.x(), s.y(), s.z());
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof ChestBlock) && !(state.getBlock() instanceof BarrelBlock)
                            && !(state.getBlock() instanceof BlockSupplyCrate)) continue;
                    BlockPos at = pos.immutable();
                    level.setBlock(at, state, 2);
                    RandomSource rand = RandomSource.create(CityRandom.hash(seed ^ 0x100717L, s.x(), s.y(), s.z()));
                    //? if < 1.21.1 {
                    RandomizableContainerBlockEntity.setLootTable(level, rand, at, tableId(s.table()));
                    //?} else {
                    /*net.minecraft.world.RandomizableContainer.setBlockEntityLootTable(level, rand, at, ResourceKey.create(Registries.LOOT_TABLE, tableId(s.table())));
                    *///?}
                }
            }
        }
    }

    /** Infizierten-Nest: Spawner-Block-Entity auf Lurker stellen (der Block selbst steht schon im Chunk-Puffer). */
    private static void spawner(WorldGenLevel level, CellPlan cell, int minX, int minZ, BlockPos.MutableBlockPos pos) {
        CellPlan.Spawner sp = cell.spawner;
        if (sp == null || sp.x() < minX || sp.x() > minX + 15 || sp.z() < minZ || sp.z() > minZ + 15) return;
        pos.set(sp.x(), sp.y(), sp.z());
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.SPAWNER)) return;
        BlockPos at = pos.immutable();
        level.setBlock(at, state, 2);
        if (level.getBlockEntity(at) instanceof SpawnerBlockEntity spawner) {
            RandomSource rand = RandomSource.create(CityRandom.hash(CelestialSeed.get() ^ 0x5BA3L, sp.x(), sp.y(), sp.z()));
            spawner.setEntityId(rand.nextFloat() < 0.75F ? InfectedMobs.LURKER.get() : InfectedMobs.SPORELING.get(), rand);
        }
    }
}
