package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.world.level.Level;

public abstract class TickRateModifier {
    private final TickRateModifierType type;
    private float maxDuration;
    private float duration;
    private float tickRateMultiplier;

    protected TickRateModifier(TickRateModifierType type, int maxDuration, float tickRateMultiplier) {
        this.type = type;
        this.maxDuration = maxDuration;
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public TickRateModifierType getType() {
        return type;
    }

    public float getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(float maxDuration) {
        this.maxDuration = maxDuration;
    }

    public float getTickRateMultiplier() {
        return tickRateMultiplier;
    }

    public void setTickRateMultiplier(float tickRateMultiplier) {
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public void masterTick() {
        duration++;
    }

    public boolean doRemove() {
        float scale = tickRateMultiplier <= 0.0F ? 1.0F : Math.max(1.0F, 1.0F / tickRateMultiplier);
        return duration >= maxDuration * scale;
    }

    public abstract boolean appliesTo(Level level, double x, double y, double z);
}
