package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.CarnivorousPlant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CarnivorousPlantRenderer extends MobRenderer<CarnivorousPlant, CarnivorousPlantModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/carnivorous_plant.png");

    public CarnivorousPlantRenderer(EntityRendererProvider.Context context) {
        super(context, new CarnivorousPlantModel(context.bakeLayer(CarnivorousPlantModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CarnivorousPlant entity) {
        return TEXTURE;
    }
}
