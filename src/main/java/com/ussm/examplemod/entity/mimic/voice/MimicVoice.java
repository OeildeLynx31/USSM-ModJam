package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Mimic;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MimicVoice {
    private static final Map<UUID, Playback> PLAYING = new ConcurrentHashMap<>();
    private static ScheduledExecutorService thread;
    public static volatile boolean enabled; // mis par le thread de voicechat

    public static boolean isAvailable() {
        return enabled;
    }

    public static boolean isTalking(Mimic mimic) {
        return PLAYING.containsKey(mimic.getUUID());
    }

    public static boolean speak(Mimic mimic, VoiceClip clip) {
        if (!enabled || clip.frames.isEmpty() || isTalking(mimic)) return false;
        Consumer<byte[]> out = MimicVoicePlugin.openChannel(mimic, clip.whisper);
        if (out == null) return false;

        PLAYING.put(mimic.getUUID(), new Playback(mimic, out, clip));
        startThread();
        return true;
    }

    public static void stop(Mimic mimic) {
        Playback playback = PLAYING.remove(mimic.getUUID());
        if (playback != null) {
            playback.out.accept(null);
        }
    }

    public static void stopAll() {
        PLAYING.values().forEach(p -> p.out.accept(null));
        PLAYING.clear();
        stopThread();
    }

    // TODO arreter le thread quand plus aucun mimic parle, la il tourne jusqu'a la fin du serveur
    private static synchronized void startThread() {
        if (thread != null) return;
        thread = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Mimic-Voice");
            t.setDaemon(true);
            return t;
        });
        // un paquet toutes les 20ms comme un vrai micro
        thread.scheduleAtFixedRate(MimicVoice::sendFrames, 0, VoiceClip.FRAME_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static synchronized void stopThread() {
        if(thread != null) {
            thread.shutdownNow();
            thread = null;
        }
    }

    private static void sendFrames() {
        try {
            Iterator<Playback> it = PLAYING.values().iterator();
            while (it.hasNext()) {
                Playback p = it.next();
                if (p.mimic.isRemoved() || !p.mimic.isAlive() || p.index >= p.clip.frames.size()) {
                    p.out.accept(null); // null = fini
                    it.remove();
                } else {
                    p.out.accept(p.clip.frames.get(p.index++));
                }
            }
        } catch (Throwable e) {
            // si ca throw une seule fois le scheduler arrete tout sans rien dire
            // https://stackoverflow.com/questions/6894595/scheduledexecutorservice-exception-handling
            ExampleMod.LOGGER.error("erreur pendant la voix d'un imitateur", e);
        }
    }

    private static class Playback {
        Mimic mimic;
        Consumer<byte[]> out;
        VoiceClip clip;
        int index = 0;

        Playback(Mimic mimic, Consumer<byte[]> out, VoiceClip clip) {
            this.mimic = mimic;
            this.out = out;
            this.clip = clip;
        }
    }
}
