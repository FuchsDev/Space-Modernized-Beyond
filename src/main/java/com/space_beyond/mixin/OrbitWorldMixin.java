package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.orbit.StationExt;
import com.space_m.orbit.OrbitWorld;
import com.space_m.orbit.OrbitalStation;

import net.minecraft.world.level.Level;

/** Solarpaneele im Orbit: in der Faltphase zwischen zwei Sternsystemen keine Sonne. */
@Mixin(value = OrbitWorld.class, remap = false)
public abstract class OrbitWorldMixin {

    @Inject(method = "getSunPower", at = @At("HEAD"), cancellable = true)
    private static void beyond$noSunInFold(Level level, int x, int z, CallbackInfoReturnable<Float> cir) {
        OrbitalStation station = OrbitalStation.getStationFromPosition(level, x, z);
        if (StationExt.isInterstellarTransfer(station)) cir.setReturnValue(0F);
    }
}
