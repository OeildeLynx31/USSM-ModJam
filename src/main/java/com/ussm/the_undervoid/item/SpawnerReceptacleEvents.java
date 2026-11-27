package com.ussm.the_undervoid.item;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.network.SpawnerCapturePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = UnderVoidMod.MODID)
public class SpawnerReceptacleEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreakSpawner(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockState state = event.getState();
        if (!state.is(Blocks.SPAWNER)) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }

        ItemStack receptacle = player.getOffhandItem();
        if (!receptacle.is(ModItems.SPAWNER_RECEPTACLE.get())) {
            return;
        }
        if (!player.isCreative() && !player.getMainHandItem().isCorrectToolForDrops(state)) {
            return;
        }

        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
            return;
        }

        ItemStack stack = new ItemStack(ModItems.CAPTURED_SPAWNER.get());
        BlockItem.setBlockEntityData(stack, spawner.getType(), spawner.saveCustomOnly(level.registryAccess()));

        var server = level.getServer();
        server.tell(new TickTask(server.getTickCount(), () -> {
            if (event.isCanceled() || level.getBlockState(pos).is(Blocks.SPAWNER)
                    || !level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
                    || player.isRemoved() || player.serverLevel() != level
                    || player.getOffhandItem() != receptacle
                    || !receptacle.is(ModItems.SPAWNER_RECEPTACLE.get())) {
                return;
            }

            Block.popResource(level, pos, stack);
            receptacle.shrink(1);
            level.playSound(null, player, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1f, 1f);
            PacketDistributor.sendToPlayer(player, SpawnerCapturePayload.INSTANCE);
        }));
    }
}