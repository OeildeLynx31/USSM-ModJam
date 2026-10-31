package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.item.ModItems;
import com.ussm.examplemod.sanity.Sanity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SmokeBossEntity extends Monster {
    private static final int DASH_WINDUP_TICKS = 15;      // 0,75 s de préparation
    private static final int DASH_DURATION_TICKS = 9;
    private static final double DASH_SPEED = 1.5;
    private static final float DASH_DAMAGE = 10.0f;
    private static final int DASH_COOLDOWN = 140;         // 7 s
    private static final int DASH_COOLDOWN_PHASE_2 = 80;  // 4 s sous 50 % de vie
    private static final double AURA_RANGE = 24.0;
    private static final float AURA_SANITY_LOSS = 1.5f;   // par seconde
    private static final int TELEPORT_COOLDOWN = 100;         // 5 s
    private static final int TELEPORT_COOLDOWN_PHASE_2 = 60;  // 3 s sous 50 % de vie
    private static final double TELEPORT_TRIGGER_DISTANCE = 10.0;
    private static final double TELEPORT_MIN_LANDING = 4.0;
    private static final double TELEPORT_MAX_LANDING = 7.0;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.examplemod.smoke_boss"),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private int dashCooldown = 100;
    private int teleportCooldown = 60;
    private int windup;
    private int dashTicks;
    private boolean dashHit;
    private Vec3 dashDir = Vec3.ZERO;
    private boolean swingOffhandNext;
    private static final float SMOKE_SCALE = 1.2f;
    private static final int SMOKE_PER_TICK = 30;
    private static final float STRENGTH_SANITY_THRESHOLD = 40.0f; // = 40 % de la santé mentale


    public SmokeBossEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        setPersistenceRequired();
        if (!level.isClientSide()) {
            // Un katana dans chaque main, qui ne tombent jamais
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KATANA.get()));
            setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.KATANA.get()));
            setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            setDropChance(EquipmentSlot.OFFHAND, 0.0f);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0f));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (tickCount % 200 == 0 && level().getNearestPlayer(this, 128.0) == null) {
            discard();
            return;
        }

        if (level() instanceof ServerLevel serverLevel) {
            if (tickCount % 20 == 0) {
                boolean weakMind = false;
                for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class,
                        getBoundingBox().inflate(AURA_RANGE))) {
                    if (!player.isCreative() && !player.isSpectator()
                            && Sanity.get(player) < STRENGTH_SANITY_THRESHOLD) {
                        weakMind = true;
                    }
                    Sanity.add(player, -AURA_SANITY_LOSS);
                }
                if (weakMind) {
                    addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, false));
                }
            }
        }

        LivingEntity target = getTarget();
        boolean phase2 = getHealth() < getMaxHealth() * 0.5f;
        if (dashCooldown > 0) {
            dashCooldown--;
        }

        if (teleportCooldown > 0) {
            teleportCooldown--;
        }

        // 1) Le dash en cours
        if (dashTicks > 0) {
            dashTicks--;
            setDeltaMovement(dashDir.x * DASH_SPEED, getDeltaMovement().y, dashDir.z * DASH_SPEED);
            hasImpulse = true;
            if (!dashHit) {
                for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(0.8))) {
                    if (!player.isCreative() && !player.isSpectator()) {
                        player.hurt(damageSources().mobAttack(this),
                                DASH_DAMAGE + (hasEffect(MobEffects.DAMAGE_BOOST) ? 3.0f : 0.0f));
                        player.knockback(1.2, -dashDir.x, -dashDir.z);
                        player.hurtMarked = true;
                        dashHit = true;
                        break;
                    }
                }
            }
            if (dashTicks == 0) {
                dashCooldown = phase2 ? DASH_COOLDOWN_PHASE_2 : DASH_COOLDOWN;
            }
            return;
        }

        // 2) La préparation du dash : il s'immobilise et la fumée se densifie
        if (windup > 0) {
            windup--;
            getNavigation().stop();
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.0, getZ(),
                        12, 0.5, 0.8, 0.5, 0.02);
            }
            if (target != null) {
                getLookControl().setLookAt(target, 30.0f, 30.0f);
            }
            if (windup == 0 && target != null) {
                Vec3 delta = target.position().subtract(position());
                Vec3 flat = new Vec3(delta.x, 0.0, delta.z);
                if (flat.lengthSqr() > 0.0001) {
                    dashDir = flat.normalize();
                    dashTicks = DASH_DURATION_TICKS;
                    dashHit = false;
                    playSound(SoundEvents.PHANTOM_SWOOP, 1.5f, 0.7f);
                }
            }
            return;
        }
