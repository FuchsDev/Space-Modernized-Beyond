package com.space_beyond.test;

import com.space_m.SpaceMod;
import com.space_m.item.SpaceNuclearItems;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Schallplatten des Originals (record_gs/gp/el, {@code ItemModRecord}): im Tag {@code minecraft:music_discs} und in
 * der Jukebox abspielbar. Ohne Tag nimmt die 1.20.1-Jukebox die Platte nicht an (sie verschwand beim Einlegen).
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RecordGameTest {

    private RecordGameTest() {}

    private static void check(boolean c, String msg) {
        if (!c) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_records", timeoutTicks = 40)
    public static void recordsPlayInJukebox(GameTestHelper helper) {
        for (RegistrySupplier<Item> rec : java.util.List.of(SpaceNuclearItems.RECORD_GS, SpaceNuclearItems.RECORD_GP, SpaceNuclearItems.RECORD_EL)) {
            ItemStack stack = new ItemStack(rec.get());
            //? if < 1.21.1 {
            check(stack.is(ItemTags.MUSIC_DISCS), rec.getId() + " fehlt im Tag minecraft:music_discs");
            BlockPos pos = new BlockPos(1, 1, 1);
            helper.setBlock(pos, Blocks.JUKEBOX);
            if (helper.getBlockEntity(pos) instanceof net.minecraft.world.level.block.entity.JukeboxBlockEntity jukebox) {
                jukebox.setFirstItem(stack.copy());
                check(!jukebox.getFirstItem().isEmpty(), rec.getId() + " wird von der Jukebox nicht angenommen");
                check(helper.getBlockState(pos).getValue(JukeboxBlock.HAS_RECORD), rec.getId() + ": Jukebox ohne Platte");
                check(jukebox.isRecordPlaying(), rec.getId() + " spielt nicht");
            } else {
                throw new GameTestAssertException("Jukebox ohne Block-Entity");
            }
            helper.setBlock(pos, Blocks.AIR);
            //?}
        }
        helper.succeed();
    }
}
