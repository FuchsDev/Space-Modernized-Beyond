package com.space_beyond.machine.misc.client;

import com.space_m.machine.misc.client.RenderRadiator;
import com.space_m.machine.misc.client.RenderSolarPanel;
import com.space_m.machine.misc.client.RenderStardar;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.machine.misc.MiscBlockEntities;
import com.space_m.machine.misc.TileEntityMachineSolarPanel;
import com.space_m.machine.misc.TileEntityMachineStardar;
import com.space_m.machine.misc.TileEntityRadiator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * StarDar (Pilot), Radiator und Solarpanel auf Raptors Renderpfad.
 * StarDar: Transformationen 1:1 aus {@link RenderStardar}:
 * Blocktransform T(0.5, -3, 0.5); {@code base} zusaetzlich R(180) + Meta-Drehung (nur fuer dieses Teil, daher je
 * Ausrichtung fest in die Geometrie gebacken - reine 90-Grad-Drehungen um Y, verlustfrei); {@code rotation}
 * R(yaw); {@code pitch} R(yaw) T(0, 10.6, 0) R_x(pitch) T(0, -10.6, 0).
 */
public final class MiscRaptorRenders {

    // ── Radiator (RenderRadiator): Kern-Mitte T(0.5, 0.5, 0.5), rotate90(meta), T(-0.5, 0, 0), Rz(-90), renderAll ──
    private static final ObjAtlasMesh RADIATOR = ObjAtlasMesh.all(RenderRadiator.MODEL, RenderRadiator.TEXTURE, "space_m/radiator");

    // ── Solarpanel (RenderSolarPanel) ──
    private static final ObjAtlasMesh SOLAR_PANEL =
            ObjAtlasMesh.parts(RenderSolarPanel.MODEL, RenderSolarPanel.TEXTURE, "space_m/solar_panel/panel", "solar_panel");
    /** Index = Meta 0..5; Panel R(90) + (2: 0, 4: 90, 3: 180, 5: 270), nur fuer dieses Teil -> eingebacken. */
    private static final ObjAtlasMesh[] SOLAR_PANEL_BY_META = new ObjAtlasMesh[6];
    /** Index = Verbinder-Maske (Bit 0 Sued, 1 Nord, 2 Ost, 3 West, 4 oben, 5 unten). */
    private static final ObjAtlasMesh[] SOLAR_CONNECTORS = new ObjAtlasMesh[64];
    static {
        for (int meta = 0; meta < 6; meta++) {
            float deg = RaptorMachineRenders.rot0(meta);
            SOLAR_PANEL_BY_META[meta] = SOLAR_PANEL.baked(ps -> {
                ps.mulPose(Axis.YP.rotationDegrees(90));
                ps.mulPose(Axis.YP.rotationDegrees(deg));
            }, "m" + meta);
        }
        for (int mask = 0; mask < 64; mask++) {
            final int m = mask;
            SOLAR_CONNECTORS[mask] = ObjAtlasMesh.drawn(RenderSolarPanel.MODEL, RenderSolarPanel.TEXTURE,
                    "space_m/solar_panel/connectors/" + mask, (model, ps, vc) -> {
                        // 1:1 RenderSolarPanel (Reihenfolge und Drehungen wie im Original)
                        if ((m & 1) != 0) model.renderPart("connector_front", ps, vc, 0);
                        if ((m & 2) != 0) model.renderPart("connector_side", ps, vc, 0);
                        if ((m & 4) != 0) {
                            ps.mulPose(Axis.YP.rotationDegrees(90));
                            model.renderPart("connector_front", ps, vc, 0);
                            ps.mulPose(Axis.YP.rotationDegrees(-90));
                        }
                        if ((m & 8) != 0) {
                            ps.mulPose(Axis.YP.rotationDegrees(90));
                            model.renderPart("connector_side", ps, vc, 0);
                            ps.mulPose(Axis.YP.rotationDegrees(-90));
                        }
                        if ((m & 16) != 0) model.renderPart("connector_top", ps, vc, 0);
                        if ((m & 32) != 0) model.renderPart("connector_bottom", ps, vc, 0);
                    });
        }
    }


    private MiscRaptorRenders() {}

    private static final float DISH_OFFSET = 10.6F;

