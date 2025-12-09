package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import com.Maxwell.cyber_ware_port.Init.ModItems;


public class HudjackItem extends CyberwareItem {
    public HudjackItem() {
        super(new Builder(1, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .requires(ModItems.CYBER_EYE)
        );

    }

}