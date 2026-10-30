package com.ussm.examplemod.entity.mimic;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;

import java.util.UUID;

// ce qu'il faut pour ressembler a un joueur meme quand il est pas la : pseudo + la propriété "textures" (skin et cape)
public record MimicProfile(UUID id, String name, String textures, String signature, boolean leftHanded) {

    public static MimicProfile of(ServerPlayer player) {
        GameProfile profile = player.getGameProfile();
        Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
        String textures = property == null ? "" : property.value();
        String signature = property == null || property.signature() == null ? "" : property.signature();
        return new MimicProfile(profile.getId(), profile.getName(), textures, signature, player.getMainArm() == HumanoidArm.LEFT);
    }

    public GameProfile toGameProfile() {
        GameProfile profile = new GameProfile(id, name);
        if (!textures.isEmpty()) {
            profile.getProperties().put("textures", new Property("textures", textures, signature.isEmpty() ? null : signature));
        }
        return profile;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", id);
        tag.putString("Name", name);
        tag.putString("Textures", textures);
        tag.putString("Signature", signature);
        tag.putBoolean("LeftHanded", leftHanded);
        return tag;
    }

    public static MimicProfile load(CompoundTag tag) {
        return new MimicProfile(tag.getUUID("Id"), tag.getString("Name"), tag.getString("Textures"),
                tag.getString("Signature"), tag.getBoolean("LeftHanded"));
    }
}
