package com.ussm.examplemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.item.custom.HazmatArmorItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class GasMaskOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "gas_mask");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/gui/gas_mask_overlay.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }
        if (!minecraft.options.getCameraType().isFirstPerson() || player.isScoping()) {
            return;
        }

        if (HazmatArmorItem.isWearingMask(player)) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            //guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.8F);
            guiGraphics.blit(TEXTURE, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
}
