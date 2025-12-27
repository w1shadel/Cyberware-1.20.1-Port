package com.maxwell.cyber_ware_port.common.item;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NeuropozyneItem extends Item {

    private static final int DURATION = 24000;

    public NeuropozyneItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide && entityLiving instanceof Player player) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.applyImmunity(DURATION);
                player.sendSystemMessage(Component.translatable("cyberware.message.suppressant_applied").withStyle(net.minecraft.ChatFormatting.GREEN));
            });
            player.removeEffect(MobEffects.CONFUSION);
            player.removeEffect(MobEffects.DIG_SLOWDOWN);
            player.removeEffect(MobEffects.WEAKNESS);
        }
        return super.finishUsingItem(stack, level, entityLiving);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return net.minecraft.world.item.ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("cyberware.item.neuropozyne.desc").withStyle(ChatFormatting.GRAY));
    }
}