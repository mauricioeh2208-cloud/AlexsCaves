package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.EnumSet;
import java.util.List;

public class SeaPigEntity extends WaterAnimal implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SeaPigEntity.class, EntityDataSerializers.BOOLEAN);
    public static final ResourceKey<LootTable> DIGESTION_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("alexscaves", "gameplay/sea_pig_digestion"));

    private float digestProgress;
    private float prevDigestProgress;
    private float squishProgress;
    private float prevSquishProgress;

    public SeaPigEntity(EntityType<? extends SeaPigEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.75F, 0.5F, false);
        this.setAirSupply(40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.05D)
            .add(Attributes.MAX_HEALTH, 8.0D);
    }

    public static boolean checkSeaPigSpawnRules(EntityType<SeaPigEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)
            && blockPos.getY() < levelAccessor.getSeaLevel() - 25;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROM_BUCKET, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 4;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getFluidState(pos.below()).isEmpty() && level.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevDigestProgress = this.digestProgress;
        this.prevSquishProgress = this.squishProgress;
        if (this.isDigesting() && this.digestProgress < 1.0F) {
            if (this.digestProgress == 0.0F) {
                this.playSound(ACSoundRegistry.SEA_PIG_EAT.get(), 1.0F, 1.0F);
            }
            this.digestProgress += 0.05F;
            if (this.digestProgress >= 1.0F) {
                this.digestProgress = 0.0F;
                this.prevDigestProgress = 0.0F;
                this.digestItem();
            }
        }
        boolean grounded = this.onGround() && !this.isInWaterOrBubble();
        if (grounded && this.squishProgress < 5.0F) {
            this.squishProgress++;
        }
        if (!grounded && this.squishProgress > 0.0F) {
            this.squishProgress--;
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            delta = delta.scale(0.8D);
            if (this.jumping || (this.horizontalCollision && this.level().getBlockState(this.blockPosition().above()).getFluidState().is(FluidTags.WATER))) {
                delta = delta.add(0.0D, 0.03D, 0.0D);
            } else {
                delta = delta.add(0.0D, -0.03D, 0.0D);
            }
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float movement = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0.0D, this.getZ() - this.zo);
        this.walkAnimation.update(Math.min(movement * 128.0F, 1.0F), 0.4F);
    }

    @Override
    protected void handleAirSupply(int previousAir) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(previousAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(40);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        InteractionResult interaction = Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
        if (itemStack.is(ACTagRegistry.SEA_PIG_DIGESTS) && !this.isDigesting() && !interaction.consumesAction()) {
            ItemStack copy = itemStack.copy();
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            copy.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, copy);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return interaction;
    }

    public boolean isDigesting() {
        return this.getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.SEA_PIG_DIGESTS);
    }

    public float getDigestProgress(float partialTick) {
        return Math.min(1.0F, this.prevDigestProgress + (this.digestProgress - this.prevDigestProgress) * partialTick);
    }

    public float getSquishProgress(float partialTick) {
        return (this.prevSquishProgress + (this.squishProgress - this.prevSquishProgress) * partialTick) * 0.2F;
    }

    private void digestItem() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(DIGESTION_LOOT_TABLE);
            LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.PIGLIN_BARTER);
            List<ItemStack> items = lootTable.getRandomItems(params);
            items.forEach(this::spawnAtLocation);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    private void doInitialPosing(LevelAccessor levelAccessor) {
        BlockPos down = this.blockPosition();
        while (!levelAccessor.getFluidState(down).isEmpty() && down.getY() > levelAccessor.getMinBuildHeight()) {
            down = down.below();
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
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
        return new ItemStack(ACItemRegistry.SEA_PIG_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.SEA_PIG_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.SEA_PIG_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SEA_PIG_DEATH.get();
    }

    private class WanderGoal extends Goal {
        private double x;
        private double y;
        private double z;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (SeaPigEntity.this.getRandom().nextInt(50) != 0) {
                return false;
            }
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
            return SeaPigEntity.this.distanceToSqr(this.x, this.y, this.z) > 4.0D;
        }

        @Override
        public void tick() {
            SeaPigEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
        }

        private BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource random = SeaPigEntity.this.getRandom();
            int range = 10;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = SeaPigEntity.this.blockPosition().offset(random.nextInt(range) - range / 2, random.nextInt(range) - range / 2, random.nextInt(range) - range / 2);
                if (SeaPigEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER) && blockPos.getY() > SeaPigEntity.this.level().getMinBuildHeight() + 1) {
                    result = blockPos;
                }
            }
            return result;
        }

        private Vec3 getPosition() {
            BlockPos water = this.findWaterBlock();
            if (SeaPigEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                while (SeaPigEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER) && water.getY() > SeaPigEntity.this.level().getMinBuildHeight() + 1) {
                    water = water.below();
                }
                return Vec3.atCenterOf(water.above());
            }
            return water == null ? DefaultRandomPos.getPos(SeaPigEntity.this, 7, 3) : Vec3.atCenterOf(water);
        }
    }
}
