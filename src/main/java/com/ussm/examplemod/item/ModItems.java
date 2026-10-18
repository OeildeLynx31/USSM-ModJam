package com.ussm.examplemod.item;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.ussm.examplemod.block.ModBlocks;
import com.ussm.examplemod.item.custom.*;

import java.util.ArrayList;
import java.util.Optional;
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);
    //Enregistrer les Items ici
    //-------------------------------------GENERAL-------------------------------------------------------

    public static final DeferredItem<Item> SPAWNER_RECEPTACLE = ITEMS.register("spawner_receptacle",
            () -> new SpawnerReceptacle(new Item.Properties()));

    //>-------------------------------------------------------
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}