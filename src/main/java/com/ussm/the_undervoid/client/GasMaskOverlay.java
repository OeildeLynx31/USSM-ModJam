package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.item.custom.HazmatArmorItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class GasMaskOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "gas_mask");
    private static final ResourceLocation MASK_LOCATION = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "textures/gui/gas_mask_overlay.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        if (mc.options.getCameraType().isFirstPerson() && !mc.player.isScoping()) {
            if (HazmatArmorItem.isWearingMask(mc.player)) {
                //pareil que la vignette mais sans alpha
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                //guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.8F);
                guiGraphics.blit(MASK_LOCATION, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            }
        }
    }
}