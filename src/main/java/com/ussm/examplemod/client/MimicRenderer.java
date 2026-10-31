package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MimicRenderer extends EntityRenderer<Mimic> { // pompé en grande partie sur PlayerRenderer
    private static final PlayerSkin DEFAULT_SKIN =
            new PlayerSkin(DefaultPlayerSkin.getDefaultTexture(), null, null, null, PlayerSkin.Model.SLIM, true);

    private final BodyRenderer steve;
    private final BodyRenderer alex; // team alex
    private final TrueFormRenderer trueFormRenderer;

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.steve = new BodyRenderer(context, false);
        this.alex = new BodyRenderer(context, true);
        this.trueFormRenderer = new TrueFormRenderer(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Mimic entity) {
        return entity.isRevealed() ? TrueFormRenderer.TEXTURE : getSkin(entity).texture();
    }

    @Override
    public void render(Mimic entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BodyRenderer body = this.steve;
        if (getSkin(entity).model() == PlayerSkin.Model.SLIM) {
            body = this.alex;
        }

        if (entity.isRevealed()) {
            this.trueFormRenderer.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

            // le faux joueur rentre dans le sol pdt que la bete sort
            float sink = entity.getEmergeTime(partialTicks) / 12f;
            if (sink < 1f) {
                poseStack.pushPose();
                poseStack.translate(0.0, -sink * 1.9f, 0.0);
                body.showName = false;
                body.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
                body.showName = true;
                poseStack.popPose();
            }
        } else {
            body.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public Vec3 getRenderOffset(Mimic entity, float partialTicks) {
        return entity.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entity, partialTicks);
    }

    private static PlayerSkin getSkin(Mimic mimic) {
        UUID uuid = mimic.getCopiedId();
        if (uuid == null) return DEFAULT_SKIN;

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.getPlayerInfo(uuid) != null) {
            return connection.getPlayerInfo(uuid).getSkin();
        }
        // joueur deco, on prend les textures que le serveur a gardé
        //return Minecraft.getInstance().getSkinManager().getOrLoad(mimic.getGameProfile()).getNow(DEFAULT_SKIN);
        return Minecraft.getInstance().getSkinManager().getInsecureSkin(mimic.getGameProfile());
    }

    private static class BodyRenderer extends LivingEntityRenderer<Mimic, PlayerModel<Mimic>> {
        boolean showName = true;

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
            // TODO cape
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
            poseStack.scale(f, f, f);
        }

        @Override
        protected boolean shouldShowName(Mimic entity) {
            return this.showName && super.shouldShowName(entity);
        }

        private void setModelProperties(Mimic entity) {
            PlayerModel<Mimic> playermodel = this.getModel();
            playermodel.setAllVisible(true);
            playermodel.crouching = entity.isCrouching();

            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(entity, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(entity, InteractionHand.OFF_HAND);
            if (humanoidmodel$armpose.isTwoHanded()) {
                humanoidmodel$armpose1 = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            } else {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
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

    private static class TrueFormRenderer extends LivingEntityRenderer<Mimic, MimicTrueFormModel> {
        private static final ResourceLocation TEXTURE =
                ResourceLocation.fromNamespaceAndPath("examplemod", "textures/entity/mimic_true_form.png");

        public TrueFormRenderer(EntityRendererProvider.Context context) {
            super(context, new MimicTrueFormModel(context.bakeLayer(MimicTrueFormModel.LAYER_LOCATION)), 0.6f);
        }

        @Override
        public ResourceLocation getTextureLocation(Mimic entity) {
            return TEXTURE;
        }

        @Override
        protected void scale(Mimic entity, PoseStack poseStack, float partialTicks) {
            poseStack.scale(1.1f, 1.1f, 1.1f); // a peu pres la taille du joueur
        }
    }
}
