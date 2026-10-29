package com.ussm.examplemod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CapturedSpawnerItem extends BlockItem {
    public CapturedSpawnerItem(Block block, Properties properties) {
        super(block, properties);
    }

    // Remet le mob dans le spawner posé, même pour un joueur non-op
    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return data.loadInto(blockEntity, level.registryAccess());
            }
        }
        return false;
    }

    // Nom affiché : "Monster Spawner (Zombie)"
    @Override
    public Component getName(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            String id = tag.getCompound("SpawnData").getCompound("entity").getString("id");
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null) {
                Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(location);
                if (type.isPresent()) {
                    return Component.translatable("block.minecraft.spawner")
                            .append(" (")
                            .append(type.get().getDescription())
                            .append(")");
                }
            }
        }
        return super.getName(stack);
    }
}