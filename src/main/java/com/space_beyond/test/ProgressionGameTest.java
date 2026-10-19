package com.space_beyond.test;

import java.util.HashSet;
import java.util.Set;

import com.space_m.SpaceMod;
import com.space_m.celestial.CelestialBody;
import com.space_m.dim.CelestialGenerators;
import com.space_m.dim.ChunkProviderCelestial;
import com.space_m.dim.WorldChunkManagerCelestial;
import com.space_beyond.dim.dungeon.DungeonDesign;
import com.space_beyond.dim.dungeon.DungeonPlan;
import com.space_beyond.dim.dungeon.PlanetDungeons;
import com.space_beyond.entity.mob.stage.EntityStageBoss;
import com.space_beyond.entity.mob.stage.StageBosses;
import com.space_beyond.progression.ItemBossCore;
import com.space_beyond.progression.ItemPlanetWeapon;
import com.space_beyond.progression.ProgressionItems;
import com.space_beyond.progression.ProgressionParts;
import com.space_beyond.progression.SpaceProgression;
import com.space_beyond.progression.SpaceProgression.Stage;
import com.space_m.rocket.RocketPart;
import com.space_m.rocket.RocketStruct;
import com.space_m.rocket.SpaceRocketItems;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Planeten-Fortschritt: Stufen der Ziele, Triebwerks-Stufen und Sperre der Rakete, Bosse (staerker je Stufe, Beute
 * mit Kern und Waffe), Boss-Dungeons aller zehn Planeten (Lage, Plan, Bauwerk im Puffer).
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ProgressionGameTest {

    private ProgressionGameTest() {}

    private static void check(boolean c, String msg) {
        if (!c) throw new GameTestAssertException(msg);
    }

    @GameTest(template = "empty3x3x3", batch = "space_progression", timeoutTicks = 40)
    public static void tiersAndThrusters(GameTestHelper helper) {
        check(SpaceProgression.requiredTier(CelestialBody.getBody("kerbin")) == 0, "Kerbin frei");
        check(SpaceProgression.requiredTier(CelestialBody.getBody("mun")) == 1, "Mun Stufe 1");
        check(SpaceProgression.requiredTier(CelestialBody.getBody("duna")) == 3, "Duna Stufe 3");
        check(SpaceProgression.requiredTier(CelestialBody.getBody("wemburos")) == 11, "Wemburos Stufe 11");
        RocketPart methalox = RocketPart.getPart(SpaceRocketItems.MP_THRUSTER_20_METHALOX.get());
        check(SpaceProgression.thrusterTier(methalox) == 1, "alte Triebwerke Stufe 1");
        for (int t = ProgressionParts.FIRST; t <= ProgressionParts.LAST; t++) {
            RocketPart mk = RocketPart.getPart(ProgressionItems.THRUSTERS.get(t - ProgressionParts.FIRST).get());
            check(mk != null && mk.type == RocketPart.PartType.THRUSTER, "Mk." + t + " ist ein Triebwerk");
            check(SpaceProgression.thrusterTier(mk) == t, "Mk." + t + " hat Stufe " + t);
        }
        RocketPart fuselage = RocketPart.getPart(SpaceRocketItems.RP_FUSELAGE_20_12.get());
        RocketPart capsule = RocketPart.getPart(SpaceRocketItems.RP_CAPSULE_20.get());
        RocketStruct basic = new RocketStruct(capsule);
        basic.addStage(fuselage, null, methalox, 1, 1);
        check(SpaceProgression.rocketMayReach(basic, CelestialBody.getBody("mun")), "Grundrakete erreicht Mun");
        check(!SpaceProgression.rocketMayReach(basic, CelestialBody.getBody("minmus")), "Grundrakete erreicht Minmus nicht");
        check(SpaceProgression.rocketMayReach(basic, CelestialBody.getBody("kerbin")), "Heimflug immer moeglich");
        check(!basic.hasSufficientFuel(CelestialBody.getBody("kerbin"), CelestialBody.getBody("duna"), false, false), "Sperre im Treibstoff-Check");
        RocketStruct mk3 = new RocketStruct(capsule);
        mk3.addStage(fuselage, null, RocketPart.getPart(ProgressionItems.THRUSTERS.get(1).get()), 1, 1);
        check(SpaceProgression.rocketTier(mk3) == 3, "Rakete mit Mk.III");
        check(SpaceProgression.rocketMayReach(mk3, CelestialBody.getBody("duna")) && !SpaceProgression.rocketMayReach(mk3, CelestialBody.getBody("ike")), "Mk.III bis Duna");
        check(SpaceProgression.tierIssue(mk3, CelestialBody.getBody("ike")) != null, "Hinweis bei fehlender Stufe");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_progression", timeoutTicks = 100)
    public static void bossesAndLoot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        double lastHp = 0;
        for (Stage s : Stage.values()) {
            if (s == Stage.WEMBUROS) continue;
            check(ProgressionItems.CORES.get(s).get() instanceof ItemBossCore, s + ": Kern");
            check(ProgressionItems.WEAPONS.get(s).get() instanceof ItemPlanetWeapon, s + ": Waffe");
            check(com.hbm_m.platform.PlatformHooks.isEdible(ProgressionItems.FOODS.get(s).get()), s + ": Essen");
            var def = StageBosses.DEFS.get(s);
            check(def != null, s + ": Boss registriert");
            EntityStageBoss boss = (EntityStageBoss) def.type().get().create(level);
            check(boss != null && boss.stage() == s, s + ": Boss-Stufe");
            boss.moveTo(helper.absoluteVec(new net.minecraft.world.phys.Vec3(1.5, 1, 1.5)));
            double hp = boss.getAttributeValue(Attributes.MAX_HEALTH);
            check(hp > lastHp, s + ": staerker als der vorige Boss");
            lastHp = hp;
            //? if < 1.21.1 {
            LootTable table = level.getServer().getLootData().getLootTable(boss.getLootTable());
            //?} else {
            /*LootTable table = level.getServer().reloadableRegistries().getLootTable(boss.getLootTable());
            *///?}
            LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, boss)
                    .withParameter(LootContextParams.ORIGIN, boss.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic()).create(LootContextParamSets.ENTITY);
            boolean core = false, weapon = false;
            for (ItemStack st : table.getRandomItems(params)) {
                core |= st.getItem() == ProgressionItems.CORES.get(s).get();
                weapon |= st.getItem() == ProgressionItems.WEAPONS.get(s).get();
            }
            check(core && weapon, s + ": Beute mit Kern und Waffe");
            boss.discard();
        }
        check(lastHp < 5000.0D, "Wemburos-Boss bleibt der staerkste");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_progression", timeoutTicks = 40)
    public static void sealGateOpensWhenAllLocksFilled(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockState field = com.space_beyond.dim.dungeon.seal.SealBlocks.SEAL_FIELD.get().defaultBlockState();
        BlockState lock = com.space_beyond.dim.dungeon.seal.SealBlocks.SEAL_LOCK.get().defaultBlockState();
        BlockPos f1 = helper.absolutePos(new BlockPos(1, 1, 1)), f2 = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos l1 = helper.absolutePos(new BlockPos(0, 1, 1)), l2 = helper.absolutePos(new BlockPos(2, 1, 1));
        level.setBlock(f1, field, 3);
        level.setBlock(f2, field, 3);
        level.setBlock(l1, lock, 3);
        level.setBlock(l2, lock, 3);
        com.space_beyond.dim.dungeon.seal.SealGates.fill(level, l1);
        check(level.getBlockState(f1).is(field.getBlock()) && level.getBlockState(f2).is(field.getBlock()), "ein Schloss: Siegel steht noch");
        com.space_beyond.dim.dungeon.seal.SealGates.fill(level, l2);
        check(level.getBlockState(f1).isAir() && level.getBlockState(f2).isAir(), "alle Schloesser: Siegel bricht");
        check(level.getBlockState(l1).getValue(com.space_beyond.dim.dungeon.seal.SealLockBlock.LIT), "Schloss bleibt gefuellt");
        helper.succeed();
    }

    @GameTest(template = "empty3x3x3", batch = "space_progression", timeoutTicks = 2400)
    public static void dungeonsOnEveryPlanet(GameTestHelper helper) {
        long seed = 13579L;
        for (var entry : PlanetDungeons.designs().entrySet()) {
            String body = entry.getKey();
            DungeonDesign design = entry.getValue();
            WorldChunkManagerCelestial world = CelestialGenerators.require(body).chunkManager().apply(seed);
            PlanetDungeons.Dungeon d = null;
            for (int dx = -6; dx <= 6 && d == null; dx++) for (int dz = -6; dz <= 6 && d == null; dz++) d = PlanetDungeons.ofDistrict(body, world, dx, dz);
            check(d != null, body + ": kein Dungeon gefunden");
            check(d.equals(PlanetDungeons.ofDistrict(body, world, Math.floorDiv(d.x() >> 4, PlanetDungeons.DISTRICT_CHUNKS),
                    Math.floorDiv(d.z() >> 4, PlanetDungeons.DISTRICT_CHUNKS))), body + ": deterministisch");
            DungeonPlan plan = PlanetDungeons.plan(world, d);
            long bosses = plan.mobs.stream().filter(m -> m.type().startsWith("space_beyond:") && PlanetDungeons.type(m.type()) != null
                    && PlanetDungeons.type(m.type()) == StageBosses.DEFS.get(design.stage()).type().get()).count();
            check(bosses == 1, body + ": genau ein Boss");
            for (DungeonPlan.Mob m : plan.mobs) check(PlanetDungeons.type(m.type()) != null, body + ": unbekannter Mob " + m.type());
            for (DungeonPlan.Mob m : plan.spawners) check(PlanetDungeons.type(m.type()) != null, body + ": unbekannter Spawner " + m.type());
            // Nutzerwunsch: eigene Mobs statt Vanilla/HBM in den Dungeons
            for (DungeonPlan.Mob m : plan.mobs) check(m.type().startsWith("space_beyond:"), body + ": fremder Mob " + m.type());
            for (DungeonPlan.Mob m : plan.spawners) check(m.type().startsWith("space_beyond:"), body + ": fremder Spawner " + m.type());
            for (DungeonPlan.Mob m : plan.elites) check(m.type().startsWith("space_beyond:") && PlanetDungeons.type(m.type()) != null, body + ": fremder Waechter " + m.type());
            for (DungeonPlan.Loot l : plan.loot) {
                BlockPos p = PlanetDungeons.worldPos(plan, l.u(), l.y(), l.v());
                check(d.distance(p.getX(), p.getZ()) <= design.radius(), body + ": Beute im Grundriss");
            }
            // Bauwerk im Puffer: Spawner, Truhen, Arena um den Boss frei
            ChunkProviderCelestial prov = CelestialGenerators.require(body).chunkProvider().apply(world, seed);
            Set<Block> seen = new HashSet<>();
            int r = design.radius();
            for (int cx = (d.x() - r) >> 4; cx <= (d.x() + r) >> 4; cx++) for (int cz = (d.z() - r) >> 4; cz <= (d.z() + r) >> 4; cz++) {
                BlockState[] b = prov.provideChunk(cx, cz).blocks.clone();
                PlanetDungeons.generateChunk(body, world, cx, cz, b);
                for (BlockState st : b) if (st != null) seen.add(st.getBlock());
            }
            check(seen.contains(Blocks.CHEST), body + ": Truhen fehlen");
            long keys = plan.loot.stream().filter(l -> l.table().equals("seal_key")).count() + plan.elites.size();
            check(keys == 3, body + ": genau drei Siegelschluessel (" + keys + ")");
            check(seen.contains(com.space_beyond.dim.dungeon.seal.SealBlocks.SEAL_FIELD.get()) && seen.contains(com.space_beyond.dim.dungeon.seal.SealBlocks.SEAL_LOCK.get()),
                    body + ": Siegeltor fehlt");
            check(seen.contains(Blocks.SPAWNER) || plan.spawners.isEmpty(), body + ": Spawner fehlen");
            DungeonPlan.Mob bossMob = plan.mobs.stream().filter(m -> PlanetDungeons.type(m.type()) == StageBosses.DEFS.get(design.stage()).type().get())
                    .findFirst().orElseThrow();
            BlockPos bossAt = PlanetDungeons.worldPos(plan, bossMob.u(), bossMob.y(), bossMob.v());
            BlockState[] arena = prov.provideChunk(bossAt.getX() >> 4, bossAt.getZ() >> 4).blocks.clone();
            PlanetDungeons.generateChunk(body, world, bossAt.getX() >> 4, bossAt.getZ() >> 4, arena);
            int idx = ((bossAt.getX() & 15) * 16 + (bossAt.getZ() & 15)) * 256 + bossAt.getY();
            check(arena[idx] == null || arena[idx].isAir() || !arena[idx].getFluidState().isEmpty(), body + ": Bossplatz frei");
        }
        check(PlanetDungeons.designs().size() == 10, "zehn Planeten-Dungeons");
        helper.succeed();
    }
}
