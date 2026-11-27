package com.ussm.the_undervoid.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.sanity.Sanity;
import com.ussm.the_undervoid.worldgen.dimension.ModDimensions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import com.ussm.the_undervoid.attachment.ModAttachments;
import com.ussm.the_undervoid.attachment.ModAttachments.SanityIllusion;

public class SanityHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "sanity");

    private static final ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "hud/sanity_full");
    private static final ResourceLocation HALF = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "hud/sanity_half");
    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "hud/sanity_empty");

    private final RandomSource random = RandomSource.create();
    private LocalPlayer trackedPlayer;
    private int displayedDose;
    private double riseStarted;
    private double revealStarted = Double.NaN;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) {
            return;
        }

        float actualSanity = Sanity.get(player);
        SanityIllusion illusion = player.getData(ModAttachments.SANITY_ILLUSION);
        double now = player.tickCount + (double) deltaTracker.getGameTimeDeltaPartialTick(false);
        float sanity = displayedSanity(player, illusion, now, actualSanity);
        if (actualSanity >= Sanity.MAX && sanity >= Sanity.MAX && !illusion.active() && !player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;
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

    private float displayedSanity(LocalPlayer player, SanityIllusion illusion, double now, float actualSanity) {
        if (trackedPlayer != player) {
            trackedPlayer = player;
            displayedDose = 0;
        }
        if (illusion.dose() == 0) return actualSanity;

        if (displayedDose != illusion.dose()) {
            displayedDose = illusion.dose();
            riseStarted = now;
            revealStarted = Double.NaN;
        }

        float riseProgress = Mth.clamp((float) ((now - riseStarted) / 6), 0f, 1f);
        if (riseProgress < 1f) return Mth.lerp(riseProgress, illusion.start(), illusion.target());
        if (illusion.active()) return illusion.target();

        if (Double.isNaN(revealStarted)) revealStarted = now;
        float revealProgress = Mth.clamp((float) ((now - revealStarted) / 8), 0f, 1f);
        return revealProgress < 1f ? Mth.lerp(revealProgress, illusion.target(), actualSanity) : actualSanity;
    }
}