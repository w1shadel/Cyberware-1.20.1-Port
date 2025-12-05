package com.Maxwell.cyber_ware_port.Common.Item.CyberWare;

import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;

public class CyberArmItem extends CyberwareItem {
    public CyberArmItem(int slotId) {
        super(new Builder(10, slotId)
                .maxInstall(1)
                .incompatible(ModItems.HUMAN_LEFT_ARM,ModItems.HUMAN_RIGHT_ARM)
                .bodyPart(BodyPartType.ARM)
                .energy(5, 0, 0, StackingRule.STATIC));
    }
}