package com.ussm.examplemod.block.custom;

import com.ussm.examplemod.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VoidLeavesBlock extends LeavesBlock {
    public VoidLeavesBlock(Properties properties) {
        super(properties);
    }
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        //des feuilles qui tombe comme sous les cerisiers
        if (random.nextInt(10) == 0) {
            BlockPos below = pos.below();
            if (!isFaceFull(level.getBlockState(below).getCollisionShape(level, below), Direction.UP)) {
                ParticleUtils.spawnParticleBelow(level, pos, random, ModParticles.FALLING_VOID_LEAF.get());
            }
        }
    }
}
