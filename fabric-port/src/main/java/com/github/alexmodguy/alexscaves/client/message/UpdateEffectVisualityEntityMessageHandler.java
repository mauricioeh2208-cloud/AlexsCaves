package com.github.alexmodguy.alexscaves.client.message;

import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.IrradiatedEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class UpdateEffectVisualityEntityMessageHandler {
    private UpdateEffectVisualityEntityMessageHandler() {
    }

    public static void handle(UpdateEffectVisualityEntityMessage message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            Player player = context.player();
            if (player == null) {
                return;
            }

            Entity entity = player.level().getEntity(message.entityID());
            Entity senderEntity = player.level().getEntity(message.fromEntityID());
            if (!(entity instanceof LivingEntity living) || senderEntity == null || senderEntity.distanceTo(living) >= 32.0F) {
                return;
            }

            Holder<MobEffect> mobEffect;
            int amplifier = 0;
            switch (message.potionType()) {
                case 0 -> mobEffect = ACEffectRegistry.IRRADIATED.holder();
                case 1 -> {
                    mobEffect = ACEffectRegistry.BUBBLED.holder();
                    entity.playSound(ACSoundRegistry.SEA_STAFF_BUBBLE.get());
                }
                case 2 -> mobEffect = ACEffectRegistry.MAGNETIZING.holder();
                case 3 -> mobEffect = ACEffectRegistry.STUNNED.holder();
                case 4 -> {
                    mobEffect = ACEffectRegistry.IRRADIATED.holder();
                    amplifier = IrradiatedEffect.BLUE_LEVEL;
                }
                default -> {
                    return;
                }
            }

            if (message.remove()) {
                living.removeEffect(mobEffect);
            } else {
                living.addEffect(new MobEffectInstance(mobEffect, message.duration(), amplifier));
            }
        });
    }
}
