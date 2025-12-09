package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;


public class RapidFireFlywheelItem extends CyberwareItem {
    public RapidFireFlywheelItem() {
        super(new Builder(8, RobosurgeonBlockEntity.SLOT_ARMS)
                .maxInstall(1)
        );

    }
}