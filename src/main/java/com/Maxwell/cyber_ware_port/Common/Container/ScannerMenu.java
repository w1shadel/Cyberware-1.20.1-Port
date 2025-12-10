package com.Maxwell.cyber_ware_port.Common.Container;import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockEntity;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;public class ScannerMenu extends AbstractContainerMenu {
    public final ScannerBlockEntity blockEntity;

    private final ContainerData data;public ScannerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));

    }
    public int getScaledProgress(int scale) {
        int progress = this.data.get(0);

        int maxProgress = this.data.get(1);if (maxProgress == 0 || progress == 0) {
            return 0;

        }

        return progress * scale / maxProgress;

    }

    public ScannerMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SCANNER_MENU.get(), pContainerId);

        blockEntity = (ScannerBlockEntity) entity;

        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, ScannerBlockEntity.SLOT_INPUT, 35, 53));

            this.addSlot(new SlotItemHandler(handler, ScannerBlockEntity.SLOT_PAPER, 15, 53));

            this.addSlot(new SlotItemHandler(handler, ScannerBlockEntity.SLOT_OUTPUT, 137, 53));

        });

        addDataSlots(data);

        addPlayerInventory(inv);

        addPlayerHotbar(inv);

    }

    public boolean isCrafting() {
        return data.get(0) > 0;

    }

    public int getScaledProgress() {
        int progress = this.data.get(0);

        int maxProgress = this.data.get(1);
  
        int progressArrowSize = 24;return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;

    }private static final int HOTBAR_SLOT_COUNT = 9;

    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;

    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;

    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;

    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 3;@Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {

        return ItemStack.EMPTY;

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                pPlayer, ModBlocks.SCANNER.get());

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0;
 i < 3;
 ++i) {
            for (int l = 0;
 l < 9;
 ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));

            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0;
 i < 9;
 ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));

        }
    }
}