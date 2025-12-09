package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
public class InternalDefibrillatorItem extends CyberwareItem {
    public InternalDefibrillatorItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)

                .energy(0, 0, 500, StackingRule.STATIC));
    }
}