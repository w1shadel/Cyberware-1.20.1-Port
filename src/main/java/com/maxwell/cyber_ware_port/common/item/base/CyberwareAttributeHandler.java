package com.maxwell.cyber_ware_port.common.item.base;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CyberWare.MODID)
public class CyberwareAttributeHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        updateAttributes(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        updateAttributes(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        updateAttributes(event.getEntity());
    }

    private static void updateAttributes(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            CyberwareUserData data = serverPlayer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            data.recalculateCapacity(serverPlayer);
        }
    }
}