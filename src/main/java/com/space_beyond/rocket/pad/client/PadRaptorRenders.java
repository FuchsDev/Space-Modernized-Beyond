package com.space_beyond.rocket.pad.client;

import com.space_m.rocket.pad.client.RenderLaunchPadRocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.space_m.blockentity.SpaceBlockEntities;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.rocket.client.RocketPronter;
import com.space_m.rocket.pad.TileEntityLaunchPadRocket;

/**
 * Raketen-Startrampe auf Raptors Renderpfad, 1:1 aus {@link RenderLaunchPadRocket}: T(0.5, 0, 0.5), Meta (2: 90,
 * 4: 180, 3: 270, 5: 0); Sockel {@code Base} starr, Versorgungsturm (ab Turmhoehe 8 und freiem Himmel) je Hoehe
 * als eine fest gebackene Geometrie, die eingelegte Rakete ({@link RocketPronter}) immediate (Hook).
 */
public final class PadRaptorRenders {

    private PadRaptorRenders() {}

    private static final ObjAtlasMesh BASE = ObjAtlasMesh.parts(RenderLaunchPadRocket.rocket_pad, RenderLaunchPadRocket.rocket_pad_tex,
            "space_m/launch_pad/base", "Base");

    /** Turm je Hoehe (Render-Thread). */
    private static final Map<Integer, ObjAtlasMesh> TOWERS = new HashMap<>();

    private static ObjAtlasMesh tower(int height) {
        return TOWERS.computeIfAbsent(height, h -> ObjAtlasMesh.drawn(RenderLaunchPadRocket.rocket_pad,
                RenderLaunchPadRocket.rocket_pad_support_tex, "space_m/launch_pad/tower/" + h, (model, ps, vc) -> {
                    // 1:1 RenderLaunchPadRocket (Schleifen und Verschiebungen wie im Original)
                    model.renderPart("Tower_Base", ps, vc, 0);
                    for (int oy = 8; oy < h - 2; oy += 3) {
                        model.renderPart("Tower_Segment", ps, vc, 0);
                        ps.translate(0, 3, 0);
                    }
                    ps.translate(0, -3, 0);
                    for (int oy = 0; oy < (h - 2) % 3; oy++) {
                        model.renderPart("Tower_Segment_Small", ps, vc, 0);
                        ps.translate(0, 1, 0);
                    }
                    ps.translate(0, -1, 0);
                    model.renderPart("Tower_Cap", ps, vc, 0);
                }));
    }

    private static boolean hasTower(TileEntityLaunchPadRocket pad) {
        return pad.canSeeSky && pad.height >= 8;
    }

    public static void register() {
        MachineRenderers.machine("space_m/launch_pad_rocket", SpaceBlockEntities.LAUNCH_PAD_ROCKET.get(), TileEntityLaunchPadRocket.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("base", be -> BASE.quads(), be -> BASE.cacheKey())
                .dynamicPart("tower", be -> hasTower(be) ? tower(be.height).quads() : List.of(),
                        be -> hasTower(be) ? tower(be.height).cacheKey() : "off")
                .hook((pad, f, ps, buf, light, overlay, api) -> {
                    if (pad.rocket == null) return;
                    ps.pushPose();
                    ps.translate(0.0F, 3.0F, 0.0F);
                    RocketPronter.prontRocket(pad.rocket, ps, buf, light);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();
    }
}
