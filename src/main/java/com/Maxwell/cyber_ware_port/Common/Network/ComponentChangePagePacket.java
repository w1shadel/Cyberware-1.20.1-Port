package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ComponentChangePagePacket {
    private final int direction; 
    private final int targetPanel; 

    public ComponentChangePagePacket(int direction, int targetPanel) {
        this.direction = direction;
        this.targetPanel = targetPanel;
    }

    public static ComponentChangePagePacket fromBytes(FriendlyByteBuf buf) {
        int direction = buf.readInt();
        int targetPanel = buf.readInt();
        return new ComponentChangePagePacket(direction, targetPanel);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(direction);
        buf.writeInt(targetPanel);
    }

    public static void handle(ComponentChangePagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {

                switch (msg.targetPanel) {
                    case 0: 
                        menu.changePage(msg.direction);
                        break;
                    case 1: 
                        menu.changeBlueprintPage(msg.direction);
                        break;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}