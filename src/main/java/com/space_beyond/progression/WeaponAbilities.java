package com.space_beyond.progression;

import java.util.List;

import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_m.particle.SpaceParticles;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Treffer-Effekte und Rechtsklick-Faehigkeiten der Planetenwaffen ({@link ItemPlanetWeapon}). Alles serverseitig,
 * Partikel ueber {@code sendParticles}.
 */
public final class WeaponAbilities {

    private WeaponAbilities() {}

    // ── Hilfen ──────────────────────────────────────────────────────────────

    static List<LivingEntity> around(ServerLevel level, Entity center, double r) {
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(center.blockPosition()).inflate(r),
                e -> e != center && e.isAlive() && e.distanceTo(center) <= r && !(e instanceof Player p && (p.isCreative() || p == center)));
    }

    static void ring(ServerLevel level, Vec3 c, double r, ParticleOptions p, int n) {
        for (int i = 0; i < n; i++) {
            double a = i * Math.PI * 2 / n;
            level.sendParticles(p, c.x + Math.cos(a) * r, c.y + 0.2, c.z + Math.sin(a) * r, 1, 0, 0.05, 0, 0.01);
        }
    }

    static DustParticleOptions dust(int rgb, float size) {
        return new DustParticleOptions(new Vector3f(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F), size);
    }

    static void sound(ServerLevel level, Entity at, SoundEvent s, float vol, float pitch) {
        level.playSound(null, at.getX(), at.getY(), at.getZ(), s, SoundSource.PLAYERS, vol, pitch);
    }

    static void push(LivingEntity e, Vec3 from, double strength, double up) {
        Vec3 d = e.position().subtract(from).multiply(1, 0, 1);
        d = d.lengthSqr() < 1.0E-4 ? Vec3.ZERO : d.normalize();
        e.setDeltaMovement(e.getDeltaMovement().add(d.x * strength, up, d.z * strength));
        e.hurtMarked = true;
    }

    // ── Treffer ─────────────────────────────────────────────────────────────

    public static void shock(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, t.getX(), t.getY() + 1, t.getZ(), 20, 0.3, 0.5, 0.3, 0.3);
    }

    public static void freeze(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.setTicksFrozen(Math.min(t.getTicksRequiredToFreeze() + 80, t.getTicksFrozen() + 120));
        t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
        level.sendParticles(ParticleTypes.SNOWFLAKE, t.getX(), t.getY() + 1, t.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
    }

    public static void rust(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
        t.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
        level.sendParticles(dust(0xB4502A, 1.4F), t.getX(), t.getY() + 1, t.getZ(), 16, 0.3, 0.5, 0.3, 0);
    }

    public static void gravity(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 1));
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, t.getX(), t.getY() + 1, t.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
    }

    public static void radiate(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
        t.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        level.sendParticles(dust(0x7CFF40, 1.2F), t.getX(), t.getY() + 1, t.getZ(), 20, 0.4, 0.6, 0.4, 0);
    }

    public static void burn(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.setRemainingFireTicks(Math.max(t.getRemainingFireTicks(), 120));
        level.sendParticles(ParticleTypes.FLAME, t.getX(), t.getY() + 1, t.getZ(), 16, 0.3, 0.5, 0.3, 0.05);
    }

    public static void venom(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        t.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 2));
        level.sendParticles(dust(0x40E0D0, 1.2F), t.getX(), t.getY() + 1, t.getZ(), 16, 0.3, 0.5, 0.3, 0);
    }

    public static void harpoon(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        Vec3 d = a.position().subtract(t.position()).normalize().scale(0.8);
        t.setDeltaMovement(t.getDeltaMovement().add(d.x, 0.2, d.z));
        t.hurtMarked = true;
        level.sendParticles(ParticleTypes.BUBBLE_POP, t.getX(), t.getY() + 1, t.getZ(), 16, 0.3, 0.5, 0.3, 0.05);
    }

    public static void crush(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        push(t, a.position(), 1.4, 0.5);
        t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3));
        level.sendParticles(ParticleTypes.CLOUD, t.getX(), t.getY() + 0.5, t.getZ(), 10, 0.4, 0.2, 0.4, 0.05);
    }

    public static void leech(ServerLevel level, ItemStack s, LivingEntity t, LivingEntity a) {
        a.heal(2.0F);
        t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
        level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, t.getX(), t.getY() + 1, t.getZ(), 20, 0.4, 0.6, 0.4, 0);
    }

    // ── Faehigkeiten ────────────────────────────────────────────────────────

    /** Mun: EMP-Welle - Gegner im Umkreis 5 zurueckgestossen und gelaehmt. */
    public static boolean emp(ServerLevel level, Player p, ItemStack s) {
        for (LivingEntity e : around(level, p, 5)) {
            push(e, p.position(), 1.2, 0.4);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
            e.hurt(level.damageSources().indirectMagic(p, p), 4.0F);
        }
        ring(level, p.position(), 5, ParticleTypes.ELECTRIC_SPARK, 48);
        sound(level, p, SoundEvents.BEACON_DEACTIVATE, 1.5F, 1.6F);
        return true;
    }

    /** Minmus: Frostnova - alles im Umkreis 6 friert ein. */
    public static boolean frostNova(ServerLevel level, Player p, ItemStack s) {
        for (LivingEntity e : around(level, p, 6)) {
            e.setTicksFrozen(e.getTicksRequiredToFreeze() + 160);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
            e.hurt(level.damageSources().freeze(), 5.0F);
        }
        ring(level, p.position(), 3, ParticleTypes.SNOWFLAKE, 30);
        ring(level, p.position(), 6, ParticleTypes.SNOWFLAKE, 48);
        sound(level, p, SoundEvents.GLASS_BREAK, 1.5F, 0.6F);
        return true;
    }

    /** Duna: Sandsprint - Sprung nach vorn, Gegner auf dem Weg werden getroffen. */
    public static boolean sandDash(ServerLevel level, Player p, ItemStack s) {
        Vec3 look = p.getLookAngle().multiply(1, 0, 1).normalize();
        p.setDeltaMovement(look.x * 2.2, 0.35, look.z * 2.2);
        p.hurtMarked = true;
        Vec3 from = p.position();
        for (int i = 1; i <= 8; i++) {
            Vec3 at = from.add(look.scale(i));
            level.sendParticles(dust(0xC2603A, 2.0F), at.x, at.y + 0.5, at.z, 6, 0.3, 0.3, 0.3, 0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(1.2), e -> e != p && e.isAlive())) {
                e.hurt(level.damageSources().playerAttack(p), 8.0F);
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
            }
        }
        sound(level, p, SoundEvents.SAND_BREAK, 1.5F, 0.6F);
        return true;
    }

    /** Ike: Schwerkraftsog - Gegner im Umkreis 8 zum Spieler gezogen, dann Schlag. */
    public static boolean gravityWell(ServerLevel level, Player p, ItemStack s) {
        for (LivingEntity e : around(level, p, 8)) {
            Vec3 d = p.position().subtract(e.position()).normalize().scale(1.1);
            e.setDeltaMovement(d.x, 0.5, d.z);
            e.hurtMarked = true;
            e.hurt(level.damageSources().indirectMagic(p, p), 6.0F);
        }
        ring(level, p.position(), 8, ParticleTypes.REVERSE_PORTAL, 60);
        sound(level, p, SoundEvents.ENDERMAN_TELEPORT, 1.5F, 0.5F);
        return true;
    }

    /** Dres: Kernschmelze-Puls - Strahlungsschaden im Umkreis 6, Gluehen. */
    public static boolean meltdownPulse(ServerLevel level, Player p, ItemStack s) {
        for (LivingEntity e : around(level, p, 6)) {
            e.hurt(level.damageSources().indirectMagic(p, p), 9.0F);
            e.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1));
            e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
        }
        level.sendParticles(dust(0x7CFF40, 2.5F), p.getX(), p.getY() + 1, p.getZ(), 120, 3, 1, 3, 0);
        level.sendParticles(ParticleTypes.FLASH, p.getX(), p.getY() + 1, p.getZ(), 1, 0, 0, 0, 0);
        sound(level, p, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.2F, 1.4F);
        return true;
    }

    /** Moho: Flammenfaecher - drei Feuerkugeln in Blickrichtung. */
    public static boolean flareFan(ServerLevel level, Player p, ItemStack s) {
        Vec3 look = p.getLookAngle();
        for (int i = -1; i <= 1; i++) {
            Vec3 d = look.yRot(i * 0.18F);
            //? if < 1.21.1 {
            SmallFireball fb = new SmallFireball(level, p, d.x, d.y, d.z);
            //?} else {
            /*SmallFireball fb = new SmallFireball(level, p, d);
            *///?}
            fb.setPos(p.getX() + d.x, p.getEyeY() - 0.2, p.getZ() + d.z);
            level.addFreshEntity(fb);
        }
        sound(level, p, SoundEvents.BLAZE_SHOOT, 1.2F, 0.8F);
        return true;
    }

    /** Eve: Schrabidium-Strahl - Linie von 16 Bloecken, trifft alles darauf. */
    public static boolean schrabBeam(ServerLevel level, Player p, ItemStack s) {
        Vec3 eye = p.getEyePosition(), look = p.getLookAngle();
        for (double d = 1; d <= 16; d += 0.5) {
            Vec3 at = eye.add(look.scale(d));
            if (!level.getBlockState(net.minecraft.core.BlockPos.containing(at)).getCollisionShape(level, net.minecraft.core.BlockPos.containing(at)).isEmpty()) break;
            level.sendParticles(dust(0x30E8D8, 1.4F), at.x, at.y, at.z, 2, 0.05, 0.05, 0.05, 0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(0.6), e -> e != p && e.isAlive())) {
                e.hurt(level.damageSources().indirectMagic(p, p), 10.0F);
                e.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 1));
            }
        }
        sound(level, p, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5F, 1.8F);
        return true;
    }

    /** Laythe: Strudelsprung - Satz nach vorn, Wasseratmung. */
    public static boolean tideDash(ServerLevel level, Player p, ItemStack s) {
        Vec3 look = p.getLookAngle();
        p.setDeltaMovement(look.scale(2.4));
        p.hurtMarked = true;
        p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
        p.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 200, 0));
        level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, p.getX(), p.getY() + 1, p.getZ(), 40, 0.5, 0.5, 0.5, 0.2);
        sound(level, p, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 1.5F, 1.0F);
        return true;
    }

    /** Tekto: Chlorwolke am angeblickten Punkt (Gift, 8 s). */
    public static boolean chlorineCloud(ServerLevel level, Player p, ItemStack s) {
        Vec3 at = p.getEyePosition().add(p.getLookAngle().scale(6));
        AreaEffectCloud cloud = new AreaEffectCloud(level, at.x, at.y - 1, at.z);
        cloud.setOwner(p);
        cloud.setRadius(3.5F);
        cloud.setDuration(160);
        cloud.setRadiusPerTick(-0.01F);
        cloud.setParticle(dust(0xB8F040, 1.5F));
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
        level.addFreshEntity(cloud);
        sound(level, p, SoundEvents.LAVA_EXTINGUISH, 1.2F, 0.6F);
        return true;
    }

    /** Sporos: Sporenstoss - Gegner geschwaecht und geblendet, Spieler regeneriert, Infektion geheilt. */
    public static boolean sporeBurst(ServerLevel level, Player p, ItemStack s) {
        for (LivingEntity e : around(level, p, 6)) {
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 1));
            e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
            e.hurt(level.damageSources().indirectMagic(p, p), 6.0F);
        }
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
        SporeInfection.apply(p, 0F);
        level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, p.getX(), p.getY() + 1, p.getZ(), 120, 3, 1, 3, 0);
        level.sendParticles(com.space_beyond.particle.BeyondParticles.VOID_MOTE.get(), p.getX(), p.getY() + 1, p.getZ(), 40, 3, 1, 3, 0.02);
        sound(level, p, SoundEvents.SPORE_BLOSSOM_BREAK, 1.5F, 0.6F);
        return true;
    }
}
