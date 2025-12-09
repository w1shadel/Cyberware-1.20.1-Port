package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;


public class FineManipulatorsItem extends CyberwareItem {
    public FineManipulatorsItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(1)
        );

    }
}