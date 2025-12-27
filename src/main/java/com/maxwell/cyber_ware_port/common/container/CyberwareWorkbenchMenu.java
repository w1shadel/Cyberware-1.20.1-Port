package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.common.block.blueprintChest.BlueprintChestBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlockEntity;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CyberwareWorkbenchMenu extends AbstractContainerMenu {

    private static final int WORKBENCH_SLOTS = 10;
    private static final int PANEL_X = -61;
    private static final int PANEL_Y = 12;
    private static final int SLOT_OFFSET_X = 10;
    private static final int SLOT_OFFSET_Y = 6;
    public final CyberwareWorkbenchBlockEntity blockEntity;
    private final Level level;
    private final List<List<Slot>> pageSlots = new ArrayList<>();
    private final List<List<Slot>> blueprintPageSlots = new ArrayList<>();
    public boolean hasExtendedInventory = false;
    public boolean hasBlueprintLibrary = false;
    public boolean isExtendedOpen = true;
    private int currentPage = 0;
    private int maxPages = 0;
    private int blueprintCurrentPage = 0;
    private int blueprintMaxPages = 0;
    private final ContainerData pageData = new SimpleContainerData(6) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> currentPage;
                case 1 -> maxPages;
                case 2 -> isExtendedOpen ? 1 : 0;
                case 3 -> hasBlueprintLibrary ? 1 : 0;
                case 4 -> blueprintCurrentPage;
                case 5 -> blueprintMaxPages;
                default -> 0;

            };

        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> {
                    currentPage = value;
                    updateSlotPositions();
                }
                case 1 -> maxPages = value;
                case 2 -> {
                    isExtendedOpen = (value == 1);
                    updateSlotPositions();

                }
                case 3 -> hasBlueprintLibrary = (value == 1);
                case 4 -> {
                    blueprintCurrentPage = value;
                    updateSlotPositions();
                }
                case 5 -> blueprintMaxPages = value;

            }
        }
    };

    public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));

    }

    public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), pContainerId);
        this.blockEntity = (CyberwareWorkbenchBlockEntity) entity;
        this.level = inv.player.level();
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.INPUT_SLOT, 15, 20));
            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.PAPER_SLOT, 15, 53));
            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT, 115, 53));
            for (int i = 0;
                 i < 3;
                 i++) {
                for (int j = 0;
                     j < 2;
                     j++) {
                    this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.OUTPUT_SLOT_START + (i * 2 + j), 71 + j * 18, 17 + i * 18));

                }
            }
            this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.SPECIAL_OUTPUT_SLOT, 141, 21));

        });
        findAndAddExternalInventory();
        findAndAddBlueprintLibrary();
        addDataSlots(pageData);
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        updateSlotPositions();

    }

    private void findAndAddExternalInventory() {
        BlockPos center = blockEntity.getBlockPos();
        List<ComponentBoxBlockEntity> foundBoxes = new ArrayList<>();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockEntity targetBe = level.getBlockEntity(center.offset(x, y, z));
                    if (targetBe instanceof ComponentBoxBlockEntity box) foundBoxes.add(box);
                }
            }
        }
        if (!foundBoxes.isEmpty()) {
            this.hasExtendedInventory = true;
            this.maxPages = foundBoxes.size();
            for (ComponentBoxBlockEntity box : foundBoxes) {
                List<Slot> currentBoxSlots = new ArrayList<>();
                box.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    for (int row = 0; row < 6; row++) {
                        for (int col = 0; col < 3; col++) {
                            int index = col + row * 3;
                            if (index < handler.getSlots()) {
                                Slot slot = new SlotItemHandler(handler, index, -10000, -10000);
                                this.addSlot(slot);
                                currentBoxSlots.add(slot);
                            }
                        }
                    }
                });
                pageSlots.add(currentBoxSlots);
            }
        }
    }

    private void findAndAddBlueprintLibrary() {
        BlockPos center = blockEntity.getBlockPos();
        List<BlueprintChestBlockEntity> foundChests = new ArrayList<>();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockEntity targetBe = level.getBlockEntity(center.offset(x, y, z));
                    if (targetBe instanceof BlueprintChestBlockEntity chest) {
                        foundChests.add(chest);
                    }
                }
            }
        }
        if (!foundChests.isEmpty()) {
            this.hasBlueprintLibrary = true;
            this.blueprintMaxPages = foundChests.size();
            for (BlueprintChestBlockEntity chest : foundChests) {
                List<Slot> currentChestSlots = new ArrayList<>();
                chest.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    for (int row = 0; row < 6; row++) {
                        for (int col = 0; col < 3; col++) {
                            int index = col + row * 3;
                            if (index < 18 && index < handler.getSlots()) {
                                Slot slot = new SlotItemHandler(handler, index, -10000, -10000) {
                                    @Override
                                    public boolean mayPlace(@NotNull ItemStack stack) {
                                        return stack.getItem() instanceof BlueprintItem;
                                    }
                                };
                                this.addSlot(slot);
                                currentChestSlots.add(slot);
                            }
                        }
                    }
                });
                blueprintPageSlots.add(currentChestSlots);
            }
        }
    }

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);

    }

    public void changePage(int direction) {
        int newPage = currentPage + direction;
        if (newPage >= 0 && newPage < maxPages) {
            currentPage = newPage;
            pageData.set(0, newPage);

        }
    }

    public void changeBlueprintPage(int direction) {
        int newPage = blueprintCurrentPage + direction;
        if (newPage >= 0 && newPage < blueprintMaxPages) {
            blueprintCurrentPage = newPage;
            pageData.set(4, newPage);

        }
    }

    public void setExtendedOpen(boolean open) {
        this.isExtendedOpen = open;
        pageData.set(2, open ? 1 : 0);

    }

    private void updateSlotPositions() {
        int startY = PANEL_Y + SLOT_OFFSET_Y;
        int leftStartX = PANEL_X + SLOT_OFFSET_X;
        for (int i = 0;
             i < pageSlots.size();
             i++) {
            List<Slot> slots = pageSlots.get(i);
            boolean isVisible = (i == currentPage) && isExtendedOpen;
            layoutSlots(slots, isVisible, leftStartX, startY);

        }
        int rightStartX = 176 + 5;
        for (int i = 0;
             i < blueprintPageSlots.size();
             i++) {
            List<Slot> slots = blueprintPageSlots.get(i);
            boolean isVisible = (i == blueprintCurrentPage) && isExtendedOpen && hasBlueprintLibrary;
            layoutSlots(slots, isVisible, rightStartX, 18);

        }
    }

    private void layoutSlots(List<Slot> slots, boolean isVisible, int startX, int startY) {
        for (int row = 0;
             row < 6;
             row++) {
            for (int col = 0;
                 col < 3;
                 col++) {
                int index = col + row * 3;
                if (index < slots.size()) {
                    Slot slot = slots.get(index);
                    if (isVisible) {
                        slot.x = startX + col * 18;
                        slot.y = startY + row * 18;

                    } else {
                        slot.x = -10000;
                        slot.y = -10000;

                    }
                }
            }
        }
    }

    public int getCurrentPage() {
        return pageData.get(0);
    }

    public int getMaxPages() {
        return pageData.get(1);
    }

    public int getBlueprintCurrentPage() {
        return pageData.get(4);
    }

    public int getBlueprintMaxPages() {
        return pageData.get(5);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        int totalSlots = this.slots.size();
        int WB_END = WORKBENCH_SLOTS;
        int EXT_START = WB_END;
        int leftCount = pageSlots.stream().mapToInt(List::size).sum();
        int EXT_END = EXT_START + leftCount;
        int LIB_START = EXT_END;
        int rightCount = blueprintPageSlots.stream().mapToInt(List::size).sum();
        int LIB_END = LIB_START + rightCount;
        int PLAYER_START = LIB_END;
        int PLAYER_END = totalSlots;
        if (pIndex < PLAYER_START) {
            if (!moveItemStackTo(sourceStack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex >= PLAYER_START && pIndex < PLAYER_END) {
            boolean moved = false;
            if (sourceStack.getItem() instanceof BlueprintItem) {
                if (moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT + 1, false)) {
                    moved = true;
                } else if (hasBlueprintLibrary && rightCount > 0) {
                    if (moveItemStackTo(sourceStack, LIB_START, LIB_END, false)) {
                        moved = true;
                    }
                }
            } else if (sourceStack.getItem() instanceof com.maxwell.cyber_ware_port.common.item.base.ICyberware) {
                if (moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.INPUT_SLOT, CyberwareWorkbenchBlockEntity.INPUT_SLOT + 1, false)) {
                    moved = true;
                } else if (isExtendedOpen && leftCount > 0) {
                    if (moveItemStackTo(sourceStack, EXT_START, EXT_END, false)) {
                        moved = true;
                    }
                }
            } else if (sourceStack.is(net.minecraft.world.item.Items.PAPER)) {
                if (moveItemStackTo(sourceStack, CyberwareWorkbenchBlockEntity.PAPER_SLOT, CyberwareWorkbenchBlockEntity.PAPER_SLOT + 1, false)) {
                    moved = true;
                }
            } else {
                if (isExtendedOpen && leftCount > 0) {
                    if (moveItemStackTo(sourceStack, EXT_START, EXT_END, false)) {
                        moved = true;
                    }
                }
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
            return ItemStack.EMPTY;
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