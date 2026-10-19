package com.space_beyond.test;

import java.util.List;

import com.space_m.SpaceMod;
import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_beyond.entity.mob.infected.EntityBlindcap;
import com.space_beyond.entity.mob.infected.EntityInfected;
import com.space_beyond.entity.mob.infected.EntityLurker;
import com.space_beyond.entity.mob.infected.EntityMycobrute;
import com.space_beyond.entity.mob.infected.EntitySporeling;
import com.space_beyond.entity.mob.infected.InfectedMobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Sporos Runde 5: die Infizierten - Registrierung (Entities, Eier, Beute), Attribute, Immunitaet/Traeger, Blindhut-
 * Gehoer, Lauerer-Hinterhalt, Pilzkoloss-Sporenstoss, Spawnlisten der Sporos-Biome.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InfectedGameTest {

    private InfectedGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static void face(Pig pig, float yaw) {
        pig.setYRot(yaw);
        pig.setYHeadRot(yaw);
        pig.yBodyRot = yaw;
        pig.setXRot(0F);
    }

    @GameTest(template = "empty3x3x3", batch = "space_infected", timeoutTicks = 40)
    public static void infectedRegistries(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        for (String n : new String[] {"sporeling", "lurker", "blindcap", "mycobrute"}) {
            check(BuiltInRegistries.ENTITY_TYPE.containsKey(com.space_beyond.BeyondMod.id(n)), "entity " + n);
            check(BuiltInRegistries.ITEM.containsKey(com.space_beyond.BeyondMod.id(n + "_spawn_egg")), "spawn egg " + n);
            ResourceLocation loot = com.space_beyond.BeyondMod.id("entities/" + n);
            //? if < 1.21.1 {
            check(level.getServer().getLootData().getLootTable(loot) != net.minecraft.world.level.storage.loot.LootTable.EMPTY, "loot " + loot);
            //?} else {
            /*check(level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, loot)) != net.minecraft.world.level.storage.loot.LootTable.EMPTY, "loot " + loot);
            *///?}
        }
        EntitySporeling s = InfectedMobs.SPORELING.get().create(level);
        EntityLurker l = InfectedMobs.LURKER.get().create(level);
        EntityBlindcap b = InfectedMobs.BLINDCAP.get().create(level);
        EntityMycobrute m = InfectedMobs.MYCOBRUTE.get().create(level);
        check(s != null && l != null && b != null && m != null, "create");
        check(s.getMaxHealth() == 16F && l.getMaxHealth() == 24F && b.getMaxHealth() == 30F && m.getMaxHealth() == 120F, "Leben");
        check(s.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) == 0.33D && m.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) == 1.0D, "Tempo/Rueckstoss");
        check(m.getAttributeBaseValue(Attributes.ARMOR) == 10D && b.getAttributeBaseValue(Attributes.ARMOR) == 6D, "Ruestung");
        check(s.getAttributeBaseValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) == 0D, "keine Verstaerkungen");
        for (EntityInfected e : List.of(s, l, b, m)) {
            check(SporeInfection.isImmune(e), "untot -> immun: " + e.getType());
            check(!e.isBaby() && !e.isSunSensitiveForTest(), "kein Kind, kein Sonnenbrand");
            check(e.getType().getCategory() == MobCategory.MONSTER, "Monster");
        }
        check(Math.abs(m.getBbHeight() - 2.6F) < 1e-4, "Koloss 2,6 hoch");
        helper.succeed();
    }

    /** Blindhut hoert nach Bewegung, Lauerer schlaegt von hinten haerter, Koloss-Stoss exponiert. */
    @GameTest(template = "empty3x3x3", batch = "space_infected", timeoutTicks = 40)
    public static void infectedBehaviour(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1));
        EntityBlindcap blind = InfectedMobs.BLINDCAP.get().create(level);
        blind.moveTo(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D, 0F, 0F);
        blind.setNoAi(true);
        level.addFreshEntity(blind);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        pig.setNoAi(true);
        pig.setPos(base.getX() + 10.5D, base.getY(), base.getZ() + 0.5D);
        check(blind.hears(pig), "gehend auf 10 Bloecke gehoert");
        pig.setPose(Pose.CROUCHING);
        check(!blind.hears(pig), "schleichend auf 10 Bloecke nicht gehoert");
        pig.setPos(base.getX() + 2.0D, base.getY(), base.getZ() + 0.5D);
        check(blind.hears(pig), "ganz nah immer gehoert");
        pig.setPose(Pose.STANDING);
        pig.setPos(base.getX() + 20.5D, base.getY(), base.getZ() + 0.5D);
        check(!blind.hears(pig), "gehend auf 20 Bloecke nicht gehoert");
        pig.setSprinting(true);
        check(blind.hears(pig), "sprintend auf 20 Bloecke gehoert");
        pig.setSprinting(false);

        // Lauerer: Schwein schaut nach Westen (-x), Lauerer steht oestlich -> von hinten
        EntityLurker lurker = InfectedMobs.LURKER.get().create(level);
        lurker.setNoAi(true);
        pig.setPos(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        lurker.moveTo(base.getX() + 3.5D, base.getY(), base.getZ() + 0.5D, 0F, 0F);
        level.addFreshEntity(lurker);
        face(pig, 90F); // yaw 90 = Blick nach -x
        check(!EntityInfected.isFacing(pig, lurker) && lurker.canAmbush(pig), "Ruecken zum Lauerer");
        check(lurker.extraDamageForTest(pig) == 4.0F, "Hinterhalt +4");
        face(pig, -90F); // Blick nach +x, zum Lauerer
        check(EntityInfected.isFacing(pig, lurker), "schaut hin");
        check(lurker.extraDamageForTest(pig) == 0F, "frontal kein Bonus");

        // Koloss: Sporenstoss exponiert das Schwein (ungeschuetzt), nicht die Infizierten
        EntityMycobrute brute = InfectedMobs.MYCOBRUTE.get().create(level);
        brute.setNoAi(true);
        brute.moveTo(base.getX() + 1.5D, base.getY(), base.getZ() + 0.5D, 0F, 0F);
        level.addFreshEntity(brute);
        SporeInfection.set(pig, 0F);
        brute.sporeBurst(0.12F, 2.5D, 5);
        check(Math.abs(SporeInfection.get(pig) - 0.12F) < 1e-6, "Stoss: 0,12 Exposition, ist " + SporeInfection.get(pig));
        check(SporeInfection.get(lurker) == 0F && SporeInfection.get(blind) == 0F, "Infizierte unbeeindruckt");
        helper.succeed();
    }

    /** Spawnlisten: alle neun Sporos-Biome tragen die vier Stadien, Stadtbiome mehr Blindhuete. */
    @GameTest(template = "empty3x3x3", batch = "space_infected", timeoutTicks = 40)
    public static void infectedSpawns(GameTestHelper helper) {
        var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
        for (String name : new String[] {"sporos_overgrown_plains", "sporos_moss_forest", "sporos_swamp", "sporos_river", "sporos_fungal_grove",
                "sporos_highland", "sporos_spore_field", "sporos_coast", "sporos_lake"}) {
            Biome b = biomes.get(ResourceKey.create(Registries.BIOME, com.space_beyond.BeyondMod.id(name)));
            check(b != null, "biome " + name);
            List<MobSpawnSettings.SpawnerData> list = b.getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
            int sporeling = 0, blindcap = 0, brute = 0;
            for (MobSpawnSettings.SpawnerData d : list) {
                if (d.type == InfectedMobs.SPORELING.get()) sporeling = d.getWeight().asInt();
                if (d.type == InfectedMobs.BLINDCAP.get()) blindcap = d.getWeight().asInt();
                if (d.type == InfectedMobs.MYCOBRUTE.get()) brute = d.getWeight().asInt();
            }
            check(sporeling == 60 && brute > 0, name + ": Sporling 60 / Koloss > 0 (" + sporeling + "/" + brute + ")");
            boolean city = name.equals("sporos_overgrown_plains") || name.equals("sporos_moss_forest");
            check(blindcap == (city ? 40 : 20), name + ": Blindhut " + blindcap);
        }
        helper.succeed();
    }
}
