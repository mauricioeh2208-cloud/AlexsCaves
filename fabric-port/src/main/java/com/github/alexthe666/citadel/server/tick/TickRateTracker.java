package com.github.alexthe666.citadel.server.tick;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class TickRateTracker {
    public final List<TickRateModifier> tickRateModifierList = new ArrayList<>();
    private long masterTickCount;

    public void masterTick() {
        masterTickCount++;
        for (TickRateModifier modifier : tickRateModifierList) {
            modifier.masterTick();
        }
        tickRateModifierList.removeIf(TickRateModifier::doRemove);
    }

    public float getEntityTickLengthModifier(Entity entity) {
        float modifier = 1.0F;
        for (TickRateModifier tickRateModifier : tickRateModifierList) {
            if (tickRateModifier.getType().isLocal() && tickRateModifier.appliesTo(entity.level(), entity.getX(), entity.getY(), entity.getZ())) {
                modifier *= tickRateModifier.getTickRateMultiplier();
            }
        }
        return modifier;
    }

    public boolean shouldSkipEntityTick(Entity entity) {
        float modifier = getEntityTickLengthModifier(entity);
        if (modifier <= 1.0F) {
            return false;
        }
        int interval = Math.max(1, Math.round(modifier));
        return (masterTickCount + entity.getId()) % interval != 0;
    }

    public boolean hasModifiersActive() {
        return !tickRateModifierList.isEmpty();
    }
}
