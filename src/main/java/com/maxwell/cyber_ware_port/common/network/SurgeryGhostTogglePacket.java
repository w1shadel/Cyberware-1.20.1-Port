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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SurgeryGhostTogglePacket(BlockPos pos, int slotId) implements CustomPacketPayload {
    public static final Type<SurgeryGhostTogglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "surgery_ghost_toggle"));

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
                    ItemStackHandler itemHandler = tile.getItemHandler();
                    ItemStack currentStack = itemHandler.getStackInSlot(slotId);
                    boolean changed = false;

                    if (!currentStack.isEmpty() && currentStack.getOrDefault(CyberWare.GHOST_COMPONENT, false)) {
                        itemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
                        changed = true;
                    } else if (currentStack.isEmpty()) {
                        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                        ItemStackHandler body = data.getInstalledCyberware();
                        ItemStack installed = body.getStackInSlot(slotId);
                        if (!installed.isEmpty()) {
                            ItemStack ghost = installed.copy();
                            ghost.set(CyberWare.GHOST_COMPONENT, true);
                            itemHandler.setStackInSlot(slotId, ghost);
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