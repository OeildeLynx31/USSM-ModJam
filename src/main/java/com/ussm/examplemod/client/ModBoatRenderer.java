package com.ussm.examplemod.client;

import com.mojang.datafixers.util.Pair;
import com.ussm.examplemod.ExampleMod;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class ModBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation VOID_BOAT_LAYER = createLocation("boat/void", "main");
    public static final ModelLayerLocation VOID_CHEST_BOAT_LAYER = createLocation("chest_boat/void", "main");

    //pas besoin de la map du tuto, y a que le void
    private final Pair<ResourceLocation, ListModel<Boat>> boatResources;

    public ModBoatRenderer(EntityRendererProvider.Context pContext, boolean pChestBoat) {
        super(pContext, pChestBoat);
        this.boatResources = Pair.of(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, getTextureLocation(pChestBoat)), this.createBoatModel(pContext, pChestBoat));
    }

    private static String getTextureLocation(boolean pChestBoat) {
        return pChestBoat ? "textures/entity/chest_boat/void.png" : "textures/entity/boat/void.png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = pChestBoat ? VOID_CHEST_BOAT_LAYER : VOID_BOAT_LAYER;
        ModelPart modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    private static ModelLayerLocation createLocation(String pPath, String pModel) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, pPath), pModel);
    }

    // neoforge dit d'override ca pour changer le modele/texture
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.boatResources;
    }
}
