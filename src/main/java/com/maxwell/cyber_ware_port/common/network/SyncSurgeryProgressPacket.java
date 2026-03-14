package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSurgeryProgressPacket(int progress, int maxProgress) implements CustomPacketPayload {
    public static final Type<SyncSurgeryProgressPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "sync_surgery_progress"));
    public static final StreamCodec<FriendlyByteBuf, SyncSurgeryProgressPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncSurgeryProgressPacket::progress,
            ByteBufCodecs.VAR_INT, SyncSurgeryProgressPacket::maxProgress,
            SyncSurgeryProgressPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPacketHandler.handleProgressPacket(this, ctx));
    }
}