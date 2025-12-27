package com.maxwell.cyber_ware_port.common.item.cyberware.Eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.item.ItemStack;

public class TargetingOverlayItem extends CyberwareItem {
    public TargetingOverlayItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(1, 0, 0, StackingRule.STATIC)
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
}