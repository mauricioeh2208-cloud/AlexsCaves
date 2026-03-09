package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RadgillEntity extends AbstractFish {
    private boolean wasJustInAcid;

    public RadgillEntity(EntityType<? extends AbstractFish> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.MAX_HEALTH, 8.0D);
    }

    public static boolean checkRadgillSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return spawnType == MobSpawnType.SPAWNER || level.getFluidState(pos).is(ACTagRegistry.ACID);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2;
    }

    @Override
    public boolean isInWater() {
        return super.isInWater() || this.isInAcid();
    }

    @Override
    public boolean isInWaterOrBubble() {
        return super.isInWaterOrBubble() || this.isInAcid();
    }

    public boolean isInAcid() {
        return this.getFluidHeight(ACTagRegistry.ACID) > 0.0D;
    }

    @Override
    public void tick() {
        super.tick();
        boolean inAcid = this.isInAcid();
        if (inAcid != this.wasJustInAcid) {
            for (int i = 0; i < 5 + this.random.nextInt(5); i++) {
                this.level().addParticle(
                    ACParticleRegistry.RADGILL_SPLASH.get(),
                    this.getRandomX(0.8F),
                    this.getBoundingBox().minY + 0.1F,
                    this.getRandomZ(0.8F),
                    (this.random.nextDouble() - 0.5D) * 0.3D,
                    0.1D + this.random.nextFloat() * 0.3D,
                    (this.random.nextDouble() - 0.5D) * 0.3D
                );
            }
            this.wasJustInAcid = inAcid;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ACItemRegistry.ACID_BUCKET.get()) && this.isAlive()) {
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            ItemStack result = this.getBucketItemStack();
            this.saveToBucketTag(result);
            ItemStack filled = ItemUtils.createFilledResult(itemStack, player, result, false);
            player.setItemInHand(hand, filled);
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                net.minecraft.advancements.CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, result);
            }
            this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (itemStack.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void saveToBucketTag(ItemStack bucket) {
        super.saveToBucketTag(bucket);
    }

    @Override
    public void loadFromBucketTag(CompoundTag compound) {
        super.loadFromBucketTag(compound);
        this.setFromBucket(true);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.RADGILL_BUCKET.get());
    }

    @Override
    protected SoundEvent getFlopSound() {
        return ACSoundRegistry.RADGILL_FLOP.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RADGILL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RADGILL_HURT.get();
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    private class WanderGoal extends Goal {
        private BlockPos target;
        private boolean jump;
        private boolean jumped;
        private int timeout;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!RadgillEntity.this.isInWater()) {
                return false;
            }
            if (RadgillEntity.this.getRandom().nextInt(10) != 0) {
                return false;
            }
            this.jump = RadgillEntity.this.random.nextFloat() <= 0.4F;
            this.target = this.findMoveToPos(this.jump);
            return this.target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return ((RadgillEntity.this.isInWater() && !this.jumped) || this.jump)
                && this.target != null
                && RadgillEntity.this.distanceToSqr(Vec3.atCenterOf(this.target)) < 3.0D
                && this.timeout < 200;
        }

        @Override
        public void stop() {
            this.jumped = false;
            this.timeout = 0;
            this.target = null;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }
            this.timeout++;
            RadgillEntity.this.getNavigation().moveTo(this.target.getX() + 0.5D, this.target.getY() + 0.25D, this.target.getZ() + 0.5D, 1.0D);
            double horizontalDistance = RadgillEntity.this.distanceToSqr(this.target.getX() + 0.5D, RadgillEntity.this.getY(), this.target.getZ() + 0.5D);
            if (horizontalDistance < 16.0D && this.jump && !this.jumped) {
                Vec3 targetVec = Vec3.atCenterOf(this.target);
                Vec3 movement = targetVec.subtract(RadgillEntity.this.position());
                if (movement.length() >= 1.0F) {
                    movement = movement.normalize();
                } else {
                    movement = Vec3.ZERO;
                }
                Vec3 jumpMotion = new Vec3(movement.x * 0.8F, 0.75F + RadgillEntity.this.random.nextFloat() * 0.3F, movement.z * 0.8F);
                RadgillEntity.this.setDeltaMovement(jumpMotion);
                if (RadgillEntity.this.getY() > this.target.getY()) {
                    this.jumped = true;
                } else {
                    RadgillEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, targetVec);
                }
            }
        }

        private boolean isLiquidAt(BlockPos pos) {
            FluidState state = RadgillEntity.this.level().getFluidState(pos);
            return state.is(net.minecraft.tags.FluidTags.WATER) || state.is(ACTagRegistry.ACID);
        }

        private BlockPos findMoveToPos(boolean jump) {
            BlockPos fishPos = RadgillEntity.this.blockPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos offset = fishPos.offset(RadgillEntity.this.random.nextInt(16) - 8, 0, RadgillEntity.this.random.nextInt(16) - 8);
                while (this.isLiquidAt(offset) && offset.getY() < RadgillEntity.this.level().getMaxBuildHeight()) {
                    offset = offset.above();
                }
                if (!this.isLiquidAt(offset) && this.isLiquidAt(offset.below())) {
                    BlockPos surface = offset.below();
                    if (jump) {
                        return surface.above(2 + RadgillEntity.this.random.nextInt(2));
                    }
                    surface = surface.below(1 + RadgillEntity.this.random.nextInt(4));
                    return this.isLiquidAt(surface) ? surface : null;
                }
            }
            return null;
        }
    }
}
