package com.ussm.examplemod.potion;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class ModBrewing {
    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ModBlocks.BLOOM_FLOWER.asItem(), ModPotions.SERENITY);
        builder.addMix(ModPotions.SERENITY, Items.REDSTONE, ModPotions.LONG_SERENITY);
        builder.addMix(ModPotions.SERENITY, Items.GLOWSTONE_DUST, ModPotions.STRONG_SERENITY);

        builder.addMix(Potions.AWKWARD, Items.GLOW_BERRIES, ModPotions.CLARITY);
        builder.addMix(ModPotions.CLARITY, Items.REDSTONE, ModPotions.LONG_CLARITY);
        builder.addMix(ModPotions.CLARITY, Items.GLOWSTONE_DUST, ModPotions.STRONG_CLARITY);
    }
}
