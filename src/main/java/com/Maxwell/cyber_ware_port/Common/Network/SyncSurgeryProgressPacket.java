package com.Maxwell.cyber_ware_port.Common.Network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSurgeryProgressPacket {
    private final int progress;
    private final int maxProgress;

    public SyncSurgeryProgressPacket(int progress, int maxProgress) {
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public static SyncSurgeryProgressPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncSurgeryProgressPacket(buf.readInt(), buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.progress);
        buf.writeInt(this.maxProgress);
    }

    public static void handle(SyncSurgeryProgressPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.update(msg.progress,msg.maxProgress));
        });
        ctx.get().setPacketHandled(true);
    }
}