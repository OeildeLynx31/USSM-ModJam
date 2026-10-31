package com.ussm.examplemod.item;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.item.custom.KatanaDash;
import com.ussm.examplemod.item.custom.KatanaItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class KatanaEvents {
    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        //if(!player.level().isClientSide()) {
        if(player.isLocalPlayer()) {
            KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
            if(dash != null && !dash.tickMovement(player)) {
                player.removeData(ModAttachments.KATANA_DASH);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if(player.isUsingItem() && isOffhandLocked(player, player.getUsedItemHand())) {
            player.stopUsingItem();
        }

        KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
        //onGround tout seul suffit pas, on prenait quand meme des degats de chute
        if(dash != null && !dash.tickHits(player) && player.onGround() && player.fallDistance == 0) {
            player.removeData(ModAttachments.KATANA_DASH);
        }
    }

    @SubscribeEvent
    public static void noFallDamage(LivingFallEvent event) {
        if(event.getEntity() instanceof ServerPlayer player && player.hasData(ModAttachments.KATANA_DASH)) {
            event.setCanceled(true);
        }
    }

    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "katana_speed");

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        if(event.getSlot() != EquipmentSlot.MAINHAND && event.getSlot() != EquipmentSlot.OFFHAND) return;

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        speed.removeModifier(SPEED_ID);
        if(KatanaItem.isKatana(player.getMainHandItem())) {
            double bonus = Config.KATANA_SPEED.get();
            if(KatanaItem.isKatana(player.getOffhandItem())) bonus = Config.DUAL_KATANA_SPEED.get();
            speed.addTransientModifier(new AttributeModifier(SPEED_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @SubscribeEvent
    public static void dashInvulnerability(EntityInvulnerabilityCheckEvent event) {
        if(event.getEntity() instanceof ServerPlayer player && player.hasData(ModAttachments.KATANA_DASH)) {
            if(player.getData(ModAttachments.KATANA_DASH).isDashing() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                event.setInvulnerable(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if(event.getEntity() instanceof ServerPlayer player && source.getEntity() instanceof LivingEntity attacker) {
            // que les coups au cac, pas les creepers
            if(source.getDirectEntity() != attacker || source.is(DamageTypeTags.IS_EXPLOSION)) return;
            if(source.is(DamageTypeTags.BYPASSES_SHIELD) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

            if(player.getData(ModAttachments.KATANA_PARRY) > player.level().getGameTime()) {
                event.setCanceled(true);
                player.removeData(ModAttachments.KATANA_PARRY);
                player.getCooldowns().removeCooldown(ModItems.KATANA.get());

                attacker.knockback(1.0, player.getX() - attacker.getX(), player.getZ() - attacker.getZ());
                if(attacker instanceof Player) attacker.hurtMarked = true; //sinon le kb marche pas sur les joueur

                ServerLevel level = player.serverLevel();
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1f, 1.3f);
                level.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY(0.6), player.getZ(), 12, 0.4, 0.4, 0.4, 0.3);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if(isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if(isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    //main gauche bloquée sauf si y'a un 2eme katana (ou un totem)
    private static boolean isOffhandLocked(Player player, InteractionHand hand) {
        if(hand != InteractionHand.OFF_HAND || !KatanaItem.isKatana(player.getMainHandItem())) return false;

        ItemStack offhand = player.getOffhandItem();
        return !offhand.isEmpty() && !KatanaItem.isKatana(offhand) && !offhand.is(Items.TOTEM_OF_UNDYING);
    }

    // TODO limiter a 1 msg par seconde, la ca spam quand on reste appuyé
    private static void warn(Player player) {
        if(player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.examplemod.offhand_locked"), true);
        }
    }
}
