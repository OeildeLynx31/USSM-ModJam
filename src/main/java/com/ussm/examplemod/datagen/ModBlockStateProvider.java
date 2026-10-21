package com.ussm.examplemod.datagen;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider { // Juste ne TOUCHES PAS à la datagen, sinon ban
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExampleMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.DARK_DIRT);
    }
    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
    public void makeBush(SweetBerryBushBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }
    public void caveBlock(Block block) {
        Function<BlockState, ConfiguredModel[]> function = cutout -> cutout();

        getVariantBuilder(block).forAllStates(function);
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
    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(ExampleMod.MODID+":block/" + deferredBlock.getId().getPath()));
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

