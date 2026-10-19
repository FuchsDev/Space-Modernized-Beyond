package com.space_beyond.entity.mob.dungeon;

import com.space_m.SpaceMod;
import com.space_m.atmosphere.AtmosphereEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/** Dungeon-Mobs: jeder laesst sich erzeugen, hat seine Werte, erstickt nicht; Schleime teilen sich beim Tod. */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DungeonMobGameTest {

    private DungeonMobGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_dungeon_mobs", timeoutTicks = 40)
    public static void everyMobSpawns(GameTestHelper helper) {
        check(DungeonMobs.KINDS.size() == 19, "19 Dungeon-Mobs, sind " + DungeonMobs.KINDS.size());
        for (var e : DungeonMobs.TYPES.entrySet()) {
            DungeonMobKind k = DungeonMobs.KINDS.get(e.getKey());
            EntityDungeonMob m = helper.spawn(e.getValue().get(), new BlockPos(1, 2, 1));
            check(m.kind() == k, e.getKey() + ": Bauplan");
            check(Math.abs(m.getMaxHealth() - k.health()) < 0.01, e.getKey() + ": Leben " + m.getMaxHealth());
            check(Math.abs(m.getAttributeValue(Attributes.ATTACK_DAMAGE) - k.damage()) < 0.01, e.getKey() + ": Schaden");
            check(AtmosphereEffects.isSuffocationImmune(m), e.getKey() + ": erstickt");
            m.discard();
        }
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_dungeon_mobs", timeoutTicks = 40)
    public static void sludgeSplits(GameTestHelper helper) {
        EntityDungeonMob m = helper.spawn(DungeonMobs.TOXIC_SLUDGE.get(), new BlockPos(1, 2, 1));
        m.hurt(helper.getLevel().damageSources().genericKill(), 10000.0F);
        helper.runAfterDelay(2, () -> {
            var kids = helper.getLevel().getEntitiesOfClass(EntityDungeonMob.class, new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(6),
                    c -> c.isAlive() && c.getType() == DungeonMobs.TOXIC_SLUDGE.get());
            check(kids.size() == DungeonMobs.KINDS.get("toxic_sludge").split, "Teil-Schleime: " + kids.size());
            check(kids.stream().allMatch(EntityDungeonMob::isSmall), "Teil-Schleime sind klein");
            kids.forEach(EntityDungeonMob::discard);
            helper.succeed();
        });
    }
}
