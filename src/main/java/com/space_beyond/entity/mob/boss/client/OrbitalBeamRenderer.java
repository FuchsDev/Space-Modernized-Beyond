package com.space_beyond.entity.mob.boss.client;

import org.joml.Matrix4f;

import com.hbm_m.platform.RenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_m.SpaceMod;
import com.space_beyond.client.compat.ShaderCompat;
import com.space_beyond.entity.mob.boss.OrbitalBeamEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Darstellung eines Orbitalschlags ({@link OrbitalBeamEntity}), komplett eigen gezeichnet:
 * <ul>
 *   <li><b>Vorwarnung</b>: zwei gegenlaeufig rotierende Runenkreise am Boden (Textur {@code effect/orbital_ring}), die
 *       sich zusammenziehen, und ein Zielstrahl aus dem Orbit, der dicker und heller wird.</li>
 *   <li><b>Einschlag</b>: Saeulenstrahl aus drei Schichten (lila Mantel, magenta/goldene Mitte, weisser Kern,
 *       Textur {@code effect/orbital_beam} mit laufender Struktur), verzweigte Blitze aus dem Himmel (prozedural,
 *       flackernd, Lila/Gold/Weiss, Lanze mit Seitenaesten), drei sich ausbreitende Schockwellenringe und ein
 *       Bodenblitz.</li>
 * </ul>
 * Alles additiv (Augen-/Blitz-Renderart), leuchtet unabhaengig vom Licht.
 */
public class OrbitalBeamRenderer extends EntityRenderer<OrbitalBeamEntity> {

    public static final ResourceLocation BEAM = com.space_beyond.BeyondMod.id("textures/effect/orbital_beam.png");
    public static final ResourceLocation RING = com.space_beyond.BeyondMod.id("textures/effect/orbital_ring.png");
    /** Weisse Textur fuer Blitze/Schockwellen: additive Augen-Renderart statt Blitz-Renderart, weil Shaderpacks
     *  (Iris/Oculus) Augen als leuchtend behandeln, eigene Blitz-Geometrie dagegen wie normale Entities beleuchten. */
    public static final ResourceLocation WHITE = com.space_beyond.BeyondMod.id("textures/effect/white.png");
    private static final int LIGHT = 0xF000F0;

