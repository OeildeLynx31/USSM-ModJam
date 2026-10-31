package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.HerobrineEntity;
import com.ussm.examplemod.entity.custom.Hedgehog;
import com.ussm.examplemod.entity.custom.Mimic;
import com.ussm.examplemod.entity.custom.ModBoatEntity;
import com.ussm.examplemod.entity.custom.ModChestBoatEntity;
import com.ussm.examplemod.item.custom.VoidBallProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import com.ussm.examplemod.entity.custom.SmokeBossEntity;
import com.ussm.examplemod.entity.custom.Cockroach;
import com.ussm.examplemod.entity.custom.GloomBird;
import com.ussm.examplemod.entity.custom.BloomButterfly;
import com.ussm.examplemod.entity.custom.FloatingLight;
import com.ussm.examplemod.entity.custom.CarnivorousPlant;
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

    public static final Supplier<EntityType<SmokeBossEntity>> SMOKE_BOSS = ENTITY_TYPES.register("smoke_boss",
            () -> EntityType.Builder.of(SmokeBossEntity::new, MobCategory.MONSTER)
                    .sized(0.7f, 2.2f)
                    .eyeHeight(2.0f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("smoke_boss"));

    public static final Supplier<EntityType<HerobrineEntity>> HEROBRINE = ENTITY_TYPES.register("herobrine",
            () -> EntityType.Builder.of(HerobrineEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .eyeHeight(1.62f)
                    .clientTrackingRange(8)
                    .build("herobrine"));

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

    // les bestioles du modeleur
    public static final Supplier<EntityType<Cockroach>> COCKROACH =
            ENTITY_TYPES.register("cockroach", () -> EntityType.Builder.of(Cockroach::new, MobCategory.AMBIENT)
                    .sized(0.4f, 0.2f).build("cockroach"));

    public static final Supplier<EntityType<GloomBird>> GLOOM_BIRD =
            ENTITY_TYPES.register("gloom_bird", () -> EntityType.Builder.of(GloomBird::new, MobCategory.AMBIENT)
                    .sized(0.6f, 0.7f)
                    .clientTrackingRange(10)
                    .build("gloom_bird"));

    public static final Supplier<EntityType<BloomButterfly>> BLOOM_BUTTERFLY =
            ENTITY_TYPES.register("bloom_butterfly", () -> EntityType.Builder.of(BloomButterfly::new, MobCategory.AMBIENT)
                    .sized(0.5f, 0.3f).build("bloom_butterfly"));

    public static final Supplier<EntityType<FloatingLight>> FLOATING_LIGHT =
            ENTITY_TYPES.register("floating_light", () -> EntityType.Builder.of(FloatingLight::new, MobCategory.AMBIENT)
                    .sized(0.4f, 0.7f).build("floating_light"));

    public static final Supplier<EntityType<CarnivorousPlant>> CARNIVOROUS_PLANT =
            ENTITY_TYPES.register("carnivorous_plant", () -> EntityType.Builder.of(CarnivorousPlant::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.0f)
                    .eyeHeight(1.6f)
                    .build("carnivorous_plant"));

    //memes tailles que le boat vanilla
    public static final Supplier<EntityType<ModBoatEntity>> VOID_BOAT = ENTITY_TYPES.register("void_boat",
            () -> EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .eyeHeight(0.5625f)
                    .clientTrackingRange(10)
                    .build("void_boat"));

    public static final Supplier<EntityType<ModChestBoatEntity>> VOID_CHEST_BOAT = ENTITY_TYPES.register("void_chest_boat",
            () -> EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .eyeHeight(0.5625f)
                    .clientTrackingRange(10)
                    .build("void_chest_boat"));

    //>-------------------------------------------------------
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
