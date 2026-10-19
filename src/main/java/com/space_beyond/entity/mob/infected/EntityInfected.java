package com.space_beyond.entity.mob.infected;

import com.space_beyond.dim.sporos.spores.SporeInfection;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Basis der Infizierten von Sporos (eigene Erweiterung, Sporos Runde 5): vom Sporenpilz uebernommene Wirte in vier
 * Stadien - {@link EntitySporeling} (frisch, schnell), {@link EntityLurker} (lauert, Hinterhalt),
 * {@link EntityBlindcap} (Pilzhut ueber dem Gesicht, blind, hoert) und {@link EntityMycobrute} (aufgedunsener Koloss).
 *
 * <p>Technisch Zombies (untot -> immun gegen die Infektion, aber Sporentraeger: Treffer exponieren das Opfer ueber
 * {@code SporeExposure.onHurt}, dazu je Stadium eigene Stoesse ueber {@link #sporeBurst}): ohne Sonnenbrand, ohne
 * Ertrunkenen-Umwandlung, ohne Ruestung/Kinder, ohne Verstaerkungen. Nur die Angriffs- und Zielgoals der Stadien,
 * keine Dorfbewohner-/Golem-Ziele. Tod: Sporenwolke ({@link #deathExposure}).
 */
public abstract class EntityInfected extends Zombie {

    protected EntityInfected(EntityType<? extends EntityInfected> type, Level level) {
        super(type, level);
        this.xpReward = 7;
    }

    /** Gemeinsame Attribute (ohne Verstaerkungen), die Stadien setzen Leben/Tempo/Schaden/Ruestung darauf. */
    protected static AttributeSupplier.Builder infectedAttributes(double health, double speed, double damage, double armor, double follow) {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, damage)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.FOLLOW_RANGE, follow)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        addTargetGoals();
    }

    /** Zielgoals des Stadiums (Spieler). */
    protected abstract void addTargetGoals();

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
        super.setBaby(false);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // keine Ruestung/Waffen
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    /** Angriff mit Stadiumsbonus ({@link #extraDamage}), danach {@link #onHit}. */
    @Override
    public boolean doHurtTarget(Entity target) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) + extraDamage(target);
        boolean hit = target.hurt(damageSources().mobAttack(this), damage);
        if (hit) {
            //? if < 1.21.1 {
            doEnchantDamageEffects(this, target);
            //?}
            setLastHurtMob(target);
            if (target instanceof LivingEntity living) onHit(living);
        }
        return hit;
    }

    protected float extraDamage(Entity target) {
        return 0F;
    }

    /** Testzugriff auf {@link #extraDamage}. */
    public final float extraDamageForTest(Entity target) {
        return extraDamage(target);
    }

    /** Testzugriff auf {@link #isSunSensitive}. */
    public final boolean isSunSensitiveForTest() {
        return isSunSensitive();
    }

    protected void onHit(LivingEntity target) {}

    /** Exposition beim Tod (Sporenwolke, Radius 3). */
    protected float deathExposure() {
        return 0.05F;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide) sporeBurst(deathExposure(), 3.0D, 30);
    }

    /** Sporenstoss: Wolke und Exposition fuer alle ungeschuetzten Lebewesen im Radius (nicht fuer Infizierte). */
    public void sporeBurst(float exposure, double radius, int particles) {
        if (!(level() instanceof ServerLevel server)) return;
        server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY(0.5D), getZ(), particles, radius * 0.4D, radius * 0.3D, radius * 0.4D, 0.02D);
        for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(radius))) {
            if (e == this || e instanceof EntityInfected) continue;
            SporeInfection.expose(e, exposure);
        }
    }

    /** Schaut {@code viewer} in Richtung {@code target} (Blickwinkel unter ~60 Grad)? */
    public static boolean isFacing(LivingEntity viewer, Entity target) {
        Vec3 look = viewer.getViewVector(1.0F).normalize();
        Vec3 to = new Vec3(target.getX() - viewer.getX(), target.getEyeY() - viewer.getEyeY(), target.getZ() - viewer.getZ());
        double len = to.length();
        if (len < 1.0E-4D) return true;
        return look.dot(to.scale(1.0D / len)) > 0.5D;
    }
}
