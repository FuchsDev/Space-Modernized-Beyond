package com.space_beyond.orbit.client;

import com.space_m.orbit.client.RenderOrbitalStation;

import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.hbm_m.client.render.machine.MachineSpecBuilder;
import com.mojang.math.Axis;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_m.orbit.OrbitBlocks;
import com.space_m.orbit.TileEntityOrbitalStation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Andockring der Orbitalstation auf Raptors Renderpfad, 1:1 aus {@link RenderOrbitalStation}: Blocktransform
 * T(0.5, 1, 0.5) + {@link RenderOrbitalStation#rotate}; {@code Port} starr, vier Arme {@code ArmZP} je um 90 Grad
 * versetzt, klappen um die Achse (0, -1.75, -2) auf. Gemeinsam mit dem Startport
 * ({@code PropulsionRaptorRenders}, Textur {@code docking_port_launcher}).
 */
public final class OrbitRaptorRenders {

    private OrbitRaptorRenders() {}

    /** Armwinkel eines Andockrings ({@code prevRot..rot}). */
    @FunctionalInterface
    public interface ArmRotation<T> {
        float get(T be, float interp);
    }

    /** Port + Arm-Geometrie einer Textur. */
    public record PortMeshes(ObjAtlasMesh port, ObjAtlasMesh arm) {
        public static PortMeshes of(ResourceLocation tex, String key) {
            return new PortMeshes(
                    ObjAtlasMesh.parts(RenderOrbitalStation.DOCKING_PORT, tex, key + "/port", "Port"),
                    ObjAtlasMesh.parts(RenderOrbitalStation.DOCKING_PORT, tex, key + "/arm", "ArmZP"));
        }
    }

    private static final PortMeshes STATION = PortMeshes.of(RenderOrbitalStation.DOCKING_PORT_TEX, "space_m/orbital_station");

    public static void register() {
        port(MachineRenderers.machine("space_m/orbital_station", OrbitBlocks.ORBITAL_STATION_BE.get(), TileEntityOrbitalStation.class),
                STATION, (s, interp) -> s.prevRot + (s.rot - s.prevRot) * interp)
                .viewDistance(256)
                .register();
    }

    /**
     * Blocktransform, Ring und vier Arme ({@link RenderOrbitalStation#renderPort}): Arm {@code i} =
     * R_y(90 * i) T(0, -1.75, -2) R_x(-arm) T(0, 1.75, 2).
     */
    public static <T extends BlockEntity> MachineSpecBuilder<T> port(MachineSpecBuilder<T> builder, PortMeshes meshes, ArmRotation<T> rotation) {
        builder.blockTransform((be, a) -> {
                    a.translate(0.5, 1.0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("port", be -> meshes.port().quads(), be -> meshes.port().cacheKey());
        for (int i = 0; i < 4; i++) {
            final float yaw = 90F * i;
            builder.dynamicPart("arm" + i, (be, interp, gameTime, ps) -> {
                ps.mulPose(Axis.YP.rotationDegrees(yaw));
                ps.translate(0, -1.75F, -2);
                ps.mulPose(Axis.XP.rotationDegrees(-rotation.get(be, interp)));
                ps.translate(0, 1.75F, 2);
                return true;
            }, be -> meshes.arm().quads(), be -> meshes.arm().cacheKey());
        }
        return builder;
    }
}
