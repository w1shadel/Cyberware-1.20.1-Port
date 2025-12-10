package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;public class ConsciousnessTransmitterItem extends CyberwareItem {
    public ConsciousnessTransmitterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN).maxInstall(1).incompatible(ModItems.CORTICAL_STACK));
    }
}
