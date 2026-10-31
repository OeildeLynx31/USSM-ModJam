package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Cockroach;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class CockroachModel extends HierarchicalModel<Cockroach> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "cockroach"), "main");
    private final ModelPart root;

    public CockroachModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.3F, -3.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 0).addBox(-1.0F, -1.8F, -5.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition right_legs = body.addOrReplaceChild("right_legs", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -0.3F, 0.0F, 0.0F, 0.0F, -0.2182F));

        PartDefinition left_legs = body.addOrReplaceChild("left_legs", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.3F, 0.0F, 0.0F, 0.0F, 0.2182F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Cockroach entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        //this.animateWalk(CritterAnimations.COCKROACH_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animateWalk(CritterAnimations.COCKROACH_WALK, limbSwing, limbSwingAmount, 4f, 4f);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}