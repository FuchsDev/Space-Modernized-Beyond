package com.space_beyond.item.survival;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

/**
 * Molotow-Geschoss: zerbricht beim Aufprall, setzt im Umkreis {@link #RADIUS} Feuer auf freie Stellen mit festem
 * Boden und zuendet Lebewesen ({@link #BURN_SECONDS} s, 2 Feuerschaden sofort). Kein Blockschaden.
 */
public class EntityMolotov extends ThrowableItemProjectile {

    public static final int RADIUS = 2;
    public static final int BURN_SECONDS = 7;

    public EntityMolotov(EntityType<? extends EntityMolotov> type, Level level) {
        super(type, level);
    }

    public EntityMolotov(Level level, LivingEntity shooter) {
        super(SurvivalItems.MOLOTOV_ENTITY.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return SurvivalItems.MOLOTOV.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(level() instanceof ServerLevel server)) return;
        ignite(server, BlockPos.containing(result.getLocation()));
        discard();
    }

    /** Feuerflaeche und Brand um {@code center} (auch fuer Tests aufrufbar). */
    public void ignite(ServerLevel server, BlockPos center) {
        server.playSound(null, center, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, 0.9F);
        server.sendParticles(ParticleTypes.FLAME, center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, 40, 1.2D, 0.4D, 1.2D, 0.05D);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS * RADIUS + 1) continue;
                for (int dy = 1; dy >= -2; dy--) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (!server.isEmptyBlock(pos)) continue;
                    BlockPos below = pos.below();
                    if (server.getBlockState(below).isAir()) continue;
                    if (server.getBlockState(below).getFluidState().isEmpty()) {
                        server.setBlockAndUpdate(pos, BaseFireBlock.getState(server, pos));
                    }
                    break;
                }
            }
        }
        for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, new AABB(center).inflate(RADIUS + 0.5D))) {
            if (e == getOwner()) continue;
            com.hbm_m.platform.PlatformHooks.setSecondsOnFire(e, BURN_SECONDS);
            e.hurt(damageSources().inFire(), 2.0F);
        }
    }
}
