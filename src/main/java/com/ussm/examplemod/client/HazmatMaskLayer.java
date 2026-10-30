package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// le masque en 3D sur la tete (le modele blockbench), la texture plate de l'armure a plus de tete
public class HazmatMaskLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "item/hazmat_mask_worn"));

    public HazmatMaskLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!stack.is(ModItems.HAZMAT_MASK.get())) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getModel(MODEL);

        poseStack.pushPose();
        getParentModel().getHead().translateAndRotate(poseStack);
        // meme placement que CustomHeadLayer (citrouille sur la tete)
        poseStack.translate(0.0F, -0.25F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.625F, -0.625F, -0.625F);
        minecraft.getItemRenderer().render(stack, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }
}
