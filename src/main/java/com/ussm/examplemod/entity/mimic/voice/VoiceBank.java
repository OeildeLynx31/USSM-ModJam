package com.ussm.examplemod.entity.mimic.voice;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.mimic.MimicMemory;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoiceBank extends SavedData {
    private final List<VoiceClip> clips = new ArrayList<>();

    public static VoiceBank get(MinecraftServer server, UUID player) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>(VoiceBank::new, VoiceBank::load), "examplemod_voice_" + player);
    }

    public List<VoiceClip> clips() {
        return clips;
    }

    public boolean offer(MinecraftServer server, UUID player, VoiceClip clip, int max) {
        if(max <= 0) return false;
        long now = System.currentTimeMillis();
        while(clips.size() >= max) {
            VoiceClip worst = clips.get(0);
            for(VoiceClip c : clips) {
                if(c.effectiveScore(now) < worst.effectiveScore(now)) worst = c;
            }
            if (worst.effectiveScore(now) >= clip.effectiveScore(now)) return false;
            clips.remove(worst);
        }
        clips.add(clip);
        setDirty();
        MimicMemory.get(server).setVoiceClips(player, clips.size());
        return true;
    }

    public void boostRecent(long since, float bonus, String tag) {
        for(int i = 0; i < clips.size(); i++) {
            VoiceClip clip = clips.get(i);
            if(clip.createdAt >= since && !clip.tags.contains(tag)) {
                clips.set(i, clip.withBonus(bonus, tag));
                setDirty();
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (VoiceClip clip : clips) {
            list.add(clip.save());
        }
        tag.put("Clips", list);
        return tag;
    }

    public static VoiceBank load(CompoundTag tag, HolderLookup.Provider registries) {
        VoiceBank bank = new VoiceBank();
        ListTag list = tag.getList("Clips", Tag.TAG_COMPOUND);
        for(int i = 0; i < list.size(); i++) {
            try {
                bank.clips.add(VoiceClip.load(list.getCompound(i)));
            } catch(IOException e) {
                ExampleMod.LOGGER.warn("extrait de voix illisible", e);
            }
        }
        return bank;
    }
}
