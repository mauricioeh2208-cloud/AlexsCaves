package com.github.alexmodguy.alexscaves.server.message;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ACNetworking {

    private ACNetworking() {
    }

    public static void init() {
        PayloadTypeRegistry.playS2C().register(UpdateEffectVisualityEntityMessage.ID, UpdateEffectVisualityEntityMessage.CODEC);
    }
}
