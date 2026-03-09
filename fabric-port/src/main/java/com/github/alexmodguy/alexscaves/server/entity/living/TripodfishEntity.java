package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TripodfishEntity extends WaterAnimal implements Bucketable {
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(TripodfishEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(TripodfishEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions STANDING_SIZE = EntityDimensions.scalable(0.95F, 1.5F);

    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private float standProgress;
    private float prevStandProgress;
    private boolean hasStandingSize;
    private int timeSwimming;
    private int timeStanding;
    private int navigateTypeLength = 300;
    private BlockPos hurtPos;
    private int fleeFor;

    public TripodfishEntity(EntityType<? extends TripodfishEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.5F, 60.0F);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.MAX_HEALTH, 8.0D);
    }

    public static boolean checkTripodfishSpawnRules(EntityType<? extends LivingEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)
            && blockPos.getY() < levelAccessor.getSeaLevel() - 30
            && randomSource.nextBoolean();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STANDING, false);
        builder.define(FROM_BUCKET, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidHurtGoal());
        this.goalSelector.addGoal(2, new WanderGoal());
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !TripodfishEntity.this.isStanding();
            }
        });
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !TripodfishEntity.this.isStanding();
            }
        });
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if (this.getTarget() == null && !this.isStanding()) {
                delta = delta.add(0.0D, -0.005D, 0.0D);
            }
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(this.isStanding() ? 0.3D : 0.9D));
            return;
        }
        super.travel(travelVector);
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2;
    }

    @Override
    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isStanding() ? STANDING_SIZE.scale(this.getScale()) : super.getDefaultDimensions(pose);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevStandProgress = this.standProgress;
        this.prevFishPitch = this.fishPitch;
        this.prevLandProgress = this.landProgress;
        float pitchTarget = (float) this.getDeltaMovement().y * 3.0F;
        if (this.isStanding()) {
            if (this.standProgress < 10.0F) {
                this.standProgress++;
            }
            if (!this.hasStandingSize) {
                this.hasStandingSize = true;
                this.refreshDimensions();
                this.navigateTypeLength = 400 + this.random.nextInt(400);
            }
            this.timeStanding++;
            this.timeSwimming = 0;
            pitchTarget = 0.0F;
            this.getNavigation().stop();
            if (!this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.05D, 0.0D).multiply(0.5D, 1.0D, 0.5D));
            }
        } else {
            if (this.standProgress > 0.0F) {
                this.standProgress--;
            }
            this.timeStanding = 0;
            this.timeSwimming++;
            if (this.hasStandingSize) {
                this.hasStandingSize = false;
                double y = this.getBbHeight() * 0.35F + this.getY();
                this.refreshDimensions();
                this.setPos(this.getX(), y, this.getZ());
                this.navigateTypeLength = 400 + this.random.nextInt(400);
            }
        }
        this.fishPitch = Mth.approachDegrees(this.fishPitch, Mth.clamp(pitchTarget, -1.4F, 1.4F) * -(180.0F / (float) Math.PI), 5.0F);
        boolean grounded = !this.isInWaterOrBubble();
        if (grounded && this.landProgress < 5.0F) {
            this.landProgress++;
        }
        if (!grounded && this.landProgress > 0.0F) {
            this.landProgress--;
        }
        if (!this.isInWaterOrBubble() && this.isAlive() && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.playSound(ACSoundRegistry.TRIPODFISH_FLOP.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.fleeFor > 0 && --this.fleeFor == 0) {
            this.hurtPos = null;
        }
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float movement = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        this.walkAnimation.update(Math.min(movement * 6.0F, 1.0F), 0.4F);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        boolean result = super.hurt(damageSource, damageAmount);
        if (result) {
            this.fleeFor = 40 + this.random.nextInt(40);
            this.hurtPos = this.blockPosition();
        }
        return result;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnType, SpawnGroupData spawnGroupData) {
        if (spawnType == MobSpawnType.NATURAL) {
            this.doInitialPosing(levelAccessor);
        }
        return super.finalizeSpawn(levelAccessor, difficultyInstance, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public void saveToBucketTag(ItemStack bucket) {
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
    }

    @Override
    public void loadFromBucketTag(CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        this.setAirSupply(2000);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.TRIPODFISH_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TRIPODFISH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TRIPODFISH_HURT.get();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(damageSource);
    }

    public float getFishPitch(float partialTick) {
        return this.prevFishPitch + (this.fishPitch - this.prevFishPitch) * partialTick;
    }

    public float getStandProgress(float partialTick) {
        return (this.prevStandProgress + (this.standProgress - this.prevStandProgress) * partialTick) * 0.1F;
    }

    private void doInitialPosing(LevelAccessor levelAccessor) {
        BlockPos down = this.blockPosition();
        while (!levelAccessor.getFluidState(down).isEmpty() && down.getY() > levelAccessor.getMinBuildHeight()) {
            down = down.below();
        }
        this.setPos(down.getX() + 0.5D, down.getY() + 1.0D, down.getZ() + 0.5D);
    }

    private class AvoidHurtGoal extends Goal {
        private Vec3 fleeTarget;

        private AvoidHurtGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TripodfishEntity.this.hurtPos != null && TripodfishEntity.this.fleeFor > 0;
        }

        @Override
        public void start() {
            TripodfishEntity.this.setStanding(false);
            this.fleeTarget = null;
        }

        @Override
        public void tick() {
            if ((this.fleeTarget == null || TripodfishEntity.this.distanceToSqr(this.fleeTarget) < 6.0D) && TripodfishEntity.this.hurtPos != null) {
                this.fleeTarget = DefaultRandomPos.getPosAway(TripodfishEntity.this, 16, 7, Vec3.atCenterOf(TripodfishEntity.this.hurtPos));
            }
            if (this.fleeTarget != null) {
                TripodfishEntity.this.getNavigation().moveTo(this.fleeTarget.x, this.fleeTarget.y, this.fleeTarget.z, 1.6D);
            }
        }
    }

    private class WanderGoal extends Goal {
        private double x;
        private double y;
        private double z;
        private boolean wantsToStand;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (TripodfishEntity.this.getRandom().nextInt(100) != 0
                && TripodfishEntity.this.isStanding()
                && TripodfishEntity.this.timeStanding < TripodfishEntity.this.navigateTypeLength) {
                return false;
            }
            this.wantsToStand = TripodfishEntity.this.isStanding()
                ? false
                : TripodfishEntity.this.timeSwimming > 300 || TripodfishEntity.this.getRandom().nextFloat() < 0.2F;
            Vec3 target = this.getPosition();
            if (target == null) {
                return false;
            }
            this.x = target.x;
            this.y = target.y;
            this.z = target.z;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !TripodfishEntity.this.getNavigation().isDone() && TripodfishEntity.this.distanceToSqr(this.x, this.y, this.z) > 9.0D;
        }

        @Override
        public void start() {
            TripodfishEntity.this.setStanding(false);
            TripodfishEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
        }

        @Override
        public void stop() {
            BlockPos ground = TripodfishEntity.this.blockPosition();
            int down = 0;
            while (TripodfishEntity.this.level().getFluidState(ground).is(FluidTags.WATER)
                && down < 3
                && ground.getY() > TripodfishEntity.this.level().getMinBuildHeight()) {
                ground = ground.below();
                down++;
            }
            if (this.wantsToStand && down <= 2) {
                TripodfishEntity.this.setStanding(true);
                TripodfishEntity.this.getNavigation().stop();
                TripodfishEntity.this.setDeltaMovement(Vec3.ZERO);
            }
        }

        private BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource randomSource = TripodfishEntity.this.getRandom();
            int range = 20;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = TripodfishEntity.this.blockPosition().offset(
                    randomSource.nextInt(range) - range / 2,
                    randomSource.nextInt(range) - range / 2,
                    randomSource.nextInt(range) - range / 2
                );
                if (TripodfishEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER)
                    && blockPos.getY() > TripodfishEntity.this.level().getMinBuildHeight()) {
                    result = blockPos;
                }
            }
            return result;
        }

        private boolean isTargetBlocked(Vec3 target) {
            Vec3 from = new Vec3(TripodfishEntity.this.getX(), TripodfishEntity.this.getEyeY(), TripodfishEntity.this.getZ());
            return TripodfishEntity.this.level().clip(new ClipContext(from, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, TripodfishEntity.this)).getType() != HitResult.Type.MISS;
        }

        private Vec3 getPosition() {
            BlockPos water = this.findWaterBlock();
            if (TripodfishEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                while (TripodfishEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER)
                    && water.getY() > TripodfishEntity.this.level().getMinBuildHeight() + 1) {
                    water = water.below();
                }
                BlockState seafloorState = TripodfishEntity.this.level().getBlockState(water.below());
                if (this.wantsToStand
                    && (seafloorState.is(Blocks.MAGMA_BLOCK)
                    || !seafloorState.getFluidState().isEmpty() && !seafloorState.getFluidState().is(FluidTags.WATER))) {
                    return null;
                }
                BlockPos above = water.above(this.wantsToStand ? 1 : 3 + TripodfishEntity.this.random.nextInt(3));
                while (!TripodfishEntity.this.level().getFluidState(above).is(FluidTags.WATER) && above.getY() > water.getY()) {
                    above = above.below();
                }
                Vec3 target = Vec3.atCenterOf(above);
                return this.isTargetBlocked(target) ? null : target;
            }
            return water == null ? DefaultRandomPos.getPos(TripodfishEntity.this, 7, 3) : Vec3.atCenterOf(water);
        }
    }
}
