package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

public class RobosurgeonMenu extends AbstractContainerMenu {
    public final RobosurgeonBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final int invHeight = 140;

    public RobosurgeonMenu(int pContainerId, Inventory inv, RegistryFriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public RobosurgeonMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ROBO_SURGEON_MENU.get(), pContainerId);
        this.blockEntity = (RobosurgeonBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        addDataSlots(data);
        ItemStacksResourceHandler handler = this.blockEntity.getItemHandler();
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            this.addSlot(new ResourceHandlerSlot(handler, handler::set, i, -10000, -10000) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    if (stack.isEmpty()) return true;
                    if (!super.mayPlace(stack)) return false;

                    ICyberware myCw = CyberwareAPI.getCyberware(stack);
                    if (myCw == null) return false;
                    for (int j = 0; j < RobosurgeonBlockEntity.TOTAL_SLOTS; j++) {
                        if (j == this.getSlotIndex()) continue;
                        ItemStack other = handler.getResource(j).toStack(handler.getAmountAsInt(j));
                        if (other.isEmpty() || other.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) continue;

                        ICyberware otherCw = CyberwareAPI.getCyberware(other);
                        if (otherCw != null) {
                            if (myCw.isIncompatible(stack, other) || otherCw.isIncompatible(other, stack)) return false;
                        }
                    }
                    int currentCount = stack.getCount();
                    for (int j = 0; j < RobosurgeonBlockEntity.TOTAL_SLOTS; j++) {
                        if (j == this.getSlotIndex()) continue;
                        ItemStack other = handler.getResource(j).toStack(handler.getAmountAsInt(j));
                        if (!other.isEmpty() && other.is(stack.getItem())) {
                            if (other.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) continue;
                            currentCount += other.getCount();
                        }
                    }
                    return currentCount <= myCw.getMaxInstallAmount(stack);
                }
                @Override
                public boolean mayPickup(Player playerIn) {
                    ItemStack stack = this.getItem();
                    return !stack.isEmpty() && !stack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false);
                }
            });
        }
        if (!inv.player.level().isClientSide() && inv.player instanceof ServerPlayer serverPlayer) {
            this.blockEntity.populateGhostItems(serverPlayer);
        }
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public void clicked(int slotId, int buttonNum, ContainerInput containerInput, Player player) {
        if (slotId >= 0 && slotId < RobosurgeonBlockEntity.TOTAL_SLOTS) {
            Slot slot = this.getSlot(slotId);
            ItemStack stackInSlot = slot.getItem();
            boolean isGhost = stackInSlot.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false);
            ItemStack carried = getCarried();

            if (isGhost) {
                slot.set(ItemStack.EMPTY);
                if (carried.isEmpty()) {
                    this.blockEntity.setChanged();
                    return; // 拾わせない
                }
            }
        }
        super.clicked(slotId, buttonNum, containerInput, player);

        if (slotId >= 0 && slotId < RobosurgeonBlockEntity.TOTAL_SLOTS) {
            this.blockEntity.setChanged();
        }
    }
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, invHeight + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, invHeight + 58));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(levelAccess, pPlayer, ModBlocks.ROBO_SURGEON.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                if (!this.moveItemStackTo(itemstack1, RobosurgeonBlockEntity.TOTAL_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                ICyberware cw = CyberwareAPI.getCyberware(itemstack1);
                if (cw != null) {
                    int slotType = cw.getSlot(itemstack1);
                    boolean moved = false;
                    for (int pass = 0; pass < 2; pass++) {
                        for (int i = slotType; i < slotType + RobosurgeonBlockEntity.SLOTS_PER_PART; i++) {
                            Slot targetSlot = this.slots.get(i);
                            if (targetSlot.hasItem() && targetSlot.getItem().getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                                targetSlot.set(ItemStack.EMPTY);
                            }
                            if (index < RobosurgeonBlockEntity.TOTAL_SLOTS && itemstack1.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                                return ItemStack.EMPTY;
                            }
                            if (targetSlot.mayPlace(itemstack1) && this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                                moved = true;
                                break;
                            }
                        }
                        if (moved) break;
                    }
                    if (!moved) return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}