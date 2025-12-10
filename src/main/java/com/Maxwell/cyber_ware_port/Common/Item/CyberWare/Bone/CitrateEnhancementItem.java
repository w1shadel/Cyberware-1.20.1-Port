package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

public class CitrateEnhancementItem extends CyberwareItem {
    public CitrateEnhancementItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BONES)
                .bodyPart(BodyPartType.BONES)
                .maxInstall(1));

    }
}