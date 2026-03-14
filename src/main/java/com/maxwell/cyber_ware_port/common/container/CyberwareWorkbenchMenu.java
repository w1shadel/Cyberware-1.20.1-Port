package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.common.block.blueprintchest.BlueprintChestBlockEntity;
import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CyberwareWorkbenchMenu extends AbstractContainerMenu {
    private static final int WORKBENCH_SLOTS = 10;
    private static final int PANEL_X = -61;
    private static final int PANEL_Y = 12;
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
                case 0 -> { currentPage = value; updateSlotPositions(); }
                case 1 -> maxPages = value;
                case 2 -> { isExtendedOpen = (value == 1); updateSlotPositions(); }
                case 3 -> hasBlueprintLibrary = (value == 1);
                case 4 -> { blueprintCurrentPage = value; updateSlotPositions(); }
                case 5 -> blueprintMaxPages = value;
            }
        }
    };

    public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, RegistryFriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CyberwareWorkbenchMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), pContainerId);
        this.blockEntity = (CyberwareWorkbenchBlockEntity) entity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.INPUT_SLOT, 15, 20));
        this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.PAPER_SLOT, 15, 53));
        this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT, 115, 53));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.OUTPUT_SLOT_START + (i * 2 + j), 71 + j * 18, 17 + i * 18));
            }
        }
        this.addSlot(new SlotItemHandler(handler, CyberwareWorkbenchBlockEntity.SPECIAL_OUTPUT_SLOT, 141, 21));

        findAndAddExternalInventory();
        findAndAddBlueprintLibrary();
        addDataSlots(pageData);
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        updateSlotPositions();
    }

    private void findAndAddExternalInventory() {
        BlockPos center = blockEntity.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos pos = center.offset(x, y, z);
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
                    if (level.getBlockEntity(pos) instanceof ComponentBoxBlockEntity && handler != null) {
                        this.hasExtendedInventory = true;
                        List<Slot> currentBoxSlots = new ArrayList<>();
                        for (int i = 0; i < 18; i++) {
                            Slot slot = new SlotItemHandler(handler, i, -10000, -10000);
                            this.addSlot(slot);
                            currentBoxSlots.add(slot);
                        }
                        pageSlots.add(currentBoxSlots);
                    }
                }
            }
        }
        this.maxPages = pageSlots.size();
    }

    private void findAndAddBlueprintLibrary() {
        BlockPos center = blockEntity.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos pos = center.offset(x, y, z);
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
                    if (level.getBlockEntity(pos) instanceof BlueprintChestBlockEntity && handler != null) {
                        this.hasBlueprintLibrary = true;
                        List<Slot> currentChestSlots = new ArrayList<>();
                        for (int i = 0; i < 18; i++) {
                            Slot slot = new SlotItemHandler(handler, i, -10000, -10000) {
                                @Override public boolean mayPlace(@NotNull ItemStack s) { return s.getItem() instanceof BlueprintItem; }
                            };
                            this.addSlot(slot);
                            currentChestSlots.add(slot);
                        }
                        blueprintPageSlots.add(currentChestSlots);
                    }
                }
            }
        }
        this.blueprintMaxPages = blueprintPageSlots.size();
    }

    public void updateSlotPositions() {
        int leftX = PANEL_X + 10;
        int topY = PANEL_Y + 6;
        for (int i = 0; i < pageSlots.size(); i++) {
            layoutSlots(pageSlots.get(i), (i == currentPage) && isExtendedOpen, leftX, topY);
        }
        int rightX = 176 + 5;
        for (int i = 0; i < blueprintPageSlots.size(); i++) {
            layoutSlots(blueprintPageSlots.get(i), (i == blueprintCurrentPage) && isExtendedOpen && hasBlueprintLibrary, rightX, 18);
        }
    }
    private static final java.lang.reflect.Field slotX, slotY;

    static {
        try {
            slotX = Slot.class.getDeclaredField("x");
            slotX.setAccessible(true);
            slotY = Slot.class.getDeclaredField("y");
            slotY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    private void setSlotPos(Slot slot, int x, int y) {
        try {
            slotX.set(slot, x);
            slotY.set(slot, y);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private void layoutSlots(List<Slot> slots, boolean visible, int startX, int startY) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (visible) {
                setSlotPos(slot, startX + (i % 3) * 18, startY + (i / 3) * 18);
            } else {
                setSlotPos(slot, -10000, -10000);
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int WB_END = WORKBENCH_SLOTS;
        int EXT_END = WB_END + pageSlots.stream().mapToInt(List::size).sum();
        int LIB_END = EXT_END + blueprintPageSlots.stream().mapToInt(List::size).sum();

        if (pIndex < LIB_END) {
            if (!moveItemStackTo(sourceStack, LIB_END, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (sourceStack.getItem() instanceof BlueprintItem) {
                if (!moveItemStackTo(sourceStack, 2, 3, false) && !moveItemStackTo(sourceStack, EXT_END, LIB_END, false)) return ItemStack.EMPTY;
            } else if (sourceStack.getItem() instanceof ICyberware) {
                if (!moveItemStackTo(sourceStack, 0, 1, false) && !moveItemStackTo(sourceStack, WB_END, EXT_END, false)) return ItemStack.EMPTY;
            } else if (sourceStack.is(Items.PAPER)) {
                if (!moveItemStackTo(sourceStack, 1, 2, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(sourceStack, WB_END, EXT_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) sourceSlot.setByPlayer(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.CYBERWARE_WORKBENCH.get());
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

    public void changePage(int direction) { currentPage = Math.clamp(currentPage + direction, 0, Math.max(0, maxPages - 1)); updateSlotPositions(); }
    public void changeBlueprintPage(int direction) { blueprintCurrentPage = Math.clamp(blueprintCurrentPage + direction, 0, Math.max(0, blueprintMaxPages - 1)); updateSlotPositions(); }
}