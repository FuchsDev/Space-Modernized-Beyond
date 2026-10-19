package com.space_beyond.entity.mob.stage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Geschoss der Stufen-Bosse: leuchtende Kugel in der Farbe des Bosses (Renderer {@code BossOrbRenderer}), optional
 * zielsuchend, mit Schwerkraft (Bogenwurf) und Flaechenwirkung beim Aufprall. Spieler koennen sie zerschlagen.
 */
public class EntityBossOrb extends Projectile {

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityBossOrb.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(EntityBossOrb.class, EntityDataSerializers.FLOAT);

    private float damage = 6.0F, splash;
    private int fire;
    private double gravity, homing;
    @Nullable private MobEffectInstance effect;
    @Nullable private LivingEntity target;
    private int life;

    public EntityBossOrb(EntityType<? extends EntityBossOrb> type, Level level) {
        super(type, level);
    }

    /** Neues Geschoss vom Boss {@code owner} mit Startgeschwindigkeit {@code motion}. */
    public static EntityBossOrb shoot(Level level, LivingEntity owner, Vec3 from, Vec3 motion, int color, float size, float damage) {
        EntityBossOrb e = new EntityBossOrb(StageBosses.BOSS_ORB.get(), level);
        e.setOwner(owner);
        e.setPos(from.x, from.y, from.z);
        e.setDeltaMovement(motion);
        e.entityData.set(COLOR, color);
        e.entityData.set(SIZE, size);
        e.damage = damage * BossAttacks.scale(owner);
        level.addFreshEntity(e);
        return e;
    }

    public EntityBossOrb gravity(double g) {
        this.gravity = g;
        return this;
    }

    public EntityBossOrb homing(LivingEntity t, double strength) {
        this.target = t;
        this.homing = strength;
        return this;
    }

    public EntityBossOrb splash(float radius) {
        this.splash = radius;
        return this;
    }

    public EntityBossOrb effect(MobEffectInstance e) {
        this.effect = e;
        return this;
    }

    /** Setzt Getroffene in Brand (Sekunden). */
    public EntityBossOrb fire(int seconds) {
        this.fire = seconds;
        return this;
    }

    /** Freund: Bosse und Dungeon-Mobs treffen sich nicht gegenseitig. */
    private static boolean friend(Entity e) {
        return e instanceof EntityStageBoss || e instanceof com.space_beyond.entity.mob.dungeon.EntityDungeonMob;
    }

    private void ignite(Entity e) {
        if (fire <= 0) return;
        //? if < 1.21.1 {
        e.setSecondsOnFire(fire);
        //?} else {
        /*e.igniteForSeconds(fire);
        *///?}
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFFFFF);
        this.entityData.define(SIZE, 0.4F);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, 0xFFFFFF);
        builder.define(SIZE, 0.4F);
    }
    *///?}

    public int color() {
        return entityData.get(COLOR);
    }

    public float size() {
        return entityData.get(SIZE);
    }

    @Override
    public void tick() {
        super.tick();
        if (++life > 200) {
            discard();
            return;
        }
        Vec3 v = getDeltaMovement();
        if (!level().isClientSide) {
            if (target != null && target.isAlive() && homing > 0) {
                double speed = v.length();
                Vec3 want = target.getEyePosition().subtract(position()).normalize().scale(speed);
                v = v.scale(1 - homing).add(want.scale(homing));
            }
            v = v.add(0, -gravity, 0);
            setDeltaMovement(v);
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hit.getType() != HitResult.Type.MISS) {
                onHit(hit);
                if (isRemoved()) return;
            }
        } else {
            int c = color();
            level().addParticle(new DustParticleOptions(new Vector3f(((c >> 16) & 255) / 255F, ((c >> 8) & 255) / 255F, (c & 255) / 255F), 1.2F),
                    getX(), getY(), getZ(), 0, 0, 0);
        }
        setPos(getX() + v.x, getY() + v.y, getZ() + v.z);
        if (v.lengthSqr() > 1.0E-4D) updateRotation();
    }

    @Override
    protected boolean canHitEntity(Entity e) {
        return !friend(e) && !(e instanceof EntityBossOrb) && super.canHitEntity(e);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity e = hit.getEntity();
        e.hurt(damageSources().indirectMagic(this, getOwner()), damage);
        if (effect != null && e instanceof LivingEntity l) l.addEffect(new MobEffectInstance(effect));
        ignite(e);
        burst();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        burst();
    }

    private void burst() {
        if (level() instanceof ServerLevel level) {
            int c = color();
            level.sendParticles(new DustParticleOptions(new Vector3f(((c >> 16) & 255) / 255F, ((c >> 8) & 255) / 255F, (c & 255) / 255F), 2.0F),
                    getX(), getY(), getZ(), 24, 0.4 + splash * 0.3, 0.4, 0.4 + splash * 0.3, 0);
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.0F, 0.6F + random.nextFloat() * 0.4F);
            if (splash > 0) {
                for (LivingEntity l : level.getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(splash),
                        l -> l.isAlive() && !friend(l) && l.distanceToSqr(this) < splash * splash)) {
                    l.hurt(damageSources().indirectMagic(this, getOwner()), damage * 0.6F);
                    if (effect != null) l.addEffect(new MobEffectInstance(effect));
                    ignite(l);
                }
            }
        }
        discard();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && source.getEntity() instanceof Player) burst();
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