    private static final ObjAtlasMesh BASE =
            ObjAtlasMesh.parts(RenderStardar.STARDAR, RenderStardar.STARDAR_TEX, "space_m/stardar/base", "base");
    /** Index = Meta 2..5 (Original: 2: 0, 4: 90, 3: 180, 5: 270, jeweils nach R(180)). */
    private static final ObjAtlasMesh[] BASE_BY_META = new ObjAtlasMesh[6];
    static {
        for (int meta = 2; meta <= 5; meta++) {
            float deg = switch (meta) {
                case 4 -> 90F;
                case 3 -> 180F;
                case 5 -> 270F;
                default -> 0F;
            };
            BASE_BY_META[meta] = BASE.baked(ps -> {
                ps.mulPose(Axis.YP.rotationDegrees(180));
                ps.mulPose(Axis.YP.rotationDegrees(deg));
            }, "m" + meta);
        }
    }
    private static final ObjAtlasMesh ROTATION =
            ObjAtlasMesh.parts(RenderStardar.STARDAR, RenderStardar.STARDAR_TEX, "space_m/stardar/rotation", "rotation");
    private static final ObjAtlasMesh PITCH =
            ObjAtlasMesh.parts(RenderStardar.STARDAR, RenderStardar.STARDAR_TEX, "space_m/stardar/pitch", "pitch");

    public static void register() {
        MachineRenderers.machine("space_m/radiator", MiscBlockEntities.RADIATOR.get(), TileEntityRadiator.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0.5, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                    a.translate(-0.5, 0, 0);
                    a.rotate(-90, 0, 0, 1);
                })
                .dynamicPart("all", be -> RADIATOR.quads(), be -> RADIATOR.cacheKey())
                .viewDistance(256)
                .register();

        // RenderSolarPanel: T(0.5, 0, 0.5); Panel mit eigener Drehung, Anschluesse je Energie-Nachbar ungedreht.
        // Kein getViewDistance -> Vanilla 64.
        MachineRenderers.machine("space_m/solar_panel", MiscBlockEntities.SOLAR_PANEL.get(), TileEntityMachineSolarPanel.class)
                .blockTransform((be, a) -> a.translate(0.5, 0.0, 0.5))
                .dynamicPart("panel", be -> solarPanel(be).quads(), be -> solarPanel(be).cacheKey())
                .dynamicPart("connectors", be -> SOLAR_CONNECTORS[connectorMask(be)].quads(),
                        be -> SOLAR_CONNECTORS[connectorMask(be)].cacheKey())
                .viewDistance(64)
                .register();

        MachineRenderers.machine("space_m/stardar", MiscBlockEntities.STARDAR.get(), TileEntityMachineStardar.class)
                .blockTransform((be, a) -> a.translate(0.5, -3.0, 0.5))
                .dynamicPart("base", be -> base(be).quads(), be -> base(be).cacheKey())
                .dynamicPart("rotation", MiscRaptorRenders::animateRotation, be -> ROTATION.quads(), be -> ROTATION.cacheKey())
                .dynamicPart("pitch", MiscRaptorRenders::animatePitch, be -> PITCH.quads(), be -> PITCH.cacheKey())
                .viewDistance(256)
                .register();
    }

    private static ObjAtlasMesh solarPanel(TileEntityMachineSolarPanel be) {
        int meta = ObjBerHelper.meta(be);
        return SOLAR_PANEL_BY_META[meta >= 0 && meta < 6 ? meta : 0];
    }

    /** {@code Library.canConnect} je Seite wie {@link RenderSolarPanel}. */
    private static int connectorMask(TileEntityMachineSolarPanel te) {
        Level level = te.getLevel();
        if (level == null) return 0;
        BlockPos p = te.getBlockPos();
        int mask = 0;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.south(), Direction.NORTH)) mask |= 1;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.north(), Direction.SOUTH)) mask |= 2;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.east(), Direction.WEST)) mask |= 4;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.west(), Direction.EAST)) mask |= 8;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.above(), Direction.DOWN)) mask |= 16;
        if (com.space_beyond.machine.misc.client.BeyondRenderUtil.canConnect(level, p.below(), Direction.UP)) mask |= 32;
        return mask;
    }

    private static ObjAtlasMesh base(TileEntityMachineStardar be) {
        return BASE_BY_META[ObjBerHelper.meta(be)];
    }

    private static float yaw(TileEntityMachineStardar s, float interp) {
        return s.prevDishYaw + (s.dishYaw - s.prevDishYaw) * interp;
    }

    private static boolean animateRotation(TileEntityMachineStardar s, float interp, long gameTime, PoseStack ps) {
        ps.mulPose(Axis.YP.rotationDegrees(yaw(s, interp)));
        return true;
    }

    private static boolean animatePitch(TileEntityMachineStardar s, float interp, long gameTime, PoseStack ps) {
        float dishPitch = s.prevDishPitch + (s.dishPitch - s.prevDishPitch) * interp;
        ps.mulPose(Axis.YP.rotationDegrees(yaw(s, interp)));
        ps.translate(0, DISH_OFFSET, 0);
        ps.mulPose(Axis.XP.rotationDegrees(dishPitch));
        ps.translate(0, -DISH_OFFSET, 0);
        return true;
    }
}
