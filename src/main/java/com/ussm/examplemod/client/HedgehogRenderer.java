package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Hedgehog;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HedgehogRenderer extends GeoEntityRenderer<Hedgehog> {
    public HedgehogRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hedgehog")));
        this.shadowRadius = 0.3f;
    }

    //geckolib reduit pas les bebes tout seul donc on le fait a la main (l'ombre aussi)
    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, Hedgehog animatable, BakedGeoModel model,
                                    boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? 0.5f : 1f;
        super.scaleModelForRender(widthScale * scale, heightScale * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    protected float getShadowRadius(Hedgehog entity) {
        return super.getShadowRadius(entity) * entity.getAgeScale();
    }
}
