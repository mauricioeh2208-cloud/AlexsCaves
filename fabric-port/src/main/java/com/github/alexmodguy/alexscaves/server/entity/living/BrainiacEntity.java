package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.EnumSet;

public class BrainiacEntity extends Monster {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_THROW_BARREL = 1;
    public static final int ANIMATION_DRINK_BARREL = 2;
    public static final int ANIMATION_BITE = 3;
    public static final int ANIMATION_SMASH = 4;

    private static final EntityDataAccessor<Boolean> HAS_BARREL = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TONGUE_SHOOT_TICK = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIMATION = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.INT);

    private int animationTick;

    public BrainiacEntity(EntityType<? extends BrainiacEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D)
            .add(Attributes.ARMOR, 8.0D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public static boolean checkBrainiacSpawnRules(EntityType<BrainiacEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return Monster.checkMonsterSpawnRules(entityType, levelAccessor, spawnType, blockPos, randomSource);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_BARREL, true);
        builder.define(TONGUE_SHOOT_TICK, 0);
        builder.define(ANIMATION, ANIMATION_NONE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ANIMATION.equals(key)) {
            this.animationTick = 0;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new PickupBarrelGoal());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimation() != ANIMATION_NONE) {
            this.animationTick++;
            if (this.animationTick > this.getAnimationLength(this.getAnimation())) {
                this.setAnimation(ANIMATION_NONE);
            }
        }
        if (this.level().isClientSide) {
            return;
        }
        if (this.hasBarrel()) {
            if (this.getAnimation() == ANIMATION_DRINK_BARREL && this.getAnimationTick() == 60) {
                this.setHasBarrel(false);
                this.heal(10.0F);
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 4));
            }
            if (this.getAnimation() == ANIMATION_THROW_BARREL && this.getAnimationTick() == 15) {
                LivingEntity attackTarget = this.getTarget();
                if (attackTarget != null && attackTarget.isAlive()) {
                    this.setHasBarrel(false);
                    Vec3 handOffset = new Vec3(0.65F, -0.3F, 0.9F)
                        .xRot(-this.getXRot() * Mth.DEG_TO_RAD)
                        .yRot(-this.yHeadRot * Mth.DEG_TO_RAD);
                    Vec3 handPosition = this.getEyePosition().add(handOffset);
                    ThrownWasteDrumEntity wasteDrumEntity = ACEntityRegistry.THROWN_WASTE_DRUM.get().create(this.level());
                    if (wasteDrumEntity != null) {
                        wasteDrumEntity.setPos(handPosition);
                        Vec3 toss = attackTarget.getEyePosition().subtract(handPosition).multiply(0.35F, 0.0F, 0.35F).add(0.0D, 0.4D, 0.0D);
                        wasteDrumEntity.setYRot(-((float) Mth.atan2(toss.x, toss.z)) * Mth.RAD_TO_DEG);
                        wasteDrumEntity.setDeltaMovement(toss.normalize().scale(Math.max(0.6D, attackTarget.distanceTo(this) * 0.2D)));
                        this.level().addFreshEntity(wasteDrumEntity);
                    }
                }
            }
        }
        if (this.getLickTicks() > 0) {
            this.setLickTicks(this.getLickTicks() - 1);
            LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null && attackTarget.isAlive() && this.hasLineOfSight(attackTarget) && attackTarget.distanceTo(this) < 20.0F) {
                this.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
                if (this.getLickTicks() == 10) {
                    this.postAttackEffect(attackTarget);
                    attackTarget.hurt(this.damageSources().mobAttack(this), 4.0F);
                    attackTarget.knockback(0.3D, attackTarget.getX() - this.getX(), attackTarget.getZ() - this.getZ());
                }
            } else {
                this.setLickTicks(0);
            }
        }
    }

    @Override
    public float maxUpStep() {
        return 1.1F;
    }

    public int getAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(int animation) {
        if (this.getAnimation() != animation) {
            this.entityData.set(ANIMATION, animation);
            this.animationTick = 0;
        }
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public boolean hasBarrel() {
        return this.entityData.get(HAS_BARREL);
    }

    public void setHasBarrel(boolean barrel) {
        this.entityData.set(HAS_BARREL, barrel);
    }

    public int getLickTicks() {
        return this.entityData.get(TONGUE_SHOOT_TICK);
    }

    public void setLickTicks(int ticks) {
        this.entityData.set(TONGUE_SHOOT_TICK, ticks);
    }

    public boolean isHoldingBarrelInHands() {
        return this.hasBarrel() && (this.getAnimation() == ANIMATION_THROW_BARREL || this.getAnimation() == ANIMATION_DRINK_BARREL) && this.getAnimationTick() > 10;
    }

    public void postAttackEffect(LivingEntity entity) {
        if (entity != null && entity.isAlive()) {
            entity.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 400));
        }
    }

    private int getAnimationLength(int animation) {
        return switch (animation) {
            case ANIMATION_THROW_BARREL -> 30;
            case ANIMATION_DRINK_BARREL -> 75;
            case ANIMATION_BITE -> 25;
            case ANIMATION_SMASH -> 20;
            default -> 0;
        };
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHasBarrel(compound.getBoolean("HasBarrel"));
        this.setAnimation(compound.getInt("Animation"));
        this.animationTick = compound.getInt("AnimationTick");
        this.setLickTicks(compound.getInt("LickTicks"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasBarrel", this.hasBarrel());
        compound.putInt("Animation", this.getAnimation());
        compound.putInt("AnimationTick", this.getAnimationTick());
        compound.putInt("LickTicks", this.getLickTicks());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.BRAINIAC_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.BRAINIAC_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.BRAINIAC_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ACSoundRegistry.BRAINIAC_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean causedByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, causedByPlayer);
        if (this.hasBarrel()) {
            this.spawnAtLocation(new ItemStack(ACBlockRegistry.WASTE_DRUM.get()));
        }
    }

    private class MeleeGoal extends Goal {
        private int tongueCooldown;

        private MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = BrainiacEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = BrainiacEntity.this.getTarget();
            if (target == null) {
                return;
            }
            if (this.tongueCooldown > 0) {
                this.tongueCooldown--;
            }
            BrainiacEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (BrainiacEntity.this.getAnimation() == ANIMATION_NONE) {
                BrainiacEntity.this.getNavigation().moveTo(target, 1.2D);
                double distance = BrainiacEntity.this.distanceTo(target);
                if (BrainiacEntity.this.hasLineOfSight(target)) {
                    if (BrainiacEntity.this.hasBarrel() && BrainiacEntity.this.getHealth() < BrainiacEntity.this.getMaxHealth() * 0.5F && BrainiacEntity.this.random.nextInt(20) == 0) {
                        BrainiacEntity.this.setAnimation(ANIMATION_DRINK_BARREL);
                        return;
                    }
                    if (BrainiacEntity.this.hasBarrel() && BrainiacEntity.this.getHealth() < BrainiacEntity.this.getMaxHealth() * 0.75F && distance < 20.0D && BrainiacEntity.this.random.nextInt(30) == 0) {
                        BrainiacEntity.this.setAnimation(ANIMATION_THROW_BARREL);
                        BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_THROW.get(), 1.0F, 1.0F);
                        return;
                    }
                    if (distance < BrainiacEntity.this.getBbWidth() + target.getBbWidth() + 3.5D) {
                        BrainiacEntity.this.setAnimation(BrainiacEntity.this.random.nextBoolean() ? ANIMATION_SMASH : ANIMATION_BITE);
                        BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_ATTACK.get(), 1.0F, 1.0F);
                        return;
                    }
                    if (this.tongueCooldown == 0 && distance < 20.0D && BrainiacEntity.this.random.nextInt(16) == 0) {
                        BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_LICK.get(), 1.0F, 1.0F);
                        BrainiacEntity.this.setLickTicks(20);
                        this.tongueCooldown = 15 + BrainiacEntity.this.random.nextInt(15);
                    }
                } else {
                    BrainiacEntity.this.setLickTicks(0);
                }
            }
            if (BrainiacEntity.this.getAnimation() == ANIMATION_SMASH && BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15) {
                this.checkAndDealDamage(target, 2.0F);
            }
            if (BrainiacEntity.this.getAnimation() == ANIMATION_BITE && BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15) {
                this.checkAndDealDamage(target, 1.0F);
            }
        }

        private void checkAndDealDamage(LivingEntity target, float damageMultiplier) {
            if (BrainiacEntity.this.hasLineOfSight(target) && BrainiacEntity.this.distanceTo(target) < BrainiacEntity.this.getBbWidth() + target.getBbWidth() + 2.0D) {
                target.hurt(BrainiacEntity.this.damageSources().mobAttack(BrainiacEntity.this), (float) BrainiacEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
                BrainiacEntity.this.postAttackEffect(target);
                target.knockback(0.3D, BrainiacEntity.this.getX() - target.getX(), BrainiacEntity.this.getZ() - target.getZ());
            }
        }
    }

    private class PickupBarrelGoal extends Goal {
        private BlockPos targetPos;
        private int searchCooldown;

        private PickupBarrelGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (BrainiacEntity.this.hasBarrel() || BrainiacEntity.this.getTarget() != null) {
                return false;
            }
            if (this.searchCooldown > 0) {
                this.searchCooldown--;
                return false;
            }
            this.searchCooldown = 40 + BrainiacEntity.this.getRandom().nextInt(40);
            this.targetPos = this.findNearestWasteDrum();
            return this.targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !BrainiacEntity.this.hasBarrel()
                && BrainiacEntity.this.getTarget() == null
                && this.targetPos != null
                && BrainiacEntity.this.level().getBlockState(this.targetPos).is(ACBlockRegistry.WASTE_DRUM.get());
        }

        @Override
        public void stop() {
            this.targetPos = null;
            BrainiacEntity.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.targetPos == null) {
                return;
            }
            Vec3 targetCenter = Vec3.atCenterOf(this.targetPos);
            BrainiacEntity.this.getLookControl().setLookAt(targetCenter.x, targetCenter.y, targetCenter.z);
            BrainiacEntity.this.getNavigation().moveTo(targetCenter.x, this.targetPos.getY() + 1.0D, targetCenter.z, 1.0D);
            if (targetCenter.distanceToSqr(BrainiacEntity.this.position()) <= Mth.square(BrainiacEntity.this.getBbWidth() + 1.0F)) {
                BrainiacEntity.this.getNavigation().stop();
                if (BrainiacEntity.this.getAnimation() == ANIMATION_NONE) {
                    BrainiacEntity.this.setAnimation(ANIMATION_BITE);
                }
                if (BrainiacEntity.this.getAnimation() == ANIMATION_BITE && BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15 && BrainiacEntity.this.level().getBlockState(this.targetPos).is(ACBlockRegistry.WASTE_DRUM.get())) {
                    BrainiacEntity.this.level().destroyBlock(this.targetPos, false);
                    BrainiacEntity.this.setHasBarrel(true);
                    this.stop();
                }
            }
        }

        private BlockPos findNearestWasteDrum() {
            BlockPos origin = BrainiacEntity.this.blockPosition();
            BlockPos best = null;
            double bestDistance = Double.MAX_VALUE;
            for (BlockPos checkPos : BlockPos.betweenClosed(origin.offset(-8, -3, -8), origin.offset(8, 3, 8))) {
                if (BrainiacEntity.this.level().getBlockState(checkPos).is(ACBlockRegistry.WASTE_DRUM.get())) {
                    double distance = checkPos.distToCenterSqr(BrainiacEntity.this.position());
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = checkPos.immutable();
                    }
                }
            }
            return best;
        }
    }
}
