package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ComponentChangePagePacket(int direction, int targetPanel) implements CustomPacketPayload {
    public static final Type<ComponentChangePagePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "component_change_page"));
    public static final StreamCodec<FriendlyByteBuf, ComponentChangePagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ComponentChangePagePacket::direction,
            ByteBufCodecs.VAR_INT, ComponentChangePagePacket::targetPanel,
            ComponentChangePagePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                if (targetPanel == 0) {
                    menu.changePage(direction);
                } else if (targetPanel == 1) {
                    menu.changeBlueprintPage(direction);
                }
            }
        });
    }
}