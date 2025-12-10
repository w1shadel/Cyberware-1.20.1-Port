package com.Maxwell.cyber_ware_port.Common.Container;

import com.Maxwell.cyber_ware_port.Common.Block.Component_Box.ComponentBoxBlockEntity;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ComponentBoxMenu extends AbstractContainerMenu {

    private final ItemStack lockedStack;

    private final int lockedSlotIndex;

    public ComponentBoxMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        boolean mainHand = extraData.readBoolean();
        this.lockedStack = playerInv.player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        this.lockedSlotIndex = mainHand ? playerInv.selected : 40;
        IItemHandler handler = new ItemStackHandler(18);
        if (!lockedStack.isEmpty()) {
            var cap = lockedStack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (cap.isPresent()) {
                handler = cap.resolve().get();

            }
        }
        addBoxSlots(handler);
        addPlayerInventory(playerInv);

    }

    public ComponentBoxMenu(int id, Inventory playerInv, ItemStack boxStack) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        this.lockedStack = boxStack;
        if (playerInv.player.getMainHandItem() == boxStack) this.lockedSlotIndex = playerInv.selected;
        else this.lockedSlotIndex = 40;
        boxStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(this::addBoxSlots);
        addPlayerInventory(playerInv);

    }

    public ComponentBoxMenu(int id, Inventory playerInv, ComponentBoxBlockEntity blockEntity) {
        super(ModMenuTypes.COMPONENT_BOX_MENU.get(), id);
        this.lockedStack = ItemStack.EMPTY;
        this.lockedSlotIndex = -1;
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(this::addBoxSlots);
        addPlayerInventory(playerInv);

    }

    private void addBoxSlots(IItemHandler handler) {
        for (int row = 0;
             row < 2;
             row++) {
            for (int col = 0;
                 col < 9;
                 col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));

            }
        }
    }

    private void addPlayerInventory(Inventory playerInv) {
        int yOffset = 18 + 2 * 18 + 14;
        for (int row = 0;
             row < 3;
             row++) {
            for (int col = 0;
                 col < 9;
                 col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));

            }
        }
        for (int col = 0;
             col < 9;
             col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, yOffset + 58));

        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (!lockedStack.isEmpty()) {
            return player.getInventory().contains(lockedStack);

        }
        return true;

    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (lockedSlotIndex >= 0) {
            if (slotId >= 0 && slotId < slots.size()) {
                if (slots.get(slotId).getItem() == lockedStack) return;

            }
            if (clickType == ClickType.SWAP && button == lockedSlotIndex) return;

        }
        super.clicked(slotId, button, clickType, player);

    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;

    }
}