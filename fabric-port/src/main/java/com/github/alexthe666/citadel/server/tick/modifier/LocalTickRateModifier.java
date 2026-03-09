package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class LocalTickRateModifier extends TickRateModifier {
    private final double range;
    private final ResourceKey<Level> dimension;

    protected LocalTickRateModifier(TickRateModifierType type, double range, ResourceKey<Level> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(type, durationInMasterTicks, tickRateMultiplier);
        this.range = range;
        this.dimension = dimension;
    }

    public double getRange() {
        return range;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public abstract Vec3 getCenter(Level level);

    @Override
    public boolean appliesTo(Level level, double x, double y, double z) {
        if (level.dimension() != dimension) {
            return false;
        }
        Vec3 center = getCenter(level);
        return center.distanceToSqr(x, y, z) < range * range;
    }
}
