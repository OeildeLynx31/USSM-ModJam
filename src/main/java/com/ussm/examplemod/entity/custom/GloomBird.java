package com.ussm.examplemod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumSet;

// Tourne en rond dans le ciel du gloom, inspiré du phantom
public class GloomBird extends FlyingMob {
    public final AnimationState flyAnimationState = new AnimationState();

    private Vec3 anchorPoint = Vec3.ZERO;
    private boolean hasAnchor = false;
    private int anchorCooldown;
    private float angle;
    private float radius = 10.0f;
    private boolean clockwise;

    public GloomBird(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        this.moveControl = new GloomBirdMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new CircleAroundAnchorGoal());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            flyAnimationState.startIfStopped(tickCount);
        }
    }

    private void pickAnchor() {
        BlockPos pos = blockPosition().offset(random.nextInt(33) - 16, 0, random.nextInt(33) - 16);
        if (!level().isLoaded(pos)) {
            pos = blockPosition();
        }
        int ground = level().getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        anchorPoint = new Vec3(pos.getX() + 0.5, ground + 7 + random.nextInt(7), pos.getZ() + 0.5);
        //System.out.println("nouvel anchor " + anchorPoint);
        radius = 6.0f + random.nextFloat() * 8.0f;
        clockwise = random.nextBoolean();
        anchorCooldown = 400 + random.nextInt(400); // 20 à 40 s
        hasAnchor = true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && !level().isClientSide()) {
            // il se sauve plus haut
            anchorPoint = anchorPoint.add(0, 5, 0);
            clockwise = !clockwise;
        }
        return flag;
    }

    // TODO sauvegarder le rayon et le sens aussi ? pas tres important
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (hasAnchor) {
            tag.putDouble("AnchorX", anchorPoint.x);
            tag.putDouble("AnchorY", anchorPoint.y);
            tag.putDouble("AnchorZ", anchorPoint.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AnchorX")) {
            anchorPoint = new Vec3(tag.getDouble("AnchorX"), tag.getDouble("AnchorY"), tag.getDouble("AnchorZ"));
            hasAnchor = true;
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.6f;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    public static boolean checkGloomBirdSpawnRules(EntityType<GloomBird> entityType, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return MobSpawnType.isSpawner(spawnType)
                || pos.getY() >= world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    }

    class CircleAroundAnchorGoal extends Goal {
        public CircleAroundAnchorGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (!hasAnchor || --anchorCooldown <= 0) {
                pickAnchor();
            }
            // Il tape un mur : demi-tour et un peu plus haut
            if (horizontalCollision) {
                clockwise = !clockwise;
                anchorPoint = anchorPoint.add(0, 2, 0);
            }
            if (random.nextInt(300) == 0) {
                radius = 6.0f + random.nextFloat() * 8.0f;
            }

            angle += (clockwise ? -0.3f : 0.3f) / radius;
            double x = anchorPoint.x + Mth.cos(angle) * radius;
            double z = anchorPoint.z + Mth.sin(angle) * radius;
            double y = anchorPoint.y + Mth.sin(angle * 2.0f) * 1.5; // monte et descend un peu
            getMoveControl().setWantedPosition(x, y, z, 1.0);
        }
    }

    class GloomBirdMoveControl extends MoveControl {
        public GloomBirdMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            Vec3 delta = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
            double dist = delta.length();
            if (dist < 0.1) return;

            Vec3 motion = this.mob.getDeltaMovement();
            Vec3 wanted = delta.scale(0.6 / dist);
            motion = motion.add(wanted.subtract(motion).scale(0.08));
            this.mob.setDeltaMovement(motion);

            double horizontal = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            float yaw = (float) (Mth.atan2(motion.z, motion.x) * (180F / (float) Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, 8.0F));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.setXRot((float) -(Mth.atan2(motion.y, horizontal) * (180F / (float) Math.PI)));
        }
    }
}
