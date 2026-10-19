package com.space_beyond.entity.mob.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.space_beyond.entity.mob.infected.EntityInfected;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer der Infizierten: Vanilla-Zombiemodell (inkl. Ruestungsebenen) mit eigener Textur je Stadium und
 * optionalem Groessenfaktor (Pilzkoloss 1,35).
 */
public class RenderInfected extends AbstractZombieRenderer<EntityInfected, ZombieModel<EntityInfected>> {

    private final ResourceLocation texture;
    private final float scale;

    public RenderInfected(EntityRendererProvider.Context ctx, ResourceLocation texture, float scale) {
        super(ctx, new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)));
        this.texture = texture;
        this.scale = scale;
        this.shadowRadius = 0.5F * scale;
    }

    @Override
    public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Zombie entity) {
        return texture;
    }

    @Override
    protected void scale(EntityInfected entity, PoseStack poseStack, float partialTick) {
        if (scale != 1.0F) poseStack.scale(scale, scale, scale);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    protected boolean isShaking(EntityInfected entity) {
        return false;
    }
}
