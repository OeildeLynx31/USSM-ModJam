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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class UndervoidFog {
    private static final float UNDERGROUND_FAR = 56f;

    private static boolean inUndervoid;
    private static boolean inGloom;
    private static float surfaceFog;
    private static float oldSurfaceFog;
    private static float miasmaFog;
    private static float oldMiasmaFog;
    private static float surfaceFar;
    private static float miasmaFar;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            inUndervoid = false;
            inGloom = false;
            return;
        }

        Holder<Biome> biome = player.level().getBiome(BlockPos.containing(player.getEyePosition()));
        boolean inMiasma = biome.is(ModBiomes.MIASMA);
        inGloom = inMiasma || biome.is(ModBiomes.GLOOM);

        float surfaceTarget = inGloom && player.getEyeY() >= Sanity.GLOOM_SURFACE_Y ? 1f : 0f;
        float miasmaTarget = inMiasma ? 1f : 0f;
        if (!inUndervoid) {
            inUndervoid = true;
            surfaceFog = surfaceTarget;
            miasmaFog = miasmaTarget;
        }
        oldSurfaceFog = surfaceFog;
        oldMiasmaFog = miasmaFog;
        surfaceFog = Mth.approach(surfaceFog, surfaceTarget, 0.05f);
        miasmaFog = Mth.approach(miasmaFog, miasmaTarget, 0.05f);

        boolean mask = HazmatArmorItem.isWearingMask(player);
        surfaceFar = mask ? 24f : 14f;
        miasmaFar = mask ? 9f : 6f;

        // nuage de particules autour du joueur dans le miasme
        if (miasmaFog > 0f && !minecraft.isPaused()) {
            spawnMiasmaParticles(player);
        }
    }

    private static void spawnMiasmaParticles(LocalPlayer player) {
        ClientLevel level = player.clientLevel;
        RandomSource random = player.getRandom();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int count = Mth.ceil(miasmaFog * 10);

        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() * 2 - 1) * miasmaFar;
            double y = player.getEyeY() + (random.nextDouble() * 2 - 1) * miasmaFar * 0.5;
            double z = player.getZ() + (random.nextDouble() * 2 - 1) * miasmaFar;
            if (!level.getBlockState(pos.set(x, y, z)).isCollisionShapeFullBlock(level, pos)) {
                level.addParticle(ModParticles.MIASMA.get(), x, y, z, 0, 0, 0);
            }
        }
    }

    public static float fogEnd() {
        if (!inUndervoid) {
            return Float.MAX_VALUE;
        }
        return Mth.lerp(miasmaFog, Mth.lerp(surfaceFog, UNDERGROUND_FAR, surfaceFar), miasmaFar);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!inUndervoid) {
            return;
        }

        float partialTick = (float) event.getPartialTick();
        float surface = Mth.lerp(partialTick, oldSurfaceFog, surfaceFog);
        float miasma = Mth.lerp(partialTick, oldMiasmaFog, miasmaFog);

        float near = Mth.lerp(surface, 8f, 0f);
        float far = Mth.lerp(surface, UNDERGROUND_FAR, surfaceFar);
        if (miasma > 0f) {
            near = Mth.lerp(miasma, near, 0f);
            far = Mth.lerp(miasma, far, miasmaFar);
        }

        event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), near));
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), far));
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        if (!inUndervoid || camera.getFluidInCamera() != FogType.NONE || !(camera.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS) || entity.hasEffect(MobEffects.NIGHT_VISION)) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        float voidDarkness = ((float) camera.getPosition().y - level.getMinBuildHeight()) * level.getLevelData().getClearColorScale();
        // le jeu assombrit tout sous y 32 a cause du vide, on compense sinon le bloom est noir
        if (voidDarkness > 0.05f && voidDarkness < 1f) {
            float boost = 1f / (voidDarkness * voidDarkness);
            event.setRed(event.getRed() * boost);
            event.setGreen(event.getGreen() * boost);
            event.setBlue(event.getBlue() * boost);
        }
    }

    @SubscribeEvent
    //pas de musique dans le gloom, c'est plus flippant
    public static void onSelectMusic(SelectMusicEvent event) {
        if (inGloom) {
            event.setMusic(null);
        }
    }
}
