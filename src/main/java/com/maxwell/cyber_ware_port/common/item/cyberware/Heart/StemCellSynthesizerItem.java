package com.maxwell.cyber_ware_port.common.item.cyberware.heart;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class StemCellSynthesizerItem extends CyberwareItem {
    public StemCellSynthesizerItem(Properties p) {
        super(new Builder(p,10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .energy(50, 0, 0, StackingRule.STATIC));
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        if (wearer.getHealth() < wearer.getMaxHealth()) {
            if (wearer.tickCount % 100 == 0) {
                CyberwareUserData data = wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                int cost = this.getEnergyConsumption(stack);
                if (data.getEnergyStored() >= cost) {
                    data.extractEnergy(cost, false);
                    wearer.heal(1.0f);
                }
            }
        }
    }
}