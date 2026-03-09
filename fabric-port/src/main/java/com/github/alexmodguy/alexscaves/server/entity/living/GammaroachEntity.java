package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class GammaroachEntity extends PathfinderMob {
    private static final Predicate<LivingEntity> IRRADIATED_TARGET = mob ->
        mob.hasEffect(ACEffectRegistry.IRRADIATED.holder());

    private static final EntityDataAccessor<Integer> SPRAY_COOLDOWN = SynchedEntityData.defineId(GammaroachEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SPRAY_TIME = SynchedEntityData.defineId(GammaroachEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FED = SynchedEntityData.defineId(GammaroachEntity.class, EntityDataSerializers.BOOLEAN);

    public GammaroachEntity(EntityType<? extends GammaroachEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.MAX_HEALTH, 14.0D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public static boolean isValidLightLevel(ServerLevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        if (levelAccessor.getBrightness(LightLayer.SKY, blockPos) > randomSource.nextInt(32)) {
            return false;
        }
        int brightness = levelAccessor.getLevel().isThundering()
            ? levelAccessor.getMaxLocalRawBrightness(blockPos, 10)
            : levelAccessor.getMaxLocalRawBrightness(blockPos);
        return brightness <= randomSource.nextInt(8);
    }

    public static boolean canMonsterSpawnInLight(EntityType<GammaroachEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return isValidLightLevel(levelAccessor, blockPos, randomSource)
            && checkMobSpawnRules(entityType, levelAccessor, spawnType, blockPos, randomSource);
    }

    public static boolean checkGammaroachSpawnRules(EntityType<GammaroachEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return canMonsterSpawnInLight(entityType, levelAccessor, spawnType, blockPos, randomSource);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPRAY_COOLDOWN, 0);
        builder.define(SPRAY_TIME, 0);
        builder.define(FED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 20, false, true, IRRADIATED_TARGET));
    }

    public int getSprayCooldown() {
        return this.entityData.get(SPRAY_COOLDOWN);
    }

    public void setSprayCooldown(int time) {
        this.entityData.set(SPRAY_COOLDOWN, time);
    }

    public int getSprayTime() {
        return this.entityData.get(SPRAY_TIME);
    }

    public void setSprayTime(int time) {
        this.entityData.set(SPRAY_TIME, time);
    }

    public boolean isFed() {
        return this.entityData.get(FED);
    }

    public void setFed(boolean fed) {
        this.entityData.set(FED, fed);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && !effectInstance.getEffect().equals(ACEffectRegistry.IRRADIATED.holder());
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.isFed();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.requiresCustomPersistence();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.5F - Math.max(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSprayCooldown() > 0) {
            this.setSprayCooldown(this.getSprayCooldown() - 1);
        }
        if (this.getSprayTime() > 0) {
            this.getNavigation().stop();
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            if (this.getSprayTime() == 30) {
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY() + 0.2F, this.getZ());
                cloud.setParticle(ACParticleRegistry.GAMMAROACH.get());
                cloud.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 2000));
                cloud.setRadius(2.3F);
                cloud.setDuration(200);
                cloud.setWaitTime(10);
                cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
                this.level().addFreshEntity(cloud);
            } else if (this.getSprayTime() <= 30 && this.getSprayTime() >= 10) {
                Vec3 randomOffset = new Vec3(this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.5F)
                    .normalize()
                    .scale(1.0D)
                    .add(this.getEyePosition());
                this.level().addParticle(ACParticleRegistry.GAMMAROACH.get(), this.getRandomX(2.0F), this.getEyeY(), this.getRandomZ(2.0F), randomOffset.x, randomOffset.y + 0.23D, randomOffset.z);
            }
            this.setSprayTime(this.getSprayTime() - 1);
        }
    }

    public void triggerSpraying() {
        if (this.getSprayCooldown() <= 0 && this.getSprayTime() <= 0) {
            this.playSound(ACSoundRegistry.GAMMAROACH_SPRAY.get(), 1.0F, 1.0F);
            this.setSprayTime(40);
            this.setSprayCooldown(10000 + this.random.nextInt(24000));
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        boolean hurt = super.hurt(damageSource, damageAmount);
        Entity attacker = damageSource.getEntity();
        if (hurt && attacker instanceof LivingEntity living && !living.hasEffect(ACEffectRegistry.IRRADIATED.holder())) {
            this.triggerSpraying();
        }
        return hurt;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.getSprayTime() > 0) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSprayCooldown(compound.getInt("SprayCooldown"));
        this.setFed(compound.getBoolean("Fed"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SprayCooldown", this.getSprayCooldown());
        compound.putBoolean("Fed", this.isFed());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult interactionResult = super.mobInteract(player, hand);
        if (interactionResult.consumesAction()) {
            return interactionResult;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ACItemRegistry.SPELUNKIE.get()) && ((!this.level().isClientSide && this.getTarget() == player) || !this.isFed())) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            this.setFed(true);
            this.setLastHurtByMob(null);
            this.setTarget(null);
            this.level().broadcastEntityEvent(this, (byte) 49);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == 49) {
            ItemStack itemStack = new ItemStack(ACItemRegistry.SPELUNKIE.get());
            for (int i = 0; i < 8; ++i) {
                Vec3 headPos = new Vec3(0.0D, 0.1D, 0.5D)
                    .xRot(-this.getXRot() * ((float) Math.PI / 180F))
                    .yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), this.getX() + headPos.x, this.getY(0.5D) + headPos.y, this.getZ() + headPos.z, (this.random.nextFloat() - 0.5F) * 0.1F, this.random.nextFloat() * 0.15F, (this.random.nextFloat() - 0.5F) * 0.1F);
            }
            return;
        }
        super.handleEntityEvent(eventId);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GAMMAROACH_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GAMMAROACH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GAMMAROACH_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GAMMAROACH_STEP.get(), 1.0F, 1.0F);
        }
    }
}
