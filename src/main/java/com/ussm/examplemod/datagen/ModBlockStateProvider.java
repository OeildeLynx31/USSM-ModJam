package com.ussm.examplemod.datagen;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
        blockWithItem(ModBlocks.DARK_DIRT);

        logBlock(((RotatedPillarBlock) ModBlocks.VOID_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.VOID_WOOD.get()), blockTexture(ModBlocks.VOID_LOG.get()), blockTexture(ModBlocks.VOID_LOG.get()));

        blockItem(ModBlocks.VOID_LOG);
        blockItem(ModBlocks.VOID_WOOD);

        blockWithItem(ModBlocks.VOID_PLANKS);

        leavesBlock(ModBlocks.VOID_LEAVES);
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

