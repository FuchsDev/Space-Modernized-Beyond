package com.space_beyond.machine.atmo.client;

import com.space_m.machine.atmo.client.AtmosphericCompressorRenderer;
import com.space_m.machine.atmo.client.AtmosphericEmitterRenderer;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.space_m.blockentity.SpaceBlockEntities;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.machine.atmo.AtmosphericCompressorBlockEntity;
import com.space_m.machine.atmo.AtmosphericEmitterBlockEntity;

/**
 * Atmosphaeren-Verdichter und -Auslass auf Raptors Renderpfad. Transformationen 1:1 aus
 * {@link AtmosphericCompressorRenderer} und {@link AtmosphericEmitterRenderer}; Gehaeuse starr, Luefter als bewegtes
 * Teil. Ohne Rueckseitenausblendung wie im Original (der VBO-Pfad zeichnet ohnehin beidseitig).
 */
public final class AtmoRaptorRenders {

    private AtmoRaptorRenders() {}

    private static final ObjAtlasMesh VENT_BODY =
            ObjAtlasMesh.parts(AtmosphericCompressorRenderer.MODEL, AtmosphericCompressorRenderer.TEX, "space_m/atmo_vent/body", "Body_Cylinder");
    private static final ObjAtlasMesh VENT_FAN =
            ObjAtlasMesh.parts(AtmosphericCompressorRenderer.MODEL, AtmosphericCompressorRenderer.TEX, "space_m/atmo_vent/fan", "Fan_Cylinder.001");

    private static final ObjAtlasMesh EMITTER_BASE =
            ObjAtlasMesh.parts(AtmosphericEmitterRenderer.MODEL, AtmosphericEmitterRenderer.TEX, "space_m/atmo_emitter/base", "Base");
    private static final ObjAtlasMesh EMITTER_FAN =
            ObjAtlasMesh.parts(AtmosphericEmitterRenderer.MODEL, AtmosphericEmitterRenderer.TEX, "space_m/atmo_emitter/fan", "Fan");

    public static void register() {
        // RenderAtmosphericCompressor: Ecke als Ursprung, R(180), Meta (2: T(0,0,-1); 3: R(180) T(1,0,0);
        // 4: R(90) T(1,0,-1); 5: R(270)); Luefter um die Achse (-0.19 | 0.19).
        MachineRenderers.machine("space_m/atmo_vent", SpaceBlockEntities.ATMOSPHERIC_VENT.get(), AtmosphericCompressorBlockEntity.class)
                .blockTransform((be, a) -> {
                    a.rotate(180F, 0, 1, 0);
                    switch (ObjBerHelper.meta(be)) {
                        case 2 -> a.translate(0, 0, -1);
                        case 3 -> { a.rotate(180F, 0, 1, 0); a.translate(1, 0, 0); }
                        case 4 -> { a.rotate(90F, 0, 1, 0); a.translate(1, 0, -1); }
                        case 5 -> a.rotate(270F, 0, 1, 0);
                        default -> { }
                    }
                })
                .dynamicPart("body", be -> VENT_BODY.quads(), be -> VENT_BODY.cacheKey())
                .dynamicPart("fan", AtmoRaptorRenders::animateVentFan, be -> VENT_FAN.quads(), be -> VENT_FAN.cacheKey())
                .viewDistance(256)
                .register();

        // RenderAtmosphericEmitter: T(0.5, 0, 0.5), rotate90(meta), T(-0.5, 0, 0.5); Fan R_y(-fan)
        MachineRenderers.machine("space_m/atmo_emitter", SpaceBlockEntities.ATMO_EMITTER.get(), AtmosphericEmitterBlockEntity.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                    a.translate(-0.5, 0, 0.5);
                })
                .dynamicPart("base", be -> EMITTER_BASE.quads(), be -> EMITTER_BASE.cacheKey())
                .dynamicPart("fan", AtmoRaptorRenders::animateEmitterFan, be -> EMITTER_FAN.quads(), be -> EMITTER_FAN.cacheKey())
                .viewDistance(256)
                .register();
    }

    private static boolean animateVentFan(AtmosphericCompressorBlockEntity atmo, float f, long gameTime, PoseStack ps) {
        float rot = atmo.prevRot + (atmo.rot - atmo.prevRot) * f;
        ps.translate(-0.19D, 0D, 0.19D);
        ps.mulPose(Axis.YN.rotationDegrees(rot));
        ps.translate(0.19D, 0D, -0.19D);
        return true;
    }

    private static boolean animateEmitterFan(AtmosphericEmitterBlockEntity emitter, float f, long gameTime, PoseStack ps) {
        float rot = emitter.prevFan + (emitter.fan - emitter.prevFan) * f;
        ps.mulPose(Axis.YP.rotationDegrees(-rot));
        return true;
    }
}
