package com.ussm.examplemod;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.IntValue VOID_SURVIVAL_TICKS;

    public static final ModConfigSpec.DoubleValue GLOOM_SURFACE_LOSS;
    public static final ModConfigSpec.DoubleValue GLOOM_UNDERGROUND_LOSS;
    public static final ModConfigSpec.DoubleValue BLOOM_GAIN;
    public static final ModConfigSpec.DoubleValue OUTSIDE_GAIN;
    public static final ModConfigSpec.DoubleValue ARRIVAL_LOSS;
    public static final ModConfigSpec.DoubleValue HAZMAT_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue HEDGEHOG_PET_GAIN;

    public static final ModConfigSpec.DoubleValue DASH_DISTANCE;
    public static final ModConfigSpec.DoubleValue DUAL_DASH_DISTANCE;
    public static final ModConfigSpec.DoubleValue DASH_DAMAGE;
    public static final ModConfigSpec.DoubleValue DUAL_DASH_DAMAGE;
    public static final ModConfigSpec.IntValue DASH_COOLDOWN;
    public static final ModConfigSpec.IntValue DUAL_DASH_COOLDOWN;
    public static final ModConfigSpec.IntValue DASH_HOVER_TICKS;
    public static final ModConfigSpec.DoubleValue MOB_KILL_COOLDOWN;
    public static final ModConfigSpec.IntValue PARRY_TICKS;
    public static final ModConfigSpec.IntValue PARRY_COOLDOWN;

    public static final ModConfigSpec.DoubleValue MIMIC_SANITY_THRESHOLD;
    public static final ModConfigSpec.DoubleValue MIMIC_CHANCE;

    public static final ModConfigSpec.DoubleValue MIASMA_SANITY_LOSS;
    public static final ModConfigSpec.DoubleValue MIASMA_DAMAGE;
    public static final ModConfigSpec.IntValue MIASMA_DAMAGE_SECONDS;

    public static final ModConfigSpec.DoubleValue HALLUCINATION_INTENSITY;

    static {
        ModConfigSpec.Builder server = new ModConfigSpec.Builder();

        server.push("void");
        VOID_SURVIVAL_TICKS = server.defineInRange("voidSurvivalTicks", 200, 1, 6000);
        server.pop();

        server.push("sanity");
        GLOOM_SURFACE_LOSS = server.defineInRange("gloomSurfaceLoss", 0.4, 0.0, 10.0);
        GLOOM_UNDERGROUND_LOSS = server.defineInRange("gloomUndergroundLoss", 0.2, 0.0, 10.0);
        BLOOM_GAIN = server.defineInRange("bloomGain", 0.25, 0.0, 10.0);
        OUTSIDE_GAIN = server.defineInRange("outsideGain", 0.1, 0.0, 10.0);
        ARRIVAL_LOSS = server.defineInRange("arrivalLoss", 10.0, 0.0, 100.0);
        HAZMAT_MULTIPLIER = server.defineInRange("hazmatMultiplier", 0.5, 0.0, 1.0);
        HEDGEHOG_PET_GAIN = server.defineInRange("hedgehogPetGain", 8.0, 0.0, 100.0);
        server.pop();

        server.push("katana");
        DASH_DISTANCE = server.defineInRange("dashDistance", 7.0, 1.0, 16.0);
        DUAL_DASH_DISTANCE = server.defineInRange("dualDashDistance", 8.0, 1.0, 16.0);
        DASH_DAMAGE = server.defineInRange("dashDamage", 12.0, 0.0, 100.0);
        DUAL_DASH_DAMAGE = server.defineInRange("dualDashDamage", 30.0, 0.0, 100.0);
        DASH_COOLDOWN = server.defineInRange("dashCooldown", 20, 0, 1200);
        DUAL_DASH_COOLDOWN = server.defineInRange("dualDashCooldown", 40, 0, 1200);
        DASH_HOVER_TICKS = server.defineInRange("dashHoverTicks", 4, 0, 100);
        MOB_KILL_COOLDOWN = server.defineInRange("mobKillCooldown", 0.5, 0.0, 1.0);
        PARRY_TICKS = server.defineInRange("parryTicks", 6, 1, 40);
        PARRY_COOLDOWN = server.defineInRange("parryCooldown", 20, 0, 200);
        server.pop();

        server.push("mimic");
        MIMIC_SANITY_THRESHOLD = server.defineInRange("mimicSanityThreshold", 70.0, 0.0, 100.0);
        MIMIC_CHANCE = server.defineInRange("mimicChancePerSecond", 0.02, 0.0, 1.0);
        server.pop();

        server.push("miasma");
        MIASMA_SANITY_LOSS = server.defineInRange("miasmaSanityLoss", 3.0, 0.0, 100.0);
        MIASMA_DAMAGE = server.defineInRange("miasmaDamage", 1.0, 0.0, 20.0);
        MIASMA_DAMAGE_SECONDS = server.defineInRange("miasmaDamageSeconds", 3, 1, 60);
        server.pop();

        SERVER_SPEC = server.build();

        ModConfigSpec.Builder client = new ModConfigSpec.Builder();
        HALLUCINATION_INTENSITY = client.defineInRange("hallucinationIntensity", 1.0, 0.0, 1.0);
        CLIENT_SPEC = client.build();
    }
}
