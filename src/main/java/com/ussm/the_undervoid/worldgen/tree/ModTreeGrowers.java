package com.ussm.the_undervoid.worldgen.tree;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower VOID = new TreeGrower(UnderVoidMod.MODID + ":void",
            Optional.empty(), Optional.of(ModConfiguredFeatures.VOID_TREE_KEY), Optional.empty());

}
