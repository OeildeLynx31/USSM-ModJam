package com.ussm.examplemod.client;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.item.custom.KatanaDash;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class DashFov {
    private static final float BOOST = 1.15f;

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        KatanaDash dash = event.getPlayer().getExistingDataOrNull(ModAttachments.KATANA_DASH);
        if (dash != null && dash.isDashing()) {
            float scale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
            event.setNewFovModifier(event.getNewFovModifier() * Mth.lerp(scale, 1f, BOOST));
        }
    }
}
