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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
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

    private static final EntityDimensions CROUCHING = EntityDimensions.scalable(0.6f, 1.5f).withEyeHeight(1.27f); //valeurs du wiki
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    private final UUID voiceChannelId = UUID.randomUUID();
    private Player prey;
    private int lureTicks, closeTicks;
    private int lureLimit = -1;
    private int lifeTicks;
    private boolean replaying;

    public Mimic(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.lookControl = new LookControl(this) {
            @Override
            public void tick() {
                if (!replaying) super.tick();
            }
        };
        if (this.getNavigation() instanceof GroundPathNavigation navigation) {
            navigation.setCanOpenDoors(true);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.86, true));
        this.goalSelector.addGoal(3, new MimicStalkGoal(this));
        this.goalSelector.addGoal(4, new MimicReplayGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        //this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.MOVEMENT_SPEED, 0.313d)
                .add(Attributes.ATTACK_DAMAGE, 4d)
                .add(Attributes.FOLLOW_RANGE, 48d);
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

    public GameProfile getGameProfile() {
        UUID id = getCopiedId();
        return new MimicProfile(id == null ? getUUID() : id, getCopiedName(),
                entityData.get(TEXTURES), entityData.get(SIGNATURE), false).toGameProfile();
    }

    public void copy(MimicMemory.Known known) {
        MimicProfile profile = known.profile();
        this.entityData.set(COPIED_ID, Optional.of(profile.id()));
        this.entityData.set(COPIED_NAME, profile.name());
        this.entityData.set(TEXTURES, profile.textures());
        this.entityData.set(SIGNATURE, profile.signature());
        for (EquipmentSlot slot : SLOTS) {
            this.setItemSlot(slot, known.equipment(slot));
            this.setDropChance(slot, 0.0F);
        }
        this.setLeftHanded(profile.leftHanded());
        this.lureLimit = -1;
    }

    @Override
    public Component getName() {
        String name = this.getCopiedName();
        if (name.isEmpty()) return super.getName();

        return isRevealed() ? Component.literal(name).withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.DARK_RED) : Component.literal(name);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? CROUCHING : super.getDefaultDimensions(pose);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        // en gros comme Player.updatePlayerPose
        if (!isDeadOrDying() && !isSleeping()) {
            Pose pose = this.isShiftKeyDown() && !this.isInWater() ? Pose.CROUCHING : Pose.STANDING;
            if (pose != this.getPose() && (pose == Pose.CROUCHING
                    || this.level().noCollision(this, this.getDimensions(Pose.STANDING).makeBoundingBox(this.position()).deflate(1.0E-7)))) {
                this.setPose(pose);
            }
        }

        if (isRevealed()) {
            if ((prey == null || !canHunt(prey)) && tickCount % 20 == 0) {
                prey = level().getNearestPlayer(getX(), getY(), getZ(), 32, e -> canHunt((Player) e));
            }
            if (prey != null && getTarget() == null) setTarget(prey);
        } else {
            if (lureLimit < 0) {
                lureTicks = 0;
                closeTicks = 0;
                lureLimit = 20 * Mth.nextInt(random, Config.MIMIC_LURE_MIN.get(), Config.MIMIC_LURE_MAX.get());
            }
            if(level().getDifficulty() != Difficulty.PEACEFUL) {
                Player player = level().getNearestPlayer(getX(), getY(), getZ(), 16, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                if (player == null) {
                    lureTicks = Math.max(0, lureTicks - 2);
                    closeTicks = 0;
                } else {
                    lureTicks++;
                    double dist = distanceTo(player);
                    if (dist < 2.5) closeTicks++;
                    else closeTicks = 0;

                    if (lureTicks >= lureLimit || closeTicks > 30) {
                        reveal(player);
                    } else if (player.getHealth() < player.getMaxHealth() * 0.35f && dist < 10 && hasLineOfSight(player)) {
                        reveal(player); //le joueur a presque plus de vie donc il attaque direct
                    }
                }
            }
        }

        tickVoice();

        if (isRevealed()) {
            setSprinting(getTarget() != null && !isShiftKeyDown());
        } else if ((tickCount + getId()) % 200 == 0) {
            ServerPlayer online = getServer().getPlayerList().getPlayer(getCopiedId());
            if (online != null) {
                for (EquipmentSlot slot : SLOTS) {
                    ItemStack stack = online.getItemBySlot(slot);
                    if (!ItemStack.matches(getItemBySlot(slot), stack)) {
                        setItemSlot(slot, stack.copy());
                        setDropChance(slot, 0.0F);
                    }
                }
            }
        }

        int despawn = Config.MIMIC_DESPAWN_SECONDS.get();
        if (despawn > 0 && ++lifeTicks > despawn * 20 && tickCount % 20 == 0) {
            boolean named = hasCustomName() && isPersistenceRequired();
            if (!named && tickCount - lastHurtTick >= 200 && !isSeen()) {
                //System.out.println("despawn " + getCopiedName());
                discard();
            }
        }
    }

    public boolean canHunt(Player player) {
        if (level().getDifficulty() == Difficulty.PEACEFUL || player.level() != level()) return false;
        return player.isAlive() && !player.isSpectator() && !player.isCreative() && distanceToSqr(player) <= 48 * 48;
    }

    private void reveal(Player target) {
        this.entityData.set(REVEALED, true);
        this.setShiftKeyDown(false);
        if (target == null) {
            target = level().getNearestPlayer(getX(), getY(), getZ(), 32, e -> e instanceof Player p && canHunt(p));
        }
        this.prey = target;
        if (target != null) {
            this.setTarget(target);
        }
        // TODO un vrai son a nous
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1.0F, 0.8F);
        MimicVoice.stop(this);
        speak(ClipPicker.Context.ATTACK);
    }

    private int lastHurtTick = -1000;

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if(hurt && !this.level().isClientSide()) {
            lastHurtTick = tickCount;
            Player attacker = source.getEntity() instanceof Player player && canHunt(player) ? player : null;
            if (!this.isRevealed()) {
                this.reveal(attacker);
            } else if (attacker != null && attacker != prey) {
                if (prey == null || distanceToSqr(attacker) < distanceToSqr(prey)) {
                    prey = attacker;
                    setTarget(attacker);
                }
            }
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
        SoundEvent sound = SoundEvents.PLAYER_ATTACK_STRONG;
        if (!hit) sound = SoundEvents.PLAYER_ATTACK_NODAMAGE;
        else if (sprinting) sound = SoundEvents.PLAYER_ATTACK_KNOCKBACK;
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

    private boolean isSeen() {
        int chunks = getServer().getPlayerList().getViewDistance();
        if (chunks < 2) chunks = 2;
        int range = Math.min(getType().clientTrackingRange(), chunks) * 16;

        for(Player player : level().players()) {
            if(player.isSpectator()) continue;
            double dx = getX() - player.getX();
            double dz = getZ() - player.getZ();
            if(dx * dx + dz * dz > range * range) continue;

            if (isLookingAt(player, getEyePosition())
                    || isLookingAt(player, position().add(0.0, getBbHeight() * 0.5, 0.0))
                    || isLookingAt(player, position().add(0.0, 0.1, 0.0))) {
                return true;
            }
        }
        return false;
    }

    // repris de EnderMan.isLookingAtMe mais en plus large
    private boolean isLookingAt(Player player, Vec3 point) {
        Vec3 vec3 = player.getViewVector(1.0F);
        Vec3 eye = player.getEyePosition();
        Vec3 vec31 = point.subtract(eye);
        double d0 = vec31.length();
        if(d0 < 1.0E-4) return true;
        double d1 = vec3.dot(vec31) / d0;
        return d1 > 0.2 ? level().clip(new ClipContext(eye, point, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player)).getType() == HitResult.Type.MISS : false;
    }

    public boolean hasMoves() {
        UUID id = getCopiedId();
        if (id == null) return false;
        MimicMemory.Known known = MimicMemory.get(getServer()).get(id);
        return known != null && known.hasMoves();
    }

    public MovementClip pickMove() {
        UUID id = getCopiedId();
        return id == null ? null : MimicMemory.get(getServer()).pickMove(id, random);
    }

    public boolean isReplaying() { return replaying; }
    public void setReplaying(boolean replaying) { this.replaying = replaying; }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // super() retire la main au pif (c'est pour ca que y'avait des mimics gaucher alors que le joueur l'est pas), on la garde de coté
        boolean leftHanded = this.isLeftHanded();
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (this.getCopiedId() == null) {
            // Spawn par oeuf ou commande : on copie quelqu'un au hasard
            Player nearest = level.getNearestPlayer(this, 64);
            MimicMemory.Known source = MimicSpawner.pickSource(level.getLevel().getServer(), nearest instanceof ServerPlayer p ? p : null, random);
            if (source != null) {
                this.copy(source);
            }
        } else {
            this.setLeftHanded(leftHanded);
        }
        return spawnGroupData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        UUID id = this.getCopiedId();
        if (id != null) {
            compound.putUUID("CopiedId", id);
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
        if (compound.hasUUID("CopiedId")) {
            this.entityData.set(COPIED_ID, Optional.of(compound.getUUID("CopiedId")));
        } else {
            this.entityData.set(COPIED_ID, Optional.empty());
        }
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


    private int voiceCooldown = 60, quietUntil;
    private long lastAnswered;
    private final Map<Long, Long> alreadySaid = new HashMap<>();

    private void tickVoice() {
        if(voiceCooldown > 0) voiceCooldown--;
        if((tickCount + getId()) % 5 != 0 || !MimicVoice.isAvailable() || MimicVoice.isTalking(this)) return;

        long now = System.currentTimeMillis();
        boolean near = false;
        long silence = Long.MAX_VALUE;
        long answer = 0;
        for(Player player : level().players()) {
            double d = distanceToSqr(player);
            if(player.isSpectator() || d > 24 * 24) continue;
            if(d <= 256) near = true;

            Long last = VoiceRecorder.lastHeard().get(player.getUUID());
            if (last == null) continue;
            long ago = now - last;
            if(ago < silence) silence = ago;
            // quelqu'un vient de parler a coté, il lui repond
            if(d <= 16 * 16 && ago >= 700 && ago <= 2500 && last > lastAnswered && !player.getUUID().equals(getCopiedId())) {
                if (last > answer) answer = last;
            }
        }
        if(!near || silence < 700) return;

        if (isRevealed()) {
            if(voiceCooldown <= 0) speak(ClipPicker.Context.ATTACK);
        } else if(answer > 0) {
            lastAnswered = answer;
            if(tickCount > quietUntil + 100 && random.nextFloat() < 0.4f) {
                speak(ClipPicker.Context.LURE);
            }
        } else if(voiceCooldown <= 0 && silence > 4000) {
            speak(ClipPicker.Context.LURE);
        }
    }

    private void speak(ClipPicker.Context context) {
        voiceCooldown = 20 * Mth.nextInt(random, 20, 50);
        UUID copied = getCopiedId();
        if(copied == null || !MimicVoice.isAvailable()) return;

        int range = context == ClipPicker.Context.LURE ? 16 : 24;
        if (level().getNearestPlayer(getX(), getY(), getZ(), range, EntitySelector.NO_SPECTATORS) == null) return;

        long now = System.currentTimeMillis();
        alreadySaid.values().removeIf(time -> now - time > 240000);
        VoiceClip clip = ClipPicker.pick(VoiceBank.get(getServer(), copied).clips(), context, alreadySaid.keySet(), random);
        if(clip != null && MimicVoice.speak(this, clip)) {
            int ticks = clip.durationMillis() / 50;
            voiceCooldown += ticks;
            quietUntil = tickCount + ticks;
            alreadySaid.put(clip.id, now);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            MimicVoice.stop(this);
        }
        super.remove(reason);
    }
}
