package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.client.model.EneraModel;
import com.ussm.examplemod.entity.custom.SmokeBossEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Fait briller tout ce qui est blanc dans la texture (texture enera_glow.png = uniquement les pixels blancs). */
public class EneraGlowLayer extends RenderLayer<SmokeBossEntity, EneraModel> {
    private static final ResourceLocation GLOW =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/enera_glow.png");
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final int PASSES = 3; // monte pour une lumière encore plus fou malade qui pete le feu

    public EneraGlowLayer(RenderLayerParent<SmokeBossEntity, EneraModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SmokeBossEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(GLOW));
        for (int i = 0; i < PASSES; i++) {
            this.getParentModel().renderToBuffer(poseStack, consumer, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
