package com.ussm.examplemod.entity.mimic.voice;

import net.minecraft.nbt.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// une phrase telle que simple voice chat nous l'envoie : des paquets opus de 20ms, gardés tels quels
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
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
        this.whisper = whisper;
        this.frames = Collections.unmodifiableList(frames);
    }

    public int durationMillis() {
        return frames.size() * FRAME_MILLIS;
    }

    public float effectiveScore(long now) {
        double hours = Math.max(0, now - createdAt) / 3_600_000.0;
        return score + 0.6f * (float) Math.exp(-hours / 24.0);
    }

    public VoiceClip withBonus(float bonus, String tag) {
        Set<String> newTags = new LinkedHashSet<>(tags);
        newTags.add(tag);
        return new VoiceClip(id, createdAt, score + bonus, newTags, whisper, frames);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Id", id);
        tag.putLong("Time", createdAt);
        tag.putFloat("Score", score);
        tag.putString("Tags", String.join(",", tags));
        tag.putBoolean("Whisper", whisper);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(frames.size());
            for (byte[] frame : frames) {
                out.writeShort(frame.length);
                out.write(frame);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        tag.putByteArray("Frames", bytes.toByteArray());
        return tag;
    }

    public static VoiceClip load(CompoundTag tag) throws IOException {
        List<byte[]> frames;
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(tag.getByteArray("Frames")))) {
            int count = in.readInt();
            frames = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                byte[] frame = new byte[in.readUnsignedShort()];
                in.readFully(frame);
                frames.add(frame);
            }
        }
        Set<String> tags = new LinkedHashSet<>();
        for (String t : tag.getString("Tags").split(",")) {
            if (!t.isBlank()) {
                tags.add(t);
            }
        }
        return new VoiceClip(tag.getLong("Id"), tag.getLong("Time"), tag.getFloat("Score"), tags, tag.getBoolean("Whisper"), frames);
    }
}
