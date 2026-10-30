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
import org.jetbrains.annotations.Nullable;

// chargé par simple voice chat lui meme grace a l'annotation, si le mod est pas la cette classe est jamais chargée
// (c'est la seule qui touche a l'api, le reste passe par MimicVoice et VoiceRecorder.Decoder)
@ForgeVoicechatPlugin
public class MimicVoicePlugin implements VoicechatPlugin {
    private VoicechatApi api;

    @Override
    public String getPluginId() {
        return ExampleMod.MODID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.api = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, event -> {
            MimicVoice.setBackend(new Backend(event.getVoicechat()));
            ExampleMod.LOGGER.info("simple voice chat est la, les imitateurs peuvent parler");
        });
        registration.registerEvent(VoicechatServerStoppedEvent.class, event -> MimicVoice.setBackend(null));
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
        registration.registerEvent(PlayerDisconnectedEvent.class, event -> VoiceRecorder.onDisconnect(event.getPlayerUuid()));
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();
        if (sender == null || api == null || !(sender.getPlayer().getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        VoiceRecorder.onPacket(player.getUUID(), event.getPacket().getOpusEncodedData(), event.getPacket().isWhispering(),
                () -> new Decoder(api.createDecoder()));
    }

    private record Decoder(OpusDecoder opus) implements VoiceRecorder.Decoder {
        @Override
        public short[] decode(byte[] data) {
            return opus.decode(data);
        }

        @Override
        public void reset() {
            opus.resetState();
        }

        @Override
        public void close() {
            opus.close();
        }
    }

    private record Backend(VoicechatServerApi api) implements MimicVoice.Backend {
        @Nullable
        @Override
        public MimicVoice.Channel open(Mimic mimic, boolean whisper) {
            EntityAudioChannel channel = api.createEntityAudioChannel(mimic.getVoiceChannelId(), api.fromEntity(mimic));
            if (channel == null) {
                return null;
            }
            channel.setDistance((float) api.getVoiceChatDistance());
            channel.setWhispering(whisper);
            return new MimicVoice.Channel() {
                @Override
                public void send(byte[] opus) {
                    channel.send(opus);
                }

                @Override
                public void flush() {
                    channel.flush();
                }
            };
        }
    }
}
