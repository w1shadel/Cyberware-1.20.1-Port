package com.maxwell.cyber_ware_port.common.item.cyberware.Bone;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class MarrowBatteryItem extends CyberwareItem {
    public MarrowBatteryItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_BONES)
                .maxInstall(1)
                .energy(0, 0, 1000, StackingRule.STATIC));

    }
}