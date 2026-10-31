package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.CarnivorousPlant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class CarnivorousPlantModel extends HierarchicalModel<CarnivorousPlant> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "carnivorous_plant"), "main");
    private final ModelPart root;
    private final ModelPart tige;
    private final ModelPart head;

    public CarnivorousPlantModel(ModelPart root) {
        this.root = root;
        this.tige = root.getChild("base").getChild("tige");
        this.head = this.tige.getChild("tige2").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bone = base.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition bone9 = bone.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(44, 32).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 56).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 56).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 1.5708F, 0.0F));
        PartDefinition bone6 = bone.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(24, 41).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(52, 22).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(52, 25).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 2.3562F, 0.0F));
        PartDefinition bone7 = bone.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(44, 24).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 53).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 53).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 0.0F, 0.0F));
        PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(16, 41).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(52, 16).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(52, 19).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 0.7854F, 0.0F));
        PartDefinition bone4 = bone.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(44, 16).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(52, 40).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(52, 43).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 3.1416F, 0.0F));
        PartDefinition bone3 = bone.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(36, 37).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 41).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(40, 45).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -2.3562F, 0.0F));
        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(42, 8).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(52, 34).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(52, 37).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -1.5708F, 0.0F));
        PartDefinition bone8 = bone.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(52, 28).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(52, 31).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -0.7854F, 0.0F));
        PartDefinition tige = base.addOrReplaceChild("tige", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition cube_r1 = tige.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -20.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 7.0F, 2.6616F, 0.0F, 0.0F));
        PartDefinition cube_r2 = tige.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 34).addBox(-5.2956F, -1.2156F, -1.294F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 5.0F, 3.0643F, -0.0461F, -0.3454F));
        PartDefinition cube_r3 = tige.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 34).addBox(-1.0F, -20.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 7.0F, 2.7925F, 0.0F, 0.0F));
        PartDefinition cube_r4 = tige.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -20.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 7.0F, 2.7489F, 0.0F, 0.0F));
        PartDefinition tige2 = tige.addOrReplaceChild("tige2", CubeListBuilder.create(), PartPose.offset(0.0F, -17.0F, 7.0F));
        PartDefinition cube_r5 = tige2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(36, 22).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.829F, 0.0F, 0.0F));
        PartDefinition head = tige2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, -9.0F));
        PartDefinition machoire2 = head.addOrReplaceChild("machoire2", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -4.0F, -11.0F, 6.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition machoire1 = head.addOrReplaceChild("machoire1", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, -4.0F, -11.0F, 6.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone10 = head.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, 1.4835F, 0.0F, 0.0F));
        PartDefinition bone11 = bone10.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(44, 40).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(8, 56).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 56).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 1.5708F, 0.0F));
        PartDefinition bone12 = bone10.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(32, 45).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(56, 16).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 19).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 2.3562F, 0.0F));
        PartDefinition bone13 = bone10.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(40, 48).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(56, 22).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 25).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 0.0F, 0.0F));
        PartDefinition bone14 = bone10.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(56, 28).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 31).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 0.7854F, 0.0F));
        PartDefinition bone15 = bone10.addOrReplaceChild("bone15", CubeListBuilder.create().texOffs(16, 49).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 56).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 34).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, 3.1416F, 0.0F));
        PartDefinition bone16 = bone10.addOrReplaceChild("bone16", CubeListBuilder.create().texOffs(24, 49).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 56).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 37).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -2.3562F, 0.0F));
        PartDefinition bone17 = bone10.addOrReplaceChild("bone17", CubeListBuilder.create().texOffs(50, 0).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(40, 56).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(56, 40).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -1.5708F, 0.0F));
        PartDefinition bone18 = bone10.addOrReplaceChild("bone18", CubeListBuilder.create().texOffs(50, 8).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(56, 43).addBox(-0.5F, -10.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(44, 56).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.2217F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(CarnivorousPlant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        //sinon la tige est toute droite quand il fait rien
        if (!entity.isPlayingAnimation()) {
            this.applyStatic(CarnivorousPlantAnimations.IDLE);
        }
        this.animate(entity.watchAnimationState, CarnivorousPlantAnimations.WATCH_PLAYER, ageInTicks);
        this.animate(entity.attackAnimationState, CarnivorousPlantAnimations.ATTACK, ageInTicks);
        this.animate(entity.yawnAnimationState, CarnivorousPlantAnimations.YAWN, ageInTicks);

        this.applyHeadRotation(netHeadYaw, headPitch);
        this.tige.zRot += Mth.sin(ageInTicks * 0.08f) * 0.03f; // petit balancement, a voir si c'est pas trop
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        this.tige.yRot += headYaw * Mth.DEG_TO_RAD;
        this.head.xRot += headPitch * Mth.DEG_TO_RAD * 0.5f;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
