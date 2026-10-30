package com.ussm.examplemod.block.custom;

import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class VoidSaplingBlock extends SaplingBlock {
    public VoidSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

    // les arbres du vide poussent que dans l'entre-monde
    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            super.advanceTree(level, pos, state, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level instanceof Level realLevel && realLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
    }
}
