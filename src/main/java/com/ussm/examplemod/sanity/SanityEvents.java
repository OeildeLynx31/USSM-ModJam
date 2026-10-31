package com.ussm.examplemod.sanity;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.item.custom.HazmatArmorItem;
import com.ussm.examplemod.potion.ModPotions;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        if (event.getEntity() instanceof ServerPlayer player && player.isAlive() && (player.tickCount + player.getId()) % 20 == 0) {
            Sanity.tick(player);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)
                && event.getEntity() instanceof ServerPlayer player) {
            Sanity.add(player, -Config.ARRIVAL_LOSS.get().floatValue());
        }
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player
                && HazmatArmorItem.isWearingMask(player)
                && ModPotions.affectsSanity(event.getItem())) {
            event.setCanceled(true);
            if (player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.examplemod.mask_blocks_drinking"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("sanity")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            int value = Math.round(Sanity.get(player));
                            context.getSource().sendSuccess(() -> Component.translatable("commands.examplemod.sanity.get", value), false);
                            return value;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, Sanity.MAX))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    float value = FloatArgumentType.getFloat(context, "value");
                                    Sanity.set(player, value);
                                    context.getSource().sendSuccess(() -> Component.translatable("commands.examplemod.sanity.set", Math.round(value)), true);
                                    return 1;
                                }))));
    }
}
