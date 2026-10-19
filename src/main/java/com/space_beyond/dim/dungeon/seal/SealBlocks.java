package com.space_beyond.dim.dungeon.seal;

import com.space_m.block.SpaceBlocks;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceCreativeTabs;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Siegel der Boss-Dungeons (Nutzerwunsch: man darf nicht einfach zum Boss laufen): Siegelfeld, Siegelschloss und
 * Siegelschluessel. Schluessel gibt es nur in den Fluegeln der Dungeons (Schatzkisten am Ende, Siegelwaechter tragen
 * einen in der Nebenhand). Aus {@code SpaceRegistry.init()} vor {@code SpaceBlocks.init()}.
 */
public final class SealBlocks {

    private SealBlocks() {}

    public static final RegistrySupplier<SealFieldBlock> SEAL_FIELD = com.space_beyond.block.BeyondBlocks.registerWithItem("seal_field",
            () -> new SealFieldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(-1.0F, 3600000.0F).noLootTable()
                    .lightLevel(s -> 10).noOcclusion().sound(SoundType.AMETHYST).pushReaction(PushReaction.BLOCK)
                    .isValidSpawn((s, l, p, e) -> false).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)),
            SpaceCreativeTabs.Tab.BLOCKS);

    public static final RegistrySupplier<SealLockBlock> SEAL_LOCK = com.space_beyond.block.BeyondBlocks.registerWithItem("seal_lock",
            () -> new SealLockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(-1.0F, 3600000.0F).noLootTable()
                    .lightLevel(s -> s.getValue(SealLockBlock.LIT) ? 13 : 4).sound(SoundType.DEEPSLATE_BRICKS).pushReaction(PushReaction.BLOCK)),
            SpaceCreativeTabs.Tab.BLOCKS);

    public static final RegistrySupplier<Item> SEAL_KEY = com.space_beyond.item.BeyondItems.register("seal_key",
            () -> new SealKeyItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)), SpaceCreativeTabs.Tab.PARTS);

    public static void init() {
        // Klasse laden: Register-Eintraege oben
    }

    /**
     * Macht einen Dungeon-Mob zum Siegelwaechter: vierfaches Leben, mehr Schaden, kaum Rueckstoss, Resistenz, leuchtet,
     * traegt einen Siegelschluessel (faellt sicher).
     */
    public static void makeGuardian(Mob mob) {
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) hp.setBaseValue(hp.getBaseValue() * 4.0D + 40.0D);
        AttributeInstance dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() * 1.6D + 2.0D);
        AttributeInstance kb = mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kb != null) kb.setBaseValue(0.7D);
        mob.setHealth(mob.getMaxHealth());
        mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(SEAL_KEY.get()));
        mob.setDropChance(EquipmentSlot.OFFHAND, 2.0F);
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        mob.setCustomName(Component.translatable("entity.space_m.seal_guardian", mob.getType().getDescription()));
        mob.setCustomNameVisible(true);
    }
}
