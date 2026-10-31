package com.ussm.examplemod.item.custom;

import com.ussm.examplemod.item.ModArmorMaterials;
import com.ussm.examplemod.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HazmatArmorItem extends ArmorItem {
    private static final EquipmentSlot[] SUIT_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public HazmatArmorItem(Type type, Properties properties) {
        super(ModArmorMaterials.HAZMAT, type, properties.durability(type.getDurability(10)));
    }

    public static boolean isWearingFullSuit(LivingEntity entity) {
        for (EquipmentSlot slot : SUIT_SLOTS) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof HazmatArmorItem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWearingMask(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.HAZMAT_MASK.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.examplemod.hazmat").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
