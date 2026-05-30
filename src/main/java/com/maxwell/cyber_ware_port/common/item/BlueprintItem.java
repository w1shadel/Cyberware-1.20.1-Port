package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class BlueprintItem extends Item {
    public BlueprintItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack createBlueprintFor(Item targetItem) {
        ItemStack stack = new ItemStack(com.maxwell.cyber_ware_port.init.ModItems.BLUEPRINT.get());
        Identifier key = BuiltInRegistries.ITEM.getKey(targetItem);
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
            tag.putString("targetItem", key.toString());
        }));
        return stack;
    }

    public static Item getTargetItem(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("targetItem")) {
                return BuiltInRegistries.ITEM.get(Identifier.parse(tag.getStringOr("targetItem", "")))
                        .map(Holder::value)
                        .orElse(null);
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        Item target = getTargetItem(itemStack);
        if (target != null) {
            builder.accept(Component.translatable("cyberware.tooltip.blueprint.schematic_for",
                    Component.translatable(target.getDescriptionId())));
        } else {
            builder.accept(Component.translatable("cyberware.tooltip.blueprint.blank"));
        }
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}