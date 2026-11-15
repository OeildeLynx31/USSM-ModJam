package com.ussm.the_undervoid.client;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.attachment.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = UnderVoidMod.MODID, value = Dist.CLIENT)
public class DashFov {
    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if(player.hasData(ModAttachments.KATANA_DASH) && player.getData(ModAttachments.KATANA_DASH).isDashing()) {
            float fovModifier = Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0F, 1.15F);
            event.setNewFovModifier(event.getNewFovModifier() * fovModifier);
        }
    }
}
