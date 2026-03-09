package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.DarknessIncarnateUserAccessor;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDarknessIncarnateMixin implements DarknessIncarnateUserAccessor {
    @Unique
    private boolean alexscaves$slowFallingFlag;

    @Unique
    private boolean alexscaves$hadDarknessIncarnateEffect;

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexscaves$tickDarknessIncarnate(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (alexscaves$slowFallingFlag) {
            alexscaves$slowFallingFlag = false;
            self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0, false, false, false));
        }

        boolean hasDarknessNow = self.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.holder());
        if (alexscaves$hadDarknessIncarnateEffect && !hasDarknessNow && !self.level().isClientSide && self instanceof ServerPlayer player) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.getAbilities().setFlyingSpeed(0.05F);
                player.onUpdateAbilities();
                alexscaves$slowFallingFlag = true;
            }
        }
        alexscaves$hadDarknessIncarnateEffect = hasDarknessNow;
    }

    @Override
    public void setSlowFallingFlag(boolean slowFallingFlag) {
        alexscaves$slowFallingFlag = slowFallingFlag;
    }

    @Override
    public boolean hasSlowFallingFlag() {
        return alexscaves$slowFallingFlag;
    }
}
