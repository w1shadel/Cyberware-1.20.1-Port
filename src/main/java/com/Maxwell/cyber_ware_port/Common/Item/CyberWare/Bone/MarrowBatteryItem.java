package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

public class MarrowBatteryItem extends CyberwareItem {
    public MarrowBatteryItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_BONES)
                .maxInstall(1)
                .energy(0, 0, 1000, StackingRule.STATIC));

    }
}