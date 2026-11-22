package com.ussm.the_undervoid.block.custom;

import com.mojang.serialization.MapCodec;
import com.ussm.the_undervoid.Config;
import com.ussm.the_undervoid.block.entity.SanityTotemBlockEntity;
import com.ussm.the_undervoid.sanity.Sanity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// totem a la hollow knight : clic droit = on regagne de la santé mentale
public class SanityTotemBlock extends BaseEntityBlock {
    public static final MapCodec<SanityTotemBlock> CODEC = simpleCodec(SanityTotemBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    // le socle du modele, la statue au dessus ne bloque pas
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 14, 16);

    public SanityTotemBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // rendu par le BlockEntityRenderer
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SanityTotemBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer) || !(level.getBlockEntity(pos) instanceof SanityTotemBlockEntity totem)) {
            return InteractionResult.PASS;
        }

        // pas au dessus du seuil d'euphorie, sinon le totem ferait du mal
        float max = Config.EUPHORIA_THRESHOLD.get().floatValue() - 1f;
        if (Sanity.get(serverPlayer) >= max) {
            serverPlayer.displayClientMessage(Component.translatable("message.the_undervoid.sanity_totem.calm"), true);
            return InteractionResult.CONSUME;
        }

        long now = level.getGameTime();
        int left = totem.secondsLeft(player.getUUID(), now, Config.SANITY_TOTEM_COOLDOWN.get() * 20);
        if (left > 0) {
            serverPlayer.displayClientMessage(Component.translatable("message.the_undervoid.sanity_totem.cooldown", left), true);
            return InteractionResult.CONSUME;
        }

        float gain = Math.min(Config.SANITY_TOTEM_GAIN.get().floatValue(), max - Sanity.get(serverPlayer));
        Sanity.add(serverPlayer, gain);
        totem.markUsed(player.getUUID(), now);

        ServerLevel serverLevel = (ServerLevel) level;
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5, 25, 0.5, 0.8, 0.5, 0.02);
        serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.2f, 0.7f);
        return InteractionResult.CONSUME;
    }
}
