package com.space_beyond.entity.mob.stage;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.space_m.atmosphere.ISuffocationImmune;
import com.space_beyond.progression.SpaceProgression;
import com.space_beyond.progression.SpaceProgression.Stage;

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
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Basis der Stufen-Bosse (Planeten-Fortschritt, je Planet einer, jeder staerker als der vorige). Gemeinsam:
 * <ul>
 *   <li>Ruht, bis ein Spieler naeher als {@link #wakeRange()} kommt, dann Erwachen (1,5 s, unverwundbar) und
 *       Bossleiste; ab 50 % Phase 2 (Ankuendigung, schnellere Angriffe, Verstaerkung).</li>
 *   <li>Angriffe als Liste ({@link Attack}): jeder mit Abklingzeit, Reichweite, optional nur Phase 2; ein Angriff
 *       laeuft ueber mehrere Ticks (Vorwarnung -> Wirkung), dazwischen Nahkampf bzw. Verfolgung.</li>
 *   <li>Bewegung {@link Move#WALK} (Pfadsuche + Nahkampf) oder {@link Move#FLY} (schwebt um das Ziel, auch im Wasser).</li>
 *   <li>Tod: Freischaltung der naechsten Stufe ({@link SpaceProgression#bossDefeated}), Beute aus der Loot-Tabelle.</li>
 *   <li>Haerte (Nutzerwunsch "die Bosse sind zu schwach"): Leben 400 (Mun) bis 3600 (Sporos), Schadensdeckel je Treffer
 *       ({@link #HIT_CAP} des Lebens), drei Phasen (50 %, 25 % - Wut: schneller, staerker), bei jedem Phasenwechsel ein
 *       Schild, solange seine Diener leben, Angriffsstaerke je Stufe ({@link #power()}), Leine zur Arena und
 *       vollstaendige Heilung, wenn niemand mehr kaempft.</li>
 * </ul>
 */
public abstract class EntityStageBoss extends Monster implements PosedMob {

    public enum Move { WALK, FLY }

    /** Ein Angriff: {@code run} wird je Tick mit der Laufzeit aufgerufen und meldet, wann er fertig ist. */
    public record Attack(String name, int cooldown, double range, boolean phaseTwoOnly, Runner run) { }

    @FunctionalInterface
    public interface Runner {
        boolean tick(ServerLevel level, LivingEntity target, int t);
    }

    private static final EntityDataAccessor<Integer> ANIM = SynchedEntityData.defineId(EntityStageBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AWAKE = SynchedEntityData.defineId(EntityStageBoss.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHASE2 = SynchedEntityData.defineId(EntityStageBoss.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHASE3 = SynchedEntityData.defineId(EntityStageBoss.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHIELD = SynchedEntityData.defineId(EntityStageBoss.class, EntityDataSerializers.BOOLEAN);

    /** Leben je Stufe (Mun ... Sporos). */
    public static final double[] HEALTH = {400, 550, 750, 1000, 1300, 1650, 2050, 2500, 3000, 3600};
    /** Hoechstens dieser Anteil des Lebens je Treffer (gegen Ein-Schuss-Waffen). */
    public static final float HIT_CAP = 0.06F;
    /** Leine: weiter als so viele Bloecke vom Erwachungsort -> zurueck; ohne Spieler im Umkreis -> Heilung. */
    private static final double LEASH = 30.0D, IDLE_RANGE = 48.0D;

    protected final ServerBossEvent bossEvent;
    protected final List<Attack> attacks = new ArrayList<>();
    private final int[] cooldowns = new int[16];
    @Nullable private Attack current;
    private int attackTime, gap = 40, awakenTime, animTime, shieldTime, idleTime, animHold;
    private final List<java.util.UUID> shieldMinions = new ArrayList<>();
    @Nullable private net.minecraft.core.BlockPos home;
    private float strafe;

    protected EntityStageBoss(EntityType<? extends EntityStageBoss> type, Level level, BossEvent.BossBarColor color) {
        super(type, level);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(type.getDescription(), color, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
        this.bossEvent.setVisible(false);
        this.xpReward = 60 + 40 * stage().tier();
        this.addTag(com.space_beyond.lib.BeyondTags.DUNGEON_TAG); // kein Ersticken/Veraetzen auf luftlosen Planeten
        this.setPersistenceRequired();
        if (move() == Move.FLY) this.setNoGravity(true);
        registerAttacks();
    }

    /** Stufe/Planet dieses Bosses. */
    public abstract Stage stage();

    /** Angriffe eintragen ({@link #attacks}). */
    protected abstract void registerAttacks();

    public Move move() {
        return Move.WALK;
    }

    protected double wakeRange() {
        return 18.0D;
    }

    /** Werte je Stufe: Leben, Ruestung, Schaden, Tempo. */
    public static AttributeSupplier.Builder attributes(int tier, double speed) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH[Math.max(0, Math.min(HEALTH.length - 1, tier - 1))])
                .add(Attributes.ARMOR, 8.0D + tier * 1.2D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D + tier * 0.6D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D + tier * 1.6D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2D)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    /** Staerke der Angriffe (Schaden der Strahlen, Wellen, Geschosse): je Stufe +12 %, in der Wut +25 %. */
    public float power() {
        return 1.0F + 0.12F * stage().tier() + (phaseThree() ? 0.25F : 0.0F);
    }

    /** Diener, die den Schild tragen (je Boss thematisch). */
    protected String minionType() {
        return switch (stage()) {
            case MUN -> "space_beyond:lost_astronaut";
            case MINMUS -> "space_beyond:frost_wisp";
            case DUNA -> "space_beyond:rust_scuttler";
            case IKE -> "space_beyond:void_shade";
            case DRES -> "space_beyond:irradiated_ghoul";
            case MOHO -> "space_beyond:cinder_imp";
            case EVE -> "space_beyond:crystal_mantis";
            case LAYTHE -> "space_beyond:abyss_diver";
            case TEKTO -> "space_beyond:refinery_drone";
            default -> "space_beyond:mycobrute";
        };
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM, 0);
        this.entityData.define(AWAKE, false);
        this.entityData.define(PHASE2, false);
        this.entityData.define(PHASE3, false);
        this.entityData.define(SHIELD, false);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM, 0);
        builder.define(AWAKE, false);
        builder.define(PHASE2, false);
        builder.define(PHASE3, false);
        builder.define(SHIELD, false);
    }
    *///?}

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if (move() == Move.WALK) this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                p -> p.distanceToSqr(this) < wakeRange() * wakeRange() * 1.6D));
    }

    // ── Zustand fuer Renderer ───────────────────────────────────────────────

    @Override
    public boolean awake() {
        return entityData.get(AWAKE);
    }

    @Override
    public boolean phaseTwo() {
        return entityData.get(PHASE2);
    }

    @Override
    public boolean phaseThree() {
        return entityData.get(PHASE3);
    }

    /** Schild aktiv (unverwundbar, solange die Diener leben). */
    public boolean shielded() {
        return entityData.get(SHIELD);
    }

    /** Index des laufenden Angriffs + 1 (0 = keiner), fuer Posen. */
    @Override
    public int anim() {
        return entityData.get(ANIM);
    }

    /** Ticks seit Beginn der aktuellen Animation (Client zaehlt selbst). */
    @Override
    public float animTime(float pt) {
        return animTime + pt;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ANIM.equals(key)) animTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        animTime++;
        if (level().isClientSide && isAlive() && awake()) {
            clientEffects();
            if (shielded()) {
                for (int i = 0; i < 3; i++) {
                    double a = random.nextDouble() * Math.PI * 2, r = getBbWidth() * 0.8 + 0.6;
                    level().addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, getX() + Math.cos(a) * r, getY() + random.nextDouble() * getBbHeight(),
                            getZ() + Math.sin(a) * r, 0, 0.02, 0);
                }
            }
            if (phaseThree() && random.nextInt(3) == 0) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME, getRandomX(0.8), getY() + random.nextDouble() * getBbHeight(), getRandomZ(0.8), 0, 0.03, 0);
            }
        }
    }

    /** Partikel je Boss (Client). */
    protected void clientEffects() {
    }

    // ── Kampf ───────────────────────────────────────────────────────────────

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) level();
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            setTarget(null);
            target = null;
        }
        if (!awake()) {
            setDeltaMovement(getDeltaMovement().multiply(0, move() == Move.FLY ? 0 : 1, 0));
            if (target != null && distanceToSqr(target) < wakeRange() * wakeRange()) awaken(level);
            return;
        }
        bossEvent.setVisible(true);
        if (awakenTime > 0) {
            awakenTime--;
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        if (home == null) home = blockPosition();
        if (tickCount % 20 == 0 && leashAndIdle(level)) return;
        if (!phaseTwo() && hp < 0.5F) {
            entityData.set(PHASE2, true);
            onPhaseTwo(level);
            raiseShield(level);
            for (ServerPlayer p : bossEvent.getPlayers()) {
                p.sendSystemMessage(Component.translatable("entity.space_m." + id() + ".phase2"));
            }
        }
        if (phaseTwo() && !phaseThree() && hp < 0.25F) {
            entityData.set(PHASE3, true);
            enrage(level);
            raiseShield(level);
            for (ServerPlayer p : bossEvent.getPlayers()) {
                p.sendSystemMessage(Component.translatable("boss.space_m.enraged", getDisplayName()).withStyle(net.minecraft.ChatFormatting.RED));
            }
        }
        if (shielded()) tickShield(level);
        if (current == null && animHold > 0 && --animHold == 0) entityData.set(ANIM, 0);
        for (int i = 0; i < attacks.size(); i++) if (cooldowns[i] > 0) cooldowns[i]--;
        if (target == null) return;

        if (move() == Move.FLY && current == null) hover(target);
        if (move() == Move.FLY && current == null && distanceToSqr(target) < 9.0D && tickCount % 20 == 0) {
            swing(InteractionHand.MAIN_HAND);
            doHurtTarget(target);
        }
        if (current != null) {
            getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (current.run().tick(level, target, attackTime++)) {
                current = null;
                // Pose sichtbar lassen: kurze Angriffe (1 Tick) zeigen ihre Animation mindestens 20 Ticks lang
                animHold = Math.max(0, 20 - attackTime);
                if (animHold == 0) entityData.set(ANIM, 0);
                gap = phaseThree() ? 10 : phaseTwo() ? 16 : 28;
            }
            return;
        }
        if (--gap > 0) return;
        // naechsten Angriff waehlen: zufaellig unter den bereiten, in Reichweite
        List<Integer> ready = new ArrayList<>();
        double dist = Math.sqrt(distanceToSqr(target));
        for (int i = 0; i < attacks.size(); i++) {
            Attack a = attacks.get(i);
            if (cooldowns[i] > 0 || dist > a.range() || (a.phaseTwoOnly() && !phaseTwo())) continue;
            ready.add(i);
        }
        if (ready.isEmpty()) {
            gap = 10;
            return;
        }
        int pick = ready.get(random.nextInt(ready.size()));
        current = attacks.get(pick);
        cooldowns[pick] = (int) (current.cooldown() * (phaseThree() ? 0.45F : phaseTwo() ? 0.65F : 0.85F));
        attackTime = 0;
        entityData.set(ANIM, pick + 1);
    }

    /** Fliegende Bosse: kreisen in 6-8 Bloecken, 2-3 ueber dem Ziel. */
    protected void hover(LivingEntity target) {
        strafe += 0.03F;
        double ang = Math.atan2(getZ() - target.getZ(), getX() - target.getX()) + 0.1D;
        double dist = 7.0D;
        Vec3 want = new Vec3(target.getX() + Math.cos(ang) * dist, target.getY() + 2.5D + Mth.sin(strafe * 3) * 0.8D,
                target.getZ() + Math.sin(ang) * dist);
        Vec3 v = want.subtract(position()).scale(0.1D);
        double max = phaseTwo() ? 0.45D : 0.33D;
        if (v.length() > max) v = v.normalize().scale(max);
        setDeltaMovement(getDeltaMovement().scale(0.6D).add(v.scale(0.4D)));
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        setYRot((float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F);
        yBodyRot = getYRot();
    }

    // ── Schild, Wut, Leine ──────────────────────────────────────────────────

    /** Schild hoch: Diener erscheinen; solange einer lebt (hoechstens 30 s), ist der Boss unverwundbar. */
    protected void raiseShield(ServerLevel level) {
        shieldMinions.clear();
        int n = 2 + stage().tier() / 3;
        for (net.minecraft.world.entity.Mob m : BossAttacks.summon(level, this, minionType(), n, 5)) {
            shieldMinions.add(m.getUUID());
            m.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 20 * 40, 0, false, false));
        }
        if (shieldMinions.isEmpty()) return;
        entityData.set(SHIELD, true);
        shieldTime = 0;
        playSound(SoundEvents.BEACON_ACTIVATE, 3.0F, 0.6F);
        for (ServerPlayer p : bossEvent.getPlayers()) {
            p.displayClientMessage(Component.translatable("boss.space_m.shield").withStyle(net.minecraft.ChatFormatting.AQUA), true);
        }
    }

    private void tickShield(ServerLevel level) {
        shieldTime++;
        if (shieldTime % 10 != 0) return;
        shieldMinions.removeIf(id -> {
            Entity e = level.getEntity(id);
            return e == null || !e.isAlive();
        });
        if (shieldMinions.isEmpty() || shieldTime > 600) {
            entityData.set(SHIELD, false);
            playSound(SoundEvents.BEACON_DEACTIVATE, 3.0F, 0.8F);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, getX(), getY() + getBbHeight() / 2, getZ(), 60, 1.0, 1.0, 1.0, 0.2);
        }
    }

    /** Wut (Phase 3): schneller und haerter. */
    protected void enrage(ServerLevel level) {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(speed.getBaseValue() * 1.25D);
        var dmg = getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() * 1.3D);
        playSound(SoundEvents.WITHER_SPAWN, 3.0F, 1.3F);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, getX(), getY() + getBbHeight() / 2, getZ(), 80, 1.2, 1.2, 1.2, 0.15);
    }

    /** Alle 20 Ticks: zu weit weg -> zurueck zum Erwachungsort; lange kein Spieler in der Naehe -> Heilung und Schlaf. */
    private boolean leashAndIdle(ServerLevel level) {
        if (home != null && distanceToSqr(home.getX() + 0.5, home.getY(), home.getZ() + 0.5) > LEASH * LEASH) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 40, 0.6, 1.0, 0.6, 0.3);
            teleportTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
            playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.6F);
        }
        Player near = level.getNearestPlayer(this, IDLE_RANGE);
        boolean fighting = near != null && !near.isCreative() && !near.isSpectator();
        idleTime = fighting ? 0 : idleTime + 20;
        if (idleTime < 600) return false;
        // Reset: volle Heilung, schlafen, Schild und Phasen zurueck
        setHealth(getMaxHealth());
        entityData.set(AWAKE, false);
        entityData.set(PHASE2, false);
        entityData.set(PHASE3, false);
        entityData.set(SHIELD, false);
        shieldMinions.clear();
        idleTime = 0;
        current = null;
        entityData.set(ANIM, 0);
        bossEvent.setVisible(false);
        bossEvent.setProgress(1.0F);
        setTarget(null);
        return true;
    }

    protected void awaken(ServerLevel level) {
        entityData.set(AWAKE, true);
        awakenTime = 30;
        bossEvent.setVisible(true);
        playSound(awakenSound(), 3.0F, 0.7F);
        for (ServerPlayer p : bossEvent.getPlayers()) p.displayClientMessage(Component.translatable("entity.space_m." + id() + ".awaken"), true);
    }

    protected SoundEvent awakenSound() {
        return SoundEvents.WITHER_SPAWN;
    }

    /** Phase 2 beginnt (Verstaerkung, Effekte). */
    protected void onPhaseTwo(ServerLevel level) {
    }

    protected String id() {
        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!awake() && source.getEntity() instanceof LivingEntity attacker && !level().isClientSide) {
            setTarget(attacker);
            awaken((ServerLevel) level());
            return false;
        }
        if (awakenTime > 0) return false;
        boolean bypass = source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY);
        if (shielded() && !bypass) {
            if (!level().isClientSide && tickCount % 4 == 0) {
                playSound(SoundEvents.SHIELD_BLOCK, 1.5F, 0.7F);
                ((ServerLevel) level()).sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.6, getZ(), 8, 0.6, 0.6, 0.6, 0.1);
            }
            return false;
        }
        if (!bypass) amount = Math.min(amount, getMaxHealth() * HIT_CAP);
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level() instanceof ServerLevel level) {
            SpaceProgression.bossDefeated(level, stage());
            com.space_beyond.dim.dungeon.seal.DungeonProtection.markDefeated(level, blockPosition());
            for (ServerPlayer p : bossEvent.getPlayers()) p.sendSystemMessage(Component.translatable("entity.space_m." + id() + ".defeated"));
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
        tag.putBoolean("Awake", awake());
        tag.putBoolean("PhaseTwo", phaseTwo());
        tag.putBoolean("PhaseThree", phaseThree());
        if (home != null) tag.putLong("Home", home.asLong());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AWAKE, tag.getBoolean("Awake"));
        entityData.set(PHASE2, tag.getBoolean("PhaseTwo"));
        entityData.set(PHASE3, tag.getBoolean("PhaseThree"));
        if (tag.contains("Home")) home = net.minecraft.core.BlockPos.of(tag.getLong("Home"));
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

    //? if < 1.21.1 {
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    //?}

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.1F;
    }
}
