package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// le totem dans l'inventaire et en main
public class SanityTotemItemRenderer extends BlockEntityWithoutLevelRenderer {
    private SanityTotemModel model;

    public SanityTotemItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (model == null) {
            model = new SanityTotemModel(Minecraft.getInstance().getEntityModels().bakeLayer(SanityTotemModel.LAYER_LOCATION));
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.scale(-1f, -1f, 1f);
        float time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        SanityTotemRenderer.draw(model, poseStack, buffers, time, light, overlay);
        poseStack.popPose();
    }
}
