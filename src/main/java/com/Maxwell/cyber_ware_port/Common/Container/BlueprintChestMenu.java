package com.Maxwell.cyber_ware_port.Common.Container;import com.Maxwell.cyber_ware_port.Common.Block.BlueprintChest.BlueprintChestBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;public class BlueprintChestMenu extends AbstractContainerMenu {

    public final BlueprintChestBlockEntity blockEntity;

    private static final int CONTAINER_SLOTS = 18;public BlueprintChestMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));

    }

    public BlueprintChestMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.BLUEPRINT_CHEST_MENU.get(), pContainerId);

        this.blockEntity = (BlueprintChestBlockEntity) entity;addEntitySlots();

        addPlayerInventory(inv);

        addPlayerHotbar(inv);

    }

    private void addEntitySlots() {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int row = 0;
 row < 2;
 row++) {     
                for (int col = 0;
 col < 9;
 col++) { 
                    this.addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {

                            return stack.getItem() instanceof BlueprintItem;

                        }
                    });

                }
            }
        });

    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);

        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;ItemStack sourceStack = sourceSlot.getItem();

        ItemStack copyOfSourceStack = sourceStack.copy();int PLAYER_INVENTORY_START = CONTAINER_SLOTS;
 
        int PLAYER_HOTBAR_START = PLAYER_INVENTORY_START + 27;
 
        int END_OF_SLOTS = PLAYER_HOTBAR_START + 9;if (pIndex < CONTAINER_SLOTS) {
            if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, END_OF_SLOTS, true)) {
                return ItemStack.EMPTY;

            }
        }

        else {

            if (sourceStack.getItem() instanceof BlueprintItem) {
                if (!moveItemStackTo(sourceStack, 0, CONTAINER_SLOTS, false)) {
                    return ItemStack.EMPTY;

                }
            } else {
                return ItemStack.EMPTY;
 
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
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                pPlayer, ModBlocks.BLUEPRINT_CHEST.get());

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0;
 i < 3;
 ++i) {
            for (int l = 0;
 l < 9;
 ++l) {this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 68 + i * 18));
 
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0;
 i < 9;
 ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 126));
 
        }
    }
}