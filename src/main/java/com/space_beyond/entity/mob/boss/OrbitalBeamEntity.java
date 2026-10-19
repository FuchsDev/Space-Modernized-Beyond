package com.space_beyond.entity.mob.boss;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.space_m.particle.SpaceParticles;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Ein Orbitalschlag als eigene, rein serverseitig gesteuerte Entity (wird nicht gespeichert). Waehrend der Vorwarnzeit
 * ({@link #warmup()}) zeichnet der Client ({@code OrbitalBeamRenderer}) einen rotierenden Runenkreis und einen sich
 * verdichtenden Zielstrahl; beim Einschlag einen Saeulenstrahl aus drei Schichten, verzweigte Blitze in Lila/Gold
 * und Schockwellenringe, dazu eigene Partikel und Kamerawackeln. Der Server wendet Schaden, Rueckstoss, Explosionen
 * und (Singularitaet) den Sog an.
 */
public class OrbitalBeamEntity extends Entity {

    /** Arten (bestimmen Groesse, Farben und Wirkung). */
    public static final int LANCE = 0, STORM = 1, VORTEX = 2, PRECISION = 3, BOSS = 4, LINE = 5, CATACLYSM = 6;

    /** Kraterradius der Cataclysm (Bloecke, Kugel in jede Richtung). */
    public static final int CRATER_RADIUS = 50;
    /** Spalten je Tick beim Ausheben des Kraters (verteilt die Last auf ~2-3 s). */
    private static final int COLUMNS_PER_TICK = 180;
    private static final int CRATER_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(OrbitalBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WARMUP = SynchedEntityData.defineId(OrbitalBeamEntity.class, EntityDataSerializers.INT);

    private float power, damage;
    private boolean breakBlocks;
    @Nullable private UUID owner;

    public OrbitalBeamEntity(EntityType<? extends OrbitalBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static OrbitalBeamEntity create(Level level, Vec3 pos, int kind, int warmup, float power, float damage, boolean breakBlocks, @Nullable Entity owner) {
        OrbitalBeamEntity e = new OrbitalBeamEntity(BossMobs.ORBITAL_BEAM.get(), level);
        e.moveTo(pos.x, pos.y, pos.z, 0, 0);
        e.entityData.set(KIND, kind);
        e.entityData.set(WARMUP, warmup);
        e.power = power;
        e.damage = damage;
        e.breakBlocks = breakBlocks;
        e.owner = owner != null ? owner.getUUID() : null;
        return e;
    }

    //? if < 1.21.1 {
    @Override
    protected void defineSynchedData() {
        this.entityData.define(KIND, 0);
        this.entityData.define(WARMUP, 40);
    }
    //?} else {
    /*@Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(KIND, 0);
        builder.define(WARMUP, 40);
    }
    *///?}

    public int kind() {
        return entityData.get(KIND);
    }

    public int warmup() {
        return entityData.get(WARMUP);
    }

    /** Strahlradius beim Einschlag (Bloecke). */
    public float radius() {
        return switch (kind()) {
            case CATACLYSM -> 12.0F;
            case LANCE -> 4.5F;
            case VORTEX -> 3.0F;
            case BOSS -> 2.2F;
            case PRECISION -> 0.9F;
            case LINE -> 1.4F;
            default -> 1.6F;
        };
    }

    /** Nachleuchten nach dem Einschlag (Ticks). */
    public int linger() {
        return switch (kind()) {
            case CATACLYSM -> 140;
            case LANCE -> 60;
            case VORTEX -> 34;
            case BOSS -> 26;
            case PRECISION -> 16;
            default -> 20;
        };
    }

    /** Gold-Anteil der Farben (0 = lila, 1 = gold). */
    public float goldness() {
        return switch (kind()) {
            case LINE -> 1.0F;
            case CATACLYSM -> 0.6F;
            case STORM -> 0.35F;
            case LANCE -> 0.5F;
            case PRECISION -> 0.15F;
            default -> 0.0F;
        };
    }

    @Override
    public void tick() {
        super.tick();
        int warm = warmup();
        if (level().isClientSide) {
            clientTick(warm);
            return;
        }
        ServerLevel level = (ServerLevel) level();
        if (columns != null) carve(level);
        if (tickCount < warm) {
            if (kind() == VORTEX) pull(level, 16.0D, 0.11D);
            if (kind() == CATACLYSM) cataclysmWarmup(level);
            if (tickCount == 1) {
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, kind() == LANCE ? 6.0F : 2.5F,
                        kind() == LANCE ? 0.5F : 1.2F);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 4.0F, 0.6F + 0.2F * random.nextFloat());
            }
        } else if (tickCount == warm) {
            impact(level);
        } else if (tickCount > warm + linger() && (columns == null || column >= columns.length / 2)) {
            discard();
        } else if (kind() == LANCE && tickCount - warm < 20 && (tickCount - warm) % 4 == 0) {
            // Nachbeben: Restenergie zuckt durch den Strahl
            hurtArea(level, 6.0D, 3.0F, false);
        }
    }

    private void impact(ServerLevel level) {
        Entity src = owner != null ? level.getEntity(owner) : null;
        Level.ExplosionInteraction blocks = breakBlocks ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE;
        int kind = kind();
        float vol = kind == LANCE ? 12.0F : kind == VORTEX ? 8.0F : 4.0F;
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, vol, kind == LANCE ? 0.45F : 0.8F + random.nextFloat() * 0.3F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, vol * 0.5F, kind == LANCE ? 0.5F : 1.1F);
        if (kind == LANCE || kind == VORTEX) level.playSound(null, getX(), getY(), getZ(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, vol, 0.7F);

        switch (kind) {
            case CATACLYSM -> {
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 24.0F, 0.4F);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.PLAYERS, 12.0F, 0.5F);
                hurtArea(level, CRATER_RADIUS, damage, true);
                if (breakBlocks) startCrater();
            }
            case LANCE -> {
                if (power > 0) {
                    level.explode(src, getX(), getY(), getZ(), power, true, blocks);
                    for (int i = 0; i < 8; i++) {
                        double a = i * Math.PI / 4;
                        level.explode(src, getX() + Math.cos(a) * 7, getY(), getZ() + Math.sin(a) * 7, power * 0.45F, false, blocks);
                    }
                }
                hurtArea(level, 12.0D, damage, true);
            }
            case VORTEX -> {
                pull(level, 16.0D, -1.6D); // Implosion schleudert nach aussen
                if (power > 0) level.explode(src, getX(), getY(), getZ(), power, false, blocks);
                hurtArea(level, 9.0D, damage, true);
            }
            case PRECISION -> hurtArea(level, 2.6D, damage, false);
            default -> {
                if (power > 0) level.explode(src, getX(), getY(), getZ(), power, false, blocks);
                hurtArea(level, 3.6D, damage, true);
            }
        }
    }

    // ── Cataclysm: Vorwarnung und Krater ───────────────────────────────────

    /** Spaltenversatz (dx, dz) nach Abstand sortiert; null, solange kein Krater ausgehoben wird. */
    @Nullable private int[] columns;
    private int column;

    /** Warnung an alle Spieler im Umkreis, Grollen, Sog der Luft zur Mitte. */
    private void cataclysmWarmup(ServerLevel level) {
        if (tickCount == 1) {
            for (ServerPlayer p : level.players()) {
                if (p.distanceToSqr(this) < 300.0D * 300.0D) {
                    p.displayClientMessage(Component.translatable("item.space_m.orbital_strike_rod.cataclysm_warning"), true);
                }
            }
        }
        if (tickCount % 20 == 0) {
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 10.0F, 0.5F);
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 10.0F, 0.5F + tickCount / 200.0F);
        }
        if (tickCount > warmup() - 30) pull(level, 30.0D, 0.04D);
    }

    private void startCrater() {
        int r = CRATER_RADIUS;
        java.util.List<int[]> list = new java.util.ArrayList<>();
        for (int dx = -r - 3; dx <= r + 3; dx++) for (int dz = -r - 3; dz <= r + 3; dz++) {
            if (dx * dx + dz * dz <= (r + 3) * (r + 3)) list.add(new int[] {dx, dz});
        }
        list.sort(java.util.Comparator.comparingInt(c -> c[0] * c[0] + c[1] * c[1]));
        columns = new int[list.size() * 2];
        for (int i = 0; i < list.size(); i++) {
            columns[i * 2] = list.get(i)[0];
            columns[i * 2 + 1] = list.get(i)[1];
        }
        column = 0;
    }

    /** Hebt je Tick {@link #COLUMNS_PER_TICK} Spalten der Kugel aus (von innen nach aussen), Rand zerklueftet,
     *  Kraterboden teils zu Obsidian, Schwarzstein und Magma verbrannt. Unzerstoerbare Bloecke bleiben. */
    private void carve(ServerLevel level) {
        int[] cols = columns;
        if (cols == null) return;
        int cx = Mth.floor(getX()), cy = Mth.floor(getY()), cz = Mth.floor(getZ());
        int minY = level.getMinBuildHeight() + 1, maxY = level.getMaxBuildHeight() - 1;
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        BlockState air = Blocks.AIR.defaultBlockState();
        long seed = getUUID().getLeastSignificantBits();
        int end = Math.min(cols.length / 2, column + COLUMNS_PER_TICK);
        for (; column < end; column++) {
            int dx = cols[column * 2], dz = cols[column * 2 + 1];
            double edge = CRATER_RADIUS * (0.93D + 0.07D * hashUnit(seed, dx >> 2, dz >> 2)) + 2.0D * hashUnit(seed ^ 0x5EEDL, dx, dz);
            double h2 = edge * edge - dx * dx - dz * dz;
            if (h2 < 0) continue;
            int h = (int) Math.sqrt(h2);
            int top = Math.min(maxY, cy + h), bottom = Math.max(minY, cy - h);
            p.set(cx + dx, cy, cz + dz);
            if (!level.isLoaded(p)) continue;
            for (int y = top; y >= bottom; y--) {
                p.setY(y);
                BlockState st = level.getBlockState(p);
                if (st.isAir() || st.getDestroySpeed(level, p) < 0) continue;
                level.setBlock(p, air, CRATER_FLAGS);
            }
            p.setY(bottom - 1);
            BlockState floor = level.getBlockState(p);
            if (!floor.isAir() && floor.getDestroySpeed(level, p) >= 0 && floor.getFluidState().isEmpty()) {
                double r = hashUnit(seed ^ 0xC4A7L, dx, dz);
                if (r < 0.18) level.setBlock(p, Blocks.OBSIDIAN.defaultBlockState(), CRATER_FLAGS);
                else if (r < 0.42) level.setBlock(p, Blocks.BLACKSTONE.defaultBlockState(), CRATER_FLAGS);
                else if (r < 0.48) level.setBlock(p, Blocks.MAGMA_BLOCK.defaultBlockState(), CRATER_FLAGS);
                else if (r < 0.5) level.setBlock(p, Blocks.CRYING_OBSIDIAN.defaultBlockState(), CRATER_FLAGS);
            }
        }
        if (tickCount % 3 == 0 && column < cols.length / 2) {
            int i = Math.min(column, cols.length / 2 - 1);
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx + cols[i * 2], cy, cz + cols[i * 2 + 1], 1, 0, 0, 0, 0);
            level.playSound(null, cx + cols[i * 2], cy, cz + cols[i * 2 + 1], SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 6.0F, 0.6F);
        }
    }

    private static double hashUnit(long seed, int x, int z) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L) ^ (z * 0xC2B2AE3D27D4EB4FL);
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (h >>> 11) * 0x1.0p-53;
    }

    /** Schaden (magisch, ignoriert Ruestung) mit Abfall zum Rand, optional Rueckstoss nach oben aussen. */
    private void hurtArea(ServerLevel level, double r, float dmg, boolean knock) {
        if (dmg <= 0) return;
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(r, r + 4, r));
        Entity src = owner != null ? level.getEntity(owner) : null;
        for (LivingEntity e : list) {
            if (owner != null && e.getUUID().equals(owner)) continue;
            // Bossschlaege treffen seine eigenen Wachen nicht
            if (src instanceof EntityUnstableSovereign && e instanceof net.minecraft.world.entity.monster.Enemy) continue;
            double d = e.position().distanceTo(position());
            if (d > r) continue;
            float f = (float) (1.0D - d / r * 0.6D);
            e.hurt(src != null ? level.damageSources().indirectMagic(this, src) : level.damageSources().magic(), dmg * f);
            if (knock) {
                Vec3 push = e.position().subtract(position()).multiply(1, 0, 1);
                push = push.lengthSqr() < 1.0E-4D ? new Vec3(0, 0, 0) : push.normalize();
                e.setDeltaMovement(e.getDeltaMovement().add(push.x * 1.4D * f, 0.7D + 0.6D * f, push.z * 1.4D * f));
                e.hurtMarked = true;
            }
        }
    }

    /** Sog zur Mitte ({@code strength} &gt; 0) oder Stoss nach aussen (&lt; 0). */
    private void pull(ServerLevel level, double r, double strength) {
        for (Entity e : level.getEntities(this, new AABB(position(), position()).inflate(r))) {
            if (e instanceof OrbitalBeamEntity || (owner != null && e.getUUID().equals(owner))) continue;
            Vec3 d = position().add(0, 1.5, 0).subtract(e.position());
            double len = d.length();
            if (len > r || len < 0.5D) continue;
            Vec3 v = d.normalize().scale(strength * (strength > 0 ? 1.0D : (1.0D - len / r)));
            e.setDeltaMovement(e.getDeltaMovement().add(v));
            e.hurtMarked = true;
            e.fallDistance = 0;
        }
    }

    // ── Client: Partikel und Kamerawackeln ──────────────────────────────────

    private void clientTick(int warm) {
        int kind = kind();
        boolean mega = kind == CATACLYSM;
        float r = radius();
        if (tickCount < warm) {
            float p = (float) tickCount / warm;
            // Funken laufen auf dem Kreis zusammen und steigen in den Strahl
            int n = mega ? 24 : kind == LANCE ? 8 : kind == VORTEX ? 3 : 2;
            for (int i = 0; i < n; i++) {
                double a = random.nextDouble() * Math.PI * 2;
                double rr = r * (3.2D - p * 2.0D);
                level().addParticle(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX() + Math.cos(a) * rr, getY() + 0.2, getZ() + Math.sin(a) * rr,
                        -Math.cos(a) * 0.12, 0.05 + p * 0.25, -Math.sin(a) * 0.12);
            }
            if (kind == LANCE || mega) {
                for (int i = 0; i < 4; i++) {
                    double h = 20 + random.nextDouble() * 60;
                    level().addParticle(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX() + random.nextGaussian() * 0.6, getY() + h, getZ() + random.nextGaussian() * 0.6, 0, -1.2, 0);
                }
            }
            if (kind == VORTEX) {
                for (int i = 0; i < 10; i++) {
                    double a = random.nextDouble() * Math.PI * 2, rr = 6 + random.nextDouble() * 10;
                    double ox = Math.cos(a) * rr, oz = Math.sin(a) * rr, oy = random.nextDouble() * 6;
                    // tangential + nach innen: Wirbel
                    level().addParticle(com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), getX() + ox, getY() + oy, getZ() + oz,
                            (-ox * 0.02 - oz * 0.03), (1.5 - oy) * 0.02, (-oz * 0.02 + ox * 0.03));
                }
            }
        } else if (tickCount == warm) {
            int sparks = mega ? 600 : kind == LANCE ? 260 : kind == VORTEX ? 160 : 50;
            for (int i = 0; i < sparks; i++) {
                double a = random.nextDouble() * Math.PI * 2, sp = 0.4 + random.nextDouble() * (mega ? 3.5 : kind == LANCE ? 1.6 : 0.7);
                level().addParticle(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX(), getY() + 0.5 + random.nextDouble() * 2, getZ(),
                        Math.cos(a) * sp, random.nextDouble() * sp * 0.8, Math.sin(a) * sp);
            }
            int embers = mega ? 400 : kind == LANCE ? 140 : kind == LINE ? 50 : 25;
            for (int i = 0; i < embers; i++) {
                level().addParticle(com.space_beyond.particle.BeyondParticles.GOLD_EMBER.get(), getX() + random.nextGaussian() * r, getY() + random.nextDouble() * 4, getZ() + random.nextGaussian() * r,
                        random.nextGaussian() * 0.25, 0.3 + random.nextDouble() * 0.6, random.nextGaussian() * 0.25);
            }
            float shake = mega ? 7.0F : kind == LANCE ? 3.2F : kind == VORTEX ? 2.2F : kind == PRECISION ? 0.6F : 1.0F;
            double range = mega ? 260 : kind == LANCE ? 64 : 28;
            EnvExecutor.runInEnv(Env.CLIENT, () -> () -> com.space_beyond.entity.mob.boss.client.ScreenShake.impact(position(), shake, range));
        } else if (tickCount - warm < linger() / 2) {
            for (int i = 0; i < (mega ? 16 : kind == LANCE ? 6 : 1); i++) {
                level().addParticle(com.space_beyond.particle.BeyondParticles.SOVEREIGN_SPARK.get(), getX() + random.nextGaussian() * r * 0.5, getY() + random.nextDouble() * 30,
                        getZ() + random.nextGaussian() * r * 0.5, 0, 0.4 + random.nextDouble() * 0.4, 0);
            }
        }
    }

    // ── Entity-Grundlagen ───────────────────────────────────────────────────

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256.0D * 256.0D;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    /** Fuer den Renderer: 0..1 Fortschritt der Vorwarnung bzw. Abklingen nach dem Einschlag. */
    public float fade(float partialTick) {
        float t = tickCount + partialTick - warmup();
        return Mth.clamp(1.0F - t / linger(), 0F, 1F);
    }
}
