package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.client.screen.CyberwareWorkbenchScreen;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
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

    public static void handleSyncPacket(SyncCyberwareDataPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            if (level.getEntity(msg.entityId()) instanceof Player player) {
                CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                data.deserializeNBT(player.registryAccess(), msg.data());
            }
        });
    }
    public static void handleSyncWorkbenchRecipe(final SyncWorkbenchRecipePacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                menu.setSyncedRecipeData(payload.ingredients(), payload.deconstructChance());
            }
        });
    }
    public static void handleProgressPacket(SyncSurgeryProgressPacket msg, IPayloadContext ctx) {
        update(msg.progress(), msg.maxProgress());
    }
}