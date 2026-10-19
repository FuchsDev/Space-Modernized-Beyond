package com.space_beyond.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.client.compat.ShaderCompat;

/**
 * Shader-Unterstuetzung der Planeten: Iris (NeoForge, Paket {@code net.irisshaders}) bzw. Oculus (Forge, je nach
 * Version {@code net.irisshaders} oder {@code net.coderbot}) waehlt das Programm eines Shaderpacks ueber
 * {@code Iris.getCurrentDimension()}. Luftleere Space-Dimensionen bekommen hier das End-Programm, siehe
 * {@link ShaderCompat}. Optional: ohne Iris/Oculus wird nichts angewendet.
 */
@Pseudo
@Mixin(targets = {"net.irisshaders.iris.Iris", "net.coderbot.iris.Iris"}, remap = false)
public abstract class IrisDimensionMixin {

    @Inject(method = "getCurrentDimension", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private static void beyond$planetPrograms(CallbackInfoReturnable<Object> cir) {
        Object end = ShaderCompat.endDimensionFor(cir.getReturnValue());
        if (end != null) cir.setReturnValue(end);
    }
}
