package com.maxwell.cyber_ware_port.common.item.base;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.recalculateCapacity(serverPlayer);
            });
        }
    }
}