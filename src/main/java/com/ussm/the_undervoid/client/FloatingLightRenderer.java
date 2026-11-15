package com.ussm.the_undervoid.client;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.entity.custom.FloatingLight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class FloatingLightRenderer extends MobRenderer<FloatingLight, FloatingLightModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "textures/entity/floating_light.png");

    public FloatingLightRenderer(EntityRendererProvider.Context context) {
        super(context, new FloatingLightModel(context.bakeLayer(FloatingLightModel.LAYER_LOCATION)), 0.15f);
    }

    @Override
    public ResourceLocation getTextureLocation(FloatingLight entity) {
        return TEXTURE;
    }
    // Toujours éclairée au max
    @Override
    protected int getBlockLightLevel(FloatingLight pEntity, BlockPos pPos) {
        return 15;
    }
}