package com.ussm.the_undervoid.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanityTotemBlockEntity extends BlockEntity {
    // dernier usage de chaque joueur, pas sauvegardé : au redemarrage tout le monde peut réutiliser
    private final Map<UUID, Long> lastUse = new HashMap<>();

    public SanityTotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SANITY_TOTEM.get(), pos, state);
    }

    // secondes restantes avant la prochaine utilisation, 0 si dispo
    public int secondsLeft(UUID player, long now, int cooldownTicks) {
        Long last = lastUse.get(player);
        if (last == null) return 0;
        long left = cooldownTicks - (now - last);
        return left <= 0 ? 0 : (int) Math.ceil(left / 20.0);
    }

    public void markUsed(UUID player, long now) {
        lastUse.put(player, now);
    }
}
