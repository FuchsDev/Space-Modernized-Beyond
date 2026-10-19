package com.space_beyond.entity.mob.boss;

import org.jetbrains.annotations.Nullable;

import com.space_m.particle.SpaceParticles;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Instabiler Kristallsplitter des Bosses {@link EntityUnstableSovereign}: kreist zuerst ({@link #orbit} Ticks) als
 * Teil eines Rings um den Boss, schiesst dann auf sein Ziel und lenkt leicht nach (zielsuchend, aber ausweichbar).
 * Treffer: 7 Magieschaden, zerspringt in Funken. Spieler koennen Splitter im Flug abschiessen oder zerschlagen.
 * Gold-Splitter (Phase 2) sind schneller und treffen haerter.
 */
public class EntityCrystalShard extends Projectile {

    private static final EntityDataAccessor<Boolean> GOLD = SynchedEntityData.defineId(EntityCrystalShard.class, EntityDataSerializers.BOOLEAN);

    private int orbit;
    private float orbitAngle;
    private int life;
    @Nullable private LivingEntity target;

    public EntityCrystalShard(EntityType<? extends EntityCrystalShard> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static EntityCrystalShard create(Level level, LivingEntity owner, @Nullable LivingEntity target, float angle, int orbit, boolean gold) {
        EntityCrystalShard e = new EntityCrystalShard(BossMobs.CRYSTAL_SHARD.get(), level);
        e.setOwner(owner);
        e.target = target;
        e.orbitAngle = angle;
        e.orbit = orbit;
        e.entityData.set(GOLD, gold);
        e.moveTo(owner.getX(), owner.getY() + 2.5D, owner.getZ(), 0, 0);
        return e;
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        this.entityData.define(GOLD, false);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(GOLD, false);
    }
    *///?}

    public boolean gold() {
        return entityData.get(GOLD);
    }

    @Override
    public void tick() {
        super.tick();
        if (++life > 160) {
            shatter();
            return;
        }
        Entity owner = getOwner();
        if (!level().isClientSide) {
            if (orbit > 0) {
                if (owner == null || !owner.isAlive()) {
                    discard();
                    return;
                }
                // Ring um den Boss: Winkel laeuft weiter, Radius zieht sich kurz vor dem Abschuss etwas zusammen
                orbitAngle += 0.18F;
                double rad = 2.6D + Math.min(orbit, 10) * 0.08D;
                Vec3 at = new Vec3(owner.getX() + Math.cos(orbitAngle) * rad, owner.getY() + 3.2D + Math.sin(orbitAngle * 2) * 0.3D,
                        owner.getZ() + Math.sin(orbitAngle) * rad);
                setDeltaMovement(at.subtract(position()));
                if (--orbit == 0) launch();
            } else if (target != null && target.isAlive()) {
                // sanftes Nachlenken
                Vec3 want = target.getEyePosition().subtract(position()).normalize().scale(speed());
                setDeltaMovement(getDeltaMovement().scale(0.9D).add(want.scale(0.1D)).normalize().scale(speed()));
            }
            if (orbit <= 0) {
                HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hit.getType() != HitResult.Type.MISS) {
                    onHit(hit);
                    if (isRemoved()) return;
                }
            }
        } else {
            level().addParticle(gold() ? com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get() : com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(),
                    getX() + random.nextGaussian() * 0.1, getY() + random.nextGaussian() * 0.1, getZ() + random.nextGaussian() * 0.1, 0, 0, 0);
        }
        Vec3 v = getDeltaMovement();
        setPos(getX() + v.x, getY() + v.y, getZ() + v.z);
        if (v.lengthSqr() > 1.0E-4D) updateRotation();
    }

    private double speed() {
        return gold() ? 1.15D : 0.85D;
    }

    private void launch() {
        if (target == null) {
            Entity owner = getOwner();
            if (owner instanceof net.minecraft.world.entity.Mob mob) target = mob.getTarget();
        }
        Vec3 dir = target != null ? target.getEyePosition().subtract(position()).normalize() : new Vec3(Math.cos(orbitAngle), 0, Math.sin(orbitAngle));
        setDeltaMovement(dir.scale(speed()));
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 1.4F, 1.6F);
    }

    @Override
    protected boolean canHitEntity(Entity e) {
        return !(e instanceof EntityUnstableSovereign) && !(e instanceof EntityCrystalShard) && !(e instanceof OrbitalBeamEntity)
                && super.canHitEntity(e);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity e = hit.getEntity();
        e.hurt(damageSources().indirectMagic(this, getOwner()), gold() ? 10.0F : 7.0F);
        if (e instanceof LivingEntity living) {
            Vec3 push = getDeltaMovement().normalize().scale(0.6D);
            living.push(push.x, 0.25D, push.z);
        }
        shatter();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        shatter();
    }

    private void shatter() {
        if (level() instanceof ServerLevel level) {
            level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY(), getZ(), 18, 0.2, 0.2, 0.2, 0.25);
            level.sendParticles(gold() ? com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get() : com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getX(), getY(), getZ(), 10, 0.3, 0.3, 0.3, 0.1);
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE, 1.2F, 0.8F + random.nextFloat() * 0.4F);
        }
        discard();
    }

    /** Im Flug abschiessbar / zerschlagbar. */
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        if (!level().isClientSide && source.getEntity() instanceof Player) shatter();
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
