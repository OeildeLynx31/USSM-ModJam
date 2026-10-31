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
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
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

    public static final DeferredBlock<Block> DIRTY_VOID_STONE = registerBlock("dirty_void_stone",
            () -> new DirtyVoidStone(BlockBehaviour.Properties.of().strength(1.5f)
                    .sound(SoundType.NYLIUM).randomTicks()));

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
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
            () -> new VoidSaplingBlock(
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
            () -> new FlowerBlock(ModEffects.SERENITY, 8, BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)
                    .lightLevel(state -> 7)));

    public static final DeferredBlock<Block> POTTED_BLOOM_FLOWER = BLOCKS.register("potted_bloom_flower",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), BLOOM_FLOWER,
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

    //---------------- decos (modeles 3D), seulement generées dans le monde ----------------
    public static final DeferredBlock<Block> PEBBLES = registerBlock("pebbles",
            () -> new DecorBlock(3.2, 0, 3.1, 14.9, 5, 14.6, false, false,
                    BlockBehaviour.Properties.of().sound(SoundType.STONE).instabreak().noLootTable().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> HANGING_CHAIN = registerBlock("hanging_chain",
            () -> new DecorBlock(4, 0, 4, 12, 16, 12, false, true,
                    BlockBehaviour.Properties.of().sound(SoundType.CHAIN).instabreak().noLootTable().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> BLOOM_MUSHROOM = registerBlock("bloom_mushroom",
            () -> new DecorBlock(1, 0, 2, 15, 3, 15, false, false,
                    BlockBehaviour.Properties.of().sound(SoundType.FUNGUS).instabreak().noLootTable().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> STONE_SKULL = registerBlock("stone_skull",
            () -> new DecorBlock(3.5, 0, 2.4, 12, 12.1, 12.2, true, false,
                    BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK).strength(1f).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> EYE_NEST = registerBlock("eye_nest",
            () -> new DecorBlock(0, 0, 0, 16, 16, 15.9, true, false,
                    BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK).strength(1.5f).lightLevel(state -> 3).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> BONE_PILE = registerBlock("bone_pile",
            () -> new DecorBlock(0.6, 0, 1, 15.4, 6.7, 15, false, false,
                    BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SCATTERED_BONE = registerBlock("scattered_bone",
            () -> new DecorBlock(7, 0, 7, 9, 11.8, 14.2, false, false,
                    BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> EYE_BLOOM = registerBlock("eye_bloom",
            () -> new DecorBlock(0, 0, 0, 16, 8.6, 16, false, false,
                    BlockBehaviour.Properties.of().sound(SoundType.SPORE_BLOSSOM).instabreak().noLootTable().lightLevel(state -> 5).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));

    //---------------- plantes du modeleur (modeles 3D aussi) ----------------
    // bloom
    public static final DeferredBlock<Block> VOID_ASTER = registerBlock("void_aster",
            () -> new UndervoidPlantBlock(4, 0, 4, 12, 7, 12, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> CAVE_MARIGOLD = registerBlock("cave_marigold",
            () -> new UndervoidPlantBlock(5, 0, 5, 11, 15, 11, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> GLIMMER_CLUSTER = registerBlock("glimmer_cluster",
            () -> new UndervoidPlantBlock(2.2, 0, 3.2, 13.8, 6.7, 14.8, false,
                    BlockBehaviour.Properties.of().sound(SoundType.CAVE_VINES).instabreak().lightLevel(state -> 9).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    // surface du gloom
    public static final DeferredBlock<Block> GLOOM_STAR = registerBlock("gloom_star",
            () -> new UndervoidPlantBlock(1, 0, 1, 15, 7.8, 15, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> EYEGRASS = registerBlock("eyegrass",
            () -> new UndervoidPlantBlock(2, 0, 2, 14, 15, 14, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noLootTable().replaceable().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> PALE_TUFT = registerBlock("pale_tuft",
            () -> new UndervoidPlantBlock(4, 0, 4, 12, 12, 12, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noLootTable().replaceable().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> CLAW_STALK = registerBlock("claw_stalk",
            () -> new UndervoidPlantBlock(3.2, 0, 2.4, 11.8, 16, 9, false,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    // miasme
    public static final DeferredBlock<Block> VENOM_DART_FLOWER = registerBlock("venom_dart_flower",
            () -> new VenomDartFlowerBlock(1, 0, 1, 14, 16, 15,
                    BlockBehaviour.Properties.of().sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SPORETHORN = registerBlock("sporethorn",
            () -> new SporethornBlock(3, 0, 3, 13, 16, 13,
                    BlockBehaviour.Properties.of().sound(SoundType.SPORE_BLOSSOM).strength(0.4f).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> BLOOM_RIFT = BLOCKS.register("bloom_rift",
            () -> new BloomRiftBlock(BlockBehaviour.Properties.of().strength(-1f, 3600000f).noLootTable()
                    .noCollission().forceSolidOn().lightLevel(state -> 12).sound(SoundType.AMETHYST)
                    .pushReaction(PushReaction.BLOCK).isValidSpawn(Blocks::never)));

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
    //-------------------------------- bois du vide --------------------------------
    public static final DeferredBlock<Block> VOID_STAIRS = registerBlock("void_stairs",
            () -> new StairBlock(ModBlocks.VOID_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)));
    public static final DeferredBlock<Block> VOID_SLAB = registerBlock("void_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)));

    public static final DeferredBlock<Block> VOID_FENCE = registerBlock("void_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final DeferredBlock<Block> VOID_FENCE_GATE = registerBlock("void_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final DeferredBlock<Block> VOID_DOOR = registerBlock("void_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));
    public static final DeferredBlock<Block> VOID_TRAPDOOR = registerBlock("void_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final DeferredBlock<Block> VOID_BUTTON = registerBlock("void_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final DeferredBlock<Block> VOID_PRESSURE_PLATE = registerBlock("void_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final DeferredBlock<Block> VOID_LEAVES = registerBlock("void_leaves",
            () -> new VoidLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)){
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

    public static final DeferredBlock<Block> BEDROCK_BREAKER = registerBlock("bedrock_breaker",
            () -> new BedrockBreakerBlock(BlockBehaviour.Properties.of().strength(5f, 1200f)
                    .requiresCorrectToolForDrops().sound(SoundType.NETHERITE_BLOCK).pushReaction(PushReaction.BLOCK)
                    .lightLevel(state -> state.getValue(BedrockBreakerBlock.CHARGING) ? 12 : 0)));


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