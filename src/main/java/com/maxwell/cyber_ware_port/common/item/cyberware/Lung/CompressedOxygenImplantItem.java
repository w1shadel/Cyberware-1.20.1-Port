package com.maxwell.cyber_ware_port.common.item.cyberware.lung;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CompressedOxygenImplantItem extends CyberwareItem {
    public CompressedOxygenImplantItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_LUNGS)
                .maxInstall(3)
                .requires(ModItems.HUMAN_LUNGS)
                .energy(5, 0, 0, StackingRule.LINEAR));
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        if (wearer.getAirSupply() < wearer.getMaxAirSupply()) {
            if (wearer.tickCount % 20 == 0) {
                wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    int count = stack.getCount();
                    int cost = this.getEnergyConsumption(stack) * count;
                    if (data.extractEnergy(cost, true) == cost) {
                        data.extractEnergy(cost, false);
                        int refillAmount = 15 * count;
                        int newAir = Math.min(wearer.getAirSupply() + refillAmount, wearer.getMaxAirSupply());
                        wearer.setAirSupply(newAir);
                    }
                });
            }
        }
    }
}