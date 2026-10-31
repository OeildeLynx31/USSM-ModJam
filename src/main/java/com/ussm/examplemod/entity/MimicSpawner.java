package com.ussm.examplemod.entity;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.entity.mimic.MimicMemory;
import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class MimicSpawner {
    public static void tick(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (player.isCreative() || player.isSpectator() || !player.isAlive()) return;
        if (!level.getServer().isSpawningMonsters() || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || !level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            return;
        }

        double threshold = Config.MIMIC_SANITY_THRESHOLD.get();
        double sanity = Sanity.get(player);
        if (sanity >= threshold) return;

        // plus la santé mentale est basse plus y'a de chance d'en croiser
        double fear = (threshold - sanity) / threshold;
        if (player.getRandom().nextDouble() >= Config.MIMIC_CHANCE.get() * fear * fear) {
            return;
        }
        if (isGloom(level.getBiome(player.blockPosition()))) {
            List<Mimic> mimics = level.getEntitiesOfClass(Mimic.class, player.getBoundingBox().inflate(48), mimic -> mimic.distanceToSqr(player) < 48 * 48);
            if (mimics.isEmpty()) {
                spawnNear(player);
            }
        }
    }

    public static Mimic spawnNear(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();

        BlockPos pos = null;
        for (int i = 0; i < 12 && pos == null; i++) {
            // derriere le joueur
            float angle = (player.getYRot() + 180.0F + (random.nextFloat() - 0.5F) * 200.0F) * Mth.DEG_TO_RAD;
            //double distance = Mth.nextDouble(random, 16, 24);
            double distance = Mth.nextDouble(random, 24, 36);
            pos = findGround(level, Mth.floor(player.getX() - Mth.sin(angle) * distance), player.getBlockY(), Mth.floor(player.getZ() + Mth.cos(angle) * distance));
        }
        if (pos == null) return null;

        Mimic mimic = ModEntities.MIMIC.get().create(level);
        if (mimic == null) return null;

        double d0 = player.getX() - (pos.getX() + 0.5);
        double d1 = player.getZ() - (pos.getZ() + 0.5);
        float yaw = (float) (Mth.atan2(d1, d0) * Mth.RAD_TO_DEG) - 90.0F;
        mimic.moveTo(pos, yaw, 0.0F);
        mimic.setYHeadRot(yaw);
        mimic.setYBodyRot(yaw);

        MimicMemory.Known source = pickSource(level.getServer(), player, random);
        if(source == null) {
            source = MimicMemory.get(level.getServer()).update(player);
        }
        mimic.copy(source);

        net.neoforged.neoforge.event.EventHooks.finalizeMobSpawn(mimic, level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
        if (level.addFreshEntity(mimic)) {
            return mimic;
        }
        return null;
    }

    public static MimicMemory.Known pickSource(MinecraftServer server, ServerPlayer target, RandomSource random) {
        MimicMemory memory = MimicMemory.get(server);
        MimicMemory.Known self = target == null ? null : memory.get(target.getUUID());
        if (self != null && random.nextDouble() < Config.MIMIC_COPY_SELF_CHANCE.get()) {
            return self;
        }

        List<MimicMemory.Known> list = new ArrayList<>();
        for (MimicMemory.Known known : memory.all()) {
            if (known == self) continue;
            ServerPlayer online = server.getPlayerList().getPlayer(known.profile().id());
            if (online != null && target != null && online.level() == target.level() && online.distanceToSqr(target) < 48 * 48) continue;
            list.add(known);
        }
        if (list.isEmpty()) return self;

        double total = 0;
        for (MimicMemory.Known known : list) {
            total += getWeight(server, known, target);
        }
        double r = random.nextDouble() * total;
        for (MimicMemory.Known known : list) {
            r -= getWeight(server, known, target);
            if (r <= 0) return known;
        }
        return list.get(list.size() - 1);
    }

    //TODO equilibrer les poids, au pif pour l'instant
    private static double getWeight(MinecraftServer server, MimicMemory.Known known, ServerPlayer target) {
        double weight = 1;
        if (known.voiceClips() > 0) weight = 3; // ceux qui ont des clips de voix en priorité
        if (server.getPlayerList().getPlayer(known.profile().id()) == null) {
            weight *= 1.5;
        } else if (target != null) {
            weight *= 2;
        }
        return weight;
    }

    private static BlockPos findGround(ServerLevel level, int x, int y, int z) {
        if (!level.isLoaded(new BlockPos(x, y, z))) return null;

        int bottom = Math.max(y - 10, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
        for (int i = y + 6; i >= bottom; i--) {
            BlockPos pos = new BlockPos(x, i, z);
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) continue;

            if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty() && level.getBlockState(pos).getFluidState().isEmpty()
                    && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty() && level.getBlockState(pos.above()).getFluidState().isEmpty()
                    && isGloom(level.getBiome(pos))) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isGloom(Holder<Biome> biome) {
        return biome.is(ModBiomes.GLOOM) || biome.is(ModBiomes.MIASMA);
    }
}
