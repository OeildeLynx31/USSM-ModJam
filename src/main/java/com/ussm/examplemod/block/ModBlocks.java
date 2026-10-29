package com.ussm.examplemod.block;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.ussm.examplemod.block.custom.*;
import com.ussm.examplemod.item.ModItems;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.worldgen.tree.ModTreeGrowers;

import java.util.function.Supplier;

public class ModBlocks {
    //enregistrer les blocs ici
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ExampleMod.MODID);

    public static final DeferredBlock<Block> VOID_STONE = registerBlock("void_stone",
            () -> new VoidStone(BlockBehaviour.Properties.of().strength(1.5f)
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> VOID_LOG = registerBlock(
            "void_log",
            () -> new ModFlammableRotatedPillarBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                            .strength(3f)
            )
    );

    public static final DeferredBlock<Block> VOID_WOOD = registerBlock(
            "void_wood",
            () -> new ModFlammableRotatedPillarBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                            .strength(3f)
            )
    );

    public static final DeferredBlock<Block> VOID_SAPLING = registerBlock(
            "void_sapling",
            () -> new SaplingBlock(
                    ModTreeGrowers.VOID,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
            )
    );

    /*public static final DeferredBlock<Block> STRIPPED_VOID_LOG =
            BLOCKS.registerBlock("stripped_void_log",
                    properties -> new ModFlammableRotatedPillarBlock(
                            properties.strength(3f)
                    ),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG));

    public static final DeferredBlock<Block> STRIPPED_VOID_WOOD =
            BLOCKS.registerBlock("stripped_void_wood",
                    properties -> new ModFlammableRotatedPillarBlock(
                            properties.strength(3f)
                    ),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD));*/
    
    public static final DeferredBlock<Block> BLOOM_FLOWER = registerBlock("bloom_flower",
            () -> new FlowerBlock(ModEffects.SERENITY, 8f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM).lightLevel(state -> 7)));

    public static final DeferredBlock<Block> POTTED_BLOOM_FLOWER = BLOCKS.register("potted_bloom_flower",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BLOOM_FLOWER,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM).lightLevel(state -> 7)));

    public static final DeferredBlock<Block> BLOOM_RIFT = BLOCKS.register("bloom_rift",
            () -> new BloomRiftBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .forceSolidOn()
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(state -> 12)
                    .noLootTable()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK)
                    .isValidSpawn(Blocks::never)
                    .sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> VOID_PLANKS = registerBlock("void_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final DeferredBlock<Block> VOID_LEAVES = registerBlock("void_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> VOID_MATTER_ORE = registerBlock("void_matter_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ANCIENT_DEBRIS)));


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