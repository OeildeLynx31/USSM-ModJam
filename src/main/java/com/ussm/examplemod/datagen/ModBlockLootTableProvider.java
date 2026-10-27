package com.ussm.examplemod.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import com.ussm.examplemod.block.ModBlocks;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.VOID_STONE.get());
        dropSelf(ModBlocks.VOID_LOG.get());
        dropSelf(ModBlocks.VOID_WOOD.get());
        dropSelf(ModBlocks.VOID_PLANKS.get());
        add(ModBlocks.VOID_LEAVES.get(), createLeavesDrops(ModBlocks.VOID_LEAVES.get(), ModBlocks.VOID_SAPLING.get(), 0.2f));
        dropSelf(ModBlocks.VOID_SAPLING.get());
        dropSelf(ModBlocks.BLOOM_FLOWER.get());
        add(ModBlocks.POTTED_BLOOM_FLOWER.get(), createPotFlowerItemTable(ModBlocks.BLOOM_FLOWER.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }


}

