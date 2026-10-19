package com.space_beyond.entity.mob.dungeon.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.space_m.SpaceMod;
import com.space_beyond.entity.mob.dungeon.EntityDungeonMob;
import com.space_beyond.entity.mob.stage.client.StageBossModel;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer der Dungeon-Mobs: Modell aus {@link DungeonMobModels} (Posen-Modell der Bosse), Textur und Leuchtschicht
 * unter {@code textures/entity/dungeon/}, eigene Groesse; Teil-Kopien kleiner.
 */
public class RenderDungeonMob extends MobRenderer<EntityDungeonMob, StageBossModel<EntityDungeonMob>> {

    private final ResourceLocation texture;
    private final float scale;

    public RenderDungeonMob(EntityRendererProvider.Context ctx, String name, LayerDefinition layer, StageBossModel.Role[] roles, StageBossModel.Key[] keys,
                            float scale) {
        super(ctx, new StageBossModel<>(layer.bakeRoot(), roles, keys), 0.45F * scale);
        this.texture = com.space_beyond.BeyondMod.id("textures/entity/dungeon/" + name + ".png");
        this.scale = scale;
        ResourceLocation glow = com.space_beyond.BeyondMod.id("textures/entity/dungeon/" + name + "_glow.png");
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(glow);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDungeonMob entity) {
        return texture;
    }

    @Override
    protected void scale(EntityDungeonMob e, PoseStack ps, float pt) {
        float s = scale;
        //? if < 1.21.1 {
        if (e.isSmall()) s *= EntityDungeonMob.SMALL_SCALE; // ab 1.21 ueber das Attribut SCALE
        //?}
        ps.scale(s, s, s);
    }
}
