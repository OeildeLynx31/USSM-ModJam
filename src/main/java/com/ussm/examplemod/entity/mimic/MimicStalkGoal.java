package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MimicStalkGoal extends Goal {
    private static final double COMFORT_DISTANCE = 4.5;

    private final Mimic mimic;
    private Player player;
    private int timeToRecalcPath;
    private final List<Boolean> lastSneaks = new ArrayList<>();
    private int sneakSpam, spamTimer;

    public MimicStalkGoal(Mimic mimic) {
        this.mimic = mimic;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private double range() {
        return mimic.hasMoves() ? 20 : 40;
    }

    @Override
    public boolean canUse() {
        if (mimic.isRevealed() || mimic.getTarget() != null) {
            return false;
        }
        this.player = mimic.level().getNearestPlayer(mimic.getX(), mimic.getY(), mimic.getZ(), range(), EntitySelector.NO_SPECTATORS);
        return this.player != null;
    }

    @Override
    public boolean canContinueToUse() {
        double stop = range() + 4;
        if (mimic.isRevealed() || mimic.getTarget() != null || player == null || !player.isAlive() || player.isSpectator()) {
            return false;
        }
        return mimic.distanceToSqr(player) < stop * stop;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.sneakSpam = 0;
        lastSneaks.clear();
    }

    @Override
    public void stop() {
        this.player = null;
        mimic.getNavigation().stop();
        mimic.setShiftKeyDown(false);
        mimic.setSprinting(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        mimic.getLookControl().setLookAt(player, 30.0F, 30.0F);
        // il refait les shift du joueur mais 10 ticks apres
        lastSneaks.add(player.isShiftKeyDown());
        boolean echoed = false;
        if (lastSneaks.size() > 10) {
            echoed = lastSneaks.remove(0);
        }

        double distance = mimic.distanceTo(player);
        if (distance > COMFORT_DISTANCE) {
            mimic.setShiftKeyDown(false);
            sneakSpam = 0;
            if (--timeToRecalcPath <= 0) {
                timeToRecalcPath = 10;
                if (distance > 12) {
                    mimic.setSprinting(true);
                    mimic.getNavigation().moveTo(player, 0.86);
                } else {
                    mimic.setSprinting(false);
                    mimic.getNavigation().moveTo(player, 1.0);
                }
            }
            return;
        }

        mimic.getNavigation().stop();
        mimic.setSprinting(false);
        RandomSource random = mimic.getRandom();
        if (sneakSpam > 0) {
            if (--spamTimer <= 0) {
                mimic.setShiftKeyDown(!mimic.isShiftKeyDown());
                sneakSpam--;
                spamTimer = 2 + random.nextInt(3);
            }
        } else {
            mimic.setShiftKeyDown(echoed);
            if (random.nextInt(160) == 0) { // spam shift = dire bonjour
                sneakSpam = 4 + 2 * random.nextInt(3);
                spamTimer = 0;
            }
        }

        if (random.nextInt(220) == 0) mimic.swing(InteractionHand.MAIN_HAND);
        if (random.nextInt(400) == 0) mimic.getJumpControl().jump();
    }
}
