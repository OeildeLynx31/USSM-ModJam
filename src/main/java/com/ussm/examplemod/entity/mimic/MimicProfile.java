package com.ussm.examplemod.entity.mimic;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;

import java.util.UUID;

public record MimicProfile(UUID id, String name, String textures, String signature, boolean leftHanded) {

    public static MimicProfile of(ServerPlayer player) {
        GameProfile profile = player.getGameProfile();
        String textures = "";
        String signature = "";
        for (Property p : profile.getProperties().get("textures")) {
            textures = p.value();
            if (p.signature() != null) signature = p.signature();
            break;
        }
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
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("Id", id);
        nbt.putString("Name", name);
        nbt.putString("Textures", textures);
        nbt.putString("Signature", signature);
        nbt.putBoolean("LeftHanded", leftHanded);
        return nbt;
    }

    public static MimicProfile load(CompoundTag nbt) {
        return new MimicProfile(
                nbt.getUUID("Id"),
                nbt.getString("Name"),
                nbt.getString("Textures"),
                nbt.getString("Signature"),
                nbt.getBoolean("LeftHanded")
        );
    }
}