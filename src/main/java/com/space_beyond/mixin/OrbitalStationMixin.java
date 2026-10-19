package com.space_beyond.mixin;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.space_beyond.celestial.StarSystem;
import com.space_beyond.orbit.BeyondStation;
import com.space_beyond.orbit.StationExt;
import com.space_beyond.orbit.interstellar.FoldDriveNetwork;
import com.space_beyond.orbit.interstellar.IFoldDrive;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_m.orbit.OrbitalStation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

/**
 * Sternsysteme und Planeten-Fortschritt an der Raumstation (im Privat-Repo direkt in {@code OrbitalStation}):
 * Ziele in einem anderen Sternsystem brauchen einen geladenen Faltantrieb ({@link IFoldDrive}, {@link FoldDriveNetwork}),
 * die Faltphase dauert {@link FoldDriveNetwork#TRANSFER_TICKS} und zeigt keine Sonne; Stationen fliegen ein Ziel erst
 * nach dem Boss der vorherigen Stufe an. Reisen im Heimatsystem bleiben unveraendert.
 */
@Mixin(value = OrbitalStation.class, remap = false)
public abstract class OrbitalStationMixin implements BeyondStation {
    @Shadow public CelestialBody orbiting;
    @Shadow public CelestialBody target;
    @Shadow public int errorTimer;
    @Shadow private double eclipseAmount;
    @Shadow @org.spongepowered.asm.mixin.Final private HashSet<com.space_m.orbit.IPropulsion> engines;

    @Unique private final HashSet<IFoldDrive> beyond$foldDrives = new HashSet<>();
    @Unique private boolean beyond$hasFoldDrive;
    @Unique private boolean beyond$foldDriveError;
    @Unique private boolean beyond$progressionLocked;

    @Override public Set<IFoldDrive> beyond$foldDrives() { return beyond$foldDrives; }
    @Override public boolean beyond$hasFoldDrive() { return beyond$hasFoldDrive; }
    @Override public boolean beyond$foldDriveError() { return beyond$foldDriveError; }
    @Override public boolean beyond$progressionLocked() { return beyond$progressionLocked; }

    @Unique
    private OrbitalStation beyond$self() {
        return (OrbitalStation) (Object) this;
    }

    /** Start der Reise: Fortschrittssperre (Server), Faltsprung verbraucht die Ladung eines Faltantriebs. */
    @Inject(method = "travelTo", at = @At(value = "INVOKE",
            target = "Lcom/space_m/orbit/OrbitalStation;setState(Lcom/space_m/orbit/OrbitalStation$StationState;I)V"), cancellable = true)
    private void beyond$beforeDeparture(Level world, CelestialBody target, CallbackInfo ci) {
        if (!world.isClientSide && !com.space_beyond.progression.SpaceProgression.stationMayTravel(target)) {
            beyond$progressionLocked = true;
            errorTimer = 100;
            ci.cancel();
            return;
        }
        if (!StarSystem.sameSystem(orbiting, target)) StationExt.consumeFoldCharge(beyond$self());
    }

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lcom/space_m/orbit/OrbitalStation;hasEngines:Z",
            opcode = org.objectweb.asm.Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void beyond$syncFoldDrives(Level world, CallbackInfo ci) {
        beyond$hasFoldDrive = !beyond$foldDrives.isEmpty();
    }

    /** {@code errorTimer = 0} (Fehleranzeige abgelaufen): auch die Beyond-Fehler loeschen. */
    @Inject(method = "update", at = @At(value = "FIELD", target = "Lcom/space_m/orbit/OrbitalStation;errorTimer:I",
            opcode = org.objectweb.asm.Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER))
    private void beyond$clearErrors(Level world, CallbackInfo ci) {
        beyond$foldDriveError = false;
        beyond$progressionLocked = false;
    }

    /**
     * Anderes System nur mit geladenem Faltantrieb dieser Station. Ohne Triebwerke bleibt es beim Original-Abbruch
     * ({@code engines.size() == 0}), erst danach zaehlt der Faltantrieb.
     */
    @Inject(method = "canTravel", at = @At("HEAD"), cancellable = true)
    private void beyond$checkFoldDrive(CelestialBody from, CelestialBody to, CallbackInfoReturnable<Boolean> cir) {
        if (engines.isEmpty()) return;
        if (!StarSystem.sameSystem(from, to)) {
            if (!StationExt.hasChargedFoldDrive(beyond$self())
                    || !FoldDriveNetwork.withStation(beyond$self(), () -> StarSystem.isReachable(from, to))) {
                beyond$foldDriveError = true;
                errorTimer = 100;
                cir.setReturnValue(false);
                return;
            }
        }
        beyond$foldDriveError = false;
    }

    /** Delta-v im Kontext dieser Station (nur sie darf mit ihrem Faltantrieb ein anderes System anfliegen). */
    @Redirect(method = "canTravel", at = @At(value = "INVOKE",
            target = "Lcom/space_m/celestial/SolarSystem;getDeltaVBetween(Lcom/space_m/celestial/CelestialBody;Lcom/space_m/celestial/CelestialBody;)D"))
    private double beyond$deltaVWithStation(CelestialBody from, CelestialBody to) {
        return FoldDriveNetwork.withStation(beyond$self(), () -> SolarSystem.getDeltaVBetween(from, to));
    }

    /** Faltphase einer interstellaren Reise: fest und lang, unabhaengig vom Schub. */
    @Inject(method = "getTransferTime", at = @At("HEAD"), cancellable = true)
    private void beyond$foldTransferTime(CallbackInfoReturnable<Integer> cir) {
        if (!StarSystem.sameSystem(orbiting, target)) cir.setReturnValue(FoldDriveNetwork.TRANSFER_TICKS);
    }

    /** In der Faltphase zwischen zwei Systemen keine Sonne (nur Sterne). */
    @Inject(method = "getEclipseAmount", at = @At(value = "INVOKE",
            target = "Lcom/space_m/celestial/SolarSystem;calculateSunSize(Lcom/space_m/celestial/CelestialBody;)D"), cancellable = true)
    private void beyond$foldEclipse(Level world, CallbackInfoReturnable<Double> cir) {
        if (StationExt.isInterstellarTransfer(beyond$self())) {
            eclipseAmount = 1;
            cir.setReturnValue(1D);
        }
    }

    @Inject(method = "serialize", at = @At("TAIL"))
    private void beyond$writeSync(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeBoolean(beyond$hasFoldDrive);
        buf.writeBoolean(beyond$foldDriveError);
        buf.writeBoolean(beyond$progressionLocked);
    }

    @Inject(method = "deserialize", at = @At("RETURN"))
    private static void beyond$readSync(FriendlyByteBuf buf, CallbackInfoReturnable<OrbitalStation> cir) {
        OrbitalStationMixin station = (OrbitalStationMixin) (Object) cir.getReturnValue();
        station.beyond$hasFoldDrive = buf.readBoolean();
        station.beyond$foldDriveError = buf.readBoolean();
        station.beyond$progressionLocked = buf.readBoolean();
    }
}
