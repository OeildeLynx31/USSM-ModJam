package com.ussm.examplemod.entity.mimic.voice;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

// choisit quoi lui faire dire selon la situation : pas de cri au calme, pas de blabla quand il attaque
// ce qui colle pas est ecarté, pas juste moins probable : sans rien d'adapté il se tait
public class ClipPicker {
    public enum Context {
        LURE,   // il fait semblant / repond a quelqu'un
        ATTACK, // il vient de se reveler
        PAIN,   // il prend des coups
        KILL    // il vient de tuer quelqu'un
    }

    private static final Set<String> CALM = Set.of("conversation", "find", "kill");
    private static final Set<String> SCARED = Set.of("loud", "hurt", "panic", "death");
    private static final Set<String> PAINFUL = Set.of("hurt", "panic", "death");
    // un mot tout seul ("quoi ?", "ouais") veut rien dire hors contexte
    private static final int SHORT_MILLIS = 1200;

    @Nullable
    public static VoiceClip pick(List<VoiceClip> clips, Context context, Collection<Long> recentlyPlayed, RandomSource random) {
        if (clips.isEmpty()) {
            return null;
        }
        double[] weights = new double[clips.size()];
        double total = 0;
        for (int i = 0; i < clips.size(); i++) {
            VoiceClip clip = clips.get(i);
            double weight = Math.max(0.1, clip.score) * fit(clip, context);
            if (weight <= 0) {
                continue;
            }
            if (clip.whisper && context != Context.LURE) {
                weight *= 0.3;
            }
            if (recentlyPlayed.contains(clip.id)) {
                weight *= context == Context.LURE ? 0.0 : 0.05;
            }
            weights[i] = weight;
            total += weight;
        }
        if (total <= 0) {
            return null;
        }
        double roll = random.nextDouble() * total;
        for (int i = 0; i < weights.length; i++) {
            roll -= weights[i];
            if (roll <= 0 && weights[i] > 0) {
                return clips.get(i);
            }
        }
        for (int i = weights.length - 1; i >= 0; i--) {
            if (weights[i] > 0) {
                return clips.get(i);
            }
        }
        return null;
    }

    private static double fit(VoiceClip clip, Context context) {
        boolean calm = clip.tags.stream().anyMatch(CALM::contains);
        boolean scared = clip.tags.stream().anyMatch(SCARED::contains);
        boolean loud = clip.tags.contains("loud");
        return switch (context) {
            case LURE -> clip.tags.stream().anyMatch(PAINFUL::contains) ? 0.0
                    : (calm ? 2.0 : 1.0) * (loud ? 0.1 : 1.0) * (clip.durationMillis() < SHORT_MILLIS ? 0.3 : 1.0);
            case ATTACK, PAIN -> scared ? (loud || clip.tags.contains("panic") ? 2.0 : 1.0) : 0.0;
            case KILL -> clip.tags.contains("kill") ? 3.0 : loud ? 1.0 : 0.0;
        };
    }
}
