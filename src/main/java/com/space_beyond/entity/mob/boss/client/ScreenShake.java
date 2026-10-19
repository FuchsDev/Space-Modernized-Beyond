package com.space_beyond.entity.mob.boss.client;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Kamerawackeln bei Orbitaleinschlaegen und Boss-Angriffen: kleine, abklingende Zufallsdrehung des Blickwinkels
 * (der vorherige Versatz wird jeden Tick zurueckgenommen, das Ziel driftet also nicht weg).
 */
public final class ScreenShake {

    private ScreenShake() {}

    private static float intensity;
    private static float lastYaw, lastPitch;
    private static final RandomSource RAND = RandomSource.create();
    private static boolean registered;

    public static void init() {
        if (registered) return;
        registered = true;
        ClientTickEvent.CLIENT_POST.register(mc -> tick());
    }

    /** Einschlag bei {@code pos}: Staerke {@code strength} (Grad), faellt bis {@code range} Bloecke auf 0. */
    public static void impact(Vec3 pos, float strength, double range) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        double d = player.position().distanceTo(pos);
        if (d > range) return;
        intensity = Math.max(intensity, strength * (float) (1.0D - d / range));
    }

    private static void tick() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            intensity = 0;
            return;
        }
        float yaw = 0, pitch = 0;
        if (intensity > 0.02F) {
            yaw = (RAND.nextFloat() - 0.5F) * 2.0F * intensity;
            pitch = (RAND.nextFloat() - 0.5F) * 2.0F * intensity;
            intensity *= 0.86F;
        } else {
            intensity = 0;
        }
        player.setYRot(player.getYRot() - lastYaw + yaw);
        player.setXRot(Mth.clamp(player.getXRot() - lastPitch + pitch, -90.0F, 90.0F));
        lastYaw = yaw;
        lastPitch = pitch;
    }
}
