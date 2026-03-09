package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TrilocarisEntity extends WaterAnimal implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(TrilocarisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(TrilocarisEntity.class, EntityDataSerializers.INT);

    private float groundProgress;
    private float prevGroundProgress;
    private float biteProgress;
    private float prevBiteProgress;
    private int timeSwimming;
    private int lastStepSoundTimestamp = -1;
    public boolean crawling;

    public TrilocarisEntity(EntityType<? extends TrilocarisEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 1.0F, 0.65F, false);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.15D)
            .add(Attributes.ATTACK_DAMAGE, 1.0D)
            .add(Attributes.MAX_HEALTH, 10.0D);
    }

    public static boolean checkTrilocarisSpawnRules(EntityType<? extends LivingEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        FluidState fluidState = levelAccessor.getFluidState(blockPos);
        return fluidState.is(FluidTags.WATER)
            && fluidState.getAmount() >= 8
            && isInCave(levelAccessor, blockPos);
    }

    private static boolean isInCave(ServerLevelAccessor levelAccessor, BlockPos pos) {
        while (levelAccessor.getFluidState(pos).is(FluidTags.WATER)) {
            pos = pos.above();
        }
        return !levelAccessor.canSeeSky(pos) && pos.getY() < levelAccessor.getSeaLevel();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROM_BUCKET, false);
        builder.define(ATTACK_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SemiAquaticPathNavigator(this, level);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 4;
    }

    @Override
    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getFluidState(pos.below()).isEmpty() && level.getFluidState(pos).is(FluidTags.WATER)
            ? 10.0F
            : super.getWalkTargetValue(pos, level);
    }

    @Override
    protected void handleAirSupply(int airSupply) {
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
    public void tick() {
        super.tick();
        this.prevGroundProgress = this.groundProgress;
        this.prevBiteProgress = this.biteProgress;
        if (this.onGround() && this.groundProgress < 5.0F) {
            this.groundProgress++;
        }
        if (!this.onGround() && this.groundProgress > 0.0F) {
            this.groundProgress--;
        }
        int attackTick = this.entityData.get(ATTACK_TICK);
        if (attackTick > 0) {
            this.entityData.set(ATTACK_TICK, attackTick - 1);
            if (this.biteProgress < 5.0F) {
                this.biteProgress++;
            }
        } else {
            if (this.biteProgress > 4.0F && this.getTarget() != null && this.distanceTo(this.getTarget()) < 1.3D) {
                this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
            if (this.biteProgress > 0.0F) {
                this.biteProgress--;
            }
        }
        if (!this.level().isClientSide) {
            float horizontalSpeed = (float) this.getDeltaMovement().horizontalDistance();
            if (this.crawling || !this.isInWaterOrBubble()) {
                this.timeSwimming = 0;
                if (horizontalSpeed > 0.01F && this.tickCount - this.lastStepSoundTimestamp > 10) {
                    this.lastStepSoundTimestamp = this.tickCount;
                    this.playSound(ACSoundRegistry.TRILOCARIS_STEP.get(), 0.2F, 1.0F);
                }
            } else {
                this.timeSwimming++;
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            if (this.crawling) {
                delta = delta.scale(0.8D);
                if (this.jumping || this.horizontalCollision) {
                    delta = delta.add(0.0D, 0.1D, 0.0D);
                } else {
                    delta = delta.add(0.0D, -0.05D, 0.0D);
                }
            }
            this.setDeltaMovement(delta.scale(0.8D));
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.entityData.set(ATTACK_TICK, 5);
        return super.doHurtTarget(entity);
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float speedModifier = !this.onGround() ? 4.0F : 16.0F;
        float movement = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        this.walkAnimation.update(Math.min(movement * speedModifier, 1.0F), 0.4F);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.TRILOCARIS_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public void saveToBucketTag(ItemStack bucket) {
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
    }

    @Override
    public void loadFromBucketTag(CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
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
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TRILOCARIS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TRILOCARIS_DEATH.get();
    }

    public float getGroundProgress(float partialTick) {
        return (this.prevGroundProgress + (this.groundProgress - this.prevGroundProgress) * partialTick) * 0.2F;
    }

    public float getBiteProgress(float partialTick) {
        return (this.prevBiteProgress + (this.biteProgress - this.prevBiteProgress) * partialTick) * 0.2F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    private class WanderGoal extends Goal {
        private double x;
        private double y;
        private double z;
        private boolean wantsToCrawl;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (TrilocarisEntity.this.getRandom().nextInt(20) != 0 && TrilocarisEntity.this.crawling) {
                return false;
            }
            this.wantsToCrawl = TrilocarisEntity.this.crawling
                ? TrilocarisEntity.this.getRandom().nextFloat() < 0.5F
                : TrilocarisEntity.this.timeSwimming > 300 || TrilocarisEntity.this.getRandom().nextFloat() < 0.15F;
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
            return TrilocarisEntity.this.distanceToSqr(this.x, this.y, this.z) > 4.0D;
        }

        @Override
        public void tick() {
            TrilocarisEntity.this.crawling = this.wantsToCrawl;
            TrilocarisEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
        }

        private BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource randomSource = TrilocarisEntity.this.getRandom();
            int range = 10;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = TrilocarisEntity.this.blockPosition().offset(
                    randomSource.nextInt(range) - range / 2,
                    randomSource.nextInt(range) - range / 2,
                    randomSource.nextInt(range) - range / 2
                );
                if (TrilocarisEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER)
                    && blockPos.getY() > TrilocarisEntity.this.level().getMinBuildHeight() + 1) {
                    result = blockPos;
                }
            }
            return result;
        }

        private Vec3 getPosition() {
            BlockPos water = this.findWaterBlock();
            if (TrilocarisEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                if (this.wantsToCrawl) {
                    while (TrilocarisEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER)
                        && water.getY() > TrilocarisEntity.this.level().getMinBuildHeight() + 1) {
                        water = water.below();
                    }
                    water = water.above();
                }
                return Vec3.atCenterOf(water);
            }
            return water == null ? DefaultRandomPos.getPos(TrilocarisEntity.this, 7, 3) : Vec3.atCenterOf(water);
        }
    }

    private class MeleeGoal extends Goal {
        private int duration;
        private int cooldown;

        private MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TrilocarisEntity.this.getTarget() != null && TrilocarisEntity.this.getTarget().isAlive() && this.duration < 300;
        }

        @Override
        public void tick() {
            this.duration++;
            LivingEntity target = TrilocarisEntity.this.getTarget();
            if (target != null && target.isAlive()) {
                if (target.isInWaterOrBubble()) {
                    TrilocarisEntity.this.getNavigation().moveTo(target, 1.0D);
                }
                if (TrilocarisEntity.this.distanceTo(target) < 1.2F && this.cooldown == 0) {
                    TrilocarisEntity.this.doHurtTarget(target);
                    this.cooldown = 30;
                }
            }
            if (this.cooldown > 0) {
                this.cooldown--;
            }
        }

        @Override
        public void stop() {
            this.duration = 0;
            this.cooldown = 0;
            TrilocarisEntity.this.setLastHurtByMob(null);
            TrilocarisEntity.this.setTarget(null);
        }
    }
}
