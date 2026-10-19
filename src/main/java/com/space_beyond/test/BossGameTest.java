package com.space_beyond.test;

import com.space_m.SpaceMod;
import com.space_m.atmosphere.AtmosphereEffects;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.sporos.structure.StructureMobs;
import com.space_beyond.dim.wemburos.WemburosBlocks;
import com.space_beyond.dim.wemburos.structure.WemburosStructures;
import com.space_beyond.entity.mob.boss.BossMobs;
import com.space_beyond.entity.mob.boss.EntityUnstableSovereign;
import com.space_beyond.entity.mob.boss.ItemOrbitalStrikeRod;
import com.space_beyond.entity.mob.boss.OrbitalBeamEntity;
import com.space_beyond.entity.mob.boss.OrbitalStrikes;
import com.hbm_m.platform.PlatformHooks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if >= 1.21.1 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

/** Boss "The Unstable Sovereign": Werte, Luftimmunitaet, Beute (Orbital Strike Rod und ihre Modi), Orbitalstrahl, Platz im Wemburos-Thronsaal. */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BossGameTest {

    private BossGameTest() {}

    private static void check(boolean c, String msg) {
        if (!c) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_boss", timeoutTicks = 40)
    public static void sovereignStatsAndLoot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        EntityUnstableSovereign boss = helper.spawn(BossMobs.UNSTABLE_SOVEREIGN.get(), new BlockPos(1, 1, 1));
        check(boss.getAttributeValue(Attributes.MAX_HEALTH) == 5000.0D && boss.getHealth() == 5000.0F, "5000 Leben (letzter Boss)");
        check(boss.state() == EntityUnstableSovereign.IDLE && !boss.phaseTwo() && boss.isNoGravity(), "wartet schwebend auf dem Thron");
        check(boss.isPersistenceRequired() && boss.fireImmune(), "dauerhaft und feuerfest");
        check(AtmosphereEffects.isSuffocationImmune(boss), "braucht keine Luft (Wemburos)");

        //? if < 1.21.1 {
        LootTable table = level.getServer().getLootData().getLootTable(boss.getLootTable());
        //?} else {
        /*LootTable table = level.getServer().reloadableRegistries().getLootTable(boss.getLootTable());
        *///?}
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, boss)
                .withParameter(LootContextParams.ORIGIN, boss.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        boolean rod = false;
        for (ItemStack s : table.getRandomItems(params)) rod |= s.getItem() instanceof ItemOrbitalStrikeRod;
        check(rod, "Beute enthaelt die Orbital Strike Rod");
        check(BossMobs.ORBITAL_STRIKE_ROD.get() instanceof ItemOrbitalStrikeRod, "Rod registriert");
        boss.discard();
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_boss", timeoutTicks = 100)
    public static void orbitalRodModesAndBeam(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack rod = new ItemStack(BossMobs.ORBITAL_STRIKE_ROD.get());
        check(ItemOrbitalStrikeRod.Mode.values().length == 6, "sechs Modi");
        check(ItemOrbitalStrikeRod.Mode.CATACLYSM.cooldown == 6000, "Cataclysm: 5 Minuten Abklingzeit");
        check(ItemOrbitalStrikeRod.mode(rod) == ItemOrbitalStrikeRod.Mode.LANCE, "Startmodus Lanze");
        PlatformHooks.putInt(rod, "Mode", ItemOrbitalStrikeRod.Mode.JUDGEMENT.ordinal());
        check(ItemOrbitalStrikeRod.mode(rod) == ItemOrbitalStrikeRod.Mode.JUDGEMENT, "Modus gespeichert");
        check(ItemOrbitalStrikeRod.remaining(rod, ItemOrbitalStrikeRod.Mode.JUDGEMENT, level) == 0, "anfangs aufgeladen");
        check(BossMobs.CRYSTAL_SHARD.get() != null, "Kristallsplitter registriert");

        Vec3 at = helper.absoluteVec(new Vec3(1.5D, 1.0D, 1.5D));
        AABB box = new AABB(at, at).inflate(4.0D);
        OrbitalStrikes.precision(level, at, null);
        check(level.getEntitiesOfClass(OrbitalBeamEntity.class, box).size() == 1, "Strahl erscheint");
        // Judgement: 14 Ticks Vorwarnung + 16 Ticks Nachleuchten, dann verschwindet er
        helper.runAfterDelay(40, () -> {
            check(level.getEntitiesOfClass(OrbitalBeamEntity.class, box).isEmpty(), "Strahl wieder weg");
            helper.succeed();
        });
    }

    @GameTest(template = "empty3x3x3", batch = "space_boss", timeoutTicks = 600)
    public static void sovereignSitsInThroneHall(GameTestHelper helper) {
        WorldChunkManagerCelestial world = CelestialGenerators.require(WemburosBlocks.BODY).chunkManager().apply(424242L);
        WemburosStructures.Structure throne = null;
        for (int d = 0; d < 900 && throne == null; d++) {
            WemburosStructures.Structure s = WemburosStructures.ofDistrict(world, d % 30 - 15, d / 30 - 15);
            if (s != null && s.type() == WemburosStructures.Type.THRONE) throne = s;
        }
        check(throne != null, "kein Thronsaal gefunden");
        WemburosStructures.Plan plan = WemburosStructures.plan(world, throne);
        long bosses = plan.mobs.stream().filter(m -> m.kind() == StructureMobs.UNSTABLE_SOVEREIGN).count();
        check(bosses == 1, "genau ein Boss im Thronsaal");
        check(StructureMobs.type(StructureMobs.UNSTABLE_SOVEREIGN) == BossMobs.UNSTABLE_SOVEREIGN.get(), "Mob-Art zeigt auf den Boss");
        helper.succeed();
    }
}
