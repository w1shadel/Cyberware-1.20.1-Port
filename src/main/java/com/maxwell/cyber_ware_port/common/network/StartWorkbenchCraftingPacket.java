package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartWorkbenchCraftingPacket {

    public StartWorkbenchCraftingPacket() {
    }

    public StartWorkbenchCraftingPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                menu.blockEntity.startCrafting();

            }
        });
        return true;

    }
}