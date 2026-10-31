package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.*;

public class ClipScorer {
    static final float VOICED_DB = -48f;

    static void keep(MinecraftServer server, VoiceRecorder.Phrase p) {
        float[] all = p.levels;

        // enleve le blanc au debut et a la fin
        int first = 0;
        while (first < all.length && all[first] <= VOICED_DB) first++;
        if (first >= all.length) return;
        int last = all.length - 1;
        while (last > first && all[last] <= VOICED_DB) last--;
        int from = Math.max(0, first - 3);
        int to = Math.min(all.length, last + 6);

        List<byte[]> frames = new ArrayList<>(p.frames.subList(from, to));
        float[] levels = Arrays.copyOfRange(all, from, to);
        int duration = frames.size() * VoiceClip.FRAME_MILLIS;
        if (duration < 700) return;

        int voiced = 0;
        double sum = 0, sumSq = 0, peak = -120;
        for (int i = 0; i < levels.length; i++) {
            double db = levels[i];
            if(db > VOICED_DB) {
                voiced++;
                sum += db;
                sumSq += db * db;
            }
            peak = Math.max(peak, db);
        }
        if ((double) voiced / levels.length < 0.35) return;

        double avg = sum / voiced;
        // le max sinon des fois c'est -0.0000001 a cause des arrondis et sqrt donne NaN
        double std = Math.sqrt(Math.max(0, sumSq / voiced - avg * avg));

        // les valeurs c'est un peu au pif, a regler
        double score;
        if (duration < 1200 || duration > 9000) score = 0.5;
        else if (duration <= 6000) score = 1;
        else score = 0.75;
        score += Mth.clamp((avg + 42) / 27, 0, 1);
        score += Mth.clamp((std - 3) / 6, 0, 1) / 2;

        Set<String> tags = new LinkedHashSet<>();
        if (peak > -10) {
            score += peak > -6 ? 1.0 : 0.5;
            tags.add("loud");
        }
        if (p.whisper) tags.add("whisper");

        score += Moments.collect(p.player, p.start, p.end, tags);

        ServerPlayer self = server.getPlayerList().getPlayer(p.player);
        if (self != null) {
            for (Map.Entry<UUID, Long> e : VoiceRecorder.lastHeard().entrySet()) {
                long heard = e.getValue();
                if (e.getKey().equals(p.player) || heard < p.start - 3000 || heard > p.end) continue;

                // qqn a parlé juste avant ou pendant, a coté de lui = il lui repond
                ServerPlayer other = server.getPlayerList().getPlayer(e.getKey());
                if (other != null && other.level() == self.level() && other.distanceToSqr(self) <= 32 * 32) {
                    score += 0.75;
                    tags.add("conversation");
                    break;
                }
            }
        }

        //System.out.println("phrase " + duration + "ms avg=" + avg + " std=" + std + " -> " + score + " " + tags);
        if (score < 0.8) return;
        VoiceClip clip = new VoiceClip(p.id, System.currentTimeMillis(), (float) score, tags, p.whisper, frames);
        VoiceBank.get(server, p.player).offer(server, p.player, clip, Config.MIMIC_VOICE_CLIPS.get());
    }
}
