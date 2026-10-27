package com.ussm.examplemod.network;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class ModEvents {
    private static final int VOID_DAMAGE_DEPTH = 64;
    private static final double FALLBACK_ARRIVAL_Y = 120;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive()) {
            return;
        }
        ServerLevel level = player.serverLevel();
        double y = player.getY();
        if (y < level.getMinBuildHeight() - VOID_DAMAGE_DEPTH) {
            if (!isUndervoid(level)) {
                tickInVoid(player);
            }
        } else if (y > level.getMaxBuildHeight() && isUndervoid(level)) {
            leaveUndervoid(player);
        } else if (player.hasData(ModAttachments.VOID_TICKS)) {
            player.removeData(ModAttachments.VOID_TICKS);
        }
    }

    private static boolean isUndervoid(ServerLevel level) {
        return level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
    }

    private static void tickInVoid(ServerPlayer player) {
        int ticks = player.getData(ModAttachments.VOID_TICKS) + 1;
        if (ticks >= Config.VOID_SURVIVAL_TICKS.get() || player.isCreative() || player.isSpectator()) {
            enterUndervoid(player);
        } else {
            player.setData(ModAttachments.VOID_TICKS, ticks);
        }
    }

    private static void enterUndervoid(ServerPlayer player) {
        ServerLevel undervoid = player.server.getLevel(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
        if (undervoid == null) {
            return;
        }
        player.removeData(ModAttachments.VOID_TICKS);

        int x = player.getBlockX();
        int z = player.getBlockZ();
        undervoid.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        int surface = undervoid.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        double y = surface > undervoid.getMinBuildHeight() ? surface : FALLBACK_ARRIVAL_Y;

        player.setCamera(player);
        player.stopRiding();
        player.changeDimension(new DimensionTransition(undervoid, new Vec3(x + 0.5, y, z + 0.5), Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
        player.resetFallDistance();
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100));
    }

    private static void leaveUndervoid(ServerPlayer player) {
        DimensionTransition transition = player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
        player.setCamera(player);
        player.stopRiding();
        player.changeDimension(transition);
        player.resetFallDistance();
    }
}
