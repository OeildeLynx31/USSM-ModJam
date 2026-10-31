package com.ussm.examplemod.datagen;

import com.ussm.examplemod.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.DARK_DEEPSLATE.get());
        add(ModBlocks.CORRUPTED_GRASS.get(), createShearsOnlyDrop(ModBlocks.CORRUPTED_GRASS.get()));
        add(ModBlocks.DEAD_SHRUB.get(), createShearsDispatchTable(ModBlocks.DEAD_SHRUB.get(),
                applyExplosionDecay(ModBlocks.DEAD_SHRUB.get(), LootItem.lootTableItem(Items.STICK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));

        dropSelf(ModBlocks.DARK_DIRT.get());
        dropSelf(ModBlocks.VOID_STONE.get());
        add(ModBlocks.BONE_PILE.get(), block -> createSingleItemTable(Items.BONE, UniformGenerator.between(1, 3)));
        add(ModBlocks.SCATTERED_BONE.get(), block -> createSingleItemTable(Items.BONE));
        add(ModBlocks.STONE_SKULL.get(), block -> createSingleItemTable(Items.BONE, UniformGenerator.between(1, 2)));
        add(ModBlocks.EYE_NEST.get(), block -> createSingleItemTable(ModBlocks.VOID_LOG.get()));
        dropSelf(ModBlocks.VOID_ASTER.get());
        dropSelf(ModBlocks.CAVE_MARIGOLD.get());
        dropSelf(ModBlocks.GLIMMER_CLUSTER.get());
        dropSelf(ModBlocks.GLOOM_STAR.get());
        dropSelf(ModBlocks.CLAW_STALK.get());
        dropSelf(ModBlocks.VENOM_DART_FLOWER.get());
        dropSelf(ModBlocks.SPORETHORN.get());
        add(ModBlocks.DIRTY_VOID_STONE.get(), block -> createSingleItemTableWithSilkTouch(block, ModBlocks.VOID_STONE.get()));
        dropSelf(ModBlocks.BEDROCK_BREAKER.get());
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
        dropSelf(ModBlocks.VOID_STAIRS.get());
        add(ModBlocks.VOID_SLAB.get(), block -> createSlabItemTable(ModBlocks.VOID_SLAB.get()));
        dropSelf(ModBlocks.VOID_FENCE.get());
        dropSelf(ModBlocks.VOID_FENCE_GATE.get());
        add(ModBlocks.VOID_DOOR.get(), block -> createDoorTable(ModBlocks.VOID_DOOR.get()));
        dropSelf(ModBlocks.VOID_TRAPDOOR.get());
        dropSelf(ModBlocks.VOID_BUTTON.get());
        dropSelf(ModBlocks.VOID_PRESSURE_PLATE.get());
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

