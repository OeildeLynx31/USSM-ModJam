package com.ussm.examplemod.datagen;

import com.ussm.examplemod.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.VOID_STONE.get());
        this.add(ModBlocks.VOID_MATTER_ORE.get(),
                block -> createOreDrops(
                        ModBlocks.VOID_MATTER_ORE.get(),
                        ModItems.UNSTABLE_MATTER.get(),
                        0, 2
                )
        );
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

    protected LootTable.Builder createOreDrops(Block block, Item item, int min, int max) {
        HolderLookup.RegistryLookup<Enchantment> enchantments =
                this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        return this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(min, max)
                                ))
                                .apply(ApplyBonusCount.addOreBonusCount(
                                        enchantments.getOrThrow(Enchantments.FORTUNE)
                                ))
                )
        );
    }


}

