package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.examplemod.entity.custom.SmokeBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SmokeBossRenderer extends HumanoidMobRenderer<SmokeBossEntity, HumanoidModel<SmokeBossEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public SmokeBossRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(SmokeBossEntity entity) {
        return TEXTURE;
    }
    @Nullable
    @Override
    protected RenderType getRenderType(SmokeBossEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return null;
    }
    @Override
    protected void scale(SmokeBossEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
    }
}