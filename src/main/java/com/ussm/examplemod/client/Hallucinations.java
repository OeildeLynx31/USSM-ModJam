package com.ussm.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.sanity.Sanity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class Hallucinations {
    private static final RandomSource RANDOM = RandomSource.create();

    private static float intensity;
    private static int ticks;

    private static int heartbeatTimer = 200;
    private static int stepsTimer = 600;
    private static int stepsLeft, stepDelay;
    private static int stareTimer = 1200, creeperTimer = 1200;

    private static EnderMan shadow;
    private static boolean shadowVisible;
    private static int shadowTimer = 1200;
    private static int shadowAge;

    // le mirage : un faux mob (ou toi meme) qui marche vers toi dans le brouillard
    private static LivingEntity mirage;
    private static int mirageTimer = 600;
    private static int mirageAge, lookTicks;

    private static boolean rendering = false;

    public static float strength() {
        return intensity * Config.HALLUCINATION_INTENSITY.get().floatValue();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            shadow = null;
            mirage = null;
            shadowVisible = false;
            intensity = 0f;
            return;
        }
        // fix quand on change de dim
        if ((shadow != null && shadow.level() != player.level()) || (mirage != null && mirage.level() != player.level())) {
            shadow = null;
            shadowVisible = false;
            mirage = null;
        }
        if (mc.isPaused()) return;

        ticks++;

        MobEffectInstance effect = player.getEffect(ModEffects.HALLUCINATIONS);
        boolean strong = effect != null && effect.getAmplifier() > 0;
        float target = effect == null ? 0f : strong ? 1f : 0.5f;
        if (intensity < target) {
            intensity = Math.min(intensity + 0.02f, target);
        } else if (intensity > target) {
            intensity = Math.max(intensity - 0.02f, target);
        }

        // battements de coeur quand ca commence a aller mal
        if (Sanity.get(player) <= Sanity.TROUBLED && --heartbeatTimer <= 0) {
            heartbeatTimer = strong ? Mth.nextInt(RANDOM, 100, 200) : Mth.nextInt(RANDOM, 200, 400);
            playSound(player, SoundEvents.WARDEN_HEARTBEAT, player.position(), 0.3f, 1f);
        }

        if (effect == null) {
            shadowVisible = false;
            mirage = null;
            stepsLeft = 0;
            return;
        }

        // des pas derriere toi
        if (stepsLeft > 0) {
            if (--stepDelay <= 0) {
                Vec3 pos = behind(player, 2.5);
                BlockPos below = BlockPos.containing(pos).below();
                SoundEvent step = player.level().getBlockState(below).getSoundType(player.level(), below, player).getStepSound();
                playSound(player, step, pos, 0.4f, 1f);
                stepsLeft--;
                stepDelay = 6;
            }
        } else if (--stepsTimer <= 0) {
            stepsLeft = 3;
            stepDelay = 0;
            if (strong) stepsTimer = Mth.nextInt(RANDOM, 200, 400);
            else stepsTimer = Mth.nextInt(RANDOM, 500, 900);
        }

        if (--stareTimer <= 0) {
            playSound(player, SoundEvents.ENDERMAN_STARE, behind(player, 4), 0.25f, 1f);
            stareTimer = strong ? Mth.nextInt(RANDOM, 600, 1200) : Mth.nextInt(RANDOM, 1200, 2400);
        }
        if (strong && --creeperTimer <= 0) {
            playSound(player, SoundEvents.CREEPER_PRIMED, behind(player, 2), 0.8f, 0.5f);
            creeperTimer = Mth.nextInt(RANDOM, 800, 1600);
        }

        if (Config.HALLUCINATION_INTENSITY.get() <= 0) {
            shadowVisible = false;
            mirage = null;
            return;
        }

        if (shadowVisible) {
            shadowAge++;
            Vec3 diff = shadow.position().add(0, 1.5, 0).subtract(player.getEyePosition());
            double dist = diff.length();
            // si tu le regarde il se barre
            if (player.getViewVector(1f).dot(diff) / dist > 0.94 || dist < 6 || shadowAge > 160)
                shadowVisible = false;
        } else if (--shadowTimer <= 0) {
            shadowTimer = strong ? Mth.nextInt(RANDOM, 25 * 20, 50 * 20) : Mth.nextInt(RANDOM, 60 * 20, 120 * 20);
            spawnShadow(player);
        }

        if (mirage != null) {
            tickMirage(player);
        } else if (--mirageTimer <= 0) {
            int min = strong ? 300 : 900;
            int max = strong ? 700 : 1800;
            mirageTimer = Mth.nextInt(RANDOM, min, max);
            spawnMirage(player, strong);
        }
    }

    // l'enderman est jamais ajouté au monde, on le rend nous meme dans onRenderLevelStage
    private static void spawnShadow(LocalPlayer player) {
        Level level = player.level();
        if (shadow == null) shadow = new EnderMan(EntityType.ENDERMAN, level);
        //level.addFreshEntity(shadow); fait rien cote client

        float side = RANDOM.nextBoolean() ? 1f : -1f;
        float angle = (player.getYRot() + side * Mth.nextFloat(RANDOM, 50f, 80f)) * Mth.DEG_TO_RAD;
        double far = Math.min(20, UndervoidFog.fogEnd() * 0.7);
        if (far < 8) return;
        double distance = Mth.nextDouble(RANDOM, Math.min(12, far - 2), far);
        double x = player.getX() - Mth.sin(angle) * distance;
        double z = player.getZ() + Mth.cos(angle) * distance;

        BlockPos pos = findGround(level, x, player.getY(), z, true);
        if (pos == null) return;

        float facing = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * Mth.RAD_TO_DEG) - 90f;
        shadow.moveTo(x, pos.getY(), z, facing, 0f);
        shadow.yBodyRot = shadow.yBodyRotO = facing;
        shadow.yHeadRot = shadow.yHeadRotO = facing;
        shadowAge = 0;
        shadowVisible = true;
    }

    private static void spawnMirage(LocalPlayer player, boolean strong) {
        double far = Math.min(18, UndervoidFog.fogEnd() * 0.85);
        if (far < 10) return;

        float angle = (player.getYRot() + Mth.nextFloat(RANDOM, -35f, 35f)) * Mth.DEG_TO_RAD;
        double distance = Mth.nextDouble(RANDOM, 9, far);
        double x = player.getX() - Mth.sin(angle) * distance;
        double z = player.getZ() + Mth.cos(angle) * distance;
        BlockPos pos = findGround(player.level(), x, player.getY(), z, false);
        if (pos == null) return;

        LivingEntity fake;
        int r = RANDOM.nextInt(strong ? 12 : 11);
        if (r >= 10 && player.level() instanceof ClientLevel level) {
            // ton propre double, avec ton stuff
            RemotePlayer clone = new RemotePlayer(level, player.getGameProfile());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                clone.setItemSlot(slot, player.getItemBySlot(slot).copy());
            }
            fake = clone;
        } else {
            EntityType<? extends Mob> type = r < 3 ? EntityType.ZOMBIE : r < 6 ? EntityType.SKELETON : r < 8 ? EntityType.CREEPER : EntityType.SPIDER;
            Mob mob = type.create(player.level());
            if (mob == null) return;
            if (type == EntityType.SKELETON) {
                mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            }
            fake = mob;
        }
        fake.moveTo(x, pos.getY(), z, 0f, 0f);
        float facing = (float) (Mth.atan2(player.getZ() - fake.getZ(), player.getX() - fake.getX()) * Mth.RAD_TO_DEG) - 90f;
        fake.setYRot(facing);
        fake.yBodyRot = facing;
        fake.yHeadRot = facing;
        //ExampleMod.LOGGER.info("mirage {} en {}", fake.getType(), pos);
        mirage = fake;
        mirageAge = 0;
        lookTicks = 0;
    }

    private static void tickMirage(LocalPlayer player) {
        LivingEntity fake = mirage;
        //fake.tick(); fait n'importe quoi, on fait a la main
        fake.xo = fake.getX(); fake.yo = fake.getY(); fake.zo = fake.getZ();
        fake.yRotO = fake.getYRot();
        fake.yBodyRotO = fake.yBodyRot;
        fake.yHeadRotO = fake.yHeadRot;
        fake.tickCount++;
        mirageAge++;
        float facing = (float) (Mth.atan2(player.getZ() - fake.getZ(), player.getX() - fake.getX()) * Mth.RAD_TO_DEG) - 90f;
        fake.setYRot(facing);
        fake.yBodyRot = facing;
        fake.yHeadRot = facing;

        float speed = 0f;
        double dx = player.getX() - fake.getX();
        double dz = player.getZ() - fake.getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        if (mirageAge > 30 && flat > 2) {
            double x = fake.getX() + dx / flat * 0.07;
            double z = fake.getZ() + dz / flat * 0.07;
            BlockPos pos = findGround(player.level(), x, fake.getY(), z, false);
            if (pos != null && Math.abs(pos.getY() - fake.getY()) <= 1) {
                fake.setPos(x, pos.getY(), z);
                speed = 0.5f;
            }
        }
        fake.walkAnimation.update(speed, 0.4f);

        Vec3 toFake = fake.position().add(0, fake.getBbHeight() * 0.6, 0).subtract(player.getEyePosition());
        double distance = toFake.length();
        if (player.getViewVector(1f).dot(toFake) / distance > 0.97) lookTicks++;
        else lookTicks = 0;

        // si tu le fixes trop ou que tu t'approches, pouf
        if (distance < 4.5 || lookTicks > 25 || mirageAge > 400) {
            Level level = player.level();
            for (int i = 0; i < 14; i++) {
                level.addParticle(ParticleTypes.POOF,
                        fake.getX() + RANDOM.nextGaussian() * 0.3,
                        fake.getY() + RANDOM.nextDouble() * fake.getBbHeight(),
                        fake.getZ() + RANDOM.nextGaussian() * 0.3,
                        0, 0.02, 0);
            }
            playSound(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, fake.position(), 0.6f, 1.2f);
            mirage = null;
        }
    }

    // tall = pour l'enderman qui fait 3 blocs
    private static BlockPos findGround(Level level, double x, double y, double z, boolean tall) {
        BlockPos.MutableBlockPos pos = BlockPos.containing(x, y + 4, z).mutable();
        for (int i = 0; i < 12; i++) {
            BlockPos ground = pos.below();
            if (level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)
                    && level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && (!tall || level.isEmptyBlock(pos.above(2)))) {
                return pos.immutable();
            }
            pos.move(Direction.DOWN);
        }
        return null;
    }

    private static Vec3 behind(LocalPlayer player, double distance) {
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(player.getX() + Mth.sin(yaw) * distance, player.getY(), player.getZ() - Mth.cos(yaw) * distance);
    }

    private static void playSound(LocalPlayer player, SoundEvent sound, Vec3 pos, float volume, float pitch) {
        player.level().playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.HOSTILE, volume, pitch, false);
    }


    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        if (!shadowVisible && mirage == null) return;

        Minecraft mc = Minecraft.getInstance();
        Vec3 cam = event.getCamera().getPosition();
        //float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        if (shadowVisible) {
            // lui il bouge pas
            dispatcher.render(shadow, shadow.getX() - cam.x, shadow.getY() - cam.y, shadow.getZ() - cam.z, shadow.getYRot(), partialTick,
                    poseStack, buffers, dispatcher.getPackedLightCoords(shadow, partialTick));
        }
        if (mirage != null) {
            double x = Mth.lerp(partialTick, mirage.xo, mirage.getX()) - cam.x;
            double y = Mth.lerp(partialTick, mirage.yo, mirage.getY()) - cam.y;
            double z = Mth.lerp(partialTick, mirage.zo, mirage.getZ()) - cam.z;
            dispatcher.render(mirage, x, y, z, mirage.getYRot(), partialTick, poseStack, buffers, dispatcher.getPackedLightCoords(mirage, partialTick));
        }
        buffers.endBatch();
    }

    // vision double : les mobs proches ont une copie decalée qui ondule
    // (marche pas sur le herisson vu que geckolib passe pas par RenderLivingEvent, tant pis)
    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        float strength = strength();
        Minecraft mc = Minecraft.getInstance();
        if (rendering || strength <= 0f || mc.player == null) return;
        if (!(event.getEntity() instanceof Mob mob) || mob == shadow || mob == mirage) return;
        if (mob.distanceToSqr(mc.player) > 24 * 24) return;

        float partialTick = event.getPartialTick();
        double time = ticks + partialTick;
        int id = mob.getId();
        if (Math.sin(time * 0.045 + id * 2.3) < 0.6 - strength * 0.5) return;
        float spread = (0.25f + 0.5f * strength) * (float) (0.6 + 0.4 * Math.sin(time * 0.11 + id));
        Vector3f left = mc.gameRenderer.getMainCamera().getLeftVector();
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(left.x() * spread, Math.sin(time * 0.07 + id) * 0.05 * strength, left.z() * spread);
        rendering = true; // sinon stackoverflow
        LivingEntityRenderer renderer = event.getRenderer();
        renderer.render(mob, Mth.lerp(partialTick, mob.yRotO, mob.getYRot()), partialTick, poseStack, event.getMultiBufferSource(), event.getPackedLight());
        rendering = false;
        poseStack.popPose();
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
        if (strength <= 0f) return;
        event.setFOV(event.getFOV() * (1 + Math.sin((ticks + event.getPartialTick()) * 0.08) * 0.04 * strength));
    }
}
