package com.Maxwell.cyber_ware_port.Common.Container;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlockEntity;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class CyberwareWorkbenchMenu extends AbstractContainerMenu {

    public final CyberwareWorkbenchBlockEntity blockEntity;
    private final Level level;public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), pContainerId);this.blockEntity = (CyberwareWorkbenchBlockEntity) entity;
        this.level = inv.player.level();this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {

            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.INPUT_SLOT, 15, 20));

            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.PAPER_SLOT, 15, 53));

            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT, 115, 53));

            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 2; j++) {
                    this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.OUTPUT_SLOT_START + (i * 2 + j), 71 + j * 18, 17 + i * 18));
                }
            }

            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.SPECIAL_OUTPUT_SLOT, 141, 21));
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    private static final int TE_INVENTORY_SLOT_COUNT = 10;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int WORKBENCH_SLOTS = 10; 
        int PLAYER_START = WORKBENCH_SLOTS; 
        int PLAYER_END = PLAYER_START + 36; 

        if (pIndex < WORKBENCH_SLOTS) {
            if (!moveItemStackTo(sourceStack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {

            if (sourceStack.getItem() instanceof com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware) {

                if (!moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.INPUT_SLOT, CyberwareWorkbenchBlockEntity.INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (sourceStack.is(net.minecraft.world.item.Items.PAPER)) {

                if (!moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.PAPER_SLOT, CyberwareWorkbenchBlockEntity.PAPER_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (sourceStack.getItem() instanceof com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem) {

                if (!moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {if (!moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.OUTPUT_SLOT_START, CyberwareWorkbenchBlockEntity.SPECIAL_OUTPUT_SLOT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.CYBERWARE_WORKBENCH.get());
    }
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}