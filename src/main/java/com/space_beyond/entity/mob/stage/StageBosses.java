package com.space_beyond.entity.mob.stage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.space_m.entity.SpaceEntities;
import com.space_m.item.SpaceItems;
import com.space_m.main.SpaceTabFill;
import com.space_beyond.progression.SpaceProgression.Stage;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Registrierung der Stufen-Bosse ({@link Bosses}), ihres Geschosses und der Spawn-Eier. Je Boss: Hitbox, Tempo, Farben
 * des Eis. Renderer: {@code StageBossClient}.
 */
public final class StageBosses {

    private StageBosses() {}

    @FunctionalInterface
    interface Factory {
        EntityStageBoss create(EntityType<? extends EntityStageBoss> type, Level level);
    }

    public record Def(Stage stage, String id, RegistrySupplier<EntityType<EntityStageBoss>> type, double speed) { }

    public static final Map<Stage, Def> DEFS = new EnumMap<>(Stage.class);
    private static final List<RegistrySupplier<ArchitecturySpawnEggItem>> EGGS = new ArrayList<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static RegistrySupplier<EntityType<EntityStageBoss>> boss(Stage stage, String id, Factory f, float w, float h, double speed,
                                                                      boolean fire, int c1, int c2) {
        RegistrySupplier<EntityType<EntityStageBoss>> type = com.space_beyond.entity.BeyondEntities.register(id, () -> {
            EntityType.Builder<EntityStageBoss> b = EntityType.Builder.<EntityStageBoss>of((t, l) -> f.create((EntityType) t, l), MobCategory.MONSTER)
                    .sized(w, h).clientTrackingRange(10).updateInterval(2);
            return fire ? b.fireImmune() : b;
        });
        DEFS.put(stage, new Def(stage, id, type, speed));
        EGGS.add(com.space_beyond.item.BeyondItems.register(id + "_spawn_egg", () -> new ArchitecturySpawnEggItem((RegistrySupplier) type, c1, c2, new Item.Properties()), null));
        return type;
    }

    public static final RegistrySupplier<EntityType<EntityStageBoss>> LUNAR_SENTINEL = boss(Stage.MUN, "lunar_sentinel", Bosses.LunarSentinel::new, 2.4F, 4.0F, 0.25, false, 0xB2B6BE, 0xFF3C32);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> CRYO_BEHEMOTH = boss(Stage.MINMUS, "cryo_behemoth", Bosses.CryoBehemoth::new, 3.2F, 5.0F, 0.23, false, 0xA0E8D6, 0x5AAAA0);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> RUST_TITAN = boss(Stage.DUNA, "rust_titan", Bosses.RustTitan::new, 3.5F, 3.8F, 0.22, false, 0x964628, 0xFFAA28);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> MONOLITH_KEEPER = boss(Stage.IKE, "monolith_keeper", Bosses.MonolithKeeper::new, 2.6F, 5.0F, 0.3, false, 0x1A1424, 0xC86EFF);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> CORE_ABOMINATION = boss(Stage.DRES, "core_abomination", Bosses.CoreAbomination::new, 3.0F, 3.2F, 0.2, false, 0x303234, 0x78FF46);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> MAGMA_WYRM = boss(Stage.MOHO, "magma_wyrm", Bosses.MagmaWyrm::new, 2.2F, 2.0F, 0.3, true, 0x2E2828, 0xFF7814);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> SCHRAB_HYDRA = boss(Stage.EVE, "schrab_hydra", Bosses.SchrabHydra::new, 2.8F, 3.4F, 0.24, false, 0x287878, 0x50F0F0);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> ABYSSAL_LEVIATHAN = boss(Stage.LAYTHE, "abyssal_leviathan", Bosses.AbyssalLeviathan::new, 3.5F, 3.0F, 0.3, false, 0x182840, 0x50F0F0);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> POLYMER_COLOSSUS = boss(Stage.TEKTO, "polymer_colossus", Bosses.PolymerColossus::new, 2.8F, 4.4F, 0.22, false, 0xDCE0D6, 0xBEFF3C);
    public static final RegistrySupplier<EntityType<EntityStageBoss>> MYCELIAL_QUEEN = boss(Stage.SPOROS, "mycelial_queen", Bosses.MycelialQueen::new, 2.2F, 5.4F, 0.2, false, 0x8C3C32, 0xFFC85A);

    public static final RegistrySupplier<EntityType<EntityBossOrb>> BOSS_ORB = com.space_beyond.entity.BeyondEntities.register("boss_orb",
            () -> EntityType.Builder.<EntityBossOrb>of(EntityBossOrb::new, MobCategory.MISC).sized(0.5F, 0.5F).noSave().fireImmune()
                    .clientTrackingRange(8).updateInterval(1));

    private static boolean initialized;

    /** Aus {@code SpaceMobs.init()}. */
    public static void init() {
        if (initialized) return;
        initialized = true;
        for (Def d : DEFS.values()) {
            SpaceEntities.attributes(d.type(), () -> EntityStageBoss.attributes(d.stage().tier(), d.speed()));
        }
        SpaceTabFill.modify(CreativeModeTabs.SPAWN_EGGS, "StageBosses",
                sink -> {
                    for (RegistrySupplier<ArchitecturySpawnEggItem> egg : EGGS) sink.accept(new ItemStack(egg.get()), String.valueOf(egg.getId()));
                });
    }
}
