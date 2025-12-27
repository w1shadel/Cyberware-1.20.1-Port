package com.maxwell.cyber_ware_port.common.item.cyberware.Bone;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;

public class CitrateEnhancementItem extends CyberwareItem {
    public CitrateEnhancementItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BONES)
                .bodyPart(BodyPartType.BONES)
                .maxInstall(1));

    }
}