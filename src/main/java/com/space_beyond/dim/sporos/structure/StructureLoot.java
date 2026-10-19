package com.space_beyond.dim.sporos.structure;

import com.space_m.SpaceMod;
import com.space_beyond.block.crate.BlockSupplyCrate;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.CelestialSeed;
import com.space_m.dim.CelestialWorld;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.city.CityRandom;
import com.space_beyond.dim.sporos.structure.SporosStructures.Structure;
import com.space_beyond.entity.mob.infected.InfectedMobs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
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
 * Dekorationsphase der Wildnis-Strukturen: Loot-Tables ({@code data/space_m/loot_tables/chests/sporos_structure/*.json})
 * fuer Truhen, Faesser und Beute-Kisten, Mobs der Spawner. Gleiches Verfahren wie {@code CityLoot}: die Bloecke stehen
 * schon im Chunk, hier werden sie mit Block-Entity neu gesetzt und befuellt. Ausserdem die Bewohner ({@link StructureMobs}).
 */
public final class StructureLoot {

    private StructureLoot() {}

    public static ResourceLocation tableId(String name) {
        return com.space_beyond.BeyondMod.id("chests/sporos_structure/" + name);
    }

    public static void decorate(WorldGenLevel level, ChunkAccess chunk) {
        int cx = chunk.getPos().x, cz = chunk.getPos().z;
        CelestialWorld world = CelestialGenerators.state(SporosBlocks.BODY).chunkManager;
        Structure s = SporosStructures.structureAt(world, cx, cz);
        if (s == null) return;
        StructurePlan plan = SporosStructures.plan(world, s);
        long seed = CelestialSeed.get();
        int minX = cx << 4, minZ = cz << 4;
        // Bloecke mit Block-Entity aus dem Chunk-Puffer (Betten, Oefen, Sensoren ...) bekommen sie hier: ohne waeren z.B.
        // Betten unsichtbar. setBlock auf der WorldGenRegion legt den Eintrag an, der beim Laden zur Block-Entity wird.
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        int r = s.type().radius;
        for (int x = Math.max(minX, s.x() - r); x <= Math.min(minX + 15, s.x() + r); x++) {
            for (int z = Math.max(minZ, s.z() - r); z <= Math.min(minZ + 15, s.z() + r); z++) {
                for (int y = plan.base - 6; y <= plan.base + 24; y++) {
                    mp.set(x, y, z);
                    BlockState st = level.getBlockState(mp);
                    if (!st.hasBlockEntity() || st.getBlock() instanceof ChestBlock || st.getBlock() instanceof BarrelBlock
                            || st.getBlock() instanceof BlockSupplyCrate || st.is(Blocks.SPAWNER)) continue;
                    if (level.getBlockEntity(mp) == null) level.setBlock(mp.immutable(), st, 2);
                }
            }
        }
        for (StructurePlan.Loot l : plan.loot) {
            int x = Frame.worldX(s, l.u(), l.v()), z = Frame.worldZ(s, l.u(), l.v()), y = plan.base + l.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(at);
            if (!(state.getBlock() instanceof ChestBlock) && !(state.getBlock() instanceof BarrelBlock)
                    && !(state.getBlock() instanceof BlockSupplyCrate)) continue;
            level.setBlock(at, state, 2);
            RandomSource rand = RandomSource.create(CityRandom.hash(seed ^ 0x57A7L, x, y, z));
            //? if < 1.21.1 {
            RandomizableContainerBlockEntity.setLootTable(level, rand, at, tableId(l.table()));
            //?} else {
            /*net.minecraft.world.RandomizableContainer.setBlockEntityLootTable(level, rand, at, ResourceKey.create(Registries.LOOT_TABLE, tableId(l.table())));
            *///?}
        }
        for (StructurePlan.MobSpot m : plan.mobs) {
            int x = Frame.worldX(s, m.u(), m.v()), z = Frame.worldZ(s, m.u(), m.v()), y = plan.base + m.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            StructureMobs.spawn(level, m.kind(), x, y, z, CityRandom.hash(seed ^ 0x40B5L, x, y, z));
        }
        for (StructurePlan.Spawner sp : plan.spawners) {
            int x = Frame.worldX(s, sp.u(), sp.v()), z = Frame.worldZ(s, sp.u(), sp.v()), y = plan.base + sp.y();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15) continue;
            BlockPos at = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(at);
            if (!state.is(Blocks.SPAWNER)) continue;
            level.setBlock(at, state, 2);
            if (level.getBlockEntity(at) instanceof SpawnerBlockEntity spawner) {
                RandomSource rand = RandomSource.create(CityRandom.hash(seed ^ 0x5BA4L, x, y, z));
                spawner.setEntityId(mob(sp.mob()), rand);
            }
        }
    }

    public static EntityType<?> mob(int mob) {
        return switch (mob) {
            case 1 -> InfectedMobs.SPORELING.get();
            case 2 -> InfectedMobs.BLINDCAP.get();
            case 3 -> InfectedMobs.MYCOBRUTE.get();
            default -> InfectedMobs.LURKER.get();
        };
    }
}
