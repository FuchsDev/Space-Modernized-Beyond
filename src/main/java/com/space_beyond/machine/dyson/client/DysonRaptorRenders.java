package com.space_beyond.machine.dyson.client;

import com.space_m.machine.dyson.client.DysonModels;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.machine.dyson.DysonBlockEntities;
import com.space_m.machine.dyson.TileEntityDysonConverterAnatmogenesis;
import com.space_m.machine.dyson.TileEntityDysonConverterHE;
import com.space_m.machine.dyson.TileEntityDysonConverterTU;
import com.space_m.machine.dyson.TileEntityDysonLauncher;
import com.space_m.machine.dyson.TileEntityDysonReceiver;

import net.minecraft.world.phys.Vec3;

/**
 * Dyson-Maschinen auf Raptors Renderpfad. Transformationen 1:1 aus {@link RenderDysonConverters} (HE, TU,
 * Anatmogenesis), {@link RenderDysonLauncher} und {@link RenderDysonReceiver}; alle mit Blocktransform
 * T(0.5, 0, 0.5) + {@link DysonModels#rotate}. Immediate bleibt nur der Empfaengerstrahl (Hook).
 */
public final class DysonRaptorRenders {

    private DysonRaptorRenders() {}

    private static final ObjAtlasMesh HE_BODY = part(DysonModels.HE_CONVERTER, DysonModels.HE_CONVERTER_TEX, "he/body", "HE_Converter");
    private static final ObjAtlasMesh HE_COILS13 = part(DysonModels.HE_CONVERTER, DysonModels.HE_CONVERTER_TEX, "he/coil13", "Coil1", "Coil3");
    private static final ObjAtlasMesh HE_COIL2 = part(DysonModels.HE_CONVERTER, DysonModels.HE_CONVERTER_TEX, "he/coil2", "Coil2");

    private static final ObjAtlasMesh TU_ALL = ObjAtlasMesh.all(DysonModels.TU_CONVERTER, DysonModels.TU_CONVERTER_TEX, "space_m/dyson/tu");

    private static final ObjAtlasMesh ANAT_BODY = part(DysonModels.ANATMOGENESIS, DysonModels.ANATMOGENESIS_TEX, "anat/body", "Anatmogenesis");
    private static final ObjAtlasMesh ANAT_COILS = part(DysonModels.ANATMOGENESIS, DysonModels.ANATMOGENESIS_TEX, "anat/coils", "Coils");

    private static final ObjAtlasMesh LAUNCH_BODY = part(DysonModels.SPINLAUNCH, DysonModels.SPINLAUNCH_TEX, "launcher/body", "Launch");
    private static final ObjAtlasMesh LAUNCH_ROTOR = part(DysonModels.SPINLAUNCH, DysonModels.SPINLAUNCH_TEX, "launcher/rotor", "The_Thing_That_Rotates");
    private static final ObjAtlasMesh LAUNCH_PAYLOAD = part(DysonModels.SPINLAUNCH, DysonModels.SPINLAUNCH_TEX, "launcher/payload", "Payload");

    private static final ObjAtlasMesh RECV_BODY = part(DysonModels.RECEIVER, DysonModels.RECEIVER_TEX, "receiver/body", "DysonReceiver");
    private static final ObjAtlasMesh RECV_COILS13 = part(DysonModels.RECEIVER, DysonModels.RECEIVER_TEX, "receiver/coil13", "Coil1", "Coil3");
    private static final ObjAtlasMesh RECV_COIL2 = part(DysonModels.RECEIVER, DysonModels.RECEIVER_TEX, "receiver/coil2", "Coil2");

    private static ObjAtlasMesh part(com.hbm_m.client.render.SimpleObjModel model, net.minecraft.resources.ResourceLocation tex,
                                     String key, String... parts) {
        return ObjAtlasMesh.parts(model, tex, "space_m/dyson/" + key, parts);
    }

