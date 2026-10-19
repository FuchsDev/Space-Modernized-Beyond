package com.space_beyond.orbit.propulsion.client;

import com.space_m.orbit.propulsion.client.PropulsionModels;

import java.util.List;

import com.hbm_m.client.render.UvShiftConsumer;
import com.hbm_m.client.render.implementations.ObjBerHelper;
import com.hbm_m.client.render.machine.MachineRenderers;
import com.hbm_m.util.BobMathUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_beyond.client.render.ObjAtlasMesh;
import com.space_beyond.client.render.RaptorMachineRenders;
import com.space_beyond.orbit.client.OrbitRaptorRenders;
import com.space_m.orbit.client.RenderOrbitalStation;
import com.space_m.orbit.propulsion.PropulsionBlockEntities;
import com.space_m.orbit.propulsion.TileEntityMachineHTR3;
import com.space_m.orbit.propulsion.TileEntityMachineHTRF4;
import com.space_m.orbit.propulsion.TileEntityMachineHTRNeo;
import com.space_m.orbit.propulsion.TileEntityOrbitalStationComputer;
import com.space_m.orbit.propulsion.TileEntityOrbitalStationLauncher;
import com.space_m.orbit.propulsion.TileEntityXenonThruster;
import com.space_m.rocket.client.RocketPronter;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Stationsantrieb auf Raptors Renderpfad. Transformationen 1:1 aus {@link RenderHTRF4}, {@link RenderOrbitalComputer},
 * {@link RenderHTR3}, {@link RenderHTRNeo}, {@link RenderXenonThruster} und {@link RenderOrbitalStationLauncher}.
 *
 * <p>Immediate (Hooks) bleiben: die Aufhaengung des HTR3 (zeitabhaengige Streckung entlang Z - Instancing kennt keine
 * Skalierung), die Abgasstrahlen von HTRNeo/Xenon (durchscheinend, voll hell, gestreckt, UV-Lauf) und die Rakete im
 * Startport ({@link RocketPronter}).
 */
public final class PropulsionRaptorRenders {

    private PropulsionRaptorRenders() {}

    private static final ObjAtlasMesh HTRF4 = ObjAtlasMesh.all(PropulsionModels.HTRF4, PropulsionModels.LPW2_TEX, "space_m/htrf4");

    private static final ObjAtlasMesh COMPUTER_BODY = ObjAtlasMesh.allExceptIgnoreCase(PropulsionModels.ORBITAL_COMPUTER,
            PropulsionModels.ORBITAL_COMPUTER_TEX, "space_m/orbital_computer/body", "drive");
    private static final ObjAtlasMesh COMPUTER_DRIVE = ObjAtlasMesh.parts(PropulsionModels.ORBITAL_COMPUTER,
            PropulsionModels.ORBITAL_COMPUTER_TEX, "space_m/orbital_computer/drive", "drive");

    private static final ObjAtlasMesh HTR3_STATIC = htr3("static", "Center", "PipeL1", "PipeL2", "PipeR1", "PipeR2");
    private static final ObjAtlasMesh HTR3_PISTON = htr3("piston", "Piston");
    private static final ObjAtlasMesh HTR3_ENGINE = htr3("engine", "Engine");
    private static final ObjAtlasMesh HTR3_SHROUD_H = htr3("shroud_h", "ShroudH");
    private static final ObjAtlasMesh HTR3_SHROUD_V = htr3("shroud_v", "ShroudV");
    private static final ObjAtlasMesh HTR3_COVER = htr3("cover", "CoverTop", "CoverBottom");

    private static final ObjAtlasMesh NEO_STATIC = ObjAtlasMesh.parts(PropulsionModels.HTRF4_NEO, PropulsionModels.HTRF4_NEO_TEX,
            "space_m/htrf4_neo/static", "Base", "Engine");
    private static final ObjAtlasMesh NEO_ROTORS = ObjAtlasMesh.parts(PropulsionModels.HTRF4_NEO, PropulsionModels.HTRF4_NEO_TEX,
            "space_m/htrf4_neo/rotors", "Rotor", "Rotor2");

