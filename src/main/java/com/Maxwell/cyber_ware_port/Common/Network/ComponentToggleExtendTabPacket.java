package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ComponentToggleExtendTabPacket {
    private final boolean open;

    public ComponentToggleExtendTabPacket(boolean open) {
        this.open = open;

    }

    public static ComponentToggleExtendTabPacket fromBytes(FriendlyByteBuf buf) {
        return new ComponentToggleExtendTabPacket(buf.readBoolean());

    }

    public static void handle(ComponentToggleExtendTabPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                menu.setExtendedOpen(msg.open);

            }
        });
        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(open);

    }
}