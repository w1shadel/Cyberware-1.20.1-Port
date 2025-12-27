package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static int currentProgress = 0;

    public static int maxProgress = 100;

    public static void update(int progress, int max) {
        currentProgress = progress;
        maxProgress = max;

    }

    public static void handleSyncPacket(SyncCyberwareDataPacket msg) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cyberware -> {
                cyberware.deserializeNBT(msg.data());

            });

        }
    }
}