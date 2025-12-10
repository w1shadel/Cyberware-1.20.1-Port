package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;public class InternalBatteryItem extends CyberwareItem {
    public InternalBatteryItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(4) 

                .energy(0, 0, 3000, StackingRule.LINEAR));

    }
}