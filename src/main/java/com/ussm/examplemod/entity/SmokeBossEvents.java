package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.SmokeBossEntity;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class SmokeBossEvents {
    private static final String NEXT_KEY = "examplemod_smoke_boss_next";

    private static final int CHECK_INTERVAL = 600;        // une vérification toutes les 30 secondes
    private static final float CHANCE = 1.0f / 60.0f;     // ~30 minutes en moyenne
    private static final long COOLDOWN = 24000L;          // 20 minutes minimum entre deux apparitions
    private static final double MIN_DISTANCE = 22.0;
    private static final double MAX_DISTANCE = 34.0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive()) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        long now = level.getGameTime();
        if (now < data.getLong(NEXT_KEY)) {
            return;
        }
        if (level.random.nextFloat() >= CHANCE) {
            return;
        }
        // Un seul boss à la fois autour du joueur
        if (!level.getEntitiesOfClass(SmokeBossEntity.class, player.getBoundingBox().inflate(160.0)).isEmpty()) {
            return;
        }

        BlockPos spot = findSpot(level, player);
        if (spot == null) {
            return;
        }
        SmokeBossEntity boss = ModEntities.SMOKE_BOSS.get().create(level);
        if (boss == null) {
            return;
        }
        boss.moveTo(spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5,
                level.random.nextFloat() * 360.0f, 0.0f);
        level.addFreshEntity(boss);
        data.putLong(NEXT_KEY, now + COOLDOWN);
    }

    private static BlockPos findSpot(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 15; attempt++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double distance = MIN_DISTANCE + level.random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
            int playerY = player.blockPosition().getY();

            for (int y = playerY + 8; y > playerY - 8; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (!level.hasChunkAt(pos)) {
                    break;
                }
                BlockPos below = pos.below();
                // 3 blocs d'air au-dessus du sol : le boss fait plus de 2 blocs de haut
                if (level.getBlockState(pos).isAir()
                        && level.getBlockState(pos.above()).isAir()
                        && level.getBlockState(pos.above(2)).isAir()
                        && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                    return pos;
                }
            }
        }
        return null;
    }
}