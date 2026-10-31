package com.ussm.examplemod.client;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.sanity.Sanity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class Hallucinations {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final double SHADOW_SEEN_DOT = 0.94;

    private static float intensity;
    private static int ticks;
    private static int heartbeatTimer = 200;
    private static int stepsTimer = 600;
    private static int stareTimer = 1200;
    private static int creeperTimer = 1200;
    private static int shadowTimer = 1200;
    private static int stepsLeft;
    private static int stepDelay;

    private static EnderMan shadow;
    private static boolean shadowVisible;
    private static int shadowAge;

    public static float strength() {
        return intensity * Config.HALLUCINATION_INTENSITY.get().floatValue();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            shadow = null;
            shadowVisible = false;
            intensity = 0f;
            return;
        }
        if (shadow != null && shadow.level() != player.level()) {
            shadow = null;
            shadowVisible = false;
        }
        if (minecraft.isPaused()) {
            return;
        }
        ticks++;

        MobEffectInstance effect = player.getEffect(ModEffects.HALLUCINATIONS);
        int level = effect == null ? -1 : effect.getAmplifier();
        float target = level < 0 ? 0f : level == 0 ? 0.5f : 1f;
        intensity += Mth.clamp(target - intensity, -0.02f, 0.02f);

        tickHeartbeat(player, level);
        if (level < 0) {
            shadowVisible = false;
            stepsLeft = 0;
            return;
        }
        tickSteps(player, level);
        tickSounds(player, level);
        if (Config.HALLUCINATION_INTENSITY.get() > 0) {
            tickShadow(player, level);
        } else {
            shadowVisible = false;
        }
    }

    private static void tickHeartbeat(LocalPlayer player, int level) {
        if (player.getData(ModAttachments.SANITY) > Sanity.TROUBLED || --heartbeatTimer > 0) {
            return;
        }
        heartbeatTimer = level > 0 ? between(100, 200) : between(200, 400);
        play(player, SoundEvents.WARDEN_HEARTBEAT, player.position(), 0.3f, 1f);
    }

    private static void tickSteps(LocalPlayer player, int level) {
        if (stepsLeft > 0) {
            if (--stepDelay <= 0) {
                Vec3 behind = behind(player, 2.5);
                BlockPos ground = BlockPos.containing(behind).below();
                SoundEvent step = player.level().getBlockState(ground).getSoundType(player.level(), ground, player).getStepSound();
                play(player, step, behind, 0.4f, 1f);
                stepsLeft--;
                stepDelay = 6;
            }
            return;
        }
        if (--stepsTimer <= 0) {
            stepsLeft = 3;
            stepDelay = 0;
            stepsTimer = level > 0 ? between(200, 400) : between(500, 900);
        }
    }

    private static void tickSounds(LocalPlayer player, int level) {
        if (--stareTimer <= 0) {
            play(player, SoundEvents.ENDERMAN_STARE, behind(player, 4), 0.25f, 1f);
            stareTimer = level > 0 ? between(600, 1200) : between(1200, 2400);
        }
        if (level > 0 && --creeperTimer <= 0) {
            play(player, SoundEvents.CREEPER_PRIMED, behind(player, 2), 0.8f, 0.5f);
            creeperTimer = between(800, 1600);
        }
    }

    private static void tickShadow(LocalPlayer player, int level) {
        if (shadowVisible) {
            shadowAge++;
            Vec3 toShadow = shadow.position().add(0, 1.5, 0).subtract(player.getEyePosition());
            double distance = toShadow.length();
            boolean seen = distance > 0 && player.getViewVector(1f).dot(toShadow) / distance > SHADOW_SEEN_DOT;
            if (seen || distance < 6 || shadowAge > 160) {
                shadowVisible = false;
            }
            return;
        }
        if (--shadowTimer > 0) {
            return;
        }
        shadowTimer = level > 0 ? between(500, 1000) : between(1200, 2400);
        spawnShadow(player);
    }

    private static void spawnShadow(LocalPlayer player) {
        Level level = player.level();
        if (shadow == null || shadow.level() != level) {
            shadow = EntityType.ENDERMAN.create(level);
            if (shadow == null) {
                return;
            }
        }
        float side = RANDOM.nextBoolean() ? 1f : -1f;
        float angle = (player.getYRot() + side * Mth.nextFloat(RANDOM, 50f, 80f)) * Mth.DEG_TO_RAD;
        double far = Math.min(20, UndervoidFog.fogEnd() * 0.7);
        if (far < 8) {
            return;
        }
        double distance = Mth.nextDouble(RANDOM, Math.min(12, far - 2), far);
        double x = player.getX() - Mth.sin(angle) * distance;
        double z = player.getZ() + Mth.cos(angle) * distance;

        BlockPos.MutableBlockPos pos = BlockPos.containing(x, player.getY() + 4, z).mutable();
        for (int i = 0; i < 12; i++, pos.move(Direction.DOWN)) {
            BlockPos ground = pos.below();
            if (level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)
                    && level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2))) {
                float facing = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * Mth.RAD_TO_DEG) - 90f;
                shadow.moveTo(x, pos.getY(), z, facing, 0f);
                shadow.yBodyRot = facing;
                shadow.yBodyRotO = facing;
                shadow.yHeadRot = facing;
                shadow.yHeadRotO = facing;
                shadowAge = 0;
                shadowVisible = true;
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!shadowVisible || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        dispatcher.render(shadow, shadow.getX() - camera.x, shadow.getY() - camera.y, shadow.getZ() - camera.z,
                shadow.getYRot(), partialTick, event.getPoseStack(), buffers, dispatcher.getPackedLightCoords(shadow, partialTick));
        buffers.endBatch();
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        float strength = strength();
        if (strength > 0f) {
            double time = ticks + event.getPartialTick();
            event.setRoll(event.getRoll() + (float) Math.sin(time * 0.05) * 3f * strength);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        float strength = strength();
        if (strength > 0f) {
            double time = ticks + event.getPartialTick();
            event.setFOV(event.getFOV() * (1 + Math.sin(time * 0.08) * 0.04 * strength));
        }
    }

    private static Vec3 behind(LocalPlayer player, double distance) {
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(player.getX() + Mth.sin(yaw) * distance, player.getY(), player.getZ() - Mth.cos(yaw) * distance);
    }

    private static void play(LocalPlayer player, SoundEvent sound, Vec3 pos, float volume, float pitch) {
        player.level().playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.HOSTILE, volume, pitch, false);
    }

    private static int between(int min, int max) {
        return Mth.nextInt(RANDOM, min, max);
    }
}
