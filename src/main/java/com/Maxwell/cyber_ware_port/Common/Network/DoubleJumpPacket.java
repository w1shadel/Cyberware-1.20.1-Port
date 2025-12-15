package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.LinearActuatorsItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
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
                    if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())
                            && !player.getPersistentData().getBoolean("cyberware_double_jumped")) {
                        LinearActuatorsItem.performDoubleJump(player);
                        player.hurtMarked = true;

                    }
                });

            }
        });
        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
    }
}