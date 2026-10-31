package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.Config;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class VoiceRecorder {
    private static final long GAP = 700; // 0,7s sans parler = fin de la phrase
    private static final int LEAD_IN = 5;
    private static final int PAUSE = 5;
    private static final int MAX_LENGTH = 10000; //8000

    private static final Map<UUID, Session> sessions = new HashMap<>();
    private static final Map<UUID, Long> LAST_HEARD = new ConcurrentHashMap<>(); // TODO enlever les joueurs qui se deco
    private static final Random random = new Random();
    private static MinecraftServer server;

    public static synchronized void setServer(MinecraftServer value) {
        server = value;
        if(value == null) {
            for (Session s : sessions.values()) {
                s.decoder.close();
            }
            sessions.clear();
            LAST_HEARD.clear();
        }
    }

    // pas l'OpusDecoder de voicechat direct ici, sinon ca crash quand le mod est pas installé
    // (cette classe est chargée meme sans lui). c'est MimicVoicePlugin qui fait le lien
    public interface Decoder {
        short[] decode(byte[] opus);

        void resetState();

        void close();
    }

    public static Map<UUID, Long> lastHeard() {
        return LAST_HEARD;
    }

    // synchronized partout sinon ConcurrentModificationException avec le thread de voicechat
    public static synchronized void tick() {
        long now = System.currentTimeMillis();
        for (UUID player : sessions.keySet()) {
            Session s = sessions.get(player);
            if (s.isOver(now)) finish(player, s, now);
        }
    }

    public static synchronized void onPacket(UUID player, byte[] opus, boolean whisper, Supplier<Decoder> decoders) {
        if (server == null) return;
        long now = System.currentTimeMillis();
        if (!Config.MIMIC_RECORD_VOICE.get()) {
            if (opus.length > 0) LAST_HEARD.put(player, now);
            return;
        }

        Session s = sessions.get(player);
        if (s == null) {
            s = new Session(decoders.get());
            sessions.put(player, s);
        }

        // paquet vide = il a lache la touche (ou l'activation vocale a coupé)
        // si il a rien dit on termine direct, sinon on attend le GAP comme d'hab dans tick()
        if (opus.length == 0) {
            s.decoder.resetState();
            //finish(player, s, now);
            if (s.active() && !s.voiced) {
                finish(player, s, now);
            }
            return;
        }

        if (s.isOver(now)) {
            finish(player, s, now);
        }
        if (!s.active()) s.reset(now);

        float db = volume(s.decoder, opus);
        s.frames.add(opus.clone());
        s.levels.add(db);
        s.last = now;
        s.whisper |= whisper;

        if (db > ClipScorer.VOICED_DB) {
            s.voiced = true;
            s.lastVoiced = now;
            s.silentRun = 0;
            LAST_HEARD.put(player, now);
        } else if (!s.voiced) {
            if(s.frames.size() > LEAD_IN) {
                s.frames.remove(0);
                s.levels.remove(0);
            }
            s.start = now;
        } else {
            s.silentRun++;
            if (s.silentRun == PAUSE) s.pauseAt = s.frames.size() - PAUSE;
        }

        if (s.frames.size() * VoiceClip.FRAME_MILLIS < MAX_LENGTH) return;

        // trop long, on coupe a la derniere respiration
        int cut = s.pauseAt;
        if (cut <= 0) {
            // pas de pause du tout, tant pis on jete
            s.reset(now);
            s.discard = true;
            return;
        }
        List<byte[]> rest = new ArrayList<>();
        List<Float> restDb = new ArrayList<>();
        while (s.frames.size() > cut) {
            rest.add(s.frames.remove(cut));
            restDb.add(s.levels.remove(cut));
        }
        boolean w = s.whisper;
        long start = now - rest.size() * VoiceClip.FRAME_MILLIS;
        s.last = start;
        finish(player, s, now);

        s.start = start;
        s.whisper = w;
        for (int i = 0; i < rest.size(); i++) {
            s.frames.add(rest.get(i));
            s.levels.add(restDb.get(i));
            if(restDb.get(i) > ClipScorer.VOICED_DB) {
                s.voiced = true;
                s.silentRun = 0;
            } else s.silentRun++;
        }
        if (!s.voiced) s.lastVoiced = now;
        s.last = now;
    }

    public static synchronized void onDisconnect(UUID player) {
        Session s = sessions.remove(player);
        if (s == null) return;

        s.decoder.close();
        if (s.active()) {
            finish(player, s, System.currentTimeMillis());
        }
    }

    private static void finish(UUID player, Session s, long now) {
        if (!s.discard) {
            Phrase p = new Phrase();
            p.id = random.nextLong();
            p.player = player;
            p.start = s.start;
            p.end = s.last;
            p.whisper = s.whisper;
            p.frames = new ArrayList<>(s.frames);
            p.levels = new float[s.levels.size()];
            for (int i = 0; i < p.levels.length; i++) {
                p.levels[i] = s.levels.get(i);
            }
            MinecraftServer mc = server;
            mc.execute(() -> ClipScorer.keep(mc, p));
        }
        s.discard = false;
        s.reset(now);
    }

    // repris de AudioUtils.calculateAudioLevel (voicechat-interaction)
    private static float volume(Decoder decoder, byte[] opus) {
        short[] pcm;
        try {
            pcm = decoder.decode(opus);
        } catch (Exception e) {
            return -120F;
        }
        if (pcm == null || pcm.length == 0) return -120f;

        double rms = 0D; // root mean square (RMS) amplitude
        for (int i = 0; i < pcm.length; i++) {
            rms += pcm[i] * pcm[i];
        }
        rms = Math.sqrt(rms / pcm.length);
        if (rms < 1) return -120f;
        return (float) (20 * Math.log10(rms / 32768.0));
    }

    static class Phrase {
        long id;
        UUID player;
        long start, end;
        boolean whisper;
        List<byte[]> frames;
        float[] levels;
    }

    private static class Session {
        Decoder decoder;
        List<byte[]> frames = new ArrayList<>();
        List<Float> levels = new ArrayList<>();
        long start, last, lastVoiced;
        boolean whisper, voiced, discard;
        int silentRun;
        int pauseAt = -1;

        Session(Decoder decoder) {
            this.decoder = decoder;
        }

        boolean active() {
            return !frames.isEmpty();
        }

        boolean isOver(long now) {
            return active() && now - (voiced ? lastVoiced : last) > GAP;
        }

        void reset(long now) {
            frames.clear();
            levels.clear();
            start = now;
            whisper = false;
            voiced = false;
            silentRun = 0;
            pauseAt = -1;
        }
    }
}
