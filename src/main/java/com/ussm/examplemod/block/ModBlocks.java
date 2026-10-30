package com.ussm.examplemod.block;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
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

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
            () -> new DarkDirt(BlockBehaviour.Properties.of().strength(1.5f)
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<Block> CORRUPTED_GRASS_BLOCK = registerBlock("corrupted_grass_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5f)
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<Block> DARK_DEEPSLATE = registerBlock("dark_deepslate",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));

    public static final DeferredBlock<Block> CORRUPTED_GRASS = registerBlock("corrupted_grass",
            () -> new CorruptedGrassPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)));

    public static final DeferredBlock<Block> DEAD_SHRUB = registerBlock("dead_shrub",
            () -> new DeadBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH)));

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

    // --- Les Fleurs utilisées pour lezs potions ---
    public static final DeferredBlock<Block> CALM_LILY = registerBlock("calm_lily",
            () -> new FlowerBlock(MobEffects.REGENERATION, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_CALM_LILY = BLOCKS.register("potted_calm_lily",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CALM_LILY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> NIGHTSHADE_BLOOM = registerBlock("nightshade_bloom",
            () -> new FlowerBlock(MobEffects.POISON, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_NIGHTSHADE_BLOOM = BLOCKS.register("potted_nightshade_bloom",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, NIGHTSHADE_BLOOM,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

        public static final DeferredBlock<Block> ASHEN_POPPY = registerBlock("ashen_poppy",
            () -> new FlowerBlock(MobEffects.WITHER, 4f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_ASHEN_POPPY = BLOCKS.register("potted_ashen_poppy",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ASHEN_POPPY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    // --- Fleurs décoratives ---
    public static final DeferredBlock<Block> PALE_TULIP = registerBlock("pale_tulip",
            () -> new FlowerBlock(MobEffects.SATURATION, 7f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_PALE_TULIP = BLOCKS.register("potted_pale_tulip",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PALE_TULIP,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> EMBER_DAISY = registerBlock("ember_daisy",
            () -> new FlowerBlock(MobEffects.FIRE_RESISTANCE, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_EMBER_DAISY = BLOCKS.register("potted_ember_daisy",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, EMBER_DAISY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> GLASS_ORCHID = registerBlock("glass_orchid",
            () -> new FlowerBlock(MobEffects.NIGHT_VISION, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_GLASS_ORCHID = BLOCKS.register("potted_glass_orchid",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GLASS_ORCHID,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> MOSS_VIOLET = registerBlock("moss_violet",
            () -> new FlowerBlock(MobEffects.JUMP, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_MOSS_VIOLET = BLOCKS.register("potted_moss_violet",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MOSS_VIOLET,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> INK_BLOSSOM = registerBlock("ink_blossom",
            () -> new FlowerBlock(MobEffects.BLINDNESS, 4f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_INK_BLOSSOM = BLOCKS.register("potted_ink_blossom",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, INK_BLOSSOM,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> DUSK_BELL = registerBlock("dusk_bell",
            () -> new FlowerBlock(MobEffects.WEAKNESS, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_DUSK_BELL = BLOCKS.register("potted_dusk_bell",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DUSK_BELL,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

    public static final DeferredBlock<Block> GHOST_FERN_BLOOM = registerBlock("ghost_fern_bloom",
            () -> new FlowerBlock(MobEffects.INVISIBILITY, 6f,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));
    public static final DeferredBlock<Block> POTTED_GHOST_FERN_BLOOM = BLOCKS.register("potted_ghost_fern_bloom",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GHOST_FERN_BLOOM,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));

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


    // <-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_->
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        RegisterBlockItem(name, toReturn);
        return toReturn;
    }

    private static DeferredBlock<Block> registerPottedFlower(String name, DeferredBlock<Block> flower) {
        return BLOCKS.register("potted_" + name,
                () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, flower,
                        BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));
    }

    //Register Item Block
    private static <T extends Block> void RegisterBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}