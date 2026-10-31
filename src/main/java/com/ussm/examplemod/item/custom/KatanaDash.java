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

import java.util.ArrayList;
import java.util.List;

public class KatanaDash {
    private static final int DASH_TICKS = 4;
    // ca part vite et ca freine a la fin
    private static final float[] PROGRESS = {0f, 0.3f, 0.6f, 0.85f, 1f};

    private Level level;
    private Vec3 start;
    private Vec3 dir;
    private Vec3 path;
    private float damage;
    private int cooldown;
    private ItemStack katana;
    private List<LivingEntity> touched = new ArrayList<>();

    private int tick = 0;
    private double reached = 0;
    private boolean killedPlayer, killedMob;
    private boolean finished = false;

    public KatanaDash(Player player, Vec3 dir, boolean dual) {
        this.dir = dir;
        level = player.level();
        start = new Vec3(player.getX(), player.getY(0.5), player.getZ());
        katana = player.getMainHandItem().copy();

        damage = Config.DASH_DAMAGE.get().floatValue();
        cooldown = Config.DASH_COOLDOWN.get();
        double distance = Config.DASH_DISTANCE.get();
        if (dual) {
            damage = Config.DUAL_DASH_DAMAGE.get().floatValue();
            cooldown = Config.DUAL_DASH_COOLDOWN.get();
            distance = Config.DUAL_DASH_DISTANCE.get();
        }

        BlockHitResult hit = level.clip(new ClipContext(start, start.add(dir.scale(distance)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.MISS) {
            distance = Math.max(0, hit.getLocation().distanceTo(start) - 0.5);
        }
        path = dir.scale(distance);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isDashing() {
        return !finished && tick < DASH_TICKS;
    }

    public boolean tickMovement(Player player) {
        if (tick < DASH_TICKS) {
            //player.setDeltaMovement(path.scale(1.0 / DASH_TICKS));
            player.setDeltaMovement(path.scale(PROGRESS[tick + 1] - PROGRESS[tick]));
            tick++;
            return true;
        }

        if (tick == DASH_TICKS) player.setDeltaMovement(Vec3.ZERO);
        boolean ground = !player.level().noCollision(player, player.getBoundingBox().move(0, -0.1, 0));
        if (ground || tick >= DASH_TICKS + Config.DASH_HOVER_TICKS.get()
                || player.isInLiquid() || player.getAbilities().flying || player.isFallFlying()) {
            return false;
        }

        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, 0, motion.z);
        tick++;
        return true;
    }

    public boolean tickHits(ServerPlayer player) {
        if(finished) return false;
        if (player.level() != level) {
            finished = true;
            return false;
        }

        ServerLevel serverLevel = player.serverLevel();
        Vec3 pos = new Vec3(player.getX(), player.getY(0.5), player.getZ());
        double progress = pos.subtract(start).dot(dir);
        if (progress < reached) progress = reached;
        if (progress > path.length()) progress = path.length();
        Vec3 from = start.add(dir.scale(reached));
        Vec3 to = start.add(dir.scale(progress));

        List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(1.5), e -> canHit(player, e) && !touched.contains(e));
        for (LivingEntity target : list) {
            //if (target.distanceTo(player) > 2) continue;
            Vec3 c = target.getBoundingBox().getCenter();
            double d = Mth.clamp(c.subtract(start).dot(dir), reached, progress);
            if (c.distanceTo(start.add(dir.scale(d))) > 1.5) continue;

            touched.add(target);
            // copié de ThrownTrident, comme ca tranchant/aura de feu marchent aussi
            DamageSource source = player.damageSources().playerAttack(player);
            float amount = EnchantmentHelper.modifyDamage(serverLevel, katana, target, source, damage);
            //System.out.println("dash hit " + target.getName().getString() + " " + amount);
            if (target.hurt(source, amount)) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, katana);
                if (target.isDeadOrDying()) onKill(player, target instanceof Player);
            }
            target.knockback(0.4, -dir.x, -dir.z);
        }

        double len = from.distanceTo(to);
        int nb = Math.max(1, (int) Math.round(len));
        if (len > 0.01) {
            for (int i = 0; i < nb; i++) {
                Vec3 p = from.lerp(to, (i + 0.5) / nb);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        }

        reached = progress;
        tick++;
        if (tick >= DASH_TICKS + 3 || reached >= path.length()) finished = true;
        return !finished;
    }

    // tuer un joueur reset le cd, un mob le reduit juste
    private void onKill(ServerPlayer player, boolean isPlayer) {
        if (killedPlayer) return;

        ItemCooldowns cooldowns = player.getCooldowns();
        Item item = katana.getItem();
        if (isPlayer) {
            cooldowns.removeCooldown(item);
            killedPlayer = true;
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.6f);
        } else if (!killedMob) {
            int left = Mth.floor(cooldowns.getCooldownPercent(item, 0) * cooldown * Config.MOB_KILL_COOLDOWN.get());
            if (left > 0) cooldowns.addCooldown(item, left);
            else cooldowns.removeCooldown(item);
            killedMob = true;
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.1f);
        }
    }

    private static boolean canHit(Player player, LivingEntity e) {
        if (e == player || !e.isAlive() || !e.isPickable()) return false;
        if (e instanceof Player other) return player.canHarmPlayer(other);
        if (e instanceof OwnableEntity pet && player.equals(pet.getOwner())) return false;
        return true;
    }
}
