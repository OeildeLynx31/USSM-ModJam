package com.ussm.examplemod.entity.custom;

import com.ussm.examplemod.sanity.Sanity;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HerobrineEntity extends PathfinderMob {
    private static final double VANISH_DISTANCE = 14.0;
    private static final int MAX_LIFETIME = 1200;          // 60 secondes
    private static final int STARE_TICKS_TO_VANISH = 60;   // 3 secondes de regard
    private static final float SANITY_LOSS_PER_SECOND = 2.0f;
    public static boolean debugMode = false;

    private int stareTicks;

    public HerobrineEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }
        Player player = level().getNearestPlayer(this, 128.0);
        if (player == null) {
            vanish();
            return;
        }
        // Mode normal seulement : durée de vie et distance
        if (!debugMode && (tickCount > MAX_LIFETIME
                || distanceToSqr(player) < VANISH_DISTANCE * VANISH_DISTANCE)) {
            vanish();
            return;
        }

        faceTarget(player);

        if (isLookedAtBy(player)) {
            stareTicks++;
            if (player instanceof ServerPlayer serverPlayer && tickCount % 20 == 0
                    && level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
                Sanity.add(serverPlayer, -SANITY_LOSS_PER_SECOND);
            }
            if (!debugMode && stareTicks >= STARE_TICKS_TO_VANISH) {
                vanish();
            }
        } else {
            stareTicks = 0;
        }
    }

    private void faceTarget(Player player) {
        Vec3 delta = player.getEyePosition().subtract(getEyePosition());
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float yaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0 / Math.PI)) - 90.0f;
        float pitch = (float) (-(Mth.atan2(delta.y, horizontal) * (180.0 / Math.PI)));
        setYRot(yaw);
        setXRot(pitch);
        setYHeadRot(yaw);
        setYBodyRot(yaw);
    }

    // Même principe que l'Enderman : le joueur le regarde-t-il en face ?
    private boolean isLookedAtBy(Player player) {
        Vec3 view = player.getViewVector(1.0f).normalize();
        Vec3 toMe = new Vec3(getX() - player.getX(), getEyeY() - player.getEyeY(), getZ() - player.getZ());
        double distance = toMe.length();
        toMe = toMe.normalize();
        return view.dot(toMe) > 1.0 - 0.1 / distance && player.hasLineOfSight(this);
    }

    public void vanish() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 1.0, getZ(),
                    20, 0.3, 0.6, 0.3, 0.02);
        }
        discard();
    }

    // Le frapper le fait disparaître
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide()) {
            vanish();
        }
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    // Jamais sauvegardé avec le monde
    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}