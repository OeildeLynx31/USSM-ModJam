package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.BloomButterfly;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BloomButterflyRenderer extends MobRenderer<BloomButterfly, BloomButterflyModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/bloom_butterfly.png");

    public BloomButterflyRenderer(EntityRendererProvider.Context context) {
        super(context, new BloomButterflyModel(context.bakeLayer(BloomButterflyModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(BloomButterfly entity) {
        return TEXTURE;
    }

}
