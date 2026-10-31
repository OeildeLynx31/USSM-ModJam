package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.client.HerobrineRenderer;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = ExampleMod.MODID,
        value = Dist.CLIENT
)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntities.VOID_BALL.get(),
                ThrownItemRenderer::new
        );
        event.registerEntityRenderer(ModEntities.HEROBRINE.get(), HerobrineRenderer::new);
    }
}