package com.Maxwell.cyber_ware_port.Common.Container;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;public class RobosurgeonMenu extends AbstractContainerMenu {

    public final RobosurgeonBlockEntity blockEntity;

    private final ContainerLevelAccess levelAccess;

    private int invheight = 140;

    private final ContainerData data;public RobosurgeonMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));

    }

    public RobosurgeonMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ROBO_SURGEON_MENU.get(), pContainerId);

        this.blockEntity = (RobosurgeonBlockEntity) entity;

        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        this.data = data;

        addDataSlots(data);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0;
 i < RobosurgeonBlockEntity.TOTAL_SLOTS;
 i++) {
                this.addSlot(new SlotItemHandler(handler, i, -10000, -10000));

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
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.getSlot(slotId);if (slot.container == blockEntity.getItemHandler() || slot.index < RobosurgeonBlockEntity.TOTAL_SLOTS) { 
                if (slot.hasItem()) {
                    ItemStack stack = slot.getItem();if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
                        slot.set(ItemStack.EMPTY);

                        return;

                    }
                }
            }
        }
        super.clicked(slotId, button, clickType, player);

    }
    private void addPlayerInventory(Inventory playerInventory) {

        int startY = invheight;for (int i = 0;
 i < 3;
 ++i) {
            for (int l = 0;
 l < 9;
 ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, startY + i * 18));

            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {

        int startY = invheight + 58;for (int i = 0;
 i < 9;
 ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, startY));

        }
    }
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(levelAccess, pPlayer, ModBlocks.ROBO_SURGEON.get());

    }
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;

    }
}