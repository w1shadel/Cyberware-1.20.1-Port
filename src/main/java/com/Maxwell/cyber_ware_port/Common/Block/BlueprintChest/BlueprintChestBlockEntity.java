package com.Maxwell.cyber_ware_port.Common.Block.BlueprintChest;

import com.Maxwell.cyber_ware_port.Common.Container.BlueprintChestMenu;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlueprintChestBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof BlueprintItem;

        }
    };private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();public BlueprintChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BLUEPRINT_CHEST.get(), pPos, pBlockState);

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.blueprint_chest");

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BlueprintChestMenu(pContainerId, pPlayerInventory, this);

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();

        }
        return super.getCapability(cap, side);

    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());

        super.saveAdditional(pTag);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        itemHandler.deserializeNBT(pTag.getCompound("inventory"));

    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0;
 i < itemHandler.getSlots();
 i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        }
        Containers.dropContents(this.level, this.worldPosition, inventory);

    }
}