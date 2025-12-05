package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCyberwareDataPacket {

    private final CompoundTag data;

    public SyncCyberwareDataPacket(CompoundTag data) {
        this.data = data;
    }

    public CompoundTag getData() {
        return data;
    }

    public static SyncCyberwareDataPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncCyberwareDataPacket(buf.readNbt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.data);
    }

    public static void handle(SyncCyberwareDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSyncPacket(msg));
        });
        ctx.get().setPacketHandled(true);
    }
}