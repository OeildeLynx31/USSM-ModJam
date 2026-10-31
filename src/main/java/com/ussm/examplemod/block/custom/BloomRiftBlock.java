package com.ussm.examplemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BloomRiftBlock extends Block implements Portal {
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public BloomRiftBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // repris de NetherPortalBlock
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        if (entity instanceof Player && ((Player) entity).getAbilities().invulnerable) {
            return 1; //direct en créatif
        }
        return 30;
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        if (!(entity instanceof ServerPlayer)) return null;

        ServerLevel overworld = level.getServer().overworld();
        overworld.getChunkAt(pos); // faut charger le chunk avant sinon ca marche pas
        BlockPos arrival = overworld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        if (arrival.getY() <= overworld.getMinBuildHeight()) {
            arrival = overworld.getSharedSpawnPos();
        }

        return new DimensionTransition(overworld, Vec3.atBottomCenterOf(arrival), Vec3.ZERO, entity.getYRot(), entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.REVERSE_PORTAL,
                    x + (random.nextDouble() - 0.5) * 0.4, y + random.nextDouble(), z + (random.nextDouble() - 0.5) * 0.4,
                    (random.nextDouble() - 0.5) * 0.05, random.nextDouble() * 0.02, (random.nextDouble() - 0.5) * 0.05);
        }
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.END_ROD,
                    x + (random.nextDouble() - 0.5) * 0.3, y + random.nextDouble(), z + (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.02, 0.01 + random.nextDouble() * 0.02, (random.nextDouble() - 0.5) * 0.02);
        }

        if (random.nextInt(80) == 0) {
            level.playLocalSound(x, y + 0.5, z, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.4f, 1.6f, false);
        }
    }
}