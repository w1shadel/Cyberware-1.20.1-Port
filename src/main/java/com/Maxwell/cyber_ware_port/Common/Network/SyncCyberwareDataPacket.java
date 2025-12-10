package com.Maxwell.cyber_ware_port.Common.Network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncCyberwareDataPacket(CompoundTag data) {

    public static SyncCyberwareDataPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncCyberwareDataPacket(buf.readNbt());

    }

    public static void handle(SyncCyberwareDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSyncPacket(msg));

        });
        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.data);

    }
}