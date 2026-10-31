package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.mimic.voice.MimicVoice;
import com.ussm.examplemod.entity.mimic.voice.Moments;
import com.ussm.examplemod.entity.mimic.voice.VoiceRecorder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class MimicEvents {
    private static int tickCount;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        VoiceRecorder.setServer(event.getServer());
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        MovementRecorder.tick(server);
        VoiceRecorder.tick();

        tickCount++;
        if(tickCount % 20 == 0) {
            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                if(player.isAlive() && player.getHealth() < player.getMaxHealth() * 0.3f) {
                    Moments.mark(server, player.getUUID(), Moments.Kind.LOW_HEALTH);
                }
            }
        }
        // skin + stuff des joueurs co toutes les 30 sec
        if(tickCount % 600 == 0) {
            MimicMemory memory = MimicMemory.get(server);
            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                memory.update(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        MimicMemory.get(player.server).update(player);

        if(MimicVoice.isAvailable() && Config.MIMIC_RECORD_VOICE.get()) {
            player.sendSystemMessage(
                    Component.translatable("examplemod.mimic.voice_notice")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            );
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            MimicMemory.get(player.server).update(player);
            MovementRecorder.forget(player.getUUID());
            Moments.forget(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent.Post event) {
        if(event.getEntity() instanceof ServerPlayer player && event.getNewDamage() >= 2.0f) { // 1 coeur
            Moments.mark(player.server, player.getUUID(), Moments.Kind.HURT);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            Moments.mark(player.server, player.getUUID(), Moments.Kind.DEATH);
        }
        if(event.getSource().getEntity() instanceof ServerPlayer killer
                && (event.getEntity() instanceof Enemy || event.getEntity() instanceof Player)) {
            Moments.mark(killer.server, killer.getUUID(), Moments.Kind.KILL);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if(event.getPlayer() instanceof ServerPlayer player && Moments.isPrecious(event.getState())) {
            Moments.mark(player.server, player.getUUID(), Moments.Kind.FIND);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MimicVoice.stopAll();
        VoiceRecorder.setServer(null);
        MovementRecorder.clear();
        Moments.clear();
    }
}
