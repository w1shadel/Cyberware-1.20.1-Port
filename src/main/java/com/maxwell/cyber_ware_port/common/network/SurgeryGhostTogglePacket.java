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
                    ItemStack currentStack = itemHandler.getResource(slotId).toStack(itemHandler.getAmountAsInt(slotId));
                    boolean changed = false;
                    if (!currentStack.isEmpty() && currentStack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                        itemHandler.set(slotId, ItemResource.EMPTY, 0);
                        changed = true;
                    } else if (currentStack.isEmpty()) {
                        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                        ItemStacksResourceHandler body = data.getInstalledCyberware();
                        ItemStack installed = body.getResource(slotId).toStack(body.getAmountAsInt(slotId));
                        if (!installed.isEmpty()) {
                            ItemStack ghost = installed.copy();
                            ghost.set(CyberWare.GHOST_COMPONENT.get(), true);
                            itemHandler.set(slotId, ItemResource.of(ghost), ghost.getCount());
                            changed = true;
                        }
                    }
                    if (changed) {
                        tile.setChanged();
                        player.level().sendBlockUpdated(pos, tile.getBlockState(), tile.getBlockState(), 3);
                    }
                }
            }
        });
    }
}