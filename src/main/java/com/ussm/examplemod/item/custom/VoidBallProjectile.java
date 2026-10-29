package com.ussm.examplemod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.ussm.examplemod.item.ModItems;

import java.util.List;

public class VoidBallProjectile extends ThrowableItemProjectile {

    private static final double RADIUS = 8.0;
    private static final double PULL_STRENGTH = 0.15;

    private static final int DURATION = 100; // 5 secondes

    private boolean activated = false;
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
    protected Item getDefaultItem() {
        return ModItems.VOID_BALL.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        activate(result.getLocation());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        activate(result.getLocation());
    }

    private void activate(Vec3 position) {
        if (activated || level().isClientSide()) {
            return;
        }

        activated = true;

        setPos(position.x, position.y, position.z);
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide() || !activated) {
            return;
        }

        duration++;

        attractEntities();

        if (duration >= DURATION) {
            discard();
        }
    }

    private void attractEntities() {

        Vec3 center = position();

        AABB area = new AABB(
                center.x - RADIUS,
                center.y - RADIUS,
                center.z - RADIUS,
                center.x + RADIUS,
                center.y + RADIUS,
                center.z + RADIUS
        );

        List<Entity> entities = level().getEntities(
                this,
                area,
                entity -> entity.isAlive()
                        && entity != getOwner()
        );

        for (Entity entity : entities) {

            Vec3 direction = center.subtract(entity.position());

            double distance = direction.length();

            if (distance < 0.5 || distance > RADIUS) {
                continue;
            }

            Vec3 pull = direction.normalize().scale(PULL_STRENGTH);

            entity.setDeltaMovement(
                    entity.getDeltaMovement().add(pull)
            );

            entity.hurtMarked = true;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("Activated", activated);
        tag.putInt("Duration", duration);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        activated = tag.getBoolean("Activated");
        duration = tag.getInt("Duration");

        if (activated) {
            setNoGravity(true);
        }
    }
}