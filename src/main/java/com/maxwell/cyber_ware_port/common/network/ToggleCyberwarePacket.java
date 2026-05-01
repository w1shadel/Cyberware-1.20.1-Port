package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public record ToggleCyberwarePacket(int slotId) implements CustomPacketPayload {
    public static final Type<ToggleCyberwarePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "toggle_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleCyberwarePacket> STREAM_CODEC = StreamCodec.composite(
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
                ItemStacksResourceHandler handler = data.getInstalledCyberware();
                ItemStack stack = handler.getResource(slotId).toStack(handler.getAmountAsInt(slotId));
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (!stack.isEmpty() && cw != null && cw.canToggle(stack)) {
                    if (!cw.isActive(stack)) {
                        for (int i = 0; i < handler.size(); i++) {
                            if (i == slotId) continue;
                            ItemStack other = handler.getResource(i).toStack(handler.getAmountAsInt(i));
                            ICyberware otherCw = CyberwareAPI.getCyberware(other);
                            if (!other.isEmpty() && otherCw != null && otherCw.isActive(other)) {
                                if ((cw.getBodyPartType(stack) != com.maxwell.cyber_ware_port.common.item.base.BodyPartType.NONE && cw.getBodyPartType(stack) == otherCw.getBodyPartType(other))
                                        || cw.isIncompatible(stack, other) || otherCw.isIncompatible(other, stack)) {
                                    player.sendSystemMessage(Component.translatable("cyberware.message.conflict_active").withStyle(ChatFormatting.BLACK));
                                    return;
                                }
                            }
                        }
                    }
                    cw.toggle(stack);
                    handler.set(slotId, ItemResource.of(stack), stack.getCount());
                    data.recalculateCapacity(player);
                    data.syncToClient(player);
                }
            }
        });
    }
}