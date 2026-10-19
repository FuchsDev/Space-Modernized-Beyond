package com.space_beyond.entity.mob.infected;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Pilzkoloss ({@code mycobrute}): Endstadium, vom Pilz aufgedunsen und gepanzert - 120 Leben, Ruestung 10 (Haerte 4),
 * 10 Schaden, langsam (0,22), nicht wegzustossen, grosse Hitbox (0,9 x 2,6, Renderer 1,35-fach). Jeder Treffer
 * platzt Sporensaecke ({@link #onHit}: Exposition 0,12 im Radius 2,5); alle 5 s mit Ziel in 8 Bloecken ein Sporenregen
 * (0,04 im Radius 4); beim Tod eine grosse Wolke (0,15, Radius 5).
 */
public class EntityMycobrute extends EntityInfected {

    private int cloudTimer;

    public EntityMycobrute(EntityType<? extends EntityMycobrute> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return infectedAttributes(120.0D, 0.22D, 10.0D, 10.0D, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void addTargetGoals() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void onHit(LivingEntity target) {
        sporeBurst(0.12F, 2.5D, 20);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        LivingEntity target = getTarget();
        if (target != null && distanceToSqr(target) < 64.0D) {
            if (++cloudTimer >= 100) {
                cloudTimer = 0;
                sporeBurst(0.04F, 4.0D, 40);
                playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0F, 0.5F);
            }
        } else {
            cloudTimer = 0;
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide) sporeBurst(0.15F, 5.0D, 80);
    }

    @Override
    protected float deathExposure() {
        return 0F; // eigene grosse Wolke in die()
    }

    @Override
    public float getVoicePitch() {
        return 0.5F + random.nextFloat() * 0.1F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.RAVAGER_STEP;
    }
}
