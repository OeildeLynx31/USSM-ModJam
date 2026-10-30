package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.Config;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

// decoupe le micro de chaque joueur en phrases (fini apres 0,7s sans voix) puis ClipScorer garde les interessantes
// les paquets arrivent sur le thread de simple voice chat, d'ou les synchronized sur chaque session
public class VoiceRecorder {
    private static final long GAP_MILLIS = 700;
    // avant le premier mot on garde que quelques paquets muets (touche maintenue sans parler)
    private static final int LEAD_IN_FRAMES = 5;
    // 5 paquets muets d'affilée = une respiration, c'est la qu'on coupe une phrase trop longue
    private static final int PAUSE_FRAMES = 5;
    private static final int MAX_CLIP_MILLIS = 10_000;

    // pour pas dependre de simple voice chat ici
    public interface Decoder {
        short[] decode(byte[] opus);

        void reset();

        void close();
    }

    public record Utterance(long id, UUID player, long start, long end, boolean whisper, List<byte[]> frames, float[] levels) {
    }

    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();
    // derniere fois qu'on a entendu chaque joueur parler (pour pas couper la parole)
    private static final Map<UUID, Long> LAST_HEARD = new ConcurrentHashMap<>();
    @Nullable
    private static volatile MinecraftServer server;

    public static void setServer(@Nullable MinecraftServer value) {
        server = value;
        if (value == null) {
            clear();
        }
    }

    public static Map<UUID, Long> lastHeard() {
        return LAST_HEARD;
    }

    // thread de simple voice chat
    public static void onPacket(UUID player, byte[] opus, boolean whisper, Supplier<Decoder> decoders) {
        if (server == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!Config.MIMIC_RECORD_VOICE.get()) {
            if (opus.length > 0) {
                LAST_HEARD.put(player, now);
            }
            return;
        }
        Session session = SESSIONS.computeIfAbsent(player, id -> new Session(decoders.get()));
        List<Utterance> finished = new ArrayList<>(2);
        synchronized (session) {
            if (opus.length == 0) {
                // fin de transmission, c'est juste une pause s'il reprend vite
                session.decoder.reset();
                if (session.active() && !session.voiced) {
                    finish(player, session, now, finished);
                }
            } else {
                if (session.isOver(now)) {
                    finish(player, session, now, finished);
                }
                if (!session.active()) {
                    session.reset(now);
                }
                float level = decodeLevel(session.decoder, opus);
                if (level > ClipScorer.VOICED_DB) {
                    session.voiced = true;
                    session.lastVoiced = now;
                    session.silentRun = 0;
                    LAST_HEARD.put(player, now);
                } else if (!session.voiced) {
                    if (session.frames.size() >= LEAD_IN_FRAMES) {
                        session.frames.remove(0);
                        System.arraycopy(session.levels, 1, session.levels, 0, session.frames.size());
                    }
                    session.start = now;
                } else if (++session.silentRun == PAUSE_FRAMES) {
                    session.pauseAt = session.frames.size() - (PAUSE_FRAMES - 1);
                }
                session.append(opus.clone(), level);
                session.last = now;
                session.whisper |= whisper;
                if (session.frames.size() * VoiceClip.FRAME_MILLIS >= MAX_CLIP_MILLIS) {
                    splitTooLong(player, session, now, finished);
                }
            }
        }
        finished.forEach(VoiceRecorder::handOver);
    }

    // thread serveur, finit les phrases suivies d'un silence
    public static void tick() {
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, Session> entry : SESSIONS.entrySet()) {
            Session session = entry.getValue();
            List<Utterance> finished = new ArrayList<>(1);
            synchronized (session) {
                if (session.isOver(now)) {
                    finish(entry.getKey(), session, now, finished);
                }
            }
            finished.forEach(VoiceRecorder::handOver);
        }
    }

    public static void onDisconnect(UUID player) {
        Session session = SESSIONS.remove(player);
        if (session == null) {
            return;
        }
        List<Utterance> finished = new ArrayList<>(1);
        synchronized (session) {
            if (session.active()) {
                finish(player, session, System.currentTimeMillis(), finished);
            }
            session.decoder.close();
        }
        finished.forEach(VoiceRecorder::handOver);
    }

    public static void clear() {
        for (Session session : SESSIONS.values()) {
            synchronized (session) {
                session.decoder.close();
            }
        }
        SESSIONS.clear();
        LAST_HEARD.clear();
    }

    private static void finish(UUID player, Session session, long now, List<Utterance> out) {
        if (!session.discard) {
            out.add(new Utterance(ThreadLocalRandom.current().nextLong(), player, session.start, session.last, session.whisper,
                    new ArrayList<>(session.frames), Arrays.copyOf(session.levels, session.frames.size())));
        }
        session.discard = false;
        session.reset(now);
    }

    // au plafond on coupe a la derniere respiration, la suite devient le debut de la phrase d'apres
    // sans respiration du tout les deux moitiés commenceraient en plein mot, on jette tout
    private static void splitTooLong(UUID player, Session session, long now, List<Utterance> out) {
        int cut = session.pauseAt;
        if (cut <= 0) {
            session.discard = true;
            finish(player, session, now, out);
            session.discard = true;
            return;
        }
        List<byte[]> rest = new ArrayList<>(session.frames.subList(cut, session.frames.size()));
        float[] restLevels = Arrays.copyOfRange(session.levels, cut, session.frames.size());
        long lastVoiced = session.lastVoiced;
        boolean whisper = session.whisper;
        session.frames.subList(cut, session.frames.size()).clear();
        session.last = now - (long) rest.size() * VoiceClip.FRAME_MILLIS;
        finish(player, session, now, out);
        session.start = now - (long) rest.size() * VoiceClip.FRAME_MILLIS;
        session.whisper = whisper;
        int trailingSilence = 0;
        for (int i = 0; i < rest.size(); i++) {
            session.append(rest.get(i), restLevels[i]);
            if (restLevels[i] > ClipScorer.VOICED_DB) {
                session.voiced = true;
                trailingSilence = 0;
            } else {
                trailingSilence++;
            }
        }
        session.silentRun = trailingSilence;
        session.lastVoiced = session.voiced ? lastVoiced : now;
        session.last = now;
    }

    // la note et la banque se font sur le thread serveur
    private static void handOver(Utterance utterance) {
        MinecraftServer current = server;
        if (current != null) {
            current.execute(() -> ClipScorer.keep(current, utterance));
        }
    }

    static float decodeLevel(Decoder decoder, byte[] opus) {
        try {
            short[] pcm = decoder.decode(opus);
            if (pcm == null || pcm.length == 0) {
                return -120f;
            }
            double sum = 0;
            for (short s : pcm) {
                sum += (double) s * s;
            }
            double rms = Math.sqrt(sum / pcm.length);
            return rms < 1 ? -120f : (float) (20 * Math.log10(rms / 32768.0));
        } catch (Exception e) {
            return -120f;
        }
    }

    private static class Session {
        final Decoder decoder;
        final List<byte[]> frames = new ArrayList<>();
        float[] levels = new float[256];
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
            return active() && now - (voiced ? lastVoiced : last) > GAP_MILLIS;
        }

        void reset(long now) {
            frames.clear();
            start = now;
            whisper = false;
            voiced = false;
            silentRun = 0;
            pauseAt = -1;
        }

        void append(byte[] opus, float level) {
            if (frames.size() == levels.length) {
                levels = Arrays.copyOf(levels, levels.length * 2);
            }
            levels[frames.size()] = level;
            frames.add(opus);
        }
    }
}
