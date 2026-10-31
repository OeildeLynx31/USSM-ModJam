package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MimicReplayGoal extends Goal {
    private final Mimic mimic;
    private MovementClip clip;
    private int index, stuckTicks;
    private float yawOffset;
    private int cooldown = 40;

    public MimicReplayGoal(Mimic mimic) {
        this.mimic = mimic;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (mimic.isRevealed() || mimic.getTarget() != null || mimic.isPassenger() || mimic.isInWater() || mimic.isInLava() || !mimic.onGround()) {
            return false;
        }
        clip = mimic.pickMove();
        if (clip != null && chooseOrientation()) {
            return true;
        }
        clip = null;
        cooldown = 60 + mimic.getRandom().nextInt(60);
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return clip != null && index < clip.size() && !mimic.isRevealed() && mimic.getTarget() == null
                && stuckTicks < 25 && !mimic.isInWater() && !mimic.isInLava();
    }

    @Override
    public void start() {
        index = 0;
        stuckTicks = 0;
        mimic.getNavigation().stop();
        mimic.setReplaying(true);
    }

    @Override
    public void stop() {
        clip = null;
        mimic.setReplaying(false);
        mimic.setShiftKeyDown(false);
        mimic.setSprinting(false);
        cooldown = 20 + mimic.getRandom().nextInt(80);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (clip == null || index >= clip.size()) {
            return;
        }
        int i = index++;

        float yaw = clip.yaw(i) + yawOffset;
        mimic.setYRot(yaw);
        mimic.setYHeadRot(yaw);
        mimic.setXRot(clip.pitch(i));

        // https://fr.wikipedia.org/wiki/Matrice_de_rotation
        double rad = yawOffset * Mth.DEG_TO_RAD;
        double mx = clip.dx(i) * Math.cos(rad) - clip.dz(i) * Math.sin(rad);
        double mz = clip.dx(i) * Math.sin(rad) + clip.dz(i) * Math.cos(rad);
        double horizontal = mx * mx + mz * mz;
        if (horizontal > 1.0E-4 && !hasGround(mimic.level(), mimic.position().add(mx * 4, 0, mz * 4))) {
            stuckTicks = Integer.MAX_VALUE; // un peu bourrin
            return;
        }
        mimic.setDeltaMovement(mx, mimic.getDeltaMovement().y, mz);

        boolean blocked = mimic.horizontalCollision && horizontal > 0.001;
        if (blocked) stuckTicks++;
        if (blocked || clip.has(i, MovementClip.JUMP)) {
            mimic.getJumpControl().jump();
        }
        mimic.setShiftKeyDown(clip.has(i, MovementClip.SNEAK));
        mimic.setSprinting(clip.has(i, MovementClip.SPRINT));
        if (clip.has(i, MovementClip.SWING)) {
            mimic.swing(InteractionHand.MAIN_HAND);
        }
    }

    private boolean chooseOrientation() {
        float base = Mth.wrapDegrees(mimic.getYRot() - clip.yaw(0));
        Player player = mimic.level().getNearestPlayer(mimic, 48);
        int start = mimic.getRandom().nextInt(4);
        double best = Double.MAX_VALUE;
        // on teste les 4 sens et on garde celui qui finit le plus pres du joueur
        for (int k = 0; k < 4; k++) {
            float offset = base + 90.0F * ((start + k) % 4);
            Vec3 end = pathEnd(offset);
            if (end == null) continue;

            if (player == null) { // personne autour
                yawOffset = offset;
                return true;
            }
            double dist = end.distanceToSqr(player.position());
            if (dist < best) {
                best = dist;
                yawOffset = offset;
            }
        }
        return best != Double.MAX_VALUE;
    }

    private Vec3 pathEnd(float offset) {
        Level level = mimic.level();
        double rad = offset * Mth.DEG_TO_RAD;
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = mimic.getX();
        double z = mimic.getZ();
        for (int i = 0; i < clip.size(); i++) {
            double dx = clip.dx(i);
            double dz = clip.dz(i);
            x += dx * cos - dz * sin;
            z += dx * sin + dz * cos;
            if (i % 5 == 4) { // tout les 5 ticks ca suffit
                Vec3 point = new Vec3(x, mimic.getY(), z);
                BlockPos head = BlockPos.containing(point).above();
                if (!level.getBlockState(head).getCollisionShape(level, head).isEmpty()) return null;
                if (!hasGround(level, point)) return null;
            }
        }
        return new Vec3(x, mimic.getY(), z);
    }

    private static boolean hasGround(Level level, Vec3 point) {
        BlockPos pos = BlockPos.containing(point);
        for (int i = 0; i <= 3; i++) {
            BlockPos below = pos.below(i);
            BlockState state = level.getBlockState(below);
            if (!state.getFluidState().isEmpty()) return false;
            if (!state.getCollisionShape(level, below).isEmpty()) return true;
        }
        return false;
    }
}