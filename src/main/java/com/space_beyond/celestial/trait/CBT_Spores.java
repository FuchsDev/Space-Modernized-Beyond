package com.space_beyond.celestial.trait;

import com.space_m.celestial.trait.CelestialBodyTrait;

import org.jetbrains.annotations.Nullable;

import com.space_m.celestial.CelestialBody;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * Sporen in der Atmosphaere eines Koerpers (eigene Erweiterung fuer Sporos, kein NTM-Original). Datenhaltung
 * (Standardwert am Koerper, Ueberschreibung im {@code SolarSystemWorldSavedData} wie alle Traits, Befehl
 * {@code /space spores}) und Sync ({@code CelestialTraitsPacket}, Index nach allen Original-Traits). Die Wirkung
 * (Runde 4) rechnet {@code com.space_beyond.dim.sporos.spores.SporeExposure}: Aufnahme je Sekunde = Dichte x Biomfaktor x
 * Ortsfaktor x Virulenz, Infektionsfortschritt und Symptome in {@code SporeInfection}.
 *
 * <ul>
 *   <li>{@link #density}: Sporendichte der Luft 0..1 (0 = keine, 1 = dichte Sporenwolke), mal Biomfaktor
 *       ({@code BiomeGenBaseSporos.sporeFactorAt}) = Exposition.</li>
 *   <li>{@link #virulence}: Infektionsstaerke je Exposition 0..1 (skaliert die Aufnahme).</li>
 *   <li>{@link #strain}: Stamm/Variante (fuer spaetere Mutationen, Filter oder Gegenmittel; noch ohne Wirkung).</li>
 * </ul>
 * NBT: {@code d}, {@code v}, {@code s}.
 */
public class CBT_Spores extends CelestialBodyTrait {

    public static final float DEFAULT_DENSITY = 0.35F;
    public static final float DEFAULT_VIRULENCE = 0.2F;
    public static final int DEFAULT_STRAIN = 1;

    public float density;
    public float virulence;
    public int strain;

    public CBT_Spores() {
        this(DEFAULT_DENSITY, DEFAULT_VIRULENCE, DEFAULT_STRAIN);
    }

    public CBT_Spores(float density, float virulence, int strain) {
        this.density = Mth.clamp(density, 0F, 1F);
        this.virulence = Mth.clamp(virulence, 0F, 1F);
        this.strain = strain;
    }

    /** Sporen-Trait der Welt (Koerper der Dimension, mit Ueberschreibungen), null = sporenfrei. */
    @Nullable
    public static CBT_Spores of(Level level) {
        return CelestialBody.getTrait(level, CBT_Spores.class);
    }

    /** Sporendichte der Welt (0 ohne Trait). */
    public static float densityAt(Level level) {
        CBT_Spores spores = of(level);
        return spores == null ? 0F : spores.density;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putFloat("d", density);
        nbt.putFloat("v", virulence);
        nbt.putInt("s", strain);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        density = Mth.clamp(nbt.getFloat("d"), 0F, 1F);
        virulence = Mth.clamp(nbt.getFloat("v"), 0F, 1F);
        strain = nbt.getInt("s");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeFloat(density);
        buf.writeFloat(virulence);
        buf.writeInt(strain);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        density = buf.readFloat();
        virulence = buf.readFloat();
        strain = buf.readInt();
    }
}