    public static void register() {
        // RenderDysonConverterHE: Gehaeuse; Coil1/Coil3 um Z (+t), Coil2 (-t) auf y 1.5
        MachineRenderers.machine("space_m/dyson_he", DysonBlockEntities.CONVERTER_HE.get(), TileEntityDysonConverterHE.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> HE_BODY.quads(), be -> HE_BODY.cacheKey())
                .dynamicPart("coil13", (be, f, gt, ps) -> coil(ps, heTime(be, f)), be -> HE_COILS13.quads(), be -> HE_COILS13.cacheKey())
                .dynamicPart("coil2", (be, f, gt, ps) -> coil(ps, -heTime(be, f)), be -> HE_COIL2.quads(), be -> HE_COIL2.cacheKey())
                .viewDistance(256)
                .register();

        // RenderDysonConverterTU: ganzes Modell
        MachineRenderers.machine("space_m/dyson_tu", DysonBlockEntities.CONVERTER_TU.get(), TileEntityDysonConverterTU.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> TU_ALL.quads(), be -> TU_ALL.cacheKey())
                .viewDistance(256)
                .register();

        // RenderDysonConverterAnatmogenesis: Gehaeuse; Coils beim Wandeln zitternd (sin(t*0.1)*0.025 + rand*0.02)
        MachineRenderers.machine("space_m/dyson_anatmogenesis", DysonBlockEntities.CONVERTER_ANATMOGENESIS.get(), TileEntityDysonConverterAnatmogenesis.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> ANAT_BODY.quads(), be -> ANAT_BODY.cacheKey())
                .dynamicPart("coils", DysonRaptorRenders::animateAnatCoils, be -> ANAT_COILS.quads(), be -> ANAT_COILS.cacheKey())
                .viewDistance(256)
                .register();

        // RenderDysonLauncher: Launch starr; Rotor um die 45-Grad-Achse auf y 8.5; Payload im Rotor bzw. im Auslauf
        MachineRenderers.machine("space_m/dyson_launcher", DysonBlockEntities.LAUNCHER.get(), TileEntityDysonLauncher.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> LAUNCH_BODY.quads(), be -> LAUNCH_BODY.cacheKey())
                .dynamicPart("rotor", DysonRaptorRenders::animateLauncherRotor, be -> LAUNCH_ROTOR.quads(), be -> LAUNCH_ROTOR.cacheKey())
                .dynamicPart("payload", DysonRaptorRenders::animateLauncherPayload, be -> LAUNCH_PAYLOAD.quads(), be -> LAUNCH_PAYLOAD.cacheKey())
                .viewDistance(256)
                .register();

        // RenderDysonReceiver: wie HE (Spulen beim Empfang), dazu die zwei Strahlen immediate
        MachineRenderers.machine("space_m/dyson_receiver", DysonBlockEntities.RECEIVER.get(), TileEntityDysonReceiver.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> RECV_BODY.quads(), be -> RECV_BODY.cacheKey())
                .dynamicPart("coil13", (be, f, gt, ps) -> coil(ps, receiverTime(be, f)), be -> RECV_COILS13.quads(), be -> RECV_COILS13.cacheKey())
                .dynamicPart("coil2", (be, f, gt, ps) -> coil(ps, -receiverTime(be, f)), be -> RECV_COIL2.quads(), be -> RECV_COIL2.cacheKey())
                .hook((receiver, f, ps, buf, light, overlay, api) -> {
                    if (!receiver.isReceiving) return;
                    long time = receiver.getLevel() != null ? receiver.getLevel().getGameTime() : 0;
                    int length = receiver.beamLength;
                    int color = 0xff8800;
                    ps.pushPose();
                    ps.translate(0.0F, 1.5F, 0.0F);
                    DysonModels.prontBeamSolid(ps, buf, new Vec3(0, 0, length), true, color, color, 0, 1, 0F, 2, 0.4F, 0.5F);
                    DysonModels.prontBeamSolid(ps, buf, new Vec3(0, 0, length), false, color, color, (int) (time % 1000), (length / 2), 0.0625F, 2, 0.4F, 0.5F);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();
    }

    /** T(0, 1.5, 0), R_z(deg), T(0, -1.5, 0) - Spulen von HE-Wandler und Empfaenger ({@code ZN} = negativer Winkel). */
    private static boolean coil(PoseStack ps, float deg) {
        ps.translate(0.0F, 1.5F, 0.0F);
        ps.mulPose(Axis.ZP.rotationDegrees(deg));
        ps.translate(0.0F, -1.5F, 0.0F);
        return true;
    }

    private static float heTime(TileEntityDysonConverterHE c, float f) {
        return c.isConverting && c.getLevel() != null ? c.getLevel().getGameTime() + f : 0;
    }

    private static float receiverTime(TileEntityDysonReceiver r, float f) {
        long time = r.getLevel() != null ? r.getLevel().getGameTime() : 0;
        return r.isReceiving ? time + f : 0;
    }

    private static boolean animateAnatCoils(TileEntityDysonConverterAnatmogenesis c, float f, long gameTime, PoseStack ps) {
        if (c.isConverting && c.getLevel() != null) {
            float t = c.getLevel().getGameTime() + f;
            ps.translate(0, Math.sin(t * 0.1) * 0.025 + c.getLevel().random.nextFloat() * 0.02, 0);
        }
        return true;
    }

    private static void rotorTransform(TileEntityDysonLauncher l, float f, PoseStack ps) {
        float t = l.lastRotation + (l.rotation - l.lastRotation) * f;
        ps.translate(0, 8.5F, 0);
        ps.mulPose(Axis.XP.rotationDegrees(45.0F));
        ps.mulPose(Axis.YP.rotationDegrees(t));
        ps.mulPose(Axis.XP.rotationDegrees(-45.0F));
        ps.translate(0, -8.5F, 0);
    }

    private static boolean animateLauncherRotor(TileEntityDysonLauncher l, float f, long gameTime, PoseStack ps) {
        rotorTransform(l, f, ps);
        return true;
    }

    /** Im Rotor (geladen, nicht im Auslauf) bzw. entlang der Schleuderbahn ({@code payloadTicks * 10}); sonst nicht. */
    private static boolean animateLauncherPayload(TileEntityDysonLauncher l, float f, long gameTime, PoseStack ps) {
        if (l.isSpinningDown) {
            float p = l.payloadTicks + f;
            ps.translate(1.0F, 8.5F, 0);
            ps.mulPose(Axis.XP.rotationDegrees(45.0F));
            ps.mulPose(Axis.YP.rotationDegrees(90.0F));
            ps.translate(p * 10.0F, 0, 0);
            ps.mulPose(Axis.XP.rotationDegrees(-45.0F));
            ps.translate(0, -8.5F, 0);
            return true;
        }
        if (l.satCount > 0) {
            rotorTransform(l, f, ps);
            return true;
        }
        return false;
    }
}
