package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Holds the EntityDataAccessor instances for magnetic entity data.
 * In 1.21, Entity.defineSynchedData is abstract, so we need to define data
 * in each concrete entity class that needs it.
 */
public class MagneticDataAccessors {
    
    // LivingEntity accessors
    public static final EntityDataAccessor<Float> LIVING_MAGNET_DELTA_X = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LIVING_MAGNET_DELTA_Y = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LIVING_MAGNET_DELTA_Z = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Direction> LIVING_MAGNET_ATTACHMENT_DIRECTION = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.DIRECTION);

    // ItemEntity accessors
    public static final EntityDataAccessor<Float> ITEM_MAGNET_DELTA_X = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ITEM_MAGNET_DELTA_Y = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ITEM_MAGNET_DELTA_Z = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Direction> ITEM_MAGNET_ATTACHMENT_DIRECTION = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.DIRECTION);

    // FallingBlockEntity accessors
    public static final EntityDataAccessor<Float> FALLING_MAGNET_DELTA_X = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> FALLING_MAGNET_DELTA_Y = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> FALLING_MAGNET_DELTA_Z = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Direction> FALLING_MAGNET_ATTACHMENT_DIRECTION = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.DIRECTION);

    /**
     * Get the magnetic delta X accessor for the given entity.
     */
    public static EntityDataAccessor<Float> getMagnetDeltaX(Entity entity) {
        if (entity instanceof LivingEntity) {
            return LIVING_MAGNET_DELTA_X;
        } else if (entity instanceof ItemEntity) {
            return ITEM_MAGNET_DELTA_X;
        } else if (entity instanceof FallingBlockEntity) {
            return FALLING_MAGNET_DELTA_X;
        }
        return null;
    }

    /**
     * Get the magnetic delta Y accessor for the given entity.
     */
    public static EntityDataAccessor<Float> getMagnetDeltaY(Entity entity) {
        if (entity instanceof LivingEntity) {
            return LIVING_MAGNET_DELTA_Y;
        } else if (entity instanceof ItemEntity) {
            return ITEM_MAGNET_DELTA_Y;
        } else if (entity instanceof FallingBlockEntity) {
            return FALLING_MAGNET_DELTA_Y;
        }
        return null;
    }

    /**
     * Get the magnetic delta Z accessor for the given entity.
     */
    public static EntityDataAccessor<Float> getMagnetDeltaZ(Entity entity) {
        if (entity instanceof LivingEntity) {
            return LIVING_MAGNET_DELTA_Z;
        } else if (entity instanceof ItemEntity) {
            return ITEM_MAGNET_DELTA_Z;
        } else if (entity instanceof FallingBlockEntity) {
            return FALLING_MAGNET_DELTA_Z;
        }
        return null;
    }

    /**
     * Get the magnetic attachment direction accessor for the given entity.
     */
    public static EntityDataAccessor<Direction> getMagnetAttachmentDirection(Entity entity) {
        if (entity instanceof LivingEntity) {
            return LIVING_MAGNET_ATTACHMENT_DIRECTION;
        } else if (entity instanceof ItemEntity) {
            return ITEM_MAGNET_ATTACHMENT_DIRECTION;
        } else if (entity instanceof FallingBlockEntity) {
            return FALLING_MAGNET_ATTACHMENT_DIRECTION;
        }
        return null;
    }
}
