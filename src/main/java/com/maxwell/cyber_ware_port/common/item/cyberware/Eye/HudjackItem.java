package com.maxwell.cyber_ware_port.common.item.cyberware.Eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;

public class HudjackItem extends CyberwareItem {
    public HudjackItem() {
        super(new Builder(1, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .requires(ModItems.CYBER_EYE)
        );

    }

}