package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.block.custom.SanityTotemBlock;
import com.ussm.the_undervoid.block.entity.SanityTotemBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class SanityTotemRenderer implements BlockEntityRenderer<SanityTotemBlockEntity> {
    private static final int FRAMES = 13;
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[FRAMES];

    static {
        for (int i = 0; i < FRAMES; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, String.format("textures/entity/sanity_totem/frame_%02d.png", i));
        }
    }

    private final SanityTotemModel model;

    public SanityTotemRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SanityTotemModel(context.bakeLayer(SanityTotemModel.LAYER_LOCATION));
    }

    // 12 etapes : 4 par tete, image 0 = tout éteint, image 12 = les 3 tetes allumées
    public static ResourceLocation texture(float charge) {
        return TEXTURES[Math.min(FRAMES - 1, (int) (charge * (FRAMES - 1)))];
    }

    public static void draw(SanityTotemModel model, PoseStack poseStack, MultiBufferSource buffers, float charge, int light, int overlay) {
        model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(texture(charge))), light, overlay, -1);
    }

    @Override
    public void render(SanityTotemBlockEntity totem, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (totem.getLevel() == null) return;
        Direction facing = totem.getBlockState().getValue(SanityTotemBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - facing.toYRot()));
        poseStack.scale(-1f, -1f, 1f);
        draw(model, poseStack, buffers, totem.charge(totem.getLevel().getGameTime() + partialTick), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    // la statue depasse du bloc, sinon elle disparait quand on regarde plus le socle
    @Override
    public AABB getRenderBoundingBox(SanityTotemBlockEntity totem) {
        BlockPos pos = totem.getBlockPos();
        return new AABB(pos).inflate(1, 0, 1).expandTowards(0, 4, 0);
    }
}
