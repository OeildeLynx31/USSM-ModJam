package com.ussm.the_undervoid.datagen;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.worldgen.ModBiomeModifiers;
import com.ussm.the_undervoid.worldgen.ModConfiguredFeatures;
import com.ussm.the_undervoid.worldgen.ModNoiseGeneratorSettings;
import com.ussm.the_undervoid.worldgen.ModPlacedFeatures;
import com.ussm.the_undervoid.worldgen.biome.ModBiomes;
import com.ussm.the_undervoid.worldgen.dimension.ModDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::boostrap)
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(UnderVoidMod.MODID));
    }
}