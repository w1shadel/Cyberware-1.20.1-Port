package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StartWorkbenchCraftingPacket() implements CustomPacketPayload {
    public static final Type<StartWorkbenchCraftingPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "start_workbench_crafting"));
    public static final StreamCodec<FriendlyByteBuf, StartWorkbenchCraftingPacket> STREAM_CODEC = StreamCodec.unit(new StartWorkbenchCraftingPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                menu.blockEntity.startCrafting();
            }
        });
    }
}