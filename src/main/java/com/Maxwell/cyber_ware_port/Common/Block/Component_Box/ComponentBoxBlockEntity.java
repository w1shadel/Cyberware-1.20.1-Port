package com.Maxwell.cyber_ware_port.Common.Block.Component_Box;


import com.Maxwell.cyber_ware_port.Common.Container.ComponentBoxMenu;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import com.Maxwell.cyber_ware_port.Common.Item.ComponentBox.ComponentBoxItem;

import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;

import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.MenuProvider;

import net.minecraft.world.entity.player.Inventory;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.items.IItemHandler;

import net.minecraftforge.items.ItemStackHandler;

import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;


public class ComponentBoxBlockEntity extends BlockEntity implements MenuProvider {

    private Component customName;


    public final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isComponent(stack);

        }
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

        }
    };


    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);


    public ComponentBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPONENT_BOX.get(), pPos, pBlockState);

    }

    private boolean isComponent(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof CyberwareItem) return false;

        if (item instanceof ComponentBoxItem) return false;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        return id != null && id.getPath().contains("component_");

    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName : Component.translatable("item.cyber_ware_port.component_box");

    }

    public void setCustomName(Component name) {
        this.customName = name;

    }

    public boolean hasCustomName() {
        return customName != null;

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {

        return new ComponentBoxMenu(pContainerId, pPlayerInventory, this);

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();

        }
        return super.getCapability(cap, side);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));

        if (pTag.contains("CustomName")) {
            this.customName = Component.Serializer.fromJson(pTag.getString("CustomName"));

        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.put("Inventory", itemHandler.serializeNBT());

        if (this.customName != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.customName));

        }
    }
}