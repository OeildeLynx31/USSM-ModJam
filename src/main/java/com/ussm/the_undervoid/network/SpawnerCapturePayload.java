package com.ussm.the_undervoid.network;

import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = UnderVoidMod.MODID)
public record SpawnerCapturePayload() implements CustomPacketPayload {
    public static final SpawnerCapturePayload INSTANCE = new SpawnerCapturePayload();
    private static final Type<SpawnerCapturePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(UnderVoidMod.MODID, "spawner_capture"));
    private static final StreamCodec<RegistryFriendlyByteBuf, SpawnerCapturePayload> CODEC =
            StreamCodec.unit(INSTANCE);

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (payload, context) -> ClientAnimation.play());
    }

    @Override
    public Type<SpawnerCapturePayload> type() {
        return TYPE;
    }

    private static class ClientAnimation {
        static void play() {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.particleEngine.createTrackingEmitter(minecraft.player, ParticleTypes.TOTEM_OF_UNDYING, 30);
                minecraft.gameRenderer.displayItemActivation(new ItemStack(ModItems.SPAWNER_RECEPTACLE.get()));
            }
        }
    }
}