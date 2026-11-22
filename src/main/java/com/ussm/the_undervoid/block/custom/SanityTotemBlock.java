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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// totem a la hollow knight : clic droit = on regagne de la santé mentale
public class SanityTotemBlock extends BaseEntityBlock {
    public static final MapCodec<SanityTotemBlock> CODEC = simpleCodec(SanityTotemBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 2);
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    public SanityTotemBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        // il faut la place pour les 3 blocs
        if (pos.getY() > level.getMaxBuildHeight() - 3
                || !level.getBlockState(pos.above()).canBeReplaced(context)
                || !level.getBlockState(pos.above(2)).canBeReplaced(context)) {
            return null;
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(PART, 1), 3);
        level.setBlock(pos.above(2), state.setValue(PART, 2), 3);
    }

    // si un morceau disparait (explosion...) les autres aussi
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        int part = state.getValue(PART);
        if (direction == Direction.UP && part < 2 && !(neighbor.is(this) && neighbor.getValue(PART) == part + 1)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (direction == Direction.DOWN && part > 0 && !(neighbor.is(this) && neighbor.getValue(PART) == part - 1)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    // casser n'importe quel morceau casse les 3, et ca drop une seule fois
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos base = pos.below(state.getValue(PART));
            BlockState baseState = level.getBlockState(base);
            if (baseState.is(this)) {
                if (!player.isCreative() && state.getValue(PART) != 0) {
                    Block.dropResources(baseState, level, base, level.getBlockEntity(base), player, player.getMainHandItem());
                }
                for (int i = 0; i < 3; i++) {
                    BlockPos part = base.above(i);
                    if (!part.equals(pos) && level.getBlockState(part).is(this)) {
                        level.setBlock(part, Blocks.AIR.defaultBlockState(), 35);
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // seul le bas est rendu par le BlockEntityRenderer
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == 0 ? new SanityTotemBlockEntity(pos, state) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos clicked, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockPos pos = clicked.below(state.getValue(PART));
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
        // il faut les 3 tetes allumées
        if (totem.charge(now) < 1f) {
            serverPlayer.displayClientMessage(Component.translatable("message.the_undervoid.sanity_totem.cooldown", totem.secondsLeft(now)), true);
            return InteractionResult.CONSUME;
        }

        float gain = Math.min(Config.SANITY_TOTEM_GAIN.get().floatValue(), max - Sanity.get(serverPlayer));
        Sanity.add(serverPlayer, gain);
        totem.use(now, Config.SANITY_TOTEM_COOLDOWN.get() * 20);

        ServerLevel serverLevel = (ServerLevel) level;
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5, 25, 0.5, 0.8, 0.5, 0.02);
        serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.2f, 0.7f);
        return InteractionResult.CONSUME;
    }
}
