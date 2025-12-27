package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("removal")
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CyberWare.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    private static int id() {
        return id++;
    }

    public static void register() {
        INSTANCE.messageBuilder(SyncCyberwareDataPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncCyberwareDataPacket::fromBytes)
                .encoder(SyncCyberwareDataPacket::toBytes)
                .consumerMainThread(SyncCyberwareDataPacket::handle)
                .add();
        INSTANCE.messageBuilder(StartWorkbenchCraftingPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StartWorkbenchCraftingPacket::new)
                .encoder(StartWorkbenchCraftingPacket::toBytes)
                .consumerMainThread(StartWorkbenchCraftingPacket::handle)
                .add();
        INSTANCE.messageBuilder(PacketToggleCyberware.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketToggleCyberware::new)
                .encoder(PacketToggleCyberware::toBytes)
                .consumerMainThread(PacketToggleCyberware::handle)
                .add();
        INSTANCE.messageBuilder(PacketSurgeryGhostToggle.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketSurgeryGhostToggle::new)
                .encoder(PacketSurgeryGhostToggle::toBytes)
                .consumerMainThread(PacketSurgeryGhostToggle::handle)
                .add();
    }
}