package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class StemCellSynthesizerItem extends CyberwareItem {
    public StemCellSynthesizerItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .energy(50, 0, 0, StackingRule.STATIC));
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.getHealth() < wearer.getMaxHealth()) {
            if (wearer.tickCount % 100 == 0) {
                wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    int cost = this.getEnergyConsumption(stack);
                    if (data.extractEnergy(cost, true) == cost) {
                        data.extractEnergy(cost, false);
                        wearer.heal(1.0f);
                    }
                });
            }
        }
    }
}