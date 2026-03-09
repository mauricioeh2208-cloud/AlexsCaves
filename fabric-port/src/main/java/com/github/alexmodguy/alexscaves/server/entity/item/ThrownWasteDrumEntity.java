package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ThrownWasteDrumEntity extends Entity {
    private static final EntityDataAccessor<Integer> ON_GROUND_FOR = SynchedEntityData.defineId(ThrownWasteDrumEntity.class, EntityDataSerializers.INT);

    public static final int MAX_TIME = 20;

    private BlockPos removeWasteAt;

    public ThrownWasteDrumEntity(EntityType<? extends ThrownWasteDrumEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ON_GROUND_FOR, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        if (this.onGround()) {
            this.setOnGroundFor(this.getOnGroundFor() + 1);
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.7D, 0.7D));
        }
        if (this.getDeltaMovement().lengthSqr() > 0.0009D) {
            AABB killBox = this.getBoundingBox();
            boolean hitTarget = false;
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, killBox)) {
                if (!(entity instanceof BrainiacEntity) && entity.hurt(ACDamageTypes.causeAcidDamage(this.level().registryAccess()), 2.0F)) {
                    hitTarget = true;
                }
            }
            if (hitTarget) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
            }
        }
        if (this.level().isClientSide) {
            return;
        }
        if (this.getOnGroundFor() == MAX_TIME) {
            BlockPos landed = this.blockPosition();
            while (landed.getY() < this.level().getMaxBuildHeight()) {
                BlockState state = this.level().getBlockState(landed);
                if (state.isAir() || state.getFluidState().is(ACTagRegistry.ACID)) {
                    break;
                }
                landed = landed.above();
            }
            this.removeWasteAt = landed;
            if (this.level().getBlockState(landed).isAir() || this.level().getFluidState(landed).is(ACTagRegistry.ACID)) {
                this.level().setBlockAndUpdate(landed, ACBlockRegistry.ACID.get().defaultBlockState());
            }
        }
        if (this.getOnGroundFor() >= MAX_TIME + 15) {
            this.discard();
            if (this.removeWasteAt != null && this.level().getBlockState(this.removeWasteAt).is(ACBlockRegistry.ACID.get())) {
                this.level().setBlockAndUpdate(this.removeWasteAt, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public int getOnGroundFor() {
        return this.entityData.get(ON_GROUND_FOR);
    }

    public void setOnGroundFor(int time) {
        this.entityData.set(ON_GROUND_FOR, time);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setOnGroundFor(compoundTag.getInt("OnGroundFor"));
        if (compoundTag.contains("RemoveWasteAt")) {
            this.removeWasteAt = BlockPos.of(compoundTag.getLong("RemoveWasteAt"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("OnGroundFor", this.getOnGroundFor());
        if (this.removeWasteAt != null) {
            compoundTag.putLong("RemoveWasteAt", this.removeWasteAt.asLong());
        }
    }
}
