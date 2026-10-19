package com.space_beyond.test;

import com.space_m.SpaceMod;
import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_beyond.entity.mob.infected.EntityBlindcap;
import com.space_beyond.entity.mob.infected.InfectedMobs;
import com.space_beyond.item.survival.EntityMolotov;
import com.space_beyond.item.survival.EntityNailBomb;
import com.space_beyond.item.survival.ItemFieldKit;
import com.space_beyond.item.survival.ItemShiv;
import com.space_beyond.item.survival.SurvivalItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/** Ueberlebens-Set: Registrierung/Rezepte, Essen, Feldverband, Shiv-Heimlichstich, Molotow-Feuer, Nagelbombe. */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SurvivalItemsGameTest {

    private SurvivalItemsGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_survival", timeoutTicks = 40)
    public static void survivalRegistries(GameTestHelper helper) {
        var recipes = helper.getLevel().getServer().getRecipeManager();
        for (String n : new String[] {"canned_beans", "jerky", "ration_bar", "alcohol", "field_kit", "molotov", "nail_bomb", "shiv", "machete", "bat", "nail_bat"}) {
            check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id(n)), "item " + n);
        }
        for (String r : new String[] {"jerky_smoking", "jerky_campfire", "ration_bar", "alcohol", "field_kit", "molotov", "nail_bomb", "shiv", "machete", "bat", "nail_bat"}) {
            check(recipes.byKey(com.space_beyond.BeyondMod.id("survival/" + r)).isPresent(), "recipe survival/" + r);
        }
        check(BuiltInRegistries.ENTITY_TYPE.containsKey(com.space_beyond.BeyondMod.id("molotov")) && BuiltInRegistries.ENTITY_TYPE.containsKey(com.space_beyond.BeyondMod.id("nail_bomb")), "Wurfgeschoss-Entities");
        //? if < 1.21.1 {
        check(new ItemStack(SurvivalItems.CANNED_BEANS.get()).isEdible() && new ItemStack(SurvivalItems.JERKY.get()).isEdible(), "essbar");
        check(SurvivalItems.tooltipDamage(SurvivalItems.MACHETE.get()) == 6F && SurvivalItems.tooltipDamage(SurvivalItems.BAT.get()) == 4F
                && SurvivalItems.tooltipDamage(SurvivalItems.NAIL_BAT.get()) == 7F && SurvivalItems.tooltipDamage(SurvivalItems.SHIV.get()) == 3F, "Tooltip-Schaden 6/4/7/3");
        //?}
        check(new ItemStack(SurvivalItems.SHIV.get()).getMaxDamage() == ItemShiv.USES, "Shiv 5 Einsaetze");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_survival", timeoutTicks = 40)
    public static void fieldKitAndShiv(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        pig.setHealth(2F);
        pig.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
        ItemStack kit = new ItemStack(SurvivalItems.FIELD_KIT.get(), 2);
        ItemFieldKit item = (ItemFieldKit) kit.getItem();
        //? if < 1.21.1 {
        check(item.getUseDuration(kit) == ItemFieldKit.USE_TICKS, "64 Ticks anlegen");
        //?} else {
        /*check(item.getUseDuration(kit, pig) == ItemFieldKit.USE_TICKS, "64 Ticks anlegen");*/
        //?}
        item.finishUsingItem(kit, level, pig);
        check(pig.getHealth() == pig.getMaxHealth() && !pig.hasEffect(MobEffects.POISON) && kit.getCount() == 1, "Feldverband heilt, entgiftet, verbraucht");

        // Shiv: Blindhut von hinten, Schwein schleicht -> sofort tot; frontal nur normaler Schaden
        EntityBlindcap blind = InfectedMobs.BLINDCAP.get().create(level);
        BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1));
        blind.setNoAi(true);
        blind.moveTo(base.getX() + 2.5D, base.getY(), base.getZ() + 0.5D, 90F, 0F); // schaut nach -x, Schwein steht bei +x
        blind.setYHeadRot(90F);
        blind.yBodyRot = 90F;
        level.addFreshEntity(blind);
        pig.setPos(base.getX() + 4.5D, base.getY(), base.getZ() + 0.5D);
        pig.setShiftKeyDown(true);
        check(ItemShiv.isStealthStab(pig, blind), "Heimlichstich erkannt");
        ItemStack shiv = new ItemStack(SurvivalItems.SHIV.get());
        shiv.getItem().hurtEnemy(shiv, blind, pig);
        check(blind.isDeadOrDying() || blind.getHealth() <= 0F, "Blindhut sofort tot: " + blind.getHealth());
        check(shiv.getDamageValue() == 1, "Shiv eine Haltbarkeit weniger");
        pig.setShiftKeyDown(false);
        EntityBlindcap blind2 = InfectedMobs.BLINDCAP.get().create(level);
        blind2.setNoAi(true);
        blind2.moveTo(base.getX() + 2.5D, base.getY(), base.getZ() + 0.5D, 90F, 0F);
        level.addFreshEntity(blind2);
        check(!ItemShiv.isStealthStab(pig, blind2), "ohne Schleichen kein Heimlichstich");
        helper.succeed();
    }

    /** Molotow entzuendet Boden und Wesen, Nagelbombe verletzt im Radius ohne Blockschaden. */
    @GameTest(template = "empty3x3x3", batch = "space_survival", timeoutTicks = 40)
    public static void molotovAndNailBomb(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1)).above(30);
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) level.setBlock(base.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        pig.setNoAi(true);
        pig.setPos(base.getX() + 1.5D, base.getY(), base.getZ() + 0.5D);
        EntityMolotov molotov = new EntityMolotov(SurvivalItems.MOLOTOV_ENTITY.get(), level);
        molotov.moveTo(base.getX() + 0.5D, base.getY() + 0.5D, base.getZ() + 0.5D, 0F, 0F);
        molotov.ignite(level, base);
        int fire = 0;
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) if (level.getBlockState(base.offset(dx, 0, dz)).is(Blocks.FIRE)) fire++;
        check(fire >= 9, "Feuerflaeche: " + fire);
        check(pig.isOnFire(), "Schwein brennt");
        check(level.getBlockState(base.below()).is(Blocks.STONE), "kein Blockschaden");

        Pig pig2 = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 2));
        pig2.setNoAi(true);
        pig2.setPos(base.getX() + 0.5D, base.getY() + 3.0D, base.getZ() + 2.5D);
        pig2.clearFire();
        float before = pig2.getHealth();
        pig2.invulnerableTime = 0;
        EntityNailBomb bomb = new EntityNailBomb(SurvivalItems.NAIL_BOMB_ENTITY.get(), level);
        bomb.detonate(level, new Vec3(base.getX() + 0.5D, base.getY() + 3.0D, base.getZ() + 0.5D));
        check(pig2.getHealth() < before, "Nagelbombe verletzt: " + pig2.getHealth() + " < " + before);
        check(level.getBlockState(base.below()).is(Blocks.STONE), "Nagelbombe ohne Blockschaden");
        check(SporeInfection.get(pig2) == 0F, "keine Sporen von Waffen");
        helper.succeed();
    }
}
