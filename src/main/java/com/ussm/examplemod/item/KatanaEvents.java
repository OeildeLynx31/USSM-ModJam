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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
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
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.isUsingItem() && isOffhandLocked(player, player.getUsedItemHand())) {
            player.stopUsingItem();
        }

        KatanaDash dash = player.getExistingDataOrNull(ModAttachments.KATANA_DASH);
        // on garde le dash tant qu'on a pas vraiment retouché le sol sinon on se mange les degats de chute
        if (dash != null && !dash.tickHits(player) && player.onGround() && player.fallDistance == 0) {
            player.removeData(ModAttachments.KATANA_DASH);
        }
    }

    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "katana_speed");

    // un katana en main = plus rapide, deux katanas = encore plus
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || (event.getSlot() != EquipmentSlot.MAINHAND && event.getSlot() != EquipmentSlot.OFFHAND)) return;

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        speed.removeModifier(SPEED_ID);
        if (KatanaItem.isKatana(player.getMainHandItem())) {
            double bonus = KatanaItem.isKatana(player.getOffhandItem()) ? Config.DUAL_KATANA_SPEED.get() : Config.KATANA_SPEED.get();
            speed.addTransientModifier(new AttributeModifier(SPEED_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
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
        if (!(event.getEntity() instanceof ServerPlayer player) || !(source.getEntity() instanceof LivingEntity attacker)) return;

        // la parade marche que sur les coups au cac
        //if (source.getDirectEntity() instanceof Projectile) return;
        if (source.getDirectEntity() != attacker) return;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypeTags.BYPASSES_SHIELD)) return;

        if (player.getData(ModAttachments.KATANA_PARRY) > player.level().getGameTime()) {
            event.setCanceled(true);
            parry(player, attacker);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            warn(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (isOffhandLocked(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    private static void parry(ServerPlayer player, LivingEntity attacker) {
        player.removeData(ModAttachments.KATANA_PARRY);
        player.getCooldowns().removeCooldown(ModItems.KATANA.get());

        attacker.knockback(1.0, player.getX() - attacker.getX(), player.getZ() - attacker.getZ());
        //sinon l'autre joueur recule pas, vu dans le code du ravageur (blockedByShield)
        if (attacker instanceof Player) attacker.hurtMarked = true;

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1f, 1.3f);
        level.sendParticles(
                ParticleTypes.CRIT,
                player.getX(),
                player.getY(0.6),
                player.getZ(),
                12,
                0.4, 0.4, 0.4,
                0.3
        );
    }

    //main gauche bloquée sauf si y'a un 2eme katana
    private static boolean isOffhandLocked(Player player, InteractionHand hand) {
        if (hand != InteractionHand.OFF_HAND || !KatanaItem.isKatana(player.getMainHandItem())) return false;

        ItemStack offhand = player.getOffhandItem();
        return !offhand.isEmpty() && !KatanaItem.isKatana(offhand);
    }

    // TODO le message spam quand on reste appuyé sur clic droit
    private static void warn(Player player) {
        if (player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.examplemod.offhand_locked"), true);
        }
    }

    @SubscribeEvent
    public static void onUseTotem(LivingUseTotemEvent event) {
        if (event.getEntity() instanceof Player player && isOffhandLocked(player, event.getHandHolding())) {
            event.setCanceled(true);
        }
    }
}
