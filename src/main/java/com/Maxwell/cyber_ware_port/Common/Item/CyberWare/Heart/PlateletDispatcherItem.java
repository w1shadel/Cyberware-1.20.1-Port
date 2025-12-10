package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class PlateletDispatcherItem extends CyberwareItem {
    public PlateletDispatcherItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .requires(ModItems.CARDIOMECHANIC_PUMP)
                .energy(2, 0, 0, StackingRule.STATIC));

    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        float ratio = entity.getHealth() / entity.getMaxHealth();
        if (ratio >= 0.8f && ratio < 1.0f) {
            if (entity.tickCount % 40 == 0) {
                entity.heal(1.0f);

            }
        }
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return true;
    }
}