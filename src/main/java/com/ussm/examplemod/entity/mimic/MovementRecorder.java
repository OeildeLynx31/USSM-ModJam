package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementRecorder {
    public static final int CLIP_TICKS = 160; // 8 sec

    private static final Map<UUID, Track> TRACKS = new HashMap<>();

    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Track track = TRACKS.computeIfAbsent(player.getUUID(), id -> new Track());
            MovementClip clip = record(player, track);
            if (clip != null) {
                int max = Config.MIMIC_MOVEMENT_CLIPS.get();
                if (max > 0) { // 0 = desactive
                    MimicMemory.get(server).offerMove(player.getUUID(), clip, max);
                }
            }
        }
    }

    public static void forget(UUID player) {
        TRACKS.remove(player);
    }
    public static void clear() {
        TRACKS.clear();
    }

    private static MovementClip record(ServerPlayer player, Track track) {
        if (!player.isAlive() || player.isSpectator() || player.isSleeping() || player.isPassenger()
                || player.isFallFlying() || player.getAbilities().flying || player.isInWater() || player.isInLava()) {
            track.reset();
            return null;
        }

        double x = player.getX(), y = player.getY(), z = player.getZ();
        if (!track.hasLast) {
            track.lastX = x;
            track.lastY = y;
            track.lastZ = z;
            track.hasLast = true;
            track.wasOnGround = player.onGround();
            return null;
        }
        double dx = x - track.lastX;
        double dy = y - track.lastY;
        double dz = z - track.lastZ;
        track.lastX = x;
        track.lastY = y;
        track.lastZ = z;
        // tp, chute, dash de katana... pas un mouvement de joueur normal
        if (dx * dx + dz * dz > 1.0 || Math.abs(dy) > 2.0) {
            track.reset();
            return null;
        }

        int flags = 0;
        if (player.isShiftKeyDown()) flags |= MovementClip.SNEAK;
        if (player.isSprinting()) flags |= MovementClip.SPRINT;
        if (track.wasOnGround && !player.onGround() && dy > 0.05) flags |= MovementClip.JUMP;
        if (player.swinging && player.swingTime == 0) flags |= MovementClip.SWING;
        track.wasOnGround = player.onGround();

        int n = track.size++;
        track.dx[n] = (float) dx;
        track.dz[n] = (float) dz;
        track.yaw[n] = player.getYHeadRot();
        track.pitch[n] = player.getXRot();
        track.flags[n] = (byte) flags;

        if (track.size < CLIP_TICKS) return null;
        track.size = 0;

        // clip fini
        double distance = 0;
        double look = 0;
        int sneaks = 0, jumps = 0, swings = 0;
        for (int i = 0; i < CLIP_TICKS; i++) {
            distance += Math.sqrt(track.dx[i] * track.dx[i] + track.dz[i] * track.dz[i]);
            if ((track.flags[i] & MovementClip.JUMP) != 0) jumps++;
            if ((track.flags[i] & MovementClip.SWING) != 0) swings++;
            if (i > 0) {
                look += Math.abs(Mth.wrapDegrees(track.yaw[i] - track.yaw[i - 1])) + Math.abs(track.pitch[i] - track.pitch[i - 1]);
                boolean sneak = (track.flags[i] & MovementClip.SNEAK) != 0;
                boolean sneakBefore = (track.flags[i - 1] & MovementClip.SNEAK) != 0;
                if (sneak != sneakBefore) sneaks++;
            }
        }
        if (distance < 1.0 && look < 90 && sneaks == 0) return null; // afk

        // valeurs au pif pour l'instant
        double score = distance / 12;
        if (score > 1.5) score = 1.5;
        score += Math.min(sneaks, 6) * 0.25;
        score += Math.min(jumps * 0.2, 1.0);
        score += Math.min(swings * 0.1, 0.6);
        if (look < 720) score += look / 720;
        else score += 1.0; // 2 tours complets
        //ExampleMod.LOGGER.info("clip de {} : score {}", player.getName().getString(), score);
        if (score < 0.8) {
            return null;
        }

        return new MovementClip(System.currentTimeMillis(), (float) score,
                track.dx.clone(), track.dz.clone(), track.yaw.clone(), track.pitch.clone(), track.flags.clone());
    }

    private static class Track {
        float[] dx = new float[CLIP_TICKS];
        float[] dz = new float[CLIP_TICKS];
        float[] yaw = new float[CLIP_TICKS];
        float[] pitch = new float[CLIP_TICKS];
        byte[] flags = new byte[CLIP_TICKS];
        int size;
        double lastX, lastY, lastZ;
        boolean hasLast, wasOnGround;

        void reset() {
            size = 0;
            hasLast = false;
        }
    }
}