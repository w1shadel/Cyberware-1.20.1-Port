package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public record SurgeryGhostTogglePacket(BlockPos pos, int slotId) implements CustomPacketPayload {
    public static final Type<SurgeryGhostTogglePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "surgery_ghost_toggle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SurgeryGhostTogglePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SurgeryGhostTogglePacket::pos,
            ByteBufCodecs.VAR_INT, SurgeryGhostTogglePacket::slotId,
            SurgeryGhostTogglePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && player.level().isLoaded(pos)) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof RobosurgeonBlockEntity tile) {
                    ItemStacksResourceHandler itemHandler = tile.getItemHandler();
                    ItemStack stack = itemHandler.getResource(slotId).toStack(itemHandler.getAmountAsInt(slotId));

                    if (!stack.isEmpty() && stack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                        boolean isRemoving = stack.getOrDefault(CyberWare.REMOVAL_COMPONENT.get(), false);
                        stack.set(CyberWare.REMOVAL_COMPONENT.get(), !isRemoving);

                        itemHandler.set(slotId, ItemResource.of(stack), stack.getCount());
                        tile.setChanged();
                        player.level().sendBlockUpdated(pos, tile.getBlockState(), tile.getBlockState(), 3);
                    }
                }
            }
        });
    }
}