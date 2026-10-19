package com.space_beyond.machine.chem.client;

import com.space_m.machine.chem.client.ChemModels;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.math.Axis;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.machine.chem.ChemMachines;
import com.space_m.machine.chem.TileEntityHydroponic;
import com.space_m.machine.chem.TileEntityMachineAlkylation;
import com.space_m.machine.chem.TileEntityMachineCryoDistill;
import com.space_m.machine.chem.TileEntityMachineMilkReformer;
import com.space_m.machine.chem.TileEntityMachineVacuumCircuit;

import net.minecraft.client.renderer.RenderType;

/**
 * Chemie-Maschinen auf Raptors Renderpfad. Transformationen 1:1 aus {@link RenderAlkylation},
 * {@link RenderCryoDistill}, {@link RenderMilkReformer}, {@link RenderVacuumCircuit} und {@link RenderHydroponic}.
 * Immediate bleiben nur der Rahmen-Gegenstand des Vakuumloeters und die vollhellen Lampen der Hydrokultur (Hooks).
 */
public final class ChemRaptorRenders {

    private ChemRaptorRenders() {}

    private static final ObjAtlasMesh ALKYL = ObjAtlasMesh.all(ChemModels.ALKYL, ChemModels.ALKYL_TEX, "space_m/alkylation");
    private static final ObjAtlasMesh CRYO = ObjAtlasMesh.all(ChemModels.CRYO_DISTILL, ChemModels.CRYODISTILL_TEX, "space_m/cryo_distill");
    private static final ObjAtlasMesh MILK = ObjAtlasMesh.all(ChemModels.MILK_REFORMER, ChemModels.MILK_REFORMER_TEX, "space_m/milk_reformer");
    private static final ObjAtlasMesh VAC_CIR = ObjAtlasMesh.all(ChemModels.VAC_CIR_STATION, ChemModels.VAC_CIR_STATION_TEX, "space_m/vacuum_circuit");
    private static final ObjAtlasMesh HYDRO_BASE = ObjAtlasMesh.parts(ChemModels.HYDROPONIC, ChemModels.HYDROPONIC_TEX, "space_m/hydroponic/base", "Base");

    public static void register() {
        // RenderAlkylation: T(0.5, 0, 0.5), rotate0(meta), renderAll
        MachineRenderers.machine("space_m/alkylation", ChemMachines.ALKYLATION_BE.get(), TileEntityMachineAlkylation.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(rotate0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> ALKYL.quads(), be -> ALKYL.cacheKey())
                .viewDistance(256)
                .register();

        // RenderCryoDistill: T(0.5, -2, 0.5), R(180), rotate0(meta), renderAll
        MachineRenderers.machine("space_m/cryo_distill", ChemMachines.CRYO_DISTILL_BE.get(), TileEntityMachineCryoDistill.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, -2.0, 0.5);
                    a.rotate(180, 0, 1, 0);
                    a.rotate(rotate0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> CRYO.quads(), be -> CRYO.cacheKey())
                .viewDistance(256)
                .register();

        // RenderMilkReformer: T(0.5, 0, 0.5), R(180), rotate90(meta), renderAll (NoCull: VBO-Pfad zeichnet beidseitig)
        MachineRenderers.machine("space_m/milk_reformer", ChemMachines.MILK_REFORMER_BE.get(), TileEntityMachineMilkReformer.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(180, 0, 1, 0);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> MILK.quads(), be -> MILK.cacheKey())
                .viewDistance(256)
                .register();

        // RenderVacuumCircuit: T(0.5, 0, 0.5), rotate90(meta), renderAll; Rahmen-Gegenstand immediate
        MachineRenderers.machine("space_m/vacuum_circuit", ChemMachines.VACUUM_CIRCUIT_BE.get(), TileEntityMachineVacuumCircuit.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> VAC_CIR.quads(), be -> VAC_CIR.cacheKey())
                .hook((solderer, interp, ps, buf, light, overlay, api) -> {
                    if (solderer.display.isEmpty()) return;
                    ps.pushPose();
                    ps.translate(0.625D, 1.25D, 0.5D);
                    ps.mulPose(Axis.YP.rotationDegrees(90));
                    ps.mulPose(Axis.XP.rotationDegrees(-90));
                    ps.scale(1.5F, 1.5F, 1.5F);
                    ObjBerHelper.renderDecoItemInFrame(ps, buf, solderer.display, light, solderer.getLevel());
                    ps.popPose();
                })
                .viewDistance(256)
                .register();

        // RenderHydroponic: T(0.5, 0, 0.5), rotate90(meta), Base; ab 200 HE "Lights" voll hell additiv (eyes) immediate
        MachineRenderers.machine("space_m/hydroponic", ChemMachines.HYDROBAY_BE.get(), TileEntityHydroponic.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("Base", be -> HYDRO_BASE.quads(), be -> HYDRO_BASE.cacheKey())
                .hook((hydro, interp, ps, buf, light, overlay, api) -> {
                    if (hydro.getEnergyStored() < 200) return;
                    ps.pushPose();
                    ChemModels.HYDROPONIC.renderPart("Lights", ps, buf.getBuffer(RenderType.eyes(ChemModels.HYDROPONIC_TEX)), 0xF000F0);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();
    }

    /** {@link ChemModels#rotate0}: {@code case 4: 90; case 3: 180; case 5: 270}, sonst 0. */
    static float rotate0(int meta) {
        return RaptorMachineRenders.rot0(meta);
    }
}
