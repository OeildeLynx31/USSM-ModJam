package com.ussm.examplemod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FloatingLight extends AmbientCreature {
    public final AnimationState floatAnimationState = new AnimationState();

    @Nullable
    private BlockPos targetPos;
    private int wanderCooldown;

    public FloatingLight(EntityType<? extends AmbientCreature> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            floatAnimationState.startIfStopped(tickCount);
            if (random.nextInt(5) == 0) {
                this.level().addParticle(ParticleTypes.END_ROD, // GLOW rendait pas bien
                        getRandomX(0.6), getY() + 0.3 + random.nextDouble() * 0.4, getRandomZ(0.6),
                        (random.nextDouble() - 0.5) * 0.01, 0.01, (random.nextDouble() - 0.5) * 0.01);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (targetPos != null && !level().isEmptyBlock(targetPos)) {
            targetPos = null;
        }
        // nouvelle cible toutes les 4-8 sec, ou quand il est arrivé
        if (targetPos == null || --wanderCooldown <= 0 || targetPos.closerToCenterThan(position(), 1.0)) {
            wanderCooldown = 80 + random.nextInt(80);
            targetPos = BlockPos.containing(
                    getX() + random.nextInt(7) - 3,
                    getY() + random.nextInt(3) - 1,
                    getZ() + random.nextInt(7) - 3);
        }

        double dx = targetPos.getX() + 0.5 - getX();
        double dy = targetPos.getY() + 0.5 - getY();
        double dz = targetPos.getZ() + 0.5 - getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.05) {
            double speed = 0.05 / distance;
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(
                    motion.x + (dx * speed - motion.x) * 0.05,
                    motion.y + (dy * speed - motion.y) * 0.05,
                    motion.z + (dz * speed - motion.z) * 0.05
            );
        }
    }

    // Pareil que le papillon : pas de gravité, on peut pas le pousser etc
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    protected void pushEntities() {}

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {}

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.3f;
    }

    public static boolean checkFloatingLightSpawnRules(EntityType<FloatingLight> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return MobSpawnType.isSpawner(reason) || level.isEmptyBlock(pos);
    }
}
