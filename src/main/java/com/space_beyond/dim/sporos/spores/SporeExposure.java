package com.space_beyond.dim.sporos.spores;

import com.space_m.celestial.CelestialBody;
import com.space_beyond.celestial.trait.CBT_Spores;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.flora.SporosFlora;
import com.space_m.machine.atmo.AtmoBlobs;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Sporenexposition (Sporos Runde 4, eigene Erweiterung): einmal je Sekunde ({@code SERVER_LEVEL_POST}) fuer jedes
 * Lebewesen in einer Welt mit Sporen-Trait ({@link CBT_Spores}, Dichte &gt; 0) die Aufnahme berechnen und den
 * Fortschritt ({@link SporeInfection}) fortschreiben.
 *
 * <p>Aufnahme je Sekunde = Dichte x Biomfaktor ({@link BiomeGenBaseSporos#sporeFactorAt}: Sporenfeld 2,0 ...
 * See 0,3) x Ort ({@link #shelter}: abgedichteter Raum 0, unter Wasser 0, ohne Himmel 0,4) x Virulenz des Traits
 * x {@link #RATE}. Geschuetzte Wesen ({@link SporeInfection#isProtected}) nehmen nichts auf. Ohne Aufnahme sinkt
 * der Fortschritt um {@link #DECAY} je Sekunde (Erholung in sauberer Luft, ~11 min von voll auf null).
 *
 * <p>Richtwerte mit den Standardwerten (Dichte 0,35, Virulenz 0,2): ueberwucherte Ebene unter freiem Himmel
 * ~0,0007/s -> Stufe 1 nach ~6 min, Stufe 3 nach ~19 min; Sporenfeld ~0,0023/s -> Stufe 1 nach ~2 min, Stufe 3
 * nach ~6 min; im Wald/unter Dach entsprechend 0,4 davon.
 *
 * <p>Tod eines infizierten Wesens (Stufe 3, kein Spieler): Sporenwolke und 1-2 Sporenstaub als zusaetzliche Beute.
 *
 * <p>Traeger: Untote atmen nicht und sind immun, tragen die Sporen aber im Gewebe - ein Treffer eines Untoten in
 * einer Sporenwelt (Zombies, Huesker, Ertrunkene der Sporos-Biome) gibt dem Getroffenen {@link #CARRIER_HIT} x Dichte
 * Exposition und eine kleine Sporenwolke.
 */
public final class SporeExposure {

    private SporeExposure() {}

    /** Fortschritt je Sekunde bei Aufnahme 1 (Dichte 1, Faktor 1, frei, Virulenz 1). */
    public static final float RATE = 0.033F;
    /** Rueckgang je Sekunde ohne Aufnahme. */
    public static final float DECAY = 0.0015F;
    /** Ortsfaktor ohne freien Himmel (Wald, Hoehle, offenes Gebaeude). */
    public static final float COVERED = 0.4F;
    /** Infektionsschub je Treffer eines untoten Traegers (mal Sporendichte der Welt, Standard 0,35 -> ~0,035). */
    public static final float CARRIER_HIT = 0.1F;

    private static boolean registered;

    public static void init() {
        if (registered) return;
        registered = true;
        TickEvent.SERVER_LEVEL_POST.register(SporeExposure::onLevelTick);
        EntityEvent.LIVING_DEATH.register(SporeExposure::onDeath);
        EntityEvent.LIVING_HURT.register(SporeExposure::onHurt);
    }

    /** Treffer eines untoten Traegers in einer Sporenwelt: Exposition fuer das Opfer. */
    private static EventResult onHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity.level().isClientSide || amount <= 0F) return EventResult.pass();
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == entity || !SporeInfection.isImmune(attacker)) return EventResult.pass();
        float density = CBT_Spores.densityAt(entity.level());
        if (density <= 0F) return EventResult.pass();
        if (SporeInfection.expose(entity, CARRIER_HIT * density) && entity.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, entity.getX(), entity.getY(0.6D), entity.getZ(), 8, 0.3D, 0.3D, 0.3D, 0.02D);
        }
        return EventResult.pass();
    }

    private static void onLevelTick(ServerLevel level) {
        if (level.getGameTime() % 20 != 7) return; // versetzt zur Atmosphaerenchemie (% 20 == 0)
        float density = CBT_Spores.densityAt(level);
        if (density <= 0F) {
            return;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof LivingEntity living && living.isAlive()) tickEntity(living);
        }
    }

    /** Ein Sekundenschritt fuer ein Lebewesen (auch fuer Tests aufrufbar). */
    public static void tickEntity(LivingEntity entity) {
        float before = SporeInfection.get(entity);
        float exposure = exposureOf(entity);
        float after;
        if (exposure > 0F) {
            after = before + exposure * RATE;
        } else {
            after = before - DECAY;
        }
        if (after != before) SporeInfection.set(entity, after);
        SporeInfection.sync(entity, before);
    }

    /**
     * Aufnahme 0..~2 eines Wesens an seinem Ort: Dichte x Biomfaktor x Ortsfaktor x Virulenz; 0 fuer geschuetzte
     * Wesen und ausserhalb von Sporenwelten.
     */
    public static float exposureOf(LivingEntity entity) {
        Level level = entity.level();
        CBT_Spores spores = CBT_Spores.of(level);
        if (spores == null || spores.density <= 0F || CelestialBody.inOrbit(level)) return 0F;
        if (SporeInfection.isProtected(entity)) return 0F;
        BlockPos pos = entity.blockPosition();
        float shelter = shelter(entity, pos);
        if (shelter <= 0F) return 0F;
        return spores.density * BiomeGenBaseSporos.sporeFactorAt(level, pos) * shelter * spores.virulence;
    }

    /** Ortsfaktor: 0 in abgedichteten Raeumen und unter Wasser, {@link #COVERED} ohne freien Himmel, sonst 1. */
    public static float shelter(LivingEntity entity, BlockPos pos) {
        if (entity.isEyeInFluid(FluidTags.WATER)) return 0F;
        if (AtmoBlobs.hasAtmosphere(entity.level(), pos)) return 0F;
        return entity.level().canSeeSky(pos.above()) ? 1F : COVERED;
    }

    private static EventResult onDeath(LivingEntity entity, DamageSource source) {
        Level level = entity.level();
        if (level.isClientSide || entity instanceof Player || SporeInfection.stage(entity) < 3) return EventResult.pass();
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, entity.getX(), entity.getY(0.5D), entity.getZ(), 30, 0.6D, 0.5D, 0.6D, 0.02D);
            ItemStack dust = new ItemStack(SporosFlora.SPORE_DUST.get(), 1 + level.random.nextInt(2));
            level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(0.5D), entity.getZ(), dust));
        }
        return EventResult.pass();
    }
}
