package com.github.alexmodguy.alexscaves.server.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DeepsightEffect extends MobEffect {
    private int lastDuration = -1;
    private int firstDuration = -1;

    protected DeepsightEffect() {
        super(MobEffectCategory.BENEFICIAL, 0X002972);
    }

    public int getActiveTime() {
        return firstDuration - lastDuration;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        lastDuration = duration;
        if (duration <= 0) {
            lastDuration = -1;
            firstDuration = -1;
        }
        if (firstDuration == -1) {
            firstDuration = duration;
        }
        return duration > 0;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        lastDuration = -1;
        firstDuration = -1;
        super.onEffectStarted(entity, amplifier);
    }

    public static float getIntensity(Player player, float partialTicks) {
        MobEffectInstance instance = player.getEffect(ACEffectRegistry.DEEPSIGHT.holder());
        if (instance == null) {
            return 0.0F;
        }
        if (instance.isInfiniteDuration()) {
            return 1.0F;
        }
        Holder<MobEffect> effect = instance.getEffect();
        if (!(effect.value() instanceof DeepsightEffect deepsightEffect)) {
            return 0.0F;
        }
        float activeTime = deepsightEffect.getActiveTime() + partialTicks;
        int duration = instance.getDuration();
        return Math.min(20, Math.min(activeTime, duration + partialTicks)) * 0.05F;
    }
}
