package com.ussm.examplemod.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.item.custom.HazmatArmorItem;
import com.ussm.examplemod.particle.ModParticles;
import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class UndervoidFog {
    private static final float SURFACE_FAR = 14f;
    private static final float MASK_SURFACE_FAR = 24f;
    private static final float UNDERGROUND_NEAR = 8f;
    private static final float UNDERGROUND_FAR = 56f;
    private static final float MIASMA_FAR = 6f;
    private static final float MASK_MIASMA_FAR = 9f;
    private static final float TRANSITION_PER_TICK = 0.05f;
    private static final int MIASMA_PARTICLES = 10;

    private static boolean inUndervoid;
    private static boolean inGloom;
    private static float surface;
    private static float previousSurface;
    private static float miasma;
    private static float previousMiasma;
    private static float surfaceFar = SURFACE_FAR;
    private static float miasmaFar = MIASMA_FAR;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            inUndervoid = false;
            inGloom = false;
            return;
        }
        Vec3 eyes = player.getEyePosition();
        Holder<Biome> biome = player.level().getBiome(BlockPos.containing(eyes));
        boolean inMiasma = biome.is(ModBiomes.MIASMA);
        inGloom = inMiasma || biome.is(ModBiomes.GLOOM);
        float target = inGloom && eyes.y >= Sanity.GLOOM_SURFACE_Y ? 1f : 0f;
        float miasmaTarget = inMiasma ? 1f : 0f;
        if (inUndervoid) {
            previousSurface = surface;
            surface = approach(surface, target);
            previousMiasma = miasma;
            miasma = approach(miasma, miasmaTarget);
        } else {
            inUndervoid = true;
            previousSurface = target;
            surface = target;
            previousMiasma = miasmaTarget;
            miasma = miasmaTarget;
        }
        boolean mask = HazmatArmorItem.isWearingMask(player);
        surfaceFar = mask ? MASK_SURFACE_FAR : SURFACE_FAR;
        miasmaFar = mask ? MASK_MIASMA_FAR : MIASMA_FAR;
        if (miasma > 0f && !Minecraft.getInstance().isPaused()) {
            spawnMiasma(player);
        }
    }

    private static void spawnMiasma(LocalPlayer player) {
        ClientLevel level = player.clientLevel;
        RandomSource random = player.getRandom();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int count = Mth.ceil(miasma * MIASMA_PARTICLES);
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() * 2 - 1) * miasmaFar;
            double y = player.getEyeY() + (random.nextDouble() * 2 - 1) * miasmaFar * 0.5;
            double z = player.getZ() + (random.nextDouble() * 2 - 1) * miasmaFar;
            if (!level.getBlockState(pos.set(x, y, z)).isCollisionShapeFullBlock(level, pos)) {
                level.addParticle(ModParticles.MIASMA.get(), x, y, z, 0, 0, 0);
            }
        }
    }

    private static float approach(float current, float target) {
        return current + Mth.clamp(target - current, -TRANSITION_PER_TICK, TRANSITION_PER_TICK);
    }

    public static float fogEnd() {
        return inUndervoid ? Mth.lerp(miasma, Mth.lerp(surface, UNDERGROUND_FAR, surfaceFar), miasmaFar) : Float.MAX_VALUE;
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!inUndervoid) {
            return;
        }
        float partialTick = (float) event.getPartialTick();
        float factor = Mth.lerp(partialTick, previousSurface, surface);
        float thickness = Mth.lerp(partialTick, previousMiasma, miasma);
        float near = Mth.lerp(thickness, Mth.lerp(factor, UNDERGROUND_NEAR, 0f), 0f);
        float far = Mth.lerp(thickness, Mth.lerp(factor, UNDERGROUND_FAR, surfaceFar), miasmaFar);
        event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), near));
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), far));
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        ClientLevel level = Minecraft.getInstance().level;
        Camera camera = event.getCamera();
        if (!inUndervoid || level == null || camera.getFluidInCamera() != FogType.NONE || !(camera.getEntity() instanceof LivingEntity entity)
                || entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS) || entity.hasEffect(MobEffects.NIGHT_VISION)) {
            return;
        }
        float voidDarkness = ((float) camera.getPosition().y - level.getMinBuildHeight()) * level.getLevelData().getClearColorScale();
        if (voidDarkness > 0.05f && voidDarkness < 1f) {
            float boost = 1f / (voidDarkness * voidDarkness);
            event.setRed(event.getRed() * boost);
            event.setGreen(event.getGreen() * boost);
            event.setBlue(event.getBlue() * boost);
        }
    }

    @SubscribeEvent
    public static void onSelectMusic(SelectMusicEvent event) {
        if (inGloom) {
            event.setMusic(null);
        }
    }
}
