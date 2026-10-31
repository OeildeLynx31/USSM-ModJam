package com.ussm.examplemod.entity;

import com.ussm.examplemod.ExampleMod;
import com.ussm.examplemod.entity.custom.HerobrineEntity;
import com.ussm.examplemod.worldgen.dimension.ModDimensions;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class HerobrineEvents {
    private static final String HAUNTED_KEY = "examplemod_herobrine_haunted";
    private static final String NEXT_KEY = "examplemod_herobrine_next";

    private static final int CHECK_INTERVAL = 200;
    private static final float CHANCE = 1.0f / 120.0f;
    private static final long COOLDOWN = 12000L;
    private static final double MIN_DISTANCE = 28.0;
    private static final double MAX_DISTANCE = 40.0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive()) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        CompoundTag data = player.getPersistentData();
        boolean inUndervoid = level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
        boolean inOverworld = level.dimension().equals(Level.OVERWORLD);

        if (inUndervoid) {
            data.putBoolean(HAUNTED_KEY, true);
        }
        // Overworld : seulement si le joueur est déjà allé dans l'Undervoid
        if (!inUndervoid && !(inOverworld && data.getBoolean(HAUNTED_KEY))) {
            return;
        }

        long now = level.getGameTime();
        if (now < data.getLong(NEXT_KEY)) {
            return;
        }
        if (level.random.nextFloat() >= CHANCE) {
            return;
        }
        if (spawnNear(player)) {
            data.putLong(NEXT_KEY, now + COOLDOWN);
        }
    }

    // Marque le joueur dès son arrivée dans l'Undervoid
    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)
                && event.getEntity() instanceof ServerPlayer player) {
            player.getPersistentData().putBoolean(HAUNTED_KEY, true);
        }
    }

    // Garde la malédiction après la mort
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();
        if (oldData.contains(HAUNTED_KEY)) {
            newData.putBoolean(HAUNTED_KEY, oldData.getBoolean(HAUNTED_KEY));
        }
        if (oldData.contains(NEXT_KEY)) {
            newData.putLong(NEXT_KEY, oldData.getLong(NEXT_KEY));
        }
    }

    // Commandes de test
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("herobrine")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("spawn").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    boolean ok = spawnNear(player);
                    context.getSource().sendSuccess(() -> Component.literal(
                            ok ? "Herobrine est apparu." : "Aucun endroit trouvé, réessaie ailleurs."), false);
                    return ok ? 1 : 0;

                }))
                .then(Commands.literal("haunt").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    player.getPersistentData().putBoolean(HAUNTED_KEY, true);
                    context.getSource().sendSuccess(() -> Component.literal("Joueur hanté."), false);
                    return 1;
                }))
                .then(Commands.literal("debug").executes(context -> {
                    HerobrineEntity.debugMode = !HerobrineEntity.debugMode;
                    context.getSource().sendSuccess(() -> Component.literal(
                            HerobrineEntity.debugMode
                                    ? "Debug ON"
                                    : "DEBUG OFF"), false);
                    return 1;
                }))
                .then(Commands.literal("reset").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    player.getPersistentData().remove(HAUNTED_KEY);
                    player.getPersistentData().remove(NEXT_KEY);
                    context.getSource().sendSuccess(() -> Component.literal("Malédiction retirée."), false);
                    return 1;
                })));
    }

    private static boolean spawnNear(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // Un seul à la fois autour du joueur
        if (!level.getEntitiesOfClass(HerobrineEntity.class, player.getBoundingBox().inflate(100.0)).isEmpty()) {
            return false;
        }
        BlockPos spot = findSpot(level, player);
        if (spot == null) {
            return false;
        }
        HerobrineEntity herobrine = ModEntities.HEROBRINE.get().create(level);
        if (herobrine == null) {
            return false;
        }
        herobrine.moveTo(spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5, 0.0f, 0.0f);
        level.addFreshEntity(herobrine);
        return true;
    }

    private static BlockPos findSpot(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double distance = MIN_DISTANCE + level.random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
            int playerY = player.blockPosition().getY();

            for (int y = playerY + 8; y > playerY - 8; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (!level.hasChunkAt(pos)) {
                    break;
                }
                BlockPos below = pos.below();
                if (level.getBlockState(pos).isAir()
                        && level.getBlockState(pos.above()).isAir()
                        && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                    if (canSee(level, player, pos)) {
                        return pos;
                    }
                    break;
                }
            }
        }
        return null;
    }

    // Le joueur doit pouvoir le voir (rien entre les deux)
    private static boolean canSee(ServerLevel level, ServerPlayer player, BlockPos pos) {
        Vec3 from = player.getEyePosition();
        Vec3 to = Vec3.atBottomCenterOf(pos).add(0.0, 1.6, 0.0);
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, player)).getType() == HitResult.Type.MISS;
    }
}