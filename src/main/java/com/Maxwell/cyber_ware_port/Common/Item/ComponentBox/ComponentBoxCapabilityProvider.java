package com.Maxwell.cyber_ware_port.Common.Item.ComponentBox;import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;public class ComponentBoxCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    private final ItemStack stack;

    private final ItemStackHandler inventory = new ItemStackHandler(18) { 
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            return isComponent(stack);

        }

        @Override
        protected void onContentsChanged(int slot) {

            CompoundTag tag = ComponentBoxCapabilityProvider.this.stack.getOrCreateTag();

            tag.put("Inventory", this.serializeNBT());

        }
    };private final LazyOptional<IItemHandler> optional = LazyOptional.of(() -> inventory);public ComponentBoxCapabilityProvider(ItemStack stack) {
        this.stack = stack;if (stack.hasTag() && stack.getTag().contains("Inventory")) {
            inventory.deserializeNBT(stack.getTag().getCompound("Inventory"));

        }
    }

    private boolean isComponent(ItemStack stack) {
        Item item = stack.getItem();if (item instanceof CyberwareItem) return false;if (item instanceof ComponentBoxItem) return false;
ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        return id != null && id.getPath().contains("component_");

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optional.cast();

        }
        return LazyOptional.empty();

    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();

    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);

    }
}