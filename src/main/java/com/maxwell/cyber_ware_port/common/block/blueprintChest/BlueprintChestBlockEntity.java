package com.maxwell.cyber_ware_port.common.block.blueprintchest;

import com.maxwell.cyber_ware_port.common.container.BlueprintChestMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlueprintChestBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(18) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return resource.toStack().getItem() instanceof BlueprintItem;
        }
    };

    public BlueprintChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BLUEPRINT_CHEST.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.blueprint_chest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new BlueprintChestMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.itemHandler.serialize(output.child("inventory"));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input.childOrEmpty("inventory"));
    }

    public void drops() {
        if (this.level == null) return;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
        }
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }
}