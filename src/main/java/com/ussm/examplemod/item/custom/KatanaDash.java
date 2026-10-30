package com.ussm.examplemod.item.custom;

import com.ussm.examplemod.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class KatanaDash {
    private static final int TICKS = 4;
    //ca part vite et ca freine sur la fin du dash
    private static final float[] PROGRESS = {0f, 0.3f, 0.6f, 0.85f, 1f};

    private final Level level;
    private final Vec3 start;
    private final Vec3 direction;
    private final Vec3 path;
    private final float damage;
    private final int cooldown;
    private final ItemStack katana;
    private final Set<Integer> hitEntities = new HashSet<>();
    private int tick;
    private double reached;
    private boolean killedPlayer;
    private boolean killedMob;
    private boolean finished;

    public KatanaDash(Player player, Vec3 direction, boolean dual) {
        this.level = player.level();
        this.start = new Vec3(player.getX(), player.getY(0.5), player.getZ());
        this.direction = direction;
        this.damage = (dual ? Config.DUAL_DASH_DAMAGE.get() : Config.DASH_DAMAGE.get()).floatValue();
        this.cooldown = dual ? Config.DUAL_DASH_COOLDOWN.get() : Config.DASH_COOLDOWN.get();
        this.katana = player.getMainHandItem().copy();

        double distance = dual ? Config.DUAL_DASH_DISTANCE.get() : Config.DASH_DISTANCE.get();
        BlockHitResult hitResult = level.clip(new ClipContext(
                start,
                start.add(direction.scale(distance)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        if (hitResult.getType() != HitResult.Type.MISS) {
            distance = Math.max(0, hitResult.getLocation().distanceTo(start) - 0.5);
        }
        this.path = direction.scale(distance);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isDashing() {
        return !finished && tick < TICKS;
    }

    public boolean tickMovement(Player player) {
        if (tick < TICKS) {
            player.setDeltaMovement(path.scale(PROGRESS[tick + 1] - PROGRESS[tick]));
        } else {
            if (tick == TICKS) player.setDeltaMovement(Vec3.ZERO);
            // petite suspension en l'air a la fin (10 ticks c'etait trop long)
            if (tick >= TICKS + Config.DASH_HOVER_TICKS.get()) return false;
            if (player.isInLiquid() || player.getAbilities().flying || player.isFallFlying()) return false;
            if (!player.level().noCollision(player, player.getBoundingBox().move(0, -0.1, 0))) return false;

            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
        tick++;
        return true;
    }

    public boolean tickHits(ServerPlayer player) {
        if (finished) return false;
        //si on passe dans l'end en plein dash (sortie de l'undervoid par le haut)
        if (player.level() != level) {
            finished = true;
            return false;
        }

        ServerLevel serverLevel = player.serverLevel();
        Vec3 center = new Vec3(player.getX(), player.getY(0.5), player.getZ());
        double progress = Mth.clamp(center.subtract(start).dot(direction), reached, path.length());
        Vec3 from = start.add(direction.scale(reached));
        Vec3 to = start.add(direction.scale(progress));

        // on tape tout ce qui est a moins de 1.5 bloc du trajet
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(1.5), e -> canHit(player, e))) {
            Vec3 pos = target.getBoundingBox().getCenter();
            double along = Mth.clamp(pos.subtract(start).dot(direction), reached, progress);
            if (pos.distanceTo(start.add(direction.scale(along))) <= 1.5 && hitEntities.add(target.getId())) {
                hurtTarget(serverLevel, player, target);
            }
        }

        double length = from.distanceTo(to);
        if (length > 0.01) {
            int count = Math.max(1, (int) Math.round(length));
            for (int i = 0; i < count; i++) {
                Vec3 pos = from.lerp(to, (i + 0.5) / count);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }
        }

        reached = progress;
        tick++;
        finished = tick >= TICKS + 3 || reached >= path.length();
        return !finished;
    }

    // copié de ThrownTrident#onHitEntity pour que les enchants marchent
    private void hurtTarget(ServerLevel serverLevel, ServerPlayer player, LivingEntity target) {
        DamageSource source = player.damageSources().playerAttack(player);
        float amount = EnchantmentHelper.modifyDamage(serverLevel, katana, target, source, damage);
        if (target.hurt(source, amount)) {
            //System.out.println("dash hit " + target + " " + amount);
            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, katana);
            if (target.isDeadOrDying()) {
                onKill(player, target instanceof Player);
            }
        }
        target.knockback(0.4, -direction.x, -direction.z);
    }

    // kill de joueur = reset complet, kill de mob = juste la moitier du cooldown
    private void onKill(ServerPlayer player, boolean playerKill) {
        if (killedPlayer || (killedMob && !playerKill)) return;

        ItemCooldowns cooldowns = player.getCooldowns();
        Item item = katana.getItem();
        if (playerKill) {
            cooldowns.removeCooldown(item);
            killedPlayer = true;
        } else {
            int left = Mth.floor(cooldowns.getCooldownPercent(item, 0) * cooldown * Config.MOB_KILL_COOLDOWN.get());
            if (left > 0) {
                cooldowns.addCooldown(item, left);
            } else {
                cooldowns.removeCooldown(item);
            }
            killedMob = true;
        }
        player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, playerKill ? 1.6f : 1.1f);
    }

    private static boolean canHit(Player player, LivingEntity target) {
        if (target == player || !target.isAlive() || !target.isPickable()) return false;
        if (target instanceof Player other) return player.canHarmPlayer(other);
        if (target instanceof OwnableEntity pet) return !player.equals(pet.getOwner());
        return true;
    }
}
