package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.ExampleMod;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Mimic extends Monster {
    private static final EntityDimensions CROUCHING = EntityDimensions.scalable(0.6f, 1.5f).withEyeHeight(1.27f);
    private static final EntityDataAccessor<Optional<UUID>> COPIED_ID = SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> COPIED_NAME = SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> REVEALED = SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.BOOLEAN);

    private static final AttributeModifier REVEAL_SPEED = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "mimic_reveal"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EquipmentSlot[] COPIED_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final LivingEntity.Fallsounds FALL_SOUNDS = new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);

    private static final double VICTIM_RANGE_SQR = 32.0 * 32.0;
    private static final double MIRROR_RANGE_SQR = 32.0 * 32.0;
    private static final double REVEAL_RANGE_SQR = 20.0 * 20.0;
    private static final int MIN_REVEAL_DELAY = 300;
    private static final int MAX_REVEAL_DELAY = 900;

    private int revealIn;
    @Nullable
    private Player original;
    @Nullable
    private Player victim;
    @Nullable
    private Component displayName;

    public Mimic(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.addGoal(3, new FollowVictimGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                target -> isRevealed() && (!target.getUUID().equals(getCopiedId()) || findVictim() == target)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COPIED_ID, Optional.empty());
        builder.define(COPIED_NAME, "");
        builder.define(REVEALED, false);
    }

    @Nullable
    public UUID getCopiedId() {
        return entityData.get(COPIED_ID).orElse(null);
    }

    public String getCopiedName() {
        return entityData.get(COPIED_NAME);
    }

    public boolean isRevealed() {
        return entityData.get(REVEALED);
    }

    public void copy(Player source) {
        entityData.set(COPIED_ID, Optional.of(source.getUUID()));
        entityData.set(COPIED_NAME, source.getGameProfile().getName());
        for (EquipmentSlot slot : COPIED_SLOTS) {
            setItemSlot(slot, source.getItemBySlot(slot).copy());
            setDropChance(slot, 0f);
        }
        setLeftHanded(source.getMainArm() == HumanoidArm.LEFT);
        original = source;
        revealIn = random.nextIntBetweenInclusive(MIN_REVEAL_DELAY, MAX_REVEAL_DELAY);
    }

    @Override
    public Component getName() {
        String name = getCopiedName();
        if (name.isEmpty()) {
            return super.getName();
        }
        if (displayName == null) {
            displayName = isRevealed()
                    ? Component.literal(name).withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.DARK_RED)
                    : Component.literal(name);
        }
        return displayName;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (COPIED_NAME.equals(key) || REVEALED.equals(key)) {
            displayName = null;
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? CROUCHING : super.getDefaultDimensions(pose);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (isRevealed()) {
            return;
        }
        if ((tickCount + getId()) % 10 == 0) {
            victim = findVictim();
        }
        mirror(original());
        if (revealIn > 0) {
            revealIn--;
            return;
        }
        Player player = victim();
        if (player != null && distanceToSqr(player) < REVEAL_RANGE_SQR) {
            reveal(player);
        }
    }

    @Nullable
    private Player findVictim() {
        UUID copied = getCopiedId();
        List<? extends Player> players = level().players();
        Player nearest = null;
        Player copiedPlayer = null;
        double best = VICTIM_RANGE_SQR;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                continue;
            }
            double distance = distanceToSqr(player);
            if (distance >= VICTIM_RANGE_SQR) {
                continue;
            }
            if (player.getUUID().equals(copied)) {
                copiedPlayer = player;
            } else if (distance < best) {
                nearest = player;
                best = distance;
            }
        }
        return nearest != null ? nearest : copiedPlayer;
    }

    @Nullable
    private Player victim() {
        Player player = victim;
        return player != null && player.isAlive() && player.level() == level() ? player : null;
    }

    @Nullable
    private Player original() {
        UUID id = getCopiedId();
        if (id == null) {
            return null;
        }
        if ((original == null || original.isRemoved()) && (tickCount + getId()) % 20 == 0) {
            original = level().getPlayerByUUID(id);
        }
        Player player = original;
        return player != null && !player.isRemoved() && player.level() == level() && distanceToSqr(player) < MIRROR_RANGE_SQR ? player : null;
    }

    private void mirror(@Nullable Player player) {
        setShiftKeyDown(player != null && player.isShiftKeyDown());
        setPose(player != null && player.isCrouching() ? Pose.CROUCHING : Pose.STANDING);
        if (player != null && player.swinging && !swinging) {
            swing(InteractionHand.MAIN_HAND);
        }
    }

    private void reveal(@Nullable LivingEntity target) {
        entityData.set(REVEALED, true);
        applyRevealSpeed();
        setShiftKeyDown(false);
        if (isCrouching()) {
            setPose(Pose.STANDING);
        }
        setTarget(target != null ? target : findVictim());
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1f, 0.8f);
    }

    private void applyRevealSpeed() {
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(REVEAL_SPEED.id())) {
            speed.addTransientModifier(REVEAL_SPEED);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !isRevealed() && !level().isClientSide()) {
            reveal(source.getEntity() instanceof Player player ? player : victim());
        }
        return hurt;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        boolean leftHanded = isLeftHanded();
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (getCopiedId() != null) {
            setLeftHanded(leftHanded);
        } else {
            Player player = level.getNearestPlayer(this, 64);
            if (player != null) {
                copy(player);
            }
        }
        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID id = getCopiedId();
        if (id != null) {
            tag.putUUID("CopiedId", id);
        }
        tag.putString("CopiedName", getCopiedName());
        tag.putBoolean("Revealed", isRevealed());
        tag.putInt("RevealIn", revealIn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(COPIED_ID, tag.hasUUID("CopiedId") ? Optional.of(tag.getUUID("CopiedId")) : Optional.empty());
        entityData.set(COPIED_NAME, tag.getString("CopiedName"));
        entityData.set(REVEALED, tag.getBoolean("Revealed"));
        revealIn = tag.getInt("RevealIn");
        if (isRevealed()) {
            applyRevealSpeed();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return FALL_SOUNDS;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    private static class FollowVictimGoal extends Goal {
        private static final double NEAR_SQR = 3.0 * 3.0;
        private static final double FAR_SQR = 6.0 * 6.0;

        private final Mimic mimic;
        @Nullable
        private Player followed;
        private int recalc;

        FollowVictimGoal(Mimic mimic) {
            this.mimic = mimic;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            followed = mimic.isRevealed() || mimic.getTarget() != null ? null : mimic.victim();
            return followed != null;
        }

        @Override
        public void start() {
            recalc = 0;
        }

        @Override
        public void stop() {
            followed = null;
            mimic.getNavigation().stop();
        }

        @Override
        public void tick() {
            Player player = followed;
            if (player == null) {
                return;
            }
            mimic.getLookControl().setLookAt(player, 10f, mimic.getMaxHeadXRot());
            if (--recalc > 0) {
                return;
            }
            recalc = adjustedTickDelay(10);
            double distance = mimic.distanceToSqr(player);
            if (distance > FAR_SQR) {
                mimic.getNavigation().moveTo(player, 1.0);
            } else if (distance < NEAR_SQR) {
                Vec3 away = DefaultRandomPos.getPosAway(mimic, 6, 3, player.position());
                if (away != null) {
                    mimic.getNavigation().moveTo(away.x, away.y, away.z, 1.0);
                }
            } else {
                mimic.getNavigation().stop();
            }
        }
    }
}
