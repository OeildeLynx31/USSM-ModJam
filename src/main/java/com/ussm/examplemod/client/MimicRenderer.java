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
import net.minecraft.world.InteractionHand;

import java.util.UUID;

public class MimicRenderer extends EntityRenderer<Mimic> { // pas mal de trucs repris de PlayerRenderer
    private static final PlayerSkin DEFAULT_SKIN =
            new PlayerSkin(DefaultPlayerSkin.getDefaultTexture(), null, null, null, PlayerSkin.Model.SLIM, true);

    private final BodyRenderer wideRenderer;
    private final BodyRenderer slimRenderer;

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.wideRenderer = new BodyRenderer(context, false);
        this.slimRenderer = new BodyRenderer(context, true);
    }

    @Override
    public ResourceLocation getTextureLocation(Mimic entity) {
        return getSkin(entity).texture();
    }

    @Override
    public void render(Mimic entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (getSkin(entity).model() == PlayerSkin.Model.SLIM) {
            this.slimRenderer.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            this.wideRenderer.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public Vec3 getRenderOffset(Mimic entity, float partialTicks) {
        if(entity.isCrouching()) {
            return new Vec3(0.0, -0.125, 0.0);
        }
        return super.getRenderOffset(entity, partialTicks);
    }

    private static PlayerSkin getSkin(Mimic mimic) {
        UUID uuid = mimic.getCopiedId();
        if (uuid == null) {
            return DEFAULT_SKIN;
        }
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        PlayerInfo info = connection == null ? null : connection.getPlayerInfo(uuid);
        if (info != null) {
            return info.getSkin();
        }
        // joueur deco : son skin depuis les textures que le serveur a gardé (skin par defaut tant que c'est pas chargé)
        return Minecraft.getInstance().getSkinManager().getInsecureSkin(mimic.getGameProfile());
    }

    private static class BodyRenderer extends LivingEntityRenderer<Mimic, PlayerModel<Mimic>> {
        public BodyRenderer(EntityRendererProvider.Context context, boolean slim) {
            super(context, new PlayerModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5f);
            this.addLayer(new HumanoidArmorLayer<>(this,
                    new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
                    new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
                    context.getModelManager()));
            this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
            this.addLayer(new HazmatMaskLayer<>(this));
            this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
            this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
            // TODO la cape du joueur copié s'affiche pas, CapeLayer marche qu'avec un AbstractClientPlayer
            //this.addLayer(new CapeLayer(this));
        }

        @Override
        public ResourceLocation getTextureLocation(Mimic entity) {
            return getSkin(entity).texture();
        }

        @Override
        public void render(Mimic entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            this.setModelProperties(entity);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        @Override
        protected void scale(Mimic entity, PoseStack poseStack, float partialTicks) {
            float f = 0.9375F;
            poseStack.scale(0.9375F, 0.9375F, 0.9375F);
        }

        private void setModelProperties(Mimic entity) {
            PlayerModel<Mimic> playermodel = this.getModel();
            playermodel.setAllVisible(true);
            playermodel.crouching = entity.isCrouching();

            HumanoidModel.ArmPose mainHandPose = getArmPose(entity, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose offHandPose = getArmPose(entity, InteractionHand.OFF_HAND);
            if (mainHandPose.isTwoHanded()) {
                offHandPose = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = mainHandPose;
                playermodel.leftArmPose = offHandPose;
            } else {
                playermodel.rightArmPose = offHandPose;
                playermodel.leftArmPose = mainHandPose;
            }
        }

        private static HumanoidModel.ArmPose getArmPose(Mimic mimic, InteractionHand hand) {
            ItemStack itemstack = mimic.getItemInHand(hand);
            if (itemstack.isEmpty()) {
                return HumanoidModel.ArmPose.EMPTY;
            } else {
                if (!mimic.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                    return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
                return HumanoidModel.ArmPose.ITEM;
            }
        }
    }
}
