package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ExpCapsuleItem extends Item {
    public ExpCapsuleItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (!pLevel.isClientSide() && customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("xp")) {
                int xp = tag.getIntOr("xp", 0);
                pPlayer.giveExperiencePoints(xp);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("xp")) {
                builder.accept(Component.literal(tag.getInt("xp") + " XP Stored"));
            }
        }
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}