package com.ussm.examplemod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// Juste du décor dans les grottes du bloom
public class BloomButterfly extends AmbientCreature {
    public final AnimationState flyAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    @Nullable
    private BlockPos targetPosition;

    public BloomButterfly(EntityType<? extends AmbientCreature> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0D);
    }

    public static boolean checkButterflySpawnRules(EntityType<BloomButterfly> butterfly, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return MobSpawnType.isSpawner(spawnType) || level.isEmptyBlock(pos);
    }

    private void setupAnimationStates() {
        this.flyAnimationState.startIfStopped(this.tickCount);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    // Repris de la chauve souris (Bat) mais en plus lent
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.targetPosition != null
                && (!this.level().isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level().getMinBuildHeight())) {
            this.targetPosition = null;
        }

        if (this.targetPosition == null || this.random.nextInt(60) == 0 || this.targetPosition.closerToCenterThan(this.position(), 1.5)) {
            this.targetPosition = BlockPos.containing(
                    //this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7),
                    this.getX() + (double)this.random.nextInt(9) - 4.0,
                    this.getY() + (double)this.random.nextInt(5) - 2.0,
                    this.getZ() + (double)this.random.nextInt(9) - 4.0);
        }

        double d2 = (double)this.targetPosition.getX() + 0.5 - this.getX();
        double d0 = (double)this.targetPosition.getY() + 0.5 - this.getY();
        double d1 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
        Vec3 vec3 = this.getDeltaMovement();
        // le sin c'est pour qu'il sautille un peu
        Vec3 vec31 = vec3.add(
                (Math.signum(d2) * 0.2 - vec3.x) * 0.1,
                (Math.signum(d0) * 0.12 - vec3.y) * 0.1 + Mth.sin(this.tickCount * 0.5f) * 0.04,
                (Math.signum(d1) * 0.2 - vec3.z) * 0.1);
        this.setDeltaMovement(vec31);
        float f = (float)(Mth.atan2(vec31.z, vec31.x) * (180F / (float)Math.PI)) - 90.0F;
        float f1 = Mth.wrapDegrees(f - this.getYRot());
        this.setYRot(this.getYRot() + Mth.clamp(f1, -20.0F, 20.0F));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPORE_BLOSSOM_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPORE_BLOSSOM_BREAK;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    // pas sur que ca serve a quelque chose mais la bat le fait
    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }
}
