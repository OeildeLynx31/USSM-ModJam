package com.ussm.examplemod.network;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive()) return;

        ServerLevel serverLevel = player.serverLevel();

        // dans le vide de l'end : faut tenir 10s pour passer dans l'entre-monde
        if (player.getY() < serverLevel.getMinBuildHeight() - 64) {
            if (serverLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

            int voidTicks = player.getData(ModAttachments.VOID_TICKS) + 1;
            //ExampleMod.LOGGER.info("void ticks {}", voidTicks);
            if (voidTicks >= Config.VOID_SURVIVAL_TICKS.get() || player.isCreative() || player.isSpectator()) {
                sendToUndervoid(player);
            } else {
                player.setData(ModAttachments.VOID_TICKS, voidTicks);
            }
        } else if (player.getY() > serverLevel.getMaxBuildHeight()
                && serverLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            teleportPlayer(player, player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING));
        } else if (player.hasData(ModAttachments.VOID_TICKS)) {
            player.removeData(ModAttachments.VOID_TICKS);
        }
    }

    private static final int APPLE_WINDOW = 20 * 60; // les pommes comptent pendant 1 min

    // 3 pommes dorees avant de sauter et le vide fait 4x moins mal
    @SubscribeEvent
    public static void onEatApple(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getItem().is(Items.GOLDEN_APPLE)) return;

        long time = player.level().getGameTime();
        int apples = player.getData(ModAttachments.VOID_APPLES);
        if (time - player.getData(ModAttachments.VOID_APPLE_TIME) > APPLE_WINDOW) apples = 0;
        player.setData(ModAttachments.VOID_APPLES, apples + 1);
        player.setData(ModAttachments.VOID_APPLE_TIME, time);
    }

    @SubscribeEvent
    public static void onVoidDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) return;
        if (player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

        boolean recent = player.level().getGameTime() - player.getData(ModAttachments.VOID_APPLE_TIME) <= APPLE_WINDOW;
        if (recent && player.getData(ModAttachments.VOID_APPLES) >= Config.VOID_GOLDEN_APPLES.get()) {
            event.setAmount((float) (event.getAmount() * Config.VOID_APPLE_DAMAGE.get()));
        }
    }

    private static void sendToUndervoid(ServerPlayer player) {
        ServerLevel undervoidLevel = player.server.getLevel(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
        if (undervoidLevel == null) return;

        player.removeData(ModAttachments.VOID_TICKS);
        player.removeData(ModAttachments.VOID_APPLES); // faut en remanger pour le prochain saut

        int x = player.getBlockX();
        int z = player.getBlockZ();
        undervoidLevel.getChunk(x >> 4, z >> 4);
        int y = undervoidLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        if (y <= undervoidLevel.getMinBuildHeight()) {
            y = 120;
        }

        teleportPlayer(player, new DimensionTransition(undervoidLevel, new Vec3(x + 0.5, y, z + 0.5), Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100));
    }

    private static void teleportPlayer(ServerPlayer player, DimensionTransition transition) {
        player.setCamera(player);
        player.stopRiding();
        player.changeDimension(transition);
        player.resetFallDistance();
    }
}
