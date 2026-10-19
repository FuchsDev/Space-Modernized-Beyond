package com.space_beyond.progression.fx.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_beyond.client.compat.ShaderCompat;
import com.space_beyond.entity.mob.boss.client.OrbitalBeamRenderer;
import com.space_beyond.progression.fx.SlashEffect;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Zeichnet {@link SlashEffect}: Schwungbogen als weiches Leuchtband (innen/aussen auslaufend, weisser Kern), das in
 * der ersten Haelfte durchschwingt und dann verblasst; Ring als sich ausbreitendes Band am Boden. Additiv (Augen-
 * Renderart), leuchtet im Dunkeln, kein Schatten im Shader-Schattenpass.
 */
public class SlashRenderer extends EntityRenderer<SlashEffect> {

    private static final int SEGMENTS = 18;

    public SlashRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0;
    }

    @Override
    public ResourceLocation getTextureLocation(SlashEffect entity) {
        return OrbitalBeamRenderer.WHITE;
    }

    @Override
    public boolean shouldRender(SlashEffect entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(SlashEffect e, float yaw, float pt, PoseStack ps, MultiBufferSource buffers, int light) {
        if (ShaderCompat.shadowPass()) return;
        float age = e.tickCount + pt, life = e.life();
        float p = Mth.clamp(age / life, 0F, 1F);
        int c = e.color();
        int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(OrbitalBeamRenderer.WHITE));
        ps.pushPose();
        if (e.mode() == SlashEffect.RING) {
            float rad = e.size() * (0.25F + 0.75F * (1 - (1 - p) * (1 - p)));
            int a = (int) (220 * (1 - p));
            band(vc, ps.last().pose(), -180, 180, rad - 0.6F, rad, rad + 0.6F, r, g, b, a, true);
        } else {
            ps.mulPose(Axis.YP.rotationDegrees(-e.getYRot()));
            ps.mulPose(Axis.XP.rotationDegrees(e.getXRot()));
            ps.mulPose(Axis.ZP.rotationDegrees(e.roll()));
            float s = e.size();
            float sweep = Math.min(1F, p * 2.2F);
            float from = -80F, to = -80F + 160F * sweep;
            int a = (int) (235 * Math.pow(1 - p, 1.3));
            band(vc, ps.last().pose(), from, to, 0.75F * s, 1.25F * s, 1.75F * s, r, g, b, a, false);
            band(vc, ps.last().pose(), from, to, 1.15F * s, 1.25F * s, 1.35F * s, 255, 255, 255, a, false);
        }
        ps.popPose();
    }

    /**
     * Bogenband von {@code a0} bis {@code a1} Grad: innen {@code r0} (durchsichtig) -> Mitte {@code rm} (Farbe) ->
     * aussen {@code r1} (durchsichtig). {@code flat}: Ring in der Bodenebene (x/z), sonst Bogen in der lokalen x/z-
     * Ebene vor dem Angreifer (+z = Blickrichtung); das Ende des Bogens (Klingenspitze) ist am hellsten.
     */
    private static void band(VertexConsumer vc, Matrix4f m, float a0, float a1, float r0, float rm, float r1, int r, int g, int b, int alpha,
                             boolean flat) {
        if (a1 <= a0 || alpha <= 0) return;
        for (int i = 0; i < SEGMENTS; i++) {
            float t0 = i / (float) SEGMENTS, t1 = (i + 1) / (float) SEGMENTS;
            float d0 = (a0 + (a1 - a0) * t0) * Mth.DEG_TO_RAD, d1 = (a0 + (a1 - a0) * t1) * Mth.DEG_TO_RAD;
            float s0 = Mth.sin(d0), c0 = Mth.cos(d0), s1 = Mth.sin(d1), c1 = Mth.cos(d1);
            int al0 = flat ? alpha : (int) (alpha * (0.15F + 0.85F * t0)), al1 = flat ? alpha : (int) (alpha * (0.15F + 0.85F * t1));
            OrbitalBeamRenderer.colorQuad(vc, m, s0 * r0, 0, c0 * r0, s1 * r0, 0, c1 * r0, s1 * rm, 0, c1 * rm, s0 * rm, 0, c0 * rm,
                    r, g, b, 0, 0, al1, al0);
            OrbitalBeamRenderer.colorQuad(vc, m, s0 * rm, 0, c0 * rm, s1 * rm, 0, c1 * rm, s1 * r1, 0, c1 * r1, s0 * r1, 0, c0 * r1,
                    r, g, b, al0, al1, 0, 0);
        }
    }
}
