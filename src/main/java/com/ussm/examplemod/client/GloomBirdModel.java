package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.GloomBird;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GloomBirdModel extends HierarchicalModel<GloomBird> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "gloom_bird"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart right_wing;
    private final ModelPart left_wing;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart lower_beak;
    private final ModelPart beak;
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public GloomBirdModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.lower_beak = this.head.getChild("lower_beak");
        this.beak = this.head.getChild("beak");
        this.right_leg = this.body.getChild("right_leg");
        this.left_leg = this.body.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, -1.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 1.0F, -10.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.0F, -2.0F, -3.1416F, -1.5272F, 3.1416F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 14).addBox(2.6921F, 1.9343F, -9.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.0F, -2.0F, 1.7379F, -1.3055F, -1.7322F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-2.5F, -4.0F, 0.0F));

        PartDefinition cube_r3 = right_wing.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(14, 17).addBox(0.0F, 2.0F, -10.0F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.5F, -2.0F, -2.0F, -1.7698F, -1.3484F, 1.7651F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(2.5F, -4.0F, 0.0F));

        PartDefinition cube_r4 = left_wing.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, 2.0F, -10.0F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.5F, -2.0F, -2.0F, -1.7698F, 1.3484F, -1.7651F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -1.0F));

        PartDefinition cube_r5 = neck.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 8).addBox(-1.0F, -3.0F, -9.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -1.0F, -1.0F, 1.7698F, -1.3484F, -1.7651F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -1.0F));

        PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 8).addBox(-3.6527F, -4.9696F, -9.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 2.0F, 0.0F, 1.8171F, -1.3909F, -1.8133F));

        PartDefinition lower_beak = head.addOrReplaceChild("lower_beak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -4.0F));

        PartDefinition cube_r7 = lower_beak.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(20, 0).addBox(-5.6527F, -2.9696F, -9.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 2.0F, 4.0F, 1.8171F, -1.3909F, -1.8133F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -4.0F));

        PartDefinition cube_r8 = beak.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(20, 3).addBox(-5.6527F, -3.9696F, -9.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 2.0F, 4.0F, 1.8171F, -1.3909F, -1.8133F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition cube_r9 = right_leg.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(22, 8).addBox(-0.3091F, 0.0F, -0.8322F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, -3.1416F, 1.5272F, -3.1416F));

        PartDefinition cube_r10 = right_leg.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(20, 6).addBox(-4.0F, 9.0F, -9.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -5.0F, -4.0F, -3.1416F, 1.5272F, -3.1416F));

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition cube_r11 = left_leg.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(20, 21).addBox(4.0F, 5.0F, -8.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.0F, -4.0F, -3.1416F, -1.5272F, 3.1416F));

        PartDefinition cube_r12 = left_leg.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(12, 21).addBox(2.0F, 9.0F, -9.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.0F, -4.0F, -3.1416F, -1.5272F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(GloomBird entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.flyAnimationState, CritterAnimations.GLOOM_BIRD_FLY, ageInTicks);

        this.applyBodyRotation(Mth.wrapDegrees(entity.yBodyRot - entity.yBodyRotO), headPitch);
    }

    // il penche quand il tourne
    private void applyBodyRotation(float turn, float headPitch) {
        turn = Mth.clamp(turn * 0.04f, -0.35f, 0.35f);

        this.body.zRot -= turn;
        this.body.xRot += headPitch *  ((float)Math.PI / 180f) * 0.5f;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
