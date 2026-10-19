package com.space_beyond.entity.mob.boss.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_beyond.client.compat.ShaderCompat;
import com.space_beyond.entity.mob.boss.EntityCrystalShard;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.util.Mth;

/**
 * Kristallsplitter des Bosses (additiv, leuchtet auch mit Shadern): zwei ineinanderliegende, gestreckte Doppelpyramiden (aussen Lila bzw. Gold, innen
 * weisser Kern), additiv, rotieren um die Flugachse; pulsieren leicht.
 */
public class CrystalShardRenderer extends EntityRenderer<EntityCrystalShard> {

    public CrystalShardRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCrystalShard entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(EntityCrystalShard e, float yaw, float pt, PoseStack ps, MultiBufferSource buffers, int light) {
        if (ShaderCompat.shadowPass()) return;
        float t = e.tickCount + pt;
        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pt, e.yRotO, e.getYRot())));
        ps.mulPose(Axis.XP.rotationDegrees(90.0F - Mth.lerp(pt, e.xRotO, e.getXRot())));
        ps.mulPose(Axis.YP.rotationDegrees(t * 18.0F));
        float pulse = 1.0F + Mth.sin(t * 0.6F) * 0.12F;
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(OrbitalBeamRenderer.WHITE));
        Matrix4f m = ps.last().pose();
        boolean gold = e.gold();
        int r = gold ? 255 : 190, g = gold ? 190 : 70, b = gold ? 60 : 255;
        crystal(vc, m, 0.28F * pulse, 0.85F, r, g, b, 200);
        crystal(vc, m, 0.12F * pulse, 0.6F, 255, 255, 255, 230);
        ps.popPose();
        super.render(e, yaw, pt, ps, buffers, light);
    }

    /** Doppelpyramide mit sechs Seiten entlang y. */
    private static void crystal(VertexConsumer vc, Matrix4f m, float w, float h, int r, int g, int b, int a) {
        int sides = 6;
        for (int i = 0; i < sides; i++) {
            float a0 = Mth.TWO_PI * i / sides, a1 = Mth.TWO_PI * (i + 1) / sides;
            float x0 = Mth.cos(a0) * w, z0 = Mth.sin(a0) * w, x1 = Mth.cos(a1) * w, z1 = Mth.sin(a1) * w;
            tri(vc, m, 0, h, 0, x0, 0, z0, x1, 0, z1, r, g, b, a);
            tri(vc, m, 0, -h * 0.55F, 0, x1, 0, z1, x0, 0, z0, r, g, b, a / 2);
        }
    }

    private static void tri(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                            float x2, float y2, float z2, int r, int g, int b, int a) {
        // als entartetes Viereck, beide Windungen
        OrbitalBeamRenderer.colorQuad(vc, m, x0, y0, z0, x1, y1, z1, x2, y2, z2, x2, y2, z2, r, g, b, a, a, a, a);
    }
}
