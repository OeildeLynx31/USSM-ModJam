package com.ussm.examplemod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CarnivorousPlant extends Monster {
    public final AnimationState watchAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState yawnAnimationState = new AnimationState();
    private int animationTimeout = 0;

    private int attackTicks;
    private int attackCooldown;
    private int yawnCooldown = 200;

    public CarnivorousPlant(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PlantAttackGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 12f, 1f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 12D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1D) // elle reculait quand on la tapait
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1D);
    }

    // Comme le husk mais canSeeSky marchait pas sous les arbres, du coup heightmap
    public static boolean checkPlantSpawnRules(EntityType<CarnivorousPlant> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!MobSpawnType.isSpawner(spawnType)) {
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            if (pos.getY() < surfaceY) {
                return false;
            }
        }
        return checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            setupAnimationStates();
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (attackTicks > 0) {
            attackTicks--;
            // A 1s dans l'anim la machoire se referme, c'est là qu'on tape
            if (attackTicks == 20) {
                //this.playSound(SoundEvents.GENERIC_EAT, 1f, 1f);
                playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1f, 0.8f + random.nextFloat() * 0.3f);
                LivingEntity target = getTarget();
                // 2.75 et pas 2.5 comme dans le goal sinon le joueur recule pendant l'anim et elle rate a chaque fois
                if (target != null && target.isAlive() && distanceToSqr(target) <= 2.75 * 2.75) {
                    doHurtTarget(target);
                }
            }
        }

        if (getTarget() == null && attackTicks == 0) {
            yawnCooldown--;
            if (yawnCooldown <= 0) {
                yawnCooldown = 300 + random.nextInt(400);
                level().broadcastEntityEvent(this, (byte) 62);
                // TODO trouver un vrai son de baillement
                playSound(SoundEvents.SNIFFER_IDLE, 1f, 0.7f);
            }
        }
        //System.out.println("yawn dans " + yawnCooldown);
    }

    private void setupAnimationStates() {
        if (this.animationTimeout > 0) {
            --this.animationTimeout;
            if (this.animationTimeout == 0) {
                this.watchAnimationState.stop();
                this.attackAnimationState.stop();
                this.yawnAnimationState.stop();
            }
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (!this.level().isClientSide() && target != null && this.getTarget() == null) {
            this.level().broadcastEntityEvent(this, (byte) 61);
        }
        super.setTarget(target);
    }

    // 4 c'est l'attaque comme pour l'iron golem, 61 et 62 je les ai repris du warden (wiki entity statuses)
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            stopAnimations();
            this.attackAnimationState.start(this.tickCount);
            this.animationTimeout = 40; // durée de l'anim en ticks
        } else if (id == 61) {
            if (!this.attackAnimationState.isStarted()) {
                stopAnimations();
                this.watchAnimationState.start(this.tickCount);
                this.animationTimeout = 40;
            }
        } else if (id == 62) {
            stopAnimations();
            this.yawnAnimationState.start(this.tickCount);
            this.animationTimeout = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void stopAnimations() {
        this.watchAnimationState.stop();
        this.attackAnimationState.stop();
        this.yawnAnimationState.stop();
    }

    public boolean isPlayingAnimation() {
        return this.watchAnimationState.isStarted() || this.attackAnimationState.isStarted() || this.yawnAnimationState.isStarted();
    }

    // C'est une plante, ça bouge pas, meme dans l'eau
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        //return SoundEvents.GRASS_BREAK;
        return SoundEvents.BIG_DRIPLEAF_TILT_DOWN;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHORUS_FLOWER_DEATH;
    }

    static class PlantAttackGoal extends Goal {
        private final CarnivorousPlant plant;

        public PlantAttackGoal(CarnivorousPlant plant) {
            this.plant = plant;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = plant.getTarget();
            return target != null && target.isAlive();
        }

        // Sinon tick() est appelé qu'un tick sur deux
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = plant.getTarget();
            if (target == null) {
                return;
            }

            plant.getLookControl().setLookAt(target, 30f, 30f);
            if (plant.attackTicks == 0 && plant.attackCooldown == 0 && plant.distanceToSqr(target) <= 2.5 * 2.5) {
                plant.attackTicks = 40;
                plant.attackCooldown = 40;
                plant.level().broadcastEntityEvent(plant, (byte) 4);
            }
        }
    }
}
