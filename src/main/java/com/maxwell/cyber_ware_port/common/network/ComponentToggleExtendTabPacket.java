package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ComponentToggleExtendTabPacket(boolean open) implements CustomPacketPayload {
    public static final Type<ComponentToggleExtendTabPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "component_toggle_tab"));

    public static final StreamCodec<FriendlyByteBuf, ComponentToggleExtendTabPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ComponentToggleExtendTabPacket::open,
            ComponentToggleExtendTabPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && player.containerMenu instanceof CyberwareWorkbenchMenu menu) {
                menu.setExtendedOpen(open);
            }
        });
    }
}