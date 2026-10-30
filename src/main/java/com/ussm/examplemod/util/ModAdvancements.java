package com.ussm.examplemod.util;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModAdvancements {
    public static final ResourceLocation OUCH_MY_HEAD = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "undervoid/ouch_my_head");
    public static final ResourceLocation TWICE_AS_SHARP = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "undervoid/twice_as_sharp");
    public static final ResourceLocation PRICKLY_FRIEND = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "undervoid/prickly_friend");

    public static void award(ServerPlayer player, ResourceLocation id) {
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) return;

        //System.out.println("award " + id);
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }
}
