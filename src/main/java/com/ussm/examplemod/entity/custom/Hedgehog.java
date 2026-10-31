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
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
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
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Hedgehog extends Animal implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation CURL_ANIM = RawAnimation.begin().thenPlayAndHold("curl");

    private static final EntityDataAccessor<Boolean> CURLED =
            SynchedEntityData.defineId(Hedgehog.class, EntityDataSerializers.BOOLEAN);

    private int curlTimer = 0;
    private long lastPet;

    public Hedgehog(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, stack -> stack.is(ModTags.Items.HEDGEHOG_FOOD), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public static boolean checkHedgehogSpawnRules(EntityType<Hedgehog> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.HEDGEHOG_SPAWNABLE_ON);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<Hedgehog> event) {
        if (isCurled())
            return event.setAndContinue(CURL_ANIM);

        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    // TODO mettre des vrais sons, la c ceux du tatou en plus aigu
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ARMADILLO_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (isCurled()) return SoundEvents.ARMADILLO_HURT_REDUCED;
        return SoundEvents.ARMADILLO_HURT;
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURLED, false);
    }

    public boolean isCurled() {
        return this.entityData.get(CURLED);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isCurled();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(level().isClientSide()) return;

        if (curlTimer > 0) {
            curlTimer--;
            if (curlTimer == 0) {
                this.entityData.set(CURLED, false);
                playSound(SoundEvents.ARMADILLO_UNROLL_FINISH, 1f, 1.3f);
            }
        }

        if (!isCurled() && this.tickCount % 10 == 0) {
            Player player = level().getNearestPlayer(this, 3.0);
            if (player != null && player.isSprinting()) {
                curlTimer = 60;
                this.entityData.set(CURLED, true);
                this.getNavigation().stop();
                playSound(SoundEvents.ARMADILLO_ROLL, 1f, 1.3f);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isCurled()) {
            // copié du guardian
            if (!source.is(DamageTypes.THORNS) && source.getDirectEntity() instanceof LivingEntity livingentity) {
                livingentity.hurt(this.damageSources().thorns(this), 2.0F);
            }
            //amount = 0;
            amount = amount / 4;
        }

        boolean flag = super.hurt(source, amount);
        if (flag) {
            curlTimer = 100;
            if (!isCurled()) {
                entityData.set(CURLED, true);
                getNavigation().stop();
                playSound(SoundEvents.ARMADILLO_ROLL, 1f, 1.3f);
            }
        }
        return flag;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty()) {
            if (!this.level().isClientSide) {
                if (isCurled()) {
                    player.hurt(damageSources().thorns(this), 1f);
                } else if (player instanceof ServerPlayer serverPlayer) {
                    long time = this.level().getGameTime();
                    if (lastPet == 0 || time - lastPet >= 20 * 60) { // 1 min entre 2 carresses
                        lastPet = time;
                        //System.out.println("caresse " + time);
                        Sanity.add(serverPlayer, Config.HEDGEHOG_PET_GAIN.get().floatValue());
                        ModAdvancements.award(serverPlayer, ModAdvancements.PRICKLY_FRIEND);
                        ServerLevel serverLevel = (ServerLevel) level();
                        serverLevel.sendParticles(ParticleTypes.HEART, getX(), getY() + 0.5, getZ(), 3, 0.3, 0.2, 0.3, 0);
                        playSound(SoundEvents.ARMADILLO_AMBIENT, 1f, 1.6f);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("LastPetTime", this.lastPet);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.lastPet = compound.getLong("LastPetTime");
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getBlockState(pos.below()).is(ModTags.Blocks.HEDGEHOG_SPAWNABLE_ON)) {
            return 10f;
        }
        return super.getWalkTargetValue(pos, level);
    }
}
