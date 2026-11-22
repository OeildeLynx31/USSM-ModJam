package com.ussm.the_undervoid.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import com.ussm.the_undervoid.item.ModItems;

public class VoidBallProjectile extends ThrowableItemProjectile {

    public static final double RADIUS = 8.0;
    private static final double PULL_STRENGTH = 0.25; // au centre, ca baisse jusqu'au bord
    private static final double CORE_RADIUS = 1.5;

    public static final int DURATION = 100; // 5 secondes

    // synchro pour que le client arrete la boule au bon endroit et lance l'anim tout de suite
    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(VoidBallProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> CENTER =
            SynchedEntityData.defineId(VoidBallProjectile.class, EntityDataSerializers.VECTOR3);

    private int duration = 0;

    public VoidBallProjectile(
            EntityType<? extends VoidBallProjectile> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    public VoidBallProjectile(
            EntityType<? extends VoidBallProjectile> entityType,
            LivingEntity owner,
            Level level
    ) {
        super(entityType, owner, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTIVE, false);
        builder.define(CENTER, new Vector3f());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (CENTER.equals(key) && level().isClientSide()) {
            Vector3f c = entityData.get(CENTER);
            setPos(c.x, c.y, c.z);
            setOldPosAndRot();
            setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.VOID_BALL.get();
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public int getDuration() {
        return duration;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        // un demi bloc devant la face touchée, sinon elle s'ouvre dans le sol
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        activate(result.getLocation().add(normal.scale(0.5)));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            result.getEntity().hurt(damageSources().thrown(this, getOwner()), 2.0F);
        }
        activate(result.getLocation());
    }

    private void activate(Vec3 position) {
        if (isActive() || level().isClientSide()) {
            return;
        }

        setPos(position.x, position.y, position.z);
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        entityData.set(CENTER, position.toVector3f());
        entityData.set(ACTIVE, true);

        level().playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 0.8F, 1.6F);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.6F, 0.5F);
    }

    @Override
    public void tick() {
        if (!isActive()) {
            super.tick();
            if (level().isClientSide()) {
                // petite trainee violette
                for (int i = 0; i < 2; i++) {
                    level().addParticle(ParticleTypes.PORTAL, getX(), getY(), getZ(),
                            (random.nextDouble() - 0.5) * 0.6, (random.nextDouble() - 0.5) * 0.6, (random.nextDouble() - 0.5) * 0.6);
                }
            }
            return;
        }

        // ouverte : elle bouge plus du tout, plus de physique de projectile
        baseTick();
        duration++;

        if (level().isClientSide()) {
            vortexParticles();
            return;
        }

        attractEntities();

        if (duration % 20 == 0) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.PORTAL_AMBIENT, SoundSource.NEUTRAL, 0.35F, 1.8F);
        }
        if (duration >= DURATION) {
            implode();
        }
    }

    private void vortexParticles() {
        // les particules de portail partent de (x + dx) et finissent sur (x), donc elles tombent toutes dans le centre
        int count = duration > DURATION - 10 ? 2 : 6;
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double dist = 1.5 + random.nextDouble() * (RADIUS - 2.0);
            level().addParticle(ParticleTypes.PORTAL, getX(), getY(), getZ(),
                    Math.cos(angle) * dist, (random.nextDouble() - 0.5) * 2.0, Math.sin(angle) * dist);
        }
        // un anneau qui tourne autour du coeur
        double spin = duration * 0.35;
        for (int i = 0; i < 2; i++) {
            double a = spin + i * Math.PI;
            level().addParticle(ParticleTypes.REVERSE_PORTAL, getX() + Math.cos(a) * 1.2, getY(), getZ() + Math.sin(a) * 1.2, 0, 0, 0);
        }
        if (duration % 3 == 0) {
            level().addParticle(ParticleTypes.SQUID_INK, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    private boolean canPull(Entity entity) {
        if (!entity.isAlive() || entity == getOwner() || entity instanceof VoidBallProjectile) return false;
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return false;
        return entity instanceof LivingEntity || entity instanceof ItemEntity || entity instanceof ExperienceOrb;
    }

    private void attractEntities() {

        Vec3 center = position();

        for (Entity entity : level().getEntities(this, getBoundingBox().inflate(RADIUS), this::canPull)) {

            Vec3 direction = center.subtract(entity.position().add(0, entity.getBbHeight() * 0.5, 0));

            double distance = direction.length();

            if (distance > RADIUS || distance < 0.01) {
                continue;
            }

            // plus on est pres plus ca tire, au bord c'est presque rien
            double strength = PULL_STRENGTH * (1.0 - distance / RADIUS) + 0.02;
            Vec3 motion = entity.getDeltaMovement().add(direction.scale(strength / distance));

            if (distance < CORE_RADIUS) {
                // on freine au centre sinon ca fait des allers retours
                motion = motion.scale(0.6);
                if (entity instanceof LivingEntity living && duration % 10 == 0) {
                    living.hurt(damageSources().indirectMagic(this, getOwner()), 1.0F);
                }
            }

            entity.setDeltaMovement(motion);
            entity.hurtMarked = true;
        }
    }

    // a la fin le vide se referme et recrache tout
    private void implode() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY(), getZ(), 80, 0.2, 0.2, 0.2, 0.5);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        }
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 0.5F, 1.8F);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 0.6F);

        Vec3 center = position();
        for (Entity entity : level().getEntities(this, getBoundingBox().inflate(4.0), this::canPull)) {
            Vec3 away = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(center);
            double distance = away.length();
            if (distance > 4.0 || distance < 0.01) continue;
            entity.setDeltaMovement(entity.getDeltaMovement().add(away.scale(0.9 * (1.0 - distance / 4.0) / distance)).add(0, 0.3, 0));
            entity.hurtMarked = true;
        }

        discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("Activated", isActive());
        tag.putInt("Duration", duration);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        duration = tag.getInt("Duration");

        if (tag.getBoolean("Activated")) {
            setNoGravity(true);
            entityData.set(CENTER, position().toVector3f());
            entityData.set(ACTIVE, true);
        }
    }
}
