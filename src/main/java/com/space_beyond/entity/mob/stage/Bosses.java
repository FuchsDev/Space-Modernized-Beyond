package com.space_beyond.entity.mob.stage;

import static com.space_beyond.entity.mob.stage.BossAttacks.aimLine;
import static com.space_beyond.entity.mob.stage.BossAttacks.beam;
import static com.space_beyond.entity.mob.stage.BossAttacks.cloud;
import static com.space_beyond.entity.mob.stage.BossAttacks.dust;
import static com.space_beyond.entity.mob.stage.BossAttacks.foes;
import static com.space_beyond.entity.mob.stage.BossAttacks.ground;
import static com.space_beyond.entity.mob.stage.BossAttacks.ring;
import static com.space_beyond.entity.mob.stage.BossAttacks.shockwave;
import static com.space_beyond.entity.mob.stage.BossAttacks.sound;
import static com.space_beyond.entity.mob.stage.BossAttacks.summon;
import static com.space_beyond.entity.mob.stage.BossAttacks.volley;

import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Die zehn Stufen-Bosse (Planeten-Fortschritt), jeder staerker als der vorige ({@link EntityStageBoss#attributes}):
 * <ol>
 *   <li><b>Lunar Sentinel</b> (Mun) - Wachmech der verlassenen Forschungsstation: Visier-Laser, Raketensalve,
 *       Stampfer; Phase 2 ruft Cyber-Krabben.</li>
 *   <li><b>Cryo Behemoth</b> (Minmus) - Golem aus Minzeis: Eisstacheln, Frostatem, Eisbrocken, Bodenschlag.</li>
 *   <li><b>Rust Titan</b> (Duna) - verrosteter Spinnenpanzer: Kanone, Sandsturm, Sturmangriff, Beben; Glyphiden.</li>
 *   <li><b>Monolith Keeper</b> (Ike) - schwebender Obelisk: Augenstrahl, Schwerkraftheber, Steinregen, Sprung.</li>
 *   <li><b>Core Abomination</b> (Dres) - RBMK-Kern, der lebt: Strahlungspuls, Schlamm, Giftsee, Kernschmelze.</li>
 *   <li><b>Magma Wyrm</b> (Moho) - fliegende Lavaschlange: Feueratem, Magmasalve, Sturzflug, Eruptionen.</li>
 *   <li><b>Schrabidium Hydra</b> (Eve) - dreikoepfig: Dreifachstrahl, zielsuchende Splitter, Saeureteiche, Biss.</li>
 *   <li><b>Abyssal Leviathan</b> (Laythe) - Tiefsee-Anglerfisch: Lockblitz, Sog, Wasserstrahl, Sturzbiss.</li>
 *   <li><b>Polymer Colossus</b> (Tekto) - Chemie-Riese aus PVC: Chlorwolken, Rohrschlag, Plastiksalve, Giftstrahl.</li>
 *   <li><b>Mycelial Queen</b> (Sporos) - Pilzkoenigin: Sporensturm, Wurzelausbruch, Ranken, Brut der Infizierten.</li>
 * </ol>
 */
public final class Bosses {

    private Bosses() {}

    static Vec3 front(LivingEntity e, double dist, double height) {
        float yaw = e.yBodyRot * Mth.DEG_TO_RAD;
        return new Vec3(e.getX() - Mth.sin(yaw) * dist, e.getY() + height, e.getZ() + Mth.cos(yaw) * dist);
    }

    //? if < 1.21.1 {
    static MobEffectInstance fx(net.minecraft.world.effect.MobEffect e, int ticks, int amp) {
    //?} else {
    /*static MobEffectInstance fx(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> e, int ticks, int amp) {
    *///?}
        return new MobEffectInstance(e, ticks, amp);
    }

    // ── 1 Mun ───────────────────────────────────────────────────────────────

    public static class LunarSentinel extends EntityStageBoss {
        public LunarSentinel(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.RED);
        }

        @Override
        public Stage stage() {
            return Stage.MUN;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("laser", 60, 24, false, (level, t, i) -> {
                Vec3 eye = getEyePosition();
                if (i < 15) {
                    if (i % 3 == 0) aimLine(level, eye, t.getEyePosition(), 0xFF3030);
                } else if (i == 15 || i == 20 || i == 25) {
                    beam(level, this, eye, t.getEyePosition(), 7.0F, 0xFF3030, null);
                    sound(level, this, SoundEvents.GUARDIAN_ATTACK, 2.0F, 1.6F);
                }
                return i >= 26;
            }));
            attacks.add(new Attack("rockets", 120, 30, false, (level, t, i) -> {
                if (i % 6 == 0 && i < 30) {
                    EntityBossOrb.shoot(level, this, front(this, -0.8, 3.6), t.getEyePosition().subtract(front(this, -0.8, 3.6)).normalize().scale(0.9)
                            .add(0, 0.25, 0), 0xFF7030, 0.35F, 6.0F).gravity(0.03).splash(2.5F);
                    sound(level, this, SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.5F, 0.7F);
                }
                return i >= 32;
            }));
            attacks.add(new Attack("stomp", 100, 6, false, (level, t, i) -> {
                if (i == 0) setDeltaMovement(0, 0.6, 0);
                if (i == 14) {
                    shockwave(level, this, position(), 5.0, 9.0F, 1.0, ParticleTypes.CLOUD);
                    sound(level, this, SoundEvents.IRON_GOLEM_DAMAGE, 2.0F, 0.5F);
                }
                return i >= 20;
            }));
            attacks.add(new Attack("drones", 400, 30, true, (level, t, i) -> {
                summon(level, this, "space_beyond:moon_drone", 3, 3);
                sound(level, this, SoundEvents.BEACON_ACTIVATE, 2.0F, 1.5F);
                return true;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:lost_astronaut", 2, 4);
        }

        @Override
        protected void clientEffects() {
            if (random.nextInt(4) == 0) level().addParticle(ParticleTypes.ELECTRIC_SPARK, getRandomX(0.6), getY() + 2.5 + random.nextDouble(), getRandomZ(0.6), 0, 0, 0);
        }
    }

    // ── 2 Minmus ────────────────────────────────────────────────────────────

    public static class CryoBehemoth extends EntityStageBoss {
        private Vec3 lock = Vec3.ZERO;

        public CryoBehemoth(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.GREEN);
        }

        @Override
        public Stage stage() {
            return Stage.MINMUS;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("ice_spikes", 90, 20, false, (level, t, i) -> {
                if (i == 0) lock = ground(level, t.position());
                if (i < 20 && i % 2 == 0) ring(level, lock, 3.0, ParticleTypes.SNOWFLAKE);
                if (i == 20) {
                    shockwave(level, this, lock, 3.0, 10.0F, 0.6, ParticleTypes.SNOWFLAKE);
                    for (LivingEntity e : foes(level, lock, 3.0)) e.setTicksFrozen(e.getTicksRequiredToFreeze() + 120);
                    level.sendParticles(dust(0xA0F0E0, 2.0F), lock.x, lock.y + 1.5, lock.z, 60, 1.2, 1.5, 1.2, 0);
                    sound(level, this, SoundEvents.GLASS_BREAK, 2.0F, 0.5F);
                }
                return i >= 24;
            }));
            attacks.add(new Attack("frost_breath", 110, 8, false, (level, t, i) -> {
                if (i % 3 == 0) {
                    Vec3 eye = getEyePosition(), dir = t.getEyePosition().subtract(eye).normalize();
                    for (int k = 1; k <= 8; k++) {
                        Vec3 at = eye.add(dir.scale(k));
                        level.sendParticles(ParticleTypes.SNOWFLAKE, at.x, at.y, at.z, 4, 0.2 * k / 3, 0.2 * k / 3, 0.2 * k / 3, 0.02);
                        for (LivingEntity e : foes(level, at, 0.6 + k * 0.2)) {
                            e.setTicksFrozen(Math.min(e.getTicksRequiredToFreeze() + 140, e.getTicksFrozen() + 25));
                            e.hurt(level.damageSources().freeze(), 2.0F);
                        }
                    }
                }
                if (i == 0) sound(level, this, SoundEvents.POWDER_SNOW_STEP, 2.0F, 0.5F);
                return i >= 30;
            }));
            attacks.add(new Attack("boulder", 80, 26, false, (level, t, i) -> {
                if (i == 10) {
                    Vec3 from = getEyePosition().add(0, 1, 0);
                    Vec3 d = t.getEyePosition().subtract(from);
                    EntityBossOrb.shoot(level, this, from, d.normalize().scale(1.0).add(0, d.length() * 0.025, 0), 0xA0F0E0, 0.8F, 10.0F)
                            .gravity(0.04).splash(3.5F).effect(fx(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
                    sound(level, this, SoundEvents.SNOW_GOLEM_SHOOT, 2.0F, 0.4F);
                }
                return i >= 14;
            }));
            attacks.add(new Attack("slam", 100, 5, false, (level, t, i) -> {
                if (i == 12) {
                    shockwave(level, this, position(), 6.0, 12.0F, 1.2, ParticleTypes.SNOWFLAKE);
                    sound(level, this, SoundEvents.ANVIL_LAND, 2.0F, 0.4F);
                }
                return i >= 18;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:frost_wisp", 3, 4);
        }

        @Override
        protected void clientEffects() {
            if (random.nextInt(3) == 0) level().addParticle(ParticleTypes.SNOWFLAKE, getRandomX(1.0), getY() + random.nextDouble() * 4, getRandomZ(1.0), 0, -0.02, 0);
        }
    }

    // ── 3 Duna ──────────────────────────────────────────────────────────────

    public static class RustTitan extends EntityStageBoss {
        private Vec3 dir = Vec3.ZERO;

        public RustTitan(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.RED);
        }

        @Override
        public Stage stage() {
            return Stage.DUNA;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("cannon", 70, 30, false, (level, t, i) -> {
                Vec3 muzzle = front(this, 2.5, 2.4);
                if (i < 16 && i % 3 == 0) aimLine(level, muzzle, t.getEyePosition(), 0xFFAA30);
                if (i == 16) {
                    EntityBossOrb.shoot(level, this, muzzle, t.getEyePosition().subtract(muzzle).normalize().scale(1.2), 0xFFAA30, 0.7F, 12.0F).splash(4.0F);
                    sound(level, this, SoundEvents.DRAGON_FIREBALL_EXPLODE, 2.0F, 1.2F);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, muzzle.x, muzzle.y, muzzle.z, 10, 0.3, 0.3, 0.3, 0.05);
                }
                return i >= 20;
            }));
            attacks.add(new Attack("sandstorm", 200, 16, false, (level, t, i) -> {
                for (LivingEntity e : foes(level, position(), 14)) {
                    Vec3 d = position().subtract(e.position()).normalize().scale(0.06);
                    e.setDeltaMovement(e.getDeltaMovement().add(d.x, 0, d.z));
                    e.hurtMarked = true;
                    if (i % 20 == 0) {
                        e.addEffect(fx(MobEffects.BLINDNESS, 40, 0));
                        e.hurt(level.damageSources().mobAttack(this), 3.0F);
                    }
                }
                double a = i * 0.4;
                for (int k = 0; k < 6; k++) {
                    double r = 3 + k * 2;
                    level.sendParticles(dust(0xC2603A, 2.5F), getX() + Math.cos(a + k) * r, getY() + 1 + k * 0.3, getZ() + Math.sin(a + k) * r, 2, 0.4, 0.4, 0.4, 0);
                }
                if (i % 20 == 0) sound(level, this, SoundEvents.SAND_BREAK, 2.0F, 0.4F);
                return i >= 60;
            }));
            attacks.add(new Attack("charge", 120, 20, false, (level, t, i) -> {
                if (i < 10) {
                    dir = t.position().subtract(position()).multiply(1, 0, 1).normalize();
                    setDeltaMovement(Vec3.ZERO);
                } else {
                    setDeltaMovement(dir.x * 1.3, getDeltaMovement().y, dir.z * 1.3);
                    for (LivingEntity e : foes(level, position(), 2.5)) {
                        e.hurt(level.damageSources().mobAttack(this), 12.0F);
                        e.setDeltaMovement(dir.x * 2, 0.7, dir.z * 2);
                        e.hurtMarked = true;
                    }
                    level.sendParticles(dust(0xC2603A, 2.0F), getX(), getY() + 0.5, getZ(), 6, 1, 0.3, 1, 0);
                }
                return i >= 24 || (i > 12 && horizontalCollision);
            }));
            attacks.add(new Attack("quake", 140, 10, false, (level, t, i) -> {
                if (i == 0) setDeltaMovement(0, 0.5, 0);
                if (i == 12 || i == 18 || i == 24) {
                    shockwave(level, this, position(), 4 + (i - 12) / 2.0, 8.0F, 0.8, ParticleTypes.CAMPFIRE_COSY_SMOKE);
                    sound(level, this, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5F, 0.5F);
                }
                return i >= 26;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:rust_scuttler", 4, 5);
        }
    }

    // ── 4 Ike ───────────────────────────────────────────────────────────────

    public static class MonolithKeeper extends EntityStageBoss {
        public MonolithKeeper(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.PURPLE);
        }

        @Override
        public Stage stage() {
            return Stage.IKE;
        }

        @Override
        public Move move() {
            return Move.FLY;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("eye_beam", 70, 28, false, (level, t, i) -> {
                Vec3 eye = getEyePosition();
                if (i < 20 && i % 3 == 0) aimLine(level, eye, t.getEyePosition(), 0xC070FF);
                if (i >= 20 && i % 4 == 0) {
                    beam(level, this, eye, t.getEyePosition(), 5.0F, 0xC070FF, fx(MobEffects.LEVITATION, 10, 0));
                    if (i == 20) sound(level, this, SoundEvents.BEACON_POWER_SELECT, 2.0F, 0.6F);
                }
                return i >= 40;
            }));
            attacks.add(new Attack("gravity", 140, 16, false, (level, t, i) -> {
                if (i == 0) {
                    for (LivingEntity e : foes(level, position(), 12)) e.addEffect(fx(MobEffects.LEVITATION, 60, 1));
                    sound(level, this, SoundEvents.SHULKER_SHOOT, 2.0F, 0.4F);
                }
                if (i == 60) {
                    for (LivingEntity e : foes(level, position(), 16)) {
                        e.removeEffect(MobEffects.LEVITATION);
                        e.setDeltaMovement(0, -2.5, 0);
                        e.hurtMarked = true;
                    }
                    sound(level, this, SoundEvents.ENDER_DRAGON_FLAP, 2.0F, 0.5F);
                }
                return i >= 62;
            }));
            attacks.add(new Attack("stone_rain", 100, 24, false, (level, t, i) -> {
                if (i % 5 == 0 && i < 40) {
                    Vec3 at = t.position().add(random.nextGaussian() * 3, 12, random.nextGaussian() * 3);
                    EntityBossOrb.shoot(level, this, at, new Vec3(0, -0.4, 0), 0x8A8098, 0.6F, 7.0F).gravity(0.05).splash(2.0F);
                }
                return i >= 40;
            }));
            attacks.add(new Attack("blink", 160, 32, false, (level, t, i) -> {
                double a = random.nextDouble() * Math.PI * 2;
                Vec3 to = t.position().add(Math.cos(a) * 8, 3, Math.sin(a) * 8);
                level.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 2, getZ(), 60, 0.6, 1.5, 0.6, 0.5);
                teleportTo(to.x, to.y, to.z);
                shockwave(level, this, ground(level, to), 4.0, 8.0F, 1.0, ParticleTypes.REVERSE_PORTAL);
                sound(level, this, SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
                return true;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:void_shade", 3, 4);
        }

        @Override
        protected void clientEffects() {
            if (random.nextInt(2) == 0) level().addParticle(ParticleTypes.REVERSE_PORTAL, getRandomX(1.2), getY() + random.nextDouble() * 4, getRandomZ(1.2), 0, 0.02, 0);
        }
    }

    // ── 5 Dres ──────────────────────────────────────────────────────────────

    public static class CoreAbomination extends EntityStageBoss {
        public CoreAbomination(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.GREEN);
        }

        @Override
        public Stage stage() {
            return Stage.DRES;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("rad_pulse", 90, 10, false, (level, t, i) -> {
                if (i < 20 && i % 2 == 0) ring(level, position(), 1 + i * 0.35, dust(0x7CFF40, 1.6F));
                if (i == 20) {
                    shockwave(level, this, position(), 8.0, 10.0F, 0.6, dust(0x7CFF40, 2.0F));
                    for (LivingEntity e : foes(level, position(), 8)) {
                        e.addEffect(fx(MobEffects.POISON, 100, 1));
                        e.addEffect(fx(MobEffects.WITHER, 60, 0));
                    }
                    sound(level, this, SoundEvents.BEACON_DEACTIVATE, 2.0F, 0.5F);
                }
                return i >= 22;
            }));
            attacks.add(new Attack("sludge", 60, 24, false, (level, t, i) -> {
                if (i == 6) {
                    volley(level, this, t, getEyePosition(), 3, 0.2, 0.9, 0x7CFF40, 0.5F, 7.0F);
                    sound(level, this, SoundEvents.SLIME_SQUISH, 2.0F, 0.5F);
                }
                return i >= 10;
            }));
            attacks.add(new Attack("toxic_pool", 120, 22, false, (level, t, i) -> {
                cloud(level, this, ground(level, t.position()), 3.0F, 140, 0x7CFF40, fx(MobEffects.POISON, 80, 1));
                return true;
            }));
            attacks.add(new Attack("meltdown", 300, 14, true, (level, t, i) -> {
                if (i < 40) {
                    if (i % 4 == 0) {
                        level.sendParticles(dust(0x7CFF40, 2.5F), getX(), getY() + 1.5, getZ(), 30, 1.5, 1.5, 1.5, 0);
                        sound(level, this, SoundEvents.NOTE_BLOCK_BIT.value(), 2.0F, 0.5F + i / 40.0F);
                    }
                    setDeltaMovement(Vec3.ZERO);
                }
                if (i == 40) {
                    shockwave(level, this, position(), 12.0, 18.0F, 1.6, dust(0x7CFF40, 2.5F));
                    level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1, getZ(), 2, 1, 1, 1, 0);
                    sound(level, this, SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 0.6F);
                }
                return i >= 44;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:toxic_sludge", 2, 5);
        }

        @Override
        protected void clientEffects() {
            if (random.nextInt(2) == 0) level().addParticle(ParticleTypes.HAPPY_VILLAGER, getRandomX(1.4), getY() + random.nextDouble() * 3, getRandomZ(1.4), 0, 0.02, 0);
        }
    }

    // ── 6 Moho ──────────────────────────────────────────────────────────────

    public static class MagmaWyrm extends EntityStageBoss {
        private Vec3 lock = Vec3.ZERO;
        private int[] spots = new int[0];

        public MagmaWyrm(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.YELLOW);
        }

        @Override
        public Stage stage() {
            return Stage.MOHO;
        }

        @Override
        public Move move() {
            return Move.FLY;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("fire_breath", 90, 12, false, (level, t, i) -> {
                Vec3 eye = getEyePosition(), dir = t.getEyePosition().subtract(eye).normalize();
                for (int k = 1; k <= 10; k += 2) {
                    Vec3 at = eye.add(dir.scale(k));
                    level.sendParticles(ParticleTypes.FLAME, at.x, at.y, at.z, 3, 0.15 * k / 2, 0.15 * k / 2, 0.15 * k / 2, 0.02);
                    if (i % 4 == 0) for (LivingEntity e : foes(level, at, 0.6 + k * 0.2)) {
                        e.setRemainingFireTicks(Math.max(e.getRemainingFireTicks(), 100));
                        e.hurt(level.damageSources().onFire(), 3.0F);
                    }
                }
                if (i % 8 == 0) sound(level, this, SoundEvents.BLAZE_SHOOT, 2.0F, 0.6F);
                return i >= 30;
            }));
            attacks.add(new Attack("magma_volley", 70, 28, false, (level, t, i) -> {
                if (i == 8) {
                    volley(level, this, t, getEyePosition(), 5, 0.15, 1.0, 0xFF7020, 0.45F, 8.0F);
                    sound(level, this, SoundEvents.GHAST_SHOOT, 2.0F, 0.6F);
                }
                return i >= 12;
            }));
            attacks.add(new Attack("dive", 140, 24, false, (level, t, i) -> {
                if (i < 15) setDeltaMovement(0, 0.5, 0);
                if (i == 15) lock = ground(level, t.position());
                if (i >= 15) {
                    Vec3 d = lock.subtract(position());
                    if (d.length() < 2.0 || i >= 34) {
                        shockwave(level, this, lock, 5.0, 14.0F, 1.2, ParticleTypes.LAVA);
                        for (LivingEntity e : foes(level, lock, 5)) e.setRemainingFireTicks(100);
                        sound(level, this, SoundEvents.DRAGON_FIREBALL_EXPLODE, 2.5F, 0.7F);
                        return true;
                    }
                    setDeltaMovement(d.normalize().scale(1.5));
                }
                return false;
            }));
            attacks.add(new Attack("eruption", 160, 20, true, (level, t, i) -> {
                if (i == 0) {
                    spots = new int[12];
                    for (int k = 0; k < 6; k++) {
                        spots[k * 2] = (int) (t.getX() + random.nextGaussian() * 4);
                        spots[k * 2 + 1] = (int) (t.getZ() + random.nextGaussian() * 4);
                    }
                }
                for (int k = 0; k < 6; k++) {
                    Vec3 at = ground(level, new Vec3(spots[k * 2] + 0.5, t.getY() + 2, spots[k * 2 + 1] + 0.5));
                    if (i < 20 && i % 2 == 0) ring(level, at, 2.0, ParticleTypes.FLAME);
                    if (i == 20) {
                        shockwave(level, this, at, 2.5, 10.0F, 0.8, ParticleTypes.LAVA);
                        level.sendParticles(ParticleTypes.LAVA, at.x, at.y + 0.5, at.z, 20, 0.5, 1.5, 0.5, 0);
                    }
                }
                if (i == 20) sound(level, this, SoundEvents.LAVA_POP, 3.0F, 0.5F);
                return i >= 22;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:cinder_imp", 3, 4);
        }

        @Override
        public boolean fireImmune() {
            return true;
        }

        @Override
        protected void clientEffects() {
            level().addParticle(ParticleTypes.FLAME, getRandomX(0.8), getY() + random.nextDouble() * 2, getRandomZ(0.8), 0, 0.02, 0);
            if (random.nextInt(4) == 0) level().addParticle(ParticleTypes.LAVA, getRandomX(0.8), getY() + 1, getRandomZ(0.8), 0, 0, 0);
        }
    }

    // ── 7 Eve ───────────────────────────────────────────────────────────────

    public static class SchrabHydra extends EntityStageBoss {
        private boolean regrown;

        public SchrabHydra(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.BLUE);
        }

        @Override
        public Stage stage() {
            return Stage.EVE;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("triple_beam", 80, 26, false, (level, t, i) -> {
                Vec3[] heads = {front(this, 3.5, 3.4), front(this, 3.0, 3.0).add(Mth.cos(yBodyRot * Mth.DEG_TO_RAD) * 2, 0, Mth.sin(yBodyRot * Mth.DEG_TO_RAD) * 2),
                        front(this, 3.0, 3.0).add(-Mth.cos(yBodyRot * Mth.DEG_TO_RAD) * 2, 0, -Mth.sin(yBodyRot * Mth.DEG_TO_RAD) * 2)};
                for (int k = 0; k < 3; k++) {
                    Vec3 aim = t.getEyePosition().add((k - 1) * 1.5, 0, 0);
                    if (i < 18 && i % 3 == 0) aimLine(level, heads[k], aim, 0x30E8D8);
                    if (i == 18) beam(level, this, heads[k], aim, 8.0F, 0x30E8D8, fx(MobEffects.POISON, 80, 1));
                }
                if (i == 18) sound(level, this, SoundEvents.AMETHYST_BLOCK_RESONATE, 2.0F, 1.8F);
                return i >= 22;
            }));
            attacks.add(new Attack("shards", 90, 26, false, (level, t, i) -> {
                if (i == 6) {
                    for (int k = 0; k < 7; k++) {
                        double a = (k - 3) * 0.25;
                        Vec3 from = front(this, 2.5, 3.2);
                        Vec3 d = t.getEyePosition().subtract(from).normalize().yRot((float) a).scale(0.7).add(0, 0.2, 0);
                        EntityBossOrb.shoot(level, this, from, d, 0x30E8D8, 0.35F, 6.0F).homing(t, 0.08);
                    }
                    sound(level, this, SoundEvents.AMETHYST_CLUSTER_BREAK, 2.0F, 0.8F);
                }
                return i >= 10;
            }));
            attacks.add(new Attack("acid", 140, 22, false, (level, t, i) -> {
                for (int k = 0; k < 3; k++) {
                    Vec3 at = ground(level, t.position().add(random.nextGaussian() * 3, 1, random.nextGaussian() * 3));
                    cloud(level, this, at, 2.5F, 160, 0x30E8D8, fx(MobEffects.POISON, 80, 1));
                }
                return true;
            }));
            attacks.add(new Attack("bite", 50, 5, false, (level, t, i) -> {
                if (i == 8 && distanceTo(t) < 5.5) {
                    t.hurt(level.damageSources().mobAttack(this), 16.0F);
                    Vec3 d = t.position().subtract(position()).normalize();
                    t.setDeltaMovement(d.x * 1.2, 0.5, d.z * 1.2);
                    t.hurtMarked = true;
                    sound(level, this, SoundEvents.EVOKER_FANGS_ATTACK, 2.0F, 0.6F);
                }
                return i >= 12;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            if (!regrown) {
                regrown = true;
                heal(getMaxHealth() * 0.1F);
                level.sendParticles(dust(0x30E8D8, 2.0F), getX(), getY() + 2, getZ(), 80, 1.5, 1.5, 1.5, 0);
            }
            summon(level, this, "space_beyond:schrab_spitter", 3, 4);
        }
    }

    // ── 8 Laythe ────────────────────────────────────────────────────────────

    public static class AbyssalLeviathan extends EntityStageBoss {
        private Vec3 dir = Vec3.ZERO;

        public AbyssalLeviathan(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.BLUE);
        }

        @Override
        public Stage stage() {
            return Stage.LAYTHE;
        }

        @Override
        public Move move() {
            return Move.FLY;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("lure_flash", 120, 20, false, (level, t, i) -> {
                Vec3 lure = front(this, 2.5, 4.0);
                if (i < 20) level.sendParticles(ParticleTypes.GLOW, lure.x, lure.y, lure.z, 4, 0.3, 0.3, 0.3, 0.05);
                if (i == 20) {
                    for (LivingEntity e : foes(level, position(), 16)) {
                        e.addEffect(fx(MobEffects.BLINDNESS, 80, 0));
                        e.addEffect(fx(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
                    }
                    level.sendParticles(ParticleTypes.FLASH, lure.x, lure.y, lure.z, 1, 0, 0, 0, 0);
                    sound(level, this, SoundEvents.ELDER_GUARDIAN_CURSE, 2.0F, 0.8F);
                }
                return i >= 22;
            }));
            attacks.add(new Attack("vortex", 160, 18, false, (level, t, i) -> {
                for (LivingEntity e : foes(level, position(), 16)) {
                    Vec3 d = position().subtract(e.position()).normalize().scale(0.12);
                    e.setDeltaMovement(e.getDeltaMovement().add(d));
                    e.hurtMarked = true;
                }
                if (i % 2 == 0) for (int k = 0; k < 8; k++) {
                    double a = i * 0.3 + k * Math.PI / 4, r = 10 - (i % 20) * 0.4;
                    level.sendParticles(ParticleTypes.BUBBLE, getX() + Math.cos(a) * r, getY() + 1, getZ() + Math.sin(a) * r, 2, 0.2, 0.5, 0.2, 0);
                }
                if (i == 50) {
                    shockwave(level, this, position(), 4.0, 14.0F, 1.4, ParticleTypes.BUBBLE_POP);
                    sound(level, this, SoundEvents.GENERIC_SPLASH, 3.0F, 0.5F);
                }
                return i >= 52;
            }));
            attacks.add(new Attack("water_jet", 70, 24, false, (level, t, i) -> {
                Vec3 mouth = front(this, 3.0, 1.5);
                if (i < 12 && i % 3 == 0) aimLine(level, mouth, t.getEyePosition(), 0x40C8FF);
                if (i == 12) {
                    beam(level, this, mouth, t.getEyePosition(), 10.0F, 0x40C8FF, null);
                    Vec3 d = t.position().subtract(position()).normalize();
                    t.setDeltaMovement(d.x * 1.8, 0.4, d.z * 1.8);
                    t.hurtMarked = true;
                    sound(level, this, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 2.0F, 0.6F);
                }
                return i >= 14;
            }));
            attacks.add(new Attack("bite_dash", 100, 20, false, (level, t, i) -> {
                if (i < 8) dir = t.getEyePosition().subtract(getEyePosition()).normalize();
                else setDeltaMovement(dir.scale(1.4));
                if (i >= 8 && distanceTo(t) < 3.5) {
                    t.hurt(level.damageSources().mobAttack(this), 16.0F);
                    sound(level, this, SoundEvents.EVOKER_FANGS_ATTACK, 2.0F, 0.5F);
                    return true;
                }
                return i >= 22;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:abyss_diver", 3, 4);
            summon(level, this, "space_beyond:deep_angler", 2, 4);
        }

        @Override
        protected void clientEffects() {
            Vec3 lure = front(this, 2.5, 4.0);
            level().addParticle(ParticleTypes.GLOW, lure.x, lure.y, lure.z, 0, 0, 0);
        }
    }

    // ── 9 Tekto ─────────────────────────────────────────────────────────────

    public static class PolymerColossus extends EntityStageBoss {
        public PolymerColossus(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.GREEN);
        }

        @Override
        public Stage stage() {
            return Stage.TEKTO;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("chlorine", 100, 22, false, (level, t, i) -> {
                for (int k = 0; k < 3; k++) {
                    Vec3 at = ground(level, t.position().add(random.nextGaussian() * 3, 1, random.nextGaussian() * 3));
                    cloud(level, this, at, 3.0F, 140, 0xB8F040, fx(MobEffects.POISON, 100, 2));
                }
                sound(level, this, SoundEvents.LAVA_EXTINGUISH, 2.0F, 0.5F);
                return true;
            }));
            attacks.add(new Attack("pipe_slam", 70, 7, false, (level, t, i) -> {
                if (i == 14) {
                    shockwave(level, this, front(this, 3, 0), 5.0, 16.0F, 1.4, ParticleTypes.CLOUD);
                    sound(level, this, SoundEvents.ANVIL_LAND, 2.0F, 0.6F);
                }
                return i >= 18;
            }));
            attacks.add(new Attack("plastic_barrage", 80, 28, false, (level, t, i) -> {
                if (i % 5 == 0 && i < 25) {
                    Vec3 from = front(this, 1.0, 3.6);
                    Vec3 d = t.getEyePosition().subtract(from);
                    EntityBossOrb.shoot(level, this, from, d.normalize().scale(0.9).add(0, d.length() * 0.02, 0), 0xE0E4D8, 0.4F, 8.0F)
                            .gravity(0.02).splash(2.0F).effect(fx(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
                return i >= 26;
            }));
            attacks.add(new Attack("toxic_beam", 110, 24, false, (level, t, i) -> {
                Vec3 visor = getEyePosition();
                if (i < 16 && i % 3 == 0) aimLine(level, visor, t.getEyePosition(), 0xB8F040);
                if (i == 16) {
                    beam(level, this, visor, t.getEyePosition(), 12.0F, 0xB8F040, fx(MobEffects.POISON, 100, 1));
                    sound(level, this, SoundEvents.GUARDIAN_ATTACK, 2.0F, 0.6F);
                }
                return i >= 18;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:polymer_ooze", 2, 5);
            summon(level, this, "space_beyond:refinery_drone", 2, 5);
        }
    }

    // ── 10 Sporos ───────────────────────────────────────────────────────────

    public static class MycelialQueen extends EntityStageBoss {
        public MycelialQueen(EntityType<? extends EntityStageBoss> type, Level level) {
            super(type, level, BossEvent.BossBarColor.YELLOW);
        }

        @Override
        public Stage stage() {
            return Stage.SPOROS;
        }

        @Override
        protected void registerAttacks() {
            attacks.add(new Attack("spore_storm", 120, 18, false, (level, t, i) -> {
                if (i % 10 == 0) {
                    for (LivingEntity e : foes(level, position(), 14)) {
                        e.addEffect(fx(MobEffects.WITHER, 60, 0));
                        e.addEffect(fx(MobEffects.BLINDNESS, 40, 0));
                    }
                    level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY() + 4, getZ(), 120, 6, 2, 6, 0);
                    level.sendParticles(dust(0xFFC85A, 2.0F), getX(), getY() + 3, getZ(), 40, 6, 2, 6, 0);
                }
                if (i == 0) sound(level, this, SoundEvents.SPORE_BLOSSOM_BREAK, 2.0F, 0.4F);
                return i >= 40;
            }));
            attacks.add(new Attack("root_eruption", 90, 20, false, (level, t, i) -> {
                for (LivingEntity e : foes(level, position(), 20)) {
                    Vec3 at = ground(level, e.position());
                    if (i < 18 && i % 2 == 0) ring(level, at, 2.0, dust(0xD2C8B4, 1.2F));
                    if (i == 18) {
                        shockwave(level, this, at, 2.5, 12.0F, 0.2, dust(0xD2C8B4, 2.0F));
                        e.addEffect(fx(MobEffects.MOVEMENT_SLOWDOWN, 60, 4));
                    }
                }
                if (i == 18) sound(level, this, SoundEvents.ROOTED_DIRT_BREAK, 3.0F, 0.5F);
                return i >= 20;
            }));
            attacks.add(new Attack("tendril_lash", 60, 8, false, (level, t, i) -> {
                if (i == 10) {
                    shockwave(level, this, position(), 7.0, 12.0F, 1.0, dust(0xD2C8B4, 1.5F));
                    sound(level, this, SoundEvents.PLAYER_ATTACK_SWEEP, 2.0F, 0.5F);
                }
                return i >= 14;
            }));
            attacks.add(new Attack("spore_bolts", 70, 26, false, (level, t, i) -> {
                if (i == 8) {
                    volley(level, this, t, getEyePosition().add(0, 1, 0), 5, 0.2, 0.7, 0xFFC85A, 0.4F, 8.0F);
                    sound(level, this, SoundEvents.SLIME_SQUISH, 2.0F, 0.6F);
                }
                return i >= 12;
            }));
            attacks.add(new Attack("brood", 220, 30, false, (level, t, i) -> {
                summon(level, this, "space_beyond:hive_wasp", 3, 4);
                if (phaseTwo()) summon(level, this, "space_beyond:mycobrute", 1, 5);
                return true;
            }));
        }

        @Override
        protected void onPhaseTwo(ServerLevel level) {
            summon(level, this, "space_beyond:lurker", 2, 5);
        }

        @Override
        protected void clientEffects() {
            if (random.nextInt(2) == 0) level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, getRandomX(2.0), getY() + 4 + random.nextDouble() * 2, getRandomZ(2.0), 0, -0.01, 0);
        }
    }
}
