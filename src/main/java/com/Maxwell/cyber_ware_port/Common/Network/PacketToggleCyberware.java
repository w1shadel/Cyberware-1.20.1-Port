package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleCyberware {
    private final int slotId;

    public PacketToggleCyberware(int slotId) {
        this.slotId = slotId;
    }

    public PacketToggleCyberware(FriendlyByteBuf buf) {
        this.slotId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slotId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    ItemStack stack = data.getInstalledCyberware().getStackInSlot(slotId);
                    if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {
                        if (cyberware.canToggle(stack)) {
                            cyberware.toggle(stack);

                            data.recalculateCapacity(player);

                            data.syncToClient(player);
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}