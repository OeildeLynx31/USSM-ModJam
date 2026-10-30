package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.particle.ModParticles;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HEDGEHOG.get(), HedgehogRenderer::new);
        event.registerEntityRenderer(ModEntities.MIMIC.get(), MimicRenderer::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(HazmatMaskLayer.MODEL);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new HazmatMaskLayer<>(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, SanityHudLayer.ID, new SanityHudLayer());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, HallucinationVignette.ID, new HallucinationVignette());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, GasMaskOverlay.ID, new GasMaskOverlay());
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.MIASMA.get(), MiasmaParticle.Provider::new);
    }

    @SubscribeEvent
    // /blue ouvre le menu de config du mod
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("blue").executes(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            ModContainer container = ModList.get().getModContainerById(ExampleMod.MODID).orElseThrow();
            //TODO tester l'onglet serveur sur un vrai serveur
            minecraft.tell(() -> minecraft.setScreen(new ConfigurationScreen(container, null)));
            return 1;
        }));
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ModDimensions.UNDERVOID_EFFECTS, new UndervoidEffects());
    }
}
