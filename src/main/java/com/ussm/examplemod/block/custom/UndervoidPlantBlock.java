package com.ussm.examplemod.block.custom;

import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

public class UndervoidPlantBlock extends DecorBlock {
    public UndervoidPlantBlock(double x0, double y0, double z0, double x1, double y1, double z1, boolean solid, Properties properties) {
        super(x0, y0, z0, x1, y1, z1, solid, false, properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos.below());
        // pareil que l'herbe corrompue : la terre (notre herbe, la mousse du bloom) ou l'ardoise sombre
        return ground.is(BlockTags.DIRT) || ground.is(ModBlocks.DARK_DEEPSLATE.get());
    }

    protected boolean touches(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        AABB hitbox = getShape(state, level, pos, CollisionContext.empty()).bounds();
        return entity.getBoundingBox().intersects(hitbox.move(pos).inflate(0.05));
    }
}
