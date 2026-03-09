package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.IrradiatedEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionContents.class)
public abstract class PotionContentsColorMixin {

    @Inject(method = "getColor(Ljava/lang/Iterable;)I", at = @At("HEAD"), cancellable = true)
    private static void alexscaves$getColor(Iterable<MobEffectInstance> effects, CallbackInfoReturnable<Integer> cir) {
        for (MobEffectInstance mobEffectInstance : effects) {
            if (mobEffectInstance.is(ACEffectRegistry.IRRADIATED.holder()) && mobEffectInstance.getAmplifier() >= IrradiatedEffect.BLUE_LEVEL) {
                cir.setReturnValue(0x00FFFF);
                return;
            }
        }
    }
}
