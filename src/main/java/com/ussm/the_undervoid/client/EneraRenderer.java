package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.client.model.EneraModel;
import com.ussm.the_undervoid.entity.custom.SmokeBossEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EneraRenderer extends MobRenderer<SmokeBossEntity, EneraModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "textures/entity/enera.png");

    public EneraRenderer(EntityRendererProvider.Context context) {
        super(context, new EneraModel(context.bakeLayer(EneraModel.LAYER_LOCATION)), 0.0f);
        this.addLayer(new EneraGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SmokeBossEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(SmokeBossEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
    }
}
