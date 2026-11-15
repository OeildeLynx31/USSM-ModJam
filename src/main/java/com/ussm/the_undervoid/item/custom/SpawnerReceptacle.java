package com.ussm.the_undervoid.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpawnerReceptacle extends Item {
    public SpawnerReceptacle(Item.Properties properties) {
        super(properties);
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.the_undervoid.spawner_receptacle_tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}