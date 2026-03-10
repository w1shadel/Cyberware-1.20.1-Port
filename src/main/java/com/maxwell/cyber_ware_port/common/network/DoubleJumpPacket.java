package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.cyberware.Leg.LinearActuatorsItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpPacket {
    public DoubleJumpPacket() {
    }

    public static DoubleJumpPacket fromBytes(FriendlyByteBuf buf) {
        return new DoubleJumpPacket();

    }

    public static void handle(DoubleJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    // The packet is sent from the client when the player attempts a double jump.
                    // On the server, we check if the cyberware is active and if the player hasn't
                    // already double-jumped.
                    if (data.isCyberwareActive(ModItems.LINEAR_ACTUATORS.get())
                            && !player.getPersistentData().getBoolean("cyberware_double_jumped")) {
                        LinearActuatorsItem.performDoubleJump(player);
                        player.hurtMarked = true; // Mark player as hurt to reset fall distance
                    }
                });

            }
        });
        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
    }
}