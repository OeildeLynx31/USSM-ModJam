package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.GloomBird;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GloomBirdRenderer extends MobRenderer<GloomBird, GloomBirdModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/gloom_bird.png");

    public GloomBirdRenderer(EntityRendererProvider.Context context) {
        super(context, new GloomBirdModel(context.bakeLayer(GloomBirdModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(GloomBird entity) {
        return TEXTURE;
    }
}