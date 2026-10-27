package com.ussm.examplemod.worldgen.tree;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower VOID = new TreeGrower(ExampleMod.MODID + ":void",
            Optional.empty(), Optional.of(ModConfiguredFeatures.VOID_TREE_KEY), Optional.empty());
}
