package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlockEntity;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ComponentBoxMenu extends AbstractContainerMenu {
    private final ItemStack lockedStack;
    private final int lockedSlotIndex;

    public ComponentBoxMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        boolean mainHand = extraData.readBoolean();
        this.lockedStack = playerInv.player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        this.lockedSlotIndex = mainHand ? playerInv.selected : 40;
        IItemHandler handler = lockedStack.getCapability(Capabilities.ItemHandler.ITEM);
        addBoxSlots(handler != null ? handler : new ItemStackHandler(18));
        addPlayerInventory(playerInv);
    }

    public ComponentBoxMenu(int id, Inventory playerInv, ItemStack boxStack) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        this.lockedStack = boxStack;
        this.lockedSlotIndex = (playerInv.player.getMainHandItem() == boxStack) ? playerInv.selected : 40;
        IItemHandler handler = boxStack.getCapability(Capabilities.ItemHandler.ITEM);
        addBoxSlots(handler != null ? handler : new ItemStackHandler(18));
        addPlayerInventory(playerInv);
    }

    public ComponentBoxMenu(int id, Inventory playerInv, ComponentBoxBlockEntity blockEntity) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        this.lockedStack = ItemStack.EMPTY;
        this.lockedSlotIndex = -1;
        addBoxSlots(blockEntity.getItemHandler());
        addPlayerInventory(playerInv);
    }

    private void addBoxSlots(IItemHandler handler) {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    private void addPlayerInventory(Inventory playerInv) {
        int yOffset = 68;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, yOffset + 58));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return lockedStack.isEmpty() || player.getInventory().contains(lockedStack);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (lockedSlotIndex >= 0) {
            if (slotId >= 0 && slotId < slots.size() && slots.get(slotId).getItem() == lockedStack) return;
            if (clickType == ClickType.SWAP && button == lockedSlotIndex) return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 18) {
                if (!this.moveItemStackTo(itemstack1, 18, 54, true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 18, false)) return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }
}