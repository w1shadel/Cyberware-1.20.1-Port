package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;


public class SyntheticSkinItem extends CyberwareItem {
    public SyntheticSkinItem() {
        super(new Builder(0, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(1));

    }

}