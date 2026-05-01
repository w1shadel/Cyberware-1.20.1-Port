package com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Rarity;

public class CreativeBatteryItem extends CyberwareItem {
    public CreativeBatteryItem(Properties p) {
        super(new Builder(p, 0, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 1_000_000, 2_000_000_000, StackingRule.STATIC)
                .rarity(Rarity.EPIC)
        );
    }
}