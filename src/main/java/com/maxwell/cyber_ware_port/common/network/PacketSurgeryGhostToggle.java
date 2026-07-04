package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSurgeryGhostToggle {
    private final BlockPos pos;
    private final int slotId;

    public PacketSurgeryGhostToggle(BlockPos pos, int slotId) {
        this.pos = pos;
        this.slotId = slotId;
    }

    public PacketSurgeryGhostToggle(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.slotId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Level level = player.level();
                if (level.isLoaded(pos)) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof RobosurgeonBlockEntity tile) {
                        handleToggle(tile, player, slotId);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    private void handleToggle(RobosurgeonBlockEntity tile, ServerPlayer player, int slotId) {
        ItemStack currentStack = tile.getItemHandler().getStackInSlot(slotId);
        boolean changed = false;
        if (!currentStack.isEmpty() && currentStack.hasTag() && currentStack.getTag().getBoolean("cyberware_ghost")) {
            tile.getItemHandler().setStackInSlot(slotId, ItemStack.EMPTY);
            changed = true;
        } else if (currentStack.isEmpty()) {
            var cap = player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
            if (cap.isPresent()) {
                ItemStackHandler body = cap.resolve().get().getInstalledCyberware();
                ItemStack installed = body.getStackInSlot(slotId);
                if (!installed.isEmpty()) {
                    ItemStack ghost = installed.copy();
                    ghost.getOrCreateTag().putBoolean("cyberware_ghost", true);
                    tile.getItemHandler().setStackInSlot(slotId, ghost);
                    changed = true;
                    com.maxwell.cyber_ware_port.common.item.base.ICyberware newCw = com.maxwell.cyber_ware_port.api.json.CyberwareAPI
                            .getCyberware(ghost);
                    if (newCw != null) {
                        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                            if (i == slotId)
                                continue;
                            ItemStack other = tile.getItemHandler().getStackInSlot(i);
                            if (!other.isEmpty()) {
                                com.maxwell.cyber_ware_port.common.item.base.ICyberware otherCw = com.maxwell.cyber_ware_port.api.json.CyberwareAPI
                                        .getCyberware(other);
                                if (otherCw != null) {
                                    if (newCw.isIncompatible(ghost, other) || otherCw.isIncompatible(other, ghost)) {
                                        if (!(other.hasTag() && other.getTag().getBoolean("cyberware_ghost"))) {
                                            if (!player.getInventory().add(other.copy())) {
                                                player.drop(other.copy(), false);
                                            }
                                        }
                                        tile.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (changed) {
            tile.setChanged();
            player.level().sendBlockUpdated(tile.getBlockPos(), tile.getBlockState(), tile.getBlockState(), 3);
        }
    }
}