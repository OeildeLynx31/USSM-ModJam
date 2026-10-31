package com.ussm.examplemod.sanity;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.item.custom.HazmatArmorItem;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class SanityEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.isAlive()) {
            if ((player.tickCount + player.getId()) % 20 == 0) {
                Sanity.tick(player);
            }
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.getTo() == ModDimensions.UNDERVOID_DIM_LEVEL_KEY && event.getEntity() instanceof ServerPlayer player) {
            Sanity.add(player, -Config.ARRIVAL_LOSS.get().floatValue());
        }
    }

    //TODO bloquer aussi les splash
    @SubscribeEvent
    public static void onDrinkPotion(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player && HazmatArmorItem.isWearingMask(player)) {
            ItemStack stack = event.getItem();
            if (stack.is(Items.POTION)) {
                boolean blocked = false;
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                for (MobEffectInstance effect : contents.getAllEffects()) {
                    if (effect.is(ModEffects.SERENITY) || effect.is(ModEffects.CLARITY) || effect.is(ModEffects.RESTORATION)
                            || effect.is(ModEffects.DISTRESS) || effect.is(ModEffects.EROSION)) {
                        blocked = true;
                    }
                }

                if (blocked) {
                    event.setCanceled(true);
                    if (player.level().isClientSide()) {
                        player.displayClientMessage(Component.translatable("message.examplemod.mask_blocks_drinking"), true);
                    }
                }
            }
        }
    }

    //Commande /sanity pour debug
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("sanity").requires(source -> source.hasPermission(2))
                .then(Commands.literal("get").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    int sanity = Math.round(Sanity.get(player));
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.examplemod.sanity.get", sanity), false);
                    return sanity;
                }))
//                .then(Commands.literal("reset").executes(ctx -> {
//                    Sanity.set(ctx.getSource().getPlayerOrException(), Sanity.MAX);
//                    return 1;
//                }))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, Sanity.MAX))
                                .executes(context -> {
                                    float value = FloatArgumentType.getFloat(context, "value");
                                    Sanity.set(context.getSource().getPlayerOrException(), value);
                                    context.getSource().sendSuccess(() -> Component.translatable("commands.examplemod.sanity.set", Math.round(value)), true);
                                    return 1;
                                }))));
    }
}