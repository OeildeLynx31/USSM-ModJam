package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Mimic;
import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

// https://modrepo.de/minecraft/voicechat/api/getting_started
@ForgeVoicechatPlugin
public class MimicVoicePlugin implements VoicechatPlugin {

    public static VoicechatApi voicechatApi;
    public static volatile VoicechatServerApi voicechatServerApi;

    @Override
    public String getPluginId() {
        return ExampleMod.MODID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(VoicechatServerStoppedEvent.class, event -> {
            MimicVoice.enabled = false;
            voicechatServerApi = null;
            MimicVoice.stopAll();
        });
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
        registration.registerEvent(PlayerDisconnectedEvent.class, event -> VoiceRecorder.onDisconnect(event.getPlayerUuid()));
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
        MimicVoice.enabled = true;
        ExampleMod.LOGGER.info("simple voice chat est la, les imitateurs peuvent parler");
    }

    static Consumer<byte[]> openChannel(Mimic mimic, boolean whisper) {
        if (voicechatServerApi == null) {
            return null;
        }
        EntityAudioChannel channel = voicechatServerApi.createEntityAudioChannel(mimic.getVoiceChannelId(), voicechatServerApi.fromEntity(mimic));
        if (channel == null) {
            return null;
        }
        channel.setDistance((float) voicechatServerApi.getVoiceChatDistance());
        channel.setWhispering(whisper);
        return opus -> {
            if (opus == null) channel.flush();
            else channel.send(opus);
        };
    }

    // le debut est repris de voicechat-interaction
    private void onMicPacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }
        if (voicechatApi == null) {
            return;
        }
        if (!(senderConnection.getPlayer().getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        VoiceRecorder.onPacket(player.getUUID(), event.getPacket().getOpusEncodedData(), event.getPacket().isWhispering(), () -> wrap(voicechatApi.createDecoder()));
    }

    private static VoiceRecorder.Decoder wrap(OpusDecoder opus) {
        return new VoiceRecorder.Decoder() {
            @Override
            public short[] decode(byte[] data) {
                return opus.decode(data);
            }

            @Override
            public void resetState() {
                opus.resetState();
            }

            @Override
            public void close() {
                opus.close();
            }
        };
    }
}
