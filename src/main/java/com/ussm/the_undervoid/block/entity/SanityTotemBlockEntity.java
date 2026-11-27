package com.ussm.the_undervoid.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// la charge est la meme pour tout le monde : elle remonte toute seule apres un usage
public class SanityTotemBlockEntity extends BlockEntity {
    private long lastUse = -1; // -1 = jamais utilisé, donc chargé
    private int duration = 1; // ticks pour se recharger en entier

    public SanityTotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANITY_TOTEM.get(), pos, state);
    }

    // 0 = vide, 1 = les 3 tetes sont allumées
    public float charge(float now) {
        if (lastUse < 0) return 1f;
        return Mth.clamp((now - lastUse) / duration, 0f, 1f);
    }

    public int secondsLeft(long now) {
        return (int) Math.ceil((1f - charge(now)) * duration / 20.0);
    }

    public void use(long now, int cooldownTicks) {
        lastUse = now;
        duration = Math.max(1, cooldownTicks);
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("LastUse", lastUse);
        tag.putInt("Duration", duration);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lastUse = tag.contains("LastUse") ? tag.getLong("LastUse") : -1;
        duration = Math.max(1, tag.getInt("Duration"));
    }

    // pour que le client voit la charge
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
