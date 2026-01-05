package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("move")
    void invokeMove(double distanceOffset, double verticalOffset, double horizontalOffset);

    @Invoker("getMaxZoom")
    double invokeGetMaxZoom(double desiredZoom);
}
