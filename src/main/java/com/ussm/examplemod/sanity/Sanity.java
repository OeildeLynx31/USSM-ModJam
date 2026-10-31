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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class Sanity {
    public static final float MAX = 100f;
    public static final float MIN = 0f;
    public static final float TROUBLED = 70f;
    public static final float HALLUCINATING = 40f;
    public static final double GLOOM_SURFACE_Y = 104.0;

    public static float get(Player player) {
        return player.getData(ModAttachments.SANITY);
    }

    public static void set(ServerPlayer player, float value) {
        player.setData(ModAttachments.SANITY, Mth.clamp(value, MIN, MAX));
    }

    public static void add(ServerPlayer player, float amount) {
        if (amount == 0f || player.isCreative() || player.isSpectator()) {
            return;
        }

        if (HazmatArmorItem.isWearingFullSuit(player)) {
            amount *= Config.HAZMAT_MULTIPLIER.get().floatValue();
        } else if (amount < 0) {
            MobEffectInstance serenity = player.getEffect(ModEffects.SERENITY);
            if (serenity != null) {
                amount *= serenity.getAmplifier() > 0 ? 0.25f : 0.5f;
            }
            if (player.hasEffect(ModEffects.EROSION)) {
                if (player.getEffect(ModEffects.EROSION).getAmplifier() >= 1) {
                    amount = amount * 3;
                } else {
                    amount = amount * 2;
                }
            }
        } else if (player.hasEffect(ModEffects.CLARITY)) {
            if (player.getEffect(ModEffects.CLARITY).getAmplifier() >= 1) {
                amount *= 3;
            } else {
                amount *= 2;
            }
        }

//        player.setData(ModAttachments.SANITY, Mth.clamp(get(player) + amount, 0f, MAX));
        float avant = get(player);
        float sanity = Mth.clamp(avant + amount, 0f, MAX);
        if (sanity != avant) {
            player.setData(ModAttachments.SANITY, sanity);
            if (avant >= MAX && sanity < MAX) {
                ModAdvancements.award(player, ModAdvancements.OUCH_MY_HEAD);
            }
        }
    }

    private static DamageSource damageSource(ServerPlayer player, String name) {
        return player.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name)));
    }

    public static void tick(ServerPlayer player) {
        if (player.isSpectator() || player.isCreative()) return;

        Level level = player.level();
        if (level.dimension() == Level.OVERWORLD) {
            if (get(player) < MAX) {
                set(player, MAX);
                player.removeEffect(ModEffects.HALLUCINATIONS);
            }
            return;
        }

        boolean miasma = false;
        if (level.dimension() == ModDimensions.UNDERVOID_DIM_LEVEL_KEY) {
            Holder<Biome> biome = level.getBiome(BlockPos.containing(player.getEyePosition()));
            if (biome.is(ModBiomes.MIASMA)) {
                miasma = true;
                add(player, (float) -Config.MIASMA_SANITY_LOSS.get());
            } else if (biome.is(ModBiomes.BLOOM)) {
                add(player, Config.BLOOM_GAIN.get().floatValue());
            } else if (player.getEyeY() >= GLOOM_SURFACE_Y) {
                add(player, -Config.GLOOM_SURFACE_LOSS.get().floatValue());
            } else {
                add(player, -Config.GLOOM_UNDERGROUND_LOSS.get().floatValue());
            }
        } else {
            add(player, Config.OUTSIDE_GAIN.get().floatValue()); // nether, end...
        }

        float sanity = get(player);
        if (sanity <= 15) {
            applyEffect(player, ModEffects.HALLUCINATIONS, 1);
            applyEffect(player, MobEffects.WEAKNESS, 0);
        } else if (sanity <= 40) {
            applyEffect(player, ModEffects.HALLUCINATIONS, 0);
        }

        // == 0 marchait que pour certains joueurs a cause du decalage dans SanityEvents
        if (sanity <= 0f && player.tickCount % (20 * Config.INSANITY_DAMAGE_SECONDS.get()) < 20) {
            player.hurt(damageSource(player, "insanity"), Config.INSANITY_DAMAGE.get().floatValue());
        }
        if (miasma && !HazmatArmorItem.isWearingFullSuit(player)) {
            if (player.tickCount % (Config.MIASMA_DAMAGE_SECONDS.get() * 20) < 20) {
                player.hurt(damageSource(player, "miasma"), Config.MIASMA_DAMAGE.get().floatValue());
            }
        }

        //euphorie, si t'es trop bien dans l'undervoid ca fait mal aussi
        if (player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY) && sanity > Config.EUPHORIA_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, true));
            if (player.tickCount % (20 * Config.EUPHORIA_DAMAGE_SECONDS.get()) < 20) {
                player.hurt(damageSource(player, "euphoria"), Config.EUPHORIA_DAMAGE.get().floatValue());
            }
        }
    }

    private static void applyEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current == null || current.getAmplifier() < amplifier || current.getDuration() <= 400) {
            player.addEffect(new MobEffectInstance(effect, 600, amplifier, false, false, true));
        }
    }
}