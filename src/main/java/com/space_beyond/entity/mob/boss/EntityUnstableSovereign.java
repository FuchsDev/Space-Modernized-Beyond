package com.space_beyond.entity.mob.boss;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.space_m.atmosphere.ISuffocationImmune;
import com.space_m.particle.SpaceParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Boss "The Unstable Sovereign" (eigene Erweiterung, Thronsaal auf Wemburos): schwebender Herrscher in Lila, Schwarz
 * und Gold, gut 4 Bloecke gross, mit rotierendem Goldhalo und Kristallfluegeln. Kampf als Zustandsautomat, jeder
 * Angriff hat eine sichtbare Vorwarnung (Runenkreise, Funkenlinien, erhobene Arme):
 * <ul>
 *   <li><b>Erwachen</b>: schwebt reglos ueber dem Podest, bis ein Spieler naeher als 20 Bloecke kommt, erhebt sich dann
 *       (2 s, unverwundbar) und die Bossleiste erscheint.</li>
 *   <li><b>Schweben</b>: umkreist sein Ziel in 6-8 Bloecken, folgt der Gelaendehoehe, schlaegt im Nahkampf mit dem
 *       Zepter zu.</li>
 *   <li><b>Kristallsalve</b>: Ring aus 6 (Phase 2: 10 goldenen) {@link EntityCrystalShard Splittern}, die um ihn kreisen
 *       und nacheinander zielsuchend abfeuern; abschiessbar.</li>
 *   <li><b>Souveraener Sturmlauf</b>: 1 s Anlauf mit Funkenlinie, dann ein Sturmangriff in gerader Linie (16 Schaden,
 *       schleudert weg); Phase 2: drei Laeufe hintereinander.</li>
 *   <li><b>Meteorsturz</b>: steigt 12 Bloecke auf, verfolgt das Ziel von oben, stuerzt herab - Schockwelle und
 *       Orbitalschlag am Einschlag; Phase 2 zusaetzlich ein Strahlenring.</li>
 *   <li><b>Orbitalgericht</b> (Phase 2): Strahlen auf und um das Ziel; Raserei: eine Sovereign Lance mit 3 s Vorwarnung.</li>
 *   <li><b>Riss-Sprung</b>: Teleport hinter das Ziel, wenn ihn ein Projektil trifft.</li>
 *   <li><b>Phasenwechsel</b> (50 %): steigt auf, Nova aus zwei Strahlenringen, ruft Goldwachen; Bossleiste wird gold.</li>
 *   <li><b>Tod</b>: 4 s Aufloesung - steigt auf, dreht sich, zerfaellt in Funken unter einem letzten Orbitalstrahl.</li>
 * </ul>
 * 1000 Leben (letzter und staerkster Stufen-Boss), Ruestung 18, kein Rueckstoss, feuerfest, verschwindet nie. Beute
 * ({@code loot_tables/entities/unstable_sovereign}): Orbital Strike Rod, Goldbloecke, Kristallsplitter, 500 XP.
 */
public class EntityUnstableSovereign extends Monster {

