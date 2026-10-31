package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.particle.ModParticles;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
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

//bus = MOD est deprecated maintenant, ca marche sans
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HEDGEHOG.get(), HedgehogRenderer::new);
        event.registerEntityRenderer(ModEntities.MIMIC.get(), MimicRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_BOAT.get(), context -> new ModBoatRenderer(context, false));
        event.registerEntityRenderer(ModEntities.VOID_CHEST_BOAT.get(), context -> new ModBoatRenderer(context, true));
        event.registerEntityRenderer(ModEntities.COCKROACH.get(), CockroachRenderer::new);
        event.registerEntityRenderer(ModEntities.GLOOM_BIRD.get(), GloomBirdRenderer::new);
        event.registerEntityRenderer(ModEntities.BLOOM_BUTTERFLY.get(), BloomButterflyRenderer::new);
        event.registerEntityRenderer(ModEntities.FLOATING_LIGHT.get(), FloatingLightRenderer::new);
        event.registerEntityRenderer(ModEntities.CARNIVOROUS_PLANT.get(), CarnivorousPlantRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModBoatRenderer.VOID_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(ModBoatRenderer.VOID_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(CockroachModel.LAYER_LOCATION, CockroachModel::createBodyLayer);
        event.registerLayerDefinition(GloomBirdModel.LAYER_LOCATION, GloomBirdModel::createBodyLayer);
        event.registerLayerDefinition(BloomButterflyModel.LAYER_LOCATION, BloomButterflyModel::createBodyLayer);
        event.registerLayerDefinition(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);
        event.registerLayerDefinition(CarnivorousPlantModel.LAYER_LOCATION, CarnivorousPlantModel::createBodyLayer);
        event.registerLayerDefinition(MimicTrueFormModel.LAYER_LOCATION, MimicTrueFormModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(HazmatMaskLayer.MODEL);
    }
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        // steve et alex
        if (event.getSkin(PlayerSkin.Model.WIDE) instanceof PlayerRenderer wide) {
            wide.addLayer(new HazmatMaskLayer<>(wide));
        }
        if (event.getSkin(PlayerSkin.Model.SLIM) instanceof PlayerRenderer slim) {
            slim.addLayer(new HazmatMaskLayer<>(slim));
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
        event.registerSpriteSet(ModParticles.FALLING_VOID_LEAF.get(), FallingVoidLeafParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("blue").executes(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            ModContainer container = ModList.get().getModContainerById(ExampleMod.MODID).get();
            mc.tell(() -> mc.setScreen(new ConfigurationScreen(container, null))); // marche pas sans le tell, jsp pourquoi
            return 1;
        }));
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ModDimensions.UNDERVOID_EFFECTS, new UndervoidEffects());
    }
}
