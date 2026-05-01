package com.maxwell.cyber_ware_port.common.block.component_box;

import com.maxwell.cyber_ware_port.common.container.ComponentBoxMenu;
import com.maxwell.cyber_ware_port.common.item.ComponentBoxItem;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentBoxBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(18) {
        @Override
        public boolean isValid(int index, ItemResource resource) {
            return isComponent(resource.toStack());
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
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
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.itemHandler.serialize(output.child("Inventory"));
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input.childOrEmpty("Inventory"));
        this.customName = input.read("CustomName", ComponentSerialization.CODEC).orElse(null);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }
}