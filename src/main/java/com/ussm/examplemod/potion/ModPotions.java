package com.ussm.examplemod.potion;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ExampleMod.MODID);

    public static final Holder<Potion> SERENITY = POTIONS.register("serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 3600)));
    public static final Holder<Potion> LONG_SERENITY = POTIONS.register("long_serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 9600)));
    public static final Holder<Potion> STRONG_SERENITY = POTIONS.register("strong_serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 1800, 1)));

    public static final Holder<Potion> CLARITY = POTIONS.register("clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 3600)));
    public static final Holder<Potion> LONG_CLARITY = POTIONS.register("long_clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 9600)));
    public static final Holder<Potion> STRONG_CLARITY = POTIONS.register("strong_clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 1800, 1)));

    public static boolean affectsSanity(ItemStack stack) {
        if (!stack.is(Items.POTION)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return false;
        }
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.is(ModEffects.SERENITY) || effect.is(ModEffects.CLARITY)) {
                return true;
            }
        }
        return false;
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
