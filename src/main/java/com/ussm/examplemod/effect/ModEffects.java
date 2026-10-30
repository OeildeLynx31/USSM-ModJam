package com.ussm.examplemod.effect;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ExampleMod.MODID);

    public static final Holder<MobEffect> HALLUCINATIONS = MOB_EFFECTS.register("hallucinations",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x6a3d9a) {});

    public static final Holder<MobEffect> SERENITY = MOB_EFFECTS.register("serenity",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7fd1c7) {});

    public static final Holder<MobEffect> CLARITY = MOB_EFFECTS.register("clarity",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xf2e28c) {});

    public static final Holder<MobEffect> RESTORATION = MOB_EFFECTS.register("restoration",
            () -> new SanityInstantEffect(MobEffectCategory.BENEFICIAL, 0x4FC3F7, 30f));

    public static final Holder<MobEffect> DISTRESS = MOB_EFFECTS.register("distress",
            () -> new SanityInstantEffect(MobEffectCategory.HARMFUL, 0x8B1A1A, -30f));

    public static final Holder<MobEffect> EROSION = MOB_EFFECTS.register("erosion",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x5A2A5A) {});

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
