package com.maxwell.cyber_ware_port.common.container;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
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
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;

public class RobosurgeonMenu extends AbstractContainerMenu {

    public final RobosurgeonBlockEntity blockEntity;

    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;
    private final int invheight = 140;

    public RobosurgeonMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2));

    }

    public RobosurgeonMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ROBO_SURGEON_MENU.get(), pContainerId);
        this.blockEntity = (RobosurgeonBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.data = data;
        addDataSlots(data);
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
                this.addSlot(new SlotItemHandler(handler, i, -10000, -10000) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if (!super.mayPlace(stack))
                            return false;

                        // 競合チェック: 他のスロットにあるアイテム(Ghost含む)と競合する場合は置かせない
                        ICyberware myCw = com.maxwell.cyber_ware_port.api.json.CyberwareAPI.getCyberware(stack);
                        if (myCw == null)
                            return true;

                        for (int otherSlotIndex = 0; otherSlotIndex < RobosurgeonBlockEntity.TOTAL_SLOTS; otherSlotIndex++) {
                            if (otherSlotIndex == this.getSlotIndex())
                                continue; // 自分自身はスキップ

                            ItemStack otherStack = handler.getStackInSlot(otherSlotIndex);
                            if (otherStack.isEmpty())
                                continue;

                            ICyberware otherCw = com.maxwell.cyber_ware_port.api.json.CyberwareAPI
                                    .getCyberware(otherStack);
                            if (otherCw == null)
                                continue;

                            // 1. 物理的競合 (BodyPartType)
                            if (myCw.getBodyPartType(
                                    stack) != com.maxwell.cyber_ware_port.common.item.base.BodyPartType.NONE
                                    && myCw.getBodyPartType(stack) == otherCw.getBodyPartType(otherStack)) {
                                return false;
                            }

                            // 2. 明示的な非互換 (isIncompatible)
                            if (myCw.isIncompatible(stack, otherStack) || otherCw.isIncompatible(otherStack, stack)) {
                                return false;
                            }
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
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.getSlot(slotId);
            if (slot.container == blockEntity.getItemHandler() || slot.index < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                if (slot.hasItem()) {
                    ItemStack stack = slot.getItem();
                    if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
                        slot.set(ItemStack.EMPTY);
                        return;

                    }
                }
            }
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
        return ItemStack.EMPTY;

    }
}