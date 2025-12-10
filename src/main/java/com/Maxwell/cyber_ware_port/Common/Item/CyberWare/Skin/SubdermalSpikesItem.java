package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;public class SubdermalSpikesItem extends CyberwareItem {
    public SubdermalSpikesItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(1));

    }
}