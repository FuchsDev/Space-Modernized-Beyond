package com.space_beyond.entity.mob.infected;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Lauerer ({@code lurker}): zweites Stadium - 24 Leben, 4 Schaden, Tempo 0,3. Greift bevorzugt Spieler an, die ihn
 * nicht ansehen (Zielwahl: unbeobachtet oder naeher als 3 Bloecke), sprintet solange das Ziel wegschaut und schlaegt
 * von hinten mit +4 Schaden ({@link #extraDamage}). Leise (Stimme tief, seltene Laute).
 */
public class EntityLurker extends EntityInfected {

    public EntityLurker(EntityType<? extends EntityLurker> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return infectedAttributes(24.0D, 0.3D, 4.0D, 2.0D, 20.0D);
    }

    @Override
    protected void addTargetGoals() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canAmbush));
    }

    /** Ziel, das nicht herschaut oder schon in Reichweite ist. */
    public boolean canAmbush(LivingEntity target) {
        return !isFacing(target, this) || distanceToSqr(target) < 9.0D;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            LivingEntity target = getTarget();
            setSprinting(target != null && !isFacing(target, this));
        }
    }

    @Override
    protected float extraDamage(Entity target) {
        return target instanceof LivingEntity living && !isFacing(living, this) ? 4.0F : 0F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    public float getVoicePitch() {
        return 0.8F + random.nextFloat() * 0.1F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
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
