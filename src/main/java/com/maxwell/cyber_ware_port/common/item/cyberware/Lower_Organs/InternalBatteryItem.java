package com.maxwell.cyber_ware_port.common.item.cyberware.Lower_Organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class InternalBatteryItem extends CyberwareItem {
    public InternalBatteryItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(4)
                .energy(0, 0, 3000, StackingRule.LINEAR));

    }
}