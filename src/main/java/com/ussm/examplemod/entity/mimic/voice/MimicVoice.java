package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Mimic;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// fait parler les imitateurs : les paquets opus enregistrés sont renvoyés tels quels toutes les 20ms dans un canal
// accroché a l'entité, les joueurs l'entendent en 3D comme une vraie voix
// le backend vient de MimicVoicePlugin, il est null si simple voice chat est pas installé
public class MimicVoice {
    public interface Channel {
        void send(byte[] opus);

        void flush();
    }

    public interface Backend {
        @Nullable
        Channel open(Mimic mimic, boolean whisper);
    }

    @Nullable
    private static volatile Backend backend;
    private static final Map<UUID, Playback> PLAYING = new ConcurrentHashMap<>();
    @Nullable
    private static ScheduledExecutorService executor;

    public static void setBackend(@Nullable Backend value) {
        backend = value;
        if (value == null) {
            stopAll();
        }
    }

    public static boolean isAvailable() {
        return backend != null;
    }

    public static boolean isTalking(Mimic mimic) {
        return PLAYING.containsKey(mimic.getUUID());
    }

    public static boolean speak(Mimic mimic, VoiceClip clip) {
        Backend current = backend;
        if (current == null || clip.frames.isEmpty() || isTalking(mimic)) {
            return false;
        }
        Channel channel = current.open(mimic, clip.whisper);
        if (channel == null) {
            return false;
        }
        PLAYING.put(mimic.getUUID(), new Playback(mimic, channel, clip));
        ensureRunning();
        return true;
    }

    public static void stop(Mimic mimic) {
        Playback playback = PLAYING.remove(mimic.getUUID());
        if (playback != null) {
            playback.channel.flush();
        }
    }

    public static void stopAll() {
        PLAYING.values().forEach(p -> p.channel.flush());
        PLAYING.clear();
        synchronized (MimicVoice.class) {
            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
        }
    }

    private static synchronized void ensureRunning() {
        if (executor != null) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "Mimic-Voice");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleAtFixedRate(MimicVoice::sendFrames, 0, VoiceClip.FRAME_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static void sendFrames() {
        try {
            Iterator<Playback> iterator = PLAYING.values().iterator();
            while (iterator.hasNext()) {
                Playback playback = iterator.next();
                if (playback.mimic.isRemoved() || !playback.mimic.isAlive() || playback.index >= playback.clip.frames.size()) {
                    playback.channel.flush();
                    iterator.remove();
                    continue;
                }
                playback.channel.send(playback.clip.frames.get(playback.index++));
            }
        } catch (Throwable t) {
            // une exception arreterait la tache pour de bon
            ExampleMod.LOGGER.error("erreur pendant la voix d'un imitateur", t);
        }
    }

    private static class Playback {
        final Mimic mimic;
        final Channel channel;
        final VoiceClip clip;
        int index;

        Playback(Mimic mimic, Channel channel, VoiceClip clip) {
            this.mimic = mimic;
            this.channel = channel;
            this.clip = clip;
        }
    }
}
