package com.ussm.examplemod.datagen;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider { // Juste ne TOUCHES PAS à la datagen, sinon ban
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExampleMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.VOID_STONE);
        simpleBlockWithItem(ModBlocks.DIRTY_VOID_STONE.get(), models().cubeBottomTop("dirty_void_stone",
                modLoc("block/corrupted_grass_block_side"), modLoc("block/void_stone"), modLoc("block/corrupted_grass_block_top")));
        blockWithItem(ModBlocks.VOID_MATTER_ORE);
        simpleBlockWithItem(ModBlocks.BEDROCK_BREAKER.get(), models().cubeColumn("bedrock_breaker",
                modLoc("block/bedrock_breaker_side"), modLoc("block/bedrock_breaker_end")));
        blockWithItem(ModBlocks.DARK_DIRT);
        blockWithItem(ModBlocks.DARK_DEEPSLATE);
        saplingBlock(ModBlocks.CORRUPTED_GRASS);
        saplingBlock(ModBlocks.DEAD_SHRUB);

        logBlock(((RotatedPillarBlock) ModBlocks.VOID_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.VOID_WOOD.get()), blockTexture(ModBlocks.VOID_LOG.get()), blockTexture(ModBlocks.VOID_LOG.get()));

        blockItem(ModBlocks.VOID_LOG);
        blockItem(ModBlocks.VOID_WOOD);

        blockWithItem(ModBlocks.VOID_PLANKS);

        stairsBlock(((StairBlock) ModBlocks.VOID_STAIRS.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.VOID_SLAB.get()), blockTexture(ModBlocks.VOID_PLANKS.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.VOID_FENCE.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.VOID_FENCE_GATE.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.VOID_DOOR.get()), modLoc("block/void_door_bottom"), modLoc("block/void_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.VOID_TRAPDOOR.get()), modLoc("block/void_trapdoor"), true, "cutout");
        buttonBlock(((ButtonBlock) ModBlocks.VOID_BUTTON.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.VOID_PRESSURE_PLATE.get()), blockTexture(ModBlocks.VOID_PLANKS.get()));

        blockItem(ModBlocks.VOID_STAIRS);
        blockItem(ModBlocks.VOID_SLAB);
        blockItem(ModBlocks.VOID_FENCE_GATE);
        blockItem(ModBlocks.VOID_PRESSURE_PLATE);
        simpleBlockItem(ModBlocks.VOID_TRAPDOOR.get(), new ModelFile.UncheckedModelFile(modLoc("block/void_trapdoor_bottom")));

        leavesBlock(ModBlocks.VOID_LEAVES);

        saplingBlock(ModBlocks.VOID_SAPLING);
        saplingBlock(ModBlocks.BLOOM_FLOWER);
        simpleBlock(ModBlocks.POTTED_BLOOM_FLOWER.get(), models().singleTexture("potted_bloom_flower",
                ResourceLocation.withDefaultNamespace("block/flower_pot_cross"), "plant",
                blockTexture(ModBlocks.BLOOM_FLOWER.get())).renderType("cutout"));
        simpleBlock(ModBlocks.BLOOM_RIFT.get(), models().cross("bloom_rift", blockTexture(ModBlocks.BLOOM_RIFT.get())).renderType("translucent"));

        flowerBlock(ModBlocks.CALM_LILY, ModBlocks.POTTED_CALM_LILY);
        flowerBlock(ModBlocks.NIGHTSHADE_BLOOM, ModBlocks.POTTED_NIGHTSHADE_BLOOM);
        flowerBlock(ModBlocks.ASHEN_POPPY, ModBlocks.POTTED_ASHEN_POPPY);
        flowerBlock(ModBlocks.PALE_TULIP, ModBlocks.POTTED_PALE_TULIP);
        flowerBlock(ModBlocks.EMBER_DAISY, ModBlocks.POTTED_EMBER_DAISY);
        flowerBlock(ModBlocks.GLASS_ORCHID, ModBlocks.POTTED_GLASS_ORCHID);
        flowerBlock(ModBlocks.MOSS_VIOLET, ModBlocks.POTTED_MOSS_VIOLET);
        flowerBlock(ModBlocks.INK_BLOSSOM, ModBlocks.POTTED_INK_BLOSSOM);
        flowerBlock(ModBlocks.DUSK_BELL, ModBlocks.POTTED_DUSK_BELL);
        flowerBlock(ModBlocks.GHOST_FERN_BLOOM, ModBlocks.POTTED_GHOST_FERN_BLOOM);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(
                blockRegistryObject.get(),
                models().singleTexture(
                        BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(),
                        ResourceLocation.withDefaultNamespace("block/leaves"),
                        "all",
                        blockTexture(blockRegistryObject.get())
                ).renderType("cutout")
        );
    }

    private void saplingBlock(DeferredBlock<Block> block) {
        simpleBlock(
                block.get(),
                models().cross(
                        block.getId().getPath(),
                        blockTexture(block.get())
                ).renderType("cutout")
        );

        itemModels().withExistingParent(
                block.getId().getPath(),
                mcLoc("item/generated")
        ).texture("layer0", blockTexture(block.get()));
    }

    private void flowerBlock(DeferredBlock<Block> flower, DeferredBlock<Block> pottedFlower) {
        saplingBlock(flower);
        simpleBlock(pottedFlower.get(), models().singleTexture(
                pottedFlower.getId().getPath(),
                ResourceLocation.withDefaultNamespace("block/flower_pot_cross"), "plant",
                blockTexture(flower.get())).renderType("cutout"));
    }

    private ConfiguredModel[] states(BlockState state, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        // on fait ce qu'on veut

        return models;
    }
    private ConfiguredModel[] cutout() {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cubeAll("cave_block",
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "block/cave_block")).renderType("cutout"));

        return models;
    }


    private void blockItem(DeferredBlock<Block> block) {
        simpleBlockItem(
                block.get(),
                new ModelFile.UncheckedModelFile(
                        modLoc("block/" + block.getId().getPath())
                )
        );
    }

    private void simpleSidedBlock(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(
                deferredBlock.get(),
                models().cubeBottomTop(
                        deferredBlock.getId().getPath(),
                        modLoc("block/" + deferredBlock.getId().getPath() + "_side"),
                        modLoc("block/" + deferredBlock.getId().getPath() + "_bottom"),
                        modLoc("block/" + deferredBlock.getId().getPath() + "_top")
                )
        );
    }
    private void simpleSpawnerBlock(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(
                deferredBlock.get(),
                models().cubeBottomTop(
                        deferredBlock.getId().getPath(),
                        modLoc("block/" + deferredBlock.getId().getPath() + "_side"),
                        modLoc("block/spawner_bottom"),
                        modLoc("block/" + deferredBlock.getId().getPath() + "_top")
                ).renderType("cutout")
        );
    }


}

