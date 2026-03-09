package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.entity.util.DarknessIncarnateUserAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;

public class DarknessIncarnateEffect extends MobEffect {
    private int lastDuration = -1;
    private int firstDuration = -1;

    protected DarknessIncarnateEffect() {
        super(MobEffectCategory.BENEFICIAL, 0X510E0E);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        toggleFlight(entity, true);
        if (entity.onGround()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.1D, 0.0D));
        }
        if ((entity.tickCount + entity.getId() * 5) % 50 == 0 && entity.getRandom().nextInt(2) == 0) {
            entity.playSound(ACSoundRegistry.DARKNESS_INCARNATE_IDLE.get());
        }
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        lastDuration = -1;
        firstDuration = -1;
        super.onEffectStarted(entity, amplifier);
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

    public int getActiveTime() {
        return firstDuration - lastDuration;
    }

    public void toggleFlight(LivingEntity living, boolean flight) {
        if (!living.level().isClientSide && living instanceof ServerPlayer player) {
            boolean prevFlying = player.getAbilities().flying;
            boolean trueFlight = isCreativePlayer(living) || flight;
            player.getAbilities().mayfly = trueFlight;
            player.getAbilities().flying = trueFlight;
            if (flight) {
                player.getAbilities().setFlyingSpeed(0.2F);
            } else {
                player.getAbilities().setFlyingSpeed(0.05F);
                if (!player.isSpectator()) {
                    player.getAbilities().flying = false;
                    if (!player.isCreative()) {
                        player.getAbilities().mayfly = false;
                    }
                    if (player instanceof DarknessIncarnateUserAccessor darknessIncarnateUserAccessor) {
                        darknessIncarnateUserAccessor.setSlowFallingFlag(true);
                    }
                }
                player.onUpdateAbilities();
            }
            if (prevFlying != flight && flight) {
                player.onUpdateAbilities();
            }
        }
        living.fallDistance = 0.0F;
    }

    public static float getIntensity(LivingEntity living, float partialTicks, float scaleBy) {
        MobEffectInstance instance = living.getEffect(ACEffectRegistry.DARKNESS_INCARNATE.holder());
        if (instance == null) {
            return 0.0F;
        }
        if (instance.isInfiniteDuration()) {
            return scaleBy;
        }
        Holder<MobEffect> effectHolder = instance.getEffect();
        if (!(effectHolder.value() instanceof DarknessIncarnateEffect effect)) {
            return 0.0F;
        }
        float activeTime = effect.getActiveTime() + partialTicks;
        int duration = instance.getDuration();
        return Math.min(scaleBy, Math.min(activeTime, duration + partialTicks)) / scaleBy;
    }

    public static boolean isInLight(LivingEntity living, int threshold) {
        BlockPos samplePos = living.getRootVehicle().blockPosition();
        int lightLevel = living.level().getBrightness(LightLayer.BLOCK, samplePos);
        float timeOfDay = living.level().getTimeOfDay(1.0F);
        if (living.level().canSeeSky(samplePos) && (timeOfDay < 0.259F || timeOfDay > 0.74F)) {
            lightLevel = 15;
        }
        return lightLevel >= threshold;
    }

    private boolean isCreativePlayer(LivingEntity living) {
        return living instanceof Player player && (player.isCreative() || player.isSpectator());
    }
}
