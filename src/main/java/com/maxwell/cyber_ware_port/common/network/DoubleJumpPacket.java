package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.cyberware.leg.LinearActuatorsItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DoubleJumpPacket() implements CustomPacketPayload {
    public static final Type<DoubleJumpPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "double_jump"));
    public static final StreamCodec<FriendlyByteBuf, DoubleJumpPacket> STREAM_CODEC = StreamCodec.unit(new DoubleJumpPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())
                        && !player.getPersistentData().getBooleanOr("cyberware_double_jumped", false)) {
                    LinearActuatorsItem.performDoubleJump(player);
                    player.hurtMarked = true;
                }
            }
        });
    }
}