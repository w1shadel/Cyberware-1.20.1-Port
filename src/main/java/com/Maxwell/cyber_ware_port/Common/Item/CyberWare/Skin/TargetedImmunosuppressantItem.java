package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

public class TargetedImmunosuppressantItem extends CyberwareItem {
    public TargetedImmunosuppressantItem() {
        super(new Builder(-25, RobosurgeonBlockEntity.SLOT_SKIN)
                .maxInstall(64)
                .energy(10, 0, 0, StackingRule.LINEAR));

    }
}