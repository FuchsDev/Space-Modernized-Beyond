package com.space_beyond.entity.mob.dungeon;

import java.util.EnumSet;

import org.jetbrains.annotations.Nullable;

import com.space_m.atmosphere.ISuffocationImmune;
import com.space_beyond.entity.mob.stage.EntityBossOrb;
import com.space_beyond.entity.mob.stage.EntityStageBoss;
import com.space_beyond.entity.mob.stage.PosedMob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Dungeon-Bewohner der Planeten (Nutzerwunsch: eigene Mobs statt Vanilla): ein gemeinsamer Mob, dessen Werte und
 * Verhalten aus seinem {@link DungeonMobKind} kommen - laufen, fliegen oder schwimmen; Nahkampf (mit Effekten, Feuer,
 * Sprung, Stampfen, Teleport) oder Fernkampf mit leuchtenden Kugeln; teilt sich, hinterlaesst Giftwolken. Erstickt
 * nicht im Vakuum. Animationen fuer das Posen-Modell: 1 Schlag, 2 Schuss, 3 Spezial.
 */
public class EntityDungeonMob extends Monster implements PosedMob, ISuffocationImmune {

    private static final EntityDataAccessor<Integer> ANIM = SynchedEntityData.defineId(EntityDungeonMob.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SMALL = SynchedEntityData.defineId(EntityDungeonMob.class, EntityDataSerializers.BOOLEAN);

    /** Groessenfaktor der Teil-Kopien. */
    public static final float SMALL_SCALE = 0.55F;

    private int animTime, animLeft, leapCd = 40, slamCd = 60, slamWind, teleportCd = 60, shotCd = 30;
    @Nullable private DungeonMobKind kindCache;

    public EntityDungeonMob(EntityType<? extends EntityDungeonMob> type, Level level) {
        super(type, level);
        DungeonMobKind k = kind();
        this.xpReward = k.xp;
        this.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG);
        if (k.move == DungeonMobKind.Move.FLY) {
            this.moveControl = new FlyingMoveControl(this, 20, true);
            this.setNoGravity(true);
        } else if (k.move == DungeonMobKind.Move.SWIM) {
            this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.06F, 0.1F, true);
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
        }
    }

    /** Bauplan (auch schon im Konstruktor von {@code Mob} verfuegbar, ueber den Entity-Typ). */
    public DungeonMobKind kind() {
        if (kindCache == null) kindCache = DungeonMobs.kind(getType());
        return kindCache;
    }

    public static AttributeSupplier.Builder attributes(DungeonMobKind k) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, k.health())
                .add(Attributes.ATTACK_DAMAGE, k.damage())
                .add(Attributes.ARMOR, k.armor)
                .add(Attributes.MOVEMENT_SPEED, k.move == DungeonMobKind.Move.SWIM ? 1.0D : k.speed)
                .add(Attributes.FLYING_SPEED, k.flySpeed)
                .add(Attributes.KNOCKBACK_RESISTANCE, k.knockResist)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM, 0);
        this.entityData.define(SMALL, false);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM, 0);
        builder.define(SMALL, false);
    }
    *///?}

    @Override
    protected PathNavigation createNavigation(Level level) {
        DungeonMobKind k = kind();
        if (k.move == DungeonMobKind.Move.FLY) {
            FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
            nav.setCanOpenDoors(false);
            nav.setCanFloat(true);
            nav.setCanPassDoors(true);
            return nav;
        }
        if (k.move == DungeonMobKind.Move.SWIM) return new WaterBoundPathNavigation(this, level);
        return super.createNavigation(level);
    }

    @Override
    protected void registerGoals() {
        DungeonMobKind k = kind();
        if (k.move == DungeonMobKind.Move.WALK && !k.breathesWater) this.goalSelector.addGoal(0, new FloatGoal(this));
        if (k.ranged != null) this.goalSelector.addGoal(2, new RangedGoal());
        else this.goalSelector.addGoal(2, new MeleeAttackGoal(this, k.move == DungeonMobKind.Move.FLY ? 1.0D : 1.15D, true));
        switch (k.move) {
            case FLY -> this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8D));
            case SWIM -> this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 30));
            default -> this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        }
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityDungeonMob.class, EntityStageBoss.class).setAlertOthers(EntityDungeonMob.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ── Zustand fuer das Modell ──

    @Override
    public boolean awake() {
        return true;
    }

    @Override
    public int anim() {
        return entityData.get(ANIM);
    }

    @Override
    public float animTime(float pt) {
        return animTime + pt;
    }

    @Override
    public boolean phaseTwo() {
        return getTarget() != null;
    }

    @Override
    public boolean phaseThree() {
        return false;
    }

    public boolean isSmall() {
        return entityData.get(SMALL);
    }

    public void setSmall(boolean small) {
        entityData.set(SMALL, small);
        //? if >= 1.21.1 {
        /*var scale = getAttribute(Attributes.SCALE);
        if (scale != null) scale.setBaseValue(small ? SMALL_SCALE : 1.0D);
        *///?}
        var hp = getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) hp.setBaseValue(kind().health() * (small ? 0.3D : 1.0D));
        setHealth(getMaxHealth());
        refreshDimensions();
    }

    //? if < 1.21.1 {
    @Override
    public float getScale() {
        return isSmall() ? SMALL_SCALE : 1.0F;
    }
    //?}

    private void play(int anim, int ticks) {
        entityData.set(ANIM, anim);
        animLeft = ticks;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ANIM.equals(key)) animTime = 0;
        if (SMALL.equals(key)) refreshDimensions();
    }

    // ── Ablauf ──

    @Override
    public void tick() {
        super.tick();
        animTime++;
        DungeonMobKind k = kind();
        if (level().isClientSide) {
            if (k.particle != null && random.nextInt(3) == 0) {
                level().addParticle(k.particle.get(), getRandomX(0.6), getY() + random.nextDouble() * getBbHeight(), getRandomZ(0.6), 0, 0.01, 0);
            }
            return;
        }
        if (animLeft > 0 && --animLeft == 0) entityData.set(ANIM, 0);
        if (k.move == DungeonMobKind.Move.FLY) {
            // sanft schweben, nicht absinken
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, v.y * 0.9 + Mth.sin(tickCount * 0.1F) * 0.004, v.z);
        }
        LivingEntity t = getTarget();
        if (t == null || !t.isAlive()) return;
        double d = distanceTo(t);
        if (leapCd > 0) leapCd--;
        if (slamCd > 0) slamCd--;
        if (teleportCd > 0) teleportCd--;
        // Sprung
        if (k.leap > 0 && leapCd == 0 && onGround() && d > 3.5 && d < 11 && hasLineOfSight(t)) {
            Vec3 dir = t.position().subtract(position()).normalize();
            setDeltaMovement(dir.x * 1.1, 0.55, dir.z * 1.1);
            hasImpulse = true;
            leapCd = k.leap;
            play(3, 14);
            playSound(SoundEvents.RAVAGER_ATTACK, 0.8F, 1.4F);
        }
        // Stampfen: Ausholen, dann Schlag
        if (k.slam > 0 && slamWind == 0 && slamCd == 0 && d < 4.5) {
            slamWind = 12;
            play(3, 22);
            getNavigation().stop();
        }
        if (slamWind > 0 && --slamWind == 0) slam(k);
        // Teleport hinter das Ziel
        if (k.teleport > 0 && teleportCd == 0 && (d > 7 || hurtTime > 0) && d < 24) {
            Vec3 back = t.position().subtract(t.getLookAngle().multiply(2.5, 0, 2.5));
            ServerLevel sl = (ServerLevel) level();
            sl.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 30, 0.4, 0.8, 0.4, 0.2);
            if (randomTeleport(back.x, t.getY(), back.z, true)) {
                sl.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 1, getZ(), 30, 0.4, 0.8, 0.4, 0.1);
                playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.7F);
                play(3, 10);
                teleportCd = k.teleport;
            } else teleportCd = 20;
        }
    }

    private void slam(DungeonMobKind k) {
        slamCd = k.slam;
        if (!(level() instanceof ServerLevel sl)) return;
        sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2, getZ(), 3, 1.2, 0.1, 1.2, 0);
        sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.2, getZ(), 30, 2.0, 0.1, 2.0, 0.05);
        playSound(SoundEvents.ANVIL_LAND, 1.0F, 0.6F);
        float dmg = (float) k.damage() * 0.9F;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(4.5),
                e -> e.isAlive() && !(e instanceof EntityDungeonMob) && !(e instanceof EntityStageBoss) && e.distanceToSqr(this) < 4.5 * 4.5)) {
            e.hurt(damageSources().mobAttack(this), dmg);
            Vec3 away = e.position().subtract(position()).normalize();
            e.knockback(1.2, -away.x, -away.z);
            e.setDeltaMovement(e.getDeltaMovement().add(0, 0.45, 0));
            e.hurtMarked = true;
            if (k.slamEffect != null) e.addEffect(k.slamEffect.get());
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            DungeonMobKind k = kind();
            play(1, 10);
            if (target instanceof LivingEntity l && k.hitEffect != null) l.addEffect(k.hitEffect.get(), this);
            if (k.fireSeconds > 0) {
                //? if < 1.21.1 {
                target.setSecondsOnFire(k.fireSeconds);
                //?} else {
                /*target.igniteForSeconds(k.fireSeconds);
                *///?}
            }
            if (k.attackSound != null) playSound(k.attackSound.get(), 1.0F, k.pitch);
        }
        return hit;
    }

    /** Schuss bzw. Salve auf das Ziel. */
    void shoot(LivingEntity t) {
        DungeonMobKind.Ranged r = kind().ranged;
        if (r == null) return;
        play(2, 14);
        Vec3 from = new Vec3(getX(), getY() + getBbHeight() * 0.7, getZ());
        for (int i = 0; i < r.volley(); i++) {
            Vec3 aim = t.getEyePosition().subtract(from);
            double dist = aim.length();
            Vec3 dir = aim.normalize();
            if (r.gravity() > 0) dir = dir.add(0, r.gravity() * dist / (r.speed() * r.speed()) * 0.5 * r.speed(), 0).normalize();
            dir = dir.add(random.nextGaussian() * r.spread(), random.nextGaussian() * r.spread() * 0.5, random.nextGaussian() * r.spread());
            EntityBossOrb orb = EntityBossOrb.shoot(level(), this, from, dir.normalize().scale(r.speed()), r.color(), r.size(), r.damage() * kind().power());
            if (r.gravity() > 0) orb.gravity(r.gravity());
            if (r.homing() > 0) orb.homing(t, r.homing());
            if (r.splash() > 0) orb.splash(r.splash());
            if (kind().hitEffect != null) orb.effect(kind().hitEffect.get());
            if (kind().fireSeconds > 0) orb.fire(kind().fireSeconds);
        }
        playSound(kind().attackSound != null ? kind().attackSound.get() : SoundEvents.BLAZE_SHOOT, 1.0F, kind().pitch);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!(level() instanceof ServerLevel sl)) return;
        DungeonMobKind k = kind();
        if (k.split > 0 && !isSmall()) {
            for (int i = 0; i < k.split; i++) {
                Entity e = getType().create(sl);
                if (!(e instanceof EntityDungeonMob m)) continue;
                m.moveTo(getX() + (random.nextDouble() - 0.5), getY() + 0.3, getZ() + (random.nextDouble() - 0.5), random.nextFloat() * 360F, 0);
                m.setSmall(true);
                if (isPersistenceRequired()) m.setPersistenceRequired();
                if (getTarget() != null) m.setTarget(getTarget());
                sl.addFreshEntity(m);
            }
        }
        if (k.deathCloud != null) {
            AreaEffectCloud cloud = new AreaEffectCloud(sl, getX(), getY(), getZ());
            cloud.setRadius(isSmall() ? 1.5F : 3.0F);
            cloud.setRadiusOnUse(-0.3F);
            cloud.setWaitTime(10);
            cloud.setDuration(120);
            cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
            cloud.addEffect(k.deathCloud.get());
            sl.addFreshEntity(cloud);
        }
    }

    // ── Physik, Umgebung ──

    @Override
    public boolean causeFallDamage(float distance, float mult, DamageSource source) {
        return kind().move == DungeonMobKind.Move.FLY ? false : super.causeFallDamage(distance, mult, source);
    }

    @Override
    protected void checkFallDamage(double dy, boolean onGround, BlockState state, BlockPos pos) {
        if (kind().move != DungeonMobKind.Move.FLY) super.checkFallDamage(dy, onGround, state, pos);
    }

    //? if < 1.21.1 {
    @Override
    public boolean canBreatheUnderwater() {
        return kind().breathesWater;
    }
    //?}

    @Override
    public boolean isPushedByFluid() {
        return kind().move != DungeonMobKind.Move.SWIM && super.isPushedByFluid();
    }

    @Override
    public void travel(Vec3 input) {
        if (kind().move == DungeonMobKind.Move.SWIM && isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed() * 0.1F, input);
            move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9D));
            return;
        }
        super.travel(input);
    }

    /** Spawner und Dungeon-Platzierung: auch im Hellen, in der Luft (Flieger) und im Wasser (Schwimmer). */
    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
        return true;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return !isPersistenceRequired() && dist > 64 * 64;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return kind().ambient.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return kind().hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return kind().death.get();
    }

    @Override
    public float getVoicePitch() {
        return kind().pitch * (isSmall() ? 1.4F : 1.0F) + (random.nextFloat() - 0.5F) * 0.15F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (kind().move != DungeonMobKind.Move.WALK) return;
        if (kind().step != null) playSound(kind().step.get(), 0.3F, kind().pitch);
        else super.playStepSound(pos, state);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Small", isSmall());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("Small")) entityData.set(SMALL, true);
    }

    // ── Fernkampf: Abstand halten, seitlich ausweichen, Kugeln schiessen; Flieger schweben ueber dem Ziel ──

    private final class RangedGoal extends Goal {
        private int seen, strafeTime;
        private boolean strafeLeft;

        RangedGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = getTarget();
            return t != null && t.isAlive();
        }

        @Override
        public void stop() {
            getNavigation().stop();
            seen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity t = getTarget();
            if (t == null) return;
            DungeonMobKind k = kind();
            DungeonMobKind.Ranged r = k.ranged;
            double d = distanceTo(t);
            boolean sight = getSensing().hasLineOfSight(t);
            seen = sight ? seen + 1 : 0;
            getLookControl().setLookAt(t, 30.0F, 30.0F);
            if (k.move == DungeonMobKind.Move.FLY) {
                double a = tickCount * 0.04 + getId();
                Vec3 want = t.position().add(Math.cos(a) * k.keepAway * 0.7, 3.0 + Math.sin(tickCount * 0.05) * 1.0, Math.sin(a) * k.keepAway * 0.7);
                if (tickCount % 10 == 0 || getNavigation().isDone()) getNavigation().moveTo(want.x, want.y, want.z, 1.0D);
            } else if (!sight || d > r.range() * 0.85) {
                if (tickCount % 10 == 0 || getNavigation().isDone()) getNavigation().moveTo(t, 1.0D);
            } else {
                getNavigation().stop();
                if (++strafeTime > 30) {
                    strafeTime = 0;
                    strafeLeft = random.nextBoolean();
                }
                float back = d < k.keepAway * 0.6 ? -0.5F : d > k.keepAway * 1.2 ? 0.5F : 0F;
                getMoveControl().strafe(back, strafeLeft ? 0.4F : -0.4F);
                setYRot(Mth.rotateIfNecessary(getYRot(), (float) (Mth.atan2(t.getZ() - getZ(), t.getX() - getX()) * Mth.RAD_TO_DEG) - 90F, 30F));
            }
            if (shotCd > 0) shotCd--;
            if (shotCd == 0 && sight && seen > 5 && d < r.range()) {
                shoot(t);
                shotCd = r.cooldown() + random.nextInt(Math.max(1, r.cooldown() / 3));
            }
        }
    }
}
