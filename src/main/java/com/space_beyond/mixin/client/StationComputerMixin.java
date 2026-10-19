package com.space_beyond.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.hbm_m.interfaces.ILookOverlay;
import com.space_beyond.orbit.StationExt;
import com.space_m.orbit.OrbitalStation;
import com.space_m.orbit.propulsion.BlockOrbitalStationComputer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * Stationscomputer-Anzeige: Planeten-Fortschritt (Boss der vorherigen Stufe fehlt) bzw. Ziel in einem anderen System
 * ohne geladenen Faltraum-Antrieb - als erste Zeile, wie im Privat-Repo direkt hinter der Triebwerkspruefung.
 */
@Mixin(value = BlockOrbitalStationComputer.class, remap = false)
public abstract class StationComputerMixin {

    @Redirect(method = "printHook", at = @At(value = "INVOKE",
            target = "Lcom/hbm_m/interfaces/ILookOverlay;printGeneric(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/network/chat/Component;IILjava/util/List;)V",
            ordinal = 1))
    private void beyond$stationLocks(GuiGraphics g, Component title, int titleCol, int bgCol, List<Component> text,
                                     GuiGraphics g2, Level world, BlockPos pos) {
        OrbitalStation station = OrbitalStation.clientStation();
        if (station != null && station.hasEngines) {
            if (StationExt.progressionLocked(station)) {
                text.add(0, Component.translatable("progression.station.locked").withStyle(ChatFormatting.RED));
            } else if (StationExt.foldDriveError(station)) {
                text.add(0, Component.translatable("station.interstellarLocked").withStyle(ChatFormatting.RED));
            }
        }
        ILookOverlay.printGeneric(g, title, titleCol, bgCol, text);
    }
}
