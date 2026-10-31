package com.ussm.examplemod.entity.mimic.voice;

import net.minecraft.nbt.CompoundTag;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VoiceClip {
    public static final int FRAME_MILLIS = 20;

    public final long id;
    public final long createdAt;
    public final float score;
    public final Set<String> tags;
    public final boolean whisper;
    public final List<byte[]> frames;

    public VoiceClip(long id, long createdAt, float score, Set<String> tags, boolean whisper, List<byte[]> frames) {
        this.id = id;
        this.createdAt = createdAt;
        this.score = score;
        this.tags = tags;
        this.whisper = whisper;
        this.frames = frames;
    }

    public int durationMillis() {
        return frames.size() * FRAME_MILLIS;
    }

    // pareil que MovementClip mais un peu plus fort
    public float effectiveScore(long now) {
        double hours = Math.max(0, now - createdAt) / 3600000.0;
        return score + 0.6f * (float) Math.exp(-hours / 24.0);
    }

    public VoiceClip withBonus(float bonus, String tag) {
        Set<String> copy = new LinkedHashSet<>(tags);
        copy.add(tag);
        return new VoiceClip(id, createdAt, score + bonus, copy, whisper, frames);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Id", id);
        tag.putLong("Time", createdAt);
        tag.putFloat("Score", score);
        tag.putString("Tags", String.join(",", tags));
        tag.putBoolean("Whisper", whisper);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(frames.size());
            for (byte[] frame : frames) {
                dos.writeShort(frame.length);
                dos.write(frame);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tag.putByteArray("Frames", baos.toByteArray());
        return tag;
    }

    public static VoiceClip load(CompoundTag tag) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(tag.getByteArray("Frames")));
        int n = in.readInt();
        List<byte[]> frames = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            byte[] frame = new byte[in.readUnsignedShort()];
            in.readFully(frame);
            frames.add(frame);
        }

        Set<String> tags = new LinkedHashSet<>();
        for(String s : tag.getString("Tags").split(",")) {
            if(!s.isBlank()) tags.add(s);
        }
        return new VoiceClip(tag.getLong("Id"), tag.getLong("Time"), tag.getFloat("Score"), tags, tag.getBoolean("Whisper"), frames);
    }
}
