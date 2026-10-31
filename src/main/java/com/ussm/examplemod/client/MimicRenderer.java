package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MimicRenderer extends EntityRenderer<Mimic> {
    private static final PlayerSkin DEFAULT_SKIN = new PlayerSkin(DefaultPlayerSkin.getDefaultTexture(), null, null, null, PlayerSkin.Model.SLIM, true);
    private static final Vec3 CROUCH_OFFSET = new Vec3(0.0, -0.125, 0.0);

    private final Body wide;
    private final Body slim;

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5f;
        wide = new Body(context, false);
        slim = new Body(context, true);
    }

    @Override
    public void render(Mimic mimic, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        PlayerSkin skin = skin(mimic);
        (skin.model() == PlayerSkin.Model.SLIM ? slim : wide).render(mimic, yaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(Mimic mimic) {
        return skin(mimic).texture();
    }

    @Override
    public Vec3 getRenderOffset(Mimic mimic, float partialTick) {
        return mimic.isCrouching() ? CROUCH_OFFSET : Vec3.ZERO;
    }

    private static PlayerSkin skin(Mimic mimic) {
        UUID id = mimic.getCopiedId();
        if (id == null) {
            return DEFAULT_SKIN;
        }
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        PlayerInfo info = connection == null ? null : connection.getPlayerInfo(id);
        return info != null ? info.getSkin() : DefaultPlayerSkin.get(id);
    }

    private static class Body extends LivingEntityRenderer<Mimic, PlayerModel<Mimic>> {
        Body(EntityRendererProvider.Context context, boolean slim) {
            super(context, new PlayerModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5f);
            addLayer(new HumanoidArmorLayer<>(this,
                    new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
                    new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
                    context.getModelManager()));
            addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
            addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
            addLayer(new ElytraLayer<>(this, context.getModelSet()));
        }

        @Override
        public void render(Mimic mimic, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
            model.setAllVisible(true);
            model.crouching = mimic.isCrouching();
            HumanoidModel.ArmPose main = armPose(mimic, mimic.getMainHandItem());
            HumanoidModel.ArmPose off = main.isTwoHanded() ? itemPose(mimic.getOffhandItem()) : armPose(mimic, mimic.getOffhandItem());
            boolean right = mimic.getMainArm() == HumanoidArm.RIGHT;
            model.rightArmPose = right ? main : off;
            model.leftArmPose = right ? off : main;
            super.render(mimic, yaw, partialTick, poseStack, buffer, light);
        }

        @Override
        public ResourceLocation getTextureLocation(Mimic mimic) {
            return skin(mimic).texture();
        }

        private static HumanoidModel.ArmPose armPose(Mimic mimic, ItemStack stack) {
            if (!mimic.swinging && stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            return itemPose(stack);
        }

        private static HumanoidModel.ArmPose itemPose(ItemStack stack) {
            return stack.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        @Override
        protected void scale(Mimic mimic, PoseStack poseStack, float partialTick) {
            poseStack.scale(0.9375f, 0.9375f, 0.9375f);
        }
    }
}
