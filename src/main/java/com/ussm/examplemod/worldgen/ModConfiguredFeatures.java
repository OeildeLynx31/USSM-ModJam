package com.ussm.examplemod.worldgen;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.block.ModBlocks;
import com.ussm.examplemod.block.custom.DecorBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> VOID_TREE_KEY = registerKey("void_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_FLOWER_PATCH_KEY = registerKey("bloom_flower_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_RIFT_KEY = registerKey("bloom_rift");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOOM_DECOR_KEY = registerKey("gloom_decor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_DECOR_KEY = registerKey("bloom_decor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_CHAIN_KEY = registerKey("bloom_chain");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, VOID_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.VOID_LOG.get()),
                new StraightTrunkPlacer(5, 2, 0),

                BlockStateProvider.simple(ModBlocks.VOID_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),

                new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

        register(context, BLOOM_FLOWER_PATCH_KEY, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BLOOM_FLOWER.get())))));

        register(context, BLOOM_RIFT_KEY, Feature.BLOCK_COLUMN,
                BlockColumnConfiguration.simple(ConstantInt.of(3), BlockStateProvider.simple(ModBlocks.BLOOM_RIFT.get())));

        // os, cranes et cailloux en surface, champis et yeux dans le bloom
        register(context, GLOOM_DECOR_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(ModBlocks.BONE_PILE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 8)
                        .add(ModBlocks.BONE_PILE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 8)
                        .add(ModBlocks.BONE_PILE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 8)
                        .add(ModBlocks.BONE_PILE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 8)
                        .add(ModBlocks.SCATTERED_BONE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 8)
                        .add(ModBlocks.SCATTERED_BONE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 8)
                        .add(ModBlocks.SCATTERED_BONE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 8)
                        .add(ModBlocks.SCATTERED_BONE.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 8)
                        .add(ModBlocks.PEBBLES.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 6)
                        .add(ModBlocks.PEBBLES.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 6)
                        .add(ModBlocks.PEBBLES.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 6)
                        .add(ModBlocks.PEBBLES.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 6)
                        .add(ModBlocks.STONE_SKULL.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 3)
                        .add(ModBlocks.STONE_SKULL.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 3)
                        .add(ModBlocks.STONE_SKULL.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 3)
                        .add(ModBlocks.STONE_SKULL.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 3)
                        .add(ModBlocks.EYE_NEST.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 1)
                        .add(ModBlocks.EYE_NEST.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 1)
                        .add(ModBlocks.EYE_NEST.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 1)
                        .add(ModBlocks.EYE_NEST.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 1))));

        register(context, BLOOM_DECOR_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(ModBlocks.BLOOM_MUSHROOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 7)
                        .add(ModBlocks.BLOOM_MUSHROOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 7)
                        .add(ModBlocks.BLOOM_MUSHROOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 7)
                        .add(ModBlocks.BLOOM_MUSHROOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 7)
                        .add(ModBlocks.EYE_BLOOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 3)
                        .add(ModBlocks.EYE_BLOOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 3)
                        .add(ModBlocks.EYE_BLOOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 3)
                        .add(ModBlocks.EYE_BLOOM.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 3))));

        register(context, BLOOM_CHAIN_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(ModBlocks.HANGING_CHAIN.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.NORTH), 1)
                        .add(ModBlocks.HANGING_CHAIN.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.EAST), 1)
                        .add(ModBlocks.HANGING_CHAIN.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.SOUTH), 1)
                        .add(ModBlocks.HANGING_CHAIN.get().defaultBlockState().setValue(DecorBlock.FACING, Direction.WEST), 1))));
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name)
        );
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
