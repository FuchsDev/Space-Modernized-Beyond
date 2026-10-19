package com.space_beyond.test;

import java.util.EnumMap;
import java.util.Map;

import com.space_m.SpaceMod;
import com.space_beyond.celestial.trait.CBT_Spores;
import com.space_m.dim.SpaceDimensions;
import com.space_beyond.dim.sporos.SporosBlocks;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.flora.SporosFlora;
import com.space_beyond.dim.sporos.flora.block.BlockSporePod;
import com.space_beyond.dim.sporos.spores.SporeExposure;
import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_beyond.dim.sporos.spores.SporeInfectionEffect;
import com.space_m.item.SpaceEffects;
import com.space_m.lib.SpaceTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Sporos Runde 4: Sporeninfektion. Registrierung (Effekt, Schadensart, Biomfaktoren), Fortschritt/Stufen/Effekt-
 * Abgleich, Schutz (Untote, dichter Anzug), Symptome je Stufe, Erholung ohne Sporen, Exposition auf Sporos und
 * der Infektionsschub der Sporenkapsel.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SporosInfectionGameTest {

    private SporosInfectionGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static void near(double actual, double expected, double eps, String msg) {
        check(Math.abs(actual - expected) <= eps, msg + ": erwartet " + expected + ", ist " + actual);
    }

    private static MobEffectInstance infection(LivingEntity e) {
        return e.getEffect(SpaceEffects.holder(com.space_beyond.item.BeyondEffects.SPORE_INFECTION));
    }

    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 20)
    public static void sporeInfectionRegistries(GameTestHelper helper) {
        check(BuiltInRegistries.MOB_EFFECT.containsKey(com.space_beyond.BeyondMod.id("spore_infection")), "Effekt spore_infection");
        check(com.space_beyond.item.BeyondEffects.SPORE_INFECTION.get() instanceof SporeInfectionEffect, "Effektklasse");
        var damage = helper.getLevel().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        check(damage.containsKey(com.space_beyond.item.BeyondEffects.SPORE_INFECTION_DAMAGE), "damage_type spore_infection");
        check(com.space_beyond.item.BeyondEffects.sporeInfection(helper.getLevel()).is(com.space_beyond.item.BeyondEffects.SPORE_INFECTION_DAMAGE), "Schadensquelle");

        // Stufen und Klemmung
        check(SporeInfection.stage(0F) == 0 && SporeInfection.stage(0.24F) == 0 && SporeInfection.stage(0.25F) == 1
                && SporeInfection.stage(0.5F) == 2 && SporeInfection.stage(0.8F) == 3 && SporeInfection.stage(1F) == 3, "Stufen");
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        check(SporeInfection.get(pig) == 0F, "frisch: 0");
        SporeInfection.set(pig, 1.7F);
        check(SporeInfection.get(pig) == 1F, "geklemmt auf 1");
        SporeInfection.set(pig, 0F);
        check(!pig.getPersistentData().contains(SporeInfection.NBT_KEY), "0 entfernt den Schluessel");

        // Biomfaktoren
        check(BiomeGenBaseSporos.sporeField.sporeFactor() == 2.0F && BiomeGenBaseSporos.fungalGrove.sporeFactor() == 1.4F
                && BiomeGenBaseSporos.overgrownPlains.sporeFactor() == 0.6F && BiomeGenBaseSporos.lake.sporeFactor() == 0.3F, "Biomfaktoren");
        check(BiomeGenBaseSporos.sporeFactorAt(helper.getLevel(), helper.absolutePos(new BlockPos(1, 1, 1))) == 1.0F, "fremdes Biom -> 1");
        check(CBT_Spores.densityAt(helper.getLevel()) == 0F, "Testwelt (Kerbin) sporenfrei");
        helper.succeed();
    }

    /** Schutz und Abgleich des Effekts mit dem Fortschritt. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 20)
    public static void sporeInfectionProtection(GameTestHelper helper) {
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
        check(!SporeInfection.isProtected(pig), "Schwein ungeschuetzt");
        check(SporeInfection.isImmune(zombie) && SporeInfection.isProtected(zombie), "Untote immun");
        check(!SporeInfection.expose(zombie, 0.5F) && SporeInfection.get(zombie) == 0F, "Untote nehmen nichts auf");

        // Exposition und Effektstufen
        check(SporeInfection.expose(pig, 0.3F) && SporeInfection.get(pig) == 0.3F, "Exposition 0,3");
        MobEffectInstance e = infection(pig);
        check(e != null && e.getAmplifier() == 0, "Stufe 1 -> Effekt Staerke 0");
        SporeInfection.expose(pig, 0.3F);
        e = infection(pig);
        check(e != null && e.getAmplifier() == 1, "Stufe 2 -> Staerke 1");
        SporeInfection.apply(pig, 0.95F);
        e = infection(pig);
        check(e != null && e.getAmplifier() == 2, "Stufe 3 -> Staerke 2");
        SporeInfection.apply(pig, 0.1F);
        check(infection(pig) == null, "Stufe 0 entfernt den Effekt");

        // dichter Anzug aus dem Tag sealed_armor (ein Teil je Slot)
        Map<EquipmentSlot, ItemStack> suit = new EnumMap<>(EquipmentSlot.class);
        for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(SpaceTags.SEALED_ARMOR)) {
            if (h.value() instanceof ArmorItem armor && !suit.containsKey(armor.getEquipmentSlot())) suit.put(armor.getEquipmentSlot(), new ItemStack(h.value()));
        }
        check(suit.size() == 4, "sealed_armor enthaelt alle vier Teile: " + suit.keySet());
        suit.forEach(pig::setItemSlot);
        check(SporeInfection.hasSealedSuit(pig) && SporeInfection.isProtected(pig), "dichter Anzug schuetzt");
        check(!SporeInfection.expose(pig, 0.5F) && SporeInfection.get(pig) == 0.1F, "geschuetzt: keine Aufnahme");
        pig.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        check(!SporeInfection.isProtected(pig), "ohne Helm undicht");
        helper.succeed();
    }

    /** Symptome je Stufe (Effekt-Tick direkt aufgerufen) und Erholung ohne Sporen. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 20)
    public static void sporeInfectionSymptomsAndDecay(GameTestHelper helper) {
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        SporeInfectionEffect effect = (SporeInfectionEffect) com.space_beyond.item.BeyondEffects.SPORE_INFECTION.get();
        check(effect.getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL, "schaedlich");
        float health = pig.getHealth();
        effect.applyEffectTick(pig, 1); // Stufe 2
        check(pig.hasEffect(MobEffects.WEAKNESS) && pig.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Stufe 2: Schwaeche + Langsamkeit");
        check(!pig.hasEffect(MobEffects.DIG_SLOWDOWN) && pig.getHealth() == health, "Stufe 2: kein Schaden");
        pig.invulnerableTime = 0;
        effect.applyEffectTick(pig, 2); // Stufe 3 (tickCount 0 -> Schaden)
        check(pig.hasEffect(MobEffects.DIG_SLOWDOWN), "Stufe 3: Abbaumuedigkeit");
        check(pig.getHealth() < health, "Stufe 3: Schaden " + pig.getHealth() + " < " + health);

        // Erholung: ohne Sporen (Kerbin) sinkt der Fortschritt je Sekundenschritt um DECAY
        SporeInfection.apply(pig, 0.5F);
        check(SporeExposure.exposureOf(pig) == 0F, "keine Exposition ohne Sporen-Trait");
        SporeExposure.tickEntity(pig);
        near(SporeInfection.get(pig), 0.5F - SporeExposure.DECAY, 1e-6, "Erholung");
        check(infection(pig) != null && infection(pig).getAmplifier() == 1, "Effekt bleibt auf Stufe 2");
        for (int i = 0; i < 200; i++) SporeExposure.tickEntity(pig);
        check(SporeInfection.get(pig) < 0.25F && infection(pig) == null, "nach 200 s unter Stufe 1, Effekt weg");
        helper.succeed();
    }

    /** Antimykotikum: registriert, Rezept vorhanden, Einnahme setzt die Infektion auf 0 und nimmt den Effekt. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 20)
    public static void antimycoticCures(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id("antimycotic")), "item antimycotic");
        check(level.getServer().getRecipeManager().byKey(com.space_beyond.BeyondMod.id("sporos/antimycotic")).isPresent(), "recipe sporos/antimycotic");
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        SporeInfection.apply(pig, 0.9F);
        check(infection(pig) != null, "infiziert");
        ItemStack pill = new ItemStack(SporosFlora.ANTIMYCOTIC.get(), 2);
        check(pill.getMaxStackSize() == 16, "Stapel 16");
        ItemStack rest = pill.getItem().finishUsingItem(pill, level, pig);
        check(rest.getCount() == 1, "eine Pille verbraucht: " + rest.getCount());
        check(SporeInfection.get(pig) == 0F && infection(pig) == null, "geheilt");
        helper.succeed();
    }

    /** Sporenkapsel: Platzen infiziert ungeschuetzte Wesen in der Wolke um {@link BlockSporePod#BURST_EXPOSURE}. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 20)
    public static void sporePodBurstInfects(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos soil = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(soil, SporosBlocks.SPOROS_SILT.get().defaultBlockState(), 3);
        level.setBlock(soil.above(), SporosFlora.SPORE_POD.get().defaultBlockState(), 3);
        check(level.getBlockState(soil.above()).is(SporosFlora.SPORE_POD.get()), "Kapsel steht auf Humus");
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 2, 1));
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(0, 2, 1));
        BlockSporePod.burst(level, soil.above());
        check(level.getBlockState(soil.above()).isAir(), "Kapsel geplatzt");
        check(pig.hasEffect(MobEffects.CONFUSION) && pig.hasEffect(MobEffects.WEAKNESS), "Uebelkeit + Schwaeche");
        near(SporeInfection.get(pig), BlockSporePod.BURST_EXPOSURE, 1e-6, "Infektionsschub");
        check(SporeInfection.get(zombie) == 0F, "Untote unbeeindruckt");
        helper.succeed();
    }

    /** Auf Sporos: Exposition unter freiem Himmel &gt; 0, Fortschritt steigt je Sekundenschritt, dichter Anzug 0. */
    @GameTest(template = "empty3x3x3", batch = "space_sporos_spores", timeoutTicks = 600)
    public static void sporeExposureOnSporos(GameTestHelper helper) {
        ServerLevel sporos = helper.getLevel().getServer().getLevel(com.space_beyond.dim.BeyondDimensions.SPOROS);
        // NeoForge-1.21.1-GameTestServer laedt keine Datapack-Dimensionen (s. DimensionGameTest)
        if (sporos == null) {
            helper.succeed();
            return;
        }
        check(CBT_Spores.densityAt(sporos) == CBT_Spores.DEFAULT_DENSITY, "Sporendichte Sporos");
        BlockPos pos = new BlockPos(8, 200, 8);
        sporos.getChunk(pos); // Biom am Ort braucht den Chunk
        Pig pig = EntityType.PIG.create(sporos);
        check(pig != null, "Schwein");
        pig.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        pig.setNoGravity(true);
        sporos.addFreshEntity(pig);
        float factor = BiomeGenBaseSporos.sporeFactorAt(sporos, pos);
        check(factor >= 0.3F && factor <= 2.0F, "Biomfaktor " + factor);
        float exposure = SporeExposure.exposureOf(pig);
        near(exposure, CBT_Spores.DEFAULT_DENSITY * factor * CBT_Spores.DEFAULT_VIRULENCE, 1e-5, "Exposition unter freiem Himmel");
        check(SporeExposure.shelter(pig, pos) == 1F, "freier Himmel");
        SporeExposure.tickEntity(pig);
        near(SporeInfection.get(pig), exposure * SporeExposure.RATE, 1e-6, "Fortschritt nach einer Sekunde");
        // dichter Anzug: nichts
        Map<EquipmentSlot, ItemStack> suit = new EnumMap<>(EquipmentSlot.class);
        for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(SpaceTags.SEALED_ARMOR)) {
            if (h.value() instanceof ArmorItem armor && !suit.containsKey(armor.getEquipmentSlot())) suit.put(armor.getEquipmentSlot(), new ItemStack(h.value()));
        }
        suit.forEach(pig::setItemSlot);
        check(SporeExposure.exposureOf(pig) == 0F, "dichter Anzug: keine Exposition");
        pig.discard();
        helper.succeed();
    }
}
