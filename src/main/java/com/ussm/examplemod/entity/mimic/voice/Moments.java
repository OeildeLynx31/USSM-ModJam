package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.block.ModBlocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class Moments {
    private static final long KEEP_MILLIS = 15000; // 15 sec
    private static final long BEFORE_MILLIS = 2500;
    private static final long AFTER_MILLIS = 1500;
    private static final Map<UUID, ArrayDeque<Event>> EVENTS = new HashMap<>();

    public enum Kind {
        HURT(1.5F, "hurt"),
        LOW_HEALTH(1.0F, "panic"),
        DEATH(3.0F, "death"),
        KILL(1.0F, "kill"),
        FIND(2.0F, "find");

        final float bonus;
        final String tag;

        Kind(float bonus, String tag) {
            this.bonus = bonus;
            this.tag = tag;
        }
    }

    public static void mark(MinecraftServer server, UUID player, Kind kind) {
        if(!MimicVoice.isAvailable()) return;
        ArrayDeque<Event> events = EVENTS.computeIfAbsent(player, id -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        events.addLast(new Event(kind, now));
        while (now - events.peekFirst().time > KEEP_MILLIS) {
            events.removeFirst();
        }
        VoiceBank.get(server, player).boostRecent(now - AFTER_MILLIS, kind.bonus, kind.tag);
    }

    public static float collect(UUID player, long start, long end, Set<String> tags) {
        ArrayDeque<Event> events = EVENTS.get(player);
        if (events == null) return 0;

        float value = 0;
        for(Event e : events) {
            if (e.time >= start - BEFORE_MILLIS && e.time <= end + AFTER_MILLIS && tags.add(e.kind.tag)) {
                value += e.kind.bonus;
            }
        }
        return value;
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

    private static class Event {
        final Kind kind;
        final long time;

        Event(Kind kind, long time) {
            this.kind = kind;
            this.time = time;
        }
    }
}
