package com.ussm.the_undervoid.network;

import com.ussm.the_undervoid.Config;
import com.ussm.the_undervoid.UnderVoidMod;
import com.ussm.the_undervoid.attachment.ModAttachments;
import com.ussm.the_undervoid.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = UnderVoidMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive()) return;

        ServerLevel serverLevel = player.serverLevel();

        // dans le vide de l'end : faut tenir 10s pour passer dans l'entre-monde
        if (player.getY() < serverLevel.getMinBuildHeight() - 64) {
            if (serverLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

            int voidTicks = player.getData(ModAttachments.VOID_TICKS) + 1;
            //UnderVoidMod.LOGGER.info("void ticks {}", voidTicks);
            if (voidTicks >= Config.VOID_SURVIVAL_TICKS.get() || player.isCreative() || player.isSpectator()) {
                sendToUndervoid(player);
            } else {
                player.setData(ModAttachments.VOID_TICKS, voidTicks);
            }
        } else if (player.getY() > serverLevel.getMaxBuildHeight()
                && serverLevel.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) {
            teleportPlayer(player, player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING));
        } else if (player.hasData(ModAttachments.VOID_TICKS)) {
            player.removeData(ModAttachments.VOID_TICKS);
        }
    }

    private static final int APPLE_WINDOW = 20 * 60; // les pommes comptent pendant 1 min

    // 3 pommes dorees avant de sauter et le vide fait 4x moins mal
    @SubscribeEvent
    public static void onEatApple(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getItem().is(Items.GOLDEN_APPLE)) return;

        long time = player.level().getGameTime();
        int apples = player.getData(ModAttachments.VOID_APPLES);
        if (apples == 0 || time - player.getData(ModAttachments.VOID_APPLE_START) > APPLE_WINDOW) {
            apples = 0;
            player.setData(ModAttachments.VOID_APPLE_START, time);}
        player.setData(ModAttachments.VOID_APPLES, apples + 1);
        player.setData(ModAttachments.VOID_APPLE_TIME, time);
    }

    @SubscribeEvent
    public static void onVoidDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) return;
        if (player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

        boolean recent = player.level().getGameTime() - player.getData(ModAttachments.VOID_APPLE_TIME) <= APPLE_WINDOW;
        if (recent && player.getData(ModAttachments.VOID_APPLES) >= Config.VOID_GOLDEN_APPLES.get()) {
            event.setAmount((float) (event.getAmount() * Config.VOID_APPLE_DAMAGE.get()));
        }
    }

    // l'eau soigne la santé mentale dans l'entre-monde, donc on peut pas en poser : elle s'evapore comme dans le nether
    // et faut trouver les mares
    @SubscribeEvent
    public static void onUseBucket(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BucketItem bucket) || !bucket.content.is(FluidTags.WATER)) return;
        if (!player.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        if (player.level() instanceof ServerLevel level && player.pick(5.0, 1f, false) instanceof BlockHitResult hit
                && hit.getType() == HitResult.Type.BLOCK) {
            evaporate(level, hit.getBlockPos().relative(hit.getDirection()));
            // le seau d'eau se vide, les seaux a poisson on les laisse sinon le poisson disparait
            if (!player.getAbilities().instabuild && stack.is(Items.WATER_BUCKET)) {
                player.setItemInHand(event.getHand(), new ItemStack(Items.BUCKET));
            }
        }
    }

    // la glace cassée devient de l'eau d'habitude
    @SubscribeEvent
    public static void onBreakIce(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !level.dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return;
        if (!event.getState().is(Blocks.ICE) || event.getPlayer().getAbilities().instabuild) return;
        if (EnchantmentHelper.hasTag(event.getPlayer().getMainHandItem(), EnchantmentTags.PREVENTS_ICE_MELTING)) return;

        event.setCanceled(true);
        level.levelEvent(2001, event.getPos(), net.minecraft.world.level.block.Block.getId(event.getState()));
        level.removeBlock(event.getPos(), false);
    }

    // pareil pour les distributeurs, appelé dans le commonSetup
    public static void registerWaterDispensers() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof BucketItem bucket) || !bucket.content.is(FluidTags.WATER)) continue;
            DispenseItemBehavior vanilla = DispenserBlock.DISPENSER_REGISTRY.get(item);
            DispenserBlock.registerBehavior(item, (BlockSource source, ItemStack stack) -> {
                if (!source.level().dimension().equals(ModDimensions.UNDERVOID_DIM_LEVEL_KEY)) return vanilla.dispense(source, stack);
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                evaporate(source.level(), source.pos().relative(facing));
                return stack.is(Items.WATER_BUCKET) ? new ItemStack(Items.BUCKET) : stack;
            });
        }
    }

    private static void evaporate(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.3, 0.3, 0.3, 0.0);
        }
    }

    private static void sendToUndervoid(ServerPlayer player) {
        ServerLevel undervoidLevel = player.server.getLevel(ModDimensions.UNDERVOID_DIM_LEVEL_KEY);
        if (undervoidLevel == null) return;

        player.removeData(ModAttachments.VOID_TICKS);
        player.removeData(ModAttachments.VOID_APPLES); // faut en remanger pour le prochain saut

        int x = player.getBlockX();
        int z = player.getBlockZ();
        undervoidLevel.getChunk(x >> 4, z >> 4);
        int y = undervoidLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        if (y <= undervoidLevel.getMinBuildHeight()) {
            y = 120;
        }

        teleportPlayer(player, new DimensionTransition(undervoidLevel, new Vec3(x + 0.5, y, z + 0.5), Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100));
    }

    private static void teleportPlayer(ServerPlayer player, DimensionTransition transition) {
        player.setCamera(player);
        player.stopRiding();
        player.changeDimension(transition);
        player.resetFallDistance();
    }
}
