package com.ussm.examplemod.entity.mimic;

import net.minecraft.nbt.CompoundTag;

import java.io.*;

public class MovementClip {
    public static final int SNEAK = 1;
    public static final int SPRINT = 2;
    public static final int JUMP = 4;
    public static final int SWING = 8;
    public static final int USE = 16;

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

    public float dx(int i) { return dx[i]; }
    public float dz(int i) { return dz[i]; }
    public float yaw(int i) { return yaw[i]; }
    public float pitch(int i) { return pitch[i]; }

    public boolean has(int i, int flag) {
        return (flags[i] & flag) != 0;
    }

    public float effectiveScore(long now) {
        double hours = Math.max(0, now - createdAt) / 3600000.0;
        return score + 0.5f * (float) Math.exp(-hours / 24);
    }

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("Time", createdAt);
        nbt.putFloat("Score", score);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(flags.length);
            for (int i = 0; i < flags.length; i++) {
                dos.writeFloat(dx[i]);
                dos.writeFloat(dz[i]);
                dos.writeFloat(yaw[i]);
                dos.writeFloat(pitch[i]);
                dos.writeByte(flags[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nbt.putByteArray("Frames", baos.toByteArray());
        return nbt;
    }

    public static MovementClip load(CompoundTag nbt) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(nbt.getByteArray("Frames")));
        int n = in.readInt();
        float[] dx = new float[n], dz = new float[n], yaw = new float[n], pitch = new float[n];
        byte[] flags = new byte[n];
        for (int i = 0; i < n; i++) {
            dx[i] = in.readFloat();
            dz[i] = in.readFloat();
            yaw[i] = in.readFloat();
            pitch[i] = in.readFloat();
            flags[i] = in.readByte();
        }
        return new MovementClip(nbt.getLong("Time"), nbt.getFloat("Score"), dx, dz, yaw, pitch, flags);
    }
}