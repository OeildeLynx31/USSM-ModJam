package com.ussm.examplemod.sanity;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.attachment.ModAttachments;
import com.ussm.examplemod.effect.ModEffects;
import com.ussm.examplemod.item.custom.HazmatArmorItem;
import com.ussm.examplemod.util.ModAdvancements;
import com.ussm.examplemod.worldgen.biome.ModBiomes;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class Sanity {
    public static final float MAX = 100f;
    public static final float TROUBLED = 70f;
    public static final float HALLUCINATING = 40f;
    public static final float PANIC = 15f;
    public static final double GLOOM_SURFACE_Y = 104.0;

    public static final ResourceKey<DamageType> INSANITY = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "insanity"));

    public static final ResourceKey<DamageType> MIASMA = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "miasma"));

    private static final int EFFECT_DURATION = 600;
    private static final int EFFECT_REFRESH_BELOW = 400;

    public static float get(Player player) {
        return player.getData(ModAttachments.SANITY);
    }

    public static void set(ServerPlayer player, float value) {
        player.setData(ModAttachments.SANITY, Mth.clamp(value, 0f, MAX));
    }

    public static void add(ServerPlayer player, float amount) {
        if (amount == 0f || player.isCreative() || player.isSpectator()) {
            return;
        }
        float current = get(player);
        float multiplier = amount < 0f ? lossMultiplier(player) : gainMultiplier(player);
        float updated = Mth.clamp(current + amount * multiplier, 0f, MAX);
        if (updated == current) {
            return;
        }
        player.setData(ModAttachments.SANITY, updated);
        if (current >= MAX && updated < MAX) {
            ModAdvancements.award(player, ModAdvancements.OUCH_MY_HEAD);
        }
    }

    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.level().dimension().equals(Level.OVERWORLD)) {
            if (get(player) < MAX) {
                set(player, MAX);
                player.removeEffect(ModEffects.HALLUCINATIONS);
            }
            return;
        }
        Holder<Biome> biome = player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)
                ? player.level().getBiome(BlockPos.containing(player.getEyePosition())) : null;
        boolean miasma = biome != null && biome.is(ModBiomes.MIASMA);
        add(player, variationPerSecond(player, biome, miasma));

        float sanity = get(player);
        if (sanity <= HALLUCINATING) {
            refresh(player, ModEffects.HALLUCINATIONS, sanity <= PANIC ? 1 : 0);
        }
        if (sanity <= PANIC) {
            refresh(player, MobEffects.WEAKNESS, 0);
        }
        if (sanity <= 0f && player.tickCount % 40 < 20) {
            player.hurt(player.damageSources().source(INSANITY), 1f);
        }
        if (miasma && !HazmatArmorItem.isWearingFullSuit(player)
                && player.tickCount % (20 * Config.MIASMA_DAMAGE_SECONDS.get()) < 20) {
            player.hurt(player.damageSources().source(MIASMA), Config.MIASMA_DAMAGE.get().floatValue());
        }
    }

    private static float variationPerSecond(ServerPlayer player, @Nullable Holder<Biome> biome, boolean miasma) {
        if (biome == null) {
            return Config.OUTSIDE_GAIN.get().floatValue();
        }
        if (miasma) {
            return -Config.MIASMA_SANITY_LOSS.get().floatValue();
        }
        if (biome.is(ModBiomes.BLOOM)) {
            return Config.BLOOM_GAIN.get().floatValue();
        }
        if (player.getEyeY() >= GLOOM_SURFACE_Y) {
            return -Config.GLOOM_SURFACE_LOSS.get().floatValue();
        }
        return -Config.GLOOM_UNDERGROUND_LOSS.get().floatValue();
    }

    private static float lossMultiplier(Player player) {
        if (HazmatArmorItem.isWearingFullSuit(player)) {
            return Config.HAZMAT_MULTIPLIER.get().floatValue();
        }
        float multiplier = 1f;
        MobEffectInstance serenity = player.getEffect(ModEffects.SERENITY);
        if (serenity != null) {
            multiplier *= serenity.getAmplifier() > 0 ? 0.25f : 0.5f;
        }
        MobEffectInstance erosion = player.getEffect(ModEffects.EROSION);
        if (erosion != null) {
            multiplier *= erosion.getAmplifier() > 0 ? 3f : 2f;
        }
        return multiplier;
    }

    private static float gainMultiplier(Player player) {
        if (HazmatArmorItem.isWearingFullSuit(player)) {
            return Config.HAZMAT_MULTIPLIER.get().floatValue();
        }
        MobEffectInstance clarity = player.getEffect(ModEffects.CLARITY);
        if (clarity == null) {
            return 1f;
        }
        return clarity.getAmplifier() > 0 ? 3f : 2f;
    }

    private static void refresh(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current == null || current.getAmplifier() < amplifier || current.getDuration() <= EFFECT_REFRESH_BELOW) {
            player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier, false, false, true));
        }
    }
}
