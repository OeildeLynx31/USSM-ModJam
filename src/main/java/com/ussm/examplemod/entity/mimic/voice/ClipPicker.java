package com.ussm.examplemod.entity.mimic.voice;

import net.minecraft.util.RandomSource;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ClipPicker {
    private static final Set<String> SCARED = Set.of("loud", "hurt", "panic", "death");

    public enum Context {
        LURE,
        ATTACK,
        PAIN,
        KILL
    }

    public static VoiceClip pick(List<VoiceClip> clips, Context context, Collection<Long> recentlyPlayed, RandomSource random) {
        if(clips.isEmpty()) return null;

        double[] weights = new double[clips.size()];
        double total = 0;
        for(int i = 0; i < clips.size(); i++) {
            VoiceClip clip = clips.get(i);
            double w = Math.max(0.1, clip.score) * fit(clip, context);
            if(w <= 0) continue;

            if(clip.whisper && context != Context.LURE) w *= 0.3;
            if (recentlyPlayed.contains(clip.id)) {
                w *= context == Context.LURE ? 0.0 : 0.05;
            }
            weights[i] = w;
            total += w;
        }
        if(total <= 0) return null;

        double r = random.nextDouble() * total;
        VoiceClip res = null;
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] == 0) continue;
            res = clips.get(i);
            r -= weights[i];
            if (r <= 0) break;
        }
        return res;
    }

    // pas de cri au calme, pas de blabla quand il attaque
    private static double fit(VoiceClip clip, Context context) {
        Set<String> tags = clip.tags;
        boolean loud = tags.contains("loud");
        switch(context) {
            case KILL:
                return tags.contains("kill") ? 3.0 : loud ? 1.0 : 0.0;
            case LURE:
                if(tags.contains("hurt") || tags.contains("panic") || tags.contains("death")) {
                    return 0.0;
                }
                double weight = 1.0;
                if(tags.contains("conversation") || tags.contains("find") || tags.contains("kill")) weight = 2.0;
                if(loud) weight *= 0.1;
                if(clip.durationMillis() < 1200) weight *= 0.3;
                return weight;
            default:
                // TODO PAIN et ATTACK font pareil pour l'instant
                if(!tags.stream().anyMatch(SCARED::contains)) return 0.0;
                return loud || tags.contains("panic") ? 2.0 : 1.0;
        }
    }
}
