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
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

// quand personne est assez pres, il rejoue un enregistrement du joueur copié (sa demarche, ses sauts, sa facon de
// regarder autour), tourné pour coller au terrain et plutot vers le joueur le plus proche
public class MimicReplayGoal extends Goal {
    private static final int MAX_FALL = 3;

    private final Mimic mimic;
    @Nullable
    private MovementClip clip;
    private int index, stuckTicks;
    private float yawOffset;
    private int cooldown = 40;

    public MimicReplayGoal(Mimic mimic) {
        this.mimic = mimic;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
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
        if (clip == null || !chooseOrientation()) {
            clip = null;
            cooldown = 60 + mimic.getRandom().nextInt(60);
            return false;
        }
        return true;
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

        double[] move = rotate(clip.dx(i), clip.dz(i), yawOffset);
        double horizontal = move[0] * move[0] + move[1] * move[1];
        if (horizontal > 1.0E-4 && !hasGround(mimic.level(), mimic.position().add(move[0] * 4, 0, move[1] * 4))) {
            stuckTicks = Integer.MAX_VALUE; // un trou devant, on arrete la
            return;
        }
        Vec3 velocity = mimic.getDeltaMovement();
        mimic.setDeltaMovement(move[0], velocity.y, move[1]);

        boolean blocked = mimic.horizontalCollision && horizontal > 1.0E-3;
        if (clip.has(i, MovementClip.JUMP) || blocked) {
            mimic.getJumpControl().jump();
        }
        if (blocked) {
            stuckTicks++;
        }
        mimic.setShiftKeyDown(clip.has(i, MovementClip.SNEAK));
        mimic.setSprinting(clip.has(i, MovementClip.SPRINT));
        if (clip.has(i, MovementClip.SWING)) {
            mimic.swing(InteractionHand.MAIN_HAND);
        }
    }

    // on essaie les 4 orientations, on garde celle qui finit le plus pres du joueur sans passer dans un mur ou un trou
    private boolean chooseOrientation() {
        float base = Mth.wrapDegrees(mimic.getYRot() - clip.yaw(0));
        Player player = mimic.level().getNearestPlayer(mimic, 48);
        int first = mimic.getRandom().nextInt(4);
        boolean found = false;
        double best = Double.MAX_VALUE;
        for (int k = 0; k < 4; k++) {
            float offset = base + 90.0F * ((first + k) % 4);
            Vec3 end = pathEnd(offset);
            if (end == null) {
                continue;
            }
            double distance = player == null ? 0 : end.distanceToSqr(player.position());
            if (!found || distance < best) {
                found = true;
                best = distance;
                yawOffset = offset;
            }
            if (player == null) {
                break;
            }
        }
        return found;
    }

    @Nullable
    private Vec3 pathEnd(float offset) {
        Level level = mimic.level();
        double x = mimic.getX();
        double z = mimic.getZ();
        for (int i = 0; i < clip.size(); i++) {
            double[] move = rotate(clip.dx(i), clip.dz(i), offset);
            x += move[0];
            z += move[1];
            if (i % 5 == 4) {
                Vec3 point = new Vec3(x, mimic.getY(), z);
                BlockPos head = BlockPos.containing(point).above();
                if (!level.getBlockState(head).getCollisionShape(level, head).isEmpty() || !hasGround(level, point)) {
                    return null;
                }
            }
        }
        return new Vec3(x, mimic.getY(), z);
    }

    private static boolean hasGround(Level level, Vec3 point) {
        BlockPos pos = BlockPos.containing(point);
        for (int dy = 0; dy <= MAX_FALL; dy++) {
            BlockPos below = pos.below(dy);
            BlockState state = level.getBlockState(below);
            if (!state.getFluidState().isEmpty()) {
                return false;
            }
            if (!state.getCollisionShape(level, below).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static double[] rotate(double dx, double dz, float degrees) {
        double rad = degrees * Mth.DEG_TO_RAD;
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new double[]{dx * cos - dz * sin, dx * sin + dz * cos};
    }
}
