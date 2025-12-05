package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleSyncPacket(SyncCyberwareDataPacket msg) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cyberware -> {

                cyberware.deserializeNBT(msg.getData());
            });
        }
    }
}