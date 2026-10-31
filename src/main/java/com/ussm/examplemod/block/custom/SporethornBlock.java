package com.ussm.examplemod.block.custom;

import com.ussm.examplemod.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

public class SporethornBlock extends UndervoidPlantBlock {
    public SporethornBlock(double x0, double y0, double z0, double x1, double y1, double z1, Properties properties) {
        super(x0, y0, z0, x1, y1, z1, true, properties);
    }

    // ca pique comme un cactus et ca crache des spores, mais ca explose pas pour de vrai
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel serverLevel && entity instanceof LivingEntity) {
            AABB hitbox = getShape(state, level, pos, CollisionContext.empty()).bounds().move(pos).inflate(0.05);
            if (entity.getBoundingBox().intersects(hitbox)) {
                //entity.makeStuckInBlock(state, new Vec3(0.8, 0.75, 0.8));
                boolean hit = entity.hurt(level.damageSources().cactus(), 1.0F);
                if (hit) {
                    serverLevel.sendParticles(ModParticles.MIASMA.get(),
                            pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                            8, 0.25, 0.25, 0.25, 0.01);
                    level.playSound(null, pos, SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.BLOCKS, 0.6f, 1.4f);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) == 0) {
            double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
            double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
            level.addParticle(ModParticles.MIASMA.get(), x, pos.getY() + 1.05, z, 0.0, 0.02, 0.0);
        }
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.DAMAGE_OTHER;
    }
}
