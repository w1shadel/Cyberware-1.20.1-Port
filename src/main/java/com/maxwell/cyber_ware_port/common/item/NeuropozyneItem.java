package com.maxwell.cyber_ware_port.common.item;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class NeuropozyneItem extends Item {
    private static final int DURATION = 24000;

    public NeuropozyneItem(Properties p) {
        super(p);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide() && entityLiving instanceof Player player) {
            CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            data.applyImmunity(DURATION);
            player.sendSystemMessage(Component.translatable("cyberware.message.suppressant_applied").withStyle(ChatFormatting.GREEN));
            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.SLOWNESS);
            player.removeEffect(MobEffects.WEAKNESS);
        }
        return super.finishUsingItem(stack, level, entityLiving);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("cyberware.item.neuropozyne.desc").withStyle(ChatFormatting.GRAY));
    }
}