package com.space_beyond.entity.mob.stage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.space_m.SpaceMod;
import com.space_beyond.entity.mob.stage.EntityStageBoss;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer eines Stufen-Bosses: Modell aus {@link StageBossModels}, Textur + Leuchtschicht, eigene Groesse; solange
 * der Schild steht, laeuft ein blauer Energiewirbel ueber das Modell (wie beim geladenen Creeper).
 */
public class RenderStageBoss extends MobRenderer<EntityStageBoss, StageBossModel<EntityStageBoss>> {

    private static final ResourceLocation SWIRL = ResourceLocation.tryParse("minecraft:textures/entity/creeper/creeper_armor.png");

    private final ResourceLocation texture;
    private final float scale;

    /** Ohne Schluesselposen (Grundbewegung nach Rollen). */
    public RenderStageBoss(EntityRendererProvider.Context ctx, String name, LayerDefinition layer, StageBossModel.Role[] roles, float scale) {
        this(ctx, name, layer, roles, new StageBossModel.Key[0], scale);
    }

    public RenderStageBoss(EntityRendererProvider.Context ctx, String name, LayerDefinition layer, StageBossModel.Role[] roles, StageBossModel.Key[] keys,
                           float scale) {
        super(ctx, new StageBossModel<>(layer.bakeRoot(), roles, keys), 0.8F * scale);
        this.texture = com.space_beyond.BeyondMod.id("textures/entity/stage/" + name + ".png");
        this.scale = scale;
        ResourceLocation glow = com.space_beyond.BeyondMod.id("textures/entity/stage/" + name + "_glow.png");
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(glow);
            }
        });
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack ps, MultiBufferSource buffers, int light, EntityStageBoss e, float limbSwing, float limbAmount, float pt,
                               float age, float yaw, float pitch) {
                if (!e.shielded()) return;
                float t = (e.tickCount + pt) * 0.01F;
                VertexConsumer vc = buffers.getBuffer(RenderType.energySwirl(SWIRL, t % 1.0F, t * 0.7F % 1.0F));
                //? if < 1.21.1 {
                getParentModel().renderToBuffer(ps, vc, 0xF000F0, OverlayTexture.NO_OVERLAY, 0.45F, 0.65F, 1.0F, 1.0F);
                //?} else {
                /*getParentModel().renderToBuffer(ps, vc, 0xF000F0, OverlayTexture.NO_OVERLAY, 0xFF73A6FF);
                *///?}
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EntityStageBoss entity) {
        return texture;
    }

    @Override
    protected void scale(EntityStageBoss e, PoseStack ps, float pt) {
        ps.scale(scale, scale, scale);
    }
}
