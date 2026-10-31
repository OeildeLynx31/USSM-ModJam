package com.ussm.examplemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
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
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }
        float sanity = player.getData(ModAttachments.SANITY);
        if (sanity >= Sanity.MAX && !player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            return;
        }

        int right = guiGraphics.guiWidth() / 2 + 91;
        int top = guiGraphics.guiHeight() - minecraft.gui.rightHeight;
        minecraft.gui.rightHeight += 10;

        int halves = Mth.ceil(sanity / 5f);
        boolean shaking = sanity <= Sanity.TROUBLED;
        random.setSeed(player.tickCount * 312871L);
        RenderSystem.enableBlend();
        for (int i = 0; i < 10; i++) {
            int y = shaking ? top + random.nextInt(3) - 1 : top;
            ResourceLocation sprite = halves >= i * 2 + 2 ? FULL : halves == i * 2 + 1 ? HALF : EMPTY;
            guiGraphics.blitSprite(sprite, right - i * 8 - 9, y, 9, 9);
        }
        RenderSystem.disableBlend();
    }
}