    public static final int IDLE = 0, AWAKEN = 1, HOVER = 2, DASH_WIND = 3, DASH = 4, RISE = 5, SLAM = 6, CAST = 7, SHIFT = 8;
    public static final int DEATH_TICKS = 80;

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EntityUnstableSovereign.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PHASE2 = SynchedEntityData.defineId(EntityUnstableSovereign.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            Component.translatable("entity.space_m.unstable_sovereign"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10)
            .setDarkenScreen(true);

    private int stateTime;
    private int attackCd = 40, meleeCd, castKind, dashesLeft, dying;
    private int lastAttack = -1;
    private float strafe;
    private Vec3 dashDir = Vec3.ZERO;
    private final Set<Integer> dashHits = new HashSet<>();

    public EntityUnstableSovereign(EntityType<? extends EntityUnstableSovereign> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG); // kein Ersticken/Veraetzen auf luftlosen Planeten
        this.setPersistenceRequired();
        this.setNoGravity(true);
        this.bossEvent.setVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5000.0D)
                .add(Attributes.ARMOR, 18.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, IDLE);
        this.entityData.define(PHASE2, false);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, IDLE);
        builder.define(PHASE2, false);
    }
    *///?}

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                p -> p.distanceToSqr(this) < 400.0D));
    }

    // ── Zustand (fuer Renderer und Partikel) ────────────────────────────────

    public int state() {
        return entityData.get(STATE);
    }

    public boolean phaseTwo() {
        return entityData.get(PHASE2);
    }

    /** Ticks im aktuellen Zustand (Client zaehlt selbst ab dem Zustandswechsel). */
    public float stateTime(float partialTick) {
        return stateTime + partialTick;
    }

    /** 0..1 Fortschritt der Todessequenz. */
    public float dying(float partialTick) {
        return dying <= 0 ? 0.0F : Mth.clamp((dying + partialTick) / DEATH_TICKS, 0F, 1F);
    }

    private void setState(int s) {
        entityData.set(STATE, s);
        stateTime = 0;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (STATE.equals(key)) stateTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        stateTime++;
        if (level().isClientSide && isAlive()) clientEffects();
    }

    // ── Kampf ───────────────────────────────────────────────────────────────

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) level();
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.isSpectator() || target instanceof Player p && p.isCreative())) {
            setTarget(null);
            target = null;
        }
        int state = state();
        bossEvent.setVisible(state != IDLE);
        if (meleeCd > 0) meleeCd--;

        switch (state) {
            case IDLE -> {
                setDeltaMovement(Vec3.ZERO);
                if (target != null && distanceToSqr(target) < 400.0D) awaken(level);
            }
            case AWAKEN -> {
                setDeltaMovement(0, 0.06D, 0);
                if (stateTime % 4 == 0) level.sendParticles(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX(), getY() + 0.2, getZ(), 12, 1.5, 0.1, 1.5, 0.05);
                if (stateTime >= 40) setState(HOVER);
            }
            case HOVER -> {
                if (target == null) {
                    setDeltaMovement(getDeltaMovement().scale(0.8D));
                    break;
                }
                if (!phaseTwo() && hp < 0.5F) {
                    beginShift(level);
                    break;
                }
                face(target);
                hoverAround(target);
                if (distanceToSqr(target) < 20.0D && meleeCd <= 0) {
                    swing(InteractionHand.MAIN_HAND);
                    doHurtTarget(target);
                    meleeCd = 24;
                }
                if (--attackCd <= 0) chooseAttack(level, target, hp);
            }
            case DASH_WIND -> {
                setDeltaMovement(getDeltaMovement().scale(0.6D));
                int wind = dashesLeft >= 2 || !phaseTwo() ? 22 : 12;
                if (target != null) {
                    face(target);
                    dashDir = target.position().add(0, 0.5, 0).subtract(position()).normalize();
                }
                // Funkenlinie zeigt die Bahn
                if (stateTime % 2 == 0) {
                    for (int i = 2; i < 18; i += 2) {
                        Vec3 p = position().add(0, 1.5, 0).add(dashDir.scale(i));
                        level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0);
                    }
                }
                if (stateTime == 1) playSound(SoundEvents.WARDEN_SONIC_CHARGE, 2.0F, 1.6F);
                if (stateTime >= wind) {
                    dashHits.clear();
                    setState(DASH);
                    playSound(SoundEvents.ENDER_DRAGON_FLAP, 3.0F, 0.6F);
                    playSound(SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 2.0F, 0.7F);
                }
            }
            case DASH -> {
                setDeltaMovement(dashDir.scale(phaseTwo() ? 1.9D : 1.6D));
                setYRot((float) (Mth.atan2(dashDir.z, dashDir.x) * Mth.RAD_TO_DEG) - 90.0F);
                yBodyRot = getYRot();
                yHeadRot = getYRot();
                level.sendParticles(com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getX(), getY() + 2, getZ(), 6, 0.5, 1.2, 0.5, 0.02);
                level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 2, getZ(), 3, 0.4, 1.0, 0.4, 0.05);
                for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.8D), this::isFoe)) {
                    if (!dashHits.add(e.getId())) continue;
                    e.hurt(damageSources().mobAttack(this), phaseTwo() ? 20.0F : 16.0F);
                    e.setDeltaMovement(dashDir.x * 2.2D, 0.8D, dashDir.z * 2.2D);
                    e.hurtMarked = true;
                    playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, 0.6F);
                }
                if (stateTime >= 12 || horizontalCollision) {
                    if (horizontalCollision) {
                        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.5, getZ(), 2, 0.5, 0.5, 0.5, 0);
                        playSound(SoundEvents.ANVIL_LAND, 2.0F, 0.5F);
                    }
                    setDeltaMovement(getDeltaMovement().scale(0.2D));
                    if (--dashesLeft > 0) setState(DASH_WIND);
                    else endAttack();
                }
            }
            case RISE -> {
                double vy = stateTime < 20 ? 0.6D : 0.0D;
                Vec3 v = Vec3.ZERO;
                if (target != null && stateTime >= 10) {
                    // ueber das Ziel gleiten
                    Vec3 d = target.position().subtract(position()).multiply(1, 0, 1);
                    v = d.lengthSqr() > 0.01 ? d.normalize().scale(Math.min(0.55D, d.length() * 0.15D)) : Vec3.ZERO;
                }
                setDeltaMovement(v.x, vy, v.z);
                if (stateTime == 1) {
                    playSound(SoundEvents.BEACON_ACTIVATE, 3.0F, 0.5F);
                    playSound(SoundEvents.ELYTRA_FLYING, 1.0F, 0.6F);
                }
                // goldene Spirale und Zielkreis am Boden
                double a = stateTime * 0.6D;
                for (int i = 0; i < 3; i++) {
                    double ang = a + i * Math.PI * 2 / 3;
                    level.sendParticles(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX() + Math.cos(ang) * 1.6, getY() + 1 + (stateTime % 10) * 0.3,
                            getZ() + Math.sin(ang) * 1.6, 1, 0, 0, 0, 0);
                }
                if (target != null && stateTime % 3 == 0) {
                    for (int i = 0; i < 16; i++) {
                        double ang = i * Math.PI / 8 + stateTime * 0.1;
                        level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), target.getX() + Math.cos(ang) * 5, target.getY() + 0.2,
                                target.getZ() + Math.sin(ang) * 5, 1, 0, 0, 0, 0);
                    }
                }
                if (stateTime >= 34) {
                    setState(SLAM);
                    playSound(SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 0.6F);
                }
            }
            case SLAM -> {
                setDeltaMovement(0, -2.4D, 0);
                level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 3, getZ(), 6, 0.6, 1.5, 0.6, 0.1);
                if (onGround() || verticalCollisionBelow() || stateTime > 30) {
                    slamImpact(level);
                    endAttack();
                }
            }
            case CAST -> {
                setDeltaMovement(getDeltaMovement().scale(0.7D));
                if (target != null) face(target);
                cast(level, target);
            }
            case SHIFT -> {
                setDeltaMovement(0, stateTime < 20 ? 0.12D : 0.0D, 0);
                shift(level);
            }
            default -> setState(HOVER);
        }
    }

    private boolean verticalCollisionBelow() {
        return verticalCollision && getDeltaMovement().y <= 0;
    }

    private boolean isFoe(LivingEntity e) {
        return e != this && e.isAlive() && !(e instanceof Enemy) && !(e instanceof Player p && (p.isCreative() || p.isSpectator()));
    }

    private void face(LivingEntity target) {
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(Mth.approachDegrees(getYRot(), yaw, 20.0F));
        yBodyRot = getYRot();
        getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    /** Kreist in 6-8 Bloecken um das Ziel, gut 2 Bloecke ueber dessen Fuessen, mit weicher Beschleunigung. */
    private void hoverAround(LivingEntity target) {
        strafe += phaseTwo() ? 0.035F : 0.025F;
        double dist = phaseTwo() ? 6.0D : 7.5D;
        double ang = Math.atan2(getZ() - target.getZ(), getX() - target.getX()) + 0.12D;
        Vec3 want = new Vec3(target.getX() + Math.cos(ang) * dist, target.getY() + 2.0D + Mth.sin(strafe * 3) * 0.6D,
                target.getZ() + Math.sin(ang) * dist);
        if (distanceToSqr(target) < 12.0D) want = want.add(position().subtract(target.position()).multiply(1, 0, 1).normalize().scale(2));
        Vec3 d = want.subtract(position());
        double max = phaseTwo() ? 0.5D : 0.38D;
        Vec3 v = d.scale(0.12D);
        if (v.length() > max) v = v.normalize().scale(max);
        setDeltaMovement(getDeltaMovement().scale(0.6D).add(v.scale(0.4D)));
    }

    private void chooseAttack(ServerLevel level, LivingEntity target, float hp) {
        boolean rage = hp < 0.25F;
        int pick;
        int options = phaseTwo() ? 4 : 3;
        do {
            pick = random.nextInt(options);
        } while (pick == lastAttack && options > 1);
        lastAttack = pick;
        switch (pick) {
            case 0 -> {
                castKind = 0;
                setState(CAST);
            }
            case 1 -> {
                dashesLeft = phaseTwo() ? 3 : 1;
                setState(DASH_WIND);
            }
            case 2 -> setState(RISE);
            default -> {
                castKind = rage && random.nextBoolean() ? 2 : 1;
                setState(CAST);
            }
        }
    }

    private void endAttack() {
        float hp = getHealth() / getMaxHealth();
        attackCd = hp < 0.25F ? 18 : phaseTwo() ? 28 : 45;
        setState(HOVER);
    }

    /** Zauber: 0 Kristallsalve, 1 Orbitalgericht, 2 Sovereign Lance (Raserei). */
    private void cast(ServerLevel level, LivingEntity target) {
        int t = stateTime;
        // Funken laufen in der erhobenen Hand zusammen
        Vec3 hand = scepterTip();
        if (t % 2 == 0) level.sendParticles(castKind == 0 ? com.space_beyond.particle.BeyondParticles.VOID_MOTE.get() : com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(),
                hand.x, hand.y, hand.z, 3, 0.4, 0.4, 0.4, 0.02);
        switch (castKind) {
            case 0 -> {
                if (t == 1) playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 3.0F, 0.5F);
                if (t == 8) {
                    int n = phaseTwo() ? 10 : 6;
                    for (int i = 0; i < n; i++) {
                        level.addFreshEntity(EntityCrystalShard.create(level, this, target, (float) (i * Math.PI * 2 / n), 16 + i * 4, phaseTwo()));
                    }
                    playSound(SoundEvents.AMETHYST_CLUSTER_PLACE, 3.0F, 0.6F);
                }
                if (t >= 30) endAttack();
            }
            case 1 -> {
                if (t == 1) playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 3.0F, 0.5F);
                if (t == 12 && target != null) {
                    OrbitalStrikes.boss(level, target.position(), this, 34);
                    for (int i = 0; i < 4; i++) {
                        double a = random.nextDouble() * Math.PI * 2, d = 3 + random.nextDouble() * 5;
                        OrbitalStrikes.boss(level, OrbitalStrikes.ground(level, target.getX() + Math.cos(a) * d, target.getY(), target.getZ() + Math.sin(a) * d),
                                this, 40 + i * 6);
                    }
                }
                if (t >= 40) endAttack();
            }
            default -> {
                if (t == 1) {
                    playSound(SoundEvents.WITHER_SPAWN, 2.0F, 1.4F);
                    for (ServerPlayer p : bossEvent.getPlayers()) p.displayClientMessage(Component.translatable("entity.space_m.unstable_sovereign.lance"), true);
                }
                if (t == 14 && target != null) OrbitalStrikes.bossLance(level, target.position(), this);
                if (t >= 50) endAttack();
            }
        }
    }

    /** Position der Zepterspitze (rechte Hand, erhoben). */
    public Vec3 scepterTip() {
        float yaw = yBodyRot * Mth.DEG_TO_RAD;
        return new Vec3(getX() - Mth.cos(yaw) * 1.4D, getY() + 5.2D, getZ() - Mth.sin(yaw) * 1.4D);
    }

    private void slamImpact(ServerLevel level) {
        playSound(SoundEvents.ANVIL_LAND, 3.0F, 0.4F);
        playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 0.6F);
        OrbitalStrikes.boss(level, position(), this, 1);
        level.sendParticles(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX(), getY() + 0.3, getZ(), 80, 4.0, 0.2, 4.0, 0.2);
        List<LivingEntity> hit = level.getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(8.0D, 3.0D, 8.0D), this::isFoe);
        for (LivingEntity e : hit) {
            double d = e.distanceTo(this);
            if (d > 8.0D) continue;
            float f = (float) (1.0D - d / 8.0D * 0.6D);
            e.hurt(damageSources().mobAttack(this), 16.0F * f);
            Vec3 push = e.position().subtract(position()).multiply(1, 0, 1).normalize().scale(1.6D * f);
            e.setDeltaMovement(push.x, 1.0D * f + 0.3D, push.z);
            e.hurtMarked = true;
        }
        if (phaseTwo()) OrbitalStrikes.ring(level, position(), this, 6, 7.0D, 20, 2);
    }

    // ── Erwachen, Phasenwechsel, Tod ───────────────────────────────────────

    private void awaken(ServerLevel level) {
        setState(AWAKEN);
        playSound(SoundEvents.END_PORTAL_SPAWN, 3.0F, 0.7F);
        playSound(SoundEvents.BEACON_ACTIVATE, 3.0F, 0.5F);
        for (ServerPlayer p : bossEvent.getPlayers()) p.displayClientMessage(Component.translatable("entity.space_m.unstable_sovereign.awaken"), true);
        OrbitalStrikes.cosmetic(level, position(), OrbitalBeamEntity.BOSS, 38);
    }

    private void beginShift(ServerLevel level) {
        entityData.set(PHASE2, true);
        setState(SHIFT);
        bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
        playSound(SoundEvents.WITHER_SPAWN, 3.0F, 0.7F);
        for (ServerPlayer p : bossEvent.getPlayers()) p.sendSystemMessage(Component.translatable("entity.space_m.unstable_sovereign.phase2"));
    }

    private void shift(ServerLevel level) {
        int t = stateTime;
        if (t % 3 == 0) level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 2.5, getZ(), 20, 2.0, 2.0, 2.0, 0.3);
        if (t == 20) {
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 2, getZ(), 1, 0, 0, 0, 0);
            Vec3 floor = OrbitalStrikes.ground(level, getX(), getY() - 3, getZ());
            OrbitalStrikes.ring(level, floor, this, 8, 5.0D, 10, 1);
            OrbitalStrikes.ring(level, floor, this, 12, 10.0D, 26, 1);
        }
        if (t == 30) summonGuards(level);
        if (t >= 60) {
            attackCd = 20;
            setState(HOVER);
        }
    }

    private void summonGuards(ServerLevel level) {
        for (int i = 0; i < 2; i++) {
            var guard = EntityType.WITHER_SKELETON.create(level);
            if (guard == null) continue;
            Vec3 at = OrbitalStrikes.ground(level, getX() + (i == 0 ? -4 : 4), getY() - 4, getZ());
            guard.moveTo(at.x, at.y, at.z, getYRot(), 0);
            guard.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            guard.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            guard.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            guard.setPersistenceRequired();
            if (getTarget() != null) guard.setTarget(getTarget());
            level.addFreshEntity(guard);
            level.sendParticles(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), at.x, at.y + 1, at.z, 40, 0.4, 1.0, 0.4, 0.1);
        }
        for (int i = 0; i < 3; i++) {
            var mite = EntityType.ENDERMITE.create(level);
            if (mite == null) continue;
            mite.moveTo(getX() + random.nextGaussian() * 3, getY() - 3, getZ() + random.nextGaussian() * 3, 0, 0);
            if (getTarget() != null) mite.setTarget(getTarget());
            level.addFreshEntity(mite);
        }
    }

    /** Riss-Sprung hinter das Ziel (sucht festen Boden). */
    private void blinkBehind(LivingEntity target) {
        Vec3 look = target.getLookAngle();
        ServerLevel level = (ServerLevel) level();
        for (int tries = 0; tries < 8; tries++) {
            double x = target.getX() - look.x * 5 + random.nextGaussian() * 2;
            double z = target.getZ() - look.z * 5 + random.nextGaussian() * 2;
            BlockPos base = BlockPos.containing(x, target.getY() + 4, z);
            for (int dy = 0; dy < 8; dy++) {
                BlockPos p = base.below(dy);
                double y = p.getY() + 1.5D;
                if (level.getBlockState(p.below()).isSolid() && level.noCollision(this, getBoundingBox().move(x - getX(), y - getY(), z - getZ()))) {
                    burst(level, com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getX(), getY() + 2, getZ(), 50);
                    teleportTo(x, y, z);
                    burst(level, com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), x, y + 2, z, 40);
                    level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 2.0F, 0.5F);
                    return;
                }
            }
        }
    }

    private static void burst(ServerLevel level, ParticleOptions type, double x, double y, double z, int n) {
        level.sendParticles(type, x, y, z, n, 0.6, 1.5, 0.6, 0.15);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int state = state();
        if (state == AWAKEN || state == SHIFT) return false;
        if (source.getDirectEntity() instanceof EntityCrystalShard || source.getDirectEntity() instanceof OrbitalBeamEntity && source.getEntity() == this) return false;
        if (state == IDLE && source.getEntity() instanceof LivingEntity attacker && !level().isClientSide) {
            setTarget(attacker);
            awaken((ServerLevel) level());
            return false;
        }
        boolean projectile = source.getDirectEntity() instanceof Projectile;
        // Schadensdeckel wie bei den Stufen-Bosses (gegen Ein-Schuss-Waffen)
        if (!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = Math.min(amount, getMaxHealth() * 0.05F);
        boolean result = super.hurt(source, amount);
        if (result && projectile && state == HOVER && !level().isClientSide && getTarget() != null && random.nextFloat() < 0.5F) {
            blinkBehind(getTarget());
        }
        return result;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && level() instanceof ServerLevel level) {
            level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), target.getX(), target.getY() + 1, target.getZ(), 20, 0.4, 0.6, 0.4, 0.2);
            playSound(SoundEvents.AMETHYST_BLOCK_HIT, 2.0F, 0.6F);
        }
        return hit;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level() instanceof ServerLevel level) {
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 3.0F, 1.2F);
            // Planeten-Fortschritt: letzter Boss (Stufe 11)
            com.space_beyond.progression.SpaceProgression.bossDefeated(level, com.space_beyond.progression.SpaceProgression.Stage.WEMBUROS);
            com.space_beyond.dim.dungeon.seal.DungeonProtection.markDefeated(level, blockPosition());
            OrbitalStrikes.cosmetic(level, OrbitalStrikes.ground(level, getX(), getY() - 2, getZ()), OrbitalBeamEntity.LANCE, DEATH_TICKS - 6);
        }
    }

    /** Eigene Todessequenz statt des Umkippens: steigt auf, dreht sich, zerfaellt in Funken. */
    @Override
    protected void tickDeath() {
        dying++;
        setDeltaMovement(0, 0.04D, 0);
        if (level() instanceof ServerLevel level) {
            bossEvent.setProgress(0);
            if (dying % 4 == 0) {
                level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 2.5, getZ(), 12 + dying / 3, 1.0, 2.0, 1.0, 0.25);
                level.sendParticles(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX(), getY() + 2.5, getZ(), 6, 1.0, 2.0, 1.0, 0.1);
            }
            if (dying % 20 == 0) playSound(SoundEvents.AMETHYST_CLUSTER_BREAK, 3.0F, 0.5F + dying / 80.0F);
            if (dying >= DEATH_TICKS && !isRemoved()) {
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 2, getZ(), 2, 0.5, 0.5, 0.5, 0);
                level.sendParticles(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 2, getZ(), 300, 1.0, 2.0, 1.0, 0.6);
                for (ServerPlayer p : bossEvent.getPlayers()) p.sendSystemMessage(Component.translatable("entity.space_m.unstable_sovereign.defeated"));
                level.broadcastEntityEvent(this, (byte) 60);
                remove(RemovalReason.KILLED);
            }
        }
    }

    // ── Client: Partikel ────────────────────────────────────────────────────

    private void clientEffects() {
        int state = state();
        if (state == IDLE) {
            if (random.nextInt(4) == 0) level().addParticle(com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getRandomX(1.0D), getY() + random.nextDouble() * 4, getRandomZ(1.0D), 0, 0.02, 0);
            return;
        }
        // Saum loest sich in lila Staub auf, Halo spruehrt Gold
        level().addParticle(com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getRandomX(0.9D), getY() + 0.2, getRandomZ(0.9D), 0, -0.03, 0);
        float yaw = yBodyRot * Mth.DEG_TO_RAD;
        if (random.nextInt(phaseTwo() ? 1 : 3) == 0) {
            double a = random.nextDouble() * Math.PI * 2;
            level().addParticle(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX() + Mth.sin(yaw) * 0.6 + Math.cos(a) * 0.9, getY() + 5.0 + Math.sin(a) * 0.9,
                    getZ() - Mth.cos(yaw) * 0.6, 0, 0.02, 0);
        }
        if (state == DASH) {
            for (int i = 0; i < 4; i++) level().addParticle(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getRandomX(1.2D), getY() + random.nextDouble() * 4, getRandomZ(1.2D), 0, 0, 0);
        }
        if (state == CAST || state == SHIFT) {
            Vec3 tip = scepterTip();
            for (int i = 0; i < 2; i++) {
                double ox = random.nextGaussian() * 1.5, oy = random.nextGaussian() * 1.5, oz = random.nextGaussian() * 1.5;
                level().addParticle(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), tip.x + ox, tip.y + oy, tip.z + oz, -ox * 0.12, -oy * 0.12, -oz * 0.12);
            }
        }
    }

    // ── Bossleiste, Speichern, Eigenschaften ───────────────────────────────

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("PhaseTwo", phaseTwo());
        tag.putBoolean("Awake", state() != IDLE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(PHASE2, tag.getBoolean("PhaseTwo"));
        if (tag.getBoolean("Awake")) setState(HOVER);
        if (phaseTwo()) bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public float getVoicePitch() {
        return 0.55F + random.nextFloat() * 0.1F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    /** Fuer den Renderer: Schwebehoehe (Sinus). */
    public float hover(float partialTick) {
        return Mth.sin((tickCount + partialTick) * 0.08F) * 0.12F;
    }
}
