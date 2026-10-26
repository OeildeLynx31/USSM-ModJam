package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.particle.ModParticles;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HEDGEHOG.get(), HedgehogRenderer::new);
        event.registerEntityRenderer(ModEntities.MIMIC.get(), MimicRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, SanityHudLayer.ID, new SanityHudLayer());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, HallucinationVignette.ID, new HallucinationVignette());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, GasMaskOverlay.ID, new GasMaskOverlay());
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.MIASMA.get(), MiasmaParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("blue").executes(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            ModList.get().getModContainerById(ExampleMod.MODID)
                    .ifPresent(container -> minecraft.tell(() -> minecraft.setScreen(new ConfigurationScreen(container, null))));
            return 1;
        }));
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ModDimensions.UNDERVOID_EFFECTS, new UndervoidEffects());
    }
}
