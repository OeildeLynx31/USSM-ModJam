package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// enregistre comment chaque joueur bouge par fenetres de 8s et garde les plus vivantes
// (que sur le thread serveur)
public class MovementRecorder {
    public static final int CLIP_TICKS = 160;

    private static final Map<UUID, Track> TRACKS = new HashMap<>();

    public static void tick(MinecraftServer server) {
        MimicMemory memory = null;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Track track = TRACKS.computeIfAbsent(player.getUUID(), id -> new Track());
            MovementClip clip = record(player, track);
            if (clip != null) {
                if (memory == null) {
                    memory = MimicMemory.get(server);
                }
                memory.offerMove(player.getUUID(), clip, Config.MIMIC_MOVEMENT_CLIPS.get());
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
        // ce qu'un imitateur peut pas refaire a pied coupe la fenetre
        if (player.isSpectator() || player.isSleeping() || player.isPassenger() || player.isFallFlying()
                || player.getAbilities().flying || player.isInWater() || player.isInLava() || !player.isAlive()) {
            track.reset();
            return null;
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
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

        int i = track.size++;
        track.dx[i] = (float) dx;
        track.dz[i] = (float) dz;
        track.yaw[i] = player.getYHeadRot();
        track.pitch[i] = player.getXRot();
        track.flags[i] = (byte) flags;
        if (track.size < CLIP_TICKS) {
            return null;
        }
        track.size = 0;
        return evaluate(track);
    }

    // une fenetre vaut le coup si le joueur fait vraiment un truc dedans
    private static MovementClip evaluate(Track track) {
        double distance = 0;
        double look = 0;
        int sneakToggles = 0, jumps = 0, swings = 0;
        for (int i = 0; i < CLIP_TICKS; i++) {
            distance += Math.sqrt(track.dx[i] * track.dx[i] + track.dz[i] * track.dz[i]);
            if (i > 0) {
                look += Math.abs(Mth.wrapDegrees(track.yaw[i] - track.yaw[i - 1])) + Math.abs(track.pitch[i] - track.pitch[i - 1]);
                if (((track.flags[i] ^ track.flags[i - 1]) & MovementClip.SNEAK) != 0) {
                    sneakToggles++;
                }
            }
            if ((track.flags[i] & MovementClip.JUMP) != 0) jumps++;
            if ((track.flags[i] & MovementClip.SWING) != 0) swings++;
        }
        if (distance < 1.0 && look < 90 && sneakToggles == 0) {
            return null; // afk
        }
        double score = Math.min(distance / 12.0, 1.5)
                + Math.min(sneakToggles * 0.25, 1.5)
                + Math.min(jumps * 0.2, 1.0)
                + Math.min(swings * 0.1, 0.6)
                + Math.min(look / 720.0, 1.0);
        if (score < 0.8) {
            return null;
        }
        return new MovementClip(System.currentTimeMillis(), (float) score, track.dx.clone(), track.dz.clone(),
                track.yaw.clone(), track.pitch.clone(), track.flags.clone());
    }

    private static class Track {
        final float[] dx = new float[CLIP_TICKS];
        final float[] dz = new float[CLIP_TICKS];
        final float[] yaw = new float[CLIP_TICKS];
        final float[] pitch = new float[CLIP_TICKS];
        final byte[] flags = new byte[CLIP_TICKS];
        int size;
        double lastX, lastY, lastZ;
        boolean hasLast, wasOnGround;

        void reset() {
            size = 0;
            hasLast = false;
        }
    }
}
