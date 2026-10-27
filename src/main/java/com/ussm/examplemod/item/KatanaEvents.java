package com.ussm.examplemod.item;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.item.custom.KatanaDash;
import com.ussm.examplemod.item.custom.KatanaItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class KatanaEvents {
    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.isLocalPlayer()) {
            KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
            if (dash != null && !dash.tickMovement(player)) {
                player.removeData(ModAttachments.KATANA_DASH);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.isUsingItem() && isLocked(player, player.getUsedItemHand())) {
                player.stopUsingItem();
            }
            KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
            if (dash != null && !dash.tickHits(player) && player.onGround()) {
                player.removeData(ModAttachments.KATANA_DASH);
            }
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasData(ModAttachments.KATANA_DASH)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
            if (dash != null && dash.isDashing()) {
                event.setInvulnerable(true);
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(event.getEntity() instanceof ServerPlayer player) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker
                && !source.is(DamageTypeTags.IS_EXPLOSION) && !source.is(DamageTypeTags.BYPASSES_SHIELD)
                && player.getData(ModAttachments.KATANA_PARRY) > player.level().getGameTime()) {
            event.setCanceled(true);
            parry(player, attacker);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (isLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (isLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void onUseTotem(LivingUseTotemEvent event) {
        if (event.getEntity() instanceof Player player && isLocked(player, event.getHandHolding())) {
            event.setCanceled(true);
        }
    }

    private static void parry(ServerPlayer player, LivingEntity attacker) {
        player.removeData(ModAttachments.KATANA_PARRY);
        player.getCooldowns().removeCooldown(ModItems.KATANA.get());
        attacker.knockback(1.0, player.getX() - attacker.getX(), player.getZ() - attacker.getZ());
        if (attacker instanceof Player) {
            attacker.hurtMarked = true;
        }
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1f, 1.3f);
        level.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY(0.6), player.getZ(), 12, 0.4, 0.4, 0.4, 0.3);
    }

    private static boolean isLocked(Player player, InteractionHand hand) {
        if (hand != InteractionHand.OFF_HAND || !KatanaItem.isKatana(player.getMainHandItem())) {
            return false;
        }
        ItemStack offhand = player.getOffhandItem();
        return !offhand.isEmpty() && !KatanaItem.isKatana(offhand);
    }

    private static void warn(Player player) {
        if (player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.examplemod.offhand_locked"), true);
        }
    }
}
