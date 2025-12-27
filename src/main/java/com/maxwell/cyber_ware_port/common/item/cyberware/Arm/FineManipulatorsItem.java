package com.maxwell.cyber_ware_port.common.item.cyberware.Arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class FineManipulatorsItem extends CyberwareItem {
    public FineManipulatorsItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(1)
        );

    }
}