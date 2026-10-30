package com.ussm.examplemod.entity.mimic;

import net.minecraft.nbt.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// 8 secondes de deplacement d'un joueur, tick par tick, pour que l'imitateur bouge comme lui
public class MovementClip {
    public static final int SNEAK = 1;
    public static final int SPRINT = 2;
    public static final int JUMP = 4;
    public static final int SWING = 8;

    public final long createdAt;
    public final float score;
    private final float[] dx, dz, yaw, pitch;
    private final byte[] flags;

    public MovementClip(long createdAt, float score, float[] dx, float[] dz, float[] yaw, float[] pitch, byte[] flags) {
        this.createdAt = createdAt;
        this.score = score;
        this.dx = dx;
        this.dz = dz;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
    }

    public int size() {
        return flags.length;
    }

    public float dx(int i) {
        return dx[i];
    }

    public float dz(int i) {
        return dz[i];
    }

    public float yaw(int i) {
        return yaw[i];
    }

    public float pitch(int i) {
        return pitch[i];
    }

    public boolean has(int i, int flag) {
        return (flags[i] & flag) != 0;
    }

    // petit bonus aux recents pour que ca se renouvelle
    public float effectiveScore(long now) {
        double hours = Math.max(0, now - createdAt) / 3_600_000.0;
        return score + 0.5f * (float) Math.exp(-hours / 24.0);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Time", createdAt);
        tag.putFloat("Score", score);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(size());
            for (int i = 0; i < size(); i++) {
                out.writeFloat(dx[i]);
                out.writeFloat(dz[i]);
                out.writeFloat(yaw[i]);
                out.writeFloat(pitch[i]);
                out.writeByte(flags[i]);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        tag.putByteArray("Frames", bytes.toByteArray());
        return tag;
    }

    public static MovementClip load(CompoundTag tag) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(tag.getByteArray("Frames")))) {
            int count = in.readInt();
            float[] dx = new float[count];
            float[] dz = new float[count];
            float[] yaw = new float[count];
            float[] pitch = new float[count];
            byte[] flags = new byte[count];
            for (int i = 0; i < count; i++) {
                dx[i] = in.readFloat();
                dz[i] = in.readFloat();
                yaw[i] = in.readFloat();
                pitch[i] = in.readFloat();
                flags[i] = in.readByte();
            }
            return new MovementClip(tag.getLong("Time"), tag.getFloat("Score"), dx, dz, yaw, pitch, flags);
        }
    }
}
