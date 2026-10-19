package com.space_beyond.dim.sporos.flora;

import java.util.Random;
import java.util.function.BiPredicate;

import com.space_m.dim.SpaceWorldgen;
import com.space_m.dim.legacy.LegacyWorld;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Weltgen-Features der Sporos-Flora (im gemeinsamen {@link SpaceWorldgen#FEATURES}, vor dessen {@code register()}):
 * drei Baumformen, Totholz und Riesenpilz - jeweils ein Feature ohne Konfiguration, das {@link HypheTree} am
 * Ursprung aufruft. Die Verteilung (Anzahl je Chunk, Hoehenkarte, Wassertiefe) steht in den Placed-Feature-JSONs
 * {@code data/space_m/worldgen/placed_feature/sporos/*.json}, die Pflanzenflecken sind Vanilla-{@code random_patch}.
 */
public final class SporosFloraFeatures {

    private SporosFloraFeatures() {}

    /** Feature ohne Konfiguration, das einen {@link HypheTree}-Generator am Ursprung ausfuehrt. */
    public static final class Simple extends Feature<NoneFeatureConfiguration> {
        private final BiPredicate<WorldGenLevel, Ctx> gen;

        public Simple(BiPredicate<WorldGenLevel, Ctx> gen) {
            super(NoneFeatureConfiguration.CODEC);
            this.gen = gen;
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
            Random rand = LegacyWorld.random(ctx.random());
            return gen.test(ctx.level(), new Ctx(rand, ctx.origin()));
        }
    }

    public record Ctx(Random rand, BlockPos origin) {}

    public static final RegistrySupplier<Simple> TREE_GIANT = com.space_beyond.dim.BeyondWorldgen.FEATURES.register("hyphe_tree_giant",
            () -> new Simple((w, c) -> HypheTree.generate(w, c.rand(), c.origin(), HypheTree.Shape.GIANT)));
    public static final RegistrySupplier<Simple> TREE_CANOPY = com.space_beyond.dim.BeyondWorldgen.FEATURES.register("hyphe_tree_canopy",
            () -> new Simple((w, c) -> HypheTree.generate(w, c.rand(), c.origin(), HypheTree.Shape.CANOPY)));
    public static final RegistrySupplier<Simple> TREE_SWAMP = com.space_beyond.dim.BeyondWorldgen.FEATURES.register("hyphe_tree_swamp",
            () -> new Simple((w, c) -> HypheTree.generate(w, c.rand(), c.origin(), HypheTree.Shape.SWAMP)));
    public static final RegistrySupplier<Simple> DEADWOOD = com.space_beyond.dim.BeyondWorldgen.FEATURES.register("hyphe_deadwood",
            () -> new Simple((w, c) -> HypheTree.generateDeadwood(w, c.rand(), c.origin())));
    public static final RegistrySupplier<Simple> GIANT_FUNGUS = com.space_beyond.dim.BeyondWorldgen.FEATURES.register("giant_fungus",
            () -> new Simple((w, c) -> HypheTree.generateGiantFungus(w, c.rand(), c.origin())));

    public static void init() {
    }
}
