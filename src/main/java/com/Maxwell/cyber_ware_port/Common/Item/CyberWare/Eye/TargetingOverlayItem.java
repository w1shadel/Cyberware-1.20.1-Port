package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class TargetingOverlayItem extends CyberwareItem {
    public TargetingOverlayItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .requires(ModItems.CYBER_EYE)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {

        if (isActive(stack)) {
            int cost = 1;
            if (energyStorage.extractEnergy(cost, true) == cost) {
                energyStorage.extractEnergy(cost, false);
            } else {}
        }
    }
}