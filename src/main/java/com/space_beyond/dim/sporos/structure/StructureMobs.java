package com.space_beyond.dim.sporos.structure;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.entity.mob.infected.InfectedMobs;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bewohner der Strukturen (Nutzerwunsch: Strukturen nicht leer). Wird in der Dekorationsphase aufgerufen, wie Vanilla es
 * bei Ozeanruinen (Ertrunkene) und Iglus macht: Entity im {@code ServerLevel} erzeugen, platzieren, dauerhaft machen,
 * {@code finalizeSpawn(STRUCTURE)}, dann {@code addFreshEntityWithPassengers} auf der {@code WorldGenRegion} (landet im
 * Proto-Chunk und wird mit ihm geladen). Der Platz wird geprueft: Fuesse und Kopf frei, darunter fester Boden;
 * sonst bis zu drei Bloecke hoeher versucht, notfalls ausgelassen.
 *
 * <p>Arten: {@link #LURKER}, {@link #SPORELING}, {@link #BLINDCAP}, {@link #MYCOBRUTE} (Infizierte), {@link #VILLAGER}
 * (Siedler der sicheren Zone), {@link #HORSE} (Stall der Siedlung); auf Wemburos {@link #ENDERMAN}, {@link #WITHER_SKELETON},
 * {@link #SKELETON}, {@link #ENDERMITE}, {@link #BLAZE}.
 */
public final class StructureMobs {

    private StructureMobs() {}

    public static final int LURKER = 0, SPORELING = 1, BLINDCAP = 2, MYCOBRUTE = 3, VILLAGER = 10, HORSE = 11;
    /** Wemburos-Waechter (Vanilla). */
    public static final int ENDERMAN = 20, WITHER_SKELETON = 21, SKELETON = 22, ENDERMITE = 23;
    /** Boss des Wemburos-Thronsaals. */
    public static final int UNSTABLE_SOVEREIGN = 24;
    /** Lavahoehle der Wemburos-Zitadelle. */
    public static final int BLAZE = 25;

    @Nullable
    public static EntityType<?> type(int kind) {
        return switch (kind) {
            case LURKER -> InfectedMobs.LURKER.get();
            case SPORELING -> InfectedMobs.SPORELING.get();
            case BLINDCAP -> InfectedMobs.BLINDCAP.get();
            case MYCOBRUTE -> InfectedMobs.MYCOBRUTE.get();
            case VILLAGER -> EntityType.VILLAGER;
            case HORSE -> EntityType.HORSE;
            case ENDERMAN -> EntityType.ENDERMAN;
            case WITHER_SKELETON -> EntityType.WITHER_SKELETON;
            case SKELETON -> EntityType.SKELETON;
            case ENDERMITE -> EntityType.ENDERMITE;
            case UNSTABLE_SOVEREIGN -> com.space_beyond.entity.mob.boss.BossMobs.UNSTABLE_SOVEREIGN.get();
            case BLAZE -> EntityType.BLAZE;
            default -> null;
        };
    }

    /** Setzt einen Mob der Art {@code kind} bei (x, y, z) (Fuesse), falls dort Platz ist. */
    public static boolean spawn(WorldGenLevel level, int kind, int x, int y, int z, long seed) {
        EntityType<?> type = type(kind);
        if (type == null) return false;
        BlockPos pos = null;
        for (int dy = 0; dy <= 3 && pos == null; dy++) {
            BlockPos p = new BlockPos(x, y + dy, z);
            if (free(level, p)) pos = p;
        }
        if (pos == null) return false;
        Entity e = type.create(level.getLevel());
        if (!(e instanceof Mob mob)) return false;
        RandomSource rand = RandomSource.create(seed);
        mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rand.nextFloat() * 360.0F, 0.0F);
        mob.setPersistenceRequired();
        //? if < 1.21.1 {
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
        //?} else {
        /*mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
        *///?}
        level.addFreshEntityWithPassengers(mob);
        return true;
    }

    private static boolean free(WorldGenLevel level, BlockPos p) {
        BlockState feet = level.getBlockState(p), head = level.getBlockState(p.above()), below = level.getBlockState(p.below());
        return feet.getCollisionShape(level, p).isEmpty() && feet.getFluidState().isEmpty()
                && head.getCollisionShape(level, p.above()).isEmpty() && head.getFluidState().isEmpty()
                && !below.getCollisionShape(level, p.below()).isEmpty();
    }
}
