package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class BeholderSyncMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeholderSyncMessage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_sync"));

    public static final StreamCodec<FriendlyByteBuf, BeholderSyncMessage> CODEC =
        StreamCodec.ofMember(BeholderSyncMessage::write, BeholderSyncMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public int beholderId;
    public boolean active;
    // Entity spawn data for remote creation
    public double x, y, z;
    public float yRot, xRot;
    public UUID usingPlayerUUID;

    public BeholderSyncMessage(int beholderId, boolean active, double x, double y, double z, float yRot, float xRot, UUID usingPlayerUUID) {
        this.beholderId = beholderId;
        this.active = active;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.usingPlayerUUID = usingPlayerUUID;
    }

    public BeholderSyncMessage(int beholderId, boolean active) {
        this(beholderId, active, 0, 0, 0, 0, 0, null);
    }

    public BeholderSyncMessage() {
    }

    public static BeholderSyncMessage read(FriendlyByteBuf buf) {
        int beholderId = buf.readInt();
        boolean active = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yRot = buf.readFloat();
        float xRot = buf.readFloat();
        UUID uuid = buf.readBoolean() ? buf.readUUID() : null;
        return new BeholderSyncMessage(beholderId, active, x, y, z, yRot, xRot, uuid);
    }

    public static void write(BeholderSyncMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.beholderId);
        buf.writeBoolean(message.active);
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
        buf.writeFloat(message.yRot);
        buf.writeFloat(message.xRot);
        buf.writeBoolean(message.usingPlayerUUID != null);
        if (message.usingPlayerUUID != null) {
            buf.writeUUID(message.usingPlayerUUID);
        }
    }

    public static void handle(BeholderSyncMessage message, IPayloadContext context) {
        // This packet is sent from server to client
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player playerSided = AlexsCaves.PROXY.getClientSidePlayer();
                if (playerSided != null && playerSided.level() instanceof ClientLevel clientLevel) {
                    Entity watcher = clientLevel.getEntity(message.beholderId);
                    // If entity doesn't exist on client and we have spawn data, create it
                    // This is necessary when viewing a Beholder from far away (unloaded chunks)
                    if (watcher == null && message.active && message.usingPlayerUUID != null) {
                        BeholderEyeEntity beholderEye = ACEntityRegistry.BEHOLDER_EYE.get().create(clientLevel);
                        if (beholderEye != null) {
                            beholderEye.setId(message.beholderId);
                            beholderEye.setPos(message.x, message.y, message.z);
                            beholderEye.setEyeYRot(message.yRot);
                            beholderEye.setEyeXRot(message.xRot);
                            beholderEye.setUsingPlayerUUID(message.usingPlayerUUID);
                            beholderEye.hasTakenFullControlOfCamera = true;
                            clientLevel.addEntity(beholderEye);
                            watcher = beholderEye;
                        }
                    }
                    if (watcher instanceof BeholderEyeEntity beholderEye) {
                        Entity beholderEyePlayer = beholderEye.getUsingPlayer();
                        beholderEye.hasTakenFullControlOfCamera = true;
                        if (beholderEyePlayer != null && beholderEyePlayer instanceof Player && beholderEyePlayer.equals(playerSided)) {
                            if (message.active) {
                                AlexsCaves.PROXY.setRenderViewEntity(playerSided, beholderEye);
                            } else {
                                AlexsCaves.PROXY.resetRenderViewEntity(playerSided);
                            }
                        }
                    }
                }
            });
        }
    }
}
