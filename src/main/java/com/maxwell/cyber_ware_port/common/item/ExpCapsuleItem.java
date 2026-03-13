package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExpCapsuleItem extends Item {
    public ExpCapsuleItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (!pLevel.isClientSide && customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("xp")) {
                int xp = tag.getInt("xp");
                pPlayer.giveExperiencePoints(xp);
                stack.shrink(1);
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CustomData customData = pStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("xp")) {
                pTooltipComponents.add(Component.literal(tag.getInt("xp") + " XP Stored"));
            }
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}