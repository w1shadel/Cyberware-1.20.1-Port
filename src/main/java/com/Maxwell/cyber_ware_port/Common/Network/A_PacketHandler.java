package com.Maxwell.cyber_ware_port.Common.Network;

import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
@SuppressWarnings("removal")
public class A_PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;
    private static int id() {
        return id++;
    }
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CyberWare.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

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
        INSTANCE.messageBuilder(ToggleCyberwarePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleCyberwarePacket::new)
                .encoder(ToggleCyberwarePacket::toBytes)
                .consumerMainThread(ToggleCyberwarePacket::handle)
                .add();
        INSTANCE.messageBuilder(SurgeryGhostTogglePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SurgeryGhostTogglePacket::new)
                .encoder(SurgeryGhostTogglePacket::toBytes)
                .consumerMainThread(SurgeryGhostTogglePacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncSurgeryProgressPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSurgeryProgressPacket::fromBytes)
                .encoder(SyncSurgeryProgressPacket::toBytes)
                .consumerMainThread(SyncSurgeryProgressPacket::handle)
                .add();
        INSTANCE.messageBuilder(OpenPortableCraftingPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenPortableCraftingPacket::fromBytes)
                .encoder(OpenPortableCraftingPacket::toBytes)
                .consumerMainThread(OpenPortableCraftingPacket::handle)
                .add();
        INSTANCE.messageBuilder(DoubleJumpPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(DoubleJumpPacket::fromBytes)
                .encoder(DoubleJumpPacket::toBytes)
                .consumerMainThread(DoubleJumpPacket::handle)
                .add();
        INSTANCE.messageBuilder(ComponentChangePagePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ComponentChangePagePacket::fromBytes)
                .encoder(ComponentChangePagePacket::toBytes)
                .consumerMainThread(ComponentChangePagePacket::handle)
                .add();
        INSTANCE.messageBuilder(ComponentToggleExtendTabPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ComponentToggleExtendTabPacket::fromBytes)
                .encoder(ComponentToggleExtendTabPacket::toBytes)
                .consumerMainThread(ComponentToggleExtendTabPacket::handle)
                .add();
    }
}