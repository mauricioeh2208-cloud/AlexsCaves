package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelTickRateMixin {
    @Shadow
    @Final
    public boolean isClientSide;

    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void alexscaves$guardEntityTick(Consumer<T> ticker, T entity, CallbackInfo ci) {
        if (!isClientSide && (Object) this instanceof ServerLevel serverLevel) {
            if (ServerTickRateTracker.getForServer(serverLevel.getServer()).shouldSkipEntityTick(entity)) {
                ci.cancel();
            }
        }
    }
}
