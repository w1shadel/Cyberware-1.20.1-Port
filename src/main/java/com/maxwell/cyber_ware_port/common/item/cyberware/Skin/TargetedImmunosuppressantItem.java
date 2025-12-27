package com.maxwell.cyber_ware_port.common.item.cyberware.Skin;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class TargetedImmunosuppressantItem extends CyberwareItem {
    public TargetedImmunosuppressantItem() {
        super(new Builder(-25, RobosurgeonBlockEntity.SLOT_SKIN)
                .maxInstall(8)
                .energy(3, 0, 0, StackingRule.LINEAR));

    }
}