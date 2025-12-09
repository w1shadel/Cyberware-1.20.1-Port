package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class StemCellSynthesizerItem extends CyberwareItem {
    public StemCellSynthesizerItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .energy(50, 0, 0, StackingRule.STATIC)); 
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {

        return 0;
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            if (entity.tickCount % 100 == 0) {

                int cost = super.getEnergyConsumption(stack);

                if (energyStorage.extractEnergy(cost, true) == cost) {
                    energyStorage.extractEnergy(cost, false);
                    entity.heal(1.0f);
                }
            }
        }
    }
}