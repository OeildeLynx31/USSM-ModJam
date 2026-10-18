package com.ussm.examplemod.block;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.ussm.examplemod.block.custom.*;
import com.ussm.examplemod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    //enregistrer les blocs ici
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ExampleMod.MODID);

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
            () -> new DarkDirt(BlockBehaviour.Properties.of().strength(1.5f)
                    .sound(SoundType.GRASS)));


    // <-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_->
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        RegisterBlockItem(name, toReturn);
        return toReturn;
    }


    //Register Item Block
    private static <T extends Block> void RegisterBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}