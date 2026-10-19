package com.space_beyond.item.survival;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Nagelbombe: Splitterladung ohne Blockschaden - beim Aufprall {@link #DAMAGE} Schaden im Radius {@link #RADIUS},
 * linear mit dem Abstand abnehmend (Explosionsschaden, Ruestung zaehlt), Rueckstoss und Nagelpartikel.
 */
public class EntityNailBomb extends ThrowableItemProjectile {

    public static final double RADIUS = 4.0D;
    public static final float DAMAGE = 14F;

    public EntityNailBomb(EntityType<? extends EntityNailBomb> type, Level level) {
        super(type, level);
    }

    public EntityNailBomb(Level level, LivingEntity shooter) {
        super(SurvivalItems.NAIL_BOMB_ENTITY.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return SurvivalItems.NAIL_BOMB.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(level() instanceof ServerLevel server)) return;
        detonate(server, result.getLocation());
        discard();
    }

    /** Splitterwirkung um {@code at} (auch fuer Tests aufrufbar). */
    public void detonate(ServerLevel server, Vec3 at) {
        server.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.5F, 1.3F);
        server.sendParticles(ParticleTypes.EXPLOSION, at.x, at.y, at.z, 3, 0.3D, 0.3D, 0.3D, 0.0D);
        server.sendParticles(ParticleTypes.CRIT, at.x, at.y, at.z, 60, 1.5D, 1.0D, 1.5D, 0.3D);
        Entity owner = getOwner();
        for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(RADIUS))) {
            double d = e.position().add(0, e.getBbHeight() * 0.5D, 0).distanceTo(at);
            if (d > RADIUS) continue;
            float dmg = (float) (DAMAGE * (1.0D - d / (RADIUS + 1.0D)));
            e.hurt(damageSources().explosion(this, owner), dmg);
            Vec3 push = e.position().subtract(at).normalize().scale(0.6D);
            e.push(push.x, 0.3D, push.z);
        }
    }
}
