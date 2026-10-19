package com.space_beyond.dim.sporos.spores;

import com.hbm_m.handler.ArmorRegistry;
import com.hbm_m.handler.HazardClass;
import com.hbm_m.network.InfoToastPacket;
import com.space_m.atmosphere.AtmosphereEffects;
import com.space_m.item.SpaceEffects;
import com.space_m.lib.SpaceTags;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Sporeninfektion eines Lebewesens (Sporos Runde 4, eigene Erweiterung, kein NTM-Original): Datenhaltung und
 * Schutzpruefung. Der Infektionsfortschritt ({@link #get}, 0..1) liegt im Entity-NBT ({@code space_m:spores},
 * wie der Sauerstoffvorrat in {@link AtmosphereEffects}); beim Tod geht er verloren (Forge kopiert die
 * Persistenzdaten des Spielers nicht), eine Wiedergeburt heilt also.
 *
 * <p>Stufen ({@link #stage}): 0 gesund, 1 ab {@link #STAGE_1} (Husten, Hunger), 2 ab {@link #STAGE_2} (Schwaeche,
 * Langsamkeit, Uebelkeit), 3 ab {@link #STAGE_3} (Schaden, Erschoepfung). Ab Stufe 1 traegt das Lebewesen den
 * Effekt {@link SpaceEffects#SPORE_INFECTION} (Staerke = Stufe - 1), der die Symptome ausfuehrt und die Stufe im
 * HUD zeigt; {@link #sync} haelt den Effekt mit dem Fortschritt in Deckung (jede Sekunde aus
 * {@link SporeExposure}).
 *
 * <p>Schutz ({@link #isProtected}): Kreativ-/Zuschauer-Spieler, Untote und erstickungsimmune Wesen, Feinstaub-
 * Filter am Kopf ({@link HazardClass#PARTICLE_FINE}: Gasmaske mit Filter, Schrabidium-/Euphemium-Helm) oder ein
 * vollstaendig dichter Anzug ({@link SpaceTags#SEALED_ARMOR} an allen vier Teilen, dann atmet man aus dem Tank).
 */
public final class SporeInfection {

    private SporeInfection() {}

    /** NBT-Schluessel des Fortschritts im Entity-NBT. */
    public static final String NBT_KEY = "space_m:spores";

    public static final float STAGE_1 = 0.25F;
    public static final float STAGE_2 = 0.5F;
    public static final float STAGE_3 = 0.8F;

    /** Toast-Kennung (HBM {@code InfoToastPacket}), frei neben 969+ (Scanner) und den HBM-Kennungen. */
    public static final int TOAST_ID = 9701;

    private static final EquipmentSlot[] ARMOR = { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD };

    /** Infektionsfortschritt 0..1. */
    public static float get(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        return data.contains(NBT_KEY) ? Mth.clamp(data.getFloat(NBT_KEY), 0F, 1F) : 0F;
    }

    public static void set(LivingEntity entity, float progress) {
        progress = Mth.clamp(progress, 0F, 1F);
        if (progress <= 0F) entity.getPersistentData().remove(NBT_KEY);
        else entity.getPersistentData().putFloat(NBT_KEY, progress);
    }

    /** Stufe 0..3 zu einem Fortschritt. */
    public static int stage(float progress) {
        if (progress >= STAGE_3) return 3;
        if (progress >= STAGE_2) return 2;
        if (progress >= STAGE_1) return 1;
        return 0;
    }

    public static int stage(LivingEntity entity) {
        return stage(get(entity));
    }

    /**
     * Sofortige Exposition (Sporenkapsel, spaeter infizierte Wesen): erhoeht den Fortschritt um {@code amount},
     * sofern das Wesen nicht geschuetzt ist; gleicht danach den Effekt ab. Liefert true, wenn etwas aufgenommen wurde.
     */
    public static boolean expose(LivingEntity entity, float amount) {
        if (entity.level().isClientSide || amount <= 0F || isProtected(entity)) return false;
        float before = get(entity);
        set(entity, before + amount);
        sync(entity, before);
        return true;
    }

    /** Fortschritt direkt setzen (Befehl, Tests) und Effekt abgleichen. */
    public static void apply(LivingEntity entity, float progress) {
        float before = get(entity);
        set(entity, progress);
        sync(entity, before);
    }

    /**
     * Effekt mit dem Fortschritt abgleichen: ab Stufe 1 {@link SpaceEffects#SPORE_INFECTION} mit Staerke
     * Stufe - 1 und 3 s Restdauer (wird jede Sekunde erneuert, laeuft nach dem letzten Abgleich von selbst aus);
     * Stufe 0 entfernt ihn. Spieler bekommen beim Aufstieg der Stufe einen Hinweis ({@code info.spores}).
     */
    public static void sync(LivingEntity entity, float before) {
        if (entity.level().isClientSide) return;
        int stage = stage(entity);
        if (stage == 0) {
            if (entity.hasEffect(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION))) {
                entity.removeEffect(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION));
            }
            return;
        }
        MobEffectInstance current = entity.getEffect(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION));
        if (current == null || current.getAmplifier() != stage - 1 || current.getDuration() < 40) {
            if (current != null && current.getAmplifier() != stage - 1) entity.removeEffect(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION));
            entity.addEffect(new MobEffectInstance(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION), 60, stage - 1, false, true, true));
        }
        if (stage > stage(before) && entity instanceof ServerPlayer player) {
            InfoToastPacket.sendTo(player, "info.spores", 60, TOAST_ID, 0xC8D45A);
        }
    }

    /** Geschuetzt (nimmt keine Sporen auf), s. Klassenkommentar. */
    public static boolean isProtected(LivingEntity entity) {
        if (entity instanceof Player player && (player.getAbilities().instabuild || player.isSpectator())) return true;
        if (isImmune(entity)) return true;
        if (ArmorRegistry.hasProtection(entity, 3, HazardClass.PARTICLE_FINE)) return true;
        return hasSealedSuit(entity);
    }

    /** Untote und erstickungsimmune Wesen atmen nicht. */
    public static boolean isImmune(LivingEntity entity) {
        if (AtmosphereEffects.isSuffocationImmune(entity)) return true;
        //? if < 1.21.1 {
        return entity.getMobType() == net.minecraft.world.entity.MobType.UNDEAD;
        //?} else {
        /*return entity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD);
        *///?}
    }

    /** Alle vier Ruestungsteile dicht ({@link SpaceTags#SEALED_ARMOR}, wie {@code ArmorUtil.getOxygenTank}). */
    public static boolean hasSealedSuit(LivingEntity entity) {
        for (EquipmentSlot slot : ARMOR) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.is(SpaceTags.SEALED_ARMOR)) return false;
        }
        return true;
    }
}
