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
    private int layer = 0;
    private int timer = 20; // 1 seconde
    private boolean finished = false;

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
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < BEAMS.size(); i++) {
            BedrockBeam beam = BEAMS.get(i);
            if (beam.level == level) {
                beam.tick();
            }
            if (beam.finished) {
                BEAMS.remove(i);
                i--;
            }
        }
    }

    private void tick() {
        int haut = level.getMaxBuildHeight() - 1 - origin.getY();
        int bas = origin.getY() - level.getMinBuildHeight();
        //System.out.println("layer " + layer + " haut=" + haut + " bas=" + bas);

        if (layer <= haut || layer <= bas) {
            for (int i = 0; i < 3; i++) {
                if (layer <= haut) carve(origin.getY() + layer);
                if (layer <= bas && layer > 0) carve(origin.getY() - layer);
                layer++;
            }
        } else {
            timer--;
            if (timer <= 0) {
                finished = true;
                return;
            }
        }

        for (int dy = -24; dy <= 24; dy += 2) {
            particles(ParticleTypes.END_ROD, origin.getY() + dy + level.random.nextDouble() * 2, 0.4);
        }
    }

    //TODO faire des degats aux mobs qui sont dans le laser ?
    private void carve(int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || state.is(Blocks.BARRIER)) continue;
                if (state.getBlock() instanceof GameMasterBlock) continue;
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        particles(ParticleTypes.ELECTRIC_SPARK, y + 0.5, 1.2);
    }

    private void particles(ParticleOptions type, double y, double spread) {
        double x = origin.getX() + 0.5;
        double z = origin.getZ() + 0.5;
        //level.sendParticles(type, x, y, z, 2, spread, 0.3, spread, 0.02);
        for (ServerPlayer player : level.players()) {
            double dx = player.getX() - x;
            double dz = player.getZ() - z;
            if (dx * dx + dz * dz < 128 * 128) {
                level.sendParticles(player, type, true, x, y, z, 2, spread, 0.3, spread, 0.02);
            }
        }
    }
}
