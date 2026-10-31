package com.ussm.examplemod.item.custom;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.item.ModToolTiers;
import com.ussm.examplemod.util.ModAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class KatanaItem extends SwordItem {
    public KatanaItem(Properties pProperties) {
        super(ModToolTiers.KATANA, pProperties.attributes(SwordItem.createAttributes(ModToolTiers.KATANA, 5, -2.2f))); // 8 dmg en jeu, 1.8 de vitesse
    }

    public static boolean isKatana(ItemStack stack) {
        return stack.getItem() instanceof KatanaItem;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND || player.isPassenger()) {
            return InteractionResultHolder.pass(stack);
        }

        // accroupi = parade
        if (player.isShiftKeyDown()) {
            player.getCooldowns().addCooldown(this, Config.PARRY_COOLDOWN.get());
            if (!level.isClientSide()) {
                player.setData(ModAttachments.KATANA_PARRY, level.getGameTime() + Config.PARRY_TICKS.get());
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8f, 1.8f);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        Vec3 look = player.getLookAngle();
        if (player.onGround() && look.y < 0) { // sinon on fonce dans le sol
            look = Vec3.directionFromRotation(0, player.getYRot());
        }
        boolean dual = isKatana(player.getOffhandItem());

        KatanaDash dash = new KatanaDash(player, look, dual);
        player.setData(ModAttachments.KATANA_DASH, dash);
        player.resetFallDistance();
        player.getCooldowns().addCooldown(this, dash.getCooldown());

        if (player instanceof ServerPlayer serverPlayer) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 1.2f);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            if (dual) {
                player.getOffhandItem().hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                ModAdvancements.award(serverPlayer, ModAdvancements.TWICE_AS_SHARP);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
