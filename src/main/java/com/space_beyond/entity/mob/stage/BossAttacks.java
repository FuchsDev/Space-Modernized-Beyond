package com.space_beyond.entity.mob.stage;

import java.util.List;

import com.space_beyond.dim.dungeon.PlanetDungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Bausteine der Boss-Angriffe (Server): Vorwarnung, Schockwellen, Strahlen, Salven, Wolken, Verstaerkung. */
public final class BossAttacks {

    private BossAttacks() {}

    public static DustParticleOptions dust(int rgb, float size) {
        return new DustParticleOptions(new Vector3f(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F), size);
    }

    /** Ziele (keine Monster, keine Bosse) im Umkreis. */
    public static List<LivingEntity> foes(ServerLevel level, Vec3 c, double r) {
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(r, r * 0.6 + 2, r),
                e -> e.isAlive() && !(e instanceof Enemy) && !(e instanceof Player p && (p.isCreative() || p.isSpectator()))
                        && e.position().distanceTo(c) <= r);
    }

    /** Partikelring am Boden (Vorwarnung). */
    public static void ring(ServerLevel level, Vec3 c, double r, ParticleOptions p) {
        int n = Math.max(12, (int) (r * 6));
        for (int i = 0; i < n; i++) {
            double a = i * Math.PI * 2 / n;
            level.sendParticles(p, c.x + Math.cos(a) * r, c.y + 0.15, c.z + Math.sin(a) * r, 1, 0, 0, 0, 0);
        }
    }

    /** Boden unter einem Punkt (bis 12 Bloecke tiefer). */
    public static Vec3 ground(ServerLevel level, Vec3 p) {
        BlockPos.MutableBlockPos m = BlockPos.containing(p.x, p.y + 1, p.z).mutable();
        for (int i = 0; i < 14; i++) {
            if (!level.getBlockState(m.below()).getCollisionShape(level, m.below()).isEmpty() && level.getBlockState(m).getCollisionShape(level, m).isEmpty()) {
                return new Vec3(p.x, m.getY(), p.z);
            }
            m.move(0, -1, 0);
        }
        return p;
    }

    /** Staerke des Angreifers: Stufen-Bosse skalieren ihren Schaden ({@link EntityStageBoss#power()}). */
    public static float scale(Entity src) {
        return src instanceof EntityStageBoss b ? b.power() : 1.0F;
    }

    /** Schockwelle: Schaden mit Abfall, Rueckstoss nach aussen/oben, Partikelring. */
    public static void shockwave(ServerLevel level, Entity src, Vec3 c, double r, float dmg, double knock, ParticleOptions p) {
        dmg *= scale(src);
        for (LivingEntity e : foes(level, c, r)) {
            double d = e.position().distanceTo(c);
            float f = (float) (1.0 - d / r * 0.6);
            e.hurt(level.damageSources().mobAttack(src instanceof LivingEntity l ? l : null), dmg * f);
            Vec3 push = e.position().subtract(c).multiply(1, 0, 1);
            push = push.lengthSqr() < 1e-4 ? Vec3.ZERO : push.normalize();
            e.setDeltaMovement(push.x * knock * f, 0.4 + 0.4 * knock * f, push.z * knock * f);
            e.hurtMarked = true;
        }
        for (double rr = 1; rr <= r; rr += 1.5) ring(level, c, rr, p);
    }

    /** Sofortstrahl von {@code from} nach {@code to}: trifft alle auf der Linie (Breite 0,8), endet an Waenden. */
    public static Vec3 beam(ServerLevel level, LivingEntity src, Vec3 from, Vec3 to, float dmg, int color, MobEffectInstance effect) {
        dmg *= scale(src);
        Vec3 dir = to.subtract(from);
        double len = Math.min(32, dir.length());
        dir = dir.normalize();
        DustParticleOptions p = dust(color, 1.6F);
        Vec3 end = from;
        java.util.Set<LivingEntity> hit = new java.util.HashSet<>();
        for (double d = 0.5; d <= len; d += 0.4) {
            Vec3 at = from.add(dir.scale(d));
            BlockPos bp = BlockPos.containing(at);
            if (!level.getBlockState(bp).getCollisionShape(level, bp).isEmpty()) break;
            end = at;
            level.sendParticles(p, at.x, at.y, at.z, 1, 0.05, 0.05, 0.05, 0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(0.8),
                    e -> e != src && e.isAlive() && !(e instanceof EntityStageBoss))) {
                if (hit.add(e)) {
                    e.hurt(level.damageSources().indirectMagic(src, src), dmg);
                    if (effect != null) e.addEffect(new MobEffectInstance(effect));
                }
            }
        }
        return end;
    }

    /** Vorwarnlinie fuer einen Strahl (Partikel in duenner Linie). */
    public static void aimLine(ServerLevel level, Vec3 from, Vec3 to, int color) {
        Vec3 dir = to.subtract(from);
        double len = Math.min(32, dir.length());
        dir = dir.normalize();
        DustParticleOptions p = dust(color, 0.6F);
        for (double d = 0.5; d <= len; d += 1.0) {
            Vec3 at = from.add(dir.scale(d));
            level.sendParticles(p, at.x, at.y, at.z, 1, 0, 0, 0, 0);
        }
    }

    /** Faecher von {@code n} Geschossen Richtung Ziel. */
    public static void volley(ServerLevel level, LivingEntity src, LivingEntity target, Vec3 from, int n, double spread, double speed,
                              int color, float size, float dmg) {
        Vec3 aim = target.getEyePosition().subtract(from).normalize();
        for (int i = 0; i < n; i++) {
            double off = n == 1 ? 0 : (i - (n - 1) / 2.0) * spread;
            Vec3 d = aim.yRot((float) off).scale(speed);
            EntityBossOrb.shoot(level, src, from, d, color, size, dmg);
        }
    }

    /** Wolke mit Effekt am Punkt. */
    public static void cloud(ServerLevel level, LivingEntity owner, Vec3 at, float radius, int ticks, int color, MobEffectInstance effect) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, at.x, at.y, at.z);
        cloud.setOwner(owner);
        cloud.setRadius(radius);
        cloud.setDuration(ticks);
        cloud.setRadiusPerTick(-radius / ticks * 0.5F);
        cloud.setParticle(dust(color, 1.5F));
        cloud.addEffect(effect);
        level.addFreshEntity(cloud);
    }

    /** Verstaerkung: Mobs nach Registry-ID rund um den Boss; gibt die erzeugten Mobs zurueck. */
    public static List<Mob> summon(ServerLevel level, LivingEntity boss, String id, int n, double r) {
        List<Mob> out = new java.util.ArrayList<>();
        EntityType<?> type = PlanetDungeons.type(id);
        if (type == null) return out;
        for (int i = 0; i < n; i++) {
            Entity e = type.create(level);
            if (!(e instanceof Mob mob)) continue;
            double a = level.random.nextDouble() * Math.PI * 2;
            Vec3 at = ground(level, boss.position().add(Math.cos(a) * r, 2, Math.sin(a) * r));
            mob.moveTo(at.x, at.y, at.z, level.random.nextFloat() * 360, 0);
            //? if < 1.21.1 {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            //?} else {
            /*mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            *///?}
            if (boss instanceof Mob bm && bm.getTarget() != null) mob.setTarget(bm.getTarget());
            level.addFreshEntity(mob);
            out.add(mob);
            level.sendParticles(dust(0xFFFFFF, 1.5F), at.x, at.y + 1, at.z, 20, 0.4, 0.8, 0.4, 0);
        }
        return out;
    }

    /** Sprung zum Ziel (Bogen). */
    public static void leap(LivingEntity boss, Vec3 to, double height) {
        Vec3 d = to.subtract(boss.position());
        double flat = Math.sqrt(d.x * d.x + d.z * d.z);
        double t = Math.max(10, flat / 0.9);
        boss.setDeltaMovement(d.x / t * 1.6, height, d.z / t * 1.6);
        boss.hurtMarked = true;
    }

    public static void sound(ServerLevel level, Entity at, SoundEvent s, float vol, float pitch) {
        level.playSound(null, at.getX(), at.getY(), at.getZ(), s, SoundSource.HOSTILE, vol, pitch);
    }
}
