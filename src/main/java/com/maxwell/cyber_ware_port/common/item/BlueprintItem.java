package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

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
                return BuiltInRegistries.ITEM.get(Identifier.parse(tag.getString("targetItem")));
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        Item target = getTargetItem(pStack);
        if (target != null) {
            pTooltipComponents.add(Component.literal("Schematic for: ").append(target.getDescription()));
        } else {
            pTooltipComponents.add(Component.literal("Blank Schematic"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}