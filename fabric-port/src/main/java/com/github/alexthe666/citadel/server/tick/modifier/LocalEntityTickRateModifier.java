package com.github.alexthe666.citadel.server.tick.modifier;

import com.github.alexthe666.citadel.server.entity.IModifiesTime;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LocalEntityTickRateModifier extends LocalTickRateModifier {
    private final int entityId;
    private final EntityType<?> expectedEntityType;

    public LocalEntityTickRateModifier(int entityId, EntityType<?> expectedEntityType, double range, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.LOCAL_ENTITY, range, dimension, durationInMasterTicks, tickRateMultiplier);
        this.entityId = entityId;
        this.expectedEntityType = expectedEntityType;
    }

    @Override
    public Vec3 getCenter(Level level) {
        Entity entity = level.getEntity(entityId);
        return entity == null ? Vec3.ZERO : entity.position();
    }

    @Override
    public boolean appliesTo(Level level, double x, double y, double z) {
        return super.appliesTo(level, x, y, z) && isEntityValid(level);
    }

    public boolean isEntityValid(Level level) {
        Entity entity = level.getEntity(entityId);
        return entity != null
            && !entity.isRemoved()
            && entity.isAlive()
            && entity.getType() == expectedEntityType
            && (!(entity instanceof IModifiesTime modifiesTime) || modifiesTime.isTimeModificationValid(this));
    }

    public int getEntityId() {
        return entityId;
    }
}
