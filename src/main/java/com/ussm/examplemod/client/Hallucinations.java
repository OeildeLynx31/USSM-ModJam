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
import org.jetbrains.annotations.Nullable;
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
    private static int shadowTimer = 1200;

    private static EnderMan shadow;
    private static boolean shadowVisible;
    private static int shadowAge;

    // le mirage : un faux mob (ou toi meme) qui marche vers toi dans le brouillard
    private static int mirageTimer = 600;
    private static LivingEntity mirage;
    private static int mirageAge, mirageStare;
    private static boolean renderingDouble;

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
            mirage = null;
            intensity = 0f;
            return;
        }
        if (shadow != null && shadow.level() != player.level()) {
            shadow = null;
            shadowVisible = false;
        }
        if (mirage != null && mirage.level() != player.level()) {
            mirage = null;
        }
        if (minecraft.isPaused()) {
            return;
        }
        ticks++;

        MobEffectInstance effect = player.getEffect(ModEffects.HALLUCINATIONS);
        boolean strong = effect != null && effect.getAmplifier() > 0;
        float target = effect == null ? 0f : strong ? 1f : 0.5f;
        intensity += Mth.clamp(target - intensity, -0.02f, 0.02f);

        // battements de coeur quand ca commence a aller mal
        if (Sanity.get(player) <= Sanity.TROUBLED && --heartbeatTimer <= 0) {
            heartbeatTimer = strong ? Mth.nextInt(RANDOM, 100, 200) : Mth.nextInt(RANDOM, 200, 400);
            play(player, SoundEvents.WARDEN_HEARTBEAT, player.position(), 0.3f, 1f);
        }

        if (effect == null) {
            shadowVisible = false;
            mirage = null;
            stepsLeft = 0;
            return;
        }

        if (stepsLeft > 0) {
            if (--stepDelay <= 0) {
                Vec3 behind = behind(player, 2.5);
                BlockPos ground = BlockPos.containing(behind).below();
                SoundEvent step = player.level().getBlockState(ground)
                        .getSoundType(player.level(), ground, player)
                        .getStepSound();
                play(player, step, behind, 0.4f, 1f);
                stepsLeft--;
                stepDelay = 6;
            }
        } else if (--stepsTimer <= 0) {
            stepsLeft = 3;
            stepDelay = 0;
            if (strong) {
                stepsTimer = RANDOM.nextIntBetweenInclusive(200, 400);
            } else {
                stepsTimer = RANDOM.nextIntBetweenInclusive(500, 900);
            }
        }

        if (--stareTimer <= 0) {
            play(player, SoundEvents.ENDERMAN_STARE, behind(player, 4), 0.25f, 1f);
            stareTimer = strong ? Mth.nextInt(RANDOM, 600, 1200) : Mth.nextInt(RANDOM, 1200, 2400);
        }
        if (strong && --creeperTimer <= 0) {
            play(player, SoundEvents.CREEPER_PRIMED, behind(player, 2), 0.8f, 0.5f);
            creeperTimer = Mth.nextInt(RANDOM, 800, 1600);
        }

        if (Config.HALLUCINATION_INTENSITY.get() <= 0) {
            shadowVisible = false;
        } else if (shadowVisible) {
            shadowAge++;
            Vec3 toShadow = shadow.position().add(0, 1.5, 0).subtract(player.getEyePosition());
            double distance = toShadow.length();
            boolean seen = player.getViewVector(1f).dot(toShadow) / distance > 0.94;
            if (seen || distance < 6 || shadowAge > 160) {
                shadowVisible = false;
            }
        } else if (--shadowTimer <= 0) {
            shadowTimer = strong ? Mth.nextInt(RANDOM, 25 * 20, 50 * 20) : Mth.nextInt(RANDOM, 60 * 20, 120 * 20);
            spawnShadow(player);
        }

        if (Config.HALLUCINATION_INTENSITY.get() <= 0) {
            mirage = null;
        } else if (mirage != null) {
            tickMirage(player);
        } else if (--mirageTimer <= 0) {
            mirageTimer = strong ? Mth.nextInt(RANDOM, 15 * 20, 35 * 20) : Mth.nextInt(RANDOM, 45 * 20, 90 * 20);
            spawnMirage(player, strong);
        }
    }

    @Nullable
    private static BlockPos findGround(Level level, double x, double y, double z, int height) {
        BlockPos.MutableBlockPos pos = BlockPos.containing(x, y + 4, z).mutable();
        for (int i = 0; i < 12; i++, pos.move(Direction.DOWN)) {
            BlockPos ground = pos.below();
            if (level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)
                    && level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && (height < 3 || level.isEmptyBlock(pos.above(2)))) {
                return pos.immutable();
            }
        }
        return null;
    }

    private static void spawnMirage(LocalPlayer player, boolean strong) {
        double far = Math.min(18, UndervoidFog.fogEnd() * 0.85);
        if (far < 10) {
            return;
        }
        // devant toi cette fois, faut que tu le voies arriver
        float angle = (player.getYRot() + Mth.nextFloat(RANDOM, -35f, 35f)) * Mth.DEG_TO_RAD;
        double distance = Mth.nextDouble(RANDOM, 9, far);
        double x = player.getX() - Mth.sin(angle) * distance;
        double z = player.getZ() + Mth.cos(angle) * distance;
        BlockPos pos = findGround(player.level(), x, player.getY(), z, 2);
        if (pos == null) {
            return;
        }

        LivingEntity fake;
        int roll = RANDOM.nextInt(strong ? 12 : 11);
        if (roll >= 10 && player.level() instanceof ClientLevel level) {
            // ton propre double, avec ton stuff
            RemotePlayer copy = new RemotePlayer(level, player.getGameProfile());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                copy.setItemSlot(slot, player.getItemBySlot(slot).copy());
            }
            fake = copy;
        } else {
            EntityType<? extends Mob> type = roll < 3 ? EntityType.ZOMBIE : roll < 6 ? EntityType.SKELETON : roll < 8 ? EntityType.CREEPER : EntityType.SPIDER;
            Mob mob = type.create(player.level());
            if (mob == null) {
                return;
            }
            if (type == EntityType.SKELETON) {
                mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            }
            fake = mob;
        }
        fake.moveTo(x, pos.getY(), z, 0f, 0f);
        face(fake, player);
        fake.xo = fake.getX();
        fake.yo = fake.getY();
        fake.zo = fake.getZ();
        mirage = fake;
        mirageAge = 0;
        mirageStare = 0;
    }

    private static void tickMirage(LocalPlayer player) {
        LivingEntity fake = mirage;
        fake.xo = fake.getX();
        fake.yo = fake.getY();
        fake.zo = fake.getZ();
        fake.yRotO = fake.getYRot();
        fake.yBodyRotO = fake.yBodyRot;
        fake.yHeadRotO = fake.yHeadRot;
        fake.tickCount++;
        mirageAge++;
        face(fake, player);

        // il reste planté une seconde et demi puis il avance tranquille
        float speed = 0f;
        Vec3 to = player.position().subtract(fake.position());
        double flat = to.horizontalDistance();
        if (mirageAge > 30 && flat > 2) {
            double x = fake.getX() + to.x / flat * 0.07;
            double z = fake.getZ() + to.z / flat * 0.07;
            BlockPos pos = findGround(player.level(), x, fake.getY(), z, 2);
            if (pos != null && Math.abs(pos.getY() - fake.getY()) <= 1) {
                fake.setPos(x, pos.getY(), z);
                speed = 0.5f;
            }
        }
        fake.walkAnimation.update(speed, 0.4f);

        Vec3 toFake = fake.position().add(0, fake.getBbHeight() * 0.6, 0).subtract(player.getEyePosition());
        double distance = toFake.length();
        mirageStare = player.getViewVector(1f).dot(toFake) / distance > 0.97 ? mirageStare + 1 : 0;
        // si tu le fixes trop ou que tu t'approches, pouf
        if (distance < 4.5 || mirageStare > 25 || mirageAge > 400) {
            Level level = player.level();
            for (int i = 0; i < 14; i++) {
                level.addParticle(ParticleTypes.POOF,
                        fake.getX() + RANDOM.nextGaussian() * 0.3,
                        fake.getY() + RANDOM.nextDouble() * fake.getBbHeight(),
                        fake.getZ() + RANDOM.nextGaussian() * 0.3,
                        0, 0.02, 0);
            }
            play(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, fake.position(), 0.6f, 1.2f);
            mirage = null;
        }
    }

    private static void face(LivingEntity entity, LocalPlayer player) {
        float facing = (float) (Mth.atan2(player.getZ() - entity.getZ(), player.getX() - entity.getX()) * Mth.RAD_TO_DEG) - 90f;
        entity.setYRot(facing);
        entity.yBodyRot = facing;
        entity.yHeadRot = facing;
    }

    // l'enderman fantome est jamais ajouté au monde, on fait juste son rendu plus bas
    private static void spawnShadow(LocalPlayer player) {
        Level level = player.level();
        if (shadow == null) {
            shadow = new EnderMan(EntityType.ENDERMAN, level);
        }

        float side = RANDOM.nextBoolean() ? 1f : -1f;
        float angle = (player.getYRot() + side * Mth.nextFloat(RANDOM, 50f, 80f)) * Mth.DEG_TO_RAD;
        double far = Math.min(20, UndervoidFog.fogEnd() * 0.7);
        // dans le miasme on voit rien de toute facon
        if (far < 8) {
            return;
        }
        double distance = Mth.nextDouble(RANDOM, Math.min(12, far - 2), far);
        double x = player.getX() - Mth.sin(angle) * distance;
        double z = player.getZ() + Mth.cos(angle) * distance;

        BlockPos pos = findGround(level, x, player.getY(), z, 3);
        if (pos != null) {
            double dx = player.getX() - x;
            double dz = player.getZ() - z;
            float facing = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90f;
            shadow.moveTo(x, pos.getY(), z, facing, 0f);
            shadow.yBodyRot = facing;
            shadow.yBodyRotO = facing;
            shadow.yHeadRot = facing;
            shadow.yHeadRotO = facing;
            shadowAge = 0;
            shadowVisible = true;
        }
    }

    //----------------------------RENDU----------------------------
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if ((!shadowVisible && mirage == null) || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        if (shadowVisible) {
            renderFake(dispatcher, shadow, camera, partialTick, event.getPoseStack(), buffers);
        }
        if (mirage != null) {
            renderFake(dispatcher, mirage, camera, partialTick, event.getPoseStack(), buffers);
        }
        buffers.endBatch();
    }

    private static void renderFake(EntityRenderDispatcher dispatcher, Entity entity, Vec3 camera, float partialTick,
                                   PoseStack poseStack, MultiBufferSource buffers) {
        dispatcher.render(
                entity,
                Mth.lerp(partialTick, entity.xo, entity.getX()) - camera.x,
                Mth.lerp(partialTick, entity.yo, entity.getY()) - camera.y,
                Mth.lerp(partialTick, entity.zo, entity.getZ()) - camera.z,
                entity.getYRot(),
                partialTick,
                poseStack,
                buffers,
                dispatcher.getPackedLightCoords(entity, partialTick)
        );
    }

    private static boolean isFake(Entity entity) {
        return entity == shadow || entity == mirage;
    }

    // vision double : les mobs proches ont une copie decalée qui ondule
    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        float strength = strength();
        Minecraft minecraft = Minecraft.getInstance();
        if (renderingDouble || strength <= 0f || !(event.getEntity() instanceof Mob mob) || isFake(mob)
                || minecraft.player == null || mob.distanceToSqr(minecraft.player) > 24 * 24) {
            return;
        }
        double time = ticks + event.getPartialTick();
        int id = mob.getId();
        // pas tout le temps et pas tous en meme temps
        if (Math.sin(time * 0.045 + id * 2.3) < 0.6 - strength * 0.5) {
            return;
        }
        float spread = (0.25f + 0.5f * strength) * (float) (0.6 + 0.4 * Math.sin(time * 0.11 + id));
        Vector3f left = minecraft.gameRenderer.getMainCamera().getLeftVector();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(left.x() * spread, Math.sin(time * 0.07 + id) * 0.05 * strength, left.z() * spread);
        renderingDouble = true;
        try {
            renderAgain(event);
        } finally {
            renderingDouble = false;
            poseStack.popPose();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity, M extends EntityModel<T>> void renderAgain(RenderLivingEvent<T, M> event) {
        T entity = (T) event.getEntity();
        float yaw = Mth.lerp(event.getPartialTick(), entity.yRotO, entity.getYRot());
        event.getRenderer().render(entity, yaw, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
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
            event.setFOV(event.getFOV() * (1 + Math.sin((ticks + event.getPartialTick()) * 0.08) * 0.04 * strength));
        }
    }

    private static Vec3 behind(LocalPlayer player, double distance) {
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(player.getX() + Mth.sin(yaw) * distance, player.getY(), player.getZ() - Mth.cos(yaw) * distance);
    }

    private static void play(LocalPlayer player, SoundEvent sound, Vec3 pos, float volume, float pitch) {
        player.level().playLocalSound(
                pos.x, pos.y, pos.z,
                sound,
                SoundSource.HOSTILE,
                volume,
                pitch,
                false
        );
    }
}
