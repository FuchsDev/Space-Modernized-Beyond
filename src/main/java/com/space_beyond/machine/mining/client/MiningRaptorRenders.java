package com.space_beyond.machine.mining.client;

import com.space_m.machine.mining.client.RenderDishControl;
import com.space_m.machine.mining.client.RenderDriveProcessor;
import com.space_m.machine.mining.client.RenderGasDock;
import com.space_m.machine.mining.client.RenderMagma;

import java.util.List;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.hbm_m.client.render.implementations.MinerRocketRenderer;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.machine.mining.MiningMachines;
import com.space_m.machine.mining.DishMachines;
import com.space_m.machine.mining.TileEntityDishControl;
import com.space_m.machine.mining.TileEntityMachineDriveProcessor;
import com.space_m.machine.mining.TileEntityMachineGasDock;
import com.space_m.machine.mining.TileEntityMachineMagma;

import net.minecraft.util.Mth;

/**
 * Bergbau-Maschinen auf Raptors Renderpfad. Transformationen 1:1 aus {@link RenderDriveProcessor}, {@link RenderMagma},
 * {@link RenderDishControl} und {@link RenderGasDock}.
 */
public final class MiningRaptorRenders {

    private MiningRaptorRenders() {}

    private static final ObjAtlasMesh DRIVE_BASE =
            ObjAtlasMesh.parts(RenderDriveProcessor.MODEL, RenderDriveProcessor.TEX, "space_m/drive_processor/base", "Base");
    private static final ObjAtlasMesh DRIVE_DRIVE =
            ObjAtlasMesh.parts(RenderDriveProcessor.MODEL, RenderDriveProcessor.TEX, "space_m/drive_processor/drive", "Drive");

    private static final ObjAtlasMesh MAGMA_BODY =
            ObjAtlasMesh.allExceptIgnoreCase(RenderMagma.MODEL, RenderMagma.TEX, "space_m/magma/body", "DrillHead", "Blades");
    private static final ObjAtlasMesh MAGMA_HEAD =
            ObjAtlasMesh.partsIgnoreCase(RenderMagma.MODEL, RenderMagma.TEX, "space_m/magma/drillhead", "DrillHead");
    private static final ObjAtlasMesh MAGMA_BLADES =
            ObjAtlasMesh.partsIgnoreCase(RenderMagma.MODEL, RenderMagma.TEX, "space_m/magma/blades", "Blades");

    private static final ObjAtlasMesh DISH_BODY =
            ObjAtlasMesh.parts(RenderDishControl.MODEL, RenderDishControl.TEX, "space_m/dish_control/body", "dish_control", "joystick");
    private static final ObjAtlasMesh DISH_DRIVE =
            ObjAtlasMesh.parts(RenderDishControl.MODEL, RenderDishControl.TEX, "space_m/dish_control/drive", "drive");

    private static final ObjAtlasMesh GAS_DOCK = ObjAtlasMesh.all(RenderGasDock.SAT_DOCK, RenderGasDock.GAS_DOCK_TEX, "space_m/gas_dock/dock");
    private static final ObjAtlasMesh GAS_ROCKET = ObjAtlasMesh.all(MinerRocketRenderer.MODEL, RenderGasDock.GAS_ROCKET_TEX, "space_m/gas_dock/rocket");

    public static void register() {
        // RenderDishControl: T(0.5, 0, 0.5), R(90), Meta (2: 0, 4: 90, 3: 180, 5: 270); dish_control + joystick,
        // drive nur wenn der StarDar ein Laufwerk hat. Kein getViewDistance -> Vanilla 64.
        MachineRenderers.machine("space_m/dish_control", DishMachines.DISH_CONTROL_BE.get(), TileEntityDishControl.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0.0, 0.5);
                    a.rotate(90, 0, 1, 0);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> DISH_BODY.quads(), be -> DISH_BODY.cacheKey())
                .dynamicPart("drive", be -> be.starDarHasDisk() ? DISH_DRIVE.quads() : List.of(),
                        be -> be.starDarHasDisk() ? DISH_DRIVE.cacheKey() : "off")
                .viewDistance(64)
                .register();