// 2b) Téléportation : seulement s'il a une cible (donc s'il est « énervé »)
        if (target != null && teleportCooldown == 0) {
            double distance = distanceTo(target);
            boolean farAway = distance > TELEPORT_TRIGGER_DISTANCE;
            boolean blocked = distance > 5.0 && !hasLineOfSight(target);
            if ((farAway || blocked) && teleportNearTarget(target)) {
                teleportCooldown = phase2 ? TELEPORT_COOLDOWN_PHASE_2 : TELEPORT_COOLDOWN;
                return;
            }
        }
        // 3) Décision : lancer un dash
        if (target != null && dashCooldown == 0) {
            double distance = distanceTo(target);
            if (distance >= 5.0 && distance <= 18.0 && hasLineOfSight(target)) {
                windup = DASH_WINDUP_TICKS;
            }
        }
    }

    // Les deux katanas frappent à tour de rôle
    @Override
    public void swing(InteractionHand hand, boolean updateSelf) {
        swingOffhandNext = !swingOffhandNext;
        super.swing(swingOffhandNext ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, updateSelf);
    }

    // Drops : le réceptacle et un fragment de lame
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.SPAWNER_RECEPTACLE.get()));
        spawnAtLocation(new ItemStack(ModItems.BLADE_FRAGMENT.get()));
    }

    // Barre de boss
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) {
            spawnSmokeBody();
        }
    }

    private void spawnSmokeBody() {
        double rad = Math.toRadians(yBodyRot);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        for (int i = 0; i < SMOKE_PER_TICK; i++) {
            double lx;
            double ly;
            double lz;
            switch (random.nextInt(8)) {
                case 0 -> { // tête
                    lx = (random.nextDouble() - 0.5) * 0.47;
                    ly = 1.41 + random.nextDouble() * 0.47;
                    lz = (random.nextDouble() - 0.5) * 0.47;
                }
                case 1, 2, 3 -> { // torse
                    lx = (random.nextDouble() - 0.5) * 0.47;
                    ly = 0.70 + random.nextDouble() * 0.71;
                    lz = (random.nextDouble() - 0.5) * 0.23;
                }
                case 4, 5 -> { // bras
                    double side = random.nextBoolean() ? 1.0 : -1.0;
                    lx = side * (0.23 + random.nextDouble() * 0.23);
                    ly = 0.70 + random.nextDouble() * 0.71;
                    lz = (random.nextDouble() - 0.5) * 0.23;
                }
                default -> { // jambes
                    double side = random.nextBoolean() ? 1.0 : -1.0;
                    lx = side * random.nextDouble() * 0.23;
                    ly = random.nextDouble() * 0.70;
                    lz = (random.nextDouble() - 0.5) * 0.23;
                }
            }
            lx *= SMOKE_SCALE;
            ly *= SMOKE_SCALE;
            lz *= SMOKE_SCALE;
            // Rotation selon l'orientation du corps
            double x = getX() + lx * cos - lz * sin;
            double z = getZ() + lx * sin + lz * cos;
            level().addParticle(random.nextInt(3) == 0 ? ParticleTypes.SMOKE : ParticleTypes.LARGE_SMOKE,
                    x, getY() + ly, z, 0.0, 0.01, 0.0);
        }
    }
    private boolean teleportNearTarget(LivingEntity target) {
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double dist = TELEPORT_MIN_LANDING
                    + random.nextDouble() * (TELEPORT_MAX_LANDING - TELEPORT_MIN_LANDING);
            double x = target.getX() + Math.cos(angle) * dist;
            double z = target.getZ() + Math.sin(angle) * dist;
            double y = target.getY() + 1.0; // randomTeleport redescend jusqu'au sol

            double fromX = getX();
            double fromY = getY();
            double fromZ = getZ();

            // false = pas de particules violettes d'Enderman
            if (randomTeleport(x, y, z, false)) {
                if (Math.abs(getY() - target.getY()) > 4.0) {
                    // Trop haut ou trop bas par rapport au joueur : on annule et on réessaie
                    teleportTo(fromX, fromY, fromZ);
                    continue;
                }
                smokeBurst(fromX, fromY, fromZ);
                smokeBurst(getX(), getY(), getZ());
                level().playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT,
                        getSoundSource(), 1.0f, 0.6f);
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENDERMAN_TELEPORT,
                        getSoundSource(), 1.0f, 0.6f);
                return true;
            }
        }
        return false;
    }

    private void smokeBurst(double x, double y, double z) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 1.2, z, 60, 0.5, 1.0, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + 1.2, z, 40, 0.5, 1.0, 0.5, 0.05);
        }
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}