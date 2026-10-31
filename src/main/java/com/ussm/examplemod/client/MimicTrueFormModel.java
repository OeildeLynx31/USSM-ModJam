package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.joml.Vector3f;

// vraie forme du mimic, c'est le rtpentity.bbmodel du modeleur
public class MimicTrueFormModel extends HierarchicalModel<Mimic> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "mimic_true_form"), "main");
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f(); // copié de HierarchicalModel

    private static final long ATTACK_OFFSET = 450L; // 0.45s, on saute le debut
    private static final float ATTACK_SPEED = 1.5F;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart bone; // la machoire en fait, merci blockbench pour les noms

    public MimicTrueFormModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("root").getChild("body").getChild("head");
        this.bone = this.head.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(-5.0F, 10.0F, -4.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(50, 0).addBox(1.0F, -2.0F, -1.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(9.0F, 0.0F, 0.0F));
        PartDefinition cube_r1 = left_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -2.0F, -21.0F, 3.0F, 4.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_r2 = left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -26.0F, 3.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 0.0F));
        PartDefinition cube_r3 = right_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -2.0F, -26.0F, 3.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_r4 = right_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -21.0F, 3.0F, 4.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -10.0F, -5.0F, 7.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -2.0F, -1.0F));
        PartDefinition bone = head.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(18, 52).addBox(-2.0F, -4.0F, -4.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(50, 54).addBox(-2.0F, -6.0F, -4.0F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -4.0F));
        PartDefinition bone2 = head.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(34, 52).addBox(-3.0F, 0.0F, -3.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(50, 56).addBox(-3.0F, 3.0F, -3.0F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -9.0F, -5.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Mimic entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if(entity.getPose() == Pose.EMERGING) {
            //this.animate(entity.emergeAnimationState, MimicAnimations.EMERGE, ageInTicks, 1f); // trop lent
            float speed = MimicAnimations.EMERGE.lengthInSeconds() * 20f / Mimic.EMERGE_TICKS;
            this.animate(entity.emergeAnimationState, MimicAnimations.EMERGE, ageInTicks, speed);
            return;
        }

        this.head.yRot += netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot += headPitch * Mth.DEG_TO_RAD * 0.5F;

        //this.animate(entity.attackAnimationState, MimicAnimations.ATTACK, ageInTicks, ATTACK_SPEED);
        float attack = 0f;
        long attackTime = 0L;
        entity.attackAnimationState.updateTime(ageInTicks, ATTACK_SPEED);
        if(entity.attackAnimationState.isStarted()) {
            attackTime = entity.attackAnimationState.getAccumulatedTime() + ATTACK_OFFSET;
            if(attackTime > 1000L) {
                attackTime = 1000L;
            }
            attack = Math.min(1f, (attackTime - ATTACK_OFFSET) / 80f) * Math.min(1f, (1000L - attackTime) / 150f); // a l'arache mais ca passe
            //System.out.println("attack " + attack + " / " + attackTime);
        }

        float run = entity.getRunTime(ageInTicks - entity.tickCount);
        KeyframeAnimations.animate(this, MimicAnimations.RUN_START, (long) (run * 1000F), 1F - attack, ANIMATION_VECTOR_CACHE);
        float sway = Math.min(limbSwingAmount * 2.5F, 1F) * Math.min(run / 0.7F, 1F) * (1F - attack);
        if(sway > 0) {
            KeyframeAnimations.animate(this, MimicAnimations.RUN, (long) (limbSwing * 50F), sway, ANIMATION_VECTOR_CACHE);
        }
        if(attack > 0) {
            KeyframeAnimations.animate(this, MimicAnimations.ATTACK, attackTime, attack, ANIMATION_VECTOR_CACHE);
        }

        this.bone.xRot += Mth.sin(ageInTicks * 0.9F) * 0.05F; //claque des dents
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
