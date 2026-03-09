package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.SugarRushEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerSugarRushMixin {
    @Inject(method = "changeDimension", at = @At("HEAD"))
    private void alexscaves$leaveSugarRushSlowMotion(DimensionTransition dimensionTransition, CallbackInfoReturnable<net.minecraft.world.entity.Entity> cir) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (self.hasEffect(ACEffectRegistry.SUGAR_RUSH.holder())) {
            SugarRushEffect.leaveSlowMotion(self, self.level());
        }
    }
}
