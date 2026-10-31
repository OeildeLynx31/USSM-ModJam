package com.ussm.examplemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SanityHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sanity");

    private static final ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hud/sanity_full");
    private static final ResourceLocation HALF = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hud/sanity_half");
    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hud/sanity_empty");

    private final RandomSource random = RandomSource.create();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) {
            return;
        }

        float sanity = Sanity.get(player);
        //guiGraphics.drawString(mc.font, "sanity " + sanity, 5, 5, 0xFFFFFF);
        if (sanity >= Sanity.MAX && !player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

        int x = guiGraphics.guiWidth() / 2 + 91;
        int y = guiGraphics.guiHeight() - mc.gui.rightHeight;
        mc.gui.rightHeight += 10;

        int halves = Mth.ceil(sanity / 5f);
        random.setSeed(player.tickCount * 312871L);
        RenderSystem.enableBlend();
        for (int i = 0; i < 10; i++) {
            int k = y;
            if (sanity <= Sanity.TROUBLED) {
                k += random.nextInt(3) - 1;
            }

            if (i * 2 + 1 < halves) {
                guiGraphics.blitSprite(FULL, x - i * 8 - 9, k, 9, 9);
            } else if (i * 2 + 1 == halves) {
                guiGraphics.blitSprite(HALF, x - i * 8 - 9, k, 9, 9);
            } else {
                guiGraphics.blitSprite(EMPTY, x - i * 8 - 9, k, 9, 9);
            }
        }
        RenderSystem.disableBlend();
    }
}