    private static final ObjAtlasMesh XENON_BODY = ObjAtlasMesh.parts(PropulsionModels.XENON_THRUSTER, PropulsionModels.XENON_THRUSTER_TEX,
            "space_m/xenon_thruster/body", "Thruster");

    private static final OrbitRaptorRenders.PortMeshes LAUNCHER_PORT =
            OrbitRaptorRenders.PortMeshes.of(RenderOrbitalStation.DOCKING_PORT_LAUNCHER_TEX, "space_m/orbital_station_launcher");

    private static ObjAtlasMesh htr3(String key, String... parts) {
        return ObjAtlasMesh.parts(PropulsionModels.HTR3, PropulsionModels.LPW2_TEX, "space_m/htr3/" + key, parts);
    }

    public static void register() {
        // RenderHTRF4: T(0.5, -2, 0.5), rotateThruster(meta), renderAll (lpw2)
        MachineRenderers.machine("space_m/htrf4", PropulsionBlockEntities.HTRF4.get(), TileEntityMachineHTRF4.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, -2.0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("all", be -> HTRF4.quads(), be -> HTRF4.cacheKey())
                .viewDistance(256)
                .register();

        // RenderOrbitalComputer: T(0.5, 0, 0.5), Meta (2: 0, 4: 90, 3: 180, 5: 270); renderAllExcept("Drive"), drive mit
        // Laufwerk. Kein getViewDistance -> Vanilla 64.
        MachineRenderers.machine("space_m/orbital_computer", PropulsionBlockEntities.ORBITAL_STATION_COMPUTER.get(), TileEntityOrbitalStationComputer.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, 0, 0.5);
                    a.rotate(RaptorMachineRenders.rot0(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("body", be -> COMPUTER_BODY.quads(), be -> COMPUTER_BODY.cacheKey())
                .dynamicPart("drive", be -> be.hasDrive ? COMPUTER_DRIVE.quads() : List.of(),
                        be -> be.hasDrive ? COMPUTER_DRIVE.cacheKey() : "off")
                .viewDistance(64)
                .register();

        // RenderHTR3: T(0.5, -3, 0.5), rotateThruster(meta); ExhaustVacuum wird nie gezeichnet
        MachineRenderers.machine("space_m/htr3", PropulsionBlockEntities.HTR3.get(), TileEntityMachineHTR3.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, -3.0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("static", be -> HTR3_STATIC.quads(), be -> HTR3_STATIC.cacheKey())
                .dynamicPart("piston", (be, f, gt, ps) -> {
                    Htr3State s = Htr3State.of(be, f);
                    ps.translate(0, 0, -s.sway * 0.125);
                    ps.translate(0, 0, s.piston * 0.375D + 0.375D);
                    return true;
                }, be -> HTR3_PISTON.quads(), be -> HTR3_PISTON.cacheKey())
                .dynamicPart("engine", (be, f, gt, ps) -> {
                    Htr3State s = Htr3State.of(be, f);
                    double magnitude = 2D;
                    ps.translate(0, 0, -s.sway * 0.125);
                    ps.translate(0, 3.5, -1);
                    ps.mulPose(Axis.YP.rotationDegrees((float) (s.v * magnitude)));
                    ps.mulPose(Axis.XP.rotationDegrees((float) (s.h * magnitude)));
                    ps.translate(0, -3.5, 1);
                    return true;
                }, be -> HTR3_ENGINE.quads(), be -> HTR3_ENGINE.cacheKey())
                .dynamicPart("shroud_h", (be, f, gt, ps) -> {
                    ps.translate(0, -Htr3State.of(be, f).h * 0.125D, 0);
                    return true;
                }, be -> HTR3_SHROUD_H.quads(), be -> HTR3_SHROUD_H.cacheKey())
                .dynamicPart("shroud_v", (be, f, gt, ps) -> {
                    ps.translate(Htr3State.of(be, f).v * 0.125D, 0, 0);
                    return true;
                }, be -> HTR3_SHROUD_V.quads(), be -> HTR3_SHROUD_V.cacheKey())
                .dynamicPart("cover", (be, f, gt, ps) -> {
                    ps.translate(0, 0, -Htr3State.of(be, f).cover * 0.125);
                    return true;
                }, be -> HTR3_COVER.quads(), be -> HTR3_COVER.cacheKey())
                // Aufhaengung: zeitabhaengige Streckung entlang Z -> bleibt immediate (keine Verzerrung im Instancing)
                .hook((rocket, f, ps, buf, light, overlay, api) -> {
                    Htr3State s = Htr3State.of(rocket, f);
                    VertexConsumer vc = buf.getBuffer(RenderType.entityCutout(PropulsionModels.LPW2_TEX));
                    ps.pushPose();
                    ps.translate(0, 0, 3.5);
                    ps.scale(1, 1, (float) ((3 + s.cover * 0.125) / 3));
                    ps.translate(0, 0, -3.5);
                    PropulsionModels.HTR3.renderPart("SuspensionBottom", ps, vc, light);
                    PropulsionModels.HTR3.renderPart("SuspensionTop", ps, vc, light);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();

        // RenderHTRNeo: T(0.5, -2, 0.5), rotateThruster(meta); Base + Engine starr, Rotoren um Z auf 2.5; Abgas immediate
        MachineRenderers.machine("space_m/htrf4_neo", PropulsionBlockEntities.HTRF4_NEO.get(), TileEntityMachineHTRNeo.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, -2.0, 0.5);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                })
                .dynamicPart("static", be -> NEO_STATIC.quads(), be -> NEO_STATIC.cacheKey())
                .dynamicPart("rotors", (be, f, gt, ps) -> {
                    float rot = be.prevRotor + (be.rotor - be.prevRotor) * f;
                    ps.translate(0, 2.5F, 0);
                    ps.mulPose(Axis.ZP.rotationDegrees(rot));
                    ps.translate(0, -2.5F, 0);
                    return true;
                }, be -> NEO_ROTORS.quads(), be -> NEO_ROTORS.cacheKey())
                .hook((rocket, interp, ps, buf, light, overlay, api) -> {
                    float trailStretch = rocket.getLevel() != null ? rocket.getLevel().random.nextFloat() : 0F;
                    trailStretch = 1.2F - (trailStretch * trailStretch * 0.1F);
                    trailStretch *= rocket.thrustAmount;
                    if (trailStretch <= 0) return;
                    ps.pushPose();
                    ps.translate(0, 0, 12);
                    ps.scale(1, 1, trailStretch);
                    ps.translate(0, 0, -12);

                    PropulsionModels.HTRF4_NEO.renderPartEntity("Exhaust", ps,
                            buf.getBuffer(RenderType.entityTranslucentEmissive(PropulsionModels.HTRF4_EXHAUST_TEX)),
                            0xF000F0, OverlayTexture.NO_OVERLAY, rocket.plasmaR, rocket.plasmaG, rocket.plasmaB, rocket.thrustAmount);

                    long time = System.currentTimeMillis();
                    double sparkleSpin = time / 250D % 1D;
                    float a = rocket.thrustAmount * 0.75F;
                    VertexConsumer sparkle = new UvShiftConsumer(buf.getBuffer(RenderType.eyes(PropulsionModels.PLASMA_SPARKLE_TEX)), 0F, (float) sparkleSpin, 0F, 1F);
                    PropulsionModels.HTRF4_NEO.renderPartEntity("Exhaust", ps, sparkle, 0xF000F0, OverlayTexture.NO_OVERLAY,
                            Math.min(rocket.plasmaR * 2 * a, 1F), Math.min(rocket.plasmaG * 2 * a, 1F), Math.min(rocket.plasmaB * 2 * a, 1F), 1F);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();

        // RenderXenonThruster: T(0.5, -1, 0.5), R(-90), Meta (2: 90, 4: 180, 3: 270, 5: 0), T(0, 0, -0.5);
        // Thruster starr, Abgas immediate
        MachineRenderers.machine("space_m/xenon_thruster", PropulsionBlockEntities.XENON_THRUSTER.get(), TileEntityXenonThruster.class)
                .blockTransform((be, a) -> {
                    a.translate(0.5, -1.0, 0.5);
                    a.rotate(-90, 0, 1, 0);
                    a.rotate(RaptorMachineRenders.rot90(ObjBerHelper.meta(be)), 0, 1, 0);
                    a.translate(0, 0, -0.5);
                })
                .dynamicPart("body", be -> XENON_BODY.quads(), be -> XENON_BODY.cacheKey())
                .hook((rocket, interp, ps, buf, light, overlay, api) -> {
                    float trailStretch = rocket.getLevel() != null ? rocket.getLevel().random.nextFloat() : 0F;
                    trailStretch = 1.2F - (trailStretch * trailStretch * 0.2F);
                    trailStretch *= rocket.thrustAmount;
                    if (trailStretch <= 0) return;
                    ps.pushPose();
                    ps.translate(0, 0, -1F);
                    ps.scale(1, 1, trailStretch);
                    ps.translate(0, 0, 1F);
                    PropulsionModels.XENON_THRUSTER.renderPartEntity("Exhaust", ps,
                            buf.getBuffer(RenderType.entityTranslucentEmissive(PropulsionModels.XENON_EXHAUST_TEX)),
                            0xF000F0, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, rocket.thrustAmount);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();

        // RenderOrbitalStation (Startport-Zweig): Ring + Arme mit Textur docking_port_launcher; Rakete immediate
        OrbitRaptorRenders.port(
                        MachineRenderers.machine("space_m/orbital_station_launcher", PropulsionBlockEntities.ORBITAL_STATION_LAUNCHER.get(),
                                TileEntityOrbitalStationLauncher.class),
                        LAUNCHER_PORT, (l, interp) -> l.prevRot + (l.rot - l.prevRot) * interp)
                .hook((launcher, interp, ps, buf, light, overlay, api) -> {
                    if (launcher.rocket == null || launcher.rocket.extraIssues.size() != 0) return;
                    ps.pushPose();
                    ps.translate(0, -launcher.rocket.getHeight() - 0.5, 0);
                    RocketPronter.prontRocket(launcher.rocket, ps, buf, light, false);
                    ps.popPose();
                })
                .viewDistance(256)
                .register();
    }

    /** Zeitgroessen von {@link RenderHTR3} ({@code t = lastTime + (time - lastTime) * interp}). */
    private record Htr3State(double sway, double h, double v, double piston, double cover) {
        static Htr3State of(TileEntityMachineHTR3 rocket, float interp) {
            double t = rocket.lastTime + (rocket.time - rocket.lastTime) * interp;

            double swayTimer = (t / 3D) % (Math.PI * 4);
            double sway = (Math.sin(swayTimer) + Math.sin(swayTimer * 2) + Math.sin(swayTimer * 4) + 2.23255D) * 0.5;

            double bellTimer = (t / 5D) % (Math.PI * 4);
            double h = (Math.sin(bellTimer + Math.PI) + Math.sin(bellTimer * 1.5D)) / 1.90596D;
            double v = (Math.sin(bellTimer) + Math.sin(bellTimer * 1.5D)) / 1.90596D;

            double pistonTimer = (t / 5D) % (Math.PI * 2);
            double piston = BobMathUtil.sps(pistonTimer);

            double coverTimer = (t / 5D) % (Math.PI * 4);
            double cover = (Math.sin(coverTimer) + Math.sin(coverTimer * 2) + Math.sin(coverTimer * 4)) * 0.5;
            return new Htr3State(sway, h, v, piston, cover);
        }
    }
}
