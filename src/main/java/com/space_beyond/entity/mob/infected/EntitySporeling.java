package com.space_beyond.entity.mob.infected;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Sporling ({@code sporeling}): frisch uebernommener Wirt - schnell (0,33), schwach (16 Leben, 3 Schaden), in Gruppen,
 * schreit mit hoher Stimme. Sieht normal (Ziel nur mit Sichtlinie).
 */
public class EntitySporeling extends EntityInfected {

    public EntitySporeling(EntityType<? extends EntitySporeling> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return infectedAttributes(16.0D, 0.33D, 3.0D, 0.0D, 24.0D);
    }

    @Override
    protected void addTargetGoals() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public float getVoicePitch() {
        return 1.35F + random.nextFloat() * 0.2F;
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
