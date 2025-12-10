package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.ItemStack;

public class ThreatMatrixItem extends CyberwareItem {
    public ThreatMatrixItem() {
        super(new Builder(15, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(8, 0, 0, StackingRule.STATIC));

    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return isActive(stack) ? 8 : 0;

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }
}