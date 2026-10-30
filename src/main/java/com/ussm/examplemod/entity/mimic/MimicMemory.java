package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ce que les imitateurs savent des joueurs deja venus (meme deco) : skin, dernier stuff vu, facon de bouger
// sauvegardé dans data/examplemod_mimics.dat de l'overworld
public class MimicMemory extends SavedData {
    private static final String NAME = "examplemod_mimics";
    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    private final Map<UUID, Known> known = new HashMap<>();

    public static MimicMemory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>(MimicMemory::new, MimicMemory::load), NAME);
    }

    @Nullable
    public Known get(UUID id) {
        return known.get(id);
    }

    public Collection<Known> all() {
        return known.values();
    }

    public Known update(ServerPlayer player) {
        MimicProfile profile = MimicProfile.of(player);
        Known entry = known.get(player.getUUID());
        if (entry == null) {
            entry = new Known(profile);
            known.put(player.getUUID(), entry);
            setDirty();
        } else if (!entry.profile.equals(profile)) {
            entry.profile = profile;
            setDirty();
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!ItemStack.matches(stack, entry.equipment[slot.ordinal()])) {
                entry.equipment[slot.ordinal()] = stack.copy();
                setDirty();
            }
        }
        entry.lastSeen = System.currentTimeMillis(); // pas besoin de resauvegarder pour ca
        return entry;
    }

    // on garde les plus vivants, un nouveau remplace le moins bon seulement s'il est mieux
    public void offerMove(UUID id, MovementClip clip, int max) {
        Known entry = known.get(id);
        if (entry == null || max <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (entry.moves.size() >= max) {
            MovementClip worst = Collections.min(entry.moves, Comparator.comparingDouble(c -> c.effectiveScore(now)));
            if (worst.effectiveScore(now) >= clip.effectiveScore(now)) {
                return;
            }
            entry.moves.remove(worst);
        }
        entry.moves.add(clip);
        setDirty();
    }

    @Nullable
    public MovementClip pickMove(UUID id, RandomSource random) {
        Known entry = known.get(id);
        if (entry == null || entry.moves.isEmpty()) {
            return null;
        }
        long now = System.currentTimeMillis();
        double total = 0;
        for (MovementClip clip : entry.moves) {
            total += clip.effectiveScore(now);
        }
        double roll = random.nextDouble() * total;
        for (MovementClip clip : entry.moves) {
            roll -= clip.effectiveScore(now);
            if (roll <= 0) {
                return clip;
            }
        }
        return entry.moves.get(entry.moves.size() - 1);
    }

    public void setVoiceClips(UUID id, int count) {
        Known entry = known.get(id);
        if (entry != null && entry.voiceClips != count) {
            entry.voiceClips = count;
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Known entry : known.values()) {
            CompoundTag player = entry.profile.save();
            ListTag equipment = new ListTag();
            for (EquipmentSlot slot : SLOTS) {
                CompoundTag stack = new CompoundTag();
                stack.putString("Slot", slot.getName());
                stack.put("Item", entry.equipment[slot.ordinal()].saveOptional(registries));
                equipment.add(stack);
            }
            player.put("Equipment", equipment);
            ListTag moves = new ListTag();
            for (MovementClip move : entry.moves) {
                moves.add(move.save());
            }
            player.put("Moves", moves);
            player.putLong("LastSeen", entry.lastSeen);
            player.putInt("VoiceClips", entry.voiceClips);
            list.add(player);
        }
        tag.put("Players", list);
        return tag;
    }

    public static MimicMemory load(CompoundTag tag, HolderLookup.Provider registries) {
        MimicMemory memory = new MimicMemory();
        ListTag list = tag.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag player = list.getCompound(i);
            Known entry = new Known(MimicProfile.load(player));
            ListTag equipment = player.getList("Equipment", Tag.TAG_COMPOUND);
            for (int j = 0; j < equipment.size(); j++) {
                CompoundTag stack = equipment.getCompound(j);
                try {
                    EquipmentSlot slot = EquipmentSlot.byName(stack.getString("Slot"));
                    entry.equipment[slot.ordinal()] = ItemStack.parseOptional(registries, stack.getCompound("Item"));
                } catch (IllegalArgumentException ignored) {
                }
            }
            ListTag moves = player.getList("Moves", Tag.TAG_COMPOUND);
            for (int j = 0; j < moves.size(); j++) {
                try {
                    entry.moves.add(MovementClip.load(moves.getCompound(j)));
                } catch (IOException e) {
                    ExampleMod.LOGGER.warn("deplacement illisible pour {}", entry.profile.name());
                }
            }
            entry.lastSeen = player.getLong("LastSeen");
            entry.voiceClips = player.getInt("VoiceClips");
            memory.known.put(entry.profile.id(), entry);
        }
        return memory;
    }

    public static class Known {
        private MimicProfile profile;
        private final ItemStack[] equipment = new ItemStack[SLOTS.length];
        private final List<MovementClip> moves = new ArrayList<>();
        private long lastSeen;
        private int voiceClips;

        Known(MimicProfile profile) {
            this.profile = profile;
            Arrays.fill(equipment, ItemStack.EMPTY);
        }

        public MimicProfile profile() {
            return profile;
        }

        public ItemStack equipment(EquipmentSlot slot) {
            return equipment[slot.ordinal()].copy();
        }

        public boolean hasMoves() {
            return !moves.isEmpty();
        }

        public int voiceClips() {
            return voiceClips;
        }
    }
}
