package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCyberwareDataPacket(CompoundTag data) implements CustomPacketPayload {
    public static final Type<SyncCyberwareDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "sync_cyberware_data"));
    public static final StreamCodec<FriendlyByteBuf, SyncCyberwareDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SyncCyberwareDataPacket::data,
            SyncCyberwareDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPacketHandler.handleSyncPacket(this, ctx));
    }
}