        // RenderGasDock (RenderDecoBlock gas_dock): T(0.5, 0, 0.5), sat_dock mit gas_dock-Textur; Sammelrakete
        // (gasRocket) solange launchTicks < 100 auf Hoehe 0.75 + clamp(launchTicks -+ interp, 0, 200).
        MachineRenderers.machine("space_m/gas_dock", MiningMachines.GAS_DOCK_BE.get(), TileEntityMachineGasDock.class)
                .blockTransform((be, a) -> a.translate(0.5, 0.0, 0.5))
                .dynamicPart("dock", be -> GAS_DOCK.quads(), be -> GAS_DOCK.cacheKey())
                .dynamicPart("rocket", MiningRaptorRenders::animateGasRocket, be -> GAS_ROCKET.quads(), be -> GAS_ROCKET.cacheKey())
                .viewDistance(256)
                .register();

        // RenderDriveProcessor: T(0.5, 0, 0.5), R(90), Meta-Drehung (2: 0, 4: 90, 3: 180, 5: 270); Base, Drive nur mit Laufwerk.
        // Kein getViewDistance im Original-BER -> Vanilla-Standard 64.
        MachineRenderers.machine("space_m/drive_processor", MiningMachines.DRIVE_PROCESSOR_BE.get(), TileEntityMachineDriveProcessor.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0.0, 0.5);
                    a.rotate(90, 0, 1, 0);
                    a.rotate(switch (ObjBerHelper.meta(be)) {
                        case 4 -> 90F;
                        case 3 -> 180F;
                        case 5 -> 270F;
                        default -> 0F;
                    }, 0, 1, 0);
                })
                .dynamicPart("Base", be -> DRIVE_BASE.quads(), be -> DRIVE_BASE.cacheKey())
                // Zustandsabhaengig ohne Animator: bleibt (wie Base) bis zur statischen Sichtweite sichtbar
                .dynamicPart("Drive", be -> be.hasDrive ? DRIVE_DRIVE.quads() : List.of(),
                        be -> be.hasDrive ? DRIVE_DRIVE.cacheKey() : "off")
                .viewDistance(64)
                .register();

        // RenderMagma: T(0.5, 0, 0.5), Meta-Drehung (3: 0, 5: 90, 2: 180, 4: 270), T(0, -heightOffset, 0);
        // alles ausser DrillHead/Blades starr, DrillHead R(+rot), Blades R(-rot), Lavaspiegel immediate.
        MachineRenderers.machine("space_m/magma", MiningMachines.MAGMA_BE.get(), TileEntityMachineMagma.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0.0, 0.5);
                    a.rotate(switch (ObjBerHelper.meta(be)) {
                        case 5 -> 90F;
                        case 2 -> 180F;
                        case 4 -> 270F;
                        default -> 0F;
                    }, 0, 1, 0);
                    a.translate(0, -MiningMachines.MACHINE_MAGMA.get().getHeightOffset(), 0);
                })
                .dynamicPart("body", be -> MAGMA_BODY.quads(), be -> MAGMA_BODY.cacheKey())
                .dynamicPart("drillhead", MiningRaptorRenders::animateDrillHead, be -> MAGMA_HEAD.quads(), be -> MAGMA_HEAD.cacheKey())
                .dynamicPart("blades", MiningRaptorRenders::animateBlades, be -> MAGMA_BLADES.quads(), be -> MAGMA_BLADES.cacheKey())
                .hook((drill, interp, ps, buf, light, overlay, api) -> {
                    float lavaHeight = drill.prevLavaHeight + (drill.lavaHeight - drill.prevLavaHeight) * interp;
                    if (lavaHeight > 0.01F) {
                        ps.pushPose();
                        com.space_beyond.machine.misc.client.BeyondRenderUtil.renderLava(ps, buf, light, lavaHeight);
                        ps.popPose();
                    }
                })
                .viewDistance(256)
                .register();
    }

    private static boolean animateGasRocket(TileEntityMachineGasDock dock, float f, long gameTime, PoseStack pose) {
        if (dock.launchTicks >= 100) return false;
        pose.translate(0.0F, 0.75F + Mth.clamp(dock.launchTicks + (dock.hasRocket ? -f : f), 0, 200), 0.0F);
        return true;
    }

    private static float drillRotation(TileEntityMachineMagma drill, float interp) {
        return drill.prevDrillRotation + (drill.drillRotation - drill.prevDrillRotation) * interp;
    }

    private static boolean animateDrillHead(TileEntityMachineMagma drill, float interp, long gameTime, PoseStack pose) {
        pose.mulPose(Axis.YP.rotationDegrees(drillRotation(drill, interp)));
        return true;
    }

    private static boolean animateBlades(TileEntityMachineMagma drill, float interp, long gameTime, PoseStack pose) {
        pose.mulPose(Axis.YP.rotationDegrees(-drillRotation(drill, interp)));
        return true;
    }
}
