package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.SugarRushEffect;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEffectHooksMixin {
    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void alexscaves$onEffectAdded(MobEffectInstance effectInstance, net.minecraft.world.entity.Entity source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (effectInstance.getEffect().value() instanceof DarknessIncarnateEffect) {
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ACSoundRegistry.DARKNESS_INCARNATE_ENTER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (effectInstance.getEffect().value() instanceof SugarRushEffect) {
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ACSoundRegistry.SUGAR_RUSH_ENTER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (self instanceof Player player && AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get()) {
                float timeBetweenTicksIncrease = 2.0F;
                SugarRushEffect.enterSlowMotion(player, player.level(), Mth.ceil(effectInstance.getDuration() * timeBetweenTicksIncrease), timeBetweenTicksIncrease);
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void alexscaves$onEffectRemoved(MobEffectInstance effectInstance, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (effectInstance.getEffect().value() instanceof DarknessIncarnateEffect darknessIncarnateEffect) {
            darknessIncarnateEffect.toggleFlight(self, false);
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ACSoundRegistry.DARKNESS_INCARNATE_EXIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (effectInstance.getEffect().value() instanceof SugarRushEffect) {
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ACSoundRegistry.SUGAR_RUSH_EXIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (self instanceof Player player && AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get()) {
                SugarRushEffect.leaveSlowMotion(player, player.level());
            }
        }
    }
}
