package com.maxwell.cyber_ware_port.common.item.componentbox;

import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ComponentBoxCapability {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> {
            final HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(BuiltInRegistries.ITEM.asLookup()));

            ItemStackHandler handler = new ItemStackHandler(18) {
                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack s) {
                    Item item = s.getItem();
                    if (item instanceof CyberwareItem || item instanceof ComponentBoxItem) return false;
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                    return id.getPath().contains("component_");
                }

                @Override
                protected void onContentsChanged(int slot) {
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(serializeNBT(provider)));
                }
            };

            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                handler.deserializeNBT(provider, data.copyTag());
            }

            return handler;
        }, ModItems.COMPONENT_BOX.get());
    }
}