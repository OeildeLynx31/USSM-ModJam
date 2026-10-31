package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.HerobrineEntity;
import com.ussm.examplemod.entity.custom.Hedgehog;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import com.ussm.examplemod.entity.custom.SmokeBossEntity;
import com.ussm.examplemod.entity.custom.Cockroach;
import com.ussm.examplemod.entity.custom.GloomBird;
import com.ussm.examplemod.entity.custom.BloomButterfly;
import com.ussm.examplemod.entity.custom.FloatingLight;
import com.ussm.examplemod.entity.custom.CarnivorousPlant;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.HEDGEHOG.get(), Hedgehog.createAttributes().build());
        event.put(ModEntities.MIMIC.get(), Mimic.createAttributes().build());
        event.put(ModEntities.HEROBRINE.get(), HerobrineEntity.createAttributes().build());
        event.put(ModEntities.SMOKE_BOSS.get(), SmokeBossEntity.createAttributes().build());
        event.put(ModEntities.COCKROACH.get(), Cockroach.createAttributes().build());
        event.put(ModEntities.GLOOM_BIRD.get(), GloomBird.createAttributes().build());
        event.put(ModEntities.BLOOM_BUTTERFLY.get(), BloomButterfly.createAttributes().build());
        event.put(ModEntities.FLOATING_LIGHT.get(), FloatingLight.createAttributes().build());
        event.put(ModEntities.CARNIVOROUS_PLANT.get(), CarnivorousPlant.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.HEDGEHOG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Hedgehog::checkHedgehogSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.COCKROACH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Cockroach::checkCockroachSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GLOOM_BIRD.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GloomBird::checkGloomBirdSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        // les volants du bloom peuvent naitre en l'air dans les grottes
        event.register(ModEntities.BLOOM_BUTTERFLY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BloomButterfly::checkButterflySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.FLOATING_LIGHT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FloatingLight::checkFloatingLightSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CARNIVOROUS_PLANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CarnivorousPlant::checkPlantSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        //+ getId() pour pas que tout les joueurs le fassent au meme tick
        if ((player.tickCount + player.getId()) % 20 == 10) {
            MimicSpawner.tick(player);
        }
    }

    // pas de monstres sous la surface dans l'undervoid
    @SubscribeEvent
    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (event.getMobCategory() != MobCategory.MONSTER) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            BlockPos pos = event.getPos();
            if (pos.getY() < level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ())) {
                event.setCanceled(true);
            }
        }
    }
}
