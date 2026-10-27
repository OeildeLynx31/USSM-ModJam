package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.entity.ModEntities;
import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.util.ModAdvancements;
import com.ussm.examplemod.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Hedgehog extends Animal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> CURLED = SynchedEntityData.defineId(Hedgehog.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation CURL = RawAnimation.begin().thenPlayAndHold("curl");

    private static final int PET_COOLDOWN = 1200;
    private static final int CURL_WHEN_HURT = 100;
    private static final int CURL_WHEN_STARTLED = 60;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int curlTicks;
    private long lastPetTime = -PET_COOLDOWN;

    public Hedgehog(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    public static boolean checkHedgehogSpawnRules(EntityType<Hedgehog> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.HEDGEHOG_SPAWNABLE_ON);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.HEDGEHOG_SPAWNABLE_ON) ? 10f : super.getWalkTargetValue(pos, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        goalSelector.addGoal(2, new TemptGoal(this, 1.1, stack -> stack.is(ModTags.Items.HEDGEHOG_FOOD), false));
        goalSelector.addGoal(3, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURLED, false);
    }

    public boolean isCurled() {
        return entityData.get(CURLED);
    }

    private void curl(int ticks) {
        curlTicks = Math.max(curlTicks, ticks);
        if (!isCurled()) {
            entityData.set(CURLED, true);
            getNavigation().stop();
            playSound(SoundEvents.ARMADILLO_ROLL, 1f, 1.3f);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) {
            return;
        }
        if (curlTicks > 0 && --curlTicks == 0) {
            entityData.set(CURLED, false);
            playSound(SoundEvents.ARMADILLO_UNROLL_FINISH, 1f, 1.3f);
        }
        if (tickCount % 10 == 0 && !isCurled()) {
            Player player = level().getNearestPlayer(this, 3.0);
            if (player != null && player.isSprinting()) {
                curl(CURL_WHEN_STARTLED);
            }
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isCurled();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean curled = isCurled();
        if (curled && !source.is(DamageTypes.THORNS) && !source.is(DamageTypeTags.IS_PROJECTILE)
                && source.getDirectEntity() instanceof LivingEntity attacker) {
            attacker.hurt(damageSources().thorns(this), 2f);
        }
        boolean hurt = super.hurt(source, curled ? amount * 0.25f : amount);
        if (hurt && !level().isClientSide()) {
            curl(CURL_WHEN_HURT);
        }
        return hurt;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || !player.getItemInHand(hand).isEmpty()) {
            return super.mobInteract(player, hand);
        }
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (isCurled()) {
            player.hurt(damageSources().thorns(this), 1f);
            return InteractionResult.CONSUME;
        }
        long time = level().getGameTime();
        if (time - lastPetTime >= PET_COOLDOWN && player instanceof ServerPlayer serverPlayer) {
            lastPetTime = time;
            Sanity.add(serverPlayer, Config.HEDGEHOG_PET_GAIN.get().floatValue());
            ModAdvancements.award(serverPlayer, ModAdvancements.PRICKLY_FRIEND);
            ((ServerLevel) level()).sendParticles(ParticleTypes.HEART, getX(), getY() + 0.5, getZ(), 3, 0.3, 0.2, 0.3, 0);
            playSound(SoundEvents.ARMADILLO_AMBIENT, 1f, 1.6f);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("LastPetTime", lastPetTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LastPetTime")) {
            lastPetTime = tag.getLong("LastPetTime");
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.HEDGEHOG_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.HEDGEHOG.get().create(level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ARMADILLO_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isCurled() ? SoundEvents.ARMADILLO_HURT_REDUCED : SoundEvents.ARMADILLO_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMADILLO_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.4f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (isCurled()) {
                return state.setAndContinue(CURL);
            }
            return state.setAndContinue(state.isMoving() ? WALK : IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
