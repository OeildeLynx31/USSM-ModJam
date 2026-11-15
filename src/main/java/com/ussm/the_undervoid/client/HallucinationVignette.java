package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.the_undervoid.UnderVoidMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class HallucinationVignette implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "hallucination_vignette");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "textures/gui/hallucination_vignette.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float alpha = Hallucinations.strength() * 0.45f;
        if (alpha < 0.01f) return;

        // copie du truc de la citrouille dans Gui
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(TEXTURE, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
