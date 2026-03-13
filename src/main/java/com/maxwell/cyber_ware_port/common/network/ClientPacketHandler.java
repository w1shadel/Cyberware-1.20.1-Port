package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPacketHandler {
    public static int currentProgress = 0;
    public static int maxProgress = 100;

    public static void update(int progress, int max) {
        currentProgress = progress;
        maxProgress = max;
    }

    public static void reset() {
        currentProgress = 0;
        maxProgress = 100;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleSyncPacket(SyncCyberwareDataPacket msg, IPayloadContext ctx) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            CyberwareUserData cyberware = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            cyberware.deserializeNBT(player.registryAccess(), msg.data());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleProgressPacket(SyncSurgeryProgressPacket msg, IPayloadContext ctx) {
        update(msg.progress(), msg.maxProgress());
    }
}