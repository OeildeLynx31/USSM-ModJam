package com.ussm.examplemod.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import com.ussm.examplemod.block.ModBlocks;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.DARK_DIRT.get());
        dropSelf(ModBlocks.VOID_LOG.get());
        dropSelf(ModBlocks.VOID_WOOD.get());
        dropSelf(ModBlocks.VOID_PLANKS.get());
        add(ModBlocks.VOID_LEAVES.get(), createLeavesDrops(ModBlocks.VOID_LEAVES.get(), ModBlocks.VOID_SAPLING.get(), 0.2f));
        dropSelf(ModBlocks.VOID_SAPLING.get());
        dropSelf(ModBlocks.BLOOM_FLOWER.get());
        add(ModBlocks.POTTED_BLOOM_FLOWER.get(), createPotFlowerItemTable(ModBlocks.BLOOM_FLOWER.get()));
        flowerLoot(ModBlocks.CALM_LILY, ModBlocks.POTTED_CALM_LILY);
        flowerLoot(ModBlocks.NIGHTSHADE_BLOOM, ModBlocks.POTTED_NIGHTSHADE_BLOOM);
        flowerLoot(ModBlocks.ASHEN_POPPY, ModBlocks.POTTED_ASHEN_POPPY);
        flowerLoot(ModBlocks.PALE_TULIP, ModBlocks.POTTED_PALE_TULIP);
        flowerLoot(ModBlocks.EMBER_DAISY, ModBlocks.POTTED_EMBER_DAISY);
        flowerLoot(ModBlocks.GLASS_ORCHID, ModBlocks.POTTED_GLASS_ORCHID);
        flowerLoot(ModBlocks.MOSS_VIOLET, ModBlocks.POTTED_MOSS_VIOLET);
        flowerLoot(ModBlocks.INK_BLOSSOM, ModBlocks.POTTED_INK_BLOSSOM);
        flowerLoot(ModBlocks.DUSK_BELL, ModBlocks.POTTED_DUSK_BELL);
        flowerLoot(ModBlocks.GHOST_FERN_BLOOM, ModBlocks.POTTED_GHOST_FERN_BLOOM);
    }
    private void flowerLoot(DeferredBlock<Block> flower, DeferredBlock<Block> pottedFlower) {
        dropSelf(flower.get());
        add(pottedFlower.get(), createPotFlowerItemTable(flower.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }


}

