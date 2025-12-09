package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import net.minecraft.world.item.ItemStack;


public class EnderJammerItem extends CyberwareItem {
    public EnderJammerItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BRAIN).maxInstall(1));

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return true;

    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {

        return isActive(stack) ? 5 : 0;

    }
}