package com.ussm.examplemod.worldgen;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class ModNoiseGeneratorSettings {

    public static final ResourceKey<NoiseGeneratorSettings> UNDERVOID_NOISE_SETTINGS =
            ResourceKey.create(
                    Registries.NOISE_SETTINGS,
                    ResourceLocation.fromNamespaceAndPath(
                            ExampleMod.MODID, "undervoid"
                    )
            );

    public static void bootstrap(
            BootstrapContext<NoiseGeneratorSettings> context
    ) {
        // does nothing. Everything is in the /worldgen/noise_settings/undervoid.json file. I keep this to not break the data generator.
    }
}