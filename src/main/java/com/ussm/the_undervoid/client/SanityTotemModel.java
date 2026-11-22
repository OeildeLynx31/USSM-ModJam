package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ussm.the_undervoid.UnderVoidMod;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// le sanity_totem_model.bbmodel du modeleur
public class SanityTotemModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "sanity_totem"), "main");

    private final ModelPart root;

    public SanityTotemModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, 8.0F, -2.0F, 0.3491F, 0.0F, 0.0F));
        Waist.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(58, 36).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(66, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.48F, 0.0F, 0.0F));
        Body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(56, 68).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.3927F, 0.0F, 0.0F));
        Body.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(80, 76).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 1.0F, 0.0F, -0.6545F, 0.0F, 0.0F));
        PartDefinition Left_Arm = Body.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(48, 84).addBox(-1.0F, -7.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -1.0908F, 0.0F, 0.0F));
        Left_Arm.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(24, 80).addBox(-3.0F, -30.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 33.0F, 2.0F, 0.0481F, -0.4359F, -0.0203F));

        PartDefinition Waist2 = partdefinition.addOrReplaceChild("Waist2", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -4.0F, -2.0F, -0.0741F, -0.1451F, 0.413F));
        Waist2.addOrReplaceChild("Head2", CubeListBuilder.create().texOffs(58, 52).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition Body2 = Waist2.addOrReplaceChild("Body2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.48F, 0.0F, 0.0F));
        Body2.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(66, 16).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.1309F, 0.0F, 0.0F));
        Body2.addOrReplaceChild("Right_Arm2", CubeListBuilder.create().texOffs(64, 84).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.0F, -3.0F, 0.3103F, -0.3569F, -2.914F));
        Body2.addOrReplaceChild("Left_Arm2", CubeListBuilder.create().texOffs(90, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, -2.0F, 0.3064F, 0.2632F, 3.0622F));

        PartDefinition Waist3 = partdefinition.addOrReplaceChild("Waist3", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.5F, -13.0F, -2.0F, 0.0578F, -0.1522F, -0.4258F));
        Waist3.addOrReplaceChild("Head3", CubeListBuilder.create().texOffs(0, 60).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition Body3 = Waist3.addOrReplaceChild("Body3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.48F, 0.0F, 0.0F));
        Body3.addOrReplaceChild("Body_r3", CubeListBuilder.create().texOffs(32, 60).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 29.0F, -14.0F, -0.5138F, 0.0779F, 0.2502F));
        Body3.addOrReplaceChild("Body_r4", CubeListBuilder.create().texOffs(0, 76).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.1309F, 0.0F, 0.0F));
        Body3.addOrReplaceChild("Right_Arm3", CubeListBuilder.create().texOffs(90, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, -3.0F, -0.8281F, 0.1787F, -0.2356F));
        Body3.addOrReplaceChild("Left_Arm3", CubeListBuilder.create().texOffs(90, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, -2.0F, -0.149F, -0.3739F, -0.572F));

        partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-17.0F, -2.0F, 0.0F, 17.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 18).addBox(-17.0F, -14.0F, 0.0F, 17.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-16.0F, -12.0F, 1.0F, 15.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(80, 68).addBox(-14.0F, -11.0F, 0.0F, 11.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
