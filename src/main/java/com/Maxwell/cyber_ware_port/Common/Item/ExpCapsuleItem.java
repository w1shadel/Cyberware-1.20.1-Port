package com.Maxwell.cyber_ware_port.Common.Item;import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;public class ExpCapsuleItem extends Item {
    public ExpCapsuleItem(Properties pProperties) {
        super(pProperties.stacksTo(1));

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide) {
            if (stack.hasTag() && stack.getTag().contains("xp")) {
                int xp = stack.getTag().getInt("xp");

                pPlayer.giveExperiencePoints(xp);stack.shrink(1);

            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());

    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pStack.hasTag() && pStack.getTag().contains("xp")) {
            pTooltipComponents.add(Component.literal(pStack.getTag().getInt("xp") + " XP Stored"));

        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

    }
}