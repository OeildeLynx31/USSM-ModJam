package com.ussm.examplemod.util;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModAdvancements {
    public static final ResourceLocation OUCH_MY_HEAD = undervoid("ouch_my_head");
    public static final ResourceLocation TWICE_AS_SHARP = undervoid("twice_as_sharp");
    public static final ResourceLocation PRICKLY_FRIEND = undervoid("prickly_friend");

    public static void award(ServerPlayer player, ResourceLocation id) {
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) {
            return;
        }
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }

    private static ResourceLocation undervoid(String name) {
        return ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "undervoid/" + name);
    }
}
