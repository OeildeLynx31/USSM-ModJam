package com.ussm.examplemod.item.custom;

import com.ussm.examplemod.Config;
import com.ussm.examplemod.item.ModItems;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
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

public class KatanaDash {
    public static final int TICKS = 4;

    private static final int HIT_TICKS = TICKS + 3;

    private static final float[] PROGRESS = {0f, 0.3f, 0.6f, 0.85f, 1f};
    private static final double HIT_RADIUS = 1.5;

    private final Level level;
    private final Vec3 waist;
    private final Vec3 direction;
    private final Vec3 path;
    private final float damage;
    private final int cooldown;
    private final ItemStack weapon;
    private final IntSet hit = new IntOpenHashSet();
    private int tick;
    private double reached;
    private boolean reset;
    private boolean shortened;
    private boolean finished;

    private KatanaDash(Level level, Vec3 waist, Vec3 direction, Vec3 path, float damage, int cooldown, ItemStack weapon) {
        this.level = level;
        this.waist = waist;
        this.direction = direction;
        this.path = path;
        this.damage = damage;
        this.cooldown = cooldown;
        this.weapon = weapon;
    }

    public static KatanaDash start(Player player, Vec3 direction, boolean dual) {
        double distance = dual ? Config.DUAL_DASH_DISTANCE.get() : Config.DASH_DISTANCE.get();
        float damage = (dual ? Config.DUAL_DASH_DAMAGE.get() : Config.DASH_DAMAGE.get()).floatValue();
        int cooldown = dual ? Config.DUAL_DASH_COOLDOWN.get() : Config.DASH_COOLDOWN.get();
        Level level = player.level();
        Vec3 waist = player.position().add(0, player.getBbHeight() * 0.5, 0);
        BlockHitResult wall = level.clip(new ClipContext(waist, waist.add(direction.scale(distance)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double travel = wall.getType() == HitResult.Type.MISS ? distance : Math.max(0, wall.getLocation().distanceTo(waist) - 0.5);
        return new KatanaDash(level, waist, direction, direction.scale(travel), damage, cooldown, player.getMainHandItem().copy());
    }

    public int cooldown() {
        return cooldown;
    }

    public boolean isDashing() {
        return !finished && tick < TICKS;
    }

    public boolean tickMovement(Player player) {
        Vec3 motion = player.getDeltaMovement();
        if (tick < TICKS) {
            player.setDeltaMovement(path.scale(PROGRESS[tick + 1] - PROGRESS[tick]));
        } else {
            boolean hovering = tick < TICKS + Config.DASH_HOVER_TICKS.get() && canHover(player);
            if (tick == TICKS) {
                player.setDeltaMovement(Vec3.ZERO);
            } else {
                player.setDeltaMovement(motion.x, hovering ? 0 : motion.y, motion.z);
            }
            if (!hovering) {
                return false;
            }
        }
        tick++;
        return true;
    }

    public boolean tickHits(ServerPlayer player) {
        if (finished || player.level() != level) {
            finished = true;
            return false;
        }
        ServerLevel serverLevel = player.serverLevel();
        Vec3 center = player.position().add(0, player.getBbHeight() * 0.5, 0);
        double progress = Math.clamp(center.subtract(waist).dot(direction), reached, path.length());
        Vec3 from = waist.add(direction.scale(reached));
        Vec3 to = waist.add(direction.scale(progress));
        reached = progress;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(HIT_RADIUS), target -> canHit(player, target))) {
            if (distanceToSegment(target.getBoundingBox().getCenter(), from, to) <= HIT_RADIUS && hit.add(target.getId())) {
                strike(serverLevel, player, target);
            }
        }
        if (to.distanceToSqr(from) > 1.0E-4) {
            int particles = Math.max(1, (int) Math.round(to.distanceTo(from)));
            for (int i = 0; i < particles; i++) {
                Vec3 point = from.lerp(to, (i + 0.5) / particles);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            }
        }
        tick++;
        finished = tick >= HIT_TICKS || reached >= path.length();
        return !finished;
    }

    private void strike(ServerLevel serverLevel, ServerPlayer player, LivingEntity target) {
        DamageSource source = player.damageSources().playerAttack(player);
        float amount = EnchantmentHelper.modifyDamage(serverLevel, weapon, target, source, damage);
        if (target.hurt(source, amount)) {
            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, weapon);
            if (target.isDeadOrDying()) {
                onKill(player, target instanceof Player);
            }
        }
        target.knockback(0.4, -direction.x, -direction.z);
    }

    private void onKill(ServerPlayer player, boolean playerKill) {
        if (reset || (shortened && !playerKill)) {
            return;
        }
        Item katana = ModItems.KATANA.get();
        ItemCooldowns cooldowns = player.getCooldowns();
        int left = playerKill ? 0 : Mth.floor(cooldowns.getCooldownPercent(katana, 0) * cooldown * Config.MOB_KILL_COOLDOWN.get());
        if (left > 0) {
            cooldowns.addCooldown(katana, left);
        } else {
            cooldowns.removeCooldown(katana);
        }
        reset = playerKill;
        shortened = true;
        player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, playerKill ? 1.6f : 1.1f);
    }

    private static boolean canHover(Player player) {
        return !player.isInLiquid() && !player.getAbilities().flying && !player.isFallFlying()
                && player.level().noCollision(player, player.getBoundingBox().move(0, -0.1, 0));
    }

    private static boolean canHit(Player player, LivingEntity target) {
        if (target == player || !target.isAlive() || !target.isPickable()) {
            return false;
        }
        if (target instanceof Player other && !player.canHarmPlayer(other)) {
            return false;
        }
        return !(target instanceof OwnableEntity pet && player.equals(pet.getOwner()));
    }

    private static double distanceToSegment(Vec3 point, Vec3 from, Vec3 to) {
        Vec3 segment = to.subtract(from);
        double lengthSqr = segment.lengthSqr();
        if (lengthSqr == 0) {
            return point.distanceTo(from);
        }
        double t = Math.clamp(point.subtract(from).dot(segment) / lengthSqr, 0.0, 1.0);
        return point.distanceTo(from.add(segment.scale(t)));
    }
}
