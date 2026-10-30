package com.ussm.examplemod.entity;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.entity.mimic.MimicMemory;
import com.ussm.examplemod.sanity.Sanity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import com.ussm.examplemod.ExampleMod;

import java.util.ArrayList;
import java.util.List;

public class MimicSpawner {
    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator() || !player.isAlive()) return;

        ServerLevel level = player.serverLevel();
        if (!level.getServer().isSpawningMonsters() || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        if (!level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

        double threshold = Config.MIMIC_SANITY_THRESHOLD.get();
        double sanity = Sanity.get(player);
        if (sanity >= threshold) {
            return;
        }

        double fear = (threshold - sanity) / threshold;
        // plus la santé mentale est basse plus y'a de chance d'en croiser
        if (player.getRandom().nextDouble() >= Config.MIMIC_CHANCE.get() * fear * fear) {
            return;
        }
        if (!isGloom(level.getBiome(player.blockPosition()))) return;

        List<Mimic> mimics = level.getEntitiesOfClass(
                Mimic.class,
                player.getBoundingBox().inflate(48),
                mimic -> mimic.distanceToSqr(player) < 48 * 48
        );
        if (mimics.isEmpty()) {
            spawnNear(player);
        }
    }

    @Nullable
    public static Mimic spawnNear(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();

        // plutot dans le dos, assez loin pour pas le voir arriver meme avec le masque (brouillard a 24)
        BlockPos pos = null;
        for (int i = 0; i < 12; i++) {
            float angle = (player.getYRot() + 180.0F + (random.nextFloat() - 0.5F) * 200.0F) * Mth.DEG_TO_RAD;
            double distance = Mth.nextDouble(random, 24, 36);
            int x = Mth.floor(player.getX() - Mth.sin(angle) * distance);
            int z = Mth.floor(player.getZ() + Mth.cos(angle) * distance);
            pos = findGround(level, x, player.getBlockY(), z);
            if (pos != null) break;
        }
        if (pos == null) {
            return null;
        }

        Mimic mimic = ModEntities.MIMIC.get().create(level);
        if (mimic == null) return null;

        double dx = player.getX() - (pos.getX() + 0.5);
        double dz = player.getZ() - (pos.getZ() + 0.5);
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        mimic.moveTo(pos, yaw, 0.0F);
        mimic.setYHeadRot(yaw);
        mimic.setYBodyRot(yaw);

        MimicMemory.Known source = pickSource(level.getServer(), player, random);
        mimic.copy(source != null ? source : MimicMemory.get(level.getServer()).update(player));

        EventHooks.finalizeMobSpawn(mimic, level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
        if (level.addFreshEntity(mimic)) {
            //ExampleMod.LOGGER.info("mimic spawn en {} ({})", pos, mimic.getCopiedName());
            return mimic;
        }
        return null;
    }


    // qui imiter ? jamais quelqu'un qui est juste a coté (ca se verrait), plutot quelqu'un de deco ou loin,
    // de preference quelqu'un dont on a la voix, et des fois toi meme
    @Nullable
    public static MimicMemory.Known pickSource(MinecraftServer server, @Nullable ServerPlayer target, RandomSource random) {
        MimicMemory memory = MimicMemory.get(server);
        MimicMemory.Known self = target == null ? null : memory.get(target.getUUID());
        if (self != null && random.nextDouble() < Config.MIMIC_COPY_SELF_CHANCE.get()) {
            return self;
        }
        List<MimicMemory.Known> candidates = new ArrayList<>();
        List<Double> weights = new ArrayList<>();
        double total = 0;
        for (MimicMemory.Known known : memory.all()) {
            if (known == self) {
                continue;
            }
            double weight = known.voiceClips() > 0 ? 3.0 : 1.0;
            ServerPlayer online = server.getPlayerList().getPlayer(known.profile().id());
            if (online != null && target != null) {
                if (online.level() == target.level() && online.distanceToSqr(target) < 48 * 48) {
                    continue;
                }
                weight *= 2.0;
            } else if (online == null) {
                weight *= 1.5;
            }
            candidates.add(known);
            weights.add(weight);
            total += weight;
        }
        if (candidates.isEmpty()) {
            return self;
        }
        double roll = random.nextDouble() * total;
        for (int i = 0; i < candidates.size(); i++) {
            roll -= weights.get(i);
            if (roll <= 0) {
                return candidates.get(i);
            }
        }
        return candidates.get(candidates.size() - 1);
    }

    // <-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_->
    @Nullable
    private static BlockPos findGround(ServerLevel level, int x, int y, int z) {
        if (!level.isLoaded(new BlockPos(x, y, z))) {
            return null;
        }

        int bottom = Math.max(y - 10, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
        // on cherche un sol a peu pres a la hauteur du joueur, en surface seulement (pas dans les caves)
        for (int i = y + 6; i >= bottom; i--) {
            BlockPos pos = new BlockPos(x, i, z);
            BlockPos below = pos.below();
            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)
                    && isEmpty(level, pos) && isEmpty(level, pos.above()) && isGloom(level.getBiome(pos))) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isEmpty(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getCollisionShape(level, pos).isEmpty() && state.getFluidState().isEmpty();
    }

    private static boolean isGloom(Holder<Biome> biome) {
        return biome.is(ModBiomes.GLOOM) || biome.is(ModBiomes.MIASMA);
    }
}
