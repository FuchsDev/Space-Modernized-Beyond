package com.space_beyond.dim.sporos.spores;

import org.jetbrains.annotations.NotNull;

import com.hbm_m.effect.HbmEffect;
import com.space_m.item.SpaceEffects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Trankeffekt "Sporeninfektion" (Sporos Runde 4, eigene Erweiterung; Symbol: Figur 4/0 aus {@code potions.png},
 * Farbe gelbgruen). Die Staerke ist die Infektionsstufe minus 1 ({@link SporeInfection#stage}); der Effekt wird
 * nicht verabreicht, sondern von {@link SporeInfection#sync} aus dem Fortschritt gesetzt und laeuft ohne
 * Nachschub nach 3 s aus (Milch nimmt nur das Symbol, nicht die Infektion).
 *
 * <p>Symptome, einmal je Sekunde ({@link #isReady}: Dauer durch 20 teilbar):
 * <ul>
 *   <li>Stufe 1: Husten (Sporenpartikel, leises Geraeusch, 15 %/s), Spieler verlieren Saettigung (Erschoepfung 0,3).</li>
 *   <li>Stufe 2: zusaetzlich Schwaeche und Langsamkeit I (je 3 s, laufend erneuert), Uebelkeit 5 s (8 %/s).</li>
 *   <li>Stufe 3: zusaetzlich Abbaumuedigkeit I und alle 2 s 1 Schaden ({@link SpaceEffects#SPORE_INFECTION_DAMAGE},
 *       durch Ruestung hindurch) - toedlich, wenn man nicht in saubere Luft kommt.</li>
 * </ul>
 */
public class SporeInfectionEffect extends HbmEffect {

    public SporeInfectionEffect() {
        super(MobEffectCategory.HARMFUL, 0xC8D45A, 4, 0);
    }

    @Override
    protected boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    protected void tick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        int stage = amplifier + 1;
        var random = entity.getRandom();

        // Stufe 1: Husten + Hunger
        if (random.nextFloat() < 0.15F) cough(entity);
        if (entity instanceof Player player) player.causeFoodExhaustion(0.3F * stage);

        if (stage >= 2) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false, true));
            if (random.nextFloat() < 0.08F) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, true));
        }

        if (stage >= 3) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false, true));
            if (entity.tickCount % 40 < 20) {
                entity.hurt(com.space_beyond.item.BeyondEffects.sporeInfection(entity.level()), 1.0F);
            }
        }
    }

    /** Husten: Sporenpartikel vor dem Gesicht, leises Geraeusch. */
    public static void cough(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel server)) return;
        var look = entity.getLookAngle();
        double x = entity.getX() + look.x * 0.5D, y = entity.getEyeY() - 0.2D, z = entity.getZ() + look.z * 0.5D;
        server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 6, 0.2D, 0.15D, 0.2D, 0.01D);
        server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_BREATH, SoundSource.NEUTRAL, 0.4F, 0.6F);
    }
}
