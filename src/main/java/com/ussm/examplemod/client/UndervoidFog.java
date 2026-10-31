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

    private static boolean inUndervoid, inGloom;
    // 0 = sous terre, 1 = surface du gloom
    private static float surfaceFog, lastSurfaceFog;
    private static float miasmaFog, lastMiasmaFog;
    private static float surfaceFar, miasmaFar;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
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
        // on vient d'arriver, pas de transition
        if (!inUndervoid) {
            inUndervoid = true;
            surfaceFog = surfaceTarget;
            miasmaFog = miasmaTarget;
        }
        lastSurfaceFog = surfaceFog;
        lastMiasmaFog = miasmaFog;
        // ~1s pour passer d'un brouillard a l'autre
        surfaceFog = Mth.approach(surfaceFog, surfaceTarget, 0.05f);
        miasmaFog = Mth.approach(miasmaFog, miasmaTarget, 0.05f);

        boolean mask = HazmatArmorItem.isWearingMask(player);
        surfaceFar = mask ? 24f : 14f;
        miasmaFar = mask ? 9f : 6f;

        if (miasmaFog > 0f && !mc.isPaused()) {
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
    }

    public static float fogEnd() {
        if (!inUndervoid) return Float.MAX_VALUE;
        float far = Mth.lerp(surfaceFog, UNDERGROUND_FAR, surfaceFar);
        return Mth.lerp(miasmaFog, far, miasmaFar);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!inUndervoid) {
            return;
        }

        float pt = (float) event.getPartialTick();
        float surface = Mth.lerp(pt, lastSurfaceFog, surfaceFog);
        float miasma = Mth.lerp(pt, lastMiasmaFog, miasmaFog);

        float near = Mth.lerp(surface, 8f, 0f);
        float far = Mth.lerp(surface, UNDERGROUND_FAR, surfaceFar);
        if (miasma > 0f) {
            near = Mth.lerp(miasma, near, 0f);
            far = Mth.lerp(miasma, far, miasmaFar);
        }

        event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), near));
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), far));
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true); // obligé sinon ca prend pas les distances (c'est dans la javadoc)
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        if (!inUndervoid || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }
        if (!(camera.getEntity() instanceof LivingEntity entity) || entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS) || entity.hasEffect(MobEffects.NIGHT_VISION)) {
            return;
        }

        // le jeu assombrit le brouillard entre minY+32 et minY a cause du void, cf https://minecraft.wiki/w/Fog
        // du coup les caves du bloom etaient toutes noires. ligne copiée de FogRenderer.setupColor, on fait juste l'inverse
        ClientLevel level = Minecraft.getInstance().level;
        float f5 = ((float)camera.getPosition().y - (float)level.getMinBuildHeight()) * level.getLevelData().getClearColorScale();
        // tout en bas ca divise par 0, 0.05 mis au pif
        if (f5 < 1.0F && f5 > 0.05F) {
            f5 *= f5;
            float boost = 1.0F / f5;
            event.setRed(event.getRed() * boost);
            event.setGreen(event.getGreen() * boost);
            event.setBlue(event.getBlue() * boost);
        }
    }

    //pas de musique dans le gloom c'est plus flippant
    @SubscribeEvent
    public static void onSelectMusic(SelectMusicEvent event) {
        if (inGloom) event.setMusic(null);
    }
}
