package com.ussm.examplemod.block.custom;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class BedrockBeam {
    private static final List<BedrockBeam> BEAMS = new ArrayList<>();

    private final ServerLevel level;
    private final BlockPos origin;
    private int reach;
    private int linger = 20;

    private BedrockBeam(ServerLevel level, BlockPos origin) {
        this.level = level;
        this.origin = origin;
    }

    public static void start(ServerLevel level, BlockPos origin) {
        BEAMS.add(new BedrockBeam(level, origin));
        level.playSound(null, origin, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 5f, 0.5f);
        level.playSound(null, origin, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 5f, 0.6f);
        level.sendParticles(ParticleTypes.FLASH, origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5, 1, 0, 0, 0, 0);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!BEAMS.isEmpty() && event.getLevel() instanceof ServerLevel level) {
            BEAMS.removeIf(beam -> beam.level == level && !beam.tick());
        }
    }

    private boolean tick() {
        int up = level.getMaxBuildHeight() - 1 - origin.getY();
        int down = origin.getY() - level.getMinBuildHeight();
        if (reach <= Math.max(up, down)) {
            // 3 couches par tick de chaque coté, ca lag pas et ca se voit avancer
            for (int i = 0; i < 3 && reach <= Math.max(up, down); i++, reach++) {
                if (reach <= up) {
                    carve(origin.getY() + reach);
                }
                if (reach > 0 && reach <= down) {
                    carve(origin.getY() - reach);
                }
            }
        } else if (--linger <= 0) {
            return false;
        }
        for (int dy = -24; dy <= 24; dy += 2) {
            particles(ParticleTypes.END_ROD, origin.getY() + dy + level.random.nextDouble() * 2, 0.4);
        }
        return true;
    }

    private void carve(int y) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                pos.set(origin.getX() + dx, y, origin.getZ() + dz);
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && !state.is(Blocks.BARRIER) && !(state.getBlock() instanceof GameMasterBlock)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        particles(ParticleTypes.ELECTRIC_SPARK, y + 0.5, 1.2);
    }

    private void particles(ParticleOptions type, double y, double spread) {
        double x = origin.getX() + 0.5;
        double z = origin.getZ() + 0.5;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(x, player.getY(), z) < 128 * 128) {
                level.sendParticles(player, type, true, x, y, z, 2, spread, 0.3, spread, 0.02);
            }
        }
    }
}
