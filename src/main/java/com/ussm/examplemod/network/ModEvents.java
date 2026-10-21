package com.ussm.examplemod.network;


import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        if (serverLevel.dimension().equals(Level.END)
                && entity.getY() < 0) {
            ServerLevel undervoidLevel = serverLevel.getServer()
                    .getLevel(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
            if (undervoidLevel != null) {
                teleportEntity(entity, undervoidLevel, new Vec3(entity.getX(), 250, entity.getZ()));
            }
        } else if (serverLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)
                && entity.getY() > 256) {
            ServerLevel endLevel = serverLevel.getServer()
                    .getLevel(Level.END);
            if (endLevel != null) {
                teleportEntity(entity, endLevel, new Vec3(entity.getX(), 1, entity.getZ()));
            }
        }
    }

    public static void teleportEntity(
            Entity entity,
            ServerLevel targetLevel,
            Vec3 targetPos
    ) {
        if (entity.level().isClientSide()) {
            return;
        }

        entity.changeDimension(new DimensionTransition(
                targetLevel,
                targetPos,
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.DO_NOTHING
        ));
    }
}
