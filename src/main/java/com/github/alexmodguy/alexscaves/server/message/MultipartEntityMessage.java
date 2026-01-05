package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MultipartEntityMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MultipartEntityMessage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "multipart_entity"));

    public static final StreamCodec<FriendlyByteBuf, MultipartEntityMessage> CODEC =
        StreamCodec.ofMember(MultipartEntityMessage::write, MultipartEntityMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public int parentId;
    public int playerId;
    public int type;
    public double damage;

    public MultipartEntityMessage(int parentId, int playerId, int type, double damage) {
        this.parentId = parentId;
        this.playerId = playerId;
        this.type = type;
        this.damage = damage;
    }


    public MultipartEntityMessage() {
    }

    public static MultipartEntityMessage read(FriendlyByteBuf buf) {
        return new MultipartEntityMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readDouble());
    }

    public static void write(MultipartEntityMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.parentId);
        buf.writeInt(message.playerId);
        buf.writeInt(message.type);
        buf.writeDouble(message.damage);
    }

    public static void handle(MultipartEntityMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (context.flow().isClientbound() == context.flow().isClientbound()) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            Entity parent = playerSided.level().getEntity(message.parentId);
            Entity interacter = playerSided.level().getEntity(message.playerId);
            if (interacter != null && parent != null && parent.isMultipartEntity() && interacter.distanceTo(parent) < 16) {
                if (message.type == 0) {
                    if (interacter instanceof Player player) {
                        parent.interact(player, player.getUsedItemHand());
                    }
                } else if (message.type == 1) {
                    parent.hurt(parent.damageSources().generic(), (float) message.damage);
                }
            }
        });
        // Packet handling is automatic in NeoForge;
    }
}
