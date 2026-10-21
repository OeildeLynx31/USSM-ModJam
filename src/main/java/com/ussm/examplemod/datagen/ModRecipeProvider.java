package com.ussm.examplemod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import com.ussm.examplemod.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder { // Juste ne TOUCHES PAS à la datagen, sinon ban
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        /* Example of recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GUNPOWDER_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(recipeOutput);
         */
    }
}

