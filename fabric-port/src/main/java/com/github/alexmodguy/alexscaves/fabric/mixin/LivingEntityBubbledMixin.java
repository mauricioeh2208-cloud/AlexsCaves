package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityBubbledMixin {
    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void alexscaves$increaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ACEffectRegistry.BUBBLED.holder())) {
            cir.setReturnValue(air);
        }
    }
}
