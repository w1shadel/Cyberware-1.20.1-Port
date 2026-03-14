package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class KatanaItem extends SwordItem {
    public KatanaItem() {
        super(Tiers.IRON, new Properties()
                .attributes(SwordItem.createAttributes(Tiers.IRON, 4, -2.0F))
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("cyberware.item.katana.desc").withStyle(ChatFormatting.GRAY));
    }
}