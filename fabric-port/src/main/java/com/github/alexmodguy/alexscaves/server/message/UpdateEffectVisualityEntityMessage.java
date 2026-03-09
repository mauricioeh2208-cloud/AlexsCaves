package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateEffectVisualityEntityMessage(
    int entityID,
    int fromEntityID,
    int potionType,
    int duration,
    boolean remove
) implements CustomPacketPayload {
    public static final Type<UpdateEffectVisualityEntityMessage> ID =
        new Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_effect_visuality"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateEffectVisualityEntityMessage> CODEC = new StreamCodec<>() {
        @Override
        public UpdateEffectVisualityEntityMessage decode(RegistryFriendlyByteBuf buf) {
            return new UpdateEffectVisualityEntityMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UpdateEffectVisualityEntityMessage packet) {
            buf.writeInt(packet.entityID);
            buf.writeInt(packet.fromEntityID);
            buf.writeInt(packet.potionType);
            buf.writeInt(packet.duration);
            buf.writeBoolean(packet.remove);
        }
    };

    public UpdateEffectVisualityEntityMessage(int entityID, int fromEntityID, int potionType, int duration) {
        this(entityID, fromEntityID, potionType, duration, false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
