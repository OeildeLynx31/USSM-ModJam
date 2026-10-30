package com.ussm.examplemod.entity.custom;

import com.mojang.authlib.GameProfile;
import com.ussm.examplemod.Config;
import com.ussm.examplemod.entity.MimicSpawner;
import com.ussm.examplemod.entity.mimic.MimicMemory;
import com.ussm.examplemod.entity.mimic.MimicProfile;
import com.ussm.examplemod.entity.mimic.MimicReplayGoal;
import com.ussm.examplemod.entity.mimic.MimicStalkGoal;
import com.ussm.examplemod.entity.mimic.MovementClip;
import com.ussm.examplemod.entity.mimic.voice.ClipPicker;
import com.ussm.examplemod.entity.mimic.voice.MimicVoice;
import com.ussm.examplemod.entity.mimic.voice.VoiceBank;
import com.ussm.examplemod.entity.mimic.voice.VoiceClip;
import com.ussm.examplemod.entity.mimic.voice.VoiceRecorder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// se fait passer pour un joueur qui est pas la (skin, pseudo, stuff), vient vers toi comme un pote puis se retourne contre toi
// fortement inspiré de mon mod mimic (fabric 1.20.1)
public class Mimic extends Monster {
    private static final EntityDataAccessor<Optional<UUID>> COPIED_ID =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> COPIED_NAME =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURES =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SIGNATURE =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> REVEALED =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.BOOLEAN);

    //comme un joueur accroupi, valeurs prises dans Player.POSES
    private static final EntityDimensions CROUCHING = EntityDimensions.scalable(0.6f, 1.5f).withEyeHeight(1.27f);
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    // la voix : il parle que si quelqu'un peut l'entendre, jamais en coupant la parole
    private static final double LISTENER_RANGE = 16;
    private static final double CONVERSATION_RANGE = 24;
    private static final long REPLY_AFTER_MILLIS = 700;
    private static final long REPLY_WITHIN_MILLIS = 2500;
    private static final long QUIET_BEFORE_MILLIS = 4000;
    private static final long REPEAT_MILLIS = 240_000; // une phrase deja dite revient pas avant 4 min

    private final UUID voiceChannelId = UUID.randomUUID();
    private final Map<Long, Long> recentClips = new HashMap<>();
    @Nullable
    private Player victim;
    private int lureTicks, closeTicks;
    private int lureLimit = -1;
    private int lifeTicks;
    private int lastHurtTick = -1000;
    private int voiceCooldown = 60, quietUntil;
    private long lastRepliedTo;
    private boolean replaying;
    @Nullable
    private GameProfile gameProfile;

    public Mimic(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.lookControl = new MimicLookControl(this);
        if (this.getNavigation() instanceof GroundPathNavigation navigation) {
            navigation.setCanOpenDoors(true);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        // 0,86 avec le sprint ca donne a peu pres la vitesse d'un joueur qui sprinte
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.86, true));
        this.goalSelector.addGoal(3, new MimicStalkGoal(this));
        this.goalSelector.addGoal(4, new MimicReplayGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.MOVEMENT_SPEED, 0.313D) // vitesse de marche d'un joueur avec la physique des mobs
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COPIED_ID, Optional.empty());
        builder.define(COPIED_NAME, "");
        builder.define(TEXTURES, "");
        builder.define(SIGNATURE, "");
        builder.define(REVEALED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (COPIED_ID.equals(key) || COPIED_NAME.equals(key) || TEXTURES.equals(key) || SIGNATURE.equals(key)) {
            gameProfile = null;
        }
    }

    @Nullable
    public UUID getCopiedId() {
        return this.entityData.get(COPIED_ID).orElse(null);
    }

    public String getCopiedName() {
        return this.entityData.get(COPIED_NAME);
    }

    public boolean isRevealed() {
        return this.entityData.get(REVEALED);
    }

    public UUID getVoiceChannelId() {
        return voiceChannelId;
    }

    // pour le skin cote client, meme si le joueur copié est deco
    public GameProfile getGameProfile() {
        if (gameProfile == null) {
            UUID id = getCopiedId();
            gameProfile = new MimicProfile(id == null ? getUUID() : id, getCopiedName(),
                    entityData.get(TEXTURES), entityData.get(SIGNATURE), false).toGameProfile();
        }
        return gameProfile;
    }

    public void copy(MimicMemory.Known known) {
        MimicProfile profile = known.profile();
        this.entityData.set(COPIED_ID, Optional.of(profile.id()));
        this.entityData.set(COPIED_NAME, profile.name());
        this.entityData.set(TEXTURES, profile.textures());
        this.entityData.set(SIGNATURE, profile.signature());
        for (EquipmentSlot slot : SLOTS) {
            this.setItemSlot(slot, known.equipment(slot));
            this.setDropChance(slot, 0.0F); //sinon on pouvait dupliquer le stuff en le tuant
        }
        this.setLeftHanded(profile.leftHanded());
        this.lureLimit = -1;
    }

    @Override
    public Component getName() {
        String name = this.getCopiedName();
        if (name.isEmpty()) {
            return super.getName();
        }
        if (this.isRevealed()) {
            return Component.literal(name).withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.DARK_RED);
        }
        return Component.literal(name);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? CROUCHING : super.getDefaultDimensions(pose);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        updatePose();
        tickLure();
        tickVoice();
        if (isRevealed()) {
            setSprinting(getTarget() != null && !isShiftKeyDown());
        }
        if (!isRevealed() && (tickCount + getId()) % 200 == 0) {
            syncEquipment();
        }

        // ils finissent tous par partir, au premier moment ou personne le regarde
        int despawn = Config.MIMIC_DESPAWN_SECONDS.get();
        if (despawn > 0 && ++lifeTicks > despawn * 20 && tickCount % 20 == 0 && !(hasCustomName() && isPersistenceRequired())
                && tickCount - lastHurtTick >= 200 && !isWatched()) {
            discard();
        }
    }

    private void updatePose() {
        if (isDeadOrDying() || isSleeping()) {
            return;
        }
        Pose wanted = isShiftKeyDown() && !isInWater() ? Pose.CROUCHING : Pose.STANDING;
        if (wanted != getPose() && (wanted == Pose.CROUCHING
                || level().noCollision(this, getDimensions(Pose.STANDING).makeBoundingBox(position()).deflate(1.0E-7)))) {
            setPose(wanted);
        }
    }

    //----------------------------FAIRE SEMBLANT PUIS FRAPPER----------------------------
    private void resetLure() {
        int min = Config.MIMIC_LURE_MIN.get();
        int max = Math.max(min, Config.MIMIC_LURE_MAX.get());
        lureTicks = 0;
        closeTicks = 0;
        lureLimit = 20 * Mth.nextInt(random, min, max);
    }

    // il se revele s'il a passé trop de temps pres de toi, si tu restes collé a lui, ou si t'es affaibli
    private void tickLure() {
        if (isRevealed()) {
            if ((victim == null || !canHunt(victim)) && tickCount % 20 == 0) {
                victim = level().getNearestPlayer(getX(), getY(), getZ(), 32, e -> e instanceof Player p && canHunt(p));
            }
            if (victim != null && getTarget() == null) {
                setTarget(victim);
            }
            return;
        }
        if (lureLimit < 0) {
            resetLure();
        }
        if (level().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        Player player = level().getNearestPlayer(getX(), getY(), getZ(), 16, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (player == null) {
            lureTicks = Math.max(0, lureTicks - 2);
            closeTicks = 0;
            return;
        }
        lureTicks++;
        double distance = distanceTo(player);
        closeTicks = distance < 2.5 ? closeTicks + 1 : 0;
        boolean weak = player.getHealth() < player.getMaxHealth() * 0.35f;
        if (lureTicks >= lureLimit || closeTicks > 30 || (weak && distance < 10 && hasLineOfSight(player))) {
            reveal(player);
        }
    }

    public boolean canHunt(Player player) {
        return player.isAlive() && !player.isCreative() && !player.isSpectator() && player.level() == level()
                && distanceToSqr(player) <= 48 * 48 && level().getDifficulty() != Difficulty.PEACEFUL;
    }

    // la il se revele et il attaque
    private void reveal(@Nullable Player target) {
        this.entityData.set(REVEALED, true);
        this.setShiftKeyDown(false);
        if (target == null) {
            target = level().getNearestPlayer(getX(), getY(), getZ(), 32, e -> e instanceof Player p && canHunt(p));
        }
        this.victim = target;
        if (target != null) {
            this.setTarget(target);
        }
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.ENDERMAN_STARE,
                SoundSource.HOSTILE,
                1.0F,
                0.8F
        );
        MimicVoice.stop(this);
        speak(ClipPicker.Context.ATTACK);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide()) {
            lastHurtTick = tickCount;
            Player attacker = source.getEntity() instanceof Player player && canHunt(player) ? player : null;
            if (!this.isRevealed()) {
                this.reveal(attacker);
            } else if (attacker != null && attacker != victim && (victim == null || distanceToSqr(attacker) < distanceToSqr(victim))) {
                victim = attacker;
                setTarget(attacker);
            }
            // il crie, mais pas a chaque coup
            if (isAlive() && !MimicVoice.isTalking(this) && tickCount > quietUntil + 120 && random.nextFloat() < 0.3f) {
                speak(ClipPicker.Context.PAIN);
            }
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean sprinting = isSprinting();
        boolean hit = super.doHurtTarget(target);
        SoundEvent sound = !hit ? SoundEvents.PLAYER_ATTACK_NODAMAGE : sprinting ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG;
        level().playSound(null, getX(), getY(), getZ(), sound, getSoundSource(), 1.0F, 1.0F);
        return hit;
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity killed) {
        if (killed instanceof Player && !MimicVoice.isTalking(this) && random.nextFloat() < 0.5f) {
            speak(ClipPicker.Context.KILL);
        }
        return super.killedEntity(level, killed);
    }

    // il reprend le stuff du joueur copié s'il est connecté
    private void syncEquipment() {
        UUID id = getCopiedId();
        MinecraftServer server = getServer();
        ServerPlayer online = id == null || server == null ? null : server.getPlayerList().getPlayer(id);
        if (online == null) {
            return;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack wanted = online.getItemBySlot(slot);
            if (!ItemStack.matches(getItemBySlot(slot), wanted)) {
                setItemSlot(slot, wanted.copy());
                setDropChance(slot, 0.0F);
            }
        }
    }

    // un joueur pourrait le voir ? (distance d'affichage, champ de vision large, rien d'opaque entre les deux)
    private boolean isWatched() {
        int viewDistance = getServer() == null ? 10 : getServer().getPlayerList().getViewDistance();
        double range = Math.min(getType().clientTrackingRange(), Math.max(2, viewDistance)) * 16.0;
        Vec3[] points = {getEyePosition(), position().add(0.0, getBbHeight() * 0.5, 0.0), position().add(0.0, 0.1, 0.0)};
        for (Player player : level().players()) {
            double dx = getX() - player.getX();
            double dz = getZ() - player.getZ();
            if (player.isSpectator() || dx * dx + dz * dz > range * range) {
                continue;
            }
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getViewVector(1.0F);
            for (Vec3 point : points) {
                Vec3 toPoint = point.subtract(eye);
                double distance = toPoint.length();
                if (distance < 1.0E-4) {
                    return true;
                }
                if (look.dot(toPoint) / distance > 0.2 && level().clip(new ClipContext(eye, point, ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE, player)).getType() == HitResult.Type.MISS) {
                    return true;
                }
            }
        }
        return false;
    }

    //----------------------------DEPLACEMENTS ENREGISTRES----------------------------
    public boolean hasMoves() {
        UUID id = getCopiedId();
        MinecraftServer server = getServer();
        if (id == null || server == null) {
            return false;
        }
        MimicMemory.Known known = MimicMemory.get(server).get(id);
        return known != null && known.hasMoves();
    }

    @Nullable
    public MovementClip pickMove() {
        UUID id = getCopiedId();
        MinecraftServer server = getServer();
        return id == null || server == null ? null : MimicMemory.get(server).pickMove(id, random);
    }

    public boolean isReplaying() {
        return replaying;
    }

    public void setReplaying(boolean replaying) {
        this.replaying = replaying;
    }

    //----------------------------VOIX----------------------------
    // surtout pour repondre juste apres qu'un joueur proche a fini sa phrase, parfois de lui meme pendant un silence
    private void tickVoice() {
        if (voiceCooldown > 0) {
            voiceCooldown--;
        }
        if ((tickCount + getId()) % 5 != 0 || !MimicVoice.isAvailable() || MimicVoice.isTalking(this)) {
            return;
        }
        long now = System.currentTimeMillis();
        Map<UUID, Long> heard = VoiceRecorder.lastHeard();
        UUID copied = getCopiedId();
        boolean listener = false;
        long silence = Long.MAX_VALUE;
        long replyTo = 0;
        for (Player player : level().players()) {
            double distanceSqr = distanceToSqr(player);
            if (player.isSpectator() || distanceSqr > CONVERSATION_RANGE * CONVERSATION_RANGE) {
                continue;
            }
            boolean close = distanceSqr <= LISTENER_RANGE * LISTENER_RANGE;
            listener |= close;
            Long last = heard.get(player.getUUID());
            if (last == null) {
                continue;
            }
            silence = Math.min(silence, now - last);
            if (close && now - last >= REPLY_AFTER_MILLIS && now - last <= REPLY_WITHIN_MILLIS && last > lastRepliedTo
                    && !player.getUUID().equals(copied)) {
                replyTo = Math.max(replyTo, last);
            }
        }
        // quelqu'un parle, il attend son tour
        if (!listener || silence < REPLY_AFTER_MILLIS) {
            return;
        }
        ClipPicker.Context context = isRevealed() ? ClipPicker.Context.ATTACK : ClipPicker.Context.LURE;
        if (replyTo > 0 && context == ClipPicker.Context.LURE) {
            lastRepliedTo = replyTo;
            if (tickCount > quietUntil + 100 && random.nextFloat() < 0.4f) {
                speak(context);
            }
            return;
        }
        if (voiceCooldown <= 0 && (context != ClipPicker.Context.LURE || silence > QUIET_BEFORE_MILLIS)) {
            speak(context);
        }
    }

    private boolean speak(ClipPicker.Context context) {
        voiceCooldown = 20 * Mth.nextInt(random, 20, 50);
        UUID copied = getCopiedId();
        MinecraftServer server = getServer();
        if (copied == null || server == null || !MimicVoice.isAvailable()) {
            return false;
        }
        double range = context == ClipPicker.Context.LURE ? LISTENER_RANGE : CONVERSATION_RANGE;
        if (level().getNearestPlayer(getX(), getY(), getZ(), range, EntitySelector.NO_SPECTATORS) == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        recentClips.values().removeIf(time -> now - time > REPEAT_MILLIS);
        VoiceClip clip = ClipPicker.pick(VoiceBank.get(server, copied).clips(), context, recentClips.keySet(), random);
        if (clip == null || !MimicVoice.speak(this, clip)) {
            return false;
        }
        voiceCooldown += clip.durationMillis() / 50;
        quietUntil = tickCount + clip.durationMillis() / 50;
        recentClips.put(clip.id, now);
        return true;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide()) {
            MimicVoice.stop(this);
        }
        super.remove(reason);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        boolean leftHanded = this.isLeftHanded();
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (this.getCopiedId() == null) {
            //oeuf ou /summon : quelqu'un qu'on connait, de preference pas la
            Player nearest = level.getNearestPlayer(this, 64);
            MimicMemory.Known source = MimicSpawner.pickSource(level.getLevel().getServer(), nearest instanceof ServerPlayer p ? p : null, random);
            if (source != null) {
                this.copy(source);
            }
        } else {
            this.setLeftHanded(leftHanded); // faut garder la main du joueur copié sinon le jeu en tire une au hasard
        }
        return spawnGroupData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getCopiedId() != null) {
            compound.putUUID("CopiedId", this.getCopiedId());
        }
        compound.putString("CopiedName", this.getCopiedName());
        compound.putString("Textures", this.entityData.get(TEXTURES));
        compound.putString("Signature", this.entityData.get(SIGNATURE));
        compound.putBoolean("Revealed", this.isRevealed());
        compound.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(COPIED_ID, compound.hasUUID("CopiedId") ? Optional.of(compound.getUUID("CopiedId")) : Optional.empty());
        this.entityData.set(COPIED_NAME, compound.getString("CopiedName"));
        this.entityData.set(TEXTURES, compound.getString("Textures"));
        this.entityData.set(SIGNATURE, compound.getString("Signature"));
        this.entityData.set(REVEALED, compound.getBoolean("Revealed"));
        this.lifeTicks = compound.getInt("LifeTicks");
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    // pendant qu'il rejoue un enregistrement c'est l'enregistrement qui tourne la tete
    private static class MimicLookControl extends LookControl {
        private final Mimic mimic;

        MimicLookControl(Mimic mimic) {
            super(mimic);
            this.mimic = mimic;
        }

        @Override
        public void tick() {
            if (!mimic.isReplaying()) {
                super.tick();
            }
        }
    }
}
