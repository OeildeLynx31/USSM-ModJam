package com.ussm.examplemod.item;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class SpawnerReceptacleEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpawner(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        if (!state.is(Blocks.SPAWNER)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isSpectator()) {
            return;
        }
        // Le réceptacle doit être dans la main secondaire
        if (!player.getOffhandItem().is(ModItems.SPAWNER_RECEPTACLE.get())) {
            return;
        }
        // En survie, il faut une pioche (sinon un spawner ne donne rien, comme en vanilla)
        if (!player.isCreative() && !player.getMainHandItem().isCorrectToolForDrops(state)) {
            return;
        }

        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
            return;
        }

        ItemStack stack = new ItemStack(ModItems.CAPTURED_SPAWNER.get());
        CompoundTag tag = new CompoundTag();

        // ID obligatoire du block entity
        tag.putString("id", "minecraft:mob_spawner");

        // Données du mob
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", "minecraft:armadillo");

        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entityTag);

        tag.put("SpawnData", spawnData);

        // Ajoute les données au composant
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        Block.popResource(level, pos, stack);
    }
}