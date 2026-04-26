package com.maxwell.cyber_ware_port.common.item.cyberware.heart;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class CardiovascularCouplerItem extends CyberwareItem {
    public CardiovascularCouplerItem(Properties p) {
        super(new Builder(p,5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .energy(0, 95, 0, StackingRule.STATIC));

    }
}
