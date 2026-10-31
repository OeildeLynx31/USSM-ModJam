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
import java.util.*;

public class MimicMemory extends SavedData {
    private final Map<UUID, Known> known = new HashMap<>();

    public static MimicMemory create() {
        return new MimicMemory();
    }

    public static MimicMemory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>(MimicMemory::create, MimicMemory::load), "examplemod_mimics");
    }

    @Nullable
    public Known get(UUID id) {
        return known.get(id);
    }

    public Collection<Known> all() {
        return known.values();
    }

    // appele au login/logout + toutes les 30s
    public Known update(ServerPlayer player) {
        MimicProfile profile = MimicProfile.of(player);
        Known entry = known.get(player.getUUID());
        if(entry == null) {
            entry = new Known(profile);
            known.put(player.getUUID(), entry);
            setDirty();
        } else if(!entry.profile.equals(profile)) {
            entry.profile = profile;
            setDirty();
        }

        for(EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if(!ItemStack.matches(stack, entry.equipment[slot.ordinal()])) {
                entry.equipment[slot.ordinal()] = stack.copy();
                setDirty();
            }
        }
        entry.lastSeen = System.currentTimeMillis();
        return entry;
    }

    // meme principe que VoiceBank : si c'est plein on vire le moins bon
    public void offerMove(UUID id, MovementClip clip, int max) {
        Known entry = known.get(id);
        if(entry == null) return;

        long now = System.currentTimeMillis();
        List<MovementClip> moves = entry.moves;
        if(moves.size() >= max) {
            int worst = 0;
            for(int i = 1; i < moves.size(); i++) {
                if(moves.get(i).effectiveScore(now) < moves.get(worst).effectiveScore(now)) worst = i;
            }
            if(moves.get(worst).effectiveScore(now) >= clip.effectiveScore(now)) return;
            moves.remove(worst);
        }
        moves.add(clip);
        setDirty();
    }

    @Nullable
    public MovementClip pickMove(UUID id, RandomSource random) {
        Known entry = known.get(id);
        if(entry == null || entry.moves.isEmpty()) {
            return null;
        }
        List<MovementClip> moves = entry.moves;
        long now = System.currentTimeMillis();
        double total = 0;
        for(MovementClip clip : moves) total += clip.effectiveScore(now);

        double r = random.nextDouble() * total;
        for(int i = 0; i < moves.size() - 1; i++) {
            r -= moves.get(i).effectiveScore(now);
            if(r <= 0) return moves.get(i);
        }
        return moves.get(moves.size() - 1);
    }

    public void setVoiceClips(UUID id, int count) {
        Known k = known.get(id);
        if(k != null && k.voiceClips != count) {
            k.voiceClips = count;
            setDirty();
        }
    }


    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag players = new ListTag();
        for(Known k : known.values()) {
            CompoundTag playerTag = k.profile.save();

            ListTag equipment = new ListTag();
            for(EquipmentSlot slot : EquipmentSlot.values()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putString("Slot", slot.getName());
                slotTag.put("Item", k.equipment[slot.ordinal()].saveOptional(registries));
                equipment.add(slotTag);
            }
            playerTag.put("Equipment", equipment);

            ListTag moves = new ListTag();
            for(MovementClip move : k.moves) moves.add(move.save());
            playerTag.put("Moves", moves);

            playerTag.putLong("LastSeen", k.lastSeen);
            playerTag.putInt("VoiceClips", k.voiceClips);
            players.add(playerTag);
        }
        tag.put("Players", players);
        return tag;
    }

    public static MimicMemory load(CompoundTag tag, HolderLookup.Provider registries) {
        MimicMemory memory = create();
        ListTag players = tag.getList("Players", 10);
        for(int i = 0; i < players.size(); i++) {
            CompoundTag playerTag = players.getCompound(i);
            Known entry = new Known(MimicProfile.load(playerTag));

            ListTag equipment = playerTag.getList("Equipment", 10);
            for(int j = 0; j < equipment.size(); j++) {
                CompoundTag slotTag = equipment.getCompound(j);
                String name = slotTag.getString("Slot");
                for(EquipmentSlot slot : EquipmentSlot.values()) {
                    if(slot.getName().equals(name)) {
                        entry.equipment[slot.ordinal()] = ItemStack.parseOptional(registries, slotTag.getCompound("Item"));
                    }
                }
            }

            for(Tag t : playerTag.getList("Moves", Tag.TAG_COMPOUND)) {
                try {
                    entry.moves.add(MovementClip.load((CompoundTag) t));
                } catch(IOException e) {
                    ExampleMod.LOGGER.warn("deplacement illisible pour {}", entry.profile.name());
                }
            }
            entry.lastSeen = playerTag.getLong("LastSeen");
            entry.voiceClips = playerTag.getInt("VoiceClips");
            memory.known.put(entry.profile.id(), entry);
        }
        return memory;
    }

    public static class Known {
        MimicProfile profile;
        final ItemStack[] equipment = new ItemStack[EquipmentSlot.values().length];
        final List<MovementClip> moves = new ArrayList<>();
        private long lastSeen; // TODO s'en servir pour oublier les joueurs qui reviennent jamais
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
