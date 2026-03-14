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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
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

        IItemHandler handler = this.blockEntity.getItemHandler();
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            this.addSlot(new SlotItemHandler(handler, i, -10000, -10000) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    if (!super.mayPlace(stack)) return false;
                    ICyberware myCw = CyberwareAPI.getCyberware(stack);
                    if (myCw == null) return true;

                    for (int j = 0; j < RobosurgeonBlockEntity.TOTAL_SLOTS; j++) {
                        if (j == this.getSlotIndex()) continue;
                        ItemStack other = handler.getStackInSlot(j);
                        if (other.isEmpty() || other.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) continue;
                        ICyberware otherCw = CyberwareAPI.getCyberware(other);
                        if (otherCw == null) continue;
                        if (myCw.isIncompatible(stack, other) || otherCw.isIncompatible(other, stack)) return false;
                    }

                    int currentCount = stack.getCount();
                    for (int j = 0; j < RobosurgeonBlockEntity.TOTAL_SLOTS; j++) {
                        if (j == this.getSlotIndex()) continue;
                        ItemStack other = handler.getStackInSlot(j);
                        if (!other.isEmpty() && other.is(stack.getItem())) {
                            if (other.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) continue;
                            currentCount += other.getCount();
                        }
                    }
                    return currentCount <= myCw.getMaxInstallAmount(stack);
                }
            });
        }

        if (!inv.player.level().isClientSide && inv.player instanceof ServerPlayer serverPlayer) {
            this.blockEntity.populateGhostItems(serverPlayer);
        }
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId >= 0 && slotId < RobosurgeonBlockEntity.TOTAL_SLOTS) {
            Slot slot = this.getSlot(slotId);
            if (slot.hasItem() && slot.getItem().getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                slot.set(ItemStack.EMPTY);
            }
            ItemStack carried = getCarried();
            if (!carried.isEmpty()) {
                ICyberware newCw = CyberwareAPI.getCyberware(carried);
                if (newCw != null) {
                    IItemHandler handler = this.blockEntity.getItemHandler();
                    for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                        ItemStack existing = handler.getStackInSlot(i);
                        if (!existing.isEmpty() && existing.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                            boolean shouldEject = (i == slotId);
                            if (!shouldEject) {
                                ICyberware existingCw = CyberwareAPI.getCyberware(existing);
                                if (existingCw != null && (newCw.isIncompatible(carried, existing) || existingCw.isIncompatible(existing, carried))) {
                                    shouldEject = true;
                                }
                            }
                            if (shouldEject) {
                                this.blockEntity.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }
            this.blockEntity.setChanged();
        }
        super.clicked(slotId, button, clickType, player);
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
                            // Don't allow moving ghost items from the table to inventory
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