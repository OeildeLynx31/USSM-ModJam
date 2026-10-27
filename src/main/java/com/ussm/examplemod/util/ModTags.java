package com.ussm.examplemod.util;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> HEDGEHOG_FOOD = TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hedgehog_food"));
    }

    public static class Blocks {
        public static final TagKey<Block> HEDGEHOG_SPAWNABLE_ON = TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hedgehog_spawnable_on"));
    }
}
