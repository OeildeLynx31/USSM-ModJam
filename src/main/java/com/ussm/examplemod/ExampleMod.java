package com.ussm.examplemod;

import com.mojang.logging.LogUtils;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.block.ModBlocks;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.item.ModArmorMaterials;
import com.ussm.examplemod.item.ModItems;
import com.ussm.examplemod.particle.ModParticles;
import com.ussm.examplemod.potion.ModPotions;
import com.ussm.examplemod.potion.ModSanityTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import static com.ussm.examplemod.item.ModItems.SPAWNER_RECEPTACLE;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";

    public static final Logger LOGGER = LogUtils.getLogger(); // Directly reference a slf4j logger

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SPAWNER_RECEPTACLE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SPAWNER_RECEPTACLE.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ModBlocks.VOID_STONE.asItem());
                output.accept(ModBlocks.DIRTY_VOID_STONE.asItem());
                output.accept(ModBlocks.DEAD_SHRUB.asItem());
                output.accept(ModBlocks.MOSS_VIOLET.asItem());
                output.accept(ModBlocks.DARK_DIRT.asItem());
                output.accept(ModBlocks.DARK_DEEPSLATE.asItem());
                output.accept(ModBlocks.CORRUPTED_GRASS.asItem());
                output.accept(ModBlocks.VOID_MATTER_ORE.asItem());
                output.accept(ModBlocks.BEDROCK_BREAKER.asItem());
                output.accept(ModBlocks.VOID_SAPLING.asItem());
                output.accept(ModBlocks.VOID_LOG.asItem());
                output.accept(ModBlocks.VOID_WOOD.asItem());
                output.accept(ModBlocks.VOID_PLANKS.asItem());
                output.accept(ModBlocks.VOID_STAIRS.asItem());
                output.accept(ModBlocks.VOID_SLAB.asItem());
                output.accept(ModBlocks.VOID_FENCE.asItem());
                output.accept(ModBlocks.VOID_FENCE_GATE.asItem());
                output.accept(ModBlocks.VOID_DOOR.asItem());
                output.accept(ModBlocks.VOID_TRAPDOOR.asItem());
                output.accept(ModBlocks.VOID_BUTTON.asItem());
                output.accept(ModBlocks.VOID_PRESSURE_PLATE.asItem());
                output.accept(ModItems.VOID_BOAT.get());
                output.accept(ModItems.VOID_CHEST_BOAT.get());
                output.accept(ModBlocks.VOID_LEAVES.asItem());
                output.accept(ModBlocks.BLOOM_FLOWER.asItem());
                output.accept(ModBlocks.PEBBLES.asItem());
                output.accept(ModBlocks.HANGING_CHAIN.asItem());
                output.accept(ModBlocks.BLOOM_MUSHROOM.asItem());
                output.accept(ModBlocks.STONE_SKULL.asItem());
                output.accept(ModBlocks.EYE_NEST.asItem());
                output.accept(ModBlocks.BONE_PILE.asItem());
                output.accept(ModBlocks.SCATTERED_BONE.asItem());
                output.accept(ModBlocks.EYE_BLOOM.asItem());
                output.accept(ModBlocks.VOID_ASTER.asItem());
                output.accept(ModBlocks.CAVE_MARIGOLD.asItem());
                output.accept(ModBlocks.GLIMMER_CLUSTER.asItem());
                output.accept(ModBlocks.GLOOM_STAR.asItem());
                output.accept(ModBlocks.EYEGRASS.asItem());
                output.accept(ModBlocks.PALE_TUFT.asItem());
                output.accept(ModBlocks.CLAW_STALK.asItem());
                output.accept(ModBlocks.VENOM_DART_FLOWER.asItem());
                output.accept(ModBlocks.SPORETHORN.asItem());
                output.accept(ModItems.HAZMAT_FABRIC.get());
                output.accept(ModItems.HAZMAT_MASK.get());
                output.accept(ModItems.HAZMAT_SUIT.get());
                output.accept(ModItems.HAZMAT_LEGGINGS.get());
                output.accept(ModItems.HAZMAT_BOOTS.get());
                output.accept(ModItems.BLADE_FRAGMENT.get());
                output.accept(ModItems.KATANA.get());
                output.accept(ModItems.VOID_MATTER.get());
                output.accept(ModItems.UNSTABLE_MATTER.get());
                output.accept(ModItems.VOID_STABILIZER.get());
                output.accept(ModItems.VOID_BALL.get());
                output.accept(ModItems.HEDGEHOG_SPAWN_EGG.get());
                output.accept(ModItems.MIMIC_SPAWN_EGG.get());
                output.accept(ModItems.SMOKE_BOSS_SPAWN_EGG.get());
                output.accept(ModItems.COCKROACH_SPAWN_EGG.get());
                output.accept(ModItems.GLOOM_BIRD_SPAWN_EGG.get());
                output.accept(ModItems.BLOOM_BUTTERFLY_SPAWN_EGG.get());
                output.accept(ModItems.FLOATING_LIGHT_SPAWN_EGG.get());
                output.accept(ModItems.CARNIVOROUS_PLANT_SPAWN_EGG.get());
                output.accept(ModBlocks.CALM_LILY.asItem());
                output.accept(ModBlocks.NIGHTSHADE_BLOOM.asItem());
                output.accept(ModBlocks.ASHEN_POPPY.asItem());
                output.accept(ModBlocks.PALE_TULIP.asItem());
                output.accept(ModBlocks.EMBER_DAISY.asItem());
                output.accept(ModBlocks.GLASS_ORCHID.asItem());
                output.accept(ModBlocks.INK_BLOSSOM.asItem());
                output.accept(ModBlocks.DUSK_BELL.asItem());
                output.accept(ModBlocks.GHOST_FERN_BLOOM.asItem());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModParticles.register(modEventBus);
        ModSanityTab.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
            pot.addPlant(ModBlocks.BLOOM_FLOWER.getId(), ModBlocks.POTTED_BLOOM_FLOWER);

            pot.addPlant(ModBlocks.CALM_LILY.getId(), ModBlocks.POTTED_CALM_LILY);
            pot.addPlant(ModBlocks.NIGHTSHADE_BLOOM.getId(), ModBlocks.POTTED_NIGHTSHADE_BLOOM);
            pot.addPlant(ModBlocks.ASHEN_POPPY.getId(), ModBlocks.POTTED_ASHEN_POPPY);
            pot.addPlant(ModBlocks.PALE_TULIP.getId(), ModBlocks.POTTED_PALE_TULIP);
            pot.addPlant(ModBlocks.EMBER_DAISY.getId(), ModBlocks.POTTED_EMBER_DAISY);
            pot.addPlant(ModBlocks.GLASS_ORCHID.getId(), ModBlocks.POTTED_GLASS_ORCHID);
            pot.addPlant(ModBlocks.MOSS_VIOLET.getId(), ModBlocks.POTTED_MOSS_VIOLET);
            pot.addPlant(ModBlocks.INK_BLOSSOM.getId(), ModBlocks.POTTED_INK_BLOSSOM);
            pot.addPlant(ModBlocks.DUSK_BELL.getId(), ModBlocks.POTTED_DUSK_BELL);
            pot.addPlant(ModBlocks.GHOST_FERN_BLOOM.getId(), ModBlocks.POTTED_GHOST_FERN_BLOOM);
        });
    }
}