package com.Maxwell.cyber_ware_port.Common.Item;import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;public class KatanaItem extends SwordItem {
    public KatanaItem() {super(Tiers.IRON, 4, -2.0F, new Properties().stacksTo(1));

    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("cyberware.item.katana.desc").withStyle(ChatFormatting.GRAY));
    }
}