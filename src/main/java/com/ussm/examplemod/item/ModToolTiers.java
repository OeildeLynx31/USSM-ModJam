package com.ussm.examplemod.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier KATANA = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL,
            1500, 6f, 2f, 14, () -> Ingredient.of(ModItems.BLADE_FRAGMENT));
}
