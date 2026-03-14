package com.maxwell.cyber_ware_port.common.block.component_box;

import com.maxwell.cyber_ware_port.common.container.ComponentBoxMenu;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.common.item.componentbox.ComponentBoxItem;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentBoxBlockEntity extends BlockEntity implements MenuProvider {
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
    private Component customName;

    public ComponentBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPONENT_BOX.get(), pPos, pBlockState);
    }

    private boolean isComponent(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof CyberwareItem || item instanceof ComponentBoxItem) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return id.getPath().contains("component_");
    }

    @Override
    public @NotNull Component getDisplayName() {
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
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ComponentBoxMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("Inventory")) {
            itemHandler.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        }
        if (pTag.contains("CustomName")) {
            this.customName = Component.Serializer.fromJson(pTag.getString("CustomName"), pRegistries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("Inventory", itemHandler.serializeNBT(pRegistries));
        if (this.customName != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.customName, pRegistries));
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}