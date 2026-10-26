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
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x6A3D9A) {});

    public static final Holder<MobEffect> SERENITY = MOB_EFFECTS.register("serenity",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7FD1C7) {});

    public static final Holder<MobEffect> CLARITY = MOB_EFFECTS.register("clarity",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xF2E28C) {});

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