    public OrbitalBeamRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0;
    }

    @Override
    public ResourceLocation getTextureLocation(OrbitalBeamEntity entity) {
        return BEAM;
    }

    @Override
    public boolean shouldRender(OrbitalBeamEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(OrbitalBeamEntity e, float yaw, float pt, PoseStack ps, MultiBufferSource buffers, int light) {
        if (ShaderCompat.shadowPass()) return; // Licht wirft keinen Schatten
        float t = e.tickCount + pt;
        int warm = e.warmup();
        float R = e.radius();
        float gold = e.goldness();
        int kind = e.kind();
        boolean mega = kind == OrbitalBeamEntity.CATACLYSM;
        if (t < warm) {
            float p = t / warm;
            float ringR = R * (3.0F - p * 1.6F);
            ring(ps, buffers, ringR, t * 2.5F, 0.08F, mix(0.7F, 1.0F, gold), mix(0.3F, 0.75F, gold), mix(1.0F, 0.25F, gold), 0.55F + p * 0.45F);
            ring(ps, buffers, ringR * 0.62F, -t * 4.0F, 0.1F, 1.0F, 0.85F, 0.35F, 0.4F + p * 0.6F);
            if (kind == OrbitalBeamEntity.VORTEX) ring(ps, buffers, ringR * 1.6F, t * 6.0F, 0.12F, 0.45F, 0.15F, 0.8F, 0.6F);
            float w = 0.04F + p * p * R * 0.25F;
            beam(ps, buffers, w, 160.0F, t, 0.85F, 0.45F, 1.0F, 0.35F + p * 0.65F, 8);
            beam(ps, buffers, w * 0.4F, 160.0F, t, 1.0F, 1.0F, 1.0F, p, 6);
            return;
        }
        float age = t - warm;
        float fade = e.fade(pt);
        float boom = age < 4 ? 1.0F + (4 - age) * 0.35F : 1.0F;
        float r = R * fade * boom;
        float h = 200.0F;
        // Saeule: Mantel, Mitte, Kern
        beam(ps, buffers, r * 1.7F, h, t, 0.55F, 0.18F, 0.95F, 0.45F * fade, 20);
        beam(ps, buffers, r, h, t * 1.6F, mix(0.9F, 1.0F, gold), mix(0.35F, 0.78F, gold), mix(1.0F, 0.3F, gold), 0.9F * fade, 18);
        beam(ps, buffers, r * 0.38F, h, t * 2.4F, 1.0F, 1.0F, 1.0F, fade, 12);
        // Runenkreis am Boden bleibt kurz und dreht schnell
        ring(ps, buffers, R * 2.2F * (0.6F + 0.4F * fade), t * 8.0F, 0.06F, 1.0F, 0.85F, 0.35F, fade);
        // Blitze aus dem Himmel (flackern alle 2 Ticks neu)
        if (age < e.linger() * 0.6F) {
            int bolts = mega ? 34 : (mega || kind == OrbitalBeamEntity.LANCE) ? 14 : kind == OrbitalBeamEntity.VORTEX ? 8 : kind == OrbitalBeamEntity.PRECISION ? 2 : 4;
            RandomSource rand = RandomSource.create(e.getId() * 31L + (long) (age / 2));
            VertexConsumer vc = buffers.getBuffer(RenderType.eyes(WHITE));
            Matrix4f m = ps.last().pose();
            for (int i = 0; i < bolts; i++) {
                boolean g = rand.nextFloat() < gold;
                float cr = g ? 1.0F : 0.82F, cg = g ? 0.8F : 0.42F, cb = g ? 0.3F : 1.0F;
                double a = rand.nextDouble() * Math.PI * 2;
                float spread = R * ((mega || kind == OrbitalBeamEntity.LANCE) ? 5.0F : 2.5F);
                float x0 = (float) Math.cos(a) * spread * rand.nextFloat(), z0 = (float) Math.sin(a) * spread * rand.nextFloat();
                float top = 60 + rand.nextFloat() * 60;
                float width = ((mega || kind == OrbitalBeamEntity.LANCE) ? 0.55F : 0.25F) * fade + 0.05F;
                bolt(vc, m, rand, x0, top, z0, 0, 0, 0, width, cr, cg, cb, 0.9F * fade, (mega || kind == OrbitalBeamEntity.LANCE) ? 3 : 1);
            }
            // Bodenblitze: Risse, die vom Einschlag nach aussen laufen
            int cracks = (mega || kind == OrbitalBeamEntity.LANCE) ? 10 : 4;
            for (int i = 0; i < cracks; i++) {
                double a = rand.nextDouble() * Math.PI * 2;
                float len = R * (2.0F + rand.nextFloat() * 3.0F) * Math.min(1.0F, age / 6.0F + 0.3F);
                bolt(vc, m, rand, 0, 0.15F, 0, (float) Math.cos(a) * len, 0.15F, (float) Math.sin(a) * len, 0.12F * fade + 0.02F,
                        1.0F, 0.85F, 0.4F, 0.8F * fade, 0);
            }
        }
        // Schockwellen
        float speed = mega ? 0.9F : kind == OrbitalBeamEntity.LANCE ? 1.1F : 0.55F;
        for (int k = 0; k < 3; k++) {
            float a2 = age - k * 5;
            if (a2 <= 0) continue;
            float rr = a2 * speed * ((mega || kind == OrbitalBeamEntity.LANCE) ? 1.6F : 1.0F) + R;
            float alpha = Mth.clamp(1.0F - a2 / (e.linger() * 0.8F), 0F, 1F);
            if (alpha <= 0) continue;
            shockwave(ps, buffers, rr, (mega || kind == OrbitalBeamEntity.LANCE) ? 1.4F : 0.7F, k == 1 ? 1.0F : 0.8F, k == 1 ? 0.8F : 0.4F, k == 1 ? 0.3F : 1.0F, alpha);
        }
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static int c(float v) {
        return Mth.clamp((int) (v * 255), 0, 255);
    }

    /** Zylinderstrahl (Augen-Renderart, additiv), Textur laeuft mit {@code scroll} nach unten. */
    private static void beam(PoseStack ps, MultiBufferSource buffers, float radius, float height, float scroll,
                             float r, float g, float b, float a, int seg) {
        if (radius <= 0.005F || a <= 0.01F) return;
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(BEAM));
        Matrix4f m = ps.last().pose();
        float v0 = -scroll * 0.06F, v1 = v0 + height / 6.0F;
        int R = c(r * a), G = c(g * a), B = c(b * a), A = c(a);
        for (int i = 0; i < seg; i++) {
            float a0 = Mth.TWO_PI * i / seg + scroll * 0.03F, a1 = Mth.TWO_PI * (i + 1) / seg + scroll * 0.03F;
            float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius, x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;
            float u0 = (float) i / seg, u1 = (float) (i + 1) / seg;
            quad(vc, m, x0, 0, z0, x1, 0, z1, x1, height, z1, x0, height, z0, u0, u1, v1, v0, R, G, B, A);
        }
    }

    /** Doppelseitiges Viereck mit Textur. */
    private static void quad(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3, float u0, float u1, float vb, float vt,
                             int r, int g, int b, int a) {
        int ov = OverlayTexture.NO_OVERLAY;
        RenderHooks.vertexFull(vc, m, x0, y0, z0, r, g, b, a, u0, vb, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x1, y1, z1, r, g, b, a, u1, vb, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x2, y2, z2, r, g, b, a, u1, vt, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x3, y3, z3, r, g, b, a, u0, vt, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x3, y3, z3, r, g, b, a, u0, vt, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x2, y2, z2, r, g, b, a, u1, vt, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x1, y1, z1, r, g, b, a, u1, vb, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x0, y0, z0, r, g, b, a, u0, vb, ov, LIGHT, 0, 1, 0);
    }

    /** Liegender, rotierender Runenkreis. */
    private static void ring(PoseStack ps, MultiBufferSource buffers, float radius, float rotDeg, float y, float r, float g, float b, float a) {
        if (radius <= 0.05F) return;
        ps.pushPose();
        ps.translate(0, y, 0);
        ps.mulPose(Axis.YP.rotationDegrees(rotDeg));
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(RING));
        Matrix4f m = ps.last().pose();
        quad(vc, m, -radius, 0, -radius, radius, 0, -radius, radius, 0, radius, -radius, 0, radius, 0, 1, 0, 1,
                c(r * a), c(g * a), c(b * a), c(a));
        ps.popPose();
    }

    /** Flacher Schockwellenring (Blitz-Renderart). */
    private static void shockwave(PoseStack ps, MultiBufferSource buffers, float radius, float width, float r, float g, float b, float a) {
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(WHITE));
        Matrix4f m = ps.last().pose();
        int seg = Math.max(24, (int) (radius * 3));
        int R = c(r), G = c(g), B = c(b), A = c(a * 0.8F);
        float ri = Math.max(0, radius - width), ro = radius + width * 0.3F;
        for (int i = 0; i < seg; i++) {
            float a0 = Mth.TWO_PI * i / seg, a1 = Mth.TWO_PI * (i + 1) / seg;
            float y = 0.12F + Mth.sin(radius * 0.5F + a0 * 3) * 0.05F;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            // innen transparent -> Kante hell -> aussen transparent (leicht nach oben gewoelbt)
            colorQuad(vc, m, c0 * ri, y, s0 * ri, c1 * ri, y, s1 * ri, c1 * radius, y, s1 * radius, c0 * radius, y, s0 * radius,
                    R, G, B, 0, 0, A, A);
            colorQuad(vc, m, c0 * radius, y, s0 * radius, c1 * radius, y, s1 * radius, c1 * ro, y + 0.3F, s1 * ro, c0 * ro, y + 0.3F, s0 * ro,
                    R, G, B, A, A, 0, 0);
        }
    }

    /** Farbviereck in beiden Windungen, additiv: Farbe wird mit dem Alpha je Ecke vormultipliziert. */
    public static void colorQuad(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                          float x2, float y2, float z2, float x3, float y3, float z3,
                          int r, int g, int b, int a0, int a1, int a2, int a3) {
        v(vc, m, x0, y0, z0, r, g, b, a0);
        v(vc, m, x1, y1, z1, r, g, b, a1);
        v(vc, m, x2, y2, z2, r, g, b, a2);
        v(vc, m, x3, y3, z3, r, g, b, a3);
        v(vc, m, x3, y3, z3, r, g, b, a3);
        v(vc, m, x2, y2, z2, r, g, b, a2);
        v(vc, m, x1, y1, z1, r, g, b, a1);
        v(vc, m, x0, y0, z0, r, g, b, a0);
    }

    private static void v(VertexConsumer vc, Matrix4f m, float x, float y, float z, int r, int g, int b, int a) {
        RenderHooks.vertexFull(vc, m, x, y, z, r * a / 255, g * a / 255, b * a / 255, a, 0.5F, 0.5F, OverlayTexture.NO_OVERLAY, LIGHT, 0, 1, 0);
    }

    /**
     * Verzweigter Blitz von (x0, y0, z0) nach (x1, y1, z1): Zickzack mit abnehmender Breite, je Segment zwei sich
     * kreuzende Baender (sieht aus jeder Richtung voll aus), {@code branches} Seitenaeste.
     */
    private static void bolt(VertexConsumer vc, Matrix4f m, RandomSource rand, float x0, float y0, float z0, float x1, float y1, float z1,
                             float width, float r, float g, float b, float a, int branches) {
        int segs = 14;
        float px = x0, py = y0, pz = z0;
        float jag = Math.max(Math.abs(y0 - y1), Mth.sqrt((x1 - x0) * (x1 - x0) + (z1 - z0) * (z1 - z0))) * 0.06F;
        for (int i = 1; i <= segs; i++) {
            float f = (float) i / segs;
            float nx = Mth.lerp(f, x0, x1) + (i < segs ? (rand.nextFloat() - 0.5F) * jag * 2 : 0);
            float ny = Mth.lerp(f, y0, y1);
            float nz = Mth.lerp(f, z0, z1) + (i < segs ? (rand.nextFloat() - 0.5F) * jag * 2 : 0);
            float w = width * (1.0F - f * 0.5F);
            segment(vc, m, px, py, pz, nx, ny, nz, w, r, g, b, a);
            segment(vc, m, px, py, pz, nx, ny, nz, w * 0.35F, 1.0F, 1.0F, 1.0F, a);
            if (branches > 0 && i < segs - 2 && rand.nextFloat() < 0.18F) {
                float bl = (y0 - y1) * 0.25F + jag * 4;
                bolt(vc, m, rand, nx, ny, nz, nx + (rand.nextFloat() - 0.5F) * bl, ny - bl * (0.4F + rand.nextFloat() * 0.6F) * (y0 == y1 ? 0 : 1),
                        nz + (rand.nextFloat() - 0.5F) * bl, w * 0.5F, r, g, b, a * 0.8F, branches - 1);
            }
            px = nx;
            py = ny;
            pz = nz;
        }
    }

    private static void segment(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                                float w, float r, float g, float b, float a) {
        int R = c(r), G = c(g), B = c(b), A = c(a);
        // zwei gekreuzte Baender (x- und z-Ausrichtung)
        colorQuad(vc, m, x0 - w, y0, z0, x0 + w, y0, z0, x1 + w, y1, z1, x1 - w, y1, z1, R, G, B, A, A, A, A);
        colorQuad(vc, m, x0, y0, z0 - w, x0, y0, z0 + w, x1, y1, z1 + w, x1, y1, z1 - w, R, G, B, A, A, A, A);
        // horizontale Segmente (Bodenrisse): zusaetzlich ein stehendes Band
        if (Math.abs(y1 - y0) < 0.01F) colorQuad(vc, m, x0, y0 - w, z0, x0, y0 + w, z0, x1, y1 + w, z1, x1, y1 - w, z1, R, G, B, A, A, A, A);
    }
}
