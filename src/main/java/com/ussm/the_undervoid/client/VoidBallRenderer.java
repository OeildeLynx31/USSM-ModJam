package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ussm.the_undervoid.item.custom.VoidBallProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

// comme le ThrownItemRenderer mais la boule tourne, pulse, grossit quand elle s'ouvre et retrecit avant d'imploser
public class VoidBallRenderer extends EntityRenderer<VoidBallProjectile> {
    private final ItemRenderer itemRenderer;

    public VoidBallRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(VoidBallProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // pareil que vanilla : pas dans la tete du joueur qui vient de la lancer
        if (entity.tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25) {
            return;
        }

        float age = entity.tickCount + partialTicks;
        float scale;
        float spin;
        if (entity.isActive()) {
            float t = entity.getDuration() + partialTicks;
            float open = Mth.clamp(t / 8f, 0f, 1f); // s'ouvre en ~0.4s
            float close = Mth.clamp((VoidBallProjectile.DURATION - t) / 10f, 0f, 1f); // se referme sur la fin
            scale = (1f + 1.4f * open) * close * (1f + 0.08f * Mth.sin(t * 0.6f));
            spin = t * 14f;
        } else {
            scale = 1.1f + 0.1f * Mth.sin(age * 0.8f);
            spin = age * 6f;
        }
        if (scale <= 0.01f) return;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(spin));
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidBallProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
