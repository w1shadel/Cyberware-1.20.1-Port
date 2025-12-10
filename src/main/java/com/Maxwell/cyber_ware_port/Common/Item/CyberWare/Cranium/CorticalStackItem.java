package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;public class CorticalStackItem extends CyberwareItem {
    public CorticalStackItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BRAIN).maxInstall(1).incompatible(ModItems.CONSCIOUSNESS_TRANSMITTER));
    }
}