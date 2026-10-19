package com.space_beyond.entity.mob.stage.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_beyond.client.compat.ShaderCompat;
import com.space_beyond.entity.mob.boss.client.OrbitalBeamRenderer;
import com.space_beyond.entity.mob.stage.EntityBossOrb;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

/** Boss-Geschoss: zwei ineinander rotierende, leuchtende Oktaeder in der Farbe des Bosses (additiv). */
public class BossOrbRenderer extends EntityRenderer<EntityBossOrb> {

    public BossOrbRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBossOrb entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(EntityBossOrb e, float yaw, float pt, PoseStack ps, MultiBufferSource buffers, int light) {
        if (ShaderCompat.shadowPass()) return;
        float t = e.tickCount + pt;
        float s = e.size() * (1.0F + Mth.sin(t * 0.7F) * 0.12F);
        int c = e.color();
        int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(OrbitalBeamRenderer.WHITE));
        ps.pushPose();
        ps.translate(0, e.getBbHeight() / 2, 0);
        ps.mulPose(Axis.YP.rotationDegrees(t * 14));
        ps.mulPose(Axis.XP.rotationDegrees(t * 9));
        octa(vc, ps.last().pose(), s, r, g, b, 200);
        ps.mulPose(Axis.ZP.rotationDegrees(45));
        octa(vc, ps.last().pose(), s * 0.55F, 255, 255, 255, 230);
        ps.popPose();
        super.render(e, yaw, pt, ps, buffers, light);
    }

    private static void octa(VertexConsumer vc, Matrix4f m, float s, int r, int g, int b, int a) {
        float[][] v = {{s, 0, 0}, {0, 0, s}, {-s, 0, 0}, {0, 0, -s}};
        for (int i = 0; i < 4; i++) {
            float[] p0 = v[i], p1 = v[(i + 1) % 4];
            for (int sgn = -1; sgn <= 1; sgn += 2) {
                OrbitalBeamRenderer.colorQuad(vc, m, 0, s * sgn, 0, p0[0], p0[1], p0[2], p1[0], p1[1], p1[2], p1[0], p1[1], p1[2], r, g, b, a, a, a, a);
            }
        }
    }
}
