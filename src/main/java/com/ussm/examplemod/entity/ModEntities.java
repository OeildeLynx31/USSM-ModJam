package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.Hedgehog;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.item.custom.VoidBallProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ExampleMod.MODID);

    public static final Supplier<EntityType<Hedgehog>> HEDGEHOG =
            ENTITY_TYPES.register("hedgehog", () -> EntityType.Builder.of(Hedgehog::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.4f).build("hedgehog"));

    public static final Supplier<EntityType<Mimic>> MIMIC =
            ENTITY_TYPES.register("mimic", () -> EntityType.Builder.of(Mimic::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .eyeHeight(1.62f)
                    .clientTrackingRange(10)
                    .build("mimic"));

    public static final Supplier<EntityType<VoidBallProjectile>> VOID_BALL =
            ENTITY_TYPES.register("void_ball",
                    () -> EntityType.Builder.<VoidBallProjectile>of(
                                    VoidBallProjectile::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("void_ball"));

    //>-------------------------------------------------------
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
