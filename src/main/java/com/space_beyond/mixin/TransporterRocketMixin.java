package com.space_beyond.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_m.fluid.trait.FT_Rocket;
import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.CelestialBody;
import com.space_m.rocket.transport.TransporterBlockEntityBase;
import com.space_m.rocket.transport.TransporterRocketBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;

/** Transporter: kein Transport in ein anderes Sternsystem (die 64 000-mB-Deckelung machte es sonst moeglich). */
@Mixin(value = TransporterRocketBlockEntity.class, remap = false)
public abstract class TransporterRocketMixin {

    @Inject(method = "sendCost", at = @At("HEAD"), cancellable = true)
    private void beyond$noInterstellar(TransporterBlockEntityBase linked, int mass, FT_Rocket fuelStats, CallbackInfoReturnable<Integer> cir) {
        BlockEntity self = (BlockEntity) (Object) this;
        CelestialBody.Target from = CelestialBody.getTarget(self.getLevel(), self.getBlockPos().getX(), self.getBlockPos().getZ());
        CelestialBody.Target to = CelestialBody.getTarget(linked.getLevel(), linked.getBlockPos().getX(), linked.getBlockPos().getZ());
        if (!StarSystem.isReachable(from.body, to.body)) cir.setReturnValue(Integer.MAX_VALUE);
    }
}
