package com.space_beyond.dim.dungeon.seal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Siegelfeld: unzerstoerbare, leuchtende Energiewand vor Boss-Arenen und Schatzkammern der Dungeons. Verschwindet erst,
 * wenn alle Schloesser ({@link SealLockBlock}) derselben Wand gefuellt sind ({@link SealGates#tryOpen}).
 */
public class SealFieldBlock extends HalfTransparentBlock {

    public SealFieldBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) != 0) return;
        double x = pos.getX() + random.nextDouble(), y = pos.getY() + random.nextDouble(), z = pos.getZ() + random.nextDouble();
        level.addParticle(random.nextBoolean() ? ParticleTypes.ENCHANT : ParticleTypes.END_ROD, x, y, z, 0, random.nextBoolean() ? 0.02 : -0.02, 0);
    }
}
