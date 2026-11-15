package com.ussm.the_undervoid.util;

import com.ussm.the_undervoid.UnderVoidMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModAdvancements {
    public static final ResourceLocation OUCH_MY_HEAD = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "undervoid/ouch_my_head");
    public static final ResourceLocation TWICE_AS_SHARP = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "undervoid/twice_as_sharp");
    public static final ResourceLocation PRICKLY_FRIEND = ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "undervoid/prickly_friend");

    public static void award(ServerPlayer player, ResourceLocation id) {
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if(advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            for(String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }
}