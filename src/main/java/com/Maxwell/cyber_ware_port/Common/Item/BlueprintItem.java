package com.Maxwell.cyber_ware_port.Common.Item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
@SuppressWarnings("removal")
public class BlueprintItem extends Item {
    public BlueprintItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack createBlueprintFor(Item targetItem) {
        ItemStack stack = new ItemStack(com.Maxwell.cyber_ware_port.Init.ModItems.BLUEPRINT.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("targetItem", ForgeRegistries.ITEMS.getKey(targetItem).toString());
        return stack;
    }

    public static Item getTargetItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("targetItem")) {
            ResourceLocation loc = new ResourceLocation(tag.getString("targetItem"));
            return ForgeRegistries.ITEMS.getValue(loc);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        Item target = getTargetItem(pStack);
        if (target != null) {
            pTooltipComponents.add(Component.literal("Schematic for: ").append(target.getDescription()));
        } else {
            pTooltipComponents.add(Component.literal("Blank Schematic"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}