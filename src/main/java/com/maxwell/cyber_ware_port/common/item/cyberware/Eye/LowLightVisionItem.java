package com.maxwell.cyber_ware_port.common.item.cyberware.Eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class LowLightVisionItem extends CyberwareItem {

    public LowLightVisionItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(2, 0, 0, StackingRule.STATIC)
                .requires(ModItems.CYBER_EYE)
        );

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return isActive(stack) ? super.getEnergyConsumption(stack) : 0;

    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (isActive(stack)) {
            wearer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }
}