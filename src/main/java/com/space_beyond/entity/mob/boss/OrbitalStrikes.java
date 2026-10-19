package com.space_beyond.entity.mob.boss;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Angriffsmuster der Orbitalwaffen ({@link ItemOrbitalStrikeRod}, Boss {@link EntityUnstableSovereign}). Jeder Schlag
 * ist ein {@link OrbitalBeamEntity}; hier werden sie mit Verzoegerung, Staerke und Anordnung erzeugt.
 */
public final class OrbitalStrikes {

    private OrbitalStrikes() {}

    public static void init() {
    }

    private static void spawn(ServerLevel level, Vec3 pos, int kind, int warmup, float power, float damage, boolean blocks, @Nullable Entity owner) {
        level.addFreshEntity(OrbitalBeamEntity.create(level, pos, kind, warmup, power, damage, blocks, owner));
    }

    /**
     * Bodenpunkt bei einer Stelle, damit Streuschlaege auf dem Gelaende landen: sucht von knapp ueber {@code y} nach
     * unten den ersten begehbaren Boden (auch in Hallen mit Dach, z. B. der Thronhalle), sonst die Hoehenkarte.
     */
    static Vec3 ground(ServerLevel level, double x, double y, double z) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(y) + 3, Mth.floor(z));
        for (int i = 0; i < 24; i++) {
            BlockPos below = p.below();
            if (!level.getBlockState(below).getCollisionShape(level, below).isEmpty()
                    && level.getBlockState(p).getCollisionShape(level, p).isEmpty()) {
                return new Vec3(x, p.getY(), z);
            }
            p.move(0, -1, 0);
        }
        int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
        return new Vec3(x, Math.abs(top - y) < 24 ? top : y, z);
    }

    /** Sovereign Lance: ein gigantischer Strahl (2 s Aufladen), Kernexplosion + Ring aus acht Explosionen, Megablitze. */
    public static void lance(ServerLevel level, Vec3 pos, @Nullable Entity owner, boolean blocks) {
        spawn(level, pos, OrbitalBeamEntity.LANCE, 40, 8.0F, 34.0F, blocks, owner);
    }

    /**
     * Sovereign Cataclysm: 5 s Vorwarnung (Warnung an alle Spieler in 300 Bloecken, Sog), dann ein gigantischer
     * Strahl, 150 Schaden mit Abfall im Umkreis von 50 Bloecken und - mit {@code blocks} - ein kugelfoermiger Krater mit
     * 50 Bloecken Radius in jede Richtung (ueber ~2 s ausgehoben, ohne Drops).
     */
    public static void cataclysm(ServerLevel level, Vec3 pos, @Nullable Entity owner, boolean blocks) {
        spawn(level, pos, OrbitalBeamEntity.CATACLYSM, 100, 0.0F, 150.0F, blocks, owner);
    }

    /** Tempest Barrage: zwoelf kleinere Blitzstrahlen verteilt auf 14 Bloecke, versetzt ueber 2,5 s. */
    public static void storm(ServerLevel level, Vec3 pos, @Nullable Entity owner, boolean blocks) {
        RandomSource r = level.random;
        spawn(level, pos, OrbitalBeamEntity.STORM, 20, 3.5F, 12.0F, blocks, owner);
        for (int i = 0; i < 11; i++) {
            double a = r.nextDouble() * Math.PI * 2, d = 3 + r.nextDouble() * 11;
            spawn(level, ground(level, pos.x + Math.cos(a) * d, pos.y, pos.z + Math.sin(a) * d), OrbitalBeamEntity.STORM,
                    20 + i * 4 + r.nextInt(4), 3.0F, 11.0F, blocks, owner);
        }
    }

    /** Gilded Rain: goldene Einschlaege marschieren in einer Linie vom Schuetzen zum Ziel (Teppich). */
    public static void line(ServerLevel level, Vec3 from, Vec3 to, @Nullable Entity owner, boolean blocks) {
        Vec3 dir = to.subtract(from).multiply(1, 0, 1);
        double len = dir.length();
        if (len < 1) {
            spawn(level, to, OrbitalBeamEntity.LINE, 16, 3.5F, 12.0F, blocks, owner);
            return;
        }
        dir = dir.normalize();
        int steps = 12;
        double start = Math.min(6, len * 0.3), span = len - start + 6;
        for (int i = 0; i < steps; i++) {
            double d = start + span * i / (steps - 1);
            Vec3 p = ground(level, from.x + dir.x * d, to.y, from.z + dir.z * d);
            spawn(level, p, OrbitalBeamEntity.LINE, 14 + i * 3, 3.5F, 12.0F, blocks, owner);
        }
    }

    /** Singularity: 3 s Sog (Radius 16), dann Implosion, die alles nach aussen schleudert. Kein Blockschaden. */
    public static void singularity(ServerLevel level, Vec3 pos, @Nullable Entity owner) {
        spawn(level, pos, OrbitalBeamEntity.VORTEX, 60, 5.0F, 22.0F, false, owner);
    }

    /** Judgement: schneller, praeziser Strahl ohne Explosion und Blockschaden, hoher Einzelschaden. */
    public static void precision(ServerLevel level, Vec3 pos, @Nullable Entity owner) {
        spawn(level, pos, OrbitalBeamEntity.PRECISION, 14, 0.0F, 40.0F, false, owner);
    }

    /** Bossvariante der Lanze (Raserei): gleicher Effekt, weniger Schaden, keine Bloecke, lange Vorwarnung. */
    public static void bossLance(ServerLevel level, Vec3 pos, Entity owner) {
        spawn(level, pos, OrbitalBeamEntity.LANCE, 60, 0.0F, 18.0F, false, owner);
    }

    /** Nur Effekt (kein Schaden, keine Explosion), z. B. der Todesstrahl des Bosses. */
    public static void cosmetic(ServerLevel level, Vec3 pos, int kind, int warmup) {
        spawn(level, pos, kind, warmup, 0.0F, 0.0F, false, null);
    }

    /** Ring aus {@code count} Bossschlaegen um {@code center} (Radius {@code radius}), je Strahl um {@code stagger} versetzt. */
    public static void ring(ServerLevel level, Vec3 center, Entity owner, int count, double radius, int warmup, int stagger) {
        for (int i = 0; i < count; i++) {
            double a = i * Math.PI * 2 / count;
            spawn(level, ground(level, center.x + Math.cos(a) * radius, center.y, center.z + Math.sin(a) * radius), OrbitalBeamEntity.BOSS,
                    warmup + i * stagger, 1.5F, 12.0F, false, owner);
        }
    }

    /** Bosschlag: mittlerer lila Strahl ohne Blockschaden. */
    public static void boss(ServerLevel level, Vec3 pos, Entity owner, int warmup) {
        spawn(level, pos, OrbitalBeamEntity.BOSS, Math.max(1, warmup), 2.5F, 14.0F, false, owner);
    }
}
