package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Cockroach;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CockroachRenderer extends MobRenderer<Cockroach, CockroachModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/cockroach.png");

    public CockroachRenderer(EntityRendererProvider.Context context) {
        super(context, new CockroachModel(context.bakeLayer(CockroachModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(Cockroach entity) {
        return TEXTURE;
    }
}
