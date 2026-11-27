package com.ussm.the_undervoid.client.model;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.entity.custom.SmokeBossEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class EneraModel extends HierarchicalModel<SmokeBossEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "enera"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public EneraModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("Waist").getChild("Head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p0 = root.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p1 = p0.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F).texOffs(36, 10).addBox(-4.0F, -6.0F, -6.5F, 8.0F, 1.0F, 2.0F).texOffs(0, 0).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 4.0F, 9.0F), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        p1.addOrReplaceChild("Head_c2", CubeListBuilder.create().texOffs(52, 29).addBox(0.0F, 2.2F, 1.0F, 1.0F, 3.0F, 10.0F), PartPose.offsetAndRotation(5.0F, -5.0F, -5.0F, 0.0F, 0.0F, -0.2618F));
        p1.addOrReplaceChild("Head_c3", CubeListBuilder.create().texOffs(22, 46).addBox(-1.0F, -0.8F, 0.0F, 1.0F, 4.0F, 10.0F), PartPose.offsetAndRotation(5.0F, -5.0F, -5.0F, 0.0F, 0.0F, -0.2618F));
        p1.addOrReplaceChild("Head_c4", CubeListBuilder.create().texOffs(44, 46).addBox(-1.0F, 2.2F, 1.0F, 1.0F, 3.0F, 10.0F), PartPose.offsetAndRotation(-5.0F, -5.0F, -5.0F, 0.0F, 0.0F, 0.3054F));
        p1.addOrReplaceChild("Head_c5", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -0.8F, 0.0F, 1.0F, 4.0F, 10.0F), PartPose.offsetAndRotation(-5.0F, -5.0F, -5.0F, 0.0F, 0.0F, 0.3054F));
        p1.addOrReplaceChild("Head_c6", CubeListBuilder.create().texOffs(24, 80).addBox(-0.2F, -1.8F, -1.0F, 2.0F, 2F, 1.0F), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 0.0F, 0.0F, -0.7854F));
        p1.addOrReplaceChild("Head_c7", CubeListBuilder.create().texOffs(56, 5).addBox(-0.2F, -11.8F, 0.0F, 10.0F, 13.0F, 0.0F), PartPose.offsetAndRotation(5.2F, -7.2F, -5.2F, 0.0F, 0.0F, -1.5708F));
        p1.addOrReplaceChild("Head_c8", CubeListBuilder.create().texOffs(24, 76).addBox(-0.7F, -2.3F, -0.5F, 3.0F, 3F, 1.0F), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 0.0F, 0.0F, -0.7854F));
        p1.addOrReplaceChild("Head_c9", CubeListBuilder.create().texOffs(36, 0).addBox(-6.5F, 2.3F, 0.19F, 13.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.2618F, 0.0F, 0.0F));
        p1.addOrReplaceChild("Head_c10", CubeListBuilder.create().texOffs(36, 5).addBox(-4.5F, -0.5F, 0.2F, 9.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition p11 = p0.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(32, 13).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p12 = p11.addOrReplaceChild("armorBody", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 5.0F).texOffs(26, 29).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 5.0F, new CubeDeformation(0.4F)).texOffs(66, 0).addBox(-1.5F, 9.0F, -4.0F, 3.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        p12.addOrReplaceChild("armorBody_c13", CubeListBuilder.create().texOffs(56, 18).addBox(-4.0F, -1.0F, -0.4F, 8.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 11.0F, -3.0F, -0.1309F, 0.0F, 0.0F));
        PartDefinition p14 = p0.addOrReplaceChild("Right Arm", CubeListBuilder.create().texOffs(44, 59).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p15 = p14.addOrReplaceChild("bipedRightArm", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p16 = p15.addOrReplaceChild("armorRightArm", CubeListBuilder.create().texOffs(66, 42).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F).texOffs(74, 18).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p17 = p16.addOrReplaceChild("shoulder2", CubeListBuilder.create().texOffs(32, 75).addBox(-4.5F, -1.5F, -2.5F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
        PartDefinition p18 = p14.addOrReplaceChild("katana", CubeListBuilder.create().texOffs(34, 89).addBox(-3.0F, 2.0F, -1.0F, 3.0F, 1.0F, 2.0F).texOffs(74, 34).addBox(-2.5F, -3.0F, -0.5F, 2.0F, 5.0F, 1.0F).texOffs(0, 90).addBox(-3.0F, -5.0F, -1.0F, 3.0F, 1.0F, 2.0F).texOffs(56, 24).addBox(-4.0F, -4.0F, -2.0F, 5.0F, 1.0F, 4.0F).texOffs(18, 76).addBox(-3.0F, -29.0F, 0.0F, 3.0F, 25.0F, 0.0F), PartPose.offsetAndRotation(-2.0F, 7.0F, 1.0F, 1.5708F, 0.0F, -1.5708F));
        PartDefinition p19 = p0.addOrReplaceChild("Left Arm", CubeListBuilder.create().texOffs(0, 60).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p20 = p19.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p21 = p20.addOrReplaceChild("armorLeftArm", CubeListBuilder.create().texOffs(50, 75).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F).texOffs(66, 75).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p22 = p21.addOrReplaceChild("shoulder", CubeListBuilder.create().texOffs(0, 76).addBox(0.5F, -1.5F, -2.5F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));
        PartDefinition p23 = root.addOrReplaceChild("Right Leg", CubeListBuilder.create().texOffs(16, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p24 = p23.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p25 = p24.addOrReplaceChild("armorRightLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        p25.addOrReplaceChild("armorRightLeg_c26", CubeListBuilder.create().texOffs(24, 89).addBox(-3.2F, -1.0F, -1.2F, 3.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(2.2F, 0.0F, -2.0F, -0.1745F, 0.0F, 0.0F));
        p25.addOrReplaceChild("armorRightLeg_c27", CubeListBuilder.create().texOffs(82, 47).addBox(-4.0F, 0.0F, -0.5F, 4.0F, 9.0F, 2.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, -1.0F, 1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition p28 = p25.addOrReplaceChild("leg_armor", CubeListBuilder.create().texOffs(82, 74).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 8.0F, 5.0F), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        PartDefinition p29 = p24.addOrReplaceChild("armorRightBoot", CubeListBuilder.create().texOffs(32, 71).addBox(-2.0F, 10.0F, -3.4F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.21F)).texOffs(76, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p30 = root.addOrReplaceChild("Left Leg", CubeListBuilder.create().texOffs(60, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p31 = p30.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition p32 = p31.addOrReplaceChild("armorLeftLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        p32.addOrReplaceChild("armorLeftLeg_c33", CubeListBuilder.create().texOffs(32, 60).addBox(0.0F, 0.0F, -0.5F, 4.0F, 9.0F, 2.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 1.0F, 0.1745F, 0.0F, 0.0F));
        p32.addOrReplaceChild("armorLeftLeg_c34", CubeListBuilder.create().texOffs(82, 87).addBox(-0.2F, -1.0F, -1.2F, 3.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-1.8F, 0.0F, -2.0F, -0.1745F, 0.0F, 0.0F));
        PartDefinition p35 = p32.addOrReplaceChild("leg_armor3", CubeListBuilder.create().texOffs(82, 34).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 8.0F, 5.0F), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition p36 = p31.addOrReplaceChild("armorLeftBoot", CubeListBuilder.create().texOffs(52, 42).addBox(-2.0F, 10.0F, -3.4F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.21F)).texOffs(76, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.21F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(SmokeBossEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.isSpawning()) {
            this.animate(entity.spawnState, EneraSpawnAnimation.SPAWN, ageInTicks, 1.0F);
            return;
        }
        // Marche
        this.animateWalk(EneraAnimations.animationmarcheboss, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        // Attak
        this.animate(entity.attack1State, EneraAnimations.animationattack1, ageInTicks, 1.0F);
        this.animate(entity.attack2State, EneraAnimations.animationattack2, ageInTicks, 1.0F);
        this.animate(entity.attack3State, EneraAnimations.animationattack3, ageInTicks, 1.0F);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
