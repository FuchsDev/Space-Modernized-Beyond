package com.space_beyond.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.space_m.rocket.RocketPart;

/** {@code RocketPart.define} ist paketprivat; die Beyond-Triebwerke ({@code ProgressionParts}) tragen sich hierueber ein. */
@Mixin(value = RocketPart.class, remap = false)
public interface RocketPartInvoker {
    @Invoker("define")
    static void beyond$define(String id, RocketPart.PartType type, RocketPart.PartSize top, RocketPart.PartSize bottom, @Nullable Object kind,
                              int tank, int mass, int thrust, int isp, double height, double guiheight, String model, String texture,
                              @Nullable String deployed, @Nullable String shroud, boolean hideInventoryModel, @Nullable String renderer) {
        throw new AssertionError();
    }
}
