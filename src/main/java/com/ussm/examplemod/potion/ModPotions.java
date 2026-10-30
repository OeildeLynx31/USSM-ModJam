package com.ussm.examplemod.potion;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, ExampleMod.MODID);

    //----------------------------SERENITY----------------------------
    public static final Holder<Potion> SERENITY = POTIONS.register("serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 3600, 0)));
    public static final Holder<Potion> LONG_SERENITY = POTIONS.register("long_serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 9600, 0)));
    public static final Holder<Potion> STRONG_SERENITY = POTIONS.register("strong_serenity",
            () -> new Potion("serenity", new MobEffectInstance(ModEffects.SERENITY, 1800, 1)));

    //----------------------------CLARITY-----------------------------
    public static final Holder<Potion> CLARITY = POTIONS.register("clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 3600, 0)));
    public static final Holder<Potion> LONG_CLARITY = POTIONS.register("long_clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 9600, 0)));
    public static final Holder<Potion> STRONG_CLARITY = POTIONS.register("strong_clarity",
            () -> new Potion("clarity", new MobEffectInstance(ModEffects.CLARITY, 1800, 1)));

    public static final Holder<Potion> RESTORATION = POTIONS.register("restoration",
            () -> new Potion("restoration", new MobEffectInstance(ModEffects.RESTORATION, 1)));
    public static final Holder<Potion> STRONG_RESTORATION = POTIONS.register("strong_restoration",
            () -> new Potion("restoration", new MobEffectInstance(ModEffects.RESTORATION, 1, 1)));

    public static final Holder<Potion> DISTRESS = POTIONS.register("distress",
            () -> new Potion("distress", new MobEffectInstance(ModEffects.DISTRESS, 1)));
    public static final Holder<Potion> STRONG_DISTRESS = POTIONS.register("strong_distress",
            () -> new Potion("distress", new MobEffectInstance(ModEffects.DISTRESS, 1, 1)));

    public static final Holder<Potion> EROSION = POTIONS.register("erosion",
            () -> new Potion("erosion", new MobEffectInstance(ModEffects.EROSION, 3600)));
    public static final Holder<Potion> STRONG_EROSION = POTIONS.register("strong_erosion",
            () -> new Potion("erosion", new MobEffectInstance(ModEffects.EROSION, 1800, 1)));
    public static final Holder<Potion> LONG_EROSION = POTIONS.register("long_erosion",
            () -> new Potion("erosion", new MobEffectInstance(ModEffects.EROSION, 9600)));

    public static void addNewPotions(CreativeModeTab.Output output) {
        output.accept(PotionContents.createItemStack(Items.POTION, RESTORATION));
        output.accept(PotionContents.createItemStack(Items.POTION, STRONG_RESTORATION));
        output.accept(PotionContents.createItemStack(Items.POTION, DISTRESS));
        output.accept(PotionContents.createItemStack(Items.POTION, STRONG_DISTRESS));
        output.accept(PotionContents.createItemStack(Items.POTION, EROSION));
        output.accept(PotionContents.createItemStack(Items.POTION, LONG_EROSION));
        output.accept(PotionContents.createItemStack(Items.POTION, STRONG_EROSION));
        output.accept(PotionContents.createItemStack(Items.POTION, CLARITY));
        output.accept(PotionContents.createItemStack(Items.POTION, STRONG_CLARITY));
        output.accept(PotionContents.createItemStack(Items.POTION, LONG_CLARITY));
        output.accept(PotionContents.createItemStack(Items.POTION, SERENITY));
        output.accept(PotionContents.createItemStack(Items.POTION, LONG_SERENITY));
        output.accept(PotionContents.createItemStack(Items.POTION, STRONG_SERENITY));

    }

    public static void register(IEventBus eventBus){
        POTIONS.register(eventBus);
    }
}