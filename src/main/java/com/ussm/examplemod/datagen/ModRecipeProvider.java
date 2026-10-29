package com.ussm.examplemod.datagen;

import com.ussm.examplemod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOID_MATTER.get())
                .pattern("BBB")
                .pattern("BVB")
                .pattern("BBB")
                .define('B', ModItems.UNSTABLE_MATTER.get())
                .define('V', ModItems.VOID_STABILIZER.get())
                .unlockedBy("has_unstable_matter",
                        has(ModItems.UNSTABLE_MATTER.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOID_STABILIZER.get())
                .pattern(" B ")
                .pattern("BVB")
                .pattern(" B ")
                .define('B', Items.NETHERITE_SCRAP)
                .define('V', Items.ECHO_SHARD)
                .unlockedBy("has_echo_shard",
                        has(Items.ECHO_SHARD))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VOID_BALL.get(), 8)
                .requires(ModItems.VOID_MATTER.get())
                .unlockedBy("has_void_matter", has(ModItems.VOID_MATTER.get()))
                .save(recipeOutput);
    }
}

