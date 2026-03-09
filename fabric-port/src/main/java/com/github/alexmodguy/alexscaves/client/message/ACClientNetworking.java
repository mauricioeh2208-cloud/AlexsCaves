package com.github.alexmodguy.alexscaves.client.message;

import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ACClientNetworking {
    private ACClientNetworking() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateEffectVisualityEntityMessage.ID, UpdateEffectVisualityEntityMessageHandler::handle);
    }
}
