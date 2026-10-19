package com.space_beyond.entity.mob.infected;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Blindhut ({@code blindcap}): drittes Stadium, der Pilz hat das Gesicht ueberwachsen - blind, orientiert sich am
 * Geraeusch (Klicklaute): Zielwahl ohne Sichtlinie nach {@link #hears}: sprintende Spieler bis 24 Bloecke, gehende
 * bis 12, schleichende nur bis 3 (und alles naeher als 2). 30 Leben, Ruestung 6, 6 Schaden plus Biss-Exposition
 * ({@link #onHit}), Tempo 0,26, schwer wegzustossen.
 */
public class EntityBlindcap extends EntityInfected {

    public static final double HEAR_SPRINT = 24.0D, HEAR_WALK = 12.0D, HEAR_SNEAK = 3.0D, HEAR_ALWAYS = 2.0D;

    public EntityBlindcap(EntityType<? extends EntityBlindcap> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return infectedAttributes(30.0D, 0.26D, 6.0D, 6.0D, 26.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void addTargetGoals() {
        // mustSee = false: Echo-Ortung statt Sichtlinie
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, this::hears));
    }

    /** Hoert den Blindhut das Lebewesen (Abstand nach Bewegung)? */
    public boolean hears(LivingEntity e) {
        double d = distanceTo(e);
        if (d <= HEAR_ALWAYS) return true;
        if (e.isSprinting()) return d <= HEAR_SPRINT;
        if (e.isCrouching()) return d <= HEAR_SNEAK;
        return d <= HEAR_WALK;
    }

    @Override
    protected void onHit(LivingEntity target) {
        com.space_beyond.dim.sporos.spores.SporeInfection.expose(target, 0.05F);
    }

    @Override
    protected float deathExposure() {
        return 0.08F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 60;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.2F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
}
