package com.ussm.examplemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.examplemod.ExampleMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class HallucinationVignette implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hallucination_vignette");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/gui/hallucination_vignette.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float alpha = Hallucinations.strength() * 0.45f;
        if (alpha < 0.01f) {
            return;
        }
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        // copié du truc de la citrouille dans Gui
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1f, 1f, 1f, alpha);
        guiGraphics.blit(TEXTURE, 0, 0, -90, 0f, 0f, width, height, width, height);
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
