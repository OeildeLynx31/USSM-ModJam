package com.ussm.examplemod.effect;

import com.ussm.examplemod.item.custom.HazmatArmorItem;
import com.ussm.examplemod.sanity.Sanity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SanityInstantEffect extends InstantenousMobEffect {
    private final float amount;

    public SanityInstantEffect(MobEffectCategory category, int color, float amount) {
        super(category, color);
        this.amount = amount;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player
                && !player.isCreative()
                && !player.isSpectator()
                && !HazmatArmorItem.isWearingFullSuit(player)) {
            Sanity.set(player, Sanity.get(player) + amount * (amplifier + 1));
        }
        return true;
    }
}