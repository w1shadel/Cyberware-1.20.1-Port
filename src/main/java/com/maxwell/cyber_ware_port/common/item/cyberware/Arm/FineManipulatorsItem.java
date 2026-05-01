package com.maxwell.cyber_ware_port.common.item.cyberware.arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class FineManipulatorsItem extends CyberwareItem {
    public FineManipulatorsItem(Properties properties) {
        super(new Builder(properties, 2, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(1)
        );

    }
}
