package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

public class RaycatEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> ABSORB_TARGET_ID = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LAY_TIME = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ABSORB_AMOUNT = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.FLOAT);

    private float sitProgress;
    private float prevSitProgress;
    private float layProgress;
    private float prevLayProgress;
    private float prevAbsorbAmount;
    private int absorbCooldown = 300 + this.random.nextInt(300);

    public RaycatEntity(EntityType<? extends RaycatEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.MAX_HEALTH, 24.0D)
            .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public static boolean checkRaycatSpawnRules(EntityType<RaycatEntity> type, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACBlockRegistry.RADROCK.get())
            && levelAccessor.getFluidState(pos).isEmpty()
            && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ABSORB_TARGET_ID, -1);
        builder.define(LAY_TIME, 0);
        builder.define(COMMAND, 0);
        builder.define(ABSORB_AMOUNT, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, Ingredient.of(Items.COD), false));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2D, 5.0F, 2.0F) {
            @Override
            public boolean canUse() {
                return RaycatEntity.this.getCommand() == 2 && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return RaycatEntity.this.getCommand() == 2 && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.3F));
        this.goalSelector.addGoal(5, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return levelReader.getBlockState(pos.below()).is(ACBlockRegistry.RADROCK.get())
            ? 10.0F
            : super.getWalkTargetValue(pos, levelReader);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ACItemRegistry.RADGILL.get());
    }

    @Override
    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        this.prevLayProgress = this.layProgress;
        this.prevAbsorbAmount = this.getAbsorbAmount();

        if (this.isInSittingPose() && this.sitProgress < 5.0F) {
            this.sitProgress++;
        }
        if (!this.isInSittingPose() && this.sitProgress > 0.0F) {
            this.sitProgress--;
        }
        if (this.getLayTime() > 0 && this.layProgress < 5.0F) {
            this.layProgress++;
        }
        if (this.getLayTime() <= 0 && this.layProgress > 0.0F) {
            this.layProgress--;
        }

        Entity absorbTarget = this.getAbsorbTarget();
        if (this.hasEffect(ACEffectRegistry.IRRADIATED.holder()) && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }
        if (this.absorbCooldown > 0) {
            this.absorbCooldown--;
        } else if (absorbTarget == null) {
            if (!this.level().isClientSide) {
                LivingEntity owner = this.getOwner();
                Entity closestIrradiated = null;
                if (owner != null && owner.distanceTo(this) < 20.0F && owner.hasEffect(ACEffectRegistry.IRRADIATED.holder())) {
                    closestIrradiated = owner;
                } else {
                    for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(9.0D))) {
                        if (!(entity instanceof RaycatEntity) && entity.hasEffect(ACEffectRegistry.IRRADIATED.holder()) && (closestIrradiated == null || closestIrradiated.distanceTo(this) > entity.distanceTo(this))) {
                            if (this.isTame() || !(entity instanceof Player)) {
                                closestIrradiated = entity;
                            }
                        }
                    }
                }
                this.setAbsorbTargetId(closestIrradiated == null ? -1 : closestIrradiated.getId());
                this.resetAbsorbCooldown();
            }
        } else if (this.getAbsorbAmount() <= 0.0F) {
            this.setAbsorbAmount(1.0F);
            this.playSound(ACSoundRegistry.RAYCAT_ABSORB.get(), 1.0F, 1.0F);
        } else {
            this.setAbsorbAmount(Math.max(0.0F, this.getAbsorbAmount() - 0.05F));
            if (this.getAbsorbAmount() <= 0.0F) {
                int currentRad = this.hasEffect(ACEffectRegistry.IRRADIATED.holder()) ? this.getEffect(ACEffectRegistry.IRRADIATED.holder()).getAmplifier() + 1 : 0;
                this.heal(10.0F);
                this.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 200, currentRad));
                this.lookAt(absorbTarget, 30.0F, 30.0F);
                if (absorbTarget instanceof LivingEntity living) {
                    MobEffectInstance effectInstance = living.getEffect(ACEffectRegistry.IRRADIATED.holder());
                    if (effectInstance != null) {
                        int timeLeft = effectInstance.getDuration();
                        int level = effectInstance.getAmplifier();
                        living.removeEffect(ACEffectRegistry.IRRADIATED.holder());
                        if (level > 0) {
                            living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), timeLeft, level - 1));
                        }
                    }
                }
                this.setAbsorbTargetId(-1);
                if (!this.level().isClientSide) {
                    this.resetAbsorbCooldown();
                }
            }
        }
    }

    private void resetAbsorbCooldown() {
        this.absorbCooldown = 300 + this.random.nextInt(300);
    }

    @Nullable
    public Entity getAbsorbTarget() {
        int id = this.entityData.get(ABSORB_TARGET_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public void setAbsorbTargetId(int id) {
        this.entityData.set(ABSORB_TARGET_ID, id);
    }

    public float getSitProgress(float partialTicks) {
        return (this.prevSitProgress + (this.sitProgress - this.prevSitProgress) * partialTicks) * 0.2F;
    }

    public float getLayProgress(float partialTicks) {
        return (this.prevLayProgress + (this.layProgress - this.prevLayProgress) * partialTicks) * 0.2F;
    }

    public float getAbsorbAmount(float partialTicks) {
        return this.prevAbsorbAmount + (this.getAbsorbAmount() - this.prevAbsorbAmount) * partialTicks;
    }

    public float getAbsorbAmount() {
        return this.entityData.get(ABSORB_AMOUNT);
    }

    public void setAbsorbAmount(float absorbAmount) {
        this.entityData.set(ABSORB_AMOUNT, absorbAmount);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public int getLayTime() {
        return this.entityData.get(LAY_TIME);
    }

    public void setLayTime(int layTime) {
        this.entityData.set(LAY_TIME, layTime);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Command", this.getCommand());
        compound.putInt("LayTime", this.getLayTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommand(compound.getInt("Command"));
        this.setLayTime(compound.getInt("LayTime"));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ACItemRegistry.RADGILL.get()) && !this.isTame()) {
            this.usePlayerItem(player, hand, itemStack);
            if (this.getRandom().nextInt(3) == 0) {
                this.tame(player);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        InteractionResult itemInteraction = itemStack.interactLivingEntity(player, this, hand);
        InteractionResult parent = super.mobInteract(player, hand);
        if (!itemInteraction.consumesAction() && !parent.consumesAction() && this.isTame() && this.isOwnedBy(player) && !this.isFood(itemStack) && !player.isShiftKeyDown()) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.displayClientMessage(Component.translatable("entity.alexscaves.all.command_" + this.getCommand(), this.getName()), true);
            this.setOrderedToSit(this.getCommand() == 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return parent.consumesAction() ? parent : itemInteraction;
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack itemStack) {
        if (this.isFood(itemStack)) {
            this.playSound(ACSoundRegistry.RAYCAT_EAT.get(), 1.0F, 1.0F);
        }
        super.usePlayerItem(player, hand, itemStack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob otherParent) {
        return ACEntityRegistry.RAYCAT.get().create(serverLevel);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTame() ? ACSoundRegistry.RAYCAT_TAME_IDLE.get() : ACSoundRegistry.RAYCAT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RAYCAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RAYCAT_HURT.get();
    }
}
