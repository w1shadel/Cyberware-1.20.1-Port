package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class RobosurgeonMenu extends AbstractContainerMenu {
    public final RobosurgeonBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final int invheight = 140;

    public RobosurgeonMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2));
    }

    public RobosurgeonMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ROBO_SURGEON_MENU.get(), pContainerId);
        this.blockEntity = (RobosurgeonBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        addDataSlots(data);
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                this.addSlot(new SlotItemHandler(handler, i, -10000, -10000) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if (!super.mayPlace(stack))
                            return false;
                        ICyberware myCw = CyberwareAPI.getCyberware(stack);
                        if (myCw == null)
                            return true;
                        for (int otherSlotIndex = 0; otherSlotIndex < RobosurgeonBlockEntity.TOTAL_SLOTS; otherSlotIndex++) {
                            if (otherSlotIndex == this.getSlotIndex())
                                continue;

                            ItemStack otherStackInside = handler.getStackInSlot(otherSlotIndex);
                            if (otherStackInside.isEmpty())
                                continue;

                            // If the other stack is a ghost, we essentially ignore its presence for
                            // compatibility checks
                            // because it will be removed/replaced when the new item is placed.
                            if (otherStackInside.hasTag() && otherStackInside.getTag() != null
                                    && otherStackInside.getTag().getBoolean("cyberware_ghost")) {
                                continue;
                            }

                            ICyberware otherCw = CyberwareAPI.getCyberware(otherStackInside);
                            if (otherCw == null)
                                continue;

                            if (myCw.isIncompatible(stack, otherStackInside)
                                    || otherCw.isIncompatible(otherStackInside, stack)) {
                                return false;
                            }
                        }

                        // Check max install amount
                        int currentCount = stack.getCount();
                        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                            if (i == this.getSlotIndex())
                                continue;
                            ItemStack otherStack = handler.getStackInSlot(i);
                            if (!otherStack.isEmpty() && otherStack.getItem() == stack.getItem()) {
                                // Skip ghost items in count - they will be replaced/handled
                                if (otherStack.hasTag() && otherStack.getTag() != null
                                        && otherStack.getTag().getBoolean("cyberware_ghost")) {
                                    continue;
                                }
                                currentCount += otherStack.getCount();
                            }
                        }
                        if (currentCount > myCw.getMaxInstallAmount(stack)) {
                            return false;
                        }

                        return true;
                    }
                });

            }
        });
        if (!inv.player.level().isClientSide && inv.player instanceof ServerPlayer serverPlayer) {
            this.blockEntity.populateGhostItems(serverPlayer);

        }
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < RobosurgeonBlockEntity.TOTAL_SLOTS) {
            Slot slot = this.getSlot(slotId);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
                    slot.set(ItemStack.EMPTY);
                }
            }
            // Auto-eject conflicting ghosts when placing a new item
            ItemStack carried = getCarried();
            if (!carried.isEmpty()) {
                ICyberware newCw = CyberwareAPI.getCyberware(carried);
                if (newCw != null) {
                    this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                        if (handler instanceof net.minecraftforge.items.IItemHandlerModifiable modifiable) {
                            for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                                ItemStack existing = modifiable.getStackInSlot(i);
                                if (!existing.isEmpty() && existing.hasTag() && existing.getTag() != null
                                        && existing.getTag().getBoolean("cyberware_ghost")) {

                                    boolean shouldEject = false;
                                    if (i == slotId) {
                                        // Always eject ghost in the same slot being occupied
                                        shouldEject = true;
                                    } else {
                                        // Only eject other ghosts if they are explicitly incompatible
                                        ICyberware existingCw = CyberwareAPI.getCyberware(existing);
                                        if (existingCw != null) {
                                            if (newCw.isIncompatible(carried, existing)
                                                    || existingCw.isIncompatible(existing, carried)) {
                                                shouldEject = true;
                                            }
                                        }
                                    }

                                    if (shouldEject) {
                                        modifiable.setStackInSlot(i, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    });
                }
            }
            this.blockEntity.setChanged();
        }
        super.clicked(slotId, button, clickType, player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int startY = invheight;
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, startY + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        int startY = invheight + 58;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, startY));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(levelAccess, pPlayer, ModBlocks.ROBO_SURGEON.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
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
                    // Try to find the correct slot in Robosurgeon
                    boolean moved = false;
                    for (int pass = 0; pass < 2; pass++) { // Pass 0: empty slots, Pass 1: ghost slots
                        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                            if (i >= slotType && i < slotType + RobosurgeonBlockEntity.SLOTS_PER_PART) {
                                Slot targetSlot = this.slots.get(i);
                                if (pass == 1 && targetSlot.hasItem()) {
                                    ItemStack targetItem = targetSlot.getItem();
                                    if (targetItem.hasTag() && targetItem.getTag() != null
                                            && targetItem.getTag().getBoolean("cyberware_ghost")) {
                                        targetSlot.set(ItemStack.EMPTY); // Clear ghost to allow move
                                    }
                                }

                                if (targetSlot.mayPlace(itemstack1)) {
                                    if (this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                                        moved = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (moved)
                            break;
                    }
                    if (!moved)
                        return ItemStack.EMPTY;
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