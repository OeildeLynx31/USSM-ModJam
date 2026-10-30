package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// note une phrase : l'acoustique (claire, 1 a 6s, forte ou un cri), ce qui se passait en jeu, et si c'etait une reponse
public class ClipScorer {
    // en dessous c'est du silence ou du bruit de fond
    static final float VOICED_DB = -48f;
    private static final int MIN_CLIP_MILLIS = 700;
    private static final double MIN_SCORE = 0.8;
    private static final long REPLY_WINDOW_MILLIS = 3000;
    private static final double CONVERSATION_RANGE = 32;

    // thread serveur
    static void keep(MinecraftServer server, VoiceRecorder.Utterance utterance) {
        VoiceClip clip = score(server, utterance);
        if (clip != null) {
            VoiceBank.get(server, utterance.player()).offer(server, utterance.player(), clip, Config.MIMIC_VOICE_CLIPS.get());
        }
    }

    @Nullable
    static VoiceClip score(MinecraftServer server, VoiceRecorder.Utterance utterance) {
        int[] range = trimSilence(utterance.levels());
        if (range == null) {
            return null;
        }
        List<byte[]> frames = List.copyOf(utterance.frames().subList(range[0], range[1] + 1));
        float[] levels = Arrays.copyOfRange(utterance.levels(), range[0], range[1] + 1);
        int durationMillis = frames.size() * VoiceClip.FRAME_MILLIS;
        if (durationMillis < MIN_CLIP_MILLIS) {
            return null;
        }

        int voiced = 0;
        double sum = 0, sumSq = 0;
        float peak = -120f;
        for (float level : levels) {
            if (level > VOICED_DB) {
                voiced++;
                sum += level;
                sumSq += (double) level * level;
            }
            peak = Math.max(peak, level);
        }
        // faut de la voix, pas un souffle ou un clic de souris
        if (voiced == 0 || (double) voiced / levels.length < 0.35) {
            return null;
        }

        Set<String> tags = new LinkedHashSet<>();
        double mean = sum / voiced;
        double deviation = Math.sqrt(Math.max(0, sumSq / voiced - mean * mean));
        double seconds = durationMillis / 1000.0;

        double score = seconds < 1.2 ? 0.5 : seconds <= 6 ? 1.0 : seconds <= 9 ? 0.75 : 0.5;
        score += clamp((mean + 42) / 27);
        score += clamp((deviation - 3) / 6) * 0.5;
        if (peak > -6) {
            score += 1.0;
            tags.add("loud");
        } else if (peak > -10) {
            score += 0.5;
            tags.add("loud");
        }
        if (utterance.whisper()) {
            tags.add("whisper");
        }

        Moments.Bonus bonus = Moments.collect(utterance.player(), utterance.start(), utterance.end());
        score += bonus.value();
        tags.addAll(bonus.tags());

        if (isReply(server, utterance)) {
            score += 0.75;
            tags.add("conversation");
        }
        if (score < MIN_SCORE) {
            return null;
        }
        return new VoiceClip(utterance.id(), System.currentTimeMillis(), (float) score, tags, utterance.whisper(), frames);
    }

    // quelqu'un de proche a parlé juste avant : c'est un echange, plus vivant
    private static boolean isReply(MinecraftServer server, VoiceRecorder.Utterance utterance) {
        ServerPlayer self = server.getPlayerList().getPlayer(utterance.player());
        if (self == null) {
            return false;
        }
        for (Map.Entry<UUID, Long> entry : VoiceRecorder.lastHeard().entrySet()) {
            long heard = entry.getValue();
            if (entry.getKey().equals(utterance.player()) || heard < utterance.start() - REPLY_WINDOW_MILLIS || heard > utterance.end()) {
                continue;
            }
            ServerPlayer other = server.getPlayerList().getPlayer(entry.getKey());
            if (other != null && other.level() == self.level() && other.distanceToSqr(self) <= CONVERSATION_RANGE * CONVERSATION_RANGE) {
                return true;
            }
        }
        return false;
    }

    // enleve le silence au debut et a la fin (frequent en push to talk) en gardant une marge
    @Nullable
    private static int[] trimSilence(float[] levels) {
        int first = 0;
        while (first < levels.length && levels[first] <= VOICED_DB) {
            first++;
        }
        if (first >= levels.length) {
            return null;
        }
        int last = levels.length - 1;
        while (last > first && levels[last] <= VOICED_DB) {
            last--;
        }
        return new int[]{Math.max(0, first - 3), Math.min(levels.length - 1, last + 5)};
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }
}
