package com.ussm.examplemod.potion;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSanityTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MODID);

    public static final Supplier<CreativeModeTab> SANITY_POTIONS = TABS.register("sanity_potions",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab." + ExampleMod.MODID + ".sanity_potions"))
                    .icon(() -> PotionContents.createItemStack(Items.POTION, ModPotions.RESTORATION))
                    .displayItems((parameters, output) -> ModPotions.addNewPotions(output))
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}