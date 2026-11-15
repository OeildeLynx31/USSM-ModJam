package com.ussm.the_undervoid.entity;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.client.EneraRenderer;
import com.ussm.the_undervoid.client.model.EneraModel;
import com.ussm.the_undervoid.entity.ModEntities;
import com.ussm.the_undervoid.client.HerobrineRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import com.ussm.the_undervoid.client.EneraRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = UnderVoidMod.MODID,
        value = Dist.CLIENT
)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.VOID_BALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.HEROBRINE.get(), HerobrineRenderer::new);
        event.registerEntityRenderer(ModEntities.SMOKE_BOSS.get(), EneraRenderer::new);

    }
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EneraModel.LAYER_LOCATION, EneraModel::createBodyLayer);
    }
}