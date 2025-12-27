package com.maxwell.cyber_ware_port.common.item.cyberware.Lower_Organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Rarity;

public class DenseBatteryItem extends CyberwareItem {
    public DenseBatteryItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 0, 60000, StackingRule.STATIC)
                .properties(p -> p.rarity(Rarity.RARE)));

    }
}