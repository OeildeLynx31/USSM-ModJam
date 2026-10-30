package com.ussm.examplemod.entity.mimic;

import com.ussm.examplemod.entity.custom.Mimic;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.EnumSet;

// il vient vers toi comme un pote : marche (ou sprinte s'il est loin), s'arrete a quelques blocs, te regarde,
// s'accroupit pour dire bonjour et refait tes accroupissements avec un petit retard
public class MimicStalkGoal extends Goal {
    private static final double COMFORT_DISTANCE = 4.5;
    private static final int ECHO_DELAY = 10;

    private final Mimic mimic;
    private final boolean[] sneakHistory = new boolean[ECHO_DELAY];
    private Player player;
    private int historyIndex, repathTimer, crouchToggles, crouchTimer;

    public MimicStalkGoal(Mimic mimic) {
        this.mimic = mimic;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    // sans deplacements enregistrés il a rien a rejouer de loin, donc il vient direct
    private double range() {
        return mimic.hasMoves() ? 20 : 40;
    }

    @Override
    public boolean canUse() {
        if (mimic.isRevealed() || mimic.getTarget() != null) {
            return false;
        }
        player = mimic.level().getNearestPlayer(mimic.getX(), mimic.getY(), mimic.getZ(), range(), EntitySelector.NO_SPECTATORS);
        return player != null;
    }

    @Override
    public boolean canContinueToUse() {
        double stop = range() + 4;
        return !mimic.isRevealed() && mimic.getTarget() == null && player != null && player.isAlive() && !player.isSpectator()
                && mimic.distanceToSqr(player) < stop * stop;
    }

    @Override
    public void start() {
        repathTimer = 0;
        crouchToggles = 0;
        historyIndex = 0;
        Arrays.fill(sneakHistory, false);
    }

    @Override
    public void stop() {
        player = null;
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
        boolean echoed = sneakHistory[historyIndex];
        sneakHistory[historyIndex] = player.isShiftKeyDown();
        historyIndex = (historyIndex + 1) % ECHO_DELAY;

        double distance = mimic.distanceTo(player);
        if (distance > COMFORT_DISTANCE) {
            mimic.setShiftKeyDown(false);
            crouchToggles = 0;
            if (--repathTimer <= 0) {
                repathTimer = 10;
                boolean sprint = distance > 12;
                mimic.setSprinting(sprint);
                mimic.getNavigation().moveTo(player, sprint ? 0.86 : 1.0);
            }
            return;
        }

        mimic.getNavigation().stop();
        mimic.setSprinting(false);
        RandomSource random = mimic.getRandom();
        if (crouchToggles > 0) {
            if (--crouchTimer <= 0) {
                mimic.setShiftKeyDown(!mimic.isShiftKeyDown());
                crouchToggles--;
                crouchTimer = 2 + random.nextInt(3);
            }
        } else {
            mimic.setShiftKeyDown(echoed);
            if (random.nextInt(160) == 0) {
                // le "bonjour" des joueurs, un nombre pair pour finir debout
                crouchToggles = 4 + 2 * random.nextInt(3);
                crouchTimer = 0;
            }
        }
        if (random.nextInt(220) == 0) {
            mimic.swing(InteractionHand.MAIN_HAND);
        }
        if (random.nextInt(400) == 0) {
            mimic.getJumpControl().jump();
        }
    }
}
