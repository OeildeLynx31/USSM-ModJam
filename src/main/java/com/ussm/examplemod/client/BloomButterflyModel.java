package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.BloomButterfly;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class BloomButterflyModel extends HierarchicalModel<BloomButterfly> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "bloom_butterfly"), "main");
    private final ModelPart root;

    public BloomButterflyModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 24.0F, -3.0F));

        PartDefinition bone2 = bone3.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 9).addBox(-6.0F, 0.0F, -2.0F, 6.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -0.5F, 0.5F));

        PartDefinition bone = bone3.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -2.0F, 6.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.5F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(BloomButterfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.flyAnimationState, CritterAnimations.BUTTERFLY_FLAP, ageInTicks);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
