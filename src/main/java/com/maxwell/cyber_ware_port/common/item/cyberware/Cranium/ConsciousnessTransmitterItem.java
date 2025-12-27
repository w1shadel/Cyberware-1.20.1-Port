package com.maxwell.cyber_ware_port.common.item.cyberware.Cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;

public class ConsciousnessTransmitterItem extends CyberwareItem {
    public ConsciousnessTransmitterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN).maxInstall(1).incompatible(ModItems.CORTICAL_STACK));
    }
}
