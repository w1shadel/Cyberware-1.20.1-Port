package com.Maxwell.cyber_ware_port.Common.Capability;

import com.Maxwell.cyber_ware_port.Config.CyberwareConfig;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(CyberWare.MODID, "cyberware_data"), new CyberwareCapabilityProvider());
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncData(serverPlayer);
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cap -> {
                if (!cap.isInitialized()) {
                    cap.fillWithHumanParts();
                }
                cap.syncToClient(serverPlayer);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncData(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.recalculateCapacity(serverPlayer);
                data.syncToClient(serverPlayer);
                syncData(serverPlayer);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (event.isWasDeath()) {

            if (CyberwareConfig.KEEP_CYBERWARE_ON_DEATH.get()) {
                original.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                    newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                        newData.copyFrom(oldData);
                    });
                });
            }

            else {

                DamageSource source = original.getLastDamageSource();
                String msgId = (source != null) ? source.getMsgId() : "";

                newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {if (msgId.equals("cyberware_organ_failure")) {
                        newData.fillWithHumanParts();
                    } else {newData.setInitialized();
                    }
                });
            }
        } else {

            original.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                });
            });
        }

        if (newPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.recalculateCapacity(serverPlayer);
                data.syncToClient(serverPlayer);
            });
        }
    }
    private static void syncData(ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            data.syncToClient(player);
        });
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (event.player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                    userData.tick(serverPlayer);
                    if (serverPlayer.tickCount % 20 == 0) {
                        userData.syncToClient(serverPlayer);
                    }
                });
            }
        }
    }
}