package com.ussm.examplemod.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.worldgen.ModNoiseGeneratorSettings;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.List;
import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<LevelStem> UNDERVOID_DIM =
            ResourceKey.create(
                    Registries.LEVEL_STEM,
                    ResourceLocation.fromNamespaceAndPath(
                            ExampleMod.MODID, "undervoid"
                    )
            );

    public static final ResourceKey<Level> UNDERVOID_DIM_LEVEL_KEY =
            ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(
                            ExampleMod.MODID, "undervoid"
                    )
            );

    public static final ResourceKey<DimensionType> UNDERVOID_DIM_TYPE =
            ResourceKey.create(
                    Registries.DIMENSION_TYPE,
                    ResourceLocation.fromNamespaceAndPath(
                            ExampleMod.MODID, "undervoid_type"
                    )
            );

    public static final ResourceLocation UNDERVOID_EFFECTS =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "undervoid");

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(UNDERVOID_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                UNDERVOID_EFFECTS, // effectsLocation
                0.1f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(
                                Pair.of(climate(Climate.Parameter.span(-1.0F, 0.35F), Climate.Parameter.span(-1.0F, 0.25F)), biomeRegistry.getOrThrow(ModBiomes.GLOOM)),
                                Pair.of(climate(Climate.Parameter.span(0.35F, 1.0F), Climate.Parameter.span(0.02F, 0.25F)), biomeRegistry.getOrThrow(ModBiomes.GLOOM)),
                                Pair.of(climate(Climate.Parameter.span(0.35F, 1.0F), Climate.Parameter.span(-1.0F, 0.02F)), biomeRegistry.getOrThrow(ModBiomes.MIASMA)),
                                Pair.of(climate(Climate.Parameter.span(-1.0F, 1.0F), Climate.Parameter.span(0.25F, 1.0F)), biomeRegistry.getOrThrow(ModBiomes.BLOOM))
                        ))),
                noiseGenSettings.getOrThrow(ModNoiseGeneratorSettings.UNDERVOID_NOISE_SETTINGS));

        LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.UNDERVOID_DIM_TYPE), noiseBasedChunkGenerator);

        context.register(UNDERVOID_DIM, stem);
    }

    private static Climate.ParameterPoint climate(Climate.Parameter humidity, Climate.Parameter depth) {
        Climate.Parameter zero = Climate.Parameter.point(0.0F);
        return Climate.parameters(zero, humidity, zero, zero, depth, zero, 0.0F);
    }
}