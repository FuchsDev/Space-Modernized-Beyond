package com.space_beyond.test;

import com.space_m.SpaceMod;
import com.space_beyond.item.ItemGolfClub;
import com.space_m.item.SpaceMaterialItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/** Golfschlaeger (Nutzerwunsch): registriert, 5 Angriffsschaden im Tooltip, Rezept, Treffer schadet. */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GolfClubGameTest {

    private GolfClubGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_golf_club", timeoutTicks = 20)
    public static void golfClub(GameTestHelper helper) {
        check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id("golf_club")), "item golf_club");
        check(com.space_beyond.item.BeyondItems.GOLF_CLUB.get() instanceof ItemGolfClub, "Klasse");
        ItemStack club = new ItemStack(com.space_beyond.item.BeyondItems.GOLF_CLUB.get());
        check(club.getMaxStackSize() == 1, "Stapel 1");
        //? if < 1.21.1 {
        check(((SwordItem) club.getItem()).getDamage() == 4F, "Schaden 4 + Grundschaden 1 = 5 im Tooltip");
        //?}
        check(helper.getLevel().getServer().getRecipeManager().byKey(com.space_beyond.BeyondMod.id("golf_club")).isPresent(), "recipe golf_club");
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        Pig other = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 2));
        check(club.getItem().hurtEnemy(club, pig, other), "hurtEnemy");
        check(club.getDamageValue() == 1, "Haltbarkeit um 1 gesunken: " + club.getDamageValue());
        helper.succeed();
    }
}
