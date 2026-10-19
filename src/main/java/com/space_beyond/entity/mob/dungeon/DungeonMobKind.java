package com.space_beyond.entity.mob.dungeon;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Bauplan eines Dungeon-Mobs ({@link EntityDungeonMob}): Werte, Bewegung, Kampfstil und Besonderheiten. Werte gelten
 * fuer Stufe 1 und wachsen mit der Stufe des Planeten ({@link #health()}, {@link #damage()}).
 */
public final class DungeonMobKind {

    public enum Move { WALK, FLY, SWIM }

    /** Fernkampf: Kugel ({@code EntityBossOrb}) in Farbe/Groesse, Schaden, Abklingzeit, Reichweite, Bogen, Zielsuche, Splash, Salve. */
    public record Ranged(int color, float size, float damage, int cooldown, double range, double speed, double gravity, double homing,
                         float splash, int volley, float spread) { }

    public final String id;
    public final Stage stage;
    public float width = 0.7F, height = 1.9F;
    public double hp = 20, dmg = 4, armor = 0, speed = 0.25, flySpeed = 0.5, knockResist = 0;
    public int xp = 8;
    public Move move = Move.WALK;
    @Nullable public Ranged ranged;
    /** Bevorzugter Abstand im Fernkampf (haelt Distanz). */
    public double keepAway = 8;
    @Nullable public Supplier<MobEffectInstance> hitEffect;
    public int fireSeconds;
    /** Sprung auf das Ziel (Abklingzeit in Ticks, 0 = nie). */
    public int leap;
    /** Stampfen: Flaechenschaden mit Rueckstoss (Abklingzeit, 0 = nie). */
    public int slam;
    @Nullable public Supplier<MobEffectInstance> slamEffect;
    /** Teleport hinter das Ziel (Abklingzeit, 0 = nie). */
    public int teleport;
    /** Teilt sich beim Tod in so viele kleine Kopien (0 = nie). */
    public int split;
    /** Wolke beim Tod. */
    @Nullable public Supplier<MobEffectInstance> deathCloud;
    public int cloudColor = 0x80FF40;
    public boolean fireImmune, breathesWater;
    @Nullable public Supplier<ParticleOptions> particle;
    public Supplier<SoundEvent> ambient, hurt, death;
    @Nullable public Supplier<SoundEvent> step, attackSound;
    public float pitch = 1.0F;
    /** Farben des Spawn-Eis. */
    public int egg1 = 0x808080, egg2 = 0xFFFFFF;

    public DungeonMobKind(String id, Stage stage) {
        this.id = id;
        this.stage = stage;
    }

    /** Leben nach Stufe (je Stufe +15 %). */
    public double health() {
        return hp * (1.0 + 0.15 * (stage.tier() - 1));
    }

    /** Nahkampfschaden nach Stufe (je Stufe +12 %). */
    public double damage() {
        return dmg * (1.0 + 0.12 * (stage.tier() - 1));
    }

    /** Faktor fuer Geschoss-/Flaechenschaden. */
    public float power() {
        return (float) (1.0 + 0.12 * (stage.tier() - 1));
    }

    // ── Bauweise ──

    public DungeonMobKind size(float w, float h) { width = w; height = h; return this; }
    public DungeonMobKind stats(double hp, double dmg, double armor, double speed) { this.hp = hp; this.dmg = dmg; this.armor = armor; this.speed = speed; return this; }
    public DungeonMobKind xp(int xp) { this.xp = xp; return this; }
    public DungeonMobKind fly(double flySpeed) { move = Move.FLY; this.flySpeed = flySpeed; return this; }
    public DungeonMobKind swim() { move = Move.SWIM; breathesWater = true; return this; }
    public DungeonMobKind amphibious() { breathesWater = true; return this; }
    public DungeonMobKind ranged(Ranged r, double keepAway) { ranged = r; this.keepAway = keepAway; return this; }
    public DungeonMobKind onHit(Supplier<MobEffectInstance> e) { hitEffect = e; return this; }
    public DungeonMobKind fire(int seconds) { fireSeconds = seconds; return this; }
    public DungeonMobKind leap(int cooldown) { leap = cooldown; return this; }
    public DungeonMobKind slam(int cooldown, @Nullable Supplier<MobEffectInstance> e) { slam = cooldown; slamEffect = e; return this; }
    public DungeonMobKind teleport(int cooldown) { teleport = cooldown; return this; }
    public DungeonMobKind split(int n) { split = n; return this; }
    public DungeonMobKind cloud(Supplier<MobEffectInstance> e, int color) { deathCloud = e; cloudColor = color; return this; }
    public DungeonMobKind fireImmune() { fireImmune = true; return this; }
    public DungeonMobKind knockResist(double k) { knockResist = k; return this; }
    public DungeonMobKind particle(Supplier<ParticleOptions> p) { particle = p; return this; }
    public DungeonMobKind sounds(Supplier<SoundEvent> ambient, Supplier<SoundEvent> hurt, Supplier<SoundEvent> death, float pitch) {
        this.ambient = ambient; this.hurt = hurt; this.death = death; this.pitch = pitch; return this;
    }
    public DungeonMobKind step(Supplier<SoundEvent> s) { step = s; return this; }
    public DungeonMobKind attackSound(Supplier<SoundEvent> s) { attackSound = s; return this; }
    public DungeonMobKind egg(int c1, int c2) { egg1 = c1; egg2 = c2; return this; }
}
