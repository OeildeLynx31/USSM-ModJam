package com.ussm.examplemod.util;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> HEDGEHOG_SPAWNABLE_ON = createTag("hedgehog_spawnable_on");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> HEDGEHOG_FOOD = createTag("hedgehog_food");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name));
        }
    }
}
