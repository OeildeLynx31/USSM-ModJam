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

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.SPAWNER_RECEPTACLE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SPAWNER_RECEPTACLE.get());
                output.accept(ModBlocks.DARK_DIRT.asItem());
                output.accept(ModBlocks.VOID_SAPLING.asItem());
                output.accept(ModBlocks.VOID_LOG.asItem());
                output.accept(ModBlocks.VOID_WOOD.asItem());
                output.accept(ModBlocks.VOID_PLANKS.asItem());
                output.accept(ModBlocks.VOID_LEAVES.asItem());
                output.accept(ModBlocks.BLOOM_FLOWER.asItem());
                output.accept(ModItems.HAZMAT_FABRIC.get());
                output.accept(ModItems.HAZMAT_MASK.get());
                output.accept(ModItems.HAZMAT_SUIT.get());
                output.accept(ModItems.HAZMAT_LEGGINGS.get());
                output.accept(ModItems.HAZMAT_BOOTS.get());
                output.accept(ModItems.BLADE_FRAGMENT.get());
                output.accept(ModItems.KATANA.get());
                output.accept(ModItems.HEDGEHOG_SPAWN_EGG.get());
                output.accept(ModItems.MIMIC_SPAWN_EGG.get());
            }).build());

    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModParticles.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BLOOM_FLOWER.getId(), ModBlocks.POTTED_BLOOM_FLOWER));
    }
}
