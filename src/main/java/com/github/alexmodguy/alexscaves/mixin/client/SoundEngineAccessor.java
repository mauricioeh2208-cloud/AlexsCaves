package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SoundEngine.class)
public interface SoundEngineAccessor {
    @Accessor("tickingSounds")
    Map<SoundInstance, ChannelAccess.ChannelHandle> getTickingSounds();

    @Accessor("queuedTickableSounds")
    List<SoundInstance> getQueuedTickableSounds();
}
