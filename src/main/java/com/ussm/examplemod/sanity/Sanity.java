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

public class Sanity {
    public static final float MAX = 100f;
    public static final float TROUBLED = 70f;
    public static final float HALLUCINATING = 40f;
    public static final float PANIC = 15f;
    public static final double GLOOM_SURFACE_Y = 104.0;

    public static final ResourceKey<DamageType> INSANITY = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "insanity")
    );
    public static final ResourceKey<DamageType> MIASMA = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "miasma")
    );
    public static final ResourceKey<DamageType> EUPHORIA = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "euphoria")
    );

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

        MobEffectInstance serenity = player.getEffect(ModEffects.SERENITY);
        MobEffectInstance erosion = player.getEffect(ModEffects.EROSION);
        if (HazmatArmorItem.isWearingFullSuit(player)) { // avec la combi les potions servent a rien
            amount *= Config.HAZMAT_MULTIPLIER.get().floatValue();
        } else if (amount < 0f) {
            if (serenity != null) {
                amount *= serenity.getAmplifier() > 0 ? 0.25f : 0.5f;
            }
            if (erosion != null) {
                amount *= erosion.getAmplifier() > 0 ? 3f : 2f;
            }
        } else if (amount > 0f && player.hasEffect(ModEffects.CLARITY)) {
            if (player.getEffect(ModEffects.CLARITY).getAmplifier() > 0) {
                amount *= 3f;
            } else {
                amount *= 2f;
            }
        }

        float current = get(player);
        float updated = Mth.clamp(current + amount, 0f, MAX);
        if (updated == current) return;

        player.setData(ModAttachments.SANITY, updated);
        if (current >= MAX && updated < MAX) {
            ModAdvancements.award(player, ModAdvancements.OUCH_MY_HEAD);
        }
    }

    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        Level level = player.level();
        if (level.dimension().equals(Level.OVERWORLD)) {
            if (get(player) < MAX) {
                set(player, MAX);
                player.removeEffect(ModEffects.HALLUCINATIONS);
            }
            return;
        }

        boolean miasma = false;
        boolean undervoid = level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
        if (undervoid) {
            Holder<Biome> biome = level.getBiome(BlockPos.containing(player.getEyePosition()));
            if (biome.is(ModBiomes.MIASMA)) {
                miasma = true;
                add(player, -Config.MIASMA_SANITY_LOSS.get().floatValue());
            } else if (biome.is(ModBiomes.BLOOM)) {
                add(player, Config.BLOOM_GAIN.get().floatValue());
            } else if (player.getEyeY() >= GLOOM_SURFACE_Y) {
                add(player, -Config.GLOOM_SURFACE_LOSS.get().floatValue());
            } else {
                add(player, -Config.GLOOM_UNDERGROUND_LOSS.get().floatValue());
            }
        } else {
            add(player, Config.OUTSIDE_GAIN.get().floatValue());
        }

        float sanity = get(player);
        if (sanity <= PANIC) {
            refresh(player, ModEffects.HALLUCINATIONS, 1);
            refresh(player, MobEffects.WEAKNESS, 0);
        } else if (sanity <= HALLUCINATING) {
            refresh(player, ModEffects.HALLUCINATIONS, 0);
        }

        if (sanity <= 0f && player.tickCount % (20 * Config.INSANITY_DAMAGE_SECONDS.get()) < 20) {
            player.hurt(player.damageSources().source(INSANITY), Config.INSANITY_DAMAGE.get().floatValue());
        }
        if (miasma && !HazmatArmorItem.isWearingFullSuit(player)
                && player.tickCount % (20 * Config.MIASMA_DAMAGE_SECONDS.get()) < 20) {
            player.hurt(player.damageSources().source(MIASMA), Config.MIASMA_DAMAGE.get().floatValue());
        }

        // trop serein dans l'entre-monde : ca tourne et ca fait mal
        if (undervoid && sanity > Config.EUPHORIA_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, true));
            if (player.tickCount % (20 * Config.EUPHORIA_DAMAGE_SECONDS.get()) < 20) {
                player.hurt(player.damageSources().source(EUPHORIA), Config.EUPHORIA_DAMAGE.get().floatValue());
            }
        }
    }

    private static void refresh(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getAmplifier() >= amplifier && current.getDuration() > 400) {
            return;
        }

        player.addEffect(new MobEffectInstance(effect, 600, amplifier, false, false, true));
    }
}
