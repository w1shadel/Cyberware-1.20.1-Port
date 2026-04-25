package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleCyberwarePacket(int slotId) implements CustomPacketPayload {
    public static final Type<ToggleCyberwarePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "toggle_cyberware"));
    public static final StreamCodec<FriendlyByteBuf, ToggleCyberwarePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ToggleCyberwarePacket::slotId,
            ToggleCyberwarePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                ItemStack stack = data.getInstalledCyberware().getStackInSlot(slotId);
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (!stack.isEmpty() && cw != null && cw.canToggle(stack)) {
                    if (!cw.isActive(stack)) {
                        for (int i = 0; i < data.getInstalledCyberware().getSlots(); i++) {
                            if (i == slotId) continue;
                            ItemStack other = data.getInstalledCyberware().getStackInSlot(i);
                            ICyberware otherCw = CyberwareAPI.getCyberware(other);
                            if (!other.isEmpty() && otherCw != null && otherCw.isActive(other)) {
                                if ((cw.getBodyPartType(stack) != com.maxwell.cyber_ware_port.common.item.base.BodyPartType.NONE && cw.getBodyPartType(stack) == otherCw.getBodyPartType(other))
                                        || cw.isIncompatible(stack, other) || otherCw.isIncompatible(other, stack)) {
                                    player.sendSystemMessage(Component.translatable("cyberware.message.conflict_active").withStyle(ChatFormatting.RED));
                                    return;
                                }
                            }
                        }
                    }
                    cw.toggle(stack);
                    data.recalculateCapacity(player);
                    data.syncToClient(player);
                }
            }
        });
    }
}