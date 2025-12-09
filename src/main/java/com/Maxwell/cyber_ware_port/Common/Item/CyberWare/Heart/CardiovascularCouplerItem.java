package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;


public class CardiovascularCouplerItem extends CyberwareItem {
    public CardiovascularCouplerItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)

                .energy(0, 5, 0, StackingRule.STATIC));

    }
}