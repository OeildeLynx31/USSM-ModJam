package com.ussm.examplemod.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

public class BedrockBreakerBlock extends Block {
    public static final BooleanProperty CHARGING = BooleanProperty.create("charging");

    private static final int CHARGE_TICKS = 60;

    public BedrockBreakerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CHARGING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGING);
    }

    // comme la tnt : si on le pose deja alimenté ca charge direct
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && level.hasNeighborSignal(pos)) {
            charge(state, level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) {
            charge(state, level, pos);
        }
    }

    private void charge(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide() || state.getValue(CHARGING)) {
            return;
        }
        level.setBlock(pos, state.setValue(CHARGING, true), 3);
        level.scheduleTick(pos, this, CHARGE_TICKS);
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.BLOCKS, 3f, 0.6f);
        level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 2f, 0.5f);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CHARGING)) {
            level.removeBlock(pos, false);
            BedrockBeam.start(level, pos);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CHARGING)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        for (int i = 0; i < 6; i++) {
            level.addParticle(ParticleTypes.ENCHANT, x, y, z,
                    (random.nextDouble() - 0.5) * 4, (random.nextDouble() - 0.5) * 4, (random.nextDouble() - 0.5) * 4);
        }
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 1.05, z, 0, 0.05, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() - 0.05, z, 0, -0.05, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.examplemod.bedrock_breaker").withStyle(ChatFormatting.GRAY));
    }
}
