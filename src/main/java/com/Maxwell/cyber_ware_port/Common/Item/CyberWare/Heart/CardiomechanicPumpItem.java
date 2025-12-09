package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import com.Maxwell.cyber_ware_port.Init.ModItems;

public class CardiomechanicPumpItem extends CyberwareItem {
    public CardiomechanicPumpItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .bodyPart(BodyPartType.HEART)
                .incompatible(ModItems.HUMAN_HEART)
                .energy(2, 0, 0, StackingRule.STATIC));

    }
}