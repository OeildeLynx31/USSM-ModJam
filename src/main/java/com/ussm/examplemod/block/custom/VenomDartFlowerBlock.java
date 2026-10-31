package com.ussm.examplemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class VenomDartFlowerBlock extends UndervoidPlantBlock {
    public VenomDartFlowerBlock(double x0, double y0, double z0, double x1, double y1, double z1, Properties properties) {
        super(x0, y0, z0, x1, y1, z1, false, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // pareil que la wither rose mais avec du poison
        if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.hasEffect(MobEffects.POISON)
                    && touches(state, level, pos, entity)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60)); // 3 sec
            }
        }
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.DAMAGE_CAUTIOUS;
    }
}
