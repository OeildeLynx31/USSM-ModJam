package com.ussm.the_undervoid.block.custom;

import com.ussm.the_undervoid.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DirtyVoidStone extends Block {
    public DirtyVoidStone(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
            level.setBlockAndUpdate(pos, ModBlocks.VOID_STONE.get().defaultBlockState());
        }
    }
}
