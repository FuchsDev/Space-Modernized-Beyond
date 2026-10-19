package com.space_beyond.dim.sporos.flora.block;

import com.space_beyond.dim.sporos.flora.SporosFlora;
import com.space_beyond.dim.sporos.spores.SporeInfection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Sporenkapsel: praller Sack, der bei Beruehrung durch Lebewesen platzt ({@link #burst}) - Sporenwolke
 * (Partikel), kurze Uebelkeit und Schwaeche fuer alle Lebewesen im Umkreis von 2 Bloecken, Beute Sporenstaub
 * (Loot-Tabelle) und - Runde 4 - ein Schub Sporeninfektion ({@link SporeInfection#expose}, {@link #BURST_EXPOSURE})
 * fuer alle ungeschuetzten Lebewesen in der Wolke. Nur ohne Sporenwolke abbaubar, wenn man sie nicht beruehrt
 * (Schere/aus der Ferne).
 */
public class BlockSporePod extends BushBlock {

    protected static final VoxelShape SHAPE = BushBlock.box(3.0D, 0.0D, 3.0D, 13.0D, 14.0D, 13.0D);

    /** Infektionsschub einer platzenden Kapsel (knapp ein Drittel bis Stufe 1). */
    public static final float BURST_EXPOSURE = 0.08F;

    public BlockSporePod(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return SporosFlora.SOIL.test(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && !entity.isSteppingCarefully()) {
            burst(level, pos);
        }
    }

    /** Kapsel platzt: Block entfernen (mit Beute), Wolke, Effekte. */
    public static void burst(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        ServerLevel server = (ServerLevel) level;
        BlockState state = level.getBlockState(pos);
        level.removeBlock(pos, false);
        Block.dropResources(state, level, pos);
        level.playSound(null, pos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 0.8F, 0.6F);
        server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, pos.getX() + 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.5D, 40, 0.8D, 0.6D, 0.8D, 0.02D);
        server.sendParticles(ParticleTypes.WHITE_ASH, pos.getX() + 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.5D, 25, 0.5D, 0.4D, 0.5D, 0.03D);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(2.0D))) {
            e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            SporeInfection.expose(e, BURST_EXPOSURE);
        }
    }

    //? if >= 1.21.1 {
    /*public static final com.mojang.serialization.MapCodec<BlockSporePod> CODEC = simpleCodec(BlockSporePod::new);
    @Override protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() { return CODEC; }
    *///?}
}
