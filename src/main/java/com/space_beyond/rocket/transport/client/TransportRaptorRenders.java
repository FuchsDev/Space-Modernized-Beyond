package com.space_beyond.rocket.transport.client;

import com.space_m.rocket.transport.client.TransporterRocketRenderer;

import com.hbm_m.client.render.implementations.MinerRocketRenderer;
import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.space_m.blockentity.SpaceBlockEntities;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.rocket.transport.TransporterRocketBlockEntity;

import net.minecraft.util.Mth;

/**
 * Transporter-Rampe auf Raptors Renderpfad, 1:1 aus {@link TransporterRocketRenderer}: T(0.5, 0, 0.5), R(90),
 * Meta (2: 0, 4: 90, 3: 180, 5: 270); {@code base} starr, Tankarm {@code pipe} kippt um (0 | 0.75 | -0.75) bis
 * -30 Grad, die Sammelrakete ({@code minerRocket}) steigt beim Start auf bzw. sinkt bei der Landung ab.
 *
 * <p>Die Kampfkapsel ({@link CombatPodRenderer}) bleibt bewusst immediate: ihr BlockEntity erbt nicht von
 * {@code BaseMachineBlockEntity}, und Raptors Culling kennt auf NeoForge nur dafuer die grosse Render-Box
 * (sonst 3x3x3 um den Kern bei einer 8 Bloecke hohen Kapsel).
 */
public final class TransportRaptorRenders {

    private TransportRaptorRenders() {}

    private static final ObjAtlasMesh BASE = ObjAtlasMesh.parts(TransporterRocketRenderer.MODEL, TransporterRocketRenderer.TEX, "space_m/transporter/base", "base");
    private static final ObjAtlasMesh PIPE = ObjAtlasMesh.parts(TransporterRocketRenderer.MODEL, TransporterRocketRenderer.TEX, "space_m/transporter/pipe", "pipe");
    private static final ObjAtlasMesh ROCKET = ObjAtlasMesh.all(MinerRocketRenderer.MODEL, MinerRocketRenderer.TEXTURE, "space_m/transporter/rocket");

    public static void register() {
        MachineRenderers.machine("space_m/transporter_rocket", SpaceBlockEntities.TRANSPORTER_ROCKET.get(), TransporterRocketBlockEntity.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0.0, 0.5);
                    a.rotate(90, 0, 1, 0);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("base", be -> BASE.quads(), be -> BASE.cacheKey())
                .dynamicPart("pipe", TransportRaptorRenders::animatePipe, be -> PIPE.quads(), be -> PIPE.cacheKey())
                .dynamicPart("rocket", TransportRaptorRenders::animateRocket, be -> ROCKET.quads(), be -> ROCKET.cacheKey())
                .viewDistance(256)
                .register();
    }

    private static boolean animatePipe(TransporterRocketBlockEntity pad, float interp, long gameTime, PoseStack ps) {
        float rot = Mth.clamp(getPipeEngage(pad, interp), 0, 1);
        ps.translate(0.0F, 0.75F, -0.75F);
        ps.mulPose(Axis.XP.rotationDegrees(rot * -30.0F));
        ps.translate(0.0F, -0.75F, 0.75F);
        return true;
    }

    private static boolean animateRocket(TransporterRocketBlockEntity pad, float interp, long gameTime, PoseStack ps) {
        if (pad.launchTicks >= 100) return false;
        ps.translate(0.0F, 0.75F + Mth.clamp(pad.launchTicks + (pad.hasRocket ? -interp : interp), 0, 200), 0.0F);
        return true;
    }

    /** {@code RenderTransporterRocket.getPipeEngage}. */
    private static float getPipeEngage(TransporterRocketBlockEntity pad, float interp) {
        if (pad.launchTicks >= 0) {
            return !pad.hasRocket ? (pad.launchTicks + interp) * 0.25F : 1.0F;
        } else {
            return 1 - (-pad.launchTicks - 1 + interp) * 0.25F;
        }
    }
}
