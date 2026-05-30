package com.maxwell.cyber_ware_port.common.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class A_PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(IEventBus eventBus) {
        eventBus.addListener(A_PacketHandler::onRegisterPayloads);
    }

    private static void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(SyncCyberwareDataPacket.TYPE, SyncCyberwareDataPacket.STREAM_CODEC, SyncCyberwareDataPacket::handle);
        registrar.playToServer(StartWorkbenchCraftingPacket.TYPE, StartWorkbenchCraftingPacket.STREAM_CODEC, StartWorkbenchCraftingPacket::handle);
        registrar.playToServer(ToggleCyberwarePacket.TYPE, ToggleCyberwarePacket.STREAM_CODEC, ToggleCyberwarePacket::handle);
        registrar.playToServer(SurgeryGhostTogglePacket.TYPE, SurgeryGhostTogglePacket.STREAM_CODEC, SurgeryGhostTogglePacket::handle);
        registrar.playToClient(SyncSurgeryProgressPacket.TYPE, SyncSurgeryProgressPacket.STREAM_CODEC, SyncSurgeryProgressPacket::handle);
        registrar.playToServer(OpenPortableCraftingPacket.TYPE, OpenPortableCraftingPacket.STREAM_CODEC, OpenPortableCraftingPacket::handle);
        registrar.playToServer(DoubleJumpPacket.TYPE, DoubleJumpPacket.STREAM_CODEC, DoubleJumpPacket::handle);
        registrar.playToServer(ComponentChangePagePacket.TYPE, ComponentChangePagePacket.STREAM_CODEC, ComponentChangePagePacket::handle);
        registrar.playToServer(ComponentToggleExtendTabPacket.TYPE, ComponentToggleExtendTabPacket.STREAM_CODEC, ComponentToggleExtendTabPacket::handle);
        registrar.playToClient(SyncWorkbenchRecipePacket.TYPE, SyncWorkbenchRecipePacket.STREAM_CODEC, ClientPacketHandler::handleSyncWorkbenchRecipe);
    }
}