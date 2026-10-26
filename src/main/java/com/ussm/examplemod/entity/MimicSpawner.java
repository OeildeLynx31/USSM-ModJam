package com.ussm.examplemod.entity;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MimicSpawner {
    private static final int ATTEMPTS = 8;
    private static final double MIN_DISTANCE = 16.0;
    private static final double MAX_DISTANCE = 28.0;
    private static final int SCAN_UP = 6;
    private static final int SCAN_DOWN = 10;
    private static final double SPACING = 48.0;
    private static final double SOURCE_RANGE_SQR = 64.0 * 64.0;

    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator() || !player.isAlive()) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (!level.getServer().isSpawningMonsters() || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || !level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            return;
        }
        double threshold = Config.MIMIC_SANITY_THRESHOLD.get();
        double sanity = Sanity.get(player);
        if (sanity >= threshold) {
            return;
        }
        double fear = (threshold - sanity) / threshold;
        if (player.getRandom().nextDouble() >= Config.MIMIC_CHANCE.get() * fear * fear) {
            return;
        }
        if (!isGloom(level.getBiome(player.blockPosition())) || hasMimicNearby(level, player)) {
            return;
        }
        spawnNear(player);
    }

    @Nullable
    public static Mimic spawnNear(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();
        int y = player.getBlockY();
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double distance = Mth.nextDouble(random, MIN_DISTANCE, MAX_DISTANCE);
            int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
            BlockPos pos = findSpot(level, x, y, z);
            if (pos != null) {
                return spawn(level, player, pos);
            }
        }
        return null;
    }

    private static boolean isGloom(Holder<Biome> biome) {
        return biome.is(ModBiomes.GLOOM) || biome.is(ModBiomes.MIASMA);
    }

    private static boolean hasMimicNearby(ServerLevel level, ServerPlayer player) {
        return !level.getEntitiesOfClass(Mimic.class, player.getBoundingBox().inflate(SPACING),
                mimic -> mimic.distanceToSqr(player) < SPACING * SPACING).isEmpty();
    }

    @Nullable
    private static BlockPos findSpot(ServerLevel level, int x, int y, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        if (!level.isLoaded(pos)) {
            return null;
        }
        int top = y + SCAN_UP;
        int bottom = Math.max(y - SCAN_DOWN, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
        boolean headFree = isFree(level, pos.setY(top + 1));
        boolean feetFree = isFree(level, pos.setY(top));
        for (int feet = top; feet >= bottom; feet--) {
            BlockState ground = level.getBlockState(pos.setY(feet - 1));
            if (headFree && feetFree && ground.isFaceSturdy(level, pos, Direction.UP)
                    && isGloom(level.getBiome(pos.setY(feet)))) {
                return pos.immutable();
            }
            headFree = feetFree;
            feetFree = isFree(level, pos.setY(feet - 1), ground);
        }
        return null;
    }

    private static boolean isFree(BlockGetter level, BlockPos pos) {
        return isFree(level, pos, level.getBlockState(pos));
    }

    private static boolean isFree(BlockGetter level, BlockPos pos, BlockState state) {
        return state.getCollisionShape(level, pos).isEmpty() && state.getFluidState().isEmpty();
    }

    @Nullable
    private static Mimic spawn(ServerLevel level, ServerPlayer player, BlockPos pos) {
        Mimic mimic = ModEntities.MIMIC.get().create(level);
        if (mimic == null) {
            return null;
        }
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;
        float yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * Mth.RAD_TO_DEG) - 90f;
        mimic.moveTo(x, pos.getY(), z, yaw, 0f);
        mimic.setYHeadRot(yaw);
        mimic.setYBodyRot(yaw);
        mimic.copy(sourceFor(player));
        EventHooks.finalizeMobSpawn(mimic, level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
        return level.addFreshEntity(mimic) ? mimic : null;
    }

    private static Player sourceFor(ServerPlayer player) {
        List<ServerPlayer> players = player.serverLevel().players();
        Player source = player;
        double best = SOURCE_RANGE_SQR;
        for (int i = 0; i < players.size(); i++) {
            ServerPlayer other = players.get(i);
            if (other == player || !other.isAlive() || other.isSpectator()) {
                continue;
            }
            double distance = other.distanceToSqr(player);
            if (distance < best) {
                source = other;
                best = distance;
            }
        }
        return source;
    }
}
