package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// les moments marquants en jeu (coup, mort, kill, minerai rare, vie basse) pour noter les phrases dites a ce moment la
// que sur le thread serveur
public class Moments {
    public enum Kind {
        HURT(1.5f, "hurt"),
        LOW_HEALTH(1.0f, "panic"),
        DEATH(3.0f, "death"),
        KILL(1.0f, "kill"),
        FIND(2.0f, "find");

        final float bonus;
        final String tag;

        Kind(float bonus, String tag) {
            this.bonus = bonus;
            this.tag = tag;
        }
    }

    public record Bonus(float value, Set<String> tags) {
    }

    private record Event(Kind kind, long time) {
    }

    private static final long KEEP_MILLIS = 15_000;
    // un evenement compte pour une phrase s'il arrive jusqu'a 2,5s avant ou 1,5s apres
    private static final long BEFORE_MILLIS = 2500;
    private static final long AFTER_MILLIS = 1500;
    private static final Map<UUID, ArrayDeque<Event>> EVENTS = new HashMap<>();

    public static void mark(MinecraftServer server, UUID player, Kind kind) {
        if (!MimicVoice.isAvailable()) {
            return;
        }
        ArrayDeque<Event> events = EVENTS.computeIfAbsent(player, id -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        events.addLast(new Event(kind, now));
        while (now - events.peekFirst().time > KEEP_MILLIS) {
            events.removeFirst();
        }
        // la phrase est notée des qu'elle finit, le cri juste avant le coup est rattrapé ici
        VoiceBank.get(server, player).boostRecent(now - AFTER_MILLIS, kind.bonus, kind.tag);
    }

    public static Bonus collect(UUID player, long start, long end) {
        Set<String> tags = new LinkedHashSet<>();
        float value = 0;
        ArrayDeque<Event> events = EVENTS.get(player);
        if (events != null) {
            for (Event event : events) {
                if (event.time >= start - BEFORE_MILLIS && event.time <= end + AFTER_MILLIS && tags.add(event.kind.tag)) {
                    value += event.kind.bonus;
                }
            }
        }
        return new Bonus(value, tags);
    }

    public static boolean isPrecious(BlockState state) {
        return state.is(BlockTags.DIAMOND_ORES) || state.is(BlockTags.EMERALD_ORES) || state.is(Blocks.ANCIENT_DEBRIS)
                || state.is(ModBlocks.VOID_MATTER_ORE.get());
    }

    public static void forget(UUID player) {
        EVENTS.remove(player);
    }

    public static void clear() {
        EVENTS.clear();
    }
}
