package com.space_beyond.progression.fx;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Sichtbarer Effekt der Planetenwaffen (nur Darstellung, wird nicht gespeichert): ein leuchtender Schwungbogen vor dem
 * Angreifer ({@link #SLASH}, schwingt in 8 Ticks durch, Neigung {@code roll}) oder ein sich ausbreitender Bodenring
 * ({@link #RING}, Faehigkeiten). Farbe je Waffe. Renderer: {@code SlashRenderer}.
 */
public class SlashEffect extends Entity {

    public static final int SLASH = 0, RING = 1;

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SlashEffect.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(SlashEffect.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(SlashEffect.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(SlashEffect.class, EntityDataSerializers.FLOAT);

    public SlashEffect(EntityType<? extends SlashEffect> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Schwungbogen vor {@code attacker}, Groesse in Bloecken (1 = Schwert). */
    public static void slash(ServerLevel level, LivingEntity attacker, int color, float size, float roll) {
        SlashEffect e = new SlashEffect(WeaponFx.SLASH.get(), level);
        Vec3 eye = attacker.getEyePosition();
        e.moveTo(eye.x, eye.y - 0.3, eye.z, attacker.getYRot(), attacker.getXRot());
        e.entityData.set(COLOR, color);
        e.entityData.set(MODE, SLASH);
        e.entityData.set(SIZE, size);
        e.entityData.set(ROLL, roll);
        level.addFreshEntity(e);
    }

    /** Bodenring um {@code at} bis zum Radius {@code radius}. */
    public static void ring(ServerLevel level, Vec3 at, int color, float radius) {
        SlashEffect e = new SlashEffect(WeaponFx.SLASH.get(), level);
        e.moveTo(at.x, at.y + 0.1, at.z, 0, 0);
        e.entityData.set(COLOR, color);
        e.entityData.set(MODE, RING);
        e.entityData.set(SIZE, radius);
        level.addFreshEntity(e);
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0xFFFFFF);
        this.entityData.define(MODE, SLASH);
        this.entityData.define(SIZE, 1.0F);
        this.entityData.define(ROLL, 0.0F);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, 0xFFFFFF);
        builder.define(MODE, SLASH);
        builder.define(SIZE, 1.0F);
        builder.define(ROLL, 0.0F);
    }
    *///?}

    public int color() {
        return entityData.get(COLOR);
    }

    public int mode() {
        return entityData.get(MODE);
    }

    public float size() {
        return entityData.get(SIZE);
    }

    public float roll() {
        return entityData.get(ROLL);
    }

    /** Lebensdauer in Ticks. */
    public int life() {
        return mode() == RING ? 14 : 8;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > life()) discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
