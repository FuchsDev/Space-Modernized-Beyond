package com.space_beyond.lifesupport;

import com.space_m.lifesupport.BlockAirScrubber;
import com.space_m.lifesupport.RenderAirScrubber;
import com.space_m.lifesupport.TileEntityAirScrubber;

import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.space_m.blockentity.SpaceBlockEntities;
import com.space_beyond.client.render.ObjAtlasMesh;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Luftwaescher auf Raptors Renderpfad, 1:1 aus {@link RenderAirScrubber}: T(0.5, 0, 0.5), R(180), Ausrichtung
 * (Nord 0, West 90, Sued 180, Ost 270); {@code Base} starr, {@code Fan} dreht um die Hochachse.
 * Kein getViewDistance im Original-BER -> Vanilla-Standard 64.
 */
public final class LifeSupportRaptorRenders {

    private LifeSupportRaptorRenders() {}

    private static final ObjAtlasMesh BASE = ObjAtlasMesh.parts(RenderAirScrubber.MODEL, RenderAirScrubber.TEX, "space_m/air_scrubber/base", "Base");
    private static final ObjAtlasMesh FAN = ObjAtlasMesh.parts(RenderAirScrubber.MODEL, RenderAirScrubber.TEX, "space_m/air_scrubber/fan", "Fan");

    public static void register() {
        MachineRenderers.machine("space_m/air_scrubber", SpaceBlockEntities.AIR_SCRUBBER.get(), TileEntityAirScrubber.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(180, 0, 1, 0);
                    BlockState state = be.getBlockState();
                    if (state.hasProperty(BlockAirScrubber.FACING)) {
                        switch (state.getValue(BlockAirScrubber.FACING)) {
                            case WEST -> a.rotate(90, 0, 1, 0);
                            case SOUTH -> a.rotate(180, 0, 1, 0);
                            case EAST -> a.rotate(270, 0, 1, 0);
                            default -> { }
                        }
                    }
                })
                .dynamicPart("base", be -> BASE.quads(), be -> BASE.cacheKey())
                .dynamicPart("fan", LifeSupportRaptorRenders::animateFan, be -> FAN.quads(), be -> FAN.cacheKey())
                .viewDistance(64)
                .register();
    }

    private static boolean animateFan(TileEntityAirScrubber scrubber, float interp, long gameTime, PoseStack ps) {
        float rotation = scrubber.prevRot + (scrubber.rot - scrubber.prevRot) * interp;
        ps.mulPose(Axis.YP.rotationDegrees(rotation));
        return true;
    }
}
