package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleCyberwarePacket {
    private final int slotId;

    public ToggleCyberwarePacket(int slotId) {
        this.slotId = slotId;

    }

    public ToggleCyberwarePacket(FriendlyByteBuf buf) {
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
                    ICyberware cw = CyberwareAPI.getCyberware(stack);
                    if (!stack.isEmpty() && cw != null) {
                        if (cw.canToggle(stack)) {

                            if (!cw.isActive(stack)) { 
                                for (int i = 0; i < data.getInstalledCyberware().getSlots(); i++) {
                                    if (i == slotId)
                                        continue;
                                    ItemStack other = data.getInstalledCyberware().getStackInSlot(i);
                                    ICyberware otherCw = CyberwareAPI.getCyberware(other);
                                    if (!other.isEmpty() && otherCw != null && otherCw.isActive(other)) {
                                        boolean conflict = false;
                                        if (cw.getBodyPartType(
                                                stack) != com.maxwell.cyber_ware_port.common.item.base.BodyPartType.NONE
                                                && cw.getBodyPartType(stack) == otherCw.getBodyPartType(other)) {
                                            conflict = true;
                                        }
                                        if (!conflict && (cw.isIncompatible(stack, other)
                                                || otherCw.isIncompatible(other, stack))) {
                                            conflict = true;
                                        }
                                        if (conflict) {

                                            player.sendSystemMessage(net.minecraft.network.chat.Component
                                                    .translatable("cyberware.message.conflict_active")
                                                    .withStyle(net.minecraft.ChatFormatting.RED));
                                            return;
                                        }
                                    }
                                }
                            }
                            cw.toggle(stack);
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